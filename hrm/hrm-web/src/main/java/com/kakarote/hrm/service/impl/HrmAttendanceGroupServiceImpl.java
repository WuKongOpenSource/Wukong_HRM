package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.EmployeeEntryStatus;
import com.kakarote.hrm.constant.IsEnum;
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceGroupBO;
import com.kakarote.hrm.entity.BO.SetAttendanceGroupBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.HrmAttendanceGroupVO;
import com.kakarote.hrm.entity.VO.HrmAttendanceShiftVO;
import com.kakarote.hrm.entity.VO.QueryMyAttendanceGroupVO;
import com.kakarote.hrm.mapper.HrmAttendanceGroupMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 考勤组表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
@Service
public class HrmAttendanceGroupServiceImpl extends BaseServiceImpl<HrmAttendanceGroupMapper, HrmAttendanceGroup> implements IHrmAttendanceGroupService {
    @Autowired
    private HrmAttendanceGroupMapper attendanceGroupMapper;
    @Autowired
    private IHrmDeptService deptService;
    @Autowired
    private IHrmAttendanceWifiService hrmAttendanceWifiService;
    @Autowired
    private IHrmAttendancePointService hrmAttendancePointService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmAttendanceShiftService attendanceShiftService;
    @Autowired
    private IHrmAttendanceRuleService attendanceRuleService;
    @Autowired
    private IHrmAttendanceGroupRelationDeptService attendGroupRelDeptService;
    @Autowired
    private IHrmAttendanceGroupRelationEmployeeService attendGroupRelEmpService;

    @Autowired
    private IHrmConfigService configService;

    @Autowired
    private IHrmAttendanceClockService attendanceClockService;
    @Autowired
    private IHrmAttendanceDateShiftService attendanceDateShiftService;

    private static final int TWO = 2;
    private static final int ONE = 1;

