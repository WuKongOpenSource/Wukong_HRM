package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.entity.BO.AppraisalPlanQuotaSettingBO;
import com.kakarote.hrm.entity.BO.QuotaSettingDimensionBO;
import com.kakarote.hrm.entity.BO.QuotaSettingDimensionQuotaBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanQuotaSetting;
import com.kakarote.hrm.entity.PO.HrmQuotaSettingDimension;
import com.kakarote.hrm.entity.PO.HrmQuotaSettingDimensionQuota;
import com.kakarote.hrm.entity.VO.AppraisalPlanQuotaSettingVO;
import com.kakarote.hrm.entity.VO.QuotaSettingDimensionQuotaVO;
import com.kakarote.hrm.entity.VO.QuotaSettingDimensionVO;
import com.kakarote.hrm.mapper.HrmAppraisalPlanQuotaSettingMapper;
import com.kakarote.hrm.service.IHrmAppraisalPlanQuotaSettingService;
import com.kakarote.hrm.service.IHrmQuotaSettingDimensionQuotaService;
import com.kakarote.hrm.service.IHrmQuotaSettingDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 考核计划-指标配置-基础表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Service
public class HrmAppraisalPlanQuotaSettingServiceImpl extends BaseServiceImpl<HrmAppraisalPlanQuotaSettingMapper, HrmAppraisalPlanQuotaSetting> implements IHrmAppraisalPlanQuotaSettingService {

    @Autowired
    private IHrmQuotaSettingDimensionService quotaSettingDimensionService;

    @Autowired
    private IHrmQuotaSettingDimensionQuotaService quotaSettingDimensionQuotaService;

    @Transactional
    @Override
    public void saveQuotaSetting(AppraisalPlanQuotaSettingBO appraisalPlanQuotaSettingBO, Long appraisalPlanId) {
        HrmAppraisalPlanQuotaSetting hrmAppraisalPlanQuotaSetting = new HrmAppraisalPlanQuotaSetting();
        BeanUtil.copyProperties(appraisalPlanQuotaSettingBO, hrmAppraisalPlanQuotaSetting);
        hrmAppraisalPlanQuotaSetting.setAppraisalPlanId(appraisalPlanId);
        saveOrUpdate(hrmAppraisalPlanQuotaSetting);//保存指标设置基础表
        List<QuotaSettingDimensionBO> quotaSettingDimensionBOList = appraisalPlanQuotaSettingBO.getQuotaSettingDimensionBOList();
        quotaSettingDimensionService.lambdaUpdate().eq(HrmQuotaSettingDimension::getAppraisalPlanId, appraisalPlanId).remove();
        quotaSettingDimensionQuotaService.lambdaUpdate().eq(HrmQuotaSettingDimensionQuota::getAppraisalPlanId, appraisalPlanId).remove();
        AtomicReference<Integer> dimensionSort = new AtomicReference<>(1);
        AtomicReference<Integer> quotaSort = new AtomicReference<>(1);
        quotaSettingDimensionBOList.stream().forEach(
                quotaSettingDimensionBO -> {
                    HrmQuotaSettingDimension hrmQuotaSettingDimension = new HrmQuotaSettingDimension();
                    BeanUtil.copyProperties(quotaSettingDimensionBO, hrmQuotaSettingDimension);
                    hrmQuotaSettingDimension.setQuotaSettingId(hrmAppraisalPlanQuotaSetting.getQuotaSettingId());
                    hrmQuotaSettingDimension.setAppraisalPlanId(appraisalPlanId);
                    hrmQuotaSettingDimension.setUpdateUserId(EmployeeHolder.getEmployeeId());
                    hrmQuotaSettingDimension.setDimensionId(null);
                    hrmQuotaSettingDimension.setSort(dimensionSort.get());
                    dimensionSort.getAndSet(dimensionSort.get() + 1);
                    hrmQuotaSettingDimension.setCreateTime(new Date());
                    quotaSettingDimensionService.save(hrmQuotaSettingDimension);//保存维度
                    List<QuotaSettingDimensionQuotaBO> quotaSettingDimensionQuotaBOList = quotaSettingDimensionBO.getQuotaSettingDimensionQuotaBOList();
                    quotaSettingDimensionQuotaBOList.stream().forEach(
                            quotaSettingDimensionQuotaBO -> {
                                HrmQuotaSettingDimensionQuota hrmQuotaSettingDimensionQuota = new HrmQuotaSettingDimensionQuota();
                                BeanUtil.copyProperties(quotaSettingDimensionQuotaBO, hrmQuotaSettingDimensionQuota);
                                hrmQuotaSettingDimensionQuota.setDimensionId(hrmQuotaSettingDimension.getDimensionId());
                                hrmQuotaSettingDimensionQuota.setAppraisalPlanId(appraisalPlanId);
                                hrmQuotaSettingDimensionQuota.setQuotaId(null);
                                hrmQuotaSettingDimensionQuota.setSort(quotaSort.get());
                                quotaSort.getAndSet(quotaSort.get() + 1);
                                hrmQuotaSettingDimensionQuota.setCreateTime(new Date());
                                quotaSettingDimensionQuotaService.save(hrmQuotaSettingDimensionQuota);//保存维度下指标
                            }
                    );
                }
        );
    }

