package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.AddAttendanceWifiBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import com.kakarote.hrm.entity.VO.AttendanceWifiPageListVO;
import com.kakarote.hrm.mapper.HrmAttendanceWifiMapper;
import com.kakarote.hrm.service.IHrmAttendanceWifiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 打卡wifi表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@Service
public class HrmAttendanceWifiServiceImpl extends BaseServiceImpl<HrmAttendanceWifiMapper, HrmAttendanceWifi> implements IHrmAttendanceWifiService {
    @Autowired
    private HrmAttendanceWifiMapper attendanceWifiMapper;

    @Override
    public BasePage<AttendanceWifiPageListVO> queryAttendanceWifiPageList(PageEntity pageEntity) {
        BasePage<HrmAttendanceWifi> attendanceWifiBasePage = attendanceWifiMapper.selectPage(pageEntity.parse(), Wrappers.emptyWrapper());
        List<AttendanceWifiPageListVO> attendanceWifiPageListVOList = new ArrayList<>();
        attendanceWifiBasePage.getList().forEach(attendanceWifi -> {
            AttendanceWifiPageListVO attendanceWifiPageListVO = new AttendanceWifiPageListVO();
            BeanUtil.copyProperties(attendanceWifi, attendanceWifiPageListVO);
            attendanceWifiPageListVOList.add(attendanceWifiPageListVO);
        });
        BasePage<AttendanceWifiPageListVO> page = new BasePage<>(attendanceWifiBasePage.getCurrent(), attendanceWifiBasePage.getSize(), attendanceWifiBasePage.getTotal());
        page.setList(attendanceWifiPageListVOList);
        return page;
    }

    @Override
    public HrmAttendanceWifi addAttendanceWifi(AddAttendanceWifiBO attendanceWifi) {
        HrmAttendanceWifi hrmAttendanceWifi = BeanUtil.copyProperties(attendanceWifi, HrmAttendanceWifi.class);
        save(hrmAttendanceWifi);
        return hrmAttendanceWifi;
    }
}
