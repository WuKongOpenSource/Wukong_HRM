package com.kakarote.hrm.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.SetTaxRuleBO;
import com.kakarote.hrm.entity.PO.HrmSalaryTaxRule;
import com.kakarote.hrm.service.IHrmSalaryTaxRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计税规则 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
@RestController
@RequestMapping("/hrmSalaryTaxRule")
@Api(tags = "薪资管理-计税规则")
public class HrmSalaryTaxRuleController {

    @Autowired
    private IHrmSalaryTaxRuleService salaryTaxRuleService;

    @PostMapping("/queryTaxRuleList")
    @ApiOperation("查询计税规则列表")
    public Result<List<HrmSalaryTaxRule>> queryTaxRuleList() {
        List<HrmSalaryTaxRule> list = salaryTaxRuleService.queryTaxRuleList();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HrmSalaryTaxRule hrmSalaryTaxRule : list) {
                Map<String, String> keyMap = new HashMap<>();
                keyMap.put("ruleName_resourceKey", "hrm.salary.tax.rule." + hrmSalaryTaxRule.getRuleName());
                hrmSalaryTaxRule.setLanguageKeyMap(keyMap);
            }
        }
        return Result.ok(list);
    }

    @PostMapping("/setTaxRule")
    @ApiOperation("修改计规则")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, behavior = BehaviorEnum.UPDATE, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_SALARY_SETTING)
    public Result setTaxRule(@RequestBody SetTaxRuleBO setTaxRuleBO) {
        OperationLog operationLog = salaryTaxRuleService.setTaxRule(setTaxRuleBO);
        return OperationResult.ok(operationLog);
    }

}

