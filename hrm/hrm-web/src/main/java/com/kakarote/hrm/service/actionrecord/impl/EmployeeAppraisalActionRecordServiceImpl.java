package com.kakarote.hrm.service.actionrecord.impl;


import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.HrmActionTypeEnum;
import com.kakarote.hrm.constant.HrmLanguageEnum;
import com.kakarote.hrm.entity.BO.UpdateScheduleBO;
import com.kakarote.hrm.entity.PO.HrmAchievementEmployeeSeg;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("employeeAppraisalActionRecordService")
public class EmployeeAppraisalActionRecordServiceImpl extends AbstractHrmActionRecordService {

    private static HrmActionTypeEnum actionTypeEnum = HrmActionTypeEnum.EMPLOYEE_APPRAISAL;
    private static HrmActionBehaviorEnum behaviorEnum = HrmActionBehaviorEnum.UPDATE;


    /**
     * 开启考核记录
     */
    public Content startAppraisalRecord(String appraisalName, List<Long> employeeAppraisalIds) {
        String content = "开启了考核";
        String transContent = HrmLanguageEnum.ASSESSMENT_STARTED.getFieldFormat();
        employeeAppraisalIds.forEach(employeeAppraisalId -> {
            save(content, transContent, employeeAppraisalId);
        });
        return new Content(appraisalName, content + ":" + appraisalName, transContent.toString(), BehaviorEnum.UPDATE);
    }

    /**
     * 提交考核
     */

    public Content submitAppraisalRecord(Long employeeAppraisalId) {
        String content = "提交了目标";
        String transContent = HrmLanguageEnum.TARGET_COMMIT.getFieldFormat();
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
     * 提交评定
     */
    public Content submitEvaluatoRecord(Long employeeAppraisalId) {
        String content = "提交了评定";
        String transContent = HrmLanguageEnum.EVALUATE_COMMIT.getFieldFormat();
        save(content, transContent, employeeAppraisalId);
        return new Content("", content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 确认考核结果，考核完成
     */
    public Content confirmResult(String appraisalName, List<Long> employeeAppraisalIds, boolean isLast) {
        String content;
        String transContent;
        if (isLast) {
            content = "确认考核结果，考核完成";
            transContent = HrmLanguageEnum.CONFIRM_ASSESSMENT_RESULT.getFieldFormat() + "，" + HrmLanguageEnum.ASSESSMENT_COMPLETED.getFieldFormat();
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
     * 修改目标进度
     */
    public Content updateSchedule(String appraisalName, UpdateScheduleBO updateScheduleBO, HrmAchievementEmployeeSeg seg) {
        String content = "修改了目标" + seg.getSegName() + "进度为" + updateScheduleBO.getSchedule() + "%,完成情况说明:" + updateScheduleBO.getExplainDesc();
        String transContent = HrmLanguageEnum.TARGET_UPDATE.getFieldFormat() + seg.getSegName() + HrmLanguageEnum.PROGRESS.getFieldFormat() + ":" + updateScheduleBO.getSchedule() + "%," + HrmLanguageEnum.COMPLETION_DESCRIPTION.getFieldFormat() + ":" + updateScheduleBO.getExplainDesc();
        save(content, transContent, seg.getEmployeeAppraisalId());
        return new Content(appraisalName, content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 拒绝
     *
     * @param appraisalName
     * @param status        2 驳回目标 3 驳回评定
     * @param reason        原因
     */
    public Content reject(String appraisalName, Long employeeAppraisalId, Integer status, String reason) {
        String content;
        String transContent;
        int two = 2;
        if (status == two) {
            content = "驳回了目标,驳回理由：" + reason;
            transContent = HrmLanguageEnum.TARGET_REJECTED.getFieldFormat() + "," + HrmLanguageEnum.REJECTED_REASON.getFieldFormat() + "：" + reason;
        } else {
            content = "驳回了评定,驳回理由：" + reason;
            transContent = HrmLanguageEnum.EVALUATE_REJECTED.getFieldFormat() + "," + HrmLanguageEnum.REJECTED_REASON.getFieldFormat() + "：" + reason;
        }
        save(content, transContent, employeeAppraisalId);

        return new Content(appraisalName, content, transContent, BehaviorEnum.UPDATE);
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


    /**
     * 保存操作记录
     *
     * @param content
     * @param employeeAppraisalId
     */
    private void save(String content, String transContent, Long employeeAppraisalId) {
        actionRecordService.saveRecord(actionTypeEnum, behaviorEnum, Collections.singletonList(content), Collections.singletonList(transContent), employeeAppraisalId);
    }

}
