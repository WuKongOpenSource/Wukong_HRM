package com.kakarote.core.feign.bi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: FieldQueryBO
 * @date 2022/4/6 17:16
 */
@Data
@ApiModel("字段查询 BO")
public class BiModuleFieldQueryBO {


    @ApiModelProperty(value = "类型 1 crm 2 无代码 3 聚合表")
    private Integer type;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "当type为1时，传递crm的label,当type为2时，传递无代码moduleId,当type为3时，传递聚合表dataId")
    private Long dataId;


}
