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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 员工绩效考核项
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_employee_seg")
@ApiModel(value = "HrmAchievementEmployeeSeg对象", description = "员工绩效考核项")
public class HrmAchievementEmployeeSeg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "seg_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long segId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeAppraisalId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty("模板考核项id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tempSegId;

    @ApiModelProperty(value = "考核项名称")
    private String segName;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "是否固定 1 是 0 否")
    private Integer isFixed;

    @ApiModelProperty(value = "权重 -1 员工写权重比 0~100")
    private BigDecimal weight;

    @ApiModelProperty(value = "目标进度")
    private Integer schedule;

    @ApiModelProperty(value = "完成情况说明")
    private String explainDesc;

    private Integer sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "考核子项")
    private List<HrmAchievementEmployeeSegItem> items;

    @TableField(exist = false)
    @ApiModelProperty(value = "考核项评分")
    private List<HrmAchievementEmployeeEvaluatoSeg> evaluatoSegList;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
