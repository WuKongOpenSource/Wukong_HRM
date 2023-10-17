package com.kakarote.hrm.controller;


import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.ApiExplain;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.PO.AdminRole;
import com.kakarote.hrm.service.IAdminRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/adminRole")
@Api(tags = "角色模块")
public class AdminRoleController {
    @Autowired
    private IAdminRoleService adminRoleService;


    @PostMapping("/auth")
    @ApiOperation("角色权限")
    public Result<JSONObject> auth() {
        JSONObject object = adminRoleService.auth(UserUtil.getUserId());
        return Result.ok(object);
    }

    @PostMapping(value = "/queryRoleByRoleId")
    @ApiExplain("查询角色信息")
    public Result<AdminRole> queryRoleByRoleId(@RequestParam("roleId") Long roleId) {
        return Result.ok(adminRoleService.getById(roleId));
    }


}

