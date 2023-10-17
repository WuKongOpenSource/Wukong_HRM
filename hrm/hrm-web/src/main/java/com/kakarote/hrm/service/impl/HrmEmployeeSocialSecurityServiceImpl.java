package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.LabelGroupEnum;
import com.kakarote.hrm.entity.BO.QuerySalaryListBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.QuerySalaryListVO;
import com.kakarote.hrm.entity.VO.SalaryOptionHeadVO;
import com.kakarote.hrm.entity.VO.SalarySocialSecurityVO;
import com.kakarote.hrm.mapper.HrmEmployeeSocialSecurityMapper;
import com.kakarote.hrm.mapper.HrmSalaryMonthEmpRecordMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.EmployeeActionRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 员工公积金信息 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmEmployeeSocialSecurityServiceImpl extends BaseServiceImpl<HrmEmployeeSocialSecurityMapper, HrmEmployeeSocialSecurityInfo> implements IHrmEmployeeSocialSecurityService {

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmEmployeeSalaryCardService salaryCardService;

    @Autowired
    private IHrmInsuranceSchemeService insuranceSchemeService;

    @Autowired
    private HrmSalaryMonthEmpRecordMapper salaryMonthEmpRecordMapper;

    @Autowired
    private IHrmSalaryMonthOptionValueService salaryMonthOptionValueService;

    @Autowired
    private IHrmSalaryMonthRecordService salaryMonthRecordService;

    @Autowired
    private IHrmInsuranceMonthEmpRecordService insuranceMonthEmpRecordService;

    @Resource
    private EmployeeActionRecordServiceImpl employeeActionRecordService;

    @Override
    public SalarySocialSecurityVO salarySocialSecurityInformation(Long employeeId) {
        HrmEmployeeSalaryCard employeeSalaryCard = salaryCardService.lambdaQuery().eq(HrmEmployeeSalaryCard::getEmployeeId, employeeId).one();
        HrmEmployeeSocialSecurityInfo securityInfo = lambdaQuery().eq(HrmEmployeeSocialSecurityInfo::getEmployeeId, employeeId).one();
        if (securityInfo == null) {
            securityInfo = new HrmEmployeeSocialSecurityInfo();
            securityInfo.setEmployeeId(employeeId);
            save(securityInfo);
        }
        if (securityInfo.getSocialSecurityStartMonth() == null) {
            Optional<HrmInsuranceMonthEmpRecord> insuranceMonthEmpRecordOpt = insuranceMonthEmpRecordService.lambdaQuery()
                    .eq(HrmInsuranceMonthEmpRecord::getEmployeeId, employeeId).orderByAsc(HrmInsuranceMonthEmpRecord::getCreateTime)
                    .oneOpt();
            if (insuranceMonthEmpRecordOpt.isPresent()) {
                HrmInsuranceMonthEmpRecord insuranceMonthEmpRecord = insuranceMonthEmpRecordOpt.get();
                String socialSecurityStartMonth = insuranceMonthEmpRecord.getYear() + "." + insuranceMonthEmpRecord.getMonth();
                securityInfo.setSocialSecurityStartMonth(socialSecurityStartMonth);
                updateById(securityInfo);
            }
        }
        if (securityInfo.getSchemeId() != null) {
            HrmInsuranceScheme insuranceScheme = insuranceSchemeService.lambdaQuery().select(HrmInsuranceScheme::getSchemeName).eq(HrmInsuranceScheme::getSchemeId, securityInfo.getSchemeId()).one();
            if (insuranceScheme != null) {
                securityInfo.setSchemeName(insuranceScheme.getSchemeName());
            }
        }
        return new SalarySocialSecurityVO(employeeSalaryCard, securityInfo);
    }

    @Override
    public void addOrUpdateSalaryCard(HrmEmployeeSalaryCard salaryCard) {
        if (salaryCard.getSalaryCardId() == null) {
            employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.SALARY_CARD, salaryCard.getEmployeeId());
        } else {
            HrmEmployeeSalaryCard old = salaryCardService.getById(salaryCard.getSalaryCardId());
            employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.SALARY_CARD, BeanUtil.beanToMap(old), BeanUtil.beanToMap(salaryCard), salaryCard.getEmployeeId());
        }
        salaryCardService.saveOrUpdate(salaryCard);
    }

    @Override
    public void deleteSalaryCard(Long salaryCardId) {
        salaryCardService.removeById(salaryCardId);
    }

    @Override
    public OperationLog addOrUpdateSocialSecurity(HrmEmployeeSocialSecurityInfo socialSecurityInfo) {
        OperationLog operationLog = null;
        if (socialSecurityInfo.getSocialSecurityInfoId() == null) {
            employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.SOCIAL_SECURITY, socialSecurityInfo.getEmployeeId());
        } else {
            HrmEmployeeSocialSecurityInfo old = getById(socialSecurityInfo.getSocialSecurityInfoId());
            employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.SOCIAL_SECURITY, BeanUtil.beanToMap(old), BeanUtil.beanToMap(socialSecurityInfo), socialSecurityInfo.getEmployeeId());

            if (ObjectUtil.notEqual(old.getSchemeId(), socialSecurityInfo.getSchemeId())) {
                String oldSchemeName = "空";
                if (old.getSchemeId() != null) {
                    HrmInsuranceScheme oldScheme = insuranceSchemeService.getById(old.getSchemeId());
                    oldSchemeName = oldScheme.getSchemeName();
                }
                HrmInsuranceScheme newScheme = insuranceSchemeService.getById(socialSecurityInfo.getSchemeId());
                String newSchemeName = newScheme.getSchemeName();
                HrmEmployee employee = employeeService.getById(socialSecurityInfo.getEmployeeId());

                operationLog = new OperationLog();
                operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
                operationLog.setOperationInfo("为" + employee.getEmployeeName() + "修改了参保方案，由【" + oldSchemeName + "】修改为【" + newSchemeName + "】");
                operationLog.setApply(ApplyEnum.HRM);
                operationLog.setApplyObject(OperateObjectEnum.HRM_EMPLOYEE);
                operationLog.setBehavior(BehaviorEnum.UPDATE_INSURANCE_SCHEME);
            }
        }
        saveOrUpdate(socialSecurityInfo);

        return operationLog;
    }

    @Override
    public void deleteSocialSecurity(Long socialSecurityInfoId) {
        removeById(socialSecurityInfoId);
    }

    @Override
    public BasePage<QuerySalaryListVO> querySalaryList(QuerySalaryListBO querySalaryListBO) {
        BasePage<QuerySalaryListVO> page = salaryMonthEmpRecordMapper.querySalaryRecord(querySalaryListBO.parse(), querySalaryListBO.getEmployeeId());
        page.getList().forEach(record -> {
            Long sEmpRecordId = record.getSEmpRecordId();
            List<HrmSalaryMonthOptionValue> list = salaryMonthOptionValueService.lambdaQuery()
                    .in(HrmSalaryMonthOptionValue::getCode, ListUtil.toList(210101, 230101, 240101))
                    .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, sEmpRecordId).list();
            for (HrmSalaryMonthOptionValue salaryMonthOptionValue : list) {
                if (salaryMonthOptionValue.getCode().equals(210101)) {
                    record.setShouldSalary(salaryMonthOptionValue.getValue());
                }
                if (salaryMonthOptionValue.getCode().equals(230101)) {
                    record.setPersonalTax(salaryMonthOptionValue.getValue());
                }
                if (salaryMonthOptionValue.getCode().equals(240101)) {
                    record.setRealSalary(salaryMonthOptionValue.getValue());
                }
            }
        });
        return page;
    }

    @Override
    public List<SalaryOptionHeadVO> querySalaryDetail(String sEmpRecordId) {
        HrmSalaryMonthEmpRecord salaryMonthEmpRecord = salaryMonthEmpRecordMapper.selectById(sEmpRecordId);
        HrmSalaryMonthRecord salaryMonthRecord = salaryMonthRecordService.getById(salaryMonthEmpRecord.getSRecordId());
        String optionHead = salaryMonthRecord.getOptionHead();
        List<SalaryOptionHeadVO> salaryOptionHeadVOList = JSON.parseArray(optionHead, SalaryOptionHeadVO.class);
        List<HrmSalaryMonthOptionValue> list = salaryMonthOptionValueService.lambdaQuery().eq(HrmSalaryMonthOptionValue::getSEmpRecordId, sEmpRecordId).list();
        HashMap<Integer, String> codeMap = new HashMap<>();
        list.forEach(value -> {
            codeMap.put(value.getCode(), value.getValue());
        });
        int two = 2;
        salaryOptionHeadVOList.forEach(option -> {
            if (option.getCode().equals(1)) {
                option.setValue(salaryMonthEmpRecord.getNeedWorkDay().toString());
            } else if (option.getCode().equals(two)) {
                option.setValue(salaryMonthEmpRecord.getActualWorkDay().toString());
            } else {
                option.setValue(codeMap.get(option.getCode()));
            }
        });
        return salaryOptionHeadVOList;
    }
}
