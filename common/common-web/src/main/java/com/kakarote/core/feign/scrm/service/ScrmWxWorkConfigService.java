package com.kakarote.core.feign.scrm.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.scrm.entity.WxWorkConfig;
import com.kakarote.core.feign.scrm.service.impl.ScrmWxWorkConfigServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2022/6/7 17:01
 */
@FeignClient(name = "scrm",contextId = "wxWorkConfig",fallback = ScrmWxWorkConfigServiceImpl.class)
public interface ScrmWxWorkConfigService {

    @PostMapping("/scrmWxWorkConfig/getById")
    public Result<WxWorkConfig> getById(@RequestParam("corpId") String corpId);
}
