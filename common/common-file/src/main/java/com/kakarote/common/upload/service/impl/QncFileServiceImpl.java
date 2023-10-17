package com.kakarote.common.upload.service.impl;

import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangzhiwei
 * 七牛上传文件
 */
public class QncFileServiceImpl implements FileService {

    private Auth clint;

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
        //默认配置
        UploadManager uploadManager = new UploadManager(new Configuration());
        String token = clint.uploadToken(bucketName);
        try {
            uploadManager.put(inputStream, key, token, null, null);
            entity.setPath(key);
        } catch (QiniuException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
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
        String token = clint.uploadToken(bucketName);
        //默认配置
        UploadManager uploadManager = new UploadManager(new Configuration());
        try {
            entity.setPath(key);
            uploadManager.put(inputStream, key, token, null, null);
        } catch (QiniuException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
        return entity;
    }

    /**
     * 删除文件
     *
     * @param key 要删除的key
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
        BucketManager bucketManager = new BucketManager(clint, new Configuration());
        try {
            bucketManager.delete(bucketName, key);
        } catch (QiniuException e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param keys key列表
     */
    @Override
    public void deleteFileBatch(List<String> keys) {
        for (String key : keys) {
            deleteFile(key);
        }
    }

    /**
     * 批量删除文件
     *
     * @param keys       key列表
     * @param bucketName 桶名称
     */
    @Override
    public void deleteFileBatch(List<String> keys, String bucketName) {
        deleteFileBatch(keys);
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
        BucketManager bucketManager = new BucketManager(clint, new Configuration());
        try {
            bucketManager.rename(bucketName, entity.getPath(), newName);
        } catch (QiniuException e) {
            throw new RuntimeException("文件重命名失败", e);
        }
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
        //默认使用http，如果需要https，在extra里面设置useHttps：true
        DownloadUrl downloadUrl = new DownloadUrl(properties.getEndpoint(), Objects.equals(Boolean.TRUE.toString(), properties.getExtra().get("useHttps")), entity.getPath());
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(downloadUrl.buildURL());
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity httpEntity = response.getEntity();
            return httpEntity.getContent();
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.qnc;
    }

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    @Override
    public FileService build(UploadProperties properties) {
        this.clint = Auth.create(properties.getAccessKeyId(), properties.getAccessKeySecret());
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
}
