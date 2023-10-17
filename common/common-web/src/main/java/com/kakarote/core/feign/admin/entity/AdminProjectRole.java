package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "项目管理用户删除参数")
@NoArgsConstructor
@AllArgsConstructor
public class AdminProjectRole implements Serializable {

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;
}
