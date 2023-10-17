package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-流程设置-结果确认保存对象")
public class ProcessSettingResultConfirmationBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "确认层级")
    private Integer level;

    @ApiModelProperty(value = "确认类型：1上级2：部门负责人3：指定评分人4：被考核人")
    private Integer confirmationType;

    @ApiModelProperty(value = "指定人id")
    private Long designatedUserId;

}
