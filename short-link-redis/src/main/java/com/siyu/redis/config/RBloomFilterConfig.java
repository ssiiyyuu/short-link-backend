package com.siyu.redis.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfig {

    @Bean
    public RBloomFilter<String> visitorBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> visitorBloomFilter = redissonClient.getBloomFilter("visitorBloomFilter");
        visitorBloomFilter.tryInit(100000000L, 0.001);
        return visitorBloomFilter;
    }

    @Bean
    public RBloomFilter<String> shortLinkBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> shortLinkBloomFilter = redissonClient.getBloomFilter("shortLinkBloomFilter");
        shortLinkBloomFilter.tryInit(100000000L, 0.001);
        return shortLinkBloomFilter;
    }
}
