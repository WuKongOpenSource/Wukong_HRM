package com.kakarote.hrm.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.constant.ClockOperType;
import com.kakarote.hrm.constant.ClockStatusEnum;
import com.kakarote.hrm.constant.ClockType;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmAttendanceShift;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.service.HrmUploadExcelService;
import com.kakarote.hrm.service.IHrmAttendanceClockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 打卡记录表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-07
 */
@RestController
@RequestMapping("/hrmAttendanceClock")
@Slf4j
@Api(tags = "考勤管理-原始打卡记录")
public class HrmAttendanceClockController {

    @Autowired
    private IHrmAttendanceClockService attendanceClockService;

    @Autowired
    private HrmUploadExcelService excelService;

    @PostMapping("/add")
    @ApiOperation("添加打卡")
    public Result add(@RequestBody SetHrmAttendanceClockB0 attendanceClock) {
        attendanceClockService.setHrmAttendanceClock(attendanceClock);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("修改打卡")
    public Result update(@RequestBody SetHrmAttendanceClockB0 attendanceClock) {
        attendanceClockService.setHrmAttendanceClock(attendanceClock);
        return Result.ok();
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("删除打卡")
    public Result deleteByIds(@RequestBody List<Long> clockIds) {
        attendanceClockService.removeByIds(clockIds);
        return Result.ok();
    }

    @PostMapping("/queryAttendanceDailyDetail")
    @ApiOperation("查询员工每日打卡明细")
    public Result<QueryAttendanceDailyDetailVO> queryAttendanceDailyDetail(@RequestBody QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        QueryAttendanceDailyDetailVO attendanceDailyDetailVO = attendanceClockService.queryAttendanceDailyDetail(queryAttendanceDailyDetailBO);
        return Result.ok(attendanceDailyDetailVO);
    }

    @PostMapping("/queryTeamAttendanceDailyDetail")
    @ApiOperation("查询团队每日打卡明细")
    public Result<BasePage<QueryTeamAttendanceDetailVO>> queryTeamAttendanceDailyDetail(@RequestBody QueryTeamAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        BasePage<QueryTeamAttendanceDetailVO> page = attendanceClockService.queryTeamAttendanceDailyDetail(queryAttendanceDailyDetailBO);
        return Result.ok(page);
    }

    @PostMapping("/queryTeamDailyAttendanceTotal")
    @ApiOperation("查询团队每日打卡汇总")
    public Result<QueryTeamDailyAttendanceTotalVO> queryTeamDailyAttendanceTotal(@RequestBody QueryTeamDailyAttendanceTotalBO queryTeamDailyAttendanceTotalBO) {
        QueryTeamDailyAttendanceTotalVO queryTeamDailyAttendanceTotalVO = attendanceClockService.queryTeamDailyAttendanceTotal(queryTeamDailyAttendanceTotalBO);
        return Result.ok(queryTeamDailyAttendanceTotalVO);
    }

    @PostMapping("/queryPageList")
    @ApiOperation("查询打卡列表")
    public Result<BasePage<QueryAttendancePageVO>> queryPageList(@RequestBody QueryAttendancePageBO attendancePageBO) {
        BasePage<QueryAttendancePageVO> page = attendanceClockService.queryPageList(attendancePageBO);
        return Result.ok(page);
    }

    @PostMapping("/queryOutCardPageList")
    @ApiOperation("查询外勤打卡列表")
    public Result<BasePage<Map<String, Object>>> queryOutCardPageList(@RequestBody QueryAttendanceOutCardBO queryAttendanceOutCardBO) {
        BasePage<Map<String, Object>> page = attendanceClockService.queryOutCardPageList(queryAttendanceOutCardBO);
        return Result.ok(page);
    }

    @PostMapping("/queryMyPageList")
    @ApiOperation("查询自己打卡列表(手机端使用)")
    public Result<BasePage<QueryAttendancePageVO>> queryMyPageList(@RequestBody PageEntity pageEntity) {
        BasePage<QueryAttendancePageVO> page = attendanceClockService.queryMyPageList(pageEntity);
        return Result.ok(page);
    }

    @PostMapping("/downloadExcel")
    @ApiOperation("获取导入模板")
    public void downloadExcel(HttpServletResponse response) {
        attendanceClockService.downloadExcel(response);
    }

    @PostMapping("/queryHrmAttendanceShift")
    @ApiOperation("查询员工当前班次")
    public Result<HrmAttendanceShift> queryHrmAttendanceShift(@RequestBody QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        HrmAttendanceShift hrmAttendanceShift = attendanceClockService.getHrmAttendanceShift(queryAttendanceDailyDetailBO);
        return Result.ok(hrmAttendanceShift);
    }

    @PostMapping("/queryCurrentAttendShiftTime")
    @ApiOperation("查询员工当天打卡的时间范围")
    public Result<AttendShiftTimeVO> queryCurrentAttendShiftTime(@RequestBody QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        AttendShiftTimeVO attendShiftTimeVO = attendanceClockService.queryCurrentAttendShiftTime(queryAttendanceDailyDetailBO);
        return Result.ok(attendShiftTimeVO);
    }

    @PostMapping("/queryMyCurrentClock")
    @ApiOperation("查询自己的当前打卡配置(手机端使用)")
    public Result<QueryMyCurrentClockVO> queryMyCurrentClock(@RequestBody QueryAttendanceDailyDetailBO queryAttendanceDailyDetailBO) {
        return Result.ok(attendanceClockService.queryMyCurrentClock(queryAttendanceDailyDetailBO));
    }

    @PostMapping("/queryMyCurrentLastClock/{employeeId}")
    @ApiOperation("查询自己需要打卡类型和阶段(手机端使用)")
    public Result<QueryMyCurrentLastClockVO> queryMyCurrentLastClock(@PathVariable("employeeId") Long employeeId) {
        return Result.ok(attendanceClockService.queryMyCurrentLastClock(employeeId));
    }

    @PostMapping("/uploadExcel")
    @ApiOperation("考勤记录数据导入")
    public Result<Long> uploadExcel(@RequestParam("file") MultipartFile file) {
        UploadExcelBO uploadExcelBO = new UploadExcelBO();
        Long messageId = excelService.uploadAttendanceExcel(file, uploadExcelBO);
        return Result.ok(messageId);
    }

    @PostMapping("/excelExport")
    @ApiOperation("导出")
    public void excelExport(@RequestBody QueryAttendancePageBO attendancePageBO, HttpServletResponse response) {
        attendancePageBO.setPageType(0);
        List<QueryAttendancePageVO> list = attendanceClockService.queryPageList(attendancePageBO).getList();
        List<Map<String, Object>> collect = list.stream().map(clock -> {
            Map<String, Object> map = BeanUtil.beanToMap(clock);
            String clockType = ClockType.valueOfName((Integer) map.get("clockType"));
            String type = ClockOperType.parseName((Integer) map.get("type"));
            String clockStatus = ClockStatusEnum.parseName((Integer) map.get("clockStatus"));
            map.put("type", type);
            map.put("clockStatus", clockStatus);
            map.remove("clockType");
            map.put("clockType", clockType);
            map.remove("clockId");
            map.remove("clockEmployeeId");
            map.remove("lng");
            map.remove("lat");
            map.remove("clockStage");
            map.remove("remark");
            return map;
        }).collect(Collectors.toList());
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("employeeName", "姓名");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("deptName", "部门");
            writer.addHeaderAlias("post", "岗位");
            writer.addHeaderAlias("attendanceTime", "上班时间");
            writer.addHeaderAlias("clockType", "打卡类型");
            writer.addHeaderAlias("clockTime", "打卡时间");
            writer.addHeaderAlias("address", "打卡地点");
            writer.addHeaderAlias("type", "打卡来源");
            writer.addHeaderAlias("clockStatus", "打卡状态");
            writer.merge(10, "打卡明细");
            writer.setOnlyAlias(true);
            writer.write(collect, true);
            writer.setRowHeight(0, 30);
            writer.setRowHeight(1, 20);
            int maxIndex = 11;
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
            response.setHeader("Content-Disposition", "attachment;filename=attendance_clock.xls");
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("导出考勤打卡记录错误：", e);
        }
    }
}

