package com.kakarote.core.common.enums;

import com.kakarote.core.exception.CrmException;

import java.util.Arrays;

/**
 * @author zhangzhiwei
 * 无代码字段类型枚举
 */

public enum ModuleFieldEnum {
    /**
     * 单行文本
     */
    TEXT(1, "text"),
    /**
     * 多行文本
     */
    TEXTAREA(2, "textarea"),
    /**
     * 单选
     */
    SELECT(3, "select"),
    /**
     * 日期
     */
    DATE(4, "date"),
    /**
     * 数字
     */
    NUMBER(5, "number"),
    /**
     * 小数、货币
     */
    FLOATNUMBER(6, "floatnumber"),
    /**
     * 手机
     */
    MOBILE(7, "mobile"),
    /**
     * 文件
     */
    FILE(8, "file"),
    /**
     * 多选
     */
    CHECKBOX(9, "checkbox"),
    /**
     * 人员
     */
    USER(10, "user"),
    /**
     * 部门
     */
    STRUCTURE(12, "structure"),
    /**
     * 日期时间
     */
    DATETIME(13, "datetime"),
    /**
     * 邮件
     */
    EMAIL(14, "email"),
    /**
     * 网址
     */
    WEBSITE(25, "website"),
    /**
     * 单个用户
     */
    SINGLE_USER(28, "single_user"),
    /**
     * 布尔值
     */
    BOOLEAN_VALUE(41,"boolean_value"),
    /**
     * 百分数
     */
    PERCENT(42,"percent"),
    /**
     * 地址
     */
    AREA_POSITION(43,"position"),
    /**
     * 定位
     */
    CURRENT_POSITION(44,"location"),
    /**
     * 明细表格
     */
    DETAIL_TABLE(45,"detail_table"),
    /**
     * 手写签名
     */
    HANDWRITING_SIGN(46,"handwriting_sign"),
    /**
     * 日期区间
     */
    DATE_INTERVAL(48,"date_interval"),
    /**
     * 描述文字
     */
    DESC_TEXT(50,"desc_text"),

	/**
	 * 数据关联
	 */
	DATA_UNION(52, "data_union"),
	/**
	 * 数据关联多选
	 */
	DATA_UNION_MULTI(54, "data_union_multi"),

	/**
	 * 统计字段
	 */
	STATISTIC(56, "statistic"),

    /**
     * 分组字段
     */
    FIELD_GROUP(60,"field_group"),

    /**
     *标签
     */
    TAG(61,"field_tag"),

    /**
     * 关注度字段
     */
    ATTENTION(62,"field_attention"),

    /**
     * 唯一编号
     */
    SERIAL_NUMBER(63,"serial_number"),

    /**
     * 计算公式
     */
    FORMULA(64, "formula"),

    /**
     * 树字段
     */
    TREE(65, "tree"),
    ;
    ModuleFieldEnum(int type, String formType) {
        this.type = type;
        this.formType = formType;
    }

    private Integer type;
    private String formType;

    public static ModuleFieldEnum parse(Integer type) {
        for (ModuleFieldEnum fieldTypeEnum : ModuleFieldEnum.values()) {
            if (fieldTypeEnum.getType().equals(type)) {
                return fieldTypeEnum;
            }
        }
        throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
    }

    public static ModuleFieldEnum parse(String formType) {
        for (ModuleFieldEnum fieldTypeEnum : ModuleFieldEnum.values()) {
            if (fieldTypeEnum.getFormType().equals(formType)) {
                return fieldTypeEnum;
            }
        }
        return TEXT;
    }

    public Integer getType() {
        return type;
    }

    public String getFormType() {
        return formType;
    }

    private static final ModuleFieldEnum[] DIGIT_TYPE = {NUMBER, FLOATNUMBER, PERCENT};

    public static Boolean isDigit(Integer type) {
        if (Arrays.asList(DIGIT_TYPE).contains(parse(type))) {
            return true;
        }
        return false;
    }
}
