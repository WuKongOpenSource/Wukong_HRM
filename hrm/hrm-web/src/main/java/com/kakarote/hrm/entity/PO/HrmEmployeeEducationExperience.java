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
 * 员工教育经历
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_education_experience")
@ApiModel(value = "HrmEmployeeEducationExperience对象", description = "员工教育经历")
@RangeValidated(minFieldName = "admissionTime", maxFieldName = "graduationTime", message = "入学时间大于毕业时间")
public class HrmEmployeeEducationExperience implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "education_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long educationId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "学历 1小学、2初中、3中专、4中职、5中技、6高中、7大专、8本科、9硕士、10博士、11博士后、12其他")
    private Integer education;

    @ApiModelProperty(value = "毕业院校")
    private String graduateSchool;

    @ApiModelProperty(value = "专业")
    private String major;

    @ApiModelProperty(value = "入学时间")
    private LocalDate admissionTime;

    @ApiModelProperty(value = "毕业时间")
    private LocalDate graduationTime;

    @ApiModelProperty(value = "教学方式 1 全日制、2成人教育、3远程教育、4自学考试、5其他")
    private Integer teachingMethods;

    @ApiModelProperty(value = "是否第一学历 0 否 1 是")
    private Integer isFirstDegree;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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
