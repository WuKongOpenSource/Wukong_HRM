package com.kakarote.hrm.utils;

import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.kakarote.core.common.cache.HrmCacheKey;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.service.IHrmEmployeeService;
import com.kakarote.ids.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * @author zhangzhiwei
 * 用户缓存相关方法
 */
@Component
public class EmployeeCacheUtil {
    static EmployeeCacheUtil ME;

    @PostConstruct
    public void init() {
        ME = this;
        employeeUserIdCache = cacheManager.getOrCreateCache(QuickConfig.newBuilder(HrmCacheKey.HRM_EMPLOYEE_USER_CACHE).cacheType(CacheType.REMOTE).expire(Duration.ofDays(3)).build());
        employeeMobileCache = cacheManager.getOrCreateCache(QuickConfig.newBuilder(HrmCacheKey.HRM_EMPLOYEE_MOBILE_CACHE).cacheType(CacheType.REMOTE).expire(Duration.ofDays(3)).build());
    }

    private Cache<Long, Long> employeeUserIdCache;

    private Cache<Long, String> employeeMobileCache;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 根据员工ID获取系统用户id
     *
     * @param employeeId 员工id
     * @return data
     */
    public static Long getUserId(Long employeeId) {
        if (employeeId == null) {
            return 0L;
        }
        Long key = employeeId;
        Long userId = ME.employeeUserIdCache.get(key);
        if (userId == null) {
            String employeeMobile = getEmployeeMobile(employeeId);
            if (StrUtil.isNotEmpty(employeeMobile)) {
                userId = ME.userService.queryUserIdByMobile(employeeMobile).getData();
                ME.employeeUserIdCache.put(key, userId);
            } else {
                userId = 0L;
            }
        }
        return userId;
    }

    /**
     * 根据员工ID获取系统用户id
     *
     * @param employeeId 员工id
     * @return data
     */
    public static String getEmployeeMobile(Long employeeId) {
        if (employeeId == null) {
            return "";
        }
        Long key = employeeId;
        String mobile = ME.employeeMobileCache.get(key);
        if (mobile == null) {
            mobile = ME.employeeService.lambdaQuery().select(HrmEmployee::getMobile).eq(HrmEmployee::getEmployeeId, employeeId).oneOpt().map(HrmEmployee::getMobile).orElse("");
            ME.employeeMobileCache.put(key, mobile);
        }
        return mobile;
    }


}
