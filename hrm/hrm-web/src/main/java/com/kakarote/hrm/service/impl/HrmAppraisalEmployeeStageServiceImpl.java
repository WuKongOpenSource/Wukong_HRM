package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.constant.appraisal.AppraisalStageStatusEnum;
import com.kakarote.hrm.constant.appraisal.ProcessingStatusEnum;
import com.kakarote.hrm.constant.appraisal.QuotaTypeEnum;
import com.kakarote.hrm.constant.appraisal.RaterTypeEnum;
import com.kakarote.hrm.entity.BO.EmployeeAppraisalStageBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.ScoringStageInfoVO;
import com.kakarote.hrm.mapper.HrmAppraisalEmployeeStageMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.utils.AchievementsUtil;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-01
 */
@Service
public class HrmAppraisalEmployeeStageServiceImpl extends BaseServiceImpl<HrmAppraisalEmployeeStageMapper, HrmAppraisalEmployeeStage> implements IHrmAppraisalEmployeeStageService {

    @Autowired
    IHrmAppraisalPlanService appraisalPlanService;

    @Autowired
    IHrmAppraisalEmployeeService appraisalEmployeeService;

    @Autowired
    IHrmAppraisalPlanProcessSettingService planProcessSettingService;

    @Autowired
    IHrmProcessSettingResultAuditService processSettingResultAuditService;

    @Autowired
    IHrmProcessSettingScoringService processSettingScoringService;

    @Autowired
    IHrmProcessSettingResultConfirmationService processSettingResultConfirmationService;

    @Autowired
    IHrmProcessSettingTargetConfirmationService processSettingTargetConfirmationService;

    @Autowired
    IHrmAppraisalEmployeeAppealRecordService appraisalEmployeeAppealRecordService;

    @Autowired
    private AchievementsUtil achievementsUtil;

    @Autowired
    private IAdminMessageService adminMessageService;

    @Autowired
    private IHrmAppraisalAppealPendingRecordService appraisalAppealPendingRecordService;

    @Autowired
    private IHrmAppraisalEmployeeRealScoreUserService realScoreUserService;

    @Autowired
    private IHrmAppraisalEmployeeScoreService appraisalEmployeeScoreService;

    @Autowired
    private IHrmAppraisalPlanRelationEmployeeService planRelationEmployeeService;

    @Override
    public List<HrmAppraisalEmployeeStage> queryAppraisalEmployeeStageList(Long appraisalEmployeePlanId) {
        boolean exists = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployeePlanId).exists();
        //如果流程已存在则直接返回
        if (exists) {
            List<HrmAppraisalEmployeeStage> appraisalEmployeeStages = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployeePlanId).orderByAsc(HrmAppraisalEmployeeStage::getSort).list();
            return appraisalEmployeeStages;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 添加考核流程
     */
    @Transactional
    @Override
    public void addEmployeeAppraisalProcess(Long appraisalPlanId, Long employeeId) {
        //查询考核计划所有阶段
        //查询考核计划流程设置
        HrmAppraisalPlanProcessSetting planProcessSetting = planProcessSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlanId).one();
        AtomicReference<Integer> sort = new AtomicReference<>(1);
        List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = new ArrayList<>();

        HrmAppraisalEmployeeStage emptyStage = new HrmAppraisalEmployeeStage();
        emptyStage.setAppraisalPlanId(appraisalPlanId);
        emptyStage.setEmployeeId(employeeId);

        //如果指标类型是员工填写类型且维度设置允许员工填写则需要添加目标填写都到流程中
        if (planProcessSetting.getQuotaSettingType().equals(QuotaTypeEnum.EMPLOYEE_FILL.getValue())) {
            HrmAppraisalEmployeeStage employeeFillStage = new HrmAppraisalEmployeeStage();
            employeeFillStage.setStageName(AppraisalStageStatusEnum.FILL.getName());
            employeeFillStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
            employeeFillStage.setStageUserId(employeeId);
            employeeFillStage.setSort(sort.getAndSet(sort.get() + 1));
            employeeFillStage.setAppraisalPlanId(appraisalPlanId);
            employeeFillStage.setEmployeeId(employeeId);
            employeeFillStage.setStageType(AppraisalStageStatusEnum.FILL.getValue());
            appraisalEmployeeStageList.add(employeeFillStage);
        }

