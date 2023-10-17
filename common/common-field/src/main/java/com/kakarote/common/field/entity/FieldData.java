package com.kakarote.common.field.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自定义字段扩展数据类
 *
 * @author zhangzhiwei
 */
@ApiModel(value = "CrmFieldData对象", description = "扩展字段数据表")
@Getter
@Setter
public class FieldData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段值")
    private String value;

    @ApiModelProperty(value = "关联ID")
    private String batchId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "数据id")
    private Long dataId;

    public FieldData() {
    }

    public FieldData(Long id) {
        this.id = id;
    }

    public FieldData(Long fieldId, String name, String value, String batchId) {
        this.fieldId = fieldId;
        this.name = name;
        this.value = value;
        this.batchId = batchId;
    }

    public FieldData(Long fieldId, String name, String value, String batchId, Long dataId) {
        this.fieldId = fieldId;
        this.name = name;
        this.value = value;
        this.batchId = batchId;
        this.dataId=dataId;
    }
}
