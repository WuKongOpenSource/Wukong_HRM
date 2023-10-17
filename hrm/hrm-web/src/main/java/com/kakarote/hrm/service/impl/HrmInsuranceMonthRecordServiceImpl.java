package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.enums.DataAuthEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TransferUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.IsEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.entity.BO.QueryInsurancePageListBO;
import com.kakarote.hrm.entity.BO.QueryInsuranceRecordListBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.QueryInsurancePageListVO;
import com.kakarote.hrm.entity.VO.QueryInsuranceRecordListVO;
import com.kakarote.hrm.mapper.HrmInsuranceMonthRecordMapper;
import com.kakarote.hrm.mapper.HrmInsuranceSchemeMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.insuranceActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import com.kakarote.hrm.utils.EmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 每月社保记录 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@Service
public class HrmInsuranceMonthRecordServiceImpl extends BaseServiceImpl<HrmInsuranceMonthRecordMapper, HrmInsuranceMonthRecord> implements IHrmInsuranceMonthRecordService {

    @Autowired
    private HrmInsuranceMonthRecordMapper insuranceMonthRecordMapper;

    @Autowired
    private IHrmInsuranceMonthEmpRecordService monthEmpRecordService;

    @Autowired
    private IHrmInsuranceMonthEmpProjectRecordService monthEmpProjectRecordService;

    @Autowired
    private HrmInsuranceSchemeMapper insuranceSchemeMapper;

    @Autowired
    private IHrmInsuranceProjectService insuranceProjectService;

    @Autowired
    private IHrmSalaryConfigService salaryConfigService;

    @Resource
    private insuranceActionRecordServiceImpl insuranceActionRecordService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Override
    public JSONObject computeInsuranceData() {
        HrmSalaryConfig salaryConfig = salaryConfigService.getOne(Wrappers.emptyWrapper());
        if (salaryConfig == null) {
            throw new CrmException(HrmCodeEnum.NO_INITIAL_CONFIGURATION);
        }
        String socialSecurityMonth = salaryConfig.getSocialSecurityStartMonth();
        DateTime dateTime = DateUtil.parse(socialSecurityMonth, "yyyy-MM");
        int month = dateTime.month() + 1;
        int year = dateTime.year();
        //查询社保上月记录,如果有就往后推一个月,如果没有就去薪资配置计薪月
        Optional<HrmInsuranceMonthRecord> lastMonthRecord = lambdaQuery().orderByDesc(HrmInsuranceMonthRecord::getCreateTime).oneOpt();
        if (lastMonthRecord.isPresent()) {
            HrmInsuranceMonthRecord insuranceMonthRecord = lastMonthRecord.get();
            DateTime date = DateUtil.offsetMonth(DateUtil.parse(insuranceMonthRecord.getYear() + "-" + insuranceMonthRecord.getMonth(), "yy-MM"), 1);
            month = date.month() + 1;
            year = date.year();
            List<Long> empRecordIds = insuranceMonthRecordMapper.queryDeleteEmpRecordIds(insuranceMonthRecord.getIRecordId());
            if (CollUtil.isNotEmpty(empRecordIds)) {
                monthEmpProjectRecordService.lambdaUpdate().in(HrmInsuranceMonthEmpProjectRecord::getIEmpRecordId, empRecordIds).remove();
                monthEmpRecordService.lambdaUpdate().in(HrmInsuranceMonthEmpRecord::getIEmpRecordId, empRecordIds).remove();
            }
            insuranceMonthRecord.setStatus(IsEnum.YES.getValue());
            updateById(insuranceMonthRecord);
        }
        List<Map<String, Long>> employeeIds = insuranceMonthRecordMapper.queryInsuranceEmployee();
        HrmInsuranceMonthRecord hrmInsuranceMonthRecord = new HrmInsuranceMonthRecord();
        hrmInsuranceMonthRecord.setTitle(month + "月社保报表");
        hrmInsuranceMonthRecord.setYear(year);
        hrmInsuranceMonthRecord.setMonth(month);
        hrmInsuranceMonthRecord.setNum(employeeIds.size());
        save(hrmInsuranceMonthRecord);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmInsuranceMonthRecord.getIRecordId(), hrmInsuranceMonthRecord.getTitle());
        operationLog.setOperationInfo("新建" + hrmInsuranceMonthRecord.getTitle());

