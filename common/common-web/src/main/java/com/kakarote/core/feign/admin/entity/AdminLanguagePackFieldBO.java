package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author zmj
 * 语言包服务
 */
@Data
@ToString
public class AdminLanguagePackFieldBO {

    @ApiModelProperty(value = "自定义字段映射")
    private Map<String,String> map;

}
