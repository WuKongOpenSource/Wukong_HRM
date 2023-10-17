package com.kakarote.core.feign.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.core.common.ApiExplain;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.AdminUserQueryBO;
import com.kakarote.core.entity.QueryParentDeptBO;
import com.kakarote.core.feign.admin.entity.*;
import com.kakarote.core.feign.crm.entity.SimpleCrmEntity;
import com.kakarote.core.feign.email.entity.EmailBO;
import com.kakarote.core.feign.scrm.entity.WxWorkUserInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhwei
 * 系统管理模块的一些请求
 */
@FeignClient(name = "admin", contextId = "admin")
@Component
public interface AdminService {

    /**
     * 判断企业是否正常
     *
     * @param companyId 企业ID
     * @return 结果信息
     */
    @PostMapping(value = "/adminCompanyManager/isNormal")
    public Result<Boolean> isNormal(@RequestParam("companyId") Long companyId);

    /**
     * 根据domain查询公司id
     *
     * @param domain:
     * @return 公司id
     */
    @PostMapping(value = "/adminCompanyManager/queryCompanyIdByDomain")
    Result<Long> queryCompanyIdByDomain(@RequestParam("domain") String domain);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return data
     */
    @RequestMapping(value = "/adminUser/queryInfoByUserId")
    Result<UserInfo> getUserInfo(@RequestParam("userId") Long userId);

    /**
     * 根据部门ID获取部门名称
     *
     * @param deptId 部门ID
     * @return 结果信息
     */
    @RequestMapping(value = "/adminDept/getNameByDeptId")
    Result<String> queryDeptName(@RequestParam("deptId") Long deptId);

    /**
     * 根据部门名称获取部门Id
     *
     * @param deptName 部门ID
     * @return 结果信息
     */
    @RequestMapping(value = "/adminDept/getIdByDeptName")
    Result<Long> getIdByDeptName(@RequestParam("deptName") String deptName);


    /**
     * 根据员工名称获取员工id
     *
     * @param userName  用户名
     * @param companyId 企业ID，仅企业微信需要
     * @return 结果信息
     */
    @RequestMapping(value = "/adminUser/getIdByRealName")
    Result<Long> getIdByUserName(@RequestParam("userName") String userName, @RequestParam(required = false, name = "companyId") Long companyId);


    /**
     * 根据员工名称获取员工id
     *
     * @param deptName 部门名
     * @param companyId 企业ID，仅企业微信需要
     * @return 结果信息
     */
    @RequestMapping(value = "/adminDept/getIdByWxDeptName")
    Result<Long> getIdByWxDeptName(@RequestParam("deptName") String deptName, @RequestParam(required = false, name = "companyId") Long companyId);

    /**
     * 查询部门下属部门
     *
     * @param deptId 上级ID
     * @return data
     */
    @RequestMapping(value = "/adminDept/queryChildDeptId")
    Result<List<Long>> queryChildDeptId(@RequestParam("deptId") Long deptId);

    /**
     * 查询该用户下级的用户
     *
     * @param userId 用户ID 0代表全部
     * @return data
     */
    @RequestMapping(value = "/adminUser/queryChildUserId")
    Result<List<Long>> queryChildUserId(@RequestParam("userId") Long userId);

    //------------------------------------------------------

    /**
     * @Description  添加获取直属下属
     * @Author UNIQUE
     * @Date 2022/8/28
     * @Param
     * @return
     **/
    @RequestMapping(value = "/adminUser/queryDirectChildUserId")
    Result<List<Long>> queryDirectChildUserId(@RequestParam("userId") Long userId);
    //------------------------------------------------------

    /**
     * 查询企业所有用户
     *
     * @param type 1或者不传为全部，2代表只查询状态为正常的员工
     * @return data
     */
    @PostMapping(value = "/adminUser/queryAllUserList")
    Result<List<Long>> queryUserList(@RequestParam(value = "type", required = false) Integer type);

    /**
     * 根据名称查询系统配置
     *
     * @param name 名称
     * @return data
     */
    @PostMapping(value = "/adminConfig/queryConfigByName")
    Result<List<AdminConfig>> queryConfigByName(@RequestParam("name") String name);

    /**
     * 根据名称查询系统配置
     *
     * @param name 名称
     * @return data
     */
    @RequestMapping(value = "/adminConfig/queryFirstConfigByName")
    Result<AdminConfig> queryFirstConfigByName(@RequestParam("name") String name);

    /**
     * 根据ids查询正常的用户Id
     *
     * @param ids id列表
     * @return data
     */
    @PostMapping(value = "/adminUser/queryNormalUserByIds")
    Result<List<Long>> queryNormalUserByIds(@RequestBody Collection<Long> ids);

