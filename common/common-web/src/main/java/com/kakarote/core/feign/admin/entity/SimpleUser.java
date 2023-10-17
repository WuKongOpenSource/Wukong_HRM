package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zhangzhiwei
 * 简单的用户对象
 */
@Getter
@Setter
@ApiModel("用户对象")
public class SimpleUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("头像")
    private String img;

    @ApiModelProperty("昵称")
    private String realname;

    @ApiModelProperty("用户状态 0禁用,1正常,2未激活")
    private Integer status;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("企业微信userId")
    private String wxUserId;

    @ApiModelProperty("外部审核人邮箱")
    private String outerUserEmail;

    public SimpleUser() {
    }

    public SimpleUser(Long userId, String img, String realname, Long deptId, String deptName) {
        this.userId = userId;
        this.img = img;
        this.realname = realname;
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public SimpleUser(Long userId, String img, String realname, Integer status, Long deptId, String deptName, String wxUserId) {
        this.userId = userId;
        this.img = img;
        this.realname = realname;
        this.status = status;
        this.deptId = deptId;
        this.deptName = deptName;
        this.wxUserId = wxUserId;
    }
}
