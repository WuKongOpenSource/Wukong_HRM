package com.kakarote.core.feign.bi.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.bi.entity.BiNotificationSendingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zyy
 * @date: 2022-02-14
 */
@FeignClient(name = "bi", contextId = "bi")
public interface BiService {

    /**
     * 查看回访客户满意度关联数据
     *
     * @param map BiSearchBO类的转换
     * @return
     */
    @PostMapping("biCustomer/queryContactsByCustomerSatisfaction")
    Result<List<String>> queryContactsByCustomerSatisfaction(@RequestBody Map<String, Object> map);

    /**
     * 查看回访产品满意度关联数据
     *
     * @param map BiSearchBO类的转换
     * @return
     */
    @PostMapping("biCustomer/queryContractsByProductSatisfaction")
    Result<List<String>> queryContractsByProductSatisfaction(@RequestBody Map<String, Object> map);

    /**
     * 查看客户跟进数据
     *
     * @param
     * @return
     */
    @PostMapping("biCustomer/queryRecordCustomerList")
    public Result<List<String>> queryRecordCustomerList(@RequestBody Map<String, Object> map);

    /**
     * 查看产品成交客户列表
     *
     * @param biParams
     * @return
     */
    @PostMapping("biCustomer/queryProductCustomerList")
    public Result<List<String>> queryProductCustomerList(@RequestBody Map<String, Object> biParams);

    /**
     * 企业微信每日提醒 今日需联系(只用于sql查询)
     *
     * @return
     */
    @PostMapping("biCustomer/todayNumJob")
    public Result<List<BiNotificationSendingVO>> todayNumJob();

    /**
     * 企业微信每日提醒 分配给我的(只用于sql查询)
     *
     * @return
     */
    @PostMapping("biCustomer/followNumJob")
    public Result<List<BiNotificationSendingVO>> followNumJob();

    /**
     * 同步聚合表数据
     *
     * @return result
     */
    @PostMapping("/biTableFlow/syncTableData")
    public Result<String> syncTableData();
}
