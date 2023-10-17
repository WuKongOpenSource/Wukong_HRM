package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("指标设置维度-保存对象")
public class QuotaSettingDimensionBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "允许员工填写1：是 0：否")
    private Boolean isAllowEdit;

    @ApiModelProperty(value = "指标设置维度-指标列表")
    private List<QuotaSettingDimensionQuotaBO> quotaSettingDimensionQuotaBOList;
}
