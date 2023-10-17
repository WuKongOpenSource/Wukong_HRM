package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.BO.AddAttendancePointBO;
import com.kakarote.hrm.entity.VO.AttendancePointPageListVO;
import com.kakarote.hrm.service.IHrmAttendancePointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 打卡地址表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@RestController
@RequestMapping("/hrmAttendancePoint")
@Api(tags = "考勤管理-打卡地址")
public class HrmAttendancePointController {
    @Autowired
    private IHrmAttendancePointService attendancePointService;

    @PostMapping("/queryAttendancePointPageList")
    @ApiOperation("查询打卡地址列表")
    public Result<BasePage<AttendancePointPageListVO>> queryAttendancePointPageList(@RequestBody PageEntity pageEntity) {
        BasePage<AttendancePointPageListVO> page = attendancePointService.queryAttendancePointPageList(pageEntity);
        return Result.ok(page);
    }

    @PostMapping("/addAttendancePoint")
    @ApiOperation("添加打卡地址")
    public Result addAttendancePoint(@RequestBody AddAttendancePointBO attendancePoint) {
        return Result.ok(attendancePointService.addAttendancePoint(attendancePoint));
    }

    @PostMapping("/delete/{attendancePointId}")
    @ApiOperation("删除打卡地址")
    public Result deleteAttendanceRule(@PathVariable("attendancePointId") Integer attendancePointId) {
        attendancePointService.removeById(attendancePointId);
        return Result.ok();
    }

}

