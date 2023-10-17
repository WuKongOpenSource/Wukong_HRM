package com.kakarote.core.feign.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.core.common.Result;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.entity.AdminUserQueryBO;
import com.kakarote.core.entity.QueryParentDeptBO;
import com.kakarote.core.feign.admin.entity.*;
import com.kakarote.core.feign.admin.service.AdminService;
import com.kakarote.core.feign.crm.entity.SimpleCrmEntity;
import com.kakarote.core.feign.email.entity.EmailBO;
import com.kakarote.core.feign.scrm.entity.WxWorkUserInfoVO;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.*;

/**
 * @author zhang
 * 向admin模块请求企业信息
 */
@Component
public class AdminServiceImpl implements AdminService {

    /**
     * 判断企业是否正常
     *
     * @param companyId 企业ID
     * @return 结果信息
     */
    @Override
    public Result<Boolean> isNormal(Long companyId) {
        return Result.error(SystemCodeEnum.SYSTEM_SERVER_EXPIRE_ERROR);
    }

    @Override
    public Result<Long> queryCompanyIdByDomain(String domain) {
        return Result.ok(null);
    }

    /**
     * 根据用户ID获取用户名
     *
     * @param userId 用户ID
     * @return data
     */
    @Override
    public Result<UserInfo> getUserInfo(Long userId) {
        return Result.ok(null);
    }

    /**
     * 根据部门ID获取部门名称
     *
     * @param deptId 部门ID
     * @return 结果信息
     */
    @Override
    public Result<String> queryDeptName(Long deptId) {
        return Result.ok("");
    }

    @Override
    public Result<Long> getIdByDeptName(String deptName) {
        return Result.ok(0L);
    }

    @Override
    public Result<Long> getIdByUserName(String userName, Long companyId) {
        return Result.ok(0L);
    }

    @Override
    public Result<Long> getIdByWxDeptName(String deptName, Long companyId) {
        return Result.ok(0L);
    }

    /**
     * 查询部门下属部门
     *
     * @param deptId 上级ID
     * @return data
     */
    @Override
    public Result<List<Long>> queryChildDeptId(Long deptId) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 查询该用户下级的用户
     *
     * @param userId 用户ID 0代表全部
     * @return data
     */
    @Override
    public Result<List<Long>> queryChildUserId(Long userId) {
        return Result.ok(new ArrayList<>());
    }

    @Override
    public Result<List<Long>> queryDirectChildUserId(Long userId) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 查询企业所有用户
     *
     * @return data
     */
    @Override
    public Result<List<Long>> queryUserList(Integer type) {
        return Result.ok(Collections.emptyList());
    }

    /**
     * 根据名称查询系统配置
     *
     * @param name 名称
     * @return data
     */
    @Override
    public Result<List<AdminConfig>> queryConfigByName(String name) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 根据名称查询系统配置
     *
     * @param name 名称
     * @return data
     */
    @Override
    public Result<AdminConfig> queryFirstConfigByName(String name) {
        return Result.ok((AdminConfig) null);
    }

    @Override
    public Result<List<Long>> queryNormalUserByIds(Collection<Long> ids) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 根据ids查询用户信息
     *
     * @param userId id
     * @return data
     */
    @Override
    public Result<SimpleUser> queryUserById(Long userId) {
        return Result.ok(null);
    }

    @Override
    public Result<SimpleUser> queryUserByWxUserId(Long companyId, String wxUserId) {
        return null;
    }

    /**
     * 根据ids查询部门信息
     *
     * @param ids id列表
     * @return data
     */
    @Override
    public Result<List<SimpleDept>> queryDeptByIds(Collection<Long> ids) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 根据ids查询部门信息
     *
     * @param ids id列表
     * @return data
     */
    @Override
    public Result<List<Long>> queryUserByDeptIds(Collection<Long> ids) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<Integer> queryDataType(Long userId, Long menuId) {
        return Result.ok(1);
    }

    /**
     * 查询权限内用户
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 权限
     */
    @Override
    public Result<List<Long>> queryUserByAuth(Long userId, Long menuId) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<JSONObject> auth() {
        return Result.ok();
    }

    @Override
    public Result<Long> queryWorkRole(Integer label) {
        return Result.ok(1L);
    }

    @Override
    public Result<AdminRole> getProjectRoleByUserId(Integer typeId, Long userId) {
        return Result.ok(new AdminRole());
    }


    /**
     * 根据角色类型查询角色
     *
     * @param roleType 角色类型
     * @return type
     */
    @Override
    public Result<List<AdminRole>> queryRoleByRoleType(Integer roleType) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<AdminRole> queryRoleByRoleId(Long roleId) {
        return Result.ok();
    }

