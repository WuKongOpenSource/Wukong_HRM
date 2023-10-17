package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.enums.DataAuthEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.RecursionUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.entity.BO.AddDeptBO;
import com.kakarote.hrm.entity.BO.QueryDeptListBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeByDeptIdBO;
import com.kakarote.hrm.entity.PO.HrmDept;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.VO.DeptEmployeeVO;
import com.kakarote.hrm.entity.VO.DeptVO;
import com.kakarote.hrm.entity.VO.QueryEmployeeListByDeptIdVO;
import com.kakarote.hrm.entity.VO.SimpleHrmDeptVO;
import com.kakarote.hrm.mapper.HrmDeptMapper;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import com.kakarote.hrm.utils.EmployeeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Slf4j
@Service
public class HrmDeptServiceImpl extends BaseServiceImpl<HrmDeptMapper, HrmDept> implements IHrmDeptService {

    @Autowired
    private HrmDeptMapper deptMapper;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Override
    public void addOrUpdate(AddDeptBO addDeptBO) {
        HrmDept hrmDept = BeanUtil.copyProperties(addDeptBO, HrmDept.class);
        boolean exists;
        if (addDeptBO.getDeptId() == null) {
            exists = lambdaQuery().eq(HrmDept::getCode, addDeptBO.getCode()).exists();
        } else {
            exists = lambdaQuery().eq(HrmDept::getCode, addDeptBO.getCode()).ne(HrmDept::getDeptId, addDeptBO.getDeptId()).exists();
        }
        if (exists) {
            throw new CrmException(HrmCodeEnum.DEPT_CODE_ALREADY_EXISTS);
        }
        saveOrUpdate(hrmDept);
    }

