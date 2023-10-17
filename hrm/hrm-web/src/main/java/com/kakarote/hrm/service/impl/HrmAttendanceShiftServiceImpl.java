package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.common.Const;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.IsEnum;
import com.kakarote.hrm.constant.ShiftTypeEnum;
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceShiftBO;
import com.kakarote.hrm.entity.BO.SetAttendanceShiftBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import com.kakarote.hrm.entity.PO.HrmAttendanceHistoryShift;
import com.kakarote.hrm.entity.PO.HrmAttendanceShift;
import com.kakarote.hrm.entity.VO.HrmAttendanceShiftVO;
import com.kakarote.hrm.mapper.HrmAttendanceShiftMapper;
import com.kakarote.hrm.service.IHrmAttendanceGroupService;
import com.kakarote.hrm.service.IHrmAttendanceHistoryShiftService;
import com.kakarote.hrm.service.IHrmAttendanceShiftService;
import com.kakarote.hrm.utils.AttendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班次表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@Service
public class HrmAttendanceShiftServiceImpl extends BaseServiceImpl<HrmAttendanceShiftMapper, HrmAttendanceShift> implements IHrmAttendanceShiftService {

    @Autowired
    private HrmAttendanceShiftMapper attendanceShiftMapper;
    @Resource
    private IHrmAttendanceGroupService attendanceGroupService;
    @Resource
    private IHrmAttendanceHistoryShiftService attendanceHistoryShiftService;
    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final int DAY = 1440;

    private static final int EIGHTHOUR = 480;

