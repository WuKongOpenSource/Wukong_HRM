package com.kakarote.core.feign.work.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bai
 */
@Data
@ApiModel("事件成员角色信息")
public class TaskOwnerBO {
    @ApiModelProperty("成员id")
    private Long userId;

    @ApiModelProperty("成员名称")
    private String name;

    @ApiModelProperty("部门")
    private String deptName;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;
}
