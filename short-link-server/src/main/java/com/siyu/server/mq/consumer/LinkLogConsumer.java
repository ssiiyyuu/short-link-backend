package com.siyu.server.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.domain.dto.VisitorInfoDTO;
import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.server.dto.LinkLogMessageDTO;
import com.siyu.server.entity.*;
import com.siyu.server.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkLogConsumer {

    private final LinkOsLogService linkOsLogService;

    private final LinkPvLogService linkPvLogService;

    private final LinkDeviceLogService linkDeviceLogService;

    private final LinkBrowserLogService linkBrowserLogService;

    private final LinkCityLogService linkCityLogService;

    private final LinkIspLogService linkIspLogService;

    private final LinkNetworkLogService linkNetworkLogService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_PV_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkPVLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDateTimeUtil.ofDate(now);
        int hour = now.getHour();
        LinkPvLog logEntity = linkPvLogService.getOne(new LambdaQueryWrapper<LinkPvLog>()
                .eq(LinkPvLog::getDate, date)
                .eq(LinkPvLog::getHour, hour));

        if(null == logEntity) {
            logEntity = new LinkPvLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setHour(hour);
            logEntity.setShortUtl(shortUrl);
            linkPvLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkPvLogService.updateById(logEntity);
        }
        log.info("短链接：{}pv日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_OS_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkOSLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkOsLog logEntity = linkOsLogService.getOne(new LambdaQueryWrapper<LinkOsLog>()
                .eq(LinkOsLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkOsLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setOs(visitorInfoDTO.getOs());
            logEntity.setShortUtl(shortUrl);
            linkOsLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkOsLogService.updateById(logEntity);
        }
        log.info("短链接：{}os日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_DEVICE_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkDeviceLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkDeviceLog logEntity = linkDeviceLogService.getOne(new LambdaQueryWrapper<LinkDeviceLog>()
                .eq(LinkDeviceLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkDeviceLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setDevice(visitorInfoDTO.getDevice());
            logEntity.setShortUtl(shortUrl);
            linkDeviceLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkDeviceLogService.updateById(logEntity);
        }
        log.info("短链接：{}device日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_BROWSER_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkBrowserLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkBrowserLog logEntity = linkBrowserLogService.getOne(new LambdaQueryWrapper<LinkBrowserLog>()
                .eq(LinkBrowserLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkBrowserLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setBrowser(visitorInfoDTO.getBrowser());
            logEntity.setShortUtl(shortUrl);
            linkBrowserLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkBrowserLogService.updateById(logEntity);
        }
        log.info("短链接：{}browser日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_NETWORK_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkNetworkLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkNetworkLog logEntity = linkNetworkLogService.getOne(new LambdaQueryWrapper<LinkNetworkLog>()
                .eq(LinkNetworkLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkNetworkLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setNetwork(visitorInfoDTO.getNetwork());
            logEntity.setShortUtl(shortUrl);
            linkNetworkLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkNetworkLogService.updateById(logEntity);
        }
        log.info("短链接：{}network日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_ISP_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkIsoLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkIspLog logEntity = linkIspLogService.getOne(new LambdaQueryWrapper<LinkIspLog>()
                .eq(LinkIspLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkIspLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setIsp(visitorInfoDTO.getIsp());
            logEntity.setShortUtl(shortUrl);
            linkIspLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkIspLogService.updateById(logEntity);
        }
        log.info("短链接：{}ISP日志记录", shortUrl);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.LINK_CITY_LOG_SAVE_KEY),
            exchange = @Exchange(name = RabbitMQConstants.LINK_LOG_EXCHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void saveLinkCityLog(LinkLogMessageDTO linkLogMessageDTO) {
        String shortUrl = linkLogMessageDTO.getShortUrl();
        VisitorInfoDTO visitorInfoDTO = linkLogMessageDTO.getVisitorInfoDTO();

        LocalDate date = LocalDate.now();
        LinkCityLog logEntity = linkCityLogService.getOne(new LambdaQueryWrapper<LinkCityLog>()
                .eq(LinkCityLog::getDate, date));

        if(null == logEntity) {
            logEntity = new LinkCityLog();
            logEntity.setCount(1);
            logEntity.setDate(date);
            logEntity.setCountry(visitorInfoDTO.getCountry());
            logEntity.setProvince(visitorInfoDTO.getProvince());
            logEntity.setCity(visitorInfoDTO.getCity());
            logEntity.setShortUtl(shortUrl);
            linkCityLogService.save(logEntity);
        } else {
            logEntity.setCount(logEntity.getCount() + 1);
            linkCityLogService.updateById(logEntity);
        }
        log.info("短链接：{}city日志记录", shortUrl);
    }

}
