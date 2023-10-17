package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字段排序表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_field_config")
@ApiModel(value = "HrmEmployeeFieldConfig对象", description = "字段排序表")
public class HrmEmployeeFieldConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字段id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "字段排序")
    private Integer sort;

    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "是否隐藏 0、不隐藏 1、隐藏")
    private Integer isHide;

    @ApiModelProperty(value = "字段宽度")
    private Integer width;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

}
