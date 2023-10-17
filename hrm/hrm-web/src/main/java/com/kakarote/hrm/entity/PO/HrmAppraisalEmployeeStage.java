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
 *
 * </p>
 *
 * @author zyl
 * @since 2022-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_stage")
@ApiModel(value = "HrmAppraisalEmployeeStage对象", description = "")
public class HrmAppraisalEmployeeStage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appraisal_stage_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "绩效考核阶段id")
    private Long appraisalStageId;

    @ApiModelProperty(value = "员工绩效id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "考核员工id")
    private Long employeeId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "1阶段状态：0：未开始 1：员工填写2：自评分 3：他人评分 4:目标确认5：结果审核6：结果确认 7：归档")
    private Integer stageType;

    @ApiModelProperty(value = "阶段名称")
    private String stageName;

    @ApiModelProperty(value = "当前阶段最后处理人")
    private Long stageUserId;

    @ApiModelProperty(value = "评分权重：仅用于阶段类型是评分阶段")
    private Double weight;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前阶段最后处理人名称")
    private String stageUserName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：0：未处理 1已处理 2待处理 3：驳回 4：重新处理")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
