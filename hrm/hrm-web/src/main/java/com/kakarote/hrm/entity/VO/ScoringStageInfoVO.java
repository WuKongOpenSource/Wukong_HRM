package com.kakarote.hrm.entity.VO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("评分阶段节点信息")
@Data
public class ScoringStageInfoVO {
    @ApiModelProperty("阶段id")
    private Long stageId;
    @ApiModelProperty("阶段处理人名称")
    private String stageUserName;
}
