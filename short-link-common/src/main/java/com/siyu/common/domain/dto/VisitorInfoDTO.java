package com.siyu.common.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class VisitorInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String uuid;

    private String ip;

    private String country;

    private String province;

    private String city;

    private String isp;

    private String os;

    private String device;

    private String browser;

    private String network;
}