    @Override
    public BasePage<HrmAttendanceGroupVO> queryAttendanceGroupPageList(QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO) {
        checkInitAttendData();
        QueryWrapper<HrmAttendanceGroup> hrmAttendanceGroupQueryWrapper = new QueryWrapper<>();
        hrmAttendanceGroupQueryWrapper.eq("old_setting", IsEnum.NO.getValue());
        hrmAttendanceGroupQueryWrapper.like(StrUtil.isNotEmpty(queryHrmAttendanceGroupBO.getName()), "name", queryHrmAttendanceGroupBO.getName());
        BasePage<HrmAttendanceGroup> attendanceGroupBasePage = attendanceGroupMapper.selectPage(queryHrmAttendanceGroupBO.parse(), hrmAttendanceGroupQueryWrapper);
        List<HrmAttendanceGroupVO> attendanceGroupPageListVOList = new ArrayList<>();
        attendanceGroupBasePage.getList().forEach(attendanceGroup -> {
            HrmAttendanceGroupVO attendanceGroupPageListVO = new HrmAttendanceGroupVO();
            BeanUtil.copyProperties(attendanceGroup, attendanceGroupPageListVO);
            Set<Long> deptIds = attendGroupRelDeptService.lambdaQuery().eq(HrmAttendanceGroupRelationDept::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list()
                    .stream().map(HrmAttendanceGroupRelationDept::getDeptId).collect(Collectors.toSet());
            Set<Long> deptIdSet = deptService.queryChildDeptId(deptIds);
            attendanceGroupPageListVO.setDeptList(deptService.querySimpleDeptList(deptIdSet));
            Set<Long> employeeIds = attendGroupRelEmpService.lambdaQuery().eq(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list()
                    .stream().map(HrmAttendanceGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
            attendanceGroupPageListVO.setEmployeeList(employeeService.querySimpleEmployeeList(employeeIds));
            HrmAttendanceRule hrmAttendanceRule = attendanceRuleService.getById(attendanceGroup.getAttendanceRuleId());
            if (hrmAttendanceRule != null) {
                attendanceGroupPageListVO.setAttendanceRuleName(hrmAttendanceRule.getAttendanceRuleName());
            }
            attendanceGroupPageListVO.setShiftSetting(StrUtil.splitTrim(attendanceGroup.getShiftSetting(), Const.SEPARATOR));
            Map<Integer, Map<String, Object>> attendanceDate = attendanceShiftService.queryAttendanceDate(attendanceGroup.getShiftSetting());
            String specialStr = Optional.ofNullable(attendanceGroup.getSpecialDateSetting()).orElse("");
            attendanceGroupPageListVO.setSpecialDateSetting(JSON.parse(specialStr));
            attendanceGroupPageListVO.setAttendanceDate(attendanceDate);
            List<HrmAttendancePoint> pointList = hrmAttendancePointService.lambdaQuery().eq(HrmAttendancePoint::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list();
            attendanceGroupPageListVO.setPointList(pointList);
            List<HrmAttendanceWifi> wifiList = hrmAttendanceWifiService.lambdaQuery().eq(HrmAttendanceWifi::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list();
            attendanceGroupPageListVO.setWifiList(wifiList);
            attendanceGroupPageListVOList.add(attendanceGroupPageListVO);
        });
        BasePage<HrmAttendanceGroupVO> page = new BasePage<>(attendanceGroupBasePage.getCurrent(), attendanceGroupBasePage.getSize(), attendanceGroupBasePage.getTotal());
        page.setList(attendanceGroupPageListVOList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAttendanceGroup(SetAttendanceGroupBO attendanceGroup) {
        HrmAttendanceGroup hrmAttendanceGroup = BeanUtil.copyProperties(attendanceGroup, HrmAttendanceGroup.class);
        //获取考勤分组ID
        Long attendanceGroupId = hrmAttendanceGroup.getAttendanceGroupId();
        List<Long> employeeIdList = attendanceGroup.getEmployeeIds();
        List<Long> deptIdsList = attendanceGroup.getDeptIds();
        //如果存在员工和部门都选择，判断当前考勤范围与员工唯一
        if (CollUtil.isNotEmpty(employeeIdList) && CollUtil.isNotEmpty(deptIdsList)) {
            Set<Long> deptIds = deptService.queryChildDeptId(deptIdsList);
            List<Long> empIdList = employeeService.lambdaQuery().in(HrmEmployee::getDeptId, deptIds).list().stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList());
            Collection<Long> empIntersection = CollUtil.intersection(employeeIdList, empIdList);
            if (!empIntersection.isEmpty()) {
                throw new CrmException(HrmCodeEnum.THE_ATTEND_GROUP_ALREADY_DEPT_EMPLOYEE_ERROR);
            }
        }

        hrmAttendanceGroup.setShiftSetting(TagUtil.fromLongSet(attendanceGroup.getShiftSetting()));
        if (ObjectUtil.isNotEmpty(attendanceGroup.getSpecialDateSetting())) {
            //设置特殊日期
            hrmAttendanceGroup.setSpecialDateSetting(JSON.toJSONString(attendanceGroup.getSpecialDateSetting()));
        }
        //获取当日生效日期
        LocalDateTime effectTime = LocalDate.now().atStartOfDay();
        if (ObjectUtil.isNotNull(attendanceGroupId)) {
            //修改考勤分组
            HrmAttendanceGroup attendGroup = lambdaQuery().eq(HrmAttendanceGroup::getAttendanceGroupId, attendanceGroupId).one();
            //设置为历史考勤分组
            lambdaUpdate().eq(HrmAttendanceGroup::getAttendanceGroupId, attendanceGroupId).set(HrmAttendanceGroup::getOldSetting, IsEnum.YES.getValue()).update();
            //生效时间为第二天
            effectTime = effectTime.plusDays(1);
            hrmAttendanceGroup.setEffectTime(effectTime);
            //关联旧的分组ID
            hrmAttendanceGroup.setOldGroupId(attendanceGroupId);
            hrmAttendanceGroup.setAttendanceGroupId(null);
            //设置是否为默认分组
            hrmAttendanceGroup.setIsDefaultSetting(attendGroup.getIsDefaultSetting());
        } else {
            hrmAttendanceGroup.setIsDefaultSetting(IsEnum.NO.getValue());
            //设置为立即生效
            hrmAttendanceGroup.setEffectTime(effectTime);
        }
        hrmAttendanceGroup.setOldSetting(IsEnum.NO.getValue());
        save(hrmAttendanceGroup);
        //涉及到的所有员工
        Set<Long> allEmployeeIdList = new HashSet<>();
        if (CollUtil.isNotEmpty(attendanceGroup.getDeptIds())) {
            LocalDateTime finalEffectTime = effectTime;
            List<HrmAttendanceGroupRelationDept> hrmAttendanceGroupRelationDeptList = attendanceGroup.getDeptIds().stream().map(deptId -> HrmAttendanceGroupRelationDept.builder()
                            .deptId(deptId).effectTime(finalEffectTime).attendanceGroupId(hrmAttendanceGroup.getAttendanceGroupId()).build())
                    .collect(Collectors.toList());
            attendGroupRelDeptService.saveBatch(hrmAttendanceGroupRelationDeptList);
            if (ObjectUtil.isNull(attendanceGroupId)) {
                //新增立即生效,移除已经生效在其他分组的部门(包含子部门)
                Set<Long> allDeptIdList = deptService.queryChildDeptId(attendanceGroup.getDeptIds());
                attendGroupRelDeptService.lambdaUpdate().ne(HrmAttendanceGroupRelationDept::getAttendanceGroupId, hrmAttendanceGroup.getAttendanceGroupId())
                        .in(HrmAttendanceGroupRelationDept::getDeptId, allDeptIdList)
                        .le(HrmAttendanceGroupRelationDept::getEffectTime, LocalDate.now().atStartOfDay()).remove();
                //获取涉及到的员工,移除部门内包含的员工
                Set<Long> empIdList = employeeService.lambdaQuery().in(HrmEmployee::getDeptId, allDeptIdList).list()
                        .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toSet());
                attendGroupRelEmpService.lambdaUpdate().ne(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, hrmAttendanceGroup.getAttendanceGroupId())
                        .in(HrmAttendanceGroupRelationEmployee::getEmployeeId, empIdList)
                        .le(HrmAttendanceGroupRelationEmployee::getEffectTime, LocalDate.now().atStartOfDay()).remove();
                allEmployeeIdList = empIdList;

            }
        }
        if (CollUtil.isNotEmpty(attendanceGroup.getEmployeeIds())) {
            LocalDateTime finalEffectTime = effectTime;
            List<HrmAttendanceGroupRelationEmployee> hrmAttendanceGroupRelationEmployees = attendanceGroup.getEmployeeIds().stream().map(employeeId -> HrmAttendanceGroupRelationEmployee
                    .builder().employeeId(employeeId).effectTime(finalEffectTime).attendanceGroupId(hrmAttendanceGroup.getAttendanceGroupId())
                    .build()).collect(Collectors.toList());
            attendGroupRelEmpService.saveBatch(hrmAttendanceGroupRelationEmployees);
            if (ObjectUtil.isNull(attendanceGroupId)) {
                Set<Long> employeeIds = new HashSet<>(attendanceGroup.getEmployeeIds());
                if (CollUtil.isNotEmpty(attendanceGroup.getDeptIds())) {
                    //获取涉及到所有部门
                    Set<Long> allDeptIdList = deptService.queryChildDeptId(attendanceGroup.getDeptIds());
                    Set<Long> empIdList = employeeService.lambdaQuery().in(HrmEmployee::getDeptId, allDeptIdList).list()
                            .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toSet());
                    //查询出所有员工
                    CollUtil.addAll(employeeIds, empIdList);
                }
                allEmployeeIdList = employeeIds;
                //新增立即生效,移除已经生效在其他分组的员工
                attendGroupRelEmpService.lambdaUpdate().ne(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, hrmAttendanceGroup.getAttendanceGroupId())
                        .in(HrmAttendanceGroupRelationEmployee::getEmployeeId, employeeIds)
                        .le(HrmAttendanceGroupRelationEmployee::getEffectTime, LocalDate.now().atStartOfDay()).remove();
            }
        }
        if (CollUtil.isNotEmpty(allEmployeeIdList) && ObjectUtil.isNull(attendanceGroupId)) {
            LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
            //删除涉及当前员工列表的今日班次
            attendanceDateShiftService.lambdaUpdate().in(HrmAttendanceDateShift::getEmployeeId, allEmployeeIdList)
                    .eq(HrmAttendanceDateShift::getUserShiftTime, localDateTime).remove();
            //查询出所有员工赋值今日班次
            HrmAttendanceShift hrmAttendanceShift = attendanceClockService.getHrmAttendanceShiftByGroup(hrmAttendanceGroup);
            List<HrmAttendanceDateShift> hrmAttendanceDateShiftList = new ArrayList<>();
            for (Long employeeId : allEmployeeIdList) {
                HrmAttendanceDateShift hrmAttendanceDateShift = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceDateShift.class);
                hrmAttendanceDateShift.setEffectTime(localDateTime);
                hrmAttendanceDateShift.setEmployeeId(employeeId);
                hrmAttendanceDateShift.setUserShiftTime(localDateTime);
                hrmAttendanceDateShiftList.add(hrmAttendanceDateShift);
            }
            attendanceDateShiftService.saveBatch(hrmAttendanceDateShiftList);
        }
        List<HrmAttendancePoint> pointList = attendanceGroup.getPointList();
        if (CollUtil.isNotEmpty(pointList)) {
            //打卡地点
            pointList.forEach(point -> {
                point.setAttendanceGroupId(hrmAttendanceGroup.getAttendanceGroupId());
                point.setAttendancePointId(null);
            });
        }
        hrmAttendancePointService.saveBatch(pointList);
        List<HrmAttendanceWifi> wifiList = attendanceGroup.getWifiList();
        if (CollUtil.isNotEmpty(wifiList)) {
            //打卡WiFi
            wifiList.forEach(wifi -> {
                wifi.setAttendanceGroupId(hrmAttendanceGroup.getAttendanceGroupId());
                wifi.setAttendanceWifiId(null);
            });
        }
        hrmAttendanceWifiService.saveBatch(wifiList);
    }

    @Override
    public void verifyAttendanceGroupName(QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO) {
        List<HrmAttendanceGroup> attendanceGroupList = lambdaQuery().eq(HrmAttendanceGroup::getName, queryHrmAttendanceGroupBO.getName())
                .ne(queryHrmAttendanceGroupBO.getAttendanceGroupId() != null, HrmAttendanceGroup::getAttendanceGroupId, queryHrmAttendanceGroupBO.getAttendanceGroupId())
                .eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue()).list();
        if (attendanceGroupList.size() > 0) {
            throw new CrmException(HrmCodeEnum.GROUP_NAME_ALREADY_EXISTS, attendanceGroupList.get(0).getName());
        }

    }

    @Override
    public HrmAttendanceGroup queryAttendanceGroup(Long employeeId) {
        HrmEmployee employee = employeeService.getById(employeeId);
        Long attendanceGroupId;
        HrmAttendanceGroupRelationEmployee attendGroupRelationEmp = attendGroupRelEmpService.lambdaQuery()
                .le(HrmAttendanceGroupRelationEmployee::getEffectTime, LocalDate.now().atStartOfDay())
                .eq(HrmAttendanceGroupRelationEmployee::getEmployeeId, employeeId).orderByDesc(HrmAttendanceGroupRelationEmployee::getCreateTime).one();
        if (ObjectUtil.isEmpty(attendGroupRelationEmp)) {
            HrmAttendanceGroupRelationDept attendGroupRelationDept = null;
            if (ObjectUtil.isNotNull(employee.getDeptId())) {
                Set<Long> deptIds = deptService.queryParentDeptId(employee.getDeptId());
                attendGroupRelationDept = attendGroupRelDeptService.lambdaQuery().le(HrmAttendanceGroupRelationDept::getEffectTime, LocalDate.now().atStartOfDay())
                        .in(HrmAttendanceGroupRelationDept::getDeptId, deptIds).orderByDesc(HrmAttendanceGroupRelationDept::getCreateTime).one();
            }
            if (ObjectUtil.isEmpty(attendGroupRelationDept)) {
                attendanceGroupId = lambdaQuery().eq(HrmAttendanceGroup::getIsDefaultSetting, IsEnum.YES.getValue()).eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue())
                        .oneOpt().map(HrmAttendanceGroup::getAttendanceGroupId).orElse(null);
            } else {
                attendanceGroupId = attendGroupRelationDept.getAttendanceGroupId();
            }
        } else {
            attendanceGroupId = attendGroupRelationEmp.getAttendanceGroupId();
        }
        HrmAttendanceGroup attendanceGroup = attendanceGroupMapper.selectById(attendanceGroupId);
        if (ObjectUtil.isNull(attendanceGroup)) {
            //查询到考勤组都不存在,再次使用默认的考勤配置
            checkInitAttendData();
            attendanceGroupId = lambdaQuery().eq(HrmAttendanceGroup::getIsDefaultSetting, IsEnum.YES.getValue())
                    .eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue()).oneOpt().map(HrmAttendanceGroup::getAttendanceGroupId).orElse(null);
            //使用默认的考勤组
            return attendanceGroupMapper.selectById(attendanceGroupId);
        }
        return attendanceGroup;
    }

