package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.constant.EmployeeStatusEnum;
import com.kakarote.hrm.constant.EmploymentFormsEnum;
import com.kakarote.hrm.constant.appraisal.AppraisalRangeTypeEnum;
import com.kakarote.hrm.constant.appraisal.InspectionTypeEnum;
import com.kakarote.hrm.entity.BO.InspectionScopeBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanEmployeeType;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanInspectionScope;
import com.kakarote.hrm.entity.PO.HrmDept;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.mapper.HrmAppraisalPlanInspectionScopeMapper;
import com.kakarote.hrm.service.IHrmAppraisalPlanEmployeeTypeService;
import com.kakarote.hrm.service.IHrmAppraisalPlanInspectionScopeService;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
@Service
public class HrmAppraisalPlanInspectionScopeServiceImpl extends BaseServiceImpl<HrmAppraisalPlanInspectionScopeMapper, HrmAppraisalPlanInspectionScope> implements IHrmAppraisalPlanInspectionScopeService {


    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmDeptService deptService;

    @Autowired
    private IHrmAppraisalPlanEmployeeTypeService appraisalPlanEmployeeTypeService;

    @Override
    public Map<Long, String> queryInspectionScope(List<Long> appraisalPlanIdList) {
        Map<Long, String> scopeName = new HashMap<>();
        Map<Long, InspectionScopeBO> scopeMap = new HashMap<>();
        List<HrmAppraisalPlanInspectionScope> appraisalPlanInspectionScopeList = lambdaQuery().in(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlanIdList).list();
        if (CollectionUtil.isNotEmpty(appraisalPlanInspectionScopeList)) {
            appraisalPlanInspectionScopeList.forEach(
                    appraisalPlanInspectionScope -> {
                        InspectionScopeBO inspectionScopeBO = scopeMap.get(appraisalPlanInspectionScope.getAppraisalPlanId());
                        if (ObjectUtil.isEmpty(inspectionScopeBO)) {
                            inspectionScopeBO = new InspectionScopeBO();
                            inspectionScopeBO.setType(AppraisalRangeTypeEnum.EMPLOYEE_DEPT.getValue());
                            inspectionScopeBO.setEmployeeIds(new ArrayList<>());
                            inspectionScopeBO.setDeptIds(new ArrayList<>());
                            scopeMap.put(appraisalPlanInspectionScope.getAppraisalPlanId(), inspectionScopeBO);
                        }
                        List<Long> employeeList = inspectionScopeBO.getEmployeeIds();
                        List<Long> deptList = inspectionScopeBO.getDeptIds();
                        if (appraisalPlanInspectionScope.getType().equals(InspectionTypeEnum.EMPLOYEE.getValue())) {
                            employeeList.add(appraisalPlanInspectionScope.getRecordId());
                        }
                        if (appraisalPlanInspectionScope.getType().equals(InspectionTypeEnum.DEPT.getValue())) {
                            deptList.add(appraisalPlanInspectionScope.getRecordId());
                        }
                        inspectionScopeBO.setEmployeeIds(employeeList);
                        inspectionScopeBO.setDeptIds(deptList);
                    }
            );
        }

        List<HrmAppraisalPlanEmployeeType> appraisalPlanEmployeeTypeList = appraisalPlanEmployeeTypeService.lambdaQuery().in(HrmAppraisalPlanEmployeeType::getAppraisalPlanId, appraisalPlanIdList).list();
        Map<Long, String> planEmployeeTypeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(appraisalPlanEmployeeTypeList)) {
            appraisalPlanEmployeeTypeList.forEach(
                    appraisalPlanEmployeeType -> {
                        String planEmployeeTypeName = planEmployeeTypeMap.get(appraisalPlanEmployeeType.getAppraisalPlanId());
                        if (StrUtil.isEmpty(planEmployeeTypeName)) {
                            planEmployeeTypeName = "聘用形式:";
                        }
                        Integer employeeType = appraisalPlanEmployeeType.getEmployeeType();
                        String employmentFormsName = EmploymentFormsEnum.parseName(employeeType);
                        String employeeStatus = appraisalPlanEmployeeType.getEmployeeStatus();
                        if (StrUtil.isNotEmpty(employeeStatus)) {
                            List<Integer> employeeStatusValues = Arrays.stream(employeeStatus.split(",")).map(value -> Integer.valueOf(value)).collect(Collectors.toList());
                            List<String> employeeStatusNames = new ArrayList<>();
                            employeeStatusValues.forEach(
                                    employeeStatusValue -> {
                                        employeeStatusNames.add(EmployeeStatusEnum.parseName(employeeStatusValue));
                                    }
                            );
                            String statusName = employeeStatusNames.stream().map(
                                    Object::toString
                            ).collect(Collectors.joining(","));
                            planEmployeeTypeName = planEmployeeTypeName + " " + employmentFormsName + "-" + statusName;
                            planEmployeeTypeMap.put(appraisalPlanEmployeeType.getAppraisalPlanId(), planEmployeeTypeName);
                        }
                    }
            );
        }

        for (Long appraisalPlanId : scopeMap.keySet()) {
            StringBuffer planScopeName = new StringBuffer();
            InspectionScopeBO inspectionScopeBO = scopeMap.get(appraisalPlanId);
            List<Long> employeeList = inspectionScopeBO.getEmployeeIds();
            List<Long> deptList = inspectionScopeBO.getDeptIds();
            if (CollectionUtil.isNotEmpty(deptList)) {
                String deptNames = Optional.ofNullable(deptService.lambdaQuery().select().in(HrmDept::getDeptId, deptList).list()).orElse(new ArrayList<>()).stream().map(HrmDept::getName).collect(Collectors.joining(","));
                planScopeName.append("部门:");
                planScopeName.append(deptNames);
                planScopeName.append("\n");

            }
            if (CollectionUtil.isNotEmpty(employeeList)) {
                String employeeNames = Optional.ofNullable(employeeService.lambdaQuery().select().in(HrmEmployee::getEmployeeId, employeeList).list()).orElse(new ArrayList<>()).stream().map(HrmEmployee::getEmployeeName).collect(Collectors.joining(","));
                planScopeName.append("员工:");
                planScopeName.append(employeeNames);
                planScopeName.append("\n");
            }
            String employeeStatusName = planEmployeeTypeMap.get(appraisalPlanId);
            if (StrUtil.isNotEmpty(employeeStatusName)) {
                planScopeName.append(employeeStatusName);
            }
            if (planScopeName.length() > 0) {
                scopeName.put(appraisalPlanId, planScopeName.toString());
            }
        }
        return scopeName;
    }

}