        //如果开启了目标确认流程则添加
        Boolean isHasTargetConfirmation = planProcessSetting.getTargetConfirmation();
        List<HrmAppraisalEmployeeStage> targetConfirmationStageList = new ArrayList<>();
        if (isHasTargetConfirmation) {
            //目标确认流程设置
            List<HrmProcessSettingTargetConfirmation> processSettingTargetConfirmationList = processSettingTargetConfirmationService.lambdaQuery().eq(HrmProcessSettingTargetConfirmation::getProcessId, planProcessSetting.getProcessId())
                    .orderByAsc(HrmProcessSettingTargetConfirmation::getSort).list();
            processSettingTargetConfirmationList.forEach(
                    processSettingTargetConfirmation -> {
                        HrmAppraisalEmployeeStage targetConfirmationStage = new HrmAppraisalEmployeeStage();
                        targetConfirmationStage.setStageType(AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue());
                        targetConfirmationStage.setStageName(AppraisalStageStatusEnum.TARGET_CONFIRMATION.getName());
                        targetConfirmationStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
                        //获取层级处理人，这个层级处理人可能不存在
                        Long stageUserId = achievementsUtil.queryStageUser(appraisalPlanId, processSettingTargetConfirmation.getConfirmationType(), processSettingTargetConfirmation.getConfirmationLevel(), processSettingTargetConfirmation.getIdentifyingPeople(), employeeId);
                        targetConfirmationStage.setStageUserId(stageUserId);
                        targetConfirmationStage.setSort(sort.getAndSet(sort.get() + 1));
                        targetConfirmationStage.setEmployeeId(employeeId);
                        targetConfirmationStage.setAppraisalPlanId(appraisalPlanId);
                        targetConfirmationStageList.add(targetConfirmationStage);
                    }
            );
            if (CollectionUtil.isNotEmpty(targetConfirmationStageList)) {
                appraisalEmployeeStageList.addAll(targetConfirmationStageList);
            } else {
                emptyStage.setStageType(AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue());
                emptyStage.setStageName(AppraisalStageStatusEnum.TARGET_CONFIRMATION.getName());
                appraisalEmployeeStageList.add(emptyStage);
            }
        }


        //对于含有员工填写的阶段则一定含有执行中状态，员工填写是在月初，评分在月末，所以员工填写完毕后有执行中这个状态-如果有目标确认则需要将该阶段放在目标确认后面
        //修改逻辑将执行中阶段添加到流程阶段中不分情况
        HrmAppraisalEmployeeStage executingStage = new HrmAppraisalEmployeeStage();
        executingStage.setStageType(AppraisalStageStatusEnum.EXECUTING.getValue());
        executingStage.setStageName(AppraisalStageStatusEnum.EXECUTING.getName());
        executingStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
        executingStage.setStageUserId(employeeId);
        executingStage.setSort(sort.getAndSet(sort.get() + 1));
        executingStage.setAppraisalPlanId(appraisalPlanId);
        executingStage.setEmployeeId(employeeId);
        appraisalEmployeeStageList.add(executingStage);


