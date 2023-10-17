package com.kakarote.core.feign.email.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailBO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "邮件类型 1：审批邮件，2：抄送邮件,3:催办邮件")
    private Integer emailFlag;

    @ApiModelProperty(value = "发送人昵称")
    private String sender;

    @ApiModelProperty(value = "发送人邮箱")
    private String senderEmail;

    private String receiptName;

    @ApiModelProperty(value = "收件人邮箱集合 逗号分隔")
    private String receiptEmails;

    private String ccName;

    @ApiModelProperty(value = "抄送邮箱 逗号隔开")
    private String ccEmails;

    @ApiModelProperty(value = "主题")
    private String theme;


    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "审核相关的id")
    private Long examineRecordId;

    @ApiModelProperty(value = "审核相关的flowid")
    private Long examineRecordFlowId;

    @ApiModelProperty(value = "审核相关的typeid")
    private Long examineRecordTypeId;

    @ApiModelProperty(value = "审核相关的typeid 类型区分 1员工 2外部联系人")
    private Integer examineRecordTypeIdStyle;

    @ApiModelProperty("审核流程ID")
    private Long examineId;
    @ApiModelProperty("审核类型")
    private Integer label;

}
