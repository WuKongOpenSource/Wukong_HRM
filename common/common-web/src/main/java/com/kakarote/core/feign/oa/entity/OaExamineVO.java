package com.kakarote.core.feign.oa.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.common.entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OaExamineVO {

    @TableId(value = "examine_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long examineId;

    @ApiModelProperty(value = "审批类型")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    @ApiModelProperty(value = "审批类型名称")
    private String categoryType;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "请假类型")
    private String typeId;

    @ApiModelProperty(value = "差旅、报销总金额")
    private BigDecimal money;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时长")
    private BigDecimal duration;

    @ApiModelProperty(value = "创建人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "提交人")
    private UserInfo submitUser;

    @ApiModelProperty(value = "附件批次id")
    private String batchId;

    @ApiModelProperty(value = "公司ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @ApiModelProperty(value = "审核记录ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long examineRecordId;

    @ApiModelProperty("审批状态")
    private Integer examineStatus;

    @ApiModelProperty(value = "附件")
    private String enclosure;

    @ApiModelProperty(value = "提交至")
    private String submitTarget;

    @ApiModelProperty(value = "密级")
    private String classification;

    @ApiModelProperty(value = "紧急程度")
    private String urgency;

    @ApiModelProperty(value = "经办人")
    private List<UserInfo> handledList;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "提交部门")
    private Long submitDeptId;

    @ApiModelProperty(value = "提交时间")
    private Date submitTime;


    @Override
    public String toString() {
        return "OaExamineVO{" +
                "examineId=" + examineId +
                ", categoryId=" + categoryId +
                ", content='" + content + '\'' +
                ", remark='" + remark + '\'' +
                ", typeId='" + typeId + '\'' +
                ", money=" + money +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration +
                ", createUserId=" + createUserId +
                ", batchId='" + batchId + '\'' +
                ", companyId=" + companyId +
                ", examineRecordId=" + examineRecordId +
                ", examineStatus=" + examineStatus +
                '}';
    }
}
