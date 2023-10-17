package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.AddSlipTemplateBO;
import com.kakarote.hrm.entity.PO.HrmSalarySlipTemplate;
import com.kakarote.hrm.service.IHrmSalarySlipTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工资表模板 前端控制器
 * </p>
 *
 * @author hmb
 * @since 2020-11-03
 */
@RestController
@RequestMapping("/hrmSalarySlipTemplate")
@Api(tags = "工资条-工资条模板接口")
public class HrmSalarySlipTemplateController {

    @Autowired
    private IHrmSalarySlipTemplateService salarySlipTemplateService;

    @PostMapping("/querySlipTemplateList")
    @ApiOperation("查询工资条模板列表")
    public Result<List<HrmSalarySlipTemplate>> querySlipTemplateList() {
        List<HrmSalarySlipTemplate> slipTemplates = salarySlipTemplateService.list();
        for (HrmSalarySlipTemplate template : slipTemplates) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("templateName_resourceKey", "hrm.config.slip.templates." + template.getTemplateName());
            template.setLanguageKeyMap(keyMap);
        }
        return Result.ok(slipTemplates);
    }

    @PostMapping("/addSlipTemplate")
    @ApiOperation("添加工资条模板")
    public Result addSlipTemplate(@Validated @RequestBody AddSlipTemplateBO addSlipTemplateBO) {
        salarySlipTemplateService.addSlipTemplate(addSlipTemplateBO);
        return Result.ok();
    }

    @PostMapping("/deleteSlipTemplate/{templateId}")
    @ApiOperation("删除工资条模板")
    public Result deleteSlipTemplate(@PathVariable("templateId") Long templateId) {
        salarySlipTemplateService.deleteSlipTemplate(templateId);
        return Result.ok();
    }

}

