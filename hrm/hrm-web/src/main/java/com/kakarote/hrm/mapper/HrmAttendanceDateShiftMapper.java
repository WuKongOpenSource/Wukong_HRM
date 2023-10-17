package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmAttendanceDateShift;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * 每日出勤班次 Mapper 接口
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-19
 */
public interface HrmAttendanceDateShiftMapper extends BaseMapper<HrmAttendanceDateShift> {
    /**
     * @param localDateTime
     * @return
     */
    Set<Long> queryEmployeeIds(@Param("localDateTime") LocalDateTime localDateTime);
}
