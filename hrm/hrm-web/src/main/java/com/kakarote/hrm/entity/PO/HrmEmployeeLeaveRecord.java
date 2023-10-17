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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工请假记录
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_leave_record")
@ApiModel(value = "HrmEmployeeLeaveRecord对象", description = "员工请假记录")
public class HrmEmployeeLeaveRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "请假记录id")
    @TableId(value = "leave_record_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long leaveRecordId;

    @ApiModelProperty(value = "员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "审批id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long examineId;

    @ApiModelProperty(value = "请假类型")
    private String leaveType;

    @ApiModelProperty(value = "请假开始时间")
    private LocalDateTime leaveStartTime;

    @ApiModelProperty(value = "请假结束时间")
    private LocalDateTime leaveEndTime;

    @ApiModelProperty(value = "请假时长")
    private BigDecimal leaveDay;

    @ApiModelProperty(value = "请假理由")
    private String leaveReason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
