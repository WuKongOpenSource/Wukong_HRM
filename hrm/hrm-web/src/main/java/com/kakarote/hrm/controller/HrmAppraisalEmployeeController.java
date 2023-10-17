package com.kakarote.hrm.controller;


import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.constant.appraisal.StatusEnum;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeStage;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工绩效考核-考核绩效基本信息 前端控制器
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Api(tags = "员工端-考核计划相关接口")
@RestController
@RequestMapping("/hrmAppraisalEmployee")
public class HrmAppraisalEmployeeController {

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @PostMapping("/queryEmployeeAppraisalPageList")
    @ApiOperation("查询员工考核绩效列表")
    public Result<BasePage<Map<String, Object>>> queryPageList(@RequestBody HrmSearchBO search) {
        search.setPageType(1);
        BasePage<Map<String, Object>> mapBasePage = appraisalEmployeeService.queryEmployeeAppraisalPageList(search);
        return Result.ok(mapBasePage);
    }

    @ApiOperation("查询员工考核绩效列表")
    @PostMapping("/queryAppraisalList")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryAppraisalList(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = appraisalEmployeeService.queryAppraisalList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("查询员工考核绩效基本信息")
    @PostMapping("/queryEmployeeAppraisalBaseInfo")
    public Result<EmployeeAppraisalPlanVO> queryEmployeeAppraisalBaseInfo(@RequestBody CommonQueryBO commonQueryBO) {
        EmployeeAppraisalPlanVO appraisalPlanBaseInfo = appraisalEmployeeService.queryEmployeeAppraisalBaseInfo(commonQueryBO);
        return Result.ok(appraisalPlanBaseInfo);
    }

    @ApiOperation("查询待处理和已处理绩效列表")
    @PostMapping("/queryStageAppraisalList")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryStageAppraisalList(@RequestBody QueryEmployeePendingAppraisalBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = appraisalEmployeeService.queryStageAppraisalList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("查询当前员工待处理阶段数据")
    @PostMapping("/queryPendingStageNum")
    public Result<JSONObject> queryPendingStageNum() {
        JSONObject num = appraisalEmployeeService.queryPendingStageNum();
        return Result.ok(num);
    }

    @ApiOperation("查询考核绩效考核人员阶段数据")
    @PostMapping("/queryStageEmployeeNum")
    public Result<JSONObject> queryStageEmployeeNum(@RequestBody CommonQueryBO commonQueryBO) {
        JSONObject num = appraisalEmployeeService.queryStageEmployeeNum(commonQueryBO);
        return Result.ok(num);
    }

    @ApiOperation("查询考核绩效考核人员分数数据")
    @PostMapping("/queryScoreEmployeeNum")
    public Result<JSONObject> queryScoreEmployeeNum(@RequestBody CommonQueryBO commonQueryBO) {
        JSONObject num = appraisalEmployeeService.queryScoreEmployeeNum(commonQueryBO);
        return Result.ok(num);
    }

    @ApiOperation("填写指标")
    @PostMapping("/fillInQuota")
    public Result fillInQuota(@RequestBody FillInQuotaBO fillInQuotaBO) {
        appraisalEmployeeService.fillInQuota(fillInQuotaBO);
        return Result.ok();
    }

    @ApiOperation("查看员工考核计划指标详情")
    @PostMapping("/quotaInformation")
    public Result<AppraisalEmployeeInfoVO> quotaInformation(@RequestBody QuotaInfoQueryBO quotaInfoQueryBO) {
        AppraisalEmployeeInfoVO appraisalEmployeeInfoVO = appraisalEmployeeService.quotaInformation(quotaInfoQueryBO);
        return Result.ok(appraisalEmployeeInfoVO);
    }

    @ApiOperation("目标确认通过")
    @PostMapping("/targetConfirmationPass")
    public Result targetConfirmationPass(@RequestParam Long appraisalEmployeeId) {
        appraisalEmployeeService.targetConfirmationPass(appraisalEmployeeId);
        return Result.ok();
    }

    @ApiOperation("目标确认驳回")
    @PostMapping("/targetConfirmationReject")
    public Result targetConfirmationReject(@RequestBody RejectScoreBO rejectScoreBO) {
        appraisalEmployeeService.targetConfirmationReject(rejectScoreBO);
        return Result.ok();
    }

    @ApiOperation("指标评分")
    @PostMapping("/quotaScore")
    public Result quotaScore(@RequestBody QuotaScoreBO quotaScoreBO) {
        appraisalEmployeeService.quotaScore(quotaScoreBO);
        return Result.ok();
    }

    @ApiOperation("指标评分预计算")
    @PostMapping("/preCalculationQuotaScore")
    public Result<PreQuotaScoreVO> preCalculationQuotaScore(@RequestBody QuotaScoreBO quotaScoreBO) {
        PreQuotaScoreVO preQuotaScoreVO = appraisalEmployeeService.preCalculationQuotaScore(quotaScoreBO);
        return Result.ok(preQuotaScoreVO);
    }

    @ApiOperation("评分驳回")
    @PostMapping("/rejectScore")
    public Result rejectScore(@RequestBody RejectScoreBO rejectScoreBO) {
        appraisalEmployeeService.rejectScore(rejectScoreBO);
        return Result.ok();
    }

    @ApiOperation("查看评分详情")
    @PostMapping("/scoreInfo")
    public Result<QuotaScoreVO> scoreInfo(@RequestParam Long appraisalEmployeeId) {
        QuotaScoreVO quotaScoreVo = appraisalEmployeeService.scoreInfo(appraisalEmployeeId);
        return Result.ok(quotaScoreVo);
    }

    @ApiOperation("结果审核通过")
    @PostMapping("/resultAuditPass")
    public Result resultAuditPass(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultAudit(quotaAuditBO, StatusEnum.NORMAL);
        return Result.ok();
    }

    @ApiOperation("结果审核驳回")
    @PostMapping("/resultAuditReject")
    public Result resultAuditReject(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultAudit(quotaAuditBO, StatusEnum.ABNORMAL);
        return Result.ok();
    }

    @ApiOperation("结果确认通过")
    @PostMapping("/resultConfirmationPass")
    public Result resultConfirmationPass(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultConfirmation(quotaAuditBO, StatusEnum.NORMAL);
        return Result.ok();
    }

    @ApiOperation("结果申诉")
    @PostMapping("/resultAppeal")
    public Result resultAppeal(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultAppeal(quotaAuditBO);
        return Result.ok();
    }

    @ApiOperation("申诉通过")
    @PostMapping("/resultAppealPass")
    public Result resultAppealPass(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultAppealPass(quotaAuditBO, false);
        return Result.ok();
    }

    @ApiOperation("申诉驳回")
    @PostMapping("/resultAppealReject")
    public Result resultAppealReject(@RequestBody ResultAuditBO quotaAuditBO) {
        appraisalEmployeeService.resultAppealReject(quotaAuditBO, false);
        return Result.ok();
    }

    @ApiOperation("查询用户考核计划阶段列表")
    @PostMapping("/queryStageList")
    public Result resultAuditReject(@RequestBody CommonQueryBO commonQueryBO) {
        List<HrmAppraisalEmployeeStage> stageList = appraisalEmployeeService.queryStageList(commonQueryBO.getId());
        return Result.ok(stageList);
    }

    @ApiOperation("绩效档案查询接口")
    @PostMapping("/queryAppraisalArchives")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryAppraisalArchives(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = appraisalEmployeeService.queryAppraisalArchives(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("查询考核计划评分阶段节点")
    @PostMapping("/queryScoringPoint")
    public Result<List<ScoringStageInfoVO>> queryScoringPoint(@RequestBody CommonQueryBO commonQueryBO) {
        List<ScoringStageInfoVO> appraisalPlanList = appraisalEmployeeService.queryScoringPoint(commonQueryBO.getId());
        return Result.ok(appraisalPlanList);
    }

    @ApiOperation("处理逾期待审核节点")
    @PostMapping("/dealWithOverdueStage")
    public Result dealWithOverdueStage() {
        appraisalEmployeeService.dealWithOverdueUnAudit();
        return Result.ok();
    }

    @ApiOperation("查询员工绩效流程记录")
    @RequestMapping("/queryRecordList")
    public Result queryRecordList(@RequestBody QueryRecordListBO queryRecordListBO) {
        List<QueryAppraisalRecordListVO> recordListVOS = appraisalEmployeeService.queryRecordList(queryRecordListBO);
        return Result.ok(recordListVOS);
    }

}

