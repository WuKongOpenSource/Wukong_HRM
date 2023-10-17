package com.kakarote.hrm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.cache.AdminCacheKey;
import com.kakarote.core.redis.Redis;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.AdminMenu;
import com.kakarote.hrm.entity.PO.AdminRole;
import com.kakarote.hrm.mapper.AdminRoleMapper;
import com.kakarote.hrm.service.IAdminMenuService;
import com.kakarote.hrm.service.IAdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@Service
public class AdminRoleServiceImpl extends BaseServiceImpl<AdminRoleMapper, AdminRole> implements IAdminRoleService {

    @Autowired
    private IAdminMenuService adminMenuService;

    @Autowired
    private Redis redis;

    /**
     * 查询用户所属权限
     *
     * @return obj
     */
    @Override
    public JSONObject auth(Long userId) {
        String cacheKey = AdminCacheKey.USER_AUTH_CACHE_KET + userId.toString();
        if (redis.exists(cacheKey)) {
            return redis.get(cacheKey);
        }
        List<AdminMenu> adminMenus = adminMenuService.queryMenuList(userId);
/*        List<AdminMenu> menus = adminMenuService.list();
        for (int i = 0; i < menus.size(); i++) {
            if (Objects.equals(0L, menus.get(i).getParentId())) {
                adminMenus.add(menus.get(i));
                for (AdminMenu menu : menus) {
                    if (Objects.equals(menu.getParentId(), menus.get(i).getMenuId())) {
                        adminMenus.add(menu);
                    }
                }
            }
        }*/

        JSONObject jsonObject = createMenu(new HashSet<>(adminMenus), 0L);
        if (UserUtil.isAdmin()) {

        }
        redis.setex(cacheKey, 300, jsonObject);
        return jsonObject;
    }

    private JSONObject createMenu(Set<AdminMenu> adminMenuList, Long parentId) {
        JSONObject jsonObject = new JSONObject();
        adminMenuList.forEach(adminMenu -> {
            if (Objects.equals(parentId, adminMenu.getParentId())) {
                if (Objects.equals(1, adminMenu.getMenuType())) {
                    JSONObject object = createMenu(adminMenuList, adminMenu.getMenuId());
                    if (!object.isEmpty()) {
                        jsonObject.put(adminMenu.getRealm(), object);
                    }
                } else {
                    jsonObject.put(adminMenu.getRealm(), Boolean.TRUE);
                }
            }
        });
        return jsonObject;
    }

}
