package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.constant.CandidateStatusEnum;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.PO.HrmRecruitCandidate;
import com.kakarote.hrm.entity.PO.HrmRecruitInterview;
import com.kakarote.hrm.entity.VO.CandidatePageListVO;
import com.kakarote.hrm.service.AdminFileService;
import com.kakarote.hrm.mapper.HrmRecruitCandidateMapper;
import com.kakarote.hrm.service.IHrmEmployeeService;
import com.kakarote.hrm.service.IHrmRecruitCandidateService;
import com.kakarote.hrm.service.IHrmRecruitInterviewService;
import com.kakarote.hrm.service.actionrecord.impl.CandidateActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 招聘候选人表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmRecruitCandidateServiceImpl extends BaseServiceImpl<HrmRecruitCandidateMapper, HrmRecruitCandidate> implements IHrmRecruitCandidateService {

    @Autowired
    private HrmRecruitCandidateMapper recruitCandidateMapper;

    @Resource
    private CandidateActionRecordServiceImpl candidateActionRecordService;

    @Autowired
    private IHrmRecruitInterviewService recruitInterviewService;

    @Autowired
    private AdminFileService adminFileService;

    @Autowired
    private EmployeeUtil employeeUtil;
    @Autowired
    private IHrmEmployeeService employeeService;

    @Override
    public BasePage<CandidatePageListVO> queryCandidateList(QueryCandidatePageListBO queryCandidatePageListBO) {
        Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
        BasePage<CandidatePageListVO> page = recruitCandidateMapper.queryPageList(queryCandidatePageListBO.parse(), queryCandidatePageListBO, deptIds);
        page.getList().forEach(obj -> {
            SimpleUser data = UserCacheUtil.getSimpleUser(obj.getCreateUserId());
            obj.setCreateUserName(data.getNickname());
            if (obj.getStatus() < CandidateStatusEnum.ARRANGE_AN_INTERVIEW.getValue()) {
                obj.setInterviewEmployeeId(null);
                obj.setOtherInterviewEmployeeIds(null);
                obj.setInterviewTime(null);
                obj.setInterviewEmployeeName(null);
                obj.setOtherInterviewEmployeeName(null);
                obj.setStageNum(0);
            }
            if (ObjectUtil.isNotEmpty(obj.getOtherInterviewEmployeeIds()) && StrUtil.isNotBlank(obj.getOtherInterviewEmployeeIds())) {
                String otherInterviewEmployeeName = employeeService.lambdaQuery().in(HrmEmployee::getEmployeeId, TagUtil.toLongSet(obj.getOtherInterviewEmployeeIds()))
                        .list().stream().map(HrmEmployee::getEmployeeName).collect(Collectors.joining(","));
                obj.setOtherInterviewEmployeeName(otherInterviewEmployeeName);
            }
            Optional<HrmRecruitInterview> hrmInterview = recruitInterviewService.lambdaQuery().eq(HrmRecruitInterview::getCandidateId, obj.getCandidateId()).orderByDesc(HrmRecruitInterview::getCreateTime).oneOpt();
            if (hrmInterview.isPresent()) {
                obj.setInterviewResult(hrmInterview.get().getResult());
            } else {
                obj.setInterviewResult(1);
            }
            //增加多语言
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("channelName_resourceKey", "admin.recruitChannel." + obj.getChannelName());
            obj.setLanguageKeyMap(keyMap);
        });
        return page;
    }

    @Override
    public CandidatePageListVO queryById(String candidateId) {
        CandidatePageListVO candidatePageListVO = recruitCandidateMapper.getById(candidateId);
        SimpleUser data = UserCacheUtil.getSimpleUser(candidatePageListVO.getCreateUserId());
        candidatePageListVO.setCreateUserName(data.getNickname());
        if (candidatePageListVO.getStatus() < CandidateStatusEnum.ARRANGE_AN_INTERVIEW.getValue()) {
            candidatePageListVO.setInterviewEmployeeId(null);
            candidatePageListVO.setOtherInterviewEmployeeIds(null);
            candidatePageListVO.setInterviewTime(null);
            candidatePageListVO.setInterviewEmployeeName(null);
            candidatePageListVO.setOtherInterviewEmployeeName(null);
            candidatePageListVO.setStageNum(0);
        }
        if (StrUtil.isNotEmpty(candidatePageListVO.getOtherInterviewEmployeeIds())) {
            String otherInterviewEmployeeName = employeeService.lambdaQuery().in(HrmEmployee::getEmployeeId, TagUtil.toLongSet(candidatePageListVO.getOtherInterviewEmployeeIds()))
                    .list().stream().map(HrmEmployee::getEmployeeName).collect(Collectors.joining(","));
            candidatePageListVO.setOtherInterviewEmployeeName(otherInterviewEmployeeName);
        }
        Optional<HrmRecruitInterview> hrmInterview = recruitInterviewService.lambdaQuery().eq(HrmRecruitInterview::getCandidateId, candidateId).orderByDesc(HrmRecruitInterview::getCreateTime).oneOpt();
        if (hrmInterview.isPresent()) {
            candidatePageListVO.setInterviewResult(hrmInterview.get().getResult());
        } else {
            candidatePageListVO.setInterviewResult(1);
        }
        return candidatePageListVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addCandidate(HrmRecruitCandidate hrmRecruitCandidate) {
        String batchId = StrUtil.isEmpty(hrmRecruitCandidate.getBatchId()) ? IdUtil.simpleUUID() : hrmRecruitCandidate.getBatchId();
        hrmRecruitCandidate.setBatchId(batchId);
        hrmRecruitCandidate.setStatusUpdateTime(LocalDateTime.now());
        save(hrmRecruitCandidate);
        Content content = candidateActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, hrmRecruitCandidate.getCandidateId());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmRecruitCandidate.getCandidateId(), hrmRecruitCandidate.getCandidateName());
        operationLog.setOperationInfo(content.getDetail());
        return operationLog;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog setCandidate(HrmRecruitCandidate hrmRecruitCandidate) {

        HrmRecruitCandidate oldDate = getById(hrmRecruitCandidate.getCandidateId());
        Content content = candidateActionRecordService.entityUpdateRecord(BeanUtil.beanToMap(oldDate), BeanUtil.beanToMap(hrmRecruitCandidate), hrmRecruitCandidate.getCandidateId(), hrmRecruitCandidate.getCandidateName());
        if (null == hrmRecruitCandidate.getAge() || null == hrmRecruitCandidate.getWorkTime()) {
            LambdaUpdateWrapper<HrmRecruitCandidate> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(HrmRecruitCandidate::getCandidateId, hrmRecruitCandidate.getCandidateId());
            wrapper.set(null == hrmRecruitCandidate.getAge(), HrmRecruitCandidate::getAge, null);
            wrapper.set(null == hrmRecruitCandidate.getWorkTime(), HrmRecruitCandidate::getWorkTime, null);
            update(wrapper);
        }
        updateById(hrmRecruitCandidate);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(oldDate.getCandidateId(), oldDate.getCandidateName());
        operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteById(Long candidateId) {
        HrmRecruitCandidate candidate = getById(candidateId);

        removeById(candidateId);
        recruitInterviewService.lambdaUpdate().eq(HrmRecruitInterview::getCandidateId, candidateId).remove();
        Content content = candidateActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, candidateId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(candidate.getCandidateId(), candidate.getCandidateName());
        operationLog.setOperationInfo(content.getDetail());
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> updateCandidateStatus(UpdateCandidateStatusBO updateCandidateStatusBO) {
        if (CollUtil.isEmpty(updateCandidateStatusBO.getCandidateIds())) {
            return new ArrayList<>();
        }
        List<OperationLog> operationLogs = candidateActionRecordService.updateCandidateStatusRecord(updateCandidateStatusBO);
        Integer status = updateCandidateStatusBO.getStatus();
        List<Long> candidateIds = updateCandidateStatusBO.getCandidateIds();
        lambdaUpdate().set(HrmRecruitCandidate::getStatus, status)
                .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                .in(HrmRecruitCandidate::getCandidateId, candidateIds)
                .update();

        return operationLogs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> updateCandidatePost(UpdateCandidatePostBO updateCandidatePostBO) {
        List<OperationLog> operationLogs = candidateActionRecordService.updateCandidatePostRecord(updateCandidatePostBO);
        lambdaUpdate().set(HrmRecruitCandidate::getPostId, updateCandidatePostBO.getPostId())
                .in(HrmRecruitCandidate::getCandidateId, updateCandidatePostBO.getCandidateIds())
                .update();
        return operationLogs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> updateCandidateRecruitChannel(UpdateCandidateRecruitChannelBO updateCandidateRecruitChannelBO) {
        List<OperationLog> operationLogs = candidateActionRecordService.updateCandidateRecruitChannel(updateCandidateRecruitChannelBO);
        lambdaUpdate().set(HrmRecruitCandidate::getChannelId, updateCandidateRecruitChannelBO.getChannelId())
                .in(HrmRecruitCandidate::getCandidateId, updateCandidateRecruitChannelBO.getCandidateIds())
                .update();
        return operationLogs;
    }

    @Override
    public List<Long> queryCleanCandidateIds(QueryCleanCandidateBO queryCleanCandidateBO) {
        QueryWrapper<HrmRecruitCandidate> wrapper = new QueryWrapper<HrmRecruitCandidate>().select("candidate_id")
                .in("status", queryCleanCandidateBO.getStatus())
                .apply("to_days(now()) - to_days(status_update_time) > {0}", queryCleanCandidateBO.getDay());
        return listObjs(wrapper, Convert::toLong);
    }

    @Override
    public Result<List<FileEntity>> queryFile(Long candidateId) {
        HrmRecruitCandidate candidate = getById(candidateId);
        return Result.ok(adminFileService.queryFileList(candidate.getBatchId()));
    }

    @Override
    public Map<Integer, Integer> queryCandidateStatusNum() {
        TreeMap<Integer, Integer> collect = new TreeMap<>();
        Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
        List<Map<String, Object>> statusMap = getBaseMapper().queryDataAuthCandidateStatus(deptIds);
        for (Map<String, Object> map : statusMap) {
            collect.put(Convert.toInt(map.get("status")), Convert.toInt(map.get("count")));
        }
        for (CandidateStatusEnum value : CandidateStatusEnum.values()) {
            if (!collect.containsKey(value.getValue())) {
                collect.put(value.getValue(), 0);
            }
        }
        return collect;
    }

    @Override
    public List<OperationLog> eliminateCandidate(EliminateCandidateBO eliminateCandidateBO) {
        lambdaUpdate().set(HrmRecruitCandidate::getEliminate, eliminateCandidateBO.getEliminate())
                .set(HrmRecruitCandidate::getRemark, eliminateCandidateBO.getRemarks())
                .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                .set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.OBSOLETE.getValue())
                .in(HrmRecruitCandidate::getCandidateId, eliminateCandidateBO.getCandidateIds())
                .update();
        employeeService.lambdaUpdate().set(HrmEmployee::getIsDel, 1).in(HrmEmployee::getCandidateId, eliminateCandidateBO.getCandidateIds()).update();
        return candidateActionRecordService.eliminateCandidateBORecord(eliminateCandidateBO);
    }

    @Override
    public List<OperationLog> deleteByIds(List<Long> candidateIds) {

        List<OperationLog> operationLogs = new ArrayList<>();
        listByIds(candidateIds).forEach(candidate -> {
            Content content = candidateActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, candidate.getCandidateId());

            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(candidate.getCandidateId(), candidate.getCandidateName());
            operationLog.setOperationInfo(content.getDetail());
            operationLogs.add(operationLog);
        });

        removeByIds(candidateIds);
        recruitInterviewService.lambdaUpdate().in(HrmRecruitInterview::getCandidateId, candidateIds).remove();

        return operationLogs;
    }

    @Override
    public List<Map<String, Object>> queryRecruitListByTime(LocalDate time, Collection<Long> deptIds) {
        return recruitCandidateMapper.queryRecruitListByTime(time, deptIds);
    }

    @Override
    public Set<String> queryRecruitStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> deptIds) {
        return recruitCandidateMapper.queryRecruitStatusList(queryNotesStatusBO, deptIds);
    }
}
