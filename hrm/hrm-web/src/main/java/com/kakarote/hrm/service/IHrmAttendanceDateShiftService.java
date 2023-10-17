package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAttendanceDateShift;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 每日出勤班次 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-19
 */
public interface IHrmAttendanceDateShiftService extends BaseService<HrmAttendanceDateShift> {

    /**
     * 查询字段配置
     *
     * @param id 主键ID
     * @return data
     * @author guomenghao
     * @since 2023-08-19
     */
    public HrmAttendanceDateShift queryById(Serializable id);

    /**
     * 保存或新增信息
     *
     * @param entity entity
     * @author guomenghao
     * @since 2023-08-19
     */
    public void addOrUpdate(HrmAttendanceDateShift entity);


    /**
     * 查询所有数据
     *
     * @param search 搜索条件
     * @return list
     * @author guomenghao
     * @since 2023-08-19
     */
    public BasePage<HrmAttendanceDateShift> queryPageList(PageEntity search);

    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     * @author guomenghao
     * @since 2023-08-19
     */
    public void deleteByIds(List<Serializable> ids);

    /**
     * 查询出已经初始化过历史班次的员工
     *
     * @param localDateTime 时间
     * @return
     */
    Set<Long> queryEmployeeIds(LocalDateTime localDateTime);
}
