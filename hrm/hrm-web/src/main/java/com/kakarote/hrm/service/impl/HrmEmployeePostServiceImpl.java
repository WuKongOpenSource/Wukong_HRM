package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.DeleteLeaveInformationBO;
import com.kakarote.hrm.entity.BO.UpdateInformationBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.HrmModelFiledVO;
import com.kakarote.hrm.entity.VO.InformationFieldVO;
import com.kakarote.hrm.entity.VO.PostInformationVO;
import com.kakarote.hrm.mapper.HrmEmployeePostMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.EmployeeActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeUtil;
import com.kakarote.hrm.utils.FieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工证书 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmEmployeePostServiceImpl extends BaseServiceImpl<HrmEmployeePostMapper, HrmEmployeeCertificate> implements IHrmEmployeePostService {

    @Autowired
    private IHrmEmployeeQuitInfoService quitInfoService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmEmployeeDataService employeeDataService;

    @Autowired
    private IHrmEmployeeFieldService employeeFieldService;

    @Resource
    private EmployeeActionRecordServiceImpl employeeActionRecordService;

    @Autowired
    private IHrmEmployeeAbnormalChangeRecordService abnormalChangeRecordService;
    @Autowired
    private IHrmFieldExtendService hrmFieldExtendService;

    private static final String COMPANY_AGE = "company_age";

    @Override
    public PostInformationVO postInformation(Long employeeId) {
        HrmEmployeeQuitInfo employeeQuitInfo = quitInfoService.lambdaQuery().eq(HrmEmployeeQuitInfo::getEmployeeId, employeeId).one();
        HrmEmployee employee = employeeService.getById(employeeId);
        if (employee.getCompanyAgeStartTime() != null && ObjectUtil.notEqual(employee.getEntryStatus(), EmployeeEntryStatus.ALREADY_LEAVE.getValue())) {
            long nowCompanyAge = LocalDateTimeUtil.between(employee.getCompanyAgeStartTime().atStartOfDay(), LocalDateTime.now()).toDays() + 1;
            if (LocalDateTimeUtil.toEpochMilli(employee.getCompanyAgeStartTime()) > System.currentTimeMillis()) {
                nowCompanyAge = 0;
            }
            if (nowCompanyAge != employee.getCompanyAge()) {
                employee.setCompanyAge((int) nowCompanyAge);
                employeeService.updateById(employee);
            }
        }
        List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employeeId);
        JSONObject employeeModel = BeanUtil.copyProperties(employee, JSONObject.class);
        List<InformationFieldVO> informationFieldVOList = employeeService.transferInformation(employeeModel, LabelGroupEnum.POST, fieldValueList);
        //计算司龄,修改描述
        String companyAgeDesc = EmployeeUtil.computeCompanyAge(employee.getCompanyAge());
        informationFieldVOList.forEach(fieldValue -> {
            if (COMPANY_AGE.equals(fieldValue.getFieldName())) {
                fieldValue.setFieldValueDesc(companyAgeDesc);
            }
            //添加语言包key
            Map<String, String> keyMap = LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", fieldValue.getFieldName(), fieldValue.getSetting());
            if ("channel_id".equals(fieldValue.getFieldName())) {
                keyMap.put("fieldValueDesc", "admin.recruitChannel." + fieldValue.getFieldValueDesc());
            }
            fieldValue.setLanguageKeyMap(keyMap);
        });
        if (employee.getEmploymentForms().equals(EmploymentFormsEnum.NO_OFFICIAL.getValue())) {
            informationFieldVOList.removeIf(fieldValue -> "probation".equals(fieldValue.getFieldName()));
        }

        return new PostInformationVO(informationFieldVOList, employeeQuitInfo);
    }

    @Override
    public OperationLog updatePostInformation(UpdateInformationBO updateInformationBO) {
        Long employeeId = updateInformationBO.getEmployeeId();
        HrmEmployee oldHrmEmployee = employeeService.getById(employeeId);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(oldHrmEmployee.getEmployeeId(), oldHrmEmployee.getEmployeeName());

        List<UpdateInformationBO.InformationFieldBO> dataList = updateInformationBO.getDataList();
        Map<FiledIsFixedEnum, List<UpdateInformationBO.InformationFieldBO>> isFixedMap = dataList.stream().collect(Collectors.groupingBy(employeeData -> FiledIsFixedEnum.parse(employeeData.getIsFixed())));
        List<UpdateInformationBO.InformationFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), FieldUtil.convertFieldValue(employeeData.getType(), employeeData.getFieldValue(), IsEnum.YES.getValue())));
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        if (employee.getDeptId() == null) {
            employeeService.lambdaUpdate().set(HrmEmployee::getDeptId, null).eq(HrmEmployee::getEmployeeId, employeeId).update();
        }
        if (employee.getParentId() == null) {
            employeeService.lambdaUpdate().set(HrmEmployee::getParentId, null).eq(HrmEmployee::getEmployeeId, employeeId).update();
        }
        employee.setEmployeeId(employeeId);
        Integer probation = employee.getProbation();
        if (probation != null) {
            if (probation == 0) {
                employee.setStatus(EmployeeStatusEnum.OFFICIAL.getValue());
                employee.setBecomeTime(employee.getEntryTime());
            } else {
                LocalDateTime dateTime = LocalDateTimeUtil.offset(employee.getEntryTime().atStartOfDay(), probation, ChronoUnit.MONTHS);
                if (dateTime.isAfter(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()))) {
                    employee.setStatus(EmployeeStatusEnum.TRY_OUT.getValue());
                } else {
                    employee.setStatus(EmployeeStatusEnum.OFFICIAL.getValue());
                }
                employee.setBecomeTime(dateTime.toLocalDate());
            }
        }
        List<UpdateInformationBO.InformationFieldBO> informationFieldBOS = isFixedMap.get(FiledIsFixedEnum.NO_FIXED);
        if (informationFieldBOS == null) {
            informationFieldBOS = new ArrayList<>();
        }
        List<HrmEmployeeData> hrmEmployeeData = informationFieldBOS.stream()
                .map(field -> {
                    Object value = field.getFieldValue();
                    if (value == null) {
                        value = "";
                    }
                    field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                    return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                }).collect(Collectors.toList());
        Dict kv = Dict.create().set("key", "employee_id").set("param", "label_group").set("labelGroup", LabelGroupEnum.POST.getValue()).set("value", employeeId).set("dataTableName", "wk_hrm_employee_data");
        List<HrmModelFiledVO> oldFieldList = ApplicationContextHolder.getBean(IHrmActionRecordService.class).queryFieldValue(kv);
        employeeFieldService.saveEmployeeField(hrmEmployeeData, LabelGroupEnum.POST, employeeId);
        if (null != oldHrmEmployee.getEntryTime() && !oldHrmEmployee.getEntryTime().equals(employee.getEntryTime())) {
            if (null == employee.getCompanyAgeStartTime() && null == oldHrmEmployee.getCompanyAgeStartTime()) {
                employee.setCompanyAgeStartTime(employee.getEntryTime());
            }
        }
        employeeService.saveOrUpdate(employee);
        //固定字段操作记录保存
        Content content = employeeActionRecordService.employeeFixedFieldRecord(BeanUtil.beanToMap(oldHrmEmployee), BeanUtil.beanToMap(employee), LabelGroupEnum.POST, employeeId);

        //非固定字段操作记录保存
        Content content1 = employeeActionRecordService.employeeNOFixedFieldRecord(informationFieldBOS, oldFieldList, employeeId);

        String[] split = content.getDetail().split(",");
        String[] split1 = content1.getDetail().split(",");
        operationLog.setOperationInfo(JSONUtil.toJsonStr(ArrayUtil.addAll(split1, split)));

        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateLeaveInformation(HrmEmployeeQuitInfo quitInfo) {
        HrmEmployee employee;

        OperationLog operationLog = new OperationLog();
        if (quitInfo.getQuitInfoId() == null) {
            boolean exists = quitInfoService.lambdaQuery().eq(HrmEmployeeQuitInfo::getEmployeeId, quitInfo.getEmployeeId()).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.THE_EMPLOYEE_HAS_ALREADY_HANDLED_THE_RESIGNATION);
            }
            Content content = employeeActionRecordService.quitRecord(quitInfo);
            employee = employeeService.getById(quitInfo.getEmployeeId());
            quitInfo.setOldStatus(employee.getStatus());

            operationLog.setOperationObject(quitInfo.getEmployeeId(), employee.getEmployeeName());
            operationLog.setOperationInfo(content.getDetail());
        } else {
            employee = new HrmEmployee();
            employee.setEmployeeId(quitInfo.getEmployeeId());
            HrmEmployeeQuitInfo old = quitInfoService.getById(quitInfo.getQuitInfoId());

            HrmEmployee hrmEmployee = employeeService.getById(quitInfo.getEmployeeId());
            operationLog.setOperationObject(hrmEmployee.getEmployeeId(), hrmEmployee.getEmployeeName());
            Content content = employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.QUIT, BeanUtil.beanToMap(old), BeanUtil.beanToMap(quitInfo), quitInfo.getEmployeeId());
            operationLog.setOperationInfo(content.getDetail());
        }
        LocalDateTime planQuitTime = quitInfo.getPlanQuitTime().atStartOfDay();
        EmployeeEntryStatus entryStatus;
        if (LocalDateTimeUtil.toEpochMilli(planQuitTime) > System.currentTimeMillis()) {
            entryStatus = EmployeeEntryStatus.TO_LEAVE;
        } else {
            entryStatus = EmployeeEntryStatus.ALREADY_LEAVE;
            if (ObjectUtil.isNotNull(employee.getCompanyAgeStartTime())) {
                long nowCompanyAge = LocalDateTimeUtil.between(employee.getCompanyAgeStartTime().atStartOfDay(), LocalDateTime.now()).toDays() + 1;
                employee.setCompanyAge(Convert.toInt(nowCompanyAge));
            }
            abnormalChangeRecordService.addAbnormalChangeRecord(quitInfo.getEmployeeId(), AbnormalChangeType.RESIGNATION, quitInfo.getPlanQuitTime().atStartOfDay());
        }
        employee.setEntryStatus(entryStatus.getValue());
        employeeService.updateById(employee);
        quitInfoService.saveOrUpdate(quitInfo);
        return operationLog;
    }

    @Override
    public OperationLog deleteLeaveInformation(DeleteLeaveInformationBO deleteLeaveInformationBO) {
        Long employeeId = deleteLeaveInformationBO.getEmployeeId();
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmEmployee.getEmployeeId(), hrmEmployee.getEmployeeName());

        HrmEmployeeQuitInfo quitInfo = quitInfoService.lambdaQuery().eq(HrmEmployeeQuitInfo::getEmployeeId, employeeId).one();
        HrmEmployee employee = new HrmEmployee();
        employee.setEmployeeId(quitInfo.getEmployeeId());
        employee.setEntryStatus(EmployeeEntryStatus.IN.getValue());
        employeeService.updateById(employee);
        quitInfoService.removeById(quitInfo.getQuitInfoId());
        Content content = employeeActionRecordService.cancelLeave(deleteLeaveInformationBO);
        operationLog.setOperationInfo(content.getDetail());
        return operationLog;
    }

    @Override
    public PostInformationVO postArchives() {
        Long employeeId = EmployeeHolder.getEmployeeId();
        HrmEmployeeQuitInfo employeeQuitInfo = quitInfoService.lambdaQuery().eq(HrmEmployeeQuitInfo::getEmployeeId, employeeId).one();
        HrmEmployee employee = employeeService.getById(employeeId);
        List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employeeId);
        JSONObject employeeModel = BeanUtil.copyProperties(employee, JSONObject.class);
        List<HrmEmployeeField> list = employeeFieldService.lambdaQuery().eq(HrmEmployeeField::getIsHidden, 0)
                .eq(HrmEmployeeField::getLabelGroup, LabelGroupEnum.POST.getValue())
                .eq(HrmEmployeeField::getIsEmployeeVisible, 1)
                .orderByAsc(HrmEmployeeField::getSorting).list();
        List<InformationFieldVO> informationFieldVOList = employeeService.transferInformation(employeeModel, list, fieldValueList);
        //计算司龄,修改描述
        String companyAgeDesc = EmployeeUtil.computeCompanyAge(employee.getCompanyAge());
        informationFieldVOList.forEach(fieldValue -> {
            if (COMPANY_AGE.equals(fieldValue.getFieldName())) {
                fieldValue.setFieldValueDesc(companyAgeDesc);
            }
            //添加语言包key
            fieldValue.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", fieldValue.getFieldName(), fieldValue.getSetting()));
        });
        if (employee.getEmploymentForms().equals(EmploymentFormsEnum.NO_OFFICIAL.getValue())) {
            informationFieldVOList.removeIf(fieldValue -> "probation".equals(fieldValue.getFieldName()));
        }

        return new PostInformationVO(informationFieldVOList, employeeQuitInfo);
    }

}
