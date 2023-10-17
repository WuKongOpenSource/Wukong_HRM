package com.kakarote.core.feign.oa.entity;

import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工作日志
 * </p>
 *
 * @author zyy
 * @since 2020-07-01
 */
@Data
public class OaLogVO implements Serializable {

    private Long logId;

    @ApiModelProperty(value = "日志类型（1日报，2周报，3月报）")
    private Long categoryId;

    @ApiModelProperty(value = "日志标题")
    private String title;

    @ApiModelProperty(value = "日志内容")
    private String content;

    @ApiModelProperty(value = "明日工作内容")
    private String tomorrow;

    @ApiModelProperty(value = "遇到问题")
    private String question;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "创建人信息")
    private SimpleUser createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "通知人")
    private String sendUserIds;

    @ApiModelProperty(value = "通知部门")
    private String sendDeptIds;

    @ApiModelProperty(value = "已读人")
    private String readUserIds;

    @ApiModelProperty(value = "文件批次ID")
    private String batchId;

    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

}
