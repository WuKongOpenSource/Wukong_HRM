package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("指标类型")
public class QuoteTypeVO {
    private Integer quoteType;
    private String quoteTypeName;
}
