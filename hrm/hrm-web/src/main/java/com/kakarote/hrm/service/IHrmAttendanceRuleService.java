package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.SetAttendanceRuleBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceRule;
import com.kakarote.hrm.entity.VO.AttendanceRulePageListVO;

/**
 * <p>
 * 打卡规则表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-10
 */
public interface IHrmAttendanceRuleService extends BaseService<HrmAttendanceRule> {
    /**
     * 查询考勤列表
     *
     * @param pageEntity
     * @return
     */
    BasePage<AttendanceRulePageListVO> queryAttendanceRulePageList(PageEntity pageEntity);

    /**
     * 添加或修改考勤规则
     *
     * @param attendanceRule
     */
    void setAttendanceRule(SetAttendanceRuleBO attendanceRule);

    /**
     * 校验考勤规则名称
     *
     * @param attendanceRule
     */
    void verifyAttendanceRuleName(SetAttendanceRuleBO attendanceRule);

    /**
     * 通过考勤规则id删除考勤规则
     *
     * @param attendanceRuleId
     * @return
     */
    OperationLog deleteAttendanceRule(Long attendanceRuleId);
}
