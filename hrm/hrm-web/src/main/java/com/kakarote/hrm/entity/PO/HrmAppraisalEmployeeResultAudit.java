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
 * 员工绩效考核-结果确认
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_result_audit")
@ApiModel(value = "HrmAppraisalEmployeeResultAudit对象", description = "员工绩效考核-结果确认")
public class HrmAppraisalEmployeeResultAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appraisal_employee_result_audit_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "结果审核id")
    private Long appraisalEmployeeResultAuditId;

    @ApiModelProperty(value = "员工绩效考核id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "审核层级")
    private Integer level;

    @ApiModelProperty(value = "审核层级名称")
    private String levelName;

    @ApiModelProperty(value = "审核类型")
    private Integer auditType;

    @ApiModelProperty(value = "审核人")
    private Long auditPerson;

    @ApiModelProperty(value = "审核状态:0未审核 1已审核 2:驳回 3：待审核")
    private Integer status;

}
