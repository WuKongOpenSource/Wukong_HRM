package com.kakarote.common.field.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务类保存对象
 *
 * @author zhangzhiwei
 */
@Data
@ToString
@ApiModel("业务类保存对象")
public class ModelEntity {

    @ApiModelProperty(value = "实体类对象")
    private Map<String, Object> entity;

    @ApiModelProperty(value = "自定义字段对象")
    private List<ModelFieldDetail> field;

    private Map<String, Object> extraMap;

    public Map<String, Object> getExtraMap() {
        if (extraMap == null) {
            extraMap = new HashMap<>(0, 1.0F);
        }
        return extraMap;
    }
}