    @Override
    public QueryMyAttendanceGroupVO queryMyAttendanceGroup(Long employeeId) {
        checkInitAttendData();
        QueryMyAttendanceGroupVO queryMyAttendanceGroupVO = new QueryMyAttendanceGroupVO();
        UserInfo user = UserUtil.getUser();

        HrmAttendanceGroup queryAttendanceGroup = queryAttendanceGroup(employeeId);
        queryMyAttendanceGroupVO.setAttendanceGroupName(queryAttendanceGroup.getName());
        SimpleUser userInfo = UserCacheUtil.getSimpleUser(EmployeeCacheUtil.getUserId(employeeId));
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        queryMyAttendanceGroupVO.setRealname(hrmEmployee.getEmployeeName());
        HrmAttendanceRule hrmAttendanceRule = attendanceRuleService.getById(queryAttendanceGroup.getAttendanceRuleId());
        if (hrmAttendanceRule != null) {
            List<Map<String, Object>> hrmAttendanceRuleList = new ArrayList<>();
            Map<String, Object> lateMap = new HashMap<>();
            lateMap.put("type", "迟到");
            lateMap.put("money", hrmAttendanceRule.getLateDeductMoney());
            if (hrmAttendanceRule.getLateRuleMethod() == ONE) {
                lateMap.put("method", "分钟");
            } else if (hrmAttendanceRule.getLateRuleMethod() == TWO) {
                lateMap.put("method", "次");
            } else {
                lateMap.put("method", "月");
            }
            Map<String, Object> earlyMap = new HashMap<>();
            earlyMap.put("type", "早退");
            earlyMap.put("money", hrmAttendanceRule.getEarlyDeductMoney());
            if (hrmAttendanceRule.getEarlyRuleMethod() == ONE) {
                earlyMap.put("method", "分钟");
            } else if (hrmAttendanceRule.getEarlyRuleMethod() == TWO) {
                earlyMap.put("method", "次");
            } else {
                earlyMap.put("method", "月");
            }
            Map<String, Object> missCardMap = new HashMap<>();
            missCardMap.put("type", "缺卡");
            missCardMap.put("money", hrmAttendanceRule.getMisscardDeductMoney());
            if (hrmAttendanceRule.getMisscardRuleMethod() == ONE) {
                missCardMap.put("method", "分钟");
            } else if (hrmAttendanceRule.getMisscardRuleMethod() == TWO) {
                missCardMap.put("method", "次");
            } else {
                missCardMap.put("method", "月");
            }
            Map<String, Object> absenteeismMap = new HashMap<>();
            absenteeismMap.put("type", "旷工");
            absenteeismMap.put("money", hrmAttendanceRule.getAbsenteeismDeductMoney());
            absenteeismMap.put("method", "天");
            hrmAttendanceRuleList.add(lateMap);
            hrmAttendanceRuleList.add(earlyMap);
            hrmAttendanceRuleList.add(missCardMap);
            hrmAttendanceRuleList.add(absenteeismMap);
            queryMyAttendanceGroupVO.setHrmAttendanceRule(hrmAttendanceRuleList);
        }
        if (queryAttendanceGroup.getIsOpenPointCard() == IsEnum.YES.getValue()) {
            List<HrmAttendancePoint> pointList = hrmAttendancePointService.lambdaQuery().eq(HrmAttendancePoint::getAttendanceGroupId, queryAttendanceGroup.getAttendanceGroupId()).list();
            queryMyAttendanceGroupVO.setPointList(pointList);
        }
        if (queryAttendanceGroup.getIsOpenWifiCard() == IsEnum.YES.getValue()) {
            List<HrmAttendanceWifi> wifiList = hrmAttendanceWifiService.lambdaQuery().eq(HrmAttendanceWifi::getAttendanceGroupId, queryAttendanceGroup.getAttendanceGroupId()).list();
            queryMyAttendanceGroupVO.setWifiList(wifiList);
        }
        List<String> shiftList = StrUtil.splitTrim(queryAttendanceGroup.getShiftSetting(), Const.SEPARATOR);
        List<HrmAttendanceShiftVO> hrmAttendanceShiftList = new ArrayList<>();
        for (int i = 0; i < shiftList.size(); i++) {
            HrmAttendanceShift attendanceShift = attendanceShiftService.getById(shiftList.get(i));
            hrmAttendanceShiftList.add(BeanUtil.copyProperties(attendanceShift, HrmAttendanceShiftVO.class));
        }
        queryMyAttendanceGroupVO.setAttendanceDate(hrmAttendanceShiftList);
        return queryMyAttendanceGroupVO;
    }

