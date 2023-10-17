package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/5/29
 */
@Data
public class ExamineFlowFieldUpdateLogBO {

    /**
     * 审批id
     */
    @ApiModelProperty(value = "审批id")
    private Long examineId;

    /**
     * 流程节点Id
     */
    @ApiModelProperty(value = "流程节点Id")
    private Long flowId;

    /**
     * 批次id
     */
    @ApiModelProperty(value = "批次id")
    private String batchId;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private Integer type;

    /**
     * 字段展示名称
     */
    @ApiModelProperty(value = "字段展示名称")
    private String name;

    /**
     * 业务id
     */
    @ApiModelProperty(value = "业务id")
    private Long typeId;

    /**
     * label
     */
    @ApiModelProperty(value = "label")
    private Integer label;

    /**
     * 字段id
     */
    @ApiModelProperty(value = "字段id")
    private Long fieldId;

    /**
     * 修改的值
     */
    @ApiModelProperty(value = "修改的值")
    private String newValue;

    /**
     * 原本的值
     */
    @ApiModelProperty(value = "原本的值")
    private String oldValue;

    /**
     * 公司id
     */
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;


    /**
     * 字段来源  0.自定义 1.原始固定 2原始字段但值存在扩展表中
     */
    @ApiModelProperty(value = "字段来源  0.自定义 1.原始固定 2原始字段但值存在扩展表中")
    private Integer fieldType;

}
