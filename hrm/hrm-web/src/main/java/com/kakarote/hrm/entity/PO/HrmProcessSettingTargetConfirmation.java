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
 *
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_process_setting_target_confirmation")
@ApiModel(value = "HrmProcessSettingTargetConfirmation对象", description = "")
public class HrmProcessSettingTargetConfirmation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "confirmation_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Long confirmationId;

    @ApiModelProperty(value = "确认类型：1上级2：部门负责人3：指定评分人4：被考核人")
    private Integer confirmationType;

    @ApiModelProperty(value = "确认级别")
    private Integer confirmationLevel;

    @ApiModelProperty(value = "确认人")
    private Long identifyingPeople;

    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "确认顺序")
    private Integer sort;

}
