package com.kakarote.core.servlet;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangzhiwei
 * 通用mapper
 */

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return entity
     */
    @Override
    default T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> dataList = selectList(queryWrapper);
        if (dataList.isEmpty()) {
            return null;
        }
        return dataList.iterator().next();
    }
}
