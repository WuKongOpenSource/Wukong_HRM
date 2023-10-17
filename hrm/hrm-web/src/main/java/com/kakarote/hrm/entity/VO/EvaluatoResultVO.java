package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EvaluatoResultVO extends SimpleHrmEmployeeVO {
    @ApiModelProperty(value = "权重")
    private BigDecimal weight;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "考核等级id")
    private Long levelId;

    @ApiModelProperty(value = "考核等级")
    private String levelName;

    @ApiModelProperty(value = "评语")
    private String evaluate;

    @ApiModelProperty("0 未评定 1 已评定")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return "EvaluatoResultVO{" +
                "weight=" + weight +
                ", score=" + score +
                ", levelId=" + levelId +
                ", levelName='" + levelName + '\'' +
                ", evaluate='" + evaluate + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
