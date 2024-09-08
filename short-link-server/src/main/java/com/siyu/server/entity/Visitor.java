package com.siyu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.siyu.common.domain.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-09-08 08:29:54
 */
@Getter
@Setter
@ApiModel(value = "Visitor对象", description = "")
public class Visitor extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("游客唯一标识")
    private String uuid;

    @ApiModelProperty("访问IP")
    private String ip;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("省份")
    private String province;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("网络业务提供商")
    private String isp;

    @ApiModelProperty("操作系统")
    private String os;

    @ApiModelProperty("访问设备")
    private String device;

    @ApiModelProperty("浏览器")
    private String browser;

    @ApiModelProperty("访问网络")
    private String network;
}
