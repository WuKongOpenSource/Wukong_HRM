package com.kakarote.hrm.service.actionrecord.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.enums.LanguageFieldEnum;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 候选人操作记录
 *
 * @author hmb
 */
@Service("candidateActionRecordService")
public class CandidateActionRecordServiceImpl extends AbstractHrmActionRecordService {

    private static HrmActionTypeEnum actionTypeEnum = HrmActionTypeEnum.RECRUIT_CANDIDATE;
    private static LabelGroupEnum labelGroupEnum = LabelGroupEnum.RECRUIT_CANDIDATE;


    @Autowired
    private IHrmRecruitPostService recruitPostService;

    @Autowired
    private IHrmRecruitCandidateService recruitCandidateService;

    @Autowired
    private IHrmRecruitChannelService recruitChannelService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmRecruitInterviewService interviewService;

    private static final String NULL = "空";

    /**
     * 属性kv
     */
    private static Dict properties = Dict.create().set("candidateName", "候选人名称").set("mobile", "手机").set("sex", "性别").set("email", "邮箱").set("age", "年龄")
            .set("postId", "职位").set("workTime", "工作年限").set("education", "学历").set("graduateSchool", "毕业院校").set("latestWorkPlace", "最近工作单位")
            .set("channelId", "招聘渠道").set("remark", "备注");


    /**
     * 新建/删除操作记录
     */
    public Content addOrDeleteRecord(HrmActionBehaviorEnum behaviorEnum, Long candidateId) {
        String content = behaviorEnum.getName() + "了" + labelGroupEnum.getDesc();
        String transContent = behaviorEnum.getFieldFormat() + " " + labelGroupEnum.getFieldFormat();
        actionRecordService.saveRecord(actionTypeEnum, behaviorEnum, Collections.singletonList(content), Collections.singletonList(transContent), candidateId);
        HrmRecruitCandidate candidate = recruitCandidateService.getById(candidateId);
        if (HrmActionBehaviorEnum.ADD.equals(behaviorEnum)) {
            return new Content(candidate.getCandidateName(), content, transContent, BehaviorEnum.SAVE);
        } else {
            return new Content(candidate.getCandidateName(), content, transContent, BehaviorEnum.DELETE);
        }
    }


