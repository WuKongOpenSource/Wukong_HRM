package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.BO.SetSalaryGroupBO;
import com.kakarote.hrm.entity.VO.SalaryGroupPageListVO;
import com.kakarote.hrm.service.IHrmSalaryGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 薪资组 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmSalaryGroup")
@Api(tags = "薪资管理-薪资组")
public class HrmSalaryGroupController {

    @Autowired
    private IHrmSalaryGroupService salaryGroupService;


    @PostMapping("/addSalaryGroup")
    @ApiOperation("添加薪资组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, behavior = BehaviorEnum.SAVE, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_SALARY_SETTING)
    public Result addSalaryGroup(@RequestBody SetSalaryGroupBO salaryGroup) {
        OperationLog operationLog = salaryGroupService.setSalaryGroup(salaryGroup);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/setSalaryGroup")
    @ApiOperation("修改薪资组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, behavior = BehaviorEnum.UPDATE, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_SALARY_SETTING)
    public Result setSalaryGroup(@RequestBody SetSalaryGroupBO salaryGroup) {
        OperationLog operationLog = salaryGroupService.setSalaryGroup(salaryGroup);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/querySalaryGroupPageList")
    @ApiOperation("查询薪资组列表")
    public Result<BasePage<SalaryGroupPageListVO>> querySalaryGroupPageList(@RequestBody PageEntity pageEntity) {
        BasePage<SalaryGroupPageListVO> page = salaryGroupService.querySalaryGroupPageList(pageEntity);
        return Result.ok(page);
    }


    @PostMapping("/delete/{groupId}")
    @ApiOperation("删除薪资组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, behavior = BehaviorEnum.DELETE, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_SALARY_SETTING)
    public Result deleteSalaryGroup(@PathVariable("groupId") Long groupId) {
        OperationLog operationLog = salaryGroupService.deleteSalaryGroup(groupId);
        return OperationResult.ok(operationLog);
    }


}

