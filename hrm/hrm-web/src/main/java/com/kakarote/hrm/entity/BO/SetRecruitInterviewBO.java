package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SetRecruitInterviewBO {

    @ApiModelProperty(value = "面试id")
    private Long interviewId;

    @ApiModelProperty(value = "候选人id(单个需要)")
    private Long candidateId;

    @ApiModelProperty(value = "候选人id列表(批量需要)")
    private List<Long> candidateIds;

    @ApiModelProperty(value = "面试方式 1现场面试 2电话面试 3视频面试")
    private Integer type;

    @ApiModelProperty(value = "面试官id")
    private Long interviewEmployeeId;

    @ApiModelProperty(value = "其他面试官")
    private List<Long> otherInterviewEmployeeIds;

    @ApiModelProperty(value = "面试时间")
    private LocalDateTime interviewTime;

    @ApiModelProperty(value = "面试地址")
    private String address;

    @Override
    public String toString() {
        return "SetRecruitInterviewBO{" +
                "interviewId=" + interviewId +
                ", candidateId=" + candidateId +
                ", candidateIds=" + candidateIds +
                ", type=" + type +
                ", interviewEmployeeId=" + interviewEmployeeId +
                ", otherInterviewEmployeeIds=" + otherInterviewEmployeeIds +
                ", interviewTime=" + interviewTime +
                ", address='" + address + '\'' +
                '}';
    }
}
