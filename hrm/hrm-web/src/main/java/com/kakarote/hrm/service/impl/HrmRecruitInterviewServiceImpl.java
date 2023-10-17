package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.CandidateStatusEnum;
import com.kakarote.hrm.constant.RecruitEnum;
import com.kakarote.hrm.entity.BO.SetInterviewResultBO;
import com.kakarote.hrm.entity.BO.SetRecruitInterviewBO;
import com.kakarote.hrm.entity.PO.AdminMessage;
import com.kakarote.hrm.entity.PO.HrmRecruitCandidate;
import com.kakarote.hrm.entity.PO.HrmRecruitInterview;
import com.kakarote.hrm.mapper.HrmRecruitInterviewMapper;
import com.kakarote.hrm.service.IAdminMessageService;
import com.kakarote.hrm.service.IHrmRecruitCandidateService;
import com.kakarote.hrm.service.IHrmRecruitInterviewService;
import com.kakarote.hrm.service.actionrecord.impl.CandidateActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 面试表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmRecruitInterviewServiceImpl extends BaseServiceImpl<HrmRecruitInterviewMapper, HrmRecruitInterview> implements IHrmRecruitInterviewService {

    @Autowired
    private IHrmRecruitCandidateService recruitCandidateService;

    @Resource
    private CandidateActionRecordServiceImpl candidateActionRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog setInterview(SetRecruitInterviewBO setRecruitInterviewBO) {
        Integer operateType = 1;
        HrmRecruitInterview hrmRecruitInterview = BeanUtil.copyProperties(setRecruitInterviewBO, HrmRecruitInterview.class);
        HrmRecruitCandidate hrmRecruitCandidate = recruitCandidateService.getById(hrmRecruitInterview.getCandidateId());
        Optional<HrmRecruitInterview> hrmInterview = lambdaQuery().eq(HrmRecruitInterview::getCandidateId, hrmRecruitInterview.getCandidateId()).orderByDesc(HrmRecruitInterview::getCreateTime).oneOpt();
        if (hrmInterview.isPresent()) {
            if (hrmRecruitCandidate.getStatus().equals(CandidateStatusEnum.ARRANGE_AN_INTERVIEW.getValue())) {
                if (hrmInterview.get().getResult().equals(RecruitEnum.RecruitInterviewResult.CANCEL.getValue())) {
                    // 取消面试后，再更改面试安排
                    operateType = 3;
                }
                hrmRecruitInterview.setInterviewId(hrmInterview.get().getInterviewId());
            }
        }
        hrmRecruitInterview.setOtherInterviewEmployeeIds(TagUtil.fromLongSet(setRecruitInterviewBO.getOtherInterviewEmployeeIds()));
        List<Long> employeeIds = new ArrayList<>();
        if (setRecruitInterviewBO.getInterviewEmployeeId() != null) {
            employeeIds.add(setRecruitInterviewBO.getInterviewEmployeeId());
        }
        if (CollectionUtil.isNotEmpty(setRecruitInterviewBO.getOtherInterviewEmployeeIds())) {
            employeeIds.addAll(setRecruitInterviewBO.getOtherInterviewEmployeeIds());
        }
        HrmRecruitCandidate candidateServiceById = recruitCandidateService.getById(hrmRecruitInterview.getCandidateId());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(candidateServiceById.getCandidateId(), candidateServiceById.getCandidateName());


        if (hrmRecruitInterview.getInterviewId() != null) {
            Content content = candidateActionRecordService.updateInterviewRecord(candidateServiceById, hrmRecruitInterview, operateType);
            operationLog.setOperationInfo(content.getDetail());
            int three = 3;
            if (operateType == three) {
                hrmRecruitInterview.setResult(1);
            }
            updateById(hrmRecruitInterview);
        } else {
            Optional<HrmRecruitInterview> maxNumOpt = lambdaQuery().eq(HrmRecruitInterview::getCandidateId, hrmRecruitInterview.getCandidateId()).orderByDesc(HrmRecruitInterview::getCreateTime).oneOpt();
            Integer stageNum;
            if (maxNumOpt.isPresent()) {
                HrmRecruitInterview maxNum = maxNumOpt.get();
                if (maxNum.getResult() == RecruitEnum.RecruitInterviewResult.UNFINISHED.getValue()) {
                    stageNum = maxNum.getStageNum() + 1;
                } else {
                    stageNum = 1;
                }
            } else {
                stageNum = 1;
            }
            hrmRecruitInterview.setStageNum(stageNum);
            save(hrmRecruitInterview);

            Content content = candidateActionRecordService.addInterviewRecord(candidateServiceById, hrmRecruitInterview);

            operationLog.setOperationInfo(content.getDetail());

            HrmRecruitCandidate candidate = new HrmRecruitCandidate();
            candidate.setStatus(CandidateStatusEnum.ARRANGE_AN_INTERVIEW.getValue());
            candidate.setStatusUpdateTime(LocalDateTime.now());
            candidate.setCandidateId(hrmRecruitInterview.getCandidateId());
            candidate.setStageNum(stageNum);
            recruitCandidateService.updateById(candidate);
            HrmRecruitCandidate recruitCandidate = recruitCandidateService.getById(hrmRecruitInterview.getCandidateId());
            employeeIds.forEach(employeeId -> {
                //为面试官发送面试安排通知
                AdminMessage adminMessage = new AdminMessage();
                adminMessage.setCreateUser(UserUtil.getUserId());
                adminMessage.setCreateTime(LocalDateTime.now());
                adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employeeId));
                adminMessage.setLabel(8);
                adminMessage.setTypeId(hrmRecruitInterview.getCandidateId());
                adminMessage.setType(AdminMessageEnum.HRM_INTERVIEW_ARRANGEMENTS.getType());
                adminMessage.setTitle(recruitCandidate.getCandidateName());
                Integer type = setRecruitInterviewBO.getType() != null ? setRecruitInterviewBO.getType() : 0;
                adminMessage.setContent(new JSONObject().fluentPut("createTime", DateUtil.format(setRecruitInterviewBO.getInterviewTime(), "MM-dd HH:mm")).fluentPut("content", "{hrm.recruit.849032bf57137fefd2c33e6400e668f8}:" + stageNum + "").toJSONString());
                ApplicationContextHolder.getBean(IAdminMessageService.class).save(adminMessage);
            });
        }
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog setInterviewResult(SetInterviewResultBO setInterviewResultBO) {
        HrmRecruitInterview hrmRecruitInterview = BeanUtil.copyProperties(setInterviewResultBO, HrmRecruitInterview.class);
        HrmRecruitCandidate hrmRecruitCandidate = recruitCandidateService.getById(setInterviewResultBO.getCandidateId());
        Optional<HrmRecruitInterview> hrmInterview = lambdaQuery().eq(HrmRecruitInterview::getCandidateId, hrmRecruitCandidate.getCandidateId()).
                eq(HrmRecruitInterview::getStageNum, hrmRecruitCandidate.getStageNum()).orderByDesc(HrmRecruitInterview::getCreateTime).oneOpt();
        if (hrmInterview.isPresent()) {
            if (setInterviewResultBO.getResult().equals(hrmInterview.get().getResult())) {
                throw new CrmException(HrmCodeEnum.NO_INTERVIEW_ARRANGEMENT);
            }
            hrmRecruitInterview.setInterviewId(hrmInterview.get().getInterviewId());
            hrmRecruitInterview.setType(hrmInterview.get().getType());
            hrmRecruitInterview.setInterviewEmployeeId(hrmInterview.get().getInterviewEmployeeId());
            hrmRecruitInterview.setOtherInterviewEmployeeIds(hrmInterview.get().getOtherInterviewEmployeeIds());
        }
        updateById(hrmRecruitInterview);
        HrmRecruitCandidate candidate = new HrmRecruitCandidate();

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmRecruitCandidate.getCandidateId(), hrmRecruitCandidate.getCandidateName());

        if (setInterviewResultBO.getResult().equals(RecruitEnum.RecruitInterviewResult.PASS.getValue())) {
            candidate.setStatus(CandidateStatusEnum.PASS_THE_INTERVIEW.getValue());
            operationLog.setBehavior(BehaviorEnum.PASS_INTERVIEW);
            operationLog.setOperationInfo("面试通过");
        } else if (setInterviewResultBO.getResult().equals(RecruitEnum.RecruitInterviewResult.NOT_PASS.getValue())) {
            candidate.setStatus(CandidateStatusEnum.OBSOLETE.getValue());
            operationLog.setBehavior(BehaviorEnum.NOT_PASS_INTERVIEW);
            operationLog.setOperationInfo("面试未通过");
        } else if (setInterviewResultBO.getResult().equals(RecruitEnum.RecruitInterviewResult.CANCEL.getValue())) {
            HrmRecruitInterview interview = getById(hrmRecruitInterview.getInterviewId());
            Content content = candidateActionRecordService.cancelInterviewRecord(interview);
            operationLog.setBehavior(BehaviorEnum.CANCEL_INTERVIEW);
            operationLog.setOperationInfo(content.getDetail());
        }
        candidate.setStatusUpdateTime(LocalDateTime.now());
        candidate.setCandidateId(setInterviewResultBO.getCandidateId());
        recruitCandidateService.updateById(candidate);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> addBatchInterview(SetRecruitInterviewBO setRecruitInterviewBO) {
        List<Long> candidateIds = new ArrayList<>();
        if (null == setRecruitInterviewBO.getCandidateIds()) {
            candidateIds.add(setRecruitInterviewBO.getCandidateId());
            setRecruitInterviewBO.setCandidateIds(candidateIds);
        }
        List<OperationLog> operationLogList = new ArrayList<>();
        setRecruitInterviewBO.getCandidateIds().forEach(candidateId -> {
            setRecruitInterviewBO.setCandidateId(candidateId);
            operationLogList.add(setInterview(setRecruitInterviewBO));
        });
        return operationLogList;
    }
}
