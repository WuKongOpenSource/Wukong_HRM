package com.kakarote.core.feign.admin.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.admin.entity.AdminRouter;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author : zjj
 * @since : 2023/2/20
 */
@FeignClient(name = "admin", contextId = "router")
public interface AdminRouterService {

    /**
     * 批量保存路由信息
     *
     * @param routers   路由信息列表
     * @return
     */
    @PostMapping("/adminRouter/saveBatch")
    Result saveBatch(@RequestBody List<AdminRouter> routers);

    @PostMapping("/adminRouter/list/{applicationId}")
    @ApiOperation("查询应用的路由信息")
    Result<List<AdminRouter>> list(@PathVariable("applicationId") Long applicationId);
}
