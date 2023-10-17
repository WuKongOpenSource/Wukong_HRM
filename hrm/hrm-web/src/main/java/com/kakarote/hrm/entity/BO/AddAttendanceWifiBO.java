package com.kakarote.hrm.entity.BO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加WiFi")
public class AddAttendanceWifiBO{

    @ApiModelProperty(value = "wifi名称")
    private String ssid;

    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ApiModelProperty(value = "考勤组id")
    private Long attendanceGroupId;

}
