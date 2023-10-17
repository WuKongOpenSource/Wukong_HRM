package com.kakarote.common.upload.service.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * aws s3上传文件
 */
public class AwsFileServiceImpl implements FileService {


    private AmazonS3 clint;

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
        clint.putObject(bucketName, key, inputStream, new ObjectMetadata());
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
        clint.putObject(bucketName, key, inputStream, new ObjectMetadata());
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
        request.setKeys(keys.stream().map(data -> new DeleteObjectsRequest.KeyVersion(formatKey(data))).collect(Collectors.toList()));
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
        S3Object object = clint.getObject(bucketName, entity.getPath());
        return object.getObjectContent();
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.aws_s3;
    }

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    @Override
    public FileService build(UploadProperties properties) {
        AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKeyId(), properties.getAccessKeySecret());
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        configuration.setConnectionTimeout(30000);
        configuration.setUseExpectContinue(false);
        this.clint = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion()))
                .withClientConfiguration(configuration)
                .withPathStyleAccessEnabled(true)
                .build();
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
