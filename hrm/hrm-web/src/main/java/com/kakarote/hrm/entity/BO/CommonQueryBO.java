package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("公共查询对象")
public class CommonQueryBO {
    @ApiModelProperty("主键id")
    private Long id;
}
