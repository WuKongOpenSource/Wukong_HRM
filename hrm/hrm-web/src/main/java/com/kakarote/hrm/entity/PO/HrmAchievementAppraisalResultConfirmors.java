package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 考核结果确认人
 * </p>
 *
 * @author guomenghao
 * @since 2021-12-26
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@TableName("wk_hrm_achievement_appraisal_result_confirmors")
@ApiModel(value = "HrmAchievementAppraisalResultConfirmors对象", description = "考核结果确认人")
public class HrmAchievementAppraisalResultConfirmors implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "confirmors_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = StringSerializer.class)
    private Long confirmorsId;

    @ApiModelProperty("员工id")
    @JsonSerialize(using = StringSerializer.class)
    private Long employeeId;

    @ApiModelProperty("绩效id")
    @JsonSerialize(using = StringSerializer.class)
    private Long appraisalId;

    @ApiModelProperty("创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = StringSerializer.class)
    private Long createUserId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新人id")
    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = StringSerializer.class)
    private Long updateUserId;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
