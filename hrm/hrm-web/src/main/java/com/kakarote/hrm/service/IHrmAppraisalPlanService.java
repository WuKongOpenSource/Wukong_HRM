package com.kakarote.hrm.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlan;
import com.kakarote.hrm.entity.VO.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考核计划基础信息表 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
public interface IHrmAppraisalPlanService extends BaseService<HrmAppraisalPlan> {

    /**
     * 查询考核计划列表
     *
     * @param queryAppraisalPlanBO
     * @return
     */
    BasePage<AppraisalPlanVO> queryPageList(QueryAppraisalPlanBO queryAppraisalPlanBO);

    /**
     * 查询考核计划员工计划列表
     *
     * @param queryEmployeeQuotaBO
     * @return
     */
    BasePage<AppraisalPlanOfEmployeeVO> queryEmployeeAppraisalList(QueryEmployeeQuotaBO queryEmployeeQuotaBO);

    /**
     * 查看考核结果列表
     *
     * @param search
     * @return
     */
    BasePage<Map<String, Object>> queryAppraisalResultPageList(HrmSearchBO search);

    /**
     * 保存考核计划基础信息
     *
     * @return
     */
    OperationLog addOrUpdate(AppraisalPlanBO appraisalPlanBO);

    /**
     * 验证名称是否唯一
     *
     * @param planName
     * @return
     */
    Boolean verifyUnique(String planName, Long appraisalPlanId);

    /**
     * 删除考核计划
     */
    OperationLog delAppraisalPlan(Long appraisalPlanId);

    /**
     * 删除归档后的考核计划
     */
    OperationLog delAppraisalPlanOfFile(Long appraisalPlanId);

    /**
     * 查询考核配置
     *
     * @param appraisalPlanId
     * @return
     */
    AppraisalPlanSettingInfoVO querySetting(Long appraisalPlanId);


    /**
     * 查询员工列表
     *
     * @param queryAppraisalEmployeeBO
     * @return
     */
    BasePage<JSONObject> queryEmployee(QueryAppraisalEmployeeBO queryAppraisalEmployeeBO);

    /**
     * 查询每个绩效状态的数量
     */
    Map<Integer, Long> queryAppraisalStatusNum();

    /**
     * 查询考核人员列表头信息
     *
     * @return
     */
    List<HeadFieldVO> queryEmployeeListHeadInfo(Long appraisalPlanId);

    /**
     * 添加考核员工
     *
     * @param appraisalEmployeeSaveBO
     */
    void addAppraisalEmployee(AppraisalEmployeeSaveBO appraisalEmployeeSaveBO);


    /**
     * 移除考核员工
     *
     * @param appraisalEmployeeSaveBO
     */
    void delAppraisalEmployee(AppraisalEmployeeSaveBO appraisalEmployeeSaveBO);


    /**
     * 启动考核计划
     *
     * @param appraisalPlanId
     */
    OperationLog startAppraisalPlan(Long appraisalPlanId);

    /**
     * 启动前检查考核计划
     *
     * @param appraisalPlanId
     */
    void checkAppraisalPlan(Long appraisalPlanId);

    /**
     * 开启评分
     *
     * @param appraisalPlanId
     * @return
     */
    OperationLog openScoring(Long appraisalPlanId);

    /**
     * 终止卡考核计划
     *
     * @param appraisalPlanId
     * @return
     */
    OperationLog terminationPlan(Long appraisalPlanId);

    /**
     * 查询考核计划结果等级列表
     */
    List<HrmAppraisalPlanLevelNumVO> queryAppraisalPlanResultLevelNum(Long appraisalPlanId);

    /**
     * 绩效面谈-相当于开启结果确认
     *
     * @param appraisalPlanId
     * @return
     */
    OperationLog toInterview(Long appraisalPlanId);

    /**
     * 绩效归档
     *
     * @param appraisalPlanId
     * @return
     */
    OperationLog placeOnFile(Long appraisalPlanId);


    /**
     * 查询考核计划下各等级员工绩效列表
     *
     * @param queryEmployeeQuotaBO
     * @return
     */
    BasePage<EmployeeAppraisalPlanVO> queryEmployeeAppraisalLevelList(QueryEmployeeQuotaBO queryEmployeeQuotaBO);


    /**
     * 查询场景处字段
     *
     * @param appraisalPlanId 考核计划id
     * @return data
     */
    List<HrmModelFiledVO> queryField(Long appraisalPlanId);


}
