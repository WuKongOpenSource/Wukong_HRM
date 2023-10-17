package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("新增审核记录")
@AllArgsConstructor
@NoArgsConstructor
public class ExamineRecordSaveBO {

    @ApiModelProperty("对应类型")
    private Integer label;

    @ApiModelProperty("对应类型ID")
    private Long typeId;

    @ApiModelProperty("审批记录ID")
    private Long recordId;

    @ApiModelProperty("OA专用 审批ID")
    private Long categoryId;

    @ApiModelProperty("数据map")
    private Map<String,Object> dataMap;

    @ApiModelProperty("最终流程")
    private List<ExamineFlowFinalBO> examineFlowFinalBOList;

    @ApiModelProperty("消息标题")
    private String title;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty(value = "申请原因")
    private String examineReason;

    @ApiModelProperty("是否发送消息（0：不发送 1：发送）")
    private Integer isSendMessage;

    @ApiModelProperty(value = "审批节点数据")
    private List<ExamineDataSaveBO> flowDataList;
}
