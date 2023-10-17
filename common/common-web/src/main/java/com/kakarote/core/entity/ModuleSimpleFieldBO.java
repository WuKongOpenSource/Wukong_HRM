package com.kakarote.core.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : zjj
 * @since : 2022/8/30
 */
@Data
public class ModuleSimpleFieldBO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "分组ID")
    private Integer groupId;

    @ApiModelProperty(value = "自定义字段英文标识")
    private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段类型")
    private Integer type;

    @ApiModelProperty(value = "0 系统 1 自定义")
    private Integer fieldType;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "字段说明")
    private String remark;

    @ApiModelProperty(value = "输入提示")
    private String inputTips;

    @ApiModelProperty(value = "最大长度")
    private Integer maxLength;

    @ApiModelProperty(value = "是否唯一 1 是 0 否")
    private Integer isUnique;

    @ApiModelProperty(value = "是否必填 1 是 0 否")
    private Integer isNull;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;
}
