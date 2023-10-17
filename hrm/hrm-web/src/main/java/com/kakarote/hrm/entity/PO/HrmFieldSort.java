package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 字段排序表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-19
 */
@Data
@Accessors(chain = true)
@TableName("wk_hrm_field_sort")
@ApiModel(value = "HrmFieldSort对象", description = "字段排序表")
public class HrmFieldSort implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "员工考核 5")
    private Integer label;

    @ApiModelProperty(value = "字段类型")
    private Integer type;

    @ApiModelProperty(value = "字段宽度")
    private Integer style;

    @ApiModelProperty(value = "字段排序")
    private Integer sort;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "是否隐藏 0、不隐藏 1、隐藏")
    private Integer isHide;

    @ApiModelProperty(value = "是否锁定")
    private Integer isLock;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HrmFieldSort that = (HrmFieldSort) o;
        return fieldName.equals(that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName);
    }
}
