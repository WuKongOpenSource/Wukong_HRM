package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryHistorySalaryDetailBO;
import com.kakarote.hrm.entity.BO.QuerySalaryPageListBO;
import com.kakarote.hrm.entity.PO.HrmSalaryMonthEmpRecord;
import com.kakarote.hrm.entity.VO.QuerySalaryListVO;
import com.kakarote.hrm.entity.VO.QuerySalaryPageListVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工每月薪资记录 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
public interface HrmSalaryMonthEmpRecordMapper extends BaseMapper<HrmSalaryMonthEmpRecord> {

    /**
     * 查询计薪人员id
     *
     * @return
     */
    List<Map<String, Object>> queryPaySalaryEmployeeList(@Param("endTime") LocalDate endTime, @Param("employeeIds") Collection<Long> dataAuthEmployeeIds);

    BasePage<QuerySalaryPageListVO> querySalaryPageList(BasePage<QuerySalaryPageListVO> parse, @Param("data") QuerySalaryPageListBO querySalaryPageListBO,
                                                        @Param("employeeIds") Collection<Long> dataAuthEmployeeIds);

    BasePage<QuerySalaryPageListVO> querySalaryPageListByRecordId(BasePage<QuerySalaryPageListVO> parse, @Param("data") QueryHistorySalaryDetailBO queryHistorySalaryDetailBO,
                                                                  @Param("employeeIds") Collection<Long> employeeIds);

    List<Long> querysEmpRecordIds(@Param("data") QuerySalaryPageListBO querySalaryPageListBO,
                                  @Param("employeeIds") Collection<Long> dataAuthEmployeeIds);

    BasePage<QuerySalaryListVO> querySalaryRecord(BasePage<Object> parse, @Param("employeeId") Long employeeId);

    List<Long> queryEmployeeIds(@Param("sRecordId") Long sRecordId, @Param("employeeIds") Collection<Long> dataAuthEmployeeIds);
}
