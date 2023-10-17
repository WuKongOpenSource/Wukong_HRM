package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RecruitEliminateVO {
    @ApiModelProperty("淘汰原因列表")
    private List<String> recruit;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;
}
