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
 * 员工绩效档案
 * </p>
 *
 * @author zyl
 * @since 2022-06-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_achievement_file")
@ApiModel(value = "HrmEmployeeAchievementFile对象", description = "员工绩效档案")
public class HrmEmployeeAchievementFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "achievement_file_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "绩效档案id")
    private Long achievementFileId;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "最近员工考核绩效id")
    private Long recentlyAppraisalEmployeeId;

    @ApiModelProperty(value = "考核次数")
    private Integer appraisalCount;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
