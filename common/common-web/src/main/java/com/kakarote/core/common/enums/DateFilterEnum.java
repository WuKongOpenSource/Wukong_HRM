package com.kakarote.core.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * 日期筛选枚举
 *
 * @author zhangzhiwei
 */
@ApiModel("日期筛选枚举")
public enum DateFilterEnum {

    /**
     * 自定义
     */
    @ApiModelProperty("自定义")
    CUSTOM("custom"),

    /**
     * 今天
     */
    @ApiModelProperty("今天")
    TODAY("today"),

    /**
     * 昨天
     */
    @ApiModelProperty("昨天")
    YESTERDAY("yesterday"),

    /**
     * 明天
     */
    @ApiModelProperty("明天")
    TOMORROW("tomorrow"),

    /**
     * 本周
     */
    @ApiModelProperty("本周")
    WEEK("week"),

    /**
     * 上周
     */
    @ApiModelProperty("上周")
    LAST_WEEK("lastWeek"),

    /**
     * 下周
     */
    @ApiModelProperty("下周")
    NEXT_WEEK("nextWeek"),

    /**
     * 本月
     */
    @ApiModelProperty("本月")
    MONTH("month"),

    /**
     * 上月
     */
    @ApiModelProperty("上月")
    LAST_MONTH("lastMonth"),

    /**
     * 下月
     */
    @ApiModelProperty("下月")
    NEXT_MONTH("nextMonth"),

    /**
     * 本季度
     */
    @ApiModelProperty("本季度")
    QUARTER("quarter"),

    /**
     * 上季度
     */
    @ApiModelProperty("上季度")
    LAST_QUARTER("lastQuarter"),

    /**
     * 下季度
     */
    @ApiModelProperty("下季度")
    NEXT_QUARTER("nextQuarter"),

    /**
     * 本年
     */
    @ApiModelProperty("本年")
    YEAR("year"),

    /**
     * 上一年
     */
    @ApiModelProperty("上一年")
    LAST_YEAR("lastYear"),

    /**
     * 下一年
     */
    @ApiModelProperty("下一年")
    NEXT_YEAR("nextYear"),

    /**
     * 上半年
     */
    FIRST_HALF_YEAR("firstHalfYear"),

    /**
     * 下半年
     */
    NEXT_HALF_YEAR("nextHalfYear"),

    /**
     * 过去七天
     */
    previous7day("previous7day"),


    /**
     * 过去30天
     */
    previous30day("previous30day"),

    /**
     * 未来7天
     */
    future7day("future7day"),


    /**
     * 未来30天
     */
    future30day("future30day"),

    ;

    DateFilterEnum(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DateFilterEnum parse(String value) {
        for (DateFilterEnum dateFilterEnum : values()) {
            if (Objects.equals(value, dateFilterEnum.getValue())) {
                return dateFilterEnum;
            }
        }
        return MONTH;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
