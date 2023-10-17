package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroupRelationDept;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 考勤组关联部门表 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-11-26
 */
public interface IHrmAttendanceGroupRelationDeptService extends BaseService<HrmAttendanceGroupRelationDept> {

    /**
     * 查询字段配置
     *
     * @param id 主键ID
     * @return data
     * @author guomenghao
     * @since 2021-11-26
     */
    public HrmAttendanceGroupRelationDept queryById(Serializable id);

    /**
     * 保存或新增信息
     *
     * @param entity entity
     * @author guomenghao
     * @since 2021-11-26
     */
    public void addOrUpdate(HrmAttendanceGroupRelationDept entity);


    /**
     * 查询所有数据
     *
     * @param search 搜索条件
     * @return list
     * @author guomenghao
     * @since 2021-11-26
     */
    public BasePage<HrmAttendanceGroupRelationDept> queryPageList(PageEntity search);

    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     * @author guomenghao
     * @since 2021-11-26
     */
    public void deleteByIds(List<Serializable> ids);
}
