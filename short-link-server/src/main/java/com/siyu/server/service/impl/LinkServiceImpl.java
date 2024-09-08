package com.siyu.server.service.impl;

import cn.hutool.core.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.context.VisitorContext;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.HashUtils;
import com.siyu.redis.constants.RedisKeyConstants;
import com.siyu.redis.properties.RedisProperties;
import com.siyu.server.entity.Link;
import com.siyu.server.mapper.LinkMapper;
import com.siyu.server.properties.LinkProperties;
import com.siyu.server.service.LinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-09-07 12:47:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    private final RBloomFilter<String> shortLinkBloomFilter;

    private final StringRedisTemplate redisTemplate;

    private final RedissonClient redissonClient;

    @Override
    public Link create(String originalUrl) {
        //1. 判断原始链接域名是否存在黑名单
        checkBlackList(originalUrl);
        //2. 生成短链接
        String shortUri = generateShortUri(originalUrl);
        String shortUrl = LinkProperties.DOMAIN + "/" + shortUri;

        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setUuid(VisitorContext.getVisitorInfo().getUuid());
        link.setDomain(LinkProperties.DOMAIN);
        link.setShortUri(shortUri);
        link.setShortUrl(shortUrl);

        //3. 保存短链接到数据库
        save(link);
        //4. 添加到布隆过滤器
        shortLinkBloomFilter.add(shortUrl);
        //5. 缓存预热
        redisTemplate.opsForValue().set(RedisKeyConstants.LINK_SHORT_URL_MAP_KEY + shortUrl, originalUrl, RedisProperties.SHORT_URL_EXPIRE_TIME, TimeUnit.HOURS);

        return link;
    }

    @Override
    public boolean redirect(String shortUriSuffix, HttpServletResponse response) {
        // 1. 根据shortUri还原出shortUrl
        String shortUrl = LinkProperties.DOMAIN + "/link/" + shortUriSuffix;
        // 2. 尝试从redis中获取originalUrl
        String originalUrl = redisTemplate.opsForValue().get(RedisKeyConstants.LINK_SHORT_URL_MAP_KEY + shortUrl);

        if(StrUtil.isNotBlank(originalUrl)) {
            sendRedirect(originalUrl, response);
            return true;
        }

        // 3. 查询布隆过滤器，防止缓存穿透
        boolean bloomFilterFlag = shortLinkBloomFilter.contains(shortUrl);
        if(!bloomFilterFlag) {
            sendRedirect("/notfound", response);
            return false;
        }

        // 4. 从数据库里查询并更新缓存，分布式锁，防止缓存击穿
        RLock lock = redissonClient.getLock(RedisKeyConstants.LINK_SHORT_URL_LOCK_KEY + shortUrl);
        lock.lock();
        try {
            // 双重检查
            originalUrl = redisTemplate.opsForValue().get(RedisKeyConstants.LINK_SHORT_URL_MAP_KEY + shortUrl);
            if(StrUtil.isNotBlank(originalUrl)) {
                sendRedirect(originalUrl, response);
                return true;
            }
            // 4.1 查询数据库
            Link link = getOne(new LambdaQueryWrapper<Link>().eq(Link::getShortUrl, shortUrl));
            if(null == link || StrUtil.isEmpty(link.getOriginalUrl())) {
                sendRedirect("/notfound", response);
                return false;
            }
            // 4.2 更新缓存
            redisTemplate.opsForValue().set(RedisKeyConstants.LINK_SHORT_URL_MAP_KEY + shortUrl, link.getOriginalUrl(), RedisProperties.SHORT_URL_EXPIRE_TIME, TimeUnit.HOURS);
            sendRedirect(link.getOriginalUrl(), response);
            return true;
        } finally {
            lock.unlock();
        }

    }

    private void checkBlackList(String originalUrl) {
        boolean blackListEnable = LinkProperties.BLACK_LIST_ENABLE;
        if(blackListEnable) {
            try {
                URI uri = new URI(originalUrl);
                String host = uri.getHost();
                String domain = host.startsWith("www.") ? host.substring(4) : host;
                if(LinkProperties.BLACK_LIST_DOMAINS.contains(domain)) {
                    throw new BusinessException(ErrorStatus.SYS_ERROR, "此域名已加入系统黑名单");
                }

            } catch (URISyntaxException e) {
                throw new BusinessException(ErrorStatus.SYS_ERROR, "URI解析失败");
            }
        }
    }

    private String generateShortUri(String originalUrl) {
        // 解决可能出现的hash冲突，重试3次
        int RETRY_SIZE = 3;
        StringBuilder originalUrlBuilder = new StringBuilder(originalUrl);
        for(int i = 0; i < RETRY_SIZE; i++) {

            String shortUri = "link/" + HashUtils.hashToBase62(originalUrlBuilder.toString());
            String shortUrl = LinkProperties.DOMAIN + "/" + shortUri;
            if(!shortLinkBloomFilter.contains(shortUrl)) {
                return shortUri;
            }
            originalUrlBuilder.append(IdUtil.fastSimpleUUID());
            log.info("发生hash冲突，本次为第{}次重试", i);
        }
        throw new BusinessException(ErrorStatus.SYS_ERROR, "短链接创建失败，请稍后再试");
    }

    private void sendRedirect(String url, HttpServletResponse response) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("短链接跳转失败，原因：{}", e.toString());
            throw new BusinessException(ErrorStatus.SYS_ERROR, "短链接跳转失败");
        }
    }
}