    /**
     * 根据ids查询用户信息
     *
     * @param userId id
     * @return data
     */
    @PostMapping(value = "/adminUser/queryUserById")
    Result<SimpleUser> queryUserById(@RequestParam("userId") Long userId);

    /**
     * 根据ids查询用户信息
     *
     * @param companyId 企业id
     * @param wxUserId  企业微信userid
     * @return data
     */
    @PostMapping(value = "/adminUser/queryUserByWxUserId")
    public Result<SimpleUser> queryUserByWxUserId(@RequestParam("companyId") Long companyId, @RequestParam("wxUserId") String wxUserId);

    /**
     * 根据ids查询部门信息
     *
     * @param ids id列表
     * @return data
     */
    @PostMapping(value = "/adminDept/queryDeptByIds")
    Result<List<SimpleDept>> queryDeptByIds(@RequestBody Collection<Long> ids);

    /**
     * 根据ids查询部门下的用户
     *
     * @param ids id列表
     * @return data
     */
    @PostMapping(value = "/adminUser/queryUserByDeptIds")
    Result<List<Long>> queryUserByDeptIds(@RequestBody Collection<Long> ids);

    /**
     * 查询数据权限
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 权限
     */
    @PostMapping(value = "/adminRole/queryDataType")
    Result<Integer> queryDataType(@RequestParam("userId") Long userId, @RequestParam("menuId") Long menuId);

    /**
     * 查询权限内用户
     *
     * @param userId 用户ID
     * @param menuId 菜单Id
     * @return 权限
     */
    @PostMapping(value = "/adminRole/queryUserByAuth")
    Result<List<Long>> queryUserByAuth(@RequestParam("userId") Long userId, @RequestParam("menuId") Long menuId);

    /**
     * 查询角色授权
     * @return
     */
    @PostMapping("/adminRole/auth")
    public Result<JSONObject> auth();

    /**
     * 查询work角色
     *
     * @param label:标签
     * @return
     */
    @PostMapping(value = "/adminRole/queryWorkRole")
    Result<Long> queryWorkRole(@RequestParam("label") Integer label);


    @PostMapping("/adminRole/getProjectRoleByUserId")
    Result<AdminRole> getProjectRoleByUserId(@RequestParam("typeId") Integer typeId, @RequestParam("userId") Long userId);

    /**
     * 根据角色类型查询角色
     *
     * @param type 角色类型
     * @return type
     */
    @PostMapping(value = "/adminRole/getRoleByType/{type}")
    Result<List<AdminRole>> queryRoleByRoleType(@PathVariable("type") Integer type);


    /**
     * 根据角色Id查询角色
     *
     * @param roleId
     * @return roleId
     */
    @PostMapping(value = "/adminRole/queryRoleByRoleId")
    Result<AdminRole> queryRoleByRoleId(@RequestParam("roleId") Long roleId);


    /**
     * 查询当前用户在某个模块下的角色
     *
     * @param type 角色类型
     * @return
     */
    @PostMapping("/adminRole/queryRoleByRoleTypeAndUserId")
    Result<List<AdminRole>> queryRoleByRoleTypeAndUserId(@RequestParam("type") Integer type);

    /**
     * 修改系統配置
     *
     * @param adminConfig 配置实体
     * @return data
     */
    @PostMapping(value = "/adminConfig/updateAdminConfig")
    Result updateAdminConfig(@RequestBody AdminConfig adminConfig);


    @PostMapping("/adminRole/listByRoleId")
    @ApiOperation("角色权限")
    Result<List<Map<String, Object>>> listByRoleId(@RequestBody Collection<Long> ids);

    /**
     * 保存或更改message
     *
     * @param message:系统消息表
     * @return
     */
    @PostMapping("/adminMessage/saveOrUpdateMessage")
    Result<Long> saveOrUpdateMessage(@RequestBody AdminMessage message);

    /**
     * 根据信息id查询系统消息表
     *
     * @param messageId:信息id
     * @return 系统消息表
     */
    @PostMapping("/adminMessage/getById/{messageId}")
    Result<AdminMessage> getMessageById(@PathVariable("messageId") Long messageId);

    /**
     * 根据名字和value查询第一个config配置
     *
     * @param name:姓名
     * @param value:
     * @return 客户规则
     */
    @ApiExplain(value = "查询config配置")
    @RequestMapping("/adminConfig/queryFirstConfigByNameAndValue")
    Result<com.kakarote.core.feign.admin.entity.AdminConfig> queryFirstConfigByNameAndValue(@RequestParam("name") String name,
                                                                                            @RequestParam("value") String value);

