package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeQuotaScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工考核计划评分表 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
public interface HrmAppraisalEmployeeQuotaScoreMapper extends BaseMapper<HrmAppraisalEmployeeQuotaScore> {

    List<HrmAppraisalEmployeeQuotaScore> queryQuotaScoreList(@Param("appraisalEmployeeId") Long appraisalEmployeeId);
}
