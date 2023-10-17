package com.kakarote.hrm.common;

import com.kakarote.common.utils.UserUtil;
import com.kakarote.hrm.entity.PO.AdminRole;
import com.kakarote.hrm.entity.VO.EmployeeInfo;
import com.kakarote.hrm.service.IHrmEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zhangzhiwei
 * user注入切面
 */
@Aspect
@Component
@Slf4j
@Order(10)
public class EmployeeAspect {

    @Autowired
    private IHrmEmployeeService employeeService;

    @Around("execution(* com.kakarote.hrm.controller..*.*(..)) && !execution(@(com.kakarote.core.common.ParamAspect) * *(..)) ")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        try {
            String mobile = UserUtil.getUser().getMobile();
            EmployeeInfo employeeInfo = employeeService.queryEmployeeInfoByMobile(mobile);
            if (employeeInfo == null) {
                employeeInfo = new EmployeeInfo();
            }
            AdminRole adminRole;
            adminRole = new AdminRole();
            adminRole.setLabel(91);
            employeeInfo.setRole(adminRole);
            EmployeeHolder.setEmployeeInfo(employeeInfo);
            return point.proceed();
        } finally {
            EmployeeHolder.remove();
        }
    }
}
