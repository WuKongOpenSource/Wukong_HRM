package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.*;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.BaseUtil;
import com.kakarote.core.utils.ExcelParseUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.mapper.HrmAttendanceClockMapper;
import com.kakarote.hrm.service.*;
import com.kakarote.hrm.utils.AttendUtil;
import com.kakarote.hrm.utils.EmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 打卡记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-07
 */
@Service
public class HrmAttendanceClockServiceImpl extends BaseServiceImpl<HrmAttendanceClockMapper, HrmAttendanceClock> implements IHrmAttendanceClockService {

    @Autowired
    private EmployeeUtil employeeUtil;
    @Autowired
    private IHrmAttendanceGroupService attendanceGroupService;
    @Autowired
    private IHrmAttendanceShiftService attendanceShiftService;
    @Autowired
    private IHrmAttendanceLegalHolidaysService attendanceLegalHolidaysService;
    @Autowired
    private IHrmEmployeeLeaveRecordService leaveRecordService;
    @Autowired
    private IHrmEmployeeService employeeService;
    @Autowired
    private IHrmEmployeeOverTimeRecordService employeeOverTimeRecordService;
    @Autowired
    private IHrmAttendancePointService attendancePointService;
    @Autowired
    private IHrmAttendanceWifiService attendanceWifiService;
    @Autowired
    private IHrmAttendanceHistoryShiftService attendanceHistoryShiftService;
    @Autowired
    private IHrmAttendanceDateShiftService attendanceDateShiftService;

    private static final int FOUR = 4;

    private static final int TWO = 2;

    private static final int THREE = 3;

    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final int SEVEN = 7;

    private static final String START_STATUS = "startStatus";

    private static final String END_STATUS = "endStatus";

    private static final String START_IS_OUT_CARD = "startIsOutCard";

    private static final String END_IS_OUT_CARD = "endIsOutCard";

    private static final String NULL = " ";

    @Override
    public void addOrUpdate(HrmAttendanceClock attendanceClock) {
        attendanceClock.setAttendanceTime(LocalDateTime.now());
        attendanceClock.setClockEmployeeId(EmployeeHolder.getEmployeeId());
        saveOrUpdate(attendanceClock);
    }

