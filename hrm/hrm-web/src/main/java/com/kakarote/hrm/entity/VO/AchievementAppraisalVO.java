package com.kakarote.hrm.entity.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author guomenghao
 * @since 2021/03/12/14:55
 */
@Getter
@Setter
public class AchievementAppraisalVO {
    @TableId(value = "考核id")
    private Long appraisalId;

    @ApiModelProperty(value = "考核名称")
    private String appraisalName;

    @Override
    public String toString() {
        return "AchievementAppraisalVO{" +
                "appraisalId=" + appraisalId +
                ", appraisalName='" + appraisalName + '\'' +
                '}';
    }
}
