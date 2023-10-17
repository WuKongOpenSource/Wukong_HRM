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
 * 绩效考核计划-流程设置-结果确认表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_process_setting_result_confirmation")
@ApiModel(value = "HrmProcessSettingResultConfirmation对象", description = "绩效考核计划-流程设置-结果确认表")
public class HrmProcessSettingResultConfirmation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "result_confirmation_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "结果确认id")
    private Long resultConfirmationId;

    @ApiModelProperty(value = "审核层级")
    private Integer level;

    @ApiModelProperty(value = "确认类型：1上级2：部门负责人3：指定评分人4：被考核人")
    private Integer confirmationType;

    @ApiModelProperty(value = "指定人id")
    private Long designatedUserId;

    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "顺序")
    private Integer sort;
}
