package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("考核计划考核等级数量统计")
public class HrmAppraisalPlanLevelNumVO {
    @ApiModelProperty("等级名称")
    private String levelName;
    @ApiModelProperty("等级数量")
    private Integer count;
}
