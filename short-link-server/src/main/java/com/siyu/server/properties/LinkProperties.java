package com.siyu.server.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Data
@Component
public class LinkProperties {

    public static String DOMAIN;

    public static boolean BLACK_LIST_ENABLE;

    public static List<String> BLACK_LIST_DOMAINS;

    @Value("${short-link.domain}")
    private String domain;

    @Value("${short-link.black-list.enable}")
    private boolean blackListEnable;

    @Value("${short-link.black-list.domains}")
    private String[] blackListDomains;

    @PostConstruct
    private void initLinkProperties() {
        DOMAIN = this.domain;
        BLACK_LIST_ENABLE = this.blackListEnable;
        BLACK_LIST_DOMAINS = Arrays.asList(this.blackListDomains);
    }

}
