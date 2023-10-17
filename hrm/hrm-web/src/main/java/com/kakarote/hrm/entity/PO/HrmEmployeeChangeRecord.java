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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工岗位/职位变更记录
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_change_record")
@ApiModel(value = "HrmEmployeeChangeRecord对象", description = "员工岗位/职位变更记录")
public class HrmEmployeeChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recordId;

    @ApiModelProperty(value = "员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "变动类型 4 转正 5调岗 6晋升 7降级 8转为全职员工")
    private Integer changeType;

    @ApiModelProperty(value = "异动原因 1 组织架构调整 2个人申请 3 工作安排 4 违规违纪 5 绩效不达标 6 个人身体原因 7 不适应当前岗位")
    private Integer changeReason;

    @ApiModelProperty(value = "原部门")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long oldDept;

    @ApiModelProperty(value = "新部门")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long newDept;

    @ApiModelProperty(value = "原岗位")
    private String oldPost = "";

    @ApiModelProperty(value = "新岗位")
    private String newPost = "";

    @ApiModelProperty(value = "新职级")
    private String oldPostLevel = "";

    @ApiModelProperty(value = "新职级")
    private String newPostLevel = "";

    @ApiModelProperty(value = "原工作地点")
    private String oldWorkAddress = "";

    @ApiModelProperty(value = "新工作地点")
    private String newWorkAddress = "";

    @ApiModelProperty(value = "原直属上级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long oldParentId;

    @ApiModelProperty(value = "新直属上级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long newParentId;

    @ApiModelProperty(value = "试用期")
    private Integer probation;

    @ApiModelProperty(value = "生效时间")
    private LocalDate effectTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("备注")
    @TableField(exist = false)
    private String remarks;


}
