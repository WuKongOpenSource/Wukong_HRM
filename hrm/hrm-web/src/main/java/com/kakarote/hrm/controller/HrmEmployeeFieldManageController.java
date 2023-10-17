package com.kakarote.hrm.controller;


import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.QueryEmployFieldManageBO;
import com.kakarote.hrm.entity.VO.EmployeeFieldManageVO;
import com.kakarote.hrm.service.IHrmEmployeeFieldManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 自定义字段管理表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-04-14
 */
@RestController
@RequestMapping("/hrmEmployeeFieldManage")
@Api(tags = "员工管理-管理员工自定义字段")
public class HrmEmployeeFieldManageController {
    @Autowired
    private IHrmEmployeeFieldManageService employeeFieldManageService;

    @PostMapping("/queryEmployeeManageField")
    @ApiOperation("查询管理可设置员工字段列表")
    public Result<List<EmployeeFieldManageVO>> queryEmployeeManageField(QueryEmployFieldManageBO queryEmployFieldManageBO) {

        List<EmployeeFieldManageVO> manageFields = employeeFieldManageService.queryEmployeeManageField(queryEmployFieldManageBO);
        return Result.ok(manageFields);
    }

    @PostMapping("/setEmployeeManageField")
    @ApiOperation("修改管理可以设置员工字段")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_BUSINESS_PARAM_SETTING, behavior = BehaviorEnum.UPDATE)
    public Result setEmployeeManageField(@RequestBody List<EmployeeFieldManageVO> manageFields) {
        employeeFieldManageService.setEmployeeManageField(manageFields);
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("员工字段设置");
        if (!manageFields.isEmpty()) {
            EmployeeFieldManageVO employeeFieldManageVO = manageFields.get(0);
            if (employeeFieldManageVO.getEntryStatus() == 1) {
                operationLog.setOperationInfo("编辑在职员工管理字段");
            } else {
                operationLog.setOperationInfo("编辑待入职员工管理字段");
            }
        }
        return OperationResult.ok(operationLog);
    }

}

