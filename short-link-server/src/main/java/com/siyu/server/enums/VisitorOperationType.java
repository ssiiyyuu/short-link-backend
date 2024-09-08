package com.siyu.server.enums;

import lombok.Getter;

@Getter
public enum VisitorOperationType {
    CREATE_LINK("创建短链接"),

    ACCESS_LINK("访问短链接");

    private final String value;

    VisitorOperationType(String value) {
        this.value = value;
    }

}
