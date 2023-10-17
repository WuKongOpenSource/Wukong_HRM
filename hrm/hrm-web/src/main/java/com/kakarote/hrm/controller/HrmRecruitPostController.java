package com.kakarote.hrm.controller;

import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.hrm.entity.BO.QueryRecruitPostPageListBO;
import com.kakarote.hrm.entity.BO.UpdateRecruitPostStatusBO;
import com.kakarote.hrm.entity.PO.HrmRecruitPost;
import com.kakarote.hrm.entity.VO.RecruitPostVO;
import com.kakarote.hrm.service.IHrmRecruitPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 招聘职位表 前端控制器
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/hrmRecruitPost")
@Api(tags = "招聘管理-职位管理")
public class HrmRecruitPostController {

    @Autowired
    private IHrmRecruitPostService recruitPostService;

    @ApiOperation("添加职位")
    @PostMapping("/addRecruitPost")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_POSITION, behavior = BehaviorEnum.SAVE)
    public Result addRecruitPost(@Validated @RequestBody HrmRecruitPost recruitPost) {
        OperationLog operationLog = recruitPostService.addRecruitPost(recruitPost);
        return OperationResult.ok(operationLog);
    }

    @ApiOperation("修改职位")
    @PostMapping("/setRecruitPost")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_POSITION, behavior = BehaviorEnum.UPDATE)
    public Result setRecruitPost(@Validated @RequestBody HrmRecruitPost recruitPost) {
        OperationLog operationLog = recruitPostService.setRecruitPost(recruitPost);
        return OperationResult.ok(operationLog);
    }

    /**
     * 获取职位详情
     *
     * @param postId
     */
    @ApiOperation("获取职位详情")
    @PostMapping("/queryById/{postId}")
    public Result<RecruitPostVO> queryById(@PathVariable("postId") Long postId) {
        RecruitPostVO recruitPostVO = recruitPostService.queryById(postId);
        return Result.ok(recruitPostVO);
    }

    @ApiOperation("查询职位列表")
    @PostMapping("/queryRecruitPostPageList")
    public Result<BasePage<RecruitPostVO>> queryRecruitPostPageList(@RequestBody QueryRecruitPostPageListBO queryRecruitPostPageListBO) {
        BasePage<RecruitPostVO> page = recruitPostService.queryRecruitPostPageList(queryRecruitPostPageListBO);
        return Result.ok(page);
    }

    @ApiOperation("修改职位状态")
    @PostMapping("/updateRecruitPostStatus")
    @OperateLog(apply = ApplyEnum.HRM, object = OperateObjectEnum.HRM_POSITION)
    public Result updateRecruitPostStatus(@RequestBody UpdateRecruitPostStatusBO updateRecruitPostStatusBO) {
        OperationLog operationLog = recruitPostService.updateRecruitPostStatus(updateRecruitPostStatusBO);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/queryPostStatusNum")
    @ApiOperation("查询每个职位状态的数量")
    public Result<Map<Integer, Long>> queryPostStatusNum() {
        Map<Integer, Long> statusMap = recruitPostService.queryPostStatusNum();
        return Result.ok(statusMap);
    }

    @PostMapping("/queryAllRecruitPostList")
    @ApiOperation("查询所有职位列表(表单使用)")
    public Result<List<HrmRecruitPost>> queryAllRecruitPostList() {
        List<HrmRecruitPost> postList = recruitPostService.queryAllRecruitPostList();
        return Result.ok(postList);
    }
}

