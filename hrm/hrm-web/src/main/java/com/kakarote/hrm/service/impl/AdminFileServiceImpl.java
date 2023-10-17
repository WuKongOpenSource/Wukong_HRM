package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.service.FileService;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.hrm.common.admin.AdminDeleteByBatchIdBO;
import com.kakarote.hrm.common.admin.FileUploadResultVO;
import com.kakarote.hrm.common.admin.RenameFileBO;
import com.kakarote.hrm.common.admin.UploadTypeEnum;
import com.kakarote.hrm.entity.PO.AdminFile;
import com.kakarote.hrm.service.AdminFileService;
import com.kakarote.hrm.mapper.AdminFileMapper;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2023/1/9
 */
@Service
public class AdminFileServiceImpl extends BaseServiceImpl<AdminFileMapper, AdminFile> implements AdminFileService {

    @Value("${wukong.common.upload.domain}")
    private String domain;

    @Value("${wukong.common.upload.type}")
    private String uploadType;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResultVO upload(MultipartFile file,
                                     String batchId,
                                     Boolean overwrite,
                                     String type,
                                     Boolean isPublic) throws IOException {
        if (StrUtil.isEmpty(batchId)) {
            batchId = IdUtil.simpleUUID();
        }
        Long fileId = BaseUtil.getNextId();
        FileUploadResultVO uploadEntity = new FileUploadResultVO();
        uploadEntity.setFileId(fileId.toString());
        uploadEntity.setBatchId(batchId);
        uploadEntity.setUrl(getUrl(fileId));
        uploadEntity.setName(file.getOriginalFilename());
        uploadEntity.setSize(file.getSize());
        fileService.uploadFile(file.getInputStream(), uploadEntity);
        if (overwrite) {
            lambdaUpdate().eq(AdminFile::getBatchId, batchId).remove();
        }
        AdminFile fileEntity = new AdminFile();
        fileEntity.setFileId(fileId);
        fileEntity.setName(uploadEntity.getName());
        fileEntity.setSize(uploadEntity.getSize());
        fileEntity.setPath(uploadEntity.getPath());
        fileEntity.setBatchId(batchId);
        if (StrUtil.isEmpty(type)) {
            type = "file";
        }
        fileEntity.setFileType(type);
        fileEntity.setType(UploadTypeEnum.parse(uploadType).getConfig());
        fileEntity.setIsPublic(isPublic);
        fileEntity.setCreateTime(LocalDateTime.now());
        fileEntity.setCreateUserId(UserUtil.getUserId());
        save(fileEntity);
        return uploadEntity;
    }