    @Override
    public OperationLog deleteAttendanceGroup(Long attendanceGroupId) {
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupMapper.selectById(attendanceGroupId);
        if (hrmAttendanceGroup.getIsDefaultSetting() == IsEnum.YES.getValue()) {
            throw new CrmException(HrmCodeEnum.ATTEND_DEFAULT_CANNOT_BE_DELETED, "考勤组");
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationInfo("删除考勤组：" + hrmAttendanceGroup.getName());
        operationLog.setOperationObject(hrmAttendanceGroup.getName());

        attendanceGroupMapper.deleteById(attendanceGroupId);
        //删除关联的员工和部门
        attendGroupRelDeptService.lambdaUpdate().eq(HrmAttendanceGroupRelationDept::getAttendanceGroupId, attendanceGroupId).remove();
        attendGroupRelEmpService.lambdaUpdate().eq(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroupId).remove();

        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeAttendanceGroup(HrmAttendanceGroup attendanceGroup) {
        //删除旧考勤组员工和部门相关的数据
        if (ObjectUtil.isNotNull(attendanceGroup.getOldGroupId())) {
            attendGroupRelDeptService.lambdaUpdate().eq(HrmAttendanceGroupRelationDept::getAttendanceGroupId, attendanceGroup.getOldGroupId()).remove();
            attendGroupRelEmpService.lambdaUpdate().eq(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroup.getOldGroupId()).remove();
            //删除旧的考勤组
            lambdaUpdate().eq(HrmAttendanceGroup::getAttendanceGroupId, attendanceGroup.getOldGroupId()).remove();
            //更新关联的旧考勤组为null
            LambdaUpdateWrapper<HrmAttendanceGroup> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(HrmAttendanceGroup::getOldGroupId, null).eq(HrmAttendanceGroup::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId());
            update(wrapper);
        }
        //删除新的其他考勤组与当前考勤组重复的员工和部门（包含子部门）相关数据
        Set<Long> allDeptIdList = new HashSet<>();
        //涉及到的所有员工
        Set<Long> allEmployeeIdList = new HashSet<>();
        Set<Long> deptIds = attendGroupRelDeptService.lambdaQuery().eq(HrmAttendanceGroupRelationDept::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list()
                .stream().map(HrmAttendanceGroupRelationDept::getDeptId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(deptIds)) {
            allDeptIdList = deptService.queryChildDeptId(deptIds);
            attendGroupRelDeptService.lambdaUpdate().ne(HrmAttendanceGroupRelationDept::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId())
                    .le(HrmAttendanceGroupRelationDept::getEffectTime, LocalDate.now().atStartOfDay())
                    .in(HrmAttendanceGroupRelationDept::getDeptId, allDeptIdList).remove();
        }
        Set<Long> employeeIds = attendGroupRelEmpService.lambdaQuery().eq(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId()).list()
                .stream().map(HrmAttendanceGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(employeeIds)) {
            if (CollUtil.isNotEmpty(allDeptIdList)) {
                Set<Long> empIdList = employeeService.lambdaQuery().in(HrmEmployee::getDeptId, allDeptIdList).list()
                        .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toSet());
                //查询出所有员工
                CollUtil.addAll(employeeIds, empIdList);
            }
            attendGroupRelEmpService.lambdaUpdate().ne(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId())
                    .le(HrmAttendanceGroupRelationEmployee::getEffectTime, LocalDate.now().atStartOfDay())
                    .in(HrmAttendanceGroupRelationEmployee::getEmployeeId, employeeIds).remove();
            allEmployeeIdList = employeeIds;
        } else {
            if (CollUtil.isNotEmpty(allDeptIdList)) {
                Set<Long> empIdList = employeeService.lambdaQuery().in(HrmEmployee::getDeptId, allDeptIdList).list()
                        .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toSet());
                attendGroupRelEmpService.lambdaUpdate().ne(HrmAttendanceGroupRelationEmployee::getAttendanceGroupId, attendanceGroup.getAttendanceGroupId())
                        .le(HrmAttendanceGroupRelationEmployee::getEffectTime, LocalDate.now().atStartOfDay())
                        .in(HrmAttendanceGroupRelationEmployee::getEmployeeId, empIdList).remove();
                allEmployeeIdList = empIdList;
            }
        }
        if (CollUtil.isNotEmpty(allEmployeeIdList)) {
            LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
            //删除涉及当前员工列表的今日班次
            attendanceDateShiftService.lambdaUpdate().in(HrmAttendanceDateShift::getEmployeeId, allEmployeeIdList)
                    .eq(HrmAttendanceDateShift::getUserShiftTime, localDateTime).remove();
            //查询出所有员工赋值今日班次
            HrmAttendanceShift hrmAttendanceShift = attendanceClockService.getHrmAttendanceShiftByGroup(attendanceGroup);
            List<HrmAttendanceDateShift> hrmAttendanceDateShiftList = new ArrayList<>();
            for (Long employeeId : allEmployeeIdList) {
                HrmAttendanceDateShift hrmAttendanceDateShift = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceDateShift.class);
                hrmAttendanceDateShift.setEffectTime(localDateTime);
                hrmAttendanceDateShift.setEmployeeId(employeeId);
                hrmAttendanceDateShift.setUserShiftTime(localDateTime);
                hrmAttendanceDateShiftList.add(hrmAttendanceDateShift);
            }
            attendanceDateShiftService.saveBatch(hrmAttendanceDateShiftList);
        }
    }



