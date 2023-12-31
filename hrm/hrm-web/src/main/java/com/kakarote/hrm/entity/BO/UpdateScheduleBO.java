package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScheduleBO {

    @ApiModelProperty(value = "考核项id")
    private Long segId;

    @ApiModelProperty(value = "目标进度")
    private Integer schedule;

    @ApiModelProperty(value = "完成情况说明")
    private String explainDesc;

    @Override
    public String toString() {
        return "UpdateScheduleBO{" +
                "segId=" + segId +
                ", schedule=" + schedule +
                ", explainDesc='" + explainDesc + '\'' +
                '}';
    }
}
