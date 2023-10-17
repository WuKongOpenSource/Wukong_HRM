package com.kakarote.core.feign.crm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApiModel("审批字段对象")
@Data
public class ExamineField implements Serializable {

    @ApiModelProperty("字段ID")
    private Long fieldId;

    @ApiModelProperty("字段展示名称")
    private String name;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("字段类型")
    private Integer type;

    @ApiModelProperty("字段来源类型 0 自定义字段 1 固定字段")
    private Integer fieldType;

    @ApiModelProperty(value = "设置列表")
    private List<String> setting;

    @ApiModelProperty(value = "权限 1不可编辑不可查看 2可查看不可编辑 3可编辑可查看")
    private Integer authLevel;


    public ExamineField(Long fieldId, String name, String fieldName, Integer type, Integer fieldType) {
        this.fieldId = fieldId;
        this.name = name;
        this.fieldName = fieldName;
        this.type = type;
        this.fieldType = fieldType;
    }

    public ExamineField() {
    }

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;
}