        //评分流程设置
        List<HrmAppraisalEmployeeRealScoreUser> realScoreUserList = new ArrayList<>();
        List<HrmAppraisalEmployeeStage> scoringStageList = new ArrayList<>();
        List<HrmProcessSettingScoring> processSettingScoringList = processSettingScoringService.lambdaQuery().eq(HrmProcessSettingScoring::getProcessId, planProcessSetting.getProcessId())
                .orderByAsc(HrmProcessSettingScoring::getSort).list();
        AtomicReference<Double> curWeight = new AtomicReference<>(new Double(0l));
        processSettingScoringList.forEach(
                processSettingScoring -> {
                    Long stageUserId = achievementsUtil.queryStageUser(appraisalPlanId, processSettingScoring.getRaterType(), processSettingScoring.getRaterLevel(), processSettingScoring.getRater(), employeeId);
                    HrmAppraisalEmployeeStage scoringStage = new HrmAppraisalEmployeeStage();
                    //如果还未添加任何节点，则将权重累加直到有第一个节点出现的时候进行赋值并清零
                    if (ObjectUtil.isNotNull(stageUserId)) {
                        Boolean isExists = checkIsExists(scoringStageList, stageUserId, processSettingScoring.getWeight());
                        if (!isExists) {//不存在的时候进行添加，如果已经存在则在上一步判断逻辑就已经进行处理了
                            if (processSettingScoring.getRaterType().equals(RaterTypeEnum.SELF.getValue())) {
                                scoringStage.setStageType(AppraisalStageStatusEnum.SELF_SCORE.getValue());
                                scoringStage.setStageName(AppraisalStageStatusEnum.SELF_SCORE.getName());
                            } else {
                                scoringStage.setStageType(AppraisalStageStatusEnum.OTHER_SCORE.getValue());
                                scoringStage.setStageName(AppraisalStageStatusEnum.OTHER_SCORE.getName());
                            }
                            scoringStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
                            scoringStage.setStageUserId(stageUserId);
                            int curSort = sort.get();
                            scoringStage.setSort(sort.getAndSet(sort.get() + 1));
                            scoringStage.setEmployeeId(employeeId);
                            scoringStage.setAppraisalPlanId(appraisalPlanId);

                            Double curStageWeight = processSettingScoring.getWeight();
                            if (curWeight.get() > 0) {
                                curStageWeight += curWeight.get();
                                curWeight.set(0d);//使用后进行清零
                            }
                            scoringStage.setWeight(curStageWeight);
                            scoringStageList.add(scoringStage);

                            //保存实际评分人列表
                            HrmAppraisalEmployeeRealScoreUser appraisalEmployeeRealScoreUser = new HrmAppraisalEmployeeRealScoreUser();
                            appraisalEmployeeRealScoreUser.setScoreUserId(stageUserId);
                            appraisalEmployeeRealScoreUser.setRejectAuthority(processSettingScoring.getRejectAuthority());
                            appraisalEmployeeRealScoreUser.setRequiredSetting(processSettingScoring.getRequiredSetting());
                            appraisalEmployeeRealScoreUser.setAppraisalPlanId(appraisalPlanId);
                            appraisalEmployeeRealScoreUser.setEmployeeId(employeeId);
                            appraisalEmployeeRealScoreUser.setStageSort(curSort);
                            appraisalEmployeeRealScoreUser.setVisibleContent(processSettingScoring.getVisibleContent());//设置当前阶段人的可见内容权限设置-这个不用进行权限叠加,就以能找到的第一个员工进行设置
                            realScoreUserList.add(appraisalEmployeeRealScoreUser);
                        }
                    } else {
                        if (CollectionUtil.isEmpty(scoringStageList)) {
                            curWeight.updateAndGet(v -> v + processSettingScoring.getWeight());
                        } else {
                            HrmAppraisalEmployeeStage scoreStage = scoringStageList.get(scoringStageList.size() - 1);
                            scoreStage.setWeight(scoreStage.getWeight() + processSettingScoring.getWeight());
                        }
                    }

                }
        );
        if (CollectionUtil.isNotEmpty(scoringStageList)) {
            appraisalEmployeeStageList.addAll(scoringStageList);
        } else {
            emptyStage.setSort(sort.getAndSet(sort.get() + 1));
            emptyStage.setStageType(AppraisalStageStatusEnum.SELF_SCORE.getValue());
            emptyStage.setStageName(AppraisalStageStatusEnum.SELF_SCORE.getName());
            appraisalEmployeeStageList.add(emptyStage);
        }
        //结果审核流程设置
        List<HrmAppraisalEmployeeStage> resultAuditStageList = new ArrayList<>();
        Boolean isOpenResultAudit = planProcessSetting.getResultAudit();
        if (isOpenResultAudit) {
            List<HrmProcessSettingResultAudit> processSettingResultAuditList = processSettingResultAuditService.lambdaQuery().eq(HrmProcessSettingResultAudit::getProcessId, planProcessSetting.getProcessId())
                    .orderByAsc(HrmProcessSettingResultAudit::getSort).list();
            processSettingResultAuditList.forEach(
                    resultAudit -> {
                        Long stageUserId = achievementsUtil.queryStageUser(appraisalPlanId, resultAudit.getAuditType(), resultAudit.getLevel(), resultAudit.getDesignatedPerson(), employeeId);
                        if (ObjectUtil.isNotNull(stageUserId)) {
                            Boolean isExists = checkIsExists(resultAuditStageList, stageUserId, null);
                            if (!isExists) {
                                HrmAppraisalEmployeeStage resultAuditStage = new HrmAppraisalEmployeeStage();
                                resultAuditStage.setStageType(AppraisalStageStatusEnum.RESULT_AUDIT.getValue());
                                resultAuditStage.setStageName(AppraisalStageStatusEnum.RESULT_AUDIT.getName());
                                resultAuditStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
                                resultAuditStage.setStageUserId(stageUserId);
                                resultAuditStage.setSort(sort.getAndSet(sort.get() + 1));
                                resultAuditStage.setEmployeeId(employeeId);
                                resultAuditStage.setAppraisalPlanId(appraisalPlanId);
                                resultAuditStageList.add(resultAuditStage);
                            }
                        }

                    }
            );
            if (CollectionUtil.isNotEmpty(resultAuditStageList)) {
                appraisalEmployeeStageList.addAll(resultAuditStageList);
            } else {
                emptyStage.setSort(sort.getAndSet(sort.get() + 1));
                emptyStage.setStageType(AppraisalStageStatusEnum.RESULT_AUDIT.getValue());
                emptyStage.setStageName(AppraisalStageStatusEnum.RESULT_AUDIT.getName());
                appraisalEmployeeStageList.add(emptyStage);
            }
        }

