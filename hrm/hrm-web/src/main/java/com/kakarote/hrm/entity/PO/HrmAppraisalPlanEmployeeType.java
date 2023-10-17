package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zyl
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan_employee_type")
@ApiModel(value = "HrmAppraisalPlanEmployeeType对象", description = "考核计划设置-考核范围-聘用形式范围表")
public class HrmAppraisalPlanEmployeeType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "employee_type_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private Long employeeTypeId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "聘用形式")
    private Integer employeeType;

    @ApiModelProperty(value = "员工状态")
    private String employeeStatus;

}
