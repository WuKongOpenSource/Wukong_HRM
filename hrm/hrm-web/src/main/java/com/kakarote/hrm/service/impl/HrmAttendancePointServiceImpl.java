package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.AddAttendancePointBO;
import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.VO.AttendancePointPageListVO;
import com.kakarote.hrm.mapper.HrmAttendancePointMapper;
import com.kakarote.hrm.service.IHrmAttendancePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 打卡地址表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@Service
public class HrmAttendancePointServiceImpl extends BaseServiceImpl<HrmAttendancePointMapper, HrmAttendancePoint> implements IHrmAttendancePointService {

    @Autowired
    private HrmAttendancePointMapper attendancePointMapper;

    @Override
    public BasePage<AttendancePointPageListVO> queryAttendancePointPageList(PageEntity pageEntity) {
        BasePage<HrmAttendancePoint> attendancePointBasePage = attendancePointMapper.selectPage(pageEntity.parse(), Wrappers.emptyWrapper());
        List<AttendancePointPageListVO> attendancePointPageListVOList = new ArrayList<>();
        attendancePointBasePage.getList().forEach(attendancePoint -> {
            AttendancePointPageListVO attendancePointPageListVO = new AttendancePointPageListVO();
            BeanUtil.copyProperties(attendancePoint, attendancePointPageListVO);
            attendancePointPageListVOList.add(attendancePointPageListVO);
        });
        BasePage<AttendancePointPageListVO> page = new BasePage<>(attendancePointBasePage.getCurrent(), attendancePointBasePage.getSize(), attendancePointBasePage.getTotal());
        page.setList(attendancePointPageListVOList);
        return page;
    }

    @Override
    public HrmAttendancePoint addAttendancePoint(AddAttendancePointBO attendancePoint) {
        HrmAttendancePoint hrmAttendancePoint = BeanUtil.copyProperties(attendancePoint, HrmAttendancePoint.class);
        save(hrmAttendancePoint);
        return hrmAttendancePoint;
    }
}
