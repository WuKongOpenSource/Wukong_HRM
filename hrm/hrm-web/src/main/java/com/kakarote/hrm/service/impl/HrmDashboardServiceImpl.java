package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aliyun.openservices.shade.com.google.common.collect.Range;
import com.aliyun.openservices.shade.com.google.common.collect.TreeRangeMap;
import com.kakarote.core.utils.TransferUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.constant.achievement.AppraisalStatus;
import com.kakarote.hrm.entity.BO.QueryNotesByTimeBO;
import com.kakarote.hrm.entity.BO.QueryNotesStatusBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmAchievementEmployeeAppraisalMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.utils.AchievementUtil;
import com.kakarote.hrm.utils.EmployeeUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrmDashboardServiceImpl implements IHrmDashboardService {

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmEmployeeAbnormalChangeRecordService abnormalChangeRecordService;

    @Autowired
    private IHrmRecruitPostService recruitPostService;

    @Autowired
    private IHrmRecruitCandidateService recruitCandidateService;

    @Autowired
    private IHrmSalaryMonthRecordService salaryMonthRecordService;

    @Autowired
    private IHrmSalaryMonthEmpRecordService salaryMonthEmpRecordService;

    @Autowired
    private IHrmSalaryMonthOptionValueService salaryMonthOptionValueService;

    @Autowired
    private IHrmDeptService deptService;

    @Autowired
    private IHrmAchievementAppraisalService appraisalService;

    @Autowired
    private IHrmAchievementTableService achievementTableService;

    @Autowired
    private HrmAchievementEmployeeAppraisalMapper employeeAppraisalMapper;

    @Autowired
    private IHrmEmployeeContractService employeeContractService;

    @Autowired
    private IHrmNotesService notesService;

    @Autowired
    private IHrmRecruitCandidateService candidateService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Autowired
    private IHrmSalarySlipService salarySlipService;

    @Autowired
    private IHrmAttendanceClockService attendanceClockService;

    private static final String INDEX = "index";

    private static final String MANAGE = "manage";

    private static final String READ = "read";

    @Override
    public Map<Integer, Long> employeeSurvey() {
        Map<Integer, Long> collect = new TreeMap<>();
        //在职
        List<HrmEmployee> list = employeeService.lambdaQuery()
                .select(HrmEmployee::getStatus)
                .in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue(), EmployeeEntryStatus.TO_LEAVE.getValue())
                .eq(HrmEmployee::getIsDel, 0)
                .list();
        LocalDate now = LocalDate.now();
        for (AbnormalChangeType value : AbnormalChangeType.values()) {
            List<HrmEmployeeAbnormalChangeRecord> changeRecordList = abnormalChangeRecordService.queryListByDate1(now.getYear(), now.getMonthValue(), value.getValue(), null);
            if (CollUtil.isNotEmpty(changeRecordList)) {
                collect.put(value.getValue(), (long) changeRecordList.size());
            } else {
                collect.put(value.getValue(), 0L);
            }
        }
        collect.put(0, (long) list.size());
        //待入职
        List<Long> toInEmployeeIds = employeeService.queryToInByMonth(now.getYear(), now.getMonthValue());
        collect.put(5, (long) toInEmployeeIds.size());
        //待离职
        List<Long> toLeaveEmployeeIds = employeeService.queryToLeaveByMonth(now.getYear(), now.getMonthValue());
        collect.put(6, (long) toLeaveEmployeeIds.size());
        return collect;
    }

    @Override
    public RecruitSurveyCountVO recruitSurvey() {
        //正在招聘职位
        Integer postCount = recruitPostService.lambdaQuery().eq(HrmRecruitPost::getStatus, IsEnum.YES.getValue()).count().intValue();
        //评选中
        Integer chooseCount = recruitCandidateService.lambdaQuery().notIn(HrmRecruitCandidate::getStatus, CandidateStatusEnum.PENDING_ENTRY.getValue()
                , CandidateStatusEnum.OBSOLETE.getValue(), CandidateStatusEnum.HAVE_JOINED.getValue()).count().intValue();
        Integer pendingEntryCount = recruitCandidateService.lambdaQuery().eq(HrmRecruitCandidate::getStatus, CandidateStatusEnum.PENDING_ENTRY.getValue())
                .apply(" status_update_time between subdate(now(),interval 6 month ) and now() ").count().intValue();
        Integer haveJoinedCount = recruitCandidateService.lambdaQuery().eq(HrmRecruitCandidate::getStatus, CandidateStatusEnum.HAVE_JOINED.getValue())
                .apply(" entry_time between subdate(now(),interval 6 month ) and now() ").count().intValue();
        return new RecruitSurveyCountVO(postCount, chooseCount, pendingEntryCount, haveJoinedCount);
    }

    @Override
    public LastSalarySurveyVO lastSalarySurvey() {
        LastSalarySurveyVO lastSalarySurveyVO = new LastSalarySurveyVO();
        DateTime now = DateUtil.offsetMonth(DateUtil.date(), -1);
        Optional<HrmSalaryMonthRecord> salaryMonthRecordOpt = salaryMonthRecordService.lambdaQuery()
                .eq(HrmSalaryMonthRecord::getYear, now.year()).eq(HrmSalaryMonthRecord::getMonth, now.month() + 1)
                .eq(HrmSalaryMonthRecord::getCheckStatus, SalaryRecordStatus.HISTORY.getValue()).oneOpt();
        if (!salaryMonthRecordOpt.isPresent()) {
            return lastSalarySurveyVO;
        }
        HrmSalaryMonthRecord salaryMonthRecord = salaryMonthRecordOpt.get();
        List<HrmSalaryMonthEmpRecord> empRecordList = salaryMonthEmpRecordService.lambdaQuery().eq(HrmSalaryMonthEmpRecord::getSRecordId, salaryMonthRecord.getSRecordId())
                .list();
        lastSalarySurveyVO.setSRecordId(salaryMonthRecord.getSRecordId());
        lastSalarySurveyVO.setTotal(empRecordList.size());
        Map<Long, Long> recordDeptIdMap = new HashMap<>();
        empRecordList.forEach(empRecord -> {
            HrmEmployee employee = employeeService.getById(empRecord.getEmployeeId());
            if (employee.getDeptId() != null) {
                recordDeptIdMap.put(empRecord.getSEmpRecordId(), employee.getDeptId());
            }
        });
        List<Long> sEmpRecordIds = empRecordList.stream().map(HrmSalaryMonthEmpRecord::getSEmpRecordId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(sEmpRecordIds)) {
            List<HrmSalaryMonthOptionValue> valueList = salaryMonthOptionValueService.lambdaQuery().in(HrmSalaryMonthOptionValue::getSEmpRecordId, sEmpRecordIds)
                    .eq(HrmSalaryMonthOptionValue::getCode, 240101).list();
            Map<Long, BigDecimal> deptCountMap = new HashMap<>();
            BigDecimal totalSalary = new BigDecimal(0);
            for (HrmSalaryMonthOptionValue value : valueList) {
                totalSalary = totalSalary.add(new BigDecimal(value.getValue()));
                Long deptId = recordDeptIdMap.get(value.getSEmpRecordId());
                if (deptId == null) {
                    continue;
                }
                if (deptCountMap.containsKey(deptId)) {
                    BigDecimal bigDecimal = deptCountMap.get(deptId);
                    BigDecimal add = bigDecimal.add(new BigDecimal(value.getValue()));
                    deptCountMap.put(deptId, add);
                } else {
                    deptCountMap.put(deptId, new BigDecimal(value.getValue()));
                }
            }
            lastSalarySurveyVO.setTotalSalary(totalSalary);
            List<LastSalarySurveyVO.DeptProportion> deptProportionList = new ArrayList<>();
            BigDecimal noDeptSalary = totalSalary;
            BigDecimal noDeptProportion = BigDecimal.ONE;
            for (Long deptId : deptCountMap.keySet()) {
                HrmDept dept = deptService.getById(deptId);
                BigDecimal salary = deptCountMap.get(deptId);
                LastSalarySurveyVO.DeptProportion deptProportion = new LastSalarySurveyVO.DeptProportion();
                deptProportion.setDeptId(deptId);
                deptProportion.setDeptName(dept.getName());
                deptProportion.setTotalSalary(salary);
                noDeptSalary = noDeptSalary.subtract(salary);
                BigDecimal proportion;
                if (totalSalary.compareTo(BigDecimal.ZERO) == 0) {
                    proportion = new BigDecimal(0);
                } else {
                    proportion = salary.divide(totalSalary, 4, BigDecimal.ROUND_UP);
                }
                noDeptProportion = noDeptProportion.subtract(proportion);
                deptProportion.setProportion(proportion);
                deptProportionList.add(deptProportion);
            }
            if (noDeptProportion.compareTo(new BigDecimal(0)) > 0) {
                LastSalarySurveyVO.DeptProportion noDept = new LastSalarySurveyVO.DeptProportion();
                noDept.setDeptId(0L);
                noDept.setDeptName("无部门");
                noDept.setProportion(noDeptProportion);
                noDept.setTotalSalary(noDeptSalary);
                deptProportionList.add(noDept);
            }
            lastSalarySurveyVO.setDeptProportionList(deptProportionList);
        }
        return lastSalarySurveyVO;
    }

    @Override
    public Map<Integer, Integer> appraisalCountSurvey() {
        Map<Integer, Integer> map = new HashMap<>();
        LocalDate localDate = LocalDate.now();
        LocalDate years = localDate.minusYears(1);
        Integer count1 = appraisalService.lambdaQuery()
                .in(HrmAchievementAppraisal::getStatus, AppraisalStatus.FILLING_IN.getValue(), AppraisalStatus.UNDER_EVALUATION.getValue()
                        , AppraisalStatus.CONFIRMING.getValue())
                .or(wrapper -> {
                    wrapper.between(HrmAchievementAppraisal::getStartTime, years, localDate);
                    wrapper.between(HrmAchievementAppraisal::getEndTime, years, localDate);
                })
                .count().intValue();
        Integer count2 = appraisalService.lambdaQuery()
                .eq(HrmAchievementAppraisal::getStatus, AppraisalStatus.ARCHIVE.getValue())
                .or(wrapper -> {
                    wrapper.between(HrmAchievementAppraisal::getStartTime, years, localDate);
                    wrapper.between(HrmAchievementAppraisal::getEndTime, years, localDate);
                })
                .count().intValue();

        map.put(1, count1);
        map.put(2, count2);
        return map;
    }

    @Override
    public List<AppraisalSurveyVO> appraisalSurvey(String status) {
        List<HrmAchievementAppraisal> achievementAppraisals;
        String one = "1";
        if (one.equals(status)) {
            achievementAppraisals = appraisalService.lambdaQuery()
                    .in(HrmAchievementAppraisal::getStatus, AppraisalStatus.FILLING_IN.getValue(), AppraisalStatus.UNDER_EVALUATION.getValue()
                            , AppraisalStatus.CONFIRMING.getValue())
                    .apply("(start_time between subdate(now(),interval 1 year) and now() or end_time between subdate(now(),interval 1 year) and now() )").list();
        } else {
            achievementAppraisals = appraisalService.lambdaQuery()
                    .eq(HrmAchievementAppraisal::getStatus, AppraisalStatus.ARCHIVE.getValue())
                    .apply("(start_time between subdate(now(),interval 1 year) and now() or end_time between subdate(now(),interval 1 year) and now() )").list();
        }
        List<AppraisalSurveyVO> appraisalSurveyVOList = TransferUtil.transferList(achievementAppraisals, AppraisalSurveyVO.class);
        for (AppraisalSurveyVO appraisalSurveyVO : appraisalSurveyVOList) {
            HrmAchievementTable achievementTable = achievementTableService.getById(appraisalSurveyVO.getTableId());
            appraisalSurveyVO.setTableName(achievementTable.getTableName());
            appraisalSurveyVO.setAppraisalTime(AchievementUtil.appraisalTimeFormat(appraisalSurveyVO.getStartTime(), appraisalSurveyVO.getEndTime()));
            List<AppraisalSurveyVO.ScoreLevelsBean> scoreLevels = new ArrayList<>();
            List<Map<String, Object>> mapList = employeeAppraisalMapper.queryScoreLevels(appraisalSurveyVO.getAppraisalId());
            mapList.forEach(map -> {
                scoreLevels.add(BeanUtil.toBean(map, AppraisalSurveyVO.ScoreLevelsBean.class));
            });
            appraisalSurveyVO.setScoreLevels(scoreLevels);
        }
        return appraisalSurveyVOList;
    }

    @Override
    public DoRemindVO toDoRemind() {
        DoRemindVO doRemindVO = new DoRemindVO();
        LocalDate now = LocalDate.now();
        //待离职
        List<Long> toLeave = employeeService.queryToLeaveByMonth(now.getYear(), now.getMonthValue());
        List<Long> toExpireContract = employeeContractService.queryToExpireContractCount();
        List<Long> toCorrect = employeeService.queryToCorrectCount();
        List<Long> toIn = employeeService.queryToInByMonth(now.getYear(), now.getMonthValue());
        List<Long> toBirthday = employeeService.queryBirthdayEmp();
        HrmSalaryMonthRecord salaryMonthRecord = salaryMonthRecordService.queryLastSalaryMonthRecord();
        doRemindVO.setToSalaryExamine(0);
        doRemindVO.setToLeave(toLeave.size());
        doRemindVO.setToExpireContract(toExpireContract.size());
        doRemindVO.setToCorrect(toCorrect.size());
        doRemindVO.setToIn(toIn.size());
        doRemindVO.setToBirthday(toBirthday.size());
        return doRemindVO;
    }

    @Override
    public List<NotesVO> queryNotesByTime(QueryNotesByTimeBO queryNotesByTimeBO) {
        List<NotesVO> notesVOList = new ArrayList<>();
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        LocalDate time = queryNotesByTimeBO.getTime();
        List<HrmNotes> notesList = notesService.queryNoteListByTime(time, employeeIds);
        notesList.forEach(notes -> {
            NotesVO notesVO = new NotesVO(notes.getNotesId(), NotesType.NOTES.getValue(), notes.getContent(), null);
            notesVOList.add(notesVO);
        });

            List<HrmEmployee> birthdayList = employeeService.queryBirthdayListByTime(time, employeeIds);
            birthdayList.forEach(employee -> {
                String content = employee.getEmployeeName() + "生日";
                if (employee.getAge() != null) {
                    content = employee.getEmployeeName() + employee.getAge() + "岁生日";
                }
                NotesVO notesVO = new NotesVO(null, NotesType.BIRTHDAY.getValue(), content, employee.getEmployeeId());
                notesVOList.add(notesVO);
            });
            List<HrmEmployee> entryList = employeeService.queryEntryEmpListByTime(time, employeeIds);
            entryList.forEach(employee -> {
                String content = employee.getEmployeeName() + "入职日";
                NotesVO notesVO = new NotesVO(null, NotesType.ENTRY.getValue(), content, employee.getEmployeeId());
                notesVOList.add(notesVO);
            });
            List<HrmEmployee> becomeList = employeeService.queryBecomeEmpListByTime(time, employeeIds);
            becomeList.forEach(employee -> {
                String content = employee.getEmployeeName() + "转正日";
                NotesVO notesVO = new NotesVO(null, NotesType.BECOME.getValue(), content, employee.getEmployeeId());
                notesVOList.add(notesVO);
            });
            List<HrmEmployee> leaveList = employeeService.queryLeaveEmpListByTime(time, employeeIds);
            leaveList.forEach(employee -> {
                String content = employee.getEmployeeName() + "离职日";
                NotesVO notesVO = new NotesVO(null, NotesType.LEAVE.getValue(), content, employee.getEmployeeId());
                notesVOList.add(notesVO);
            });
            Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
            List<Map<String, Object>> recruitList = candidateService.queryRecruitListByTime(time, deptIds);
            recruitList.forEach(candidate -> {
                Date interviewTime = (Date) candidate.get("interviewTime");
                boolean isAm = DateUtil.isAM(interviewTime);
                String format = DateUtil.format(interviewTime, "HH:mm");
                String content = candidate.get("candidateName") + (isAm ? "上午" : "下午") + format + "面试";
                NotesVO notesVO = new NotesVO(null, NotesType.RECRUIT.getValue(), content, Convert.toLong(candidate.get("candidateId")));
                notesVOList.add(notesVO);
            });
        return notesVOList;
    }

    @Override
    public Set<String> queryNotesStatus(QueryNotesStatusBO queryNotesStatusBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        Set<String> notesStatusList = notesService.queryNoteStatusList(queryNotesStatusBO, employeeIds);
        Set<String> timeList = new TreeSet<>(notesStatusList);
            DateRange dateTimes = new DateRange(Timestamp.valueOf(queryNotesStatusBO.getStartTime().atStartOfDay()), Timestamp.valueOf(queryNotesStatusBO.getEndTime().atStartOfDay()), DateField.DAY_OF_YEAR);
            dateTimes.forEach(dateTime -> {
                List<HrmEmployee> birthdayList = employeeService.queryBirthdayListByTime(dateTime.toLocalDateTime().toLocalDate(), employeeIds);
                if (birthdayList.size() > 0) {
                    timeList.add(DateUtil.formatDate(dateTime));
                }
            });
            Set<String> entryTimeList = employeeService.queryEntryStatusList(queryNotesStatusBO, employeeIds);
            Set<String> becomeTimeList = employeeService.queryBecomeStatusList(queryNotesStatusBO, employeeIds);
            Set<String> leaveStatusList = employeeService.queryLeaveStatusList(queryNotesStatusBO, employeeIds);
            timeList.addAll(entryTimeList);
            timeList.addAll(becomeTimeList);
            timeList.addAll(leaveStatusList);
            Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
            Set<String> recruitStatusList = candidateService.queryRecruitStatusList(queryNotesStatusBO, deptIds);
            timeList.addAll(recruitStatusList);
        return timeList;
    }

    @Override
    public Map<Integer, Long> myTeam() {
        Map<Integer, Long> collect = new TreeMap<>();
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        List<HrmEmployee> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(employeeIds)) {
            //在职
            list = employeeService.lambdaQuery()
                    .select(HrmEmployee::getStatus)
                    .in(HrmEmployee::getEntryStatus, EmployeeEntryStatus.IN.getValue(), EmployeeEntryStatus.TO_LEAVE.getValue())
                    .eq(HrmEmployee::getIsDel, 0)
                    .in(HrmEmployee::getEmployeeId, employeeIds)
                    .list();
        }
        LocalDate now = LocalDate.now();
        for (AbnormalChangeType value : AbnormalChangeType.values()) {
            List<HrmEmployeeAbnormalChangeRecord> changeRecordList = abnormalChangeRecordService.queryListByDate1(now.getYear(), now.getMonthValue(), value.getValue(), employeeIds);
            if (CollUtil.isNotEmpty(changeRecordList)) {
                collect.put(value.getValue(), (long) changeRecordList.size());
            } else {
                collect.put(value.getValue(), 0L);
            }
        }
        collect.put(0, (long) list.size());
        return collect;
    }

    @Override
    public TeamSurveyVO teamSurvey() {
        TeamSurveyVO result = new TeamSurveyVO();
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.EMPLOYEE_MENU_ID);
        BigDecimal allNum = new BigDecimal(employeeIds.size());
        List<HrmEmployee> employeeList = new ArrayList<>();
        if (CollUtil.isNotEmpty(employeeIds)) {
            employeeList = employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId, HrmEmployee::getStatus, HrmEmployee::getSex, HrmEmployee::getAge, HrmEmployee::getCompanyAge)
                    .in(HrmEmployee::getEmployeeId, employeeIds).eq(HrmEmployee::getIsDel, 0).in(HrmEmployee::getEntryStatus, 1, 3).list();
            allNum = Convert.toBigDecimal(employeeList.size());
        }
        result.setStatusAnalysis(getStatusAnalysisList(allNum, employeeList));
        result.setSexAnalysis(getSexAnalysisList(allNum, employeeList));
        result.setAgeAnalysis(getAgeAnalysisList(allNum, employeeList));
        result.setCompanyAgeAnalysis(getCompanyAgeAnalysisList(allNum, employeeList));
        return result;
    }

    /**
     * 员工状态占比分析
     */
    private List<TeamSurveyPropertiesVO> getStatusAnalysisList(BigDecimal allNum, List<HrmEmployee> employeeList) {
        Map<Integer, Long> statusMap = employeeList.stream()
                .collect(Collectors.groupingBy(HrmEmployee::getStatus, Collectors.counting()));
        List<TeamSurveyPropertiesVO> statusAnalysis = new ArrayList<>();
        for (EmployeeStatusEnum value : EmployeeStatusEnum.values()) {
            Long count = 0L;
            if (statusMap.containsKey(value.getValue())) {
                count = statusMap.get(value.getValue());
            }
            BigDecimal proportion = BigDecimal.ZERO;
            if (!allNum.equals(BigDecimal.ZERO)) {
                proportion = new BigDecimal(count).divide(allNum, 2, BigDecimal.ROUND_HALF_UP);
            }
            TeamSurveyPropertiesVO statusProperties = TeamSurveyPropertiesVO.builder().name(value.getName())
                    .count(count)
                    .proportion(proportion.toString())
                    .build();
            statusAnalysis.add(statusProperties);
        }
        return statusAnalysis;
    }

    /**
     * 员工性别占比分析
     */
    private List<TeamSurveyPropertiesVO> getSexAnalysisList(BigDecimal allNum, List<HrmEmployee> employeeList) {
        Map<Integer, Long> sexMap = employeeList.stream()
                .collect(Collectors.groupingBy(
                        employee -> Optional.ofNullable(employee.getSex()).orElse(-1),
                        TreeMap::new, Collectors.counting()));
        List<TeamSurveyPropertiesVO> sexAnalysis = new ArrayList<>();
        for (SexEnum value : SexEnum.values()) {
            Long count = 0L;
            if (sexMap.containsKey(value.getValue())) {
                count = sexMap.get(value.getValue());
            }
            BigDecimal proportion = BigDecimal.ZERO;
            if (!allNum.equals(BigDecimal.ZERO)) {
                proportion = new BigDecimal(count).divide(allNum, 2, BigDecimal.ROUND_HALF_UP);
            }
            TeamSurveyPropertiesVO sexProperties = TeamSurveyPropertiesVO.builder().name(value.getName())
                    .count(count)
                    .proportion(proportion.toString())
                    .build();
            sexAnalysis.add(sexProperties);
        }
        return sexAnalysis;
    }

    /**
     * 员工年龄占比分析
     */
    private List<TeamSurveyPropertiesVO> getAgeAnalysisList(BigDecimal allNum, List<HrmEmployee> employeeList) {
        List<TeamSurveyPropertiesVO> ageAnalysis = new ArrayList<>();
        TreeRangeMap<Integer, String> ageRangeMap = TreeRangeMap.create();
        ageRangeMap.put(Range.lessThan(0), "未填写");
        ageRangeMap.put(Range.closed(0, 17), "17以下");
        ageRangeMap.put(Range.closed(18, 25), "18-25");
        ageRangeMap.put(Range.closed(26, 35), "26-35");
        ageRangeMap.put(Range.closed(36, 45), "36-45");
        ageRangeMap.put(Range.closed(46, 55), "46-55");
        ageRangeMap.put(Range.atLeast(56), "56以上");
        Map<String, Long> ageMap = employeeList.stream()
                .collect(Collectors.groupingBy(employee -> {
                    Integer age = Optional.ofNullable(employee.getAge()).orElse(-1);
                    return ageRangeMap.get(age);
                }, TreeMap::new, Collectors.counting()));

        Collection<String> values = ageRangeMap.asMapOfRanges().values();
        for (String name : values) {
            Long count = 0L;
            if (ageMap.containsKey(name)) {
                count = ageMap.get(name);
            }
            BigDecimal proportion = BigDecimal.ZERO;
            if (!allNum.equals(BigDecimal.ZERO)) {
                proportion = new BigDecimal(count).divide(allNum, 2, BigDecimal.ROUND_HALF_UP);
            }
            TeamSurveyPropertiesVO ageProperties = TeamSurveyPropertiesVO.builder().name(name)
                    .count(count)
                    .proportion(proportion.toString())
                    .build();
            ageAnalysis.add(ageProperties);
        }
        return ageAnalysis;
    }

    /**
     * 员工司龄占比分析
     */
    private List<TeamSurveyPropertiesVO> getCompanyAgeAnalysisList(BigDecimal allNum, List<HrmEmployee> employeeList) {
        List<TeamSurveyPropertiesVO> companyAgeAnalysis = new ArrayList<>();
        TreeRangeMap<Integer, String> companyAgeRangeMap = TreeRangeMap.create();
        companyAgeRangeMap.put(Range.lessThan(0), "未填写");
        companyAgeRangeMap.put(Range.closed(0, 89), "3个月内");
        companyAgeRangeMap.put(Range.closed(90, 179), "3-6个月");
        companyAgeRangeMap.put(Range.closed(180, 364), "6个月-1年");
        companyAgeRangeMap.put(Range.closed(365, 365 * 3 - 1), "1-3年");
        companyAgeRangeMap.put(Range.closed(365 * 3, 365 * 5 - 1), "3-5年");
        companyAgeRangeMap.put(Range.closed(365 * 5, 365 * 10 - 1), "5-10年");
        companyAgeRangeMap.put(Range.atLeast(365 * 10), "10年以上");
        TreeMap<String, Long> companyAgeMap = employeeList.stream().collect(Collectors.groupingBy(employee -> {
            Integer companyAge = Optional.ofNullable(employee.getCompanyAge()).orElse(-1);
            return companyAgeRangeMap.get(companyAge);
        }, TreeMap::new, Collectors.counting()));
        Collection<String> values = companyAgeRangeMap.asMapOfRanges().values();
        for (String name : values) {
            Long count = 0L;
            if (companyAgeMap.containsKey(name)) {
                count = companyAgeMap.get(name);
            }
            BigDecimal proportion = BigDecimal.ZERO;
            if (!allNum.equals(BigDecimal.ZERO)) {
                proportion = new BigDecimal(count).divide(allNum, 2, BigDecimal.ROUND_HALF_UP);
            }
            TeamSurveyPropertiesVO companyAgeProperties = TeamSurveyPropertiesVO.builder().name(name)
                    .count(count)
                    .proportion(proportion.toString())
                    .build();
            companyAgeAnalysis.add(companyAgeProperties);
        }
        return companyAgeAnalysis;
    }

    @Override
    public MySurveyVO mySurvey() {
        if (EmployeeHolder.getEmployeeId() == null) {
            return null;
        }
        HrmEmployee employee = employeeService.getById(EmployeeHolder.getEmployeeId());
        MySurveyVO mySurveyVO = BeanUtil.copyProperties(employee, MySurveyVO.class);
        HrmDept dept = deptService.getById(employee.getDeptId());
        mySurveyVO.setDeptName(Optional.ofNullable(dept).map(HrmDept::getName).orElse(""));
        mySurveyVO.setEntryDay(DateUtil.betweenDay(Timestamp.valueOf(employee.getEntryTime().atStartOfDay()), new Date(), true));
        HrmSalarySlip salarySlip = salarySlipService.lambdaQuery().eq(HrmSalarySlip::getEmployeeId, employee.getEmployeeId())
                .one();
        if (salarySlip != null && salarySlip.getReadStatus() == 0) {
            String slipRemarks = UserCacheUtil.getUserName(salarySlip.getCreateUserId())
                    + "更新了您" + salarySlip.getMonth() + "月的工资条，前往查看>>";
            mySurveyVO.setSlipRemarks(slipRemarks);
        }
        return mySurveyVO;
    }

    @Override
    public Set<String> myNotesStatus(QueryNotesStatusBO queryNotesStatusBO) {
        Set<String> notesStatusList = notesService.queryNoteStatusList(queryNotesStatusBO, ListUtil.toList(EmployeeHolder.getEmployeeId()));
        Set<String> timeList = new TreeSet<>(notesStatusList);
        Set<String> clockStatusList = attendanceClockService.queryClockStatusList(queryNotesStatusBO, ListUtil.toList(EmployeeHolder.getEmployeeId()));
        timeList.addAll(clockStatusList);
        return timeList;
    }

    @Override
    public List<NotesVO> myNotesByTime(QueryNotesByTimeBO queryNotesByTimeBO) {
        LocalDate time = queryNotesByTimeBO.getTime();
        List<NotesVO> notesVOList = new ArrayList<>();
        List<HrmNotes> notesList = notesService.queryNoteListByTime(time, ListUtil.toList(EmployeeHolder.getEmployeeId()));
        //List<HrmAttendanceClock> clockList = attendanceClockService.queryClockListByTime(time, Lists.newArrayList(EmployeeHolder.getEmployeeId()));
        notesList.forEach(notes -> {
            NotesVO notesVO = new NotesVO(notes.getNotesId(), NotesType.NOTES.getValue(), notes.getContent(), null);
            notesVOList.add(notesVO);
        });
/*        clockList.forEach(clock -> {
            String content = clock.getEmployeeName() + ClockType.valueOfName(clock.getClockType())+"打卡";
            NotesVO notesVO = new NotesVO(null, NotesType.CLOCK.getValue(), content, clock.getClockEmployeeId());
            notesVOList.add(notesVO);
        });*/
        return notesVOList;
    }
}
