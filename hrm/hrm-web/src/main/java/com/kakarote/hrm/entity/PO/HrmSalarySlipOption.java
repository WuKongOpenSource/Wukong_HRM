package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工资条工资项
 * </p>
 *
 * @author hmb
 * @since 2020-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_salary_slip_option")
@ApiModel(value = "HrmSalarySlipOption对象", description = "工资条工资项")
public class HrmSalarySlipOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "工资条id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long slipId;

    @ApiModelProperty(value = "薪资项名称")
    private String name;

    @ApiModelProperty(value = "选项类型 1 分类 2 薪资项")
    private Integer type;

    @ApiModelProperty(value = "薪资项code")
    private Integer code;

    @ApiModelProperty(value = "薪资项value")
    private String value;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "父级分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid;

    @ApiModelProperty(value = "排序")
    private Integer sort;

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

    @TableField(exist = false)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<HrmSalarySlipOption> optionList;

    @ApiModelProperty(value = "语言包map")
    @TableField(exist = false)
    private Map<String, String> languageKeyMap;

}
