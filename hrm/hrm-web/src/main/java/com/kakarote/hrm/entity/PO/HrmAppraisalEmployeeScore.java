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
 * 员工考核计划-评分
 * </p>
 *
 * @author zyl
 * @since 2022-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_score")
@ApiModel(value = "HrmAppraisalEmployeeScore对象", description = "员工考核计划-评分")
public class HrmAppraisalEmployeeScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "score_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "scoreId")
    private Long scoreId;

    @ApiModelProperty(value = "员工考核计划id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "评语")
    private String comments;

    @ApiModelProperty(value = "评分人id")
    private Long rater;

    @TableField(exist = false)
    @ApiModelProperty(value = "评分人名称")
    private String raterName;

    @ApiModelProperty(value = "得分")
    private Double score;

    @ApiModelProperty(value = "等级")
    private String level;

    @ApiModelProperty(value = "评分权重")
    private Double weight;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
