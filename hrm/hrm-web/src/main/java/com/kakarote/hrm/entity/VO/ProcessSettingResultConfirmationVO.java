package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("考核计划-流程设置-结果确认展示对象")
public class ProcessSettingResultConfirmationVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "确认类型")
    private Integer confirmationType;

    @ApiModelProperty(value = "确认层级")
    private Integer level;

    @ApiModelProperty(value = "指定人id")
    private Long designatedUserId;

}
