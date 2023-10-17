package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateFieldWidthBO {

    @ApiModelProperty("字段id")
    private Long fieldId;

    @ApiModelProperty("宽度")
    private Integer width;

    @Override
    public String toString() {
        return "UpdateFieldWidthBO{" +
                "fieldId=" + fieldId +
                ", width=" + width +
                '}';
    }
}
