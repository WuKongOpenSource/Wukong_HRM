package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("考核计划指标设置展示对象")
@Data
public class AppraisalPlanQuotaSettingVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指标id")
    private Long quotaSettingId;

    @ApiModelProperty(value = "总分计算")
    private Integer scoreCalculation;

    @ApiModelProperty(value = "评分上限类型")
    private Integer upperLimitType;

    @ApiModelProperty(value = "评分上限")
    private Integer upperLimitScore;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新人")
    private Long updateUserId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalPlanId;

    @ApiModelProperty(value = "指标设置-维度列表")
    private List<QuotaSettingDimensionVO> quotaSettingDimensionVOList;
}
