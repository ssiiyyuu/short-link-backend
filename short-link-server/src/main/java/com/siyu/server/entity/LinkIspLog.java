package com.siyu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.siyu.common.domain.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
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
 * @since 2024-09-08 08:35:53
 */
@Getter
@Setter
@TableName("link_isp_log")
@ApiModel(value = "LinkIspLog对象", description = "")
public class LinkIspLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("短链接url")
    private String shortUtl;

    @ApiModelProperty("网络服务提供商")
    private String isp;

    @ApiModelProperty("短链接网络服务提供商访问量")
    private Integer count;

    @ApiModelProperty("日志记录日期")
    private LocalDate date;
}
