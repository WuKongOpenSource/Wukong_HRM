package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-流程设置-结果审核展示对象")
public class ProcessSettingResultAuditVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "审核层级")
    private Integer level;

    @ApiModelProperty(value = "审核类型")
    private Integer auditType;

    @ApiModelProperty(value = "指定评分人")
    private Long designatedPerson;

}
