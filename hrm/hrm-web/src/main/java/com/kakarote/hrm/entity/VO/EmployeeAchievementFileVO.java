package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("员工绩效档案返回对象")
public class EmployeeAchievementFileVO {

    @ApiModelProperty(value = "绩效档案id")
    private Long achievementFileId;

    @ApiModelProperty(value = "员工Id")
    private String employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门名称")
    private String post;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "员工状态")
    private Integer status;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "最近员工考核绩效id")
    private Long recentlyAppraisalEmployeeId;

    @ApiModelProperty(value = "最近考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty(value = "最近绩效评分")
    private Double recentlyScore;

    @ApiModelProperty(value = "最近绩效等级")
    private String recentlyLevel;

    @ApiModelProperty(value = "考核次数")
    private Integer appraisalCount;

    @ApiModelProperty(value = "系数")
    private Double coefficient;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