    /**
     * 查询MenuId
     *
     * @param realm1
     * @param realm2
     * @param realm3
     * @return MenuId
     */
    @RequestMapping("/adminMenu/queryMenuId")
    Result<Long> queryMenuId(@RequestParam("realm1") String realm1, @RequestParam("realm2") String realm2,
                             @RequestParam("realm3") String realm3);

    /**
     * 查询云平台公司配置表
     *
     * @param companyIds:公司ids
     * @return 云平台公司配置表
     */
    @PostMapping("/adminCompanyManager/queryCompanyList")
    Result<List<AdminCompany>> queryCompanyList(@RequestParam(value = "companyIds", required = false) List<Long> companyIds);

    /**
     * 查询用户id根据用户名和公司id
     *
     * @param username:用户名字
     * @param companyId:公司id
     * @return
     */
    @PostMapping("/adminUser/queryUserIdByUserNameAndCompanyId")
    @ApiOperation("查询用户id根据用户名和企业id")
    Result<Long> queryUserIdByUserNameAndCompanyId(@RequestParam("username") String username
            , @RequestParam("companyId") Long companyId);

    /**
     * 模拟查询登陆用户信息
     *
     * @param userId:用户id
     * @return 用户表
     */
    @PostMapping("/adminUser/queryLoginUserInfo")
    @ApiExplain("模拟查询登陆用户信息")
    Result<UserInfo> queryLoginUserInfo(@RequestParam("userId") Long userId);

    /**
     * 查询用户id通过用户名
     *
     * @param userName:用户名
     * @return 用户id
     */
    @PostMapping("/adminUser/queryUserIdByUserName")
    @ApiExplain("查询用户id通过用户名")
    Result<Long> queryUserIdByUserName(@RequestParam("userName") String userName);


    @PostMapping("/adminUser/queryUserIdByRealName")
    @ApiOperation("查询用户id根据真实姓名")
    public Result<List<Long>> queryUserIdByRealName(@RequestParam("realNames") List<String> realNames);

    /**
     * 查询所有员工
     *
     * @param
     * @return 用户表
     */
    @PostMapping("/adminUser/queryAllUserInfoList")
    @ApiExplain("查询所有员工")
    Result<List<UserInfo>> queryUserInfoList();

    /**
     * 通过角色ID查询用户
     *
     * @param roleId:角色id
     * @return
     */
    @PostMapping("/adminRole/queryUserIdByRoleId")
    @ApiExplain("通过角色ID查询用户")
    Result<List<Long>> queryUserIdByRoleId(@RequestParam("roleId") Long roleId);

    /**
     * 获取指定用户的上级用户
     *
     * @param queryBO:用户查询BO
     * @return 用户表
     */
    @PostMapping("/adminUser/queryParentByLevel")
    @ApiExplain("获取指定用户的上级用户")
    Result<List<UserInfo>> queryParentByLevel(@RequestBody AdminUserQueryBO queryBO);

    /**
     * 根据角色id查询菜单
     *
     * @param roleIds:角色ids
     * @return 后台菜单表
     */
    @PostMapping("/adminMenu/queryMenuListByRoleIds")
    @ApiOperation("根据角色id查询菜单")
    Result<List<AdminMenu>> queryMenuListByRoleIds(@RequestBody List<Long> roleIds);

    /**
     * 超管查询所有菜单
     *
     * @param companyId:公司id
     * @param userId:用户id
     * @return 后台菜单表
     */
    @PostMapping("/adminMenu/queryMenuList")
    @ApiOperation("超管查询所有菜单")
    Result<List<AdminMenu>> queryMenuList(@RequestParam("userId") Long userId, @RequestParam("companyId") Long companyId);

    /**
     * 查询所有菜单
     *
     * @param
     * @return 后台菜单表
     */
    @PostMapping("/adminMenu/queryAllMenuList")
    @ApiOperation("查询所有菜单")
    Result<List<AdminMenu>> queryAllMenuList();

    /**
     * 根据名字和公司id查询配置
     *
     * @param name:名字
     * @param companyId:公司id
     * @return
     */
    @RequestMapping(value = "/adminConfig/queryConfigByNameAndCompanyId")
    Result<AdminConfig> queryConfigByNameAndCompanyId(@RequestParam("name") String name, @RequestParam("companyId") Long companyId);

    /**
     * 修改应用剩余次数
     *
     * @param name   应用名称
     * @param number 使用次数
     * @return 操作条数，0为操作失败
     */
    @RequestMapping(value = "/adminConfig/updateAdminApplyNumberInfo")
    Result<Integer> updateAdminApplyNumberInfo(@RequestParam("name") String name, @RequestParam("number") Integer number);

