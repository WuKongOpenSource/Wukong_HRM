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
 * 考核计划-流程配置-基础表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan_process_setting")
@ApiModel(value = "HrmAppraisalPlanProcessSetting对象", description = "考核计划-流程配置-基础表")
public class HrmAppraisalPlanProcessSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "process_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "指标制定类型1:统一制定2：员工填写")
    private Integer quotaSettingType;

    @ApiModelProperty(value = "目标确认：1：开启 0:未开启")
    private Boolean targetConfirmation;

    @ApiModelProperty(value = "结果审核")
    private Boolean resultAudit;

    @ApiModelProperty(value = "结果确认")
    private Boolean resultConfirmation;

    @ApiModelProperty(value = "超期未处理类型")
    private Integer beOverdueType;

    @ApiModelProperty(value = "超期未处理天数")
    private Integer overdueDays;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

}
