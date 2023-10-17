package com.kakarote.hrm.utils;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.appraisal.AppraisalStageStatusEnum;
import com.kakarote.hrm.constant.appraisal.RaterTypeEnum;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.VO.DeptVO;
import com.kakarote.hrm.service.IHrmAppraisalPlanService;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 绩效管理工具类
 */
@Component
public class AchievementsUtil {
    @Autowired
    IHrmEmployeeService employeeService;

    @Autowired
    IHrmDeptService hrmDeptService;

    @Autowired
    IHrmAppraisalPlanService appraisalPlanService;

    /**
     * 递归调用查询多级上级员工id
     *
     * @param level
     * @param employeeId
     * @return
     */
    public Long queryLevelEmployee(Integer level, Long employeeId) {
        //todo 如果多级上级实际没有那么多级的情况要做判断处理
        HrmEmployee employeeInfo = employeeService.lambdaQuery().eq(HrmEmployee::getEmployeeId, employeeId).one();
        if (ObjectUtil.isNotNull(employeeInfo)) {
            if (level >= 1) {
                level = level - 1;
                return queryLevelEmployee(level, employeeInfo.getParentId());
            } else {
                return employeeInfo.getEmployeeId();
            }
        }
        return null;
    }

    /**
     * 递归调用查询多级上级部门负责人
     *
     * @param level
     * @param deptId
     * @return
     */
    public Long queryLevelDeptOwner(Integer level, Long deptId) {
        //todo 如果多级上级实际没有那么多级的情况要做判断处理
        DeptVO deptInfo = hrmDeptService.queryById(deptId);
        if (ObjectUtil.isNotNull(deptInfo)) {
            if (level > 1) {
                level = level - 1;
                return queryLevelDeptOwner(level, deptInfo.getParentId());
            } else {
                return deptInfo.getMainEmployeeId();
            }
        }
        return null;
    }


    /**
     * @param raterType  确认类型
     * @param level      级别
     * @param appoint    指定人
     * @param employeeId 被考核人
     * @return
     */
    public Long queryStageUser(Long appraisalPlanId, Integer raterType, Integer level, Long appoint, Long employeeId) {
        RaterTypeEnum raterTypeEnum = RaterTypeEnum.parse(raterType);
        Long userId = null;
        switch (raterTypeEnum) {
            case SELF:
                userId = employeeId;
                break;
            case SUPERIOR:
                userId = queryLevelEmployee(level, employeeId);
                break;
            case APPOINTOR:
                userId = appoint;
                break;
            case DEPT_HEAD:
                HrmEmployee employee = employeeService.getById(employeeId);
                if (ObjectUtil.isNotNull(employee.getDeptId())) {
                    userId = queryLevelDeptOwner(level, employee.getDeptId());
                }
                break;
        }
        return userId;
    }


    /**
     * @param stageType
     * @param status    1：已处理 2：待处理 3：驳回
     * @return
     */
    public Integer queryMessageType(Integer stageType, Integer status) {
        Integer messageType = null;
        if (status == 3) {
            messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_ASSESSED_REJECT.getType();
            return messageType;
        }
        AppraisalStageStatusEnum stageStatusEnum = AppraisalStageStatusEnum.parse(stageType);
        switch (stageStatusEnum) {
            case FILL:
                if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_WRITE.getType();
                }
                break;
            case TARGET_CONFIRMATION:
                if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_CONFIRMATION.getType();
                }
                break;
            case SELF_SCORE:
            case OTHER_SCORE:
                if (status == 1) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING_SUCCESS.getType();
                } else if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING.getType();

                }
                break;
            case RESULT_AUDIT:
                if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_RESULT_AUDIT_WAITING.getType();
                }
                break;
            case RESULT_CONFIRMATION:
                if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_CONFIRMED.getType();
                }
                break;
            case APPEAL_CONFIRMATION:
                if (status == 1) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_PASS.getType();
                }
                if (status == 2) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_WAITING.getType();
                }
                if (status == 3) {
                    messageType = AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_REJECT.getType();
                }
                break;
        }
        return messageType;
    }
}
