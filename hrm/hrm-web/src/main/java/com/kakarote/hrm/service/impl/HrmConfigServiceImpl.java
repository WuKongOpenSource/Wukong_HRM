package com.kakarote.hrm.service.impl;

import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.mapper.HrmConfigMapper;
import com.kakarote.hrm.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 人力资源配置表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-13
 */
@Service
@Slf4j
public class HrmConfigServiceImpl extends BaseServiceImpl<HrmConfigMapper, HrmConfig> implements IHrmConfigService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IAdminMessageService adminMessageService;

    @Autowired
    private IHrmAttendanceGroupService attendanceGroupService;

    @Autowired
    private IHrmAttendanceRuleService attendanceRuleService;

    @Autowired
    private IHrmAttendanceShiftService attendanceShiftService;

    @Autowired
    private IHrmDeptService hrmDeptService;

    @Autowired
    private IHrmAttendanceGroupRelationDeptService attendanceGroupRelationDeptService;

    @Override
    public void addOrUpdate(HrmConfig hrmConfig) {
        saveOrUpdate(hrmConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initHrmData() {
    }

    /**
     * 初始化默认考勤相关的参数
     */
    @Override
    public void initAttendData() {
        //初始化默认扣款规则
        HrmAttendanceRule hrmAttendanceRule = new HrmAttendanceRule();
        BigDecimal money = new BigDecimal(0.00);
        hrmAttendanceRule.setAttendanceRuleName("默认扣款规则");
        hrmAttendanceRule.setLateRuleMethod(1);
        hrmAttendanceRule.setLateDeductMoney(money);
        hrmAttendanceRule.setEarlyRuleMethod(1);
        hrmAttendanceRule.setEarlyDeductMoney(money);
        hrmAttendanceRule.setMisscardRuleMethod(1);
        hrmAttendanceRule.setMisscardDeductMoney(money);
        hrmAttendanceRule.setAbsenteeismRuleMethod(1);
        hrmAttendanceRule.setAbsenteeismDeductMoney(money);
        hrmAttendanceRule.setIsPersonalization(0);
        hrmAttendanceRule.setLateMinutesOrCounts(0);
        hrmAttendanceRule.setIsDefaultSetting(1);
        attendanceRuleService.save(hrmAttendanceRule);
        Long attendanceRuleId = hrmAttendanceRule.getAttendanceRuleId();
        //初始化班次休息
        HrmAttendanceShift hrmAttendanceShift = new HrmAttendanceShift();
        hrmAttendanceShift.setShiftType(0);
        hrmAttendanceShift.setShiftName("休息");
        hrmAttendanceShift.setShiftHours(0);
        hrmAttendanceShift.setIsDefaultSetting(1);
        hrmAttendanceShift.setEffectTime(LocalDateTime.now());
        attendanceShiftService.save(hrmAttendanceShift);
        Long restId = hrmAttendanceShift.getShiftId();
        //初始化班次早晚打卡
        HrmAttendanceShift shift = new HrmAttendanceShift();
        shift.setShiftType(1);
        shift.setShiftName("早晚打卡");
        shift.setShiftHours(480);
        shift.setStart1("09:00");
        shift.setEnd1("18:00");
        shift.setAdvanceCard1("05:00");
        shift.setLateCard1("17:59");
        shift.setEarlyCard1("9:01");
        shift.setPostponeCard1("04:59");
        shift.setRestTimeStatus(0);
        shift.setRestStartTime("12:00");
        shift.setRestEndTime("13:00");
        shift.setIsDefaultSetting(1);
        shift.setEffectTime(LocalDateTime.now());
        attendanceShiftService.save(shift);
        Long singleShiftId = shift.getShiftId();
        //初始化默认考勤组
        HrmAttendanceGroup attendanceGroup = new HrmAttendanceGroup();
        attendanceGroup.setName("默认考勤组");
        attendanceGroup.setDailyTime(new BigDecimal(8.00));
        attendanceGroup.setAttendanceRuleId(attendanceRuleId);
        attendanceGroup.setIsOpenPointCard(0);
        attendanceGroup.setIsOpenWifiCard(0);
        attendanceGroup.setIsAutoCard(0);
        attendanceGroup.setIsRest(1);
        attendanceGroup.setIsDefaultSetting(1);
        attendanceGroup.setOldSetting(0);
        attendanceGroup.setEffectTime(LocalDate.now().atStartOfDay());
        attendanceGroupService.save(attendanceGroup);
        Long attendanceGroupId = attendanceGroup.getAttendanceGroupId();
        //查询出顶级部门
        HrmDept hrmDept = hrmDeptService.lambdaQuery().eq(HrmDept::getParentId, 0L).last("limit 1").one();
        Long deptId = hrmDept.getDeptId();
        //考勤组关联部门
        HrmAttendanceGroupRelationDept hrmAttendanceGroupRelationDept = HrmAttendanceGroupRelationDept.builder().attendanceGroupId(attendanceGroupId)
                .deptId(deptId).effectTime(LocalDateTime.now()).build();
        attendanceGroupRelationDeptService.save(hrmAttendanceGroupRelationDept);
        //设置考勤班次
        updateAttendGroup(restId, UserUtil.getUserId(), attendanceGroupId, singleShiftId);
    }

    private void updateAttendGroup(Long restId, Long userId, Long attendanceGroupId, Long singleShift) {
        List<Long> shifts = Arrays.asList(singleShift, singleShift, singleShift, singleShift, singleShift, restId, restId);
        StringBuffer sb = new StringBuffer();
        shifts.forEach(shift -> {
            sb.append(shift.toString()).append(",");
        });
        String shiftStr = sb.deleteCharAt(sb.length() - 1).toString();
        attendanceGroupService.lambdaUpdate().eq(HrmAttendanceGroup::getAttendanceGroupId, attendanceGroupId)
                .set(HrmAttendanceGroup::getShiftSetting, shiftStr)
                .set(HrmAttendanceGroup::getUpdateUserId, userId).update();
    }

}
