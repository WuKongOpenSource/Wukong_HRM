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
 * 社保方案表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_insurance_scheme")
@ApiModel(value = "HrmInsuranceScheme对象", description = "社保方案表")
public class HrmInsuranceScheme implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社保方案id")
    @TableId(value = "scheme_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long schemeId;

    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    @ApiModelProperty(value = "参保城市")
    private String city;

    @ApiModelProperty(value = "户籍类型")
    private String houseType;

    @ApiModelProperty(value = "参保类型 1 比例 2 金额")
    private Integer schemeType;

    @ApiModelProperty(value = "1 删除 0 使用")
    private Integer isDel;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
