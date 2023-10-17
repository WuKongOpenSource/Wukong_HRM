package com.kakarote.core.feign.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kakarote.core.entity.UploadEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件操作实体类对象
 *
 * @author zhangzhiwei
 */
@Data
@ApiModel("文件对象")
public class FileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件大小")
    private Long size;

    @ApiModelProperty("批次ID")
    private String batchId;

    @ApiModelProperty("url")
    private String url;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("是否只读")
    private Integer readOnly;

    @ApiModelProperty("是否是公开文件")
    private Integer isPublic;

    private String path;


    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    @ApiModelProperty(value = "下级列表")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FileEntity> children = new ArrayList<>();

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
        if (UploadEntity.PUBLIC_KEY.equals(isPublic)) {
            this.url = path;
        } else {
            this.url = "/adminFile/down/" + fileId;
        }
    }

    public String getUrl() {
        if (UploadEntity.PUBLIC_KEY.equals(isPublic)) {
            return path;
        } else {
            return "/adminFile/down/" + fileId;
        }
    }
}
