package com.kakarote.core.feign.bi.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.core.common.enums.FieldEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字段排序表
 * </p>
 *
 * @author UNIQUE
 * @since 2022-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BiFieldVO对象", description="bi字段排序表")
public class BiFieldVO implements Serializable {

    private static final long serialVersionUID=1L;

    public BiFieldVO() {

    }
    public BiFieldVO(String fieldName, FieldEnum fieldEnum, String name, Integer fieldType, String options, String fieldSearchFormType, String fieldSearchFormName) {
        this.fieldName = fieldName;
        this.name = name;
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
        this.fieldType = fieldType;
        this.options = options;
        this.fieldSearchFormType = fieldSearchFormType;
        this.fieldSearchFormName = fieldSearchFormName;
    }
    public BiFieldVO(String fieldName, FieldEnum fieldEnum, String name, Integer fieldType, String fieldSearchFormType, String fieldSearchFormName) {
        this.fieldName = fieldName;
        this.name = name;
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
        this.fieldType = fieldType;
        this.fieldSearchFormType = fieldSearchFormType;
        this.fieldSearchFormName = fieldSearchFormName;
    }
    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "字段ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型 1 crm 2 无代码 3 聚合表")
    private Integer labelType;

    @ApiModelProperty(value = "当type为1时，传递crm的label,当type为2时，传递无代码moduleId,当type为3时，传递聚合表dataId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dataId;

    @ApiModelProperty(value = "字段类型")
    private Integer type;

    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "字段是否是系统字段")
    private Integer fieldType;

    @ApiModelProperty(value = "字段类型")
    private String formType;

    @ApiModelProperty(value = "维度指标对应的查询的名称的类型 一些字段需要用到")
    private String fieldSearchFormType;

    @ApiModelProperty(value = "维度指标对应的查询的名称 一些字段需要用到")
    private String fieldSearchFormName;

    @ApiModelProperty(value = "字段的下拉选择项")
    private String options;

    @ApiModelProperty(value = "字段的下拉选择项")
    private List<Object> setting = new ArrayList<>();

    @ApiModelProperty(value = "字段排序")
    private Integer sorting;

    @ApiModelProperty(value = "默认0 维度 1  指标2")
    private Integer dataType;

    @ApiModelProperty(value = "转化后的 统计方式 作为补充")
    private String turnFieldText;

    @ApiModelProperty(value = "选项列表")
    private List<Object> optionsList;

    @ApiModelProperty(value = "逻辑表单数据")
    private Map<String, Object> optionsData;


    @ApiModelProperty(value = "字段说明  特别用途 - 明细表格：添加字段说明 | 单选/多选： 标识开启逻辑表单")
    private String remark;

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;

}
