package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.AppraisalPlanCommon;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.EmploymentFormsEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.constant.appraisal.*;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmAppraisalPlanMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.utils.AchievementsUtil;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import com.kakarote.hrm.utils.EmployeeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 考核计划基础信息表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-21
 */
@Slf4j
@Service
public class HrmAppraisalPlanServiceImpl extends BaseServiceImpl<HrmAppraisalPlanMapper, HrmAppraisalPlan> implements IHrmAppraisalPlanService {

    @Autowired
    private IHrmAppraisalPlanQuotaSettingService appraisalPlanQuotaSettingService;

    @Autowired
    private IHrmQuotaSettingDimensionService quotaSettingDimensionService;

    @Autowired
    private IHrmQuotaSettingDimensionQuotaService quotaSettingDimensionQuotaService;

    @Autowired
    private IHrmAppraisalPlanProcessSettingService appraisalPlanProcessSettingService;

    @Autowired
    private IHrmAppraisalPlanResultSettingService planResultSettingService;

    @Autowired
    private IHrmAppraisalPlanRelationEmployeeService appraisalPlanRelationEmployeeService;

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @Autowired
    private IHrmAchievementsResultTemplateService resultTemplateService;

    @Autowired
    private IHrmAchievementsAssessmentTemplateService templateService;

    @Autowired
    private IHrmAppraisalEmployeeStageService appraisalEmployeeStageService;

    @Autowired
    private IHrmAppraisalEmployeeDimensionService appraisalEmployeeDimensionService;

    @Autowired
    private IHrmAppraisalEmployeeQuotaService appraisalEmployeeQuotaService;

    @Autowired
    private IAdminMessageService adminMessageService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmAppraisalPlanInspectionScopeService inspectionScopeService;

    @Autowired
    private IHrmAppraisalPlanService planService;

    @Autowired
    private AchievementsUtil achievementsUtil;

    @Autowired
    private IHrmEmployeeAchievementFileService achievementFileService;

    @Autowired
    private IHrmAppraisalEmployeeRealScoreUserService realScoreUserService;

    @Autowired
    private IHrmAppraisalPlanEmployeeTypeService appraisalPlanEmployeeTypeService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Autowired
    private IHrmEmployeeAchievementFileService employeeAchievementFileService;

