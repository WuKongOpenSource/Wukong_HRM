package com.kakarote.core.feign.examine.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.AdminCompany;
import com.kakarote.core.feign.admin.entity.ExamineEmailBO;
import com.kakarote.core.feign.examine.entity.*;
import com.kakarote.core.feign.oa.entity.ExamineVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author JiaS
 * @date 2020/12/17
 */
@FeignClient(name = "examine", contextId = "record")
public interface ExamineService {

    /**
     * 新增审核记录
     *
     * @param examineRecordSaveBO:新增审核记录
     * @return 新增审核记录返回VO
     */
    @PostMapping("/examineRecord/addExamineRecord")
    public Result<ExamineRecordReturnVO> addExamineRecord(@RequestBody ExamineRecordSaveBO examineRecordSaveBO);

    /**
     * 查询正常审批流程
     *
     * @param label
     * @return 审批表
     */
    @PostMapping("/examines/queryNormalExamine")
    public Result<List<ExamineInfoVo>> queryNormalExamine(@RequestParam("label") Integer label);

    /**
     * 查询正常审批流程
     *
     * @return 审批表
     */
    @PostMapping("/examines/queryAllNormalExamine")
    public Result<List<ExamineInfoVo>> queryAllNormalExamine();
    /**
     * 查询审批日志根据id
     *
     * @param examineLogId:审批日志id
     * @return 审批日志
     */
    @PostMapping("/examineRecord/queryExamineLogById")
    public Result<ExamineRecordLog> queryExamineLogById(@RequestParam("examineLogId") Integer examineLogId);

    /**
     * 查询审批记录信息
     *
     * @param recordId:记录id
     * @return 新增审核记录返回VO
     */
    @PostMapping("/examineRecord/queryExamineRecordInfo")
    public Result<ExamineRecordReturnVO> queryExamineRecordInfo(@RequestParam("recordId") Long recordId);

    /**
     * 查询审批根据审批id
     *
     * @param examineId:审批id
     * @return 审批表
     */
    @PostMapping("/examines/queryExamineById")
    public Result<ExamineInfoVo> queryExamineById(@RequestParam("examineId") Long examineId);

    /**
     * 删除记录根据记录id
     *
     * @param recordId:记录id
     * @return
     */
    @PostMapping("/examineRecord/deleteExamineRecord")
    public Result<Boolean> deleteExamineRecord(@RequestParam("recordId") Long recordId);

    /**
     * 修改审批状态
     *
     * @param recordId:记录id
     * @param examineStatus:审批状态
     * @return
     */
    @PostMapping("/examineRecord/updateExamineRecord")
    public Result<Boolean> updateExamineRecord(@RequestParam("recordId") Long recordId, @RequestParam("examineStatus") Integer examineStatus);

    /**
     * 查询oa模块审批idlist
     *
     * @param status:状态
     * @param categoryId:种类id
     * @return
     */
    @PostMapping("/examineWaiting/queryOaExamineIdList")
    public Result<List<Long>> queryOaExamineIdList(@RequestParam("status") Integer status, @RequestParam("categoryId") Long categoryId);

    /**
     * 查询crm模块审批id
     *
     * @param label:标签
     * @param status:状态
     * @return
     */
    @PostMapping("/examineWaiting/queryCrmExamineIdList")
    public Result<List<Long>> queryCrmExamineIdList(@RequestParam("label") Integer label, @RequestParam("status") Integer status);

    /**
     * 删除审批记录和日志
     *
     * @param label:标签
     * @return
     */
    @PostMapping("/examineRecord/deleteExamineRecordAndLog")
    public Result<Boolean> deleteExamineRecordAndLog(@RequestParam("label") Integer label);

    /**
     * 保存审批数据保存BO
     *
     * @param examineSaveBO:审批数据保存BO
     * @return
     */
    @PostMapping("/examines/addExamine")
    public Result<ExamineInfoVo> addExamine(@RequestBody ExamineSaveBO examineSaveBO);

    /**
     * 查询审批流动信息
     *
     * @param examineId:审批id
     * @return
     */
    @PostMapping("/examines/queryExamineFlowInfo")
    public Result<ExaminePreviewVO> queryExamineFlowInfo(@RequestParam("examineId") Long examineId);

    /**
     * 查询记录List
     *
     * @param recordId：记录id
     * @param ownerUserId:负责人id
     * @return
     */
    @PostMapping("/examineRecord/queryExamineRecordList")
    public Result<JSONObject> queryExamineRecordList(@RequestParam("recordId") Long recordId, @RequestParam(value = "ownerUserId", required = false) Long ownerUserId);


    /**
     * 查询oa模块审批
     *
     * @param examinePageBo
     * @return
     */
    @PostMapping("/examineWaiting/queryOaExamineListWait")
    public BasePage<ExamineVO> queryOaExamineListWait(@RequestBody ExaminePageBO examinePageBo);


    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/examines/getAllFieldLanguageRel")
    public Result<List<Map<String,Object>>> getAllFieldLanguageRel();



    @PostMapping("/examineRecord/timeOutRecordHandler")
    @ApiOperation("查询超时未处理recordList")
    public Result<List<Long>> queryTimeOutRecordList(@RequestParam("userId")Long userId);


    @PostMapping("/examineSuperExamines/queryExamineCanLoopLimitTimeCompanyList")
    @ApiOperation("查询超时处理的所有公司")
    Result<List<AdminCompany>> queryExamineCanLoopLimitTimeCompanyList();

    @PostMapping("/examineSuperExamines/handleExamineUserAbnormal")
    @ApiOperation("审批账户异常处理")
    public Result handleExamineUserAbnormal();

    @PostMapping("/examineSuperExamines/set/email/audit/valid")
    @ApiOperation("审批账户异常处理")
    public Result setEmailAuditValid(@RequestBody ExamineEmailBO examineBO);

    @PostMapping("/examineSuperExamines/get/email/audit/valid")
    @ApiOperation("审批账户异常处理")
    public Result<ExamineEmailBO> getEmailAuditValid(@RequestBody ExamineEmailBO examineBO);

    @PostMapping("/examineRecord/selectAllNoHandleLimitCompany")
    @ApiOperation("查询超时未处理公司")
    public Result<List<AdminCompany>> selectAllNoHandleLimitCompany();

    @PostMapping("/examineRecord/selectAllNoHandleAbnormalCompany")
    @ApiOperation("查询审批账号为空的公司id")
    public Result<List<AdminCompany>> selectAllNoHandleAbnormalCompany();

    @PostMapping("/examines/queryExamineAdvanceConfig")
    @ApiOperation("查询审核信息高级配置")
    public Result<ExamineAdvancedConfigVO> queryExamineAdvanceConfig(@RequestParam("examineId") Long examineId);


    @PostMapping("/examineDelegate/updateDelegateStatus")
    @ApiOperation("流程委托在到达结束时间后会自动变更为“停用”状态")
    public Result updateDelegateStatus();


    @ApiOperation("查询历史审批ID")
    @PostMapping(value = "/examines/queryHistoryExamineId")
    public Result<List<Long>> queryHistoryExamineId(@RequestParam("examineId") Long examineId);

}
