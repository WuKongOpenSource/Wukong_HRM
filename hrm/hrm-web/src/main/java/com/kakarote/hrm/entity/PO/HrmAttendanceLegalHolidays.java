package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
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
 * 考勤法定节假日
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_attendance_legal_holidays")
@ApiModel(value = "HrmAttendanceLegalHolidays对象", description = "考勤法定节假日")
public class HrmAttendanceLegalHolidays implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "holiday_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long holidayId;

    @ApiModelProperty(value = "假期时间")
    private LocalDateTime holidayTime;

    @ApiModelProperty(value = "类型 1.上班 2.休息")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;


}
