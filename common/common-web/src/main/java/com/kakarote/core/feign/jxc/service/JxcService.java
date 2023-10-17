package com.kakarote.core.feign.jxc.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.admin.entity.SimpleUser;
import com.kakarote.core.feign.jxc.service.impl.JxcServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author wwl
 */
@FeignClient(name = "jxc", contextId = "jxc", fallback = JxcServiceImpl.class)
public interface JxcService {

    /**
     * 修改仓库Create用户name
     *
     * @param adminUser:用户对象
     */
    @PostMapping("/jxcWarehouse/updateWarehouseCreateUserName")
    void updateWarehouseCreateUserName(@RequestBody SimpleUser adminUser);


    /**
     * 批量更新es
     *
     * @param id:数据id
     * @param name:数据名
     * @param type:数据类型
     */
    @PostMapping(value = "/jxcField/batchUpdateEsData")
    void batchUpdateEsData(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("type") String type);

    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/jxcField/getAllFieldLanguageRel")
    Result<List<Map<String, Object>>> getAllFieldLanguageRel();

}
