package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.AddInsuranceEmpBO;
import com.kakarote.hrm.entity.BO.QueryEmpInsuranceMonthBO;
import com.kakarote.hrm.entity.BO.QueryInsuranceRecordListBO;
import com.kakarote.hrm.entity.BO.UpdateInsuranceProjectBO;
import com.kakarote.hrm.entity.PO.HrmInsuranceMonthEmpRecord;
import com.kakarote.hrm.entity.VO.EmpInsuranceByIdVO;
import com.kakarote.hrm.entity.VO.QueryEmpInsuranceMonthVO;
import com.kakarote.hrm.entity.VO.SimpleHrmEmployeeVO;
import com.kakarote.hrm.service.IHrmInsuranceMonthEmpRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 员工每月社保记录 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmInsuranceMonthEmpRecord")
@Api(tags = "社保管理-员工社保")
public class HrmInsuranceMonthEmpRecordController {


    @Autowired
    private IHrmInsuranceMonthEmpRecordService monthEmpRecordService;

    @PostMapping("/queryEmpInsuranceMonth")
    @ApiOperation("查询每月员工社保列表")
    public Result<BasePage<QueryEmpInsuranceMonthVO>> queryEmpInsuranceMonth(@RequestBody QueryEmpInsuranceMonthBO queryEmpInsuranceMonthBO) {
        BasePage<QueryEmpInsuranceMonthVO> page = monthEmpRecordService.queryEmpInsuranceMonth(queryEmpInsuranceMonthBO);
        return Result.ok(page);
    }

    @PostMapping("/queryById/{iempRecordId}")
    @ApiOperation("查询员工社保详情")
    public Result<EmpInsuranceByIdVO> queryById(@PathVariable("iempRecordId") String iempRecordId) {
        EmpInsuranceByIdVO empInsuranceByIdVO = monthEmpRecordService.queryById(iempRecordId);
        return Result.ok(empInsuranceByIdVO);
    }


    @PostMapping("/stop")
    @ApiOperation("停止参保")
    @OperateLog(apply = ApplyEnum.HRM, behavior = BehaviorEnum.STOP_INSURANCE, object = OperateObjectEnum.HRM_INSURANCE_SCHEME)
    public Result stop(@RequestBody List<Long> ids) {
        List<OperationLog> operationLogList = monthEmpRecordService.stop(ids);
        return OperationResult.ok(operationLogList);
    }

    @PostMapping("/updateInsuranceProject")
    @ApiOperation("修改参保方案项目")
    @OperateLog(apply = ApplyEnum.HRM, behavior = BehaviorEnum.UPDATE_INSURANCE_SCHEME, object = OperateObjectEnum.HRM_INSURANCE_SCHEME)
    public Result updateInsuranceProject(@RequestBody UpdateInsuranceProjectBO updateInsuranceProjectBO) {
        OperationLog operationLog = monthEmpRecordService.updateInsuranceProject(updateInsuranceProjectBO);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/batchUpdateInsuranceProject")
    @ApiOperation("批量修改参保方案项目")
    @OperateLog(apply = ApplyEnum.HRM, behavior = BehaviorEnum.UPDATE_INSURANCE_SCHEME, object = OperateObjectEnum.HRM_INSURANCE_SCHEME)
    public Result batchUpdateInsuranceProject(@RequestBody UpdateInsuranceProjectBO updateInsuranceProjectBO) {
        List<OperationLog> operationLogList = monthEmpRecordService.batchUpdateInsuranceProject(updateInsuranceProjectBO);
        return OperationResult.ok(operationLogList);
    }

    @PostMapping("/addInsuranceEmp")
    @ApiOperation("添加参保人员")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_INSURANCE_SCHEME, behavior = BehaviorEnum.ADD_INSURANCE_EMP)
    public Result addInsuranceEmp(@RequestBody AddInsuranceEmpBO addInsuranceEmpBO) {
        List<OperationLog> operationLogList = monthEmpRecordService.addInsuranceEmp(addInsuranceEmpBO);
        return OperationResult.ok(operationLogList);
    }

    @PostMapping("/queryNoInsuranceEmp/{iRecordId}")
    @ApiOperation("查询本月没有参保人员")
    public Result<List<SimpleHrmEmployeeVO>> queryNoInsuranceEmp(@PathVariable("iRecordId") Long iRecordId) {
        List<SimpleHrmEmployeeVO> employeeVOS = monthEmpRecordService.queryNoInsuranceEmp(iRecordId);
        return Result.ok(employeeVOS);
    }

    @PostMapping("/myInsurance")
    @ApiOperation("我的社保")
    public Result<BasePage<HrmInsuranceMonthEmpRecord>> myInsurancePageList(@RequestBody QueryInsuranceRecordListBO recordListBO) {
        BasePage<HrmInsuranceMonthEmpRecord> page = monthEmpRecordService.myInsurancePageList(recordListBO);
        return Result.ok(page);
    }

    @PostMapping("/myInsuranceDetail/{iempRecordId}")
    @ApiOperation("我的社保详情")
    public Result<EmpInsuranceByIdVO> myInsuranceDetail(@PathVariable("iempRecordId") String iempRecordId) {
        EmpInsuranceByIdVO empInsuranceByIdVO = monthEmpRecordService.queryById(iempRecordId);
        return Result.ok(empInsuranceByIdVO);
    }

}

