package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.AchievementsCommon;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.entity.BO.AchievementsDimensionBO;
import com.kakarote.hrm.entity.BO.AchievementsTemplateBO;
import com.kakarote.hrm.entity.BO.QueryAchievementsTemplateBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimension;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentDimensionQuota;
import com.kakarote.hrm.entity.PO.HrmAchievementsAssessmentTemplate;
import com.kakarote.hrm.entity.VO.AchievementsDimensionVO;
import com.kakarote.hrm.entity.VO.AchievementsQuotaVO;
import com.kakarote.hrm.entity.VO.AchievementsTemplatePageVO;
import com.kakarote.hrm.entity.VO.AchievementsTemplateVO;
import com.kakarote.hrm.mapper.HrmAchievementsAssessmentTemplateMapper;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentDimensionQuotaService;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentDimensionService;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 绩效管理-考核模板 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Service
public class HrmAchievementsAssessmentTemplateServiceImpl extends BaseServiceImpl<HrmAchievementsAssessmentTemplateMapper, HrmAchievementsAssessmentTemplate> implements IHrmAchievementsAssessmentTemplateService {

    @Autowired
    private IHrmAchievementsAssessmentDimensionService assessmentDimensionService;

    @Autowired
    private IHrmAchievementsAssessmentDimensionQuotaService assessmentDimensionQuotaService;

    @Transactional
    @Override
    public Long addOrUpdate(AchievementsTemplateBO achievementsTemplateBO) {
        Long operationUser = EmployeeHolder.getEmployeeId();
        if (ObjectUtil.isEmpty(operationUser)) {
            if (UserUtil.isAdmin()) {
                operationUser = 0l;
            }
        }
        Date createTime = null;
        Long createUserId = null;
        //如果是更新模板则将之前的模板数据进行逻辑删除，然后将新数据的模板id设置为空，走插入逻辑
        if (ObjectUtil.isNotNull(achievementsTemplateBO.getTemplateId())) {
            boolean exists = lambdaQuery().eq(HrmAchievementsAssessmentTemplate::getTemplateName, achievementsTemplateBO.getTemplateName()).ne(HrmAchievementsAssessmentTemplate::getTemplateId, achievementsTemplateBO.getTemplateId()).eq(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_TRUE).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "模板名称已存在!");
            }
            HrmAchievementsAssessmentTemplate assessmentTemplate = lambdaQuery().eq(HrmAchievementsAssessmentTemplate::getTemplateId, achievementsTemplateBO.getTemplateId())
                    .eq(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_TRUE).one();
            if (ObjectUtil.isNull(assessmentTemplate)) {
                throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
            }
            createTime = assessmentTemplate.getCreateTime();
            createUserId = assessmentTemplate.getCreateUserId();
            //更新之前的模板数据状态为false-这一步是为了保留在更新模板的时候不影响已使用的模板的考核计划去查询对应的模板信息
            lambdaUpdate().set(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_FALSE)
                    .eq(HrmAchievementsAssessmentTemplate::getTemplateId, achievementsTemplateBO.getTemplateId()).update();
            achievementsTemplateBO.setTemplateId(null);
        } else {
            boolean exists = lambdaQuery().eq(HrmAchievementsAssessmentTemplate::getTemplateName, achievementsTemplateBO.getTemplateName()).eq(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_TRUE).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "模板名称已存在!");
            }
        }
        checkTemplateParam(achievementsTemplateBO);//参数校验
        AtomicReference<Integer> quotaNum = new AtomicReference<>(0);
        AtomicReference<Integer> dimensionNum = new AtomicReference<>(0);
        List<AchievementsDimensionBO> achievementsDimensionBOList = achievementsTemplateBO.getAchievementsDimensionBOList();
        HrmAchievementsAssessmentTemplate achievementsAssessmentTemplate = new HrmAchievementsAssessmentTemplate();
        BeanUtil.copyProperties(achievementsTemplateBO, achievementsAssessmentTemplate);
