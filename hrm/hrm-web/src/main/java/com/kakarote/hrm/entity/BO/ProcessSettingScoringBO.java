package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-流程设置-考核评分流程保存对象")
public class ProcessSettingScoringBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评分人类型：1上级2：部门负责人3：指定评分人4：被考核人")
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

    @ApiModelProperty(value = "驳回权限:默认为关闭状态0:关闭 1：开启")
    private Boolean rejectAuthority;

}
