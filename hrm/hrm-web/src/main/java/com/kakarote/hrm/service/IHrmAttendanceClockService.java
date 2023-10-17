package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmAttendanceClock;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import com.kakarote.hrm.entity.PO.HrmAttendanceShift;
import com.kakarote.hrm.entity.VO.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 打卡记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-07
 */
public interface IHrmAttendanceClockService extends BaseService<HrmAttendanceClock> {

    /**
     * 添加活修改打卡
     *
     * @param attendanceClock
     */
    void addOrUpdate(HrmAttendanceClock attendanceClock);

    /**
     * 查询打卡列表
     *
     * @param attendancePageBO
     * @return
     */
    BasePage<QueryAttendancePageVO> queryPageList(QueryAttendancePageBO attendancePageBO);

    /**
     * 根据时间和员工id查询打卡记录
     *
     * @param time
     * @param employeeIds
     * @return
     */
    List<HrmAttendanceClock> queryClockListByTime(LocalDate time, Collection<Long> employeeIds);

    /**
     * 查询打卡时间列表
     *
     * @param queryNotesStatusBO
     * @param employeeIds
     * @return
     */
    Set<String> queryClockStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds);

    /**
     * 查询自己打卡列表(手机端使用)
     *
     * @param pageEntity
     * @return
     */
    BasePage<QueryAttendancePageVO> queryMyPageList(PageEntity pageEntity);

    /**
     * 添加或修改打卡
     *
     * @param attendanceClock
     */
    void setHrmAttendanceClock(SetHrmAttendanceClockB0 attendanceClock);

    /**
     * 查询月度汇总
     *
     * @param queryAttendanceEmpMonthRecordBO
     * @return
     */
    BasePage<QueryAttendanceEmpMonthRecordVO> queryAttendanceEmpMonthRecordPageList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO);

    /**
     * 查询员工每日班次中打卡明细
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    QueryAttendanceDailyDetailVO queryAttendanceDailyDetail(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO);

    /**
     * 查询员工月度打卡明细
     *
     * @param queryAttendanceEmpMonthRecordBO
     * @return
     */
    BasePage<QueryAttendanceEmpMonthDetailVO> queryAttendanceEmpMonthDetailPageList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO);

    /**
     * 查询出符合条件的考勤员工列表
     *
     * @param queryAttendanceEmpMonthRecordBO
     * @return
     */
    BasePage<QueryEmployeeAttendanceVO> queryAttendanceEmpList(QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO);

    /**
     * 获取时间段之间所有日期集合
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    List<String> findDates(LocalDate dBegin, LocalDate dEnd);

    /**
     * 通过员工和当前时间查询获取到最终对应的班次
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    HrmAttendanceShift getHrmAttendanceShift(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO);

    /**
     * 通过区间查询出加班次数
     *
     * @param times
     * @param employeeId
     * @return
     */
    Integer queryEmpAttendanceOverTimeCountDays(List<LocalDate> times, Long employeeId);

    /**
     * 查询出员工指定日期考勤汇总
     *
     * @param date
     * @param employeeId
     * @return
     */
    Map<String, Object> queryAttendanceEmpRecordDetail(String date, Long employeeId);

    /**
     * 通过员工id查询对应区间内的考勤明细
     *
     * @param queryAttendanceEmpMonthDetail
     * @return
     */
    QueryAttendanceEmpDetailVO queryAttendanceEmpMonthDetail(QueryAttendanceEmpMonthDetailBO queryAttendanceEmpMonthDetail);

    /**
     * 查询外勤数据
     *
     * @param queryAttendanceOutCardBO
     * @return
     */
    BasePage<Map<String, Object>> queryOutCardPageList(QueryAttendanceOutCardBO queryAttendanceOutCardBO);

    void downloadExcel(HttpServletResponse response);

    /**
     * 查询自我当前打卡
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    QueryMyCurrentClockVO queryMyCurrentClock(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO);

    /**
     * 通过条件查询团队当前日期打卡明细
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    BasePage<QueryTeamAttendanceDetailVO> queryTeamAttendanceDailyDetail(QueryTeamAttendanceDailyDetailBO queryAttendanceDailyDetailBO);

    /**
     * 通过条件查询团队考勤汇总
     *
     * @param queryTeamDailyAttendanceTotalBO
     * @return
     */
    QueryTeamDailyAttendanceTotalVO queryTeamDailyAttendanceTotal(QueryTeamDailyAttendanceTotalBO queryTeamDailyAttendanceTotalBO);

    /**
     * 查询单一员工月度汇总
     *
     * @param queryAttendEmpRecordBO
     * @return
     */
    QueryAttendEmpMonthRecordVO queryAttendEmpMonthRecord(QueryAttendEmpRecordBO queryAttendEmpRecordBO);

    /**
     * 查询单一员工月度状态明细（手机端）
     *
     * @param queryAttendanceEmpDetailBO
     * @return
     */
    QueryAttendanceEmpDetailVO queryAttendEmpMonthStatusDetail(QueryAttendanceEmpDetailBO queryAttendanceEmpDetailBO);

    /**
     * 查询自己需要打卡类型和阶段
     *
     * @param employeeId
     * @return
     */
    QueryMyCurrentLastClockVO queryMyCurrentLastClock(Long employeeId);

    /**
     * 查询出当前员工指定范围时间范围内考勤
     *
     * @param dates      时间区间
     * @param employeeId 员工ID
     * @return
     */
    Map<String, Map<String, Object>> queryAttendanceEmpRecordDetailByDate(List<HrmAttendanceClock> startEmployeeClockList, List<HrmAttendanceClock> endEmployeeClockList, List<String> dates, HrmAttendanceGroup hrmAttendanceGroup, Long employeeId);

    List<HrmAttendanceClock> queryAttendanceClockList(Integer clockType, LocalDateTime startDateTime, LocalDateTime endDateTime, List<Long> employeeIds, Integer clockStage);

    /**
     * 查询员工当天打卡的时间范围
     *
     * @param queryAttendanceDailyDetailBO
     * @return
     */
    AttendShiftTimeVO queryCurrentAttendShiftTime(QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO);

    /**
     * 通过考勤组获取当前班次
     *
     * @param attendanceGroup
     * @return
     */
    HrmAttendanceShift getHrmAttendanceShiftByGroup(HrmAttendanceGroup attendanceGroup);

    /**
     * 获取日期区间的班次
     *
     * @param dates
     * @param hrmAttendanceGroup
     * @param employeeId
     * @return
     */
    Map<String, HrmAttendanceShift> getHrmAttendanceShiftMap(List<String> dates, HrmAttendanceGroup hrmAttendanceGroup, Long employeeId);
}
