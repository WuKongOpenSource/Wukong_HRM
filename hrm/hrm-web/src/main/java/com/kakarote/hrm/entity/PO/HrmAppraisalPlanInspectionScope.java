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
 * @since 2022-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan_inspection_scope")
@ApiModel(value = "HrmAppraisalPlanInspectionScope对象", description = "考核计划-考核范围配置")
public class HrmAppraisalPlanInspectionScope implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "inspection_scope_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "考核范围id")
    private Long inspectionScopeId;

    @ApiModelProperty(value = "新建考核计划-考核范围类型1:员工 2:部门")
    private Integer type;

    @ApiModelProperty(value = "员工或部门id")
    private Long recordId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

}
