package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmRecruitCandidate;
import com.kakarote.hrm.entity.VO.CandidatePageListVO;
import com.kakarote.hrm.service.IHrmRecruitCandidateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 招聘候选人表 前端控制器
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/hrmRecruitCandidate")
@Api(tags = "招聘管理-候选人接口")
public class HrmRecruitCandidateController {

    @Autowired
    private IHrmRecruitCandidateService recruitCandidateService;


    @PostMapping("/queryPageList")
    @ApiOperation("查询候选人列表页")
    public Result<BasePage<CandidatePageListVO>> queryCandidatePageList(@RequestBody QueryCandidatePageListBO queryCandidatePageListBO) {
        BasePage<CandidatePageListVO> page = recruitCandidateService.queryCandidateList(queryCandidatePageListBO);
        return Result.ok(page);
    }


    @PostMapping("/queryById/{candidateId}")
    @ApiOperation("查询基本信息")
    public Result<CandidatePageListVO> queryById(@PathVariable("candidateId") String candidateId) {
        CandidatePageListVO candidate = recruitCandidateService.queryById(candidateId);
        return Result.ok(candidate);
    }


    @PostMapping("/addCandidate")
    @ApiOperation("新建候选人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.SAVE)
    public Result addCandidate(@Validated @RequestBody HrmRecruitCandidate hrmRecruitCandidate) {
        OperationLog operationLog = recruitCandidateService.addCandidate(hrmRecruitCandidate);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setCandidate")
    @ApiOperation("编辑候选人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.UPDATE)
    public Result setCandidate(@Validated @RequestBody HrmRecruitCandidate hrmRecruitCandidate) {
        OperationLog operationLog = recruitCandidateService.setCandidate(hrmRecruitCandidate);
        return OperationResult.ok(operationLog);
    }

    /**
     * 删除候选人
     */
    @PostMapping("/deleteById/{candidateId}")
    @ApiOperation("删除候选人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.DELETE)
    public Result deleteById(@PathVariable("candidateId") Long candidateId) {
        OperationLog operationLog = recruitCandidateService.deleteById(candidateId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("删除候选人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.DELETE)
    public Result deleteByIds(@RequestBody List<Long> candidateIds) {
        List<OperationLog> operationLogs = recruitCandidateService.deleteByIds(candidateIds);
        return OperationResult.ok(operationLogs);
    }


    @PostMapping("/updateCandidateStatus")
    @ApiOperation("批量修改候选人状态")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.UPDATE_STATUS)
    public Result updateCandidateStatus(@RequestBody UpdateCandidateStatusBO updateCandidateStatusBO) {
        List<OperationLog> operationLogs = recruitCandidateService.updateCandidateStatus(updateCandidateStatusBO);
        return OperationResult.ok(operationLogs);
    }

    @PostMapping("/updateCandidatePost")
    @ApiOperation("批量修改候选人职位")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.UPDATE_RECRUIT_POST)
    public Result updateCandidatePost(@RequestBody UpdateCandidatePostBO updateCandidatePostBO) {
        List<OperationLog> operationLogs = recruitCandidateService.updateCandidatePost(updateCandidatePostBO);
        return OperationResult.ok(operationLogs);
    }

    @PostMapping("/updateCandidateRecruitChannel")
    @ApiOperation("批量修改候选人招聘渠道")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.UPDATE_RECRUIT_CHANNEL)
    public Result updateCandidateRecruitChannel(@RequestBody UpdateCandidateRecruitChannelBO updateCandidateRecruitChannelBO) {
        List<OperationLog> operationLogs = recruitCandidateService.updateCandidateRecruitChannel(updateCandidateRecruitChannelBO);
        return OperationResult.ok(operationLogs);
    }

    @PostMapping("/eliminateCandidate")
    @ApiOperation("淘汰/流失候选人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_CANDIDATE, behavior = BehaviorEnum.UPDATE_STATUS)
    public Result eliminateCandidate(@RequestBody EliminateCandidateBO eliminateCandidateBO) {
        List<OperationLog> operationLogs = recruitCandidateService.eliminateCandidate(eliminateCandidateBO);
        return OperationResult.ok(operationLogs);
    }


    @PostMapping("/queryCleanCandidateIds")
    @ApiOperation("查询一键清理候选人,查询完之后调用修改状态接口")
    public Result<List<Long>> queryCleanCandidateIds(@RequestBody QueryCleanCandidateBO queryCleanCandidateBO) {
        List<Long> candidateIds = recruitCandidateService.queryCleanCandidateIds(queryCleanCandidateBO);
        return Result.ok(candidateIds);
    }

    @PostMapping("/queryFile/{candidateId}")
    @ApiOperation("查询候选人附件")
    public Result<List<FileEntity>> queryFile(@PathVariable("candidateId") Long candidateId) {
        return recruitCandidateService.queryFile(candidateId);
    }

    @PostMapping("/queryCandidateStatusNum")
    @ApiOperation("查询每个候选人状态的数量")
    public Result<Map<Integer, Integer>> queryCandidateStatusNum() {
        Map<Integer, Integer> statusMap = recruitCandidateService.queryCandidateStatusNum();
        return Result.ok(statusMap);
    }


}

