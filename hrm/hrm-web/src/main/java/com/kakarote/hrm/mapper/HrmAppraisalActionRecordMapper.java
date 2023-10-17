package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmAppraisalActionRecord;
import com.kakarote.hrm.entity.VO.QueryAppraisalRecordListVO;

import java.util.List;

/**
 * <p>
 * 绩效考核审核记录 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
public interface HrmAppraisalActionRecordMapper extends BaseMapper<HrmAppraisalActionRecord> {

    List<QueryAppraisalRecordListVO> queryAppraisalRecordList(Long appraisalEmployeeId, Integer type);
}
