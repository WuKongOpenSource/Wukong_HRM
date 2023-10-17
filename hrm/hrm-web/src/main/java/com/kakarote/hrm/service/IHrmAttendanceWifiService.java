package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AddAttendanceWifiBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import com.kakarote.hrm.entity.VO.AttendanceWifiPageListVO;

/**
 * <p>
 * 打卡wifi表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
public interface IHrmAttendanceWifiService extends BaseService<HrmAttendanceWifi> {
    /**
     * 查询打卡wifi列表
     *
     * @param pageEntity
     * @return
     */
    BasePage<AttendanceWifiPageListVO> queryAttendanceWifiPageList(PageEntity pageEntity);

    /**
     * 添加打卡wifi
     *
     * @param attendanceWifi
     */
    HrmAttendanceWifi addAttendanceWifi(AddAttendanceWifiBO attendanceWifi);
}
