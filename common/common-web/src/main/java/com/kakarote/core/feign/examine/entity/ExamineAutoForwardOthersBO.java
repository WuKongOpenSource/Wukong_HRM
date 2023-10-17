package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("自动转交他人处理对象")
public class ExamineAutoForwardOthersBO {
    @ApiModelProperty("当前处理人邮箱")
    private String curHandlerEmail;
    @ApiModelProperty("审核记录Id")
    private Long recordId;
    @ApiModelProperty("最终审批流id")
    private Long flowFinalId;
    @ApiModelProperty("审核记录日志id")
    private Long recordLogId;
    @ApiModelProperty("审核类型1 依次审批 2 会签 3 或签")
    private Integer type;
    @ApiModelProperty("详见PassFlagEnum")
    private Integer auditPassFlag=0;
    @ApiModelProperty("转交人")
    private List<ExamineFlowFinalUser> examineFlowFinalUserList;
}
