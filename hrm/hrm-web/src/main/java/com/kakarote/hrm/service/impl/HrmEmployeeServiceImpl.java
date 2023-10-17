package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.*;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.DataAuthEnum;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.BaseUtil;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.core.utils.TransferUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.cron.EmployeeChangeCron;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.service.AdminFileService;
import com.kakarote.hrm.mapper.HrmEmployeeMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.CandidateActionRecordServiceImpl;
import com.kakarote.hrm.service.actionrecord.impl.EmployeeActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import com.kakarote.hrm.utils.EmployeeUtil;
import com.kakarote.hrm.utils.FieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 员工表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmEmployeeServiceImpl extends BaseServiceImpl<HrmEmployeeMapper, HrmEmployee> implements IHrmEmployeeService {

    @Autowired
    private HrmEmployeeMapper employeeMapper;

    @Autowired
    private IHrmEmployeeDataService employeeDataService;

    @Autowired
    private IHrmEmployeeFieldService employeeFieldService;

    @Autowired
    private IHrmDeptService hrmDeptService;

    @Autowired
    private IHrmEmployeeEducationExperienceService educationExperienceService;

    @Autowired
    private IHrmEmployeeWorkExperienceService workExperienceService;

    @Autowired
    private IHrmEmployeePostService certificateService;

    @Autowired
    private IHrmEmployeeContactsService contactsService;

    @Autowired
    private IHrmEmployeeContactsDataService contactsDataService;

    @Autowired
    private IHrmEmployeeTrainingExperienceService trainingExperienceService;

    @Autowired
    private IHrmEmployeeChangeRecordService changeRecordService;

    @Autowired
    private IHrmInsuranceSchemeService insuranceSchemeService;

    @Autowired
    private IHrmEmployeeSocialSecurityService securityInfoService;

    @Resource
    private EmployeeActionRecordServiceImpl employeeActionRecordService;

    @Autowired
    private IHrmEmployeeQuitInfoService quitInfoService;

    @Autowired
    private IHrmRecruitChannelService recruitChannelService;

    @Autowired
    private IHrmEmployeeAbnormalChangeRecordService abnormalChangeRecordService;

    @Autowired
    private IHrmRecruitCandidateService candidateService;

    @Resource
    private CandidateActionRecordServiceImpl candidateActionRecordService;

    @Autowired
    private EmployeeUtil employeeUtil;
    @Autowired
    private IAdminMessageService messageService;
    @Autowired
    private IHrmFieldExtendService hrmFieldExtendService;

    @Autowired
    private AdminFileService adminFileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> add(AddEmployeeBO employeeVO) {
        String jobNumber = employeeVO.getJobNumber();
        boolean exists = lambdaQuery().eq(HrmEmployee::getIsDel, 0).eq(HrmEmployee::getJobNumber, jobNumber).exists();
        if (exists) {
            throw new CrmException(HrmCodeEnum.JOB_NUMBER_EXISTED, jobNumber);
        }

        List<OperationLog> operationLogList = new ArrayList<>();

        if (employeeVO.getCandidateId() != null) {
            if (employeeVO.getEntryStatus() == 1) {
                candidateService.lambdaUpdate().set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.HAVE_JOINED.getValue())
                        .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                        .set(HrmRecruitCandidate::getEntryTime, new Date())
                        .eq(HrmRecruitCandidate::getCandidateId, employeeVO.getCandidateId()).update();
                UpdateCandidateStatusBO updateCandidateStatusBO = new UpdateCandidateStatusBO();
                updateCandidateStatusBO.setCandidateIds(Collections.singletonList(employeeVO.getCandidateId()));
                updateCandidateStatusBO.setStatus(CandidateStatusEnum.HAVE_JOINED.getValue());
                operationLogList.addAll(candidateActionRecordService.updateCandidateStatusRecord(updateCandidateStatusBO));

            } else {
                candidateService.lambdaUpdate().set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.PENDING_ENTRY.getValue())
                        .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                        .eq(HrmRecruitCandidate::getCandidateId, employeeVO.getCandidateId()).update();
                UpdateCandidateStatusBO updateCandidateStatusBO = new UpdateCandidateStatusBO();
                updateCandidateStatusBO.setCandidateIds(Collections.singletonList(employeeVO.getCandidateId()));
                updateCandidateStatusBO.setStatus(CandidateStatusEnum.PENDING_ENTRY.getValue());
                operationLogList.addAll(candidateActionRecordService.updateCandidateStatusRecord(updateCandidateStatusBO));
            }
        }
        HrmEmployee employee = BeanUtil.copyProperties(employeeVO, HrmEmployee.class);
        if (employee.getCandidateId() != null) {
            HrmRecruitCandidate candidate = candidateService.getById(employee.getCandidateId());
            employee.setChannelId(candidate.getChannelId());
        }
        transferEmployee(employee);
        employee.setCompanyAgeStartTime(employee.getEntryTime());
        save(employee);
        boolean mobileFieldCount = employeeFieldService.query().eq("field_name", "flied_kwbova").eq("label_group", LabelGroupEnum.COMMUNICATION.getValue()).eq("type", FieldTypeEnum.MOBILE.getValue()).exists();
        if (mobileFieldCount) {
            //若是存在自定义手机号码字段，则进行更新
            HrmEmployee hrmEmployee = employeeMapper.selectById(employee.getEmployeeId());
            JSONObject employeeModel = BeanUtil.copyProperties(hrmEmployee, JSONObject.class);
            List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employee.getEmployeeId());
            List<InformationFieldVO> communicationInformation = transferInformation(employeeModel, LabelGroupEnum.COMMUNICATION, fieldValueList);
            List<InformationFieldVO> informationFieldVOS = communicationInformation.stream().collect(Collectors.groupingBy(employeeData -> FiledIsFixedEnum.parse(employeeData.getIsFixed()))).get(FiledIsFixedEnum.NO_FIXED);
            String fliedKwbova = "flied_kwbova";
            List<HrmEmployeeData> hrmEmployeeInformationData = informationFieldVOS.stream()
                    .map(field -> {
                        if (fliedKwbova.equals(field.getFieldName()) && field.getType().equals(FieldTypeEnum.MOBILE.getValue())) {
                            field.setFieldValue(employee.getMobile());
                            field.setFieldValueDesc(employee.getMobile());
                        }
                        Object value = field.getFieldValue();
                        if (value == null) {
                            value = "";
                        }
                        field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                        return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                    }).collect(Collectors.toList());
            employeeFieldService.saveEmployeeField(hrmEmployeeInformationData, LabelGroupEnum.COMMUNICATION, employee.getEmployeeId());
        }
        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.PERSONAL, employee.getEmployeeId());


        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
        operationLog.setOperationInfo(content.getDetail());

        if (employeeVO.getEntryStatus() == 1) {
            abnormalChangeRecordService.addAbnormalChangeRecord(employee.getEmployeeId(), AbnormalChangeType.NEW_ENTRY, LocalDateTime.now());
        }
        //发送通知
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateUser(UserUtil.getUserId());
        adminMessage.setCreateTime(LocalDateTime.now());
        adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employee.getEmployeeId()));
        adminMessage.setLabel(8);
        adminMessage.setType(AdminMessageEnum.HRM_EMPLOYEE_OPEN.getType());
        messageService.save(adminMessage);

        if (operationLogList.isEmpty()) {
            operationLogList.add(operationLog);
        }
        return operationLogList;
    }

    @Override
    public List<SimpleHrmEmployeeVO> queryInspectionAllEmployeeList(String employeeName) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        LambdaQueryWrapper<HrmEmployee> wrapper = new QueryWrapper<HrmEmployee>().lambda().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName, HrmEmployee::getPost,
                HrmEmployee::getEntryStatus, HrmEmployee::getIsDel, HrmEmployee::getDeptId).eq(HrmEmployee::getIsDel, 0).like(StrUtil.isNotEmpty(employeeName), HrmEmployee::getEmployeeName, employeeName);
        List<HrmEmployee> hrmEmployeeList = this.list(wrapper);
        List<SimpleHrmEmployeeVO> simpleHrmEmployeeVOList = new ArrayList<>();
        for (HrmEmployee employee : hrmEmployeeList) {
            if (employeeIds.contains(employee.getEmployeeId())) {
                simpleHrmEmployeeVOList.add(transferSimpleEmp(employee));
            }
        }
        return simpleHrmEmployeeVOList;
    }

    /**
     * 转换入职员工信息
     *
     * @return
     */
    private HrmEmployee transferEmployee(HrmEmployee employee) {
        if (employee.getIdType() != null && employee.getIdType() == IdTypeEnum.ID_CARD.getValue() && StrUtil.isNotEmpty(employee.getIdNumber())) {
            String idNumber = employee.getIdNumber();
            if (!IdcardUtil.isValidCard(idNumber)) {
                throw new CrmException(HrmCodeEnum.IDENTITY_INFORMATION_IS_ILLEGAL, idNumber);
            }
            employee.setDateOfBirth(IdcardUtil.getBirthDate(idNumber).toLocalDateTime().toLocalDate());
        }
        if (ObjectUtil.isNotNull(employee.getDateOfBirth())) {
            if (employee.getDateOfBirth().isBefore(LocalDate.now())) {
                employee.setAge(DateUtil.ageOfNow(LocalDateTimeUtil.format(employee.getDateOfBirth(), DatePattern.NORM_DATE_PATTERN)));
            }
        }
        if (employee.getEmploymentForms() != null && employee.getEmploymentForms() == EmploymentFormsEnum.OFFICIAL.getValue()) {
            if (ObjectUtil.isEmpty(employee.getProbation())) {
                employee.setProbation(0);
            }
            Integer probation = employee.getProbation();
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
        LambdaQueryChainWrapper<HrmEmployee> wrapper = lambdaQuery().eq(HrmEmployee::getMobile, employee.getMobile()).eq(HrmEmployee::getIsDel, 0);
        boolean count;
        if (employee.getEmployeeId() == null) {
            count = wrapper.exists();
        } else {
            count = wrapper.ne(HrmEmployee::getEmployeeId, employee.getEmployeeId()).exists();
        }
        if (count) {
            throw new CrmException(HrmCodeEnum.PHONE_NUMBER_ALREADY_EXISTS, employee.getMobile());
        }
        if (StrUtil.isNotEmpty(employee.getWorkCity()) && BaseUtil.isJSON(employee.getWorkCity())) {
            JSONObject jsonObject = JSONObject.parseObject(employee.getWorkCity());
            StringBuilder workCity = new StringBuilder();
            workCity.append(jsonObject.getString("province"));
            if (StrUtil.isNotEmpty(jsonObject.getString("city"))) {
                workCity.append(jsonObject.getString("city"));
            }
            if (StrUtil.isNotEmpty(jsonObject.getString("area"))) {
                workCity.append(jsonObject.getString("area"));
            }
            employee.setWorkCity(workCity.toString());
        }
        return employee;
    }

    @Override
    public List<SimpleHrmEmployeeVO> queryAllEmployeeList(String employeeName) {
        LambdaQueryWrapper<HrmEmployee> wrapper = new QueryWrapper<HrmEmployee>().lambda().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName, HrmEmployee::getPost,
                HrmEmployee::getEntryStatus, HrmEmployee::getIsDel, HrmEmployee::getDeptId).eq(HrmEmployee::getIsDel, 0).like(StrUtil.isNotEmpty(employeeName), HrmEmployee::getEmployeeName, employeeName);
        List<HrmEmployee> hrmEmployeeList = this.list(wrapper);
        List<SimpleHrmEmployeeVO> simpleHrmEmployeeVOList = new ArrayList<>();
        for (HrmEmployee employee : hrmEmployeeList) {
            simpleHrmEmployeeVOList.add(transferSimpleEmp(employee));
        }
        return simpleHrmEmployeeVOList;
    }

    @Override
    public SimpleHrmEmployeeVO transferSimpleEmp(HrmEmployee employee) {
        SimpleHrmEmployeeVO simpleHrmEmployeeVO = new SimpleHrmEmployeeVO();
        simpleHrmEmployeeVO.setEmployeeId(employee.getEmployeeId());
        simpleHrmEmployeeVO.setEmployeeName(employee.getEmployeeName());
        int status = 1;
        if (employee.getIsDel() == 1) {
            status = 3;
        }
        if (employee.getIsDel() == 0 && employee.getEntryStatus().equals(EmployeeEntryStatus.IN.getValue())) {
            status = 1;
        }
        if (employee.getIsDel() == 0 &&
                (employee.getEntryStatus().equals(EmployeeEntryStatus.ALREADY_LEAVE.getValue()) ||
                        employee.getEntryStatus().equals(EmployeeEntryStatus.TO_IN.getValue()))) {
            status = 2;
        }
        simpleHrmEmployeeVO.setStatus(status);
        simpleHrmEmployeeVO.setPost(employee.getPost());
        if (null != employee.getDeptId()) {
            DeptVO deptVO = hrmDeptService.queryById(employee.getDeptId());
            if (deptVO != null) {
                simpleHrmEmployeeVO.setDeptName(deptVO.getName());
            }
        }
        return simpleHrmEmployeeVO;
    }

    @Override
    public List<SimpleHrmEmployeeVO> queryInEmployeeList() {
        LambdaQueryWrapper<HrmEmployee> wrapper = new QueryWrapper<HrmEmployee>().lambda().select(HrmEmployee::getEmployeeId, HrmEmployee::getDeptId, HrmEmployee::getEmployeeName, HrmEmployee::getPost)
                .eq(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue()).eq(HrmEmployee::getIsDel, 0);
        List<HrmEmployee> hrmEmployeeList = this.list(wrapper);
        hrmEmployeeList.forEach(employee -> {
            if (employee.getDeptId() != null) {
                DeptVO deptVO = hrmDeptService.queryById(employee.getDeptId());
                if (deptVO != null) {
                    employee.setDeptName(deptVO.getName());
                }
            }
        });
        return TransferUtil.transferList(hrmEmployeeList, SimpleHrmEmployeeVO.class);
    }

    @Override
    public PersonalInformationVO personalInformation(Long employeeId) {
        HrmEmployee hrmEmployee = employeeMapper.selectById(employeeId);
        List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employeeId);
        JSONObject employeeModel = BeanUtil.copyProperties(hrmEmployee, JSONObject.class);
        //基本信息
        List<InformationFieldVO> information = transferInformation(employeeModel, LabelGroupEnum.PERSONAL, fieldValueList);
        //通讯信息
        List<InformationFieldVO> communicationInformation = transferInformation(employeeModel, LabelGroupEnum.COMMUNICATION, fieldValueList);
        //教育经历
        List<HrmEmployeeEducationExperience> educationExperienceList = educationExperienceService.lambdaQuery().eq(HrmEmployeeEducationExperience::getEmployeeId, employeeId).list();
        //工作经历
        List<HrmEmployeeWorkExperience> workExperienceList = workExperienceService.lambdaQuery().eq(HrmEmployeeWorkExperience::getEmployeeId, employeeId).list();
        //证书
        List<HrmEmployeeCertificate> certificateList = certificateService.lambdaQuery().eq(HrmEmployeeCertificate::getEmployeeId, employeeId).list();
        //培训经历
        List<HrmEmployeeTrainingExperience> trainingExperienceList = trainingExperienceService.lambdaQuery().eq(HrmEmployeeTrainingExperience::getEmployeeId, employeeId).list();
        //联系人信息

        List<Map<String, Object>> hrmEmployeeContacts = new ArrayList<>();
        List<HrmEmployeeContacts> contactsList = contactsService.lambdaQuery().eq(HrmEmployeeContacts::getEmployeeId, employeeId).list();
        contactsList.forEach(contacts -> {
            QueryWrapper<HrmEmployeeContactsData> eq = Wrappers.<HrmEmployeeContactsData>query().select("field_id", "field_value", "field_value_desc").eq("contacts_id", contacts.getContactsId());
            List<HrmEmployeeContactsData> list = contactsDataService.list(eq);
            //联系人自定义字段值
            List<HrmEmployeeData> contactsFieldValueList = TransferUtil.transferList(list, HrmEmployeeData.class);
            Map<String, Object> hrmEmployeeContact = new HashMap<>();
            hrmEmployeeContact.put("contactsId", contacts.getContactsId());
            hrmEmployeeContact.put("information", transferInformation(BeanUtil.copyProperties(contacts, JSONObject.class), LabelGroupEnum.CONTACT_PERSON, contactsFieldValueList));
            hrmEmployeeContacts.add(hrmEmployeeContact);
        });
        return new PersonalInformationVO(information, communicationInformation, educationExperienceList, workExperienceList, certificateList, hrmEmployeeContacts, trainingExperienceList);
    }


    @Override
    public PersonalInformationVO personalArchives() {
        Long employeeId = EmployeeHolder.getEmployeeId();
        HrmEmployee hrmEmployee = employeeMapper.selectById(employeeId);
        List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employeeId);
        JSONObject employeeModel = BeanUtil.copyProperties(hrmEmployee, JSONObject.class);
        List<HrmEmployeeField> personalFieldList = employeeFieldService.lambdaQuery().eq(HrmEmployeeField::getIsHidden, 0)
                .eq(HrmEmployeeField::getLabelGroup, LabelGroupEnum.PERSONAL.getValue())
                .eq(HrmEmployeeField::getIsEmployeeVisible, 1)
                .orderByAsc(HrmEmployeeField::getSorting).list();
        //基本信息
        List<InformationFieldVO> information = transferInformation(employeeModel, personalFieldList, fieldValueList);
        List<HrmEmployeeField> communicationFieldList = employeeFieldService.lambdaQuery().eq(HrmEmployeeField::getIsHidden, 0)
                .eq(HrmEmployeeField::getLabelGroup, LabelGroupEnum.COMMUNICATION.getValue())
                .eq(HrmEmployeeField::getIsEmployeeVisible, 1)
                .orderByAsc(HrmEmployeeField::getSorting).list();
        //通讯信息
        List<InformationFieldVO> communicationInformation = transferInformation(employeeModel, communicationFieldList, fieldValueList);
        //教育经历
        List<HrmEmployeeEducationExperience> educationExperienceList = educationExperienceService.lambdaQuery().eq(HrmEmployeeEducationExperience::getEmployeeId, employeeId).list();
        //工作经历
        List<HrmEmployeeWorkExperience> workExperienceList = workExperienceService.lambdaQuery().eq(HrmEmployeeWorkExperience::getEmployeeId, employeeId).list();
        //证书
        List<HrmEmployeeCertificate> certificateList = certificateService.lambdaQuery().eq(HrmEmployeeCertificate::getEmployeeId, employeeId).list();
        //培训经历
        List<HrmEmployeeTrainingExperience> trainingExperienceList = trainingExperienceService.lambdaQuery().eq(HrmEmployeeTrainingExperience::getEmployeeId, employeeId).list();
        //联系人信息
        List<Map<String, Object>> hrmEmployeeContacts = new ArrayList<>();
        List<HrmEmployeeContacts> contactsList = contactsService.lambdaQuery().eq(HrmEmployeeContacts::getEmployeeId, employeeId).list();
        contactsList.forEach(contacts -> {
            QueryWrapper<HrmEmployeeContactsData> eq = Wrappers.<HrmEmployeeContactsData>query().select("field_id", "field_value", "field_value_desc").eq("contacts_id", contacts.getContactsId());
            List<HrmEmployeeContactsData> list = contactsDataService.list(eq);
            //联系人自定义字段值
            List<HrmEmployeeData> contactsFieldValueList = TransferUtil.transferList(list, HrmEmployeeData.class);
            Map<String, Object> hrmEmployeeContact = new HashMap<>();
            hrmEmployeeContact.put("contactsId", contacts.getContactsId());
            hrmEmployeeContact.put("information", transferInformation(BeanUtil.copyProperties(contacts, JSONObject.class), LabelGroupEnum.CONTACT_PERSON, contactsFieldValueList));
            hrmEmployeeContacts.add(hrmEmployeeContact);
        });
        return new PersonalInformationVO(information, communicationInformation, educationExperienceList, workExperienceList, certificateList, hrmEmployeeContacts, trainingExperienceList);
    }

    /**
     * 自定义字段基本信息转换
     *
     * @param model
     * @param labelGroupEnum
     * @param fieldValueList
     * @return
     */
    @Override
    public List<InformationFieldVO> transferInformation(JSONObject model, LabelGroupEnum labelGroupEnum, List<HrmEmployeeData> fieldValueList) {
        List<HrmEmployeeField> hrmEmployeeFieldList = employeeFieldService.queryInformationFieldByLabelGroup(labelGroupEnum);
        return transferInformation(model, hrmEmployeeFieldList, fieldValueList);
    }

    @Override
    public List<InformationFieldVO> transferInformation(JSONObject model, List<HrmEmployeeField> hrmEmployeeFieldList, List<HrmEmployeeData> fieldValueList) {
        List<InformationFieldVO> informationFieldVOList = new ArrayList<>();
        Map<Long, HrmEmployeeData> fieldValueMap = new HashMap<>();
        fieldValueList.forEach(field -> fieldValueMap.put(field.getFieldId(), field));
        hrmEmployeeFieldList.forEach(field -> {
            InformationFieldVO fieldVO = BeanUtil.copyProperties(field, InformationFieldVO.class);
            Integer isFixed = fieldVO.getIsFixed();
            Integer type = fieldVO.getType();
            //固定字段
            if (isFixed == 1) {
                String fieldName = StrUtil.toCamelCase(fieldVO.getFieldName());
                Integer componentType = fieldVO.getComponentType();
                fieldVO.setFieldValue(model.get(fieldName) != null ? model.get(fieldName) : "");
                if (componentType == ComponentType.HRM_EMPLOYEE.getValue()) {
                    fieldVO.setFieldValueDesc(model.getLong(fieldName) != null && model.getLong(fieldName) != 0L ?
                            Optional.ofNullable(employeeMapper.selectById(model.getLong(fieldName))).map(HrmEmployee::getEmployeeName).orElse("") : "");
                } else if (componentType == ComponentType.HRM_DEPT.getValue()) {
                    fieldVO.setFieldValueDesc(model.getLong(fieldName) != null && model.getLong(fieldName) != 0L ?
                            Optional.ofNullable(hrmDeptService.getById(model.getLong(fieldName))).map(HrmDept::getName).orElse("") : "");
                } else if (componentType == ComponentType.RECRUIT_CHANNEL.getValue()) {
                    fieldVO.setFieldValueDesc(model.getLong(fieldName) != null && model.getLong(fieldName) != 0 ?
                            Optional.ofNullable(recruitChannelService.getById(model.getLong(fieldName))).map(HrmRecruitChannel::getValue).orElse("") : "");
                } else if (componentType == ComponentType.NO.getValue() && type.equals(FieldTypeEnum.SELECT.getValue())) {
                    Object value = model.get(fieldName);
                    if (value != null) {
                        List<Map<String, Object>> list = JSON.parseObject(fieldVO.getOptions(), List.class);
                        if (ObjectUtil.isNotEmpty(list)) {
                            String keyName = "value";
                            list.forEach(map -> {
                                if (map.get(keyName).equals(value)) {
                                    fieldVO.setFieldValueDesc(map.get("name"));
                                }
                            });
                        } else {
                            fieldVO.setFieldValueDesc(value);
                        }
                        fieldVO.setFieldValue(value);
                    } else {
                        fieldVO.setFieldValueDesc("");
                    }
                } else {
                    if (fieldVO.getType().equals(FieldTypeEnum.DATE.getValue())) {
                        if (ObjectUtil.isNotEmpty(model.get(fieldName))) {
                            if (model.get(fieldName) instanceof LocalDateTime) {
                                fieldVO.setFieldValueDesc(LocalDateTimeUtil.format((LocalDateTime) model.get(fieldName), DatePattern.NORM_DATE_PATTERN));
                                fieldVO.setFieldValue(LocalDateTimeUtil.format((LocalDateTime) model.get(fieldName), DatePattern.NORM_DATE_PATTERN));
                            }
                            if (model.get(fieldName) instanceof LocalDate) {
                                fieldVO.setFieldValueDesc(LocalDateTimeUtil.format((LocalDate) model.get(fieldName), DatePattern.NORM_DATE_PATTERN));
                                fieldVO.setFieldValue(LocalDateTimeUtil.format((LocalDate) model.get(fieldName), DatePattern.NORM_DATE_PATTERN));
                            }
                        } else {
                            fieldVO.setFieldValueDesc("");
                            fieldVO.setFieldValue("");
                        }
                    } else {
                        fieldVO.setFieldValueDesc(model.get(fieldName) != null ? model.get(fieldName) : "");
                    }
                }
            } else {
                if (fieldValueMap.get(fieldVO.getFieldId()) != null) {
                    if (fieldVO.getType().equals(FieldEnum.AREA_POSITION.getType())) {
                        String value = fieldValueMap.get(fieldVO.getFieldId()).getFieldValue();
                        if (StrUtil.isNotEmpty(value)) {
                            String contains = "=";
                            if (value.contains(contains)) {
                                System.out.println(value);

                            } else {
                                fieldVO.setFieldValue(JSON.parseArray(value));
                                fieldVO.setFieldValueDesc(JSON.parseArray(value));
                            }
                        } else {
                            fieldVO.setFieldValue(new ArrayList<>());
                            fieldVO.setFieldValueDesc(new ArrayList<>());
                        }
                    } else if (fieldVO.getType().equals(FieldEnum.CURRENT_POSITION.getType())) {
                        fieldVO.setFieldValue(JSON.parseObject(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue()));
                        fieldVO.setFieldValueDesc(JSON.parseObject(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc()));
                    } else if (fieldVO.getType().equals(FieldEnum.FILE.getType())) {
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue())) {
                            fieldVO.setFieldValue(adminFileService.queryFileList(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue()));
                        } else {
                            fieldVO.setFieldValue(new ArrayList<>());
                        }
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc())) {
                            fieldVO.setFieldValueDesc(adminFileService.queryFileList(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc()));
                        } else {
                            fieldVO.setFieldValueDesc(new ArrayList<>());
                        }
                    } else if (fieldVO.getType().equals(FieldEnum.STRUCTURE.getType())) {
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue())) {
                            fieldVO.setFieldValue(hrmDeptService.querySimpleDeptList(TagUtil.toLongSet(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue())));
                        } else {
                            fieldVO.setFieldValue(new ArrayList<>());
                        }
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc())) {
                            fieldVO.setFieldValueDesc(hrmDeptService.querySimpleDeptList(TagUtil.toLongSet(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc())));
                        } else {
                            fieldVO.setFieldValueDesc(new ArrayList<>());
                        }
                    } else if (fieldVO.getType().equals(FieldEnum.USER.getType())) {
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue())) {
                            fieldVO.setFieldValue(querySimpleEmployeeList(TagUtil.toLongSet(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue())));
                        } else {
                            fieldVO.setFieldValue(new ArrayList<>());
                        }
                        if (StrUtil.isNotEmpty(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc())) {
                            fieldVO.setFieldValueDesc(querySimpleEmployeeList(TagUtil.toLongSet(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc())));
                        } else {
                            fieldVO.setFieldValueDesc(new ArrayList<>());
                        }
                    } else {
                        fieldVO.setFieldValue(fieldValueMap.get(fieldVO.getFieldId()).getFieldValue());
                        fieldVO.setFieldValueDesc(fieldValueMap.get(fieldVO.getFieldId()).getFieldValueDesc());
                    }
                } else {
                    fieldVO.setFieldValue("");
                    fieldVO.setFieldValueDesc("");
                }
            }
            FieldEnum typeEnum = FieldEnum.parse(fieldVO.getType());
            recordToFormType(fieldVO, typeEnum);


            //添加语言包key
            Map<String, String> keyMap = LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", fieldVO.getFieldName(), fieldVO.getSetting());
            keyMap.put("fieldValueDesc_resourceKey", "customField.hrmField." + LanguageFieldUtil.getFieldName(fieldVO.getFieldName()) + "Options." + fieldVO.getFieldValueDesc());
            if (CollectionUtil.isNotEmpty(fieldVO.getSetting())) {
                Object first = CollectionUtil.getFirst(fieldVO.getSetting());
                if (NumberUtil.isNumber(LanguageFieldUtil.getSettingValue(first))) {

                } else {
                    keyMap.put("fieldValue_resourceKey", "customField.hrmField." + LanguageFieldUtil.getFieldName(fieldVO.getFieldName()) + "Options." + fieldVO.getFieldValueDesc());
                }
            }

            fieldVO.setLanguageKeyMap(keyMap);
            informationFieldVOList.add(fieldVO);

        });
        return informationFieldVOList;
    }

    @Override
    public HrmEmployee queryById(Long employeeId) {
        HrmEmployee employee = getById(employeeId);
        if (ObjectUtil.isNotNull(employee)) {
            hrmDeptService.lambdaQuery().select(HrmDept::getName)
                    .eq(HrmDept::getDeptId, employee.getDeptId()).oneOpt().ifPresent(dept -> employee.setDeptName(dept.getName()));
        } else {
            log.info("当前员工查询为空,Id:{}", employeeId);
        }
        return employee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog updateInformation(UpdateInformationBO updateInformationBO) {
        Long employeeId = updateInformationBO.getEmployeeId();
        if (employeeId == null) {
            employeeId = EmployeeHolder.getEmployeeId();
        }
        HrmEmployee oldEmployee = queryById(employeeId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(oldEmployee.getEmployeeId(), oldEmployee.getEmployeeName());

        List<UpdateInformationBO.InformationFieldBO> dataList = updateInformationBO.getDataList();
        Map<FiledIsFixedEnum, List<UpdateInformationBO.InformationFieldBO>> isFixedMap = getIsFixedMap(dataList);
        List<UpdateInformationBO.InformationFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), employeeData.getFieldValue()));
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        employee.setEmployeeId(employeeId);
        if (employee.getIdType() != null && employee.getIdType() == IdTypeEnum.ID_CARD.getValue() && StrUtil.isNotEmpty(employee.getIdNumber())) {
            String idNumber = employee.getIdNumber();
            if (!IdcardUtil.isValidCard(idNumber)) {
                throw new CrmException(HrmCodeEnum.IDENTITY_INFORMATION_IS_ILLEGAL, idNumber);
            }
            employee.setDateOfBirth(IdcardUtil.getBirthDate(idNumber).toLocalDateTime().toLocalDate());
        }
        if (ObjectUtil.isNotNull(employee.getDateOfBirth())) {
            if (employee.getDateOfBirth().isBefore(LocalDate.now())) {
                employee.setAge(DateUtil.ageOfNow(LocalDateTimeUtil.format(employee.getDateOfBirth(), DatePattern.NORM_DATE_PATTERN)));
            }
        }
        boolean jobCount = lambdaQuery().eq(HrmEmployee::getJobNumber, employee.getJobNumber()).ne(HrmEmployee::getEmployeeId, employee.getEmployeeId())
                .eq(HrmEmployee::getIsDel, 0).exists();
        if (jobCount) {
            throw new CrmException(HrmCodeEnum.JOB_NUMBER_EXISTED);
        }
        boolean mobileCount = lambdaQuery().eq(HrmEmployee::getMobile, employee.getMobile()).ne(HrmEmployee::getEmployeeId, employee.getEmployeeId())
                .eq(HrmEmployee::getIsDel, 0).exists();
        if (mobileCount) {
            throw new CrmException(HrmCodeEnum.PHONE_NUMBER_ALREADY_EXISTS, employee.getMobile());
        }
        List<UpdateInformationBO.InformationFieldBO> informationFieldBOS = isFixedMap.get(FiledIsFixedEnum.NO_FIXED);
        List<HrmEmployeeData> hrmEmployeeData = informationFieldBOS.stream()
                .map(field -> {
                    Object value = field.getFieldValue();
                    if (value == null) {
                        value = "";
                    }
                    field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                    return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                }).collect(Collectors.toList());
        Dict kv = Dict.create().set("key", "employee_id").set("param", "label_group").set("labelGroup", LabelGroupEnum.PERSONAL.getValue()).set("value", employeeId).set("dataTableName", "wk_hrm_employee_data");
        List<HrmModelFiledVO> oldFieldList = ApplicationContextHolder.getBean(IHrmActionRecordService.class).queryFieldValue(kv);
        employeeFieldService.saveEmployeeField(hrmEmployeeData, LabelGroupEnum.PERSONAL, employeeId);
        if (employee.getDateOfBirth() == null) {
            lambdaUpdate().eq(HrmEmployee::getEmployeeId, employeeId).set(HrmEmployee::getDateOfBirth, null).update();
        }
        updateById(employee);
        if (ObjectUtil.isNotNull(oldEmployee)) {
            //固定字段操作记录保存
            Content content = employeeActionRecordService.employeeFixedFieldRecord(BeanUtil.beanToMap(oldEmployee), BeanUtil.beanToMap(employee), LabelGroupEnum.PERSONAL, employeeId);
            //非固定字段操作记录保存

            String[] recprdInfo = content.getDetail().split(",");

            Content content1 = employeeActionRecordService.employeeNOFixedFieldRecord(informationFieldBOS, oldFieldList, employeeId);

            String[] recprdInfo1 = content1.getDetail().split(",");

            String[] updateInfo = ArrayUtil.addAll(recprdInfo, recprdInfo1);
            operationLog.setOperationInfo(JSONUtil.toJsonStr(updateInfo));
        }
        return operationLog;
    }

    private <E> Map<FiledIsFixedEnum, List<E>> getIsFixedMap(List<E> dataList) {
        Map<FiledIsFixedEnum, List<E>> listMap =
                dataList.stream().collect(Collectors.groupingBy(employeeData -> {
                    if (employeeData instanceof UpdateInformationBO.InformationFieldBO) {
                        return FiledIsFixedEnum.parse(((UpdateInformationBO.InformationFieldBO) employeeData).getIsFixed());
                    } else if (employeeData instanceof AddEmployeeFieldManageBO.EmployeeFieldBO) {
                        return FiledIsFixedEnum.parse(((AddEmployeeFieldManageBO.EmployeeFieldBO) employeeData).getIsFixed());
                    }
                    return FiledIsFixedEnum.FIXED;
                }));
        if (!listMap.containsKey(FiledIsFixedEnum.FIXED)) {
            listMap.put(FiledIsFixedEnum.FIXED, new ArrayList<>());
        }
        if (!listMap.containsKey(FiledIsFixedEnum.NO_FIXED)) {
            listMap.put(FiledIsFixedEnum.NO_FIXED, new ArrayList<>());
        }
        return listMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog updateCommunication(UpdateInformationBO updateInformationBO) {
        Long employeeId = updateInformationBO.getEmployeeId();
        if (employeeId == null) {
            employeeId = EmployeeHolder.getEmployeeId();
        }
        HrmEmployee oldEmployee = queryById(employeeId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(oldEmployee.getEmployeeId(), oldEmployee.getEmployeeName());


        List<UpdateInformationBO.InformationFieldBO> dataList = updateInformationBO.getDataList();
        Map<FiledIsFixedEnum, List<UpdateInformationBO.InformationFieldBO>> isFixedMap = getIsFixedMap(dataList);
        List<UpdateInformationBO.InformationFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), employeeData.getFieldValue()));
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        employee.setEmployeeId(employeeId);
        updateById(employee);
        List<UpdateInformationBO.InformationFieldBO> informationFieldBOS = isFixedMap.get(FiledIsFixedEnum.NO_FIXED);
        List<HrmEmployeeData> hrmEmployeeData = informationFieldBOS.stream()
                .map(field -> {
                    Object value = field.getFieldValue();
                    if (value == null) {
                        value = "";
                    }
                    field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                    return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                }).collect(Collectors.toList());
        Dict set = Dict.create().set("key", "employee_id").set("value", employeeId).set("param", "label_group").set("labelGroup", LabelGroupEnum.CONTACT_PERSON.getValue()).set("dataTableName", "wk_hrm_employee_data");
        List<HrmModelFiledVO> oldFieldList = ApplicationContextHolder.getBean(IHrmActionRecordService.class).queryFieldValue(set);
        employeeFieldService.saveEmployeeField(hrmEmployeeData, LabelGroupEnum.CONTACT_PERSON, employeeId);
        //固定字段操作记录保存
        Content content = employeeActionRecordService.employeeFixedFieldRecord(BeanUtil.beanToMap(oldEmployee), BeanUtil.beanToMap(employee), LabelGroupEnum.CONTACT_PERSON, employeeId);
        //非固定字段操作记录保存
        Content content1 = employeeActionRecordService.employeeNOFixedFieldRecord(informationFieldBOS, oldFieldList, employeeId);
        String[] split = content.getDetail().split(",");
        String[] split1 = content1.getDetail().split(",");
        operationLog.setOperationInfo(JSONUtil.toJsonStr(ArrayUtil.addAll(split, split1)));

        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateEduExperience(HrmEmployeeEducationExperience educationExperience) {
        OperationLog operationLog = new OperationLog();
        HrmEmployee employee = getById(educationExperience.getEmployeeId());
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        if (educationExperience.getEducationId() == null) {
            Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.EDUCATIONAL_EXPERIENCE, educationExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        } else {
            HrmEmployeeEducationExperience old = educationExperienceService.getById(educationExperience.getEducationId());
            Content content = employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.EDUCATIONAL_EXPERIENCE, BeanUtil.beanToMap(old), BeanUtil.beanToMap(educationExperience), educationExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));
        }
        educationExperienceService.saveOrUpdate(educationExperience);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteEduExperience(Long educationId) {
        HrmEmployeeEducationExperience educationExperience = educationExperienceService.getById(educationId);
        OperationLog operationLog = new OperationLog();
        HrmEmployee employee = getById(educationExperience.getEmployeeId());
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, LabelGroupEnum.EDUCATIONAL_EXPERIENCE, educationExperience.getEmployeeId());

        operationLog.setOperationInfo(content.getDetail());
        educationExperienceService.removeById(educationId);
        return operationLog;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateWorkExperience(HrmEmployeeWorkExperience workExperience) {
        HrmEmployee employee = getById(workExperience.getEmployeeId());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        if (workExperience.getWorkExpId() == null) {
            Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.WORK_EXPERIENCE, workExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        } else {
            HrmEmployeeWorkExperience old = workExperienceService.getById(workExperience.getWorkExpId());
            Content content = employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.WORK_EXPERIENCE, BeanUtil.beanToMap(old), BeanUtil.beanToMap(workExperience), workExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));
        }
        //TODO 操作记录
        workExperienceService.saveOrUpdate(workExperience);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteWorkExperience(Long workExpId) {
        HrmEmployeeWorkExperience workExperience = workExperienceService.getById(workExpId);

        HrmEmployee employee = getById(workExperience.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, LabelGroupEnum.WORK_EXPERIENCE, workExperience.getEmployeeId());
        operationLog.setOperationInfo(content.getDetail());
        workExperienceService.removeById(workExpId);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateCertificate(HrmEmployeeCertificate certificate) {
        HrmEmployee employee = getById(certificate.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        if (certificate.getCertificateId() == null) {
            Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.CERTIFICATE, certificate.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));

        } else {
            HrmEmployeeCertificate old = certificateService.getById(certificate.getCertificateId());
            Content content = employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.CERTIFICATE, BeanUtil.beanToMap(old), BeanUtil.beanToMap(certificate), certificate.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));
        }
        certificateService.saveOrUpdate(certificate);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteCertificate(Long certificateId) {

        HrmEmployeeCertificate certificate = certificateService.getById(certificateId);
        HrmEmployee employee = getById(certificate.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, LabelGroupEnum.CERTIFICATE, certificate.getEmployeeId());
        operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        certificateService.removeById(certificateId);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateTrainingExperience(HrmEmployeeTrainingExperience trainingExperience) {

        HrmEmployee employee = getById(trainingExperience.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
        if (trainingExperience.getTrainingId() == null) {
            Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.TRAINING_EXPERIENCE, trainingExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        } else {
            HrmEmployeeTrainingExperience old = trainingExperienceService.getById(trainingExperience.getTrainingId());
            Content content = employeeActionRecordService.entityUpdateRecord(LabelGroupEnum.TRAINING_EXPERIENCE, BeanUtil.beanToMap(old), BeanUtil.beanToMap(trainingExperience), trainingExperience.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));

        }
        trainingExperienceService.saveOrUpdate(trainingExperience);
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteTrainingExperience(Long trainingId) {
        HrmEmployeeTrainingExperience trainingExperience = trainingExperienceService.getById(trainingId);
        HrmEmployee employee = getById(trainingExperience.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, LabelGroupEnum.TRAINING_EXPERIENCE, trainingExperience.getEmployeeId());
        operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        trainingExperienceService.removeById(trainingId);
        return operationLog;
    }

    @Override
    public List<HrmEmployeeField> queryContactsAddField() {
        return employeeFieldService.queryInformationFieldByLabelGroup(LabelGroupEnum.CONTACT_PERSON);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addOrUpdateContacts(UpdateInformationBO updateInformationBO) {
        Long contactsId = updateInformationBO.getContactsId();
        HrmEmployeeContacts oldEmployeeContacts = contactsService.getById(contactsId);
        Long employeeId = updateInformationBO.getEmployeeId();
        List<UpdateInformationBO.InformationFieldBO> fieldList = updateInformationBO.getDataList();
        Map<FiledIsFixedEnum, List<UpdateInformationBO.InformationFieldBO>> isFixedMap = getIsFixedMap(fieldList);
        List<UpdateInformationBO.InformationFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(contactsData -> jsonObject.put(contactsData.getFieldName(), FieldUtil.convertFieldValue(contactsData.getType(), contactsData.getFieldValue(), IsEnum.YES.getValue())));
        HrmEmployeeContacts employeeContacts = jsonObject.toJavaObject(HrmEmployeeContacts.class);
        employeeContacts.setEmployeeId(employeeId);
        employeeContacts.setContactsId(contactsId);

        HrmEmployee employee = getById(employeeContacts.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        if (employeeContacts.getContactsId() == null) {
            Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.CONTACT_PERSON, employeeContacts.getEmployeeId());
            operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));

        } else {
            //固定字段操作记录保存
            Content content = employeeActionRecordService.employeeFixedFieldRecord(BeanUtil.beanToMap(oldEmployeeContacts), BeanUtil.beanToMap(employeeContacts), LabelGroupEnum.CONTACT_PERSON, employeeId);
            operationLog.setOperationInfo(JSONUtil.toJsonStr(content.getDetail().split(",")));
        }
        contactsService.saveOrUpdate(employeeContacts);
        List<UpdateInformationBO.InformationFieldBO> informationFieldBOS = isFixedMap.get(FiledIsFixedEnum.NO_FIXED);
        if (null != informationFieldBOS && informationFieldBOS.size() > 0) {
            List<HrmEmployeeContactsData> hrmEmployeeContactsData = informationFieldBOS.stream()
                    .map(field -> {
                        Object value = field.getFieldValue();
                        if (value == null) {
                            value = "";
                        }
                        field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                        return BeanUtil.copyProperties(field, HrmEmployeeContactsData.class);
                    }).collect(Collectors.toList());
            Dict set = Dict.create().set("key", "contacts_id").set("value", contactsId).set("param", "label_group").set("labelGroup", LabelGroupEnum.CONTACT_PERSON.getValue()).set("dataTableName", "wk_hrm_employee_contacts_data");
            List<HrmModelFiledVO> oldFieldList = ApplicationContextHolder.getBean(IHrmActionRecordService.class).queryFieldValue(set);
            employeeFieldService.saveEmployeeContactsField(hrmEmployeeContactsData, LabelGroupEnum.CONTACT_PERSON, employeeContacts.getContactsId());
            //非固定字段操作记录保存
            employeeActionRecordService.employeeNOFixedFieldRecord(informationFieldBOS, oldFieldList, employeeId);
        }
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog deleteContacts(Long contractsId) {
        HrmEmployeeContacts employeeContacts = contactsService.getById(contractsId);

        HrmEmployee employee = getById(employeeContacts.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.DELETE, LabelGroupEnum.CONTACT_PERSON, employeeContacts.getEmployeeId());
        operationLog.setOperationInfo(JSONUtil.toJsonStr(ListUtil.toList(content.getDetail())));
        contactsService.removeById(contractsId);
        contactsDataService.lambdaUpdate().eq(HrmEmployeeContactsData::getContactsId, contractsId).remove();
        return operationLog;
    }

    @Override
    public List<OperationLog> deleteByIds(List<Long> employeeIds) {
        List<HrmEmployee> hrmEmployees = listByIds(employeeIds);

        List<OperationLog> operationLogList = new ArrayList<>();
        for (HrmEmployee hrmEmployee : hrmEmployees) {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(hrmEmployee.getEmployeeId(), hrmEmployee.getEmployeeName());
            operationLog.setOperationInfo("删除了员工" + hrmEmployee.getEmployeeName());
            operationLogList.add(operationLog);
        }

        lambdaUpdate().set(HrmEmployee::getIsDel, 1).in(HrmEmployee::getEmployeeId, employeeIds).update();
        abnormalChangeRecordService.lambdaUpdate().in(HrmEmployeeAbnormalChangeRecord::getEmployeeId, employeeIds).remove();
        return operationLogList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog change(HrmEmployeeChangeRecord hrmEmployeeChangeRecord) {

        HrmEmployee employee = getById(hrmEmployeeChangeRecord.getEmployeeId());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());
        if (hrmEmployeeChangeRecord.getChangeType() != HrmActionBehaviorEnum.BECOME.getValue()) {
            hrmEmployeeChangeRecord.setOldDept(employee.getDeptId());
            hrmEmployeeChangeRecord.setOldParentId(employee.getParentId());
            hrmEmployeeChangeRecord.setOldPost(employee.getPost());
            hrmEmployeeChangeRecord.setOldPostLevel(employee.getPostLevel());
            hrmEmployeeChangeRecord.setOldWorkAddress(employee.getWorkAddress());
        }
        if (LocalDateTimeUtil.toEpochMilli(hrmEmployeeChangeRecord.getEffectTime()) <= System.currentTimeMillis()) {
            //生效时间是当前或者之前 直接修改员工状态
            HrmEmployee hrmEmployee = EmployeeChangeCron.employeeChangeRecord(hrmEmployeeChangeRecord);
            saveOrUpdate(hrmEmployee);
        } else {
            changeRecordService.saveOrUpdate(hrmEmployeeChangeRecord);
        }
        //添加异动记录
        if (hrmEmployeeChangeRecord.getChangeType() == HrmActionBehaviorEnum.BECOME.getValue()) {
            lambdaUpdate().set(HrmEmployee::getBecomeTime, hrmEmployeeChangeRecord.getEffectTime()).eq(HrmEmployee::getEmployeeId, hrmEmployeeChangeRecord.getEmployeeId())
                    .update();
            abnormalChangeRecordService.addAbnormalChangeRecord(hrmEmployeeChangeRecord.getEmployeeId(), AbnormalChangeType.BECOME, hrmEmployeeChangeRecord.getEffectTime().atStartOfDay());
        } else if (hrmEmployeeChangeRecord.getChangeType() == HrmActionBehaviorEnum.CHANGE_POST.getValue()) {
            abnormalChangeRecordService.addAbnormalChangeRecord(hrmEmployeeChangeRecord.getEmployeeId(), AbnormalChangeType.CHANGE_POST, hrmEmployeeChangeRecord.getEffectTime().atStartOfDay());
        }
        Content content = employeeActionRecordService.changeRecord(hrmEmployeeChangeRecord);
        operationLog.setOperationInfo(content.getDetail());
        return operationLog;
    }


    @Override
    public List<OperationLog> updateInsuranceScheme(UpdateInsuranceSchemeBO updateInsuranceSchemeBO) {
        List<OperationLog> operationLogList = new ArrayList<>();

        updateInsuranceSchemeBO.getEmployeeIds().forEach(employeeId -> {
            Long schemeId = updateInsuranceSchemeBO.getSchemeId();
            HrmEmployee employee = getById(employeeId);
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

            Optional<HrmEmployeeSocialSecurityInfo> socialSecurityInfoOpt = securityInfoService.lambdaQuery()
                    .eq(HrmEmployeeSocialSecurityInfo::getEmployeeId, employeeId).oneOpt();
            Long oldSchemeId = null;
            if (socialSecurityInfoOpt.isPresent()) {
                HrmEmployeeSocialSecurityInfo socialSecurityInfo = socialSecurityInfoOpt.get();
                oldSchemeId = socialSecurityInfo.getSchemeId();
                securityInfoService.lambdaUpdate().set(HrmEmployeeSocialSecurityInfo::getSchemeId, schemeId).eq(HrmEmployeeSocialSecurityInfo::getEmployeeId, employeeId).update();
            } else {
                HrmEmployeeSocialSecurityInfo socialSecurityInfo = new HrmEmployeeSocialSecurityInfo();
                socialSecurityInfo.setSchemeId(schemeId);
                socialSecurityInfo.setEmployeeId(employeeId);
                securityInfoService.save(socialSecurityInfo);
            }
            if (oldSchemeId == null || !oldSchemeId.equals(schemeId)) {
                HrmInsuranceScheme oldInsuranceScheme = insuranceSchemeService.getById(oldSchemeId);
                HrmInsuranceScheme newInsuranceScheme = insuranceSchemeService.getById(schemeId);
                Content content = employeeActionRecordService.updateSchemeRecord(oldInsuranceScheme, newInsuranceScheme, employee);
                operationLog.setOperationInfo(content.getDetail());
                operationLogList.add(operationLog);
            }
        });
        return operationLogList;
    }

    @Override
    public BasePage<Map<String, Object>> queryPageList(QueryEmployeePageListBO employeePageListBO) {
        List<Long> birthdayEmpList = null;
        int six = 6;
        if (employeePageListBO.getToDoRemind() != null && employeePageListBO.getToDoRemind() == six) {
            birthdayEmpList = queryBirthdayEmp();
        }
        String sortField = StrUtil.isNotEmpty(employeePageListBO.getSortField()) ? StrUtil.toUnderlineCase(employeePageListBO.getSortField()) : null;
        employeePageListBO.setSortField(sortField);
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        BasePage<Map<String, Object>> page = employeeMapper.queryPageList(employeePageListBO.parse(), employeePageListBO, birthdayEmpList, employeeIds);
        page.getList().forEach(map -> {
            Long employeeId = Convert.toLong(map.get("employeeId"));
            List<JSONObject> fieldDatalist = employeeDataService.queryFiledListByEmployeeId(employeeId);
            fieldDatalist.forEach(fieldData -> {
                map.put(StrUtil.toCamelCase(fieldData.getString("fieldName")), employeeFieldService.convertValueByFormType(fieldData.getString("fieldValueDesc"), FieldEnum.parse(Integer.valueOf(fieldData.getString("type")))));
            });
            String keyName = "companyAgeStartTime";
            if (map.get(keyName) != null && ObjectUtil.notEqual(Convert.toInt(map.get("entryStatus")), EmployeeEntryStatus.ALREADY_LEAVE.getValue())) {
                LocalDateTime companyAgeStartTime = LocalDateTimeUtil.parse((String) map.get("companyAgeStartTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long nowCompanyAge = LocalDateTimeUtil.between(companyAgeStartTime, LocalDateTime.now()).toDays() + 1;
                if (LocalDateTimeUtil.toEpochMilli(companyAgeStartTime) > System.currentTimeMillis()) {
                    nowCompanyAge = 0;
                }
                map.put("companyAge", EmployeeUtil.computeCompanyAge((int) nowCompanyAge));
            }
        });
        return page;
    }


    public Map<String, HrmEmployeeField> getEmployeeFieldMap() {
        List<HrmEmployeeField> hrmEmployeeFields = employeeFieldService.lambdaQuery().select(HrmEmployeeField::getName, HrmEmployeeField::getFieldName,
                        HrmEmployeeField::getType, HrmEmployeeField::getComponentType)
                .eq(HrmEmployeeField::getIsHeadField, 1).eq(HrmEmployeeField::getIsFixed, 0).eq(HrmEmployeeField::getIsHidden, 0)
                .list();
        Map<String, HrmEmployeeField> employeeFieldMap = new HashMap<>();
        for (HrmEmployeeField employeeField : hrmEmployeeFields) {
            employeeFieldMap.put(employeeField.getFieldName(), employeeField);
            if (employeeField.getComponentType().equals(ComponentType.ADMIN_USER.getValue()) && !employeeFieldMap.containsKey("dept")) {
                employeeFieldMap.put("dept", null);
            } else if (employeeField.getType().equals(ComponentType.ADMIN_DEPT.getValue()) && !employeeFieldMap.containsKey("user")) {
                employeeFieldMap.put("user", null);
            }
        }
        return employeeFieldMap;
    }

    @Override
    public List<SimpleHrmEmployeeVO> querySimpleEmployeeList(Collection<Long> employeeIds) {
        if (CollUtil.isEmpty(employeeIds)) {
            return new ArrayList<>();
        }
        return lambdaQuery().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName)
                .in(HrmEmployee::getEmployeeId, employeeIds).eq(HrmEmployee::getIsDel, 0).list()
                .stream().map(employee -> {
                    SimpleHrmEmployeeVO simpleHrmEmployeeVO = new SimpleHrmEmployeeVO();
                    simpleHrmEmployeeVO.setEmployeeId(employee.getEmployeeId());
                    simpleHrmEmployeeVO.setEmployeeName(employee.getEmployeeName());
                    return simpleHrmEmployeeVO;
                }).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Long> queryEmployeeStatusNum() {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);

        List<HrmEmployee> list;
        if (employeeIds != null && employeeIds.size() == 0) {
            list = new ArrayList<>();
        } else {
            list = lambdaQuery().select(HrmEmployee::getStatus)
                    .in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue(), EmployeeEntryStatus.TO_LEAVE.getValue())
                    .in(employeeIds != null, HrmEmployee::getEmployeeId, employeeIds)
                    .eq(HrmEmployee::getIsDel, 0)
                    .list();
        }
        //查询在职状态人数
        TreeMap<Integer, Long> collect = list
                .stream().collect(Collectors.groupingBy(HrmEmployee::getStatus, TreeMap::new, Collectors.counting()));
        for (EmployeeStatusEnum value : EmployeeStatusEnum.values()) {
            if (!collect.containsKey(value.getValue())) {
                collect.put(value.getValue(), 0L);
            }
        }
        //在职
        collect.put(11, (long) list.size());
        if (employeeIds != null && employeeIds.size() == 0) {
            //全职(正式+试用)
            collect.put(12, 0L);
            //待入职
            collect.put(13, 0L);
            //待离职
            collect.put(14, 0L);
            //已离职
            collect.put(15, 0L);
        } else {
            //全职(正式+试用)
            Long fullTimeCount = lambdaQuery().in(HrmEmployee::getStatus, EmployeeStatusEnum.OFFICIAL.getValue(), EmployeeStatusEnum.TRY_OUT.getValue())
                    .in(employeeIds != null, HrmEmployee::getEmployeeId, employeeIds)
                    .eq(HrmEmployee::getIsDel, 0)
                    .in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue(), EmployeeEntryStatus.TO_LEAVE.getValue()).count();
            collect.put(12, fullTimeCount);
            //待入职
            collect.put(13, lambdaQuery().in(employeeIds != null, HrmEmployee::getEmployeeId, employeeIds).in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.TO_IN.getValue()).eq(HrmEmployee::getIsDel, 0).count());
            //待离职
            collect.put(14, lambdaQuery().in(employeeIds != null, HrmEmployee::getEmployeeId, employeeIds).in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.TO_LEAVE.getValue()).eq(HrmEmployee::getIsDel, 0).count());
            //已离职
            collect.put(15, lambdaQuery().in(employeeIds != null, HrmEmployee::getEmployeeId, employeeIds).in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.ALREADY_LEAVE.getValue()).eq(HrmEmployee::getIsDel, 0).count());

        }
        return collect;
    }

    @Override
    public OperationLog againOnboarding(AddEmployeeFieldManageBO addEmployeeFieldManageBO) {
        //删除离职信息
        quitInfoService.lambdaUpdate().eq(HrmEmployeeQuitInfo::getEmployeeId, addEmployeeFieldManageBO.getEmployeeId())
                .remove();
        // 员工个人信息自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeFieldList = addEmployeeFieldManageBO.getEmployeeFieldList();
        // 员工岗位自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> postFieldList = addEmployeeFieldManageBO.getPostFieldList();
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeDataList = Stream.concat(employeeFieldList.stream(), postFieldList.stream()).collect(Collectors.toList());
        Map<FiledIsFixedEnum, List<AddEmployeeFieldManageBO.EmployeeFieldBO>> isFixedMap = getIsFixedMap(employeeDataList);
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), employeeData.getFieldValue()));


        HrmEmployee hrmEmployee = getById(addEmployeeFieldManageBO.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmEmployee.getEmployeeId(), hrmEmployee.getEmployeeName());
        operationLog.setOperationInfo("为" + hrmEmployee.getEmployeeName() + "办理再入职");
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        employee.setEmployeeId(addEmployeeFieldManageBO.getEmployeeId());
        employee.setEntryStatus(EmployeeEntryStatus.IN.getValue());
        transferEmployee(employee);
        updateById(employee);
        abnormalChangeRecordService.addAbnormalChangeRecord(employee.getEmployeeId(), AbnormalChangeType.NEW_ENTRY, LocalDateTime.now());
        return operationLog;
    }

    @Override
    public EmployeeInfo queryEmployeeInfoByMobile(String mobile) {
        return employeeMapper.queryEmployeeInfoByMobile(mobile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog confirmEntry(AddEmployeeFieldManageBO addEmployeeFieldManageBO) {
        // 员工个人信息自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeFieldList = addEmployeeFieldManageBO.getEmployeeFieldList();
        // 员工岗位自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> postFieldList = addEmployeeFieldManageBO.getPostFieldList();
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeDataList = Stream.concat(employeeFieldList.stream(), postFieldList.stream()).collect(Collectors.toList());
        Map<FiledIsFixedEnum, List<AddEmployeeFieldManageBO.EmployeeFieldBO>> isFixedMap = getIsFixedMap(employeeDataList);
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), employeeData.getFieldValue()));
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        employee.setEmployeeId(addEmployeeFieldManageBO.getEmployeeId());
        boolean exists = lambdaQuery().eq(HrmEmployee::getJobNumber, employee.getJobNumber()).eq(HrmEmployee::getIsDel, 0)
                .ne(HrmEmployee::getEmployeeId, employee.getEmployeeId()).exists();
        if (exists) {
            throw new CrmException(HrmCodeEnum.JOB_NUMBER_EXISTED, employee.getJobNumber());
        }
        employee.setEntryStatus(EmployeeEntryStatus.IN.getValue());
        employee.setIsDel(0);
        if (employee.getEmploymentForms() == EmploymentFormsEnum.OFFICIAL.getValue()) {
            Integer probation = employee.getProbation();
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
        updateById(employee);
        abnormalChangeRecordService.addAbnormalChangeRecord(employee.getEmployeeId(), AbnormalChangeType.NEW_ENTRY, LocalDateTime.now());
        HrmEmployee hrmEmployee = getById(addEmployeeFieldManageBO.getEmployeeId());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmEmployee.getEmployeeId(), hrmEmployee.getEmployeeName());
        operationLog.setOperationInfo("为" + hrmEmployee.getEmployeeName() + "办理入职");

        if (hrmEmployee.getCandidateId() != null) {
            candidateService.lambdaUpdate().set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.HAVE_JOINED.getValue()).set(HrmRecruitCandidate::getEntryTime, new Date())
                    .eq(HrmRecruitCandidate::getCandidateId, hrmEmployee.getCandidateId()).update();
        }
        return operationLog;
    }

    @Override
    public List<HrmEmployeeField> downloadExcelFiled() {
        List<HrmEmployeeField> list = employeeFieldService.lambdaQuery().eq(HrmEmployeeField::getIsImportField, 1).eq(HrmEmployeeField::getIsHidden, 0)
                .list();
        for (HrmEmployeeField hrmEmployeeField : list) {
            if ("channel_id".equals(hrmEmployeeField.getFieldName())) {
                List<HrmRecruitChannel> channelList = recruitChannelService.lambdaQuery().eq(HrmRecruitChannel::getStatus, 1).list();
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (HrmRecruitChannel channel : channelList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", channel.getValue());
                    mapList.add(map);
                }
                hrmEmployeeField.setOptions(JSON.toJSONString(mapList));
            }
        }
        return list.stream().sorted(Comparator.comparingInt(HrmEmployeeField::getSorting)).sorted(Comparator.comparingInt(HrmEmployeeField::getLabel)).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> export(QueryEmployeePageListBO employeePageListBO) {
        List<Map<String, Object>> list = employeeMapper.export(employeePageListBO);
        list.forEach(map -> {
            Long deptId = (Long) map.remove("deptId");
            Integer employmentForms = (Integer) map.remove("employmentForms");
            Integer status = (Integer) map.remove("status");
            Integer idType = (Integer) map.remove("idType");
            HrmDept hrmDept = hrmDeptService.getById(deptId);
            if (hrmDept != null) {
                map.put("deptName", hrmDept.getName());
            } else {
                map.put("deptName", "");
            }
            map.put("employmentForms", EmploymentFormsEnum.parseName(employmentForms));
            map.put("status", EmployeeStatusEnum.parseName(status));
            if (idType == null) {
                map.put("idType", "");
            } else {
                map.put("idType", IdTypeEnum.parseName(idType));
            }
        });
        return list;
    }

    @Override
    public Integer queryFieldValueNoDelete(List<HrmEmployeeField> uniqueList) {
        return employeeMapper.queryFieldValueNoDelete(uniqueList);
    }

    @Override
    public List<Long> queryToInByMonth(int year, int month) {
        return employeeMapper.queryToInByMonth(year, month);
    }

    @Override
    public List<Long> queryToLeaveByMonth(int year, int month) {
        return employeeMapper.queryToLeaveByMonth(year, month);
    }

    @Override
    public List<Long> queryToCorrectCount() {
        return employeeMapper.queryToCorrectCount();
    }


    @Override
    public List<Long> queryBirthdayEmp() {
        DateTime start = DateUtil.beginOfMonth(new Date());
        DateTime end = DateUtil.endOfMonth(new Date());
        DateRange dateTimes = new DateRange(start, end, DateField.DAY_OF_YEAR);
        List<String> lunarList = new ArrayList<>();
        List<String> solarList = new ArrayList<>();
        dateTimes.forEach(dateTime -> {
            String[] strings = queryBirthdayTime(dateTime);
            lunarList.add(strings[0]);
            solarList.add(strings[1]);
        });
        return employeeMapper.queryBirthdayEmp(lunarList, solarList);
    }

    @Override
    public List<HrmEmployee> queryBirthdayListByTime(LocalDate time, Collection<Long> employeeIds) {
        String[] strings = queryBirthdayTime(Timestamp.valueOf(time.atStartOfDay()));
        return employeeMapper.queryBirthdayListByTime(strings[0], strings[1], employeeIds);
    }

    @Override
    public List<HrmEmployee> queryEntryEmpListByTime(LocalDate time, Collection<Long> employeeIds) {
        return employeeMapper.queryEntryEmpListByTime(time, employeeIds);
    }

    private String[] queryBirthdayTime(Date date) {
        ChineseDate chineseDate = new ChineseDate(date);
        String lunarMonth = String.valueOf(chineseDate.getMonth());
        String lunarDay = String.valueOf(chineseDate.getDay());
        if (lunarMonth.length() == 1) {
            lunarMonth = "0" + lunarMonth;
        }
        if (lunarDay.length() == 1) {
            lunarDay = "0" + lunarDay;
        }
        LocalDate localDate = LocalDate.parse(DateUtil.format(date, "yyyy-MM-dd"));
        String solarMonth = String.valueOf(localDate.getMonthValue());
        String solarDay = String.valueOf(localDate.getDayOfMonth());
        if (solarMonth.length() == 1) {
            solarMonth = "0" + solarMonth;
        }
        if (solarDay.length() == 1) {
            solarDay = "0" + solarDay;
        }
        String lunarBirthday = lunarMonth + lunarDay;
        String solarBirthday = solarMonth + solarDay;
        return new String[]{lunarBirthday, solarBirthday};
    }

    @Override
    public List<HrmEmployee> queryBecomeEmpListByTime(LocalDate time, Collection<Long> employeeIds) {
        return employeeMapper.queryBecomeEmpListByTime(time, employeeIds);
    }

    @Override
    public List<HrmEmployee> queryLeaveEmpListByTime(LocalDate time, Collection<Long> employeeIds) {
        return employeeMapper.queryLeaveEmpListByTime(time, employeeIds);
    }

    @Override
    public DeptEmployeeListVO queryDeptEmployeeList(Long deptId) {
        DeptEmployeeListVO deptEmployeeListVO = new DeptEmployeeListVO();
        List<DeptEmployeeVO> deptList = hrmDeptService.queryDeptEmployeeList();
        createTree(deptId, deptList);
        List<SimpleHrmEmployeeVO> employeeList = employeeMapper.querySimpleEmpByDeptId(deptId);
        List<DeptEmployeeVO> collect = deptList.stream().filter(dept -> dept.getParentId().equals(deptId)).collect(Collectors.toList());
        deptEmployeeListVO.setDeptList(collect);
        deptEmployeeListVO.setEmployeeList(employeeList);
        return deptEmployeeListVO;
    }


    @Override
    public DeptEmployeeListVO queryInspectionDeptEmployeeList(Long deptId) {
        Integer dataAuthType = DataAuthEnum.ALL.getValue();
        Collection<Long> deptIds = null;
        if (!EmployeeHolder.isHrmAdmin() && !DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            deptIds = employeeUtil.queryDataAuthDeptId(dataAuthType);
        } else {
            List<HrmDept> hrmDeptList = hrmDeptService.list();
            if (CollectionUtil.isNotEmpty(hrmDeptList)) {
                deptIds = hrmDeptList.stream().map(HrmDept::getDeptId).collect(Collectors.toList());
            }
        }
        DeptEmployeeListVO deptEmployeeListVO = new DeptEmployeeListVO();
        //查询所有部门下的所有员工列表
        List<DeptEmployeeVO> deptList = hrmDeptService.queryDeptEmployeeList();
        //查询当前部门下的员工列表
        List<SimpleHrmEmployeeVO> employeeList = employeeMapper.querySimpleEmpByDeptId(deptId);


        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.CREATE_ACHIEVEMENT_SETTING_MENU_ID);
        List<SimpleHrmEmployeeVO> empFilterList = employeeList.stream().filter(employeeVO -> {
            return employeeIds.contains(employeeVO.getEmployeeId());
        }).collect(Collectors.toList());

        //查询指定部门的下级部门并返回树结构
        List<DeptEmployeeVO> treeDept = createTreeInspection(deptId, deptList);
        DeptEmployeeVO root = new DeptEmployeeVO();

        DeptEmployeeVO node = new DeptEmployeeVO();
        node.setChildren(treeDept);
        //上一步得到的部门树结构是没有经过权限过滤的，这一步对权限进行过滤
        if (CollUtil.isNotEmpty(treeDept)) {
            filterNode(root, node, deptIds);
        }
//        createTree(deptId, treeDept);
        deptEmployeeListVO.setDeptList(root.getChildren());
        deptEmployeeListVO.setEmployeeList(empFilterList);

        return deptEmployeeListVO;
    }

    private List<DeptEmployeeVO> createTree(Long pid, List<DeptEmployeeVO> deptList) {
        List<DeptEmployeeVO> treeDept = new ArrayList<>();
        for (DeptEmployeeVO dept : deptList) {
            if (pid.equals(dept.getParentId())) {
                treeDept.add(dept);
                List<DeptEmployeeVO> children = createTree(dept.getDeptId(), deptList);
                if (CollUtil.isNotEmpty(children)) {
                    for (DeptEmployeeVO child : children) {
                        dept.setAllNum(dept.getAllNum() + child.getAllNum());
                    }
                    dept.setHasChildren(1);
                } else {
                    dept.setHasChildren(0);
                }
            }
        }
        return treeDept;
    }


    private List<DeptEmployeeVO> createTreeInspection(Long pid, List<DeptEmployeeVO> deptList) {
        List<DeptEmployeeVO> treeDept = new ArrayList<>();
        for (DeptEmployeeVO dept : deptList) {
            if (pid.equals(dept.getParentId())) {
                treeDept.add(dept);
                List<DeptEmployeeVO> children = createTreeInspection(dept.getDeptId(), deptList);
                dept.setChildren(children);
            }
        }
        return treeDept;
    }


    /**
     * 树结构根据指定部门重组
     *
     * @param result      返回接过
     * @param node        全部树结构
     * @param findDeptIds 需要筛选的部门id
     */
    private static void filterNode(DeptEmployeeVO result, DeptEmployeeVO node, Collection<Long> findDeptIds) {
        List<DeptEmployeeVO> childList = node.getChildren();
        if (findDeptIds.contains(node.getDeptId())) {
            DeptEmployeeVO newNode = BeanUtil.copyProperties(node, DeptEmployeeVO.class);
            newNode.setChildren(null);
            if (CollUtil.isEmpty(result.getChildren())) {
                result.setChildren(ListUtil.toList(newNode));
            } else {
                List<DeptEmployeeVO> childList1 = result.getChildren();
                childList1.add(newNode);
            }
            findDeptIds.remove(node.getDeptId());
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            for (DeptEmployeeVO deptVO : childList) {
                filterNode(newNode, deptVO, findDeptIds);
            }
        } else {
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            for (DeptEmployeeVO deptVO : childList) {
                filterNode(result, deptVO, findDeptIds);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> adminAddEmployee(List<AddEmployeeBO> employeeBOS) {
        List<OperationLog> operationLogList = new ArrayList<>();
        for (AddEmployeeBO employeeBO : employeeBOS) {
            employeeBO.setEntryStatus(EmployeeEntryStatus.IN.getValue());
            operationLogList.addAll(add(employeeBO));
        }
        return operationLogList;
    }

    @Override
    public Set<SimpleHrmEmployeeVO> queryDeptUserListByUser(DeptUserListByUserBO deptUserListByUserBO) {
        Set<SimpleHrmEmployeeVO> employeeVOS = new HashSet<>();
        if (CollUtil.isNotEmpty(deptUserListByUserBO.getDeptIdList())) {
            List<HrmEmployee> userList = findChildUserList(deptUserListByUserBO.getDeptIdList());
            List<SimpleHrmEmployeeVO> hrmSimpleEmpVOS = TransferUtil.transferList(userList, SimpleHrmEmployeeVO.class);
            employeeVOS.addAll(hrmSimpleEmpVOS);
        }
        if (CollUtil.isNotEmpty(deptUserListByUserBO.getEmployeeIdList())) {
            List<HrmEmployee> userList = query().select("employee_id", "employee_name", "post", "sex", "mobile", "email").in("employee_id", deptUserListByUserBO.getEmployeeIdList())
                    .eq("is_del", 0).in("entry_status", 1, 3).list();
            List<SimpleHrmEmployeeVO> hrmSimpleUserVOS = TransferUtil.transferList(userList, SimpleHrmEmployeeVO.class);
            employeeVOS.addAll(hrmSimpleUserVOS);
        }
        return employeeVOS;
    }

    private List<HrmEmployee> findChildUserList(List<Long> deptIds) {
        List<HrmEmployee> empList = new ArrayList<>();
        for (Long deptId : deptIds) {
            List<HrmEmployee> list = query().select("employee_id", "employee_name", "post", "sex", "mobile", "email").eq("dept_id", deptId)
                    .in("entry_status", 1, 3).eq("is_del", 0).list();
            empList.addAll(list);
            List<HrmDept> childList = hrmDeptService.lambdaQuery().select(HrmDept::getDeptId).eq(HrmDept::getParentId, deptId).list();
            if (CollUtil.isNotEmpty(childList)) {
                List<Long> childDeptIds = childList.stream().map(HrmDept::getDeptId).collect(Collectors.toList());
                empList.addAll(findChildUserList(childDeptIds));
            }
        }
        return empList;
    }

    @Override
    public Set<Long> filterDeleteEmployeeIds(Set<Long> employeeIds) {
        return employeeMapper.filterDeleteEmployeeIds(employeeIds);
    }

    @Override
    public List<SimpleHrmEmployeeVO> queryAllSimpleEmployeeList(Collection<Long> employeeIds) {
        if (CollUtil.isEmpty(employeeIds)) {
            return new ArrayList<>();
        }
        List<HrmEmployee> list = lambdaQuery().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName, HrmEmployee::getEntryStatus, HrmEmployee::getIsDel)
                .in(HrmEmployee::getEmployeeId, employeeIds).list();
        return list.stream().map(this::transferSimpleEmp).collect(Collectors.toList());
    }

    @Override
    public Set<Long> queryChildEmployeeId(List<Long> employeeIds) {
        Set<Long> result = new HashSet<>(employeeIds);
        if (CollUtil.isNotEmpty(employeeIds)) {
            employeeIds.forEach(employeeId -> {
                List<Long> collect = lambdaQuery().select(HrmEmployee::getEmployeeId).eq(HrmEmployee::getParentId, employeeId).list()
                        .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList());
                result.addAll(queryChildEmployeeId(collect));
            });
        }
        return result;
    }

    @Override
    public Set<String> queryEntryStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds) {
        return employeeMapper.queryEntryStatusList(queryNotesStatusBO, employeeIds);
    }

    @Override
    public Set<String> queryBecomeStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds) {
        return employeeMapper.queryBecomeStatusList(queryNotesStatusBO, employeeIds);
    }

    @Override
    public Set<String> queryLeaveStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds) {
        return employeeMapper.queryLeaveStatusList(queryNotesStatusBO, employeeIds);
    }

    @Override
    public List<HrmModelFiledVO> queryEmployeeField(Integer entryStatus) {
        List<HrmModelFiledVO> filedVOS = employeeFieldService.queryField(entryStatus);
        return filedVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OperationLog> addEmployeeField(AddEmployeeFieldManageBO addEmployeeFieldManageBO) {
        String jobNumber = addEmployeeFieldManageBO.getJobNumber();
        if (StrUtil.isNotEmpty(jobNumber)) {
            boolean exists = lambdaQuery().eq(HrmEmployee::getIsDel, 0).eq(HrmEmployee::getJobNumber, jobNumber).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.JOB_NUMBER_EXISTED, jobNumber);
            }
        }


        List<OperationLog> operationLogList = new ArrayList<>();

        if (addEmployeeFieldManageBO.getCandidateId() != null) {
            if (addEmployeeFieldManageBO.getEntryStatus() == 1) {
                UpdateCandidateStatusBO updateCandidateStatusBO = new UpdateCandidateStatusBO();
                updateCandidateStatusBO.setCandidateIds(Collections.singletonList(addEmployeeFieldManageBO.getCandidateId()));
                updateCandidateStatusBO.setStatus(CandidateStatusEnum.HAVE_JOINED.getValue());
                operationLogList.addAll(candidateActionRecordService.updateCandidateStatusRecord(updateCandidateStatusBO));
                candidateService.lambdaUpdate().set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.HAVE_JOINED.getValue())
                        .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                        .set(HrmRecruitCandidate::getEntryTime, new Date())
                        .eq(HrmRecruitCandidate::getCandidateId, addEmployeeFieldManageBO.getCandidateId()).update();

            } else {
                UpdateCandidateStatusBO updateCandidateStatusBO = new UpdateCandidateStatusBO();
                updateCandidateStatusBO.setCandidateIds(Collections.singletonList(addEmployeeFieldManageBO.getCandidateId()));
                updateCandidateStatusBO.setStatus(CandidateStatusEnum.PENDING_ENTRY.getValue());
                operationLogList.addAll(candidateActionRecordService.updateCandidateStatusRecord(updateCandidateStatusBO));
                candidateService.lambdaUpdate().set(HrmRecruitCandidate::getStatus, CandidateStatusEnum.PENDING_ENTRY.getValue())
                        .set(HrmRecruitCandidate::getStatusUpdateTime, new Date())
                        .eq(HrmRecruitCandidate::getCandidateId, addEmployeeFieldManageBO.getCandidateId()).update();
            }

        }
        // 员工个人信息自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeFieldList = addEmployeeFieldManageBO.getEmployeeFieldList();
        // 员工岗位自定义字段列表
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> postFieldList = addEmployeeFieldManageBO.getPostFieldList();
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> employeeDataList = Stream.concat(employeeFieldList.stream(), postFieldList.stream()).collect(Collectors.toList());
        Map<FiledIsFixedEnum, List<AddEmployeeFieldManageBO.EmployeeFieldBO>> isFixedMap = getIsFixedMap(employeeDataList);
        List<AddEmployeeFieldManageBO.EmployeeFieldBO> fixedEmployeeData = isFixedMap.get(FiledIsFixedEnum.FIXED);
        JSONObject jsonObject = new JSONObject();
        fixedEmployeeData.forEach(employeeData -> jsonObject.put(employeeData.getFieldName(), employeeData.getFieldValue()));
        HrmEmployee employee = jsonObject.toJavaObject(HrmEmployee.class);
        if (addEmployeeFieldManageBO.getCandidateId() != null) {
            HrmRecruitCandidate candidate = candidateService.getById(addEmployeeFieldManageBO.getCandidateId());
            employee.setChannelId(candidate.getChannelId());
            employee.setCandidateId(addEmployeeFieldManageBO.getCandidateId());
        }
        employee.setEntryStatus(addEmployeeFieldManageBO.getEntryStatus());
        if (null == employee.getCompanyAgeStartTime()) {
            employee.setCompanyAgeStartTime(employee.getEntryTime());
        }
        transferEmployee(employee);
        save(employee);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(employee.getEmployeeId(), employee.getEmployeeName());

        List<HrmEmployeeData> hrmEmployeeData = isFixedMap.get(FiledIsFixedEnum.NO_FIXED).stream()
                .map(field -> {
                    Object value = field.getFieldValue();
                    if (value == null) {
                        value = "";
                    }
                    field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                    return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                }).collect(Collectors.toList());
        if (!hrmEmployeeData.isEmpty()) {
            List<HrmEmployeeData> personalData = hrmEmployeeData.stream().filter(employeeData -> employeeData.getLabelGroup().equals(LabelGroupEnum.PERSONAL.getValue())).collect(Collectors.toList());
            employeeFieldService.saveEmployeeField(personalData, LabelGroupEnum.PERSONAL, employee.getEmployeeId());
            List<HrmEmployeeData> postData = hrmEmployeeData.stream().filter(employeeData -> employeeData.getLabelGroup().equals(LabelGroupEnum.POST.getValue())).collect(Collectors.toList());
            employeeFieldService.saveEmployeeField(postData, LabelGroupEnum.POST, employee.getEmployeeId());
        }
        boolean exists = employeeFieldService.query().eq("field_name", "flied_kwbova").eq("label_group", LabelGroupEnum.COMMUNICATION.getValue()).eq("type", FieldTypeEnum.MOBILE.getValue()).exists();
        if (exists) {
            //若是存在自定义手机号码字段，则进行更新
            HrmEmployee hrmEmployee = employeeMapper.selectById(employee.getEmployeeId());
            JSONObject employeeModel = BeanUtil.copyProperties(hrmEmployee, JSONObject.class);
            List<HrmEmployeeData> fieldValueList = employeeDataService.queryListByEmployeeId(employee.getEmployeeId());
            List<InformationFieldVO> communicationInformation = transferInformation(employeeModel, LabelGroupEnum.COMMUNICATION, fieldValueList);
            List<InformationFieldVO> informationFieldVOS = communicationInformation.stream().collect(Collectors.groupingBy(employeeData -> FiledIsFixedEnum.parse(employeeData.getIsFixed()))).get(FiledIsFixedEnum.NO_FIXED);
            String fliedKwbova = "flied_kwbova";
            List<HrmEmployeeData> hrmEmployeeInformationData = informationFieldVOS.stream()
                    .map(field -> {
                        if (fliedKwbova.equals(field.getFieldName()) && field.getType().equals(FieldTypeEnum.MOBILE.getValue())) {
                            field.setFieldValue(employee.getMobile());
                            field.setFieldValueDesc(employee.getMobile());
                        }
                        Object value = field.getFieldValue();
                        if (value == null) {
                            value = "";
                        }
                        field.setFieldValue(employeeFieldService.convertObjectValueToString(field.getType(), field.getFieldValue(), value.toString()));
                        return BeanUtil.copyProperties(field, HrmEmployeeData.class);
                    }).collect(Collectors.toList());
            employeeFieldService.saveEmployeeField(hrmEmployeeInformationData, LabelGroupEnum.COMMUNICATION, employee.getEmployeeId());
        }
        Content content = employeeActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, LabelGroupEnum.PERSONAL, employee.getEmployeeId());
        operationLog.setOperationInfo(content.getDetail());
        if (addEmployeeFieldManageBO.getEntryStatus() == 1) {
            abnormalChangeRecordService.addAbnormalChangeRecord(employee.getEmployeeId(), AbnormalChangeType.NEW_ENTRY, LocalDateTime.now());
        }
        //发送通知
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateUser(UserUtil.getUserId());
        adminMessage.setCreateTime(LocalDateTime.now());
        adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employee.getEmployeeId()));
        adminMessage.setLabel(8);
        adminMessage.setType(AdminMessageEnum.HRM_EMPLOYEE_OPEN.getType());
        messageService.save(adminMessage);

        if (operationLogList.isEmpty()) {
            operationLogList.add(operationLog);
        }
        return operationLogList;
    }

    @Override
    public void recordToFormType(InformationFieldVO record, FieldEnum typeEnum) {
        record.setFormType(typeEnum.getFormType());
        switch (typeEnum) {
            case CHECKBOX:
                String contains = "[]";
                if (record.getDefaultValue().toString().contains(contains)) {
                    record.setDefaultValue("");
                }
                record.setDefaultValue(StrUtil.splitTrim((CharSequence) record.getDefaultValue().toString(), Const.SEPARATOR));
                record.setFieldValue(StrUtil.splitTrim((CharSequence) record.getFieldValue(), Const.SEPARATOR));
            case SELECT:
                if (Objects.equals(record.getRemark(), FieldEnum.OPTIONS_TYPE.getFormType())) {
                    if (CollUtil.isEmpty(record.getOptionsData())) {
                        JSONObject optionsData = JSON.parseObject(record.getOptions());
                        record.setOptionsData(optionsData);
                        record.setSetting(new ArrayList<>(optionsData.keySet()));
                    }
                } else {
                    if (CollUtil.isEmpty(record.getSetting())) {
                        try {
                            String dtStr = Optional.ofNullable(record.getOptions()).orElse("").toString();
                            List<Object> jsonArrayList = JSON.parseObject(dtStr, List.class);
                            record.setSetting(jsonArrayList);
                        } catch (Exception e) {
                            record.setSetting(new ArrayList<>(StrUtil.splitTrim(record.getOptions(), Const.SEPARATOR)));
                        }
                    }
                }
                break;
            case DATE_INTERVAL:
                String dataValueStr = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                if (StrUtil.isNotEmpty(dataValueStr)) {
                    record.setDefaultValue(StrUtil.split(dataValueStr, Const.SEPARATOR));
                }
                if (record.getFieldValue() instanceof String) {
                    record.setFieldValue(StrUtil.split((String) record.getFieldValue(), Const.SEPARATOR));
                }
                break;
            case USER:
            case STRUCTURE:
                record.setDefaultValue(new ArrayList<>(0));
                break;
            case AREA:
                String defaultValue = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                record.setDefaultValue(JSON.parse(defaultValue));
                if (record.getFieldValue() instanceof String) {
                    String value = Optional.ofNullable(record.getFieldValue()).orElse("").toString();
                    record.setFieldValue(value);
                }
                break;
            case DETAIL_TABLE:
                if (CollUtil.isEmpty(record.getFieldExtendList())) {
                    record.setFieldExtendList(hrmFieldExtendService.queryHrmFieldExtend(record.getFieldId()));
                }
                record.setFieldValue(employeeFieldService.convertValueByFormType(record.getFieldValue(), typeEnum));
                break;
            case DESC_TEXT:
                record.setFieldValue(record.getDefaultValue());
                break;
            default:
                record.setSetting(new ArrayList<>());
                break;
        }
    }

    @Override
    public SimpleHrmEmployeeVO querySimpleEmployee(Long employeeId) {
        if (employeeId == null) {
            return new SimpleHrmEmployeeVO();
        }
        HrmEmployee hrmEmployee = lambdaQuery().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName)
                .eq(HrmEmployee::getEmployeeId, employeeId).eq(HrmEmployee::getIsDel, 0).one();
        return BeanUtil.copyProperties(hrmEmployee, SimpleHrmEmployeeVO.class);
    }

    /**
     * 查询部门员工列表(考勤打卡调用)
     *
     * @param deptId
     * @return
     */
    @Override
    public DeptEmployeeListVO queryAttendDeptEmployeeList(Long deptId) {
        Integer dataAuthType = DataAuthEnum.ALL.getValue();
        Collection<Long> deptIds = null;
        if (!EmployeeHolder.isHrmAdmin() && !DataAuthEnum.ALL.getValue().equals(dataAuthType)) {
            deptIds = employeeUtil.queryDataAuthDeptId(dataAuthType);
        } else {
            List<HrmDept> hrmDeptList = hrmDeptService.list();
            if (CollectionUtil.isNotEmpty(hrmDeptList)) {
                deptIds = hrmDeptList.stream().map(HrmDept::getDeptId).collect(Collectors.toList());
            }
        }
        DeptEmployeeListVO deptEmployeeListVO = new DeptEmployeeListVO();
        //查询所有部门下的所有员工列表
        List<DeptEmployeeVO> deptList = hrmDeptService.queryDeptEmployeeList();
        //查询当前部门下的员工列表
        List<SimpleHrmEmployeeVO> employeeList = employeeMapper.querySimpleEmpByDeptId(deptId);
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ATTENDANCE_MENU_ID);
        List<SimpleHrmEmployeeVO> empFilterList = employeeList.stream().filter(employeeVO -> {
            return employeeIds.contains(employeeVO.getEmployeeId());
        }).collect(Collectors.toList());
        //查询指定部门的下级部门并返回树结构
        List<DeptEmployeeVO> treeDept = createTreeInspection(deptId, deptList);
        DeptEmployeeVO root = new DeptEmployeeVO();
        DeptEmployeeVO node = new DeptEmployeeVO();
        node.setChildren(treeDept);
        if (CollUtil.isNotEmpty(treeDept)) {
            filterNode(root, node, deptIds);
        }
        deptEmployeeListVO.setDeptList(root.getChildren());
        deptEmployeeListVO.setEmployeeList(empFilterList);
        return deptEmployeeListVO;
    }

    /**
     * 查询考勤范围可查询的所有员工
     *
     * @param employeeName
     * @return
     */
    @Override
    public List<SimpleHrmEmployeeVO> queryAttendanceAllEmployeeList(String employeeName) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        LambdaQueryWrapper<HrmEmployee> wrapper = new QueryWrapper<HrmEmployee>().lambda().select(HrmEmployee::getEmployeeId, HrmEmployee::getEmployeeName, HrmEmployee::getPost,
                HrmEmployee::getEntryStatus, HrmEmployee::getIsDel, HrmEmployee::getDeptId).eq(HrmEmployee::getIsDel, 0).like(StrUtil.isNotEmpty(employeeName), HrmEmployee::getEmployeeName, employeeName);
        List<HrmEmployee> hrmEmployeeList = this.list(wrapper);
        List<SimpleHrmEmployeeVO> simpleHrmEmployeeVOList = new ArrayList<>();
        for (HrmEmployee employee : hrmEmployeeList) {
            if (employeeIds.contains(employee.getEmployeeId())) {
                simpleHrmEmployeeVOList.add(transferSimpleEmp(employee));
            }
        }
        return simpleHrmEmployeeVOList;
    }
}
