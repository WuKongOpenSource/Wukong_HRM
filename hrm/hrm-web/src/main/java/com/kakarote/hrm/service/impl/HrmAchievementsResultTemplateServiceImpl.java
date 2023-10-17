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
import com.kakarote.hrm.entity.BO.QueryAchievementsResultTemplateBO;
import com.kakarote.hrm.entity.BO.ResultTemplateBO;
import com.kakarote.hrm.entity.BO.ResultTemplateLevelBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplate;
import com.kakarote.hrm.entity.PO.HrmAchievementsResultTemplateLevel;
import com.kakarote.hrm.entity.VO.AchievementsResultTemplateVO;
import com.kakarote.hrm.entity.VO.ResultTemplateLevelVO;
import com.kakarote.hrm.entity.VO.ResultTemplatePageVO;
import com.kakarote.hrm.mapper.HrmAchievementsResultTemplateMapper;
import com.kakarote.hrm.service.IHrmAchievementsResultTemplateLevelService;
import com.kakarote.hrm.service.IHrmAchievementsResultTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Slf4j
@Service
public class HrmAchievementsResultTemplateServiceImpl extends BaseServiceImpl<HrmAchievementsResultTemplateMapper, HrmAchievementsResultTemplate> implements IHrmAchievementsResultTemplateService {

    @Autowired
    private IHrmAchievementsResultTemplateLevelService resultTemplateLevelService;

    @Transactional
    @Override
    public Long saveOrUpdate(ResultTemplateBO resultTemplateBO) {
        Long operationUser = EmployeeHolder.getEmployeeId();
        if (ObjectUtil.isEmpty(operationUser)) {
            if (UserUtil.isAdmin()) {
                operationUser = 0l;
            }
        }
        Date createTime = null;
        if (resultTemplateBO.getResultTemplateId() != null) {
            boolean exists = lambdaQuery().eq(HrmAchievementsResultTemplate::getResultTemplateName, resultTemplateBO.getResultTemplateName()).ne(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplateBO.getResultTemplateId()).eq(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_TRUE).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "结果模板名称已存在!");
            }
            HrmAchievementsResultTemplate resultTemplate = lambdaQuery().select().eq(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplateBO.getResultTemplateId()).
                    eq(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_TRUE).one();
            lambdaUpdate().set(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_FALSE)
                    .eq(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplate.getResultTemplateId()).update();
//            resultTemplateLevelService.lambdaUpdate().eq(HrmAchievementsResultTemplateLevel::getResultTemplateId,resultTemplate.getResultTemplateId()).remove();
            resultTemplateBO.setResultTemplateId(null);
            resultTemplateBO.setCreateUserId(resultTemplate.getCreateUserId());
            createTime = resultTemplate.getCreateTime();
        } else {
            boolean exists = lambdaQuery().eq(HrmAchievementsResultTemplate::getResultTemplateName, resultTemplateBO.getResultTemplateName()).eq(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_TRUE).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "结果模板名称已存在!");
            }
        }
        checkResultTemplateParam(resultTemplateBO);
        HrmAchievementsResultTemplate resultTemplate = new HrmAchievementsResultTemplate();
        BeanUtil.copyProperties(resultTemplateBO, resultTemplate);
        if (resultTemplate.getCreateUserId() == null) {
//            resultTemplate.setCreateUserId(EmployeeHolder.getEmployeeId());
            resultTemplate.setCreateUserId(operationUser);
        }
