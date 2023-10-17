package com.kakarote.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.Result;
import com.kakarote.core.exception.CrmException;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * @author zhangzhiwei
 * feign客户端解码设置
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new FeignDecode(feignHttpMessageConverter()));
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        messageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(messageConverter);

        return () -> httpMessageConverters;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header("proxyHost", Const.DEFAULT_DOMAIN);
    }

    public static class FeignDecode extends SpringDecoder {

        FeignDecode(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }

        @Override
        public Object decode(Response response, Type type) throws FeignException, IOException, CrmException {
            Object data = super.decode(response, type);
            if (data instanceof Result) {
                if (!((Result<?>) data).hasSuccess()) {
                    throw new CrmException(((Result<?>) data).getCode(), ((Result<?>) data).getMsg());
                }
            }
            return data;
        }
    }
}
