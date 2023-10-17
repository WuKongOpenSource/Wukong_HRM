package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.EmployeeAppraisalStageBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeStage;
import com.kakarote.hrm.entity.VO.ScoringStageInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-06-01
 */
public interface HrmAppraisalEmployeeStageMapper extends BaseMapper<HrmAppraisalEmployeeStage> {

    /**
     * 查询考核阶段列表
     *
     * @param appraisalEmployeeId
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryStageList(@Param("appraisalEmployeeId") Long appraisalEmployeeId);


    List<HrmAppraisalEmployeeStage> queryStageListByIds(@Param("ids") List<Long> stageIdList);

    /**
     * 查询员工绩效某个阶段待处理人信息
     *
     * @param appraisalEmployeeId
     * @param stageType
     * @return
     */
    List<EmployeeAppraisalStageBO> queryStageInfo(@Param("appraisalEmployeeId") Long appraisalEmployeeId, @Param("stageType") Integer stageType);

    /**
     * 查下评分阶段节点处理人列表
     *
     * @param appraisalEmployeeId
     * @return
     */
    List<ScoringStageInfoVO> queryStageByStageType(@Param("appraisalEmployeeId") Long appraisalEmployeeId, @Param("stageTypes") List<Integer> stageTypes);

    /**
     * 查询行数最多的员工考核流程数据
     *
     * @param appraisalPlanId
     */
    List<HrmAppraisalEmployeeStage> queryMaxCountStageData(@Param("appraisalPlanId") Long appraisalPlanId);


    /**
     * 根据考核计划和员工id列表查询阶段数据
     *
     * @param appraisalEmployeeId
     * @param employeeList
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryStageByEmployeeIds(@Param("appraisalPlanId") Long appraisalEmployeeId, @Param("employeeList") List<Long> employeeList);

    Map<String, Object> queryStageUserNameBySort(@Param("appraisalEmployeeId") Long appraisalEmployeeId, @Param("sort") Integer sort);


    HrmAppraisalEmployeeStage queryAppraisalStageInfoBySort(@Param("appraisalPlanId") Long appraisalPlanId, @Param("employeeId") Long employeeId, @Param("sort") Integer sort);

    /**
     * 根据阶段类型查询阶段信息
     *
     * @param appraisalPlanId
     * @param employeeId
     * @param stageType
     * @return
     */
    HrmAppraisalEmployeeStage queryAppraisalStageInfoByStageType(@Param("appraisalPlanId") Long appraisalPlanId, @Param("employeeId") Long employeeId, @Param("stageType") Integer stageType);

    /**
     * 查询当前员工所有阶段待处理数量
     *
     * @param employeeId
     * @return
     */
    Map<String, Integer> queryEmployeePendingStageNumOfAll(@Param("employeeId") Long employeeId);
}
