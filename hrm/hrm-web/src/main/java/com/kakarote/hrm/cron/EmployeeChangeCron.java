package com.kakarote.hrm.cron;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.hrm.constant.EmployeeEntryStatus;
import com.kakarote.hrm.constant.EmployeeStatusEnum;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.IsEnum;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.PO.HrmEmployeeChangeRecord;
import com.kakarote.hrm.entity.PO.HrmEmployeeQuitInfo;
import com.kakarote.hrm.service.IHrmAttendanceGroupService;
import com.kakarote.hrm.service.IHrmEmployeeChangeRecordService;
import com.kakarote.hrm.service.IHrmEmployeeQuitInfoService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EmployeeChangeCron {

    @Autowired
    private IHrmEmployeeChangeRecordService changeRecordService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmEmployeeQuitInfoService quitInfoService;

    @Autowired
    private IHrmAttendanceGroupService attendanceGroupService;


    /**
     * 调岗晋升转正
     *
     */
    public void employeeChangeRecords() {
        List<HrmEmployee> employeeList = new ArrayList<>();
        List<HrmEmployeeChangeRecord> hrmEmployeeChangeRecords = changeRecordService.lambdaQuery()
                .apply("to_days(effect_time) = to_days(now())")
                .groupBy(HrmEmployeeChangeRecord::getEmployeeId)
                .having("max(create_time)").list();
        hrmEmployeeChangeRecords.forEach(changeRecord -> {
            employeeList.add(employeeChangeRecord(changeRecord));
        });
        List<HrmEmployee> list = employeeService.lambdaQuery()
                .ne(HrmEmployee::getStatus, EmployeeStatusEnum.OFFICIAL.getValue())
                .apply("to_days(become_time) = to_days(now())")
                .list();
        list.forEach(employee -> {
            employee.setBecomeTime(LocalDateTime.now().toLocalDate());
            employee.setStatus(EmployeeStatusEnum.OFFICIAL.getValue());
            employee.setUpdateTime(LocalDateTime.now());
        });
        employeeList.addAll(list);
        if (CollUtil.isNotEmpty(employeeList)) {
            employeeService.saveOrUpdateBatch(employeeList);
        }
    }

    /**
     * 员工离职变更状态
     *
     */
    public void employeeQuit() {
        List<HrmEmployeeQuitInfo> quitInfoList = quitInfoService.lambdaQuery().select(HrmEmployeeQuitInfo::getEmployeeId)
                .apply("to_days(plan_quit_time) = to_days(now())").list();
        List<HrmEmployee> employeeList = quitInfoList.stream().map(quitInfo -> {
            HrmEmployee employee = employeeService.queryById(quitInfo.getEmployeeId());
            employee.setEntryStatus(EmployeeEntryStatus.ALREADY_LEAVE.getValue());
            if (ObjectUtil.isNotNull(employee.getCompanyAgeStartTime())) {
                long nowCompanyAge = LocalDateTimeUtil.between(employee.getCompanyAgeStartTime().atStartOfDay(), LocalDateTime.now()).toDays() + 1;
                if (LocalDateTimeUtil.toEpochMilli(employee.getCompanyAgeStartTime()) > System.currentTimeMillis()) {
                    nowCompanyAge = 0;
                }
                employee.setCompanyAge(Convert.toInt(nowCompanyAge));
            }
            return employee;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(employeeList)) {
            employeeService.saveOrUpdateBatch(employeeList);
        }
    }

    public static HrmEmployee employeeChangeRecord(HrmEmployeeChangeRecord changeRecord) {
        Integer changeType = changeRecord.getChangeType();
        HrmEmployee employee = new HrmEmployee();
        employee.setEmployeeId(changeRecord.getEmployeeId());
        employee.setDeptId(changeRecord.getNewDept());
        employee.setPost(changeRecord.getNewPost());
        employee.setPostLevel(changeRecord.getNewPostLevel());
        if (changeType.equals(HrmActionBehaviorEnum.CHANGE_POST.getValue())
                || changeType.equals(HrmActionBehaviorEnum.PROMOTED.getValue())
                || changeType.equals(HrmActionBehaviorEnum.DEGRADE.getValue())
                || changeType.equals(HrmActionBehaviorEnum.CHANGE_FULL_TIME_EMPLOYEE.getValue())) {
            employee.setWorkAddress(changeRecord.getNewWorkAddress());
            if (changeType.equals(HrmActionBehaviorEnum.CHANGE_FULL_TIME_EMPLOYEE.getValue())) {
                Integer probation = changeRecord.getProbation();
                employee.setProbation(probation);
                if (probation == 0) {
                    employee.setBecomeTime(LocalDateTime.now().toLocalDate());
                    employee.setStatus(EmployeeStatusEnum.OFFICIAL.getValue());
                } else {
                    employee.setStatus(EmployeeStatusEnum.TRY_OUT.getValue());
                }
            }
        } else if (changeType.equals(HrmActionBehaviorEnum.BECOME.getValue())) {
            employee.setBecomeTime(LocalDateTime.now().toLocalDate());
            employee.setStatus(EmployeeStatusEnum.OFFICIAL.getValue());
        }
        employee.setUpdateTime(LocalDateTime.now());
        return employee;
    }

    public void changeAttendanceGroup() {
        //查询出所有的员工,为之前未设置过考勤时间设置出勤班次
        attendanceGroupService.setAttendanceDateShiftByGroup();
        //查询出所有生效的考勤组
        List<HrmAttendanceGroup> hrmAttendanceGroupList = attendanceGroupService.lambdaQuery()
                .eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue()).apply("to_days(effect_time) = to_days(now())").list();
        if (CollUtil.isNotEmpty(hrmAttendanceGroupList)) {
            hrmAttendanceGroupList.forEach(hrmAttendanceGroup -> {
                attendanceGroupService.changeAttendanceGroup(hrmAttendanceGroup);
            });
        }
    }
}
