package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryLeaveRecordPageListBO;
import com.kakarote.hrm.entity.BO.SetEmployeeLeaveRecordB0;
import com.kakarote.hrm.entity.PO.HrmEmployeeLeaveRecord;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工请假记录 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
public interface IHrmEmployeeLeaveRecordService extends BaseService<HrmEmployeeLeaveRecord> {
    /**
     * 分页查询记录
     *
     * @param leaveRecordPageListBO
     * @return
     */
    BasePage<Map<String, Object>> queryLeaveRecordPageList(QueryLeaveRecordPageListBO leaveRecordPageListBO);

    /**
     * 查询请假记录
     *
     * @param leaveTime
     * @param employeeId
     * @return
     */
    List<HrmEmployeeLeaveRecord> queryLeaveRecord(LocalDateTime leaveTime, Long employeeId);

    /**
     * 添加请假记录
     *
     * @param employeeLeaveRecord
     */
    void addEmployeeLeaveRecord(SetEmployeeLeaveRecordB0 employeeLeaveRecord);

    /**
     * 同步最新的请假记录
     */
    void queryOaLeaveExamineList();

    /**
     * 通过班次上班或者下班时间判断是否存在请假记录
     *
     * @param currentDate
     * @param employeeId
     * @return
     */
    HrmEmployeeLeaveRecord queryStartOrEndLeaveRecord(LocalDateTime currentDate, Long employeeId);

    /**
     * 查询当前日期请假人数
     *
     * @param currentDate
     * @param employeeIds
     * @return
     */
    Integer queryLeaveEmpCount(String currentDate, Collection<Long> employeeIds);

    /**
     * 查询请假类型
     *
     * @return
     */
    List<String> queryLeaveTypeList();
}
