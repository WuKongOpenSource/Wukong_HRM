package com.kakarote.common.upload.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * @author 上传文件配置
 */
@Data
@ConfigurationProperties(prefix = "wukong.common.upload")
public class UploadProperties {

    /**
     * 上传类型
     */
    private UploadType type;

    /**
     * 端点
     */
    private String endpoint;

    /**
     * 默认的域名
     */
    private String domain;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 区域
     */
    private String region;

    /**
     * 账号
     */
    private String accessKeyId;

    /**
     * 密码
     */
    private String accessKeySecret;

    /**
     * 额外数据
     */
    private Map<String, Object> extra;

    public Map<String, Object> getExtra() {
        if (extra == null) {
            return Collections.emptyMap();
        }
        return extra;
    }
}
