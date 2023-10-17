package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.EvaluatoListBO;
import com.kakarote.hrm.entity.PO.HrmAchievementEmployeeAppraisal;
import com.kakarote.hrm.entity.VO.EvaluatoListVO;
import com.kakarote.hrm.entity.VO.QueryMyAppraisalVO;
import com.kakarote.hrm.entity.VO.ResultConfirmListVO;
import com.kakarote.hrm.entity.VO.TargetConfirmListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工绩效考核 Mapper 接口
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface HrmAchievementEmployeeAppraisalMapper extends BaseMapper<HrmAchievementEmployeeAppraisal> {

    BasePage<QueryMyAppraisalVO> queryMyAppraisal(BasePage<QueryMyAppraisalVO> parse, @Param("employeeId") Long employeeId, @Param("status") Integer status);

    BasePage<EvaluatoListVO> evaluatoList(BasePage<Object> parse,
                                          @Param("employeeId") Long employeeId,
                                          @Param("data") EvaluatoListBO evaluatoListBO);

    BasePage<TargetConfirmListVO> queryTodoAppraisalByStatus(BasePage<TargetConfirmListVO> parse,
                                                             @Param("employeeId") Long employeeId,
                                                             @Param("status") Integer status,
                                                             @Param("search") String search,
                                                             @Param("appraisalId") Long appraisalId);

    BasePage<ResultConfirmListVO> queryResultConfirmList(BasePage<Object> parse, @Param("employeeId") Long employeeId, @Param("search") String search);

    List<Map<String, Object>> queryScoreLevels(Long appraisalId);

    Map<String, Object> queryAppraisalIdInfo(Long appraisalId);

    List<Map<String, Object>> queryEmployeeByLevelId(@Param("levelId") Long levelId,
                                                     @Param("appraisalId") Long appraisalId);

    Integer queryWaitingNum(Long appraisalId);

    Integer queryTotalNum(Long appraisalId);

    List<TargetConfirmListVO> queryTargetConfirmScreen(@Param("employeeId") Long employeeId,
                                                       @Param("status") Integer status);

    List<EvaluatoListVO> queryEvaluatoScreen(@Param("employeeId") Long employeeId, @Param("status") Integer status);
}
