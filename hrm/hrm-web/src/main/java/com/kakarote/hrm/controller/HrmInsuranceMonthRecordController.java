package com.kakarote.hrm.controller;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryInsurancePageListBO;
import com.kakarote.hrm.entity.BO.QueryInsuranceRecordListBO;
import com.kakarote.hrm.entity.VO.QueryInsurancePageListVO;
import com.kakarote.hrm.entity.VO.QueryInsuranceRecordListVO;
import com.kakarote.hrm.service.IHrmInsuranceMonthRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 每月社保记录 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmInsuranceMonthRecord")
@Api(tags = "社保管理-生成社保")
public class HrmInsuranceMonthRecordController {

    @Autowired
    private IHrmInsuranceMonthRecordService insuranceMonthRecordService;

    @PostMapping("/computeInsuranceData")
    @ApiOperation("核算社保数据")
    @OperateLog(apply = ApplyEnum.HRM, behavior = BehaviorEnum.SAVE, object = OperateObjectEnum.HRM_INSURANCE_SCHEME)
    public Result computeInsuranceData() {
        JSONObject data = insuranceMonthRecordService.computeInsuranceData();
        OperationLog operationLog = (OperationLog) data.get("operationLog");
        return OperationResult.ok(data.getString("year"), ListUtil.toList(operationLog));
    }

    @PostMapping("/queryInsuranceRecordList")
    @ApiOperation("查询社保统计数据列表")
    public Result<BasePage<QueryInsuranceRecordListVO>> queryInsuranceRecordList(@RequestBody QueryInsuranceRecordListBO recordListBO) {
        BasePage<QueryInsuranceRecordListVO> page = insuranceMonthRecordService.queryInsuranceRecordList(recordListBO);
        return Result.ok(page);
    }

    @PostMapping("/queryInsuranceRecordList/{iRecordId}")
    @ApiOperation("查询社保详情统计数据(详情统计)")
    public Result<QueryInsuranceRecordListVO> queryInsuranceRecord(@PathVariable("iRecordId") String iRecordId) {
        QueryInsuranceRecordListVO data = insuranceMonthRecordService.queryInsuranceRecord(iRecordId);
        return Result.ok(data);
    }

    @PostMapping("/queryInsurancePageList")
    @ApiOperation("查询社保数据列表")
    public Result<BasePage<QueryInsurancePageListVO>> queryInsurancePageList(@RequestBody QueryInsurancePageListBO queryInsurancePageListBO) {
        BasePage<QueryInsurancePageListVO> page = insuranceMonthRecordService.queryInsurancePageList(queryInsurancePageListBO);
        return Result.ok(page);
    }

    @PostMapping("/deleteInsurance/{iRecordId}")
    @ApiOperation("删除社保记录")
    @OperateLog(apply = ApplyEnum.HRM, behavior = BehaviorEnum.DELETE, object = OperateObjectEnum.HRM_INSURANCE_SCHEME)
    public Result deleteInsurance(@PathVariable("iRecordId") Long iRecordId) {
        OperationLog operationLog = insuranceMonthRecordService.deleteInsurance(iRecordId);
        return OperationResult.ok(operationLog);
    }

}

