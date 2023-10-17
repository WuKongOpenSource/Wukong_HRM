package com.kakarote.hrm.common.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiwei
 * 菜单相关VO
 */
@Data
@ToString
@ApiModel("角色列表返回")
public class AdminMenuVO {
    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long  menuId;

    @ApiModelProperty(value = "上级菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "权限标识")
    private String realm;

    @ApiModelProperty(value = "菜单类型  1目录 2 菜单 3 按钮 4特殊")
    private Integer menuType;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "项目管理权限类型")
    private Integer projectType;

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;

    @ApiModelProperty(value = "子菜单")
    private List<AdminMenuVO> childMenu;

}
