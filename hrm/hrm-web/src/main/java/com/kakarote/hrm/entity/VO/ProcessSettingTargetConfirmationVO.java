package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("目标确认展示对象")
@Data
public class ProcessSettingTargetConfirmationVO {

    @ApiModelProperty(value = "确认类型：1上级2：部门负责人3：指定评分人4：被考核人")
    private Integer confirmationType;

    @ApiModelProperty(value = "确认级别")
    private Integer confirmationLevel;

    @ApiModelProperty(value = "确认人")
    private Long identifyingPeople;
}
