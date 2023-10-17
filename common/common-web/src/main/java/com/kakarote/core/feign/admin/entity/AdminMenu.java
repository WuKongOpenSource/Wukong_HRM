package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 后台菜单表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-03
 */
@Data
@Accessors(chain = true)
@TableName("wk_admin_menu")
@ApiModel(value = "AdminMenu对象", description = "后台菜单表")
public class AdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    private Long menuId;

    @ApiModelProperty(value = "上级菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "权限标识")
    private String realm;

    @ApiModelProperty(value = "权限URL")
    private String realmUrl;

    @ApiModelProperty(value = "所属模块")
    private String realmModule;

    @ApiModelProperty(value = "1 普通项目 2敏捷项目")
    private Integer projectType;

    @ApiModelProperty(value = "受否拥有权限")
    private Boolean projectAuth;

    @ApiModelProperty(value = "菜单类型  1目录 2 菜单 3 按钮 4特殊")
    private Integer menuType;

    @ApiModelProperty(value = "排序（同级有效）")
    private Integer sort;

    @ApiModelProperty(value = "状态 1 启用 0 禁用")
    private Integer status;

    @ApiModelProperty(value = "菜单说明")
    private String remarks;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据权限")
    private Integer dataType;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminMenu adminMenu = (AdminMenu) o;
        return Objects.equals(menuId, adminMenu.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}
