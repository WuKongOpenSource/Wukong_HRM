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

/**
 * <p>
 * 考评规则等级
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_appraisal_score_level")
@ApiModel(value = "HrmAchievementAppraisalScoreLevel对象", description = "考评规则等级")
public class HrmAchievementAppraisalScoreLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "level_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long levelId;

    @ApiModelProperty(value = "考核id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appraisalId;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "最小分数")
    private BigDecimal minScore;

    @ApiModelProperty(value = "最大分数")
    private BigDecimal maxScore;

    @ApiModelProperty(value = "最小人数比例")
    private Integer minNum;

    @ApiModelProperty(value = "最大人数比例")
    private Integer maxNum;

    private Integer sort;

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
