package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.constant.HrmEnum;
import com.kakarote.hrm.constant.appraisal.*;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmAppraisalEmployeeMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.AppraisalActionRecordServiceImpl;
import com.kakarote.hrm.utils.AchievementsUtil;
import com.kakarote.hrm.utils.EmployeeCacheUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工绩效考核-考核绩效基本信息 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-05-26
 */
@Service
public class HrmAppraisalEmployeeServiceImpl extends BaseServiceImpl<HrmAppraisalEmployeeMapper, HrmAppraisalEmployee> implements IHrmAppraisalEmployeeService, HrmPageService {

    @Autowired
    IHrmAppraisalEmployeeDimensionService appraisalEmployeeDimensionService;

    @Autowired
    IHrmAppraisalPlanResultSettingService resultSettingService;

    @Autowired
    IHrmAppraisalEmployeeQuotaService appraisalEmployeeQuotaService;

    @Autowired
    IHrmAppraisalEmployeeScoreService appraisalEmployeeScoreService;

    @Autowired
    IHrmAppraisalEmployeeQuotaScoreService appraisalEmployeeQuotaScoreService;

    @Autowired
    IHrmProcessSettingTargetConfirmationService processSettingTargetConfirmationService;

    @Autowired
    IHrmAppraisalPlanProcessSettingService processSettingService;

    @Autowired
    IHrmAppraisalPlanQuotaSettingService quotaSettingService;

    @Autowired
    IHrmProcessSettingScoringService processSettingScoringService;

    @Autowired
    IHrmProcessSettingResultAuditService processSettingResultAuditService;

    @Autowired
    IHrmAppraisalPlanService appraisalPlanService;

    @Autowired
    IHrmAppraisalEmployeeStageService appraisalEmployeeStageService;

    @Autowired
    IHrmAppraisalEmployeeAppealRecordService appraisalEmployeeAppealRecordService;

    @Autowired
    private IAdminMessageService adminMessageService;

    @Resource
    private AppraisalActionRecordServiceImpl actionRecordService;

    @Autowired
    private IHrmAppraisalAppealPendingRecordService appraisalAppealPendingRecordService;

    @Autowired
    private IHrmAppraisalEmployeeRealScoreUserService realScoreUserService;

    @Autowired
    private IHrmAppraisalActionRecordService appraisalActionRecordService;

    @Autowired
    private IHrmDeptService hrmDeptService;

    @Autowired
    private IHrmEmployeeService hrmEmployeeService;

    @Autowired
    private IHrmEmployeeFieldService hrmEmployeeFieldService;

    @Autowired
    private AchievementsUtil achievementsUtil;

    @Transactional
    @Override
    public void deleteAppraisalEmployeeByEmployeeId(List<Long> employeeIdList) {

        for (Long employeeId : employeeIdList) {
            Integer count = lambdaQuery().select(HrmAppraisalEmployee::getAppraisalEmployeeId).eq(HrmAppraisalEmployee::getEmployeeId, employeeId).eq(HrmAppraisalEmployee::getAppraisalStatus, AppraisalStatusEnum.RUNNING.getValue()).count().intValue();
            if (count >= 1) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "有考核中绩效成绩，删除失败");
            }
        }
        List<HrmAppraisalEmployee> appraisalEmployeeList = lambdaQuery().select(HrmAppraisalEmployee::getAppraisalEmployeeId).in(HrmAppraisalEmployee::getEmployeeId, employeeIdList).eq(HrmAppraisalEmployee::getAppraisalStatus, AppraisalStatusEnum.PLACE_ON_FILE.getValue()).list();
        if (CollectionUtil.isEmpty(appraisalEmployeeList)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        List<Long> appraisalEmployeeId = appraisalEmployeeList.stream().map(HrmAppraisalEmployee::getAppraisalEmployeeId).collect(Collectors.toList());
        deleteAppraisalEmployeeById(appraisalEmployeeId);
    }

    @Override
    public BasePage<EmployeeAppraisalPlanVO> queryAppraisalList(QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        return baseMapper.queryAppraisalList(queryEmployeeQuotaBO.parse(), queryEmployeeQuotaBO);
    }

    @Override
    public BasePage<EmployeeAppraisalPlanVO> queryMyAppraisalFileList(QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        queryEmployeeQuotaBO.setEmployeeId(EmployeeHolder.getEmployeeId());
        return baseMapper.queryAppraisalList(queryEmployeeQuotaBO.parse(), queryEmployeeQuotaBO);
    }

    @Transactional
    @Override
    public void delEmployeeAppraisalData(Long appraisalPlanId) {
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalPlanId);
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        List<HrmAppraisalEmployee> appraisalEmployeeList = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).list();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeList)) {
            List<Long> employeeAppraisalIdList = new ArrayList<>();
            appraisalEmployeeList.forEach(
                    appraisalEmployee -> {
                        employeeAppraisalIdList.add(appraisalEmployee.getAppraisalEmployeeId());
                        appraisalAppealPendingRecordService.lambdaUpdate().eq(HrmAppraisalAppealPendingRecord::getAppraisalPlanId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        appraisalEmployeeDimensionService.lambdaUpdate().eq(HrmAppraisalEmployeeDimension::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        appraisalEmployeeQuotaService.lambdaUpdate().eq(HrmAppraisalEmployeeQuota::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        appraisalEmployeeQuotaScoreService.lambdaUpdate().eq(HrmAppraisalEmployeeQuotaScore::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        appraisalEmployeeScoreService.lambdaUpdate().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        appraisalEmployeeAppealRecordService.lambdaUpdate().eq(HrmAppraisalEmployeeAppealRecord::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                        lambdaUpdate().eq(HrmAppraisalEmployee::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();
                    }
            );
            deletePage(employeeAppraisalIdList);
        }

    }

    @Transactional
    @Override
    public Integer deleteAppraisalEmployeeById(List<Long> appraisalEmployeeIdList) {
        Integer delCount = 0;
        List<Long> delAppraisalEmployeeIdList = new ArrayList<>();
        for (Long appraisalEmployeeId : appraisalEmployeeIdList) {
            Optional<HrmAppraisalEmployee> appraisalEmployee = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalEmployeeId, appraisalEmployeeId).oneOpt();
            if (appraisalEmployee.isPresent()) {
                if (!appraisalEmployee.get().getAppraisalStatus().equals(AppraisalStatusEnum.PLACE_ON_FILE.getValue())) {
                    throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "有考核中绩效成绩，删除失败");
                }
                Boolean result = lambdaUpdate().eq(HrmAppraisalEmployee::getAppraisalEmployeeId, appraisalEmployeeId).eq(HrmAppraisalEmployee::getAppraisalStatus, AppraisalStatusEnum.PLACE_ON_FILE.getValue()).remove();
                if (result) {
                    delAppraisalEmployeeIdList.add(appraisalEmployeeId);
                    delCount++;
                }
            }
        }
        deletePage(delAppraisalEmployeeIdList);
        return delCount;
    }

    @Override
    public BasePage<Map<String, Object>> queryEmployeeAppraisalPageList(HrmSearchBO search) {
        BasePage<Map<String, Object>> basePage = queryList(search, false);
        return basePage;
    }

    @Override
    public EmployeeAppraisalPlanVO queryEmployeeAppraisalBaseInfo(CommonQueryBO commonQueryBO) {
        EmployeeAppraisalPlanVO appraisalEmployee = baseMapper.queryBaseInfo(commonQueryBO.getId());//判断当前用户是否有目标驳回权限,用于前端按钮控制使用
        boolean exists = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, commonQueryBO.getId())
                .eq(HrmAppraisalEmployeeStage::getStageUserId, EmployeeHolder.getEmployeeId())
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue()).exists();

        if (exists) {
            appraisalEmployee.setCanRejectTarget(true);
        } else {
            appraisalEmployee.setCanRejectTarget(false);
        }
        return appraisalEmployee;
    }

    @Override
    public BasePage<EmployeeAppraisalPlanVO> queryStageAppraisalList(QueryEmployeePendingAppraisalBO queryEmployeePendingAppraisalBO) {
        queryEmployeePendingAppraisalBO.setEmployeeId(EmployeeHolder.getEmployeeId());
        List<Integer> finalStage = new ArrayList<>();
        finalStage.add(queryEmployeePendingAppraisalBO.getStage());
        if (ObjectUtil.isNull(queryEmployeePendingAppraisalBO.getStage())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "阶段类型不能为空");
        }
        if (queryEmployeePendingAppraisalBO.getStage().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue())) {
            finalStage.add(AppraisalStageStatusEnum.SELF_SCORE.getValue());
        }
        queryEmployeePendingAppraisalBO.setFinalStage(finalStage);
        BasePage<EmployeeAppraisalPlanVO> result = baseMapper.queryStageAppraisalList(queryEmployeePendingAppraisalBO.parse(), queryEmployeePendingAppraisalBO);
        return result;
    }

    @Override
    public JSONObject queryStageEmployeeNum(CommonQueryBO commonQueryBO) {
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, commonQueryBO.getId()).one();
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        Integer TOTAL = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count().intValue();
        Map<String, Object> stageNum = baseMapper.queryStageEmployeeNum(commonQueryBO.getId());
        //阶段状态：0：未开始 1：员工填写  2:目标确认 3：自评分 4：他人评分5：结果审核6：结果确认 7：归档
