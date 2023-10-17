package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="AdminRole对象", description="角色表")
@NoArgsConstructor
public class AdminRole implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "role_id",type = IdType.ASSIGN_ID)
    private Long roleId;

    @ApiModelProperty(value = "名称")
    private String roleName;

    @ApiModelProperty(value = "0、自定义角色1、管理角色 2、客户管理角色 3、人事角色 4、财务角色 5、项目角色 8、项目自定义角色")
    private Integer roleType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "1 启用 0 禁用")
    private Integer status;

    @ApiModelProperty(value = "数据权限 1、本人，2、本人及下属，3、本部门，4、本部门及下属部门，5、全部 ")
    private Integer dataType;

    @ApiModelProperty(value = "0 隐藏 1 不隐藏")
    private Integer isHidden;

    @ApiModelProperty(value = "0 默认角色 1 项目管理员角色 2 项目管理超管角色 3 项目编辑角色 4 项目只读角色 5开发 6产品 7测试 8 项目经理 9运维")
    private Integer label;

    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    @ApiModelProperty(value = "菜单列表")
    private Map<String,List<Long>> rules;

    @ApiModelProperty(value = "菜单id列表")
    private List<Long> menuIds = new ArrayList<>();

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;

    public AdminRole(Long roleId, String roleName, Integer roleType, String remark, Integer status, Integer dataType, Integer isHidden, Integer label, Long companyId, Long createUserId, Long updateUserId, LocalDateTime createTime, LocalDateTime updateTime) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleType = roleType;
        this.remark = remark;
        this.status = status;
        this.dataType = dataType;
        this.isHidden = isHidden;
        this.label = label;
        this.companyId = companyId;
        this.createUserId = createUserId;
        this.updateUserId = updateUserId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
