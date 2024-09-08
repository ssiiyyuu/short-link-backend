package com.siyu.common.enums;

import lombok.Getter;

@Getter
public enum ErrorStatus {

    SYS_ERROR("A0000", "系统异常"),

    PARAM_ERROR("A0100", "参数校验失败"),

    NOT_FOUND("S0000", "资源丢失"),

    UNKNOWN("UNKNOWN", "UNKNOWN");

    ErrorStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;

    private final String message;

    /**
     * 根据状态码获取枚举类型
     * @param code
     * @return ErrorStatus
     */
    public static ErrorStatus resolve(String code) {
        if (code == null || code.isEmpty()) {
            return UNKNOWN;
        }
        for (ErrorStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }


}
