package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.PO.HrmAttendancePoint;
import com.kakarote.hrm.entity.PO.HrmAttendanceWifi;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class HrmAttendanceGroupVO {

    @ApiModelProperty(value = "考勤组id")
    private Long attendanceGroupId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工作时长")
    private BigDecimal dailyTime;

    @ApiModelProperty(value = "部门范围")
    private List<SimpleHrmDeptVO> deptList;

    @ApiModelProperty(value = "员工范围")
    private List<SimpleHrmEmployeeVO> employeeList;

    @ApiModelProperty(value = "扣款规则id")
    private Long attendanceRuleId;

    @ApiModelProperty(value = "扣款规则名称")
    private String attendanceRuleName;

    @ApiModelProperty("是否启用定位打卡")
    private Integer isOpenPointCard;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectTime;


    @ApiModelProperty(value = "定位列表")
    private List<HrmAttendancePoint> pointList;

    @ApiModelProperty("是否启用wifi打卡")
    private Integer isOpenWifiCard;

    @ApiModelProperty(value = "wifi列表")
    private List<HrmAttendanceWifi> wifiList;

    @ApiModelProperty(value = "是否自动打卡（0否 1是）")
    private Integer isAutoCard;

    @ApiModelProperty(value = "考勤班组设置")
    private List<String> shiftSetting;

    @ApiModelProperty(value = "是否法定节假日休息（0否 1是）")
    private Integer isRest;

    @ApiModelProperty(value = "特殊日期设置")
    private Object specialDateSetting;

    @ApiModelProperty(value = "是否是默认配置（0否 1是）")
    private Integer isDefaultSetting;

    @ApiModelProperty(value = "出勤时间")
    private Map<Integer, Map<String, Object>> attendanceDate;

    @Override
    public String toString() {
        return "HrmAttendanceGroupVO{" +
                "attendanceGroupId=" + attendanceGroupId +
                ", name='" + name + '\'' +
                ", dailyTime=" + dailyTime +
                ", deptList=" + deptList +
                ", employeeList=" + employeeList +
                ", attendanceRuleId=" + attendanceRuleId +
                ", attendanceRuleName='" + attendanceRuleName + '\'' +
                ", isOpenPointCard=" + isOpenPointCard +
                ", pointList=" + pointList +
                ", isOpenWifiCard=" + isOpenWifiCard +
                ", wifiList=" + wifiList +
                ", isAutoCard=" + isAutoCard +
                ", shiftSetting=" + shiftSetting +
                ", isRest=" + isRest +
                ", specialDateSetting=" + specialDateSetting +
                ", isDefaultSetting=" + isDefaultSetting +
                ", attendanceDate=" + attendanceDate +
                '}';
    }
}
