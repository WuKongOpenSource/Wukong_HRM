package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HrmActionRecordBO {


    @ApiModelProperty("记录内容")
    private String content;

    @ApiModelProperty("翻译记录内容")
    private String transContent;
}
