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
 * 薪资组
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_salary_group")
@ApiModel(value = "HrmSalaryGroup对象", description = "薪资组")
public class HrmSalaryGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "薪资组id")
    @TableId(value = "group_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @ApiModelProperty(value = "薪资组名称")
    private String groupName;

    @ApiModelProperty(value = "月计薪标准")
    private String salaryStandard;

    @ApiModelProperty(value = "转正、调薪月规则")
    private String changeRule;

    @ApiModelProperty(value = "计税规则id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
