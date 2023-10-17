package com.kakarote.hrm.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.utils.ExcelParseUtil;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.constant.FieldTypeEnum;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.DeptEmployeeListVO;
import com.kakarote.hrm.entity.VO.EmployeeInfo;
import com.kakarote.hrm.entity.VO.PersonalInformationVO;
import com.kakarote.hrm.entity.VO.SimpleHrmEmployeeVO;
import com.kakarote.hrm.service.HrmUploadExcelService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 员工表 前端控制器
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/hrmEmployee")
@Api(tags = "员工管理")
@Slf4j
public class HrmEmployeeController {

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private HrmUploadExcelService excelService;

    @PostMapping("/queryLoginEmployee")
    @ApiOperation("查询登录员工")
    public Result<EmployeeInfo> queryLoginEmployee() {
        return Result.ok(EmployeeHolder.getEmployeeInfo());
    }

    @PostMapping("/addEmployee")
    @ApiOperation("新建员工")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.SAVE)
    public Result addEmployee(@Valid @RequestBody AddEmployeeBO employeeVO) {
        List<OperationLog> operationLogList = employeeService.add(employeeVO);
        return OperationResult.ok(operationLogList);
    }

    @PostMapping("/confirmEntry")
    @ApiOperation("确认入职")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.DETERMINE_ENTRY)
    public Result confirmEntry(@RequestBody AddEmployeeFieldManageBO employeeBO) {
        OperationLog operationLog = employeeService.confirmEntry(employeeBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/queryEmployeeStatusNum")
    @ApiOperation("查询每个员工状态的数量")
    public Result<Map<Integer, Long>> queryEmployeeStatusNum() {
        Map<Integer, Long> statusMap = employeeService.queryEmployeeStatusNum();
        return Result.ok(statusMap);
    }

    @PostMapping("/queryPageList")
    @ApiOperation("分页查询员工列表")
    public Result<BasePage<Map<String, Object>>> queryPageList(@RequestBody QueryEmployeePageListBO employeePageListBO) {
        BasePage<Map<String, Object>> map = employeeService.queryPageList(employeePageListBO);
        return Result.ok(map);
    }

    @PostMapping("/queryAllEmployeeList")
    @ApiOperation("查询所用员工(表单选择使用)")
    public Result<List<SimpleHrmEmployeeVO>> queryAllEmployeeList(@RequestParam(name = "employeeName", required = false) String employeeName) {
        List<SimpleHrmEmployeeVO> list = employeeService.queryAllEmployeeList(employeeName);
        return Result.ok(list);
    }


    @PostMapping("/queryInspectionAllEmployeeList")
    @ApiOperation("查询考核范围可查询的所有员工(表单选择使用)")
    public Result<List<SimpleHrmEmployeeVO>> queryInspectionAllEmployeeList(@RequestParam(name = "employeeName", required = false) String employeeName) {
        List<SimpleHrmEmployeeVO> list = employeeService.queryInspectionAllEmployeeList(employeeName);
        return Result.ok(list);
    }

    @PostMapping("/queryAttendanceAllEmployeeList")
    @ApiOperation("查询考勤范围可查询的所有员工(表单选择使用)")
    public Result<List<SimpleHrmEmployeeVO>> queryAttendanceAllEmployeeList(@RequestParam(name = "employeeName", required = false) String employeeName) {
        List<SimpleHrmEmployeeVO> list = employeeService.queryAttendanceAllEmployeeList(employeeName);
        return Result.ok(list);
    }

    @PostMapping("/queryDeptEmployeeList/{deptId}")
    @ApiOperation("查询部门员工列表")
    public Result<DeptEmployeeListVO> queryDeptEmployeeList(@PathVariable("deptId") Long deptId) {
        DeptEmployeeListVO deptEmployeeListVO = employeeService.queryDeptEmployeeList(deptId);
        return Result.ok(deptEmployeeListVO);
    }

    @PostMapping("/queryInspectionDeptEmployeeList/{deptId}")
    @ApiOperation("查询部门员工列表")
    public Result<DeptEmployeeListVO> queryInspectionDeptEmployeeList(@PathVariable("deptId") Long deptId) {
        DeptEmployeeListVO deptEmployeeListVO = employeeService.queryInspectionDeptEmployeeList(deptId);
        return Result.ok(deptEmployeeListVO);
    }

    @PostMapping("/queryAttendDeptEmployeeList/{deptId}")
    @ApiOperation("查询部门员工列表(考勤打卡调用)")
    public Result<DeptEmployeeListVO> queryAttendDeptEmployeeList(@PathVariable("deptId") Long deptId) {
        DeptEmployeeListVO deptEmployeeListVO = employeeService.queryAttendDeptEmployeeList(deptId);
        return Result.ok(deptEmployeeListVO);
    }

    @PostMapping("/queryDeptEmpListByUser")
    @ApiOperation("查询部门用户列表(hrm添加员工使用)")
    public Result<Set<SimpleHrmEmployeeVO>> queryDeptEmpListByUser(@RequestBody DeptUserListByUserBO deptUserListByUserBO) {
        Set<SimpleHrmEmployeeVO> userList = employeeService.queryDeptUserListByUser(deptUserListByUserBO);
        return Result.ok(userList);
    }

    @PostMapping("/queryInEmployeeList")
    @ApiOperation("查询在职员工(表单选择使用)")
    public Result<List<SimpleHrmEmployeeVO>> queryInEmployeeList() {
        List<SimpleHrmEmployeeVO> list = employeeService.queryInEmployeeList();
        return Result.ok(list);
    }


    @PostMapping("/personalInformation/{employeeId}")
    @ApiOperation("个人基本信息")
    public Result<PersonalInformationVO> personalInformation(@PathVariable("employeeId") Long employeeId) {
        PersonalInformationVO personalInformationVO = employeeService.personalInformation(employeeId);
        return Result.ok(personalInformationVO);
    }

    @PostMapping("/queryById/{employeeId}")
    @ApiOperation("查询员工详情")
    public Result<HrmEmployee> queryById(@PathVariable("employeeId") Long employeeId) {
        HrmEmployee hrmEmployee = employeeService.queryById(employeeId);
        return Result.ok(hrmEmployee);
    }

    @PostMapping("/updateInformation")
    @ApiOperation("修改员工基本信息")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result updateInformation(@RequestBody UpdateInformationBO updateInformationBO) {
        OperationLog operationLog = employeeService.updateInformation(updateInformationBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/updateCommunication")
    @ApiOperation("修改通讯信息")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result updateCommunication(@RequestBody UpdateInformationBO updateInformationBO) {
        OperationLog operationLog = employeeService.updateCommunication(updateInformationBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/addExperience")
    @ApiOperation("添加教育经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result addOrUpdateEduExperience(@Validated @RequestBody HrmEmployeeEducationExperience educationExperience) {
        OperationLog operationLog = employeeService.addOrUpdateEduExperience(educationExperience);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setEduExperience")
    @ApiOperation("修改教育经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result setEduExperience(@Validated @RequestBody HrmEmployeeEducationExperience educationExperience) {
        OperationLog operationLog = employeeService.addOrUpdateEduExperience(educationExperience);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/deleteEduExperience/{educationId}")
    @ApiOperation("删除教育经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result deleteEduExperience(@PathVariable("educationId") Long educationId) {
        OperationLog operationLog = employeeService.deleteEduExperience(educationId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/addWorkExperience")
    @ApiOperation("添加工作经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result addWorkExperience(@Validated @RequestBody HrmEmployeeWorkExperience workExperience) {
        OperationLog operationLog = employeeService.addOrUpdateWorkExperience(workExperience);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setWorkExperience")
    @ApiOperation("修改工作经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result setWorkExperience(@Validated @RequestBody HrmEmployeeWorkExperience workExperience) {
        OperationLog operationLog = employeeService.addOrUpdateWorkExperience(workExperience);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/deleteWorkExperience/{workExpId}")
    @ApiOperation("删除工作经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result deleteWorkExperience(@PathVariable("workExpId") Long workExpId) {
        OperationLog operationLog = employeeService.deleteWorkExperience(workExpId);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/addCertificate")
    @ApiOperation("添加证书")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result addCertificate(@Validated @RequestBody HrmEmployeeCertificate certificate) {
        employeeService.addOrUpdateCertificate(certificate);
        return Result.ok();
    }

    @PostMapping("/setCertificate")
    @ApiOperation("修改证书")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result setCertificate(@Validated @RequestBody HrmEmployeeCertificate certificate) {
        employeeService.addOrUpdateCertificate(certificate);
        return Result.ok();
    }

    @PostMapping("/deleteCertificate/{certificateId}")
    @ApiOperation("删除证书")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result deleteCertificate(@PathVariable("certificateId") Long certificateId) {
        employeeService.deleteCertificate(certificateId);
        return Result.ok();
    }


    @PostMapping("/addTrainingExperience")
    @ApiOperation("添加培训经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result addTrainingExperience(@Validated @RequestBody HrmEmployeeTrainingExperience trainingExperience) {
        OperationLog operationLog = employeeService.addOrUpdateTrainingExperience(trainingExperience);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setTrainingExperience")
    @ApiOperation("修改培训经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)

    public Result setTrainingExperience(@Validated @RequestBody HrmEmployeeTrainingExperience trainingExperience) {
        OperationLog operationLog = employeeService.addOrUpdateTrainingExperience(trainingExperience);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/deleteTrainingExperience/{trainingId}")
    @ApiOperation("删除培训经历")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)

    public Result deleteTrainingExperience(@PathVariable("trainingId") Long trainingId) {
        OperationLog operationLog = employeeService.deleteTrainingExperience(trainingId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/queryContactsAddField")
    @ApiOperation("查询联系人添加字段")
    public Result<List<HrmEmployeeField>> queryContactsAddField() {
        List<HrmEmployeeField> hrmEmployeeFieldList = employeeService.queryContactsAddField();
        return Result.ok(hrmEmployeeFieldList);
    }


    @PostMapping("/addContacts")
    @ApiOperation("添加联系人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result addContacts(@RequestBody UpdateInformationBO updateInformationBO) {
        OperationLog operationLog = employeeService.addOrUpdateContacts(updateInformationBO);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setContacts")
    @ApiOperation("修改联系人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result setContacts(@RequestBody UpdateInformationBO updateInformationBO) {
        OperationLog operationLog = employeeService.addOrUpdateContacts(updateInformationBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/deleteContacts/{contractsId}")
    @ApiOperation("删除联系人")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)

    public Result deleteContacts(@PathVariable("contractsId") Long contractsId) {
        OperationLog operationLog = employeeService.deleteContacts(contractsId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("删除员工")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.DELETE)
    public Result deleteByIds(@RequestBody List<Long> employeeIds) {
        List<OperationLog> operationLogList = employeeService.deleteByIds(employeeIds);
        return OperationResult.ok(operationLogList);
    }


    @PostMapping("/become")
    @ApiOperation("转正")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.BECOME)
    public Result become(@RequestBody HrmEmployeeChangeRecord hrmEmployeeChangeRecord) {
        OperationLog operationLog = employeeService.change(hrmEmployeeChangeRecord);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/changePost")
    @ApiOperation("调整部门/岗位")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.CHANGE_POST)
    public Result changePost(@RequestBody HrmEmployeeChangeRecord hrmEmployeeChangeRecord) {
        OperationLog operationLog = employeeService.change(hrmEmployeeChangeRecord);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/promotion")
    @ApiOperation("晋升/降级")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.PROMOTION)
    public Result promotion(@RequestBody HrmEmployeeChangeRecord hrmEmployeeChangeRecord) {
        OperationLog operationLog = employeeService.change(hrmEmployeeChangeRecord);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/updateInsuranceScheme")
    @ApiOperation("修改社保方案")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.INSURANCE_SCHEME)
    public Result updateInsuranceScheme(@RequestBody UpdateInsuranceSchemeBO updateInsuranceSchemeBO) {
        List<OperationLog> operationLogs = employeeService.updateInsuranceScheme(updateInsuranceSchemeBO);
        return OperationResult.ok(operationLogs);
    }


    @PostMapping("/againOnboarding")
    @ApiOperation("再入职")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.REINSTATEMENT)
    public Result againOnboarding(@RequestBody AddEmployeeFieldManageBO employeeBO) {
        OperationLog operationLog = employeeService.againOnboarding(employeeBO);
        return OperationResult.ok(operationLog);
    }

    /**
     * @author wyq
     * 获取导入模板
     */
    @PostMapping("/downloadExcel")
    @ApiOperation("获取导入模板")
    public void downloadExcel(HttpServletResponse response) {
        List<HrmEmployeeField> hrmEmployeeFields = employeeService.downloadExcelFiled();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("员工导入表");
        sheet.setDefaultRowHeight((short) 400);
        CellStyle textStyle = wb.createCellStyle();
        HSSFDataFormat format = wb.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        for (int i = 0; i < hrmEmployeeFields.size(); i++) {
            HrmEmployeeField hrmEmployeeField = hrmEmployeeFields.get(i);
            if (Objects.equals(hrmEmployeeField.getType(), FieldTypeEnum.DATE.getValue())) {
                CellStyle dateStyle = wb.createCellStyle();
                HSSFDataFormat dateFormat = wb.createDataFormat();
                dateStyle.setDataFormat(dateFormat.getFormat("yyyy\"年\"m\"月\"d\"日\""));
                sheet.setDefaultColumnStyle(i, dateStyle);
            } else if (Objects.equals(hrmEmployeeField.getType(), FieldTypeEnum.DATETIME.getValue())) {
                CellStyle dateStyle = wb.createCellStyle();
                HSSFDataFormat dateFormat = wb.createDataFormat();
                dateStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                sheet.setDefaultColumnStyle(i, dateStyle);
            } else {
                sheet.setDefaultColumnStyle(i, textStyle);
            }
            sheet.setColumnWidth(i, 20 * 256);
        }
        CellStyle cellStyle = wb.createCellStyle();
        HSSFRow titleRow = sheet.createRow(0);
        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);
        titleRow.createCell(0).setCellValue("员工导入模板(*)为必填项");
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleRow.getCell(0).setCellStyle(cellStyle);
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, hrmEmployeeFields.size() - 1);
        sheet.addMergedRegion(region);
        try {
            HSSFRow row = sheet.createRow(1);
            for (int i = 0; i < hrmEmployeeFields.size(); i++) {
                HrmEmployeeField hrmEmployeeField = hrmEmployeeFields.get(i);
                String options = hrmEmployeeField.getOptions();
                // 在第一行第一个单元格，插入选项
                HSSFCell cell = row.createCell(i);
                // 普通写入操作
                String cellValue;
                if (hrmEmployeeField.getIsNull() == 1) {
                    cellValue = hrmEmployeeField.getName() + "(*)";
                } else {
                    cellValue = hrmEmployeeField.getName();
                }
                if (StrUtil.isNotEmpty(hrmEmployeeField.getRemark())) {
                    cellValue += "(" + hrmEmployeeField.getRemark() + ")";
                }
                if (hrmEmployeeField.getType().equals(FieldTypeEnum.DATE.getValue())) {
                    cellValue += "-例:[2020年10月1日]";
                }
                if ("dept_id".equals(hrmEmployeeField.getFieldName())) {
                    cellValue += "-(组织编码)";
                }
                cell.setCellValue(cellValue);
                if (StrUtil.isNotEmpty(options)) {
                    List<String> setting = new ArrayList<>();
                    if (JSONUtil.isJsonArray(options)) {
                        JSONArray array = JSON.parseArray(options);
                        for (int j = 0; j < array.size(); j++) {
                            setting.add(array.getJSONObject(j).getString("name"));
                        }
                    } else {
                        setting = StrUtil.splitTrim(options, Const.SEPARATOR);
                    }
                    String fieldName = hrmEmployeeField.getFieldName();
                    HSSFSheet hidden = wb.createSheet(fieldName);
                    HSSFCell sheetCell = null;
                    for (int j = 0, length = setting.size(); j < length; j++) {
                        String name = setting.get(j);
                        HSSFRow sheetRow = hidden.createRow(j);
                        sheetCell = sheetRow.createCell(0);
                        sheetCell.setCellValue(name);
                    }
                    Name namedCell = wb.createName();
                    namedCell.setNameName(fieldName);
                    namedCell.setRefersToFormula(fieldName + "!$A$1:$A$" + setting.size());
                    CellRangeAddressList regions = new CellRangeAddressList(2, Integer.MAX_VALUE, i, i);
                    DVConstraint constraint = DVConstraint.createFormulaListConstraint(fieldName);
                    HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
                    wb.setSheetHidden(wb.getSheetIndex(hidden), true);
                    sheet.addValidationData(dataValidation);
                }
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=employee_import.xls");
            wb.write(response.getOutputStream());

        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                wb.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @author wyq
     * 员工导入
     */
    @PostMapping("/uploadExcel")
    @ApiOperation("员工导入")
    public Result<Long> uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam("repeatHandling") Integer repeatHandling) {
        UploadExcelBO uploadExcelBO = new UploadExcelBO();
        uploadExcelBO.setRepeatHandling(repeatHandling);
        Long messageId = excelService.uploadExcel(file, uploadExcelBO);
        return Result.ok(messageId);
    }

    @PostMapping("/export")
    @ApiOperation("导出员工")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.EXCEL_EXPORT, type = OperateTypeEnum.EXPORT)
    public void export(@RequestBody QueryEmployeePageListBO employeePageListBO) {
        List<Map<String, Object>> list = employeeService.export(employeePageListBO);

        List<ExcelParseUtil.ExcelDataEntity> dataList = new ArrayList<>();
        dataList.add(ExcelParseUtil.toEntity("employeeName", "姓名"));
        dataList.add(ExcelParseUtil.toEntity("jobNumber", "工号"));
        dataList.add(ExcelParseUtil.toEntity("mobile", "手机号"));
        dataList.add(ExcelParseUtil.toEntity("deptName", "部门"));
        dataList.add(ExcelParseUtil.toEntity("post", "岗位"));
        dataList.add(ExcelParseUtil.toEntity("employmentForms", "聘用形式"));
        dataList.add(ExcelParseUtil.toEntity("status", "员工状态"));
        dataList.add(ExcelParseUtil.toEntity("entryTime", "入职日期"));
        dataList.add(ExcelParseUtil.toEntity("idType", "证件类型"));
        dataList.add(ExcelParseUtil.toEntity("idNumber", "证件号码"));
        ExcelParseUtil.exportExcel(list, new ExcelParseUtil.ExcelParseService() {
            @Override
            public String getExcelName() {
                return "员工信息";
            }

            @Override
            public boolean isXlsx() {
                return true;
            }
        }, dataList);

    }

    @PostMapping("/adminAddEmployee")
    @ApiOperation("从系统用户添加员工")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.SAVE)
    public Result adminAddEmployee(@RequestBody List<AddEmployeeBO> employeeList) {
        List<OperationLog> operationLogs = employeeService.adminAddEmployee(employeeList);
        return OperationResult.ok(operationLogs);
    }

    @PostMapping("/field")
    @ApiOperation("查询新增所需字段")
    public Result<List> queryEmployeeField(@ApiParam("入职状态 1 在职 2 待入职") @RequestParam(value = "entryStatus") Integer entryStatus) {
        return Result.ok(employeeService.queryEmployeeField(entryStatus));
    }

    @PostMapping("/addEmployeeField")
    @ApiOperation("新建自定义字段员工")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.SAVE)
    public Result addEmployee(@RequestBody AddEmployeeFieldManageBO addEmployeeFieldManageBO) {
        List<OperationLog> operationLogList = employeeService.addEmployeeField(addEmployeeFieldManageBO);
        return OperationResult.ok(operationLogList);
    }
}

