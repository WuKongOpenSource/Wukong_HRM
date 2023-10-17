package com.kakarote.common.upload.entity;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/9/18
 */
public enum UploadType {
    /**
     * 亚马逊
     */
    aws_s3(),

    /**
     * ftp
     */
    ftp(),

    /**
     * 本地
     */
    local(),

    /**
     * 阿里云OSS
     */
    oss(),

    /**
     * 七牛云
     */
    qnc(),

    /**
     * 腾讯云
     */
    cos();

    UploadType() {
    }

}
