package com.kakarote.hrm.common.admin;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;

/**
 * @author zhangzhiwei
 * 上传文件方式枚举
 */

public enum UploadTypeEnum {
    /**
     * 本地上传
     */
    LOCAL(1, "local"),
    /**
     * 阿里云OSS
     */
    ALI_OSS(2, "oss"),
    /**
     * 腾讯云COS
     */
    ALI_COS(3, "cos"),
    /**
     * 七牛云QNC
     */
    ALI_QNC(4, "qnc"),
    /**
     * 亚马逊文件上传
     */
    AWS(5, "aws_s3"),

    NULL(99, "null"),
    ;

    private UploadTypeEnum(Integer config, String type) {
        this.config = config;
        this.type = type;
    }

    private Integer config;

    private String type;

    public Integer getConfig() {
        return this.config;
    }

    public String getType() {
        return this.type;
    }

    public static UploadTypeEnum parse(String type) {
        return Arrays.stream(UploadTypeEnum.values())
                .filter(e -> ObjectUtil.equal(type, e.getType()))
                .findFirst().orElse(NULL);
    }
}
