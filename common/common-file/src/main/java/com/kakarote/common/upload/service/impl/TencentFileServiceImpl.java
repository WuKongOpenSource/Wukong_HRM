package com.kakarote.common.upload.service.impl;

import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * tencent cos 上传文件
 */
public class TencentFileServiceImpl implements FileService {


    private COSClient clint;

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
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, new ObjectMetadata());
        clint.putObject(putObjectRequest);
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
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, new ObjectMetadata());
        clint.putObject(putObjectRequest);
        return entity;
    }

    /**
     * 删除文件
     *
     * @param key 文件key
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
        request.setKeys(keys.stream().map(key -> new DeleteObjectsRequest.KeyVersion(formatKey(key))).collect(Collectors.toList()));
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
        // 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, entity.getPath());
        COSObject cosObject = clint.getObject(getObjectRequest);
        return cosObject.getObjectContent();
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.cos;
    }

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    @Override
    public FileService build(UploadProperties properties) {
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(properties.getAccessKeyId(), properties.getAccessKeySecret());
        // 2 设置 bucket 的区域。
        Region region = new Region(properties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        this.clint = new COSClient(cred, clientConfig);
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