    @Override
    public void download(Long fileId, HttpServletResponse response) {
        AdminFile fileEntity = getById(fileId);
        if (ObjectUtil.isNotNull(fileEntity)) {
            UploadEntity uploadEntity = new UploadEntity();
            uploadEntity.setPath(fileEntity.getPath());
            InputStream inputStream = fileService.downFile(uploadEntity);
            String contentType = ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileEntity.getName()), "application/octet-stream");
            ServletUtil.write(response, inputStream, contentType, fileEntity.getName());
        }
    }

    @Override
    public void deleteById(Long fileId) {
        AdminFile fileEntity = getById(fileId);
        if (ObjectUtil.isNotNull(fileEntity)) {
            fileService.deleteFile(fileEntity.getPath());
            removeById(fileId);
        }
    }

    @Override
    public void deleteByBatchId(AdminDeleteByBatchIdBO requestBO) {
        Integer type = requestBO.getType();
        // 1 附件 2 图片
        String fileType = "file";
        if (Objects.equals(2, type)) {
            fileType = "img";
        }
        List<AdminFile> fileList = lambdaQuery()
                .select(AdminFile::getFileId)
                .eq(AdminFile::getBatchId, requestBO.getBatchId())
                .eq(StrUtil.isNotEmpty(fileType), AdminFile::getFileType, fileType)
                .list();
        if (CollUtil.isEmpty(fileList)) {
            return;
        }
        List<String> pathList = fileList.stream().map(AdminFile::getPath).collect(Collectors.toList());
        fileService.deleteFileBatch(pathList);
        List<Long> fileIdList = fileList.stream().map(AdminFile::getFileId).collect(Collectors.toList());
        removeByIds(fileIdList);
    }

    @Override
    public void renameFile(RenameFileBO requestBO) {
        lambdaUpdate()
                .set(AdminFile::getName, requestBO.getName())
                .eq(AdminFile::getFileId, requestBO.getFileId())
                .update();
    }

    @Override
    public List<FileEntity> queryFileList(String batchId) {
        if (StrUtil.isEmpty(batchId)) {
            return new ArrayList<>();
        }
        List<AdminFile> fileEntities = lambdaQuery().eq(AdminFile::getBatchId, batchId).list();
        return fileEntities.stream().map(this::transVO).collect(Collectors.toList());
    }

    @Override
    public FileEntity queryByBatchId(String batchId) {
        AdminFile fileEntity = lambdaQuery().eq(AdminFile::getBatchId, batchId).one();
        if (ObjectUtil.isNull(fileEntity)) {
            return null;
        }
        return transVO(fileEntity);
    }

    private FileEntity transVO(AdminFile fileEntity) {
        FileEntity vo = new FileEntity();
        vo.setIsPublic(fileEntity.getIsPublic()==true?1:0);
        vo.setPath(fileEntity.getPath());
        vo.setUrl(this.getUrl(fileEntity.getFileId()));
        vo.setFileId(fileEntity.getFileId());
        vo.setName(fileEntity.getName());
        vo.setCreateUserName(UserCacheUtil.getUserName(fileEntity.getCreateUserId()));
        vo.setSize(fileEntity.getSize());
        vo.setBatchId(fileEntity.getBatchId());
        vo.setFileType(fileEntity.getFileType());
        return vo;
    }


    @Override
    public String getUrl(Long fieldId) {
        return StrUtil.join("/", domain, fieldId);
    }

    /**
     * 通过batchId删除
     *
     * @param batchId batchId
     */
    @Override
    public void deleteByBatchId(List<String> batchId) {
        if (batchId.size() == 0) {
            return;
        }
        List<AdminFile> fileList = lambdaQuery().select(AdminFile::getFileId).in(AdminFile::getBatchId, batchId).list();
        List<Long> fileIdList = fileList.stream().map(AdminFile::getFileId).collect(Collectors.toList());
        fileIdList.forEach(this::deleteById);
    }

    /**
     * 复制文件
     *
     * @param batchId 文件batchId
     * @return 新的batchId
     */
    @Override
    public String copyFile(String batchId) {
        String newBatchId = IdUtil.simpleUUID();
        if (StrUtil.isEmpty(batchId)) {
            return newBatchId;
        }
        List<AdminFile> fileList = lambdaQuery().eq(AdminFile::getBatchId, batchId).list();
        for (AdminFile adminFile : fileList) {
            adminFile.setFileId(BaseUtil.getNextId());
            adminFile.setBatchId(newBatchId);
        }
        saveBatch(fileList);
        return newBatchId;
    }

    /**
     * 上传临时文件，此文件会七天后删除
     *
     * @param file    文件对象
     * @param batchId batchId
     * @return entity
     */
    @Override
    public UploadEntity uploadTempFile(MultipartFile file, String batchId) {
        UploadEntity entity = new UploadEntity();
        entity.setFileId(BaseUtil.getNextId().toString());
        entity.setName(file.getOriginalFilename());
        entity.setSize(file.getSize());
        try {
            fileService.uploadTempFile(file.getInputStream(), entity);
        } catch (Exception ex) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }
        return BeanUtil.copyProperties(entity, UploadEntity.class);
    }

    /**
     * 通过文件ID查询
     *
     * @param fileId 文件ID
     * @return data
     */
    @Override
    public FileEntity queryById(Long fileId) {
        AdminFile adminFile = getById(fileId);
        return parseFileEntity(adminFile);
    }

    /**
     * 格式化文件信息
     *
     * @param adminFile 文件对象
     */
    private FileEntity parseFileEntity(AdminFile adminFile) {
        FileEntity entity = new FileEntity();
        if (adminFile == null) {
            return entity;
        }
        BeanUtil.copyProperties(adminFile, entity);
        entity.setCreateUserName(UserCacheUtil.getUserName(adminFile.getCreateUserId()));
        return entity;
    }
}