//        resultTemplate.setUpdateUserId(EmployeeHolder.getEmployeeId());
        resultTemplate.setUpdateUserId(operationUser);
        if (ObjectUtil.isNotNull(createTime)) {
            resultTemplate.setCreateTime(createTime);//如果是更新逻辑则创建数据的时间其实应该还复用之前的
        }
        resultTemplate.setStatus(AchievementsCommon.STATUS_TRUE);
        save(resultTemplate);
        AtomicReference<Integer> sort = new AtomicReference<>(1);
        List<String> levelSetting = new ArrayList<>();
        resultTemplateBO.getResultTemplateLevelBOList().stream().forEach(
                resultTemplateLevelBO -> {
                    HrmAchievementsResultTemplateLevel resultTemplateLevel = new HrmAchievementsResultTemplateLevel();
                    BeanUtil.copyProperties(resultTemplateLevelBO, resultTemplateLevel);
                    resultTemplateLevel.setResultTemplateId(resultTemplate.getResultTemplateId());
                    resultTemplateLevel.setCreateTime(new Date());
                    resultTemplateLevel.setSort(sort.get());
                    sort.getAndSet(sort.get() + 1);
                    resultTemplateLevelService.save(resultTemplateLevel);
                    levelSetting.add(resultTemplateLevelBO.getLevelName());
                }
        );
        String levelSettingStr = levelSetting.stream().map(String::valueOf).collect(Collectors.joining(","));
        lambdaUpdate().set(HrmAchievementsResultTemplate::getLevelSetting, levelSettingStr)
                .eq(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplate.getResultTemplateId()).update();
        return resultTemplate.getResultTemplateId();
    }

    @Override
    public AchievementsResultTemplateVO information(Long resultTemplateId) {
        boolean exists = lambdaQuery().select().eq(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplateId)
                .eq(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_TRUE).exists();
        if (!exists) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        AchievementsResultTemplateVO achievementsResultTemplateVO = new AchievementsResultTemplateVO();
        HrmAchievementsResultTemplate resultTemplate = lambdaQuery().select().eq(HrmAchievementsResultTemplate::getResultTemplateId, resultTemplateId).one();
        BeanUtil.copyProperties(resultTemplate, achievementsResultTemplateVO);

        List<HrmAchievementsResultTemplateLevel> resultTemplateLevels = resultTemplateLevelService.queryListByTemplateId(resultTemplate.getResultTemplateId());
        List<ResultTemplateLevelVO> resultTemplateLevelVOList = resultTemplateLevels.stream().map(
                resultTemplateTmp -> {
                    ResultTemplateLevelVO resultTemplateLevelVO = new ResultTemplateLevelVO();
                    BeanUtil.copyProperties(resultTemplateTmp, resultTemplateLevelVO);
                    return resultTemplateLevelVO;
                }
        ).collect(Collectors.toList());
        achievementsResultTemplateVO.setResultTemplateLevelBOList(resultTemplateLevelVOList);
        return achievementsResultTemplateVO;
    }

    @Override
    public BasePage<ResultTemplatePageVO> queryPageList(QueryAchievementsResultTemplateBO search) {
        BasePage<ResultTemplatePageVO> pageList = baseMapper.queryResultTemplateList(search.parse(), search.getResultTemplateName());
        return pageList;
    }

    @Override
    public void deleteByIdList(OperationReq operationReq) {
        if (CollectionUtil.isEmpty(operationReq.getIds())) {
            throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "要删除数据id不能为空");
        }
        lambdaUpdate().set(HrmAchievementsResultTemplate::getStatus, AchievementsCommon.STATUS_FALSE)
                .in(HrmAchievementsResultTemplate::getResultTemplateId, operationReq.getIds()).update();
    }

    /**
     * 结果模板保存参数规则校验
     *
     * @param resultTemplateBO
     */
    public void checkResultTemplateParam(ResultTemplateBO resultTemplateBO) {
        List<ResultTemplateLevelBO> resultTemplateLevelBOList = resultTemplateBO.getResultTemplateLevelBOList();
        List<String> names = new ArrayList<>();
        resultTemplateLevelBOList.stream().forEach(
                resultTemplateLevelBO -> {
                    if (!names.contains(resultTemplateLevelBO.getLevelName())) {
                        names.add(resultTemplateLevelBO.getLevelName());
                    } else {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "等级名称(" + resultTemplateLevelBO.getLevelName() + ")重复");
                    }
                    Double scoreLowerLimit = resultTemplateLevelBO.getScoreLowerLimit();//分数下限
                    Double scoreUpperLimit = resultTemplateLevelBO.getScoreLowerLimit();//分数上限
                    if (scoreLowerLimit < 0) {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "评分下限必须大于0");
                    }
                    if (scoreUpperLimit > 100) {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "评分上限必须小于100");
                    }
                    if (scoreLowerLimit > scoreUpperLimit) {
                        throw new CrmException(HrmCodeEnum.TEMPLATE_SAVE_PARAM_ERROR, "分数下限不能大于分数上限");
                    }
                }
        );

    }
}
