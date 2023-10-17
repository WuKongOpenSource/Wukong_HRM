package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("crm审核限时处理人员")
public class ExamineAdvancedConfigLimitTimeUsersVO {

    @ApiModelProperty(value = "处理人id")
    private Long userId;

    @ApiModelProperty(value = "处理人名称")
    private String realname;

    @ApiModelProperty(value = "处理顺序")
    private Integer sort;

    @ApiModelProperty(value = "处理人类型 0指定用户 1 员工 2 角色")
    private Integer type;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "指定上级")
    private Integer parentLevel;
}
