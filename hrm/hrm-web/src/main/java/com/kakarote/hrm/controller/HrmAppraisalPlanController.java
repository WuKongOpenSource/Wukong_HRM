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
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.service.IHrmAppraisalPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考核计划基础信息表 前端控制器
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Api(tags = "考核计划相关接口")
@RestController
@RequestMapping("/hrmAppraisalPlan")
public class HrmAppraisalPlanController {

    @Autowired
    IHrmAppraisalPlanService hrmAppraisalPlanService;


    @ApiOperation("查看考核计划列表")
    @PostMapping("/queryPageList")
    public Result<BasePage<AppraisalPlanVO>> queryPageList(@RequestBody QueryAppraisalPlanBO queryAppraisalPlanBO) {
        BasePage<AppraisalPlanVO> planList = hrmAppraisalPlanService.queryPageList(queryAppraisalPlanBO);
        return Result.ok(planList);
    }

    @PostMapping("/queryField")
    @ApiOperation("查询场景搜索字段")
    public Result<List<HrmModelFiledVO>> queryField(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        List<HrmModelFiledVO> filedVOS = hrmAppraisalPlanService.queryField(appraisalPlanId);
        return Result.ok(filedVOS);
    }

    @ApiOperation("查询员工考核绩效列表")
    @PostMapping("/queryEmployeeAppraisalList")
    public Result<BasePage<AppraisalPlanOfEmployeeVO>> queryEmployeeAppraisalList(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<AppraisalPlanOfEmployeeVO> appraisalPlanList = hrmAppraisalPlanService.queryEmployeeAppraisalList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }


    @PostMapping("/queryAppraisalResultPageList")
    @ApiOperation("考核结果列表接口")
    public Result<BasePage<Map<String, Object>>> queryAppraisalResultPageList(@RequestBody HrmSearchBO search) {
        search.setPageType(1);
        BasePage<Map<String, Object>> mapBasePage = hrmAppraisalPlanService.queryAppraisalResultPageList(search);
        return Result.ok(mapBasePage);
    }


