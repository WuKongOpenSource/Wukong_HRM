package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("转交他人处理对象")
public class ExamineForwardOthersBO {
    @ApiModelProperty("当前处理人邮箱")
    private String curHandlerEmail;
    @ApiModelProperty("审核记录Id")
    private Long recordId;
    @ApiModelProperty("最终审批流id")
    private Long flowFinalId;
    @ApiModelProperty("审核记录日志id")
    private Long recordLogId;
    @ApiModelProperty("转交人")
    private ExamineFlowFinalUser examineFlowFinalUser;
}
