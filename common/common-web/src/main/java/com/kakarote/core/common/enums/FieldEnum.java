package com.kakarote.core.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 自定义字段枚举类
 *
 * @author zhangzhiwei
 * @date 2021/1/11
 */
@Getter
public enum FieldEnum {

    /**
     * 字段类型 1 单行文本 2 多行文本 3 单选 4日期 5 数字 6 小数 7 手机  8 文件 9 多选 10 人员
     * 11 附件 12 部门 13 日期时间 14 邮箱 15客户 16 商机 17 联系人 18 地图 19 产品类型 20 合同 21 回款计划  29图片
     */
    TEXT(1, "text", "单行文本"),
    TEXTAREA(2, "textarea", "多行文本"),
    SELECT(3, "select", "单选"),
    DATE(4, "date", "日期"),
    NUMBER(5, "number", "数字"),
    FLOATNUMBER(6, "floatnumber", "小数"),
    MOBILE(7, "mobile", "手机"),
    FILE(8, "file", "文件"),
    CHECKBOX(9, "checkbox", "多选"),
    USER(10, "user", "人员"),
    ATTACHMENT(11, "attachment", "附件"),
    STRUCTURE(12, "structure", "部门"),
    DATETIME(13, "datetime", "日期时间"),
    EMAIL(14, "email", "邮件"),
    CUSTOMER(15, "customer", "客户"),
    BUSINESS(16, "business", "商机"),
    CONTACTS(17, "contacts", "联系人"),
    MAP_ADDRESS(18, "map_address", "地图"),
    CATEGORY(19, "category", "产品类型"),
    CONTRACT(20, "contract", "合同"),
    RECEIVABLES_PLAN(21, "receivables_plan", "回款计划"),
    BUSINESS_CAUSE(22, "business_cause", "商机业务"),
    EXAMINE_CAUSE(23, "examine_cause", "审批业务"),
    ADDRESS(24, "address", "地址"),
    WEBSITE(25, "website", "网址"),
    RETURN_VISIT(26, "return_visit", "回访"),
    RETURN_VISIT_CONTRACT(27, "return_visit_contract", "回访合同"),
    SINGLE_USER(28, "single_user", "单个人员"),
    /**
     * 进销存
     */
    PIC(29, "pic", "图片"),
    SUPPLIER_CAUSE(30, "supplier_cause", "供应商"),
    PURCHASE_CAUSE(31, "purchase_cause", "采购订单"),
    SALE_CAUSE(32, "sale_cause", "销售订单"),
    WAREHOUSE_CAUSE(33, "warehouse_cause", "仓库"),
    RELATED_ID(34, "related_id", "关联对象"),
    COLLECTION_OBJECT(35, "collection_object", "收藏"),
    RETREAT_CAUSE(36, "retreat_cause", "采购退货单"),
    SALE_RETURN_CAUSE(37, "sale_return_cause", "销售退货"),
    STATE_CAUSE(39, "state_cause", "状态标识"),
    /**
     * 人资
     */
    AREA(40, "area", "省市区"),

    BOOLEAN_VALUE(41, "boolean_value", "布尔值"),
    PERCENT(42, "percent", "百分数"),
    AREA_POSITION(43, "position", "地址"),
    CURRENT_POSITION(44, "location", "定位"),
    DETAIL_TABLE(45, "detail_table", "明细表格"),
    HANDWRITING_SIGN(46, "handwriting_sign", "手写签名"),
    DATE_INTERVAL(48, "date_interval", "日期区间"),
    OPTIONS_TYPE(49, "options_type", "选项字段:逻辑表单、批量编辑、其他"),
    DESC_TEXT(50, "desc_text", "描述文字"),
    CALCULATION_FUNCTION(51, "calculation_function", "计算函数"),
    RELATE_CAUSE(52, "relate_cause", "关联业务"),
    QUOTE_TYPE(53, "quote_type", "引用字段"),
    CITY(54, "city", "省市"),
    RECRUIT_CHANNEL(55, "recruit_channel", "招聘渠道"),
    FIELD_GROUP(60, "field_group", "分组字段"),
    TAG(61, "field_tag", "标签"),
    ATTENTION(62, "field_attention", "关注度字段"),
    SERIAL_NUMBER(63, "serial_number", "唯一编号"),
    INTENTIONAL_BUSINESS(64, "product", "产品"),
    SUPERIOR_CUSTOMER(65, "superior_customer_id", "相关客户"),
    CUSTOMER_RELATIONS(66, "customer_relations", "客户关系"),

    RTF(70, "rich_text_format", "富文本"),

    DATA_UNION(100, "data_union", "数据关联字段"),

    DATA_UNION_MULTI(110, "data_union_multi", "数据关联多选"),

    //补全状体
    CUSTOMER_DEAL_STATUS(101, "dealStatus", "成交状态"),
    PRODUCT_STATUS(102, "productStatus", "是否上下架"),
    RECEIVABLES_PLAN_STATUS(103, "receivedStatus", "汇款状态"),
    INVOICE_TYPE(104, "invoiceType", "开票类型"),
    INVOICE_STATUS(105, "invoiceStatus", "开票状态"),
    CHECK_STATUS_BASE(106, "checkStatus", "审核状态"),
    RECEIVABLES(107, "receivables", "回款"),
    ;

    private final Integer type;

    @Getter
    private final String formType;

    @Getter
    private final String desc;

    private static final HashMap<Integer, FieldEnum> TYPE_TO_FIELD_ENUM_MAP = new HashMap<Integer, FieldEnum>() {
        {
            for (FieldEnum value : FieldEnum.values()) {
                put(value.getType(), value);
            }
        }
    };

    private static final HashMap<String, FieldEnum> FORM_TYPE_TO_FIELD_ENUM_MAP = new HashMap<String, FieldEnum>() {
        {
            for (FieldEnum value : FieldEnum.values()) {
                put(value.getFormType(), value);
            }
        }
    };


    FieldEnum(Integer type, String formType, String desc) {
        this.type = type;
        this.formType = formType;
        this.desc = desc;
    }


    public static FieldEnum parse(Integer type) {
        FieldEnum fieldEnum = TYPE_TO_FIELD_ENUM_MAP.get(type);
        if (fieldEnum == null) {
            return TEXT;
        }
        return fieldEnum;
    }

    public static FieldEnum parse(String formType) {
        FieldEnum fieldEnum = FORM_TYPE_TO_FIELD_ENUM_MAP.get(formType);
        if (fieldEnum == null) {
            return TEXT;
        }
        return fieldEnum;
    }

    /**
     * 仪表盘显示的字段类型
     **/
    public static List<Integer> getShowFieldEnumType() {
        List<Integer> biDashboardNoShowFieldEnum = new ArrayList<>();
        List<Integer> list = Arrays.asList(101, 102, 103, 104, 105, 106);
        for (FieldEnum value : FieldEnum.values()) {
            if (value.getType() < 46 || list.contains(value.getType())) {
                biDashboardNoShowFieldEnum.add(value.getType());
            }
        }
        //增加编号字段
        biDashboardNoShowFieldEnum.add(63);
        return biDashboardNoShowFieldEnum;
    }

    /**
     * 仅数字类型
     *
     * @author UNIQUE
     * @date 2023/4/8
     */
    public static List<String> getShowTargetFieldEnumType() {
        return Arrays.asList(FieldEnum.NUMBER.getFormType()
                , FieldEnum.FLOATNUMBER.getFormType()
                , FieldEnum.PERCENT.getFormType()
        );
    }
}
