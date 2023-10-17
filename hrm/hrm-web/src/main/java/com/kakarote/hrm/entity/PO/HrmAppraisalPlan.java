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
 * 考核计划基础信息表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_plan")
@ApiModel(value = "HrmAppraisalPlan对象", description = "考核计划基础信息表")
public class HrmAppraisalPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appraisal_plan_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "聘用形式1 正式 2 非正式")
    private Integer employType;

    @ApiModelProperty(value = "员工类型")
    private String employeeStatus;

    @ApiModelProperty(value = "考核周期类型1:月度 2：季度 3:上半年、4:下半年、5:全年、6:其他")
    private Integer appraisalCycleType;

    @ApiModelProperty(value = "考核周期")
    private String appraisalCycle;

    @ApiModelProperty(value = "季度")
    private String quarter;

    @ApiModelProperty(value = "考核开始时间")
    private Date startTime;

    @ApiModelProperty(value = "考核结束时间")
    private Date endTime;

    @ApiModelProperty(value = "考核说明")
    private String appraisalIllustrate;

    @ApiModelProperty(value = "考核模板id")
    private Long appraisalTemplateId;

    @ApiModelProperty(value = "是否同步到薪资")
    private Boolean syncToSalary;

    @ApiModelProperty(value = "同步薪资月份")
    private String paidForMonth;

    @ApiModelProperty(value = "引用的结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新人")
    private Long updateUserId;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "阶段状态：0：未开始 1：员工填写2：自评分 3：他人评分 4:目标确认5：结果审核6：结果确认7：绩效面谈8：归档")
    private Integer stageStatus;

    @ApiModelProperty(value = "状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档 5:考核终止")
    private Integer status;

    @ApiModelProperty(value = "进行中可操作阶段状态： 1：开启评分 2：发起绩效面谈3：归档")
    private Integer operationStage;
}
