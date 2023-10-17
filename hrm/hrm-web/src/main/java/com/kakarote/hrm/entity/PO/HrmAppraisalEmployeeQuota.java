package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 员工绩效考核-填写指标项
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_quota")
@ApiModel(value = "HrmAppraisalEmployeeQuota对象", description = "员工绩效考核-填写指标项")
public class HrmAppraisalEmployeeQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "quota_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "员工填写指标id")
    private Long quotaId;

    @ApiModelProperty(value = "考核计划id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "维度id")
    private Long dimensionId;

    @ApiModelProperty(value = "指标名称")
    private String quotaName;

    @ApiModelProperty(value = "指标说明")
    private String quotaIllustrate;

    @ApiModelProperty(value = "考核标准")
    private String standard;

    @ApiModelProperty(value = "指标权重")
    private Double quotaWeight;

    @ApiModelProperty(value = "评分方式")
    private Integer scoreType;

    @ApiModelProperty(value = "是否系统预设1：是 0：否")
    private Boolean preset;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
