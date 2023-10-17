package com.kakarote.common.upload.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.entity.UploadType;
import com.kakarote.common.upload.service.FileService;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class FtpFileServiceImpl implements FileService {

    private UploadProperties properties;

    private ChannelSftp channel;

    private Session session;

    private int timeout = 60000;

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
        channel = getFtpClient();
        try {
            if (createDir(bucketName + SLASH + key.split(SLASH)[0])) {
                channel.put(inputStream, key.split(SLASH)[1]);
            } else {
                throw new RuntimeException("ftp连接失败");
            }
        } catch (Exception e) {
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
        createDir(bucketName + SLASH + "temp/" + key.split(SLASH)[1]);
        entity.setPath(key);
        uploadFile(inputStream, entity, bucketName);
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
        channel = getFtpClient();
        try {
            channel.cd(bucketName);
            channel.rm(key);
        } catch (SftpException e) {
            throw new RuntimeException("删除文件失败", e);
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
        try {
            channel.rename(entity.getPath(), newName);
        } catch (SftpException e) {
            throw new RuntimeException("修改文件名称失败");
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
        channel = getFtpClient();
        String path;
        if (entity.getPath().startsWith(bucketName)) {
            path = entity.getPath();
        } else {
            path = bucketName + SLASH + entity.getPath();
        }
        if (!isDirExist(path)) {
            throw new RuntimeException("文件不存在");
        }
        try {
            return channel.get(path);
        } catch (SftpException e) {
            throw new RuntimeException("获取文件失败", e);
        }
    }

    /**
     * 获取文件上传类型
     *
     * @return type
     */
    @Override
    public UploadType getType() {
        return UploadType.ftp;
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
        createDir(properties.getBucketName());
        getFtpClient();
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
     * 创建一个文件目录
     *
     * @param createpath 路径
     * @return
     */
    public boolean createDir(String createpath) {
        channel = getFtpClient();
        try {
            if (isDirExist(createpath)) {
                channel.cd(createpath);
                return true;
            }
            String[] pathArry = createpath.split("/");
            StringBuilder filePath = new StringBuilder("/");
            for (String path : pathArry) {
                if (path.isEmpty()) {
                    continue;
                }
                filePath.append(path).append("/");
                if (isDirExist(filePath.toString())) {
                    channel.cd(filePath.toString());
                } else {
                    // 建立目录
                    channel.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    channel.cd(filePath.toString());
                }
            }
            channel.cd(createpath);
        } catch (SftpException e) {
            throw new RuntimeException("创建文件夹失败", e);
        }
        return true;
    }


    /**
     * 判断目录是否存在
     *
     * @param directory 路径
     * @return
     */
    public boolean isDirExist(String directory) {
        try {
            channel.lstat(directory);
            return true;
        } catch (Exception e) {
            if (e.getMessage().equalsIgnoreCase("no such file")) {
                return false;
            }
        }
        return false;
    }

    /**
     * 初始化FTP服务器
     *
     * @return
     */
    private ChannelSftp getFtpClient() {
        if (channel != null && channel.isConnected()) {
            return channel;
        } else {
            JSch jSch = new JSch();
            try {
                String endpoint = properties.getEndpoint();
                String hostname = endpoint.split(":")[0];
                int port = Integer.parseInt(endpoint.split(":")[1]);
                session = jSch.getSession(properties.getAccessKeyId(), hostname, port);
                session.setPassword(properties.getAccessKeySecret());
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                //为Session对象设置properties
                session.setConfig(config);
                //设置超时
                session.setTimeout(timeout);
                session.connect();
                System.out.println("Session连接成功");
                // 打开SFTP通道
                channel = (ChannelSftp) session.openChannel("sftp");
                channel.connect();
                return channel;
            } catch (Exception e) {
                throw new RuntimeException("ftp连接失败" + e.getMessage());
            }
        }
    }


}
