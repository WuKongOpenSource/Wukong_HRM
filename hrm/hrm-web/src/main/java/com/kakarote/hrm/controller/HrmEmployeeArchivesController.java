package com.kakarote.hrm.controller;

import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.SendWriteArchivesBO;
import com.kakarote.hrm.entity.VO.EmployeeArchivesFieldVO;
import com.kakarote.hrm.entity.VO.PersonalInformationVO;
import com.kakarote.hrm.entity.VO.PostInformationVO;
import com.kakarote.hrm.service.IHrmEmployeeFieldService;
import com.kakarote.hrm.service.IHrmEmployeePostService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hrmEmployeeArchives")
@Api(tags = "员工档案")
@Slf4j
public class HrmEmployeeArchivesController {

    @Autowired
    private IHrmEmployeeFieldService employeeFieldService;

    @Autowired
    private IHrmEmployeePostService employeePostService;

    @Autowired
    private IHrmEmployeeService employeeService;


    @PostMapping("/queryEmployeeArchivesField")
    @ApiOperation("查询员工档案设置字段列表")
    public Result<List<EmployeeArchivesFieldVO>> queryEmployeeArchivesField() {
        List<EmployeeArchivesFieldVO> archivesFields = employeeFieldService.queryEmployeeArchivesField();
        return Result.ok(archivesFields);
    }

    @PostMapping("/setEmployeeArchivesField")
    @ApiOperation("修改员工档案字段")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_EMPLOYEE_SETTING, behavior = BehaviorEnum.UPDATE)
    public Result setEmployeeArchivesField(@RequestBody List<EmployeeArchivesFieldVO> archivesFields) {
        employeeFieldService.setEmployeeArchivesField(archivesFields);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("员工档案字段");
        operationLog.setOperationInfo("编辑员工档案字段");
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/sendWriteArchives")
    @ApiOperation("发送填写档案信息")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_EMPLOYEE_SETTING, behavior = BehaviorEnum.INVITE)
    public Result sendWriteArchives(@RequestBody SendWriteArchivesBO writeArchivesBO) {
        List<OperationLog> operationLogList = employeeFieldService.sendWriteArchives(writeArchivesBO);
        return OperationResult.ok(operationLogList);
    }

    @PostMapping("/postArchives")
    @ApiOperation("岗位档案信息")
    public Result<PostInformationVO> postArchives() {
        PostInformationVO information = employeePostService.postArchives();
        return Result.ok(information);
    }

    @PostMapping("/personalArchives")
    @ApiOperation("个人档案信息")
    public Result<PersonalInformationVO> personalArchives() {
        PersonalInformationVO personalInformationVO = employeeService.personalArchives();
        return Result.ok(personalInformationVO);
    }
}
