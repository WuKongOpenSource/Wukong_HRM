package com.kakarote.hrm.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.constant.AttendanceResultEnum;
import com.kakarote.hrm.entity.BO.QueryAttendEmpRecordBO;
import com.kakarote.hrm.entity.BO.QueryAttendanceEmpDetailBO;
import com.kakarote.hrm.entity.BO.QueryAttendanceEmpMonthDetailBO;
import com.kakarote.hrm.entity.BO.QueryAttendanceEmpMonthRecordBO;
import com.kakarote.hrm.entity.VO.QueryAttendEmpMonthRecordVO;
import com.kakarote.hrm.entity.VO.QueryAttendanceEmpDetailVO;
import com.kakarote.hrm.entity.VO.QueryAttendanceEmpMonthDetailVO;
import com.kakarote.hrm.entity.VO.QueryAttendanceEmpMonthRecordVO;
import com.kakarote.hrm.service.IHrmAttendanceClockService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工每月考勤记录 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-18
 */
@RestController
@RequestMapping("/hrmAttendanceEmpMonthRecord")
@Api(tags = "考勤管理-员工每月考勤记录")
@Slf4j
public class HrmAttendanceEmpMonthRecordController {
    @Autowired
    private IHrmAttendanceClockService attendanceClockService;

    @PostMapping("/queryAttendanceEmpMonthRecordPageList")
    @ApiOperation("查询员工月度汇总列表")
    public Result<BasePage<QueryAttendanceEmpMonthRecordVO>> queryAttendanceEmpMonthRecordPageList(@RequestBody QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO) {
        BasePage<QueryAttendanceEmpMonthRecordVO> page = attendanceClockService.queryAttendanceEmpMonthRecordPageList(queryAttendanceEmpMonthRecordBO);
        return Result.ok(page);
    }

    @PostMapping("/queryAttendEmpMonthRecord")
    @ApiOperation("查询单一员工月度汇总")
    public Result<QueryAttendEmpMonthRecordVO> queryAttendEmpMonthRecord(@RequestBody QueryAttendEmpRecordBO queryAttendEmpRecordBO) {
        QueryAttendEmpMonthRecordVO queryAttendEmpMonthRecord = attendanceClockService.queryAttendEmpMonthRecord(queryAttendEmpRecordBO);
        return Result.ok(queryAttendEmpMonthRecord);
    }

    @PostMapping("/queryAttendEmpMonthStatusDetail")
    @ApiOperation("查询单一员工月度状态明细（手机端）")
    public Result<QueryAttendanceEmpDetailVO> queryAttendEmpMonthStatusDetail(@RequestBody QueryAttendanceEmpDetailBO queryAttendanceEmpDetailBO) {
        QueryAttendanceEmpDetailVO queryAttendanceEmpDetailVO = attendanceClockService.queryAttendEmpMonthStatusDetail(queryAttendanceEmpDetailBO);
        return Result.ok(queryAttendanceEmpDetailVO);
    }

    @PostMapping("/queryAttendanceEmpMonthDailyDetailPageList")
    @ApiOperation("查询打卡概况列表")
    public Result<BasePage<QueryAttendanceEmpMonthDetailVO>> queryAttendanceEmpMonthDetailPageList(@RequestBody QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO) {
        BasePage<QueryAttendanceEmpMonthDetailVO> page = attendanceClockService.queryAttendanceEmpMonthDetailPageList(queryAttendanceEmpMonthRecordBO);
        return Result.ok(page);
    }

    @PostMapping("/queryAttendanceEmpMonthDailyDetail")
    @ApiOperation("查询单一打卡概况")
    public Result<QueryAttendanceEmpDetailVO> queryAttendanceEmpMonthDailyDetail(@RequestBody QueryAttendanceEmpMonthDetailBO queryAttendanceEmpMonthDetail) {
        return Result.ok(attendanceClockService.queryAttendanceEmpMonthDetail(queryAttendanceEmpMonthDetail));
    }

