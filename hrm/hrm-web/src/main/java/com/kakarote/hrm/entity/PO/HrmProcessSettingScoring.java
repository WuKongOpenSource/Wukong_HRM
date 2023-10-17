package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 考核计划-流程设置-评分流程表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_process_setting_scoring")
@ApiModel(value = "HrmProcessSettingScoring对象", description = "考核计划-流程设置-评分流程表")
public class HrmProcessSettingScoring implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "scoring_process_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "评分流程id")
    private Long scoringProcessId;

    @ApiModelProperty(value = "评分人类型：1上级2：部门负责人3：指定评分人4：被考核人")
    private Integer raterType;

    @ApiModelProperty(value = "评分人级别")
    private Integer raterLevel;

    @ApiModelProperty(value = "评分人")
    private Long rater;

    @ApiModelProperty(value = "权重")
    private Double weight;

    @ApiModelProperty(value = "评分方式")
    private Integer scoringType;

    @ApiModelProperty(value = "可见内容")
    private Integer visibleContent;

    @ApiModelProperty(value = "必填设置 false:关闭 true：开启")
    private Boolean requiredSetting;

    @ApiModelProperty(value = "驳回权限:默认为关闭状态0:关闭 1：开启")
    private Boolean rejectAuthority;

    @ApiModelProperty(value = "层级")
    private Integer sort;

    @ApiModelProperty(value = "流程id")
    private Long processId;

}
