package com.kakarote.core.feign.work.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.work.entity.ProjectVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "work", contextId = "taskJob")
public interface WorkService {

    /**
     * 修改任务job
     *
     * @param
     * @return
     */
    @PostMapping("/workTask/updateTaskJob")
    Result updateTaskJob();


    /**
     * 初始化work数据
     *
     * @param
     * @return
     */
    @PostMapping("/work/initWorkData")
    Result<Boolean> initWorkData();


    /**
     * 初始化work任务
     *
     * @param
     * @return
     */
    @PostMapping("/work/initWorkWorkTask")
    Result<Boolean> initWorkTask();

    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/work/getAllFieldLanguageRel")
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel();

    /**
     * 查询项目
     *
     * @param
     * @return
     */
    @PostMapping("/project/getProjectById")
    public Result<ProjectVo> getProjectById(@RequestParam("projectId") Long projectId);
    @PostMapping("/projectTask/queryByRelationId")
    public Result<List<Map<String, Object>>> queryByRelationId(@RequestParam("relationId") Long relationId);
}
