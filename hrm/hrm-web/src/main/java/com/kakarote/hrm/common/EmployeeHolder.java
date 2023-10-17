package com.kakarote.hrm.common;

import com.kakarote.common.utils.UserUtil;
import com.kakarote.hrm.entity.PO.AdminRole;
import com.kakarote.hrm.entity.VO.EmployeeInfo;

public class EmployeeHolder {

    private static ThreadLocal<EmployeeInfo> employeeInfoThreadLocal = new ThreadLocal<>();


    public static EmployeeInfo getEmployeeInfo() {
        return employeeInfoThreadLocal.get();
    }

    public static Long getEmployeeId() {
        if (employeeInfoThreadLocal.get() != null) {
            return employeeInfoThreadLocal.get().getEmployeeId();
        } else {
            return -1L;
        }
    }

    /**
     * 是否是人资管理角色
     *
     * @return
     */
    public static boolean isHrmAdmin() {
        EmployeeInfo employeeInfo = employeeInfoThreadLocal.get();
        AdminRole role = employeeInfo.getRole();
        return UserUtil.isAdmin() || (role != null && role.getLabel() == 91);
    }

    public static void setEmployeeInfo(EmployeeInfo employeeInfo) {
        employeeInfoThreadLocal.set(employeeInfo);
    }

    public static void remove() {
        employeeInfoThreadLocal.remove();
    }
}
