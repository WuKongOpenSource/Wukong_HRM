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
 * 打卡地址表
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_attendance_point")
@ApiModel(value = "HrmAttendancePoint对象", description = "打卡地址表")
public class HrmAttendancePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "打卡地点id")
    @TableId(value = "attendance_point_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendancePointId;

    @ApiModelProperty(value = "考勤组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceGroupId;

    @ApiModelProperty(value = "地点名称")
    private String name;

    @ApiModelProperty(value = "定位名称")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "经度")
    private String lng;

    @ApiModelProperty(value = "范围（米）")
    private Integer pointRange;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
