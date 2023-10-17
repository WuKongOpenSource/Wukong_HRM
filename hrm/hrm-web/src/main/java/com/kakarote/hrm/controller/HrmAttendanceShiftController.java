package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceShiftBO;
import com.kakarote.hrm.entity.BO.SetAttendanceShiftBO;
import com.kakarote.hrm.entity.VO.HrmAttendanceShiftVO;
import com.kakarote.hrm.service.IHrmAttendanceShiftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 班次表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-12
 */
@RestController
@RequestMapping("/hrmAttendanceShift")
@Api(tags = "考勤管理-班次接口")
public class HrmAttendanceShiftController {

    @Autowired
    private IHrmAttendanceShiftService attendanceShiftService;

    @PostMapping("/queryAttendanceShiftPageList")
    @ApiOperation("查询考勤班次列表")
    public Result<BasePage<HrmAttendanceShiftVO>> queryAttendanceShiftPageList(@RequestBody QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO) {
        BasePage<HrmAttendanceShiftVO> page = attendanceShiftService.queryAttendanceShiftPageList(queryHrmAttendanceShiftBO);
        return Result.ok(page);
    }

    @PostMapping("/addAttendanceShift")
    @ApiOperation("添加考勤班次")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.SAVE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result addAttendanceShift(@RequestBody SetAttendanceShiftBO attendanceShift) {
        attendanceShiftService.setAttendanceShift(attendanceShift);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceShift.getShiftName());
        operationLog.setOperationInfo("新建考勤班次：" + attendanceShift.getShiftName());
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setAttendanceShift")
    @ApiOperation("修改考勤班次")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.UPDATE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result setAttendanceShift(@RequestBody SetAttendanceShiftBO attendanceShift) {
        attendanceShiftService.setAttendanceShift(attendanceShift);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceShift.getShiftName());
        operationLog.setOperationInfo("编辑考勤班次：" + attendanceShift.getShiftName());
        return OperationResult.ok(operationLog);

    }

    @PostMapping("/delete/{attendanceShiftId}")
    @ApiOperation("删除考勤班次")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.DELETE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result deleteAttendanceShift(@PathVariable("attendanceShiftId") Long attendanceShiftId) {
        OperationLog operationLog = attendanceShiftService.deleteAttendanceShift(attendanceShiftId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/verifyAttendanceShiftName")
    @ApiOperation("校验考勤班次名称")
    public Result verifyAttendanceShiftName(@RequestBody QueryHrmAttendanceShiftBO queryHrmAttendanceShiftBO) {
        attendanceShiftService.verifyAttendanceShiftName(queryHrmAttendanceShiftBO);
        return Result.ok();
    }


}

