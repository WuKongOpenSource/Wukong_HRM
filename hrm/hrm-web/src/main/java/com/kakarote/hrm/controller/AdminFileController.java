package com.kakarote.hrm.controller;


import com.kakarote.common.result.Result;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.hrm.common.admin.AdminDeleteByBatchIdBO;
import com.kakarote.hrm.common.admin.RenameFileBO;
import com.kakarote.hrm.service.AdminFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 附件表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/adminFile")
@Api(tags = "文件操作相关接口")
public class AdminFileController {

    @Autowired
    private AdminFileService adminFileService;


    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<UploadEntity> upload(@RequestParam("file")
                                       @ApiParam("文件") MultipartFile file,
                                       @ApiParam("batchId") String batchId,
                                       @RequestParam(value = "overwrite", required = false) boolean overwrite,
                                       @ApiParam("文件类型") String type,
                                       @RequestParam(value = "isPublic", required = false) Boolean isPublic) throws IOException {
        UploadEntity uploadEntity = adminFileService.upload(file, batchId, overwrite, type, isPublic);
        return Result.ok(uploadEntity);
    }

    @GetMapping(value = "/download/{fileId}")
    @ApiOperation(value = "下载文件接口")
    public void down(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        adminFileService.download(fileId, response);
    }

    @ApiOperation("删除文件")
    @PostMapping("/deleteById/{fileId}")
    public Result deleteById(@NotNull @PathVariable @ApiParam("文件ID") Long fileId) {
        adminFileService.deleteById(fileId);
        return Result.ok();
    }

    @ApiOperation("删除文件")
    @PostMapping("/deleteByBatchId")
    public Result deleteByBatchId(@RequestBody AdminDeleteByBatchIdBO requestBO) {
        adminFileService.deleteByBatchId(requestBO);
        return Result.ok();
    }

    @ApiOperation("修改文件名称")
    @PostMapping("/renameFile")
    public Result renameFile(@RequestBody RenameFileBO requestBO) {
        adminFileService.renameFile(requestBO);
        return Result.ok();
    }

    @ApiOperation("查询文件列表")
    @PostMapping("/fileList/{batchId}")
    public Result<List<FileEntity>> fileList(@PathVariable @ApiParam("batchId") String batchId) {
        List<FileEntity> result = adminFileService.queryFileList(batchId);
        return Result.ok(result);
    }

    @ApiOperation("查询文件")
    @PostMapping("/file/{batchId}")
    public Result<FileEntity> queryByBatchId(@NotNull @PathVariable @ApiParam("batchId") String batchId) {
        FileEntity result = adminFileService.queryByBatchId(batchId);
        return Result.ok(result);
    }
}

