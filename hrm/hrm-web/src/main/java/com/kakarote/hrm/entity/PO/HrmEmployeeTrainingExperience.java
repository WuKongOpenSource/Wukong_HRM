package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.core.common.RangeValidated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 培训经历
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_training_experience")
@ApiModel(value = "HrmEmployeeTrainingExperience对象", description = "培训经历")
@RangeValidated
public class HrmEmployeeTrainingExperience implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "training_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long trainingId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "培训课程")
    private String trainingCourse;

    @ApiModelProperty(value = "培训机构名称")
    private String trainingOrganName;

    @ApiModelProperty(value = "培训开始时间")
    private LocalDate startTime;

    @ApiModelProperty(value = "培训结束时间")
    private LocalDate endTime;

    @ApiModelProperty(value = "培训时长")
    private String trainingDuration;

    @ApiModelProperty(value = "培训成绩")
    private String trainingResults;

    @ApiModelProperty(value = "培训课程名称")
    private String trainingCertificateName;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "排序")
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