//        achievementsAssessmentTemplate.setUpdateUserId(EmployeeHolder.getEmployeeId());
        achievementsAssessmentTemplate.setUpdateUserId(operationUser);
        if (ObjectUtil.isNotNull(createTime)) {//如果更新时间不为空则说明是更新模板逻辑，则创建时间还用之前模板的创建时间不过要把更新时间变更为当前修改时间
            achievementsAssessmentTemplate.setCreateTime(createTime);
            achievementsAssessmentTemplate.setUpdateTime(new Date());
            achievementsAssessmentTemplate.setCreateUserId(createUserId);
        } else {
//            achievementsAssessmentTemplate.setCreateUserId(EmployeeHolder.getEmployeeId());
            achievementsAssessmentTemplate.setCreateUserId(operationUser);
            achievementsAssessmentTemplate.setCreateTime(new Date());
        }
        achievementsAssessmentTemplate.setStatus(AchievementsCommon.STATUS_TRUE);
        save(achievementsAssessmentTemplate);
        Long finalOperationUser = operationUser;
        achievementsDimensionBOList.stream().forEach(
                dimensionBO -> {
                    //处理维度
                    HrmAchievementsAssessmentDimension assessmentDimension = new HrmAchievementsAssessmentDimension();
                    BeanUtil.copyProperties(dimensionBO, assessmentDimension);
                    assessmentDimension.setTemplateId(achievementsAssessmentTemplate.getTemplateId());//给模板关联维度
//                    assessmentDimension.setCreateUserId(EmployeeHolder.getEmployeeId());
                    assessmentDimension.setCreateUserId(finalOperationUser);
//                    assessmentDimension.setUpdateUserId(EmployeeHolder.getEmployeeId());
                    assessmentDimension.setUpdateUserId(finalOperationUser);
                    assessmentDimensionService.save(assessmentDimension);//保存维度
                    dimensionNum.getAndSet(dimensionNum.get() + 1);
                    dimensionBO.getAchievementsPointBOList().stream().forEach(
                            achievementsPointBO -> {
                                //处理指标
                                HrmAchievementsAssessmentDimensionQuota assessmentDimensionQuota = new HrmAchievementsAssessmentDimensionQuota();
                                BeanUtil.copyProperties(achievementsPointBO, assessmentDimensionQuota);
                                assessmentDimensionQuota.setDimensionId(assessmentDimension.getDimensionId());//给维度关联指标
                                assessmentDimensionQuotaService.save(assessmentDimensionQuota);
                                quotaNum.getAndSet(quotaNum.get() + 1);
                            }
                    );
                }
        );
        //更新模板的维度数量和指标数量
        lambdaUpdate().set(HrmAchievementsAssessmentTemplate::getDimensionNum, dimensionNum.get())
                .set(HrmAchievementsAssessmentTemplate::getQuotaNum, quotaNum.get())
                .eq(HrmAchievementsAssessmentTemplate::getTemplateId, achievementsAssessmentTemplate.getTemplateId()).update();
        return achievementsAssessmentTemplate.getTemplateId();
    }

    @Override
    public BasePage<AchievementsTemplatePageVO> queryPageList(QueryAchievementsTemplateBO search) {
        BasePage<AchievementsTemplatePageVO> assessmentTemplateBasePage = baseMapper.queryTemplateList(search.parse(), search.getTemplateName());
        return assessmentTemplateBasePage;
    }

    @Override
    public void deleteByIdList(OperationReq operationReq) {
        if (CollectionUtil.isEmpty(operationReq.getIds())) {
            throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "要删除数据id不能为空");
        }
        lambdaUpdate().set(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_FALSE)
                .in(HrmAchievementsAssessmentTemplate::getTemplateId, operationReq.getIds()).update();
    }

    @Override
    public AchievementsTemplateVO information(Long templateId) {
        Integer count = lambdaQuery().select().eq(HrmAchievementsAssessmentTemplate::getTemplateId, templateId).eq(HrmAchievementsAssessmentTemplate::getStatus, AchievementsCommon.STATUS_TRUE).count().intValue();
        if (count < 1) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        AchievementsTemplateVO achievementsTemplateVO = baseMapper.queryTemplateBaseInformation(templateId);

        List<HrmAchievementsAssessmentDimension> dimensionList = assessmentDimensionService.queryDimensionListByTemplateId(templateId);
        List<Long> dimensionIdList = dimensionList.stream().map(HrmAchievementsAssessmentDimension::getDimensionId).collect(Collectors.toList());
        List<HrmAchievementsAssessmentDimensionQuota> quotaList = assessmentDimensionQuotaService.queryQuotaListByDimensionIds(dimensionIdList);

        Map<Long, List<AchievementsQuotaVO>> quotaListMap = quotaList.stream().map(
                dimensionQuotaPO -> {
                    AchievementsQuotaVO achievementsQuotaVO = new AchievementsQuotaVO();
                    BeanUtil.copyProperties(dimensionQuotaPO, achievementsQuotaVO);
                    return achievementsQuotaVO;
                }

        ).collect(Collectors.groupingBy(AchievementsQuotaVO::getDimensionId));

        List<AchievementsDimensionVO> dimensionVOList = dimensionList.stream().map(
                dimensionPO -> {
                    AchievementsDimensionVO achievementsDimensionVO = new AchievementsDimensionVO();
                    BeanUtil.copyProperties(dimensionPO, achievementsDimensionVO);
                    achievementsDimensionVO.setAchievementsQuotaVOList(quotaListMap.get(dimensionPO.getDimensionId()));
                    return achievementsDimensionVO;
                }
        ).collect(Collectors.toList());
        achievementsTemplateVO.setDimensionVOList(dimensionVOList);
        return achievementsTemplateVO;
    }


    private void checkTemplateParam(AchievementsTemplateBO achievementsTemplateBO) {
        if (ObjectUtil.isEmpty(achievementsTemplateBO.getUpperLimitType())) {
            throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "评分上限类型不能为空");
        }

        if (ObjectUtil.isEmpty(achievementsTemplateBO.getUpperLimitScore())) {
            throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "评分上限不能为空");
        }

        List<AchievementsDimensionBO> achievementsDimensionBOList = achievementsTemplateBO.getAchievementsDimensionBOList();
        if (CollectionUtil.isEmpty(achievementsDimensionBOList)) {
            throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "至少新建一个考核纬度！");
        }

        List<String> dimensionNameList = new ArrayList<>();
        List<String> quotaNameList = new ArrayList<>();
        AtomicReference<Double> dimensionTotal = new AtomicReference<>(0d);//维度权重总和
        achievementsDimensionBOList.stream().forEach(
                dimensionBO -> {
                    AtomicReference<Double> total = new AtomicReference<>(0d);//当前维度下指标权重总和
                    dimensionBO.getAchievementsPointBOList().stream().forEach(
                            pointBO -> {
                                if (ObjectUtil.isNotNull(pointBO.getQuotaWeight())) {
                                    if (pointBO.getQuotaWeight() > 100) {
                                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "指标权重为 0~100%");
                                    }
                                    total.updateAndGet(v -> (double) (v + pointBO.getQuotaWeight()));
                                } else {
                                    throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "指标权重不能为空");
                                }
                                if (!quotaNameList.contains(pointBO.getQuotaName())) {
                                    quotaNameList.add(pointBO.getQuotaName());
                                } else {
                                    throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "指标名称(" + pointBO.getQuotaName() + ")重复");
                                }

                            }
                    );
                    if (ObjectUtil.isNull(dimensionBO.getDimensionWeight())) {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "纬度权重不能为空");
                    }
                    if (dimensionBO.getDimensionWeight() > 100) {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "纬度权重为 0~100%");
                    }
                    //如果流程设置里配置的员工填写，且当前维度允许用户填写，用户才能填写
                    if (dimensionBO.getIsAllowEdit()) {
                        if (total.get() > 100) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度指标(" + dimensionBO.getDimensionName() + ")总和必须小于等于100%！");
                        }
                    } else {
                        if (total.get() != 100) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度指标(" + dimensionBO.getDimensionName() + ")总和必须等于100%！");
                        }
                    }
                    dimensionTotal.updateAndGet(v -> (double) (v + dimensionBO.getDimensionWeight()));//维度权重总和
                    if (!dimensionNameList.contains(dimensionBO.getDimensionName())) {
                        dimensionNameList.add(dimensionBO.getDimensionName());
                    } else {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "维度名称(" + dimensionBO.getDimensionName() + ")重复");
                    }
                }
        );
    }


}
