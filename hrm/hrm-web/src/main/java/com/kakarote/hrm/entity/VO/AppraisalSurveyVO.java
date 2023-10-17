package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AppraisalSurveyVO {


    private Long appraisalId;

    @ApiModelProperty(value = "考核名称")
    private String appraisalName;

    @ApiModelProperty(value = "考核表模板id")
    private Long tableId;

    private String appraisalTime;

    @ApiModelProperty("考核模板名称")
    private String tableName;

    @ApiModelProperty(value = "考核开始时间")
    private LocalDate startTime;

    @ApiModelProperty(value = "考核结束时间")
    private LocalDate endTime;

    @ApiModelProperty("等级分布")
    private List<ScoreLevelsBean> scoreLevels;

    @Override
    public String toString() {
        return "AppraisalSurveyVO{" +
                "appraisalId=" + appraisalId +
                ", appraisalName='" + appraisalName + '\'' +
                ", tableId=" + tableId +
                ", appraisalTime='" + appraisalTime + '\'' +
                ", tableName='" + tableName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
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