    @Override
    public AppraisalPlanQuotaSettingVO queryPlanQuotaSettingVO(Long appraisalPlan) {
        AppraisalPlanQuotaSettingVO appraisalPlanQuotaSettingVO = new AppraisalPlanQuotaSettingVO();

        HrmAppraisalPlanQuotaSetting appraisalPlanQuotaSettingInfo = lambdaQuery().select()
                .eq(HrmAppraisalPlanQuotaSetting::getAppraisalPlanId, appraisalPlan).one();
        BeanUtil.copyProperties(appraisalPlanQuotaSettingInfo, appraisalPlanQuotaSettingVO);

        List<HrmQuotaSettingDimension> quotaSettingDimensionList = quotaSettingDimensionService.lambdaQuery()
                .eq(HrmQuotaSettingDimension::getQuotaSettingId, appraisalPlanQuotaSettingInfo.getQuotaSettingId()).list();
        List<HrmQuotaSettingDimensionQuota> quotaSettingDimensionQuotaList = quotaSettingDimensionQuotaService.lambdaQuery()
                .eq(HrmQuotaSettingDimensionQuota::getAppraisalPlanId, appraisalPlan).list();

        Map<Long, List<QuotaSettingDimensionQuotaVO>> dimensionQuotaListMap = quotaSettingDimensionQuotaList.stream().map(
                quotaSettingDimension -> {
                    QuotaSettingDimensionQuotaVO quotaVO = new QuotaSettingDimensionQuotaVO();
                    BeanUtil.copyProperties(quotaSettingDimension, quotaVO);
                    return quotaVO;
                }
        ).collect(Collectors.groupingBy(QuotaSettingDimensionQuotaVO::getDimensionId));

        List<QuotaSettingDimensionVO> appraisalPlanQuotaSettingVOList = quotaSettingDimensionList.stream().map(
                quotaSettingDimension -> {
                    QuotaSettingDimensionVO quotaSettingDimensionVO = new QuotaSettingDimensionVO();
                    BeanUtil.copyProperties(quotaSettingDimension, quotaSettingDimensionVO);
                    quotaSettingDimensionVO.setQuotaSettingDimensionQuotaVOList(dimensionQuotaListMap.get(quotaSettingDimension.getDimensionId()));
                    return quotaSettingDimensionVO;
                }
        ).collect(Collectors.toList());

        appraisalPlanQuotaSettingVO.setQuotaSettingDimensionVOList(appraisalPlanQuotaSettingVOList);
        return appraisalPlanQuotaSettingVO;
    }

    @Transactional
    @Override
    public void deleteByAppraisalPlanId(Long appraisalPlanId) {
        quotaSettingDimensionService.lambdaUpdate().eq(HrmQuotaSettingDimension::getAppraisalPlanId, appraisalPlanId).remove();
        quotaSettingDimensionQuotaService.lambdaUpdate().eq(HrmQuotaSettingDimensionQuota::getAppraisalPlanId, appraisalPlanId).remove();
        lambdaUpdate().eq(HrmAppraisalPlanQuotaSetting::getAppraisalPlanId, appraisalPlanId).remove();
    }
}
