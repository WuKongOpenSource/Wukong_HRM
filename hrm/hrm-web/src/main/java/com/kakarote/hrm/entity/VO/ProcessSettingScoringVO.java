package com.kakarote.hrm.entity.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-流程设置-考核评分流程展示对象")
public class ProcessSettingScoringVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评分人类型")
    private Integer raterType;

    @ApiModelProperty(value = "评分人级别")
    private Integer raterLevel;

    @ApiModelProperty(value = "评分人")
    private Long rater;

    @ApiModelProperty(value = "权重")
    private Double weight;

    @ApiModelProperty(value = "评分方式")
    private Integer scoringType;

    @ApiModelProperty(value = "可见内容")
    private Integer visibleContent;

    @ApiModelProperty(value = "必填设置0:关闭 1：开启")
    private Boolean requiredSetting;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "驳回权限:默认为关闭状态0:关闭 1：开启")
    private Boolean rejectAuthority;
}
