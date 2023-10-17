package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCandidatePostBO {

    @ApiModelProperty("候选人id数组")
    private List<Long> candidateIds;

    @ApiModelProperty("职位id")
    private Long postId;

    @Override
    public String toString() {
        return "UpdateCandidatePostBO{" +
                "candidateIds=" + candidateIds +
                ", postId=" + postId +
                '}';
    }
}
