package com.kakarote.hrm.service.impl;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.PO.HrmAttendanceDateShift;
import com.kakarote.hrm.mapper.HrmAttendanceDateShiftMapper;
import com.kakarote.hrm.service.IHrmAttendanceDateShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 每日出勤班次 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-19
 */
@Service
public class HrmAttendanceDateShiftServiceImpl extends BaseServiceImpl<HrmAttendanceDateShiftMapper, HrmAttendanceDateShift> implements IHrmAttendanceDateShiftService {

    /**
     * 查询字段配置
     *
     * @param id 主键ID
     * @return data
     * @author guomenghao
     * @since 2023-08-19
     */
    @Override
    public HrmAttendanceDateShift queryById(Serializable id) {
        return getById(id);
    }

    /**
     * 保存或新增信息
     *
     * @param entity entity
     * @author guomenghao
     * @since 2023-08-19
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(HrmAttendanceDateShift entity) {
        saveOrUpdate(entity);
    }


    /**
     * 查询所有数据
     *
     * @param search 搜索条件
     * @return list
     * @author guomenghao
     * @since 2023-08-19
     */
    @Override
    public BasePage<HrmAttendanceDateShift> queryPageList(PageEntity search) {
        return lambdaQuery().page(search.parse());
    }

    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     * @author guomenghao
     * @since 2023-08-19
     */
    @Override
    public void deleteByIds(List<Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }
    /**
     * 查询出已经初始化过历史班次的员工
     *
     * @param localDateTime
     * @return
     */
    @Override
    public Set<Long> queryEmployeeIds(LocalDateTime localDateTime) {
        return baseMapper.queryEmployeeIds(localDateTime);
    }
}