    @Override
    public BasePage<AppraisalPlanVO> queryPageList(QueryAppraisalPlanBO queryAppraisalPlanBO) {
        AppraisalStatusEnum appraisalStatusEnum = AppraisalStatusEnum.parse(queryAppraisalPlanBO.getStatus());
        if (ObjectUtil.isNull(appraisalStatusEnum)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "要查询的表单状态不存在！");
        }
        if (ObjectUtil.isNotNull(queryAppraisalPlanBO.getStatus()) && queryAppraisalPlanBO.getStatus().equals(AppraisalPlanCommon.STATUS_DEL)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "要查询的表单状态不存在！");
        }

        Collection<Long> employeeIds = new ArrayList<>();
        //当前考核计划除了管理员，上级角色只能查看自己创建的数据
        employeeIds = employeeUtil.queryAppraisalPlanDataAuthEmpIdByMenuId(MenuIdConstant.APPRAISAL_PLAN_MENU_ID);

        BasePage<AppraisalPlanVO> appraisalPlanVOList = baseMapper.queryAppraisalPlanPageListByStatus(queryAppraisalPlanBO.parse(), queryAppraisalPlanBO.getAppraisalPlanName(), queryAppraisalPlanBO.getStatus(), employeeIds);

        if (CollectionUtil.isNotEmpty(appraisalPlanVOList.getList())) {
            List<AppraisalPlanVO> appraisalPlanList = appraisalPlanVOList.getList();
            List<Long> appraisalPlanIdList = new ArrayList<>();
            appraisalPlanVOList.getList().forEach(
                    appraisalPlan -> {
                        appraisalPlanIdList.add(appraisalPlan.getAppraisalPlanId());
                    }
            );
            Map<Long, String> inspectionScope = inspectionScopeService.queryInspectionScope(appraisalPlanIdList);
            appraisalPlanList.forEach(
                    appraisalPlan -> {
                        String inspectionScopeName = inspectionScope.get(appraisalPlan.getAppraisalPlanId());
                        if (StrUtil.isNotEmpty(inspectionScopeName)) {
                            appraisalPlan.setInspectionScopeName(inspectionScopeName);
                        }
                    }
            );
        }
        //todo  对数量统计做处理
        return appraisalPlanVOList;
    }

    private String employeeStatusName(Integer employeeStatus) {
        //员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包
        String statusName = "";
        switch (employeeStatus) {
            case 1:
                statusName = "正式";
                break;
            case 2:
                statusName = "试用";
                break;
            case 3:
                statusName = "实习";
                break;
            case 4:
                statusName = "兼职";
                break;
            case 5:
                statusName = "劳务";
                break;
            case 6:
                statusName = "顾问";
                break;
            case 7:
                statusName = "返聘";
                break;
            case 8:
                statusName = "外包";
                break;
        }
        return statusName;
    }

    @Override
    public BasePage<AppraisalPlanOfEmployeeVO> queryEmployeeAppraisalList(QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        BasePage<AppraisalPlanOfEmployeeVO> planOfEmployeeVOBasePage = baseMapper.queryAppraisalOfEmployeeList(queryEmployeeQuotaBO.parse(), queryEmployeeQuotaBO);
        List<AppraisalPlanOfEmployeeVO> dataList = planOfEmployeeVOBasePage.getList();
        if (CollectionUtil.isNotEmpty(dataList)) {
            dataList.forEach(
                    data -> {
                        data.setStageStatusName(AppraisalStageStatusEnum.parseName(data.getStageType()));
                        data.setEmploymentFormsName(EmploymentFormsEnum.parseName(data.getEmploymentForms()));
                    }
            );
        }
        return planOfEmployeeVOBasePage;
    }

    @Override
    public BasePage<Map<String, Object>> queryAppraisalResultPageList(HrmSearchBO search) {
        return appraisalEmployeeService.queryEmployeeAppraisalPageList(search);
    }

    @Transactional
    @Override
    public OperationLog addOrUpdate(AppraisalPlanBO appraisalPlanBO) {

        OperationLog operationLog = new OperationLog();
        //todo 草稿和未开始的区别
        checkSaveParam(appraisalPlanBO);//保存参数校验
        HrmAppraisalPlan appraisalPlan = new HrmAppraisalPlan();
        BeanUtil.copyProperties(appraisalPlanBO, appraisalPlan);
        appraisalPlan.setStatus(AppraisalPlanCommon.STATUS_OFF);//因为只有为开始的考核计划允许编辑，所以这里不管新增和编辑都设置为未开启
        if (ObjectUtil.isNull(appraisalPlanBO.getAppraisalPlanId())) {
            appraisalPlan.setCreateTime(new Date());
            appraisalPlan.setCreateUserId(EmployeeHolder.getEmployeeId());
            operationLog.setBehavior(BehaviorEnum.SAVE);
            operationLog.setOperationInfo("新建了：" + appraisalPlan.getAppraisalPlanName());
        } else {
            HrmAppraisalPlan hrmAppraisalPlan = getById(appraisalPlanBO.getAppraisalPlanId());
            operationLog.setOperationInfo("修改了：" + hrmAppraisalPlan.getAppraisalPlanName());

            operationLog.setBehavior(BehaviorEnum.UPDATE);
            appraisalPlan.setUpdateTime(new Date());
            appraisalPlan.setUpdateUserId(EmployeeHolder.getEmployeeId());
        }

        if (ObjectUtil.isNotNull(appraisalPlanBO.getAppraisalEmployeeResultSettingBO())) {
            appraisalPlan.setResultTemplateId(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getResultTemplateId());
        }
        if (ObjectUtil.isNotNull(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getSyncToSalary())) {
            appraisalPlan.setSyncToSalary(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getSyncToSalary());
        } else {
            appraisalPlan.setSyncToSalary(false);
        }
        appraisalPlan.setPaidForMonth(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getPaidForMonth());
        saveOrUpdate(appraisalPlan);
        if (operationLog.getBehavior().equals(BehaviorEnum.SAVE)) {
            operationLog.setOperationObject(appraisalPlan.getAppraisalPlanId(), appraisalPlan.getAppraisalPlanName());
        }

        lambdaUpdate().set(HrmAppraisalPlan::getEmployeeStatus, null).set(HrmAppraisalPlan::getEmployType, null).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).update();
        inspectionScopeService.lambdaUpdate().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).remove();
        appraisalPlanEmployeeTypeService.lambdaUpdate().eq(HrmAppraisalPlanEmployeeType::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).remove();
        //保存考核计划指标设置
        appraisalPlanQuotaSettingService.saveQuotaSetting(appraisalPlanBO.getAppraisalPlanQuotaSettingBO(), appraisalPlan.getAppraisalPlanId());
        appraisalPlanProcessSettingService.saveAppraisalPlanProcessSetting(appraisalPlanBO.getAppraisalPlanProcessSettingBO(), appraisalPlan.getAppraisalPlanId());
        planResultSettingService.savePlanResultSetting(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getAppraisalPlanResultSettingLevelList(), appraisalPlan.getAppraisalPlanId());

        List<HrmAppraisalPlanEmployeeType> appraisalPlanEmployeeTypeList = new ArrayList<>();
        //处理考核范围中的部门关联关系
        List<InspectionScopeBO> inspectionScopeBO = appraisalPlanBO.getInspectionScope();
        inspectionScopeBO.forEach(
                inspectionScope -> {
                    Integer typeValue = inspectionScope.getType();
                    AppraisalRangeTypeEnum employeeType = AppraisalRangeTypeEnum.parse(typeValue);
                    switch (employeeType) {
                        case EMPLOYEE_DEPT:
                            List<Long> deptIds = inspectionScope.getDeptIds();
                            List<Long> employeeIdList = inspectionScope.getEmployeeIds();
                            List<HrmAppraisalPlanInspectionScope> appraisalPlanInspectionScopeList = new ArrayList<>();
                            if (CollectionUtil.isNotEmpty(deptIds)) {
                                deptIds.forEach(
                                        deptId -> {
                                            HrmAppraisalPlanInspectionScope appraisalPlanInspectionScope = new HrmAppraisalPlanInspectionScope();
                                            appraisalPlanInspectionScope.setAppraisalPlanId(appraisalPlan.getAppraisalPlanId());
                                            appraisalPlanInspectionScope.setRecordId(deptId);
                                            appraisalPlanInspectionScope.setType(InspectionTypeEnum.DEPT.getValue());
                                            appraisalPlanInspectionScopeList.add(appraisalPlanInspectionScope);
                                        }
                                );
                            }
                            if (CollectionUtil.isNotEmpty(employeeIdList)) {
                                employeeIdList.forEach(
                                        employeeId -> {
                                            HrmAppraisalPlanInspectionScope appraisalPlanInspectionScope = new HrmAppraisalPlanInspectionScope();
                                            appraisalPlanInspectionScope.setAppraisalPlanId(appraisalPlan.getAppraisalPlanId());
                                            appraisalPlanInspectionScope.setRecordId(employeeId);
                                            appraisalPlanInspectionScope.setType(InspectionTypeEnum.EMPLOYEE.getValue());
                                            appraisalPlanInspectionScopeList.add(appraisalPlanInspectionScope);

                                        }
                                );
                            }
                            inspectionScopeService.saveBatch(appraisalPlanInspectionScopeList);
                            break;
                        case EMPLOYEE_TYPE:
                            List<Integer> employeeStatus = inspectionScope.getEmployeeStatus();
                            if (CollectionUtil.isEmpty(employeeStatus)) {
                                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "员工状态不能为空");
                            }
                            String employeeStatusStr = employeeStatus.stream().map(
                                    Object::toString
                            ).collect(Collectors.joining(","));
                            HrmAppraisalPlanEmployeeType appraisalPlanEmployeeType = new HrmAppraisalPlanEmployeeType();
                            appraisalPlanEmployeeType.setEmployeeType(inspectionScope.getEmployType());
                            appraisalPlanEmployeeType.setAppraisalPlanId(appraisalPlan.getAppraisalPlanId());
                            appraisalPlanEmployeeType.setEmployeeStatus(employeeStatusStr);
                            appraisalPlanEmployeeTypeList.add(appraisalPlanEmployeeType);
                            break;
                    }
                }
        );
        if (CollectionUtil.isNotEmpty(appraisalPlanEmployeeTypeList)) {
            appraisalPlanEmployeeTypeService.saveBatch(appraisalPlanEmployeeTypeList);
        }
        return operationLog;
    }


    @Override
    public Boolean verifyUnique(String planName, Long appraisalPlanId) {
        LambdaQueryChainWrapper<HrmAppraisalPlan> baseQuery = lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanName, planName);
        if (ObjectUtil.isNotNull(appraisalPlanId)) {
            baseQuery.ne(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId);
        }
        baseQuery.ne(HrmAppraisalPlan::getStatus, AppraisalPlanCommon.STATUS_DEL);
        boolean exists = baseQuery.exists();
        if (exists) {
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public OperationLog delAppraisalPlan(Long appraisalPlanId) {


        HrmAppraisalPlan appraisalPlan = lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).one();
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        OperationLog operationLog = new OperationLog();

        operationLog.setOperationObject(appraisalPlan.getAppraisalPlanId(), appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("删除了：" + appraisalPlan.getAppraisalPlanName());


        employeeAchievementFileService.delAppraisalRecordByAppraisalPlanId(appraisalPlanId);
        /**
         * 删除基础配置数据
         */
        //删除考核计划流程设置
        HrmAppraisalPlanProcessSetting planProcessSetting = appraisalPlanProcessSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).one();
        appraisalPlanProcessSettingService.delProcessSetting(planProcessSetting.getProcessId());
        appraisalPlanQuotaSettingService.deleteByAppraisalPlanId(appraisalPlanId);
        planResultSettingService.lambdaUpdate().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).remove();
        inspectionScopeService.lambdaUpdate().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlanId).remove();
        appraisalPlanEmployeeTypeService.lambdaUpdate().eq(HrmAppraisalPlanEmployeeType::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).remove();
        /**
         * 删除检查考核设置的时候产生的数据
         */
        //删除考核计划关联员工
        appraisalPlanRelationEmployeeService.lambdaUpdate().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).remove();
        //删除考核计划员工阶段流程-由于检查并开启的时候会建立员工考核计划阶段流程信息 所以如果当前考核计划删除的时候要删除掉这些关联信息
        appraisalEmployeeStageService.lambdaUpdate().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).remove();
        //删除真实评分人列表
        realScoreUserService.lambdaUpdate().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalPlanId).remove();
        lambdaUpdate().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).remove();
        return operationLog;
    }

    @Transactional
    @Override
    public OperationLog delAppraisalPlanOfFile(Long appraisalPlanId) {

        HrmAppraisalPlan appraisalPlan = lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalPlan::getStatus, AppraisalStatusEnum.PLACE_ON_FILE.getValue()).one();
        if (appraisalPlan == null) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(appraisalPlan.getAppraisalPlanId(), appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("删除了：" + appraisalPlan.getAppraisalPlanName());


        //删除考核绩效档案相关数据
        employeeAchievementFileService.delAppraisalRecordByAppraisalPlanId(appraisalPlanId);
        /**
         * 删除基础配置数据
         */
        //删除考核计划流程
        /**
         * 删除基础配置数据
         */
        //删除考核计划流程设置
        HrmAppraisalPlanProcessSetting planProcessSetting = appraisalPlanProcessSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlanId).one();
        appraisalPlanProcessSettingService.delProcessSetting(planProcessSetting.getProcessId());
        appraisalPlanQuotaSettingService.deleteByAppraisalPlanId(appraisalPlanId);
        planResultSettingService.lambdaUpdate().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).remove();
        inspectionScopeService.lambdaUpdate().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlanId).remove();
        /**
         * 删除检查考核设置的时候产生的数据
         */
        //删除考核计划关联员工
        appraisalPlanRelationEmployeeService.lambdaUpdate().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).remove();
        //删除考核计划员工阶段流程-由于检查并开启的时候会建立员工考核计划阶段流程信息 所以如果当前考核计划删除的时候要删除掉这些关联信息
        appraisalEmployeeService.delEmployeeAppraisalData(appraisalPlanId);
        appraisalEmployeeStageService.lambdaUpdate().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).remove();
        //删除真实评分人列表
        realScoreUserService.lambdaUpdate().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalPlanId).remove();
        lambdaUpdate().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).remove();
        return operationLog;
    }

    @Override
    public AppraisalPlanSettingInfoVO querySetting(Long appraisalPlanId) {
        HrmAppraisalPlan appraisalPlanInfo = lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).one();
        if (ObjectUtil.isNull(appraisalPlanInfo)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        AppraisalPlanSettingInfoVO appraisalPlanSettingInfoVO = new AppraisalPlanSettingInfoVO();
        HrmAchievementsResultTemplate resultTemplate = resultTemplateService.getById(appraisalPlanInfo.getResultTemplateId());
        HrmAchievementsAssessmentTemplate template = templateService.getById(appraisalPlanInfo.getAppraisalTemplateId());
        BeanUtil.copyProperties(appraisalPlanInfo, appraisalPlanSettingInfoVO);
        if (ObjectUtil.isNotNull(resultTemplate)) {
            appraisalPlanSettingInfoVO.setResultTemplateName(resultTemplate.getResultTemplateName());
        }
        if (ObjectUtil.isNotNull(template)) {
            appraisalPlanSettingInfoVO.setAppraisalTemplateName(template.getTemplateName());
        }
        AppraisalPlanQuotaSettingVO appraisalPlanQuotaSettingVO = appraisalPlanQuotaSettingService.queryPlanQuotaSettingVO(appraisalPlanId);
        AppraisalPlanProcessSettingVO appraisalPlanProcessSettingVO = appraisalPlanProcessSettingService.queryProcessSettingVO(appraisalPlanId);
        List<AppraisalPlanResultSettingVO> appraisalPlanResultSettingVOList = planResultSettingService.queryResultSettingList(appraisalPlanId);
        appraisalPlanSettingInfoVO.setAppraisalPlanQuotaSettingVO(appraisalPlanQuotaSettingVO);
        appraisalPlanSettingInfoVO.setAppraisalPlanProcessSettingVO(appraisalPlanProcessSettingVO);

        AppraisalEmployeeResultSettingVO appraisalEmployeeResultSettingVO = new AppraisalEmployeeResultSettingVO();
        appraisalEmployeeResultSettingVO.setSyncToSalary(appraisalPlanInfo.getSyncToSalary());
        appraisalEmployeeResultSettingVO.setResultTemplateId(appraisalPlanInfo.getResultTemplateId());
        appraisalEmployeeResultSettingVO.setPaidForMonth(appraisalPlanInfo.getPaidForMonth());
        appraisalEmployeeResultSettingVO.setAppraisalPlanResultSettingLevelList(appraisalPlanResultSettingVOList);
        appraisalPlanSettingInfoVO.setAppraisalPlanResultSettingVO(appraisalEmployeeResultSettingVO);

        //考核范围
        List<InspectionScopeBO> inspectionScope = new ArrayList<>();
        InspectionScopeBO employeeDeptType = new InspectionScopeBO();
        employeeDeptType.setType(AppraisalRangeTypeEnum.EMPLOYEE_DEPT.getValue());
        List<Long> deptIds = new ArrayList<>();
        List<Long> employeeIds = new ArrayList<>();
        List<HrmAppraisalPlanInspectionScope> inspectionScopes = inspectionScopeService.lambdaQuery().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlanId).list();
        if (CollectionUtil.isNotEmpty(inspectionScopes)) {
            inspectionScopes.forEach(
                    inspectionScopeData -> {
                        if (inspectionScopeData.getType().equals(InspectionTypeEnum.EMPLOYEE.getValue())) {
                            employeeIds.add(inspectionScopeData.getRecordId());
                        }
                        if (inspectionScopeData.getType().equals(InspectionTypeEnum.DEPT.getValue())) {
                            deptIds.add(inspectionScopeData.getRecordId());
                        }
                    }
            );
            employeeDeptType.setDeptIds(deptIds);
            employeeDeptType.setEmployeeIds(employeeIds);
            inspectionScope.add(employeeDeptType);
        }

        List<HrmAppraisalPlanEmployeeType> appraisalPlanEmployeeTypeList = appraisalPlanEmployeeTypeService.lambdaQuery().eq(HrmAppraisalPlanEmployeeType::getAppraisalPlanId, appraisalPlanId).list();
        if (CollectionUtil.isNotEmpty(appraisalPlanEmployeeTypeList)) {
            appraisalPlanEmployeeTypeList.forEach(
                    appraisalPlanEmployeeType -> {
                        InspectionScopeBO employeeType = new InspectionScopeBO();
                        employeeType.setType(AppraisalRangeTypeEnum.EMPLOYEE_TYPE.getValue());
                        employeeType.setEmployType(appraisalPlanEmployeeType.getEmployeeType());
                        String employeeStatusValue = appraisalPlanEmployeeType.getEmployeeStatus();
                        if (StrUtil.isNotEmpty(employeeStatusValue)) {
                            List<Integer> employeeStatus = Arrays.stream(employeeStatusValue.split(",")).map(value -> Integer.valueOf(value)).collect(Collectors.toList());
                            employeeType.setEmployeeStatus(employeeStatus);
                        }
                        inspectionScope.add(employeeType);
                    }
            );
        }
        appraisalPlanSettingInfoVO.setInspectionScope(inspectionScope);
        return appraisalPlanSettingInfoVO;
    }

    @Override
    public BasePage<JSONObject> queryEmployee(QueryAppraisalEmployeeBO queryAppraisalEmployeeBO) {
        List<JSONObject> employeeList = new ArrayList<>();
        queryAppraisalEmployeeBO.setPageType(1);
        //查询考核员工列表
        BasePage<AppraisalEmployeeVO> appraisalEmployeeList = appraisalPlanRelationEmployeeService.queryAppraisalEmployeeList(queryAppraisalEmployeeBO);
        List<AppraisalEmployeeVO> appraisalEmployeeVOList = appraisalEmployeeList.getList();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeVOList)) {
            List<Long> employeeIdList = appraisalEmployeeVOList.stream().map(AppraisalEmployeeVO::getEmployeeId).collect(Collectors.toList());

            //查询考核计划头列表
            List<HeadFieldVO> headFieldVOList = queryEmployeeListHeadInfo(queryAppraisalEmployeeBO.getAppraisalPlanId());
            //查询员工考核阶段列表-由于此处是分页后的一页数据因此不需要在此限制查询数量
            List<HrmAppraisalEmployeeStage> stageListInfo = appraisalEmployeeStageService.queryStageByEmployeeIds(queryAppraisalEmployeeBO.getAppraisalPlanId(), employeeIdList);
            //保存员工id和其对应的考核阶段信息
            Map<Long, List<HrmAppraisalEmployeeStage>> stageList = stageListInfo.stream().collect(Collectors.groupingBy(HrmAppraisalEmployeeStage::getEmployeeId));
            appraisalEmployeeVOList.forEach(
                    appraisalEmployeeVO -> {
                        List<HrmAppraisalEmployeeStage> employeeStageList = stageList.get(appraisalEmployeeVO.getEmployeeId());
                        JSONObject employeeAndStageData = assemblyValue(appraisalEmployeeVO, employeeStageList, headFieldVOList);
                        employeeList.add(employeeAndStageData);
                    }
            );
        }
        BasePage<JSONObject> resultList = appraisalEmployeeList.copy(JSONObject.class);
        resultList.setList(employeeList);
        return resultList;
    }


    private JSONObject assemblyValue(AppraisalEmployeeVO appraisalEmployeeVO, List<HrmAppraisalEmployeeStage> employeeStageList, List<HeadFieldVO> headFieldVOList) {
        JSONObject employeeAndStageData = new JSONObject();
        int j = 0;
        for (int i = 0; i < headFieldVOList.size(); i++) {
            HeadFieldVO headFieldVO = headFieldVOList.get(i);
            employeeAndStageData.put("employeeId", appraisalEmployeeVO.getEmployeeId());
            if (headFieldVO.getKey().equals("employeeName")) {
                employeeAndStageData.put(headFieldVO.getKey(), appraisalEmployeeVO.getEmployeeName());
            }
            if (headFieldVO.getKey().equals("deptName")) {
                employeeAndStageData.put(headFieldVO.getKey(), appraisalEmployeeVO.getDeptName());
            }
            if (ObjectUtil.isNotNull(headFieldVO.getStageType()) && CollectionUtil.isNotEmpty(employeeStageList)) {//如果阶段类型不为空，且有值的时候才赋值并递增，因为列表头的字段值可能为空可以跳过，但是实际数据的值不能跳过
                HrmAppraisalEmployeeStage employeeStage = employeeStageList.get(j);
                if (ObjectUtil.isNotNull(employeeStage) && headFieldVO.getStageType().equals(employeeStage.getStageType())) {
                    employeeAndStageData.put(headFieldVO.getKey(), employeeStage.getStageUserName());
                    j++;
                } else {
                    employeeAndStageData.put(headFieldVO.getKey(), "-");
                }
            }
        }
        return employeeAndStageData;
    }

    @Override
    public Map<Integer, Long> queryAppraisalStatusNum() {
        Collection<Long> employeeIds = new ArrayList<>();
        //当前考核计划除了管理员，上级角色只能查看自己创建的数据
        employeeIds = employeeUtil.queryAppraisalPlanDataAuthEmpIdByMenuId(MenuIdConstant.APPRAISAL_PLAN_MENU_ID);
        LambdaQueryChainWrapper<HrmAppraisalPlan> baseLambda = lambdaQuery();
        baseLambda.select(HrmAppraisalPlan::getStatus);
        if (CollectionUtil.isNotEmpty(employeeIds)) {
            baseLambda.in(HrmAppraisalPlan::getCreateUserId, employeeIds);
        }
        TreeMap<Integer, Long> collect = baseLambda.list()
                .stream().collect(Collectors.groupingBy(HrmAppraisalPlan::getStatus, TreeMap::new, Collectors.counting()));
        for (AppraisalStatusEnum value : AppraisalStatusEnum.values()) {
            if (!collect.containsKey(value.getValue())) {
                collect.put(value.getValue(), 0L);
            }
        }
        return collect;
    }

    @Override
    public List<HeadFieldVO> queryEmployeeListHeadInfo(Long appraisalPlanId) {
        List<HrmAppraisalEmployeeStage> stageList = appraisalEmployeeStageService.queryMaxCountStageData(appraisalPlanId);
        List<HeadFieldVO> headList = new ArrayList<>();
        HeadFieldVO nameField = new HeadFieldVO("employeeName", "姓名", null);
        HeadFieldVO deptField = new HeadFieldVO("deptName", "部门", null);
        headList.add(nameField);
        headList.add(deptField);
        Integer curStageSort = 0;//当前阶段内sort
        Boolean nextChange = true;
        Integer size = stageList.size();
        for (int i = 0; i < stageList.size(); i++) {
            HrmAppraisalEmployeeStage curStage = stageList.get(i);
            if (curStage.getStageType().equals(AppraisalStageStatusEnum.END.getValue())) {
                continue;
            }
            HeadFieldVO headFieldVO = AppraisalStageStatusEnum.generateKey(curStage.getStageType());
            if (!nextChange) {//如果当前阶段没改变则将当前的值加上目前阶段内的排序
                headFieldVO.setKey(headFieldVO.getKey() + curStageSort);
                headFieldVO.setName(headFieldVO.getName() + curStageSort);
            }
            headList.add(headFieldVO);
            if (i < size - 1) {//如果下一阶段不为空
                HrmAppraisalEmployeeStage nextStage = stageList.get(i + 1);//获取下一阶段
                if (curStage.getStageType().equals(nextStage.getStageType())) {//如果当前阶段类型和下一阶段类型相同则设置标志位并把阶段内排序递增
                    nextChange = false;
                    curStageSort++;
                } else {
                    nextChange = true;
                    curStageSort = 0;
                }
            }
        }
        return headList;
    }


    @Transactional
    @Override
    public void addAppraisalEmployee(AppraisalEmployeeSaveBO appraisalEmployeeSaveBO) {
        List<Long> employeeIdList = appraisalEmployeeSaveBO.getEmployeeIdList();
        List<HrmAppraisalPlanRelationEmployee> appraisalPlanRelationEmployeeList = new ArrayList<>();
        List<HrmAppraisalPlanInspectionScope> appraisalPlanInspectionScopeList = new ArrayList<>();
        List<Long> employeeIds = appraisalPlanRelationEmployeeService.lambdaQuery().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId()).list()
                .stream().map(HrmAppraisalPlanRelationEmployee::getEmployeeId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(employeeIds)) {
            employeeIdList.removeAll(employeeIds);
        }
        employeeIdList.forEach(
                employeeId -> {
                    HrmAppraisalPlanRelationEmployee appraisalPlanRelationEmployee = new HrmAppraisalPlanRelationEmployee();
                    appraisalPlanRelationEmployee.setAppraisalPlanId(appraisalEmployeeSaveBO.getAppraisalPlanId());
                    appraisalPlanRelationEmployee.setEmployeeId(employeeId);
                    appraisalPlanRelationEmployee.setCreateTime(new Date());
                    appraisalPlanRelationEmployeeList.add(appraisalPlanRelationEmployee);
                    HrmAppraisalPlanInspectionScope appraisalPlanInspectionScope = new HrmAppraisalPlanInspectionScope();
                    appraisalPlanInspectionScope.setAppraisalPlanId(appraisalEmployeeSaveBO.getAppraisalPlanId());
                    appraisalPlanInspectionScope.setRecordId(employeeId);
                    appraisalPlanInspectionScope.setType(InspectionTypeEnum.EMPLOYEE.getValue());
                    appraisalPlanInspectionScopeList.add(appraisalPlanInspectionScope);
                    appraisalEmployeeStageService.addEmployeeAppraisalProcess(appraisalEmployeeSaveBO.getAppraisalPlanId(), employeeId);//添加阶段流程记录
                }
        );
        if (CollectionUtil.isNotEmpty(appraisalPlanRelationEmployeeList)) {
            appraisalPlanRelationEmployeeService.saveBatch(appraisalPlanRelationEmployeeList);
        }
        if (CollectionUtil.isNotEmpty(appraisalPlanInspectionScopeList)) {
            inspectionScopeService.lambdaUpdate().eq(HrmAppraisalPlanInspectionScope::getType, InspectionTypeEnum.EMPLOYEE.getValue())
                    .eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId())
                    .in(HrmAppraisalPlanInspectionScope::getRecordId, appraisalEmployeeSaveBO.getEmployeeIdList()).remove();
            inspectionScopeService.saveBatch(appraisalPlanInspectionScopeList);
        }
    }

    @Override
    public void delAppraisalEmployee(AppraisalEmployeeSaveBO appraisalEmployeeSaveBO) {
        if (ObjectUtil.isNull(appraisalEmployeeSaveBO.getAppraisalPlanId())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核计划不能为空");
        }
        if (CollectionUtil.isEmpty(appraisalEmployeeSaveBO.getEmployeeIdList())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "要移除的员工不能为空");
        }
        HrmAppraisalPlan hrmAppraisalPlan = getById(appraisalEmployeeSaveBO.getAppraisalPlanId());
        if (ObjectUtil.isNull(hrmAppraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        appraisalPlanRelationEmployeeService.lambdaUpdate()
                .eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId())
                .in(HrmAppraisalPlanRelationEmployee::getEmployeeId, appraisalEmployeeSaveBO.getEmployeeIdList()).remove();
        //移除该员工关联的阶段流程
        appraisalEmployeeStageService.lambdaUpdate().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId()).in(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployeeSaveBO.getEmployeeIdList()).remove();
        //移除真实评分人流程
        realScoreUserService.lambdaUpdate().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId()).in(HrmAppraisalEmployeeRealScoreUser::getEmployeeId, appraisalEmployeeSaveBO.getEmployeeIdList()).remove();
        //移除考核范围里面的员工
        inspectionScopeService.lambdaUpdate().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalEmployeeSaveBO.getAppraisalPlanId()).in(HrmAppraisalPlanInspectionScope::getRecordId, appraisalEmployeeSaveBO.getEmployeeIdList()).remove();
    }

    @Transactional
    @Override
    public OperationLog startAppraisalPlan(Long appraisalPlanId) {
        OperationLog operationLog = new OperationLog();

        HrmAppraisalPlan appraisalPlan = getById(appraisalPlanId);
        operationLog.setOperationObject(appraisalPlanId, appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("检查并开启考核");

        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "考核计划已被删除！");
        }
        if (appraisalPlan.getStatus() != AppraisalStatusEnum.NO_START.getValue()) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "考核计划为未开始时才能开启！");
        }
        //先校验参数
        boolean exists = appraisalEmployeeStageService.lambdaQuery().isNull(HrmAppraisalEmployeeStage::getStageUserId).eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).exists();
        if (exists) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_START_ERROR, "含有阶段处理人为空的数据请修改核对后重新开启");
        }
        //将员工考核流程添加进数据库
        List<HrmAppraisalPlanRelationEmployee> employeeList = appraisalPlanRelationEmployeeService.lambdaQuery().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).list();
        List<HrmAppraisalEmployee> appraisalEmployeeList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(employeeList)) {
            employeeList.forEach(
                    employee -> {
                        HrmAppraisalEmployee hrmAppraisalEmployee = new HrmAppraisalEmployee();
                        hrmAppraisalEmployee.setEmployeeId(employee.getEmployeeId());
                        hrmAppraisalEmployee.setAppraisalPlanId(appraisalPlanId);
                        appraisalEmployeeList.add(hrmAppraisalEmployee);
                    }
            );
        }
        if (CollectionUtil.isNotEmpty(appraisalEmployeeList)) {
            appraisalEmployeeService.saveAppraisalEmployeeList(appraisalEmployeeList);//保存员工绩效考核数据
        }
        List<HrmAppraisalEmployee> hrmAppraisalEmployees = appraisalEmployeeService.lambdaQuery().select(HrmAppraisalEmployee::getAppraisalEmployeeId, HrmAppraisalEmployee::getAppraisalPlanId, HrmAppraisalEmployee::getEmployeeId).eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).list();
        hrmAppraisalEmployees.forEach(
                hrmAppraisalEmployee -> {
                    //给考核员工添加考核指标项
                    addAppraisalQuotaItem(appraisalPlanId, hrmAppraisalEmployee.getAppraisalEmployeeId());
                    appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, hrmAppraisalEmployee.getAppraisalEmployeeId())
                            .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, hrmAppraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, hrmAppraisalEmployee.getEmployeeId()).update();
                }
        );

        //更新当前考核计划第一个阶段为待处理
        /**
         * 如果第一个阶段是评分阶段则不需要设置,第一个阶段为待处理阶段,应该让开启评分的时候取设置
         * //todo目前最新逻辑加入执行中阶段作为缓冲，可以直接设置第一个阶段
         */
        appraisalEmployeeStageService.lambdaUpdate().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getSort, 1).set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).update();
        appraisalEmployeeService.updateAllEmployeeStageForStart(appraisalPlanId, 1, AppraisalStatusEnum.RUNNING.getValue());

        lambdaUpdate().set(HrmAppraisalPlan::getStatus, AppraisalPlanCommon.STATUS_IN_PROGRESS).set(HrmAppraisalPlan::getOperationStage, AppraisalOperationStatusEnum.OPEN_SCORING.getValue()).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).update();

        //发送消息通知
        List<HrmAppraisalEmployee> hrmAppraisalEmployeeList = appraisalEmployeeService.lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).list();
        hrmAppraisalEmployeeList.forEach(
                employee -> {
                    HrmAppraisalEmployeeStage hrmAppraisalEmployeeStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, employee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getSort, 1).one();
                    //更新员工绩效档案数据
                    achievementFileService.addOrUpdate(employee.getAppraisalEmployeeId(), employee.getEmployeeId());
                    Integer messageType = achievementsUtil.queryMessageType(hrmAppraisalEmployeeStage.getStageType(), ProcessingStatusEnum.PENDING.getValue());
                    //发送消息通知
                    sendMessage(UserUtil.getUserId(), employee.getEmployeeId(), employee.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
                }
        );
        return operationLog;
    }

    @Override
    public void checkAppraisalPlan(Long appraisalPlanId) {
        //检验考核计划参数和流程设置
        //检验通过后加将员工阶段添加到阶段记录表中
        HrmAppraisalPlan appraisalPlan = planService.getById(appraisalPlanId);
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        List<HrmAppraisalPlanInspectionScope> appraisalPlanInspectionScopeList = inspectionScopeService.lambdaQuery().eq(HrmAppraisalPlanInspectionScope::getAppraisalPlanId, appraisalPlanId).list();
        List<HrmAppraisalPlanRelationEmployee> appraisalPlanRelationEmployeeList = new ArrayList<>();
        List<Long> deptIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(appraisalPlanInspectionScopeList)) {
            appraisalPlanInspectionScopeList.forEach(
                    scopeData -> {
                        if (scopeData.getType().equals(InspectionTypeEnum.EMPLOYEE.getValue())) {
                            userIds.add(scopeData.getRecordId());
                        }
                        if (scopeData.getType().equals(InspectionTypeEnum.DEPT.getValue())) {
                            deptIds.add(scopeData.getRecordId());
                        }
                    }
            );
        }
        if (CollectionUtil.isNotEmpty(deptIds)) {
            deptIds.forEach(
                    deptId -> {
                        List<HrmEmployee> userList = employeeService.lambdaQuery().eq(HrmEmployee::getDeptId, deptId).list();
                        if (CollectionUtil.isNotEmpty(userList)) {
                            userList.forEach(
                                    user -> {
                                        userIds.add(user.getEmployeeId());
                                    }
                            );
                        }
                    }
            );
        }
        List<Long> filterUserIdList = new ArrayList<>();
        List<HrmAppraisalPlanEmployeeType> appraisalPlanEmployeeTypeList = appraisalPlanEmployeeTypeService.lambdaQuery().eq(HrmAppraisalPlanEmployeeType::getAppraisalPlanId, appraisalPlanId).list();
        if (CollectionUtil.isNotEmpty(appraisalPlanEmployeeTypeList)) {//如果存在聘用形式过滤条件则对员工做过滤

            List<HrmEmployee> employeeList = new ArrayList<>();
            appraisalPlanEmployeeTypeList.forEach(
                    appraisalPlanEmployeeType -> {
                        List<Integer> employeeStatus = Arrays.stream(appraisalPlanEmployeeType.getEmployeeStatus().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                        List<HrmEmployee> employeeTypeList = employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).eq(HrmEmployee::getEmploymentForms, appraisalPlanEmployeeType.getEmployeeType()).in(HrmEmployee::getStatus, employeeStatus).list();
                        employeeList.addAll(employeeTypeList);
                    }
            );

            if (CollectionUtil.isNotEmpty(employeeList)) {
                employeeList.forEach(
                        employee -> {
                            if (CollectionUtil.isNotEmpty(userIds)) {
                                if (userIds.contains(employee.getEmployeeId())) {
                                    filterUserIdList.add(employee.getEmployeeId());
                                }
                            } else {
                                filterUserIdList.add(employee.getEmployeeId());
                            }
                        }
                );
            }
        } else {
            filterUserIdList.addAll(userIds);
        }
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        List<Long> finalUserList = filterUserIdList.stream().filter(employeeIds::contains).collect(Collectors.toList());

        finalUserList.forEach(
                filterUserId -> {
                    HrmAppraisalPlanRelationEmployee appraisalPlanRelationEmployee = new HrmAppraisalPlanRelationEmployee();
                    appraisalPlanRelationEmployee.setEmployeeId(filterUserId);
                    appraisalPlanRelationEmployee.setAppraisalPlanId(appraisalPlanId);
                    appraisalPlanRelationEmployeeList.add(appraisalPlanRelationEmployee);
                }
        );

        appraisalPlanRelationEmployeeService.lambdaUpdate().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).remove();
        appraisalPlanRelationEmployeeService.saveBatch(appraisalPlanRelationEmployeeList);
        //检查并开启考核计划前先移除历史的阶段信息然后直接走插入逻辑
        appraisalEmployeeStageService.lambdaUpdate().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).remove();
        appraisalPlanRelationEmployeeList.forEach(
                appraisalPlanRelationEmployee -> {
                    appraisalEmployeeStageService.addEmployeeAppraisalProcess(appraisalPlanId, appraisalPlanRelationEmployee.getEmployeeId());
                }
        );
    }

    /**
     * 给员工考核计划添加指系统配置指标项
     */
    @Transactional
    public void addAppraisalQuotaItem(Long appraisalPlanId, Long appraisalEmployeeId) {
        List<HrmQuotaSettingDimension> dimensionList = quotaSettingDimensionService.lambdaQuery().eq(HrmQuotaSettingDimension::getAppraisalPlanId, appraisalPlanId).list();
        Map<Long, List<HrmQuotaSettingDimensionQuota>> quotaList = quotaSettingDimensionQuotaService.lambdaQuery().eq(HrmQuotaSettingDimensionQuota::getAppraisalPlanId, appraisalPlanId).list().stream().collect(Collectors.groupingBy(HrmQuotaSettingDimensionQuota::getDimensionId));
        dimensionList.forEach(
                dimension -> {
                    HrmAppraisalEmployeeDimension appraisalEmployeeDimension = new HrmAppraisalEmployeeDimension();
                    BeanUtil.copyProperties(dimension, appraisalEmployeeDimension);
                    appraisalEmployeeDimension.setDimensionId(null);
                    appraisalEmployeeDimension.setAppraisalEmployeeId(appraisalEmployeeId);
                    appraisalEmployeeDimensionService.save(appraisalEmployeeDimension);
                    List<HrmQuotaSettingDimensionQuota> quotas = quotaList.get(dimension.getDimensionId());
                    quotas.forEach(
                            quota -> {
                                HrmAppraisalEmployeeQuota appraisalEmployeeQuota = new HrmAppraisalEmployeeQuota();
                                BeanUtil.copyProperties(quota, appraisalEmployeeQuota);
                                appraisalEmployeeQuota.setAppraisalEmployeeId(appraisalEmployeeId);
                                appraisalEmployeeQuota.setQuotaId(null);
                                appraisalEmployeeQuota.setDimensionId(appraisalEmployeeDimension.getDimensionId());
                                appraisalEmployeeQuota.setPreset(true);//系统预设
                                appraisalEmployeeQuotaService.save(appraisalEmployeeQuota);
                            }
                    );
                }
        );

    }

    @Transactional
    @Override
    public OperationLog openScoring(Long appraisalPlanId) {
        //查看当前阶段是否有未完成的
        HrmAppraisalPlan appraisalPlan = getById(appraisalPlanId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(appraisalPlanId, appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("开启评分");

        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        if (!appraisalPlan.getOperationStage().equals(AppraisalOperationStatusEnum.OPEN_SCORING.getValue())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "当前操作无效！");
        }
        List<HrmAppraisalPlanRelationEmployee> appraisalEmployeeList = appraisalPlanRelationEmployeeService.lambdaQuery().eq(HrmAppraisalPlanRelationEmployee::getAppraisalPlanId, appraisalPlanId).list();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeList)) {
            appraisalEmployeeList.forEach(
                    appraisalEmployeeInfo -> {
                        //获取评分阶段第一个节点
                        HrmAppraisalEmployeeStage appraisalEmployee = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployeeInfo.getEmployeeId())
                                .and(item -> item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue())).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
                        //如果开启评分前还有其他节点则校验评分前阶段是否处理完成
                        boolean exists = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId())
                                .ne(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).ne(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.EXECUTING.getValue()).lt(HrmAppraisalEmployeeStage::getSort, appraisalEmployee.getSort()).exists();
                        if (exists) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STATUS_CHANGE_ERROR, "指标填写或确认未完成，无法开启评分");
                        }
                        //更新评分阶段的第一个评分阶段状态为待处理
                        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, appraisalEmployee.getAppraisalStageId()).update();
                        //更新员工绩效考核单状态
                        appraisalEmployeeService.updateEmployeeStageStatus(appraisalPlanId, appraisalEmployee.getEmployeeId(), appraisalEmployee.getSort());
                    }
            );
        }
        //更新全部考核人员的阶段状态
        //更新所有员工的执行中阶段为已处理-一个员工最多只有一个执行中阶段数据
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.EXECUTING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).update();
        //更新当前考核计划可操作按钮状态为绩效面谈
        lambdaUpdate().set(HrmAppraisalPlan::getOperationStage, AppraisalOperationStatusEnum.OPEN_INTERVIEW.getValue()).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).update();
        //查询出阶段中他人评分和自评阶段待处理状态的阶段（由于有些配置会导致他人评分实际上是自评人本人所以这里要两种类型全查）-由于保存的时候做过判断如果是他人评分且他人等于考核人员本人，那么会将评分类型设置为自评
        List<HrmAppraisalEmployeeStage> employeeStageList = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                .in(HrmAppraisalEmployeeStage::getStageType, Arrays.asList(AppraisalStageStatusEnum.SELF_SCORE.getValue(), AppraisalStageStatusEnum.OTHER_SCORE.getValue()))
                .groupBy(HrmAppraisalEmployeeStage::getEmployeeId).list();
        employeeStageList.forEach(
                employeeStage -> {
                    sendMessage(UserUtil.getUserId(), employeeStage.getStageUserId(), employeeStage.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING.getType(), appraisalPlan.getAppraisalPlanName());
                }
        );
        return operationLog;
    }

    @Transactional
    @Override
    public OperationLog terminationPlan(Long appraisalPlanId) {
        HrmAppraisalPlan appraisalPlan = getById(appraisalPlanId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(appraisalPlanId, appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("终止考核");

        employeeAchievementFileService.delAppraisalRecordByAppraisalPlanId(appraisalPlanId);
        appraisalEmployeeService.delEmployeeAppraisalData(appraisalPlanId);//删除员工端数据
        delAppraisalPlan(appraisalPlanId);//删除管理端数据
        return operationLog;
    }

    @Override
    public List<HrmAppraisalPlanLevelNumVO> queryAppraisalPlanResultLevelNum(Long appraisalPlanId) {
        List<HrmAppraisalPlanResultSetting> resultSettingList = planResultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).list();
        List<HrmAppraisalPlanLevelNumVO> appraisalPlanLevelNumVOList = new ArrayList<>();
        resultSettingList.forEach(
                resultSetting -> {
                    HrmAppraisalPlanLevelNumVO appraisalPlanLevelNumVO = new HrmAppraisalPlanLevelNumVO();
                    Integer count = appraisalEmployeeService.lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployee::getLevel, resultSetting.getLevelName()).count().intValue();
                    appraisalPlanLevelNumVO.setLevelName(resultSetting.getLevelName());
                    appraisalPlanLevelNumVO.setCount(count);
                    appraisalPlanLevelNumVOList.add(appraisalPlanLevelNumVO);
                }
        );
        return appraisalPlanLevelNumVOList;
    }

    @Transactional
    @Override
    public OperationLog toInterview(Long appraisalPlanId) {
        //判断所有的评分流程和结果审核流程是否正常结束完成
        HrmAppraisalPlan appraisalPlan = getById(appraisalPlanId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(appraisalPlanId, appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("发起绩效面谈");
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        if (!appraisalPlan.getOperationStage().equals(AppraisalOperationStatusEnum.OPEN_INTERVIEW.getValue())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "当前操作无效！");
        }
        HrmAppraisalPlanProcessSetting planProcessSetting = appraisalPlanProcessSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlanId).one();
        //如果配置了结果审核
        if (ObjectUtil.isNotNull(planProcessSetting.getResultAudit()) && planProcessSetting.getResultAudit()) {
            //判断当前考核计划结果审核阶段是否还有未处理完成的
            boolean exists = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                    .ne(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_AUDIT.getValue()).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STATUS_CHANGE_ERROR, "指标评分或指标审核未完成，无法发起绩效面谈");
            }
        } else {
            //如果未配置结果审核则判断评分阶段是否还有未处理完成的-每个考核计划都有评分阶段
            boolean exists = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId)
                    .ne(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue())
                    .and(item -> item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue())).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STATUS_CHANGE_ERROR, "指标评分或指标审核未完成，无法发起绩效面谈");
            }
        }
        //如果评分流程和结果审核都已完成则判断是否配置了结果确认
        //如果流程配置了结果确认则可以开启绩效面谈
        if (ObjectUtil.isNotNull(planProcessSetting.getResultConfirmation()) && planProcessSetting.getResultConfirmation()) {
            //更新结果确认阶段第一阶段为待审核
            //更新考核阶段到结果确认阶段
            appraisalEmployeeStageService.toInterviewStage(appraisalPlanId);
        }
        //归档阶段要通过按钮开启
        lambdaUpdate().set(HrmAppraisalPlan::getOperationStage, AppraisalOperationStatusEnum.OPEN_FILE.getValue()).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).update();
        //开启绩效则触发目标确认阶段，目标确认人为员工本人
        List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()).list();
        appraisalEmployeeStageList.forEach(
                appraisalEmployeeStage -> {
                    sendMessage(UserUtil.getUserId(), appraisalEmployeeStage.getStageUserId(), appraisalEmployeeStage.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_CONFIRMED.getType(), appraisalPlan.getAppraisalPlanName());
                }
        );
        return operationLog;
    }

    @Transactional
    @Override
    public OperationLog placeOnFile(Long appraisalPlanId) {
        HrmAppraisalPlan appraisalPlan = getById(appraisalPlanId);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(appraisalPlanId, appraisalPlan.getAppraisalPlanName());
        operationLog.setOperationInfo("归档");

        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        if (!appraisalPlan.getOperationStage().equals(AppraisalOperationStatusEnum.OPEN_FILE.getValue())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "当前操作无效！");
        }
        //2.开启归档需进行校验：员工未确认，且对应的申诉流程未结束，无法发起绩效面谈，则toast提示：绩效面谈未结束，无法归档谈
        //查询是否还有员工未进入到结果审核阶段
        HrmAppraisalPlanProcessSetting planProcessSetting = appraisalPlanProcessSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlanId).one();
        //如果流程配置了结果确认则校验结果确认是否完成，如果没有开启结果确认则直接进行归档，因为在绩效面谈阶段已经判断过评分和审核流程是否完成了
        if (ObjectUtil.isNotNull(planProcessSetting.getResultConfirmation()) && planProcessSetting.getResultConfirmation()) {
            //判断是否还有未完成的绩效面谈
            boolean exists = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.END.getValue()).ne(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).exists();
            if (exists) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STATUS_CHANGE_ERROR, "绩效面谈未结束，无法归档");
            }
        }
        //更新当前考核计划中的所有员工考核计划的阶段状态为归档完成
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.END.getValue()).update();
        appraisalEmployeeService.updateAllEmployeeStatus(appraisalPlanId, AppraisalStatusEnum.PLACE_ON_FILE);
        //更新员工考核计划信息的阶段状态
        //更新考核计划总状态未已归档
        lambdaUpdate().set(HrmAppraisalPlan::getStatus, AppraisalStatusEnum.PLACE_ON_FILE.getValue()).set(HrmAppraisalPlan::getStageStatus, AppraisalStageStatusEnum.END.getValue()).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalPlanId).update();

        return operationLog;
    }


    @Override
    public BasePage<EmployeeAppraisalPlanVO> queryEmployeeAppraisalLevelList(QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        return appraisalEmployeeService.queryAppraisalList(queryEmployeeQuotaBO);
    }

    @Override
    public List<HrmModelFiledVO> queryField(Long appraisalPlanId) {
        List<HrmModelFiledVO> filedVOList = new ArrayList<>();
        filedVOList.add(new HrmModelFiledVO("employeeName", "姓名", FieldEnum.TEXT, 1));
        filedVOList.add(new HrmModelFiledVO("mobile", "手机号", FieldEnum.MOBILE, 1));
        filedVOList.add(new HrmModelFiledVO("jobNumber", "工号", FieldEnum.TEXT, 1));
        filedVOList.add(new HrmModelFiledVO("deptId", "部门", FieldEnum.STRUCTURE, 1));

        List<Object> employmentFormList = new ArrayList<>();
        employmentFormList.add(new JSONObject().fluentPut("name", "非正式").fluentPut("value", EmploymentFormsEnum.NO_OFFICIAL.getValue()));
        employmentFormList.add(new JSONObject().fluentPut("name", "正式").fluentPut("value", EmploymentFormsEnum.OFFICIAL.getValue()));
        filedVOList.add(new HrmModelFiledVO("employmentForms", "聘用形式", FieldEnum.SELECT, 1).setSetting(employmentFormList));


        List<Object> appraisalStatusList = new ArrayList<>();
        Arrays.stream(AppraisalStageStatusEnum.values()).forEach(
                appraisalStageStatusEnum -> {
                    appraisalStatusList.add(new JSONObject().fluentPut("name", appraisalStageStatusEnum.getName()).fluentPut("value", appraisalStageStatusEnum.getValue()));
                }
        );
        filedVOList.add(new HrmModelFiledVO("stageStatusName", "考核状态", FieldEnum.SELECT, 1).setSetting(appraisalStatusList));
        filedVOList.add(new HrmModelFiledVO("stageUsers", "待处理人", FieldEnum.USER, 1));
        filedVOList.add(new HrmModelFiledVO("score", "评分", FieldEnum.TEXT, 1));

        List<HrmAppraisalPlanResultSetting> resultSettingList = planResultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlanId).list();

        List<Object> levelList = new ArrayList<>();
        resultSettingList.forEach(
                resultSetting -> {
                    levelList.add(new JSONObject().fluentPut("name", resultSetting.getLevelName()).fluentPut("value", resultSetting.getLevelName()));
                }
        );
        filedVOList.add(new HrmModelFiledVO("level", "结果", FieldEnum.SELECT, 1).setSetting(levelList));
        return filedVOList;
    }

    private void checkQuotaSettingParam(AppraisalPlanQuotaSettingBO appraisalPlanQuotaSettingBO, Integer settingType) {
        if (ObjectUtil.isEmpty(appraisalPlanQuotaSettingBO.getUpperLimitType())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分上限类型不能为空");
        }

        if (ObjectUtil.isEmpty(appraisalPlanQuotaSettingBO.getUpperLimitScore())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分上限不能为空");
        }

        List<QuotaSettingDimensionBO> quotaSettingDimensionBOList = appraisalPlanQuotaSettingBO.getQuotaSettingDimensionBOList();
        if (CollectionUtil.isEmpty(quotaSettingDimensionBOList)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "至少新建一个考核纬度！");
        }

        List<String> dimensionNameList = new ArrayList<>();
        List<String> quotaNameList = new ArrayList<>();
        AtomicReference<Double> dimensionTotal = new AtomicReference<>(0d);//维度权重总和
        quotaSettingDimensionBOList.stream().forEach(
                dimensionBO -> {
                    AtomicReference<Double> total = new AtomicReference<>(0d);
                    dimensionBO.getQuotaSettingDimensionQuotaBOList().stream().forEach(
                            pointBO -> {
                                if (ObjectUtil.isNotNull(pointBO.getQuotaWeight())) {
                                    if (pointBO.getQuotaWeight() > 100) {
                                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "指标权重为 0~100%");
                                    }
                                    total.updateAndGet(v -> new Double((double) (v + pointBO.getQuotaWeight())));
                                }
                                if (!quotaNameList.contains(pointBO.getQuotaName())) {
                                    quotaNameList.add(pointBO.getQuotaName());
                                } else {
                                    throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "指标名称(" + pointBO.getQuotaName() + ")重复");
                                }

                            }
                    );
                    if (ObjectUtil.isNull(dimensionBO.getDimensionWeight())) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度权重为必填项");
                    }
                    if (dimensionBO.getDimensionWeight() > 100) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度权重为 0~100%");
                    }
                    if (ObjectUtil.isNull(dimensionBO.getIsAllowEdit())) {
                        dimensionBO.setIsAllowEdit(false);
                    }
                    //如果流程设置里配置的员工填写，且当前维度允许用户填写，用户才能填写
                    if (dimensionBO.getIsAllowEdit() && settingType.equals(QuotaTypeEnum.EMPLOYEE_FILL.getValue())) {
                        if (total.get() > 100) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度指标(" + dimensionBO.getDimensionName() + ")总和必须小于等于100%！");
                        }
                    } else {
                        if (total.get() != 100) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "纬度指标(" + dimensionBO.getDimensionName() + ")总和必须等于100%！");
                        }
                    }

                    dimensionTotal.updateAndGet(v -> new Double((double) (v + dimensionBO.getDimensionWeight())));//维度权重总和

                    if (!dimensionNameList.contains(dimensionBO.getDimensionName())) {
                        dimensionNameList.add(dimensionBO.getDimensionName());
                    } else {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "维度名称(" + dimensionBO.getDimensionName() + ")重复");
                    }
                }
        );