    @Override
    public Result<List<AdminRole>> queryRoleByRoleTypeAndUserId(Integer type) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result updateAdminConfig(AdminConfig adminConfig) {
        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> listByRoleId(Collection<Long> ids) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result saveOrUpdateMessage(AdminMessage message) {
        return Result.error(SystemCodeEnum.SYSTEM_SERVER_ERROR);
    }

    @Override
    public Result<AdminMessage> getMessageById(Long messageId) {
        return Result.ok((AdminMessage) null);
    }

    @Override
    public Result<AdminConfig> queryFirstConfigByNameAndValue(String name, String value) {
        return Result.ok(new AdminConfig().setStatus(0));
    }

    @Override
    public Result<Long> queryMenuId(String realm1, String realm2, String realm3) {
        return Result.ok(0L);
    }

    @Override
    public Result<List<AdminCompany>> queryCompanyList(List<Long> companyIds) {
        return Result.ok(Collections.emptyList());
    }

    /**
     * 查询用户id根据用户名和公司id
     *
     * @param username  :用户名字
     * @param companyId :公司id
     * @return
     */
    @Override
    public Result<Long> queryUserIdByUserNameAndCompanyId(String username, Long companyId) {
        return Result.ok(0L);
    }

    @Override
    public Result<UserInfo> queryLoginUserInfo(Long userId) {
        return Result.ok(new UserInfo());
    }

    @Override
    public Result<Long> queryUserIdByUserName(String userName) {
        return Result.ok(0L);
    }

    @Override
    public Result<List<Long>> queryUserIdByRealName(List<String> realNames) {
        return Result.ok();
    }

    @Override
    public Result<List<UserInfo>> queryUserInfoList() {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<List<Long>> queryUserIdByRoleId(Long roleId) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<List<UserInfo>> queryParentByLevel(AdminUserQueryBO queryBO) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<List<AdminMenu>> queryMenuListByRoleIds(List<Long> roleIds) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<List<AdminMenu>> queryMenuList(Long userId, Long companyId) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result batchAddUpdateLanguageKey(AdminLanguagePackFieldBO languageVOS) {
        return Result.ok();
    }

    @Override
    public Result setEmailInfo(EmailBO emailBO) {
        return Result.ok();
    }

    @Override
    public Result<List<String>> queryDeprMemberById(List<String> deptIds) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * scrm员工离职账号禁用  因scrm员工离职时没有登录状态 会导致主账号也被禁用 所以做特殊处理
     *
     * @param adminUserId :用户状态修改BO
     * @return com.kakarote.core.common.Result
     */
    @Override
    public Result scrmAccountDisabled(Long adminUserId) {
        return Result.ok();
    }

    @Override
    public Result<SimpleUser> bindingWxWorkUserId(String token, WxWorkUserInfoVO wxWorkUserInfoVO) {
        return Result.ok(null);
    }

    @Override
    public Result<List<AdminMenu>> queryAllMenuList() {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<AdminConfig> queryConfigByNameAndCompanyId(String name, Long companyId) {
        return Result.ok((AdminConfig) null);
    }

    @Override
    public void queryApplyOrderByStatusAndCloseTime() {
        Collections.emptyList();
    }


    @Override
    public Result<Integer> updateAdminApplyNumberInfo(String name, Integer number) {
        return Result.ok(0);
    }

    @Override
    public Result<List<Long>> getAllRoleMenu(List<Long> roleIds) {
        return null;
    }

    @Override
    public Result<List<AdminRole>> queryProjectRoleByTypes(List<Integer> types) {
        return Result.ok(null);
    }

    @Override
    public Result cpSendMessage(String content, Long companyId, String wxUserId) {
        return Result.ok(null);
    }

    @Override
    public Result<List<Long>> queryUserSubsidiaryDeptId(Long userId) {
        return Result.ok(new ArrayList<>());
    }

    @Override
    public Result<List<Long>> queryParentDeptIdByLevel(QueryParentDeptBO queryBO) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public  Result<Map<Integer,List<Long>>>  groupByLevelQueryDeptId(QueryParentDeptBO queryBO) {
        return Result.ok(new HashMap<>());
    }

    /**
     * ids查询部门和子级部门中员工列表
     *
     * @param ids id列表
     * @return data
     */
    @Override
    public Result<List<Long>> queryUserListByDeptIds(List<Long> ids) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<List<Long>> queryUserIdByRoleIds(List<Long> roleIds) {
        return Result.ok(Collections.emptyList());
    }

    @Override
    public Result<String> dingtalkRegister(@Valid AdminCloudDingtalkCO adminCloudDingtalkCO) {
        return Result.ok();
    }

    @Override
    public Result openOrClose(Long companyId, Integer status) {
        return Result.ok();
    }

    @Override
    public Result addUser(AdminUserCO adminUserCO) {
        return Result.ok();
    }

    @Override
    public Result<List<SimpleCrmEntity>> queryModuleInfo(Collection ids) {
        return Result.ok(Collections.emptyList());
    }
}