    /**
     * 职位实体类修改
     */
    public Content entityUpdateRecord(Map<String, Object> oldRecord, Map<String, Object> newRecord, Long candidateId, String candidateName) {
        HrmActionRecordListBO recordListBO = entityCommonUpdateRecord(labelGroupEnum, properties, oldRecord, newRecord);
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, recordListBO.getContentList(), recordListBO.getTransContentList(), candidateId);
        return new Content(candidateName, CollUtil.join(recordListBO.getContentList(), ","), CollUtil.join(recordListBO.getTransContentList(), ","), BehaviorEnum.UPDATE);
    }

    @Override
    protected String compare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        String content = super.compare(labelGroupEnum, properties, newFieldKey, oldValue, newValue);
        String postId = "postId";
        String sex = "sex";
        String education = "education";
        String channelId = "channelId";
        if (postId.equals(newFieldKey)) {
            HrmRecruitPost oldPost = recruitPostService.getById(oldValue);
            HrmRecruitPost newPost = recruitPostService.getById(newValue);
            if (oldPost != null) {
                oldValue = oldPost.getPostName();
            }
            if (newPost != null) {
                newValue = newPost.getPostName();
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (sex.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = SexEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = SexEnum.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (education.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = CandidateEducationEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = CandidateEducationEnum.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (channelId.equals(newFieldKey)) {
            HrmRecruitChannel oldChannel = recruitChannelService.getById(oldValue);
            HrmRecruitChannel newChannel = recruitChannelService.getById(newValue);
            if (oldChannel != null) {
                oldValue = oldChannel.getValue();
            }
            if (newChannel != null) {
                newValue = newChannel.getValue();
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        }
        return content;
    }

    @Override
    protected String transCompare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        String content = super.transCompare(labelGroupEnum, properties, newFieldKey, oldValue, newValue);
        String postId = "postId";
        String sex = "sex";
        String education = "education";
        String channelId = "channelId";
        if (postId.equals(newFieldKey)) {
            HrmRecruitPost oldPost = recruitPostService.getById(oldValue);
            HrmRecruitPost newPost = recruitPostService.getById(newValue);
            if (oldPost != null) {
                oldValue = oldPost.getPostName();
            }
            if (newPost != null) {
                newValue = newPost.getPostName();
            }
            content = "  " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (sex.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = SexEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = SexEnum.parseName(Integer.parseInt(newValue));
            }
            content = "  " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (education.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = CandidateEducationEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = CandidateEducationEnum.parseName(Integer.parseInt(newValue));
            }
            content = "  " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (channelId.equals(newFieldKey)) {
            HrmRecruitChannel oldChannel = recruitChannelService.getById(oldValue);
            HrmRecruitChannel newChannel = recruitChannelService.getById(newValue);
            if (oldChannel != null) {
                oldValue = oldChannel.getValue();
            }
            if (newChannel != null) {
                newValue = newChannel.getValue();
            }
            content = "  " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        }
        return content;
    }

    /**
     * 批量修改候选人状态 操作记录
     * --移动到初选通过   2
     * --移动到面试通过  4
     * --移动到待入职   6
     * --恢复到新候选人  1
     */
    public List<OperationLog> updateCandidateStatusRecord(UpdateCandidateStatusBO updateCandidateStatusBO) {

        List<OperationLog> operationLogs = new ArrayList<>();

        List<Long> candidateIds = updateCandidateStatusBO.getCandidateIds();
        Integer status = updateCandidateStatusBO.getStatus();

        CandidateStatusEnum parse = CandidateStatusEnum.parse(status);

        BehaviorEnum behaviorEnum = BehaviorEnum.valueOf(parse.name());

        String content = "将候选人{}由{}移到" + parse.getName();
        for (Long candidateId : candidateIds) {
            HrmRecruitCandidate candidate = recruitCandidateService.getById(candidateId);
            String format = StrUtil.format(content, candidate.getCandidateName(), CandidateStatusEnum.parseName(candidate.getStatus()));
            String transFormat = " " + HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + HrmLanguageEnum.MOVE.getFieldFormat() + CandidateStatusEnum.parseName(status);
            actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(format), Collections.singletonList(transFormat), candidateId);

            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(candidateId, candidate.getCandidateName());
            operationLog.setOperationInfo(format);
            operationLog.setBehavior(behaviorEnum);
            operationLog.setApplyObject(OperateObjectEnum.HRM_CANDIDATE);
            operationLogs.add(operationLog);
        }
        return operationLogs;
    }

    public List<OperationLog> eliminateCandidateBORecord(EliminateCandidateBO eliminateCandidateBO) {

        List<OperationLog> operationLogs = new ArrayList<>();
        String content = "淘汰了候选人{},淘汰原因:" + eliminateCandidateBO.getEliminate();
        if (StrUtil.isNotEmpty(eliminateCandidateBO.getRemarks())) {
            content += ",备注:" + eliminateCandidateBO.getRemarks();
        }

        for (Long candidateId : eliminateCandidateBO.getCandidateIds()) {
            HrmRecruitCandidate candidate = recruitCandidateService.getById(candidateId);
            String format = StrUtil.format(content, candidate.getCandidateName());

            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(candidateId, candidate.getCandidateName());
            operationLog.setOperationInfo(format);
            operationLogs.add(operationLog);

            String transFormat = HrmLanguageEnum.ELIMINATE.getFieldFormat() + HrmLanguageEnum.CANDIDATE.getFieldFormat() + " " + candidate.getCandidateName() + "," + HrmLanguageEnum.ELIMINATE_REASON.getFieldFormat() + ":" + eliminateCandidateBO.getEliminate();
            if (StrUtil.isNotEmpty(eliminateCandidateBO.getRemarks())) {
                transFormat += "," + HrmLanguageEnum.REMARK.getFieldFormat() + ":" + eliminateCandidateBO.getRemarks();
            }
            actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(format), Collections.singletonList(transFormat), candidateId);
        }
        return operationLogs;
    }


    /**
     * 修改应聘职位操作记录
     */
    public List<OperationLog> updateCandidatePostRecord(UpdateCandidatePostBO updateCandidatePostBO) {

        List<OperationLog> operationLogs = new ArrayList<>();
        List<Long> candidateIds = updateCandidatePostBO.getCandidateIds();
        Long postId = updateCandidatePostBO.getPostId();
        HrmRecruitPost recruitPost = recruitPostService.getById(postId);
        String content = "为候选人{}更改了应聘职位:" + recruitPost.getPostName();

        for (Long candidateId : candidateIds) {
            HrmRecruitCandidate candidate = recruitCandidateService.getById(candidateId);
            String format = StrUtil.format(content, candidate.getCandidateName());
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(candidateId, candidate.getCandidateName());
            operationLog.setOperationInfo(format);
            operationLogs.add(operationLog);

            String transFormat = HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + " " + HrmLanguageEnum.APPLY_POST.getFieldFormat() + ":" + recruitPost.getPostName();
            actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(format), Collections.singletonList(transFormat), candidateId);
        }
        return operationLogs;
    }

    /**
     * 修改应聘渠道操作记录
     */
    public List<OperationLog> updateCandidateRecruitChannel(UpdateCandidateRecruitChannelBO updateCandidateRecruitChannelBO) {

        List<OperationLog> operationLogs = new ArrayList<>();
        List<Long> candidateIds = updateCandidateRecruitChannelBO.getCandidateIds();
        HrmRecruitChannel channel = recruitChannelService.getById(updateCandidateRecruitChannelBO.getChannelId());
        String recruitChannel = channel.getValue();
        String content = "为候选人{}更改了应聘渠道:" + recruitChannel;
        for (Long candidateId : candidateIds) {
            HrmRecruitCandidate candidate = recruitCandidateService.getById(candidateId);
            String format = StrUtil.format(content, candidate.getCandidateName());
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(candidateId, candidate.getCandidateName());
            operationLog.setOperationInfo(format);
            operationLogs.add(operationLog);

            String transFormat = HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + HrmLanguageEnum.APPLY_POST.getFieldFormat() + ":" + recruitChannel;
            actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(format), Collections.singletonList(transFormat), candidateId);
        }
        return operationLogs;
    }

    /**
     * 一键清理候选人操作记录
     *
     * @param jsonObject
     */
    public void cleanCandidateRecord(JSONObject jsonObject) {
        List<Long> candidateIds = jsonObject.getJSONArray("candidateIds").toJavaList(Long.class);
        String eliminate = jsonObject.getString("eliminate");
        String content = "清理了候选人,原因" + eliminate;
        String transContent = HrmLanguageEnum.CLEAR.getFieldFormat() + " " + HrmLanguageEnum.CANDIDATE.getFieldFormat() + "," + HrmLanguageEnum.REASON.getFieldFormat() + eliminate;
        for (Long candidateId : candidateIds) {
            actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(content), Collections.singletonList(transContent), candidateId);
        }
    }

    /**
     * 安排面试操作记录
     *
     * @param candidate
     * @param interview
     */
    public Content addInterviewRecord(HrmRecruitCandidate candidate, HrmRecruitInterview interview) {

        HrmEmployee interviewEmployee = employeeService.getById(interview.getInterviewEmployeeId());
        String interviewEmployeeName = "";
        String content = "为候选人" + candidate.getCandidateName() + "安排了面试：" + DateUtil.formatLocalDateTime(interview.getInterviewTime());
        String transContent = HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + HrmLanguageEnum.ARRANGE.getFieldFormat() + " " + HrmLanguageEnum.INTERVIEW.getFieldFormat() + "：" + DateUtil.formatLocalDateTime(interview.getInterviewTime());
        if (interview.getType() != null) {
            content += "，面试方式:" + InterviewType.parseName(interview.getType());
            transContent += "，" + HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat() + ":" + InterviewType.parseName(interview.getType());
        }
        if (interviewEmployee != null) {
            interviewEmployeeName = interviewEmployee.getEmployeeName();
            content += "，面试官：" + interviewEmployeeName;
            transContent += "，" + HrmLanguageEnum.INTERVIEWER.getFieldFormat() + "：" + interviewEmployeeName;
        }
        Set<Long> employeeIds = TagUtil.toLongSet(interview.getOtherInterviewEmployeeIds());
        StringBuilder otherInterviewEmployeeName = new StringBuilder();
        if (CollectionUtil.isNotEmpty(employeeIds)) {
            employeeIds.forEach(employeeId -> {
                HrmEmployee employee = employeeService.getById(employeeId);
                //判断员工不存在 || 删除 || 已离职
                if (employee.getIsDel() == 1 || employee.getEntryStatus() == EmployeeEntryStatus.ALREADY_LEAVE.getValue()) {
                    throw new CrmException(HrmCodeEnum.EMPLOYEE_NO_EXIST, employee.getEmployeeName());
                }
                otherInterviewEmployeeName.append(employee.getEmployeeName()).append("、");
            });
        }
        if (StrUtil.isNotEmpty(otherInterviewEmployeeName.toString())) {
            content += "，其他面试官：" + otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1).toString();
            transContent += "，" + HrmLanguageEnum.INTERVIEWER_OTHER.getFieldFormat() + "：" + otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1).toString();
        }
        content += "，面试轮次：第" + interview.getStageNum() + "轮";
        transContent += "，" + HrmLanguageEnum.INTERVIEW_ROUNDS.getFieldFormat() + "： " + interview.getStageNum() + "  ";
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(content), Collections.singletonList(transContent), interview.getCandidateId());
        return new Content(candidate.getCandidateName(), content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 取消面试操作记录
     *
     * @param interview
     */
    public Content cancelInterviewRecord(HrmRecruitInterview interview) {
        HrmRecruitCandidate candidate = recruitCandidateService.getById(interview.getCandidateId());
        HrmEmployee interviewEmployee = employeeService.getById(interview.getInterviewEmployeeId());
        String interviewEmployeeName = "";
        if (interviewEmployee != null) {
            interviewEmployeeName = interviewEmployee.getEmployeeName();
        }
        Set<Long> employeeIds = TagUtil.toLongSet(interview.getOtherInterviewEmployeeIds());
        StringBuilder otherInterviewEmployeeName = new StringBuilder();
        if (CollectionUtil.isNotEmpty(employeeIds)) {
            employeeIds.forEach(employeeId -> {
                HrmEmployee employee = employeeService.getById(employeeId);
                //判断员工不存在 || 删除 || 已离职
                if (employee.getIsDel() == 1 || employee.getEntryStatus() == EmployeeEntryStatus.ALREADY_LEAVE.getValue()) {
                    throw new CrmException(HrmCodeEnum.EMPLOYEE_NO_EXIST, employee.getEmployeeName());
                }
                otherInterviewEmployeeName.append(employee.getEmployeeName()).append("、");
            });
        }
        String content = "为候选人" + candidate.getCandidateName() + "取消了面试：" + DateUtil.formatLocalDateTime(interview.getInterviewTime());
        String transContent = HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + HrmLanguageEnum.CANCEL.getFieldFormat() + HrmLanguageEnum.INTERVIEW.getFieldFormat() + " ：" + DateUtil.formatLocalDateTime(interview.getInterviewTime());
        if (interview.getType() != null) {
            content += "，面试方式：" + InterviewType.parseName(interview.getType());
            transContent += "，" + HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat() + "：" + InterviewType.parseName(interview.getType());
        }
        if (StrUtil.isNotEmpty(interview.getCancelReason())) {
            content += "，取消原因：" + interview.getCancelReason();
            transContent += "，" + HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat() + "：" + interview.getCancelReason();
        }
        if (StrUtil.isNotEmpty(interviewEmployeeName)) {
            content += "，原面试官：" + interviewEmployeeName;
            transContent += "，" + HrmLanguageEnum.INTERVIEWER_LAST.getFieldFormat() + "：" + interviewEmployeeName;
        }
        if (StrUtil.isNotEmpty(otherInterviewEmployeeName.toString())) {
            content += "，原其他面试官：" + otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1).toString();
            transContent += "，" + HrmLanguageEnum.INTERVIEWER_OTHER_LAST.getFieldFormat() + "：" + otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1).toString();
        }
        content += "，原面试轮次：第" + interview.getStageNum() + "轮";
        transContent += "，" + HrmLanguageEnum.INTERVIEW_ROUNDS_LAST.getFieldFormat() + "： " + interview.getStageNum() + "  ";
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(content), Collections.singletonList(transContent), interview.getCandidateId());
        return new Content(candidate.getCandidateName(), content, transContent, BehaviorEnum.UPDATE);
    }

    /**
     * 更改面试安排记录
     *
     * @param candidate
     * @param interview
     * @param operateType
     * @return
     */
    public Content updateInterviewRecord(HrmRecruitCandidate candidate, HrmRecruitInterview interview, Integer operateType) {
        HrmRecruitInterview hrmRecruitInterview = interviewService.getById(interview.getInterviewId());
        StringBuilder content = new StringBuilder();
        StringBuilder transContent = new StringBuilder();
        content.append("为候选人" + candidate.getCandidateName() + "更改了面试安排");
        transContent.append(HrmLanguageEnum.CANDIDATE.getFieldFormat() + candidate.getCandidateName() + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + HrmLanguageEnum.INTERVIEW_PLAN.getFieldFormat());
        int three = 3;
        if (operateType == three) {
            if (null != interview.getInterviewTime()) {
                content.append("，面试时间：" + "从无更改为").append(DateUtil.formatLocalDateTime(interview.getInterviewTime()));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEW_PLAN.getFieldFormat()).append("：").append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(DateUtil.formatLocalDateTime(interview.getInterviewTime()));
            }
            if (interview.getType() != null) {
                content.append("，面试方式：" + "从无更改为").append(InterviewType.parseName(interview.getType()));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat()).append("：").append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(InterviewType.parseName(interview.getType()));
            }
            if (null != interview.getInterviewEmployeeId()) {
                HrmEmployee interviewEmployee = employeeService.getById(interview.getInterviewEmployeeId());
                if (interviewEmployee != null) {
                    content.append("，面试官：" + "从无更改为").append(interviewEmployee.getEmployeeName());
                    transContent.append("，").append(HrmLanguageEnum.INTERVIEWER.getFieldFormat()).append("：").append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(interviewEmployee.getEmployeeName());
                }
            }
            Set<Long> employeeIds = TagUtil.toLongSet(interview.getOtherInterviewEmployeeIds());
            StringBuilder otherInterviewEmployeeName = new StringBuilder();
            if (CollectionUtil.isNotEmpty(employeeIds)) {
                employeeIds.forEach(employeeId -> {
                    HrmEmployee employee = employeeService.getById(employeeId);
                    //判断员工不存在 || 删除 || 已离职
                    if (employee.getIsDel() == 1 || employee.getEntryStatus() == EmployeeEntryStatus.ALREADY_LEAVE.getValue()) {
                        throw new CrmException(HrmCodeEnum.EMPLOYEE_NO_EXIST, employee.getEmployeeName());
                    }
                    otherInterviewEmployeeName.append(employee.getEmployeeName()).append("、");
                });
                content.append("，其他面试官：" + "从无更改为").append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEWER_OTHER.getFieldFormat()).append("：").append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
            }
        } else {
            if (!interview.getInterviewTime().equals(hrmRecruitInterview.getInterviewTime())) {
                content.append("，面试时间：" + "从").append(DateUtil.formatLocalDateTime(hrmRecruitInterview.getInterviewTime())).append("更改为").append(DateUtil.formatLocalDateTime(interview.getInterviewTime()));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEW_TIME.getFieldFormat()).append("：").append("   ").append(DateUtil.formatLocalDateTime(hrmRecruitInterview.getInterviewTime())).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(DateUtil.formatLocalDateTime(interview.getInterviewTime()));
            }
            if (ObjectUtil.notEqual(interview.getType(), hrmRecruitInterview.getType())) {
                String oldInterViewType = "无";
                String interViewType = "无";
                String transoldInterViewType = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
                String transInterViewType = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
                ;

                if (hrmRecruitInterview.getType() != null) {
                    oldInterViewType = InterviewType.parseName(hrmRecruitInterview.getType());
                    transoldInterViewType = InterviewType.parseName(hrmRecruitInterview.getType());
                }
                if (interview.getType() != null) {
                    interViewType = InterviewType.parseName(interview.getType());
                    transInterViewType = InterviewType.parseName(interview.getType());
                }
                content.append("，面试方式：" + "从").append(oldInterViewType).append("更改为").append(interViewType);
                transContent.append("，").append(HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat()).append("：").append("   ").append(transoldInterViewType).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(transInterViewType);
            }
            String oldInterviewEmployeeName = "无";
            String interviewEmployeeName = "无";
            String transOldInterviewEmployeeName = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
            String transInterviewEmployeeName = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
            if (null != hrmRecruitInterview.getInterviewEmployeeId()) {
                HrmEmployee oldInterviewEmployee = employeeService.getById(hrmRecruitInterview.getInterviewEmployeeId());
                if (oldInterviewEmployee != null) {
                    oldInterviewEmployeeName = oldInterviewEmployee.getEmployeeName();
                    transOldInterviewEmployeeName = oldInterviewEmployeeName;
                }
            }
            if (null != interview.getInterviewEmployeeId()) {
                HrmEmployee interviewEmployee = employeeService.getById(interview.getInterviewEmployeeId());
                if (interviewEmployee != null) {
                    interviewEmployeeName = interviewEmployee.getEmployeeName();
                    transInterviewEmployeeName = interviewEmployeeName;
                }
            }
            if (!oldInterviewEmployeeName.equals(interviewEmployeeName)) {
                content.append("，面试官：" + "从").append(oldInterviewEmployeeName).append("更改为").append(interviewEmployeeName);
            }
            if (!transOldInterviewEmployeeName.equals(transInterviewEmployeeName)) {
                transContent.append("，").append(HrmLanguageEnum.INTERVIEW_METHOD.getFieldFormat()).append("：").append("   ").append(transOldInterviewEmployeeName).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(transInterviewEmployeeName);
            }

            StringBuilder oldOtherInterviewEmployeeName = new StringBuilder();
            StringBuilder otherInterviewEmployeeName = new StringBuilder();
            Set<Long> oldEmployeeIds = TagUtil.toLongSet(hrmRecruitInterview.getOtherInterviewEmployeeIds());
            Set<Long> employeeIds = TagUtil.toLongSet(interview.getOtherInterviewEmployeeIds());
            if (CollectionUtil.isNotEmpty(oldEmployeeIds)) {
                oldEmployeeIds.forEach(employeeId -> {
                    HrmEmployee employee = employeeService.getById(employeeId);
                    //判断员工不存在 || 删除 || 已离职
                    if (employee.getIsDel() == 1 || employee.getEntryStatus() == EmployeeEntryStatus.ALREADY_LEAVE.getValue()) {
                        throw new CrmException(HrmCodeEnum.EMPLOYEE_NO_EXIST, employee.getEmployeeName());
                    }
                    oldOtherInterviewEmployeeName.append(employee.getEmployeeName()).append("、");
                });
            }
            if (CollectionUtil.isNotEmpty(employeeIds)) {
                employeeIds.forEach(employeeId -> {
                    HrmEmployee employee = employeeService.getById(employeeId);
                    //判断员工不存在 || 删除 || 已离职
                    if (employee.getIsDel() == 1 || employee.getEntryStatus() == EmployeeEntryStatus.ALREADY_LEAVE.getValue()) {
                        throw new CrmException(HrmCodeEnum.EMPLOYEE_NO_EXIST, employee.getEmployeeName());
                    }
                    otherInterviewEmployeeName.append(employee.getEmployeeName()).append("、");
                });
            }
            if (CollectionUtil.isNotEmpty(employeeIds) && CollectionUtil.isNotEmpty(oldEmployeeIds) && !TagUtil.isSetEqual(oldEmployeeIds, employeeIds)) {
                content.append("，其他面试官：" + "从").append(oldOtherInterviewEmployeeName.deleteCharAt(oldOtherInterviewEmployeeName.length() - 1)).append("更改为").append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEWER_OTHER.getFieldFormat()).append("：").append("   ").append(oldOtherInterviewEmployeeName.deleteCharAt(oldOtherInterviewEmployeeName.length() - 1)).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
            } else if (CollectionUtil.isNotEmpty(employeeIds) && CollectionUtil.isEmpty(oldEmployeeIds)) {
                content.append("，其他面试官：" + "从" + "无" + "更改为").append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
                transContent.append("，").append(HrmLanguageEnum.INTERVIEWER_OTHER.getFieldFormat()).append("：").append("   ").append(LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat()).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(otherInterviewEmployeeName.deleteCharAt(otherInterviewEmployeeName.length() - 1));
            } else if (CollectionUtil.isEmpty(employeeIds) && CollectionUtil.isNotEmpty(oldEmployeeIds)) {
                content.append("，其他面试官：" + "从").append(oldOtherInterviewEmployeeName.deleteCharAt(oldOtherInterviewEmployeeName.length() - 1)).append("更改为").append("无");
                transContent.append("，").append(HrmLanguageEnum.INTERVIEWER_OTHER.getFieldFormat()).append("：").append("   ").append(oldOtherInterviewEmployeeName.deleteCharAt(oldOtherInterviewEmployeeName.length() - 1)).append(LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat()).append(LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat());
            }
        }
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(content.toString()), Collections.singletonList(transContent.toString()), interview.getCandidateId());
        return new Content(candidate.getCandidateName(), content.toString(), transContent.toString(), BehaviorEnum.UPDATE);
    }

    ;
}
