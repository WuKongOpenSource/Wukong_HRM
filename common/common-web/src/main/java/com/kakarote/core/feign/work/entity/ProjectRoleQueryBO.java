package com.kakarote.core.feign.work.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bai
 */
@Data
@ApiModel("查询项目关联成员")
public class ProjectRoleQueryBO {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户头像")
    private String img;
}
