package com.kakarote.core.feign.hrm.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.hrm.entity.HrmEmployee;
import com.kakarote.core.feign.hrm.service.impl.HrmServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "hrm", contextId = "hrm", fallback = HrmServiceImpl.class)
public interface HrmService {

    /**
     * 初始化数据
     *
     * @param companyId:公司id
     * @param userId:用户id
     * @param companyName:公司名
     * @param initData
     * @return
     */
    @PostMapping("/hrmInit/initData")
    @ApiOperation("初始化数据")
    Result<Integer> initData(@RequestParam("companyId") Long companyId, @RequestParam("userId") Long userId, @RequestParam("companyName") String companyName, @RequestParam("initData") Boolean initData);

    /**
     * 通过员工ids查询
     *
     * @param employeeIds:员工id
     * @return
     */
    @PostMapping("/hrmEmployee/queryEmployeeListByIds")
    @ApiOperation("通过员工ids查询")
    Result<Set<HrmEmployee>> queryEmployeeListByIds(@RequestBody List<Long> employeeIds);

    /**
     * 员工改动记录
     *
     * @param companyId:公司id
     * @return
     */
    @PostMapping("/hrmJob/employeeChangeRecords")
    public void employeeChangeRecords(@RequestParam("companyId") Long companyId);

    /**
     * 查询登陆用户是否在人资员工中
     *
     * @param
     * @return
     */
    @PostMapping("/hrmEmployee/queryIsInHrm")
    @ApiOperation("查询登陆用户是否在人资员工中")
    public Result<Boolean> queryIsInHrm();

    /**
     * 重置初始化数据
     *
     * @param
     * @return
     */
    @PostMapping("/hrmInit/initHrmData")
    @ApiOperation("重置初始化数据")
    Result initHrmData();

    @PostMapping("/hrmJob/changeAttendanceGroup")
    public void changeAttendanceGroup(@RequestParam("companyId") Long companyId);

    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/hrmEmployeeField/getAllFieldLanguageRel")
    public Result<List<Map<String,Object>>> getAllFieldLanguageRel();


    /**
     * 结果确认超期未处理
     *
     * @return
     */
    @PostMapping("/hrmJob/overdueUnAudit")
    public void overdueUnAudit();

    /**
     * 查询人资里面的公司ID
     * @return
     */
    @PostMapping("/hrmJob/queryCompanyIds")
    Result<List<Long>> queryCompanyIds();
}
