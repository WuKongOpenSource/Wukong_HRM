package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("检查考核人员列表-头信息")
public class HeadFieldVO {
    @ApiModelProperty("字段属性")
    private String key;
    @ApiModelProperty("字段名称")
    private String name;
    @ApiModelProperty("阶段类型")
    private Integer stageType;

    public HeadFieldVO() {
    }

    public HeadFieldVO(String key, String name, Integer stageType) {
        this.key = key;
        this.name = name;
        this.stageType = stageType;
    }
}
