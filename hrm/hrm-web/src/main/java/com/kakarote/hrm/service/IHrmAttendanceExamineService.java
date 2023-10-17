package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAttendanceExamine;

import java.io.Serializable;

/**
 * <p>
 * 考勤审批设置 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-07
 */
public interface IHrmAttendanceExamineService extends BaseService<HrmAttendanceExamine> {

    /**
     * 查询字段配置
     *
     * @param id 主键ID
     * @return data
     * @author guomenghao
     * @since 2023-08-07
     */
    public HrmAttendanceExamine queryById(Serializable id);

    /**
     * 保存或新增信息
     *
     * @param entity entity
     * @author guomenghao
     * @since 2023-08-07
     */
    public void addOrUpdate(HrmAttendanceExamine entity);

    /**
     * 查询请假审批配置
     *
     * @return
     */
    HrmAttendanceExamine queryHrmAttendanceExamine();
}