        insuranceActionRecordService.computeInsuranceDataLog(hrmInsuranceMonthRecord);
        int finalYear = year;
        int finalMonth = month;
        employeeIds.forEach(employeeMap -> {
            Long employeeId = employeeMap.get("employeeId");
            Long schemeId = employeeMap.get("schemeId");
            Map<String, Object> stringObjectMap = insuranceSchemeMapper.queryInsuranceSchemeCountById(schemeId);
            HrmInsuranceMonthEmpRecord insuranceMonthEmpRecord = new HrmInsuranceMonthEmpRecord();
            BeanUtil.fillBeanWithMap(stringObjectMap, insuranceMonthEmpRecord, true);
            insuranceMonthEmpRecord.setIRecordId(hrmInsuranceMonthRecord.getIRecordId());
            insuranceMonthEmpRecord.setEmployeeId(employeeId);
            insuranceMonthEmpRecord.setSchemeId(schemeId);
            insuranceMonthEmpRecord.setYear(finalYear);
            insuranceMonthEmpRecord.setMonth(finalMonth);
            monthEmpRecordService.save(insuranceMonthEmpRecord);
            //发送通知
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateUser(UserUtil.getUserId());
            adminMessage.setCreateTime(LocalDateTime.now());
            adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employeeId));
            adminMessage.setLabel(8);
            adminMessage.setType(AdminMessageEnum.HRM_EMPLOYEE_INSURANCE.getType());
            adminMessage.setTitle(finalYear + "-" + finalMonth + "{admin.hrm.9e06b58abc0ca454d6f1463aa168010c}");
            ApplicationContextHolder.getBean(IAdminMessageService.class).save(adminMessage);
            List<HrmInsuranceProject> insuranceProjectList = insuranceProjectService.lambdaQuery().eq(HrmInsuranceProject::getSchemeId, schemeId).list();
            List<HrmInsuranceMonthEmpProjectRecord> monthEmpProjectRecordList = TransferUtil.transferList(insuranceProjectList, HrmInsuranceMonthEmpProjectRecord.class);
            monthEmpProjectRecordList.forEach(monthEmpProjectRecord -> {
                monthEmpProjectRecord.setIEmpRecordId(insuranceMonthEmpRecord.getIEmpRecordId());
            });
            monthEmpProjectRecordService.saveBatch(monthEmpProjectRecordList);
        });
        JSONObject data = new JSONObject();
        data.put("year", year);
        data.put("operationLog", operationLog);

        return data;
    }

    @Override
    public BasePage<QueryInsuranceRecordListVO> queryInsuranceRecordList(QueryInsuranceRecordListBO recordListBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.INSURANCE_MENU_ID);
        Integer dataAuthType = DataAuthEnum.ALL.getValue();
        boolean isAll = EmployeeHolder.isHrmAdmin() || DataAuthEnum.ALL.getValue().equals(dataAuthType);
        return insuranceMonthRecordMapper.queryInsuranceRecordList(recordListBO.parse(), recordListBO, employeeIds, isAll);
    }

    @Override
    public BasePage<QueryInsurancePageListVO> queryInsurancePageList(QueryInsurancePageListBO queryInsurancePageListBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.INSURANCE_MENU_ID);
        return insuranceMonthRecordMapper.queryInsurancePageList(queryInsurancePageListBO.parse(), queryInsurancePageListBO, employeeIds);
    }

    @Override
    public QueryInsuranceRecordListVO queryInsuranceRecord(String iRecordId) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.INSURANCE_MENU_ID);
        boolean exists = false;
        if (CollUtil.isNotEmpty(employeeIds)) {
            exists = monthEmpRecordService.lambdaQuery().eq(HrmInsuranceMonthEmpRecord::getIRecordId, iRecordId)
                    .in(HrmInsuranceMonthEmpRecord::getEmployeeId, employeeIds).exists();
        }
        if (exists) {
            return insuranceMonthRecordMapper.queryInsuranceRecord(iRecordId, employeeIds);
        }
        return insuranceMonthRecordMapper.queryNoEmpInsuranceRecord(iRecordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteInsurance(Long iRecordId) {
        Integer count = lambdaQuery().count().intValue();
        if (count == 1) {
            throw new CrmException(HrmCodeEnum.INSURANCE_CANNOT_BE_DELETED);
        }

        HrmInsuranceMonthRecord insuranceMonthRecord = getById(iRecordId);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(insuranceMonthRecord.getIRecordId(), insuranceMonthRecord.getTitle());
        operationLog.setOperationInfo("删除" + insuranceMonthRecord.getTitle());

        List<Long> iEmpRecordIds = monthEmpRecordService.lambdaQuery().select(HrmInsuranceMonthEmpRecord::getIEmpRecordId).eq(HrmInsuranceMonthEmpRecord::getIRecordId, iRecordId).list()
                .stream().map(HrmInsuranceMonthEmpRecord::getIEmpRecordId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(iEmpRecordIds)) {
            monthEmpProjectRecordService.lambdaUpdate().in(HrmInsuranceMonthEmpProjectRecord::getIEmpRecordId, iEmpRecordIds).remove();
            monthEmpRecordService.lambdaUpdate().in(HrmInsuranceMonthEmpRecord::getIEmpRecordId, iEmpRecordIds).remove();
        }
        removeById(iRecordId);
        HrmInsuranceMonthRecord monthRecord = lambdaQuery().orderByDesc(HrmInsuranceMonthRecord::getCreateTime).one();
        monthRecord.setStatus(0);
        updateById(monthRecord);
        insuranceActionRecordService.deleteInsurance(monthRecord);
        return operationLog;
    }
}
