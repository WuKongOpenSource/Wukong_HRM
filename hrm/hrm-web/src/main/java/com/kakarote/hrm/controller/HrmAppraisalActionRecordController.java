package com.kakarote.hrm.controller;


import com.kakarote.core.common.Result;
import com.kakarote.hrm.entity.BO.QueryRecordListBO;
import com.kakarote.hrm.entity.VO.QueryAppraisalRecordListVO;
import com.kakarote.hrm.service.IHrmAppraisalActionRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 绩效考核审核记录 前端控制器
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
@RestController
@RequestMapping("/hrmAppraisalActionRecord")
public class HrmAppraisalActionRecordController {

    @Autowired
    private IHrmAppraisalActionRecordService appraisalActionRecordService;

    @ApiOperation("查询员工绩效流程记录")
    @RequestMapping("/queryRecordList")
    public Result queryRecordList(@RequestBody QueryRecordListBO queryRecordListBO) {
        List<QueryAppraisalRecordListVO> recordListVOS = appraisalActionRecordService.queryRecordList(queryRecordListBO);
        return Result.ok(recordListVOS);
    }
}

