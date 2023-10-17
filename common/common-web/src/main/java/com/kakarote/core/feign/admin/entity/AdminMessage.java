package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统消息表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_admin_message")
@ApiModel(value="AdminMessage对象", description="系统消息表")
public class AdminMessage implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "消息ID")
    @TableId(value = "message_id")
    private Long messageId;

    @ApiModelProperty(value = "消息标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "消息大类 1 任务 2 日志 3 oa审批 4公告 5 日程 6 crm消息")
    private Integer label;

    @ApiModelProperty(value = "消息类型 详见AdminMessageEnum")
    private Integer type;

    @ApiModelProperty(value = "关联ID")
    private Long typeId;

    @ApiModelProperty(value = "消息创建者 0为系统")
    private Long createUser;

    @ApiModelProperty(value = "接收人")
    private Long recipientUser;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty(value = "是否已读 0 未读 1 已读")
    private Integer isRead;

    @ApiModelProperty(value = "已读时间")
    private LocalDateTime readTime;

    @ApiModelProperty(value = "企业ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}
