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
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.BO.SetAttendanceRuleBO;
import com.kakarote.hrm.entity.VO.AttendanceRulePageListVO;
import com.kakarote.hrm.service.IHrmAttendanceRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 打卡规则表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-10
 */
@RestController
@RequestMapping("/hrmAttendanceRule")
@Api(tags = "考勤管理-考勤规则")
public class HrmAttendanceRuleController {
    @Autowired
    private IHrmAttendanceRuleService attendanceRuleService;

    @PostMapping("/queryAttendanceRulePageList")
    @ApiOperation("查询考勤规则列表")
    public Result<BasePage<AttendanceRulePageListVO>> queryAttendanceRulePageList(@RequestBody PageEntity pageEntity) {
        BasePage<AttendanceRulePageListVO> page = attendanceRuleService.queryAttendanceRulePageList(pageEntity);
        return Result.ok(page);
    }

    @PostMapping("/addAttendanceRule")
    @ApiOperation("添加考勤规则")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.SAVE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result addAttendanceRule(@RequestBody SetAttendanceRuleBO attendanceRule) {
        attendanceRuleService.setAttendanceRule(attendanceRule);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceRule.getAttendanceRuleName());
        operationLog.setOperationInfo("新建考勤扣款：" + attendanceRule.getAttendanceRuleName());
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setAttendanceRule")
    @ApiOperation("修改考勤规则")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.UPDATE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result setAttendanceRule(@RequestBody SetAttendanceRuleBO attendanceRule) {
        attendanceRuleService.setAttendanceRule(attendanceRule);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(attendanceRule.getAttendanceRuleName());
        operationLog.setOperationInfo("编辑考勤扣款：" + attendanceRule.getAttendanceRuleName());
        return OperationResult.ok(operationLog);

    }

    @PostMapping("/delete/{attendanceRuleId}")
    @ApiOperation("删除考勤规则")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.DELETE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result deleteAttendanceRule(@PathVariable("attendanceRuleId") Long attendanceRuleId) {
        OperationLog operationLog = attendanceRuleService.deleteAttendanceRule(attendanceRuleId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/verifyAttendanceRuleName")
    @ApiOperation("校验考勤规则名称")
    public Result verifyAttendanceRuleName(@RequestBody SetAttendanceRuleBO attendanceRule) {
        attendanceRuleService.verifyAttendanceRuleName(attendanceRule);
        return Result.ok();
    }
}

