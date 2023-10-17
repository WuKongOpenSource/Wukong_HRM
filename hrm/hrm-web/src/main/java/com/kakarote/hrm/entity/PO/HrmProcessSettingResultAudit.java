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
 * KIP考核计划-流程设置-结果审核表
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_process_setting_result_audit")
@ApiModel(value = "HrmProcessSettingResultAudit对象", description = "KIP考核计划-流程设置-结果审核表")
public class HrmProcessSettingResultAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "result_audit_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "结果审核id")
    private Long resultAuditId;

    @ApiModelProperty(value = "审核层级")
    private Integer level;

    @ApiModelProperty(value = "审核类型")
    private Integer auditType;

    @ApiModelProperty(value = "指定评分人")
    private Long designatedPerson;

    @ApiModelProperty(value = "流程id")
    private Long processId;

    @ApiModelProperty(value = "顺序")
    private Integer sort;
}
