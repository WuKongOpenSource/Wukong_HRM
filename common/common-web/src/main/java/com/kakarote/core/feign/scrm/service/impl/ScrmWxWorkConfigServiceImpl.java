package com.kakarote.core.feign.scrm.service.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.scrm.entity.WxWorkConfig;
import com.kakarote.core.feign.scrm.service.ScrmWxWorkConfigService;
import org.springframework.stereotype.Component;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2022/6/7 17:01
 */
@Component
public class ScrmWxWorkConfigServiceImpl implements ScrmWxWorkConfigService {

    @Override
    public Result<WxWorkConfig> getById(String corpId) {
        return Result.error(404,"scrm模块未启动");
    }
}
