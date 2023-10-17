package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.constant.appraisal.AppraisalQuotaType;
import com.kakarote.hrm.entity.BO.AchievementsTemplateBO;
import com.kakarote.hrm.entity.BO.QueryAchievementsTemplateBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.VO.AchievementsTemplatePageVO;
import com.kakarote.hrm.entity.VO.AchievementsTemplateVO;
import com.kakarote.hrm.entity.VO.QuoteTypeVO;
import com.kakarote.hrm.service.IHrmAchievementsAssessmentTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 绩效管理-考核模板 前端控制器
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Api(tags = "绩效管理-考核模板")
@RestController
@RequestMapping("/hrmAchievementsAssessmentTemplate")
public class HrmAchievementsAssessmentTemplateController {

    @Autowired
    private IHrmAchievementsAssessmentTemplateService assessmentTemplateService;

    @ApiOperation("查看考核模板列表")
    @PostMapping("/queryPageList")
    public Result<BasePage<AchievementsTemplatePageVO>> queryPageList(@RequestBody QueryAchievementsTemplateBO search) {
        search.setPageType(1);
        BasePage<AchievementsTemplatePageVO> result = assessmentTemplateService.queryPageList(search);
        return Result.ok(result);
    }

    @ApiOperation("保存模板")
    @PostMapping("/saveTemplate")
    public Result queryTemplateList(@RequestBody AchievementsTemplateBO achievementsTemplateBO) {
        assessmentTemplateService.addOrUpdate(achievementsTemplateBO);
        return Result.ok(null);
    }

    @ApiOperation("更新模板")
    @PostMapping("/updateTemplate")
    public Result updateTemplate(@RequestBody AchievementsTemplateBO achievementsTemplateBO) {
        assessmentTemplateService.addOrUpdate(achievementsTemplateBO);
        return Result.ok(null);
    }

    @ApiOperation("删除模板")
    @PostMapping("/delTemplate")
    public Result queryTemplateList(@RequestBody OperationReq operationReq) {
        assessmentTemplateService.deleteByIdList(operationReq);
        return Result.ok(null);
    }

    @ApiOperation("模板详情")
    @PostMapping("/information/{templateId}")
    public Result<AchievementsTemplateVO> queryTemplateList(@PathVariable("templateId") Long templateId) {
        AchievementsTemplateVO achievementsTemplateVO = assessmentTemplateService.information(templateId);
        return Result.ok(achievementsTemplateVO);
    }

    @ApiOperation("指标类型列表")
    @PostMapping("/quoteTypeList")
    public Result<List<QuoteTypeVO>> quoteTypeList() {
        List<QuoteTypeVO> quoteTypeVOList = new ArrayList<>();
        for (AppraisalQuotaType value : AppraisalQuotaType.values()) {
            QuoteTypeVO quoteType = new QuoteTypeVO();
            quoteType.setQuoteType(value.getValue());
            quoteType.setQuoteTypeName(value.getName());
            quoteTypeVOList.add(quoteType);
        }
        return Result.ok(quoteTypeVOList);
    }

}

