package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceGroupBO;
import com.kakarote.hrm.entity.BO.SetAttendanceGroupBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import com.kakarote.hrm.entity.VO.HrmAttendanceGroupVO;
import com.kakarote.hrm.entity.VO.QueryMyAttendanceGroupVO;

import java.util.List;

/**
 * <p>
 * 考勤组表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
public interface IHrmAttendanceGroupService extends BaseService<HrmAttendanceGroup> {
    /**
     * 查询考勤组列表
     *
     * @param queryHrmAttendanceGroupBO
     * @return
     */
    BasePage<HrmAttendanceGroupVO> queryAttendanceGroupPageList(QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO);

    /**
     * 新增或修改考勤组
     *
     * @param attendanceGroup
     */
    void setAttendanceGroup(SetAttendanceGroupBO attendanceGroup);

    /**
     * 校验考勤组名称
     *
     * @param queryHrmAttendanceGroupBO
     * @return
     */
    void verifyAttendanceGroupName(QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO);

    /**
     * 查询员工属于考勤组
     *
     * @param employeeId
     * @return
     */
    HrmAttendanceGroup queryAttendanceGroup(Long employeeId);

    /**
     * 查询我的考勤分组
     *
     * @param employeeId
     * @return
     */
    QueryMyAttendanceGroupVO queryMyAttendanceGroup(Long employeeId);

    /**
     * 通过考勤组id删除考勤组
     *
     * @param attendanceGroupId
     */
    OperationLog deleteAttendanceGroup(Long attendanceGroupId);

    /**
     * 变更考勤组相关考勤组
     *
     * @param hrmAttendanceGroup
     */
    void changeAttendanceGroup(HrmAttendanceGroup hrmAttendanceGroup);

    /**
     * 为未初始化今日之前考勤班次的员工设置出勤班次
     */
    void setAttendanceDateShiftByGroup();

    /**
     * 查询是否存在默认考勤组
     */
    void checkInitAttendData();
}
