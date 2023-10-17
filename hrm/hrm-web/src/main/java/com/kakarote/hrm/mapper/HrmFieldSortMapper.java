package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmFieldSort;
import com.kakarote.hrm.entity.VO.HrmFieldSortVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字段排序表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-19
 */
public interface HrmFieldSortMapper extends BaseMapper<HrmFieldSort> {

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    public List<HrmFieldSortVO> queryListHead(@Param("label") Integer label, @Param("userId") Long userId);

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    public List<HrmFieldSortVO> queryListHeadByIds(@Param("label") Integer label, @Param("userId") Long userId, @Param("ids") List<Long> ids);
}
