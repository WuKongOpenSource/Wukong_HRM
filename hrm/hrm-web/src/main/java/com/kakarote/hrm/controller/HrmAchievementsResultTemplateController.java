package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryAchievementsResultTemplateBO;
import com.kakarote.hrm.entity.BO.ResultTemplateBO;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.VO.AchievementsResultTemplateVO;
import com.kakarote.hrm.entity.VO.ResultTemplatePageVO;
import com.kakarote.hrm.service.IHrmAchievementsResultTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 考核结果模板
 * </p>
 *
 * @author zyl
 * @since 2022-05-18
 */
@Api(tags = "绩效管理-考核结果模板")
@RestController
@RequestMapping("/hrmAchievementsResultTemplate")
public class HrmAchievementsResultTemplateController {

    @Autowired
    private IHrmAchievementsResultTemplateService resultTemplateService;

    @ApiOperation("添加考核结果模板")
    @PostMapping("/addResultTemplate")
    public Result<Long> addResultTemplate(@RequestBody ResultTemplateBO resultTemplateBO) {
        resultTemplateService.saveOrUpdate(resultTemplateBO);
        return Result.ok();
    }

    @ApiOperation("更新考核结果模板")
    @PostMapping("/updateResultTemplate")
    public Result<Long> updateResultTemplate(@RequestBody ResultTemplateBO resultTemplateBO) {
        resultTemplateService.saveOrUpdate(resultTemplateBO);
        return Result.ok();
    }

    @ApiOperation("查看考核结果模板详情")
    @PostMapping("/information/{id}")
    public Result<AchievementsResultTemplateVO> information(@PathVariable("id") Long id) {
        AchievementsResultTemplateVO resultTemplateVO = resultTemplateService.information(id);
        return Result.ok(resultTemplateVO);
    }

    @ApiOperation("查看考核结果模板列表")
    @PostMapping("/queryPageList")
    public Result<BasePage<ResultTemplatePageVO>> queryPageList(@RequestBody QueryAchievementsResultTemplateBO search) {
        search.setPageType(1);
        BasePage<ResultTemplatePageVO> resultTemplateVOList = resultTemplateService.queryPageList(search);
        return Result.ok(resultTemplateVOList);
    }

    @ApiOperation("删除模板")
    @PostMapping("/delTemplate")
    public Result queryTemplateList(@RequestBody OperationReq operationReq) {
        resultTemplateService.deleteByIdList(operationReq);
        return Result.ok();
    }

}

