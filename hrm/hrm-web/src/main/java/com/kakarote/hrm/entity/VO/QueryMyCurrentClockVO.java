package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("查询自己的当前打卡配置")
public class QueryMyCurrentClockVO {

    @ApiModelProperty("班次")
    private HrmAttendanceShiftVO hrmAttendanceShiftVO;

    @ApiModelProperty(value = "定位列表")
    private List<HrmAttendancePoint> pointList;

    @ApiModelProperty(value = "wifi列表")
    private List<HrmAttendanceWifi> wifiList;

    @ApiModelProperty("班次标题")
    private String attendanceShiftTitle;

    @ApiModelProperty(value = "是否自动打卡（0否 1是）")
    private Integer isAutoCard;

    @ApiModelProperty("考勤班次时间")
    private LocalDate attendanceShiftDate;

}
