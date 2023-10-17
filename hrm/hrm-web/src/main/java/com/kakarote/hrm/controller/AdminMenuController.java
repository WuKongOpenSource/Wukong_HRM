package com.kakarote.hrm.controller;


import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.common.admin.AdminRoleTypeEnum;
import com.kakarote.hrm.entity.PO.AdminMenu;
import com.kakarote.hrm.service.IAdminMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 后台菜单表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/adminMenu")
@Api(tags = "菜单模块")
public class AdminMenuController {

    @Autowired
    private IAdminMenuService adminMenuService;

    @RequestMapping("/getMenuListByType/{type}")
    @ApiOperation("根据类型查询菜单")
    public Result<JSONObject> getMenuListByType(@PathVariable("type") Integer type) {
        AdminRoleTypeEnum typeEnum = AdminRoleTypeEnum.parse(type);
        JSONObject byType = adminMenuService.getMenuListByType(typeEnum);
        return Result.ok(byType);
    }

    /**
     * 查询所有菜单
     * @return
     */
    @RequestMapping("/queryAllMenuList")
    @ApiOperation("通过realm查询菜单")
    public Result<List<AdminMenu>> queryAllMenuList(){
        return Result.ok(adminMenuService.queryAllMenuList());

    }
}