//        if(dimensionTotal.get()!=100){
//            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核纬度权重合计需等于100%");
//        }

    }

    private void checkSaveParam(AppraisalPlanBO appraisalPlanBO) {
        if (ObjectUtil.isNotNull(appraisalPlanBO.getAppraisalPlanId())) {
            HrmAppraisalPlan appraisalPlan = getById(appraisalPlanBO.getAppraisalPlanId());
            if (ObjectUtil.isNull(appraisalPlan)) {
                throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
            }
            if (!appraisalPlan.getStatus().equals(AppraisalStatusEnum.NO_START.getValue())) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核计划已开启无法进行编辑！");
            }
        }
        if (StrUtil.isEmpty(appraisalPlanBO.getAppraisalPlanName())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核计划名称必填");
        }
        if (CollectionUtil.isEmpty(appraisalPlanBO.getInspectionScope())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核范围必填");
        }

        if (ObjectUtil.isEmpty(appraisalPlanBO.getAppraisalCycleType())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核周期类型必填");
        }
        if (!verifyUnique(appraisalPlanBO.getAppraisalPlanName(), appraisalPlanBO.getAppraisalPlanId())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "考核计划名称已存在");
        }

        if (ObjectUtil.isNull(appraisalPlanBO.getAppraisalEmployeeResultSettingBO())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "结果等级设置不能为空");
        }

        //校验流程配置信息
        checkProcessSaveParam(appraisalPlanBO.getAppraisalPlanProcessSettingBO());
        Integer settingType = appraisalPlanBO.getAppraisalPlanProcessSettingBO().getQuotaSettingType();
        //校验维度和指标设置
        checkQuotaSettingParam(appraisalPlanBO.getAppraisalPlanQuotaSettingBO(), settingType);
        //校验结果设置
        checkResultTemplateParam(appraisalPlanBO.getAppraisalEmployeeResultSettingBO().getAppraisalPlanResultSettingLevelList());
    }


    private void checkProcessSaveParam(AppraisalPlanProcessSettingBO appraisalPlanProcessSettingBO) {
        if ((ObjectUtil.isNull(appraisalPlanProcessSettingBO.getResultAudit())) || (!appraisalPlanProcessSettingBO.getResultAudit())) {
            appraisalPlanProcessSettingBO.setProcessSettingResultAuditBOList(null);
        }
        if ((ObjectUtil.isNull(appraisalPlanProcessSettingBO.getResultConfirmation())) || (!appraisalPlanProcessSettingBO.getResultConfirmation())) {
            appraisalPlanProcessSettingBO.setProcessSettingResultConfirmationBOList(null);
        }
        List<ProcessSettingScoringBO> processSettingScoringBOList = appraisalPlanProcessSettingBO.getProcessSettingScoringBOList();
        if (CollectionUtil.isEmpty(processSettingScoringBOList)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分人列表不能为空");
        }
        processSettingScoringBOList.forEach(
                processSettingScoring -> {
                    if (ObjectUtil.isNull(processSettingScoring.getRaterType())) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分人类型不能为空");
                    }
                    if (processSettingScoring.getRaterType().equals(RaterTypeEnum.SELF)) {
                        if (ObjectUtil.isNotNull(processSettingScoring.getRejectAuthority())) {
                            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "被考核人不能设置驳回权限");
                        }
                    } else {
                        if (ObjectUtil.isNull(processSettingScoring.getRejectAuthority())) {
                            processSettingScoring.setRejectAuthority(false);
                        }
                    }
                }
        );
    }

    /**
     * 结果模板保存参数规则校验
     *
     * @param appraisalPlanResultSettingBOList
     */
    public void checkResultTemplateParam(List<AppraisalPlanResultSettingBO> appraisalPlanResultSettingBOList) {
        if (CollectionUtil.isEmpty(appraisalPlanResultSettingBOList)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "结果等级列表不能为空");
        }
        List<String> names = new ArrayList<>();
        appraisalPlanResultSettingBOList.stream().forEach(
                resultTemplateLevelBO -> {
                    if (!names.contains(resultTemplateLevelBO.getLevelName())) {
                        names.add(resultTemplateLevelBO.getLevelName());
                    } else {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "等级名称(" + resultTemplateLevelBO.getLevelName() + ")重复");
                    }

                    Double scoreLowerLimit = resultTemplateLevelBO.getScoreLowerLimit();//分数下限
                    Double scoreUpperLimit = resultTemplateLevelBO.getScoreLowerLimit();//分数上限

                    if (scoreLowerLimit < 0) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分下限必须大于0");
                    }
                    if (scoreUpperLimit > 100) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评分上限必须小于100");
                    }
                    if (scoreLowerLimit > scoreUpperLimit) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "分数下限不能大于分数上限");
                    }
                }
        );

    }

    public void sendMessage(Long userId, Long employeeId, Long typeId, Integer type, String title) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateUser(userId);
        adminMessage.setCreateTime(LocalDateTime.now());
        adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employeeId));
        adminMessage.setTypeId(typeId);
        adminMessage.setLabel(8);//8标示人资模块消息
        adminMessage.setType(type);
        adminMessage.setTitle(title);
        adminMessageService.save(adminMessage);
    }

}
