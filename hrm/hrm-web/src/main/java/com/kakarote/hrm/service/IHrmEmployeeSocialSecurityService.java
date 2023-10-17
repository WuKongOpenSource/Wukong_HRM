package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QuerySalaryListBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeSalaryCard;
import com.kakarote.hrm.entity.PO.HrmEmployeeSocialSecurityInfo;
import com.kakarote.hrm.entity.VO.QuerySalaryListVO;
import com.kakarote.hrm.entity.VO.SalaryOptionHeadVO;
import com.kakarote.hrm.entity.VO.SalarySocialSecurityVO;

import java.util.List;

/**
 * <p>
 * 员工公积金信息 服务类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface IHrmEmployeeSocialSecurityService extends BaseService<HrmEmployeeSocialSecurityInfo> {

    /**
     * 工资社保基本信息
     *
     * @param employeeId
     * @return
     */
    SalarySocialSecurityVO salarySocialSecurityInformation(Long employeeId);

    /**
     * 添加修改工资卡
     *
     * @param salaryCard
     */
    void addOrUpdateSalaryCard(HrmEmployeeSalaryCard salaryCard);

    /**
     * 删除工资卡
     *
     * @param salaryCardId
     */
    void deleteSalaryCard(Long salaryCardId);

    /**
     * 添加修改社保信息
     *
     * @param socialSecurityInfo
     */
    OperationLog addOrUpdateSocialSecurity(HrmEmployeeSocialSecurityInfo socialSecurityInfo);

    /**
     * 删除社保
     *
     * @param socialSecurityInfoId
     */
    void deleteSocialSecurity(Long socialSecurityInfoId);

    /**
     * 查询薪资列表
     *
     * @param querySalaryListBO
     * @return
     */
    BasePage<QuerySalaryListVO> querySalaryList(QuerySalaryListBO querySalaryListBO);

    /**
     * 查询薪资详情
     *
     * @param sEmpRecordId
     * @return
     */
    List<SalaryOptionHeadVO> querySalaryDetail(String sEmpRecordId);
}
