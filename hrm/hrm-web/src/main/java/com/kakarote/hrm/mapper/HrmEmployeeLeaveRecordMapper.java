package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryLeaveRecordPageListBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeLeaveRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工请假记录 Mapper 接口
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
public interface HrmEmployeeLeaveRecordMapper extends BaseMapper<HrmEmployeeLeaveRecord> {
    /**
     * 查询请假记录列表
     *
     * @param page
     * @param leaveRecordPageListBO
     * @return
     */
    BasePage<Map<String, Object>> queryLeaveRecordPageList(BasePage<Map<String, Object>> page, @Param("data") QueryLeaveRecordPageListBO leaveRecordPageListBO);

    /**
     * 查询请假记录
     *
     * @param leaveTime
     * @param employeeId
     * @return
     */
    List<HrmEmployeeLeaveRecord> queryLeaveRecord(@Param("leaveTime") LocalDateTime leaveTime, @Param("employeeId") Long employeeId);

    /**
     * 通过班次上下班时间查询是否存在请假
     *
     * @param currentDate
     * @param employeeId
     * @return
     */
    HrmEmployeeLeaveRecord queryStartOrEndLeaveRecord(@Param("leaveTime") LocalDateTime currentDate, @Param("employeeId") Long employeeId);

    /**
     * @param currentDate
     * @param employeeIds
     * @return
     */
    Integer queryLeaveEmpCount(@Param("currentDate") String currentDate, @Param("employeeIds") Collection<Long> employeeIds);
}
