package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("查询我的考勤组")
public class QueryMyAttendanceGroupVO {

    @ApiModelProperty("姓名")
    private String realname;

    @ApiModelProperty(value = "用户头像")
    private String img;

    @ApiModelProperty(value = "考勤组名称")
    private String attendanceGroupName;

    @ApiModelProperty(value = "扣款规则")
    private List<Map<String, Object>> hrmAttendanceRule;

    @ApiModelProperty(value = "定位列表")
    private List<HrmAttendancePoint> pointList;

    @ApiModelProperty(value = "wifi列表")
    private List<HrmAttendanceWifi> wifiList;

    @ApiModelProperty(value = "出勤时间")
    private List<HrmAttendanceShiftVO> attendanceDate;

}