//        Integer NO_START = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.NO_START.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer FILL = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.FILL.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer EXECUTING = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.EXECUTING.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer SELF_SCORE = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.SELF_SCORE.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer OTHER_SCORE = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.OTHER_SCORE.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer TARGET_CONFIRMATION = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer RESULT_AUDIT = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.RESULT_AUDIT.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer RESULT_CONFIRMATION = lambdaQuery().in(HrmAppraisalEmployee::getStageStatus, Arrays.asList(AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue(),AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue())).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer PLACE_ON_FILE = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.PLACE_ON_FILE.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
//        Integer END = lambdaQuery().eq(HrmAppraisalEmployee::getStageStatus, AppraisalStageStatusEnum.END.getValue()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count();
        JSONObject num = new JSONObject();
        num.put("NO_START", stageNum.get("noStart"));
        num.put("FILL", stageNum.get("fill"));
        num.put("EXECUTING", stageNum.get("executing"));
        num.put("SELF_SCORE", stageNum.get("selfScore"));
        num.put("OTHER_SCORE", stageNum.get("otherScore"));
        num.put("TARGET_CONFIRMATION", stageNum.get("targetConfirmation"));
        num.put("RESULT_AUDIT", stageNum.get("resultAudit"));
        num.put("RESULT_CONFIRMATION", stageNum.get("resultConfirmation"));
        num.put("PLACE_ON_FILE", stageNum.get("placeOnFile"));
        num.put("END", stageNum.get("endNum"));
        num.put("TOTAL", TOTAL);
        return num;
    }

    @Override
    public JSONObject queryPendingStageNum() {
//        Integer FILL = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.FILL.getValue()),EmployeeHolder.getEmployeeId());
//        Integer TARGET_CONFIRMATION = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue()),EmployeeHolder.getEmployeeId());
//        Integer SCORE = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.SELF_SCORE.getValue(),AppraisalStageStatusEnum.OTHER_SCORE.getValue()),EmployeeHolder.getEmployeeId());
//        Integer RESULT_AUDIT = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.RESULT_AUDIT.getValue()),EmployeeHolder.getEmployeeId());
//        Integer RESULT_CONFIRMATION = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue()),EmployeeHolder.getEmployeeId());
//        Integer APPEAL_CONFIRMATION = appraisalEmployeeStageService.queryEmployeePendingStageNum(Arrays.asList(AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue()),EmployeeHolder.getEmployeeId());
        Map<String, Integer> countMap = appraisalEmployeeStageService.queryEmployeePendingStageNumOfAll(EmployeeHolder.getEmployeeId());
        if (CollUtil.isEmpty(countMap)) {
            countMap = new HashMap<>();
        }
        JSONObject num = new JSONObject();
        num.put("FILL", countMap.get("fillNum"));
        num.put("TARGET_CONFIRMATION", countMap.get("targetConfirmationNum"));
        num.put("SCORE", countMap.get("scoreNum"));
        num.put("RESULT_AUDIT", countMap.get("resultAuditNum"));
        num.put("RESULT_CONFIRMATION", countMap.get("resultConfirmationNum"));
        num.put("APPEAL_CONFIRMATION", countMap.get("appealConfirmationNum"));
        return num;
    }

    @Override
    public JSONObject queryScoreEmployeeNum(CommonQueryBO commonQueryBO) {
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, commonQueryBO.getId()).one();
        if (ObjectUtil.isNull(appraisalPlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        List<HrmAppraisalPlanResultSetting> resultSetting = resultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).list();
        JSONObject num = new JSONObject();

        resultSetting.forEach(level -> {
                    Integer SCORE_COUNT = lambdaQuery().eq(HrmAppraisalEmployee::getLevel, level.getLevelName()).eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count().intValue();
                    num.put(level.getLevelName(), SCORE_COUNT);
                }
        );
        Integer TOTAL = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).count().intValue();
        num.put("TOTAL", TOTAL);
        Integer UNCLASSIFIED = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, commonQueryBO.getId()).isNull(HrmAppraisalEmployee::getLevel).count().intValue();
        num.put("UNCLASSIFIED", UNCLASSIFIED);
        return num;
    }

    @Transactional
    @Override
    public void fillInQuota(FillInQuotaBO fillInQuotaBO) {

        HrmAppraisalEmployee appraisalEmployeePlan = getById(fillInQuotaBO.getAppraisalEmployeeId());

        //如果当前阶段的处理人不是当前操作人则拦截掉
        if (!appraisalEmployeePlan.getEmployeeId().equals(EmployeeHolder.getEmployeeId())) {//如果当前操作人不是被考核人则无权限操作
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "无权操作！");
        }

        if (ObjectUtil.isNull(appraisalEmployeePlan)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        Long appraisalPlanId = appraisalEmployeePlan.getAppraisalPlanId();
        HrmAppraisalPlanProcessSetting processSetting = processSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlanId).one();


        if (processSetting.getQuotaSettingType().equals(QuotaTypeEnum.SYSTEM_FORMULATE.getValue())) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "当前考核不允许员工填写！");
        }

        fillInQuotaBO.getAppraisalEmployeeDimensionBOList().stream().forEach(
                dimension -> {
                    HrmAppraisalEmployeeDimension employeeDimension = appraisalEmployeeDimensionService.lambdaQuery().select(HrmAppraisalEmployeeDimension::getDimensionName, HrmAppraisalEmployeeDimension::getIsAllowEdit).eq(HrmAppraisalEmployeeDimension::getDimensionId, dimension.getDimensionId()).one();
                    if (ObjectUtil.isNull(employeeDimension.getIsAllowEdit()) || !employeeDimension.getIsAllowEdit()) {
                        throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "当前维度(" + employeeDimension.getDimensionName() + ")不允许员工填写！");
                    }
                }
        );

        //由于维度不允许员工新增且维度信息是从考核计划配置中获取，所以只需要删除指标（非系统预设）信息
        appraisalEmployeeQuotaService.lambdaUpdate().eq(HrmAppraisalEmployeeQuota::getAppraisalEmployeeId, fillInQuotaBO.getAppraisalEmployeeId()).ne(HrmAppraisalEmployeeQuota::getPreset, true).remove();
        HrmAppraisalEmployeeQuota appraisalEmployeeQuotaSystemPre = appraisalEmployeeQuotaService.lambdaQuery().select(HrmAppraisalEmployeeQuota::getSort).eq(HrmAppraisalEmployeeQuota::getAppraisalEmployeeId, fillInQuotaBO.getAppraisalEmployeeId()).orderByDesc(HrmAppraisalEmployeeQuota::getSort).last("limit 1").one();

        AtomicReference<Integer> quotaSort = new AtomicReference<>(appraisalEmployeeQuotaSystemPre.getSort() + 1);
        List<HrmAppraisalEmployeeQuota> quotaList = new ArrayList<>();
        fillInQuotaBO.getAppraisalEmployeeDimensionBOList().stream().forEach(
                appraisalEmployeeDimensionBO -> {
                    AtomicReference<Double> total = new AtomicReference<>(0d);
                    List<HrmAppraisalEmployeeQuota> systemQuotaList = appraisalEmployeeQuotaService.lambdaQuery().eq(HrmAppraisalEmployeeQuota::getDimensionId, appraisalEmployeeDimensionBO.getDimensionId()).list();
                    systemQuotaList.forEach(
                            curQuota -> {
                                total.updateAndGet(v -> v + curQuota.getQuotaWeight());
                            }
                    );
                    List<AppraisalEmployeeQuotaBO> appraisalEmployeeQuotaBOList = appraisalEmployeeDimensionBO.getAppraisalEmployeeQuotaBOList();
                    appraisalEmployeeQuotaBOList.stream().forEach(
                            appraisalEmployeeQuotaBO -> {
                                HrmAppraisalEmployeeQuota appraisalEmployeeQuota = new HrmAppraisalEmployeeQuota();
                                BeanUtil.copyProperties(appraisalEmployeeQuotaBO, appraisalEmployeeQuota);
                                appraisalEmployeeQuota.setDimensionId(appraisalEmployeeDimensionBO.getDimensionId());
                                total.updateAndGet(v -> v + appraisalEmployeeQuota.getQuotaWeight());
                                appraisalEmployeeQuota.setSort(quotaSort.get());
                                appraisalEmployeeQuota.setAppraisalEmployeeId(fillInQuotaBO.getAppraisalEmployeeId());
                                quotaList.add(appraisalEmployeeQuota);
                                quotaSort.getAndSet(quotaSort.get() + 1);
                            }
                    );
                    if (total.get() != 100) {
                        throw new CrmException(HrmCodeEnum.EMPLOYEE_APPEAL_ERROR, "维度指标总和必须等于%100");
                    }
                }
        );
        appraisalEmployeeQuotaService.saveBatch(quotaList);
        appraisalEmployeeStageService.updateStageStatusPass(appraisalPlanId, appraisalEmployeePlan.getEmployeeId(), AppraisalStageStatusEnum.FILL);
        actionRecordService.submitAppraisalRecord(appraisalEmployeePlan.getAppraisalEmployeeId());

    }

    @Override
    public AppraisalEmployeeInfoVO quotaInformation(QuotaInfoQueryBO quotaInfoQueryBO) {
        Long appraisalEmployeeId = quotaInfoQueryBO.getAppraisalEmployeeId();
        //获取考核计划配置
        HrmAppraisalEmployee appraisalEmployee = getById(appraisalEmployeeId);
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        AppraisalEmployeeInfoVO appraisalEmployeeInfoVO = new AppraisalEmployeeInfoVO();
        List<HrmAppraisalEmployeeDimension> appraisalEmployeeDimensionList = appraisalEmployeeDimensionService
                .lambdaQuery().eq(HrmAppraisalEmployeeDimension::getAppraisalEmployeeId, appraisalEmployeeId).list();
        List<Long> dimensionIdList = appraisalEmployeeDimensionList.stream().map(HrmAppraisalEmployeeDimension::getDimensionId).collect(Collectors.toList());
        List<HrmAppraisalEmployeeQuota> appraisalEmployeeQuotaList = appraisalEmployeeQuotaService.lambdaQuery()
                .in(HrmAppraisalEmployeeQuota::getDimensionId, dimensionIdList).list();

        //获取指标评分信息
        Map<Long, List<QuotaEmployeeScoreVO>> quotaScoreData = new HashMap<>();

        //员工评分详情
        List<HrmAppraisalEmployeeQuotaScore> appraisalEmployeeQuotaScoreList = appraisalEmployeeQuotaScoreService.queryQuotaScoreList(appraisalEmployeeId);

        //获取排序后的真实评分人列表
        List<HrmAppraisalEmployeeRealScoreUser> realScoreUserList = realScoreUserService.lambdaQuery().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId())
                .eq(HrmAppraisalEmployeeRealScoreUser::getEmployeeId, appraisalEmployee.getEmployeeId()).orderByAsc(HrmAppraisalEmployeeRealScoreUser::getStageSort).list();

        //查询当前评分人设置项-用于设置评分驳回权限供前端使用
        HrmAppraisalEmployeeRealScoreUser curRealScoreUser = realScoreUserService.lambdaQuery().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId())
                .eq(HrmAppraisalEmployeeRealScoreUser::getEmployeeId, appraisalEmployee.getEmployeeId()).eq(HrmAppraisalEmployeeRealScoreUser::getScoreUserId, EmployeeHolder.getEmployeeInfo().getEmployeeId()).one();
        if (ObjectUtil.isNotNull(curRealScoreUser)) {
            appraisalEmployeeInfoVO.setRejectAuthority(curRealScoreUser.getRejectAuthority());
            appraisalEmployeeInfoVO.setRequiredSetting(curRealScoreUser.getRequiredSetting());
        } else {
            appraisalEmployeeInfoVO.setRejectAuthority(false);
            appraisalEmployeeInfoVO.setRequiredSetting(false);
        }

        Boolean isHasCurRater = false;//判断已评分列表是否含有当前评分人-这个也是判断当前评分界面是否是重新评分
        Boolean onlySelf = false;
        List<HrmAppraisalEmployeeQuotaScore> scoreListOfSort = new ArrayList<>();//对已进行评分的数据进行排序
        if (CollectionUtil.isNotEmpty(realScoreUserList) && CollectionUtil.isNotEmpty(appraisalEmployeeQuotaScoreList)) {
            Map<Long, List<HrmAppraisalEmployeeQuotaScore>> scoreListMap = appraisalEmployeeQuotaScoreList.stream().collect(Collectors.groupingBy(HrmAppraisalEmployeeQuotaScore::getRater));
            Map<Long, List<HrmAppraisalEmployeeQuotaScore>> filterListMap = new HashMap<>();
            if (ObjectUtil.isNotNull(curRealScoreUser) && curRealScoreUser.getVisibleContent().equals(VisibleContentEnum.SELF.getValue())) {//如果当前是评分页面且当前评分用户的评分查看权限是仅自己则过滤掉其他人的评分情况
                filterListMap.put(EmployeeHolder.getEmployeeId(), scoreListMap.get(EmployeeHolder.getEmployeeId()));
                onlySelf = true;
            } else {
                filterListMap = scoreListMap;
            }
            if (ObjectUtil.isNotNull(filterListMap.get(EmployeeHolder.getEmployeeId()))) {
                isHasCurRater = true;
            }
            Map<Long, List<HrmAppraisalEmployeeQuotaScore>> finalFilterListMap = filterListMap;
            realScoreUserList.forEach(
                    realScoreUserInfo -> {
                        //按照排序后的评分人列表进行顺序添加
                        if (CollectionUtil.isNotEmpty(finalFilterListMap.get(realScoreUserInfo.getScoreUserId()))) {//实际评分人是流程中全部评分人,但是评分记录中可能还有未进行评分的所以找不到记录
                            scoreListOfSort.addAll(finalFilterListMap.get(realScoreUserInfo.getScoreUserId()));
                        }
                    }
            );
        }

        //对排序后数据再根据指标id进行分组
        if (CollectionUtil.isNotEmpty(scoreListOfSort)) {
            quotaScoreData = scoreListOfSort.stream().map(
                    scoreInfo -> {
                        QuotaEmployeeScoreVO quotaEmployeeScoreVO = new QuotaEmployeeScoreVO();
                        BeanUtil.copyProperties(scoreInfo, quotaEmployeeScoreVO);
                        if (quotaInfoQueryBO.getIsScoring()) {
                            if (scoreInfo.getRater().equals(EmployeeHolder.getEmployeeId())) {
                                //如果评分列表中包含当前评分人-且当前页面是评分页面则进行一些额外设置-重新评分情况下会有这种情况发生
                                quotaEmployeeScoreVO.setCanFill(true);
                            }
                        }
                        quotaEmployeeScoreVO.setEmployeeName(scoreInfo.getRaterName());
                        return quotaEmployeeScoreVO;
                    }
            ).collect(Collectors.groupingBy(QuotaEmployeeScoreVO::getQuotaId));
        }

        Map<Long, List<QuotaEmployeeScoreVO>> finalQuotaScoreData = quotaScoreData;//已进行评分的员工数据进行分组
        //遍历员工实际考核指标列表并设置评分人
        Boolean finalIsHasCurRater = isHasCurRater;
        Map<Long, List<AchievementsQuotaVO>> achievementsQuotaVOList = appraisalEmployeeQuotaList.stream().map(
                appraisalEmployeeQuota -> {
                    AchievementsQuotaVO achievementsQuotaVO = new AchievementsQuotaVO();
                    BeanUtil.copyProperties(appraisalEmployeeQuota, achievementsQuotaVO);
                    List<QuotaEmployeeScoreVO> quotaEmployeeScoreVOList = finalQuotaScoreData.get(appraisalEmployeeQuota.getQuotaId());
                    if (CollectionUtil.isEmpty(quotaEmployeeScoreVOList)) {
                        quotaEmployeeScoreVOList = new ArrayList<>();
                    }
                    if (quotaInfoQueryBO.getIsScoring() && !finalIsHasCurRater) {//如果是评分界面且历史评分列表不包含当前评分人的情况再进行添加,避免重新评分情况下会产生数据重复
                        //如果是填写页面，则去查当前这个人是否是待填写状态如果是则设置标志位
                        //由于回填页面数据的时候有可能当前评分人是驳回后重新评分因此要展示评分数据,而且如果是驳回阶段,应该展示的指标评分列表应该只展示驳回节点的数据
                        QuotaEmployeeScoreVO quotaEmployeeScoreVO = new QuotaEmployeeScoreVO();
                        quotaEmployeeScoreVO.setQuotaId(appraisalEmployeeQuota.getQuotaId());
                        quotaEmployeeScoreVO.setEmployeeName(EmployeeHolder.getEmployeeInfo().getEmployeeName());
                        quotaEmployeeScoreVO.setCanFill(true);
                        quotaEmployeeScoreVOList.add(quotaEmployeeScoreVO);
                    }
                    //保存员工指标评分列表
                    achievementsQuotaVO.setQuotaEmployeeScoreVOList(quotaEmployeeScoreVOList);
                    return achievementsQuotaVO;
                }
        ).collect(Collectors.groupingBy(AchievementsQuotaVO::getDimensionId));

        List<AchievementsDimensionVO> dimensionVOList = appraisalEmployeeDimensionList.stream().map(
                appraisalEmployeeDimension -> {
                    AchievementsDimensionVO achievementsDimensionVO = new AchievementsDimensionVO();
                    BeanUtil.copyProperties(appraisalEmployeeDimension, achievementsDimensionVO);
                    achievementsDimensionVO.setAchievementsQuotaVOList(achievementsQuotaVOList.get(appraisalEmployeeDimension.getDimensionId()));
                    return achievementsDimensionVO;
                }
        ).collect(Collectors.toList());
        appraisalEmployeeInfoVO.setDimensionVOList(dimensionVOList);

        //获取员工评分记录
        List<ScoreRecordVO> scoreRecordVOList = new ArrayList<>();
        List<HrmAppraisalEmployeeScore> appraisalEmployeeScoreList = appraisalEmployeeScoreService.queryEmployeeScoreList(appraisalEmployee.getAppraisalEmployeeId());//员工评分记录
        if (CollectionUtil.isNotEmpty(appraisalEmployeeScoreList)) {
            Boolean finalOnlySelf = onlySelf;
            appraisalEmployeeScoreList.forEach(
                    appraisalEmployeeScore -> {
                        ScoreRecordVO scoreRecordVO = new ScoreRecordVO();
                        if (quotaInfoQueryBO.getIsScoring() && appraisalEmployeeScore.getRater().equals(EmployeeHolder.getEmployeeId())) {
                            scoreRecordVO.setCanFill(true);
                        }
                        scoreRecordVO.setComments(appraisalEmployeeScore.getComments());
                        scoreRecordVO.setEmployeeName(appraisalEmployeeScore.getRaterName());

                        if (ObjectUtil.isEmpty(appraisalEmployeeScore.getScore())) {
                            scoreRecordVO.setResult("");
                            scoreRecordVO.setWeight(appraisalEmployeeScore.getWeight());
                        } else {
                            double employeeSourceScore = appraisalEmployeeScore.getScore();
                            BigDecimal score = new BigDecimal(employeeSourceScore);
                            double df = score.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                            scoreRecordVO.setResult(df + "分" + "(" + appraisalEmployeeScore.getLevel() + ")");
                            scoreRecordVO.setWeight(appraisalEmployeeScore.getWeight());
                        }
                        if (!finalOnlySelf) {//如果可以查看所有人的评分和评语则直接添加
                            scoreRecordVOList.add(scoreRecordVO);
                        } else if (appraisalEmployeeScore.getRater().equals(EmployeeHolder.getEmployeeId())) {
                            //如果只允许查看自己的评分和评语则判断当前评分列表评分人员是否是自己如果不是则过滤掉
                            scoreRecordVOList.add(scoreRecordVO);
                        }
                    }
            );
        }

        appraisalEmployeeInfoVO.setScoreRecordVOList(scoreRecordVOList);
        HrmAppraisalPlanQuotaSetting quotaSetting = quotaSettingService.lambdaQuery().select(HrmAppraisalPlanQuotaSetting::getUpperLimitScore).eq(HrmAppraisalPlanQuotaSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
        appraisalEmployeeInfoVO.setUpperLimitScore(quotaSetting.getUpperLimitScore());//返回评分上限
        Boolean isEnd = appraisalEmployeeStageService.isEndScore(appraisalEmployee.getAppraisalEmployeeId(), EmployeeHolder.getEmployeeId());//查询当前员工是否是最后评分
        appraisalEmployeeInfoVO.setIsEnd(isEnd);
        return appraisalEmployeeInfoVO;
    }

    @Transactional
    @Override
    public void targetConfirmationPass(Long appraisalEmployeeId) {
        HrmAppraisalEmployee appraisalEmployee = getById(appraisalEmployeeId);
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        appraisalEmployeeStageService.updateStageStatusPass(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), AppraisalStageStatusEnum.TARGET_CONFIRMATION);
        actionRecordService.confirmAppraisalRecord(appraisalEmployeeId);
    }

    @Transactional
    @Override
    public void targetConfirmationReject(RejectScoreBO rejectScoreBO) {
        HrmAppraisalEmployee appraisalEmployee = getById(rejectScoreBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        HrmAppraisalEmployeeStage curPendingStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue())
                .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.TARGET_CONFIRMATION.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (ObjectUtil.isNull(curPendingStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STAGE_NULL_ERROR);
        }
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.NOT_PROCESSED.getValue())
                .eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).le(HrmAppraisalEmployeeStage::getSort, curPendingStage.getSort()).update();
        HrmAppraisalEmployeeStage fillStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.FILL.getValue()).one();
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, fillStage.getAppraisalStageId())
                .eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).update();
        updateEmployeeStageStatus(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), fillStage.getSort());

        HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalEmployee.getAppraisalPlanId());
        //todo 添加操作记录

        sendMessage(UserUtil.getUserId(), curPendingStage.getEmployeeId(), appraisalEmployee.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_WRITE_REJECT.getType(), appraisalPlan.getAppraisalPlanName());

        actionRecordService.rejectConfirmAppraisalRecord(rejectScoreBO.getAppraisalEmployeeId(), rejectScoreBO.getRejectReason());
    }

    @Transactional
    @Override
    public void quotaScore(QuotaScoreBO quotaScoreBO) {

        HrmAppraisalEmployee appraisalEmployee = getById(quotaScoreBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        Boolean isSelfScoring = false;
        if (EmployeeHolder.getEmployeeId().equals(appraisalEmployee.getEmployeeId())) {//如果评分和和被考核员工是同一个人则为自评
            isSelfScoring = true;
        }
        LambdaQueryChainWrapper<HrmAppraisalEmployeeStage> baseQuery = appraisalEmployeeStageService.lambdaQuery()
                .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId());
        if (isSelfScoring) {
            baseQuery.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue());
        } else {
            baseQuery.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue());
        }
        HrmAppraisalEmployeeStage appraisalEmployeeStage = baseQuery.eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (ObjectUtil.isNull(appraisalEmployeeStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_CONFIRMATION_ERROR, "无权操作！");
        }

        List<QuotaScoreInfoBO> quotaScoreInfoBOList = quotaScoreBO.getQuotaScoreInfoBOList();//用户指标 评分列表
        Map<Long, QuotaScoreInfoBO> quotaScoreInfoBOMap = new HashMap<>();//将指标id和评分信息分开保存方便取用
        quotaScoreInfoBOList.forEach(
                quotaScoreInfo -> {
                    quotaScoreInfoBOMap.put(quotaScoreInfo.getQuotaId(), quotaScoreInfo);
                }
        );

        List<HrmAppraisalEmployeeDimension> appraisalEmployeeDimensionList = appraisalEmployeeDimensionService.lambdaQuery()
                .eq(HrmAppraisalEmployeeDimension::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId()).list();//获取员工考核计划维度

        Map<Long, Double> dimensionWeightMap = new HashMap<>();
        appraisalEmployeeDimensionList.forEach(
                appraisalEmployeeDimension -> {
                    dimensionWeightMap.put(appraisalEmployeeDimension.getDimensionId(), appraisalEmployeeDimension.getDimensionWeight());
                }
        );

        HrmAppraisalPlanQuotaSetting quotaSetting = quotaSettingService.lambdaQuery().eq(HrmAppraisalPlanQuotaSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
        Integer scoreCalculation = quotaSetting.getScoreCalculation();//预留类型用于区分总分计算方式
        //查询员工实际填写的指标设置
        List<HrmAppraisalEmployeeQuota> appraisalEmployeeQuotaList = appraisalEmployeeQuotaService.lambdaQuery().eq(HrmAppraisalEmployeeQuota::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId()).list();

        Double scoreResultD = null;
        BigDecimal scoreResult = new BigDecimal(0);
        for (HrmAppraisalEmployeeQuota appraisalEmployeeQuota : appraisalEmployeeQuotaList) {
            BigDecimal quotaWeight = new BigDecimal(appraisalEmployeeQuota.getQuotaWeight());
            BigDecimal dimensionWeight = new BigDecimal(dimensionWeightMap.get(appraisalEmployeeQuota.getDimensionId()));
            QuotaScoreInfoBO quotaScoreInfoBO = quotaScoreInfoBOMap.get(appraisalEmployeeQuota.getQuotaId());
            BigDecimal curScoreData = new BigDecimal(quotaScoreInfoBO.getScore());
            BigDecimal curScoreResult = curScoreData.multiply(quotaWeight).divide(new BigDecimal(100)).multiply(dimensionWeight).divide(new BigDecimal(100));
            scoreResult = scoreResult.add(curScoreResult);
        }
        scoreResultD = scoreResult.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        HrmAppraisalEmployeeRealScoreUser realScoreUser = realScoreUserService.lambdaQuery().eq(HrmAppraisalEmployeeRealScoreUser::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId())
                .eq(HrmAppraisalEmployeeRealScoreUser::getEmployeeId, appraisalEmployee.getEmployeeId()).eq(HrmAppraisalEmployeeRealScoreUser::getScoreUserId, EmployeeHolder.getEmployeeId()).one();
        if (ObjectUtil.isNotNull(realScoreUser.getRequiredSetting()) && realScoreUser.getRequiredSetting()) {//校验必填设置是否开启
            if (StrUtil.isEmpty(quotaScoreBO.getComments())) {
                throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_SAVE_PARAM_ERROR, "评语为必填字段！");
            }
        }

        //查询考核计划结果设置
        List<HrmAppraisalPlanResultSetting> resultSettingList = resultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).list();
        //查询考核计划维度和指标评分权重
        String levelName = queryResultLevel(scoreResultD, resultSettingList);
        if (StrUtil.isEmpty(levelName)) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPRAISAL_LEVEL_ERROR, "当前计算总分未包含在等级设置列表中");
        }

        HrmAppraisalEmployeeScore employeeScore = appraisalEmployeeScoreService.lambdaQuery().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId())
                .eq(HrmAppraisalEmployeeScore::getRater, EmployeeHolder.getEmployeeId()).one();//获取当前评分人指标评分详情
        if (ObjectUtil.isNotNull(employeeScore)) {
            employeeScore.setAppraisalEmployeeId(quotaScoreBO.getAppraisalEmployeeId());
            employeeScore.setComments(quotaScoreBO.getComments());
            employeeScore.setRater(EmployeeHolder.getEmployeeId());
            employeeScore.setScore(scoreResultD);
            employeeScore.setLevel(levelName);
            employeeScore.setWeight(appraisalEmployeeStage.getWeight());
            appraisalEmployeeScoreService.updateById(employeeScore);
            appraisalEmployeeQuotaScoreService.lambdaUpdate().eq(HrmAppraisalEmployeeQuotaScore::getScoreId, employeeScore.getScoreId()).remove();//删除评分进行重新评分
        } else {
            employeeScore = new HrmAppraisalEmployeeScore();
            employeeScore.setAppraisalEmployeeId(quotaScoreBO.getAppraisalEmployeeId());
            employeeScore.setComments(quotaScoreBO.getComments());
            employeeScore.setRater(EmployeeHolder.getEmployeeId());
            employeeScore.setScore(scoreResultD);
            employeeScore.setLevel(levelName);
            employeeScore.setWeight(appraisalEmployeeStage.getWeight());
            appraisalEmployeeScoreService.save(employeeScore);
        }
        Long scoreId = employeeScore.getScoreId();//评分id
        List<HrmAppraisalEmployeeQuotaScore> employeeQuotaScores = new ArrayList<>();

        //保存指标评分详情列表
        quotaScoreBO.getQuotaScoreInfoBOList().forEach(
                quotaScore -> {
                    HrmAppraisalEmployeeQuotaScore hrmAppraisalEmployeeQuotaScore = new HrmAppraisalEmployeeQuotaScore();
                    hrmAppraisalEmployeeQuotaScore.setQuotaId(quotaScore.getQuotaId());
                    hrmAppraisalEmployeeQuotaScore.setScoreId(scoreId);
                    hrmAppraisalEmployeeQuotaScore.setComments(quotaScore.getComments());
                    hrmAppraisalEmployeeQuotaScore.setAppraisalEmployeeId(quotaScoreBO.getAppraisalEmployeeId());
                    hrmAppraisalEmployeeQuotaScore.setScore(quotaScore.getScore());
                    hrmAppraisalEmployeeQuotaScore.setRater(EmployeeHolder.getEmployeeId());
                    employeeQuotaScores.add(hrmAppraisalEmployeeQuotaScore);
                }
        );
        appraisalEmployeeQuotaScoreService.saveBatch(employeeQuotaScores);

        AppraisalStageStatusEnum stageEnum = AppraisalStageStatusEnum.parse(appraisalEmployeeStage.getStageType());
        //更新流程状态和阶段状态
        Boolean isEnd = appraisalEmployeeStageService.updateStageStatusPass(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), stageEnum);
        if (isEnd) {
            //目前只有一种
            List<HrmAppraisalEmployeeScore> scoreList = appraisalEmployeeScoreService.lambdaQuery().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).list();
            BigDecimal totalScore = new BigDecimal(0);
            for (HrmAppraisalEmployeeScore score : scoreList) {
                BigDecimal scoreTmp = new BigDecimal(score.getScore());
                BigDecimal weight = new BigDecimal(score.getWeight());
                BigDecimal curScoreResult = scoreTmp.multiply(weight).divide(new BigDecimal(100));
                totalScore = totalScore.add(curScoreResult);
            }
            Double totalSoreD = totalScore.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            String totalScoreLevelName = queryResultLevel(totalSoreD, resultSettingList);
            lambdaUpdate().set(HrmAppraisalEmployee::getScore, totalSoreD).set(HrmAppraisalEmployee::getLevel, totalScoreLevelName).eq(HrmAppraisalEmployee::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).update();
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("score", totalSoreD);
            fieldMap.put("level", totalScoreLevelName);
            updateField(fieldMap, Arrays.asList(appraisalEmployee.getAppraisalEmployeeId()));
        }
        if (isSelfScoring) {
            actionRecordService.selfScoringRecord(appraisalEmployee.getAppraisalEmployeeId());
        } else {
            actionRecordService.otherScoringRecord(appraisalEmployee.getAppraisalEmployeeId());
        }
    }

    @Override
    public PreQuotaScoreVO preCalculationQuotaScore(QuotaScoreBO quotaScoreBO) {
        HrmAppraisalEmployee appraisalEmployee = getById(quotaScoreBO.getAppraisalEmployeeId());

        List<QuotaScoreInfoBO> quotaScoreInfoBOList = quotaScoreBO.getQuotaScoreInfoBOList();//用户指标 评分列表
        Map<Long, QuotaScoreInfoBO> quotaScoreInfoBOMap = new HashMap<>();//将指标id和评分信息分开保存方便取用
        quotaScoreInfoBOList.forEach(
                quotaScoreInfo -> {
                    quotaScoreInfoBOMap.put(quotaScoreInfo.getQuotaId(), quotaScoreInfo);
                }
        );

        List<HrmAppraisalEmployeeDimension> appraisalEmployeeDimensionList = appraisalEmployeeDimensionService.lambdaQuery()
                .eq(HrmAppraisalEmployeeDimension::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId()).list();//获取员工考核计划维度

        Map<Long, Double> dimensionWeightMap = new HashMap<>();
        appraisalEmployeeDimensionList.forEach(
                appraisalEmployeeDimension -> {
                    dimensionWeightMap.put(appraisalEmployeeDimension.getDimensionId(), appraisalEmployeeDimension.getDimensionWeight());
                }
        );

        Double scoreResultD = 0d;
        BigDecimal scoreResult = new BigDecimal(0);
        //查询员工实际填写的指标设置
        List<HrmAppraisalEmployeeQuota> appraisalEmployeeQuotaList = appraisalEmployeeQuotaService.lambdaQuery().eq(HrmAppraisalEmployeeQuota::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId()).list();
        for (HrmAppraisalEmployeeQuota appraisalEmployeeQuota : appraisalEmployeeQuotaList) {
            BigDecimal quotaWeight = new BigDecimal(appraisalEmployeeQuota.getQuotaWeight());
            BigDecimal dimensionWeight = new BigDecimal(dimensionWeightMap.get(appraisalEmployeeQuota.getDimensionId()));
            QuotaScoreInfoBO quotaScoreInfoBO = quotaScoreInfoBOMap.get(appraisalEmployeeQuota.getQuotaId());
            BigDecimal curScore = new BigDecimal(quotaScoreInfoBO.getScore()).multiply(quotaWeight).divide(new BigDecimal(100)).multiply(dimensionWeight).divide(new BigDecimal(100));
            scoreResult = scoreResult.add(curScore);
        }
        scoreResultD = scoreResult.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        List<HrmAppraisalPlanResultSetting> resultSettingList = resultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).list();

        //查询考核计划维度和指标评分权重
        String levelName = queryResultLevel(scoreResultD, resultSettingList);
        if (StrUtil.isEmpty(levelName)) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPRAISAL_LEVEL_ERROR, "当前计算总分未包含在等级设置列表中");
        }

        Boolean isSelfScoring = false;
        if (EmployeeHolder.getEmployeeId().equals(appraisalEmployee.getEmployeeId())) {//如果评分和和被考核员工是同一个人则为自评
            isSelfScoring = true;
        }

        LambdaQueryChainWrapper<HrmAppraisalEmployeeStage> baseQuery = appraisalEmployeeStageService.lambdaQuery()
                .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId());
        if (isSelfScoring) {
            baseQuery.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue());
        } else {
            baseQuery.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue());
        }

        HrmAppraisalEmployeeStage appraisalEmployeeStage = baseQuery.eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (ObjectUtil.isNull(appraisalEmployeeStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_CONFIRMATION_ERROR, "无权操作！");
        }

        HrmAppraisalEmployeeScore employeeScore = appraisalEmployeeScoreService.lambdaQuery().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, quotaScoreBO.getAppraisalEmployeeId())
                .eq(HrmAppraisalEmployeeScore::getRater, EmployeeHolder.getEmployeeId()).one();//获取当前评分人指标评分详情

        if (ObjectUtil.isNotNull(employeeScore)) {
            employeeScore.setScore(scoreResultD);
        } else {
            employeeScore = new HrmAppraisalEmployeeScore();
            employeeScore.setAppraisalEmployeeId(quotaScoreBO.getAppraisalEmployeeId());
            employeeScore.setComments(quotaScoreBO.getComments());
            employeeScore.setRater(EmployeeHolder.getEmployeeId());
            employeeScore.setScore(scoreResultD);
            employeeScore.setLevel(levelName);
            employeeScore.setWeight(appraisalEmployeeStage.getWeight());

        }
        //目前只有一种
        List<HrmAppraisalEmployeeScore> scoreList = appraisalEmployeeScoreService.lambdaQuery().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).list();
        AtomicReference<BigDecimal> totalScore = new AtomicReference<>(new BigDecimal(0));
        scoreList.forEach(
                score -> {
                    if (ObjectUtil.isEmpty(score.getScore())) {
                        score.setScore(0d);
                    }
                    BigDecimal scoreTmp = new BigDecimal(score.getScore());
                    BigDecimal weightTmp = new BigDecimal(score.getWeight());
                    BigDecimal resultTmp = scoreTmp.multiply(weightTmp).divide(new BigDecimal(100));
                    totalScore.updateAndGet(v -> v.add(resultTmp));

                }
        );
        //将当前员工的评分也计入
        BigDecimal scoreTmp = new BigDecimal(employeeScore.getScore());
        BigDecimal weightTmp = new BigDecimal(employeeScore.getWeight());
        BigDecimal resultTmp = scoreTmp.multiply(weightTmp).divide(new BigDecimal(100));
        totalScore.updateAndGet(v -> v.add(resultTmp));

        double score = totalScore.get().setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        String totalScoreLevelName = queryResultLevel(score, resultSettingList);
        if (StrUtil.isEmpty(totalScoreLevelName)) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPRAISAL_LEVEL_ERROR, "当前计算总分未包含在等级设置列表中");
        }
        PreQuotaScoreVO preQuotaScoreVO = new PreQuotaScoreVO();
        preQuotaScoreVO.setScore(scoreResultD);
        preQuotaScoreVO.setLevelName(levelName);
        preQuotaScoreVO.setLevelNameOfTotal(totalScoreLevelName);
        preQuotaScoreVO.setScoreOfTotal(score);
        return preQuotaScoreVO;
    }


    private String queryResultLevel(Double score, List<HrmAppraisalPlanResultSetting> resultSettingList) {
        for (HrmAppraisalPlanResultSetting appraisalPlanResultSetting : resultSettingList) {
            Double scoreLowerLimit = appraisalPlanResultSetting.getScoreLowerLimit();//分数下限
            Double scoreUpperLimit = appraisalPlanResultSetting.getScoreUpperLimit();//分数上限
            if ((scoreLowerLimit <= score) && (score <= scoreUpperLimit)) {
                return appraisalPlanResultSetting.getLevelName();
            }
        }
        return null;
    }


    @Override
    public void rejectScore(RejectScoreBO rejectScoreBO) {
        HrmAppraisalEmployee appraisalEmployee = getById(rejectScoreBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        HrmAppraisalEmployeeStage curStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId())
                .eq(HrmAppraisalEmployeeStage::getSort, appraisalEmployee.getStageSort()).and(item -> item.eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.SELF_SCORE.getValue()).or().eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.OTHER_SCORE.getValue())).one();
        if (ObjectUtil.isNull(curStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STAGE_NULL_ERROR);
        }
        HrmAppraisalEmployeeStage preStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId())
                .eq(HrmAppraisalEmployeeStage::getSort, appraisalEmployee.getStageSort() - 1).one();
        if (preStage.getStageType().equals(AppraisalStageStatusEnum.SELF_SCORE.getValue()) || preStage.getStageType().equals(AppraisalStageStatusEnum.OTHER_SCORE.getValue())) {
            //驳回前一个人的评分
            appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, preStage.getAppraisalStageId()).update();
            //将当前待处理评分状态改为未处理
            appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.NOT_PROCESSED.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, curStage.getAppraisalStageId()).update();
            //更新被驳回员工的评分表得评分和等级置为空
            appraisalEmployeeScoreService.lambdaUpdate().set(HrmAppraisalEmployeeScore::getLevel, null).set(HrmAppraisalEmployeeScore::getScore, null).eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, rejectScoreBO.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeScore::getRater, preStage.getStageUserId()).update();
            //更新当前员工考核得处理阶段
            updateEmployeeStageStatus(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), preStage.getSort());
        } else {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_QUERY_PARAM_ERROR, "第一级评分驳回无效！");
        }
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalEmployee.getAppraisalPlanId());
        //todo 添加操作记录
        sendMessage(UserUtil.getUserId(), appraisalEmployee.getEmployeeId(), appraisalEmployee.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING_REJECT.getType(), appraisalPlan.getAppraisalPlanName());
        actionRecordService.rejectScoringRecord(rejectScoreBO.getAppraisalEmployeeId(), rejectScoreBO.getRejectReason());
    }

    @Override
    public QuotaScoreVO scoreInfo(Long appraisalEmployeeId) {
        QuotaScoreVO quotaScoreVo = new QuotaScoreVO();
        HrmAppraisalEmployee appraisalEmployee = getById(appraisalEmployeeId);
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        HrmAppraisalPlan appraisalPlan = appraisalPlanService.lambdaQuery().eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
        HrmAppraisalPlanProcessSetting processSetting = processSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalPlan.getAppraisalPlanId()).one();

        //获取员工指标评分记录详情
        List<HrmAppraisalEmployeeQuotaScore> appraisalEmployeeQuotaScoreList = appraisalEmployeeQuotaScoreService.queryQuotaScoreList(appraisalEmployeeId);//员工评分详情
        List<QuotaEmployeeScoreVO> quotaEmployeeScoreVOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeQuotaScoreList)) {
            appraisalEmployeeQuotaScoreList.forEach(
                    appraisalEmployeeQuotaScore -> {
                        QuotaEmployeeScoreVO quotaEmployeeScoreVO = new QuotaEmployeeScoreVO();
                        quotaEmployeeScoreVO.setComments(appraisalEmployeeQuotaScore.getComments());
                        quotaEmployeeScoreVO.setScore(appraisalEmployeeQuotaScore.getScore());
                        quotaEmployeeScoreVO.setEmployeeName(appraisalEmployeeQuotaScore.getRaterName());
                        quotaEmployeeScoreVOList.add(quotaEmployeeScoreVO);
                    }
            );
        }

        //获取员工评分记录
        List<HrmAppraisalEmployeeScore> appraisalEmployeeScoreList = appraisalEmployeeScoreService.lambdaQuery().eq(HrmAppraisalEmployeeScore::getAppraisalEmployeeId, appraisalEmployeeId).list();//员工评分记录
        Map<Long, HrmAppraisalEmployeeScore> employeeScoreMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(appraisalEmployeeScoreList)) {
            appraisalEmployeeScoreList.forEach(
                    appraisalEmployeeScore -> {
                        employeeScoreMap.put(appraisalEmployeeScore.getRater(), appraisalEmployeeScore);
                    }
            );
        }
        List<HrmProcessSettingScoring> processSettingScoringList = processSettingScoringService.lambdaQuery().eq(HrmProcessSettingScoring::getProcessId, processSetting.getProcessId())
                .orderByAsc(HrmProcessSettingScoring::getSort).list();//获取评分设置-并根据考核评分流程配置的顺序进行加载
        List<ScoreRecordVO> scoreRecordVOList = new ArrayList<>();
        processSettingScoringList.forEach(
                processSettingScore -> {
                    HrmAppraisalEmployeeScore appraisalEmployeeScore = employeeScoreMap.get(processSettingScore.getRater());
                    if (ObjectUtil.isNotNull(appraisalEmployeeScore)) {
                        ScoreRecordVO scoreRecordVO = new ScoreRecordVO();
                        scoreRecordVO.setComments(appraisalEmployeeScore.getComments());
                        scoreRecordVO.setEmployeeName(UserCacheUtil.getUserName(processSettingScore.getRater()));
                        DecimalFormat df = new DecimalFormat("0.00");
                        String scoreStr = df.format(appraisalEmployeeScore.getScore());
                        scoreRecordVO.setResult(scoreStr + "分" + "(" + appraisalEmployeeScore.getLevel() + ")");
                        scoreRecordVO.setWeight(processSettingScore.getWeight());
                        scoreRecordVOList.add(scoreRecordVO);
                    }
                }
        );
        quotaScoreVo.setQuotaEmployeeScoreVOList(quotaEmployeeScoreVOList);
        quotaScoreVo.setScoreRecordVOList(scoreRecordVOList);
        return quotaScoreVo;
    }

    @Transactional
    @Override
    public void resultAudit(ResultAuditBO resultAuditBO, StatusEnum statusEnum) {
        HrmAppraisalEmployee appraisalEmployee = getById(resultAuditBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        int stageSort = appraisalEmployee.getStageSort();//获取当前处理阶段节点
        switch (statusEnum) {
            case NORMAL:
                appraisalEmployeeStageService.updateStageStatusPass(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), AppraisalStageStatusEnum.RESULT_AUDIT);
                actionRecordService.resultAuditPass(appraisalEmployee.getAppraisalEmployeeId());
                break;
            case ABNORMAL:
                //结果审核驳回 - 则找到被驳回的节点的最前面的一个节点
                HrmAppraisalEmployeeStage firstStage = appraisalEmployeeStageService.lambdaQuery().in(HrmAppraisalEmployeeStage::getAppraisalStageId, resultAuditBO.getAppraisalStageIdList()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
                //更新阶段流程被驳回的节点状态为待处理
                appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).in(HrmAppraisalEmployeeStage::getAppraisalStageId, resultAuditBO.getAppraisalStageIdList()).update();
                /**
                 * 设置结果审核驳回标志位
                 */
                appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.REJECT.getValue())
                        .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId())
                        .eq(HrmAppraisalEmployeeStage::getSort, stageSort).update();

                List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = appraisalEmployeeStageService.lambdaQuery().in(HrmAppraisalEmployeeStage::getAppraisalStageId, resultAuditBO.getAppraisalStageIdList()).list();
                //更新员工考核计划流程的节点回退为最前面被驳回的节点
                lambdaUpdate().set(HrmAppraisalEmployee::getScore, null).set(HrmAppraisalEmployee::getLevel, null).set(HrmAppraisalEmployee::getStageSort, firstStage.getSort()).set(HrmAppraisalEmployee::getStageStatus, firstStage.getStageType()).eq(HrmAppraisalEmployee::getAppraisalEmployeeId, resultAuditBO.getAppraisalEmployeeId()).update();

                //将驳回的评分节点分数等级数据设置为空
                appraisalEmployeeScoreService.rejectScoreByStageId(appraisalEmployee.getAppraisalEmployeeId(), resultAuditBO.getAppraisalStageIdList());
                HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalEmployee.getAppraisalPlanId());
                appraisalEmployeeStageList.forEach(
                        appraisalEmployeeStage -> {
                            //给驳回的结点的人发送评分被驳回通知
                            sendMessage(UserUtil.getUserId(), appraisalEmployeeStage.getEmployeeId(), appraisalEmployeeStage.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING_REJECT.getType(), appraisalPlan.getAppraisalPlanName());
                            //发送评分待处理通知
                            sendMessage(UserUtil.getUserId(), appraisalEmployeeStage.getEmployeeId(), appraisalEmployeeStage.getAppraisalEmployeeId(), AdminMessageEnum.HRM_EMPLOYEE_APPRAISAL_SCORING.getType(), appraisalPlan.getAppraisalPlanName());
                        }
                );
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("level", null);
                fieldMap.put("score", null);
                fieldMap.put("stageSort", firstStage.getSort());
                fieldMap.put("stageUsers", firstStage.getStageUserId());
                fieldMap.put("stageUsersName", firstStage.getStageUserName());
                fieldMap.put("stageStatus", firstStage.getStageType());
                fieldMap.put("stageStatusName", firstStage.getStageName());
                updateField(fieldMap, Arrays.asList(appraisalEmployee.getAppraisalEmployeeId()));

                actionRecordService.resultAuditReject(appraisalEmployee.getAppraisalEmployeeId(), resultAuditBO.getReason());
                break;
        }
    }


    @Override
    public void resultConfirmation(ResultAuditBO resultAuditBO, StatusEnum statusEnum) {
        HrmAppraisalEmployee appraisalEmployee = getById(resultAuditBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        switch (statusEnum) {
            case NORMAL://结果确认通过
                appraisalEmployeeStageService.updateStageStatusPass(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), AppraisalStageStatusEnum.RESULT_CONFIRMATION);
                actionRecordService.confirmResult(resultAuditBO.getAppraisalEmployeeId());
                break;
        }

    }

    @Override
    public List<HrmAppraisalEmployeeStage> queryStageList(Long appraisalEmployeeId) {
        List<HrmAppraisalEmployeeStage> appraisalEmployeeStageList = appraisalEmployeeStageService.queryAppraisalEmployeeStageList(appraisalEmployeeId);
        return appraisalEmployeeStageList;
    }

    @Override
    public Long queryAppraisalPlanProcessId(Long appraisalEmployeeId) {
        HrmAppraisalEmployee appraisalEmployee = getById(appraisalEmployeeId);
        HrmAppraisalPlanProcessSetting planProcessSetting = processSettingService.lambdaQuery().eq(HrmAppraisalPlanProcessSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
        return planProcessSetting.getProcessId();
    }

    @Transactional
    @Override
    public void resultAppeal(ResultAuditBO quotaAuditBO) {
        HrmAppraisalEmployee appraisalEmployee = getById(quotaAuditBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        HrmAppraisalEmployeeStage appraisalEmployeeStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.RESULT_CONFIRMATION.getValue())
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (ObjectUtil.isNull(appraisalEmployeeStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STAGE_NULL_ERROR);
        }
        if (!appraisalEmployee.getEmployeeId().equals(EmployeeHolder.getEmployeeId())) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPEAL_ERROR, "只有被考核人才能进行结果申诉");
        }
        List<HrmAppraisalEmployeeAppealRecord> recordList = new ArrayList<>();
        List<Long> appraisalStageIdList = quotaAuditBO.getAppraisalStageIdList();
        if (CollectionUtil.isEmpty(appraisalStageIdList)) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPEAL_ERROR, "申诉节点不能为空");
        }
        List<HrmAppraisalEmployeeStage> stageList = appraisalEmployeeStageService.queryStageListByIds(appraisalStageIdList);
        String stageUserName = stageList.stream().map(HrmAppraisalEmployeeStage::getStageUserName).collect(Collectors.joining(","));
        appraisalStageIdList.forEach(
                appraisalStageId -> {
                    HrmAppraisalEmployeeAppealRecord appraisalEmployeeAppealRecord = new HrmAppraisalEmployeeAppealRecord();
                    appraisalEmployeeAppealRecord.setAppraisalEmployeeId(quotaAuditBO.getAppraisalEmployeeId());
                    appraisalEmployeeAppealRecord.setAppraisalStageId(appraisalStageId);
                    recordList.add(appraisalEmployeeAppealRecord);
                }
        );
        appraisalEmployeeAppealRecordService.saveBatch(recordList);//添加申诉节点记录用于申诉全部通过的时候进行节点回撤的时候知道回撤到哪个节点，以及回撤哪些节点
        //查找申诉确认人的第一级
        HrmAppraisalEmployeeStage appealStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId())
                .eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        //更新结果确认为已申诉
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.APPEALED.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, appraisalEmployeeStage.getAppraisalStageId()).update();
        //更新申诉确认人的处理状态为待处理
        appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).eq(HrmAppraisalEmployeeStage::getAppraisalStageId, appealStage.getAppraisalStageId()).update();
        updateEmployeeStageStatus(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), appealStage.getSort());
        //待处理申诉表添加记录，用于定时任务查询和执行
        appraisalAppealPendingRecordService.addAppealPendingRecord(appraisalEmployee.getAppraisalEmployeeId(), appealStage.getAppraisalStageId());
        actionRecordService.resultAppeal(quotaAuditBO.getAppraisalEmployeeId(), quotaAuditBO.getReason(), stageUserName, quotaAuditBO.getBatchId());

        HrmAppraisalPlan appraisalPlan = appraisalPlanService.getById(appraisalEmployee.getAppraisalPlanId());
        Integer messageType = achievementsUtil.queryMessageType(appealStage.getStageType(), ProcessingStatusEnum.PENDING.getValue());
        if (ObjectUtil.isNotNull(messageType)) {//只处理已有的消息类型
            sendMessage(UserUtil.getUserId(), appealStage.getStageUserId(), appealStage.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
        }

    }

    @Transactional
    @Override
    public void resultAppealPass(ResultAuditBO quotaAuditBO, Boolean isJob) {
        HrmAppraisalEmployee appraisalEmployee = getById(quotaAuditBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        //获取当前待处理阶段
        HrmAppraisalEmployeeStage appraisalEmployeeStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue())
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (!isJob && (!EmployeeHolder.getEmployeeId().equals(appraisalEmployeeStage.getStageUserId()))) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPEAL_ERROR, "没有驳回权限!");
        }
        //申诉确认通过
        appraisalEmployeeStageService.updateStageStatusPass(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), AppraisalStageStatusEnum.APPEAL_CONFIRMATION);
        //处理过了,就移除对应的定时任务
        appraisalAppealPendingRecordService.lambdaUpdate().eq(HrmAppraisalAppealPendingRecord::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalAppealPendingRecord::getStageId, appraisalEmployeeStage.getAppraisalStageId()).remove();
        actionRecordService.resultAppealPass(quotaAuditBO.getAppraisalEmployeeId());
    }

    @Transactional
    @Override
    public void resultAppealReject(ResultAuditBO quotaAuditBO, Boolean isJob) {
        HrmAppraisalEmployee appraisalEmployee = getById(quotaAuditBO.getAppraisalEmployeeId());
        if (ObjectUtil.isNull(appraisalEmployee)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        //获取当前待处理阶段
        HrmAppraisalEmployeeStage appraisalEmployeeStage = appraisalEmployeeStageService.lambdaQuery().eq(HrmAppraisalEmployeeStage::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalEmployeeStage::getStageType, AppraisalStageStatusEnum.APPEAL_CONFIRMATION.getValue())
                .eq(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PENDING.getValue()).orderByAsc(HrmAppraisalEmployeeStage::getSort).last("limit 1").one();
        if (ObjectUtil.isNull(appraisalEmployeeStage)) {
            throw new CrmException(HrmCodeEnum.APPRAISAL_PLAN_STAGE_NULL_ERROR);
        }
        if (!isJob && (!EmployeeHolder.getEmployeeId().equals(appraisalEmployeeStage.getStageUserId()))) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_APPEAL_ERROR, "没有驳回权限!");
        }
        //由于申诉阶段被驳回后将后面的申诉确认和结果确认全部设置为已处理，并将结果确认设置为已处理
        if (ObjectUtil.isNotNull(appraisalEmployeeStage)) {
            appraisalEmployeeStageService.lambdaUpdate().set(HrmAppraisalEmployeeStage::getStatus, ProcessingStatusEnum.PROCESSED.getValue())
                    .eq(HrmAppraisalEmployeeStage::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId())
                    .eq(HrmAppraisalEmployeeStage::getEmployeeId, appraisalEmployee.getEmployeeId()).ge(HrmAppraisalEmployeeStage::getSort, appraisalEmployeeStage.getSort()).update();
            HrmAppraisalEmployeeStage endStageInfo = appraisalEmployeeStageService.queryAppraisalStageInfoByStageType(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), AppraisalStageStatusEnum.END.getValue());
            updateEmployeeStageStatus(endStageInfo.getAppraisalPlanId(), endStageInfo.getEmployeeId(), endStageInfo.getSort());
            //处理过了,就移除对应的定时任务
            appraisalAppealPendingRecordService.lambdaUpdate().eq(HrmAppraisalAppealPendingRecord::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).eq(HrmAppraisalAppealPendingRecord::getStageId, appraisalEmployeeStage.getAppraisalStageId()).remove();
            appraisalEmployeeAppealRecordService.lambdaUpdate().eq(HrmAppraisalEmployeeAppealRecord::getAppraisalEmployeeId, appraisalEmployee.getAppraisalEmployeeId()).remove();//由于申诉被拒绝考核流程结束，因此移除申诉相关节点记录

            Integer messageType = achievementsUtil.queryMessageType(appraisalEmployeeStage.getStageType(), ProcessingStatusEnum.REJECT.getValue());
            HrmAppraisalPlan appraisalPlan = appraisalPlanService.lambdaQuery().select(HrmAppraisalPlan::getAppraisalPlanName).eq(HrmAppraisalPlan::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).one();
            if (ObjectUtil.isNotNull(messageType)) {//只处理已有的消息类型
                sendMessage(UserUtil.getUserId(), appraisalEmployee.getEmployeeId(), appraisalEmployee.getAppraisalEmployeeId(), messageType, appraisalPlan.getAppraisalPlanName());
            }
            actionRecordService.resultAppealReject(quotaAuditBO.getAppraisalEmployeeId(), quotaAuditBO.getReason());
        }
    }

    @Override
    public BasePage<EmployeeAppraisalPlanVO> queryAppraisalArchives(QueryEmployeeQuotaBO queryEmployeeQuotaBO) {
        queryEmployeeQuotaBO.setEmployeeId(EmployeeHolder.getEmployeeId());//查询员工本人绩效档案列表
        return baseMapper.queryAppraisalList(queryEmployeeQuotaBO.parse(), queryEmployeeQuotaBO);
    }

    @Override
    public List<ScoringStageInfoVO> queryScoringPoint(Long appraisalEmployeeId) {
        List<ScoringStageInfoVO> employeeStageList = appraisalEmployeeStageService.queryScoringStage(appraisalEmployeeId);
        return employeeStageList;
    }

    @Override
    public void dealWithOverdueUnAudit() {
        try {
            UserUtil.setUser(0L);
            List<HrmAppraisalAppealPendingRecord> pendingRecordList = appraisalAppealPendingRecordService.queryOverduePendingRecordList();
            List<Long> dealWithIds = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(pendingRecordList)) {
                pendingRecordList.forEach(
                        pendingRecord -> {
                            Integer overdueType = pendingRecord.getOverdueType();
                            OverdueTypeEnum overdueTypeEnum = OverdueTypeEnum.parse(overdueType);
                            switch (overdueTypeEnum) {
                                case PASS:
                                    ResultAuditBO quotaAuditBO = new ResultAuditBO();
                                    quotaAuditBO.setAppraisalEmployeeId(pendingRecord.getAppraisalEmployeeId());
                                    resultAppealPass(quotaAuditBO, true);
                                    dealWithIds.add(pendingRecord.getPendingRecordId());
                                    break;
                                case REFUSE:
                                    ResultAuditBO refuseAuditBO = new ResultAuditBO();
                                    refuseAuditBO.setAppraisalEmployeeId(pendingRecord.getAppraisalEmployeeId());
                                    refuseAuditBO.setReason("超期未处理");
                                    resultAppealReject(refuseAuditBO, true);
                                    dealWithIds.add(pendingRecord.getPendingRecordId());
                                    break;
                            }
                        }
                );
            }
            //任务处理完成后删除对应的任务
            if (CollectionUtil.isNotEmpty(dealWithIds)) {
                appraisalAppealPendingRecordService.lambdaUpdate().in(HrmAppraisalAppealPendingRecord::getPendingRecordId, dealWithIds).remove();
            }
        } catch (Exception e) {
            logger.info("job触发逾期未审核任务出错", e);
        } finally {
            UserUtil.removeUser();
        }
    }

    @Override
    public List<QueryAppraisalRecordListVO> queryRecordList(QueryRecordListBO queryRecordListBO) {
        return appraisalActionRecordService.queryRecordList(queryRecordListBO);
    }

    @Override
    public void saveAppraisalEmployeeList(List<HrmAppraisalEmployee> hrmAppraisalEmployeeList) {
        hrmAppraisalEmployeeList.forEach(
                hrmAppraisalEmployee -> {
                    HrmModelSaveBO hrmModelSaveBO = new HrmModelSaveBO();
                    hrmModelSaveBO.setEntity(BeanUtil.beanToMap(hrmAppraisalEmployee));
                    hrmAppraisalEmployee.setCreateUserId(EmployeeHolder.getEmployeeId());
                    hrmAppraisalEmployee.setCreateTime(new Date());
                    hrmAppraisalEmployee.setUpdateTime(new Date());
                    save(hrmAppraisalEmployee);
                    hrmModelSaveBO.setField(new ArrayList<>());
                    savePage(hrmModelSaveBO, hrmAppraisalEmployee.getAppraisalEmployeeId(), false);//保存至ES
                }
        );
    }

    @Override
    public void updateAllEmployeeStageForStart(Long appraisalPlanId, Integer stageSort, Integer status) {
        List<HrmAppraisalEmployee> appraisalEmployeeList = lambdaQuery().select(HrmAppraisalEmployee::getAppraisalEmployeeId, HrmAppraisalEmployee::getAppraisalPlanId, HrmAppraisalEmployee::getEmployeeId).eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).list();
        appraisalEmployeeList.forEach(
                appraisalEmployee -> {
                    HrmAppraisalEmployeeStage appraisalEmployeeStage = appraisalEmployeeStageService.queryAppraisalStageInfoBySort(appraisalEmployee.getAppraisalPlanId(), appraisalEmployee.getEmployeeId(), stageSort);
                    LambdaUpdateChainWrapper<HrmAppraisalEmployee> baseUpdate = lambdaUpdate();
                    if (ObjectUtil.isNotNull(status)) {
                        baseUpdate.set(HrmAppraisalEmployee::getAppraisalStatus, status);
                    }
                    baseUpdate.set(HrmAppraisalEmployee::getStageSort, stageSort).set(HrmAppraisalEmployee::getStageStatus, appraisalEmployeeStage.getStageType()).eq(HrmAppraisalEmployee::getEmployeeId, appraisalEmployeeStage.getEmployeeId()).eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).update();
                    //更新ES相关状态信息
                    Map<String, Object> map = new HashMap<>();
                    map.put("stageUsers", appraisalEmployeeStage.getStageUserId());
                    map.put("stageUsersName", appraisalEmployeeStage.getStageUserName());
                    map.put("stageStatus", appraisalEmployeeStage.getStageType());
                    map.put("stageStatusName", AppraisalStageStatusEnum.parseName(appraisalEmployeeStage.getStageType()));
                    if (ObjectUtil.isNotNull(status)) {
                        map.put("appraisalStatus", appraisalEmployeeStage.getStageType());
                        map.put("appraisalStatusName", AppraisalStageStatusEnum.parseName(appraisalEmployeeStage.getStageType()));
                    }
                    //由于每个员工的阶段流程不一样，需单独做处理
                    updateField(map, Arrays.asList(appraisalEmployee.getAppraisalEmployeeId()));
                }
        );

    }

    @Override
    public void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort) {
        updateEmployeeStageStatus(appraisalPlanId, employeeId, stageSort, null, null, false);
    }

    @Override
    public void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status) {
        updateEmployeeStageStatus(appraisalPlanId, employeeId, stageSort, status, null, false);
    }

    @Override
    public void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status, Map<String, Object> supplementField) {
        updateEmployeeStageStatus(appraisalPlanId, employeeId, stageSort, status, supplementField, false);
    }

    @Transactional
    @Override
    public void updateEmployeeStageStatus(Long appraisalPlanId, Long employeeId, Integer stageSort, Integer status, Map<String, Object> supplementField, Boolean needCleanScore) {
        HrmAppraisalEmployeeStage appraisalEmployeeStage = appraisalEmployeeStageService.queryAppraisalStageInfoBySort(appraisalPlanId, employeeId, stageSort);
        LambdaUpdateChainWrapper<HrmAppraisalEmployee> baseUpdate = lambdaUpdate();
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotNull(status)) {
            baseUpdate.set(HrmAppraisalEmployee::getAppraisalStatus, status);
            map.put("appraisalStatus", status);
            map.put("appraisalStatusName", AppraisalStatusEnum.parseName(status));
        }
        if (ObjectUtil.isNotNull(needCleanScore) && needCleanScore) {
            baseUpdate.set(HrmAppraisalEmployee::getLevel, null);
            baseUpdate.set(HrmAppraisalEmployee::getScore, null);
            map.put("level", null);
            map.put("score", null);
        }
        baseUpdate.set(HrmAppraisalEmployee::getStageSort, stageSort).set(HrmAppraisalEmployee::getStageStatus, appraisalEmployeeStage.getStageType()).eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployee::getEmployeeId, employeeId).update();
        HrmAppraisalEmployee appraisalEmployee = lambdaQuery().eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).eq(HrmAppraisalEmployee::getEmployeeId, employeeId).one();

        map.put("stageUsers", appraisalEmployeeStage.getStageUserId());
        map.put("stageUsersName", appraisalEmployeeStage.getStageUserName());
        map.put("stageStatus", appraisalEmployeeStage.getStageType());
        map.put("stageStatusName", AppraisalStageStatusEnum.parseName(appraisalEmployeeStage.getStageType()));
        if (MapUtil.isNotEmpty(supplementField)) {
            map.putAll(supplementField);
        }
        updateField(map, Arrays.asList(appraisalEmployee.getAppraisalEmployeeId()));
    }

    @Override
    public void updateAllEmployeeStatus(Long appraisalPlanId, AppraisalStatusEnum appraisalStatusEnum) {
        List<HrmAppraisalEmployee> appraisalEmployeeList = lambdaQuery().select(HrmAppraisalEmployee::getAppraisalEmployeeId, HrmAppraisalEmployee::getAppraisalPlanId, HrmAppraisalEmployee::getEmployeeId).eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).list();
        LambdaUpdateChainWrapper<HrmAppraisalEmployee> baseUpdate = lambdaUpdate();
        baseUpdate.set(HrmAppraisalEmployee::getAppraisalStatus, appraisalStatusEnum.getValue());
        if (appraisalStatusEnum.getValue().equals(AppraisalStatusEnum.PLACE_ON_FILE.getValue())) {
            baseUpdate.set(HrmAppraisalEmployee::getFileTime, new Date());//设置归档日期
        }
        baseUpdate.eq(HrmAppraisalEmployee::getAppraisalPlanId, appraisalPlanId).update();
        appraisalEmployeeList.forEach(
                appraisalEmployeeInfo -> {
                    Map<String, Object> fieldMap = new HashMap<>();
                    fieldMap.put("appraisalStatus", appraisalStatusEnum.getValue());
                    fieldMap.put("appraisalStatusName", appraisalStatusEnum.getName());
                    fieldMap.put("stageUsers", null);
                    fieldMap.put("stageUsersName", null);
                    updateField(fieldMap, Arrays.asList(appraisalEmployeeInfo.getAppraisalEmployeeId()));
                }
        );
    }

    @Override
    public List<HrmAppraisalEmployee> queryEmployeeListByPlanId(Long appraisalPlanId) {
        return baseMapper.queryEmployeeListByPlanId(appraisalPlanId);
    }

    @Override
    public HrmAppraisalEmployee queryRecentlyAppraisalEmployee(Long employeeId) {
        return baseMapper.queryRecentlyAppraisalEmployee(employeeId);
    }

    /**
     * 发送通知
     *
     * @param employeeId 接收人
     * @param typeId     关联Id
     * @param type       类型
     * @param title      标题
     */
    public void sendMessage(Long userId, Long employeeId, Long typeId, Integer type, String title) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateUser(userId);
        adminMessage.setCreateTime(LocalDateTime.now());
        adminMessage.setRecipientUser(EmployeeCacheUtil.getUserId(employeeId));
        adminMessage.setTypeId(typeId);
        adminMessage.setLabel(8);
        adminMessage.setType(type);
        adminMessage.setTitle(title);
        adminMessageService.save(adminMessage);
    }

    @Override
    public void setOtherField(Map<String, Object> map) {
        HrmEmployee appraisalEmployeeInfo = hrmEmployeeService.queryById((Long) map.get("employeeId"));
        if (ObjectUtil.isNull(appraisalEmployeeInfo)) {
            appraisalEmployeeInfo = new HrmEmployee();
        }
//        HrmEmployee employeeInfo = hrmEmployeeService.queryById((Long) map.get("createUserId"));
        map.put("jobNumber", appraisalEmployeeInfo.getJobNumber());
        map.put("mobile", appraisalEmployeeInfo.getMobile());
        map.put("deptId", appraisalEmployeeInfo.getDeptId());
        map.put("employeeName", appraisalEmployeeInfo.getEmployeeName());
        map.put("employmentForms", appraisalEmployeeInfo.getEmploymentForms());
        String deptName = hrmDeptService.querySimpleDept(appraisalEmployeeInfo.getDeptId()).getDeptName();
        map.put("deptName", deptName);
        HrmAppraisalEmployeeStage stageInfo = appraisalEmployeeStageService.queryAppraisalStageInfoBySort((Long) map.get("appraisalPlanId"), (Long) map.get("employeeId"), (Integer) map.get("stageSort"));
        if (ObjectUtil.isNotNull(stageInfo)) {//启动考核计划的时候新数据可能没有阶段信息
            map.put("stageUsers", stageInfo.getStageUserId());
            map.put("stageUsersName", stageInfo.getStageUserName());
            map.put("appraisalStatusName", AppraisalStatusEnum.parseName((Integer) map.get("appraisalStatus")));
            map.put("stageStatusName", AppraisalStageStatusEnum.parseName(stageInfo.getStageType()));
        }
    }

    @Override
    public String[] appendSearch() {
        return new String[0];
    }

    @Override
    public List<HrmModelFiledVO> queryDefaultField() {
        List<HrmModelFiledVO> filedList = hrmEmployeeFieldService.queryHrmField(getLabel().getType());
        filedList.add(new HrmModelFiledVO("updateTime", FieldEnum.DATETIME, 1));
        filedList.add(new HrmModelFiledVO("createTime", FieldEnum.DATETIME, 1));
        filedList.add(new HrmModelFiledVO("createUserId", FieldEnum.USER, 1));
        filedList.add(new HrmModelFiledVO("status", FieldEnum.TEXT, 1));
        filedList.add(new HrmModelFiledVO("activateStatus", FieldEnum.TEXT, 1));
        filedList.add(new HrmModelFiledVO("createUserName", FieldEnum.TEXT, 1));
        filedList.add(new HrmModelFiledVO("appraisalStatus", FieldEnum.TEXT, 1));
        filedList.add(new HrmModelFiledVO("stageStatus", FieldEnum.TEXT, 1));
        filedList.add(new HrmModelFiledVO("stageSort", FieldEnum.TEXT, 1));
        return filedList;
    }

    @Override
    public HrmEnum getLabel() {
        return HrmEnum.APPRAISAL_EMPLOYEE;
    }
}
