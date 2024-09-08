package com.siyu.redis.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class RedisProperties {

    public static int SHORT_URL_EXPIRE_TIME;

    @Value("${short-link.redis.short-url-expire-time}")
    private int shortUrlExpireTime;

    @PostConstruct
    private void initRedisProperties() {
        SHORT_URL_EXPIRE_TIME = this.shortUrlExpireTime;
    }
}
