package com.kakarote.hrm.common.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileBO {

    @ApiModelProperty(value = "附件id")
    private Long fileId;

    @ApiModelProperty(value = "附件名称")
    private String name;

    @Override
    public String toString() {
        return "RenameFileBO{" +
                "fileId=" + fileId +
                ", name='" + name + '\'' +
                '}';
    }
}
