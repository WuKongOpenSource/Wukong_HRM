package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("crm审核对象")
public class ExamineEmailBO {
    @ApiModelProperty("审核流程ID")
    private Long examineId;
    @ApiModelProperty("审核记录ID")
    private Long recordId;
    @ApiModelProperty("审核流程ID")
    private Long flowId;
    @ApiModelProperty("审核类型")
    private Integer label;
    @ApiModelProperty("唯一标识")
    private String emailToken;
    @ApiModelProperty("邮箱账号")
    private String toEmail;
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("审核相关的typeid")
    private Long examineRecordTypeId;
    @ApiModelProperty("审核相关的typeid 类型区分 1员工 2外部联系人")
    private Integer examineRecordTypeIdStyle;

}
