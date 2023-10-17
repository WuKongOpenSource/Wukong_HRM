package com.kakarote.core.feign.finance.service.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.finance.service.FinanceService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class FinanceServiceImpl implements FinanceService {

    @Override
    public Result initFinanceData() {
        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel() {
        return Result.ok(new ArrayList<>());
    }
}
