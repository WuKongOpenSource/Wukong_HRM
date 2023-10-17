package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author JiaS
 * @date 2020/11/21
 */
@Data
public class AdminProjectRoleBO {

    @ApiModelProperty("人员ID")
    private Long userId;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("权限id列表")
    private List<Long> roleIds;

    @ApiModelProperty("绑定项目ID")
    private Long projectId;

    @ApiModelProperty(value = "是否所有人可见 1 是 0 否")
    private Integer isOpen;
}
