package com.kakarote.hrm.entity.BO;

import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class SetAttendanceGroupBO {
    @ApiModelProperty(value = "考勤组id")
    private Long attendanceGroupId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工作时长")
    private BigDecimal dailyTime;

    @ApiModelProperty(value = "部门范围")
    private List<Long> deptIds;

    @ApiModelProperty(value = "员工范围")
    private List<Long> employeeIds;

    @ApiModelProperty(value = "扣款规则id")
    private Long attendanceRuleId;

    @ApiModelProperty("是否启用定位打卡")
    private Integer isOpenPointCard;

    @ApiModelProperty("是否启用wifi打卡")
    private Integer isOpenWifiCard;

    @ApiModelProperty(value = "是否自动打卡（0否 1是）")
    private Integer isAutoCard;

    @ApiModelProperty(value = "考勤班组设置")
    private List<Long> shiftSetting;

    @ApiModelProperty(value = "是否法定节假日休息（0否 1是）")
    private Integer isRest;

    @ApiModelProperty(value = "特殊日期设置")
    private Object specialDateSetting;

    @ApiModelProperty(value = "是否是默认配置（0否 1是）")
    private Integer isDefaultSetting;

    @ApiModelProperty(value = "wifi列表")
    private List<HrmAttendanceWifi> wifiList;

    @ApiModelProperty(value = "定位列表")
    private List<HrmAttendancePoint> pointList;

    @Override
    public String toString() {
        return "SetAttendanceGroupBO{" +
                "attendanceGroupId=" + attendanceGroupId +
                ", name='" + name + '\'' +
                ", dailyTime=" + dailyTime +
                ", deptIds=" + deptIds +
                ", employeeIds=" + employeeIds +
                ", attendanceRuleId=" + attendanceRuleId +
                ", isOpenPointCard=" + isOpenPointCard +
                ", isOpenWifiCard=" + isOpenWifiCard +
                ", isAutoCard=" + isAutoCard +
                ", shiftSetting=" + shiftSetting +
                ", isRest=" + isRest +
                ", specialDateSetting=" + specialDateSetting +
                ", isDefaultSetting=" + isDefaultSetting +
                ", wifiList=" + wifiList +
                ", pointList=" + pointList +
                '}';
    }
}
