package com.kakarote.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.CalendarUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.cache.CrmCacheKey;
import com.kakarote.core.common.enums.DateFilterEnum;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.feign.admin.service.AdminService;
import com.kakarote.core.feign.crm.entity.BiEntityParams;
import com.kakarote.core.redis.Redis;
import com.kakarote.core.servlet.ApplicationContextHolder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author zhangzhiwei
 */
public class BiParamsUtil {


    /**
     * 获取查询时间，用户等参数
     *
     * @param biParams 查询参数
     * @return timeEntity
     */
    public static BiTimeEntity analyzeType(BiEntityParams biParams, Long menuId) {
        BiTimeEntity biTimeEntity = new BiTimeEntity();
        //日期数据处理
        analyzeDate(biTimeEntity, biParams);
        //员工信息处理
        analyzeUser(biTimeEntity, biParams, menuId);
        return biTimeEntity;

    }


    /**
     * 根据当前用户的部门及下属部门取交集
     *
     * @param deptIds dept列表
     * @return data
     */
    public static List<Long> filterDeptId(List<Long> deptIds) {
        if (UserUtil.isAdmin()) {
            return deptIds;
        }
        Long deptId = UserUtil.getUser().getDeptId();
        List<Long> subDeptIdList = ApplicationContextHolder.getBean(AdminService.class).queryChildDeptId(deptId).getData();
        subDeptIdList.add(deptId);
        subDeptIdList.retainAll(deptIds);
        return subDeptIdList;
    }

