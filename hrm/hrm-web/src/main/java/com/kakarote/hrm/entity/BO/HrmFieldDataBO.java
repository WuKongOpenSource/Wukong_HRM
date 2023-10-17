package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "hrm自定义字段", description = "hrm自定义字段")
public class HrmFieldDataBO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "字段Id")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段值")
    private String value;

    @ApiModelProperty(value = "batchId")
    private String batchId;

    @ApiModelProperty(value = "对应主表ID")
    private String typeId;

    @ApiModelProperty(value = "字段类型")
    private Integer type;
}
