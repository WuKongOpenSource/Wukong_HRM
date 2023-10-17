package com.kakarote.core.feign.jxc.service.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.feign.admin.entity.SimpleUser;
import com.kakarote.core.feign.jxc.service.JxcService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wwl
 * @date 2022/7/13 16:00
 */
@Component
public class JxcServiceImpl implements JxcService {


    @Override
    public void updateWarehouseCreateUserName(SimpleUser adminUser) {

    }

    @Override
    public void batchUpdateEsData(String id, String name, String type) {

    }

    @Override
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel() {
        return null;
    }
}
