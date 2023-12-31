package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AddInsuranceEmpBO;
import com.kakarote.hrm.entity.BO.QueryEmpInsuranceMonthBO;
import com.kakarote.hrm.entity.BO.QueryInsuranceRecordListBO;
import com.kakarote.hrm.entity.BO.UpdateInsuranceProjectBO;
import com.kakarote.hrm.entity.PO.HrmInsuranceMonthEmpRecord;
import com.kakarote.hrm.entity.VO.EmpInsuranceByIdVO;
import com.kakarote.hrm.entity.VO.QueryEmpInsuranceMonthVO;
import com.kakarote.hrm.entity.VO.SimpleHrmEmployeeVO;

import java.util.List;

/**
 * <p>
 * 员工每月社保记录 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
public interface IHrmInsuranceMonthEmpRecordService extends BaseService<HrmInsuranceMonthEmpRecord> {

    /**
     * 查询每月员工社保列表
     *
     * @param queryEmpInsuranceMonthBO
     * @return
     */
    BasePage<QueryEmpInsuranceMonthVO> queryEmpInsuranceMonth(QueryEmpInsuranceMonthBO queryEmpInsuranceMonthBO);

    /**
     * 查询员工社保详情
     *
     * @param iempRecordId
     * @return
     */
    EmpInsuranceByIdVO queryById(String iempRecordId);

    /**
     * 停止参保
     *
     * @param ids
     * @return
     */
    List<OperationLog> stop(List<Long> ids);

    /**
     * 修改参保方案项目
     *
     * @param updateInsuranceProjectBO
     * @return
     */
    OperationLog updateInsuranceProject(UpdateInsuranceProjectBO updateInsuranceProjectBO);

    /**
     * 添加参保人员
     *
     * @param addInsuranceEmpBO
     */
    List<OperationLog> addInsuranceEmp(AddInsuranceEmpBO addInsuranceEmpBO);

    /**
     * 查询本月没有参保人员
     *
     * @param iRecordId
     * @return
     */
    List<SimpleHrmEmployeeVO> queryNoInsuranceEmp(Long iRecordId);

    /**
     * 批量修改参保方案项目
     *
     * @param updateInsuranceProjectBO
     * @return
     */
    List<OperationLog> batchUpdateInsuranceProject(UpdateInsuranceProjectBO updateInsuranceProjectBO);

    /**
     * 我的社保
     *
     * @param recordListBO
     * @return
     */
    BasePage<HrmInsuranceMonthEmpRecord> myInsurancePageList(QueryInsuranceRecordListBO recordListBO);
}
