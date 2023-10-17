package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryCoefficientBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeAchievementFileBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeQuotaBO;
import com.kakarote.hrm.entity.BO.QuotaInfoQueryBO;
import com.kakarote.hrm.entity.DTO.EmployeeAchievementFileReq;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.VO.AppraisalEmployeeInfoVO;
import com.kakarote.hrm.entity.VO.EmployeeAchievementFileVO;
import com.kakarote.hrm.entity.VO.EmployeeAppraisalPlanVO;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import com.kakarote.hrm.service.IHrmEmployeeAchievementFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 员工绩效档案 前端控制器
 * </p>
 *
 * @author zyl
 * @since 2022-06-14
 */
@RestController
@Api(tags = "员工绩效档案")
@RequestMapping("/hrmEmployeeAchievementFile")
public class HrmEmployeeAchievementFileController {

    @Autowired
    private IHrmEmployeeAchievementFileService employeeAchievementFileService;

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @ApiOperation("查询绩效档案列表")
    @PostMapping("/queryAppraisalFileList")
    public Result<BasePage<EmployeeAchievementFileVO>> queryAppraisalFileList(@RequestBody QueryEmployeeAchievementFileBO employeeAchievementFileBO) {
        BasePage<EmployeeAchievementFileVO> appraisalPlanList = employeeAchievementFileService.queryEmployeeAchievementFileList(employeeAchievementFileBO);
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("删除员工绩效档案考核记录")
    @PostMapping("/delAppraisalFileRecordList")
    public Result delAppraisalFileRecordList(@RequestBody EmployeeAchievementFileReq operationReq) {
        employeeAchievementFileService.delAppraisalFileRecordList(operationReq);
        return Result.ok();
    }

    @ApiOperation("删除员工绩效档案所有考核记录")
    @PostMapping("/delAppraisalFileRecordListOfAll")
    public Result delAppraisalFileRecordListOfAll(@RequestBody OperationReq operationReq) {
        employeeAchievementFileService.delAppraisalFileRecordListOfAll(operationReq);
        return Result.ok();
    }

    @ApiOperation("查询员工考核绩效列表")
    @PostMapping("/queryEmployeeAppraisalList")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryEmployeeAppraisalList(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = appraisalEmployeeService.queryAppraisalList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("查看员工考核计划详情")
    @PostMapping("/queryAppraisalInformation")
    public Result<AppraisalEmployeeInfoVO> queryAppraisalInformation(@RequestBody QuotaInfoQueryBO quotaInfoQueryBO) {
        AppraisalEmployeeInfoVO appraisalEmployeeInfoVO = appraisalEmployeeService.quotaInformation(quotaInfoQueryBO);
        return Result.ok(appraisalEmployeeInfoVO);
    }

    @ApiOperation("员工端-查询我的考核绩效档案列表")
    @PostMapping("/queryMyAppraisalFileList")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryMyAppraisalFileList(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = appraisalEmployeeService.queryMyAppraisalFileList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }


    @ApiOperation("薪资核算-查询员工考核结果系数")
    @PostMapping("/queryCoefficient")
    public Result<Map<Long, Double>> queryCoefficient(@RequestBody QueryCoefficientBO queryCoefficientBO) {
        Map<Long, Double> employeeCoefficient = employeeAchievementFileService.queryCoefficient(queryCoefficientBO);
        return Result.ok(employeeCoefficient);
    }

}

