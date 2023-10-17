package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryAttendanceEmpMonthRecordBO;
import com.kakarote.hrm.entity.BO.QueryAttendanceOutCardBO;
import com.kakarote.hrm.entity.BO.QueryAttendancePageBO;
import com.kakarote.hrm.entity.BO.QueryNotesStatusBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceClock;
import com.kakarote.hrm.entity.VO.QueryAttendanceEmpMonthRecordVO;
import com.kakarote.hrm.entity.VO.QueryAttendancePageVO;
import com.kakarote.hrm.entity.VO.QueryEmployeeAttendanceVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 打卡记录表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-07
 */
public interface HrmAttendanceClockMapper extends BaseMapper<HrmAttendanceClock> {

    BasePage<QueryAttendancePageVO> queryPageList(BasePage<QueryAttendancePageVO> parse,
                                                  @Param("data") QueryAttendancePageBO attendancePageBO, @Param("employeeIds") Collection<Long> employeeIds);

    List<HrmAttendanceClock> queryClockListByTime(@Param("time") LocalDate time, @Param("employeeIds") Collection<Long> employeeIds);

    Set<String> queryClockStatusList(@Param("data") QueryNotesStatusBO queryNotesStatusBO, @Param("employeeIds") Collection<Long> employeeIds);

    BasePage<QueryAttendancePageVO> queryMyPageList(BasePage<Object> parse, @Param("employeeId") Long employeeId);

    BasePage<QueryEmployeeAttendanceVO> queryAttendanceEmpPageList(BasePage<QueryAttendanceEmpMonthRecordVO> page,
                                                                   @Param("data") QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO, @Param("employeeIds") Collection<Long> employeeIds);

    Integer queryEmpAttendanceOverTimeCountDays(@Param("times") List<LocalDate> times, @Param("employeeId") Long employeeId);

    BasePage<Map<String, Object>> queryOutCardPageList(BasePage<Map<String, Object>> parse, @Param("data") QueryAttendanceOutCardBO queryAttendanceOutCardBO);

    /**
     * 根据条件查询接口
     *
     * @param clockType     打卡类型
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @param employeeIds   员工列表
     * @param clockStage    打卡阶段
     * @return
     */
    List<HrmAttendanceClock> queryAttendanceClockList(@Param("clockType") Integer clockType, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("employeeIds") List<Long> employeeIds, @Param("clockStage") Integer clockStage);
}
