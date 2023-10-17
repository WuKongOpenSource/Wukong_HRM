package com.kakarote.common.field.config;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kakarote.common.field.mapper.FieldDataMapper;
import com.kakarote.common.field.service.impl.FieldDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自定义字段ES自动配置类
 *
 * @author zhangzhiwei
 */
@Configuration
@EnableConfigurationProperties(ElasticClientProperties.class)
@Import({FieldDataServiceImpl.class})
@MapperScan(basePackageClasses = {FieldDataMapper.class})
@Slf4j
public class FieldAutoConfiguration {


    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticClientProperties properties) {
        if (properties.getUris() == null || properties.getUris().isEmpty()) {
            log.warn("Elasticsearch client is not configured. Please configure spring.elasticsearch.uris in your application.yml");
            return null;
        }


        HttpHost[] httpHosts = properties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(httpHosts);
        if (StrUtil.isNotEmpty(properties.getPathPrefix())) {
            builder.setPathPrefix(properties.getPathPrefix());
        }
        boolean isVerifyPassWord = StrUtil.isAllNotEmpty(properties.getUsername(), properties.getPassword());
        if (isVerifyPassWord) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
            builder.setHttpClientConfigCallback(hc -> hc.setDefaultCredentialsProvider(credentialsProvider));
        } else {
            log.warn("Elasticsearch client is not configured. Please configure spring.elasticsearch.username and spring.elasticsearch.password in your application.yml");
        }

        builder.setRequestConfigCallback(rq -> {
            rq.setConnectTimeout(properties.getConnectionTimeout());
            rq.setSocketTimeout(properties.getSocketTimeout());
            return rq;
        });
        ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper(new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, false)));
        return new ElasticsearchClient(transport);
    }
}
