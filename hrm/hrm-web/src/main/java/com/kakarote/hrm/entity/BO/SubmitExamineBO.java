package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitExamineBO {

    @ApiModelProperty("薪资记录id")
    private Long sRecordId;

    @ApiModelProperty("审核人id")
    private Long checkUserId;

    private Long examineRecordId;

    private Integer checkStatus;

    @Override
    public String toString() {
        return "SubmitExamineBO{" +
                "sRecordId=" + sRecordId +
                ", checkUserId=" + checkUserId +
                ", examineRecordId=" + examineRecordId +
                ", checkStatus=" + checkStatus +
                '}';
    }
}
