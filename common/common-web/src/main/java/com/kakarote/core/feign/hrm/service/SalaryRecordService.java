package com.kakarote.core.feign.hrm.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.hrm.entity.HrmSalaryMonthRecord;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hrm", contextId = "salary")
public interface SalaryRecordService {

    /**
     * 通过id查询薪资记录
     *
     * @param sRecordId:s记录id
     * @return 每月薪资记录
     */
    @PostMapping("/hrmSalaryMonthRecord/querySalaryRecordById")
    @ApiOperation("通过id查询薪资记录")
    Result<HrmSalaryMonthRecord> querySalaryRecordById(@RequestParam("sRecordId") Long sRecordId);

    /**
     * 通过id查询薪资记录
     *
     * @param sRecordId:s记录id
     * @param checkStatus:检查状态
     * @return data
     */
    @PostMapping("/hrmSalaryMonthRecord/updateCheckStatus")
    @ApiOperation("通过id查询薪资记录")
    Result updateCheckStatus(@RequestParam("sRecordId") Long sRecordId, @RequestParam("checkStatus") Integer checkStatus);
}