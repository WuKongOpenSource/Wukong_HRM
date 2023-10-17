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

/**
 * <p>
 * 员工异常表动记录表（薪资列表统计需要）
 * </p>
 *
 * @author huangmingbo
 * @since 2020-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_abnormal_change_record")
@ApiModel(value = "HrmEmployeeAbnormalChangeRecord对象", description = "员工异常表动记录表（薪资列表统计需要）")
public class HrmEmployeeAbnormalChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "change_record_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long changeRecordId;

    @ApiModelProperty(value = "异动类型 1 新入职 2 离职 3 转正 4 调岗")
    private Integer type;

    @ApiModelProperty(value = "异动员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "异动时间")
    private LocalDateTime changeTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
