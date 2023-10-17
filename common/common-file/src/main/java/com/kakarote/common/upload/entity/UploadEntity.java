package com.kakarote.common.upload.entity;

import lombok.Data;

/**
 * 上传文件entity对象
 *
 * @author zhangzhiwei
 */
@Data
public class UploadEntity {

    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 上传后的相对路径
     */
    private String path;

    /**
     * 公网直接可访问的url
     */
    private String url;

    public UploadEntity() {

    }

    public UploadEntity(String path) {
        this.path = path;
    }

}
