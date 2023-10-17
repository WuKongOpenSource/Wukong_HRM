package com.kakarote.core.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 10323
 * @description: 用户查询BO
 * @date 2021/9/2514:08
 */
@Data
@ApiModel("用户查询BO")
public class QueryParentDeptBO {

    @ApiModelProperty("当前要查询的用户")
    private Long deptId;

    @ApiModelProperty("等级")
    private Integer level;

}
