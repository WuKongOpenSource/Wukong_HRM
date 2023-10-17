package com.kakarote.core.feign.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动详情实体类
 * @author zyy
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "活动记录保存", description = "活动记录保存")
@ToString
public class ActivityContent implements Serializable {

    public ActivityContent() {
    }

    /**
     * 普通模块新建
     */
    public ActivityContent(Long id, String name, Integer contentType) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
    }

    public ActivityContent(String name, Integer contentType , String content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;

    }

    /**
     * 正常审批 & 日志
     */
    public ActivityContent(String name, Integer checkStatus, Integer type,Integer contentType, String content) {
        this.name = name;
        this.checkStatus = checkStatus;
        this.type = type;
        this.contentType = contentType;
        this.content = content;

    }

    /**
     * 出差
     */
    public ActivityContent(String name, Integer checkStatus, Integer type, Integer contentType, String content, BigDecimal duration, String ... a) {
        this.name = name;
        this.checkStatus = checkStatus;
        this.type = type;
        this.contentType = contentType;
        this.content = content;
        this.duration = duration;
    }

    /**
     * 差旅报销
     */
    public ActivityContent(String name,  Integer checkStatus, Integer type,Integer contentType, String content, BigDecimal money) {
        this.name = name;
        this.checkStatus = checkStatus;
        this.type = type;
        this.contentType = contentType;
        this.content = content;
        this.money = money;
    }

    /**
     * 加班
     */
    public ActivityContent(String name, Integer checkStatus, Integer type, Integer contentType, String content, String startTime, String endTime) {
        this.name = name;
        this.checkStatus = checkStatus;
        this.type = type;
        this.contentType = contentType;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @ApiModelProperty(value = "反推来源id")
    private Long id;


    @ApiModelProperty(value = "活动名称")
    private String name;


    @ApiModelProperty(value = "审批状态")
    private Integer checkStatus;


    @ApiModelProperty(value = "审批类型 1 普通审批 2 请假审批 3 出差审批 4 加班审批 5 差旅报销 6 借款申请 0 自定义审批")
    private Integer type;


    @ApiModelProperty(value = "冗余活动类型 1 线索 2 客户 3 联系人 4 产品 5 商机 6 合同 7回款 8日志 9审批 10日程 11任务 12 发邮件 14 回款计划")
    private Integer contentType;


    @ApiModelProperty(value = "内容   日志：日志内容     审批：出差-差旅事由  加班-加班原因  差旅报销-差旅事由   加班-加班原因")
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


    @ApiModelProperty(value = "金额")
    private BigDecimal money;


    @ApiModelProperty(value = "手机号")
    private String mobile;


    @ApiModelProperty(value = "编号")
    private String num;


    @ApiModelProperty(value = "阶段组名称")
    private String flowName;


    @ApiModelProperty(value = "职务")
    private String post;

    @ApiModelProperty(value = "负责人")
    private Long ownerUserId;
}
