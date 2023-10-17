package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AddAttendancePointBO;
import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.VO.AttendancePointPageListVO;

/**
 * <p>
 * 打卡地址表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
public interface IHrmAttendancePointService extends BaseService<HrmAttendancePoint> {
    /**
     * 查询列表数据
     *
     * @param pageEntity
     * @return
     */
    BasePage<AttendancePointPageListVO> queryAttendancePointPageList(PageEntity pageEntity);

    /**
     * 添加地点
     *
     * @param attendancePoint
     */
    HrmAttendancePoint addAttendancePoint(AddAttendancePointBO attendancePoint);
}
