package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("考核计划列表查询对象")
@Data
@EqualsAndHashCode(callSuper = false)
public class QueryAppraisalPlanBO extends PageEntity {

    @ApiModelProperty("考核计划名称")
    private String appraisalPlanName;

    @ApiModelProperty("阶段状态：0：未开始 1：员工填写2：自评分 3：他人评分 4:目标确认5：结果审核6：结果确认 7：归档")
    private Integer status;
}