    /**
     * 根据状态和关闭时间查询应用产品
     *
     * @param
     * @return
     */
    @PostMapping("/adminPay/queryApplyOrderByStatusAndCloseTime")
    void queryApplyOrderByStatusAndCloseTime();

    @PostMapping("/adminLanguagePack/batchAddUpdateLanguageKey")
    public Result batchAddUpdateLanguageKey(@RequestBody AdminLanguagePackFieldBO languageBOS);


    @PostMapping(value = "/adminCompanyManager/sendExamineInfo")
    public Result setEmailInfo(@RequestBody EmailBO emailBO);


    @PostMapping("/adminDept/queryDeptMemberById")
    public Result<List<String>> queryDeprMemberById(@RequestParam("deptIds") List<String> deptIds);

    /**
     * scrm员工离职账号禁用  因scrm员工离职时没有登录状态 会导致主账号也被禁用 所以做特殊处理
     *
     * @param adminUserId:用户状态修改BO
     * @return com.kakarote.core.common.Result
     */
    @PostMapping("/adminUser/scrmAccountDisabled")
    @ApiOperation("scrm禁用账号")
    public Result scrmAccountDisabled(@RequestParam("adminUserId") Long adminUserId);

    @PostMapping("/adminUser/bindingWxWorkUserId")
    @ApiOperation("绑定企业微信用户id")
    public Result<SimpleUser> bindingWxWorkUserId(@RequestParam String token, @RequestBody WxWorkUserInfoVO wxWorkUserInfoVO);

    /**
     * 查询成员项目管理角色菜单
     *
     * @param roleIds
     * @return
     */
    @PostMapping(value = "/adminRole/getAllRoleMenu")
    Result<List<Long>> getAllRoleMenu(@RequestParam("roleIds") List<Long> roleIds);


    /**
     * 查询成员项目管理角色菜单
     *
     * @param types
     * @return
     */
    @PostMapping(value = "/adminRole/queryProjectRoleByTypes")
    Result<List<AdminRole>> queryProjectRoleByTypes(@RequestParam("types") List<Integer> types);


    /**
     * 发送企业微信消息提醒
     * @param content
     * @param companyId
     * @param wxUserId
     * @return
     */
    @PostMapping(value = "/adminSuite/cpSendMessage")
    Result cpSendMessage(@RequestParam("content")String content,@RequestParam("companyId")Long companyId,@RequestParam("wxUserId")String wxUserId);

    /**
     * ids查询部门和子级部门中员工列表
     *
     * @param ids id列表
     * @return data
     */
    @PostMapping(value = "/adminUser/queryUserListByDeptIds")
    Result<List<Long>> queryUserListByDeptIds(@RequestBody List<Long> ids);
    /**
     * 查询用户关联的附属部门id
     * @param userId
     * @return
     */
    @PostMapping(value = "/adminUser/queryUserSubsidiaryDeptId")
    Result<List<Long>> queryUserSubsidiaryDeptId(@RequestParam("userId") Long userId);

    @PostMapping("/adminDept/queryParentDeptIdByLevel")
    @ApiExplain("获取指定部门的上级部门")
    Result<List<Long>> queryParentDeptIdByLevel(@RequestBody QueryParentDeptBO queryBO);


    @PostMapping("/adminDept/groupByLevelQueryDeptId")
    @ApiExplain("以上级分组返回上级部门")
    Result<Map<Integer,List<Long>>> groupByLevelQueryDeptId(@RequestBody QueryParentDeptBO queryBO);

    @PostMapping("/adminRole/queryUserIdByRoleIds")
    @ApiExplain("通过多个角色ID查询用户")
    Result<List<Long>> queryUserIdByRoleIds(@RequestParam("roleIds") List<Long> roleIds);
    @PostMapping("/adminDingtalk/dingtalkRegister")
    @ApiOperation("钉钉注册")
    Result<String> dingtalkRegister(@RequestBody @Valid AdminCloudDingtalkCO adminCloudDingtalkCO);


    @PostMapping("/adminDingtalk/openOrClose")
    @ApiOperation("钉钉企业开启、关闭")
    Result openOrClose(@ApiParam("企业id") @RequestParam("companyId") Long companyId, @ApiParam("企业状态 1 正常 0 禁用") @RequestParam("status") Integer status);


    @PostMapping("/adminDingtalk/addUser")
    @ApiOperation("钉钉员工添加")
    Result addUser(@RequestBody @Valid AdminUserCO adminUserCO);


    /**
     * 查询module信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/adminConfig/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryModuleInfo(@RequestBody Collection ids);

}
