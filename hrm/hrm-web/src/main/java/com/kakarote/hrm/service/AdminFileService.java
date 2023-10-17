package com.kakarote.hrm.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.hrm.common.admin.AdminDeleteByBatchIdBO;
import com.kakarote.hrm.common.admin.FileUploadResultVO;
import com.kakarote.hrm.common.admin.RenameFileBO;
import com.kakarote.hrm.entity.PO.AdminFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface AdminFileService extends BaseService<AdminFile> {

    /**
     * 文件上传
     *
     * @param file      文件对象
     * @param batchId   批次 ID
     * @param overwrite 重写
     * @param type      文件类型
     * @param isPublic  是否公有
     * @return
     * @throws IOException
     */
    FileUploadResultVO upload(MultipartFile file,
                              String batchId,
                              Boolean overwrite,
                              String type,
                              Boolean isPublic) throws IOException;

    /**
     * 文件下载
     *
     * @param fileId   文件 ID
     * @param response http 响应
     */
    void download(Long fileId, HttpServletResponse response);

    /**
     * 根据 fileId 删除文件
     *
     * @param fileId 文件 ID
     */
    void deleteById(Long fileId);

    /**
     * 根据 batchId 删除文件
     *
     * @param requestBO
     */
    void deleteByBatchId(AdminDeleteByBatchIdBO requestBO);

    /**
     * 文件重命名
     *
     * @param requestBO
     */
    void renameFile(RenameFileBO requestBO);

    /**
     * 查询文件列表
     *
     * @param batchId 批次 ID
     * @return
     */
    List<FileEntity> queryFileList(String batchId);

    /**
     * 根据批次 ID 查询文件
     *
     * @param batchId 批次 ID
     * @return
     */
    FileEntity queryByBatchId(String batchId);

    /**
     * 根据文件 ID获取文件 URL
     *
     * @param fieldId 文件 ID
     * @return
     */
    String getUrl(Long fieldId);

    /**
     * 通过batchId删除
     *
     * @param batchId batchId
     * @return data
     */
    public void deleteByBatchId(List<String> batchId);

    /**
     * 复制文件
     *
     * @param batchId 文件batchId
     * @return 新的batchId
     */
    String copyFile(String batchId);

    public UploadEntity uploadTempFile(MultipartFile file, String batchId);

    /**
     * 通过文件ID查询
     *
     * @param fileId 文件ID
     * @return data
     */
    public FileEntity queryById(Long fileId);

}
