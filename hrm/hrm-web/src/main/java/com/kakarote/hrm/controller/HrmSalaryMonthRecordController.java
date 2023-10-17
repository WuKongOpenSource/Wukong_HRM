package com.kakarote.hrm.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.ApiExplain;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.constant.TaxType;
import com.kakarote.hrm.entity.BO.QuerySalaryPageListBO;
import com.kakarote.hrm.entity.BO.UpdateSalaryBO;
import com.kakarote.hrm.entity.PO.HrmSalaryMonthRecord;
import com.kakarote.hrm.entity.VO.QuerySalaryPageListVO;
import com.kakarote.hrm.entity.VO.SalaryOptionHeadVO;
import com.kakarote.hrm.service.IHrmSalaryMonthRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 每月薪资记录 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmSalaryMonthRecord")
@Api(tags = "薪资管理-薪资管理")
@Slf4j
public class HrmSalaryMonthRecordController {


    @Autowired
    private IHrmSalaryMonthRecordService salaryMonthRecordService;

    private static final int NINE = 9;

    @PostMapping("/querySalaryOptionHead")
    @ApiOperation("查询薪资项表头")
    public Result<List<SalaryOptionHeadVO>> querySalaryOptionHead() {
        List<SalaryOptionHeadVO> salaryOptionHeadVOList = salaryMonthRecordService.querySalaryOptionHead();
        return Result.ok(salaryOptionHeadVOList);
    }

    @PostMapping("/queryPaySalaryNumByType/{type}")
    @ApiOperation("查询计薪人员数量")
    public Result<Integer> queryPaySalaryNumByType(@ApiParam(value = "0 未计薪 1 计薪") @PathVariable("type") Integer type) {
        List<Map<String, Object>> mapList = salaryMonthRecordService.queryPaySalaryEmployeeListByType(type, null);
        return Result.ok(mapList.size());
    }

    @PostMapping("/queryNoPaySalaryEmployee")
    @ApiOperation("查询未计薪人员列表")
    public Result<List<Map<String, Object>>> queryNoPaySalaryEmployee() {
        List<Map<String, Object>> mapList = salaryMonthRecordService.queryNoPaySalaryEmployee();
        return Result.ok(mapList);
    }

