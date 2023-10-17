package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 员工绩效考核申诉确认待处理节点记录
 * </p>
 *
 * @author zyl
 * @since 2022-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_appeal_pending_record")
@ApiModel(value = "HrmAppraisalAppealPendingRecord对象", description = "员工绩效考核申诉确认待处理节点记录")
public class HrmAppraisalAppealPendingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pending_record_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "待处理记录id")
    private Long pendingRecordId;

    @ApiModelProperty(value = "员工考核绩效id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "员工考核绩效id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "待处理节点id")
    private Long stageId;

    @ApiModelProperty(value = "超期未处理类型1：超期未审批拒绝2：超期未审批通过")
    private Integer overdueType;

    @ApiModelProperty(value = "逾期时间")
    private LocalDateTime overdueTime;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
