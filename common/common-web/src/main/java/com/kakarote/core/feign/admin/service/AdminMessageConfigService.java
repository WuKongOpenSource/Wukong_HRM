package com.kakarote.core.feign.admin.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.admin.entity.AdminMessageConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 10323
 * @description: 消息配置
 * @date 2021/9/259:20
 */
@FeignClient(name = "admin", contextId = "messageConfig")
public interface AdminMessageConfigService {

    /**
     * 根据标签和动作获取系统消息通知配置
     *
     * @param label:data
     * @param action:data
     * @param companyId:公司id
     * @return
     */
    @PostMapping("/adminMessageConfig/getByLabelAndAction")
    Result<AdminMessageConfig> getByLabelAndAction(@RequestParam("label") String label, @RequestParam("action") String action, @RequestParam("companyId") Long companyId);
}
