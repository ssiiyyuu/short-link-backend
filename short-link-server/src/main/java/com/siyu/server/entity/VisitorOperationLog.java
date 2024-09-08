package com.siyu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-09-07 09:36:23
 */
@Getter
@Setter
@TableName("visitor_operation_log")
@ApiModel(value = "VisitorOperationLog对象", description = "")
public class VisitorOperationLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("游客唯一标识")
    private String uuid;

    @ApiModelProperty("请求接口")
    private String uri;

    @ApiModelProperty("请求方法")
    private String method;

    @ApiModelProperty("请求参数")
    private String params;

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("接口耗时")
    private Long duration;

    @ApiModelProperty("操作对象")
    private String target;

    @ApiModelProperty("操作类型")
    private String type;
}
