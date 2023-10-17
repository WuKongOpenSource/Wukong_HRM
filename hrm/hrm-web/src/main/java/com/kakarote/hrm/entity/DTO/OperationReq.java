package com.kakarote.hrm.entity.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("通用操作对象")
public class OperationReq {
    @ApiModelProperty("操作对象id列表")
    private List<Long> ids;
}
