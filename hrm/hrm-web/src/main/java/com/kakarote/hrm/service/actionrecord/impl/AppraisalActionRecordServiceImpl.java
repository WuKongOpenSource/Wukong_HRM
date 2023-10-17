package com.kakarote.hrm.service.actionrecord.impl;

import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.HrmActionTypeEnum;
import com.kakarote.hrm.constant.HrmLanguageEnum;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("appraisalActionRecordService")
public class AppraisalActionRecordServiceImpl extends AbstractHrmActionRecordService {

    private static HrmActionTypeEnum actionTypeEnum = HrmActionTypeEnum.KPI_APPRAISAL;
    private static HrmActionBehaviorEnum behaviorEnum = HrmActionBehaviorEnum.UPDATE;


    /**
     * 提交考核
     */

    public Content submitAppraisalRecord(Long employeeAppraisalId) {
        String content = "填写并提交了";
        String transContent = HrmLanguageEnum.ASSESSMENT_COMMIT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 确认目标
     */
    public Content confirmAppraisalRecord(Long employeeAppraisalId) {
        String content = "确认了目标";
        String transContent = HrmLanguageEnum.TARGET_CONFIRM.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 确认驳回
     */
    public Content rejectConfirmAppraisalRecord(Long employeeAppraisalId, String reason) {
        String content = "驳回了目标，驳回理由：" + reason;
        String transContent = HrmLanguageEnum.TARGET_REJECTED.getFieldFormat() + ":" + reason;
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 提交自评
     */
    public Content selfScoringRecord(Long employeeAppraisalId) {
        String content = "填写并提交自评";
        String transContent = HrmLanguageEnum.EVALUATE_SELF_COMMIT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 他人提交评分
     */
    public Content otherScoringRecord(Long employeeAppraisalId) {
        String content = "填写并提交评分";
        String transContent = HrmLanguageEnum.EVALUATE_COMMIT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 结果审核
     */
    public Content resultAuditPass(Long employeeAppraisalId) {
        String content = "结果审核通过";
        String transContent = HrmLanguageEnum.EVALUATE_COMMIT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 结果审核驳回
     */
    public Content resultAuditReject(Long employeeAppraisalId, String reason) {
        String content = " 驳回了评分，驳回理由：" + reason;
        String transContent = HrmLanguageEnum.EVALUATE_REJECTED.getFieldFormat() + ":" + reason;
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 驳回评分
     *
     * @param employeeAppraisalId
     * @return
     */
    public Content rejectScoringRecord(Long employeeAppraisalId, String reason) {
        String content = " 驳回了评分，驳回理由：" + reason;
        String transContent = HrmLanguageEnum.EVALUATE_REJECTED.getFieldFormat() + ":" + reason;
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 确认考核结果，考核完成
     */
    public Content confirmResult(Long employeeAppraisalId) {
        String content = "考核结果已确认";
        String transContent = HrmLanguageEnum.CONFIRM_ASSESSMENT_RESULT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 结果申诉
     */
    public Content resultAppeal(Long employeeAppraisalId, String reason, String stageUserNames, String batchId) {
        String content = "考核结果申诉，申诉原因：" + reason + ",申诉节点列表:" + stageUserNames;
        String transContent = HrmLanguageEnum.ASSESSMENT_APPEAL.getFieldFormat() + ":" + reason + "," + HrmLanguageEnum.ASSESSMENT_APPEAL_NODE.getFieldFormat();
        saveAndFile(content, transContent, employeeAppraisalId, batchId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 结果申诉通过
     */
    public Content resultAppealPass(String appraisalName, List<Long> employeeAppraisalIds, boolean isLast) {
        String content;
        String transContent;
        if (isLast) {
            content = "确认考核结果，考核完成";
            transContent = HrmLanguageEnum.CONFIRM_ASSESSMENT_RESULT.getFieldFormat() + "," + HrmLanguageEnum.ASSESSMENT_COMPLETED.getFieldFormat();
        } else {
            content = "确认考核结果";
            transContent = HrmLanguageEnum.CONFIRM_ASSESSMENT_RESULT.getFieldFormat();
        }

        employeeAppraisalIds.forEach(employeeAppraisalId -> {
            save(content, transContent, employeeAppraisalId);
        });
        return new Content(appraisalName, content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 结果申诉通过
     */
    public Content resultAppealPass(Long employeeAppraisalId) {
        String content;
        content = "结果申诉已被通过";
        String transContent = HrmLanguageEnum.ASSESSMENT_APPEAL_PASS.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 结果申诉驳回
     */
    public Content resultAppealReject(Long employeeAppraisalId, String reason) {
        String content = "结果审核驳回,驳回原因:" + reason + "，考核完成";
        String transContent = HrmLanguageEnum.ASSESSMENT_APPEAL_REJECTED.getFieldFormat() + "reason" + HrmLanguageEnum.ASSESSMENT_COMPLETED.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }


    /**
     * 终止考核
     */
    public Content stopAppraisal(String appraisalName, List<Long> employeeAppraisalIds) {
        String content = "终止了考核";
        String transContent = HrmLanguageEnum.ASSESSMENT_END.getFieldFormat();
        employeeAppraisalIds.forEach(employeeAppraisalId -> {
            save(content, transContent, employeeAppraisalId);
        });
        return new Content(appraisalName, content, transContent, BehaviorEnum.UPDATE);
    }

    private void save(String content, String transContent, Long employeeAppraisalId) {
        appraisalActionRecordService.saveRecord(actionTypeEnum, behaviorEnum, content, transContent, employeeAppraisalId, null);
    }

    /**
     * 保存操作记录
     *
     * @param content
     * @param employeeAppraisalId
     */
    private void saveAndFile(String content, String transContent, Long employeeAppraisalId, String batchId) {
        appraisalActionRecordService.saveRecord(actionTypeEnum, behaviorEnum, content, transContent, employeeAppraisalId, batchId);
    }
}
