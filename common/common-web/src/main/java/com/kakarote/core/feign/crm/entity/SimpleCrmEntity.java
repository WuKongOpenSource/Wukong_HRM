package com.kakarote.core.feign.crm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("crm模板")
public class SimpleCrmEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "类型")
    private Object extraData;
}
