package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.RecursionUtil;
import com.kakarote.hrm.common.admin.AdminMenuVO;
import com.kakarote.hrm.common.admin.AdminRoleTypeEnum;
import com.kakarote.hrm.entity.PO.AdminMenu;
import com.kakarote.hrm.mapper.AdminMenuMapper;
import com.kakarote.hrm.service.IAdminMenuService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 后台菜单表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@Service
public class AdminMenuServiceImpl extends BaseServiceImpl<AdminMenuMapper, AdminMenu> implements IAdminMenuService {

    /**
     * 查询用户所拥有的菜单权限
     *
     * @param userId 用户列表
     * @return 菜单权限的并集
     */
    @Override
    public List<AdminMenu> queryMenuList(Long userId) {
            return query().list();
    }



    /**
     * 根据类型查询菜单
     *
     * @param typeEnum type
     * @return data
     */
    @Override
    public JSONObject getMenuListByType(AdminRoleTypeEnum typeEnum) {
        JSONObject object = new JSONObject();
        String realm = typeEnum.getName();
        AdminMenuVO data = queryMenuListByRealm(realm);
        List<AdminMenuVO> menuList = getMenuList(data.getMenuId());
        data.setChildMenu(menuList);
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("menuName_resourceKey", "promission.system." + data.getMenuId());
        data.setLanguageKeyMap(keyMap);
        object.put("data", data);
        return object;
    }

    /**
     * 通过parentId和realm查询菜单
     *
     * @return 菜单列表
     * @author zhangzhiwei
     */
    private AdminMenuVO queryMenuListByRealm(String realm) {
        AdminMenu adminMenu = lambdaQuery().eq(AdminMenu::getParentId, 0).eq(AdminMenu::getRealm, realm).one();
        return BeanUtil.copyProperties(adminMenu, AdminMenuVO.class);
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     * @author zhangzhiwei
     */
    @Override
    public List<AdminMenu> queryAllMenuList() {
        return query().list();
    }

    private List<AdminMenuVO> getMenuList(Long parentId, String... notRealm) {
        LambdaQueryChainWrapper<AdminMenu> chainWrapper = lambdaQuery();
        if (notRealm.length > 0) {
            chainWrapper.notIn(AdminMenu::getRealm, Arrays.asList(notRealm));
        }
        chainWrapper.orderByAsc(AdminMenu::getSort);
        List<AdminMenu> list = chainWrapper.list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (AdminMenu adminMenu : list) {
                Map<String, String> keyMap = new HashMap<>();
                keyMap.put("menuName_resourceKey", "promission.system." + adminMenu.getMenuId());
                adminMenu.setLanguageKeyMap(keyMap);
            }
        }
        return RecursionUtil.getChildListTree(list, "parentId", parentId, "menuId", "childMenu", AdminMenuVO.class);
    }
}
