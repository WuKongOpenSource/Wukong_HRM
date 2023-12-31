package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmConfig;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 人力资源配置表 Mapper 接口
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-13
 */
public interface HrmConfigMapper extends BaseMapper<HrmConfig> {
    /**
     * @param tableName
     */
    void removeAllData(@Param("tableName") String tableName);
}
