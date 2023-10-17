package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.SetLegalHolidaysB0;
import com.kakarote.hrm.entity.PO.HrmAttendanceLegalHolidays;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 考勤法定节假日 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-23
 */
public interface IHrmAttendanceLegalHolidaysService extends BaseService<HrmAttendanceLegalHolidays> {
    /**
     * 添加法定节假日
     *
     * @param setLegalHolidaysB0
     */
    void addLegalHolidays(SetLegalHolidaysB0 setLegalHolidaysB0);

    /**
     * 通过日期查询法定节假日
     *
     * @param day
     * @return
     */
    HrmAttendanceLegalHolidays checkIsLegalHolidays(LocalDateTime day);

    /**
     * 查询法定节假日列表
     *
     * @return
     */
    List<HrmAttendanceLegalHolidays> queryLegalHolidayList();
}
