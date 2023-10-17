package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.constant.appraisal.RaterTypeEnum;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmAppraisalPlanProcessSettingMapper;
import com.kakarote.hrm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 考核计划-流程配置-基础表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Service
public class HrmAppraisalPlanProcessSettingServiceImpl extends BaseServiceImpl<HrmAppraisalPlanProcessSettingMapper, HrmAppraisalPlanProcessSetting> implements IHrmAppraisalPlanProcessSettingService {

    @Autowired
    private IHrmQuotaSettingDimensionService quotaSettingDimensionService;

    @Autowired
    private IHrmQuotaSettingDimensionQuotaService quotaSettingDimensionQuotaService;

    @Autowired
    private IHrmProcessSettingScoringService processSettingScoringService;

    @Autowired
    private IHrmProcessSettingResultAuditService processSettingResultAuditService;

    @Autowired
    private IHrmProcessSettingResultConfirmationService processSettingResultConfirmationService;

    @Autowired
    private IHrmProcessSettingTargetConfirmationService targetConfirmationService;

    @Transactional
    @Override
    public void saveAppraisalPlanProcessSetting(AppraisalPlanProcessSettingBO appraisalPlanProcessSettingBO, Long appraisalPlan) {
        HrmAppraisalPlanProcessSetting hrmAppraisalPlanProcessSetting = new HrmAppraisalPlanProcessSetting();
        BeanUtil.copyProperties(appraisalPlanProcessSettingBO, hrmAppraisalPlanProcessSetting);
        hrmAppraisalPlanProcessSetting.setAppraisalPlanId(appraisalPlan);
        saveOrUpdate(hrmAppraisalPlanProcessSetting);
        List<ProcessSettingScoringBO> processSettingScoringBOList = appraisalPlanProcessSettingBO.getProcessSettingScoringBOList();
        List<ProcessSettingResultAuditBO> processSettingResultAuditBOList = appraisalPlanProcessSettingBO.getProcessSettingResultAuditBOList();
        List<ProcessSettingResultConfirmationBO> processSettingResultConfirmationBOList = appraisalPlanProcessSettingBO.getProcessSettingResultConfirmationBOList();

        List<HrmProcessSettingScoring> hrmProcessSettingScoringList = new ArrayList<>();
        processSettingScoringBOList.forEach(
                processSettingScoringBO -> {
                    HrmProcessSettingScoring hrmProcessSettingScoring = new HrmProcessSettingScoring();
                    BeanUtil.copyProperties(processSettingScoringBO, hrmProcessSettingScoring);
                    hrmProcessSettingScoring.setProcessId(hrmAppraisalPlanProcessSetting.getProcessId());
                    hrmProcessSettingScoring.setScoringProcessId(null);
                    hrmProcessSettingScoringList.add(hrmProcessSettingScoring);
                }
        );
        processSettingScoringService.lambdaUpdate().eq(HrmProcessSettingScoring::getProcessId, hrmAppraisalPlanProcessSetting.getProcessId()).remove();
        processSettingScoringService.saveBatch(hrmProcessSettingScoringList);

        if (ObjectUtil.isNotNull(appraisalPlanProcessSettingBO.getResultAudit()) && appraisalPlanProcessSettingBO.getResultAudit()) {
            List<HrmProcessSettingResultAudit> processSettingResultAuditList = new ArrayList<>();
            processSettingResultAuditBOList.forEach(
                    processSettingResultAuditBO -> {
                        HrmProcessSettingResultAudit processSettingResultAudit = new HrmProcessSettingResultAudit();
                        BeanUtil.copyProperties(processSettingResultAuditBO, processSettingResultAudit);
                        processSettingResultAudit.setProcessId(hrmAppraisalPlanProcessSetting.getProcessId());
                        processSettingResultAudit.setResultAuditId(null);
                        processSettingResultAuditList.add(processSettingResultAudit);
                    }
            );
            processSettingResultAuditService.lambdaUpdate().eq(HrmProcessSettingResultAudit::getProcessId, hrmAppraisalPlanProcessSetting.getProcessId()).remove();
            processSettingResultAuditService.saveBatch(processSettingResultAuditList);
        }

        if (ObjectUtil.isNotNull(appraisalPlanProcessSettingBO.getResultConfirmation()) && appraisalPlanProcessSettingBO.getResultConfirmation()) {
            List<HrmProcessSettingResultConfirmation> processSettingResultConfirmationList = new ArrayList<>();
            processSettingResultConfirmationBOList.forEach(
                    processSettingResultConfirmationBO -> {
                        HrmProcessSettingResultConfirmation hrmProcessSettingResultConfirmation = new HrmProcessSettingResultConfirmation();
                        BeanUtil.copyProperties(processSettingResultConfirmationBO, hrmProcessSettingResultConfirmation);
                        hrmProcessSettingResultConfirmation.setProcessId(hrmAppraisalPlanProcessSetting.getProcessId());
                        hrmProcessSettingResultConfirmation.setResultConfirmationId(null);
                        processSettingResultConfirmationList.add(hrmProcessSettingResultConfirmation);
                    }
            );
            processSettingResultConfirmationService.lambdaUpdate().eq(HrmProcessSettingResultConfirmation::getProcessId, hrmAppraisalPlanProcessSetting.getProcessId()).remove();
            processSettingResultConfirmationService.saveBatch(processSettingResultConfirmationList);
        }

        targetConfirmationService.lambdaUpdate().eq(HrmProcessSettingTargetConfirmation::getProcessId, hrmAppraisalPlanProcessSetting.getProcessId()).remove();
        if (ObjectUtil.isNotNull(appraisalPlanProcessSettingBO.getTargetConfirmation()) && appraisalPlanProcessSettingBO.getTargetConfirmation()) {
            HrmProcessSettingTargetConfirmation processSettingTargetConfirmation = new HrmProcessSettingTargetConfirmation();
            ProcessSettingTargetConfirmationBO processSettingTargetConfirmationBO = appraisalPlanProcessSettingBO.getProcessSettingTargetConfirmationBO();
            if (ObjectUtil.isNotNull(processSettingTargetConfirmationBO)) {
                BeanUtil.copyProperties(processSettingTargetConfirmationBO, processSettingTargetConfirmation);
                processSettingTargetConfirmation.setProcessId(hrmAppraisalPlanProcessSetting.getProcessId());
                processSettingTargetConfirmation.setConfirmationId(null);
                targetConfirmationService.save(processSettingTargetConfirmation);
            }
        }

    }

