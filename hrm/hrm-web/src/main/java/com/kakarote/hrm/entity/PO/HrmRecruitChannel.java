package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 招聘渠道表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_recruit_channel")
@ApiModel(value = "HrmRecruitChannel对象", description = "招聘渠道表")
public class HrmRecruitChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "channel_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long channelId;

    @ApiModelProperty(value = "是否系统默认0 否 1 是")
    private Integer isSys;

    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Integer status;

    private String value;
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "语言包map")
    @TableField(exist = false)
    private Map<String, String> languageKeyMap;


}
