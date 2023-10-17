package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 绩效考核关联员工表
 * </p>
 *
 * @author zyl
 * @since 2022-05-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan_relation_employee")
@ApiModel(value = "HrmAppraisalPlanRelationEmployee对象", description = "绩效考核关联员工表")
public class HrmAppraisalPlanRelationEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appraisal_plan_relation_employee_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "关联id")
    private Long appraisalPlanRelationEmployeeId;

    @ApiModelProperty(value = "绩效考核id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
