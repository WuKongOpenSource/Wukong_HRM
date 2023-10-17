package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("指标设置-维度-指标-展示对象")
public class QuotaSettingDimensionQuotaVO {
    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "考核维度id")
    private Long dimensionId;
}
