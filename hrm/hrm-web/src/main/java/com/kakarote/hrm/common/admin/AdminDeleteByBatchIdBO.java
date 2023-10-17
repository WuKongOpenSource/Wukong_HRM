package com.kakarote.hrm.common.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDeleteByBatchIdBO {

    private String batchId;

    @ApiModelProperty("1 附件 2 图片")
    private Integer type;

    @Override
    public String toString() {
        return "AdminDeleteByBatchIdBO{" +
                "batchId='" + batchId + '\'' +
                ", type=" + type +
                '}';
    }
}
