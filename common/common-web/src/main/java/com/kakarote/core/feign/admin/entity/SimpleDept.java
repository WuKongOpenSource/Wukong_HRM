package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzhiwei
 * 简单的用户对象
 */
@Data
@ApiModel("部门对象")
public class SimpleDept implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("父级ID 顶级部门为0")
    private  Long parentId;

}
