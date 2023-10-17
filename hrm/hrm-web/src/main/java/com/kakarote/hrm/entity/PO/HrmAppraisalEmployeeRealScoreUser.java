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
 * @since 2022-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_real_score_user")
@ApiModel(value = "HrmAppraisalEmployeeRealScoreUser对象", description = "")
public class HrmAppraisalEmployeeRealScoreUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "real_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private Long realId;

    @ApiModelProperty(value = "评分人id")
    private Long scoreUserId;

    @ApiModelProperty(value = "评分权重")
    private Double weight;

    @ApiModelProperty(value = "是否必填")
    private Boolean requiredSetting;

    @ApiModelProperty(value = "驳回权限")
    private Boolean rejectAuthority;

    @ApiModelProperty(value = "可见内容")
    private Integer visibleContent;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "考核员工id")
    private Long employeeId;

    @ApiModelProperty(value = "排序")
    private Integer stageSort;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
