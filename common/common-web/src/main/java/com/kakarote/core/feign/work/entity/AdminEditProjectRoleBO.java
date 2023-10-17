package com.kakarote.core.feign.work.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author baijc
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@ApiModel("项目管理编辑角色权限")
public class AdminEditProjectRoleBO implements Serializable {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("角色ID")
    private List<Long> roleIds;
}
