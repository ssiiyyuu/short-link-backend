package com.siyu.server.aop;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.constants.CookieNameConstants;
import com.siyu.common.constants.RequestHeaderConstants;
import com.siyu.common.context.VisitorContext;
import com.siyu.common.domain.R;
import com.siyu.common.domain.dto.VisitorInfoDTO;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.IpUtils;
import com.siyu.common.utils.UserAgentUtils;
import com.siyu.redis.constants.RedisKeyConstants;
import com.siyu.server.annotation.VisitorOperationLogger;
import com.siyu.server.entity.Visitor;
import com.siyu.server.entity.VisitorOperationLog;
import com.siyu.server.enums.VisitorOperationType;
import com.siyu.server.mq.producer.LinkLogProducer;
import com.siyu.server.mq.producer.VisitorOperationLogProducer;
import com.siyu.server.mq.producer.VisitorProducer;
import com.siyu.server.properties.LinkProperties;
import com.siyu.server.service.VisitorService;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class VisitorOperationLogAOP {

    private final RBloomFilter<String> visitorBloomFilter;

    private final VisitorProducer visitorProducer;

    private final LinkLogProducer linkLogProducer;

    private final VisitorOperationLogProducer visitorOperationLogProducer;

    private final VisitorService visitorService;

    private final ThreadLocal<Long> beforeApiTimeMillis = new ThreadLocal<>();


    @Around("@annotation(logger)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, VisitorOperationLogger logger) throws Throwable {
        beforeApiTimeMillis.set(System.currentTimeMillis());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = Objects.requireNonNull(requestAttributes.getRequest());
        HttpServletResponse response = Objects.requireNonNull(requestAttributes.getResponse());

        // 从proceedingJoinPoint中取出参数KV并转换为JSON字符串
        String[] argNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        Object[] args = proceedingJoinPoint.getArgs();
        Map<String, Object> argMap = new LinkedHashMap<>();
        for(int i = 0; i < args.length; i++) {
            argMap.put(argNames[i], args[i]);
        }
        String params = JSONObject.toJSONString(argMap);
        params = params.length() >= 1000 ? params.substring(0, 1000) : params;

        // 1. 从request中获取游客UUID
        // 2. 如果UUID合法则从数据库查出游客信息
        // 3. 如果UUID不合法则签发UUID，并保存游客数据
        VisitorInfoDTO visitorInfoDTO = Optional.ofNullable(getVisitorInfoDTO(request))
                .orElseGet(() -> generateVisitorInfoDTO(request, response));
        VisitorContext.setVisitorInfo(visitorInfoDTO);

        // 方法执行
        R<?> proceed = (R<?>) proceedingJoinPoint.proceed();

        // MQ保存游客日志
        VisitorOperationLog visitorOperationLog = new VisitorOperationLog();
        visitorOperationLog.setUuid(visitorInfoDTO.getUuid());
        visitorOperationLog.setType(logger.operationType().getValue());
        visitorOperationLog.setTarget(logger.operationTarget());
        visitorOperationLog.setParams(params);
        visitorOperationLog.setUri(request.getRequestURI());
        visitorOperationLog.setMethod(request.getMethod());
        visitorOperationLog.setDuration(System.currentTimeMillis() - beforeApiTimeMillis.get());
        visitorOperationLog.setSuccess(proceed.getSuccess());
        visitorOperationLogProducer.sendVisitorOperationLogSaveMessage(visitorOperationLog);

        // 如果当前请求位访问短链接且请求成功，则保存短链接监控日志
        if(proceed.getSuccess() && VisitorOperationType.ACCESS_LINK.equals(logger.operationType())) {
            String shortUriSuffix = (String) JSONObject.parseObject(visitorOperationLog.getParams()).get("shortUriSuffix");
            String shortUrl = LinkProperties.DOMAIN + "/link/" + shortUriSuffix;
            linkLogProducer.sendLinkLogSaveMessage(shortUrl, visitorInfoDTO);
        }

        // 移除ThreadLocal防止内存泄露
        VisitorContext.remove();
        beforeApiTimeMillis.remove();
        return proceed;
    }

    private VisitorInfoDTO getVisitorInfoDTO(HttpServletRequest request) {
        // 1. 从cookie中拿到出uuid
        String uuid = getUUID(request);

        // 2. 验证uuid是否伪造
        // 2.1 查询布隆过滤器中是否存在
        boolean bloomFilterFlag = visitorBloomFilter.contains(uuid);
        if(!bloomFilterFlag) {
            return null;
        }
        // 2.2 查询数据库
        Visitor visitor = visitorService.getOne(new LambdaQueryWrapper<Visitor>().eq(Visitor::getUuid, uuid));
        return BeanUtils.copyProperties(visitor, new VisitorInfoDTO());
    }

    private VisitorInfoDTO generateVisitorInfoDTO(HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtils.getIp(request);
        String ua = request.getHeader(RequestHeaderConstants.USER_AGENT_HEADER);

        // 1. 签发uuid
        String uuid = issueUUID(ip, ua, response);

        // 2. 保存游客实体到数据库
        String[] ipAddressArray = IpUtils.getIpAddressArray(ip);
        UserAgent.ImmutableUserAgent immutableUserAgent = UserAgentUtils.parserUA(ua);
        Visitor visitor = new Visitor();
        visitor.setUuid(uuid);
        visitor.setIp(ip);
        visitor.setOs(UserAgentUtils.getOS(immutableUserAgent));
        visitor.setDevice(UserAgentUtils.getDevice(immutableUserAgent));
        visitor.setBrowser(UserAgentUtils.getBrowser(immutableUserAgent));
        visitor.setNetwork(UserAgentUtils.getNetwork(immutableUserAgent));
        visitor.setCountry(IpUtils.getCountry(ipAddressArray));
        visitor.setProvince(IpUtils.getProvince(ipAddressArray));
        visitor.setCity(IpUtils.getCity(ipAddressArray));
        visitor.setIsp(IpUtils.getISP(ipAddressArray));

        // 发送给MQ保存
        visitorProducer.sendVisitorSaveMessage(visitor);
        return BeanUtils.copyProperties(visitor, new VisitorInfoDTO());
    }

    private String getUUID(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(null == cookies) {
            return null;
        }
        Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                .filter(cookie -> CookieNameConstants.UUID_NAME.equals(cookie.getName()))
                .findFirst();
        if(cookieOptional.isEmpty()) {
            return null;
        }
        String uuid = cookieOptional.get().getValue();
        if(null == uuid || uuid.length() != 36) {
            return null;
        }
        return uuid;
    }



    private String issueUUID(String ip, String ua, HttpServletResponse response) {
        // 保证同一个小时内相同IP，相同UA签发出的UUID是相同的，防止刷游客量
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long hourMillis = calendar.getTimeInMillis();

        String payload = ip + ua + hourMillis;
        String uuid = UUID.nameUUIDFromBytes(payload.getBytes()).toString();

        // 添加到cookie中
        Cookie cookie = new Cookie(CookieNameConstants.UUID_NAME, uuid);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);

        return uuid;
    }

}