    @PostMapping("/excelEmpMonthRecordExport")
    @ApiOperation("导出员工月度汇总")
    public void excelEmpMonthRecordExport(@RequestBody QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO, HttpServletResponse response) {
        queryAttendanceEmpMonthRecordBO.setPageType(0);
        List<QueryAttendanceEmpMonthRecordVO> list = attendanceClockService.queryAttendanceEmpMonthRecordPageList(queryAttendanceEmpMonthRecordBO).getList();
        List<Map<String, Object>> collect = list.stream().map(empMonthRecord -> {
            Map<String, Object> map = BeanUtil.beanToMap(empMonthRecord);
            map.remove("employeeId");
            map.remove("entryTime");
            map.remove("status");
            map.remove("workCity");
            map.remove("companyAge");
            map.remove("remark");
            map.remove("attendanceGroupName");
            map.remove("isFullAttendance");
            return map;
        }).collect(Collectors.toList());
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("employeeName", "姓名");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("deptName", "部门");
            writer.addHeaderAlias("post", "岗位");
            writer.addHeaderAlias("attendDays", "应出勤天数");
            writer.addHeaderAlias("actualDays", "实际出勤天数");
            writer.addHeaderAlias("lateMinute", "迟到时长（分钟）");
            writer.addHeaderAlias("lateCount", "迟到次数");
            writer.addHeaderAlias("earlyMinute", "早退时长（分钟）");
            writer.addHeaderAlias("earlyCount", "早退次数");
            writer.addHeaderAlias("absenteeismDays", "旷工天数");
            writer.addHeaderAlias("misscardCount", "缺卡次数");
            writer.addHeaderAlias("overTimeCount", "加班次数");
            writer.addHeaderAlias("leaveDays", "请假天数");
            writer.addHeaderAlias("outDays", "外勤天数");
            writer.merge(14, "员工月度汇总");
            writer.setOnlyAlias(true);
            writer.write(collect, true);
            writer.setRowHeight(0, 30);
            writer.setRowHeight(1, 20);
            int maxIndex = 15;
            for (int i = 0; i < maxIndex; i++) {
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
            response.setHeader("Content-Disposition", "attachment;filename=attendance_emp_month_record.xls");
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("导出员工月度汇总错误：", e);
        }
    }

    @PostMapping("/excelEmpMonthDailyDetailExport")
    @ApiOperation("导出打卡概况")
    public void excelEmpMonthDailyDetailExport(@RequestBody QueryAttendanceEmpMonthRecordBO queryAttendanceEmpMonthRecordBO, HttpServletResponse response) {
        queryAttendanceEmpMonthRecordBO.setPageType(0);
        List<QueryAttendanceEmpMonthDetailVO> attendanceEmpMonthDetailVOList = attendanceClockService.queryAttendanceEmpMonthDetailPageList(queryAttendanceEmpMonthRecordBO).getList();
        List<Map<String, Object>> collect = new ArrayList<>();
        //导出的日期范围
        List<String> strDateList = new ArrayList<>();
        int num = 4;
        if (attendanceEmpMonthDetailVOList.size() > 0) {
            num = 4 + attendanceEmpMonthDetailVOList.get(0).getDateList().size();
            collect = attendanceEmpMonthDetailVOList.stream().map(detailVO -> {
                Map<String, Object> map = new HashMap<>();
                map.put("employeeName", detailVO.getEmployeeName());
                map.put("jobNumber", detailVO.getJobNumber());
                map.put("deptName", detailVO.getDeptName());
                map.put("post", detailVO.getPost());
                List<Map<String, Object>> dateList = detailVO.getDateList();
                if (dateList.size() > 0) {
                    for (Map<String, Object> date : dateList) {
                        String[] strings = (String[]) date.get("time");
                        if (ArrayUtil.isNotEmpty(strings)) {
                            StringBuilder sb = new StringBuilder();
                            for (String string : strings) {
                                if (string.startsWith("-")) {//没有打卡/休息/旷工
                                    string = string.replace("-", "");
                                    sb.append(AttendanceResultEnum.parseName(Convert.toInt(string)));
                                    sb.append("  ");
                                } else {
                                    String[] split = string.split("-");
                                    String time = split[0];
                                    String status = split[1];
                                    sb.append(time).append(" ");
                                    sb.append(AttendanceResultEnum.parseName(Convert.toInt(status)));
                                    sb.append("  ");
                                }
                            }
                            map.put(date.get("date").toString(), sb.toString());
                        } else {
                            map.put(date.get("date").toString(), "");
                        }
                        strDateList.add(date.get("date").toString());
                    }
                }
                return map;
            }).collect(Collectors.toList());
        }
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("employeeName", "姓名");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("deptName", "部门");
            writer.addHeaderAlias("post", "岗位");
            for (String date : strDateList) {
                writer.addHeaderAlias(date, date);
            }
            writer.merge(num - 1, "打卡概况");
            writer.setOnlyAlias(true);
            writer.write(collect, true);
            writer.setRowHeight(0, 20);
            writer.setRowHeight(1, 20);
            for (int i = 0; i < num; i++) {
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
            response.setHeader("Content-Disposition", "attachment;filename=EmpMonthDailyDetail.xls");
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("导出打卡概况错误：", e);
        }

    }

}