    /**
     * 为未初始化今日之前考勤班次的员工设置出勤班次
     *
     */
    @Override
    public void setAttendanceDateShiftByGroup() {
        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        List<HrmEmployee> hrmEmployeeList;
        //查询出已经设置过的历史出勤班次员工列表
        Set<Long> employeeIds = attendanceDateShiftService.queryEmployeeIds(localDateTime);
        hrmEmployeeList = employeeService.lambdaQuery().in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue(), EmployeeEntryStatus.TO_LEAVE.getValue())
                .eq(HrmEmployee::getIsDel, 0).list();
        if (CollUtil.isNotEmpty(employeeIds)) {
            //移除已经生成过的员工
            hrmEmployeeList.removeIf(employee -> employeeIds.contains(employee.getEmployeeId()));
        }
        if (CollUtil.isNotEmpty(hrmEmployeeList)) {
            LocalDateTime yesterday = localDateTime.plusDays(-1);
            List<HrmAttendanceDateShift> hrmAttendanceDateShiftList = new ArrayList<>();
            for (HrmEmployee hrmEmployee : hrmEmployeeList) {
                LocalDate startTime = hrmEmployee.getCreateTime().toLocalDate();
                long createMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime());
                long entryMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime());
                long yesterdayMilli = LocalDateTimeUtil.toEpochMilli(yesterday);
                if (createMilli < entryMilli) {
                    //创建时间再入职时间之前，则以员工入职日期开始考勤
                    startTime = hrmEmployee.getEntryTime();
                }
                if (LocalDateTimeUtil.toEpochMilli(startTime) <= yesterdayMilli) {
                    //开始时间小于或等于昨天
                    Long employeeId = hrmEmployee.getEmployeeId();
                    List<String> dates = attendanceClockService.findDates(startTime, yesterday.toLocalDate());
                    HrmAttendanceGroup hrmAttendanceGroup = queryAttendanceGroup(employeeId);
                    Map<String, HrmAttendanceShift> hrmAttendanceShiftMap = attendanceClockService.getHrmAttendanceShiftMap(dates, hrmAttendanceGroup, employeeId);
                    for (String date : dates) {
                        HrmAttendanceShift hrmAttendanceShift = hrmAttendanceShiftMap.get(date);
                        HrmAttendanceDateShift hrmAttendanceDateShift = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceDateShift.class);
                        hrmAttendanceDateShift.setEffectTime(localDateTime);
                        hrmAttendanceDateShift.setEmployeeId(employeeId);
                        hrmAttendanceDateShift.setUserShiftTime(LocalDateTimeUtil.parse(date, DatePattern.NORM_DATE_PATTERN));
                        hrmAttendanceDateShiftList.add(hrmAttendanceDateShift);
                    }
                }
            }
            if (CollUtil.isNotEmpty(hrmAttendanceDateShiftList)) {
                int listSize = hrmAttendanceDateShiftList.size();
                int toIndex = 1000;
                for (int i = 0; i < listSize; i += 1000) {
                    if (i + 1000 > listSize) {
                        toIndex = listSize - i;
                    }
                    //分割list
                    List<HrmAttendanceDateShift> newList = hrmAttendanceDateShiftList.subList(i, i + toIndex);
                    attendanceDateShiftService.saveBatch(newList);
                }
            }
        }
    }

    /**
     * 查询是否存在默认考勤组
     */
    @Override
    public void checkInitAttendData() {
        List<HrmAttendanceGroup> attendanceGroupList = lambdaQuery().eq(HrmAttendanceGroup::getIsDefaultSetting, IsEnum.YES.getValue()).list();
        if (CollUtil.isEmpty(attendanceGroupList)) {
            //未初始化过默认考勤组
            configService.initAttendData();
        }
    }

}
