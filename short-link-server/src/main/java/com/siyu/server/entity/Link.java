package com.siyu.server.entity;

import com.siyu.common.domain.BaseEntity;

import java.io.Serial;
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
 * @since 2024-09-07 12:47:58
 */
@Getter
@Setter
@ApiModel(value = "Link对象", description = "")
public class Link extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("域名")
    private String domain;

    @ApiModelProperty("短链接uri")
    private String shortUri;

    @ApiModelProperty("短链接url")
    private String shortUrl;

    @ApiModelProperty("原始连接url")
    private String originalUrl;

    @ApiModelProperty("短链接创建人uuid")
    private String uuid;
}
