package com.kakarote.hrm.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.constant.appraisal.AppraisalStatusEnum;
import com.kakarote.hrm.constant.appraisal.StatusEnum;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployee;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeStage;
import com.kakarote.hrm.entity.VO.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工绩效考核-考核绩效基本信息 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
public interface IHrmAppraisalEmployeeService extends BaseService<HrmAppraisalEmployee> {

    /**
     * 根据员工id删除已归档数据
     *
     * @param employeeIdList
     */
    void deleteAppraisalEmployeeByEmployeeId(List<Long> employeeIdList);

    /**
     * 查询员工考核计划列表
     *
     * @param queryEmployeeQuotaBO
     * @return
     */
    BasePage<EmployeeAppraisalPlanVO> queryAppraisalList(QueryEmployeeQuotaBO queryEmployeeQuotaBO);

    /**
     * 员工端-查询我的绩效档案列表
     *
     * @param queryEmployeeQuotaBO
     * @return
     */
    BasePage<EmployeeAppraisalPlanVO> queryMyAppraisalFileList(QueryEmployeeQuotaBO queryEmployeeQuotaBO);

    /**
     * 根据考核计划id删除所有员工端相关数据
     *
     * @param appraisalPlanId
     */
    void delEmployeeAppraisalData(Long appraisalPlanId);

    /**
     * 根据考核员工id删除已归档数据
     *
     * @param appraisalEmployeeIdList
     */
    Integer deleteAppraisalEmployeeById(List<Long> appraisalEmployeeIdList);

    /**
     * 查询考核计划下的考核员工列表
     *
     * @param search
     * @return
     */
    BasePage<Map<String, Object>> queryEmployeeAppraisalPageList(HrmSearchBO search);

    /**
     * 查看员工当前绩效基本信息
     *
     * @param commonQueryBO
     * @return
     */
    EmployeeAppraisalPlanVO queryEmployeeAppraisalBaseInfo(@RequestBody CommonQueryBO commonQueryBO);

    /**
     * 查询员工待处理绩效列表
     *
     * @return
     */
    BasePage<EmployeeAppraisalPlanVO> queryStageAppraisalList(QueryEmployeePendingAppraisalBO queryEmployeePendingAppraisalBO);

    //查询员工考核列表各阶段人员数量
    JSONObject queryStageEmployeeNum(CommonQueryBO commonQueryBO);

    /**
     * 查询当前员工待处理阶段数量数据
     *
     * @return
     */
    JSONObject queryPendingStageNum();

    /**
     * 查询绩考核人考核结果人数
     *
     * @param commonQueryBO
     * @return
     */
    JSONObject queryScoreEmployeeNum(CommonQueryBO commonQueryBO);

    /**
     * 指标填写
     *
     * @param fillInQuotaBO
     */
    void fillInQuota(FillInQuotaBO fillInQuotaBO);

    /**
     * 查询绩效考核详情
     *
     * @param quotaInfoQueryBO
     * @return
     */
    AppraisalEmployeeInfoVO quotaInformation(QuotaInfoQueryBO quotaInfoQueryBO);

    /**
     * 目标确认通过
     *
     * @param appraisalEmployeeId
     */
    void targetConfirmationPass(Long appraisalEmployeeId);


    /**
     * 目标确认驳回
     *
     * @param rejectScoreBO
     */
    void targetConfirmationReject(RejectScoreBO rejectScoreBO);


    /**
     * 指标评分
     *
     * @param quotaScoreBO
     */
    void quotaScore(QuotaScoreBO quotaScoreBO);


    /**
     * 预计算指标评分
     *
     * @param quotaScoreBO
     */
    PreQuotaScoreVO preCalculationQuotaScore(QuotaScoreBO quotaScoreBO);

    /**
     * 评分驳回
     */
    void rejectScore(RejectScoreBO rejectScoreBO);

    /**
     * 查看员工考核-评分详情
     *
     * @param appraisalEmployeeId
     */
    QuotaScoreVO scoreInfo(Long appraisalEmployeeId);


    /**
     * 结果审核
     *
     * @param resultAuditBO
     */
    void resultAudit(ResultAuditBO resultAuditBO, StatusEnum statusEnum);

    /**
     * 结果确认
     *
     * @param resultAuditBO
     */
    void resultConfirmation(ResultAuditBO resultAuditBO, StatusEnum statusEnum);

    /**
     * 查询员工考核计划阶段列表
     *
     * @param appraisalEmployeeId
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryStageList(Long appraisalEmployeeId);

    /**
     * 根据员工考核id查询考核计划流程id
     *
     * @param appraisalEmployeeId
     * @return
     */
    Long queryAppraisalPlanProcessId(Long appraisalEmployeeId);


    /**
     * 结果申诉
     *
     * @param quotaAuditBO
     */
    void resultAppeal(ResultAuditBO quotaAuditBO);

    /**
     * //申诉确认通过
     */
    void resultAppealPass(ResultAuditBO quotaAuditBO, Boolean isJob);

    /**
     * //申诉确认驳回
     *
     * @param quotaAuditBO
     */
    void resultAppealReject(ResultAuditBO quotaAuditBO, Boolean isJob);


    BasePage<EmployeeAppraisalPlanVO> queryAppraisalArchives(QueryEmployeeQuotaBO queryEmployeeQuotaBO);


    /**
     * @param appraisalEmployeeId
     * @return
     */
    List<ScoringStageInfoVO> queryScoringPoint(Long appraisalEmployeeId);


    /**
     * 处理超期未处理的记录
     */
    void dealWithOverdueUnAudit();

    /**
     * 查询操作记录列表
     *
     * @param queryRecordListBO
     * @return
     */
    List<QueryAppraisalRecordListVO> queryRecordList(QueryRecordListBO queryRecordListBO);


    /**
     * 批量保存员工数据
     *
     * @param hrmAppraisalEmployeeList
     */
    void saveAppraisalEmployeeList(List<HrmAppraisalEmployee> hrmAppraisalEmployeeList);


    /**
     * 更新全部员工考核计划状态-根据sort进行更新 -- 该方法用于sort对应的类型是一定相同的情况
     */
    void updateAllEmployeeStageForStart(Long appraisalPlanId, Integer stageSort, Integer status);


    /**
     * 更新指定员工考核计划状态-重载
     */
    void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort);

    /**
     * 更新指定员工考核计划状态-重载
     */
    void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status);

    /**
     * 更新指定员工考核计划状态-重载
     */
    void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status, Map<String, Object> supplementField);

    /**
     * 更新指定员工考核计划状态
     */
    void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status, Map<String, Object> supplementField, Boolean needCleanScore);


    /**
     * 更新全部员工绩效考核单的状态，不含阶段状态
     */
    void updateAllEmployeeStatus(Long appraisalPlanId, AppraisalStatusEnum appraisalStatusEnum);

    /**
     * 查询当次考核计划所有考核员工
     *
     * @param appraisalPlanId
     * @return
     */
    List<HrmAppraisalEmployee> queryEmployeeListByPlanId(Long appraisalPlanId);


    /**
     * 查询员工最近一次考核记录
     *
     * @param employeeId
     * @return
     */
    HrmAppraisalEmployee queryRecentlyAppraisalEmployee(Long employeeId);

}
