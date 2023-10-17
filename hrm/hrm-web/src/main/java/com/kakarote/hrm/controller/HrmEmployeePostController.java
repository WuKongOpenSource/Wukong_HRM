package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.DeleteLeaveInformationBO;
import com.kakarote.hrm.entity.BO.UpdateInformationBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeQuitInfo;
import com.kakarote.hrm.entity.VO.PostInformationVO;
import com.kakarote.hrm.service.IHrmEmployeePostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 员工岗位 前端控制器
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/hrmEmployeePost")
@Api(tags = "员工管理-员工岗位接口")
public class HrmEmployeePostController {

    @Autowired
    private IHrmEmployeePostService employeePostService;

    @PostMapping("/postInformation/{employeeId}")
    @ApiOperation("岗位信息")
    public Result<PostInformationVO> postInformation(@PathVariable("employeeId") Long employeeId) {
        PostInformationVO postInformationVO = employeePostService.postInformation(employeeId);
        return Result.ok(postInformationVO);
    }

    @PostMapping("/updatePostInformation")
    @ApiOperation("修改岗位信息")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE)
    public Result updatePostInformation(@RequestBody UpdateInformationBO updateInformationBO) {
        OperationLog operationLog = employeePostService.updatePostInformation(updateInformationBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/addLeaveInformation")
    @ApiOperation("办理离职")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.ADD_LEAVE)
    public Result addLeaveInformation(@RequestBody HrmEmployeeQuitInfo quitInfo) {
        OperationLog operationLog = employeePostService.addOrUpdateLeaveInformation(quitInfo);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setLeaveInformation")
    @ApiOperation("修改离职信息")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.UPDATE_LEAVE)
    public Result setLeaveInformation(@RequestBody HrmEmployeeQuitInfo quitInfo) {
        OperationLog operationLog = employeePostService.addOrUpdateLeaveInformation(quitInfo);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/deleteLeaveInformation")
    @ApiOperation("取消离职")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_EMPLOYEE, behavior = BehaviorEnum.DELETE_LEAVE)
    public Result deleteLeaveInformation(@RequestBody DeleteLeaveInformationBO deleteLeaveInformationBO) {
        OperationLog operationLog = employeePostService.deleteLeaveInformation(deleteLeaveInformationBO);
        return OperationResult.ok(operationLog);
    }

}

