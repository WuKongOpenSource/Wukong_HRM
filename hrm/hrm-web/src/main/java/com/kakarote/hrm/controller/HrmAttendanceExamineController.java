package com.kakarote.hrm.controller;

import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.PO.HrmAttendanceExamine;
import com.kakarote.hrm.service.IHrmAttendanceExamineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * <p>
 * 考勤审批设置 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2023-08-07
 */
@RestController
@RequestMapping("/hrmAttendanceExamine")
@Api(tags = "考勤管理-审批设置")
public class HrmAttendanceExamineController {

    @Autowired
    private IHrmAttendanceExamineService hrmAttendanceExamineService;

    @PostMapping("/queryById/{id}")
    @ApiOperation("根据ID查询")
    public Result<HrmAttendanceExamine> queryById(@PathVariable("id") @ApiParam(name = "id", value = "id") Serializable id) {
        HrmAttendanceExamine entity = hrmAttendanceExamineService.queryById(id);
        return Result.ok(entity);
    }

    @PostMapping("/queryHrmAttendanceExamine")
    @ApiOperation("查询请假审批配置")
    public Result<HrmAttendanceExamine> queryHrmAttendanceExamine() {
        HrmAttendanceExamine entity = hrmAttendanceExamineService.queryHrmAttendanceExamine();
        return Result.ok(entity);
    }

    @PostMapping("/add")
    @ApiOperation("保存数据")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.UPDATE, object = OperateObjectEnum.HUMAN_ATTENDANCE_RULE_SETTING)
    public Result<String> add(@RequestBody HrmAttendanceExamine entity) {
        hrmAttendanceExamineService.addOrUpdate(entity);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("考勤审批设置");
        operationLog.setOperationInfo("编辑考勤审批设置");
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/update")
    @ApiOperation("修改数据")
    public Result<String> update(@RequestBody HrmAttendanceExamine entity) {
        hrmAttendanceExamineService.addOrUpdate(entity);
        return Result.ok();
    }
}
