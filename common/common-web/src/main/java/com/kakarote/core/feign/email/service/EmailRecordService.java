package com.kakarote.core.feign.email.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.email.entity.EmailBO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2022/7/7 10:38
 */
@FeignClient(name = "email",contextId = "email")
public interface EmailRecordService {
//    @PostMapping("/emailRecord/runTask")
//    @ApiOperation("执行定时任务")
//    public Result runTask();

    @PostMapping("/emailRecord/sendEmailForModule")
    @ApiOperation("发送邮件")
    public Result sendEmailForModule(@RequestBody EmailBO emailBO);
}