    @Override
    public BasePage<QueryAttendancePageVO> queryPageList(QueryAttendancePageBO attendancePageBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ATTENDANCE_MENU_ID);
        return getBaseMapper().queryPageList(attendancePageBO.parse(), attendancePageBO, employeeIds);
    }

    @Override
    public List<HrmAttendanceClock> queryClockListByTime(LocalDate time, Collection<Long> employeeIds) {
        return getBaseMapper().queryClockListByTime(time, employeeIds);
    }

    @Override
    public Set<String> queryClockStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds) {
        return getBaseMapper().queryClockStatusList(queryNotesStatusBO, employeeIds);
    }

    @Override
    public BasePage<QueryAttendancePageVO> queryMyPageList(PageEntity pageEntity) {
        return getBaseMapper().queryMyPageList(pageEntity.parse(), EmployeeHolder.getEmployeeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setHrmAttendanceClock(SetHrmAttendanceClockB0 attendanceClock) {
        if (attendanceClock.getClockStatus() == null) {
            attendanceClock.setClockStatus(ClockStatusEnum.NORMAL.getValue());
            if (attendanceClock.getClockType().intValue() == ClockType.GO_TO.getValue()) {
                if (LocalDateTimeUtil.toEpochMilli(attendanceClock.getClockTime()) > LocalDateTimeUtil.toEpochMilli(attendanceClock.getAttendanceTime())) {
                    attendanceClock.setClockStatus(ClockStatusEnum.LATE.getValue());
                }
            } else {
                if (LocalDateTimeUtil.toEpochMilli(attendanceClock.getClockTime()) < LocalDateTimeUtil.toEpochMilli(attendanceClock.getAttendanceTime())) {
                    attendanceClock.setClockStatus(ClockStatusEnum.EARLY.getValue());
                }
            }
        }
        HrmAttendanceClock hrmAttendanceClock = BeanUtil.copyProperties(attendanceClock, HrmAttendanceClock.class);
        saveOrUpdate(hrmAttendanceClock);
        if (attendanceClock.getClockStatus() == SEVEN) {//加班
            //查询对应的班次
            QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
            queryAttendanceDailyDetailBO.setCurrentDate(attendanceClock.getAttendanceTime().toLocalDate());
            queryAttendanceDailyDetailBO.setEmployeeId(attendanceClock.getClockEmployeeId());
            //查询出今日班次
            HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
            HrmEmployeeOverTimeRecord hrmEmployeeOverTimeRecord = new HrmEmployeeOverTimeRecord();
            if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.REST.getValue())) {
                hrmEmployeeOverTimeRecord = employeeOverTimeRecordService.lambdaQuery().eq(HrmEmployeeOverTimeRecord::getEmployeeId, attendanceClock.getClockEmployeeId())
                        .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN)).orderByDesc(HrmEmployeeOverTimeRecord::getCreateTime).one();
                if (ObjectUtil.isNull(hrmEmployeeOverTimeRecord)) {
                    hrmEmployeeOverTimeRecord = new HrmEmployeeOverTimeRecord();
                }
                if (attendanceClock.getClockType() == ClockType.GO_TO.getValue()) {
                    HrmAttendanceClock hrmStartAttendanceClock = lambdaQuery().eq(HrmAttendanceClock::getClockType, ClockType.GO_TO.getValue()).eq(HrmAttendanceClock::getClockEmployeeId, queryAttendanceDailyDetailBO.getEmployeeId())
                            .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN)).orderByDesc(HrmAttendanceClock::getCreateTime).one();
                    hrmEmployeeOverTimeRecord.setEmployeeId(attendanceClock.getClockEmployeeId());
                    //休息日加班
                    hrmEmployeeOverTimeRecord.setOverTimeType(TWO);
                    hrmEmployeeOverTimeRecord.setAttendanceTime(queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay());
                    hrmEmployeeOverTimeRecord.setOverTimeStartTime(hrmStartAttendanceClock.getClockTime());
                } else {
                    if (ObjectUtil.isNull(hrmEmployeeOverTimeRecord.getOverTimeStartTime())) {
                        throw new CrmException(HrmCodeEnum.THE_ATTEND_GO_TO_EMPLOYEE_ERROR);
                    }
                    HrmAttendanceClock hrmEndAttendanceClock = lambdaQuery().eq(HrmAttendanceClock::getClockType, ClockType.GET_OFF.getValue()).eq(HrmAttendanceClock::getClockEmployeeId, queryAttendanceDailyDetailBO.getEmployeeId())
                            .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN)).orderByDesc(HrmAttendanceClock::getCreateTime).one();
                    hrmEmployeeOverTimeRecord.setOverTimeEndTime(hrmEndAttendanceClock.getClockTime());
                    hrmEmployeeOverTimeRecord.setOverTimes(new BigDecimal(LocalDateTimeUtil.between(hrmEmployeeOverTimeRecord.getOverTimeStartTime(), hrmEmployeeOverTimeRecord.getOverTimeEndTime()).toHours()));
                }
                employeeOverTimeRecordService.saveOrUpdate(hrmEmployeeOverTimeRecord);
            } else {
                //工作日加班
                hrmEmployeeOverTimeRecord.setOverTimeType(ONE);
                String end = hrmAttendanceShift.getEnd1();
                if (attendanceClock.getClockStage() == TWO) {
                    end = hrmAttendanceShift.getEnd2();
                } else if (attendanceClock.getClockStage() == THREE) {
                    end = hrmAttendanceShift.getEnd3();
                }
                //获取加班开始时间
                LocalDateTime endTime = DateUtil.parseLocalDateTime(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN) + " " + AttendUtil.convertTime(end));
                hrmEmployeeOverTimeRecord.setOverTimeStartTime(endTime);
                hrmEmployeeOverTimeRecord.setOverTimes(new BigDecimal(LocalDateTimeUtil.between(endTime, attendanceClock.getClockTime()).toHours()));
                hrmEmployeeOverTimeRecord.setOverTimeEndTime(attendanceClock.getClockTime());
                hrmEmployeeOverTimeRecord.setEmployeeId(attendanceClock.getClockEmployeeId());
                employeeOverTimeRecordService.save(hrmEmployeeOverTimeRecord);
            }
        }
    }

    @Override
    public BasePage<QueryAttendanceEmpMonthRecordVO> queryAttendanceEmpMonthRecordPageList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO) {
        List<QueryAttendanceEmpMonthRecordVO> empMonthRecordVOList = new ArrayList<>();
        BasePage<QueryEmployeeAttendanceVO> employeeAttendanceBasePage = queryAttendanceEmpList(queryAttendanceEmpMonthRecordBO);
        List<QueryEmployeeAttendanceVO> queryEmployeeAttendanceList = employeeAttendanceBasePage.getList();
        BasePage<QueryAttendanceEmpMonthRecordVO> page = new BasePage<>(employeeAttendanceBasePage.getCurrent(), employeeAttendanceBasePage.getSize(), employeeAttendanceBasePage.getTotal());
        if (queryEmployeeAttendanceList != null && queryEmployeeAttendanceList.size() > ZERO) {
            leaveRecordService.queryOaLeaveExamineList();
            //查询出日期区间所有的日期
            List<String> dates = findDates(queryAttendanceEmpMonthRecordBO.getTimes().get(0), queryAttendanceEmpMonthRecordBO.getTimes().get(1));
            List<Long> employeeIds = queryEmployeeAttendanceList.stream().map(QueryEmployeeAttendanceVO::getEmployeeId).collect(Collectors.toList());
            //查询出日期区间所有的上班打卡记录
            LocalDateTime startDateTime = queryAttendanceEmpMonthRecordBO.getTimes().get(0).atStartOfDay();
            LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(queryAttendanceEmpMonthRecordBO.getTimes().get(1).atStartOfDay());
            List<HrmAttendanceClock> startClockList = queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
            Map<Long, List<HrmAttendanceClock>> startEmployeeClockMap = startClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
            //查询出日期区间所有的下班打卡记录
            List<HrmAttendanceClock> endClockList = queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
            Map<Long, List<HrmAttendanceClock>> endEmployeeClockMap = endClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
            UserInfo userInfo = UserUtil.getUser();
            queryEmployeeAttendanceList.stream().parallel().forEach(queryEmployeeAttendanceVO -> {
                UserUtil.setUser(userInfo);
                List<HrmAttendanceClock> startEmployeeClockList = startEmployeeClockMap.get(queryEmployeeAttendanceVO.getEmployeeId());
                List<HrmAttendanceClock> endEmployeeClockList = endEmployeeClockMap.get(queryEmployeeAttendanceVO.getEmployeeId());
                QueryAttendanceEmpMonthRecordVO empMonthRecordVO = getQueryAttendanceEmpMonthRecordVO(startEmployeeClockList, endEmployeeClockList, queryAttendanceEmpMonthRecordBO, dates, queryEmployeeAttendanceVO);
                empMonthRecordVOList.add(empMonthRecordVO);
            });
            List<QueryAttendanceEmpMonthRecordVO> queryAttendanceEmpMonthRecordVOList;
            if (queryAttendanceEmpMonthRecordBO.getIsFullAttendance() != null && queryAttendanceEmpMonthRecordBO.getIsFullAttendance() == IsEnum.YES.getValue()) {
                queryAttendanceEmpMonthRecordVOList = empMonthRecordVOList.stream().filter(empMonthRecordVO -> empMonthRecordVO.getIsFullAttendance() == IsEnum.YES.getValue()).sorted(Comparator.comparingLong(empMonthRecordVO -> empMonthRecordVO.getEmployeeId())).collect(Collectors.toList());
            } else {
                queryAttendanceEmpMonthRecordVOList = empMonthRecordVOList.stream().sorted(Comparator.comparingLong(empMonthRecordVO -> empMonthRecordVO.getEmployeeId())).collect(Collectors.toList());
            }
            UserUtil.removeUser();
            page.setList(queryAttendanceEmpMonthRecordVOList);
        }
        return page;
    }

    /**
     * 获取员工时间区间的考勤汇总
     *
     * @param queryAttendanceEmpMonthRecordBO
     * @param dates
     * @param queryEmployeeAttendanceVO
     * @return
     */
    private QueryAttendanceEmpMonthRecordVO getQueryAttendanceEmpMonthRecordVO(List<HrmAttendanceClock> startEmployeeClockList, List<HrmAttendanceClock> endEmployeeClockList, QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO, List<String> dates, QueryEmployeeAttendanceVO queryEmployeeAttendanceVO) {
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
        // 外勤天数
        Integer outDays = ZERO;
        // 缺卡次数
        Integer misscardCount = ZERO;
        // 不被统计天数
        Integer unCountDays = ZERO;
        // 请假天数
        BigDecimal leaveDays = new BigDecimal(ZERO);
        // 旷工天数
        BigDecimal absenteeismDays = new BigDecimal(ZERO);
        QueryAttendanceEmpMonthRecordVO empMonthRecordVO = new QueryAttendanceEmpMonthRecordVO();
        BeanUtil.copyProperties(queryEmployeeAttendanceVO, empMonthRecordVO);
        //查询日期区间所有的加班
        Integer overTimeCount = queryEmpAttendanceOverTimeCountDays(queryAttendanceEmpMonthRecordBO.getTimes(), queryEmployeeAttendanceVO.getEmployeeId());
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(queryEmployeeAttendanceVO.getEmployeeId());
        Map<String, Map<String, Object>> empRecordDetailMap = queryAttendanceEmpRecordDetailByDate(startEmployeeClockList, endEmployeeClockList, dates, hrmAttendanceGroup, queryEmployeeAttendanceVO.getEmployeeId());
        for (String date : dates) {
            Map<String, Object> map = empRecordDetailMap.get(date);
            Integer status = (Integer) map.get("status");
            switch (status) {
                case 0:
                    normalDays = normalDays + 1;
                    lateMinute = lateMinute + (Integer) map.get("lateMinutes");
                    lateCount = lateCount + (Integer) map.get("lateCount");
                    earlyMinute = earlyMinute + (Integer) map.get("earlyMinutes");
                    earlyCount = earlyCount + (Integer) map.get("earlyCount");
                    outDays = outDays + (Integer) map.get("IsOutCard");
                    break;
                case 4:
                    absenteeismDays = absenteeismDays.add(new BigDecimal(map.get("absenteeismDay").toString()));
                    break;
                case 9:
                    restDays = restDays + 1;
                    break;
                case 10:
                    unCountDays = unCountDays + 1;
                    break;
                default:
                    break;
            }
            misscardCount = misscardCount + (Integer) map.get("misscardCount");
            leaveDays = leaveDays.add(new BigDecimal(map.get("leaveDay").toString()));
        }
        Integer overTime = overTimeCount != null ? overTimeCount : ZERO;
        empMonthRecordVO.setAttendDays(dates.size() - restDays);
        empMonthRecordVO.setActualDays(normalDays - Convert.toInt(leaveDays) + overTime);
        empMonthRecordVO.setAbsenteeismDays(absenteeismDays);
        empMonthRecordVO.setEarlyCount(earlyCount);
        empMonthRecordVO.setEarlyMinute(earlyMinute);
        empMonthRecordVO.setLateCount(lateCount);
        empMonthRecordVO.setLateMinute(lateMinute);
        empMonthRecordVO.setOutDays(outDays);
        empMonthRecordVO.setMisscardCount(misscardCount);
        empMonthRecordVO.setLeaveDays(leaveDays);
        empMonthRecordVO.setOverTimeCount(overTime);
        empMonthRecordVO.setAttendanceGroupName(hrmAttendanceGroup.getName());
        empMonthRecordVO.setIsFullAttendance(IsEnum.NO.getValue());
        if (empMonthRecordVO.getActualDays() >= empMonthRecordVO.getAttendDays() && earlyCount == ZERO && lateCount == ZERO && leaveDays.compareTo(new BigDecimal(ZERO)) == ZERO) {
            empMonthRecordVO.setIsFullAttendance(IsEnum.YES.getValue());
        }
        return empMonthRecordVO;
    }

    @Override
    public Map<String, Map<String, Object>> queryAttendanceEmpRecordDetailByDate(List<HrmAttendanceClock> startEmployeeClockList, List<HrmAttendanceClock> endEmployeeClockList, List<String> dates, HrmAttendanceGroup hrmAttendanceGroup, Long employeeId) {
        Map<String, Map<String, Object>> empRecordDetailMap = new HashMap<>();
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        long entryTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime());
        long createTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate());
        long nowMilli = LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        //查询出日期区间的班次
        Map<String, HrmAttendanceShift> hrmAttendanceShiftMap = getHrmAttendanceShiftMap(dates, hrmAttendanceGroup, employeeId);
        Map<String, Map<String, Object>> detailMap = queryRecordDetailByDate(startEmployeeClockList, endEmployeeClockList, dates, hrmAttendanceShiftMap, employeeId, ONE);
        for (String date : dates) {
            long currentMilli = LocalDateTimeUtil.toEpochMilli(DateUtil.parseDate(date).toLocalDateTime().toLocalDate());
            Map<String, Object> map = new HashMap<>();
            HrmAttendanceShift hrmAttendanceShift = hrmAttendanceShiftMap.get(date);
            map.put("date", date);
            map.put("hrmAttendanceShift", hrmAttendanceShift);
            map.put("IsOutCard", ZERO);
            map.put("lateMinutes", ZERO);
            map.put("lateCount", ZERO);
            map.put("earlyMinutes", ZERO);
            map.put("earlyCount", ZERO);
            map.put("misscardCount", ZERO);
            map.put("absenteeismDay", ZERO);
            map.put("leaveMinutes", ZERO);
            map.put("absenteeismMinutes", ZERO);
            map.put("leaveDay", ZERO);
            map.put("status", AttendanceResultEnum.NORMAL.getValue());
            //当前时间小于当前时间
            boolean currentDate = currentMilli < nowMilli;
            //员工入职时间小于等于当前时间
            boolean entryTime = entryTimeMilli <= currentMilli;
            //员工创建时间小于等于当前时间
            boolean createTime = createTimeMilli <= currentMilli;
            if (currentDate && entryTime && createTime) {
                if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.REST.getValue())) {
                    map.put("status", AttendanceResultEnum.REST.getValue());
                } else if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                    Map<String, Object> RecordDetail = detailMap.get(date);
                    if (RecordDetail.get(START_IS_OUT_CARD).equals(IsEnum.YES.getValue()) || RecordDetail.get(END_IS_OUT_CARD).equals(IsEnum.YES.getValue())) {
                        map.put("IsOutCard", IsEnum.YES.getValue());
                    }
                    if (RecordDetail.get(START_STATUS).equals(AttendanceResultEnum.LATE.getValue())) {
                        map.put("lateCount", ONE);
                        map.put("lateMinutes", RecordDetail.get("startMinutes"));
                    }
                    if (RecordDetail.get(START_STATUS).equals(AttendanceResultEnum.MISSCARD.getValue())) {
                        map.put("misscardCount", ONE);
                    }
                    if (RecordDetail.get(END_STATUS).equals(AttendanceResultEnum.EARLY.getValue())) {
                        map.put("earlyCount", ONE);
                        map.put("earlyMinutes", RecordDetail.get("endMinutes"));
                    }
                    if (RecordDetail.get(END_STATUS).equals(AttendanceResultEnum.MISSCARD.getValue())) {
                        if (RecordDetail.get(START_STATUS).equals(AttendanceResultEnum.MISSCARD.getValue())) {
                            map.put("misscardCount", TWO);
                        } else {
                            map.put("misscardCount", ONE);
                        }
                    }
                    String misscardCount = "misscardCount";
                    if (map.get(misscardCount).equals(TWO)) {
                        map.put("misscardCount", ZERO);
                        map.put("status", AttendanceResultEnum.ABSENTEEISM.getValue());
                        map.put("absenteeismDay", new BigDecimal(1));
                    }
                    map.put("leaveMinutes", RecordDetail.get("leaveMinutes"));
                    BigDecimal leaveHour = new BigDecimal((int) map.get("leaveMinutes"));
                    BigDecimal leaveDay = leaveHour.divide(new BigDecimal(hrmAttendanceShift.getShiftHours()), 2, BigDecimal.ROUND_HALF_UP);
                    map.put("leaveDay", leaveDay);
                }
            } else {
                map.put("status", 10);
            }
            empRecordDetailMap.put(date, map);
        }

        return empRecordDetailMap;
    }

    @Override
    public List<HrmAttendanceClock> queryAttendanceClockList(Integer clockType, LocalDateTime startDateTime, LocalDateTime endDateTime, List<Long> employeeIds, Integer clockStage) {
        List<HrmAttendanceClock> hrmAttendanceClockList = baseMapper.queryAttendanceClockList(clockType, startDateTime, endDateTime, employeeIds, clockStage);
        return hrmAttendanceClockList;
    }

    /**
     * 查询员工当天打卡的时间范围
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    @Override
    public AttendShiftTimeVO queryCurrentAttendShiftTime(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        AttendShiftTimeVO attendShiftTimeVO = new AttendShiftTimeVO();
        HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
        HrmEmployee hrmEmployee = employeeService.getById(queryAttendanceDailyDetailBO.getEmployeeId());
        long entryTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime());
        long createTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate());
        long nowMilli = LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        long currentMilli = LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        //当前出勤时间小于当前时间
        boolean currentDate = currentMilli <= nowMilli;
        //员工入职时间小于等于当前时间
        boolean entryTime = entryTimeMilli <= currentMilli;
        //员工创建时间小于等于当前时间
        boolean createTime = createTimeMilli <= currentMilli;
        attendShiftTimeVO.setStatus(0);
        if (currentDate && entryTime && createTime) {
            if (ObjectUtil.notEqual(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.REST.getValue())) {
                attendShiftTimeVO = queryCurrentAttendShiftTime(hrmAttendanceShiftVO, ONE, queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay());
            }
            attendShiftTimeVO.setStatus(1);
        }
        return attendShiftTimeVO;
    }

    /**
     * 通过考勤组获取当前班次
     *
     * @param hrmAttendanceGroup
     * @return
     */
    @Override
    public HrmAttendanceShift getHrmAttendanceShiftByGroup(HrmAttendanceGroup hrmAttendanceGroup) {
        // 查询出特殊日期
        Map<String, Object> dateValueMap = new HashMap<>();
        if (BaseUtil.isJSONArray(hrmAttendanceGroup.getSpecialDateSetting())) {
            JSONArray array = JSON.parseArray(hrmAttendanceGroup.getSpecialDateSetting());
            for (int j = 0; j < array.size(); j++) {
                dateValueMap.put(array.getJSONObject(j).getString("date"), array.getJSONObject(j).getString("shift"));
            }
        }
        //查询出所有法定节假日
        if (hrmAttendanceGroup.getIsRest() == IsEnum.YES.getValue()) {
            List<HrmAttendanceLegalHolidays> list = attendanceLegalHolidaysService.queryLegalHolidayList();
            if (CollUtil.isNotEmpty(list)) {
                //查询出默认设置的休息班次
                HrmAttendanceShift hrmAttendanceShift1 = attendanceShiftService.lambdaQuery().select(HrmAttendanceShift::getShiftId)
                        .eq(HrmAttendanceShift::getShiftType, ShiftTypeEnum.REST.getValue()).eq(HrmAttendanceShift::getIsDefaultSetting, IsEnum.YES.getValue()).one();
                list.forEach(attendanceLegalHolidays -> dateValueMap.put(DateUtil.formatDate(Timestamp.valueOf(attendanceLegalHolidays.getHolidayTime())), hrmAttendanceShift1.getShiftId()));
            }
        }
        List<String> shiftList = StrUtil.splitTrim(hrmAttendanceGroup.getShiftSetting(), Const.SEPARATOR);
        Integer dayOfWeek = LocalDateTimeUtil.dayOfWeek(LocalDate.now()).getValue();
        dayOfWeek = dayOfWeek.equals(1) ? 7 : dayOfWeek - 1;
        String shiftId = shiftList.get(dayOfWeek - 1);
        //获取今天对应得班次
        HrmAttendanceShift hrmAttendanceShift;
        Object shift = dateValueMap.get(LocalDateTimeUtil.format(LocalDate.now(), DatePattern.NORM_DATE_PATTERN));
        if (ObjectUtil.isNotEmpty(shift)) {
            hrmAttendanceShift = attendanceShiftService.getById(Convert.toLong(shift.toString()));
        } else {
            hrmAttendanceShift = attendanceShiftService.getById(shiftId);
        }
        if (ObjectUtil.isNull(hrmAttendanceShift)) {
            //未匹配到班次，则查询默认班次
            hrmAttendanceShift = attendanceShiftService.lambdaQuery()
                    .eq(HrmAttendanceShift::getIsDefaultSetting, IsEnum.YES).orderByDesc(HrmAttendanceShift::getCreateTime).last("limit 1").one();
        }
        if (hrmAttendanceShift.getEffectTime().isAfter(LocalDateTime.now())) {
            //查询到的班次未生效,去查询历史班次
            HrmAttendanceHistoryShift hrmAttendanceHistoryShift = attendanceHistoryShiftService.lambdaQuery()
                    .eq(HrmAttendanceHistoryShift::getShiftId, hrmAttendanceShift.getShiftId()).orderByDesc(HrmAttendanceHistoryShift::getUpdateTime).one();
            hrmAttendanceShift = BeanUtil.copyProperties(hrmAttendanceHistoryShift, HrmAttendanceShift.class);
        }
        return hrmAttendanceShift;
    }

    private Map<String, Map<String, Object>> queryRecordDetailByDate(List<HrmAttendanceClock> startEmployeeClockList, List<HrmAttendanceClock> endEmployeeClockList, List<String> dates, Map<String, HrmAttendanceShift> hrmAttendanceShiftMap, Long employeeId, Integer clockStage) {
        Map<String, Map<String, Object>> detailMap = new HashMap<>();
        //查询出打卡记录
        Map<String, QueryAttendanceRecordVO> startAttendanceRecordMap = queryStartRecordList(startEmployeeClockList, dates, hrmAttendanceShiftMap, employeeId, clockStage);
        Map<String, QueryAttendanceRecordVO> endAttendanceRecordMap = queryEndRecordList(endEmployeeClockList, dates, hrmAttendanceShiftMap, employeeId, clockStage);
        for (String date : dates) {
            Map<String, Object> map = new HashMap<>();
            HrmAttendanceShift hrmAttendanceShift = hrmAttendanceShiftMap.get(date);
            QueryAttendanceRecordVO startAttendanceRecordVO = startAttendanceRecordMap.get(date);
            QueryAttendanceRecordVO endAttendanceRecordVO = endAttendanceRecordMap.get(date);
            map.put("startStatus", startAttendanceRecordVO.getStatus());
            map.put("startMinutes", startAttendanceRecordVO.getMinutes() != null ? startAttendanceRecordVO.getMinutes() : 0);
            map.put("endStatus", endAttendanceRecordVO.getStatus());
            map.put("endMinutes", endAttendanceRecordVO.getMinutes() != null ? endAttendanceRecordVO.getMinutes() : 0);
            map.put("startIsOutCard", startAttendanceRecordVO.getIsOutCard() != null ? startAttendanceRecordVO.getIsOutCard() : 0);
            map.put("endIsOutCard", endAttendanceRecordVO.getIsOutCard() != null ? endAttendanceRecordVO.getIsOutCard() : 0);
            map.put("leaveMinutes", endAttendanceRecordVO.getLeaveMinutes() != null ? endAttendanceRecordVO.getLeaveMinutes() : 0);
            if (startAttendanceRecordVO.getStatus().equals(THREE) && endAttendanceRecordVO.getStatus().equals(THREE)) {
                if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                    map.put("absenteeismMinutes", hrmAttendanceShift.getShiftHours());
                }
            } else {
                map.put("absenteeismMinutes", ZERO);
            }
            detailMap.put(date, map);
        }
        return detailMap;
    }

    /**
     * 查询出下班打卡记录
     *
     * @param endEmployeeClockList  原始下班打卡记录
     * @param dates                 日期区间
     * @param hrmAttendanceShiftMap 考勤班次
     * @param clockStage            打卡阶段
     * @return
     */
    private Map<String, QueryAttendanceRecordVO> queryEndRecordList(List<HrmAttendanceClock> endEmployeeClockList, List<String> dates, Map<String, HrmAttendanceShift> hrmAttendanceShiftMap, Long employeeId, Integer clockStage) {
        Map<String, QueryAttendanceRecordVO> endAttendanceRecordMap = new HashMap<>();
        Map<String, HrmAttendanceClock> endClockMap = new HashMap<>();
        if (CollUtil.isNotEmpty(endEmployeeClockList)) {
            for (HrmAttendanceClock hrmAttendanceClock : endEmployeeClockList) {
                String formatDate = LocalDateTimeUtil.format(hrmAttendanceClock.getAttendanceTime(), DatePattern.NORM_DATE_PATTERN);
                endClockMap.put(formatDate, hrmAttendanceClock);
            }
        }
        for (String date : dates) {
            HrmAttendanceShift hrmAttendanceShift = hrmAttendanceShiftMap.get(date);
            HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
            QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
            queryAttendanceDailyDetailBO.setCurrentDate(LocalDate.parse(date).atStartOfDay().toLocalDate());
            queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
            queryAttendanceDailyDetailBO.setMulti(ONE);
            HrmAttendanceClock hrmAttendanceClock = endClockMap.get(date);
            QueryAttendanceRecordVO queryAttendanceRecordVO = queryEndRecordByClockStage(queryAttendanceDailyDetailBO, hrmAttendanceClock, hrmAttendanceShiftVO, clockStage);
            endAttendanceRecordMap.put(date, queryAttendanceRecordVO);
        }
        return endAttendanceRecordMap;
    }

    /**
     * 查询出上班打卡记录
     *
     * @param startEmployeeClockList 原始上班打卡记录
     * @param dates                  日期区间
     * @param hrmAttendanceShiftMap  考勤班次
     * @param clockStage             打卡阶段
     * @return
     */
    private Map<String, QueryAttendanceRecordVO> queryStartRecordList(List<HrmAttendanceClock> startEmployeeClockList, List<String> dates, Map<String, HrmAttendanceShift> hrmAttendanceShiftMap, Long employeeId, Integer clockStage) {
        Map<String, QueryAttendanceRecordVO> startAttendanceRecordMap = new HashMap<>();
        Map<String, HrmAttendanceClock> startClockMap = new HashMap<>();
        if (CollUtil.isNotEmpty(startEmployeeClockList)) {
            for (HrmAttendanceClock hrmAttendanceClock : startEmployeeClockList) {
                String formatDate = LocalDateTimeUtil.format(hrmAttendanceClock.getAttendanceTime(), DatePattern.NORM_DATE_PATTERN);
                startClockMap.put(formatDate, hrmAttendanceClock);
            }
        }
        for (String date : dates) {
            HrmAttendanceShift hrmAttendanceShift = hrmAttendanceShiftMap.get(date);
            HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
            QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
            queryAttendanceDailyDetailBO.setCurrentDate(LocalDate.parse(date).atStartOfDay().toLocalDate());
            queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
            queryAttendanceDailyDetailBO.setMulti(ONE);
            HrmAttendanceClock hrmAttendanceClock = startClockMap.get(date);
            QueryAttendanceRecordVO queryAttendanceRecordVO = queryStartRecordByClockStage(queryAttendanceDailyDetailBO, hrmAttendanceClock, hrmAttendanceShiftVO, clockStage);
            startAttendanceRecordMap.put(date, queryAttendanceRecordVO);
        }
        return startAttendanceRecordMap;
    }

    /**
     * 获取日期区间的班次
     *
     * @param dates
     * @return
     */
    @Override
    public Map<String, HrmAttendanceShift> getHrmAttendanceShiftMap(List<String> dates, HrmAttendanceGroup hrmAttendanceGroup, Long employeeId) {
        Map<String, HrmAttendanceShift> hrmAttendanceShiftMap = new HashMap<>();
        for (String date : dates) {
            QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
            queryAttendanceDailyDetailBO.setCurrentDate(DateUtil.parseDate(date).toLocalDateTime().toLocalDate());
            queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
            queryAttendanceDailyDetailBO.setHrmAttendanceGroup(hrmAttendanceGroup);
            HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
            hrmAttendanceShiftMap.put(date, hrmAttendanceShift);
        }
        return hrmAttendanceShiftMap;
    }

    @Override
    public Map<String, Object> queryAttendanceEmpRecordDetail(String date, Long employeeId) {
        Map<String, Object> map = new HashMap<>();
        QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
        queryAttendanceDailyDetailBO.setCurrentDate(DateUtil.parseDate(date).toLocalDateTime().toLocalDate());
        queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
        //查询出今日班次
        HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
        map.put("date", date);
        map.put("hrmAttendanceShift", hrmAttendanceShift);
        map.put("IsOutCard", ZERO);
        map.put("lateMinutes", ZERO);
        map.put("lateCount", ZERO);
        map.put("earlyMinutes", ZERO);
        map.put("earlyCount", ZERO);
        map.put("misscardCount", ZERO);
        map.put("absenteeismDay", ZERO);
        map.put("leaveMinutes", ZERO);
        map.put("absenteeismMinutes", ZERO);
        map.put("leaveDay", ZERO);
        map.put("status", AttendanceResultEnum.NORMAL.getValue());
        //查询员工入职日期
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        boolean currentDate = LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate()) < LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        boolean entryTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        boolean createTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        if (currentDate && entryTime && createTime) {
            if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.REST.getValue())) {
                map.put("status", AttendanceResultEnum.REST.getValue());
            } else if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                Map<String, Object> RecordDetail = queryRecordDetailByParams(queryAttendanceDailyDetailBO, hrmAttendanceShiftVO, ONE);
                if (RecordDetail.get(START_IS_OUT_CARD).equals(IsEnum.YES.getValue()) || RecordDetail.get(END_IS_OUT_CARD).equals(IsEnum.YES.getValue())) {
                    map.put("IsOutCard", IsEnum.YES.getValue());
                }
                if (RecordDetail.get(START_STATUS).equals(AttendanceResultEnum.LATE.getValue())) {
                    map.put("lateCount", ONE);
                    map.put("lateMinutes", RecordDetail.get("startMinutes"));
                }
                if (RecordDetail.get(START_STATUS).equals(AttendanceResultEnum.MISSCARD.getValue())) {
                    map.put("misscardCount", ONE);
                }
                if (RecordDetail.get(END_STATUS).equals(AttendanceResultEnum.EARLY.getValue())) {
                    map.put("earlyCount", ONE);
                    map.put("earlyMinutes", RecordDetail.get("endMinutes"));
                }
                if (RecordDetail.get(END_STATUS).equals(AttendanceResultEnum.MISSCARD.getValue())) {
                    map.put("misscardCount", TWO);
                }
                String misscardCount = "misscardCount";
                if (map.get(misscardCount).equals(TWO)) {
                    map.put("misscardCount", ZERO);
                    map.put("status", AttendanceResultEnum.ABSENTEEISM.getValue());
                    map.put("absenteeismDay", new BigDecimal(1));
                }
                map.put("leaveMinutes", RecordDetail.get("leaveMinutes"));
                BigDecimal leaveHour = new BigDecimal((int) map.get("leaveMinutes"));
                BigDecimal leaveDay = leaveHour.divide(new BigDecimal(hrmAttendanceShift.getShiftHours()), 2, BigDecimal.ROUND_HALF_UP);
                map.put("leaveDay", leaveDay);
            }
        } else {
            map.put("status", 10);
        }
        return map;
    }

    @Override
    public QueryAttendanceEmpDetailVO queryAttendanceEmpMonthDetail(QueryAttendanceEmpMonthDetailBO queryAttendanceEmpMonthDetail) {
        QueryAttendanceEmpDetailVO queryAttendanceEmpDetailVO = new QueryAttendanceEmpDetailVO();
        LocalDate startTime = queryAttendanceEmpMonthDetail.getTimes().get(0);
        LocalDate endTime = queryAttendanceEmpMonthDetail.getTimes().get(1);
        List<String> dates = findDates(startTime, endTime);
        List<Long> employeeIds = ListUtil.toList(queryAttendanceEmpMonthDetail.getEmployeeId());
        //查询出日期区间所有的上班打卡记录
        LocalDateTime startDateTime = queryAttendanceEmpMonthDetail.getTimes().get(0).atStartOfDay();
        LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(queryAttendanceEmpMonthDetail.getTimes().get(1).atStartOfDay());
        List<HrmAttendanceClock> startClockList = queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        //查询出日期区间所有的下班打卡记录
        List<HrmAttendanceClock> endClockList = queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        leaveRecordService.queryOaLeaveExamineList();
        //查询是否包含今天数据
        Integer includeToday = queryAttendanceEmpMonthDetail.getIncludeToday();
        if (ObjectUtil.isNull(includeToday)) {
            //默认查询包含当天
            includeToday = 1;
        }
        //员工班次考勤明细
        Map<String, AttendEmpOverViewVO> attendEmpOverViewMap = queryAttendEmpMonthDetailByDate(includeToday, startClockList, endClockList, dates, queryAttendanceEmpMonthDetail.getEmployeeId());
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String date : dates) {
            Map<String, Object> map = new HashMap<>();
            AttendEmpOverViewVO attendEmpOverViewVO = attendEmpOverViewMap.get(date);
            boolean flag = false;
            int status = queryAttendanceEmpMonthDetail.getStatus().intValue();
            AttendanceResultEnum attendanceResultEnum = AttendanceResultEnum.parse(status);
            List<Integer> integers = Arrays.asList(attendEmpOverViewVO.getStartStatus1(), attendEmpOverViewVO.getEndStatus1());
            switch (attendanceResultEnum) {
                case ABSENTEEISM:
                    if (attendEmpOverViewVO.getAbsenteeism1() > ZERO || attendEmpOverViewVO.getAbsenteeism2() > ZERO || attendEmpOverViewVO.getAbsenteeism3() > ZERO) {
                        flag = true;
                    }
                    break;
                case EARLY:
                    if (attendEmpOverViewVO.getEarly1() > ZERO || attendEmpOverViewVO.getEarly2() > ZERO || attendEmpOverViewVO.getEarly3() > ZERO) {
                        flag = true;
                    }
                    break;
                case LATE:
                    if (attendEmpOverViewVO.getLate1() > ZERO || attendEmpOverViewVO.getLate2() > ZERO || attendEmpOverViewVO.getLate3() > ZERO) {
                        flag = true;
                    }
                    break;
                case MISSCARD:
                case LEAVE:
                case OVERTIME:
                    if (integers.contains(status)) {
                        flag = true;
                    }
                    break;
                case OUT:
                    if (attendEmpOverViewVO.getIsOutCard() == IsEnum.YES.getValue()) {
                        flag = true;
                    }
                    break;
                case ALL:
                    flag = true;
                    break;
                default:
                    flag = false;
                    break;
            }
            if (flag) {
                map.put(date, attendEmpOverViewVO);
                mapList.add(map);
            }
        }
        queryAttendanceEmpDetailVO.setDateList(mapList);
        queryAttendanceEmpDetailVO.setStartTime(startTime);
        queryAttendanceEmpDetailVO.setEndTime(endTime);
        return queryAttendanceEmpDetailVO;
    }

    private Map<String, AttendEmpOverViewVO> queryAttendEmpMonthDetailByDate(Integer includeToday, List<HrmAttendanceClock> startClockList, List<HrmAttendanceClock> endClockList, List<String> dates, Long employeeId) {
        Map<String, AttendEmpOverViewVO> mapList = new HashMap<>();
        //查询员工入职日期
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        long entryTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime());
        long createTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate());
        long nowMilli = LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(employeeId);
        //查询出日期区间的班次
        Map<String, HrmAttendanceShift> hrmAttendanceShiftMap = getHrmAttendanceShiftMap(dates, hrmAttendanceGroup, employeeId);
        //查询出打卡记录
        Map<String, QueryAttendanceRecordVO> startAttendanceRecordMap = queryStartRecordList(startClockList, dates, hrmAttendanceShiftMap, employeeId, ONE);
        Map<String, QueryAttendanceRecordVO> endAttendanceRecordMap = queryEndRecordList(endClockList, dates, hrmAttendanceShiftMap, employeeId, ONE);
        for (String date : dates) {
            AttendEmpOverViewVO attendEmpOverViewVO = new AttendEmpOverViewVO();
            attendEmpOverViewVO.setAbsenteeism1(ZERO);
            attendEmpOverViewVO.setAbsenteeism2(ZERO);
            attendEmpOverViewVO.setAbsenteeism3(ZERO);
            attendEmpOverViewVO.setLate1(ZERO);
            attendEmpOverViewVO.setLate2(ZERO);
            attendEmpOverViewVO.setLate3(ZERO);
            attendEmpOverViewVO.setEarly1(ZERO);
            attendEmpOverViewVO.setEarly2(ZERO);
            attendEmpOverViewVO.setEarly3(ZERO);
            attendEmpOverViewVO.setIsOutCard(ZERO);
            QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
            queryAttendanceDailyDetailBO.setCurrentDate(LocalDate.parse(date).atStartOfDay().toLocalDate());
            queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
            HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
            attendEmpOverViewVO.setShiftType(hrmAttendanceShift.getShiftType());
            long currentMilli = LocalDateTimeUtil.toEpochMilli(DateUtil.parseDate(date).toLocalDateTime().toLocalDate());
            boolean currentDate;
            if (ObjectUtil.equal(ONE, includeToday)) {
                currentDate = nowMilli >= currentMilli;
            } else {
                currentDate = nowMilli > currentMilli;
            }
            boolean entryTime = entryTimeMilli <= currentMilli;
            boolean createTime = createTimeMilli <= currentMilli;
            if (currentDate && entryTime && createTime) {
                if (ObjectUtil.equal(ShiftTypeEnum.REST.getValue(), hrmAttendanceShift.getShiftType())) {
                    QueryAttendanceRecordVO startAttendanceRecordVO = startAttendanceRecordMap.get(date);
                    attendEmpOverViewVO.setStartStatus1(startAttendanceRecordVO.getStatus());
                    if (ObjectUtil.isNotNull(startAttendanceRecordVO.getClockTime())) {
                        attendEmpOverViewVO.setStart1(DateUtil.format(startAttendanceRecordVO.getClockTime(), "HH:mm"));
                    }
                    QueryAttendanceRecordVO endAttendanceRecordVO = endAttendanceRecordMap.get(date);
                    attendEmpOverViewVO.setEndStatus1(endAttendanceRecordVO.getStatus());
                    if (ObjectUtil.isNotNull(endAttendanceRecordVO.getClockTime())) {
                        attendEmpOverViewVO.setEnd1(DateUtil.format(endAttendanceRecordVO.getClockTime(), "HH:mm"));
                    }
                    attendEmpOverViewVO.setOtType1(TWO);
                    if (startAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue() || endAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue()) {
                        attendEmpOverViewVO.setIsOutCard(IsEnum.YES.getValue());
                    }
                } else if (ObjectUtil.equal(ShiftTypeEnum.COMMON.getValue(), hrmAttendanceShift.getShiftType())) {
                    QueryAttendanceRecordVO startAttendanceRecordVO = startAttendanceRecordMap.get(date);
                    attendEmpOverViewVO.setStartStatus1(startAttendanceRecordVO.getStatus());
                    if (ObjectUtil.isNotNull(startAttendanceRecordVO.getClockTime())) {
                        attendEmpOverViewVO.setStart1(DateUtil.format(startAttendanceRecordVO.getClockTime(), "HH:mm"));
                    }
                    if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue()) {
                        attendEmpOverViewVO.setStart1(hrmAttendanceShift.getStart1());
                        attendEmpOverViewVO.setLeaveType1(startAttendanceRecordVO.getLeaveType());
                    }
                    if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LATE.getValue()) {
                        attendEmpOverViewVO.setLate1(startAttendanceRecordVO.getMinutes());
                    }
                    QueryAttendanceRecordVO endAttendanceRecordVO = endAttendanceRecordMap.get(date);
                    attendEmpOverViewVO.setEndStatus1(endAttendanceRecordVO.getStatus());
                    if (ObjectUtil.isNotNull(endAttendanceRecordVO.getClockTime())) {
                        attendEmpOverViewVO.setEnd1(DateUtil.format(endAttendanceRecordVO.getClockTime(), "HH:mm"));
                    }
                    if (endAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue()) {
                        attendEmpOverViewVO.setEnd1(hrmAttendanceShift.getEnd1());
                    }
                    if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.EARLY.getValue()) {
                        attendEmpOverViewVO.setEarly1(endAttendanceRecordVO.getMinutes());
                    }
                    if (endAttendanceRecordVO.getStatus() == AttendanceResultEnum.OVERTIME.getValue()) {
                        attendEmpOverViewVO.setOtType1(ONE);
                        DateTime endTime = DateUtil.parseDateTime(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN) + " " + AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
                        attendEmpOverViewVO.setOtTime1((int) DateUtil.between(Timestamp.valueOf(endAttendanceRecordVO.getClockTime()), endTime, DateUnit.MINUTE));
                    }
                    if (attendEmpOverViewVO.getStartStatus1().equals(AttendanceResultEnum.MISSCARD.getValue()) && attendEmpOverViewVO.getEndStatus1().equals(AttendanceResultEnum.MISSCARD.getValue())) {
                        attendEmpOverViewVO.setAbsenteeism1(hrmAttendanceShift.getShiftHours());
                    }
                    if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue() && endAttendanceRecordVO.getStatus() != AttendanceResultEnum.LEAVE.getValue()) {
                        attendEmpOverViewVO.setLeaveEnd1(endAttendanceRecordVO.getLeaveEndTime());
                    }
                    if (startAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue() || endAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue()) {
                        attendEmpOverViewVO.setIsOutCard(IsEnum.YES.getValue());
                    }
                }
            }
            mapList.put(date, attendEmpOverViewVO);
        }
        return mapList;
    }


    @Override
    public BasePage<Map<String, Object>> queryOutCardPageList(QueryAttendanceOutCardBO queryAttendanceOutCardBO) {
        return getBaseMapper().queryOutCardPageList(queryAttendanceOutCardBO.parse(), queryAttendanceOutCardBO);
    }

    @Override
    public void downloadExcel(HttpServletResponse response) {
        List<JSONObject> list = new ArrayList<>();
        list.add(queryField("mobile", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "手机号", 1));
        list.add(queryField("clockTime", FieldEnum.DATETIME.getFormType(), FieldEnum.DATETIME.getType(), "打卡时间", 1));
        list.add(queryField("attendanceTime", FieldEnum.DATETIME.getFormType(), FieldEnum.DATETIME.getType(), "上班日期时间", 1));
        list.add(queryField("clockType", FieldEnum.SELECT.getFormType(), FieldEnum.SELECT.getType(), "打卡类型", 1).
                fluentPut("setting", Arrays.asList(ClockType.GO_TO.getName(), ClockType.GET_OFF.getName())));
        list.add(queryField("type", FieldEnum.SELECT.getFormType(), FieldEnum.SELECT.getType(), "打卡来源", 1).
                fluentPut("setting", Arrays.asList(ClockOperType.APP.getName(), ClockOperType.HR.getName(), ClockOperType.AUTO.getName())));
        list.add(queryField("clockStatus", FieldEnum.SELECT.getFormType(), FieldEnum.SELECT.getType(), "打卡状态", 1).
                fluentPut("setting", Arrays.asList(ClockStatusEnum.NORMAL.getName(), ClockStatusEnum.EARLY.getName(), ClockStatusEnum.LATE.getName(), ClockStatusEnum.OUT.getName(), ClockStatusEnum.OVERTIME.getName())));
        list.add(queryField("clockStage", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "打卡阶段", 1));
        list.add(queryField("address", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "打卡地址", 1));
        list.add(queryField("lng", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "经度", 1));
        list.add(queryField("lat", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "纬度", 1));
        list.add(queryField("remark", FieldEnum.TEXT.getFormType(), FieldEnum.TEXT.getType(), "备注", 0));
        ExcelParseUtil.importExcel(new ExcelParseUtil.ExcelParseService() {
            @Override
            public String getExcelName() {
                return "考勤原始记录";
            }
        }, list, response, "attendance");
    }

    @Override
    public QueryMyCurrentClockVO queryMyCurrentClock(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        String attendanceShiftTitle = "";
        QueryMyCurrentClockVO queryMyCurrentClockVO = new QueryMyCurrentClockVO();
        HrmAttendanceShiftVO hrmAttendanceShift = queryMyCurrentShift(queryAttendanceDailyDetailBO);
        if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
            hrmAttendanceShift.setStart1(AttendUtil.convertTime(hrmAttendanceShift.getStart1()));
            hrmAttendanceShift.setEnd1(AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
            hrmAttendanceShift.setAdvanceCard1(AttendUtil.convertTime(hrmAttendanceShift.getAdvanceCard1()));
            hrmAttendanceShift.setLateCard1(AttendUtil.convertTime(hrmAttendanceShift.getLateCard1()));
            hrmAttendanceShift.setEarlyCard1(AttendUtil.convertTime(hrmAttendanceShift.getEarlyCard1()));
            hrmAttendanceShift.setPostponeCard1(AttendUtil.convertTime(hrmAttendanceShift.getPostponeCard1()));
            if (hrmAttendanceShift.getRestTimeStatus() == IsEnum.YES.getValue()) {
                hrmAttendanceShift.setRestStartTime(AttendUtil.convertTime(hrmAttendanceShift.getRestStartTime()));
                hrmAttendanceShift.setRestEndTime(AttendUtil.convertTime(hrmAttendanceShift.getRestEndTime()));
            }
        }
        queryMyCurrentClockVO.setHrmAttendanceShiftVO(hrmAttendanceShift);
        HrmAttendanceGroup queryAttendanceGroup = attendanceGroupService.queryAttendanceGroup(queryAttendanceDailyDetailBO.getEmployeeId());
        if (queryAttendanceGroup.getIsOpenPointCard() == IsEnum.YES.getValue()) {
            List<HrmAttendancePoint> pointList = attendancePointService.lambdaQuery().eq(HrmAttendancePoint::getAttendanceGroupId, queryAttendanceGroup.getAttendanceGroupId()).list();
            queryMyCurrentClockVO.setPointList(pointList);
        }
        if (queryAttendanceGroup.getIsOpenWifiCard() == IsEnum.YES.getValue()) {
            List<HrmAttendanceWifi> wifiList = attendanceWifiService.lambdaQuery().eq(HrmAttendanceWifi::getAttendanceGroupId, queryAttendanceGroup.getAttendanceGroupId()).list();
            queryMyCurrentClockVO.setWifiList(wifiList);
        }
        queryMyCurrentClockVO.setIsAutoCard(queryAttendanceGroup.getIsAutoCard());
        if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
            attendanceShiftTitle = hrmAttendanceShift.getShiftName() + ":" + AttendUtil.convertTimeDivision(hrmAttendanceShift.getStart1()) + "-" + AttendUtil.convertTimeDivision(hrmAttendanceShift.getEnd1());
            if (hrmAttendanceShift.getRestTimeStatus() == IsEnum.YES.getValue()) {
                attendanceShiftTitle = attendanceShiftTitle + " " + "休息:" + AttendUtil.convertTimeDivision(hrmAttendanceShift.getRestStartTime()) + "-" + AttendUtil.convertTimeDivision(hrmAttendanceShift.getRestEndTime());
            }
        } else {
            attendanceShiftTitle = hrmAttendanceShift.getShiftName();
        }
        queryMyCurrentClockVO.setAttendanceShiftDate(DateUtil.parseDate(hrmAttendanceShift.getAttendanceShiftDate()).toLocalDateTime().toLocalDate());
        queryMyCurrentClockVO.setAttendanceShiftTitle(attendanceShiftTitle);
        return queryMyCurrentClockVO;
    }

    private HrmAttendanceShiftVO queryMyCurrentShift(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        // 昨日
        LocalDate oldDate = LocalDateTimeUtil.offset(LocalDateTime.now(), -1, ChronoUnit.DAYS).toLocalDate();
        // 今日
        LocalDateTime date = LocalDateTime.now();
        int hour = date.getHour();
        // 当前分钟
        int minute = date.getMinute();
        queryAttendanceDailyDetailBO.setCurrentDate(oldDate);
        HrmAttendanceShift oldHrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        queryAttendanceDailyDetailBO.setCurrentDate(date.toLocalDate());
        if (ObjectUtil.equal(oldHrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
            //最早打卡
            String[] start1 = oldHrmAttendanceShift.getAdvanceCard1().split(":");
            //最晚打卡
            String[] end1 = oldHrmAttendanceShift.getPostponeCard1().split(":");
            //获取到小时
            int startHour1 = Integer.valueOf(start1[0]).intValue();
            int endHour1 = Integer.valueOf(end1[0]).intValue();
            //获取对应的分钟
            int startMinute1 = Integer.valueOf(start1[1]).intValue();
            int endMinute1 = Integer.valueOf(end1[1]).intValue();
            if (startHour1 >= endHour1) {
                // 打卡区间跨次日
                if (startMinute1 > endMinute1) {
                    if (hour == endHour1) {
                        if (minute < endMinute1) {
                            //昨日打卡范围
                            queryAttendanceDailyDetailBO.setCurrentDate(oldDate);
                        }
                    } else if (hour < endHour1) {
                        queryAttendanceDailyDetailBO.setCurrentDate(oldDate);
                    }
                }
            }
        }
        HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
        hrmAttendanceShiftVO.setAttendanceShiftDate(queryAttendanceDailyDetailBO.getCurrentDate().format(DateTimeFormatter.ISO_DATE));
        return hrmAttendanceShiftVO;
    }

    private JSONObject queryField(String fieldName, String formType, Integer type, String name, Integer isNull) {
        JSONObject json = new JSONObject();
        json.fluentPut("fieldName", fieldName)
                .fluentPut("formType", formType)
                .fluentPut("type", type)
                .fluentPut("name", name).fluentPut("isNull", isNull);
        return json;
    }

    private AttendEmpOverViewVO queryAttendEmpMonthDetail(String date, Long employeeId) {
        AttendEmpOverViewVO attendEmpOverViewVO = new AttendEmpOverViewVO();
        attendEmpOverViewVO.setAbsenteeism1(ZERO);
        attendEmpOverViewVO.setAbsenteeism2(ZERO);
        attendEmpOverViewVO.setAbsenteeism3(ZERO);
        attendEmpOverViewVO.setLate1(ZERO);
        attendEmpOverViewVO.setLate2(ZERO);
        attendEmpOverViewVO.setLate3(ZERO);
        attendEmpOverViewVO.setEarly1(ZERO);
        attendEmpOverViewVO.setEarly2(ZERO);
        attendEmpOverViewVO.setEarly3(ZERO);
        attendEmpOverViewVO.setIsOutCard(ZERO);
        QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
        queryAttendanceDailyDetailBO.setCurrentDate(LocalDate.parse(date).atStartOfDay().toLocalDate());
        queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
        HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        attendEmpOverViewVO.setShiftType(hrmAttendanceShift.getShiftType());
        HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
        //查询员工入职日期
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        boolean currentDate = LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate()) < LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        boolean entryTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        boolean createTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        if (currentDate && entryTime && createTime) {
            if (ObjectUtil.equal(ShiftTypeEnum.REST.getValue(), hrmAttendanceShift.getShiftType())) {
                QueryAttendanceRecordVO startAttendanceRecordVO = queryStartRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, ONE);
                attendEmpOverViewVO.setStartStatus1(startAttendanceRecordVO.getStatus());
                if (ObjectUtil.isNotNull(startAttendanceRecordVO.getClockTime())) {
                    attendEmpOverViewVO.setStart1(DateUtil.format(startAttendanceRecordVO.getClockTime(), "HH:mm"));
                }
                QueryAttendanceRecordVO endAttendanceRecordVO = queryEndRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, ONE);
                attendEmpOverViewVO.setEndStatus1(endAttendanceRecordVO.getStatus());
                if (ObjectUtil.isNotNull(endAttendanceRecordVO.getClockTime())) {
                    attendEmpOverViewVO.setEnd1(DateUtil.format(endAttendanceRecordVO.getClockTime(), "HH:mm"));
                }
                attendEmpOverViewVO.setOtType1(TWO);
                if (startAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue() || endAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue()) {
                    attendEmpOverViewVO.setIsOutCard(IsEnum.YES.getValue());
                }
            } else if (ObjectUtil.equal(ShiftTypeEnum.COMMON.getValue(), hrmAttendanceShift.getShiftType())) {
                QueryAttendanceRecordVO startAttendanceRecordVO = queryStartRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, ONE);
                attendEmpOverViewVO.setStartStatus1(startAttendanceRecordVO.getStatus());
                if (ObjectUtil.isNotNull(startAttendanceRecordVO.getClockTime())) {
                    attendEmpOverViewVO.setStart1(DateUtil.format(startAttendanceRecordVO.getClockTime(), "HH:mm"));
                }
                if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue()) {
                    attendEmpOverViewVO.setStart1(hrmAttendanceShift.getStart1());
                    attendEmpOverViewVO.setLeaveType1(startAttendanceRecordVO.getLeaveType());
                }
                if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LATE.getValue()) {
                    attendEmpOverViewVO.setLate1(startAttendanceRecordVO.getMinutes());
                }
                QueryAttendanceRecordVO endAttendanceRecordVO = queryEndRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, ONE);
                attendEmpOverViewVO.setEndStatus1(endAttendanceRecordVO.getStatus());
                if (ObjectUtil.isNotNull(endAttendanceRecordVO.getClockTime())) {
                    attendEmpOverViewVO.setEnd1(DateUtil.format(endAttendanceRecordVO.getClockTime(), "HH:mm"));
                }
                if (endAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue()) {
                    attendEmpOverViewVO.setEnd1(hrmAttendanceShift.getEnd1());
                }
                if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.EARLY.getValue()) {
                    attendEmpOverViewVO.setEarly1(endAttendanceRecordVO.getMinutes());
                }
                if (endAttendanceRecordVO.getStatus() == AttendanceResultEnum.OVERTIME.getValue()) {
                    attendEmpOverViewVO.setOtType1(ONE);
                    DateTime endTime = DateUtil.parseDateTime(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN) + " " + AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
                    attendEmpOverViewVO.setOtTime1((int) DateUtil.between(Timestamp.valueOf(endAttendanceRecordVO.getClockTime()), endTime, DateUnit.MINUTE));
                }
                if (attendEmpOverViewVO.getStartStatus1().equals(AttendanceResultEnum.MISSCARD.getValue()) && attendEmpOverViewVO.getEndStatus1().equals(AttendanceResultEnum.MISSCARD.getValue())) {
                    attendEmpOverViewVO.setAbsenteeism1(hrmAttendanceShift.getShiftHours());
                }
                if (startAttendanceRecordVO.getStatus() == AttendanceResultEnum.LEAVE.getValue() && endAttendanceRecordVO.getStatus() != AttendanceResultEnum.LEAVE.getValue()) {
                    attendEmpOverViewVO.setLeaveEnd1(endAttendanceRecordVO.getLeaveEndTime());
                }
                if (startAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue() || endAttendanceRecordVO.getIsOutCard() == IsEnum.YES.getValue()) {
                    attendEmpOverViewVO.setIsOutCard(IsEnum.YES.getValue());
                }
            }
        }

        return attendEmpOverViewVO;
    }

    private Map<String, Object> queryRecordDetailByParams(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO, HrmAttendanceShiftVO hrmAttendanceShiftVO, Integer clockStage) {
        Map<String, Object> map = new HashMap<>();
        QueryAttendanceRecordVO startAttendanceRecordVO = queryStartRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, clockStage);
        QueryAttendanceRecordVO endAttendanceRecordVO = queryEndRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, clockStage);
        map.put("startStatus", startAttendanceRecordVO.getStatus());
        map.put("startMinutes", startAttendanceRecordVO.getMinutes() != null ? startAttendanceRecordVO.getMinutes() : 0);
        map.put("endStatus", endAttendanceRecordVO.getStatus());
        map.put("endMinutes", endAttendanceRecordVO.getMinutes() != null ? endAttendanceRecordVO.getMinutes() : 0);
        map.put("startIsOutCard", startAttendanceRecordVO.getIsOutCard() != null ? startAttendanceRecordVO.getIsOutCard() : 0);
        map.put("endIsOutCard", endAttendanceRecordVO.getIsOutCard() != null ? endAttendanceRecordVO.getIsOutCard() : 0);
        map.put("leaveMinutes", endAttendanceRecordVO.getLeaveMinutes() != null ? endAttendanceRecordVO.getLeaveMinutes() : 0);
        if (startAttendanceRecordVO.getStatus().equals(THREE) && endAttendanceRecordVO.getStatus().equals(THREE)) {
            if (ObjectUtil.equal(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                map.put("absenteeismMinutes", hrmAttendanceShiftVO.getShiftHours());
            } else {
                BigDecimal absenteeismMinutes;
                String end = hrmAttendanceShiftVO.getEnd1();
                String start = hrmAttendanceShiftVO.getStart1();
                if (clockStage == TWO) {
                    end = hrmAttendanceShiftVO.getEnd2();
                    start = hrmAttendanceShiftVO.getStart2();
                } else if (clockStage == THREE) {
                    end = hrmAttendanceShiftVO.getEnd3();
                    start = hrmAttendanceShiftVO.getStart3();
                }
                DateTime startTime = DateUtil.parseDateTime(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN) + " " + AttendUtil.convertTime(start));
                DateTime endTime = DateUtil.parseDateTime(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN) + " " + AttendUtil.convertTime(end));
                absenteeismMinutes = new BigDecimal(DateUtil.between(endTime, startTime, DateUnit.MINUTE));
                map.put("absenteeismMinutes", absenteeismMinutes);
            }

        } else {
            map.put("absenteeismMinutes", ZERO);
        }
        return map;
    }

    @Override
    public QueryAttendanceDailyDetailVO queryAttendanceDailyDetail(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        QueryAttendanceDailyDetailVO queryAttendanceDailyDetailVO = new QueryAttendanceDailyDetailVO();
        List<QueryAttendanceRecordVO> attendanceEmpDailyRecordList = new ArrayList<>();
        HrmEmployee hrmEmployee = employeeService.getById(queryAttendanceDailyDetailBO.getEmployeeId());
        queryAttendanceDailyDetailVO.setEmployeeName(hrmEmployee.getEmployeeName());
        boolean entryTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        boolean createTime = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate()) <= LocalDateTimeUtil.toEpochMilli(queryAttendanceDailyDetailBO.getCurrentDate());
        if (entryTime && createTime) {
            //获取今天对应的班次
            HrmAttendanceShift hrmAttendanceShift = getHrmAttendanceShift(queryAttendanceDailyDetailBO);
            HrmAttendanceShiftVO hrmAttendanceShiftVO = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceShiftVO.class);
            queryAttendanceDailyDetailVO.setHrmAttendanceShiftVO(hrmAttendanceShiftVO);
            //同步OA最新请假记录
            leaveRecordService.queryOaLeaveExamineList();
            //查询当日是否存在请假
            List<HrmEmployeeLeaveRecord> hrmEmployeeLeaveRecordList = leaveRecordService.queryLeaveRecord(queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay(), queryAttendanceDailyDetailBO.getEmployeeId());
            if (hrmEmployeeLeaveRecordList.size() > ZERO) {
                queryAttendanceDailyDetailVO.setHrmEmployeeLeaveRecordLists(hrmEmployeeLeaveRecordList);
            }
            //计算考勤明细
            if (ObjectUtil.notEqual(ShiftTypeEnum.SUBSECTION.getValue(), hrmAttendanceShiftVO.getShiftType())) {
                //查询上班卡
                QueryAttendanceRecordVO startAttendanceRecordVO = queryStartRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, 1);
                if (ObjectUtil.notEqual(AttendanceResultEnum.REST.getValue(), startAttendanceRecordVO.getStatus())) {
                    attendanceEmpDailyRecordList.add(startAttendanceRecordVO);
                }
                //查询下班卡
                QueryAttendanceRecordVO endAttendanceRecordVO = queryEndRecordByClockStage(queryAttendanceDailyDetailBO, null, hrmAttendanceShiftVO, 1);
                if (ObjectUtil.notEqual(AttendanceResultEnum.REST.getValue(), endAttendanceRecordVO.getStatus())) {
                    attendanceEmpDailyRecordList.add(endAttendanceRecordVO);
                }
            }
        }
        queryAttendanceDailyDetailVO.setAttendanceEmpDailyRecordList(attendanceEmpDailyRecordList);
        return queryAttendanceDailyDetailVO;
    }

    @Override
    public HrmAttendanceShift getHrmAttendanceShift(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        //查询班次
        HrmAttendanceDateShift hrmAttendanceDateShift = attendanceDateShiftService.lambdaQuery()
                .eq(HrmAttendanceDateShift::getEmployeeId, queryAttendanceDailyDetailBO.getEmployeeId())
                .le(HrmAttendanceDateShift::getEffectTime, LocalDate.now())
                .eq(HrmAttendanceDateShift::getUserShiftTime, queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay()).last(" limit 1").one();
        if (ObjectUtil.isNotNull(hrmAttendanceDateShift)) {
            HrmAttendanceShift hrmAttendanceShift = BeanUtil.copyProperties(hrmAttendanceDateShift, HrmAttendanceShift.class);
            return hrmAttendanceShift;
        }
        HrmAttendanceGroup hrmAttendanceGroup;
        if (ObjectUtil.isNotNull(queryAttendanceDailyDetailBO.getHrmAttendanceGroup())) {
            hrmAttendanceGroup = queryAttendanceDailyDetailBO.getHrmAttendanceGroup();
        } else {
            hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(queryAttendanceDailyDetailBO.getEmployeeId());
        }
        // 查询出特殊日期
        Map<String, Object> dateValueMap = new HashMap<>();
        if (BaseUtil.isJSONArray(hrmAttendanceGroup.getSpecialDateSetting())) {
            JSONArray array = JSON.parseArray(hrmAttendanceGroup.getSpecialDateSetting());
            for (int j = 0; j < array.size(); j++) {
                dateValueMap.put(array.getJSONObject(j).getString("date"), array.getJSONObject(j).getString("shift"));
            }
        }
        //查询出所有法定节假日
        if (hrmAttendanceGroup.getIsRest() == IsEnum.YES.getValue()) {
            List<HrmAttendanceLegalHolidays> list = attendanceLegalHolidaysService.queryLegalHolidayList();
            if (CollUtil.isNotEmpty(list)) {
                //查询出默认设置的休息班次
                HrmAttendanceShift hrmAttendanceShift1 = attendanceShiftService.lambdaQuery().select(HrmAttendanceShift::getShiftId)
                        .eq(HrmAttendanceShift::getShiftType, ShiftTypeEnum.REST.getValue()).eq(HrmAttendanceShift::getIsDefaultSetting, IsEnum.YES.getValue()).one();
                list.forEach(attendanceLegalHolidays -> dateValueMap.put(DateUtil.formatDate(Timestamp.valueOf(attendanceLegalHolidays.getHolidayTime())), hrmAttendanceShift1.getShiftId()));
            }
        }
        List<String> shiftList = StrUtil.splitTrim(hrmAttendanceGroup.getShiftSetting(), Const.SEPARATOR);
        Integer dayOfWeek = LocalDateTimeUtil.dayOfWeek(queryAttendanceDailyDetailBO.getCurrentDate()).getValue();
        dayOfWeek = dayOfWeek.equals(1) ? 7 : dayOfWeek - 1;
        String shiftId = shiftList.get(dayOfWeek - 1);
        //获取今天对应得班次
        HrmAttendanceShift hrmAttendanceShift;
        Object shift = dateValueMap.get(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN));
        if (ObjectUtil.isNotEmpty(shift)) {
            hrmAttendanceShift = attendanceShiftService.getById(Convert.toLong(shift.toString()));
        } else {
            hrmAttendanceShift = attendanceShiftService.getById(shiftId);
        }
        if (ObjectUtil.isNull(hrmAttendanceShift)) {
            //未匹配到班次，则查询默认班次
            hrmAttendanceShift = attendanceShiftService.lambdaQuery().eq(HrmAttendanceShift::getIsDefaultSetting, IsEnum.YES).orderByDesc(HrmAttendanceShift::getCreateTime).last("limit 1").one();
        }
        if (hrmAttendanceShift.getEffectTime().isAfter(LocalDateTime.now())) {
            //查询到的班次未生效,去查询历史班次
            HrmAttendanceHistoryShift hrmAttendanceHistoryShift = attendanceHistoryShiftService.lambdaQuery()
                    .eq(HrmAttendanceHistoryShift::getShiftId, hrmAttendanceShift.getShiftId()).orderByDesc(HrmAttendanceHistoryShift::getUpdateTime).one();
            hrmAttendanceShift = BeanUtil.copyProperties(hrmAttendanceHistoryShift, HrmAttendanceShift.class);
        }
        LocalDateTime localDateTime = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        if (ObjectUtil.equal(queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay(), LocalDate.now().atStartOfDay())) {
            HrmAttendanceDateShift dateShift = BeanUtil.copyProperties(hrmAttendanceShift, HrmAttendanceDateShift.class);
            dateShift.setEffectTime(localDateTime);
            dateShift.setEmployeeId(queryAttendanceDailyDetailBO.getEmployeeId());
            dateShift.setUserShiftTime(localDateTime);
            attendanceDateShiftService.save(dateShift);
        }
        return hrmAttendanceShift;
    }

    @Override
    public Integer queryEmpAttendanceOverTimeCountDays(List<LocalDate> times, Long employeeId) {
        LocalDate localDate = times.get(1);
        LocalDate now = LocalDate.now();
        if (LocalDateTimeUtil.toEpochMilli(localDate) >= LocalDateTimeUtil.toEpochMilli(now)) {
            //统计不统计当天
            times.set(1, now.plusDays(-1));
        }
        return getBaseMapper().queryEmpAttendanceOverTimeCountDays(times, employeeId);
    }

    @Override
    public BasePage<QueryAttendanceEmpMonthDetailVO> queryAttendanceEmpMonthDetailPageList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO) {
        List<QueryAttendanceEmpMonthDetailVO> empMonthRecordVOList = new ArrayList<>();
        BasePage<QueryEmployeeAttendanceVO> employeeAttendanceBasePage = queryAttendanceEmpList(queryAttendanceEmpMonthRecordBO);
        List<QueryEmployeeAttendanceVO> queryEmployeeAttendanceList = employeeAttendanceBasePage.getList();
        BasePage<QueryAttendanceEmpMonthDetailVO> page = new BasePage<>(employeeAttendanceBasePage.getCurrent(), employeeAttendanceBasePage.getSize(), employeeAttendanceBasePage.getTotal());
        if (CollUtil.isNotEmpty(queryEmployeeAttendanceList)) {
            //查询请假记录
            leaveRecordService.queryOaLeaveExamineList();
            //校验是否存在默认考勤组
            attendanceGroupService.checkInitAttendData();
            //查询出范围内的日期
            List<String> dates = findDates(queryAttendanceEmpMonthRecordBO.getTimes().get(0), queryAttendanceEmpMonthRecordBO.getTimes().get(1));
            UserInfo userInfo = UserUtil.getUser();
            List<Long> employeeIds = queryEmployeeAttendanceList.stream().map(QueryEmployeeAttendanceVO::getEmployeeId).collect(Collectors.toList());
            //查询出日期区间所有的上班打卡记录
            LocalDateTime startDateTime = queryAttendanceEmpMonthRecordBO.getTimes().get(0).atStartOfDay();
            LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(queryAttendanceEmpMonthRecordBO.getTimes().get(1).atStartOfDay());
            List<HrmAttendanceClock> startClockList = queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
            Map<Long, List<HrmAttendanceClock>> startEmployeeClockMap = startClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
            //查询出日期区间所有的下班打卡记录
            List<HrmAttendanceClock> endClockList = queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
            Map<Long, List<HrmAttendanceClock>> endEmployeeClockMap = endClockList.stream().collect(Collectors.groupingBy(HrmAttendanceClock::getClockEmployeeId));
            queryEmployeeAttendanceList.stream().parallel().forEach(queryEmployeeAttendanceVO -> {
                UserUtil.setUser(userInfo);
                QueryAttendanceEmpMonthDetailVO empMonthDetailVO = new QueryAttendanceEmpMonthDetailVO();
                BeanUtil.copyProperties(queryEmployeeAttendanceVO, empMonthDetailVO);
                List<HrmAttendanceClock> startEmployeeClockList = startEmployeeClockMap.get(queryEmployeeAttendanceVO.getEmployeeId());
                List<HrmAttendanceClock> endEmployeeClockList = endEmployeeClockMap.get(queryEmployeeAttendanceVO.getEmployeeId());
                List<Map<String, Object>> mapList = queryAttendanceEmpDetailByDate(startEmployeeClockList, endEmployeeClockList, dates, empMonthDetailVO.getEmployeeId());
                empMonthDetailVO.setDateList(mapList);
                empMonthRecordVOList.add(empMonthDetailVO);
            });
            //排序
            List<QueryAttendanceEmpMonthDetailVO> collect = empMonthRecordVOList.stream().sorted(Comparator.comparingLong(empMonthDetailVO -> empMonthDetailVO.getEmployeeId())).collect(Collectors.toList());
            UserUtil.removeUser();
            page.setList(collect);
        }
        return page;
    }

    private List<Map<String, Object>> queryAttendanceEmpDetailByDate(List<HrmAttendanceClock> startEmployeeClockList, List<HrmAttendanceClock> endEmployeeClockList, List<String> dates, Long employeeId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        HrmEmployee hrmEmployee = employeeService.getById(employeeId);
        long entryTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getEntryTime());
        long createTimeMilli = LocalDateTimeUtil.toEpochMilli(hrmEmployee.getCreateTime().toLocalDate());
        long nowMilli = LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
        //查询出员工考勤组
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(employeeId);
        //查询出日期区间的班次
        Map<String, HrmAttendanceShift> hrmAttendanceShiftMap = getHrmAttendanceShiftMap(dates, hrmAttendanceGroup, employeeId);
        //查询出打卡记录
        Map<String, QueryAttendanceRecordVO> startAttendanceRecordMap = queryStartRecordList(startEmployeeClockList, dates, hrmAttendanceShiftMap, employeeId, ONE);
        Map<String, QueryAttendanceRecordVO> endAttendanceRecordMap = queryEndRecordList(endEmployeeClockList, dates, hrmAttendanceShiftMap, employeeId, ONE);
        for (String date : dates) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            long currentMilli = LocalDateTimeUtil.toEpochMilli(DateUtil.parseDate(date).toLocalDateTime().toLocalDate());
            String startTimeStatus;
            String endTimeStatus;
            boolean currentDate = currentMilli > nowMilli;
            boolean entryTime = entryTimeMilli > currentMilli;
            boolean createTime = createTimeMilli > currentMilli;
            if (entryTime || currentDate || createTime) {
                //时间大于今天或者员工入职大于今天和创建时间大于今天的汇总不进行统计
                map.put("time", new String[]{});
            } else {
                QueryAttendanceRecordVO startAttendanceRecordVO = startAttendanceRecordMap.get(date);
                QueryAttendanceRecordVO endAttendanceRecordVO = endAttendanceRecordMap.get(date);
                if (ObjectUtil.isNotNull(startAttendanceRecordVO.getClockTime())) {
                    startTimeStatus = DateUtil.format(startAttendanceRecordVO.getClockTime(), "HH:mm") + "-" + startAttendanceRecordVO.getStatus();
                } else {
                    startTimeStatus = "-" + startAttendanceRecordVO.getStatus();
                }
                if (ObjectUtil.isNotNull(endAttendanceRecordVO.getClockTime())) {
                    endTimeStatus = DateUtil.format(endAttendanceRecordVO.getClockTime(), "HH:mm") + "-" + endAttendanceRecordVO.getStatus();
                } else {
                    endTimeStatus = "-" + endAttendanceRecordVO.getStatus();
                }
                String timeStatus = "-3";
                String timeStatusNine = "-9";
                if (timeStatus.equals(startTimeStatus) && timeStatus.equals(endTimeStatus)) {
                    map.put("time", new String[]{"-4"});
                } else if (timeStatusNine.equals(startTimeStatus) && timeStatusNine.equals(endTimeStatus)) {
                    map.put("time", new String[]{"-9"});
                } else {
                    map.put("time", new String[]{startTimeStatus, endTimeStatus});
                }
            }
            mapList.add(map);
        }
        return mapList;

    }

    @Override
    public BasePage<QueryEmployeeAttendanceVO> queryAttendanceEmpList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ATTENDANCE_MENU_ID);
        String sortField = StrUtil.isNotEmpty(queryAttendanceEmpMonthRecordBO.getSortField()) ? StrUtil.toUnderlineCase(queryAttendanceEmpMonthRecordBO.getSortField()) : null;
        queryAttendanceEmpMonthRecordBO.setSortField(sortField);
        BasePage<QueryEmployeeAttendanceVO> employeeAttendanceBasePage = getBaseMapper().queryAttendanceEmpPageList(queryAttendanceEmpMonthRecordBO.parse(), queryAttendanceEmpMonthRecordBO, employeeIds);
        return employeeAttendanceBasePage;
    }

    @Override
    public List<String> findDates(LocalDate start, LocalDate end) {
        String startStr = LocalDateTimeUtil.format(start, DatePattern.NORM_DATE_PATTERN);
        String endStr = LocalDateTimeUtil.format(end, DatePattern.NORM_DATE_PATTERN);
        //装返回的日期集合容器
        List<String> Datelist = new ArrayList<String>();
        LocalDate startDate = LocalDate.parse(startStr);
        LocalDate endDate = LocalDate.parse(endStr);
        long distance = ChronoUnit.DAYS.between(startDate, endDate);

        Stream.iterate(startDate, date -> date.plusDays(1)).
                limit(distance + 1).
                forEach(day -> Datelist.add(day.toString()));
        return Datelist;
    }

    /**
     * 查询下班打卡结果
     *
     * @param queryAttendanceDailyDetailBO
     * @param hrmAttendanceShiftVO
     * @param clockStage
     * @return
     */
    private QueryAttendanceRecordVO queryEndRecordByClockStage(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO, HrmAttendanceClock endAttendanceClock, HrmAttendanceShiftVO hrmAttendanceShiftVO, Integer clockStage) {
        QueryAttendanceRecordVO endAttendanceRecordVO = new QueryAttendanceRecordVO();
        endAttendanceRecordVO.setIsOutCard(IsEnum.NO.getValue());
        endAttendanceRecordVO.setShiftType(hrmAttendanceShiftVO.getShiftType());
        endAttendanceRecordVO.setClockType(TWO);
        endAttendanceRecordVO.setClockStage(clockStage);
        endAttendanceRecordVO.setStatus(AttendanceResultEnum.MISSCARD.getValue());
        if (ObjectUtil.isNull(endAttendanceClock) && ObjectUtil.notEqual(ONE, queryAttendanceDailyDetailBO.getMulti())) {
            endAttendanceClock = lambdaQuery().eq(HrmAttendanceClock::getClockType, ClockType.GET_OFF.getValue()).eq(HrmAttendanceClock::getClockEmployeeId, queryAttendanceDailyDetailBO.getEmployeeId())
                    .eq(HrmAttendanceClock::getClockStage, clockStage)
                    .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN)).orderByDesc(HrmAttendanceClock::getCreateTime).one();
        }
        if (ObjectUtil.notEqual(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.REST.getValue())) {
            //正常状态
            AttendShiftTimeVO attendShiftTimeVO = queryCurrentAttendShiftTime(hrmAttendanceShiftVO, clockStage, queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay());
            String end = AttendUtil.convertTimeDivision(attendShiftTimeVO.getEnd());
            //班次开始时间
            LocalDateTime startTime = attendShiftTimeVO.getStartTime();
            //班次结束时间
            LocalDateTime endTime = attendShiftTimeVO.getEndTime();
            //最晚打卡时间
            LocalDateTime postponeTime = attendShiftTimeVO.getPostponeTime();
            //出勤时间点
            endAttendanceRecordVO.setAttendanceTime(endTime);
            //判断有无请假记录
            HrmEmployeeLeaveRecord hrmEmployeeLeaveRecord = leaveRecordService.queryStartOrEndLeaveRecord(endTime, queryAttendanceDailyDetailBO.getEmployeeId());
            if (hrmEmployeeLeaveRecord != null) {
                //存在请假时间
                endAttendanceRecordVO.setLeaveType(hrmEmployeeLeaveRecord.getLeaveType());
                if (LocalDateTimeUtil.toEpochMilli(endTime) <= LocalDateTimeUtil.toEpochMilli(hrmEmployeeLeaveRecord.getLeaveEndTime())) {
                    //请假时间大于当天班次
                    endAttendanceRecordVO.setStatus(AttendanceResultEnum.LEAVE.getValue());
                    endAttendanceRecordVO.setLeaveEndTime(end);
                    if (ObjectUtil.equal(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                        //早晚打卡的请假时间（分钟）
                        endAttendanceRecordVO.setLeaveMinutes(hrmAttendanceShiftVO.getShiftHours());
                    } else {
                        endAttendanceRecordVO.setLeaveMinutes((int) LocalDateTimeUtil.between(startTime, endTime).toMinutes());
                    }
                } else {
                    Integer laveMinutes = ZERO;
                    //获取到请假的当天时间
                    String toDay = LocalDateTimeUtil.format(hrmEmployeeLeaveRecord.getLeaveEndTime(), DatePattern.NORM_DATE_PATTERN);
                    if (ObjectUtil.equal(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                        long minutes = DateUtil.between(Timestamp.valueOf(hrmEmployeeLeaveRecord.getLeaveEndTime()), DateUtil.parseDateTime(toDay + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getStart1())), DateUnit.MINUTE);
                        laveMinutes = (int) minutes;
                        if (hrmAttendanceShiftVO.getRestTimeStatus() == IsEnum.YES.getValue()) {
                            //如果有休息时间，则减去休息时间
                            DateTime restStartTime = DateUtil.parseDateTime(toDay + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getRestStartTime()));
                            DateTime restEndTime = DateUtil.parseDateTime(toDay + AttendUtil.convertTime(hrmAttendanceShiftVO.getRestEndTime()));
                            if (LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.parse(DateUtil.formatDateTime(restEndTime), DatePattern.NORM_DATETIME_PATTERN)) < LocalDateTimeUtil.toEpochMilli(hrmEmployeeLeaveRecord.getLeaveEndTime())) {
                                //请假结束时间大于休息结束时间
                                long restMinutes = DateUtil.between(restStartTime, restEndTime, DateUnit.MINUTE);
                                laveMinutes = laveMinutes - (int) restMinutes;
                            } else if (LocalDateTimeUtil.toEpochMilli(LocalDateTimeUtil.parse(DateUtil.formatDateTime(restStartTime), DatePattern.NORM_DATETIME_PATTERN)) < LocalDateTimeUtil.toEpochMilli(hrmEmployeeLeaveRecord.getLeaveEndTime())) {
                                //请假结束时间大于休息开始时间
                                long restMinutes = LocalDateTimeUtil.between(LocalDateTimeUtil.parse(DateUtil.formatDateTime(restStartTime), DatePattern.NORM_DATETIME_PATTERN), hrmEmployeeLeaveRecord.getLeaveEndTime()).toMinutes();
                                laveMinutes = laveMinutes - (int) restMinutes;
                            }
                        }
                    }
                    endAttendanceRecordVO.setLeaveEndTime(DateUtil.format(hrmEmployeeLeaveRecord.getLeaveEndTime(), "HH:mm"));
                    endAttendanceRecordVO.setLeaveMinutes(laveMinutes);
                }
                if (endAttendanceClock != null) {
                    endAttendanceRecordVO.setClockTime(endAttendanceClock.getClockTime());
                    endAttendanceRecordVO.setAttendanceTime(endAttendanceClock.getAttendanceTime());
                    endAttendanceRecordVO.setType(endAttendanceClock.getType());
                    endAttendanceRecordVO.setAddress(endAttendanceClock.getAddress());
                    endAttendanceRecordVO.setWifi(endAttendanceClock.getSsid());
                    endAttendanceRecordVO.setRemark(endAttendanceClock.getRemark());
                    endAttendanceRecordVO.setIsOutCard(endAttendanceClock.getIsOutWork());
                }
            } else {
                if (endAttendanceClock != null) {
                    endAttendanceRecordVO.setClockTime(endAttendanceClock.getClockTime());
                    endAttendanceRecordVO.setAttendanceTime(endAttendanceClock.getAttendanceTime());
                    endAttendanceRecordVO.setType(endAttendanceClock.getType());
                    endAttendanceRecordVO.setAddress(endAttendanceClock.getAddress());
                    endAttendanceRecordVO.setWifi(endAttendanceClock.getSsid());
                    endAttendanceRecordVO.setRemark(endAttendanceClock.getRemark());
                    endAttendanceRecordVO.setIsOutCard(endAttendanceClock.getIsOutWork());
                    endAttendanceRecordVO.setStatus(AttendanceResultEnum.NORMAL.getValue());
                    LocalDateTime originalClockTime = endAttendanceRecordVO.getClockTime();
                    //精确时间到分
                    String format = LocalDateTimeUtil.format(originalClockTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    LocalDateTime clockTime = LocalDateTimeUtil.parse(format, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    if (LocalDateTimeUtil.toEpochMilli(clockTime) < LocalDateTimeUtil.toEpochMilli(endAttendanceRecordVO.getAttendanceTime())) {
                        //早退
                        long minutes = LocalDateTimeUtil.between(clockTime, endAttendanceRecordVO.getAttendanceTime()).toMinutes();
                        endAttendanceRecordVO.setMinutes((int) minutes);
                        endAttendanceRecordVO.setStatus(AttendanceResultEnum.EARLY.getValue());
                    }
                    if (endAttendanceClock.getClockStatus() == SEVEN) {
                        long minutes = LocalDateTimeUtil.between(endAttendanceRecordVO.getAttendanceTime(), clockTime).toMinutes();
                        endAttendanceRecordVO.setMinutes((int) minutes);
                        endAttendanceRecordVO.setStatus(AttendanceResultEnum.OVERTIME.getValue());
                    }
                } else {
                    if (LocalDateTimeUtil.toEpochMilli(LocalDateTime.now()) < LocalDateTimeUtil.toEpochMilli(postponeTime) && ObjectUtil.notEqual(endAttendanceRecordVO.getStatus(), AttendanceResultEnum.LEAVE.getValue())) {
                        ////未到下班最晚打卡时间且没有请假
                        endAttendanceRecordVO.setStatus(AttendanceResultEnum.ALL.getValue());
                    }
                }
            }
        } else {
            //休息状态
            if (endAttendanceClock != null) {
                //存在出勤
                endAttendanceRecordVO.setClockTime(endAttendanceClock.getClockTime());
                endAttendanceRecordVO.setAttendanceTime(endAttendanceClock.getAttendanceTime());
                endAttendanceRecordVO.setType(endAttendanceClock.getType());
                endAttendanceRecordVO.setAddress(endAttendanceClock.getAddress());
                endAttendanceRecordVO.setWifi(endAttendanceClock.getSsid());
                endAttendanceRecordVO.setRemark(endAttendanceClock.getRemark());
                endAttendanceRecordVO.setIsOutCard(endAttendanceClock.getIsOutWork());
                endAttendanceRecordVO.setStatus(AttendanceResultEnum.OVERTIME.getValue());
            } else {
                endAttendanceRecordVO.setStatus(AttendanceResultEnum.REST.getValue());
            }
        }
        return endAttendanceRecordVO;
    }

    /**
     * 查询上班打卡结果
     *
     * @param queryAttendanceDailyDetailBO
     * @param hrmAttendanceShiftVO
     * @param clockStage
     * @return
     */
    private QueryAttendanceRecordVO queryStartRecordByClockStage(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO, HrmAttendanceClock hrmAttendanceClock, HrmAttendanceShiftVO hrmAttendanceShiftVO, Integer clockStage) {
        QueryAttendanceRecordVO startAttendanceRecordVO = new QueryAttendanceRecordVO();
        startAttendanceRecordVO.setIsOutCard(IsEnum.NO.getValue());
        startAttendanceRecordVO.setShiftType(hrmAttendanceShiftVO.getShiftType());
        startAttendanceRecordVO.setClockType(ONE);
        startAttendanceRecordVO.setClockStage(clockStage);
        startAttendanceRecordVO.setStatus(AttendanceResultEnum.MISSCARD.getValue());
        if (ObjectUtil.isNull(hrmAttendanceClock) && ObjectUtil.notEqual(ONE, queryAttendanceDailyDetailBO.getMulti())) {
            hrmAttendanceClock = lambdaQuery().eq(HrmAttendanceClock::getClockType, ClockType.GO_TO.getValue()).eq(HrmAttendanceClock::getClockEmployeeId, queryAttendanceDailyDetailBO.getEmployeeId())
                    .eq(HrmAttendanceClock::getClockStage, clockStage)
                    .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN)).orderByDesc(HrmAttendanceClock::getCreateTime).one();
        }
        if (ObjectUtil.notEqual(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.REST.getValue())) {
            //不是休息状态
            AttendShiftTimeVO attendShiftTimeVO = queryCurrentAttendShiftTime(hrmAttendanceShiftVO, clockStage, queryAttendanceDailyDetailBO.getCurrentDate().atStartOfDay());
            String start = AttendUtil.convertTimeDivision(attendShiftTimeVO.getStart());
            LocalDateTime startTime = attendShiftTimeVO.getStartTime();
            LocalDateTime lateTime = attendShiftTimeVO.getLateTime();
            startAttendanceRecordVO.setAttendanceTime(startTime);
            HrmEmployeeLeaveRecord hrmEmployeeLeaveRecord = leaveRecordService.queryStartOrEndLeaveRecord(startTime, queryAttendanceDailyDetailBO.getEmployeeId());
            if (hrmEmployeeLeaveRecord != null) {
                //请假
                startAttendanceRecordVO.setLeaveType(hrmEmployeeLeaveRecord.getLeaveType());
                startAttendanceRecordVO.setLeaveStartTime(start);
                startAttendanceRecordVO.setStatus(AttendanceResultEnum.LEAVE.getValue());
                if (hrmAttendanceClock != null) {
                    //请假且存在打卡
                    startAttendanceRecordVO.setClockTime(hrmAttendanceClock.getClockTime());
                    startAttendanceRecordVO.setAttendanceTime(hrmAttendanceClock.getAttendanceTime());
                    startAttendanceRecordVO.setType(hrmAttendanceClock.getType());
                    startAttendanceRecordVO.setAddress(hrmAttendanceClock.getAddress());
                    startAttendanceRecordVO.setWifi(hrmAttendanceClock.getSsid());
                    startAttendanceRecordVO.setRemark(hrmAttendanceClock.getRemark());
                    startAttendanceRecordVO.setIsOutCard(hrmAttendanceClock.getIsOutWork());
                }
            } else {
                if (hrmAttendanceClock != null) {
                    startAttendanceRecordVO.setClockTime(hrmAttendanceClock.getClockTime());
                    startAttendanceRecordVO.setAttendanceTime(hrmAttendanceClock.getAttendanceTime());
                    startAttendanceRecordVO.setType(hrmAttendanceClock.getType());
                    startAttendanceRecordVO.setAddress(hrmAttendanceClock.getAddress());
                    startAttendanceRecordVO.setWifi(hrmAttendanceClock.getSsid());
                    startAttendanceRecordVO.setRemark(hrmAttendanceClock.getRemark());
                    startAttendanceRecordVO.setIsOutCard(hrmAttendanceClock.getIsOutWork());
                    //存在打卡,默认正常
                    startAttendanceRecordVO.setStatus(AttendanceResultEnum.NORMAL.getValue());
                    LocalDateTime originalClockTime = startAttendanceRecordVO.getClockTime();
                    //精确时间到分
                    String format = LocalDateTimeUtil.format(originalClockTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    LocalDateTime clockTime = LocalDateTimeUtil.parse(format, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    if (LocalDateTimeUtil.toEpochMilli(clockTime) > LocalDateTimeUtil.toEpochMilli(startAttendanceRecordVO.getAttendanceTime())) {
                        //迟到
                        long minutes = LocalDateTimeUtil.between(startAttendanceRecordVO.getAttendanceTime(), clockTime).toMinutes();
                        startAttendanceRecordVO.setMinutes((int) minutes);
                        startAttendanceRecordVO.setStatus(AttendanceResultEnum.LATE.getValue());
                    }
                    if (hrmAttendanceClock.getClockStatus() == SEVEN) {
                        startAttendanceRecordVO.setStatus(AttendanceResultEnum.OVERTIME.getValue());
                    }
                } else {
                    if (LocalDateTimeUtil.toEpochMilli(LocalDateTime.now()) < LocalDateTimeUtil.toEpochMilli(lateTime) && ObjectUtil.notEqual(startAttendanceRecordVO.getStatus(), AttendanceResultEnum.LEAVE.getValue())) {
                        //未到上班最晚打卡时间且未请假
                        startAttendanceRecordVO.setStatus(AttendanceResultEnum.ALL.getValue());
                    }
                }

            }
        } else {
            if (hrmAttendanceClock != null) {
                startAttendanceRecordVO.setClockTime(hrmAttendanceClock.getClockTime());
                startAttendanceRecordVO.setAttendanceTime(hrmAttendanceClock.getAttendanceTime());
                startAttendanceRecordVO.setType(hrmAttendanceClock.getType());
                startAttendanceRecordVO.setAddress(hrmAttendanceClock.getAddress());
                startAttendanceRecordVO.setWifi(hrmAttendanceClock.getSsid());
                startAttendanceRecordVO.setRemark(hrmAttendanceClock.getRemark());
                startAttendanceRecordVO.setStatus(AttendanceResultEnum.OVERTIME.getValue());
                startAttendanceRecordVO.setIsOutCard(hrmAttendanceClock.getIsOutWork());
            } else {
                startAttendanceRecordVO.setStatus(AttendanceResultEnum.REST.getValue());
            }
        }
        return startAttendanceRecordVO;
    }

    @Override
    public BasePage<QueryTeamAttendanceDetailVO> queryTeamAttendanceDailyDetail(QueryTeamAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        List<QueryTeamAttendanceDetailVO> teamAttendanceDetailVOList = new ArrayList<>();
        Collection<Long> employeeIds;
        if (queryAttendanceDailyDetailBO.getDeptId() == null) {
            employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ATTENDANCE_MENU_ID);
        } else {
            employeeIds = employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).eq(HrmEmployee::getIsDel, IsEnum.NO.getValue()).eq(HrmEmployee::getDeptId, queryAttendanceDailyDetailBO.getDeptId()).list()
                    .stream().map(HrmEmployee::getEmployeeId).collect(Collectors.toList());
        }
        QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO = BeanUtil.copyProperties(queryAttendanceDailyDetailBO, QueryAttendanceEmpMonthRecordBO.class);
        queryAttendanceEmpMonthRecordBO.setTimes(Arrays.asList(queryAttendanceDailyDetailBO.getCurrentDate(), null));
        BasePage<QueryEmployeeAttendanceVO> employeeAttendanceBasePage = getBaseMapper().queryAttendanceEmpPageList(queryAttendanceEmpMonthRecordBO.parse(), queryAttendanceEmpMonthRecordBO, employeeIds);
        List<QueryEmployeeAttendanceVO> queryEmployeeAttendanceList = employeeAttendanceBasePage.getList();
        if (queryEmployeeAttendanceList != null && queryEmployeeAttendanceList.size() > ZERO) {
            for (QueryEmployeeAttendanceVO queryEmployeeAttendanceVO : queryEmployeeAttendanceList) {
                QueryTeamAttendanceDetailVO queryTeamAttendanceDetailVO = new QueryTeamAttendanceDetailVO();
                queryTeamAttendanceDetailVO.setEmployeeId(queryEmployeeAttendanceVO.getEmployeeId());
                queryTeamAttendanceDetailVO.setEmployeeName(queryEmployeeAttendanceVO.getEmployeeName());
                AttendEmpOverViewVO attendEmpOverViewVO = queryAttendEmpMonthDetail(LocalDateTimeUtil.format(queryAttendanceDailyDetailBO.getCurrentDate(), DatePattern.NORM_DATE_PATTERN), queryEmployeeAttendanceVO.getEmployeeId());
                boolean flag = false;
                int status = queryAttendanceDailyDetailBO.getClockStatus();
                AttendanceResultEnum attendanceResultEnum = AttendanceResultEnum.parse(status);
                List<Integer> integers = Arrays.asList(attendEmpOverViewVO.getStartStatus1(), attendEmpOverViewVO.getStartStatus2(), attendEmpOverViewVO.getStartStatus3(),
                        attendEmpOverViewVO.getEndStatus1(), attendEmpOverViewVO.getEndStatus2(), attendEmpOverViewVO.getEndStatus3());
                switch (attendanceResultEnum) {
                    case ABSENTEEISM:
                        if (attendEmpOverViewVO.getAbsenteeism1() > ZERO || attendEmpOverViewVO.getAbsenteeism2() > ZERO || attendEmpOverViewVO.getAbsenteeism3() > ZERO) {
                            flag = true;
                        }
                        break;
                    case EARLY:
                        if (attendEmpOverViewVO.getEarly1() > ZERO || attendEmpOverViewVO.getEarly2() > ZERO || attendEmpOverViewVO.getEarly3() > ZERO) {
                            flag = true;
                        }
                        break;
                    case LATE:
                        if (attendEmpOverViewVO.getLate1() > 0 || attendEmpOverViewVO.getLate2() > 0 || attendEmpOverViewVO.getLate3() > 0) {
                            flag = true;
                        }
                        break;
                    case MISSCARD:
                    case LEAVE:
                    case OVERTIME:
                        if (integers.contains(status)) {
                            flag = true;
                        }
                        break;
                    case OUT:
                        if (attendEmpOverViewVO.getIsOutCard() == IsEnum.YES.getValue()) {
                            flag = true;
                        }
                        break;
                    case ALL:
                        flag = true;
                        break;
                    default:
                        flag = false;
                        break;
                }
                if (flag) {
                    queryTeamAttendanceDetailVO.setStartStatus1(attendEmpOverViewVO.getStartStatus1());
                    queryTeamAttendanceDetailVO.setStartStatus2(attendEmpOverViewVO.getStartStatus2());
                    queryTeamAttendanceDetailVO.setStartStatus3(attendEmpOverViewVO.getStartStatus3());
                    queryTeamAttendanceDetailVO.setEndStatus1(attendEmpOverViewVO.getEndStatus1());
                    queryTeamAttendanceDetailVO.setEndStatus2(attendEmpOverViewVO.getEndStatus2());
                    queryTeamAttendanceDetailVO.setEndStatus3(attendEmpOverViewVO.getEndStatus3());
                    teamAttendanceDetailVOList.add(queryTeamAttendanceDetailVO);
                }
            }
        }
        BasePage<QueryTeamAttendanceDetailVO> page = new BasePage<>(employeeAttendanceBasePage.getCurrent(), employeeAttendanceBasePage.getSize(), employeeAttendanceBasePage.getTotal());
        page.setList(teamAttendanceDetailVOList);
        return page;
    }

    @Override
    public QueryTeamDailyAttendanceTotalVO queryTeamDailyAttendanceTotal(QueryTeamDailyAttendanceTotalBO queryTeamDailyAttendanceTotalBO) {
        Integer lateEmpCount = ZERO;
        Integer earlyEmpCount = ZERO;
        Integer absenteeismEmpCount = ZERO;
        Integer misscardEmpCount = ZERO;
        Integer outEmpCount = ZERO;
        Integer restEmpCount = ZERO;
        Integer unEmpCount = ZERO;
        QueryTeamDailyAttendanceTotalVO queryTeamDailyAttendanceTotalVO = new QueryTeamDailyAttendanceTotalVO();
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ATTENDANCE_MENU_ID);
        if (employeeIds.size() > ZERO) {
            for (Long employeeId : employeeIds) {
                Map<String, Object> map = queryAttendanceEmpRecordDetail(queryTeamDailyAttendanceTotalBO.getCurrentDate(), employeeId);
                Integer status = (Integer) map.get("status");
                switch (status) {
                    case 0:
                        if ((Integer) map.get("lateCount") > ZERO) {
                            lateEmpCount = lateEmpCount + 1;
                        }
                        if ((Integer) map.get("earlyCount") > ZERO) {
                            earlyEmpCount = earlyEmpCount + 1;
                        }
                        if ((Integer) map.get("misscardCount") > ZERO) {
                            misscardEmpCount = misscardEmpCount + 1;
                        }
                        if ((Integer) map.get("IsOutCard") > 0) {
                            outEmpCount = outEmpCount + 1;
                        }
                        break;
                    case 4:
                        absenteeismEmpCount = absenteeismEmpCount + 1;
                        break;
                    case 9:
                        restEmpCount = restEmpCount + 1;
                        break;
                    case 10:
                        unEmpCount = unEmpCount + 1;
                    default:
                        break;
                }
            }
            Integer overTimeEmpCount = employeeOverTimeRecordService.lambdaQuery().in(HrmEmployeeOverTimeRecord::getEmployeeId, employeeIds)
                    .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", queryTeamDailyAttendanceTotalBO.getCurrentDate()).count().intValue();
            Integer leaveEmpCount = leaveRecordService.queryLeaveEmpCount(queryTeamDailyAttendanceTotalBO.getCurrentDate(), employeeIds);
            queryTeamDailyAttendanceTotalVO.setLateEmpCount(lateEmpCount);
            queryTeamDailyAttendanceTotalVO.setEarlyEmpCount(earlyEmpCount);
            queryTeamDailyAttendanceTotalVO.setMisscardEmpCount(misscardEmpCount);
            queryTeamDailyAttendanceTotalVO.setAbsenteeismEmpCount(absenteeismEmpCount);
            queryTeamDailyAttendanceTotalVO.setOutEmpCount(outEmpCount);
            queryTeamDailyAttendanceTotalVO.setOverTimeEmpCount(overTimeEmpCount);
            queryTeamDailyAttendanceTotalVO.setLeaveEmpCount(leaveEmpCount);
            Integer requiredEmpCount = employeeIds.size() - unEmpCount;
            if (restEmpCount == requiredEmpCount) {
                queryTeamDailyAttendanceTotalVO.setAttendEmpCount(ZERO);
            } else {
                queryTeamDailyAttendanceTotalVO.setAttendEmpCount(requiredEmpCount - leaveEmpCount);
            }
        }

        return queryTeamDailyAttendanceTotalVO;
    }

    @Override
    public QueryAttendEmpMonthRecordVO queryAttendEmpMonthRecord(QueryAttendEmpRecordBO queryAttendEmpRecordBO) {
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
        // 外勤天数
        Integer outDays = ZERO;
        // 缺卡次数
        Integer misscardCount = ZERO;
        // 不被统计天数
        Integer unCountDays = ZERO;
        // 请假天数
        BigDecimal leaveDays = new BigDecimal(ZERO);
        // 旷工天数
        BigDecimal absenteeismDays = new BigDecimal(ZERO);
        QueryAttendEmpMonthRecordVO queryAttendEmpMonthRecordVO = new QueryAttendEmpMonthRecordVO();
        leaveRecordService.queryOaLeaveExamineList();
        Long employeeId = queryAttendEmpRecordBO.getEmployeeId();
        List<Long> employeeIds = ListUtil.toList(employeeId);
        // 查询出日期区间所有的日期
        List<String> dates = findDates(queryAttendEmpRecordBO.getTimes().get(0), queryAttendEmpRecordBO.getTimes().get(1));
        LocalDateTime startDateTime = queryAttendEmpRecordBO.getTimes().get(0).atStartOfDay();
        LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(queryAttendEmpRecordBO.getTimes().get(1).atStartOfDay());
        List<HrmAttendanceClock> startClockList = queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        //查询出日期区间所有的下班打卡记录
        List<HrmAttendanceClock> endClockList = queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        // 查询日期区间所有的加班
        Integer overTimeCount = queryEmpAttendanceOverTimeCountDays(queryAttendEmpRecordBO.getTimes(), employeeId);
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(employeeId);
        Map<String, Map<String, Object>> empRecordDetailMap = queryAttendanceEmpRecordDetailByDate(startClockList, endClockList, dates, hrmAttendanceGroup, employeeId);
        for (String date : dates) {
            Map<String, Object> map = empRecordDetailMap.get(date);
            Integer status = (Integer) map.get("status");
            switch (status) {
                case 0:
                    normalDays = normalDays + 1;
                    lateMinute = lateMinute + (Integer) map.get("lateMinutes");
                    lateCount = lateCount + (Integer) map.get("lateCount");
                    earlyMinute = earlyMinute + (Integer) map.get("earlyMinutes");
                    earlyCount = earlyCount + (Integer) map.get("earlyCount");
                    outDays = outDays + (Integer) map.get("IsOutCard");
                    break;
                case 4:
                    absenteeismDays = absenteeismDays.add(new BigDecimal(map.get("absenteeismDay").toString()));
                    break;
                case 9:
                    restDays = restDays + 1;
                    break;
                case 10:
                    unCountDays = unCountDays + 1;
                    break;
                default:
                    break;
            }
            misscardCount = misscardCount + (Integer) map.get("misscardCount");
            leaveDays = leaveDays.add(new BigDecimal(map.get("leaveDay").toString()));
        }
        Integer overTime = overTimeCount != null ? overTimeCount : ZERO;
        queryAttendEmpMonthRecordVO.setAttendDays(dates.size() - restDays);
        queryAttendEmpMonthRecordVO.setActualDays(normalDays - Convert.toDouble(leaveDays).intValue() + overTime);
        queryAttendEmpMonthRecordVO.setAbsenteeismDays(absenteeismDays);
        queryAttendEmpMonthRecordVO.setEarlyCount(earlyCount);
        queryAttendEmpMonthRecordVO.setEarlyMinute(earlyMinute);
        queryAttendEmpMonthRecordVO.setLateCount(lateCount);
        queryAttendEmpMonthRecordVO.setLateMinute(lateMinute);
        queryAttendEmpMonthRecordVO.setOutDays(outDays);
        queryAttendEmpMonthRecordVO.setMisscardCount(misscardCount);
        queryAttendEmpMonthRecordVO.setLeaveDays(leaveDays);
        queryAttendEmpMonthRecordVO.setOverTimeCount(overTime);
        queryAttendEmpMonthRecordVO.setIsFullAttendance(IsEnum.NO.getValue());
        if (queryAttendEmpMonthRecordVO.getActualDays() >= queryAttendEmpMonthRecordVO.getAttendDays() && earlyCount == ZERO && lateCount == ZERO && leaveDays.compareTo(new BigDecimal(ZERO)) == ZERO) {
            queryAttendEmpMonthRecordVO.setIsFullAttendance(IsEnum.YES.getValue());
        }
        return queryAttendEmpMonthRecordVO;

    }

    @Override
    public QueryAttendanceEmpDetailVO queryAttendEmpMonthStatusDetail(QueryAttendanceEmpDetailBO queryAttendanceEmpDetailBO) {
        QueryAttendanceEmpDetailVO queryAttendanceEmpDetailVO = new QueryAttendanceEmpDetailVO();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, queryAttendanceEmpDetailBO.getYear());
        cal.set(Calendar.MONTH, queryAttendanceEmpDetailBO.getMonth() - 1);
        queryAttendanceEmpDetailVO.setStartTime(LocalDateTimeUtil.of(cal.getTime()).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate());
        queryAttendanceEmpDetailVO.setEndTime(LocalDateTimeUtil.of(cal.getTime()).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate());
        List<String> dates = findDates(queryAttendanceEmpDetailVO.getStartTime(), queryAttendanceEmpDetailVO.getEndTime());
        List<Map<String, Object>> monthMapList = new ArrayList<>();
        List<Long> employeeIds = ListUtil.toList(queryAttendanceEmpDetailBO.getEmployeeId());
        //查询出日期区间所有的上班打卡记录
        LocalDateTime startDateTime = queryAttendanceEmpDetailVO.getStartTime().atStartOfDay();
        LocalDateTime endDateTime = LocalDateTimeUtil.endOfDay(queryAttendanceEmpDetailVO.getEndTime().atStartOfDay());
        List<HrmAttendanceClock> startClockList = queryAttendanceClockList(ClockType.GO_TO.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        //查询出日期区间所有的下班打卡记录
        List<HrmAttendanceClock> endClockList = queryAttendanceClockList(ClockType.GET_OFF.getValue(), startDateTime, endDateTime, employeeIds, ONE);
        HrmAttendanceGroup hrmAttendanceGroup = attendanceGroupService.queryAttendanceGroup(queryAttendanceEmpDetailBO.getEmployeeId());
        Map<String, Map<String, Object>> empRecordDetailMap = queryAttendanceEmpRecordDetailByDate(startClockList, endClockList, dates, hrmAttendanceGroup, queryAttendanceEmpDetailBO.getEmployeeId());
        //0正常 1异常
        Integer empStatus = ZERO;
        for (String date : dates) {
            Map<String, Object> mapList = new HashMap<>();
            Map<String, Object> map = empRecordDetailMap.get(date);
            Integer status = (Integer) map.get("status");
            if (status.equals(AttendanceResultEnum.NORMAL.getValue())) {
                if ((Integer) map.get("lateCount") > ZERO || (Integer) map.get("earlyCount") > ZERO || (Integer) map.get("IsOutCard") > ZERO) {
                    empStatus = ONE;
                }
            } else if (status.equals(AttendanceResultEnum.ABSENTEEISM.getValue())) {
                empStatus = ONE;
            }
            mapList.put(date, empStatus);
            monthMapList.add(mapList);
        }
        queryAttendanceEmpDetailVO.setDateList(monthMapList);
        return queryAttendanceEmpDetailVO;
    }

    @Override
    public QueryMyCurrentLastClockVO queryMyCurrentLastClock(Long employeeId) {
        //今日
        String today = DateUtil.formatDate(DateUtil.date());
        //昨日
        String yesterday = DateUtil.formatDate(DateUtil.yesterday());
        //明日
        String tomorrow = DateUtil.formatDate(DateUtil.tomorrow());
        QueryMyCurrentLastClockVO queryMyCurrentLastClockVO = new QueryMyCurrentLastClockVO();
        queryMyCurrentLastClockVO.setLeaveStatus(ZERO);
        queryMyCurrentLastClockVO.setClockStage(ONE);
        queryMyCurrentLastClockVO.setClockType(ClockType.GO_TO.getValue());
        queryMyCurrentLastClockVO.setClockStatus(ZERO);
        QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO = new QueryAttendanceDailyDetailBO();
        queryAttendanceDailyDetailBO.setEmployeeId(employeeId);
        HrmAttendanceShiftVO hrmAttendanceShiftVO = queryMyCurrentShift(queryAttendanceDailyDetailBO);
        HrmAttendanceClock attendanceClock = lambdaQuery().eq(HrmAttendanceClock::getClockEmployeeId, employeeId)
                .apply("date_format(attendance_time,'%Y-%m-%d') = {0}", hrmAttendanceShiftVO.getAttendanceShiftDate()).orderByDesc(HrmAttendanceClock::getCreateTime).one();
        if (ObjectUtil.equal(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.REST.getValue())) {
            queryMyCurrentLastClockVO.setClockStatus(ONE);
            if (attendanceClock != null) {
                queryMyCurrentLastClockVO.setClockType(TWO);
            }
        } else if (ObjectUtil.equal(hrmAttendanceShiftVO.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
            DateTime startTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getStart1()));
            DateTime endTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEnd1()));
            DateTime advanceTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getAdvanceCard1()));
            DateTime lateTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getLateCard1()));
            DateTime earlyTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEarlyCard1()));
            DateTime postponeTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getPostponeCard1()));
            if (advanceTime.getTime() > startTime.getTime()) {
                advanceTime = DateUtil.parseDateTime(yesterday + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getAdvanceCard1()));
            }
            if (startTime.getTime() > endTime.getTime()) {
                //下班时间是第二天
                endTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEnd1()));
            }
            if (lateTime.getTime() < startTime.getTime()) {
                //上班最晚打卡时间是第二天
                lateTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getLateCard1()));
            }
            if (earlyTime.getTime() < startTime.getTime()) {
                //下班最早打卡时间是第二天
                earlyTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEarlyCard1()));
            }
            if (postponeTime.getTime() < endTime.getTime()) {
                //下班最晚打卡时间是第二天
                postponeTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getPostponeCard1()));
            }
            DateTime time = DateTime.now();
            //格式化当前时间忽略秒
            String format = DateUtil.format(time, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
            DateTime now = DateUtil.parse(format, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
            if (now.getTime() > startTime.getTime() && now.getTime() < endTime.getTime()) {//上班之后到下班之前
                if (attendanceClock != null) {
                    queryMyCurrentLastClockVO.setClockType(TWO);
                    if (attendanceClock.getClockType() == ClockType.GO_TO.getValue()) {//打过上班卡
                        if (now.getTime() >= earlyTime.getTime()) {
                            queryMyCurrentLastClockVO.setClockStatus(FOUR);
                        } else {
                            queryMyCurrentLastClockVO.setClockStatus(ZERO);
                        }
                    } else {
                        //打过下班卡
                        queryMyCurrentLastClockVO.setClockStatus(TWO);
                    }
                } else {//没有打过卡
                    if (now.getTime() <= lateTime.getTime()) {//迟到
                        queryMyCurrentLastClockVO.setClockType(ONE);
                        queryMyCurrentLastClockVO.setClockStatus(THREE);
                    } else {
                        queryMyCurrentLastClockVO.setClockType(TWO);
                        if (now.getTime() >= earlyTime.getTime()) {//早退
                            queryMyCurrentLastClockVO.setClockStatus(FOUR);
                        } else {
                            //未到打卡时间
                            queryMyCurrentLastClockVO.setClockStatus(ZERO);
                        }
                    }
                }
            } else if (now.getTime() >= endTime.getTime() && now.getTime() <= postponeTime.getTime()) {
                //下班之后到下班打卡结束时间之前
                queryMyCurrentLastClockVO.setClockType(TWO);
                if (attendanceClock != null) {
                    if (attendanceClock.getClockType() == ClockType.GET_OFF.getValue()) {
                        queryMyCurrentLastClockVO.setClockStatus(TWO);
                    } else {
                        //下班正常打卡
                        queryMyCurrentLastClockVO.setClockStatus(ONE);
                    }
                } else {
                    queryMyCurrentLastClockVO.setClockStatus(ONE);
                }
            }
            //查询是否请假
            List<HrmEmployeeLeaveRecord> hrmEmployeeLeaveRecordList = leaveRecordService.queryLeaveRecord(LocalDate.now().atStartOfDay(), employeeId);
            if (hrmEmployeeLeaveRecordList.size() > 0) {
                queryMyCurrentLastClockVO.setLeaveStatus(ONE);
            }
        }
        return queryMyCurrentLastClockVO;
    }

    /**
     * 查询当前记前考勤阶段对应的出勤时间
     *
     * @param hrmAttendanceShiftVO
     * @param clockStage
     * @param currentDate
     * @return
     */
    public AttendShiftTimeVO queryCurrentAttendShiftTime(HrmAttendanceShiftVO hrmAttendanceShiftVO, Integer clockStage, LocalDateTime currentDate) {
        AttendShiftTimeVO attendShiftTimeVO = new AttendShiftTimeVO();
        String end = hrmAttendanceShiftVO.getEnd1();
        String start = hrmAttendanceShiftVO.getStart1();
        DateTime startTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(start));
        DateTime endTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(end));
        //上班最早打卡时间
        DateTime advanceTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getAdvanceCard1()));
        //上班最晚打卡时间
        DateTime lateTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getLateCard1()));
        //下班最早打卡时间
        DateTime earlyTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEarlyCard1()));
        //下班最晚打卡时间
        DateTime postponeTime = DateUtil.parseDateTime(DateUtil.formatDate(Timestamp.valueOf(currentDate)) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getPostponeCard1()));
        if (startTime.getTime() > endTime.getTime()) {
            endTime = DateUtil.offsetDay(endTime, 1);
        }
        if (lateTime.getTime() < startTime.getTime()) {
            lateTime = DateUtil.offsetDay(lateTime, 1);
        }
        if (postponeTime.getTime() < endTime.getTime()) {
            postponeTime = DateUtil.offsetDay(postponeTime, 1);
        }
        if (advanceTime.getTime() > startTime.getTime()) {
            advanceTime = DateUtil.offsetDay(advanceTime, -1);
        }
        if (earlyTime.getTime() < startTime.getTime()) {
            earlyTime = DateUtil.offsetDay(earlyTime, 1);
        }
        if (clockStage == TWO) {
            end = hrmAttendanceShiftVO.getEnd2();
            start = hrmAttendanceShiftVO.getStart2();
            startTime = DateUtil.parseDateTime(DateUtil.formatDate(endTime) + " " + AttendUtil.convertTime(start));
            if (startTime.getTime() < endTime.getTime()) {
                startTime = DateUtil.offsetDay(startTime, 1);
            }
            endTime = DateUtil.parseDateTime(DateUtil.formatDate(endTime) + " " + AttendUtil.convertTime(end));
            if (endTime.getTime() < startTime.getTime()) {
                endTime = DateUtil.offsetDay(endTime, 1);
            }
        } else if (clockStage == THREE) {
            end = hrmAttendanceShiftVO.getEnd3();
            start = hrmAttendanceShiftVO.getStart3();
            DateTime startTime2 = DateUtil.parseDateTime(DateUtil.formatDate(endTime) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getStart2()));
            if (startTime.getTime() < endTime.getTime()) {
                startTime2 = DateUtil.offsetDay(startTime2, 1);
            }
            DateTime endTime2 = DateUtil.parseDateTime(DateUtil.formatDate(endTime) + " " + AttendUtil.convertTime(hrmAttendanceShiftVO.getEnd2()));
            if (endTime2.getTime() < startTime2.getTime()) {
                endTime2 = DateUtil.offsetDay(endTime2, 1);
            }
            startTime = DateUtil.parseDateTime(DateUtil.formatDate(endTime2) + " " + AttendUtil.convertTime(start));
            if (startTime.getTime() < endTime2.getTime()) {
                startTime = DateUtil.offsetDay(startTime, 1);
            }
            endTime = DateUtil.parseDateTime(DateUtil.formatDate(endTime2) + " " + AttendUtil.convertTime(end));
            if (endTime.getTime() < startTime.getTime()) {
                endTime = DateUtil.offsetDay(endTime, 1);
            }
        }
        attendShiftTimeVO.setStart(start);
        attendShiftTimeVO.setEnd(end);
        attendShiftTimeVO.setAdvanceTime(advanceTime.toLocalDateTime());
        attendShiftTimeVO.setEarlyTime(earlyTime.toLocalDateTime());
        attendShiftTimeVO.setPostponeTime(postponeTime.toLocalDateTime());
        attendShiftTimeVO.setLateTime(lateTime.toLocalDateTime());
        attendShiftTimeVO.setStartTime(startTime.toLocalDateTime());
        attendShiftTimeVO.setEndTime(endTime.toLocalDateTime());
        return attendShiftTimeVO;
    }
}
