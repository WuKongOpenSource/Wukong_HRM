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
 * 员工绩效考核-考核绩效基本信息
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee")
@ApiModel(value = "HrmAppraisalEmployee对象", description = "员工绩效考核-考核绩效基本信息")
public class HrmAppraisalEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appraisal_employee_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "绩效考核员工考核信息id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核员工id")
    private Long employeeId;

    @ApiModelProperty(value = "状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档 5:考核终止")
    private Integer appraisalStatus;

    @ApiModelProperty(value = "当前阶段处理状态：1进行中 2：已完成")
    private Integer activateStatus;

    @ApiModelProperty(value = "阶段状态：0：未开始 1：员工填写2：自评分 3：他人评分 4:目标确认5：结果审核6：结果确认7：申诉确认8：归档")
    private Integer stageStatus;

    @ApiModelProperty(value = "当前员工考核计划阶段进度")
    private Integer stageSort;

    @ApiModelProperty(value = "评分")
    private Integer score;

    @ApiModelProperty(value = "考核等级")
    private String level;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "归档时间")
    private Date fileTime;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新人")
    private Long updateUserId;

}
