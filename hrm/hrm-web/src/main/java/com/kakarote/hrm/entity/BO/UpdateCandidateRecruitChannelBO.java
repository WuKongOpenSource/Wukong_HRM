package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCandidateRecruitChannelBO {

    @ApiModelProperty("候选人id数组")
    private List<Long> candidateIds;

    @ApiModelProperty("招聘渠道")
    private Long channelId;

    @Override
    public String toString() {
        return "UpdateCandidateRecruitChannelBO{" +
                "candidateIds=" + candidateIds +
                ", channelId=" + channelId +
                '}';
    }
}
