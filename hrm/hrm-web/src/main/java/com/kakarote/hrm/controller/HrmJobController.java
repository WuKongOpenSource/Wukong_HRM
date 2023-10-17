package com.kakarote.hrm.controller;

import com.kakarote.core.common.ParamAspect;
import com.kakarote.hrm.cron.EmployeeChangeCron;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hrmJob")
public class HrmJobController {

    @Autowired
    private EmployeeChangeCron employeeChangeCron;

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @PostMapping("/employeeChangeRecords")
    @ParamAspect
    public void employeeChangeRecords() {
        employeeChangeCron.employeeChangeRecords();
        employeeChangeCron.employeeQuit();
    }

    @PostMapping("/changeAttendanceGroup")
    @ParamAspect
    public void changeAttendanceGroup() {
        employeeChangeCron.changeAttendanceGroup();
    }

    @PostMapping("/overdueUnAudit")
    @ParamAspect
    public void overdueUnAudit() {
        appraisalEmployeeService.dealWithOverdueUnAudit();
    }

}
