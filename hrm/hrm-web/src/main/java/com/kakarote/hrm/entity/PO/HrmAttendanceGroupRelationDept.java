package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 考勤组关联部门表
 * </p>
 *
 * @author guomenghao
 * @since 2021-11-26
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@TableName("wk_hrm_attendance_group_relation_dept")
@ApiModel(value = "HrmAttendanceGroupRelationDept对象", description = "考勤组关联部门表")
public class HrmAttendanceGroupRelationDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "attendance_group_relation_dept_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceGroupRelationDeptId;

    @ApiModelProperty("考勤组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attendanceGroupId;

    @ApiModelProperty("部门id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectTime;

    @ApiModelProperty("创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新人id")
    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
