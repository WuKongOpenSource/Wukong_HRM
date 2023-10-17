package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 绩效考核结果模板
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievements_result_template")
@ApiModel(value = "HrmAchievementsResultTemplate对象", description = "绩效管理-结果模板表")
public class HrmAchievementsResultTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "结果模板id")
    @TableId(value = "result_template_id", type = IdType.ASSIGN_ID)
    private Long resultTemplateId;

    @ApiModelProperty(value = "结果设置名称")
    private String resultTemplateName;

    @ApiModelProperty(value = "等级设置")
    private String levelSetting;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人")
    private Long updateUserId;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "状态：1：正常 0：删除")
    private Integer status;
}
