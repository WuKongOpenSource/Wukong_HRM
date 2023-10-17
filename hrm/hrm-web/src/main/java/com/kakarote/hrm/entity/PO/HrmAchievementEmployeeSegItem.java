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
 * 员工考核项选项
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_achievement_employee_seg_item")
@ApiModel(value = "HrmAchievementEmployeeSegItem对象", description = "员工考核项选项")
public class HrmAchievementEmployeeSegItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "item_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long segId;

    @ApiModelProperty("模板考核子项项id(新添加的是0)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tempItemId;

    @ApiModelProperty(value = "选项名称")
    private String itemName;

    @ApiModelProperty(value = "值")
    private String value;

    private Integer sort;

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
}
