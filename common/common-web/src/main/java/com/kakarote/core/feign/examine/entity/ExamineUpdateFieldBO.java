package com.kakarote.core.feign.examine.entity;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/5/24
 */

@Data
@ApiModel("审批字段修改字段BP")
public class ExamineUpdateFieldBO {

    /**
     * 审批模块
     */
    @ApiModelProperty(value = "审批模块")
    private Integer label;

    @ApiModelProperty(value = "batchId")
    private String batchId;

    @ApiModelProperty(value = "业务id")
    private Long typeId;

    /**
     * 修改字段信息
     */
    @ApiModelProperty(value = "修改字段信息")
    private List<JSONObject> data;


}
