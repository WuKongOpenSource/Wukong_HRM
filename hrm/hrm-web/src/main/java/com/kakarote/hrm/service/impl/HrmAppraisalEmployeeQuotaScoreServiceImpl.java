package com.kakarote.hrm.service.impl;

import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeQuotaScore;
import com.kakarote.hrm.mapper.HrmAppraisalEmployeeQuotaScoreMapper;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeQuotaScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 员工考核计划评分表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Service
public class HrmAppraisalEmployeeQuotaScoreServiceImpl extends BaseServiceImpl<HrmAppraisalEmployeeQuotaScoreMapper, HrmAppraisalEmployeeQuotaScore> implements IHrmAppraisalEmployeeQuotaScoreService {

    @Override
    public List<HrmAppraisalEmployeeQuotaScore> queryQuotaScoreList(Long appraisalEmployeeId) {
        return baseMapper.queryQuotaScoreList(appraisalEmployeeId);
    }
}
