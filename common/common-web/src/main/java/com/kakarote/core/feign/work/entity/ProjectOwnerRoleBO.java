package com.kakarote.core.feign.work.entity;

import com.kakarote.core.feign.admin.entity.AdminRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bai
 */
@Data
@ApiModel("项目成员角色信息")
public class ProjectOwnerRoleBO {
    @ApiModelProperty("成员id")
    private Long userId;

    @ApiModelProperty("成员姓名")
    private String realname;

    @ApiModelProperty("成员头像")
    private String img;

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("角色")
    private List<AdminRole> adminRoles;

    @ApiModelProperty("筛选标记")
    private Integer labelProject;

    @ApiModelProperty("是否是项目创建人 0:不是,1:是")
    private Integer isPmCreater;
}
