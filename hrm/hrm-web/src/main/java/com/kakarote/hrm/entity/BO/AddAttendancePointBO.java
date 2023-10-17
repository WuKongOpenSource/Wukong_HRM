package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加地点")
public class AddAttendancePointBO {

    @ApiModelProperty(value = "地点名称")
    private String name;

    @ApiModelProperty(value = "定位名称")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "经度")
    private String lng;

    @ApiModelProperty(value = "范围（米）")
    private Integer pointRange;

}
