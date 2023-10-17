package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("crm审核高级配置节点限时处理对象")
public class ExamineAdvancedConfigLimitTimeVO {
    @ApiModelProperty(value = "限时处理id")
    private Long limitTimeHandleId;

    @ApiModelProperty(value = "审核id")
    private Long examineId;

    @ApiModelProperty(value = "高级配置id")
    private Long advancedConfigId;

    @ApiModelProperty(value = "处理方式：1自动提醒，2自动转交 3自动同意")
    private Integer handleType;

    @ApiModelProperty(value = "审批方式： 1 依次审批 2 会签 3 或签")
    private Integer examineType;

    @ApiModelProperty(value = "超多少分钟自动处理")
    private Double limitTime;

    @ApiModelProperty(value = "超多少时间自动处理-单位:minute:分钟，hour小时，day:天")
    private String limitTimeUnit;

    @ApiModelProperty(value = "处理人员列表")
    private List<Map<String,Object>> handleUserList;

}
