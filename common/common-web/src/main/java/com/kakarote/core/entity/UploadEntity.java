package com.kakarote.core.entity;

import lombok.Data;

/**
 * 上传文件entity对象
 *
 * @author zhangzhiwei
 */
@Data
public class UploadEntity {

    /**
     * 公共文件使用的key
     */
    public static final Integer PUBLIC_KEY = 1;

    /**
     * 私有文件使用的key
     */
    public static final Integer PRIVATE_KEY = 0;

    public UploadEntity() {

    }

    public UploadEntity(String fileId, String name, Long size, String batchId, Integer isPublic) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
        this.batchId = batchId;
        this.isPublic = isPublic;
    }

    public UploadEntity(String fileId, String name, Long size, String batchId) {
        this(fileId, name, size, batchId, PRIVATE_KEY);
    }

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
     * 批次ID
     */
    private String batchId;

    /**
     * 上传类型
     */
    private Integer type;

    /**
     * 上传路径
     */
    private String path;

    /**
     * 文件路径，公共文件使用
     */
    private String url;

    /**
     * 是否公共文件
     */
    private Integer isPublic;

    public String getUrl() {
        if (PUBLIC_KEY.equals(isPublic)) {
            return path;
        } else {
            return "/adminFile/down/" + fileId;
        }
    }

}
