package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 考核维度
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_dimension")
@ApiModel(value = "HrmAppraisalEmployeeDimension对象", description = "考核维度")
public class HrmAppraisalEmployeeDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "dimension_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "考核维度id")
    private Long dimensionId;

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "允许员工填写true：是 false：否")
    private Boolean isAllowEdit;

    @ApiModelProperty(value = "员工考核计划id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人")
    private Long updateUserId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
