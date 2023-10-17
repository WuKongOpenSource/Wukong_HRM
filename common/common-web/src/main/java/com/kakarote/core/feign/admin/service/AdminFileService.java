package com.kakarote.core.feign.admin.service;

import com.kakarote.core.common.ApiExplain;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.UploadEntity;
import com.kakarote.core.feign.admin.entity.FileEntity;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * @author zhangzhiwei
 * 文件操作相关接口
 */
@FeignClient(name = "admin", contextId = "file")
public interface AdminFileService {

    /**
     * 根据batchId查询文件列表
     *
     * @param batchId batchId
     * @return data
     */
    @PostMapping(value = "/adminFile/queryFileList/{batchId}")
    public Result<List<FileEntity>> queryFileList(@PathVariable("batchId") String batchId);

    /**
     * 根据fileId查询文件
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    @PostMapping(value = "/adminFile/queryById/{fileId}")
    public Result<FileEntity> queryById(@PathVariable("fileId") Long fileId);

    /**
     * 根据文件id批量查询
     *
     * @param fileIds 文件ID列表
     * @return 文件列表
     */
    @PostMapping(value = "/adminFile/queryByIds")
    public Result<List<FileEntity>> queryByIds(@RequestBody Collection<Long> fileIds);

    /**
     * 根据batchId查询单个文件
     *
     * @param batchId batchId
     * @return data
     */
    @PostMapping(value = "/adminFile/queryOneByBatchId/{batchId}")
    public Result<FileEntity> queryOne(@PathVariable("batchId") String batchId);

    /**
     * 根据fileId删除文件
     *
     * @param fileId 文件ID
     * @return data
     */
    @PostMapping(value = "/adminFile/deleteById/{fileId}")
    public Result<String> delete(@PathVariable("fileId") Object fileId);

    /**
     * 根据batchId删除文件
     *
     * @param batchId batchId
     * @return data
     */
    @RequestMapping(value = "/adminFile/deleteByBatchIds", method = RequestMethod.POST)
    public Result<String> delete(@RequestBody List<String> batchId);

    /**
     * 根据batchId查询文件数量
     *
     * @param batchId:文件上传流
     * @return number
     */
    @PostMapping(value = "/adminFile/queryNum")
    public Result<Integer> queryNum(@RequestBody List<String> batchId);

    /**
     * 根据batchIdList查询对应文件
     *
     * @param batchIdList:文件上传流
     * @return 文件对象
     */
    @PostMapping(value = "/adminFile/queryFileList")
    public Result<List<FileEntity>> queryFileList(@RequestBody List<String> batchIdList);


    /**
     * 复制文件
     *
     * @param batchId 文件batchId
     * @return 新的batchId
     */
    @PostMapping(value = "/adminFile/copyFile")
    @ApiExplain("复制文件")
    Result<String> copyFile(@RequestParam(value = "batchId") String batchId);

    /**
     * 上传文件
     *
     * @param file 文件体
     * @param batchId batchId
     * @param type 文件类型
     * @return 上传文件entity对象
     */
    @PostMapping(value = "/adminFile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiExplain("上传文件")
    public Result<UploadEntity> upload(@RequestPart("file") MultipartFile file,
                                       @RequestParam("batchId") String batchId,
                                       @RequestParam("type") String type);

    /**
     * 上传临时文件,文件会在7天后删除
     *
     * @param file 文件体
     * @param batchId batchId
     * @return 上传文件entity对象
     */
    @PostMapping(value = "/adminFile/uploadTempFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiExplain("上传文件")
    public Result<UploadEntity> uploadTempFile(@RequestPart("file") MultipartFile file, @RequestParam("batchId") String batchId);

    /**
     * 获取文件流信息
     *
     * @param upload 文件信息
     * @return response
     */
    @PostMapping(value = "/adminFile/getFileStreamByPath")
    @ApiOperation(value = "获取文件流")
    public Response getFileStreamByPath(@RequestBody UploadEntity upload);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     */
    @GetMapping(value = "/adminFile/down/{fileId}")
    @ApiOperation(value = "下载文件")
    public Response downloadFile(@RequestParam("fileId") Long fileId);
}
