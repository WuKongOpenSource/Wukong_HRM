package com.kakarote.hrm.controller;


import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryLeaveRecordPageListBO;
import com.kakarote.hrm.entity.BO.SetEmployeeLeaveRecordB0;
import com.kakarote.hrm.service.IHrmEmployeeLeaveRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工请假记录 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */

@RestController
@RequestMapping("/hrmEmployeeLeaveRecord")
@Slf4j
@Api(tags = "考勤管理-请假记录")
public class HrmEmployeeLeaveRecordController {
    @Autowired
    private IHrmEmployeeLeaveRecordService employeeLeaveRecordService;

    @PostMapping("/queryLeaveRecordPageList")
    @ApiOperation("分页查询请假记录列表")
    public Result<BasePage<Map<String, Object>>> queryPageList(@RequestBody QueryLeaveRecordPageListBO leaveRecordPageListBO) {
        BasePage<Map<String, Object>> map = employeeLeaveRecordService.queryLeaveRecordPageList(leaveRecordPageListBO);
        return Result.ok(map);
    }

    @PostMapping("/addEmployeeLeaveRecord")
    @ApiOperation("添加请假记录")
    public Result addEmployeeLeaveRecord(@RequestBody SetEmployeeLeaveRecordB0 employeeLeaveRecord) {
        employeeLeaveRecordService.addEmployeeLeaveRecord(employeeLeaveRecord);
        return Result.ok();
    }

    @PostMapping("/queryLeaveTypeList")
    @ApiOperation("查询请假类型")
    public Result<List<String>> queryLeaveTypeList() {
        List<String> LeaveTypes = employeeLeaveRecordService.queryLeaveTypeList();
        return Result.ok(LeaveTypes);
    }

    @PostMapping("/export")
    @ApiOperation("导出请假记录")
    public void export(@RequestBody QueryLeaveRecordPageListBO leaveRecordPageListBO, HttpServletResponse response) {
        List<Map<String, Object>> list = employeeLeaveRecordService.queryLeaveRecordPageList(leaveRecordPageListBO).getList();
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("employeeName", "姓名");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("deptName", "部门");
            writer.addHeaderAlias("post", "岗位");
            writer.addHeaderAlias("leaveType", "请假类型");
            writer.addHeaderAlias("leaveStartTime", "请假开始时间");
            writer.addHeaderAlias("leaveEndTime", "请假结束时间");
            writer.addHeaderAlias("leaveDay", "请假时长");
            writer.addHeaderAlias("leaveReason", "请假理由");
            writer.addHeaderAlias("remark", "备注");
            writer.merge(9, "请假记录信息");
            writer.setOnlyAlias(true);
            writer.write(list, true);
            writer.setRowHeight(0, 30);
            writer.setRowHeight(1, 20);
            int ten = 10;
            for (int i = 0; i < ten; i++) {
                writer.setColumnWidth(i, 20);
            }
            Cell cell = writer.getCell(0, 0);
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = writer.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
            //自定义标题别名
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=employee_leave_record.xls");
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("导出请假记录错误：", e);
        }
    }
}

