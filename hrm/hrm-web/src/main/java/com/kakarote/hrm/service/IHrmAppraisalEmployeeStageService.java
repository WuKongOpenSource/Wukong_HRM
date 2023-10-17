package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.constant.appraisal.AppraisalStageStatusEnum;
import com.kakarote.hrm.entity.BO.EmployeeAppraisalStageBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployeeStage;
import com.kakarote.hrm.entity.VO.ScoringStageInfoVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-01
 */
public interface IHrmAppraisalEmployeeStageService extends BaseService<HrmAppraisalEmployeeStage> {

    /**
     * 根据员工考核绩效id查询阶段记录列表
     *
     * @param appraisalEmployeePlanId
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryAppraisalEmployeeStageList(Long appraisalEmployeePlanId);


    /**
     * 阶段状态更新
     *
     * @param appraisalPlan
     * @param employeeId
     */
    Boolean updateStageStatusPass(Long appraisalPlan, Long employeeId, AppraisalStageStatusEnum appraisalStageStatusEnum);


    /**
     * * 查询员工绩效某个阶段待处理人信息
     *
     * @param appraisalEmployeeId
     */
    List<EmployeeAppraisalStageBO> queryAppraisalEmployeeStage(Long appraisalEmployeeId, Integer status);

    /**
     * 当前是否是最后一个评分
     *
     * @param appraisalEmployeeId
     * @param employeeId
     */
    Boolean isEndScore(Long appraisalEmployeeId, Long employeeId);

    /**
     * 检查考核计划时将流程添加进去
     *
     * @param appraisalPlanId
     * @param employeeId
     */
    void addEmployeeAppraisalProcess(Long appraisalPlanId, Long employeeId);

    /**
     * 根据id查询阶段列表
     *
     * @param stageIdList
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryStageListByIds(List<Long> stageIdList);


    /**
     * 查询评分阶段所有节点信息-驳回时使用
     *
     * @param appraisalEmployeeId
     * @return
     */
    List<ScoringStageInfoVO> queryScoringStage(Long appraisalEmployeeId);


    /**
     * 查询当前考核计划员工阶段最多的员工阶段数据
     *
     * @param appraisalPlanId
     * @return
     */
    List<HrmAppraisalEmployeeStage> queryMaxCountStageData(Long appraisalPlanId);


    /**
     * * 查询员工绩效某个阶段待处理人信息
     *
     * @param appraisalEmployeeId
     */
    List<HrmAppraisalEmployeeStage> queryStageByEmployeeIds(Long appraisalEmployeeId, List<Long> employeeList);


    //更新员工考核计划阶段状态
    void toInterviewStage(Long appraisalPlanId);

    /**
     * 查询员工待处理阶段数量
     *
     * @param stageType
     * @param employeeId
     */
    Integer queryEmployeePendingStageNum(List<Integer> stageType, Long employeeId);


    /**
     * 查询员工待处理阶段数量
     *
     * @param employeeId
     */
    Map<String, Integer> queryEmployeePendingStageNumOfAll(Long employeeId);

    /**
     * 根据阶段sort查询阶段处理人
     *
     * @param appraisalEmployeeId
     * @param stageSort
     * @return
     */
    Map<String, Object> queryStageUserNameByStageSort(Long appraisalEmployeeId, Integer stageSort);


    /**
     * 根据阶段sort查询阶段信息
     *
     * @param AppraisalPlanId
     * @param employeeId
     * @param sort
     * @return
     */
    HrmAppraisalEmployeeStage queryAppraisalStageInfoBySort(Long AppraisalPlanId, Long employeeId, Integer sort);


    /**
     * 根据阶段stageType查询阶段信息
     *
     * @param AppraisalPlanId
     * @param employeeId
     * @param stageType
     * @return
     */
    HrmAppraisalEmployeeStage queryAppraisalStageInfoByStageType(Long AppraisalPlanId, Long employeeId, Integer stageType);
}
