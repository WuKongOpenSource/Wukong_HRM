package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("打卡WIFI")
public class AttendanceWifiPageListVO {
    @ApiModelProperty(value = "打卡wifiid")
    private Long attendanceWifiId;

    @ApiModelProperty(value = "wifi名称")
    private String ssid;

    @ApiModelProperty(value = "mac地址")
    private String mac;
    @ApiModelProperty(value = "考勤组id")
    private Long attendanceGroupId;

}