        //结果确认流程设置
        Boolean isOpenResultConfirmation = planProcessSetting.getResultConfirmation();
        if (isOpenResultConfirmation) {
            //如果开启了结果确认则将当前被考核人设置为结果确认人

            HrmAppraisalEmployeeStage resultAuditStage = new HrmAppraisalEmployeeStage();
            resultAuditStage.setStageType(AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue());
            resultAuditStage.setStageName(AppraisalStageStatusEnum.RESULT_CONFIRMATION.getName());
            resultAuditStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
            resultAuditStage.setStageUserId(employeeId);//设置结果确认人为当前被考核人
            resultAuditStage.setSort(sort.getAndSet(sort.get() + 1));
            resultAuditStage.setEmployeeId(employeeId);
            resultAuditStage.setAppraisalPlanId(appraisalPlanId);
            appraisalEmployeeStageList.add(resultAuditStage);

            //将申诉确认人列表添加到考核阶段中
            List<HrmAppraisalEmployeeStage> appealConfirmationStageList = new ArrayList<>();
            List<HrmProcessSettingResultConfirmation> processSettingResultConfirmationList = processSettingResultConfirmationService.lambdaQuery().eq(HrmProcessSettingResultConfirmation::getProcessId, planProcessSetting.getProcessId())
                    .orderByAsc(HrmProcessSettingResultConfirmation::getSort).list();
            processSettingResultConfirmationList.forEach(
                    resultConfirmation -> {
                        //todo 获取当前阶段评分人
                        Long stageUserId = achievementsUtil.queryStageUser(appraisalPlanId, resultConfirmation.getConfirmationType(), resultConfirmation.getLevel(), resultConfirmation.getDesignatedUserId(), employeeId);
                        if (ObjectUtil.isNotNull(stageUserId)) {//如果当前节点的处理人为不为空就添加-在添加的时候要进行判断看是否要进行合并
                            Boolean isExists = checkIsExists(appealConfirmationStageList, stageUserId, null);
                            if (!isExists) {
                                HrmAppraisalEmployeeStage appealConfirmationStage = new HrmAppraisalEmployeeStage();
                                appealConfirmationStage.setStageType(AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue());
                                appealConfirmationStage.setStageName(AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getName());
                                appealConfirmationStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
                                appealConfirmationStage.setStageUserId(stageUserId);
                                appealConfirmationStage.setSort(sort.getAndSet(sort.get() + 1));
                                appealConfirmationStage.setEmployeeId(employeeId);
                                appealConfirmationStage.setAppraisalPlanId(appraisalPlanId);
                                appealConfirmationStageList.add(appealConfirmationStage);
                            }
                        }
                    }
            );
            if (CollectionUtil.isNotEmpty(appealConfirmationStageList)) {
                appraisalEmployeeStageList.addAll(appealConfirmationStageList);
            } else {
                emptyStage.setSort(sort.getAndSet(sort.get() + 1));
                emptyStage.setStageType(AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue());
                emptyStage.setStageName(AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getName());
                appraisalEmployeeStageList.add(emptyStage);
            }
        }

        //通用结束阶段添加
        HrmAppraisalEmployeeStage endStage = new HrmAppraisalEmployeeStage();
        endStage.setStageType(AppraisalStageStatusEnum.END.getValue());
        endStage.setStageName(AppraisalStageStatusEnum.END.getName());
        endStage.setStatus(ProcessingStatusEnum.NOT_PROCESSED.getValue());
        endStage.setStageUserId(employeeId);
        endStage.setSort(sort.getAndSet(sort.get() + 1));
        endStage.setAppraisalPlanId(appraisalPlanId);
        endStage.setEmployeeId(employeeId);
        appraisalEmployeeStageList.add(endStage);