    @Override
    public DeptVO queryById(Long deptId) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        boolean exists = false;
        if (CollUtil.isNotEmpty(employeeIds)) {
            exists = employeeService.lambdaQuery().eq(HrmEmployee::getDeptId, deptId).in(HrmEmployee::getEmployeeId, employeeIds).exists();
        }
        if (exists) {
            return deptMapper.queryById(deptId, employeeIds);
        }
        return deptMapper.queryNoEmployeeDept(deptId);
    }

    @Override
    public List<DeptVO> queryTreeList(QueryDeptListBO queryDeptListBO) {
        Integer dataAuthType = DataAuthEnum.ALL.getValue();
        List<DeptVO> deptVOList = new ArrayList<>();
        if (EmployeeHolder.isHrmAdmin() || DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            String tree = "tree";
            if (tree.equals(queryDeptListBO.getType()) || queryDeptListBO.getType() == null) {
                List<DeptVO> deptList = deptMapper.queryList(queryDeptListBO);
                if (StrUtil.isNotEmpty(queryDeptListBO.getName())) {
                    List<HrmDept> list = lambdaQuery().select(HrmDept::getDeptId).like(HrmDept::getName, queryDeptListBO.getName()).list();
                    for (HrmDept dept : list) {
                        deptVOList.addAll(createTree1(dept.getDeptId(), deptList));
                    }
                } else {
                    deptVOList = createTree(0L, deptList);
                }
            }
            String update = "update";
            if (update.equals(queryDeptListBO.getType())) {
                List<HrmDept> deptList = list();
                deptVOList = deptList.stream().map(dept -> {
                    DeptVO deptVO = new DeptVO();
                    deptVO.setDeptId(dept.getDeptId());
                    deptVO.setName(dept.getName());
                    deptVO.setParentId(dept.getParentId());
                    return deptVO;
                }).collect(Collectors.toList());
                List<Long> ids = RecursionUtil.getChildList(deptVOList, "parentId", queryDeptListBO.getId(), "deptId", "deptId");
                ids.add(queryDeptListBO.getId());
                deptVOList.removeIf(dept -> ids.contains(dept.getDeptId()));
            }
        } else {
            if (dataAuthType != null) {
                deptVOList = getDataAuthDeptList(queryDeptListBO, dataAuthType);
            }
        }
        return deptVOList;
    }

    /**
     * 部门数据权限搜索
     */
    private List<DeptVO> getDataAuthDeptList(QueryDeptListBO queryDeptListBO, Integer dataAuthType) {
        List<DeptVO> deptVOList = new ArrayList<>();
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpId(dataAuthType);
        if (CollUtil.isEmpty(employeeIds)) {
            return deptVOList;
        }
        List<DeptVO> queryDept = deptMapper.queryDeptByEmpIds(employeeIds);
        Map<Long, DeptVO> deptIdMap = queryDept.stream().collect(Collectors.toMap(DeptVO::getDeptId, e -> e));
        Set<Long> deptIds = employeeUtil.queryDataAuthDeptId(dataAuthType);
        List<DeptVO> deptList = deptMapper.queryList(queryDeptListBO);
        for (DeptVO deptVO : deptList) {
            if (deptIdMap.containsKey(deptVO.getDeptId())) {
                DeptVO dept = deptIdMap.get(deptVO.getDeptId());
                deptVO.setAllNum(dept.getAllNum());
                deptVO.setMyAllNum(dept.getMyAllNum());
                deptVO.setFullTimeNum(dept.getFullTimeNum());
                deptVO.setMyFullTimeNum(dept.getMyFullTimeNum());
                deptVO.setNuFullTimeNum(dept.getNuFullTimeNum());
                deptVO.setMyNuFullTimeNum(dept.getNuFullTimeNum());
            } else {
                deptVO.setAllNum(0);
                deptVO.setMyAllNum(0);
                deptVO.setFullTimeNum(0);
                deptVO.setMyFullTimeNum(0);
                deptVO.setNuFullTimeNum(0);
                deptVO.setMyNuFullTimeNum(0);
            }
        }
        if (StrUtil.isNotEmpty(queryDeptListBO.getName())) {
            List<HrmDept> list = lambdaQuery().select(HrmDept::getDeptId).in(HrmDept::getDeptId, deptIds).like(HrmDept::getName, queryDeptListBO.getName()).list();
            for (HrmDept dept : list) {
                deptVOList.addAll(createTree1(dept.getDeptId(), deptList));
            }
            return deptVOList;
        } else {
            List<DeptVO> treeDept = createTree(0L, deptList);
            DeptVO root = new DeptVO();
            if (CollUtil.isNotEmpty(treeDept)) {
                filterNode(root, treeDept.get(0), deptIds);
            }
            deptVOList = root.getChildren();
            return deptVOList;
        }
    }

    /**
     * 树结构根据指定部门重组
     *
     * @param result      返回接过
     * @param node        全部树结构
     * @param findDeptIds 需要筛选的部门id
     */
    private static void filterNode(DeptVO result, DeptVO node, Set<Long> findDeptIds) {
        List<DeptVO> childList = node.getChildren();
        if (findDeptIds.contains(node.getDeptId())) {
            DeptVO newNode = BeanUtil.copyProperties(node, DeptVO.class);
            newNode.setChildren(null);
            if (CollUtil.isEmpty(result.getChildren())) {
                result.setChildren(Arrays.asList(newNode));
            } else {
                List<DeptVO> childList1 = result.getChildren();
                childList1.add(newNode);
            }
            findDeptIds.remove(node.getDeptId());
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            for (DeptVO deptVO : childList) {
                filterNode(newNode, deptVO, findDeptIds);
            }
        } else {
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            for (DeptVO deptVO : childList) {
                filterNode(result, deptVO, findDeptIds);
            }
        }
    }

    private List<DeptVO> createTree(Long pid, List<DeptVO> deptList) {
        List<DeptVO> treeDept = new ArrayList<>();
        for (DeptVO dept : deptList) {
            if (pid.equals(dept.getParentId())) {
                treeDept.add(dept);
                List<DeptVO> children = createTree(dept.getDeptId(), deptList);
                for (DeptVO child : children) {
                    dept.setAllNum(dept.getAllNum() + child.getAllNum());
                    dept.setFullTimeNum(dept.getFullTimeNum() + child.getFullTimeNum());
                    dept.setNuFullTimeNum(dept.getNuFullTimeNum() + child.getNuFullTimeNum());
                }
                dept.setChildren(children);
            }
        }
        return treeDept;
    }

    private List<DeptVO> createTree1(Long id, List<DeptVO> deptList) {
        List<DeptVO> treeDept = new ArrayList<>();
        for (DeptVO dept : deptList) {
            if (id.equals(dept.getDeptId())) {
                treeDept.add(dept);
                List<DeptVO> children = createTree(dept.getDeptId(), deptList);
                for (DeptVO child : children) {
                    dept.setAllNum(dept.getAllNum() + child.getAllNum());
                    dept.setFullTimeNum(dept.getFullTimeNum() + child.getFullTimeNum());
                    dept.setNuFullTimeNum(dept.getNuFullTimeNum() + child.getNuFullTimeNum());
                }
            }
        }
        return treeDept;
    }

    @Override
    public List<SimpleHrmDeptVO> querySimpleDeptList(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return new ArrayList<>();
        }
        return lambdaQuery().select(HrmDept::getDeptId, HrmDept::getName)
                .in(HrmDept::getDeptId, deptIds)
                .list()
                .stream().map(dept -> {
                    SimpleHrmDeptVO simpleHrmDeptVO = new SimpleHrmDeptVO();
                    simpleHrmDeptVO.setDeptId(dept.getDeptId());
                    simpleHrmDeptVO.setDeptName(dept.getName());
                    return simpleHrmDeptVO;
                }).collect(Collectors.toList());
    }

    @Override
    public SimpleHrmDeptVO querySimpleDept(Long deptId) {
        if (deptId == null) {
            return new SimpleHrmDeptVO();
        }
        HrmDept hrmDept = lambdaQuery().select(HrmDept::getDeptId, HrmDept::getName).eq(HrmDept::getDeptId, deptId).one();
        SimpleHrmDeptVO simpleHrmDeptVO = new SimpleHrmDeptVO();
        simpleHrmDeptVO.setDeptId(hrmDept.getDeptId());
        simpleHrmDeptVO.setDeptName(hrmDept.getName());
        return simpleHrmDeptVO;
    }

    @Override
    public BasePage<QueryEmployeeListByDeptIdVO> queryEmployeeByDeptId(QueryEmployeeByDeptIdBO employeeByDeptIdBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.DEPT_MENU_ID);
        return deptMapper.queryEmployeeByDeptId(employeeByDeptIdBO.parse(), employeeByDeptIdBO, employeeIds);
    }

    @Override
    public void deleteDeptById(String deptId) {
        boolean exists = employeeService.lambdaQuery().in(HrmEmployee::getEntryStatus, 1, 3).eq(HrmEmployee::getDeptId, deptId).eq(HrmEmployee::getIsDel, 0).exists();
        if (exists) {
            throw new CrmException(HrmCodeEnum.THERE_ARE_EMPLOYEES_UNDER_THE_DEPARTMENT);
        }
        boolean childExists = lambdaQuery().eq(HrmDept::getParentId, deptId).exists();
        if (childExists) {
            throw new CrmException(HrmCodeEnum.THERE_ARE_SUB_DEPARTMENTS_THAT_CANNOT_BE_DELETED);
        }
        HrmDept dept = getById(deptId);
        String one = "1";
        if (one.equals(dept.getCode())) {
            throw new CrmException(HrmCodeEnum.TOP_LEVEL_DEPARTMENT_CANNOT_BE_DELETED);
        }
        removeById(deptId);
    }

    @Override
    public Set<Long> queryChildDeptId(Collection<Long> deptIds) {
        Set<Long> newDeptIds = new HashSet<>(deptIds);
        if (CollUtil.isNotEmpty(deptIds)) {
            deptIds.forEach(deptId -> {
                List<Long> childList = RecursionUtil.getChildList(list(), "parentId", deptId, "deptId", "deptId");
                newDeptIds.addAll(childList);
            });
        }
        return newDeptIds;
    }

    @Override
    public Set<Long> queryParentDeptId(Long pid) {
        Set<Long> deptIds = new HashSet<>();
        deptIds.add(pid);
        Optional<HrmDept> hrmDeptOpt = lambdaQuery().select(HrmDept::getDeptId, HrmDept::getParentId).eq(HrmDept::getDeptId, pid).oneOpt();
        if (hrmDeptOpt.isPresent()) {
            HrmDept hrmDept = hrmDeptOpt.get();
            if (ObjectUtil.notEqual(0L, hrmDept.getParentId())) {
                Set<Long> integers = queryParentDeptId(hrmDept.getParentId());
                deptIds.addAll(integers);
            }
        }
        return deptIds;
    }

    @Override
    public List<DeptEmployeeVO> queryDeptEmployeeList() {
        return deptMapper.queryDeptEmployeeList();
    }
}
