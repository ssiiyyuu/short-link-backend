package com.siyu.common.utils;

import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class UserAgentUtils {

    private static UserAgentAnalyzer userAgentAnalyzer = null;

    @PostConstruct
    private void initUserAgentAnalyzer() {
        userAgentAnalyzer = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .withFields(UserAgent.OPERATING_SYSTEM_NAME_VERSION_MAJOR)
                .withFields(UserAgent.DEVICE_NAME)
                .withFields(UserAgent.NETWORK_TYPE)
                .withFields(UserAgent.AGENT_NAME_VERSION_MAJOR)
                .build();
    }

    public static UserAgent.ImmutableUserAgent parserUA(String ua) {
        if(userAgentAnalyzer == null) {
            throw new BusinessException(ErrorStatus.SYS_ERROR, "UserAgentAnalyzer初始化异常");
        }
        return userAgentAnalyzer.parse(ua);
    }

    public static String getOS(@NotNull UserAgent.ImmutableUserAgent userAgent) {
        return userAgent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION_MAJOR);
    }
    public static String getDevice(@NotNull UserAgent.ImmutableUserAgent userAgent) {
        return userAgent.getValue(UserAgent.DEVICE_NAME);
    }
    public static String getNetwork(@NotNull UserAgent.ImmutableUserAgent userAgent) {
        return userAgent.getValue(UserAgent.NETWORK_TYPE);
    }
    public static String getBrowser(@NotNull UserAgent.ImmutableUserAgent userAgent) {
        return userAgent.getValue(UserAgent.AGENT_NAME_VERSION_MAJOR);
    }
}