    @PostMapping(value = "/downloadAttendanceTemp")
    @ApiOperation(value = "下载考勤导入模板", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadAttendanceTemp(HttpServletResponse response) {
        List<Map<String, Object>> mapList = salaryMonthRecordService.queryPaySalaryEmployeeListByType(1, null);
        mapList.forEach(map -> {
            map.remove("employeeId");
            map.remove("deptId");
            map.put("加班工资", "");
            map.put("迟到扣款", "");
            map.put("早退扣款", "");
            map.put("旷工扣款", "");
            map.put("假期扣款", "");
            map.put("缺卡扣款", "");
            map.put("综合扣款", "");
            map.put("实际计薪天数", "");
        });
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("employeeName", "员工名称");
        writer.addHeaderAlias("post", "岗位");
        writer.addHeaderAlias("jobNumber", "工号");
        writer.addHeaderAlias("deptName", "部门");
        writer.addHeaderAlias("加班工资", "加班工资");
        writer.addHeaderAlias("迟到扣款", "迟到扣款");
        writer.addHeaderAlias("早退扣款", "早退扣款");
        writer.addHeaderAlias("旷工扣款", "旷工扣款");
        writer.addHeaderAlias("假期扣款", "假期扣款");
        writer.addHeaderAlias("缺卡扣款", "缺卡扣款");
        writer.addHeaderAlias("综合扣款", "综合扣款");
        writer.addHeaderAlias("实际计薪天数", "实际计薪天数");
        writer.merge(11, "员工考勤数据模板");
        int maxIndex = 12;
        for (int i = 0; i < maxIndex; i++) {
            writer.setColumnWidth(i, 30);
        }
        writer.write(mapList, true);
        response.setContentType("application/octet-stream;charset=UTF-8");
        //默认Excel名称
        response.setHeader("Content-Disposition", "attachment;filename=attendance_temp.xls");
        try (ServletOutputStream out = response.getOutputStream()) {
            writer.flush(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @PostMapping(value = "/downCumulativeTaxOfLastMonthTemp")
    @ApiOperation(value = "下载上月个税累计导入模板", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downCumulativeTaxOfLastMonthTemp(HttpServletResponse response) {
        List<Map<String, Object>> mapList = salaryMonthRecordService.queryPaySalaryEmployeeListByType(1, TaxType.SALARY_TAX_TYPE);
        mapList.forEach(map -> {
            map.remove("employeeId");
            map.remove("deptId");
            map.put("累计收入额（截至上月）", "");
            map.put("累计减除费用（截至上月）", "");
            map.put("累计专项扣除（截至上月）", "");
            map.put("累计已预缴税额", "");
        });
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("employeeName", "员工名称");
        writer.addHeaderAlias("post", "岗位");
        writer.addHeaderAlias("jobNumber", "工号");
        writer.addHeaderAlias("deptName", "部门");
        writer.addHeaderAlias("累计收入额（截至上月）", "累计收入额（截至上月）");
        writer.addHeaderAlias("累计减除费用（截至上月）", "累计减除费用（截至上月）");
        writer.addHeaderAlias("累计专项扣除（截至上月）", "累计专项扣除（截至上月）");
        writer.addHeaderAlias("累计已预缴税额", "累计已预缴税额");
        writer.merge(8, "上月个税累计信息数据模板");
        for (int i = 0; i < NINE; i++) {
            writer.setColumnWidth(i, 30);
        }
        writer.write(mapList, true);
        response.setContentType("application/octet-stream;charset=UTF-8");
        //默认Excel名称
        response.setHeader("Content-Disposition", "attachment;filename=cumulative_tax_of_last_month_temp.xls");
        try (ServletOutputStream out = response.getOutputStream()) {
            writer.flush(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @PostMapping(value = "/downloadAdditionalDeductionTemp")
    @ApiOperation(value = "下载个税专项附加扣除累计导入模板", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadAdditionalDeductionTemp(HttpServletResponse response) {
        List<Map<String, Object>> mapList = salaryMonthRecordService.queryPaySalaryEmployeeListByType(1, TaxType.SALARY_TAX_TYPE);
        mapList.forEach(map -> {
            map.remove("employeeId");
            map.remove("deptId");
            map.put("累计子女教育", "");
            map.put("累计住房租金", "");
            map.put("累计住房贷款利息", "");
            map.put("累计赡养老人", "");
            map.put("累计继续教育", "");
        });
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("employeeName", "员工名称");
        writer.addHeaderAlias("post", "岗位");
        writer.addHeaderAlias("jobNumber", "工号");
        writer.addHeaderAlias("deptName", "部门");
        writer.addHeaderAlias("累计子女教育", "累计子女教育");
        writer.addHeaderAlias("累计住房租金", "累计住房租金");
        writer.addHeaderAlias("累计住房贷款利息", "累计住房贷款利息");
        writer.addHeaderAlias("累计赡养老人", "累计赡养老人");
        writer.addHeaderAlias("累计继续教育", "累计继续教育");
        writer.merge(8, "个税专项附加扣除累计数据模板");
        for (int i = 0; i < NINE; i++) {
            writer.setColumnWidth(i, 30);
        }
        writer.write(mapList, true);
        response.setContentType("application/octet-stream;charset=UTF-8");
        //默认Excel名称
        response.setHeader("Content-Disposition", "attachment;filename=additional_deduction_temp.xls");
        try (ServletOutputStream out = response.getOutputStream()) {
            writer.flush(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @PostMapping("/addNextMonthSalary")
    @ApiOperation("创建下月薪资表")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_SALARY, behavior = BehaviorEnum.SAVE)
    public Result addNextMonthSalary() {
        OperationLog operationLog = salaryMonthRecordService.addNextMonthSalary();
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/queryEmployeeChangeNum")
    @ApiOperation("查询计薪列表员工异动数量")
    public Result<Map<Integer, Long>> queryEmployeeChangeNum() {
        Map<Integer, Long> map = salaryMonthRecordService.queryEmployeeChangeNum();
        return Result.ok(map);
    }

    @PostMapping("/queryLastSalaryMonthRecord")
    @ApiOperation("查询最新的薪资记录")
    public Result<HrmSalaryMonthRecord> queryLastSalaryMonthRecord() {
        HrmSalaryMonthRecord salaryMonthRecord = salaryMonthRecordService.queryLastSalaryMonthRecord();
        return Result.ok(salaryMonthRecord);
    }

    @PostMapping("/computeSalaryData")
    @ApiOperation("核算薪资数据")
    public Result computeSalaryData(@ApiParam("薪资记录id") @RequestParam("srecordId") Long srecordId,
                                    @ApiParam("是否同步社保数据") @RequestParam("isSyncInsuranceData") Boolean isSyncInsuranceData,
                                    @ApiParam("是否同步考勤数据") @RequestParam(name = "isSyncAttendanceData", defaultValue = "false") Boolean isSyncAttendanceData,
                                    @ApiParam("考勤数据") @RequestParam(name = "attendanceFile", required = false) MultipartFile attendanceFile,
                                    @ApiParam("专项附加扣除累计") @RequestParam(name = "additionalDeductionFile", required = false) MultipartFile additionalDeductionFile,
                                    @ApiParam("截至上月个税累计") @RequestParam(name = "cumulativeTaxOfLastMonthFile", required = false) MultipartFile cumulativeTaxOfLastMonthFile
    ) {
        salaryMonthRecordService.computeSalaryData(srecordId, isSyncInsuranceData, isSyncAttendanceData, attendanceFile, additionalDeductionFile, cumulativeTaxOfLastMonthFile);
        return Result.ok();
    }

    @PostMapping("/querySalaryPageList")
    @ApiOperation("查询薪资列表")
    public Result<BasePage<QuerySalaryPageListVO>> querySalaryPageList(@RequestBody QuerySalaryPageListBO querySalaryPageListBO) {
        BasePage<QuerySalaryPageListVO> page = salaryMonthRecordService.querySalaryPageList(querySalaryPageListBO);
        return Result.ok(page);
    }

    @PostMapping("/updateSalary")
    @ApiOperation("在线修改薪资")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_SALARY, behavior = BehaviorEnum.UPDATE)
    public Result updateSalary(@RequestBody List<UpdateSalaryBO> updateSalaryBOList) {
        OperationResult operationResult = salaryMonthRecordService.updateSalary(updateSalaryBOList);
        return Result.ok();
    }

    @PostMapping("/querySalaryOptionCount/{sRecordId}")
    @ApiOperation("查询薪资项合计")
    public Result<List<Map<String, Object>>> querySalaryOptionCount(@PathVariable("sRecordId") String sRecordId) {
        List<Map<String, Object>> list = salaryMonthRecordService.querySalaryOptionCount(sRecordId);
        return Result.ok(list);
    }

    @PostMapping("/deleteSalary/{sRecordId}")
    @ApiOperation("删除薪资记录")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_SALARY, behavior = BehaviorEnum.DELETE)
    public Result deleteSalary(@PathVariable("sRecordId") Long sRecordId) {
        OperationLog operationLog = salaryMonthRecordService.deleteSalary(sRecordId);
        return OperationResult.ok(operationLog);
    }


    /**
     * 对外提供feign接口,审批使用
     */
    @PostMapping("/querySalaryRecordById")
    @ApiExplain("通过id查询薪资记录")
    public Result<HrmSalaryMonthRecord> querySalaryRecordById(@RequestParam("sRecordId") Long sRecordId) {
        HrmSalaryMonthRecord salaryMonthRecord = salaryMonthRecordService.getById(sRecordId);
        return Result.ok(salaryMonthRecord);
    }

    @PostMapping("/updateCheckStatus")
    @ApiExplain("修改审核状态")
    public Result updateCheckStatus(@RequestParam("sRecordId") Long sRecordId, @RequestParam("checkStatus") Integer checkStatus) {
        salaryMonthRecordService.updateCheckStatus(sRecordId, checkStatus);
        return Result.ok();
    }
}

