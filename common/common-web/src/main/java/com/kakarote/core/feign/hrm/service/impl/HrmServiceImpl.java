package com.kakarote.core.feign.hrm.service.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.hrm.entity.HrmEmployee;
import com.kakarote.core.feign.hrm.service.HrmService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HrmServiceImpl implements HrmService {
    @Override
    public Result<Integer> initData(Long companyId, Long userId,String companyName,Boolean initData) {
        return Result.ok(1);
    }

    @Override
    public Result<Set<HrmEmployee>> queryEmployeeListByIds(List<Long> employeeIds) {
        return Result.ok(new HashSet<>());
    }

    @Override
    public void employeeChangeRecords(Long companyId) {

    }

    @Override
    public Result<Boolean> queryIsInHrm() {
        return Result.ok(false);
    }

    @Override
    public Result initHrmData() {
        return Result.ok();
    }
    @Override
    public void changeAttendanceGroup(Long companyId) {

    }

    @Override
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel() {
        return Result.ok(new ArrayList<>());
    }


    @Override
    public void overdueUnAudit() {

    }

    /**
     * 查询人资里面的公司ID
     *
     * @return
     */
    @Override
    public Result<List<Long>> queryCompanyIds() {
        return Result.ok(new ArrayList<>());
    }

}
