package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/5/24
 */

@Data
@ApiModel("审批字段授权保存BO")
public class ExamineFieldAuthBO {

    /**
     * crm模块
     */
    @ApiModelProperty(value = "crm模块")
    private Integer label;

    /**
     * 字段id
     */
    @ApiModelProperty(value = "字段id")
    private Long fieldId;

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldName;

    /**
     * 权限 1不可编辑不可查看 2可查看不可编辑 3可编辑可查看
     */
    @ApiModelProperty(value = "权限 1不可编辑不可查看 2可查看不可编辑 3可编辑可查看")
    private Integer authLevel;


    @ApiModelProperty(value = "明细表格权限（十进制转二进制）")
    private Integer tableLevel;

    @ApiModelProperty(value = "明细表格字段")
    private List<ExamineFieldAuthBO> fieldExtendList;
}
