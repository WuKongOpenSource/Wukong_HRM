package com.kakarote.hrm.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryEmployeePageListBO;
import com.kakarote.hrm.entity.BO.QueryNotesStatusBO;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.PO.HrmEmployeeField;
import com.kakarote.hrm.entity.VO.EmployeeInfo;
import com.kakarote.hrm.entity.VO.SimpleHrmEmployeeVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 员工表 Mapper 接口
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface HrmEmployeeMapper extends BaseMapper<HrmEmployee> {

    @InterceptorIgnore(tenantLine = "true")
    BasePage<Map<String, Object>> queryPageList(BasePage<Map<String, Object>> page,
                                                @Param("data") QueryEmployeePageListBO employeePageListBO,
                                                @Param("birthdayEmpList") List<Long> birthdayEmpList,
                                                @Param("employeeIds") Collection<Long> employeeIds);

    EmployeeInfo queryEmployeeInfoByMobile(String mobile);

    List<Map<String, Object>> queryNoPaySalaryEmployee(@Param("employeeIds") List<Long> employeeIds);

    Integer queryFieldValueNoDelete(@Param("uniqueList") List<HrmEmployeeField> uniqueList);

    List<Long> queryToInByMonth(@Param("year") int year, @Param("month") int month);

    List<Long> queryToLeaveByMonth(@Param("year") int year, @Param("month") int month);


    /**
     * 查询生日员工数量
     *
     * @param lunarList 农历
     * @param solarList 阳历
     * @return
     */
    List<Long> queryBirthdayEmp(@Param("lunarList") List<String> lunarList, @Param("solarList") List<String> solarList);

    /**
     * 查询生日员工
     *
     * @param lunarBirthday 农历
     * @param solarBirthday 阳历
     * @param employeeIds
     * @return
     */
    List<HrmEmployee> queryBirthdayListByTime(@Param("lunarBirthday") String lunarBirthday, @Param("solarBirthday") String solarBirthday,
                                              @Param("employeeIds") Collection<Long> employeeIds);

    List<HrmEmployee> queryEntryEmpListByTime(@Param("time") LocalDate time, @Param("employeeIds") Collection<Long> employeeIds);

    List<HrmEmployee> queryBecomeEmpListByTime(@Param("time") LocalDate time, @Param("employeeIds") Collection<Long> employeeIds);

    List<HrmEmployee> queryLeaveEmpListByTime(@Param("time") LocalDate time, @Param("employeeIds") Collection<Long> employeeIds);

    List<Long> queryToCorrectCount();

    List<Integer> queryToInCount();

    List<SimpleHrmEmployeeVO> querySimpleEmpByDeptId(Long deptId);

    List<Map<String, Object>> export(@Param("data") QueryEmployeePageListBO employeePageListBO);

    Set<Long> filterDeleteEmployeeIds(@Param("employeeIds") Set<Long> employeeIds);

    Set<String> queryEntryStatusList(@Param("data") QueryNotesStatusBO queryNotesStatusBO, @Param("employeeIds") Collection<Long> employeeIds);

    Set<String> queryBecomeStatusList(@Param("data") QueryNotesStatusBO queryNotesStatusBO, @Param("employeeIds") Collection<Long> employeeIds);

    Set<String> queryLeaveStatusList(@Param("data") QueryNotesStatusBO queryNotesStatusBO, @Param("employeeIds") Collection<Long> employeeIds);
}
