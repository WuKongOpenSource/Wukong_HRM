package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("消息内容保存信息")
public class AdminMessageContentBO {


    @ApiModelProperty(value = "通知名称")
    private String name;


    @ApiModelProperty(value = "通知名称")
    private String examineName;


    @ApiModelProperty(value = "审批状态")
    private Integer checkStatus;


    @ApiModelProperty(value = "请假类型 和 日程类型")
    private Object type;

    @ApiModelProperty(value = "内容")
    private Object content;


    @ApiModelProperty(value = "出差地点")
    private String endAddress;


    @ApiModelProperty(value = "出差总天数")
    private BigDecimal duration;


    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String startTime;


    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String endTime;


    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String createTime;


    @ApiModelProperty(value = "金额")
    private BigDecimal money;


    @ApiModelProperty(value = "手机号")
    private String mobile;


    @ApiModelProperty(value = "备注  或  拒绝理由")
    private String remarks;


    @ApiModelProperty(value = "客户名称")
    private Object customer;

    @ApiModelProperty(value = "合同名称")
    private Object contract;

    @ApiModelProperty(value = "产品")
    private Object products;


    @ApiModelProperty(value = "关联客户")
    @TableField(exist = false)
    private String customers;

    @ApiModelProperty(value = "关联联系人")
    @TableField(exist = false)
    private String contactss;

    @ApiModelProperty(value = "关联商机")
    @TableField(exist = false)
    private String businesss;

    @ApiModelProperty(value = "关联合同")
    @TableField(exist = false)
    private String contracts;

    @ApiModelProperty("申请原因")
    @TableField(exist = false)
    private String examineReason;

}
