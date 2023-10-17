package com.kakarote.core.feign.authorization.service;

import com.kakarote.core.common.Const;
import com.kakarote.core.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authorization",contextId = "authorization")
public interface AuthorizationService {

    /**
     * 判断当前有无权限访问
     * @param authentication
     * @param url
     * @param method
     * @return
     */
    @RequestMapping(value = "/permission")
    Result hasPermission(@RequestHeader(Const.TOKEN_NAME) String authentication, @RequestParam("url") String url, @RequestParam("method") String method);

}
