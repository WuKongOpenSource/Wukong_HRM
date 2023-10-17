package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统消息通知配置表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_admin_message_config")
@ApiModel(value="AdminMessageConfig对象", description="系统消息通知配置表")
public class AdminMessageConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "消息推送配置一级key，如线索，客户等")
    private String label;

    @ApiModelProperty(value = "消息推送配置二级key，如导入，转移等")
    private String name;

    @ApiModelProperty(value = "是否开启消息推送 0 否 1 是")
    private Integer pushMessage;

    @ApiModelProperty(value = "是否推送负责人 0 否 1 是")
    private Integer pushOwnerUser;

    @ApiModelProperty(value = "是否推送团队成员  0 否 1 是")
    private Integer pushTermMember;

    @ApiModelProperty(value = "是否推送上级，0为不推送，否则为上级层级")
    private String pushParentUser;

    @ApiModelProperty(value = "是否推送自定义用户，用户id数组，最多10位")
    private String pushCustomUser;

    @ApiModelProperty(value = "是否推送自定义部门，部门id数组，最多10位")
    private String pushCustomDept;

    @ApiModelProperty(value = "是否开启浏览器通知 0 否 1 是")
    private Integer pushBrowser;

    @ApiModelProperty(value = "是否开启短信通知 0 否 1 是")
    private Integer pushSms;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "公司ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;


}
