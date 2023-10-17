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
import com.kakarote.hrm.entity.BO.QueryHrmAttendanceGroupBO;
import com.kakarote.hrm.entity.BO.SetAttendanceGroupBO;
import com.kakarote.hrm.entity.VO.HrmAttendanceGroupVO;
import com.kakarote.hrm.entity.VO.QueryMyAttendanceGroupVO;
import com.kakarote.hrm.service.IHrmAttendanceGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 考勤组表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
@RestController
@RequestMapping("/hrmAttendanceGroup")
@Api(tags = "考勤管理-考勤组")
public class HrmAttendanceGroupController {
    @Autowired
    private IHrmAttendanceGroupService attendanceGroupService;

    @PostMapping("/queryAttendanceGroupPageList")
    @ApiOperation("查询考勤组列表")
    public Result<BasePage<HrmAttendanceGroupVO>> queryAttendanceGroupPageList(@RequestBody QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO) {
        BasePage<HrmAttendanceGroupVO> page = attendanceGroupService.queryAttendanceGroupPageList(queryHrmAttendanceGroupBO);
        return Result.ok(page);
    }

    @PostMapping("/addAttendanceGroup")
    @ApiOperation("添加考勤组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.SAVE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result addAttendanceGroup(@RequestBody SetAttendanceGroupBO attendanceGroup) {
        attendanceGroupService.setAttendanceGroup(attendanceGroup);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceGroup.getName());
        operationLog.setOperationInfo("新建考勤组：" + attendanceGroup.getName());
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setAttendanceGroup")
    @ApiOperation("修改考勤组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.UPDATE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result setAttendanceGroup(@RequestBody SetAttendanceGroupBO attendanceGroup) {
        attendanceGroupService.setAttendanceGroup(attendanceGroup);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceGroup.getName());
        operationLog.setOperationInfo("编辑考勤组：" + attendanceGroup.getName());
        return OperationResult.ok(operationLog);

    }

    @PostMapping("/delete/{attendanceGroupId}")
    @ApiOperation("删除考勤组")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.DELETE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result deleteAttendanceGroup(@PathVariable("attendanceGroupId") Long attendanceGroupId) {
        OperationLog operationLog = attendanceGroupService.deleteAttendanceGroup(attendanceGroupId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/verifyAttendanceGroupName")
    @ApiOperation("校验考勤组名称")
    public Result verifyAttendanceGroupName(@RequestBody QueryHrmAttendanceGroupBO queryHrmAttendanceGroupBO) {
        attendanceGroupService.verifyAttendanceGroupName(queryHrmAttendanceGroupBO);
        return Result.ok();
    }

    @PostMapping("/queryMyAttendanceGroup/{employeeId}")
    @ApiOperation("查询我的考勤组")
    public Result<QueryMyAttendanceGroupVO> queryMyAttendanceGroup(@PathVariable("employeeId") Long employeeId) {
        return Result.ok(attendanceGroupService.queryMyAttendanceGroup(employeeId));
    }

}

