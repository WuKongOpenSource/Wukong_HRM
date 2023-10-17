package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUniqueBO {

    @ApiModelProperty("字段id")
    private Long fieldId;

    @ApiModelProperty("值")
    private String value;

    private Long id;
    @ApiModelProperty("状态")
    private Integer status = 0;

    @Override
    public String toString() {
        return "VerifyUniqueBO{" +
                "fieldId=" + fieldId +
                ", value='" + value + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