    @Override
    public AppraisalPlanProcessSettingVO queryProcessSettingVO(Long appraisalPlan) {
        //todo 引入层级排序
        HrmAppraisalPlanProcessSetting appraisalPlanProcessSetting = lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlan).one();
        List<HrmProcessSettingScoring> processSettingScoringList = processSettingScoringService.lambdaQuery().eq(HrmProcessSettingScoring::getProcessId, appraisalPlanProcessSetting.getProcessId()).list();
        List<HrmProcessSettingResultAudit> processSettingResultAuditList = processSettingResultAuditService.lambdaQuery().eq(HrmProcessSettingResultAudit::getProcessId, appraisalPlanProcessSetting.getProcessId()).list();//可能为空
        List<HrmProcessSettingResultConfirmation> processSettingResultConfirmationList = processSettingResultConfirmationService.lambdaQuery().eq(HrmProcessSettingResultConfirmation::getProcessId, appraisalPlanProcessSetting.getProcessId()).list();//可能为空
        AppraisalPlanProcessSettingVO appraisalPlanProcessSettingVO = new AppraisalPlanProcessSettingVO();
        BeanUtil.copyProperties(appraisalPlanProcessSetting, appraisalPlanProcessSettingVO);

        List<ProcessSettingScoringVO> processSettingScoringVOList = processSettingScoringList.stream().map(
                processSettingScoring -> {
                    ProcessSettingScoringVO processSettingScoringVO = new ProcessSettingScoringVO();
                    BeanUtil.copyProperties(processSettingScoring, processSettingScoringVO);
                    if (processSettingScoringVO.getRaterType().equals(RaterTypeEnum.SELF.getValue())) {
                        processSettingScoringVO.setRejectAuthority(null);
                    }
                    return processSettingScoringVO;
                }
        ).collect(Collectors.toList());

        List<ProcessSettingResultAuditVO> processSettingResultAuditVOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(processSettingResultAuditList)) {
            processSettingResultAuditVOList = processSettingResultAuditList.stream().map(
                    processSettingResultAudit -> {
                        ProcessSettingResultAuditVO processSettingResultAuditVO = new ProcessSettingResultAuditVO();
                        BeanUtil.copyProperties(processSettingResultAudit, processSettingResultAuditVO);
                        return processSettingResultAuditVO;
                    }
            ).collect(Collectors.toList());
        }

        List<ProcessSettingResultConfirmationVO> processSettingResultConfirmationVOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(processSettingResultConfirmationList)) {
            processSettingResultConfirmationVOList = processSettingResultConfirmationList.stream().map(
                    processSettingResultConfirmation -> {
                        ProcessSettingResultConfirmationVO processSettingResultConfirmationVO = new ProcessSettingResultConfirmationVO();
                        BeanUtil.copyProperties(processSettingResultConfirmation, processSettingResultConfirmationVO);
                        return processSettingResultConfirmationVO;
                    }
            ).collect(Collectors.toList());
        }

        //目前目标确认设置就一级所以只需要查询一个数据即可
        HrmProcessSettingTargetConfirmation targetConfirmation = targetConfirmationService.lambdaQuery().eq(HrmProcessSettingTargetConfirmation::getProcessId, appraisalPlanProcessSetting.getProcessId()).one();
        ProcessSettingTargetConfirmationVO processSettingTargetConfirmationVO = new ProcessSettingTargetConfirmationVO();
        if (ObjectUtil.isNotNull(targetConfirmation)) {
            BeanUtil.copyProperties(targetConfirmation, processSettingTargetConfirmationVO);
        }
        appraisalPlanProcessSettingVO.setProcessSettingTargetConfirmationVO(processSettingTargetConfirmationVO);
        appraisalPlanProcessSettingVO.setProcessSettingScoringVOList(processSettingScoringVOList);
        appraisalPlanProcessSettingVO.setProcessSettingResultAuditVOList(processSettingResultAuditVOList);
        appraisalPlanProcessSettingVO.setProcessSettingResultConfirmationVOList(processSettingResultConfirmationVOList);

        return appraisalPlanProcessSettingVO;
    }

    @Transactional
    @Override
    public void delProcessSetting(Long processId) {
        targetConfirmationService.lambdaUpdate().eq(HrmProcessSettingTargetConfirmation::getProcessId, processId).remove();
        processSettingScoringService.lambdaUpdate().eq(HrmProcessSettingScoring::getProcessId, processId).remove();
        processSettingResultAuditService.lambdaUpdate().eq(HrmProcessSettingResultAudit::getProcessId, processId).remove();
        processSettingResultConfirmationService.lambdaUpdate().eq(HrmProcessSettingResultConfirmation::getProcessId, processId).remove();
        removeById(processId);
    }

}
