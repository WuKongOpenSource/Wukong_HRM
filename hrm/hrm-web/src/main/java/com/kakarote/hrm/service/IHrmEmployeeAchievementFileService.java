package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryCoefficientBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeAchievementFileBO;
import com.kakarote.hrm.entity.DTO.EmployeeAchievementFileReq;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmEmployeeAchievementFile;
import com.kakarote.hrm.entity.VO.EmployeeAchievementFileVO;

import java.util.Map;

/**
 * <p>
 * 员工绩效档案 服务类
 * </p>
 *
 * @author zyl
 * @since 2022-06-14
 */
public interface IHrmEmployeeAchievementFileService extends BaseService<HrmEmployeeAchievementFile> {


    BasePage<EmployeeAchievementFileVO> queryEmployeeAchievementFileList(QueryEmployeeAchievementFileBO employeeAchievementFileBO);

    void addOrUpdate(Long appraisalEmployeeId, Long employeeId);

    /**
     * 查询员工考核绩效结果系数
     *
     * @return
     */
    Map<Long, Double> queryCoefficient(QueryCoefficientBO queryCoefficientBO);


    /**
     * 删除绩效档案
     *
     * @param operationReq
     */
    void delAppraisalFileRecordList(EmployeeAchievementFileReq operationReq);

    /**
     * 删除绩效档案某个员工所有考核记录
     *
     * @param operationReq
     */
    void delAppraisalFileRecordListOfAll(OperationReq operationReq);

    /**
     * 根据考核计划id删除员工考核记录
     *
     * @param appraisalPlanId
     */
    void delAppraisalRecordByAppraisalPlanId(Long appraisalPlanId);
}