    /**
     * 日期时间处理，根据biParams的dateFilterEnum，计算出真实的开始时间和结束时间
     *
     * @param biTimeEntity timeEntity
     * @param biParams     biParams
     */
    public static void analyzeDate(BiTimeEntity biTimeEntity, BiEntityParams biParams) {
        if (biParams.getDateFilter() == null) {
            return;
        }
        Calendar calendar = CalendarUtil.calendar();
        Calendar startCalendar, endCalendar;
        switch (biParams.getDateFilter()) {
            case CUSTOM: {
                //自定义时间筛选，开始时间和结束时间都不可为空
                Assert.notNull(biParams.getStartDate(), CrmException::new);
                Assert.notNull(biParams.getEndDate(), CrmException::new);
                //自定义时间筛选，最多90天
                long betweenDay = DateUtil.betweenDay(biParams.getStartDate(), biParams.getEndDate(), true);
                Assert.checkBetween(betweenDay, 0, 90, CrmException::new);
                //最多筛选90天的数据，开始时间默认为当天00:00:00,结束时间默认为23:59:59
                biTimeEntity.setBeginDate(DateUtil.beginOfDay(biParams.getStartDate()));
                biTimeEntity.setEndDate(DateUtil.endOfDay(biParams.getEndDate()));
                return;
            }
            case YESTERDAY:
                //时间往前推一天
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            case TOMORROW:
                if (biParams.getDateFilter() == DateFilterEnum.TOMORROW) {
                    //时间往前推一天
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            case TODAY:
                startCalendar = CalendarUtil.beginOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case LAST_WEEK:
                //时间往前推一周
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
            case NEXT_WEEK:
                if (biParams.getDateFilter() == DateFilterEnum.NEXT_WEEK) {
                    //时间往后推一周
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }
            case WEEK:
                startCalendar = CalendarUtil.beginOfWeek(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfWeek(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
            case NEXT_MONTH:
                if (biParams.getDateFilter() == DateFilterEnum.NEXT_MONTH) {
                    calendar.add(Calendar.MONTH, 1);
                }
            case MONTH:
                startCalendar = CalendarUtil.beginOfMonth(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfMonth(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case LAST_QUARTER:
                calendar.add(Calendar.MONTH, -3);
            case NEXT_QUARTER:
                if (biParams.getDateFilter() == DateFilterEnum.NEXT_QUARTER) {
                    calendar.add(Calendar.MONTH, 3);
                }
            case QUARTER:
                startCalendar = CalendarUtil.beginOfQuarter(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfQuarter(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case NEXT_YEAR:
                calendar.add(Calendar.MONTH, 12);
            case LAST_YEAR:
                if (biParams.getDateFilter() == DateFilterEnum.LAST_YEAR) {
                    calendar.add(Calendar.MONTH, -12);
                }
            case YEAR:
                startCalendar = CalendarUtil.beginOfYear(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfYear(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case FIRST_HALF_YEAR:
                startCalendar = CalendarUtil.beginOfYear(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.calendar();
                //每年上半年是到 6月30号，23:59:59
                endCalendar.set(startCalendar.get(Calendar.YEAR), Calendar.JULY, 30, 23, 59, 59);
                break;
            case NEXT_HALF_YEAR:
                startCalendar = CalendarUtil.calendar();
                startCalendar.set(startCalendar.get(Calendar.YEAR), Calendar.AUGUST, 1, 0, 0, 0);
                endCalendar = CalendarUtil.endOfYear(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case previous7day:
                startCalendar = CalendarUtil.beginOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                startCalendar.add(Calendar.DAY_OF_YEAR, -7);
                endCalendar = CalendarUtil.endOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case previous30day:
                startCalendar = CalendarUtil.beginOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                startCalendar.add(Calendar.DAY_OF_YEAR, -30);
                endCalendar = CalendarUtil.endOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                break;
            case future7day:
                startCalendar = CalendarUtil.beginOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                startCalendar.add(Calendar.DAY_OF_YEAR, 7);
                break;
            case future30day:
                startCalendar = CalendarUtil.beginOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                endCalendar = CalendarUtil.endOfDay(CalendarUtil.calendar(calendar.getTimeInMillis()));
                startCalendar.add(Calendar.DAY_OF_YEAR, 30);
                break;
            default:
                throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        if (biParams.getDateFilter() != DateFilterEnum.CUSTOM) {
            biTimeEntity.setBeginDate(startCalendar.getTime());
            biTimeEntity.setEndDate(endCalendar.getTime());
        }
    }


    /**
     * 用户数据处理
     *
     * @param biTimeEntity timeEntity
     * @param biParams     biParams
     * @param menuId       菜单ID
     */
    public static void analyzeUser(BiTimeEntity biTimeEntity, BiEntityParams biParams, Long menuId) {
        Set<Long> userList = getAllUserList(biParams);
        biTimeEntity.setUserIds(filterUser(userList, menuId));
    }

    /**
     * 用户数据处理
     *
     * @param userIds 当前用户列表
     * @param menuId  菜单ID
     */
    public static List<Long> filterUser(Collection<Long> userIds, Long menuId) {
        if (UserUtil.isAdmin()) {
            if (userIds.isEmpty()) {
                //如果无权访问任何数据，则添加一个不存在的用户，0L
                userIds.add(0L);
            }
            return new ArrayList<>(userIds);
        }
        List<Long> authUserList = queryAuthUserList(menuId);
        ArrayList<Long> list = new ArrayList<>(userIds);
        list.retainAll(authUserList);
        if (list.isEmpty()) {
            //如果无权访问任何数据，则添加一个不存在的用户，0L
            list.add(0L);
        }
        return list;
    }


    /**
     * 用户数据处理，根据数据权限，查询用户列表，未做权限拦截
     *
     * @param biParams biParams
     */
    public static Set<Long> getAllUserList(BiEntityParams biParams) {
        if (biParams.getDataType() == null) {
            throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        Set<Long> userIdList = new HashSet<>();
        UserInfo userInfo = UserUtil.getUser();
        AdminService adminService = ApplicationContextHolder.getBean(AdminService.class);
        switch (biParams.getDataType()) {
            case MYSELF:
                userIdList.add(userInfo.getUserId());
                break;
            case MYSELF_AND_SUBORDINATE:
                userIdList.add(userInfo.getUserId());
                userIdList.addAll(adminService.queryChildUserId(userInfo.getUserId()).getData());
                break;
            case THIS_DEPARTMENT:
                userIdList.addAll(adminService.queryUserByDeptIds(Collections.singleton(userInfo.getDeptId())).getData());
                break;
            case THIS_DEPARTMENT_AND_SUBORDINATE:
                List<Long> data = adminService.queryChildDeptId(userInfo.getDeptId()).getData();
                data.add(userInfo.getDeptId());
                userIdList.addAll(adminService.queryUserByDeptIds(data).getData());
                break;
            case ALL:
                userIdList.addAll(adminService.queryUserList(1).getData());
                break;
            case CUSTOM:
                //员工列表
                if (CollUtil.isNotEmpty(biParams.getUserList())) {
                    userIdList.addAll(biParams.getUserList());
                }
                //部门列表
                if (CollUtil.isNotEmpty(biParams.getDeptList())) {
                    if (ObjectUtil.equals(1, biParams.getIsNeedChild())) {
                        //需要查询部门和子级部门中员工
                        userIdList.addAll(adminService.queryUserListByDeptIds(biParams.getDeptList()).getData());
                    } else {
                        userIdList.addAll(adminService.queryUserByDeptIds(biParams.getDeptList()).getData());
                    }
                }
                break;
            default:
                break;
        }
        return userIdList;
    }


    /**
     * 根据用户进行数据权限过滤，不会破坏原有数据
     */
    public List<Long> filterUserIdList(List<Long> allUserList, List<Long> userList) {
        List<Long> arrayList = new ArrayList<>(userList);
        arrayList.retainAll(allUserList);
        return arrayList;
    }

    /**
     * 根据菜单ID进行数据权限过滤
     */
    public List<Long> filterUserIdList(List<Long> allUserList, Long menuId) {
        List<Long> authUserList = queryAuthUserList(menuId);
        return filterUserIdList(allUserList, authUserList);
    }

    /**
     * 查询权限内用户ID
     *
     * @param menuId 菜单ID
     * @return userIds
     */
    @SuppressWarnings("unchecked")
    public static List<Long> queryAuthUserList(Long menuId) {
        Long userId = UserUtil.getUserId();
        Redis redis = BaseUtil.getRedis();
        String key = CrmCacheKey.CRM_AUTH_USER_CACHE_KEY + userId;
        Map<Object, Object> map = redis.getRedisMap(key);
        if (map != null && map.containsKey(menuId)) {
            return (List<Long>) map.get(menuId);
        }
        List<Long> userIds = ApplicationContextHolder.getBean(AdminService.class).queryUserByAuth(userId, menuId).getData();
        redis.put(key, menuId, userIds);
        //手动设置过期时间
        redis.expire(key, 30 * 60);
        return userIds;
    }

    @Data
    @Accessors(chain = true)
    @ApiModel("BiTimeEntity")
    public static class BiTimeEntity {

        @ApiModelProperty("开始时间")
        private Date beginDate;

        @ApiModelProperty("结束时间")
        private Date endDate;

        @ApiModelProperty("用户列表")
        private List<Long> userIds = new ArrayList<>();

        @ApiModelProperty("部门列表")
        private List<Long> deptIds = new ArrayList<>();

        @ApiModelProperty("企业ID")
        private Long companyId;

        @ApiModelProperty("日期格式化函数，clickhouse需要")
        private String dateFormat;


        public BiTimeEntity(Date beginDate, Date endDate, List<Long> userIds, Long companyId) {
            this.beginDate = beginDate;
            this.endDate = endDate;
            this.userIds = userIds;
            this.companyId = companyId;
        }

        public BiTimeEntity() {

        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = BeanUtil.beanToMap(this);
            return map;
        }

        /**
         * 将当前参数签名生成KEY，用于做redis等二次缓存
         *
         * @return key
         */
        public final String generateKey() {
            Map<String, Object> objectMap = BeanUtil.beanToMap(this, false, true);
            return MapUtil.sortJoin(objectMap, StrPool.AT, StrPool.COLON, false);
        }
    }
}