    @ApiOperation("保存考核计划")
    @PostMapping("/addAppraisalPlan")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI)
    public Result<Long> addAppraisalPlan(@RequestBody AppraisalPlanBO appraisalPlanBO) {
        OperationLog operationLog = hrmAppraisalPlanService.addOrUpdate(appraisalPlanBO);
        JSONObject operationObject = (JSONObject) operationLog.getOperationObject();
        return OperationResult.ok(operationObject.getLong("typeId"), ListUtil.toList(operationLog));
    }

    @ApiOperation("查看考核设置")
    @PostMapping("/querySetting")
    public Result<AppraisalPlanSettingInfoVO> querySetting(@RequestBody AppraisalPlanReq appraisalPlanReq) {
        AppraisalPlanSettingInfoVO planSettingInfo = hrmAppraisalPlanService.querySetting(appraisalPlanReq.getAppraisalPlanId());
        return Result.ok(planSettingInfo);
    }

    @ApiOperation("删除考核计划")
    @PostMapping("/delAppraisalPlan")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.DELETE)
    public Result<Long> addAppraisalPlan(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.delAppraisalPlan(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }


    @ApiOperation("删除已归档的考核计划")
    @PostMapping("/delAppraisalPlanOfFile")
    public Result<Long> delAppraisalPlanOfFile(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.delAppraisalPlanOfFile(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }


    @ApiOperation("查询考核人员列表-列表头")
    @PostMapping("/queryEmployeeListHeadInfo")
    public Result queryEmployeeListHeadInfo(@RequestBody CommonQueryBO commonQueryBO) {
        List<HeadFieldVO> headFieldVOList = hrmAppraisalPlanService.queryEmployeeListHeadInfo(commonQueryBO.getId());
        return Result.ok(headFieldVOList);
    }

    @ApiOperation("查看考核人员列表")
    @PostMapping("/queryEmployeeList")
    public Result<BasePage<JSONObject>> queryEmployeeList(@RequestBody QueryAppraisalEmployeeBO queryAppraisalEmployeeBO) {
        BasePage<JSONObject> employeeList = hrmAppraisalPlanService.queryEmployee(queryAppraisalEmployeeBO);
        return Result.ok(employeeList);
    }

    @PostMapping("/queryAppraisalStatusNum")
    @ApiOperation("查询每个绩效状态的数量")
    public Result<Map<Integer, Long>> queryAppraisalStatusNum() {
        Map<Integer, Long> map = hrmAppraisalPlanService.queryAppraisalStatusNum();
        return Result.ok(map);
    }

    @ApiOperation("添加考核人员")
    @PostMapping("/addAppraisalEmployeeList")
    public Result<BasePage<AppraisalEmployeeVO>> addAppraisalEmployeeList(@RequestBody AppraisalEmployeeSaveBO appraisalEmployeeSaveBO) {
        hrmAppraisalPlanService.addAppraisalEmployee(appraisalEmployeeSaveBO);
        return Result.ok();
    }

    @ApiOperation("移除考核人员")
    @PostMapping("/delAppraisalEmployee")
    public Result<BasePage<AppraisalEmployeeVO>> delAppraisalEmployee(@RequestBody AppraisalEmployeeSaveBO appraisalEmployeeSaveBO) {
        hrmAppraisalPlanService.delAppraisalEmployee(appraisalEmployeeSaveBO);
        return Result.ok();
    }

    @ApiOperation("检查考核计划")
    @PostMapping("/checkAppraisalPlan")
    public Result checkAppraisalPlan(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        hrmAppraisalPlanService.checkAppraisalPlan(appraisalPlanId);
        return Result.ok();
    }

    @ApiOperation("启动考核计划")
    @PostMapping("/startAppraisalPlan")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.OPEN_APPRAISAL)
    public Result<BasePage<AppraisalEmployeeVO>> startAppraisalPlan(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.startAppraisalPlan(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("开启评分")
    @PostMapping("/openScoring")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.OPEN_SCORING)
    public Result<BasePage<AppraisalEmployeeVO>> openScoring(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.openScoring(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("绩效面谈")
    @PostMapping("/toInterview")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.START_INTERVIEW)
    public Result toInterview(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.toInterview(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("绩效归档")
    @PostMapping("/placeOnFile")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.ARCHIVE)
    public Result placeOnFile(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.placeOnFile(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("终止考核计划")
    @PostMapping("/terminationPlan")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_KPI, behavior = BehaviorEnum.TERMINATION_APPRAISAL)
    public Result terminationPlan(@RequestParam("appraisalPlanId") Long appraisalPlanId) {
        OperationLog operationLog = hrmAppraisalPlanService.terminationPlan(appraisalPlanId);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("查询考核计划结果设置等级数量列表")
    @PostMapping("/queryAppraisalPlanResultLevelNum")
    public Result<List<HrmAppraisalPlanLevelNumVO>> queryAppraisalPlanResultLevelNum(@RequestBody CommonQueryBO commonQueryBO) {
        List<HrmAppraisalPlanLevelNumVO> levelList = hrmAppraisalPlanService.queryAppraisalPlanResultLevelNum(commonQueryBO.getId());
        return Result.ok(levelList);
    }

    @ApiOperation("查询考核计划各等级对应员工绩效列表")
    @PostMapping("/queryEmployeeAppraisalLevelList")
    public Result<BasePage<EmployeeAppraisalPlanVO>> queryEmployeeAppraisalLevelList(@RequestBody QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<EmployeeAppraisalPlanVO> appraisalPlanList = hrmAppraisalPlanService.queryEmployeeAppraisalLevelList(queryEmployeeQuotaBO);
        return Result.ok(appraisalPlanList);
    }

}

