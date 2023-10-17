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
 * 员工加班表
 * </p>
 *
 * @author guomenghao
 * @since 2021-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_over_time_record")
@ApiModel(value = "HrmEmployeeOverTimeRecord对象", description = "员工加班表")
public class HrmEmployeeOverTimeRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "加班记录id")
    @TableId(value = "over_time_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long overTimeId;

    @ApiModelProperty(value = "员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "加班类型 1工作日加班 2休息日加班")
    private Integer overTimeType;

    @ApiModelProperty(value = "加班开始时间")
    private LocalDateTime overTimeStartTime;

    @ApiModelProperty(value = "加班结束时间")
    private LocalDateTime overTimeEndTime;

    @ApiModelProperty(value = "上班时间")
    private LocalDateTime attendanceTime;

    @ApiModelProperty(value = "加班时长")
    private BigDecimal overTimes;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
