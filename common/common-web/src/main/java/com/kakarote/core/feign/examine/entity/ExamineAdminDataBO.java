package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @创建人 xuepengfei
 * @创建时间 2023/5/24
 * @描述
 */

@Data
@ApiModel("crm审核高级配置对象")
public class ExamineAdminDataBO {

    @ApiModelProperty(value = "审批记录ID")
    private Long recordId;

    @ApiModelProperty("审核流程ID")
    private Long flowId;

    private List<ExamineFlowVO> examineFlowVO;


}
