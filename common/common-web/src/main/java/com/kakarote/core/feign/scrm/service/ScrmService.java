package com.kakarote.core.feign.scrm.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.scrm.service.impl.ScrmServicImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2022/6/15 16:55
 */
@FeignClient(name = "scrm", contextId = "scrm", fallback = ScrmServicImpl.class)
public interface ScrmService {

    /**
     * 初始化数据
     *
     * @param companyId:公司id
     * @param userId:用户id
     * @param companyName:公司名
     * @param initData
     * @return
     */
    @PostMapping("/scrmInit/initData")
    @ApiOperation("初始化数据")
    Result<Integer> initData(@RequestParam("companyId") Long companyId, @RequestParam("userId") Long userId, @RequestParam("companyName") String companyName, @RequestParam("initData") Boolean initData);


    @PostMapping("/scrmCustomer/queryByScrmId")
    @ApiOperation("根据scrmCustomerId查询")
    Result<Map<String, Object>> queryByScrmId(@RequestParam("scrmCustomerId") Long scrmCustomerId);

    @PostMapping("/scrmWxWorkUser/unbindingWxWorkByUserId")
    @ApiOperation("解绑企业微信用户id")
    public Result unbindingWxWorkByUserId(@RequestParam("userId") @NotNull Long userId);

}
