package com.kakarote.core.feign.examine.entity;

import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审批人信息
 * @author zhangzhiwei
 */
@Data
@ApiModel("审批人")
public class ExamineFlowFinalUser {

    @ApiModelProperty("审批人id")
    private Long userId;

    @ApiModelProperty("外部审批人邮箱")
    private String email;

    @ApiModelProperty("审批人状态 状态,0禁用,1正常,2未激活 4邮箱")
    private Integer userStatus = 0;

    @ApiModelProperty(value = "审批人id", hidden = true)
    private SimpleUser simpleUser;
}
