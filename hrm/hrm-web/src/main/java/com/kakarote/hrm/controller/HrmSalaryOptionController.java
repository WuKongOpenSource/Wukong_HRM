package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.SetSalaryOptionBO;
import com.kakarote.hrm.entity.VO.SalaryOptionDetailVO;
import com.kakarote.hrm.service.IHrmSalaryOptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统薪资项 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmSalaryOption")
@Api(tags = "薪资管理-系统薪资项")
public class HrmSalaryOptionController {

    @Autowired
    private IHrmSalaryOptionService salaryOptionService;

    @PostMapping("/querySalaryOptionDetail")
    @ApiOperation("查询薪资项详情")
    public Result<SalaryOptionDetailVO> querySalaryOptionDetail() {
        SalaryOptionDetailVO detailVO = salaryOptionService.querySalaryOptionDetail();
        return Result.ok(detailVO);
    }


    @PostMapping("/setSalaryOption")
    @ApiOperation("修改薪资项")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, behavior = BehaviorEnum.UPDATE, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_SALARY_SHEET_SETTING)
    public Result setSalaryOption(@RequestBody SetSalaryOptionBO setSalaryOptionBO) {
        salaryOptionService.setSalaryOption(setSalaryOptionBO);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("工资表");
        operationLog.setOperationInfo("修改工资表薪资项");
        return OperationResult.ok(operationLog);
    }

}