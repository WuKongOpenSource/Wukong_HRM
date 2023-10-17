package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.field.utils.FieldUtil;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.AdminLanguageUtil;
import com.kakarote.core.utils.BaseUtil;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmEmployeeFieldMapper;
import com.kakarote.hrm.mapper.HrmSalaryChangeTemplateMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.EmployeeActionRecordServiceImpl;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 自定义字段表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmEmployeeFieldServiceImpl extends BaseServiceImpl<HrmEmployeeFieldMapper, HrmEmployeeField> implements IHrmEmployeeFieldService {

    private static Map<Integer, List<String>> fieldColumnNameProperties = new HashMap<>();

    static {
        fieldColumnNameProperties.put(LabelGroupEnum.PERSONAL.getValue(), Arrays.asList("employee_id", "employee_name", "mobile", "country", "nation", "id_type", "id_number", "sex", "email", "native_place", "date_of_birth", "birthday_type", "birthday", "age", "address", "highest_education", "entry_time", "probation", "become_time", "job_number", "dept_id", "parent_id", "post", "post_level", "work_address", "work_detail_address", "work_city", "employment_forms", "status", "company_age_start_time", "company_age", "is_del", "create_user_id", "create_time", "update_time", "company_id"));
        fieldColumnNameProperties.put(LabelGroupEnum.CONTACT_PERSON.getValue(), Arrays.asList("contacts_id", "employee_id", "contacts_name", "relation", "contacts_phone", "contacts_work_unit", "contacts_post", "contacts_address", "create_time", "sort", "company_id"));
    }

    private static final FieldEnum[] DEFAULT_FIELD_ENUMS = {FieldEnum.AREA, FieldEnum.AREA_POSITION, FieldEnum.CURRENT_POSITION, FieldEnum.DETAIL_TABLE};
    @Autowired
    private HrmEmployeeFieldMapper employeeFieldMapper;

    @Autowired
    private IHrmEmployeeFieldConfigService employeeFieldConfigService;

    @Autowired
    private IHrmEmployeeDataService employeeDataService;

    @Autowired
    private IHrmEmployeeContactsDataService employeeContactsDataService;

    @Autowired
    private IHrmEmployeeContactsService employeeContactsService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IAdminMessageService adminMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private IHrmEmployeeFieldManageService employeeFieldManageService;
    @Autowired
    private IHrmRecruitChannelService recruitChannelService;

    @Autowired
    private IHrmDeptService hrmDeptService;

    @Autowired
    private IHrmFieldExtendService hrmFieldExtendService;

    @Autowired
    private HrmRecruitPostTypeServiceImpl hrmRecruitPostTypeServiceImpl;
    @Autowired
    private HrmSalaryChangeTemplateMapper hrmSalaryChangeTemplateMapper;

    @Autowired
    private HrmSalaryMonthRecordServiceImpl hrmSalaryMonthRecordServiceImpl;

    @Autowired
    private HrmSalaryOptionServiceImpl hrmSalaryOptionServiceImpl;

    @Autowired
    private IHrmSalarySlipTemplateService salarySlipTemplateService;

    @Autowired
    private IHrmSalaryTaxRuleService salaryTaxRuleService;
    @Autowired
    private IHrmSalaryGroupService salaryGroupService;

    @Autowired
    private IHrmInsuranceSchemeService insuranceSchemeService;

    @Autowired
    private IHrmConfigService hrmConfigService;


    @Autowired
    private IHrmFieldSortService hrmFieldSortService;

    @Override
    public List<HrmEmployeeField> queryInformationFieldByLabelGroup(LabelGroupEnum labelGroup) {

        List<HrmEmployeeField> fieldList = lambdaQuery().eq(HrmEmployeeField::getIsHidden, 0)
                .eq(HrmEmployeeField::getLabelGroup, labelGroup.getValue())
                .orderByAsc(HrmEmployeeField::getSorting).list();
        fieldList.forEach(field -> {
            recordToFormType2(field, FieldEnum.parse(field.getType()));
            //添加语言包key
            field.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName(), field.getSetting()));
        });
        return fieldList;
    }

    @Override
    public List<EmployeeHeadFieldVO> queryListHeads() {
        UserInfo user = UserUtil.getUser();
        boolean exists = employeeFieldConfigService.lambdaQuery().eq(HrmEmployeeFieldConfig::getUserId, user.getUserId()).exists();
        if (!exists) {
            List<Long> isHeadFieldIds = employeeFieldMapper.queryHeadFieldId();
            List<HrmEmployeeFieldConfig> fieldConfigList = isHeadFieldIds.stream().map(fieldId -> {
                HrmEmployeeFieldConfig fieldConfig = new HrmEmployeeFieldConfig();
                fieldConfig.setSort(isHeadFieldIds.indexOf(fieldId) + 1);
                fieldConfig.setUserId(user.getUserId());
                fieldConfig.setIsHide(0);
                fieldConfig.setWidth(100);
                fieldConfig.setFieldId(fieldId);
                fieldConfig.setCreateTime(LocalDateTime.now());
                return fieldConfig;
            }).collect(toList());
            employeeFieldConfigService.saveBatch(fieldConfigList);
        }
        List<EmployeeHeadFieldVO> listHeads = employeeFieldMapper.queryListHeads(user.getUserId());
        for (EmployeeHeadFieldVO field : listHeads) {
            field.setFieldName(StrUtil.toCamelCase(field.getFieldName()));
            //添加语言包key
            field.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName()));
        }

        return listHeads;
    }

    @Override
    public void updateFieldConfig(List<UpdateFieldConfigBO> updateFieldConfigBOList) {
        List<HrmEmployeeFieldConfig> fieldConfigList = updateFieldConfigBOList.stream()
                .map(fieldConfigBO -> BeanUtil.copyProperties(fieldConfigBO, HrmEmployeeFieldConfig.class))
                .collect(toList());
        employeeFieldConfigService.saveOrUpdateBatch(fieldConfigList);
    }


    @Override
    public List<FiledListVO> queryFields() {
        List<FiledListVO> listVOS = employeeFieldMapper.queryFields();
        if (CollectionUtil.isNotEmpty(listVOS)) {
            for (FiledListVO listVO : listVOS) {
                //添加语言包key
                listVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrmConfig.", StrUtil.toString(listVO.getLabelGroup())));
            }
        }

        return listVOS;
    }

    @Override
    public List<List<HrmEmployeeField>> queryFieldByLabel(Integer label) {
        List<HrmEmployeeField> fieldList = lambdaQuery()
                .notIn(label.equals(LabelGroupEnum.PERSONAL.getValue()), HrmEmployeeField::getFieldName, "age")
                .orderByAsc(HrmEmployeeField::getSorting)
                .eq(HrmEmployeeField::getLabelGroup, label).list();
        fieldList.forEach(field -> {
            recordToFormType2(field, FieldEnum.parse(field.getType()));
            //添加语言包key
            field.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName(), field.getSetting()));
        });
        return FieldUtil.convertFormPositionFieldList(fieldList, HrmEmployeeField::getXAxis, HrmEmployeeField::getYAxis, HrmEmployeeField::getSorting);
    }

    @Override
    @Transactional(rollbackFor = {CrmException.class})
    public void saveField(AddEmployeeFieldBO addEmployeeFieldBO) {
        Integer labelGroup = addEmployeeFieldBO.getLabelGroup();
        LabelGroupEnum groupEnum = LabelGroupEnum.parse(labelGroup);
        Map<String, String> keyMap = new HashMap<>();
        List<AddEmployeeFieldBO.EmployeeSaveField> employeeSaveFieldVOData = addEmployeeFieldBO.getData();
        if (employeeSaveFieldVOData.isEmpty()) {
            throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        if (employeeSaveFieldVOData.size() > Const.QUERY_MAX_SIZE) {
            throw new CrmException(HrmCodeEnum.HRM_FIELD_NUM_ERROR);
        }

        List<HrmEmployeeField> hrmEmployeeFieldList = new ArrayList<>();
        List<Long> fieldIds = new ArrayList<>();
        for (int i = 0; i < employeeSaveFieldVOData.size(); i++) {
            HrmEmployeeField hrmEmployeeField = BeanUtil.copyProperties(employeeSaveFieldVOData.get(i), HrmEmployeeField.class);
            //判断数字格式限制的小数位数是否正确
            if (FieldUtil.equalsByType(hrmEmployeeField.getType(), FieldEnum.NUMBER, FieldEnum.FLOATNUMBER, FieldEnum.PERCENT)) {
                if (FieldUtil.verifyStrForNumRestrict(hrmEmployeeField.getMaxNumRestrict(), hrmEmployeeField.getMinNumRestrict())) {
                    throw new CrmException(HrmCodeEnum.THE_FIELD_NUM_RESTRICT_ERROR);
                }
            }
            //判断明细表格的数据是否正确
            if (FieldEnum.DETAIL_TABLE.getType().equals(hrmEmployeeField.getType())) {
                if (CollUtil.isEmpty(hrmEmployeeField.getFieldExtendList())) {
                    throw new CrmException(HrmCodeEnum.THE_FIELD_DETAIL_TABLE_FORMAT_ERROR);
                }
            }
            if (ObjectUtil.isEmpty(hrmEmployeeField.getDefaultValue())) {
                hrmEmployeeField.setDefaultValue("");
            } else {
                boolean isNeedHandle = FieldUtil.equalsByType(hrmEmployeeField.getType(), FieldEnum.AREA, FieldEnum.AREA_POSITION, FieldEnum.CURRENT_POSITION);
                if (isNeedHandle) {
                    hrmEmployeeField.setDefaultValue(JSON.toJSONString(hrmEmployeeField.getDefaultValue()));
                }
                if (hrmEmployeeField.getDefaultValue() instanceof Collection) {
                    hrmEmployeeField.setDefaultValue(StrUtil.join(Const.SEPARATOR, hrmEmployeeField.getDefaultValue()));
                }
            }
            if (null == hrmEmployeeField.getFieldId()) {
                if (Arrays.asList(11, 12).contains(labelGroup)) {
                    hrmEmployeeField.setLabel(2);
                } else if (Arrays.asList(1, 2, 3, 4, 5, 6, 7).contains(labelGroup)) {
                    hrmEmployeeField.setLabel(1);
                } else if (Objects.equals(21, labelGroup)) {
                    hrmEmployeeField.setLabel(3);
                } else if (Arrays.asList(31, 32).contains(labelGroup)) {
                    hrmEmployeeField.setLabel(4);
                }
                List<String> existNameList = lambdaQuery().eq(HrmEmployeeField::getLabel, hrmEmployeeField.getLabel()).list().stream().map(HrmEmployeeField::getFieldName).collect(toList());
                String name = getNextFieldName(hrmEmployeeField.getLabel(), hrmEmployeeField.getType(), existNameList, Const.AUTH_DATA_RECURSION_NUM);
                hrmEmployeeField.setFieldName(name);
                hrmEmployeeField.setFieldId(null);
                hrmEmployeeField.setIsFixed(0);
                hrmEmployeeField.setIsHeadField(0);
                hrmEmployeeField.setIsImportField(0);
            } else {
                fieldIds.add(hrmEmployeeField.getFieldId());
            }
            hrmEmployeeField.setSorting(i + 1);
            hrmEmployeeFieldList.add(hrmEmployeeField);
        }
        Map<String, List<HrmEmployeeField>> collect = hrmEmployeeFieldList.stream().collect(groupingBy(HrmEmployeeField::getName));
        for (Map.Entry<String, List<HrmEmployeeField>> entry : collect.entrySet()) {
            if (entry.getValue().size() > 1) {
                throw new CrmException(HrmCodeEnum.CUSTOM_FORM_NAME_CANNOT_BE_REPEATED);
            }
        }

        //字段与表里字段重复校验
        List<String> fieldColumnNameList;
        if (groupEnum == LabelGroupEnum.PERSONAL || groupEnum == LabelGroupEnum.COMMUNICATION || groupEnum == LabelGroupEnum.POST) {
            fieldColumnNameList = fieldColumnNameProperties.get(LabelGroupEnum.PERSONAL.getValue());
        } else {
            fieldColumnNameList = fieldColumnNameProperties.get(LabelGroupEnum.CONTACT_PERSON.getValue());
        }
        List<String> fieldNameList = hrmEmployeeFieldList.stream()
                .filter(adminField -> adminField.getIsFixed() == null || adminField.getIsFixed() == 0)
                .map(HrmEmployeeField::getName)
                .collect(toList());
        Collection<String> intersection = CollUtil.intersection(fieldColumnNameList, fieldNameList);
        if (CollUtil.isNotEmpty(intersection)) {
            CollUtil.join(intersection, ".");
            throw new CrmException(HrmCodeEnum.CUSTOM_FORM_NAME_DUPLICATES_SYSTEM_FIELD);
        }
        //删除该类型下被删除的字段和字段值
        List<Long> deleteFieldIds = lambdaQuery().eq(HrmEmployeeField::getLabelGroup, labelGroup).list().stream().map(HrmEmployeeField::getFieldId).collect(toList());
        deleteFieldIds.removeIf(fieldIds::contains);
        if (CollUtil.isNotEmpty(deleteFieldIds)) {
            if (groupEnum == LabelGroupEnum.PERSONAL || groupEnum == LabelGroupEnum.COMMUNICATION || groupEnum == LabelGroupEnum.POST) {
                employeeDataService.lambdaUpdate().in(HrmEmployeeData::getFieldId, deleteFieldIds).remove();
            } else {
                employeeContactsDataService.lambdaUpdate().in(HrmEmployeeContactsData::getFieldId, deleteFieldIds).remove();
            }
            lambdaUpdate().in(HrmEmployeeField::getFieldId, deleteFieldIds).remove();
            //员工自定义权限管理表删除对应数据
            employeeFieldManageService.lambdaUpdate().in(HrmEmployeeFieldManage::getFieldId, deleteFieldIds).remove();
            //删除自定义字段
            hrmFieldExtendService.lambdaUpdate().in(HrmFieldExtend::getParentFieldId, deleteFieldIds).remove();
        } else {
            deleteFieldIds = new ArrayList<>();
        }
        //修改自定义字段value的name 同步自定义表头
        ArrayList<Long> deleteConfigFieldIds = ListUtil.toList(deleteFieldIds);
        hrmEmployeeFieldList.forEach(hrmEmployeeField -> {
            //自定义字段是表头字段 并且隐藏 删除fieldConfig
            if (hrmEmployeeField.getIsHeadField() == IsEnum.YES.getValue() && hrmEmployeeField.getIsHidden() == IsEnum.YES.getValue()) {
                deleteConfigFieldIds.add(hrmEmployeeField.getFieldId());
            }
            //更新自定义字段value表name
            if (groupEnum == LabelGroupEnum.PERSONAL || groupEnum == LabelGroupEnum.COMMUNICATION || groupEnum == LabelGroupEnum.POST) {
                UpdateWrapper<HrmEmployeeData> updateWrapper = new UpdateWrapper<HrmEmployeeData>().set("name", hrmEmployeeField.getName()).eq("field_id", hrmEmployeeField.getFieldId());
                employeeDataService.update(updateWrapper);
            } else {
                UpdateWrapper<HrmEmployeeContactsData> updateWrapper = new UpdateWrapper<HrmEmployeeContactsData>().set("name", hrmEmployeeField.getName()).eq("field_id", hrmEmployeeField.getFieldId());
                employeeContactsDataService.update(updateWrapper);
            }
            hrmEmployeeField.setLabelGroup(labelGroup);
            saveOrUpdate(hrmEmployeeField);
            if (FieldEnum.DETAIL_TABLE.getType().equals(hrmEmployeeField.getType())) {
                if (null == hrmEmployeeField.getFieldId()) {
                    hrmFieldExtendService.saveOrUpdateHrmFieldExtend(hrmEmployeeField.getFieldExtendList(), hrmEmployeeField.getFieldId(), false);
                } else {
                    hrmFieldExtendService.saveOrUpdateHrmFieldExtend(hrmEmployeeField.getFieldExtendList(), hrmEmployeeField.getFieldId(), true);
                }
            }
            List<HrmEmployeeFieldManage> hrmEmployeeFieldManageList = employeeFieldManageService.lambdaQuery().eq(HrmEmployeeFieldManage::getFieldName, hrmEmployeeField.getFieldName()).list();
            if (hrmEmployeeFieldManageList.size() > 0) {
                hrmEmployeeFieldManageList.forEach(hrmEmployeeFieldManage -> hrmEmployeeFieldManage.setName(hrmEmployeeField.getName()));
                employeeFieldManageService.updateBatchById(hrmEmployeeFieldManageList);
            } else {
                List<Integer> integers = Arrays.asList(1, 2);//员工状态
                for (Integer integer : integers) {
                    HrmEmployeeFieldManage hrmEmployeeFieldManage = BeanUtil.copyProperties(hrmEmployeeField, HrmEmployeeFieldManage.class);
                    hrmEmployeeFieldManage.setEntryStatus(integer);
                    hrmEmployeeFieldManage.setIsManageVisible(1);
                    employeeFieldManageService.save(hrmEmployeeFieldManage);
                }
            }

            //更新多语言语言包
            HrmEmployeeField languageField = hrmEmployeeField;
            if (Objects.equals(languageField.getRemark(), FieldEnum.OPTIONS_TYPE.getFormType())) {
                JSONObject optionsData = JSON.parseObject(languageField.getOptions(), Feature.OrderedField);
                languageField.setOptionsData(optionsData);
                if (CollectionUtil.isEmpty(languageField.getSetting())) {
                    languageField.setSetting(new ArrayList<>(optionsData.keySet()));
                }
            } else {
                if (CollectionUtil.isEmpty(languageField.getSetting())) {
                    if (BaseUtil.isJSONArray(languageField.getOptions())) {
                        languageField.setSetting(JSON.parseArray(languageField.getOptions()));
                    } else {
                        languageField.setSetting(StrUtil.splitTrim(languageField.getOptions(), Const.SEPARATOR));
                    }
                }

            }
            String fieldNameKey = LanguageFieldUtil.getFieldNameKey(languageField.getFieldName(), languageField.getLabel());
            keyMap.put(fieldNameKey, languageField.getName());
            if (CollectionUtil.isNotEmpty(languageField.getSetting())) {
                Map<String, String> optionsKeyNameMap = LanguageFieldUtil.getOptionsKeyNameMap(languageField.getSetting(), languageField.getFieldName(), languageField.getLabel());
                keyMap.putAll(optionsKeyNameMap);
            }
        });


        if (CollUtil.isNotEmpty(deleteConfigFieldIds)) {
            employeeFieldConfigService.lambdaUpdate().in(HrmEmployeeFieldConfig::getFieldId, deleteConfigFieldIds).remove();
        }

    }

    @Override
    public void saveEmployeeField(List<HrmEmployeeData> fieldList, LabelGroupEnum labelGroupEnum, Long employeeId) {
        if (fieldList == null || fieldList.size() == 0) {
            return;
        }
        QueryWrapper<HrmEmployeeData> wrapper = Wrappers.<HrmEmployeeData>query().eq("employee_id", employeeId).eq("label_group", labelGroupEnum.getValue());
        employeeDataService.remove(wrapper);
        fieldList.forEach(fieldData -> {
            fieldData.setLabelGroup(labelGroupEnum.getValue());
            fieldData.setEmployeeId(employeeId);
            if (fieldData.getType().equals(FieldTypeEnum.CHECKBOX.getValue())) {
                if (StrUtil.isNotEmpty(fieldData.getFieldValue())) {
                    String contains = "[]";
                    if (fieldData.getFieldValue().contains(contains)) {
                        fieldData.setFieldValue("");
                        fieldData.setFieldValueDesc("");
                    } else {
                        List<String> optionList = Arrays.stream(fieldData.getFieldValue().split(",")).collect(Collectors.toList());
                        CollectionUtil.sortByPinyin(optionList);
                        fieldData.setFieldValue(CollectionUtil.join(optionList, ","));
                        fieldData.setFieldValueDesc(CollectionUtil.join(optionList, ","));
                    }

                }
            } else {
                fieldData.setFieldValueDesc(fieldData.getFieldValue());
            }

        });
        employeeDataService.saveBatch(fieldList);
    }

    @Override
    public void saveEmployeeContactsField(List<HrmEmployeeContactsData> fieldList, LabelGroupEnum contactPerson, Long contactsId) {
        if (fieldList == null || fieldList.size() == 0) {
            return;
        }
        QueryWrapper<HrmEmployeeContactsData> wrapper = Wrappers.<HrmEmployeeContactsData>query().eq("contacts_id", contactsId).eq("label_group", contactPerson.getValue());
        employeeContactsDataService.remove(wrapper);
        fieldList.forEach(fieldData -> {
            fieldData.setLabelGroup(contactPerson.getValue());
            fieldData.setContactsId(contactsId);
        });
        employeeContactsDataService.saveBatch(fieldList);
    }

    @Override
    public VerifyUniqueBO verifyUnique(VerifyUniqueBO verifyUniqueBO) {
        HrmEmployeeField employeeField = getById(verifyUniqueBO.getFieldId());
        if (employeeField.getIsUnique().equals(IsEnum.YES.getValue())) {
            Integer count;
            if (employeeField.getLabelGroup().equals(LabelGroupEnum.CONTACT_PERSON.getValue())) {
                if (employeeField.getIsFixed().equals(IsEnum.YES.getValue())) {
                    count = employeeContactsService.verifyUnique(employeeField.getFieldName(), verifyUniqueBO.getValue(), verifyUniqueBO.getId());
                } else {
                    count = employeeContactsDataService.verifyUnique(verifyUniqueBO.getFieldId(), verifyUniqueBO.getValue(), verifyUniqueBO.getId());
                }
            } else {
                if (employeeField.getIsFixed().equals(IsEnum.YES.getValue())) {
                    QueryChainWrapper<HrmEmployee> wrapper = employeeService.query().eq(employeeField.getFieldName(), verifyUniqueBO.getValue()).eq("is_del", 0);
                    if (verifyUniqueBO.getId() != null) {
                        count = wrapper.ne("employee_id", verifyUniqueBO.getId()).count().intValue();
                    } else {
                        count = wrapper.count().intValue();
                    }
                } else {
                    count = employeeDataService.verifyUnique(verifyUniqueBO.getFieldId(), verifyUniqueBO.getValue(), verifyUniqueBO.getId());
                }
            }
            if (count != null && count > 0) {
                verifyUniqueBO.setStatus(0);
            } else {
                verifyUniqueBO.setStatus(1);
            }
        }
        return verifyUniqueBO;
    }

    @Override
    public void updateFieldWidth(UpdateFieldWidthBO updateFieldWidthBO) {
        employeeFieldConfigService.lambdaUpdate()
                .set(HrmEmployeeFieldConfig::getWidth, updateFieldWidthBO.getWidth())
                .eq(HrmEmployeeFieldConfig::getUserId, UserUtil.getUserId())
                .eq(HrmEmployeeFieldConfig::getFieldId, updateFieldWidthBO.getFieldId()).update();
    }

    @Override
    public List<EmployeeArchivesFieldVO> queryEmployeeArchivesField() {
        List<EmployeeArchivesFieldVO> fieldVOS = getBaseMapper().queryEmployeeArchivesField();
        if (CollectionUtil.isNotEmpty(fieldVOS)) {
            for (EmployeeArchivesFieldVO field : fieldVOS) {
                //添加语言包key
                field.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName()));
            }
        }
        return fieldVOS;
    }

    @Override
    public void setEmployeeArchivesField(List<EmployeeArchivesFieldVO> archivesFields) {
        List<HrmEmployeeField> employeeFields = archivesFields.stream().map(field -> {
            HrmEmployeeField hrmEmployeeField = new HrmEmployeeField();
            hrmEmployeeField.setFieldId(field.getFieldId());
            hrmEmployeeField.setIsEmployeeVisible(field.getIsEmployeeVisible());
            hrmEmployeeField.setIsEmployeeUpdate(field.getIsEmployeeUpdate());
            return hrmEmployeeField;
        }).collect(toList());
        updateBatchById(employeeFields);
    }


    @Override
    public List<OperationLog> sendWriteArchives(SendWriteArchivesBO writeArchivesBO) {
        Set<Long> userIds = new HashSet<>(writeArchivesBO.getUserIds());
        List<Long> longList = userService.queryUserByDeptIds(writeArchivesBO.getDeptIds()).getData();
        userIds.addAll(longList);
        List<OperationLog> operationLogList = new ArrayList<>();

        for (Long userId : userIds) {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationObject("员工档案");
            operationLog.setOperationInfo(UserUtil.getUser().getNickname() + "邀请" + UserCacheUtil.getUserName(userId) + "填写员工档案");
            operationLogList.add(operationLog);
            AdminMessage adminMessage = new AdminMessage();
            AdminMessageEnum adminMessageEnum = AdminMessageEnum.HRM_WRITE_ARCHIVES;
            adminMessage.setTitle(adminMessageEnum.getRemarks());
            adminMessage.setLabel(adminMessageEnum.getLabel());
            adminMessage.setType(adminMessageEnum.getType());
            adminMessage.setCreateUser(UserUtil.getUserId());
            adminMessage.setRecipientUser(userId);
            adminMessage.setCreateTime(LocalDateTime.now());
            adminMessageService.save(adminMessage);
        }
        return operationLogList;
    }

    @Override
    public List<HrmModelFiledVO> queryField(Integer entryStatus) {
        List<HrmEmployeeFieldManage> employeeFieldList = employeeFieldManageService.queryManageField(entryStatus);
        Map<String, Integer> employeeFieldMap = new HashMap<>();
        employeeFieldList.forEach(hrmEmployeeFieldManage -> {
            employeeFieldMap.put(StrUtil.toCamelCase(hrmEmployeeFieldManage.getFieldName()), hrmEmployeeFieldManage.getIsManageVisible());
        });
        List<HrmEmployeeField> hrmFieldList = lambdaQuery().eq(HrmEmployeeField::getIsHidden, 0).ne(HrmEmployeeField::getFieldName, "company_age").ne(HrmEmployeeField::getFieldName, "become_time").in(HrmEmployeeField::getLabelGroup, 1, 11)
                .orderByAsc(HrmEmployeeField::getLabelGroup, HrmEmployeeField::getSorting).list();

        hrmFieldList.removeIf(field -> Arrays.asList(0, 2).contains(employeeFieldMap.get(StrUtil.toCamelCase(field.getFieldName()))));
        // 非正式类型
        String informalType = "[{\"name\":\"实习\",\"value\":3},{\"name\":\"兼职\",\"value\":4},{\"name\":\"劳务\",\"value\":5},{\"name\":\"顾问\",\"value\":6},{\"name\":\"返聘\",\"value\":7},{\"name\":\"外包\",\"value\":8}]";
        List<HrmModelFiledVO> fieldList = hrmFieldList.stream().map(field -> {
            HrmModelFiledVO hrmModelFiled = BeanUtil.copyProperties(field, HrmModelFiledVO.class);
            FieldEnum typeEnum = FieldEnum.parse(hrmModelFiled.getType());
            String fieldName = StrUtil.toCamelCase(hrmModelFiled.getFieldName());
            hrmModelFiled.setIsManageRequired(employeeFieldMap.get(fieldName));
            hrmModelFiled.setFormType(typeEnum.getFormType());
            recordToFormType(hrmModelFiled, typeEnum);
            String status = "status";
            String channelId = "channelId";
            if (status.equals(fieldName)) {
                hrmModelFiled.setOptions(informalType);
                hrmModelFiled.setSetting(JSON.parseObject(informalType, List.class));
                hrmModelFiled.setName("非正式类型");
            }
            if (channelId.equals(fieldName)) {
                List<HrmRecruitChannel> channelList = recruitChannelService.lambdaQuery()
                        .eq(HrmRecruitChannel::getStatus, 1).list();
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (HrmRecruitChannel channel : channelList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", channel.getValue());
                    map.put("value", channel.getChannelId());
                    mapList.add(map);
                }
                hrmModelFiled.setOptions(JSON.toJSONString(mapList));
            }

            //添加语言包key
            hrmModelFiled.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", hrmModelFiled.getFieldName(), hrmModelFiled.getSetting()));
            hrmModelFiled.setOriginFieldName(StrUtil.toSymbolCase(hrmModelFiled.getFieldName(), '_'));
            return hrmModelFiled;
        }).collect(Collectors.toList());
        return fieldList;
    }

    @Override
    public List<HrmModelFiledVO> queryHrmField(Integer type) {
        QueryWrapper<HrmEmployeeField> wrapper = new QueryWrapper<>();
        wrapper.eq("label", type).orderByAsc("sorting");
        List<HrmEmployeeField> hrmFieldList = list(wrapper);
        List<HrmModelFiledVO> filedVOList = hrmFieldList.stream().map(field ->
                BeanUtil.copyProperties(field, HrmModelFiledVO.class)
        ).collect(Collectors.toList());
        filedVOList.stream().forEach(
                filedVO -> {
                    filedVO.setFieldType(1);
                }
        );
        return filedVOList;
    }

    @Override
    public void recordToFormType(HrmModelFiledVO record, FieldEnum typeEnum) {
        record.setFormType(typeEnum.getFormType());
        switch (typeEnum) {
            case CHECKBOX:
                record.setDefaultValue(StrUtil.splitTrim((CharSequence) record.getDefaultValue(), Const.SEPARATOR));
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
                if (StrUtil.isNotEmpty(record.getDefaultValue().toString())) {
                    record.setDefaultValue(StrUtil.split((String) record.getDefaultValue(), Const.SEPARATOR));
                }
                break;
            case USER:
            case STRUCTURE:
                record.setDefaultValue(new ArrayList<>(0));
                break;
            case AREA:
            case AREA_POSITION:
            case CURRENT_POSITION:
                String defaultValue = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                record.setDefaultValue(JSON.parse(defaultValue));
                break;
            case DETAIL_TABLE:
                if (CollUtil.isEmpty(record.getFieldExtendList())) {
                    record.setFieldExtendList(hrmFieldExtendService.queryHrmFieldExtend(record.getFieldId()));
                }
                break;
            default:
                record.setSetting(new ArrayList<>());
                break;
        }
    }

    @Override
    public String getNextFieldName(Integer label, Integer fieldType, List<String> existNameList, Integer depth) {
        if (depth < 0) {
            throw new CrmException(SystemCodeEnum.SYSTEM_ERROR);
        }
        List<String> nameList = lambdaQuery().eq(HrmEmployeeField::getLabel, label).eq(HrmEmployeeField::getType, fieldType).notIn(existNameList.size() > 0, HrmEmployeeField::getFieldName, existNameList).list().stream().map(HrmEmployeeField::getFieldName).collect(Collectors.toList());
        if (nameList.size() == 0) {
            String name;
            try {
                name = "field_" + RandomUtil.randomString(RandomUtil.BASE_CHAR, 6);
            } catch (Exception e) {
                log.error("保存出现冲突", e);
                name = getNextFieldName(label, fieldType, existNameList, --depth);
            }
            nameList.add(name);
        }
        return nameList.get(0);
    }

    @Override
    public String convertObjectValueToString(Integer type, Object value, String defaultValue) {
        boolean isNeedHandle = equalsByType(type);
        if (isNeedHandle) {
            if (value instanceof JSONObject) {
                return !ObjectUtil.isEmpty(value) ? ((JSONObject) value).toJSONString() : "";
            }
            return !ObjectUtil.isEmpty(value) ? JSON.toJSONString(value) : "";
        }
        if (FieldEnum.DATE_INTERVAL.getType().equals(type)) {
            if (value instanceof JSONObject) {
                value = !ObjectUtil.isEmpty(value) ? JSON.parseObject(((JSONObject) value).toJSONString(), List.class) : null;
            }
            return !ObjectUtil.isEmpty(value) ? StrUtil.join(Const.SEPARATOR, value) : "";
        }
        return defaultValue;
    }

    @Override
    public boolean equalsByType(Object type) {
        return equalsByType(type, DEFAULT_FIELD_ENUMS);
    }

    @Override
    public boolean equalsByType(Object type, FieldEnum... fieldEnums) {
        if (type instanceof String) {
            for (FieldEnum anEnum : fieldEnums) {
                if (anEnum.getFormType().equals(type)) {
                    return true;
                }
            }
        } else {
            for (FieldEnum anEnum : fieldEnums) {
                if (Objects.equals(anEnum.getType(), type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object convertValueByFormType(Object value, FieldEnum typeEnum) {
        Object newValue = null;
        switch (typeEnum) {
            case CHECKBOX:
                newValue = StrUtil.splitTrim((CharSequence) value, Const.SEPARATOR);
                break;
            case USER:
                newValue = employeeService.querySimpleEmployeeList(TagUtil.toLongSet(value.toString()));
                break;
            case STRUCTURE:
                newValue = hrmDeptService.querySimpleDeptList(TagUtil.toLongSet(value.toString()));
                break;
            case HANDWRITING_SIGN:
                newValue = value.toString();
                break;
            case SINGLE_USER:
                newValue = employeeService.querySimpleEmployee(Convert.toLong(value.toString()));
                break;
            case AREA:
            case CURRENT_POSITION:
            case AREA_POSITION:
                String valueStr = Optional.ofNullable(value).orElse("").toString();
                newValue = JSON.parse(valueStr);
                break;
            case DETAIL_TABLE:
                newValue = handleDetailTableData(value);
                break;
            case DATE_INTERVAL:
                String dateIntervalStr = Optional.ofNullable(value).orElse("").toString();
                newValue = StrUtil.split(dateIntervalStr, Const.SEPARATOR);
                break;
            default:
                newValue = value;
                break;
        }
        return newValue;
    }

    @Override
    public List<Map<String, Object>> getAllFieldLanguageRel() {
        List<Map<String, Object>> listMap = new ArrayList<>();
        //员工字段
        List<HrmEmployeeField> fieldList = list();
        if (CollectionUtil.isNotEmpty(fieldList)) {
            for (HrmEmployeeField field : fieldList) {
                Map<String, String> nameResourceKey = LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName());
                nameResourceKey.forEach((item, value) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("fileName", value);
                    map.put("chinese", field.getName());
                    map.put("translateName", "");
                    listMap.add(map);
                });
                if (ObjectUtil.isNotEmpty(field.getOptions())) {
                    field.setSetting(JSON.parseObject(field.getOptions(), List.class));
                }
                List<?> setting = field.getSetting();
                if (CollectionUtil.isNotEmpty(setting)) {
                    Map<String, String> optionsKeyMap = LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "customField.hrmField.", field.getFieldName(), field.getSetting());
                    for (int i = 0; i < setting.size(); i++) {
                        Map<String, Object> map = new HashMap<>();
                        Object o = setting.get(i);
                        if (ObjectUtil.isNotEmpty(o) && JSONUtil.isTypeJSON(StrUtil.toString(o))) {
                            Object name = "";
                            Object object = JSON.parse(StrUtil.toString(o));
                            if (object instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) object;
                                name = jsonObject.get("name");
                            } else if (object instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) object;
                                name = jsonArray.get(0);
                            } else {
                                name = object;
                            }
                            map.put("fileName", optionsKeyMap.get(StrUtil.toString(i)));
                            map.put("chinese", name);
                            map.put("translateName", "");
                            listMap.add(map);
                        }

                    }
                }


            }
        }

        //岗位类别
        List<HrmRecruitPostType> postTypes = hrmRecruitPostTypeServiceImpl.list();
        if (CollectionUtil.isNotEmpty(postTypes)) {
            for (HrmRecruitPostType postType : postTypes) {
                Map<String, String> languageKeyMap = LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.postType.", postType.getName());
                if (CollectionUtil.isNotEmpty(languageKeyMap)) {
                    languageKeyMap.forEach((item, value) -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("fileName", value);
                        map.put("chinese", postType.getName());
                        map.put("translateName", "");
                        listMap.add(map);
                    });
                }

            }
        }
        //薪资操作
        List<ChangeSalaryOptionVO> options = hrmSalaryChangeTemplateMapper.queryChangeSalaryOption();
        if (CollectionUtil.isNotEmpty(options)) {
            options.forEach(option -> {
                //添加语言包key
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm." + StrUtil.toString(option.getCode()));
                map.put("chinese", option.getName());
                map.put("translateName", "");
                listMap.add(map);
            });
        }
        //薪资变更模板
        List<HrmSalaryChangeTemplate> templateList = hrmSalaryChangeTemplateMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtil.isNotEmpty(templateList)) {
            for (HrmSalaryChangeTemplate hrmSalaryChangeTemplate : templateList) {
                List<ChangeSalaryOptionVO> salaryOptionVOS = JSON.parseArray(hrmSalaryChangeTemplate.getValue(), ChangeSalaryOptionVO.class);
                if (CollectionUtil.isNotEmpty(salaryOptionVOS)) {
                    for (ChangeSalaryOptionVO optionVO : salaryOptionVOS) {
                        //添加语言包key
                        //添加语言包key
                        Map<String, Object> map = new HashMap<>();
                        map.put("fileName", "hrm." + StrUtil.toString(optionVO.getCode()));
                        map.put("chinese", optionVO.getName());
                        map.put("translateName", "");
                        listMap.add(map);
                    }
                }
                //添加语言包key
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm.templateName." + hrmSalaryChangeTemplate.getTemplateName());
                map.put("chinese", hrmSalaryChangeTemplate.getTemplateName());
                map.put("translateName", "");
                listMap.add(map);
            }
        }

        //月度工资
        List<SalaryOptionHeadVO> salaryOptionHeadVOS = hrmSalaryMonthRecordServiceImpl.querySalaryOptionHead();
        if (CollectionUtil.isNotEmpty(salaryOptionHeadVOS)) {
            for (SalaryOptionHeadVO option : salaryOptionHeadVOS) {
                Map<String, String> languageKeyMap = option.getLanguageKeyMap();
                if (CollectionUtil.isNotEmpty(languageKeyMap)) {
                    languageKeyMap.forEach((item, value) -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("fileName", value);
                        map.put("chinese", option.getName());
                        map.put("translateName", "");
                        listMap.add(map);
                    });
                }

            }
        }
        //打印模板
        List<SalaryOptionVO> optionVOS = hrmSalaryOptionServiceImpl.querySalaryOptionList();
        if (CollectionUtil.isNotEmpty(optionVOS)) {
            for (SalaryOptionVO option : optionVOS) {
                Map<String, String> languageKeyMap = option.getLanguageKeyMap();
                if (CollectionUtil.isNotEmpty(languageKeyMap)) {
                    languageKeyMap.forEach((item, value) -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("fileName", value);
                        map.put("chinese", option.getName());
                        map.put("translateName", "");
                        listMap.add(map);
                    });
                }
            }
        }
        //自定义
        for (HrmLanguageEnum languageEnum : HrmLanguageEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", languageEnum.getKey());
            map.put("chinese", languageEnum.getName());
            map.put("translateName", "");
            listMap.add(map);

        }

        //自定义
        for (HrmActionBehaviorEnum hrmActionBehaviorEnum : HrmActionBehaviorEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", hrmActionBehaviorEnum.getLanguageKey());
            map.put("chinese", hrmActionBehaviorEnum.getName());
            map.put("translateName", "");
            listMap.add(map);

        }
        //自定义
        for (LabelGroupEnum labelGroupEnum : LabelGroupEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", labelGroupEnum.getLanguageKey());
            map.put("chinese", labelGroupEnum.getName());
            map.put("translateName", "");
            listMap.add(map);

        }
        Map<LabelGroupEnum, Dict> propertiesMap = EmployeeActionRecordServiceImpl.propertiesMap;
        propertiesMap.forEach((item, value) -> {
            value.forEach((key, val) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm.record.employee.labelGroup" + item.getValue() + "." + key);
                map.put("chinese", val);
                map.put("translateName", "");
                listMap.add(map);

            });
        });
        //工资模板
        List<HrmSalarySlipTemplate> slipTemplates = salarySlipTemplateService.list();
        if (CollectionUtil.isNotEmpty(slipTemplates)) {
            for (HrmSalarySlipTemplate slipTemplate : slipTemplates) {
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm.config.slip.templates." + slipTemplate.getTemplateName());
                map.put("chinese", slipTemplate.getTemplateName());
                map.put("translateName", "");
                listMap.add(map);
            }
        }
        //薪资税规则
        List<HrmSalaryTaxRule> salaryTaxRules = salaryTaxRuleService.queryTaxRuleList();
        if (CollectionUtil.isNotEmpty(salaryTaxRules)) {
            for (HrmSalaryTaxRule salaryTaxRule : salaryTaxRules) {
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm.salary.tax.rule." + salaryTaxRule.getRuleName());
                map.put("chinese", salaryTaxRule.getRuleName());
                map.put("translateName", "");
                listMap.add(map);
            }
        }
        List<HrmSalaryGroup> salaryGroupList = salaryGroupService.list();
        if (CollectionUtil.isNotEmpty(salaryGroupList)) {
            Set<String> groupNames = salaryGroupList.stream().map(HrmSalaryGroup::getGroupName).collect(Collectors.toSet());
            for (String salaryGroup : groupNames) {
                Map<String, Object> groupMap = new HashMap<>();
                groupMap.put("fileName", "hrm.salary.group." + salaryGroup);
                groupMap.put("chinese", salaryGroup);
                groupMap.put("translateName", "");
                listMap.add(groupMap);
            }
            Set<String> changeRules = salaryGroupList.stream().map(HrmSalaryGroup::getChangeRule).collect(Collectors.toSet());
            for (String changeRule : changeRules) {
                Map<String, Object> groupMap = new HashMap<>();
                groupMap.put("fileName", "hrm.salary.change.rule." + changeRule);
                groupMap.put("chinese", changeRule);
                groupMap.put("translateName", "");
                listMap.add(groupMap);
            }
        }

        List<HrmInsuranceScheme> insuranceSchemes = insuranceSchemeService.list();
        if (CollectionUtil.isNotEmpty(insuranceSchemes)) {
            Set<String> schemeNames = insuranceSchemes.stream().map(HrmInsuranceScheme::getSchemeName).collect(Collectors.toSet());
            for (String schemeName : schemeNames) {
                Map<String, Object> groupMap = new HashMap<>();
                groupMap.put("fileName", "hrm.config.insurance.scheme." + schemeName);
                groupMap.put("chinese", schemeName);
                groupMap.put("translateName", "");
                listMap.add(groupMap);
            }
            Set<String> houseTypes = insuranceSchemes.stream().map(HrmInsuranceScheme::getHouseType).collect(Collectors.toSet());
            for (String houseType : houseTypes) {
                Map<String, Object> groupMap = new HashMap<>();
                groupMap.put("fileName", "hrm.config.insurance.house.type." + houseType);
                groupMap.put("chinese", houseType);
                groupMap.put("translateName", "");
                listMap.add(groupMap);
            }
        }

        //薪资税规则
        List<HrmConfig> configs = hrmConfigService.list();
        if (CollectionUtil.isNotEmpty(configs)) {
            for (HrmConfig config : configs) {
                Map<String, Object> map = new HashMap<>();
                map.put("fileName", "hrm.config." + config.getType() + config.getValue());
                map.put("chinese", config.getValue());
                map.put("translateName", "");
                listMap.add(map);
            }
        }
        //错误信息
        for (HrmCodeEnum codeEnum : HrmCodeEnum.values()) {
            //添加语言包key
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", "error." + codeEnum.getCode());
            map.put("chinese", AdminLanguageUtil.repalceMsg(codeEnum.getMsg()));
            map.put("translateName", "");
            listMap.add(map);
        }
        return listMap;
    }

    @Override
    public List<HrmEmployeeField> list(Integer label, boolean isQueryHide) {
        QueryWrapper<HrmEmployeeField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("label", label);
        if (!isQueryHide) {
            queryWrapper.eq("is_hidden", 0);
        }
        queryWrapper.orderByAsc("sorting");
        List<HrmEmployeeField> fieldList = list(queryWrapper);
        Map<String, String> map = fieldList.stream().collect(Collectors.toMap(HrmEmployeeField::getFieldName, HrmEmployeeField::getName, (v1, v2) -> v1));
        map.put("createTime", "创建时间");
        fieldList.forEach(field -> {
            recordToFormType2(field, FieldEnum.parse(field.getType()));
        });
        return fieldList;
    }

    @Override
    public List<HrmFieldSortVO> queryListHead(Integer label) {
        List<HrmFieldSortVO> fieldSortVOList = hrmFieldSortService.queryListHead(label);
        fieldSortVOList.forEach(fieldSort -> {
            String website = "website";
            if (website.equals(fieldSort.getFieldName())) {
                fieldSort.setFormType(FieldEnum.WEBSITE.getFormType());
            } else {
                fieldSort.setFormType(FieldEnum.parse(fieldSort.getType()).getFormType());
            }
        });
        fieldSortVOList.removeIf(fieldSort -> FieldUtil.equalsByType(fieldSort.getType(), FieldEnum.DESC_TEXT, FieldEnum.DETAIL_TABLE, FieldEnum.FIELD_GROUP));
        return fieldSortVOList;
    }

    private void recordToFormType2(HrmEmployeeField record, FieldEnum typeEnum) {
        record.setFormType(typeEnum.getFormType());
        switch (typeEnum) {
            case CHECKBOX:
                record.setDefaultValue(StrUtil.splitTrim((CharSequence) record.getDefaultValue(), Const.SEPARATOR));
            case SELECT:
                if (Objects.equals(record.getRemark(), FieldEnum.OPTIONS_TYPE.getFormType())) {
                    JSONObject optionsData = JSON.parseObject(record.getOptions(), Feature.OrderedField);
                    record.setOptionsData(optionsData);
                    record.setSetting(new ArrayList<>(optionsData.keySet()));
                } else {
                    if (CollUtil.isEmpty(record.getSetting())) {
                        if (JSONUtil.isTypeJSONArray(record.getOptions())) {
                            record.setSetting(JSON.parseArray(record.getOptions()));
                        } else {
                            record.setSetting(StrUtil.splitTrim(record.getOptions(), Const.SEPARATOR));
                        }
                    }
                }
                break;
            case DATE_INTERVAL:
                String dataValueStr = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                if (StrUtil.isNotEmpty(dataValueStr)) {
                    record.setDefaultValue(StrUtil.split(dataValueStr, Const.SEPARATOR));
                }
                break;
            case USER:
            case STRUCTURE:
                record.setDefaultValue(new ArrayList<>(0));
                break;
            case AREA:
            case AREA_POSITION:
            case CURRENT_POSITION:
                String defaultValue = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                record.setDefaultValue(JSON.parse(defaultValue));
                break;
            case DETAIL_TABLE:
                record.setFieldExtendList(hrmFieldExtendService.queryHrmFieldExtend(record.getFieldId()));
                break;
            default:
                record.setSetting(new ArrayList<>());
                break;
        }
    }

    private List<List<HrmModelFiledVO>> handleDetailTableData(Object value) {
        List<List<HrmModelFiledVO>> dtDataList = new ArrayList<>();
        String dtStr = Optional.ofNullable(value).orElse("").toString();
        List<JSONArray> jsonArrayList = JSON.parseObject(dtStr, List.class);
        if (null != jsonArrayList && jsonArrayList.size() > 0) {
            jsonArrayList.forEach(jsonArray -> {
                dtDataList.add(jsonArray.toJavaList(HrmModelFiledVO.class));
            });
            for (List<HrmModelFiledVO> hrmModelFiled : dtDataList) {
                for (HrmModelFiledVO hrmModelFiledVO : hrmModelFiled) {
                    FieldEnum fieldEnum = FieldEnum.parse(hrmModelFiledVO.getType());
                    Object valueData = hrmModelFiledVO.getFieldValue();
                    if (ObjectUtil.isEmpty(valueData)) {
                        continue;
                    }
                    switch (fieldEnum) {
                        case CHECKBOX:
                            if (valueData instanceof String) {
                                hrmModelFiledVO.setFieldValue(StrUtil.split(valueData.toString(), Const.SEPARATOR));
                            }
                            break;
                        case USER:
                            hrmModelFiledVO.setFieldValue(employeeService.querySimpleEmployeeList(TagUtil.toLongSet(valueData.toString())));
                            break;
                        case STRUCTURE:
                            hrmModelFiledVO.setFieldValue(hrmDeptService.querySimpleDeptList(TagUtil.toLongSet(valueData.toString())));
                            break;
                        case FILE:
                            hrmModelFiledVO.setFieldValue(ApplicationContextHolder.getBean(AdminFileService.class).queryFileList(valueData.toString()));
                            break;
                        case SINGLE_USER:
                            hrmModelFiledVO.setFieldValue(employeeService.querySimpleEmployee(Convert.toLong(value.toString())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return dtDataList;
    }
}
