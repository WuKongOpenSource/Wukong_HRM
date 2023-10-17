package com.kakarote.core.feign.km;

import com.kakarote.core.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * @author JiaS
 * @date 2020/11/18
 */
@FeignClient(name = "km",contextId = "knowledgeLibrary")
public interface KmService {

    /**
     * 初始化km数据
     * @param
     * @return
    */
    @PostMapping("/kmKnowledgeLibrary/initKmData")
    Result<Boolean> initKmData();


    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/kmDocument/getAllFieldLanguageRel")
    public Result<List<Map<String,Object>>> getAllFieldLanguageRel();
}
