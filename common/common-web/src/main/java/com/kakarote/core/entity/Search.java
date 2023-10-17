package com.kakarote.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakarote.core.common.enums.FieldSearchEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义字段通用搜索对象
 * @author zhangzhiwei
 */
@Data
@ApiModel(value = "高级筛选子查询")
@Accessors(chain = true)
public class Search {

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "格式")
    private String formType;

    @ApiModelProperty(value = "高级筛选列表")
    @JsonProperty("type")
    private FieldSearchEnum searchEnum;

    @ApiModelProperty(value = "高级筛选中 传递类型")
    private Integer searchType;

    @ApiModelProperty(value = "值列表")
    private List<String> values = new ArrayList<>();

    @ApiModelProperty(value = "额外数据")
    private Object extraData;


    /**
     * module模块需要的字段
     */
    @ApiModelProperty(value = "明細表格字段")
    private ModuleSimpleFieldBO detailTableField;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "筛选条件类型为1(匹配字段)时，传入当前模块字段id")
    private Long currentFieldId;

    @NotNull
    @ApiModelProperty(value = "当前模块临时字段ID")
    private String tempCurrentFieldId;

    public Search(String name, String formType, FieldSearchEnum searchEnum, List<String> values) {
        this.name = name;
        this.formType = formType;
        this.searchEnum = searchEnum;
        this.values = values;
    }

    public Search(String name, String formType, FieldSearchEnum searchEnum) {
        this.name = name;
        this.formType = formType;
        this.searchEnum = searchEnum;
    }

    public Search() {
    }
}