        //end
        saveBatch(appraisalEmployeeStageList);
        if (CollectionUtil.isNotEmpty(realScoreUserList)) {
            realScoreUserService.lambdaUpdate().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeRealScoreUser::getEmployeeId, employeeId).remove();
            realScoreUserService.saveBatch(realScoreUserList);
        }
    }


    private Boolean checkIsExists(List<HrmAppraisalEmployeeStage> stageList, Long curStageUserId, Double curWeight) {
        AtomicReference<Boolean> isExists = new AtomicReference<>(false);
        if (CollectionUtil.isNotEmpty(stageList)) {
            stageList.forEach(
                    stage -> {
                        if (stage.getStageUserId().equals(curStageUserId)) {
                            isExists.set(true);
                            //如果是评分阶段则如果当前阶段处理人已经存在则将当前处理人权重合并至已存在的节点进行处理
                            if ((stage.getStageType().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue())) || (stage.getStageType().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue()))) {
                                Double stageWeight = stage.getWeight();
                                stage.setWeight(stageWeight + curWeight);
                            }
                        }
                    }
            );
        }
        return isExists.get();
    }


    @Override
    public List<HrmAppraisalEmployeeStage> queryStageListByIds(List<Long> stageIdList) {
        return baseMapper.queryStageListByIds(stageIdList);
    }

    @Override
    public List<ScoringStageInfoVO> queryScoringStage(Long appraisalEmployeeId) {
        List<ScoringStageInfoVO> scoringStageInfoVOList = baseMapper.queryStageByStageType(appraisalEmployeeId, Arrays.asList(AppraisalStageStatusEnum.SELF_SCORE.getValue(), AppraisalStageStatusEnum.OTHER_SCORE.getValue()));
        return scoringStageInfoVOList;
    }

    @Override
    public List<HrmAppraisalEmployeeStage> queryMaxCountStageData(Long appraisalPlanId) {
        List<HrmAppraisalEmployeeStage> stageList = baseMapper.queryMaxCountStageData(appraisalPlanId);
        return stageList;
    }

    @Override
    public List<HrmAppraisalEmployeeStage> queryStageByEmployeeIds(Long appraisalEmployeeId, List<Long> employeeList) {
        return baseMapper.queryStageByEmployeeIds(appraisalEmployeeId, employeeList);
    }

    @Override
    public void toInterviewStage(Long appraisalPlanId) {
        List<HrmAppraisalPlanRelationEmployee> appraisalPlanRelationEmployeeList = planRelationEmployeeService.lambdaQuery().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).list();
        appraisalPlanRelationEmployeeList.stream().forEach(
                employeeInfo -> {
                    //获取结果确认阶段信息
                    HrmAppraisalEmployeeStage employeeStage = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeInfo.getEmployeeId())
                            .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
                    if (ObjectUtil.isNotNull(employeeStage)) {
                        //开启绩效面谈的时候结果确认的待处理人才能更新为待处理状态;为了控制开启绩效面谈之前不让这个阶段的处理人在待处理列表中查看到
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                                .eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeStage.getAppraisalStageId()).update();
                        //更新所有员工考核流程进入到绩效面谈阶段（结果确认）->这一步逻辑进行了变更,结果确认阶段会在结果审核后自动进行推进不需要开启绩效面谈时再推进
//                      appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId,employeeInfo.getEmployeeId(),employeeStage.getSort());
                    }
                }
        );
    }

    @Override
    public Integer queryEmployeePendingStageNum(List<Integer> stageType, Long employeeId) {
        return lambdaQuery().eq(HrmAppraisalEmployeeStage::getStageUserId, employeeId).eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).in(HrmAppraisalEmployeeStage::getStageType, stageType).count().intValue();
    }

    @Override
    public Map<String, Integer> queryEmployeePendingStageNumOfAll(Long employeeId) {
        return baseMapper.queryEmployeePendingStageNumOfAll(employeeId);
    }

    @Override
    public Map<String, Object> queryStageUserNameByStageSort(Long appraisalEmployeeId, Integer stageSort) {
        return baseMapper.queryStageUserNameBySort(appraisalEmployeeId, stageSort);
    }

    @Override
    public HrmAppraisalEmployeeStage queryAppraisalStageInfoBySort(Long AppraisalPlanId, Long employeeId, Integer sort) {
        return baseMapper.queryAppraisalStageInfoBySort(AppraisalPlanId, employeeId, sort);
    }

    @Override
    public HrmAppraisalEmployeeStage queryAppraisalStageInfoByStageType(Long AppraisalPlanId, Long employeeId, Integer stageType) {
        return baseMapper.queryAppraisalStageInfoByStageType(AppraisalPlanId, employeeId, stageType);
    }

    @Transactional
    @Override
    public Boolean updateStageStatusPass(Long appraisalPlanId, Long employeeId, AppraisalStageStatusEnum appraisalStageStatusEnum) {
        Boolean isReScoring = false;//是否结果确认驳回重新评分
        Boolean canAutoNextConfirmation = false;//可以直接进入结果确认 - 评分后的结果审核不需要按钮开启所以不做判断
        //查询当前待处理节点
        //查询员工当前需要处理的绩效考核阶段最前面的一个待处理节点 由于审核流是顺序的，且重新评分的时候优先处理前面被驳回的节点
        HrmAppraisalEmployeeStage appraisalEmployeeStage = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                .eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId).eq(HrmAppraisalEmployeeStage::getStageType, appraisalStageStatusEnum.getValue())
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                .orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        Integer rejectStageOfAuditCount = 0;
        Integer rejectStageOfConfirmationCount = 0;
        Boolean hasOtherScoringStage = false;
        //只有评分阶段会设置这些值
        if (appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue()) || appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue())) {
            //对于结果审核只需要被驳回的节点重新评分即可，
            rejectStageOfAuditCount = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId)
                    .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_AUDIT.getValue()).eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.REJECT.getValue()).count().intValue();
            //对于结果确认需要被驳回的节点重新评分即可，
            rejectStageOfConfirmationCount = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId)
                    .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()).eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.APPEALED.getValue()).count().intValue();
            if (rejectStageOfAuditCount > 0 || rejectStageOfConfirmationCount > 0) {//如果结果审核和结果确认有驳回状态则需要重新评分
                isReScoring = true;
            }
            //查询是否还有其他被驳回的节点未进行评分-处理当前员工正在处理的这个节点
            Integer hasOtherPendingScoreStage = lambdaQuery().ne(HrmAppraisalEmployeeStage::getAppraisalStageId, appraisalEmployeeStage.getAppraisalStageId()).eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                    .eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId).and(item -> item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue()))
                    .and(item -> item.eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).or().eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.NOT_PROCESSED.getValue())).count().intValue();

            if (isReScoring) {
                /**如果是重新评分
                 * 如果一次被驳回两个评分节点，则重新评完分之后要查询下是否还有其他待评分节点未进行评分如果没有则进行阶段推进
                 * 1：查询除了当前节点是否还有其他待处理的节点
                 */
                if (ObjectUtil.isNull(hasOtherPendingScoreStage) || hasOtherPendingScoreStage < 1) {//如果不存在未处理的评分
                    //如果在重新评分阶段不存在其他未处理的节点，则可以进行阶段推进
                    if (rejectStageOfConfirmationCount > 0) {
                        canAutoNextConfirmation = true;
                    }
                }
            }
            //查询评分阶段是否还有其他未评分节点
            if (hasOtherPendingScoreStage > 0) {
                hasOtherScoringStage = true;
            }
        }
        //获取下一个待处理的节点（包括未处理节点和驳回的节点，因为含有驳回阶段的也要重新去处理）--由于按正常流程阶段走的时候下一阶段还是未处理阶段所以要在此处添加未处理的查询状态
        HrmAppraisalEmployeeStage employeeNextStage = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                .eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId).gt(HrmAppraisalEmployeeStage::getSort, appraisalEmployeeStage.getSort())
                .and(item -> item.eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.NOT_PROCESSED.getValue()).or().eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                        .or().eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.REJECT.getValue()).or().eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.APPEALED.getValue())).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        Boolean isCurStageEnd = false;//判断当前节点是不是当前阶段最后一个节点
        if (ObjectUtil.isNull(employeeNextStage)) {
            isCurStageEnd = true;
        } else if (!appraisalEmployeeStage.getStageType().equals(employeeNextStage.getStageType())) {//如果下一阶段不为空且下一阶段的类型和当前阶段类型不一样则设置当前阶段是否是最后阶段为true
            isCurStageEnd = true;
        }
        Boolean isAutoOpenNextStage = true;//是否自动推进到下一个大阶段 比如评分阶段只能通过手动开启 不能 进行阶段自动推进  注：由于在阶段流程种加入了执行中状态则不需要考虑下一阶段是评分阶段而进行控制
        //如果当前节点是当前阶段最后一个阶段则校验是否可以进行自动推进
        //如果是评分节点没有其他待评分的时候当前节点才算是当前阶段的最后一个节点
        if (appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue()) || appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue())) {
            if (hasOtherScoringStage && isReScoring) {//如果还有其他未进行评分的节点,且是重新评分则不能进行阶段推进,必须等所有节点全部评分完才进行推进
                isCurStageEnd = false;
                isAutoOpenNextStage = false;
            } else {
                isAutoOpenNextStage = true;
            }
        }
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalPlanId);
        Boolean isRejectStagePass = false;
        if (isCurStageEnd) {
            if (ObjectUtil.isNull(employeeNextStage)) {
                isAutoOpenNextStage = false;
            } else {
                AppraisalStageStatusEnum curStageTypeEnum = AppraisalStageStatusEnum.parse(appraisalEmployeeStage.getStageType());
                switch (curStageTypeEnum) {
                    case APPEAL_CONFIRMATION:
                        //如果当前阶段是申诉确认通过阶段的最后一个节点,则需要进行阶段回撤
                        isAutoOpenNextStage = false;//首先不能进行阶段推进
                        //进行阶段回撤
                        HrmAppraisalEmployee appraisalEmployee = appraisalEmployeeService.lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployee::getEmployeeId, employeeId).one();
                        //查询驳回的节点
                        List<HrmAppraisalEmployeeAppealRecord> appraisalEmployeeAppealRecords = appraisalEmployeeAppealRecordService.lambdaQuery().eq(HrmAppraisalEmployeeAppealRecord::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).list();
                        List<Long> stageIds = appraisalEmployeeAppealRecords.stream().map(HrmAppraisalEmployeeAppealRecord::getAppraisalStageId).collect(Collectors.toList());
                        try {
                            List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = lambdaQuery().in(HrmAppraisalEmployeeStage::getAppraisalStageId, stageIds).list();
                            appraisalEmployeeStageList.forEach(
                                    appraisalEmployeeStageInfo -> {
                                        Integer messageType = achievementsUtil.queryMessageType(appraisalEmployeeStageInfo.getStageType(), ProcessingStatusEnum.PENDING.getValue());
                                        sendMessage(UserUtil.getUserId(), appraisalEmployeeStageInfo.getStageUserId(), appraisalEmployeeStageInfo.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
                                    }
                            );
                        } catch (Exception e) {
                            log.error("发送消息出错：{}", e.getMessage());
                        }
                        //更新对应的评分节点为待处理
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).in(HrmAppraisalEmployeeStage::getAppraisalStageId, stageIds).update();
                        //更新申诉记录表中被驳回的记录状态为已处理
                        appraisalEmployeeAppealRecordService.lambdaUpdate().set(HrmAppraisalEmployeeAppealRecord::getStatus, 1).in(HrmAppraisalEmployeeAppealRecord::getAppraisalStageId, stageIds).update();
                        /**
                         * 这里添加驳回标志位-用于判断是否需要重走流程
                         */
                        HrmAppraisalEmployeeStage firstScoreStage = lambdaQuery().in(HrmAppraisalEmployeeStage::getAppraisalStageId, stageIds).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
                        HrmAppraisalEmployeeStage lastScoreStage = lambdaQuery().and(item ->
                                item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue())
                        ).eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).orderByDesc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
                        //更新处理结果确认其他大于最后评分节点的状态数据，因为结果确认是驳回状态用于判断是否进行阶段推进的标识所以不能修改
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.NOT_PROCESSED.getValue()).ne(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, lastScoreStage.getAppraisalEmployeeId()).gt(HrmAppraisalEmployeeStage::getSort, lastScoreStage.getSort()).update();
                        //由于是结果确认申诉被通过因此需要重走流程(评分流程只需要被驳回的人重新评分)
                        appraisalEmployeeService.updateEmployeeStageStatus(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), firstScoreStage.getSort(), null, null, true);
                        //将对对应的评分结果设置为空
                        appraisalEmployeeScoreService.rejectScoreByStageId(appraisalEmployee.getAppraisalEmployeeId(), stageIds);
                        isRejectStagePass = true;
                        break;
                    case RESULT_CONFIRMATION:
                        isAutoOpenNextStage = true;
                        break;
                    default:
                        isAutoOpenNextStage = true;
                        break;
                }
            }
        }
        if (!isRejectStagePass) {//在结果申诉通过的时候已经将当前阶段的处理状态设置过了，此处不能进行覆盖设置
            //更新当前阶段状态为已处理
            lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, appraisalEmployeeStage.getAppraisalStageId()).update();
            //如果重新评分的人又进行驳回了则重新评人的阶段状态会变为未处理
            if (isReScoring && hasOtherScoringStage && ((appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue())) || (appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue())))) {
                lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeNextStage.getAppraisalStageId()).update();
                Integer messageType = achievementsUtil.queryMessageType(employeeNextStage.getStageType(), ProcessingStatusEnum.PENDING.getValue());
                if (ObjectUtil.isNotNull(messageType)) {//只处理已有的消息类型
                    sendMessage(UserUtil.getUserId(), employeeNextStage.getStageUserId(), employeeNextStage.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
                }
            }
        }
        //如果当前处理人不是阶段处理人则发送已处理消息通知
        if (!EmployeeHolder.getEmployeeId().equals(appraisalEmployeeStage.getEmployeeId())) {
            Integer messageType = achievementsUtil.queryMessageType(appraisalEmployeeStage.getStageType(), ProcessingStatusEnum.PROCESSED.getValue());
            if (ObjectUtil.isNotNull(messageType)) {
                sendMessage(UserUtil.getUserId(), appraisalEmployeeStage.getEmployeeId(), appraisalEmployeeStage.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
            }
        }
        Boolean canSendMsg = true;
        //查询下一阶段信息
        //如果下一阶段不为空且可以自动推进则直接执行-上面一级判断过如果下一阶段为空不进行阶段推进操作
        if (isAutoOpenNextStage) {
            AppraisalStageStatusEnum nextStageEnum = AppraisalStageStatusEnum.parse(employeeNextStage.getStageType());
            switch (nextStageEnum) {
                case END:
                    lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeNextStage.getAppraisalStageId()).update();
                    //更新员工考核计划主表阶段状态
                    appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId, employeeId, employeeNextStage.getSort());
                    break;
                case APPEAL_CONFIRMATION:
                    //如果有申诉确认阶段则必有结果确认，因为开启了结果确认才有申诉确认，且申诉确认人是当前被考核人员
                    //如果下一阶段是申诉确认，<"且">当前阶段是结果确认通过了，则直接将后面的都设置为通过
                    if (appraisalEmployeeStage.getStageType().equals(AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue())) {
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue())
                                .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployeeStage.getAppraisalPlanId())
                                .eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployeeStage.getEmployeeId()).ge(HrmAppraisalEmployeeStage::getSort, appraisalEmployeeStage.getSort()).update();
                        HrmAppraisalEmployeeStage endStageInfo = queryAppraisalStageInfoByStageType(appraisalEmployeeStage.getAppraisalPlanId(), appraisalEmployeeStage.getEmployeeId(), AppraisalStageStatusEnum.END.getValue());
                        appraisalEmployeeService.updateEmployeeStageStatus(endStageInfo.getAppraisalPlanId(), endStageInfo.getEmployeeId(), endStageInfo.getSort());
                        //由于结果确认已通过且将后面所有流程都自动设置为已通过，则不用再发送待处理信息
                        canSendMsg = false;
                    } else {
                        //下一个待处理阶段是申诉确认则添待处理记录-用于逾期未审核任务处理
                        appraisalAppealPendingRecordService.addAppealPendingRecord(employeeNextStage.getAppraisalEmployeeId(), employeeNextStage.getAppraisalStageId());
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeNextStage.getAppraisalStageId()).update();
                        //更新员工考核计划主表阶段状态
                        appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId, employeeId, employeeNextStage.getSort());
                    }
                    break;
                case RESULT_CONFIRMATION:
                    if (ProcessingStatusEnum.NOT_PROCESSED.getValue().equals(employeeNextStage.getStatus())) {
                        canSendMsg = false;//结果确认通知在发起绩效面谈的时候统一提醒:
                    }
                    //如果下一阶段是结果确认
                    //对于结果确认需要被驳回的节点重新评分即可，
                    rejectStageOfConfirmationCount = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, employeeId)
                            .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()).eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.APPEALED.getValue()).count().intValue();
                    if (rejectStageOfConfirmationCount > 0) {
                        canAutoNextConfirmation = true;
                    }
                    if (canAutoNextConfirmation) {//如果是重新进行处理了说了绩效面谈已经开启过了,否则只更员工考核总阶段信息,但是待处理阶段节点状态暂不更新
                        lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeNextStage.getAppraisalStageId()).update();
                    }
                    appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId, employeeId, employeeNextStage.getSort());
                    break;
                default:
                    lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, employeeNextStage.getAppraisalStageId()).update();
                    //更新员工考核计划主表阶段状态
                    appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId, employeeId, employeeNextStage.getSort());
                    break;
            }
            Integer messageType = achievementsUtil.queryMessageType(employeeNextStage.getStageType(), ProcessingStatusEnum.PENDING.getValue());
            if (canSendMsg && ObjectUtil.isNotNull(messageType)) {//只处理已有的消息类型
                sendMessage(UserUtil.getUserId(), employeeNextStage.getStageUserId(), employeeNextStage.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
            }
        }
        //如果还有其他未进行评分的节点则不能进行总分计算
        if (hasOtherScoringStage && ((appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue())) || (appraisalStageStatusEnum.getValue().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue())))) {
            isCurStageEnd = false;
        }
        return isCurStageEnd;
    }

    /**
     * 1：评分
     * 2：结果审核
     * 2.1：结果审核通过
     * 2.2 结果审核驳回
     * 3：结果确认
     * 3.1结果确认申诉
     * 3.1.1  进入申诉流程
     * 3.2结果确认通过 - 归档
     * 该方法只针对结果确认申诉成功的驳回 和 结果审核
     * 1常规驳回，需要重走流程（用于结果确认驳回阶段） 2：只驳回特定评分节点，不需要重走之后的所有流程（用于结果审核驳回阶段）
     */


    @Override
    public List<EmployeeAppraisalStageBO> queryAppraisalEmployeeStage(Long appraisalEmployeeId, Integer status) {
        List<EmployeeAppraisalStageBO> employeeAppraisalStageBOList = baseMapper.queryStageInfo(appraisalEmployeeId, status);
        return employeeAppraisalStageBOList;
    }

    @Override
    public Boolean isEndScore(Long appraisalEmployeeId, Long employeeId) {

        List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployeeId).and(item -> {
            item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue());
        }).list();
        Boolean isEnd = false;
        Boolean hasCurEmployee = false;
        int i = 0;
        if (CollectionUtil.isNotEmpty(appraisalEmployeeStageList)) {
            for (HrmAppraisalEmployeeStage appraisalEmployeeStage : appraisalEmployeeStageList) {
                if (appraisalEmployeeStage.getStageUserId().equals(employeeId)) {
                    if (appraisalEmployeeStage.getStatus().equals(ProcessingStatusEnum.PENDING.getValue())) {
                        hasCurEmployee = true;
                    }
                } else {
                    if (appraisalEmployeeStage.getStatus().equals(ProcessingStatusEnum.PENDING.getValue()) || appraisalEmployeeStage.getStatus().equals(ProcessingStatusEnum.NOT_PROCESSED.getValue())) {
                        i++;
                    }
                }
            }
        }
        if (hasCurEmployee && i == 0) {
            isEnd = true;
        }
        return isEnd;
    }

    public void sendMessage(Long userId, Long employeeId, Long typeId, Integer type, String title) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateUser(userId);
        adminMessage.setCreateTime(LocalDateTime.now());
        adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employeeId));
        adminMessage.setTypeId(typeId);
        adminMessage.setLabel(8);//8标示人资模块消息
        adminMessage.setType(type);
        adminMessage.setTitle(title);
        adminMessageService.save(adminMessage);
    }

}
