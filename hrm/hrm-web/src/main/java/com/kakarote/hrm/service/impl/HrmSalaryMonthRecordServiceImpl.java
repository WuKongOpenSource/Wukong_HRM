package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TransferUtil;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.common.tax.TaxRuleContext;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.QueryHistorySalaryDetailBO;
import com.kakarote.hrm.entity.BO.QueryHistorySalaryListBO;
import com.kakarote.hrm.entity.BO.QuerySalaryPageListBO;
import com.kakarote.hrm.entity.BO.UpdateSalaryBO;
import com.kakarote.hrm.entity.DTO.ComputeSalaryDTO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.QueryHistorySalaryDetailVO;
import com.kakarote.hrm.entity.VO.QueryHistorySalaryListVO;
import com.kakarote.hrm.entity.VO.QuerySalaryPageListVO;
import com.kakarote.hrm.entity.VO.SalaryOptionHeadVO;
import com.kakarote.hrm.mapper.HrmEmployeeMapper;
import com.kakarote.hrm.mapper.HrmSalaryMonthEmpRecordMapper;
import com.kakarote.hrm.mapper.HrmSalaryMonthRecordMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.service.actionrecord.impl.SalaryActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * <p>
 * 每月薪资记录 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@Service
@Slf4j
public class HrmSalaryMonthRecordServiceImpl extends BaseServiceImpl<HrmSalaryMonthRecordMapper, HrmSalaryMonthRecord> implements IHrmSalaryMonthRecordService {

    @Autowired
    private IHrmSalaryOptionService salaryOptionService;

    @Autowired
    private HrmSalaryMonthEmpRecordMapper salaryMonthEmpRecordMapper;

    @Autowired
    private IHrmSalaryMonthEmpRecordService salaryMonthEmpRecordService;

    @Autowired
    private IHrmInsuranceMonthRecordService insuranceMonthRecordService;

    @Autowired
    private IHrmInsuranceMonthEmpRecordService insuranceMonthEmpRecordService;

    @Autowired
    private IHrmSalaryMonthOptionValueService salaryMonthOptionValueService;

    @Autowired
    private IHrmSalaryConfigService salaryConfigService;

    @Autowired
    private IHrmSalaryGroupService salaryGroupService;

    @Autowired
    private HrmSalaryMonthRecordMapper salaryMonthRecordMapper;

    @Autowired
    private IHrmEmployeeAbnormalChangeRecordService abnormalChangeRecordService;

    @Autowired
    private HrmEmployeeMapper employeeMapper;

    @Autowired
    private IHrmDeptService deptService;

    @Autowired
    private TaxRuleContext taxRuleContext;

    @Resource
    private SalaryActionRecordServiceImpl salaryActionRecordService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Autowired
    private IHrmAttendanceClockService attendanceClockService;
    @Autowired
    private IHrmAttendanceGroupService attendanceGroupService;
    @Autowired
    private IHrmAttendanceRuleService attendanceRuleService;

    @Autowired
    private IHrmSalaryGroupRelationDeptService salaryGroupRelDeptService;

    @Autowired
    private IHrmSalaryGroupRelationEmployeeService salaryGroupRelEmpService;

    @Autowired
    private IHrmSalaryMonthRecordService salaryMonthRecordService;

    private static final int TWO = 2;

    private static final int FIVE = 5;

    private static final int FOUR = 4;

    private static final int STATUS = 11;

    private static final int ZERO = 0;

    private static final int THREE = 3;

    private static final int ONE = 1;

    @Override
    public List<SalaryOptionHeadVO> querySalaryOptionHead() {
        List<HrmSalaryOption> list = salaryOptionService.lambdaQuery()
                .select(HrmSalaryOption::getCode, HrmSalaryOption::getName, HrmSalaryOption::getIsFixed)
                .eq(HrmSalaryOption::getIsShow, IsEnum.YES.getValue())
                .ne(HrmSalaryOption::getParentCode, 0)
                .eq(HrmSalaryOption::getIsOpen, 1)
                .orderByAsc(HrmSalaryOption::getCode).list();
        List<SalaryOptionHeadVO> optionHeadVOList = new LinkedList<>();
        optionHeadVOList.add(new SalaryOptionHeadVO(1, "计薪天数", 1));
        optionHeadVOList.add(new SalaryOptionHeadVO(2, "实际计薪天数", 1));
        List<SalaryOptionHeadVO> salaryOptionHeadVOList = TransferUtil.transferList(list, SalaryOptionHeadVO.class);
        optionHeadVOList.addAll(salaryOptionHeadVOList);
        List<Integer> attendList = Arrays.asList(180101, 190101, 190102, 190103, 190104, 190105, 190106, 200101);
        for (SalaryOptionHeadVO headVO : optionHeadVOList) {
            if (attendList.contains(headVO.getCode())) {
                //允许考勤相关的字段编辑
                headVO.setIsFixed(0);
            }
            //添加语言包key
            headVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.", StrUtil.toString(headVO.getCode())));
        }

        return optionHeadVOList;
    }

