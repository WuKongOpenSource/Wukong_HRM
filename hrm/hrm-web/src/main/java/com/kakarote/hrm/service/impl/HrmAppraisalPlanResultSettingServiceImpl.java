package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.AppraisalPlanResultSettingBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanResultSetting;
import com.kakarote.hrm.entity.VO.AppraisalPlanResultSettingVO;
import com.kakarote.hrm.mapper.HrmAppraisalPlanResultSettingMapper;
import com.kakarote.hrm.service.IHrmAppraisalPlanResultSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 绩效考核结果等级列表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Service
public class HrmAppraisalPlanResultSettingServiceImpl extends BaseServiceImpl<HrmAppraisalPlanResultSettingMapper, HrmAppraisalPlanResultSetting> implements IHrmAppraisalPlanResultSettingService {

    @Transactional
    @Override
    public void savePlanResultSetting(List<AppraisalPlanResultSettingBO> appraisalPlanResultSettingBOList, Long appraisalPlanId) {
        List<HrmAppraisalPlanResultSetting> appraisalPlanResultSettingList = new ArrayList<>();
        appraisalPlanResultSettingBOList.forEach(
                appraisalPlanResultSettingBO -> {
                    HrmAppraisalPlanResultSetting hrmAppraisalPlanResultSetting = new HrmAppraisalPlanResultSetting();
                    BeanUtil.copyProperties(appraisalPlanResultSettingBO, hrmAppraisalPlanResultSetting);
                    hrmAppraisalPlanResultSetting.setAppraisalPlanId(appraisalPlanId);
                    hrmAppraisalPlanResultSetting.setLevelId(null);
                    hrmAppraisalPlanResultSetting.setCreateTime(new Date());
                    appraisalPlanResultSettingList.add(hrmAppraisalPlanResultSetting);
                }
        );
        lambdaUpdate().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).remove();
        saveBatch(appraisalPlanResultSettingList);
    }

    @Override
    public List<AppraisalPlanResultSettingVO> queryResultSettingList(Long appraisalPlanId) {
        List<HrmAppraisalPlanResultSetting> resultSettingList = lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).list();
        List<AppraisalPlanResultSettingVO> appraisalPlanResultSettingVOList = resultSettingList.stream().map(
                resultSetting -> {
                    AppraisalPlanResultSettingVO appraisalPlanResultSettingVO = new AppraisalPlanResultSettingVO();
                    BeanUtil.copyProperties(resultSetting, appraisalPlanResultSettingVO);
                    return appraisalPlanResultSettingVO;
                }
        ).collect(Collectors.toList());
        return appraisalPlanResultSettingVOList;
    }

}
