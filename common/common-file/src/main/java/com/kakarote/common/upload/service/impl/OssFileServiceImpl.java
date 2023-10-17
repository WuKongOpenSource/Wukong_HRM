package com.kakarote.common.upload.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * oss上传文件
 */
public class OssFileServiceImpl implements FileService {

    private OSS clint;

    private UploadProperties properties;


    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @return result
     */
    @Override
    public UploadEntity uploadFile(InputStream inputStream, UploadEntity entity) {
        return uploadFile(inputStream, entity, properties.getBucketName());
    }

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @param bucketName  桶名称
     * @return result
     */
    @Override
    public UploadEntity uploadFile(InputStream inputStream, UploadEntity entity, String bucketName) {
        String key = buildFileName(entity.getFileId(), entity.getName());
        entity.setPath(key);
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
        clint.putObject(bucketName, key, inputStream);
        return entity;
    }

    /**
     * 上传临时文件，和正式上传不同的是，此文件7天后会被删除
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @return result
     */
    @Override
    public UploadEntity uploadTempFile(InputStream inputStream, UploadEntity entity) {
        return uploadTempFile(inputStream, entity, properties.getBucketName());
    }

    /**
     * 上传临时文件，和正式上传不同的是，此文件7天后会被删除
     *
     * @param inputStream 文件流
     * @param entity      参数对象
     * @param bucketName  桶名称
     * @return result
     */
    @Override
    public UploadEntity uploadTempFile(InputStream inputStream, UploadEntity entity, String bucketName) {
        String key = "temp/" + buildFileName(entity.getFileId(), entity.getName());
        entity.setPath(key);
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
        clint.putObject(bucketName, key, inputStream);
        return entity;
    }

    /**
     * 删除文件
     *
     * @param key 上传接口返回的path
     */
    @Override
    public void deleteFile(String key) {
        deleteFile(key, properties.getBucketName());
    }

    /**
     * 删除文件
     *
     * @param key        上传接口返回的path
     * @param bucketName 桶名称
     */
    @Override
    public void deleteFile(String key, String bucketName) {
        clint.deleteObject(bucketName, formatKey(key));
    }

    /**
     * 批量删除文件
     *
     * @param keys key
     * @return result
     */
    @Override
    public void deleteFileBatch(List<String> keys) {
        deleteFileBatch(keys, properties.getBucketName());
    }

    /**
     * 批量删除文件
     *
     * @param keys       key列表
     * @param bucketName 桶名称
     */
    @Override
    public void deleteFileBatch(List<String> keys, String bucketName) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
        request.setKeys(keys.stream().map(this::formatKey).collect(Collectors.toList()));
        clint.deleteObjects(request);
    }

    /**
     * 重命名文件
     *
     * @param entity   参数对象
     * @param fileName 文件名称
     */
    @Override
    public void renameFile(UploadEntity entity, String fileName) {
        renameFile(entity, fileName, properties.getBucketName());
    }

    /**
     * 重命名文件
     *
     * @param entity     参数对象
     * @param fileName   文件名称
     * @param bucketName 桶名称
     */
    @Override
    public void renameFile(UploadEntity entity, String fileName, String bucketName) {
        if (!entity.getPath().contains(JOIN_STR)) {
            return;
        }
        String newName = entity.getPath().split(JOIN_STR)[0] + JOIN_STR + fileName;
        clint.copyObject(bucketName, entity.getPath(), bucketName, newName);
        deleteFile(entity.getPath());
    }

    /**
     * 获取文件
     *
     * @param entity 参数对象
     * @return 文件流，可能为空
     */
    @Override
    public InputStream downFile(UploadEntity entity) {
        return downFile(entity, properties.getBucketName());

    }

    /**
     * 获取文件
     *
     * @param entity     参数对象
     * @param bucketName 桶名称
     * @return 文件流，可能为空
     */
    @Override
    public InputStream downFile(UploadEntity entity, String bucketName) {
        OSSObject object = clint.getObject(bucketName, entity.getPath());
        return object.getObjectContent();
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.oss;
    }

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    @Override
    public FileService build(UploadProperties properties) {
        this.clint = new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        this.properties = properties;
        return this;
    }

    /**
     * 获取配置信息
     *
     * @return properties
     */
    @Override
    public UploadProperties getProperties() {
        return properties;
    }

    /**
     * 格式化key,去除域名信息
     *
     * @param oldKey 原来的key
     * @return key
     */
    private String formatKey(String oldKey) {
        if (oldKey.startsWith(properties.getDomain())) {
            return oldKey.replace(properties.getDomain(), "");
        }
        return oldKey;
    }
}
