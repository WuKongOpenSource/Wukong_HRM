package com.kakarote.core.feign.finance.service;


import com.kakarote.core.common.Result;
import com.kakarote.core.feign.finance.service.impl.FinanceServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;


@FeignClient(name = "finance", contextId = "finance", fallback = FinanceServiceImpl.class)
public interface FinanceService {

    /**
     * 初始化财务数据
     *
     * @param
     * @return
     */
    @PostMapping("/financeAccountSet/initFinanceData")
    Result initFinanceData();


    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/financeDashboard/getAllFieldLanguageRel")
    public Result<List<Map<String,Object>>> getAllFieldLanguageRel();
}
