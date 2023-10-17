package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResultConfirmListVO {

    @ApiModelProperty("绩效id")
    private Long appraisalId;

    @ApiModelProperty("绩效名称")
    private String appraisalName;

    @ApiModelProperty("开始时间")
    private LocalDate startTime;

    @ApiModelProperty("结束时间")
    private LocalDate endTime;

    @ApiModelProperty("考核时间")
    private String appraisalTime;

    @ApiModelProperty("总数")
    private Integer totalNum;

    @ApiModelProperty("待确认数")
    private Integer waitingNum;

    @ApiModelProperty("等级分布")
    private List<ScoreLevelsBean> scoreLevels;

    @Override
    public String toString() {
        return "ResultConfirmListVO{" +
                "appraisalId=" + appraisalId +
                ", appraisalName='" + appraisalName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", appraisalTime='" + appraisalTime + '\'' +
                ", totalNum=" + totalNum +
                ", waitingNum=" + waitingNum +
                ", scoreLevels=" + scoreLevels +
                '}';
    }

    @Getter
    @Setter
    public static class ScoreLevelsBean {

        @ApiModelProperty("等级id")
        private Long levelId;
        @ApiModelProperty("等级名称")
        private String levelName;
        @ApiModelProperty("数量")
        private int num;
    }
}
