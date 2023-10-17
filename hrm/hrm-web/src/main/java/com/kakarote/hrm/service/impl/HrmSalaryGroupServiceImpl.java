package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.entity.BO.SetSalaryGroupBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.SalaryGroupPageListVO;
import com.kakarote.hrm.mapper.HrmSalaryGroupMapper;
import com.kakarote.hrm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资组 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@Service("salaryGroupService")
public class HrmSalaryGroupServiceImpl extends BaseServiceImpl<HrmSalaryGroupMapper, HrmSalaryGroup> implements IHrmSalaryGroupService {

    @Autowired
    private HrmSalaryGroupMapper salaryGroupMapper;

    @Autowired
    private IHrmSalaryTaxRuleService taxRuleService;

    @Autowired
    private IHrmDeptService deptService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmSalaryGroupRelationDeptService salaryGroupRelDeptService;

    @Autowired
    private IHrmSalaryGroupRelationEmployeeService salaryGroupRelEmpService;


    @Override
    public BasePage<SalaryGroupPageListVO> querySalaryGroupPageList(PageEntity pageEntity) {
        BasePage<HrmSalaryGroup> salaryGroupBasePage = salaryGroupMapper.selectPage(pageEntity.parse(), Wrappers.emptyWrapper());
        List<SalaryGroupPageListVO> salaryGroupPageListVOList = new ArrayList<>();
        salaryGroupBasePage.getList().forEach(salaryGroup -> {
            HrmSalaryTaxRule taxRule = taxRuleService.lambdaQuery().select(HrmSalaryTaxRule::getRuleName)
                    .eq(HrmSalaryTaxRule::getRuleId, salaryGroup.getRuleId()).one();
            SalaryGroupPageListVO salaryGroupPageListVO = new SalaryGroupPageListVO();
            BeanUtil.copyProperties(salaryGroup, salaryGroupPageListVO);
            salaryGroupPageListVO.setRuleName(taxRule.getRuleName());
            Set<Long> deptIds = salaryGroupRelDeptService.lambdaQuery().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, salaryGroup.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationDept::getDeptId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(deptIds)) {
                salaryGroupPageListVO.setDeptList(deptService.querySimpleDeptList(deptIds));
            }
            Set<Long> employeeIds = salaryGroupRelEmpService.lambdaQuery().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, salaryGroup.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(employeeIds)) {
                salaryGroupPageListVO.setEmployeeList(employeeService.querySimpleEmployeeList(employeeIds));
            }
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("groupName_resourceKey", "hrm.salary.group." + salaryGroupPageListVO.getGroupName());
            keyMap.put("ruleName_resourcekey", "hrm.salary.tax.rule." + salaryGroupPageListVO.getRuleName());
            keyMap.put("changeRule_resourceKey", "hrm.salary.change.rule." + salaryGroupPageListVO.getChangeRule());
            salaryGroupPageListVO.setLanguageKeyMap(keyMap);
            salaryGroupPageListVOList.add(salaryGroupPageListVO);
        });
        BasePage<SalaryGroupPageListVO> page = new BasePage<>(salaryGroupBasePage.getCurrent(), salaryGroupBasePage.getSize(), salaryGroupBasePage.getTotal());
        page.setList(salaryGroupPageListVOList);
        return page;
    }

    @Override
    public HrmSalaryTaxRule queryEmployeeTaxRule(Long employeeId) {
        HrmEmployee employee = employeeService.getById(employeeId);
        Long salaryGroupId;
        HrmSalaryGroupRelationEmployee hrmSalaryGroupRelationEmployee = salaryGroupRelEmpService.lambdaQuery().eq(HrmSalaryGroupRelationEmployee::getEmployeeId, employeeId).one();
        if (ObjectUtil.isNull(hrmSalaryGroupRelationEmployee)) {
            Set<Long> deptIds = deptService.queryParentDeptId(employee.getDeptId());
            HrmSalaryGroupRelationDept hrmSalaryGroupRelationDept = salaryGroupRelDeptService.lambdaQuery().in(HrmSalaryGroupRelationDept::getDeptId, deptIds).one();
            salaryGroupId = hrmSalaryGroupRelationDept.getSalaryGroupId();
        } else {
            salaryGroupId = hrmSalaryGroupRelationEmployee.getSalaryGroupId();
        }
        HrmSalaryGroup salaryGroup = salaryGroupMapper.selectById(salaryGroupId);
        return taxRuleService.getById(salaryGroup.getRuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog setSalaryGroup(SetSalaryGroupBO salaryGroup) {
        List<HrmSalaryGroup> salaryGroupList = lambdaQuery().ne(salaryGroup.getGroupId() != null, HrmSalaryGroup::getGroupId, salaryGroup.getGroupId()).list();
        List<Long> addDeptIds = salaryGroup.getDeptIds();
        List<Long> addEmployeeIds = salaryGroup.getEmployeeIds();
        salaryGroupList.forEach(salaryGroup1 -> {
            Set<Long> deptIds = salaryGroupRelDeptService.lambdaQuery().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, salaryGroup1.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationDept::getDeptId).collect(Collectors.toSet());
            Set<Long> employeeIds = salaryGroupRelEmpService.lambdaQuery().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, salaryGroup1.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
            Collection<Long> empIntersection = CollUtil.intersection(addEmployeeIds, employeeIds);
            if (!empIntersection.isEmpty()) {
                throw new CrmException(HrmCodeEnum.THE_SALARY_GROUP_ALREADY_HAS_SELECTED_EMPLOYEE);
            }
            Set<Long> list = deptService.queryChildDeptId(deptIds);
            Set<Long> addList = deptService.queryChildDeptId(addDeptIds);
            Collection<Long> deptIntersection = CollUtil.intersection(list, addList);
            if (!deptIntersection.isEmpty()) {
                throw new CrmException(HrmCodeEnum.THE_SALARY_GROUP_ALREADY_HAS_SELECTED_DEPT);
            }
        });
        HrmSalaryGroup salaryGroup1 = BeanUtil.copyProperties(salaryGroup, HrmSalaryGroup.class);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(salaryGroup1.getGroupName());
        if (salaryGroup1.getGroupId() == null) {
            operationLog.setOperationInfo("新建薪资组：" + salaryGroup1.getGroupName());
        } else {
            operationLog.setOperationInfo("编辑薪资组：" + salaryGroup1.getGroupName());
        }

        saveOrUpdate(salaryGroup1);
        salaryGroupRelDeptService.lambdaUpdate().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, salaryGroup1.getGroupId())
                .notIn(CollUtil.isNotEmpty(salaryGroup.getDeptIds()), HrmSalaryGroupRelationDept::getDeptId, salaryGroup.getDeptIds()).remove();
        salaryGroupRelEmpService.lambdaUpdate().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, salaryGroup1.getGroupId())
                .notIn(CollUtil.isNotEmpty(salaryGroup.getEmployeeIds()), HrmSalaryGroupRelationEmployee::getEmployeeId, salaryGroup.getEmployeeIds()).remove();
        Set<Long> existDeptIds;
        Set<Long> existEmployeeIds;
        if (CollUtil.isNotEmpty(salaryGroup.getDeptIds())) {
            existDeptIds = salaryGroupRelDeptService.lambdaQuery().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, salaryGroup1.getGroupId())
                    .list().stream().map(HrmSalaryGroupRelationDept::getDeptId).collect(Collectors.toSet());
            Collection<Long> disjunctionDeptIds = CollUtil.disjunction(salaryGroup.getDeptIds(), existDeptIds);
            List<HrmSalaryGroupRelationDept> hrmSalaryGroupRelationDeptList = disjunctionDeptIds.stream()
                    .map(deptId -> HrmSalaryGroupRelationDept.builder().deptId(deptId)
                            .salaryGroupId(salaryGroup1.getGroupId()).build()).collect(Collectors.toList());
            salaryGroupRelDeptService.saveBatch(hrmSalaryGroupRelationDeptList);
        }
        if (CollUtil.isNotEmpty(salaryGroup.getEmployeeIds())) {
            existEmployeeIds = salaryGroupRelEmpService.lambdaQuery().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, salaryGroup1.getGroupId())
                    .list().stream().map(HrmSalaryGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
            Collection<Long> disjunctionEmployeeIds = CollUtil.disjunction(salaryGroup.getEmployeeIds(), existEmployeeIds);
            List<HrmSalaryGroupRelationEmployee> hrmSalaryGroupRelationEmployeeList = disjunctionEmployeeIds.stream()
                    .map(employeeId -> HrmSalaryGroupRelationEmployee.builder().employeeId(employeeId)
                            .salaryGroupId(salaryGroup1.getGroupId()).build()).collect(Collectors.toList());
            salaryGroupRelEmpService.saveBatch(hrmSalaryGroupRelationEmployeeList);
        }

        return operationLog;
    }

    @Override
    public List<HrmSalaryGroup> querySalaryGroupByTaxType(int taxType) {
        return getBaseMapper().querySalaryGroupByTaxType(taxType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteSalaryGroup(Long groupId) {

        HrmSalaryGroup byId = getById(groupId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(byId.getGroupName());
        operationLog.setOperationInfo("删除薪资组：" + byId.getGroupName());

        baseMapper.deleteById(groupId);
        //关联表删除
        salaryGroupRelEmpService.lambdaUpdate().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, groupId).remove();
        salaryGroupRelDeptService.lambdaUpdate().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, groupId).remove();

        return operationLog;
    }
}
