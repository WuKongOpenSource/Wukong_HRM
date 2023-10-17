package com.kakarote.common.upload.service;


import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author z
 * 文件上传接口
 */
public interface FileService {

    /**
     * 文件ID和文件名的连接符
     */
    String JOIN_STR = "-";

    /**
     * 文件分隔符
     */
    String SLASH = "/";

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @return result
     */
    public UploadEntity uploadFile(InputStream inputStream, UploadEntity entity);

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @param bucketName  桶名称
     * @return result
     */
    public UploadEntity uploadFile(InputStream inputStream, UploadEntity entity, String bucketName);

    /**
     * 上传临时文件，和正式上传不同的是，此文件7天后会被删除
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @return result
     */
    public UploadEntity uploadTempFile(InputStream inputStream, UploadEntity entity);

    /**
     * 上传临时文件，和正式上传不同的是，此文件7天后会被删除
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @param bucketName  桶名称
     * @return result
     */
    public UploadEntity uploadTempFile(InputStream inputStream, UploadEntity entity, String bucketName);

    /**
     * 删除文件
     *
     * @param key 上传接口返回的path
     */
    public void deleteFile(String key);

    /**
     * 删除文件
     *
     * @param key        上传接口返回的path
     * @param bucketName 桶名称
     */
    public void deleteFile(String key, String bucketName);

    /**
     * 批量删除文件
     *
     * @param keys key列表
     */
    public void deleteFileBatch(List<String> keys);


    /**
     * 批量删除文件
     *
     * @param keys       key列表
     * @param bucketName 桶名称
     */
    public void deleteFileBatch(List<String> keys, String bucketName);


    /**
     * 重命名文件
     *
     * @param entity   参数对象
     * @param fileName 文件名称
     */
    public void renameFile(UploadEntity entity, String fileName);

    /**
     * 重命名文件
     *
     * @param entity   参数对象
     * @param fileName 文件名称
     * @param bucketName 桶名称
     */
    public void renameFile(UploadEntity entity, String fileName, String bucketName);

    /**
     * 获取文件
     *
     * @param entity 参数对象
     * @return 文件流，可能为空
     */
    public InputStream downFile(UploadEntity entity);

    /**
     * 获取文件
     *
     * @param entity 参数对象
     * @return 文件流，可能为空
     * @param bucketName 桶名称
     */
    public InputStream downFile(UploadEntity entity,String bucketName);
    /**
     * 获取文件上传类型
     *
     * @return type
     */
    UploadType getType();

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    FileService build(UploadProperties properties);

    /**
     * 生成默认的文件名
     * 文件名默认为 fileId-fileName
     *
     * @param fileId   文件ID
     * @param fileName 文件名
     * @return 格式后的文件名
     */
    default String buildFileName(String fileId, String fileName) {
        if (fileId == null || fileName == null) {
            throw new IllegalArgumentException("fileId or fileName is null");
        }
        return getDateStr() + SLASH + fileId + JOIN_STR + fileName;
    }

    /**
     * 获取日期类型的字符串，上传文件默认按照日期存放
     *
     * @return data
     */
    default String getDateStr() {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
    }

    /**
     * 获取配置信息
     *
     * @return properties
     */
    UploadProperties getProperties();
}
