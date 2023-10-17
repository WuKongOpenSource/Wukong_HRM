package com.kakarote.common.upload.service.impl;

import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;

import java.io.*;
import java.util.List;

/**
 * @author zhangzhiwei
 * 本地上传文件
 */
public class LocalFileServiceImpl implements FileService {

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
        checkPath(bucketName + SLASH + key.split(SLASH)[0]);
        try {
            writeFromStream(inputStream, bucketName + SLASH + key);
        } catch (IOException e) {
            throw new RuntimeException("文件上传错误", e);
        }
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
        entity.setPath(key);
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
        String path = bucketName + SLASH + key;
        checkPath(bucketName + SLASH + "temp/" + key.split(SLASH)[1]);
        entity.setPath(key);
        try {
            writeFromStream(inputStream, path);
        } catch (IOException e) {
            throw new RuntimeException("文件上传错误", e);
        }
        if (properties.getDomain() != null) {
            entity.setUrl(properties.getDomain() + SLASH + key);
        }
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
        File file = new File(bucketName + SLASH + key);
        if (file.exists()) {
            boolean delete = file.delete();
            if (!delete) {
                throw new RuntimeException("文件删除失败");
            }
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
            deleteFile(key, properties.getBucketName());
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
        for (String key : keys) {
            deleteFile(key, bucketName);
        }
    }

    /**
     * 重命名文件
     *
     * @param entity   参数对象
     * @param fileName 文件名称
     */
    @Override
    public void renameFile(UploadEntity entity, String fileName) {
        if (!entity.getPath().contains(JOIN_STR)) {
            return;
        }
        String newName = entity.getPath().split(JOIN_STR)[0] + JOIN_STR + fileName;
        File file = new File(entity.getPath());
        if (file.exists()) {
            boolean rename = file.renameTo(new File(newName));
            if (!rename) {
                throw new RuntimeException("修改文件名称失败");
            }
        }
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
        renameFile(entity, fileName);
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
        File file;
        if (entity.getPath().startsWith(bucketName)) {
            file = new File(entity.getPath());
        } else {
            file = new File(bucketName + SLASH + entity.getPath());
        }
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件不存在", e);
        }
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.local;
    }

    /**
     * 生成service
     *
     * @param properties 参数
     * @return fileService
     */
    @Override
    public FileService build(UploadProperties properties) {
        this.properties = properties;
        checkPath(properties.getBucketName());
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
     * 将流的内容写入文件,操作会关闭文件流
     *
     * @param in 输入流
     */
    public void writeFromStream(InputStream in, String path) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            in.close();
        }
    }

    /**
     * 判断路径是否存在
     *
     * @param path 路径
     */
    private void checkPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdir = file.mkdirs();
            if (!mkdir) {
                throw new RuntimeException("文件夹创建失败");
            }
        }
    }
}
