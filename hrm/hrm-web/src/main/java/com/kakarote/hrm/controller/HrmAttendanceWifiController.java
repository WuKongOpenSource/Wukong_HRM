package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.BO.AddAttendanceWifiBO;
import com.kakarote.hrm.entity.VO.AttendanceWifiPageListVO;
import com.kakarote.hrm.service.IHrmAttendanceWifiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 打卡wifi表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@RestController
@RequestMapping("/hrmAttendanceWifi")
@Api(tags = "考勤管理-WiFi")
public class HrmAttendanceWifiController {

    @Autowired
    private IHrmAttendanceWifiService attendanceWifiService;

    @PostMapping("/queryAttendanceWifiPageList")
    @ApiOperation("查询打卡Wifi列表")
    public Result<BasePage<AttendanceWifiPageListVO>> queryAttendanceWifiPageList(@RequestBody PageEntity pageEntity) {
        BasePage<AttendanceWifiPageListVO> page = attendanceWifiService.queryAttendanceWifiPageList(pageEntity);
        return Result.ok(page);
    }

    @PostMapping("/addAttendanceWifi")
    @ApiOperation("添加打卡Wifi")
    public Result addAttendanceWifi(@RequestBody AddAttendanceWifiBO attendanceWifi) {
        return Result.ok(attendanceWifiService.addAttendanceWifi(attendanceWifi));
    }

    @PostMapping("/delete/{attendanceWifiId}")
    @ApiOperation("删除打卡Wifi")
    public Result deleteAttendanceRule(@PathVariable("attendanceWifiId") Integer attendanceWifiId) {
        attendanceWifiService.removeById(attendanceWifiId);
        return Result.ok();
    }

}

