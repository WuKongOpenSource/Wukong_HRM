package com.kakarote.hrm.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

/**
 * @author guomenghao
 * 考勤工具类
 */
@Component
public class AttendUtil {

    public static final String colon = ":";

    /**
     * 转换时分秒
     *
     * @param time
     * @return
     */
    public static String convertTime(String time) {
        String hourStr = "";
        if (StrUtil.isNotEmpty(time)) {
            String[] split = time.split(colon);
            int hour = Convert.toInt(split[0]);
            String minute = Convert.toStr(split[1]);
            if (hour < 0) {
                hour = hour + 24;
            } else if (hour > 24) {
                hour = hour - 24;
            }
            if (hour < 10) {
                hourStr = "0" + Math.abs(hour);
            } else {
                hourStr = Convert.toStr(hour);
            }
            return hourStr.concat(colon).concat(minute).concat(":00");
        }
        return hourStr;
    }

    /**
     * 装换时分
     *
     * @param time
     * @return
     */
    public static String convertTimeDivision(String time) {
        String hourStr = "";
        if (StrUtil.isNotEmpty(time)) {
            String[] split = time.split(colon);
            int hour = Convert.toInt(split[0]);
            String minute = Convert.toStr(split[1]);
            if (hour < 0) {
                hour = hour + 24;
            } else if (hour > 24) {
                hour = hour - 24;
            }
            if (hour < 10) {
                hourStr = "0" + Math.abs(hour);
            } else {
                hourStr = Convert.toStr(hour);
            }
            return hourStr.concat(colon).concat(minute);
        }
        return hourStr;
    }
}
