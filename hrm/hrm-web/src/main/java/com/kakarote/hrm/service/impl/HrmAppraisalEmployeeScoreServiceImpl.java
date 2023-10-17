package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeScore;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeStage;
import com.kakarote.hrm.mapper.HrmAppraisalEmployeeScoreMapper;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeScoreService;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工考核计划-评分 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-30
 */
@Service
public class HrmAppraisalEmployeeScoreServiceImpl extends BaseServiceImpl<HrmAppraisalEmployeeScoreMapper, HrmAppraisalEmployeeScore> implements IHrmAppraisalEmployeeScoreService {

    @Autowired
    private IHrmAppraisalEmployeeStageService appraisalEmployeeStageService;

    @Autowired
    IHrmAppraisalEmployeeService appraisalEmployeeService;

    @Override
    public List<HrmAppraisalEmployeeScore> queryEmployeeScoreList(Long appraisalEmployeeId) {
        return baseMapper.queryEmployeeScoreList(appraisalEmployeeId);
    }

    @Override
    public void rejectScoreByStageId(Long appraisalEmployeeId, List<Long> appraisalStageId) {
        List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = appraisalEmployeeStageService.lambdaQuery().in(HrmAppraisalEmployeeStage::getAppraisalStageId, appraisalStageId).list();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeStageList)) {
            List<Long> stageUserIdList = appraisalEmployeeStageList.stream().map(HrmAppraisalEmployeeStage::getStageUserId).collect(Collectors.toList());
            lambdaUpdate().set(HrmAppraisalEmployeeScore::getLevel, null).set(HrmAppraisalEmployeeScore::getScore, null)
                    .eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, appraisalEmployeeId).in(HrmAppraisalEmployeeScore::getRater, stageUserIdList).update();
        }
    }
}
