package com.kakarote.core.feign.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zyy
 * 活动记录保存
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "活动记录保存", description = "活动记录保存")
@ToString
public class CrmActivityBO implements Serializable {

    public CrmActivityBO() {
    }

    public CrmActivityBO(Integer type, Integer activityType, Long activityTypeId, ActivityContent activityContent) {
        this.type = type;
        this.activityType = activityType;
        this.activityTypeId = activityTypeId;
        this.activityContent = activityContent;

    }

    @ApiModelProperty(value = "活动类型")
    private Integer type;

    @ApiModelProperty(value = "活动模块类型")
    private Integer activityType;

    @ApiModelProperty(value = "活动数据ID")
    private Long activityTypeId;

    @ApiModelProperty(value = "活动详情")
    private ActivityContent activityContent;

    @ApiModelProperty(value = "关联业务 客户")
    private List<Long> customerIds;

    @ApiModelProperty(value = "关联业务 联系人")
    private List<Long> contactsIds;

    @ApiModelProperty(value = "关联业务 商机")
    private List<Long> businessIds;

    @ApiModelProperty(value = "关联业务 合同")
    private List<Long> contractIds;

    @ApiModelProperty(value = "关联业务 回款")
    private List<Long> receivablesIds;

    @ApiModelProperty(value = "关联业务 产品")
    private List<Long> productIds;

    @ApiModelProperty(value = "下次联系时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextTime;

    @ApiModelProperty(value = "0 删除 1 正常")
    private Integer status;

    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "批次id")
    private String batchId;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "日期类型，前端需要")
    private Integer timeType;

    @ApiModelProperty(value = "是否有关联数据   0 没有  1 有")
    private Integer isRelation;

    @ApiModelProperty(value = "关联模块")
    private List<Long> moduleIds;
}
