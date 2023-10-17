package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.SetInterviewResultBO;
import com.kakarote.hrm.entity.BO.SetRecruitInterviewBO;
import com.kakarote.hrm.service.IHrmRecruitInterviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 面试表 前端控制器
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/hrmRecruitInterview")
@Api(tags = "招聘管理-面试管理")
public class HrmRecruitInterviewController {

    @Autowired
    private IHrmRecruitInterviewService recruitInterviewService;

    /**
     * 新建或编辑面试(安排面试)
     */
    @PostMapping("/addInterview")
    @ApiOperation("安排面试")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.ARRANGE_INTERVIEW)
    public Result addInterview(@RequestBody SetRecruitInterviewBO setRecruitInterviewBO) {
        OperationLog operationLog = recruitInterviewService.setInterview(setRecruitInterviewBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/setInterviewResult")
    @ApiOperation("填写面试结果")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE)
    public Result setInterviewResult(@RequestBody SetInterviewResultBO setInterviewResultBO) {
        OperationLog operationLog = recruitInterviewService.setInterviewResult(setInterviewResultBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/addBatchInterview")
    @ApiOperation("批量安排面试")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.ARRANGE_INTERVIEW)
    public Result addBatchInterview(@RequestBody SetRecruitInterviewBO setRecruitInterviewBO) {
        List<OperationLog> operationLogList = recruitInterviewService.addBatchInterview(setRecruitInterviewBO);
        return OperationResult.ok(operationLogList);
    }
}

