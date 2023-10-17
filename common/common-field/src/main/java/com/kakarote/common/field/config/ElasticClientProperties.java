package com.kakarote.common.field.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author 上传文件配置
 */
@Data
@ConfigurationProperties("spring.elasticsearch")
public class ElasticClientProperties {

    /**
     * 服务地址
     */
    private List<String> uris;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接超时时间,默认15秒
     */
    private Integer connectionTimeout = 15000;

    /**
     * socket连接超时时间,默认120秒
     */
    private Integer socketTimeout = 120000;

    /**
     * 是否启用健康检查,默认不启用
     */
    private boolean connectionCheckEnabled = false;

    /**
     * 路径前缀
     */
    private String pathPrefix;
}
