package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.SetLegalHolidaysB0;
import com.kakarote.hrm.entity.PO.HrmAttendanceLegalHolidays;
import com.kakarote.hrm.service.IHrmAttendanceLegalHolidaysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 考勤法定节假日 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-23
 */
@RestController
@RequestMapping("/hrmAttendanceLegalHolidays")
@Api(tags = "考勤管理-法定节假日")
public class HrmAttendanceLegalHolidaysController {

    @Autowired
    private IHrmAttendanceLegalHolidaysService attendanceLegalHolidaysService;

    @PostMapping("/addLegalHolidays")
    @ApiOperation("添加法定节假日")
    public Result addLegalHolidays(@RequestBody SetLegalHolidaysB0 setLegalHolidaysB0) {
        attendanceLegalHolidaysService.addLegalHolidays(setLegalHolidaysB0);
        return Result.ok();
    }

    @PostMapping("/checkIsLegalHolidays/{day}")
    @ApiOperation("校验是否是法定节假日")
    public Result<HrmAttendanceLegalHolidays> checkIsLegalHolidays(@PathVariable("day") LocalDateTime day) {
        HrmAttendanceLegalHolidays attendanceLegalHolidays = attendanceLegalHolidaysService.checkIsLegalHolidays(day);
        return Result.ok(attendanceLegalHolidays);
    }

    @PostMapping("/delete/{holidayId}")
    @ApiOperation("删除法定节假日")
    public Result deleteHolidayId(@PathVariable("holidayId") Long holidayId) {
        attendanceLegalHolidaysService.removeById(holidayId);
        return Result.ok();
    }

}

