package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FiledListVO {
    @ApiModelProperty("标签 1 个人信息 2 通讯信息 7 联系人信息 11 岗位信息")
    private Integer labelGroup;
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;
}
