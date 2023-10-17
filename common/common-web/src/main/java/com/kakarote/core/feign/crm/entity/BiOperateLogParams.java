package com.kakarote.core.feign.crm.entity;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/15
 */

@Data
@ApiModel("操作日志")
@EqualsAndHashCode(callSuper = true)
public class BiOperateLogParams extends PageEntity {

    @ApiModelProperty("用户ID")
    private List<Long> userList;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("所属应用")
    private Integer apply;

    @ApiModelProperty("操作对象")
    private Integer module;

    @ApiModelProperty("操作行为")
    private List<Integer> behavior;

    @ApiModelProperty("企业ID")
    private Long companyId;

    @ApiModelProperty("操作类型")
    private Integer type;


}
