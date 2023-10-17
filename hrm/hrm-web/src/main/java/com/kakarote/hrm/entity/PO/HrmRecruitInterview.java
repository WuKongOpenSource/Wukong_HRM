package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_recruit_interview")
@ApiModel(value = "HrmRecruitInterview对象", description = "面试表")
public class HrmRecruitInterview implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "面试id")
    @TableId(value = "interview_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long interviewId;

    @ApiModelProperty(value = "候选人id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long candidateId;

    @ApiModelProperty(value = "面试方式 1现场面试 2电话面试 3视频面试")
    @TableField(value = "type")
    private Integer type;

    @ApiModelProperty(value = "面试轮次")
    private Integer stageNum;

    @ApiModelProperty(value = "面试官id")
    @TableField(value = "interview_employee_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long interviewEmployeeId;

    @ApiModelProperty(value = "其他面试官")
    @TableField(value = "other_interview_employee_ids")
    private String otherInterviewEmployeeIds;

    @ApiModelProperty(value = "面试时间")
    private LocalDateTime interviewTime;

    @ApiModelProperty(value = "面试地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "面试情况 1面试未完成 2面试通过 3面试未通过 4 面试取消")
    private Integer result;

    @ApiModelProperty(value = "评价")
    private String evaluate;

    @ApiModelProperty(value = "取消原因")
    private String cancelReason;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
