package com.kakarote.hrm.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryEmployeeAchievementFileBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployee;
import com.kakarote.hrm.entity.PO.HrmEmployeeAchievementFile;
import com.kakarote.hrm.entity.VO.EmployeeAchievementFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 员工绩效档案 Mapper 接口
 * </p>
 *
 * @author zyl
 * @since 2022-06-14
 */
public interface HrmEmployeeAchievementFileMapper extends BaseMapper<HrmEmployeeAchievementFile> {

    BasePage<EmployeeAchievementFileVO> queryEmployeeAchievementFileList(BasePage<EmployeeAchievementFileVO> parse, @Param("data") QueryEmployeeAchievementFileBO employeeAchievementFileBO, @Param("employeeIds") Collection<Long> employeeIds);

    HrmAppraisalEmployee queryRecentlyAppraisal(@Param("employeeId") Long employeeId, @Param("month") String month);

    /**
     * 根据员工id查询员工考核档案
     *
     * @param employeeId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    HrmEmployeeAchievementFile queryAchievementFileByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 更新考核次数
     *
     * @param param
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateAchievementCount(@Param("data") Map<String, Object> param);


    /**
     * 更新考核次数
     *
     * @param employeeId
     */
    @InterceptorIgnore(tenantLine = "true")
    void removeByEmployeeId(@Param("employeeId") Long employeeId);
}
