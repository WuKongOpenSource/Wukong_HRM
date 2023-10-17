package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("结果审核对象")
public class ResultAuditBO {
    @ApiModelProperty("员工绩效考核id")
    private Long appraisalEmployeeId;

    @ApiModelProperty("文件batchId")
    private String batchId;

    @ApiModelProperty("评分节点列表")
    private List<Long> appraisalStageIdList;

    @ApiModelProperty("原因")
    private String reason;
}
