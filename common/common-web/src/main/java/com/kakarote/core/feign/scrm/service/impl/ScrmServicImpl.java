package com.kakarote.core.feign.scrm.service.impl;

import cn.hutool.core.map.MapUtil;
import com.kakarote.core.common.Result;
import com.kakarote.core.feign.scrm.service.ScrmService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2022/6/15 16:56
 */
@Component
public class ScrmServicImpl implements ScrmService {
    @Override
    public Result<Integer> initData(Long companyId, Long userId, String companyName, Boolean initData) {
        return Result.error(404,"模块未启动");
    }

    @Override
    public Result<Map<String, Object>> queryByScrmId(Long scrmCustomerId) {
        return Result.ok(MapUtil.newHashMap());
    }

    @Override
    public Result unbindingWxWorkByUserId(Long userId) {
        return Result.ok(null);
    }

}
