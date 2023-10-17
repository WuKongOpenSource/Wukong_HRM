package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryOverTimeRecordPageListBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeOverTimeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 员工加班表 Mapper 接口
 * </p>
 *
 * @author guomenghao
 * @since 2021-09-07
 */
public interface HrmEmployeeOverTimeRecordMapper extends BaseMapper<HrmEmployeeOverTimeRecord> {
    /**
     * 查询加班记录列表
     *
     * @param page
     * @param queryOverTimeRecordPageListBO
     * @return
     */
    BasePage<Map<String, Object>> queryOverTimeRecordPageList(BasePage<Map<String, Object>> page, @Param("data") QueryOverTimeRecordPageListBO queryOverTimeRecordPageListBO);


}