    @Override
    public BasePage<HrmAttendanceShiftVO> queryAttendanceShiftPageList(QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO) {
        QueryWrapper<HrmAttendanceShift> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotEmpty(queryHrmAttendanceShiftBO.getShiftName()), "shift_name", queryHrmAttendanceShiftBO.getShiftName());
        BasePage<HrmAttendanceShift> attendanceShiftBasePage = attendanceShiftMapper.selectPage(queryHrmAttendanceShiftBO.parse(), queryWrapper);
        List<HrmAttendanceShiftVO> attendanceShiftPageListVOList = new ArrayList<>();
        attendanceShiftBasePage.getList().forEach(attendanceShift -> {
            HrmAttendanceShiftVO attendanceShiftPageListVO = new HrmAttendanceShiftVO();
            BeanUtil.copyProperties(attendanceShift, attendanceShiftPageListVO);
            attendanceShiftPageListVOList.add(attendanceShiftPageListVO);
        });
        BasePage<HrmAttendanceShiftVO> page = new BasePage<>(attendanceShiftBasePage.getCurrent(), attendanceShiftBasePage.getSize(), attendanceShiftBasePage.getTotal());
        page.setList(attendanceShiftPageListVOList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAttendanceShift(SetAttendanceShiftBO attendanceShift) {
        HrmAttendanceShift hrmAttendanceShift = BeanUtil.copyProperties(attendanceShift, HrmAttendanceShift.class);
        Integer shiftMinute = ZERO;
        Integer restMinute = ZERO;
        DateTime effectTime = null;
        if (ObjectUtil.equal(hrmAttendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
            //今日
            String today = DateUtil.formatDate(DateUtil.date());
            //昨日
            String yesterday = DateUtil.formatDate(DateUtil.yesterday());
            //明日
            String tomorrow = DateUtil.formatDate(DateUtil.tomorrow());
            DateTime startTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getStart1()));
            DateTime endTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
            DateTime advanceTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getAdvanceCard1()));
            DateTime lateTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getLateCard1()));
            DateTime earlyTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getEarlyCard1()));
            DateTime postponeTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getPostponeCard1()));
            if (startTime.getTime() < endTime.getTime()) {
                if (advanceTime.getTime() > startTime.getTime()) {
                    advanceTime = DateUtil.parseDateTime(yesterday + " " + AttendUtil.convertTime(hrmAttendanceShift.getAdvanceCard1()));
                }
                if (startTime.getTime() > postponeTime.getTime()) {
                    postponeTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getPostponeCard1()));
                }
                if (advanceTime.getTime() >= startTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能晚于等于上班时间");
                }
                long hours = DateUtil.between(startTime, advanceTime, DateUnit.MINUTE);
                if (hours >= EIGHTHOUR) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能早于或等于上班时间的8小时");
                }
                if (advanceTime.getTime() > lateTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能晚于上班有效打卡结束时间");
                }
                if (lateTime.getTime() < startTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡结束时间不能早于上班时间");
                }
                if (lateTime.getTime() >= endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡结束时间不能晚于等于下班时间");
                }
                if (lateTime.getTime() > postponeTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡结束时间不能晚于下班有效打卡结束时间");
                }
                if (earlyTime.getTime() < startTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "下班有效打卡开始时间不能早于等于上班时间");
                }
                if (earlyTime.getTime() > endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "下班有效打卡开始时间不能晚于下班时间");
                }
                if (postponeTime.getTime() <= endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "下班有效打卡结束时间不能早于等于下班时间");
                }
                shiftMinute = Convert.toInt(DateUtil.between(startTime, endTime, DateUnit.MINUTE));
                if (hrmAttendanceShift.getRestTimeStatus() == IsEnum.YES.getValue()) {
                    DateTime restStartTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getRestStartTime()));
                    DateTime restEndTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getRestEndTime()));
                    restMinute = Convert.toInt(DateUtil.between(restStartTime, restEndTime, DateUnit.MINUTE));
                }
            } else {
                endTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
                shiftMinute = Convert.toInt(DateUtil.between(startTime, endTime, DateUnit.MINUTE));
                if (shiftMinute.compareTo(DAY) > ZERO) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班时间到下班时间之间的总时长不能超过24小时");
                }
                postponeTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getPostponeCard1()));
                if (advanceTime.getTime() > startTime.getTime()) {
                    advanceTime = DateUtil.parseDateTime(yesterday + " " + AttendUtil.convertTime(hrmAttendanceShift.getAdvanceCard1()));
                }
                if (advanceTime.getTime() >= startTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能晚于等于上班时间");
                }
                long hours = DateUtil.between(startTime, advanceTime, DateUnit.MINUTE);
                if (hours >= EIGHTHOUR) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能早于或等于上班时间的8小时");
                }
                if (lateTime.getTime() < startTime.getTime()) {
                    lateTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getLateCard1()));
                }
                if (lateTime.getTime() < advanceTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡开始时间不能晚于上班有效打卡结束时间");
                }
                if (lateTime.getTime() < startTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡结束时间不能早于上班时间");
                }
                if (lateTime.getTime() >= endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "上班有效打卡结束时间不能晚于等于下班时间");
                }
                if (earlyTime.getTime() < startTime.getTime()) {
                    earlyTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getEarlyCard1()));
                }
                if (earlyTime.getTime() > endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "下班有效打卡开始时间不能晚于下班时间");
                }
                if (postponeTime.getTime() <= endTime.getTime()) {
                    throw new CrmException(HrmCodeEnum.SHIFT_SET_ILLEGAL, "下班有效打卡结束时间不能早于等于下班时间");
                }
                if (hrmAttendanceShift.getRestTimeStatus() == IsEnum.YES.getValue()) {
                    DateTime restStartTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getRestStartTime()));
                    DateTime restEndTime = DateUtil.parseDateTime(today + " " + AttendUtil.convertTime(hrmAttendanceShift.getRestEndTime()));
                    if (restEndTime.getTime() < restStartTime.getTime()) {
                        restEndTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getRestEndTime()));
                    }
                    restMinute = Convert.toInt(DateUtil.between(restStartTime, restEndTime, DateUnit.MINUTE));
                }
            }
            if (endTime.getTime() < startTime.getTime()) {
                endTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getEnd1()));
            }
            if (postponeTime.getTime() < endTime.getTime()) {
                postponeTime = DateUtil.parseDateTime(tomorrow + " " + AttendUtil.convertTime(hrmAttendanceShift.getPostponeCard1()));
            }
            effectTime = DateUtil.offsetMinute(postponeTime, 1);
        }
        hrmAttendanceShift.setShiftHours(shiftMinute - restMinute);
        if (ObjectUtil.isNotEmpty(hrmAttendanceShift.getShiftId())) {
            hrmAttendanceShift.setEffectTime(effectTime.toLocalDateTime());
            HrmAttendanceHistoryShift historyShift = attendanceHistoryShiftService.lambdaQuery().apply("to_days(update_time) = to_days(now())").one();
            if (ObjectUtil.isEmpty(historyShift)) {
                HrmAttendanceShift hrmAttendShift = getById(hrmAttendanceShift.getShiftId());
                HrmAttendanceHistoryShift hrmAttendanceHistoryShift = BeanUtil.copyProperties(hrmAttendShift, HrmAttendanceHistoryShift.class);
                hrmAttendanceHistoryShift.setUpdateTime(LocalDateTime.now());
                attendanceHistoryShiftService.save(hrmAttendanceHistoryShift);
            }
        } else {
            hrmAttendanceShift.setEffectTime(LocalDateTime.now());
        }
        saveOrUpdate(hrmAttendanceShift);
    }

    @Override
    public void verifyAttendanceShiftName(QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO) {
        List<HrmAttendanceShift> attendanceShiftList = lambdaQuery().eq(HrmAttendanceShift::getShiftName, queryHrmAttendanceShiftBO.getShiftName())
                .ne(queryHrmAttendanceShiftBO.getShiftId() != null, HrmAttendanceShift::getShiftId, queryHrmAttendanceShiftBO.getShiftId()).list();
        if (attendanceShiftList.size() > ZERO) {
            throw new CrmException(HrmCodeEnum.SHIFT_NAME_ALREADY_EXISTS, attendanceShiftList.get(0).getShiftName());
        }
    }


    @Override
    public Map<Integer, Map<String, Object>> queryAttendanceDate(String shiftSetting) {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        List<String> shiftList = StrUtil.splitTrim(shiftSetting, Const.SEPARATOR);
        for (int i = 0; i < shiftList.size(); i++) {
            Map<String, Object> shiftMap = new HashMap<>();
            HrmAttendanceShift attendanceShift = getById(shiftList.get(i));
            if (attendanceShift != null) {
                shiftMap.put("shiftId", attendanceShift.getShiftId());
                if (ObjectUtil.equal(attendanceShift.getShiftType(), ShiftTypeEnum.REST.getValue())) {
                    shiftMap.put("shiftType", ShiftTypeEnum.REST.getValue());
                    shiftMap.put("shiftDetail", "休息");
                } else if (ObjectUtil.equal(attendanceShift.getShiftType(), ShiftTypeEnum.COMMON.getValue())) {
                    String shiftDetail = attendanceShift.getShiftName() + ":" + AttendUtil.convertTimeDivision(attendanceShift.getStart1()) + "-" + AttendUtil.convertTimeDivision(attendanceShift.getEnd1());
                    if (attendanceShift.getRestTimeStatus() == IsEnum.YES.getValue()) {
                        shiftDetail = shiftDetail + ",休息时间:" + AttendUtil.convertTimeDivision(attendanceShift.getRestStartTime()) + "-" + AttendUtil.convertTimeDivision(attendanceShift.getRestEndTime());
                    }
                    shiftMap.put("shiftType", ShiftTypeEnum.COMMON.getValue());
                    shiftMap.put("shiftDetail", shiftDetail);
                }
            }
            map.put(i + 1, shiftMap);
        }
        return map;
    }

    @Override
    public OperationLog deleteAttendanceShift(Long attendanceShiftId) {
        HrmAttendanceShift hrmAttendanceShift = attendanceShiftMapper.selectById(attendanceShiftId);
        if (hrmAttendanceShift.getIsDefaultSetting() == IsEnum.YES.getValue()) {
            throw new CrmException(HrmCodeEnum.ATTEND_DEFAULT_CANNOT_BE_DELETED, "考勤班次");
        }
        List<HrmAttendanceGroup> specialDateGroupList = attendanceGroupService.lambdaQuery().eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue())
                .like(HrmAttendanceGroup::getSpecialDateSetting, attendanceShiftId).list();
        List<HrmAttendanceGroup> shiftGroupList = attendanceGroupService.lambdaQuery().eq(HrmAttendanceGroup::getOldSetting, IsEnum.NO.getValue())
                .apply("FIND_IN_SET ('" + attendanceShiftId + "',shift_setting)").list();
        if (CollUtil.isNotEmpty(specialDateGroupList) || CollUtil.isNotEmpty(shiftGroupList)) {
            throw new CrmException(HrmCodeEnum.ATTEND_USED_CANNOT_BE_DELETED, "考勤班次");
        }
        attendanceShiftMapper.deleteById(attendanceShiftId);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmAttendanceShift.getShiftName());
        operationLog.setOperationInfo("删除考勤班次：" + hrmAttendanceShift.getShiftName());
        return operationLog;

    }
}

