package com.kakarote.hrm.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.constant.LabelGroupEnum;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 员工表 服务类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface IHrmEmployeeService extends BaseService<HrmEmployee> {

    /**
     * 新建员工
     *
     * @param employeeVO
     * @return
     */
    List<OperationLog> add(AddEmployeeBO employeeVO);

    /**
     * 查询所用员工(表单选择使用)
     *
     * @return
     */
    List<SimpleHrmEmployeeVO> queryAllEmployeeList(String employeeName);

    /**
     * 查询考核范围上级可查询所用员工(表单选择使用)
     *
     * @return
     */
    List<SimpleHrmEmployeeVO> queryInspectionAllEmployeeList(String employeeName);

    /**
     * 转换员工信息
     *
     * @param employee
     * @return
     */
    SimpleHrmEmployeeVO transferSimpleEmp(HrmEmployee employee);

    /**
     * 个人基本信息
     *
     * @param employeeId
     * @return
     */
    PersonalInformationVO personalInformation(Long employeeId);

    /**
     * 个人档案信息
     *
     * @return
     */
    PersonalInformationVO personalArchives();

    /**
     * 查询员工详情
     *
     * @param employeeId
     * @return
     */
    HrmEmployee queryById(Long employeeId);

    /**
     * 转换基本信息
     */
    List<InformationFieldVO> transferInformation(JSONObject model, LabelGroupEnum labelGroupEnum, List<HrmEmployeeData> fieldValueList);

    /**
     * 转换基本信息
     */
    List<InformationFieldVO> transferInformation(JSONObject model, List<HrmEmployeeField> employeeFields, List<HrmEmployeeData> fieldValueList);

    /**
     * 修改基本信息
     *
     * @param updateInformationBO
     */
    OperationLog updateInformation(UpdateInformationBO updateInformationBO);

    /**
     * 修改通讯信息
     *
     * @param updateInformationBO
     */
    OperationLog updateCommunication(UpdateInformationBO updateInformationBO);

    /**
     * 添加活修改教育经历
     *
     * @param educationExperience
     * @return
     */
    OperationLog addOrUpdateEduExperience(HrmEmployeeEducationExperience educationExperience);

    /**
     * 删除教育经历
     *
     * @param educationId
     */
    OperationLog deleteEduExperience(Long educationId);

    /**
     * 添加修改工作经历
     *
     * @param workExperience
     * @return
     */
    OperationLog addOrUpdateWorkExperience(HrmEmployeeWorkExperience workExperience);

    /**
     * 删除工作经历
     *
     * @param workExpId
     */
    OperationLog deleteWorkExperience(Long workExpId);

    /**
     * 添加或修改证书
     *
     * @param certificate
     */
    OperationLog addOrUpdateCertificate(HrmEmployeeCertificate certificate);

    /**
     * 删除证书
     *
     * @param certificateId
     */
    OperationLog deleteCertificate(Long certificateId);

    /**
     * 添加修改培训经历
     *
     * @param trainingExperience
     */
    OperationLog addOrUpdateTrainingExperience(HrmEmployeeTrainingExperience trainingExperience);

    /**
     * 删除培训经历
     *
     * @param trainingId
     */
    OperationLog deleteTrainingExperience(Long trainingId);

    /**
     * 查询联系人添加字段
     *
     * @return
     */
    List<HrmEmployeeField> queryContactsAddField();

    /**
     * 添加修改联系人
     *
     * @param updateInformationBO
     */
    OperationLog addOrUpdateContacts(UpdateInformationBO updateInformationBO);

    /**
     * 删除联系人
     *
     * @param contractsId
     */
    OperationLog deleteContacts(Long contractsId);

    /**
     * 删除员工
     *
     * @param employeeIds
     */
    List<OperationLog> deleteByIds(List<Long> employeeIds);

    /**
     * 转正
     *
     * @param hrmEmployeeChangeRecord
     */
    OperationLog change(HrmEmployeeChangeRecord hrmEmployeeChangeRecord);

    /**
     * 修改社保方案
     *
     * @param updateInsuranceSchemeBO
     */
    List<OperationLog> updateInsuranceScheme(UpdateInsuranceSchemeBO updateInsuranceSchemeBO);

    /**
     * 分页查询员工列表
     *
     * @param employeePageListBO
     * @return
     */
    BasePage<Map<String, Object>> queryPageList(QueryEmployeePageListBO employeePageListBO);

    /**
     * 查询员工列表
     *
     * @param employeeIds
     * @return
     */
    List<SimpleHrmEmployeeVO> querySimpleEmployeeList(Collection<Long> employeeIds);

    /**
     * 查询员工数量统计
     *
     * @return
     */
    Map<Integer, Long> queryEmployeeStatusNum();


    /**
     * 在入职
     *
     * @param employeeBO
     */
    OperationLog againOnboarding(AddEmployeeFieldManageBO employeeBO);

    /**
     * 通过手机号查询员工信息
     *
     * @param mobile
     * @return
     */
    EmployeeInfo queryEmployeeInfoByMobile(String mobile);

    /**
     * 确认入职
     *
     * @param employeeBO
     * @return
     */
    OperationLog confirmEntry(AddEmployeeFieldManageBO employeeBO);

    /**
     * 获取导入模板字段列表
     *
     * @return
     */
    List<HrmEmployeeField> downloadExcelFiled();


    /**
     * 导出员工列表
     *
     * @param employeePageListBO
     * @return
     */
    List<Map<String, Object>> export(QueryEmployeePageListBO employeePageListBO);

    /**
     * 字段唯一验证
     *
     * @param uniqueList
     * @return
     */
    Integer queryFieldValueNoDelete(List<HrmEmployeeField> uniqueList);

    /**
     * 根据月份查询待入职员工id
     *
     * @param year
     * @param month
     * @return
     */
    List<Long> queryToInByMonth(int year, int month);

    /**
     * 根绝月份查询待离职员工id
     *
     * @param year
     * @param month
     * @return
     */
    List<Long> queryToLeaveByMonth(int year, int month);

    /**
     * 查询待转正数量
     *
     * @return
     */
    List<Long> queryToCorrectCount();


    /**
     * 查询过生日员工
     *
     * @param time
     * @param employeeIds
     * @return
     */
    List<HrmEmployee> queryBirthdayListByTime(LocalDate time, Collection<Long> employeeIds);

    /**
     * 查询入职员工
     *
     * @param time
     * @param employeeIds
     * @return
     */
    List<HrmEmployee> queryEntryEmpListByTime(LocalDate time, Collection<Long> employeeIds);

    /**
     * 查询转正员工
     *
     * @param time
     * @param employeeIds
     * @return
     */
    List<HrmEmployee> queryBecomeEmpListByTime(LocalDate time, Collection<Long> employeeIds);

    /**
     * 查询离职员工
     *
     * @param time
     * @param employeeIds
     * @return
     */
    List<HrmEmployee> queryLeaveEmpListByTime(LocalDate time, Collection<Long> employeeIds);


    /**
     * 查询生日员工
     *
     * @return
     */
    List<Long> queryBirthdayEmp();

    /**
     * 查询在职员工
     *
     * @return
     */
    List<SimpleHrmEmployeeVO> queryInEmployeeList();


    /**
     * 查询部门员工列表
     *
     * @param deptId
     * @return
     */
    DeptEmployeeListVO queryDeptEmployeeList(Long deptId);


    /**
     * 查看考核范围员工列表
     *
     * @return
     */
    DeptEmployeeListVO queryInspectionDeptEmployeeList(Long deptId);

    /**
     * 从系统用户添加员工
     *
     * @param employeeBOS
     */
    List<OperationLog> adminAddEmployee(List<AddEmployeeBO> employeeBOS);

    /**
     * 查询部门用户列表(hrm添加员工使用)
     *
     * @param deptUserListByUserBO
     * @return
     */
    Set<SimpleHrmEmployeeVO> queryDeptUserListByUser(DeptUserListByUserBO deptUserListByUserBO);

    /**
     * 过滤删除员工
     *
     * @param employeeIds
     * @return
     */
    Set<Long> filterDeleteEmployeeIds(Set<Long> employeeIds);

    List<SimpleHrmEmployeeVO> queryAllSimpleEmployeeList(Collection<Long> employeeIds);

    /**
     * 查询下级员工
     *
     * @param employeeIds
     * @return
     */
    Set<Long> queryChildEmployeeId(List<Long> employeeIds);

    /**
     * 查询员工入职时间
     *
     * @param queryNotesStatusBO
     * @param employeeIds
     * @return
     */
    Set<String> queryEntryStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds);

    /**
     * 查询员工转正时间
     *
     * @param queryNotesStatusBO
     * @param employeeIds
     * @return
     */
    Set<String> queryBecomeStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds);

    /**
     * 查询员工离职时间
     *
     * @param queryNotesStatusBO
     * @param employeeIds
     * @return
     */
    Set<String> queryLeaveStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds);

    /**
     * 查询新增所需要字段
     *
     * @param entryStatus
     * @return
     */
    List<HrmModelFiledVO> queryEmployeeField(Integer entryStatus);

    /**
     * 保存自定义员工字段
     *
     * @param addEmployeeFieldManageBO
     */
    List<OperationLog> addEmployeeField(AddEmployeeFieldManageBO addEmployeeFieldManageBO);

    /**
     * 格式化数据
     *
     * @param record   data
     * @param typeEnum type
     */
    public void recordToFormType(InformationFieldVO record, FieldEnum typeEnum);

    /**
     * 查询员工信息
     *
     * @param employeeIds
     * @return
     */
    SimpleHrmEmployeeVO querySimpleEmployee(Long employeeIds);

    /**
     * 查询部门员工列表(考勤打卡调用)
     *
     * @param deptId
     * @return
     */
    DeptEmployeeListVO queryAttendDeptEmployeeList(Long deptId);

    /**
     * 查询考勤范围可查询的所有员工
     *
     * @param employeeName
     * @return
     */
    List<SimpleHrmEmployeeVO> queryAttendanceAllEmployeeList(String employeeName);
}
