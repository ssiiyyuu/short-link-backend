package com.siyu.rabbitMQ.constants;

public class RabbitMQConstants {

    // exchange
    public static final String VISITOR_EXCHANGE = "short-link.visitor.exchange";
    public static final String VISITOR_OPERATION_LOG_EXCHANGE = "short-link.visitor-operation-log.exchange";
    public static final String LINK_LOG_EXCHANGE = "short-link.link-log.exchange";

    // queue
    public static final String VISITOR_SAVE_QUEUE = "short-link.visitor.save.queue";
    public static final String VISITOR_OPERATION_LOG_SAVE_QUEUE = "short-link.visitor-operation-log.save.queue";
    public static final String LINK_PV_LOG_SAVE_KEY = "short-link.link-pv-log.save.queue";
    public static final String LINK_OS_LOG_SAVE_KEY = "short-link.link-os-log.save.queue";
    public static final String LINK_DEVICE_LOG_SAVE_KEY = "short-link.link-device-log.save.queue";
    public static final String LINK_BROWSER_LOG_SAVE_KEY = "short-link.link-browser-log.save.queue";
    public static final String LINK_NETWORK_LOG_SAVE_KEY = "short-link.link-network-log.save.queue";
    public static final String LINK_ISP_LOG_SAVE_KEY = "short-link.link-isp-log.save.queue";
    public static final String LINK_CITY_LOG_SAVE_KEY = "short-link.link-city-log.save.queue";

    // binding key
    public static final String VISITOR_SAVE_KEY = "short-link.visitor.save.key";
    public static final String VISITOR_OPERATION_LOG_SAVE_KEY = "short-link.visitor-operation-log.save.key";
}
