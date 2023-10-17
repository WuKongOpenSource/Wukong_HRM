package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("打卡地址")
public class AttendancePointPageListVO {

    @ApiModelProperty(value = "打卡地点id")
    private Long attendancePointId;

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

    @ApiModelProperty(value = "考勤组id")
    private Long attendanceGroupId;

}
