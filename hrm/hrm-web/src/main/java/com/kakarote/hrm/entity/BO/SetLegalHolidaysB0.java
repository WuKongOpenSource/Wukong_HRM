package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SetLegalHolidaysB0 {

    @ApiModelProperty(value = "假期时间")
    private LocalDateTime holidayTime;

    @ApiModelProperty(value = "类型 1.上班 2.休息")
    private Integer type;
}
