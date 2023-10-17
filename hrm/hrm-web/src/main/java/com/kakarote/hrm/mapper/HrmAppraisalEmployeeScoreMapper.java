package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工考核计划-评分 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-05-30
 */
public interface HrmAppraisalEmployeeScoreMapper extends BaseMapper<HrmAppraisalEmployeeScore> {

    List<HrmAppraisalEmployeeScore> queryEmployeeScoreList(@Param("appraisalEmployeeId") Long appraisalEmployeeId);
}
