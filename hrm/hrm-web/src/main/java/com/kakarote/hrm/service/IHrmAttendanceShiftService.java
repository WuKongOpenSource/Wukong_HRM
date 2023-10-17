package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceShiftBO;
import com.kakarote.hrm.entity.BO.SetAttendanceShiftBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceShift;
import com.kakarote.hrm.entity.VO.HrmAttendanceShiftVO;

import java.util.Map;

/**
 * <p>
 * 班次表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
public interface IHrmAttendanceShiftService extends BaseService<HrmAttendanceShift> {
    /**
     * 查询考勤班次列表
     *
     * @param queryHrmAttendanceShiftBO
     * @return
     */
    BasePage<HrmAttendanceShiftVO> queryAttendanceShiftPageList(QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO);

    /**
     * 添加或修改班次
     *
     * @param attendanceShift
     */
    void setAttendanceShift(SetAttendanceShiftBO attendanceShift);

    /**
     * 校验考勤班次名称
     *
     * @param queryHrmAttendanceShiftBO
     * @return
     */
    void verifyAttendanceShiftName(QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO);

    /**
     * 查询考勤时间
     *
     * @param shiftSetting 考勤班次
     * @return
     */
    Map<Integer, Map<String, Object>> queryAttendanceDate(String shiftSetting);

    /**
     * 通过班次id删除班次
     *
     * @param attendanceShiftId
     */
    OperationLog deleteAttendanceShift(Long attendanceShiftId);
}
