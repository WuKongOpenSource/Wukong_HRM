package com.kakarote.hrm.utils;

import cn.hutool.core.collection.CollUtil;
import com.kakarote.core.common.enums.DataAuthEnum;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.entity.PO.HrmDept;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.VO.EmployeeInfo;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmployeeUtil {

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmDeptService deptService;


    /**
     * 根据菜单id获取权限内的员工id
     *
     * @param menuId
     * @return
     */
    public Collection<Long> queryAppraisalPlanDataAuthEmpIdByMenuId(Long menuId) {
        Collection<Long> dataAuthEmployeeIds = null;
        Integer dataAuthType = DataAuthEnum.ALL.getValue();
        //关于考核计划列表的数据查看权限，不是管理员和具备全部数据权限则只能查看创建人是自己的数据
        if (!EmployeeHolder.isHrmAdmin() && !DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            dataAuthEmployeeIds = Arrays.asList(EmployeeHolder.getEmployeeId());
        }
        return dataAuthEmployeeIds;
    }


    public static String computeCompanyAge(Integer companyAge) {
        if (companyAge <= 0) {
            return "";
        }
        int year = companyAge / 365;
        int month = (companyAge % 365) / 30;
        int day = (companyAge % 365) % 30;
        StringBuilder sb = new StringBuilder();
        if (year != 0) {
            sb.append(year).append("年");
        }
        if (month != 0) {
            sb.append(month).append("月");
        }
        if (day != 0) {
            sb.append(day).append("天");
        }
        return sb.toString();
    }

    /**
     * 根据菜单id获取权限内的员工id
     *
     * @param menuId
     * @return
     */
    public Collection<Long> queryDataAuthEmpIdByMenuId(Long menuId) {
        Collection<Long> dataAuthEmployeeIds = null;
        Integer dataAuthType =DataAuthEnum.ALL.getValue();
        if (!EmployeeHolder.isHrmAdmin() && !DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            dataAuthEmployeeIds = queryDataAuthEmpId(dataAuthType);
        } else {
            dataAuthEmployeeIds = employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).eq(HrmEmployee::getIsDel, 0).list()
                    .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList());
        }
        return dataAuthEmployeeIds;
    }

    /**
     * 查询数据权限内的员工id
     *
     * @param dataAuthType
     * @return
     */
    public Collection<Long> queryDataAuthEmpId(Integer dataAuthType) {
        EmployeeInfo employeeInfo = EmployeeHolder.getEmployeeInfo();
        Set<Long> employeeIds = new HashSet<>();
        employeeIds.add(employeeInfo.getEmployeeId());
        Set<Long> deptIds = new HashSet<>();
        if (dataAuthType != null) {
            DataAuthEnum dataAuthEnum = DataAuthEnum.parse(dataAuthType);
            switch (dataAuthEnum) {
                case ALL:
                    employeeIds.addAll(employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).list()
                            .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList()));
                    break;
                case THIS_DEPARTMENT_AND_SUBORDINATE:
                    deptIds.add(employeeInfo.getDeptId());
                    deptIds.addAll(deptService.queryChildDeptId(Collections.singletonList(employeeInfo.getDeptId())));
                    break;
                case THIS_DEPARTMENT:
                    deptIds.add(employeeInfo.getDeptId());
                    break;
                case MYSELF_AND_SUBORDINATE:
                    employeeIds.add(employeeInfo.getEmployeeId());
                    employeeIds.addAll(employeeService.queryChildEmployeeId(Collections.singletonList(employeeInfo.getEmployeeId())));
                    break;
                case MYSELF:
                    employeeIds.add(employeeInfo.getEmployeeId());
                    break;
                default:
                    break;
            }
        }
        if (CollUtil.isNotEmpty(deptIds)) {
            employeeIds.addAll(employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).in(HrmEmployee::getDeptId, deptIds).list()
                    .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList()));
        }
        return employeeIds;
    }

    public Collection<Long> queryDataAuthDeptIdByMenuId(Long menuId) {
        Collection<Long> dataAuthDeptIds = null;
/*        Integer dataAuthType = adminService.queryDataType(UserUtil.getUserId(), menuId).getData();
        if (!EmployeeHolder.isHrmAdmin() && !DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            dataAuthDeptIds = queryDataAuthDeptId(dataAuthType);
        }*/
        return dataAuthDeptIds;
    }

    /**
     * 查询数据权限内的部门id
     *
     * @param dataAuthType
     * @return
     */
    public Set<Long> queryDataAuthDeptId(Integer dataAuthType) {
        EmployeeInfo employeeInfo = EmployeeHolder.getEmployeeInfo();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        if (dataAuthType != null) {
            DataAuthEnum dataAuthEnum = DataAuthEnum.parse(dataAuthType);
            switch (dataAuthEnum) {
                case ALL:
                    deptIds.addAll(deptService.lambdaQuery().select(HrmDept::getDeptId).list()
                            .stream().map(HrmDept::getDeptId).collect(Collectors.toList()));
                    break;
                case THIS_DEPARTMENT_AND_SUBORDINATE:
                    deptIds.add(employeeInfo.getDeptId());
                    deptIds.addAll(deptService.queryChildDeptId(Collections.singletonList(employeeInfo.getDeptId())));
                    break;
                case THIS_DEPARTMENT:
                    deptIds.add(employeeInfo.getDeptId());
                    break;
                case MYSELF_AND_SUBORDINATE:
                    employeeIds.add(employeeInfo.getEmployeeId());
                    employeeIds.addAll(employeeService.queryChildEmployeeId(Collections.singletonList(employeeInfo.getEmployeeId())));
                    break;
                case MYSELF:
                    employeeIds.add(employeeInfo.getEmployeeId());
                    break;
                default:
                    break;
            }
        }
        if (CollUtil.isNotEmpty(employeeIds)) {
            deptIds.addAll(employeeService.lambdaQuery().select(HrmEmployee::getDeptId).in(HrmEmployee::getEmployeeId, employeeIds).list()
                    .stream().filter(employee -> employee != null && employee.getDeptId() != null).map(HrmEmployee::getDeptId).collect(Collectors.toList()));
        }
        return deptIds;
    }
}
