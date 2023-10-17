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
 * 部门表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_dept")
@ApiModel(value = "HrmDept对象", description = "部门表")
public class HrmDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "dept_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    @ApiModelProperty(value = "父级ID 顶级部门为0")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @ApiModelProperty(value = "1 公司 2 部门")
    private Integer deptType;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "部门编码")
    private String code;

    @ApiModelProperty(value = "部门负责人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainEmployeeId;

    @ApiModelProperty(value = "分管领导")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long leaderEmployeeId;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
