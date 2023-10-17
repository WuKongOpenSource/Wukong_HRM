package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmSalarySlipOption;
import com.kakarote.hrm.entity.VO.QuerySendDetailListVO;
import com.kakarote.hrm.entity.VO.QuerySendRecordListVO;
import com.kakarote.hrm.entity.VO.SlipEmployeeVO;
import com.kakarote.hrm.service.IHrmSalarySlipRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 发工资条记录 前端控制器
 * </p>
 *
 * @author hmb
 * @since 2020-11-03
 */
@RestController
@RequestMapping("/hrmSalarySlipRecord")
@Api(tags = "工资条-工资条记录接口")
public class HrmSalarySlipRecordController {

    @Autowired
    private IHrmSalarySlipRecordService slipRecordService;

    @PostMapping("/querySlipEmployeePageList")
    @ApiOperation("查询工资条选择发送员工列表")
    public Result<BasePage<SlipEmployeeVO>> querySlipEmployeePageList(@RequestBody QuerySlipEmployeePageListBO slipEmployeePageListBO) {
        BasePage<SlipEmployeeVO> page = slipRecordService.querySlipEmployeePageList(slipEmployeePageListBO);
        return Result.ok(page);
    }

    @PostMapping("/sendSalarySlip")
    @ApiOperation("发工资条")
    @OperateLog(behavior = BehaviorEnum.SEND_SALARY_SLIP, apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_SALARY_SLIP_RECORD)
    public Result sendSalarySlip(@RequestBody SendSalarySlipBO sendSalarySlipBO) {
        OperationLog operationLog = slipRecordService.sendSalarySlip(sendSalarySlipBO);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/querySendRecordList")
    @ApiOperation("查询发放工资条记录列表")
    public Result<BasePage<QuerySendRecordListVO>> querySendRecordList(@RequestBody QuerySendRecordListBO querySendRecordListBO) {
        BasePage<QuerySendRecordListVO> page = slipRecordService.querySendRecordList(querySendRecordListBO);
        return Result.ok(page);
    }

    @PostMapping("/deleteSendRecord/{id}")
    @ApiOperation("删除发放记录")
    @OperateLog(behavior = BehaviorEnum.DELETE, apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_SALARY_SLIP_RECORD)
    public Result deleteSendRecord(@PathVariable("id") String id) {
        OperationLog operationLog = slipRecordService.deleteSendRecord(id);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/querySendDetailList")
    @ApiOperation("查询发放工资条记录详情列表")
    public Result<BasePage<QuerySendDetailListVO>> querySendDetailList(@RequestBody QuerySendDetailListBO querySendRecordListBO) {
        BasePage<QuerySendDetailListVO> page = slipRecordService.querySendDetailList(querySendRecordListBO);
        return Result.ok(page);
    }

    @PostMapping("/querySlipDetail/{id}")
    @ApiOperation("查询工资条明细")
    public Result<List<HrmSalarySlipOption>> querySlipDetail(@PathVariable("id") Long id) {
        List<HrmSalarySlipOption> list = slipRecordService.querySlipDetail(id);
        return Result.ok(list);
    }

    @PostMapping("/setSlipRemarks")
    @ApiOperation("添加修改工资条备注")
    public Result setSlipRemarks(@RequestBody SetSlipRemarksBO setSlipRemarksBO) {
        slipRecordService.setSlipRemarks(setSlipRemarksBO);
        return Result.ok();
    }

}