    /**
     * 通过类型查询计薪/未计薪人员
     *
     * @param type 0 未计薪 1 计薪
     * @return
     */
    @Override
    public List<Map<String, Object>> queryPaySalaryEmployeeListByType(Integer type, TaxType taxType) {
        HrmSalaryMonthRecord salaryMonthRecord = lambdaQuery().orderByDesc(HrmSalaryMonthRecord::getCreateTime).one();
        Collection<Long> dataAuthEmployeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.SALARY_MENU_ID);
        List<Map<String, Object>> list = salaryMonthEmpRecordMapper.queryPaySalaryEmployeeList(salaryMonthRecord.getEndTime(), dataAuthEmployeeIds);
        List<HrmSalaryGroup> salaryGroupList;
        if (taxType != null) {
            salaryGroupList = salaryGroupService.querySalaryGroupByTaxType(taxType.getValue());
        } else {
            salaryGroupList = salaryGroupService.lambdaQuery().list();
        }
        Set<Long> deptIdList = new HashSet<>();
        Set<Long> employeeIdList = new HashSet<>();
        salaryGroupList.forEach(salaryGroup -> {
            Set<Long> deptIds = salaryGroupRelDeptService.lambdaQuery().eq(HrmSalaryGroupRelationDept::getSalaryGroupId, salaryGroup.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationDept::getDeptId).collect(Collectors.toSet());
            Set<Long> employeeIds = salaryGroupRelEmpService.lambdaQuery().eq(HrmSalaryGroupRelationEmployee::getSalaryGroupId, salaryGroup.getGroupId()).list()
                    .stream().map(HrmSalaryGroupRelationEmployee::getEmployeeId).collect(Collectors.toSet());
            deptIdList.addAll(deptService.queryChildDeptId(deptIds));
            employeeIdList.addAll(employeeIds);
        });
        List<Map<String, Object>> employeeList = new ArrayList<>();
        list.forEach(map -> {
            Long employeeId = Convert.toLong(map.get("employeeId"));
            Long deptId = Convert.toLong(map.get("deptId"));
            if (type.equals(0)) {
                if (!deptIdList.contains(deptId) && !employeeIdList.contains(employeeId)) {
                    employeeList.add(map);
                }
            } else {
                if (deptIdList.contains(deptId) || employeeIdList.contains(employeeId)) {
                    employeeList.add(map);
                }
            }
        });
        return employeeList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void computeSalaryData(Long sRecordId, Boolean isSyncInsuranceData, Boolean isSyncAttendanceData, MultipartFile attendanceFile, MultipartFile additionalDeductionFile, MultipartFile cumulativeTaxOfLastMonthFile) {
        HrmSalaryMonthRecord salaryMonthRecord = getById(sRecordId);
        HrmSalaryConfig salaryConfig = salaryConfigService.getOne(Wrappers.emptyWrapper());
        int year = salaryMonthRecord.getYear();
        int month = salaryMonthRecord.getMonth();
        if (isSyncInsuranceData) {
            Integer socialSecurityMonthType = salaryConfig.getSocialSecurityMonthType();
            DateTime date = DateUtil.parse(year + "-" + month, "yy-MM");
            if (socialSecurityMonthType == 0) {
                date = DateUtil.offsetMonth(DateUtil.parse(year + "-" + month, "yy-MM"), -1);
            } else if (socialSecurityMonthType == TWO) {
                date = DateUtil.offsetMonth(DateUtil.parse(year + "-" + month, "yy-MM"), 1);
            }
            Optional<HrmInsuranceMonthRecord> insuranceMonthRecordOpt = insuranceMonthRecordService.lambdaQuery()
                    .eq(HrmInsuranceMonthRecord::getYear, date.year())
                    .eq(HrmInsuranceMonthRecord::getMonth, date.month() + 1).oneOpt();
            if (!insuranceMonthRecordOpt.isPresent()) {
                throw new CrmException(HrmCodeEnum.SOCIAL_SECURITY_DATA_IS_NOT_GENERATED_THIS_MONTH);
            }
        }
        List<Map<String, Object>> mapList = queryPaySalaryEmployeeListByType(1, null);
        salaryMonthRecord.setNum(mapList.size());
        salaryMonthRecord.setCheckStatus(SalaryRecordStatus.COMPUTE.getValue());
        //考勤数据map
        Map<String, Map<Integer, String>> attendanceDataMap;
        try {
            attendanceDataMap = resolveAttendanceData(attendanceFile);
        } catch (Exception e) {
            throw new CrmException(HrmCodeEnum.ATTENDANCE_DATA_ERROR);
        }
        if (isSyncAttendanceData) {
            attendanceDataMap.clear();
            LocalDate dateStartTime = DateUtil.beginOfMonth(DateUtil.parse(year + "-" + month, "yy-MM")).toLocalDateTime().toLocalDate();
            LocalDate dateEndTime = DateUtil.endOfMonth(DateUtil.parse(year + "-" + month, "yy-MM")).toLocalDateTime().toLocalDate();
            List<String> dates = attendanceClockService.findDates(dateStartTime, dateEndTime);
            List<Long> employeeIds = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                //查询出员工ID列表
                Long employeeId = Convert.toLong(map.get("employeeId"));
                employeeIds.add(employeeId);
            }
            if (CollUtil.isNotEmpty(employeeIds)) {
                //考勤规则
                Map<Long, HrmAttendanceRule> hAttendanceRuleMap = attendanceRuleService.list().stream().collect(Collectors.toMap(HrmAttendanceRule::getAttendanceRuleId, Function.identity()));
                LocalDateTime startDateTime = dateStartTime.atStartOfDay();
                LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(dateEndTime.atStartOfDay());
                List<HrmAttendanceClock> startClockList = attendanceClockService.queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
                Map<Long, List<HrmAttendanceClock>> startEmployeeClockMap = startClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
                //查询出日期区间所有的下班打卡记录
                List<HrmAttendanceClock> endClockList = attendanceClockService.queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
                Map<Long, List<HrmAttendanceClock>> endEmployeeClockMap = endClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
                for (Map<String, Object> map : mapList) {
                    // 休息天数
                    Integer restDays = ZERO;
                    // 正常出勤天数
                    Integer normalDays = ZERO;
                    // 迟到分钟
                    Integer lateMinute = ZERO;
                    // 迟到次数
                    Integer lateCount = ZERO;
                    // 早退分钟
                    Integer earlyMinute = ZERO;
                    // 早退次数
                    Integer earlyCount = ZERO;
                    // 缺卡次数
                    Integer misscardCount = ZERO;
                    // 加班次数
                    Integer overTimeCount;
                    // 请假天数
                    BigDecimal leaveDays = new BigDecimal(ZERO);
                    // 旷工天数
                    BigDecimal absenteeismDays = new BigDecimal(0);
                    Map<String, Object> attendanceEmpRecordMap = new HashMap<>();
                    Map<Integer, String> empAttendanceMap = new HashMap<>();
                    Long employeeId = Convert.toLong(map.get("employeeId"));
                    String jobNumber = (String) map.get("jobNumber");
                    //查询员工所属考勤组
                    HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(employeeId);
                    //查询对应的扣款规则
                    HrmAttendanceRule hrmAttendanceRule = hAttendanceRuleMap.get(hrmAttendanceGroup.getAttendanceRuleId());
                    //加班天数
                    overTimeCount = attendanceClockService.queryEmpAttendanceOverTimeCountDays(Arrays.asList(LocalDateTimeUtil.of(dateStartTime).toLocalDate(), LocalDateTimeUtil.of(dateEndTime).toLocalDate()), employeeId);
                    //上班打卡
                    List<HrmAttendanceClock> startEmployeeClockList = startEmployeeClockMap.get(employeeId);
                    //下班打卡
                    List<HrmAttendanceClock> endEmployeeClockList = endEmployeeClockMap.get(employeeId);
                    Map<String, Map<String, Object>> attendanceRecordDetailMap = attendanceClockService.queryAttendanceEmpRecordDetailByDate(startEmployeeClockList, endEmployeeClockList, dates, hrmAttendanceGroup, employeeId);
                    for (String date : dates) {
                        Map<String, Object> attendanceMap = attendanceRecordDetailMap.get(date);
                        Integer status = (Integer) attendanceMap.get("status");
                        switch (status) {
                            case 0:
                                normalDays = normalDays + 1;
                                lateMinute = lateMinute + (Integer) attendanceMap.get("lateMinutes");
                                lateCount = lateCount + (Integer) attendanceMap.get("lateCount");
                                earlyMinute = earlyMinute + (Integer) attendanceMap.get("earlyMinutes");
                                earlyCount = earlyCount + (Integer) attendanceMap.get("earlyCount");
                                break;
                            case 4:
                                absenteeismDays = absenteeismDays.add(new BigDecimal(attendanceMap.get("absenteeismDay").toString()));
                                break;
                            case 9:
                                restDays = restDays + 1;
                                break;
                            default:
                                break;
                        }
                        misscardCount = misscardCount + (Integer) attendanceMap.get("misscardCount");
                        leaveDays = leaveDays.add(new BigDecimal(attendanceMap.get("leaveDay").toString()));
                    }
                    attendanceEmpRecordMap.put("normalDays", normalDays - Convert.toDouble(leaveDays).intValue());
                    attendanceEmpRecordMap.put("absenteeismDays", absenteeismDays);
                    attendanceEmpRecordMap.put("earlyCount", earlyCount);
                    attendanceEmpRecordMap.put("earlyMinute", earlyMinute);
                    attendanceEmpRecordMap.put("lateCount", lateCount);
                    attendanceEmpRecordMap.put("lateMinute", lateMinute);
                    attendanceEmpRecordMap.put("misscardCount", misscardCount);
                    attendanceEmpRecordMap.put("overTimeCount", overTimeCount != null ? overTimeCount : ZERO);
                    BigDecimal lateMoney = new BigDecimal(0);
                    BigDecimal earlyMoney = new BigDecimal(0);
                    Integer isPersonal = hrmAttendanceRule.getIsPersonalization();
                    //计算迟到扣款
                    if (hrmAttendanceRule.getLateRuleMethod() == ONE) {
                        lateMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("lateMinute").toString()));
                        if (isPersonal == IsEnum.YES.getValue()) {
                            if ((int) attendanceEmpRecordMap.get("lateMinute") > hrmAttendanceRule.getLateMinutesOrCounts() && hrmAttendanceRule.getLateMinutesOrCounts() > 0) {
                                lateMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(hrmAttendanceRule.getLateMinutesOrCounts().toString())).setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    } else if (hrmAttendanceRule.getLateRuleMethod() == TWO) {
                        lateMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("lateCount").toString()));
                        if (isPersonal == IsEnum.YES.getValue()) {
                            if ((int) attendanceEmpRecordMap.get("lateCount") > hrmAttendanceRule.getLateMinutesOrCounts() && hrmAttendanceRule.getLateMinutesOrCounts() > 0) {
                                lateMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(hrmAttendanceRule.getLateMinutesOrCounts().toString())).setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    } else if (hrmAttendanceRule.getLateRuleMethod() == THREE) {
                        lateMoney = hrmAttendanceRule.getLateDeductMoney();
                    }
                    //计算早退
                    if (hrmAttendanceRule.getEarlyRuleMethod() == ONE) {
                        earlyMoney = hrmAttendanceRule.getEarlyDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("earlyMinute").toString()));
                        if (isPersonal == IsEnum.YES.getValue()) {
                            if ((int) attendanceEmpRecordMap.get("earlyMinute") > hrmAttendanceRule.getEarlyMinutesOrCounts() && hrmAttendanceRule.getEarlyMinutesOrCounts() > 0) {
                                earlyMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(hrmAttendanceRule.getEarlyMinutesOrCounts().toString())).setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    } else if (hrmAttendanceRule.getEarlyRuleMethod() == TWO) {
                        earlyMoney = hrmAttendanceRule.getEarlyDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("earlyCount").toString()));
                        if (isPersonal == IsEnum.YES.getValue()) {
                            if ((int) attendanceEmpRecordMap.get("earlyCount") > hrmAttendanceRule.getEarlyMinutesOrCounts() && hrmAttendanceRule.getEarlyMinutesOrCounts() > 0) {
                                earlyMoney = hrmAttendanceRule.getLateDeductMoney().multiply(new BigDecimal(hrmAttendanceRule.getEarlyMinutesOrCounts().toString())).setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    } else if (hrmAttendanceRule.getEarlyRuleMethod() == THREE) {
                        earlyMoney = hrmAttendanceRule.getEarlyDeductMoney();
                    }
                    BigDecimal absenteeismMoney = hrmAttendanceRule.getAbsenteeismDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("absenteeismDays").toString())).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal misscardMoney = hrmAttendanceRule.getMisscardDeductMoney().multiply(new BigDecimal(attendanceEmpRecordMap.get("misscardCount").toString())).setScale(2, RoundingMode.HALF_UP);
                    empAttendanceMap.put(1, attendanceEmpRecordMap.get("normalDays").toString());
                    empAttendanceMap.put(180101, "0");
                    empAttendanceMap.put(190101, lateMoney.toString());
                    empAttendanceMap.put(190102, earlyMoney.toString());
                    empAttendanceMap.put(190103, absenteeismMoney.toString());
                    empAttendanceMap.put(190104, "0");
                    empAttendanceMap.put(190105, misscardMoney.toString());
                    empAttendanceMap.put(190106, "0");
                    attendanceDataMap.put(jobNumber, empAttendanceMap);
                }
            }

        }
        //附加扣除项map
        Map<String, Map<Integer, String>> additionalDeductionDataMap;
        try {
            additionalDeductionDataMap = resolveAdditionalDeductionData(additionalDeductionFile);
        } catch (Exception e) {
            throw new CrmException(HrmCodeEnum.ADDITIONAL_DEDUCTION_DATA_ERROR);
        }
        //截止上月个税累计map
        Map<String, Map<Integer, String>> cumulativeTaxOfLastMonthDataMap;
        try {
            cumulativeTaxOfLastMonthDataMap = resolveCumulativeTaxOfLastMonthData(cumulativeTaxOfLastMonthFile);
        } catch (Exception e) {
            throw new CrmException(HrmCodeEnum.CUMULATIVE_TAX_OF_LAST_MONTH_DATA_ERROR);
        }
        for (Map<String, Object> map : mapList) {
            //薪资项
            List<HrmSalaryOption> salaryOptionList = salaryOptionService.lambdaQuery().ne(HrmSalaryOption::getParentCode, 0).list();
            Map<Integer, List<HrmSalaryOption>> salaryOptionListMap = salaryOptionList.stream().collect(Collectors.groupingBy(HrmSalaryOption::getIsFixed));
            List<HrmSalaryOption> noFixedSalaryOptionList = salaryOptionListMap.get(0);
            Long employeeId = Convert.toLong(map.get("employeeId"));
            String jobNumber = (String) map.get("jobNumber");
            Optional<HrmSalaryMonthEmpRecord> salaryMonthEmpRecordOpt = salaryMonthEmpRecordService.lambdaQuery().eq(HrmSalaryMonthEmpRecord::getSRecordId, sRecordId)
                    .eq(HrmSalaryMonthEmpRecord::getEmployeeId, employeeId).oneOpt();
            HrmSalaryMonthEmpRecord salaryMonthEmpRecord;
            List<HrmSalaryMonthOptionValue> optionValueList;
            if (salaryMonthEmpRecordOpt.isPresent()) {
                salaryMonthEmpRecord = salaryMonthEmpRecordOpt.get();
                salaryMonthOptionValueService.lambdaUpdate().eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).remove();
                //获取非固定项
                List<HrmSalaryMonthOptionValue> noFixedOptionValueList = getNoFixedOptionValue(salaryMonthEmpRecord, noFixedSalaryOptionList, true);
                optionValueList = new ArrayList<>(noFixedOptionValueList);
            } else {
                salaryMonthEmpRecord = new HrmSalaryMonthEmpRecord();
                salaryMonthEmpRecord.setSRecordId(salaryMonthRecord.getSRecordId());
                salaryMonthEmpRecord.setEmployeeId(employeeId);
                salaryMonthEmpRecord.setActualWorkDay(new BigDecimal("21.75"));
                salaryMonthEmpRecord.setNeedWorkDay(new BigDecimal("21.75"));
                salaryMonthEmpRecord.setYear(year);
                salaryMonthEmpRecord.setMonth(month);
                salaryMonthEmpRecordService.save(salaryMonthEmpRecord);
                //获取非固定项
                List<HrmSalaryMonthOptionValue> noFixedOptionValueList = getNoFixedOptionValue(salaryMonthEmpRecord, noFixedSalaryOptionList, true);
                optionValueList = new ArrayList<>(noFixedOptionValueList);
            }

            //获取考勤数据
            try {
                if (attendanceFile != null || isSyncAttendanceData) {
                    Map<Integer, String> codeValueMap = attendanceDataMap.get(jobNumber);
                    salaryMonthEmpRecord.setActualWorkDay(new BigDecimal(codeValueMap.get(1)));
                    salaryMonthEmpRecordService.updateById(salaryMonthEmpRecord);
                    codeValueMap.remove(1);
                    //获取固定项
                    List<HrmSalaryMonthOptionValue> fixedOptionValueList = getFixedOptionValue(salaryMonthEmpRecord, codeValueMap);
                    optionValueList.addAll(fixedOptionValueList);
                } else {
                    if (!salaryMonthEmpRecordOpt.isPresent()) {
                        salaryMonthEmpRecord.setNeedWorkDay(new BigDecimal("21.75"));
                    }
                }
            } catch (Exception e) {
                throw new CrmException(HrmCodeEnum.ATTENDANCE_DATA_ERROR);
            }
            //获取社保项
            List<HrmSalaryMonthOptionValue> socialSecurityOption = getSocialSecurityOption(salaryMonthEmpRecord, isSyncInsuranceData);
            optionValueList.addAll(socialSecurityOption);
            //个税专项附加扣除累计数据
            Map<Integer, String> additionalDeductionData = additionalDeductionDataMap.get(jobNumber);
            if (additionalDeductionData != null) {
                salaryMonthOptionValueService.lambdaUpdate().in(HrmSalaryMonthOptionValue::getCode, Arrays.asList(
                                260101, 260102, 260103, 260104, 260105))
                        .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).remove();
                additionalDeductionData.forEach((code, value) -> {
                    HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
                    salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
                    salaryMonthOptionValue.setCode(code);
                    salaryMonthOptionValue.setValue(value);
                    optionValueList.add(salaryMonthOptionValue);
                });
            }
            salaryMonthOptionValueService.saveBatch(optionValueList);
            Map<Integer, String> cumulativeTaxOfLastMonthData = cumulativeTaxOfLastMonthDataMap.get(jobNumber);
            List<HrmSalaryMonthOptionValue> salaryMonthOptionValues = computeSalary(salaryMonthEmpRecord, cumulativeTaxOfLastMonthData);
            salaryMonthOptionValueService.saveBatch(salaryMonthOptionValues);
            salaryMonthEmpRecordService.updateById(salaryMonthEmpRecord);
        }
        Map<String, Object> countMap = salaryMonthRecordMapper.queryMonthSalaryCount(salaryMonthRecord.getSRecordId());
        BeanUtil.fillBeanWithMap(countMap, salaryMonthRecord, true);
        updateById(salaryMonthRecord);
        salaryActionRecordService.computeSalaryDataLog(salaryMonthRecord);
    }

    private Map<String, Map<Integer, String>> resolveCumulativeTaxOfLastMonthData(MultipartFile cumulativeTaxOfLastMonthFile) throws Exception {
        Map<String, Map<Integer, String>> jobNumberMap = new HashMap<>();
        if (cumulativeTaxOfLastMonthFile != null) {
            Map<Integer, Integer> indexCodeMap = new HashMap<>();
            indexCodeMap.put(4, 250101);
            indexCodeMap.put(5, 250102);
            indexCodeMap.put(6, 250103);
            indexCodeMap.put(7, 250105);
            ExcelReader reader = ExcelUtil.getReader(cumulativeTaxOfLastMonthFile.getInputStream());
            List<List<Object>> read = reader.read();
            for (int i = TWO; i < read.size(); i++) {
                List<Object> row = read.get(i);
                String jobNumber = row.get(2).toString();
                Map<Integer, String> codeValueMap = new HashMap<>();
                indexCodeMap.forEach((k, v) -> {
                    if (ObjectUtil.isNotEmpty(row.get(k))) {
                        codeValueMap.put(v, row.get(k).toString());
                    } else {
                        codeValueMap.put(v, "0");
                    }
                });
                jobNumberMap.put(jobNumber, codeValueMap);
            }

        }
        return jobNumberMap;
    }

    /**
     * 解析excel附加扣除项
     *
     * @param additionalDeductionFile
     * @return
     */
    private Map<String, Map<Integer, String>> resolveAdditionalDeductionData(MultipartFile additionalDeductionFile) throws Exception {
        Map<String, Map<Integer, String>> jobNumberMap = new HashMap<>();
        if (additionalDeductionFile != null) {
            Map<Integer, Integer> indexCodeMap = new HashMap<>();
            indexCodeMap.put(4, 260101);
            indexCodeMap.put(5, 260102);
            indexCodeMap.put(6, 260103);
            indexCodeMap.put(7, 260104);
            indexCodeMap.put(8, 260105);
            ExcelReader reader = ExcelUtil.getReader(additionalDeductionFile.getInputStream());
            List<List<Object>> read = reader.read();
            for (int i = TWO; i < read.size(); i++) {
                List<Object> row = read.get(i);
                String jobNumber = row.get(2).toString();
                Map<Integer, String> codeValueMap = new HashMap<>();
                indexCodeMap.forEach((k, v) -> {
                    if (ObjectUtil.isNotEmpty(row.get(k))) {
                        codeValueMap.put(v, row.get(k).toString());
                    } else {
                        codeValueMap.put(v, "0");
                    }
                });
                jobNumberMap.put(jobNumber, codeValueMap);
            }
        }
        return jobNumberMap;
    }

    /**
     * 当月生成多次薪资,需要删除历史生成的薪资
     *
     * @param sRecordId 薪资记录id
     */
    private void deleteSalaryRecord(Long sRecordId) {
        List<Long> sEmpRecordIds = salaryMonthEmpRecordService.lambdaQuery().select(HrmSalaryMonthEmpRecord::getSEmpRecordId)
                .eq(HrmSalaryMonthEmpRecord::getSRecordId, sRecordId).list()
                .stream().map(HrmSalaryMonthEmpRecord::getSEmpRecordId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(sEmpRecordIds)) {
            salaryMonthOptionValueService.lambdaUpdate().in(HrmSalaryMonthOptionValue::getSEmpRecordId, sEmpRecordIds).remove();
            salaryMonthEmpRecordService.lambdaUpdate().in(HrmSalaryMonthEmpRecord::getSEmpRecordId, sEmpRecordIds).remove();
        }
        //TODO 删除审核记录
    }

    @Autowired
    private IHrmSalaryArchivesService salaryArchivesService;


    /**
     * 获取非固定性value(除了社保项)
     * 90101 个人社保
     * 90102  个人公积金
     * 100101  企业社保
     * 110101  企业公积金
     *
     * @return
     */
    private List<HrmSalaryMonthOptionValue> getNoFixedOptionValue(HrmSalaryMonthEmpRecord salaryMonthEmpRecord, List<HrmSalaryOption> noFixedSalaryOptionList, boolean isNew) {
        //移除社保公积金项
        noFixedSalaryOptionList.removeIf(salaryOption ->
                salaryOption.getCode().equals(100101) || salaryOption.getCode().equals(100102)
                        || salaryOption.getCode().equals(110101) || salaryOption.getCode().equals(120101)
        );
        List<HrmSalaryMonthOptionValue> noFixedOptionValueList = new ArrayList<>();
        if (isNew) {
            List<HrmSalaryArchivesOption> archivesOptionList = salaryArchivesService.querySalaryArchivesOption(salaryMonthEmpRecord.getEmployeeId(), salaryMonthEmpRecord.getYear(), salaryMonthEmpRecord.getMonth());
            Map<Integer, String> optionValueCodeMap = archivesOptionList.stream().collect(Collectors.toMap(HrmSalaryArchivesOption::getCode, HrmSalaryArchivesOption::getValue));
            BigDecimal all = new BigDecimal(0);
            if (CollUtil.isNotEmpty(noFixedSalaryOptionList)) {
                for (HrmSalaryOption salaryOption : noFixedSalaryOptionList) {
                    String value = "0";
                    if (StrUtil.isNotEmpty(optionValueCodeMap.get(salaryOption.getCode()))) {
                        value = optionValueCodeMap.get(salaryOption.getCode());
                    }
                    HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
                    salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
                    salaryMonthOptionValue.setCode(salaryOption.getCode());
                    salaryMonthOptionValue.setValue(value);
                    all = all.add(new BigDecimal(value));
                    noFixedOptionValueList.add(salaryMonthOptionValue);
                }
            }
            // 未设定薪资档案
            if (all.compareTo(BigDecimal.ZERO) == 0) {
                DateTime date = DateUtil.offsetMonth(DateUtil.parse(salaryMonthEmpRecord.getYear() + "-" + salaryMonthEmpRecord.getMonth(), "yy-MM"), -1);
                Optional<HrmSalaryMonthEmpRecord> salaryMonthEmpRecordOpt = salaryMonthEmpRecordService.lambdaQuery()
                        .eq(HrmSalaryMonthEmpRecord::getYear, date.year())
                        .eq(HrmSalaryMonthEmpRecord::getMonth, date.month() + 1).eq(HrmSalaryMonthEmpRecord::getEmployeeId, salaryMonthEmpRecord.getEmployeeId()).oneOpt();
                if (salaryMonthEmpRecordOpt.isPresent()) {
                    noFixedOptionValueList.clear();
                    List<HrmSalaryMonthOptionValue> oldOptionValueList = salaryMonthOptionValueService.lambdaQuery()
                            .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecordOpt.get().getSEmpRecordId()).list();
                    Map<Integer, String> oldValueCodeMap = oldOptionValueList.stream().collect(Collectors.toMap(HrmSalaryMonthOptionValue::getCode, HrmSalaryMonthOptionValue::getValue, (k1, k2) -> k1));
                    noFixedSalaryOptionList.forEach(salaryOption -> {
                        String value = "0";
                        if (StrUtil.isNotEmpty(oldValueCodeMap.get(salaryOption.getCode()))) {
                            value = oldValueCodeMap.get(salaryOption.getCode());
                        }
                        HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
                        salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
                        salaryMonthOptionValue.setCode(salaryOption.getCode());
                        salaryMonthOptionValue.setValue(value);
                        noFixedOptionValueList.add(salaryMonthOptionValue);
                    });
                }
            }
        } else {
            List<HrmSalaryMonthOptionValue> oldOptionValueList = salaryMonthOptionValueService.lambdaQuery()
                    .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).list();
            Set<Integer> oldCodeSet = new HashSet<>();
            oldOptionValueList.forEach(oldOptionValue -> {
                oldCodeSet.add(oldOptionValue.getCode());
            });
            noFixedSalaryOptionList.forEach(salaryOption -> {
                if (!oldCodeSet.contains(salaryOption.getCode())) {
                    HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
                    salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
                    salaryMonthOptionValue.setCode(salaryOption.getCode());
                    salaryMonthOptionValue.setValue("0");
                    noFixedOptionValueList.add(salaryMonthOptionValue);
                }
            });
        }
        return noFixedOptionValueList;
    }

    /**
     * 获取固定薪资项
     *
     * @param salaryMonthEmpRecord
     * @param codeValueMap
     * @return
     */
    private List<HrmSalaryMonthOptionValue> getFixedOptionValue(HrmSalaryMonthEmpRecord salaryMonthEmpRecord, Map<Integer, String> codeValueMap) {
        salaryMonthOptionValueService.lambdaUpdate().in(HrmSalaryMonthOptionValue::getCode, Arrays.asList(180101, 190101, 190102, 190103, 190104, 190105, 190106, 200101))
                .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).remove();
        List<HrmSalaryMonthOptionValue> fixedOptionValueList = new ArrayList<>();
        //考勤扣款合计
        BigDecimal attendanceDeductionTotal = new BigDecimal(0);
        for (Integer code : codeValueMap.keySet()) {
            String value = codeValueMap.get(code);
            if (code != 180101) {
                //除去加班工资项
                attendanceDeductionTotal = attendanceDeductionTotal.add(new BigDecimal(value));
            }
            HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
            salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
            salaryMonthOptionValue.setCode(code);
            salaryMonthOptionValue.setValue(value);
            fixedOptionValueList.add(salaryMonthOptionValue);
        }
        //考勤扣款合计
        HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
        salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
        salaryMonthOptionValue.setCode(200101);
        salaryMonthOptionValue.setValue(attendanceDeductionTotal.toString());
        fixedOptionValueList.add(salaryMonthOptionValue);
        return fixedOptionValueList;
    }


    /**
     * 获取社保薪资项
     *
     * @param salaryMonthEmpRecord 100101 个人社保
     *                             100102  个人公积金
     *                             110101  企业社保
     *                             120101  企业公积金
     */
    public List<HrmSalaryMonthOptionValue> getSocialSecurityOption(HrmSalaryMonthEmpRecord salaryMonthEmpRecord, Boolean isSyncInsuranceData) {
        HrmSalaryConfig salaryConfig = salaryConfigService.getOne(Wrappers.emptyWrapper());
        Map<Integer, String> socialSecurityOptionMap = new HashMap<>();
        List<HrmSalaryMonthOptionValue> salaryMonthOptionValueList = new ArrayList<>();
        if (!isSyncInsuranceData) {
            List<HrmSalaryMonthOptionValue> socialSecurityOptions = salaryMonthOptionValueService.lambdaQuery().in(HrmSalaryMonthOptionValue::getCode, Arrays.asList(100101, 100102, 110101, 120101))
                    .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).list();
            if (CollUtil.isNotEmpty(socialSecurityOptions)) {
                return salaryMonthOptionValueList;
            } else {
                socialSecurityOptionMap.put(100101, "0");
                socialSecurityOptionMap.put(100102, "0");
                socialSecurityOptionMap.put(110101, "0");
                socialSecurityOptionMap.put(120101, "0");
            }
        } else {
            salaryMonthOptionValueService.lambdaUpdate().in(HrmSalaryMonthOptionValue::getCode, Arrays.asList(100101, 100102, 110101, 120101))
                    .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, salaryMonthEmpRecord.getSEmpRecordId()).remove();
            Integer socialSecurityMonthType = salaryConfig.getSocialSecurityMonthType();
            DateTime date = DateUtil.parse(salaryMonthEmpRecord.getYear() + "-" + salaryMonthEmpRecord.getMonth(), "yy-MM");
            if (socialSecurityMonthType == 0) {
                date = DateUtil.offsetMonth(DateUtil.parse(salaryMonthEmpRecord.getYear() + "-" + salaryMonthEmpRecord.getMonth(), "yy-MM"), -1);
            } else if (socialSecurityMonthType == TWO) {
                date = DateUtil.offsetMonth(DateUtil.parse(salaryMonthEmpRecord.getYear() + "-" + salaryMonthEmpRecord.getMonth(), "yy-MM"), 1);
            }
            Optional<HrmInsuranceMonthEmpRecord> salaryMonthEmpRecordOpt = insuranceMonthEmpRecordService.lambdaQuery()
                    .eq(HrmInsuranceMonthEmpRecord::getYear, date.year())
                    .eq(HrmInsuranceMonthEmpRecord::getMonth, date.month() + 1)
                    .eq(HrmInsuranceMonthEmpRecord::getEmployeeId, salaryMonthEmpRecord.getEmployeeId())
                    .eq(HrmInsuranceMonthEmpRecord::getStatus, 1)
                    .oneOpt();
            if (salaryMonthEmpRecordOpt.isPresent()) {
                HrmInsuranceMonthEmpRecord insuranceMonthEmpRecord = salaryMonthEmpRecordOpt.get();
                socialSecurityOptionMap.put(100101, insuranceMonthEmpRecord.getPersonalInsuranceAmount() == null ? "0" : insuranceMonthEmpRecord.getPersonalInsuranceAmount().toString());
                socialSecurityOptionMap.put(100102, insuranceMonthEmpRecord.getPersonalProvidentFundAmount() == null ? "0" : insuranceMonthEmpRecord.getPersonalProvidentFundAmount().toString());
                socialSecurityOptionMap.put(110101, insuranceMonthEmpRecord.getCorporateInsuranceAmount() == null ? "0" : insuranceMonthEmpRecord.getCorporateInsuranceAmount().toString());
                socialSecurityOptionMap.put(120101, insuranceMonthEmpRecord.getCorporateProvidentFundAmount() == null ? "0" : insuranceMonthEmpRecord.getCorporateProvidentFundAmount().toString());
            } else {
                socialSecurityOptionMap.put(100101, "0");
                socialSecurityOptionMap.put(100102, "0");
                socialSecurityOptionMap.put(110101, "0");
                socialSecurityOptionMap.put(120101, "0");
            }
        }
        socialSecurityOptionMap.forEach((code, value) -> {
            HrmSalaryMonthOptionValue salaryMonthOptionValue = new HrmSalaryMonthOptionValue();
            salaryMonthOptionValue.setSEmpRecordId(salaryMonthEmpRecord.getSEmpRecordId());
            salaryMonthOptionValue.setCode(code);
            salaryMonthOptionValue.setValue(value);
            salaryMonthOptionValueList.add(salaryMonthOptionValue);
        });
        return salaryMonthOptionValueList;
    }

    /**
     * 解析考勤数据
     */
    public Map<String, Map<Integer, String>> resolveAttendanceData(MultipartFile multipartFile) throws Exception {
        Map<Integer, Integer> indexCodeMap = new HashMap<>();
        indexCodeMap.put(4, 180101);
        indexCodeMap.put(5, 190101);
        indexCodeMap.put(6, 190102);
        indexCodeMap.put(7, 190103);
        indexCodeMap.put(8, 190104);
        indexCodeMap.put(9, 190105);
        indexCodeMap.put(10, 190106);
        indexCodeMap.put(11, 1);
        Map<String, Map<Integer, String>> jobNumberMap = new HashMap<>();
        if (multipartFile != null) {
            ExcelReader reader = null;
            reader = ExcelUtil.getReader(multipartFile.getInputStream());
            List<List<Object>> read = reader.read();
            for (int i = TWO; i < read.size(); i++) {
                List<Object> row = read.get(i);
                String jobNumber = row.get(2).toString();
                Map<Integer, String> codeValueMap = new HashMap<>();
                indexCodeMap.forEach((k, v) -> {
                    if (ObjectUtil.isNotEmpty(row.get(k))) {
                        codeValueMap.put(v, row.get(k).toString());
                    } else {
                        codeValueMap.put(v, "0");
                    }
                });
                jobNumberMap.put(jobNumber, codeValueMap);
            }
        } else {
            List<Map<String, Object>> mapList = queryPaySalaryEmployeeListByType(1, null);
            for (Map<String, Object> map : mapList) {
                Map<Integer, String> codeValueMap = new HashMap<>();
                indexCodeMap.forEach((k, v) -> {
                    codeValueMap.put(v, "0");
                });
                jobNumberMap.put((String) map.get("jobNumber"), codeValueMap);
            }
        }
        return jobNumberMap;
    }


    /**
     * 计算薪资
     *
     * @param salaryMonthEmpRecord
     * @param cumulativeTaxOfLastMonthData 上个月的累计税数据
     * @return
     */
    public List<HrmSalaryMonthOptionValue> computeSalary(HrmSalaryMonthEmpRecord salaryMonthEmpRecord,
                                                         Map<Integer, String> cumulativeTaxOfLastMonthData) {
        //员工计税规则
        HrmSalaryTaxRule taxRule = salaryGroupService.queryEmployeeTaxRule(salaryMonthEmpRecord.getEmployeeId());
        return taxRuleContext.getTaxRuleStrategyOptionValue(salaryMonthEmpRecord, taxRule, cumulativeTaxOfLastMonthData);
    }


    @Override
    public BasePage<QuerySalaryPageListVO> querySalaryPageList(QuerySalaryPageListBO querySalaryPageListBO) {
        Collection<Long> dataAuthEmployeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.SALARY_MENU_ID);
        BasePage<QuerySalaryPageListVO> page = salaryMonthEmpRecordMapper.querySalaryPageList(querySalaryPageListBO.parse(), querySalaryPageListBO, dataAuthEmployeeIds);
        page.getList().forEach(querySalaryPageListVO -> {
            List<ComputeSalaryDTO> list = salaryMonthOptionValueService.querySalaryOptionValue(querySalaryPageListVO.getSEmpRecordId());
            List<QuerySalaryPageListVO.SalaryValue> salaryValues = TransferUtil.transferList(list, QuerySalaryPageListVO.SalaryValue.class);
            salaryValues.add(new QuerySalaryPageListVO.SalaryValue(0L, 1, querySalaryPageListVO.getNeedWorkDay().toString(), 1, "计薪天数"));
            salaryValues.add(new QuerySalaryPageListVO.SalaryValue(0L, 2, querySalaryPageListVO.getActualWorkDay().toString(), 1, "实际计薪天数"));
            querySalaryPageListVO.setSalary(salaryValues);
        });
        List<Long> sEmpRecordIds = salaryMonthEmpRecordMapper.querysEmpRecordIds(querySalaryPageListBO, dataAuthEmployeeIds);
        if (sEmpRecordIds.size() == 0) {
            sEmpRecordIds.add(0L);
        }
        List<Map<String, Object>> salaryOption = querySalaryByIds(sEmpRecordIds, querySalaryPageListBO.getSRecordId());
        JSONObject json = new JSONObject();
        json.put("salaryOption", salaryOption);
        page.setExtraData(json);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationResult updateSalary(List<UpdateSalaryBO> updateSalaryBOList) {
        List<HrmSalaryMonthOptionValue> salaryMonthOptionValueList = new ArrayList<>();
        updateSalaryBOList.forEach(updateSalaryBO -> {
            Long sEmpRecordId = updateSalaryBO.getSEmpRecordId();
            HrmSalaryMonthEmpRecord salaryMonthEmpRecord = salaryMonthEmpRecordService.getById(sEmpRecordId);
            Map<Integer, String> map = updateSalaryBO.getSalaryValues();
            map.forEach((code, value) -> {
                salaryMonthOptionValueService.lambdaUpdate().set(HrmSalaryMonthOptionValue::getValue, value)
                        .eq(HrmSalaryMonthOptionValue::getCode, code)
                        .eq(HrmSalaryMonthOptionValue::getSEmpRecordId, sEmpRecordId)
                        .update();
            });
            //重新计算薪资
            List<HrmSalaryMonthOptionValue> salaryMonthOptionValues = computeSalary(salaryMonthEmpRecord, null);
            salaryMonthOptionValueList.addAll(salaryMonthOptionValues);
        });
        salaryMonthOptionValueService.saveBatch(salaryMonthOptionValueList);
        HrmSalaryMonthRecord salaryMonthRecord = queryLastSalaryMonthRecord();
        Map<String, Object> countMap = salaryMonthRecordMapper.queryMonthSalaryCount(salaryMonthRecord.getSRecordId());
        BeanUtil.fillBeanWithMap(countMap, salaryMonthRecord, true);
        updateById(salaryMonthRecord);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(salaryMonthRecord.getSRecordId(), salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle());
        operationLog.setOperationInfo("编辑薪资报表：" + salaryMonthRecord.getYear() + "-" + salaryMonthRecord.getTitle());
        return null;
    }

    @Override
    public HrmSalaryMonthRecord computeSalaryCount(HrmSalaryMonthRecord salaryMonthRecord) {
        //保存薪资项表头
        List<Long> empRecordIds = salaryMonthRecordMapper.queryDeleteEmpRecordIds(salaryMonthRecord.getSRecordId());
        if (CollUtil.isNotEmpty(empRecordIds)) {
            salaryMonthOptionValueService.lambdaUpdate().in(HrmSalaryMonthOptionValue::getSEmpRecordId, empRecordIds).remove();
            salaryMonthEmpRecordService.lambdaUpdate().in(HrmSalaryMonthEmpRecord::getSEmpRecordId, empRecordIds).remove();
        }
        Integer num = salaryMonthEmpRecordService.lambdaQuery().eq(HrmSalaryMonthEmpRecord::getSRecordId, salaryMonthRecord.getSRecordId()).count().intValue();
        salaryMonthRecord.setNum(num);
        List<SalaryOptionHeadVO> salaryOptionHeadVOList = querySalaryOptionHead();
        salaryMonthRecord.setOptionHead(JSON.toJSONString(salaryOptionHeadVOList));
        Map<String, Object> countMap = salaryMonthRecordMapper.queryMonthSalaryCount(salaryMonthRecord.getSRecordId());
        return BeanUtil.fillBeanWithMap(countMap, salaryMonthRecord, true);
    }

    @Override
    public BasePage<QueryHistorySalaryListVO> queryHistorySalaryList(QueryHistorySalaryListBO queryHistorySalaryListBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.SALARY_MENU_ID);
        if (CollUtil.isEmpty(employeeIds)) {
            return new BasePage<>();
        }
        BasePage<QueryHistorySalaryListVO> salaryList = salaryMonthRecordMapper.queryHistorySalaryList(queryHistorySalaryListBO.parse(), queryHistorySalaryListBO, employeeIds);
        List<QueryHistorySalaryListVO> list = salaryList.getList();
        if (CollectionUtil.isNotEmpty(list)) {
            //添加语言包key
            Map<String, String> keyMap = new HashMap<>();
            for (QueryHistorySalaryListVO listVO : list) {
                keyMap.put("title_resourceKey", "{" + HrmLanguageEnum.parseKey(listVO.getMonth()) + "}" + "{" + HrmLanguageEnum.SALARY_REPORT.getKey() + "}");
                listVO.setLanguageKeyMap(keyMap);
            }
        }
        return salaryList;
    }

    @Override
    public QueryHistorySalaryDetailVO queryHistorySalaryDetail(QueryHistorySalaryDetailBO queryHistorySalaryDetailBO) {
        HrmSalaryMonthRecord monthRecord = getById(queryHistorySalaryDetailBO.getSRecordId());
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.SALARY_MENU_ID);
        QueryHistorySalaryDetailVO historySalaryDetailVO = salaryMonthRecordMapper.queryHistorySalaryDetail(queryHistorySalaryDetailBO.getSRecordId(), employeeIds);
        List<SalaryOptionHeadVO> salaryOptionHeadVOList = JSON.parseArray(monthRecord.getOptionHead(), SalaryOptionHeadVO.class);
        BasePage<QuerySalaryPageListVO> page = salaryMonthEmpRecordMapper.querySalaryPageListByRecordId(queryHistorySalaryDetailBO.parse(), queryHistorySalaryDetailBO, employeeIds);
        page.getList().forEach(querySalaryPageListVO -> {
            List<ComputeSalaryDTO> list = salaryMonthOptionValueService.querySalaryOptionValue(querySalaryPageListVO.getSEmpRecordId());
            List<QuerySalaryPageListVO.SalaryValue> salaryValues = TransferUtil.transferList(list, QuerySalaryPageListVO.SalaryValue.class);
            salaryValues.add(new QuerySalaryPageListVO.SalaryValue(0L, 1, querySalaryPageListVO.getNeedWorkDay().toString(), 1, "计薪天数"));
            salaryValues.add(new QuerySalaryPageListVO.SalaryValue(0L, 2, querySalaryPageListVO.getActualWorkDay().toString(), 1, "实际计薪天数"));
            querySalaryPageListVO.setSalary(salaryValues);
        });
        for (SalaryOptionHeadVO headVO : salaryOptionHeadVOList) {
            //添加语言包key
            headVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.", StrUtil.toString(headVO.getCode())));
        }
        historySalaryDetailVO.setSalaryOptionHeadList(salaryOptionHeadVOList);
        historySalaryDetailVO.setPageData(page);
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("title_resourceKey", "{" + HrmLanguageEnum.parseKey(historySalaryDetailVO.getMonth()) + "}" + "{" + HrmLanguageEnum.SALARY_REPORT.getKey() + "}");
        historySalaryDetailVO.setLanguageKeyMap(keyMap);
        return historySalaryDetailVO;
    }


    @Override
    public OperationLog addNextMonthSalary() {
        //查询薪资上月记录,如果有就往后推一个月,如果没有就去薪资配置计薪月
        HrmSalaryMonthRecord lastSalaryMonthRecord = lambdaQuery().orderByDesc(HrmSalaryMonthRecord::getCreateTime).one();
        HrmSalaryConfig salaryConfig = salaryConfigService.getOne(Wrappers.emptyWrapper());
        LocalDate date = LocalDateTimeUtil.offset(LocalDateTimeUtil.of(DateUtil.parse(lastSalaryMonthRecord.getYear() + "-" + lastSalaryMonthRecord.getMonth(), "yy-MM")), 1, ChronoUnit.MONTHS).toLocalDate();
        int month = date.getMonthValue();
        int year = date.getYear();
        LocalDate startTime = LocalDate.of(year, month, salaryConfig.getSalaryCycleStartDay());
        LocalDate endTime;
        if (salaryConfig.getSalaryCycleStartDay() > 1) {
            LocalDate dateTime = LocalDateTimeUtil.offset(startTime.atStartOfDay(), 1, ChronoUnit.MONTHS).toLocalDate();
            int nextMonth = dateTime.getMonthValue();
            endTime = LocalDate.of(year, nextMonth, salaryConfig.getSalaryCycleEndDay());
        } else {
            endTime = startTime.with(TemporalAdjusters.lastDayOfMonth());
        }
        lastSalaryMonthRecord.setCheckStatus(SalaryRecordStatus.HISTORY.getValue());
        computeSalaryCount(lastSalaryMonthRecord);
        updateById(lastSalaryMonthRecord);
        HrmSalaryMonthRecord salaryMonthRecord = new HrmSalaryMonthRecord();
        salaryMonthRecord.setTitle(HrmLanguageEnum.parseName(month) + HrmLanguageEnum.SALARY_REPORT.getName());
        salaryMonthRecord.setYear(year);
        salaryMonthRecord.setMonth(month);
        salaryMonthRecord.setStartTime(startTime);
        salaryMonthRecord.setEndTime(endTime);
        salaryMonthRecord.setNum(queryPaySalaryEmployeeListByType(1, null).size());
        save(salaryMonthRecord);
        salaryActionRecordService.addNextMonthSalaryLog(salaryMonthRecord);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(salaryMonthRecord.getSRecordId(), year + "-" + salaryMonthRecord.getTitle());
        operationLog.setOperationInfo("新建薪资报表：" + year + "-" + salaryMonthRecord.getTitle());
        return operationLog;
    }

    @Override
    public Map<Integer, Long> queryEmployeeChangeNum() {
        HrmSalaryMonthRecord salaryMonthRecord = queryLastSalaryMonthRecord();
        Collection<Long> dataAuthEmployeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.SALARY_MENU_ID);
        Collection<Long> employeeIds = salaryMonthEmpRecordService.queryEmployeeIds(salaryMonthRecord.getSRecordId(), dataAuthEmployeeIds);
        Map<Integer, Long> collect = new TreeMap<>();
        for (AbnormalChangeType value : AbnormalChangeType.values()) {
            List<HrmEmployeeAbnormalChangeRecord> changeRecordList = abnormalChangeRecordService.queryListByDate(salaryMonthRecord.getStartTime(), salaryMonthRecord.getEndTime(), employeeIds, value.getValue());
            if (CollUtil.isNotEmpty(changeRecordList)) {
                collect.put(value.getValue(), (long) changeRecordList.size());
            } else {
                collect.put(value.getValue(), 0L);
            }
        }
        collect.put(0, (long) employeeIds.size());
        return collect;
    }

    /**
     * 查询最新薪资月记录
     *
     * @return
     */
    @Override
    public HrmSalaryMonthRecord queryLastSalaryMonthRecord() {
        return lambdaQuery().orderByDesc(HrmSalaryMonthRecord::getCreateTime).one();
    }

    @Override
    public void updateCheckStatus(Long sRecordId, Integer checkStatus) {
        lambdaUpdate().set(HrmSalaryMonthRecord::getCheckStatus, checkStatus).eq(HrmSalaryMonthRecord::getSRecordId, sRecordId).update();
    }

    @Override
    public List<Map<String, Object>> queryNoPaySalaryEmployee() {
        List<Map<String, Object>> list = queryPaySalaryEmployeeListByType(0, null);
        List<Long> employeeIds = list.stream().map(map -> Convert.toLong(map.get("employeeId"))).collect(Collectors.toList());
        if (CollUtil.isEmpty(employeeIds)) {
            return new ArrayList<>();
        }
        return employeeMapper.queryNoPaySalaryEmployee(employeeIds);
    }

    @Override
    public List<Map<String, Object>> querySalaryOptionCount(String sRecordId) {
        return salaryMonthRecordMapper.querySalaryOptionCount(sRecordId);
    }

    private List<Map<String, Object>> querySalaryByIds(List<Long> sEmpRecordIds, Long sRecordId) {
        return salaryMonthRecordMapper.querySalaryByIds(sEmpRecordIds, sRecordId);
    }

    @Override
    public OperationLog deleteSalary(Long sRecordId) {
        int count = (int) count();
        if (count == 1) {
            throw new CrmException(HrmCodeEnum.SALARY_CANNOT_BE_DELETED);
        }
        HrmSalaryMonthRecord monthRecord = getById(sRecordId);
        removeById(sRecordId);
        deleteSalaryRecord(sRecordId);
        HrmSalaryMonthRecord salaryMonthRecord = queryLastSalaryMonthRecord();
        lambdaUpdate().set(HrmSalaryMonthRecord::getCheckStatus, SalaryRecordStatus.COMPUTE.getValue())
                .set(HrmSalaryMonthRecord::getExamineRecordId, null).eq(HrmSalaryMonthRecord::getSRecordId, salaryMonthRecord.getSRecordId()).update();
        salaryActionRecordService.deleteSalaryLog(monthRecord);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(monthRecord.getSRecordId(), monthRecord.getYear() + "-" + monthRecord.getTitle());
        operationLog.setOperationInfo("删除薪资报表：" + monthRecord.getYear() + "-" + monthRecord.getTitle());
        return operationLog;
    }

}
