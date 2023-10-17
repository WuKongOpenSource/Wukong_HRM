package com.kakarote.common.log.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/14
 */
@Getter
public enum OperateObjectEnum {

    USER(1, "用户", ApplyEnum.ADMIN.getType()),

    /**
     * crm模块枚举
     */
    LEADS(1, "线索", ApplyEnum.CRM.getType()),
    CUSTOMER(2, "客户", ApplyEnum.CRM.getType()),
    CONTACTS(3, "联系人", ApplyEnum.CRM.getType()),
    PRODUCT(4, "产品", ApplyEnum.CRM.getType()),
    BUSINESS(5, "商机", ApplyEnum.CRM.getType()),
    CONTRACT(6, "合同", ApplyEnum.CRM.getType()),
    RECEIVABLES(7, "回款", ApplyEnum.CRM.getType()),
    RECEIVABLES_PLAN(8, "回款计划", ApplyEnum.CRM.getType()),
    MARKETING(10, "市场活动", ApplyEnum.CRM.getType()),
    RETURN_VISIT(17, "客户回访", ApplyEnum.CRM.getType()),
    INVOICE(18, "发票", ApplyEnum.CRM.getType()),
    ACTIVITY(19, "跟进记录", ApplyEnum.CRM.getType()),
    OUT_WORK_SIGN(22, "外勤签到", ApplyEnum.CRM.getType()),


    //审批管理
    OA_EXAMINE_MANAGE(1, "审批", ApplyEnum.OA.getType()),


    JXC_PRODUCT(1, "产品", ApplyEnum.JXC.getType()),
    JXC_SUPPLIER(2, "供应商", ApplyEnum.JXC.getType()),
    JXC_PURCHASE(3, "采购订单", ApplyEnum.JXC.getType()),
    JXC_RETREAT(4, "采购退货单", ApplyEnum.JXC.getType()),
    JXC_SALE(5, "销售订单", ApplyEnum.JXC.getType()),
    JXC_SALE_RETURN(6, "销售退货单", ApplyEnum.JXC.getType()),
    JXC_RECEIPT(7, "入库单", ApplyEnum.JXC.getType()),
    JXC_OUTBOUND(8, "出库单", ApplyEnum.JXC.getType()),
    JXC_PAYMENT(9, "付款单", ApplyEnum.JXC.getType()),
    JXC_COLLECTION(10, "回款单", ApplyEnum.JXC.getType()),
    JXC_INVENTORY(11, "盘点", ApplyEnum.JXC.getType()),
    JXC_ALLOCATION(12, "调拨", ApplyEnum.JXC.getType()),
    JXC_WAREHOUSE(15, "仓库", ApplyEnum.JXC.getType()),

    HRM_CANDIDATE(1, "候选人", ApplyEnum.HRM.getType()),
    HRM_POSITION(2, "招聘职位", ApplyEnum.HRM.getType()),
    HRM_EMPLOYEE(3, "员工管理", ApplyEnum.HRM.getType()),
    HRM_SALARY_ARCHIVES(4, "薪资档案", ApplyEnum.HRM.getType()),
    HRM_SALARY_SLIP_RECORD(5, "发放记录", ApplyEnum.HRM.getType()),
    HRM_KPI(6, "KPI考核", ApplyEnum.HRM.getType()),
    HRM_INSURANCE_SCHEME(7, "社保管理", ApplyEnum.HRM.getType()),
    HRM_SALARY(8, "薪资管理", ApplyEnum.HRM.getType()),

    PM_PROJECT(1, "项目", ApplyEnum.PM.getType()),
    PM_MATTERS(2, "事项", ApplyEnum.PM.getType()),
    PM_ITERATE(3, "迭代", ApplyEnum.PM.getType()),

    FS_VOUCHER(1, "凭证", ApplyEnum.FS.getType()),
    FS_SUBJECT(2, "科目", ApplyEnum.FS.getType()),
    FS_INITIAL_BALANCE(3, "初始余额", ApplyEnum.FS.getType()),
    FS_ADJUVANT_CARD(4, "辅助核算", ApplyEnum.FS.getType()),
    //明细账
    FS_DETAIL_ACCOUNT(5, "明细账", ApplyEnum.FS.getType()),
    //总账
    FS_GENERAL_LEDGER(6, "总账", ApplyEnum.FS.getType()),
    //    科目余额表
    FS_SUBJECT_BALANCE(7, "科目余额表", ApplyEnum.FS.getType()),
    //多栏账
    FS_MULTIPLE_BOOKS(8, "多栏账", ApplyEnum.FS.getType()),
    //项目明细账
    FS_PROJECT_DETAIL_ACCOUNT(9, "项目明细账", ApplyEnum.FS.getType()),
    //项目余额表
    FS_PROJECT_BALANCE(10, "项目余额表", ApplyEnum.FS.getType()),
    //数量金额明细账
    FS_QUANTITY_AMOUNT_DETAIL_ACCOUNT(11, "数量金额明细账", ApplyEnum.FS.getType()),
    //数量金额总账
    FS_QUANTITY_AMOUNT_GENERAL_LEDGER(12, "数量金额总账", ApplyEnum.FS.getType()),
    //资产负债表
    FS_BALANCE_SHEET(13, "资产负债表", ApplyEnum.FS.getType()),
    //利润表
    FS_PROFIT_STATEMENT(14, "利润表", ApplyEnum.FS.getType()),
    //现金流量表
    FS_CASH_FLOW_STATEMENT(15, "现金流量表", ApplyEnum.FS.getType()),
    //结账
    FS_CLOSING(16, "结账", ApplyEnum.FS.getType()),
    //期末结转凭证模板
    FS_CLOSING_VOUCHER_TEMPLATE(17, "期末结转凭证模板", ApplyEnum.FS.getType()),
    //凭证字
    FS_VOUCHER_WORD(18, "凭证字", ApplyEnum.FS.getType()),
    //币种
    FS_CURRENCY(19, "币种", ApplyEnum.FS.getType()),
    //系统参数
    FS_SYSTEM_PARAM(20, "系统参数", ApplyEnum.FS.getType()),


    COMPANY(1, "企业首页", ApplyEnum.SYSTEM_SETTING.getType()),
    COMPANY_WECHAT(2, "企业微信配置", ApplyEnum.SYSTEM_SETTING.getType()),
    APPLICATION_MANAGEMENT(3, "应用管理", ApplyEnum.SYSTEM_SETTING.getType()),
    APPLICATION_LINK_CENTER(4, "应用联接中心", ApplyEnum.SYSTEM_SETTING.getType()),
    EMPLOYEE_DEPARTMENT_MANAGEMENT(5, "员工与部门管理", ApplyEnum.SYSTEM_SETTING.getType()),
    DELEGATE_FLOW(6, "委托流程", ApplyEnum.SYSTEM_SETTING.getType()),
    AMOUNT_UNIT(7, "金额单位", ApplyEnum.SYSTEM_SETTING.getType()),
    LANGUAGE_SETTING(8, "多语言设置", ApplyEnum.SYSTEM_SETTING.getType()),
    INIT_DATA(9, "初始化数据", ApplyEnum.SYSTEM_SETTING.getType()),
    COMPANY_SECURITY_SETTING(10, "企业安全设置", ApplyEnum.SYSTEM_SETTING.getType()),


    SYSTEM_MANAGEMENT_ROLE(1, "系统管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    OA_MANAGEMENT_ROLE(2, "办公管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    CUSTOMER_MANAGEMENT_ROLE(3, "客户管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    HRM_MANAGEMENT_ROLE(4, "人力资源管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    FINANCIAL_MANAGEMENT_ROLE(5, "财务管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    JXC_MANAGEMENT_ROLE(6, "进销存管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    MARKETING_MANAGEMENT_ROLE(7, "营销管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),
    CUSTOMIZATION_MANAGEMENT_ROLE(8, "自定义管理角色", ApplyEnum.ROLE_PERMISSION_MANAGEMENT.getType()),


    CUSTOM_ROLE(1, "自定义角色设置", ApplyEnum.PROJECT_MANAGEMENT.getType()),
    STATUS_SETTINGS(2, "状态设置", ApplyEnum.PROJECT_MANAGEMENT.getType()),

    OFFICE_BUSINESS_PARAM(1, "业务参数设置", ApplyEnum.OFFICE_MANAGEMENT.getType()),
    APPROVAL_FLOW(2, "办公审批流", ApplyEnum.OFFICE_MANAGEMENT.getType()),
    LOG_TEMPLATE(3, "日志模板设置", ApplyEnum.OFFICE_MANAGEMENT.getType()),
    LOG_PRINT_TEMPLATE(4, "日志打印模板", ApplyEnum.OFFICE_MANAGEMENT.getType()),
    EXAMINE_PRINT_TEMPLATE(5, "审批打印模板", ApplyEnum.OFFICE_MANAGEMENT.getType()),

    CUSTOMER_MANAGEMENT_CUSTOM_FIELD(1, "客户管理自定义字段", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    BUSINESS_APPROVAL_FLOW(2, "业务审批流", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    LEADS_POOL_RULES(3, "线索池规则设置", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    CUSTOMER_SEA_RULES(4, "客户公海规则设置", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    CUSTOMER_PRINT_TEMPLATE(5, "自定义打印模板设置", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    CUSTOMER_BUSINESS_PARAM(6, "业务参数设置", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    CUSTOMER_TARGET_TEMPLATE(7, "业绩目标设置", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),
    MARKET_ACTIVITIES_FORM(8, "市场活动表单", ApplyEnum.CUSTOMER_MANAGEMENT.getType()),

    //人力资源管理自定义字段
    HUMAN_MANAGEMENT_CUSTOM_FIELD(1, "自定义字段", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //业务审批流
    HUMAN_APPROVAL_FLOW(2, "业务审批流", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //薪资设置
    HUMAN_SALARY_SETTING(3, "薪资设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //计薪设置
    HUMAN_PAYROLL_SETTING(4, "计薪设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //工资表设置
    HUMAN_SALARY_SHEET_SETTING(5, "工资表设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //社保方案设置
    HUMAN_INSURANCE_SCHEME_SETTING(6, "社保方案设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //业务参数设置
    HUMAN_BUSINESS_PARAM_SETTING(7, "业务参数设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //员工管理设置
    HUMAN_EMPLOYEE_SETTING(8, "员工管理设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),
    //考勤规则设置
    HUMAN_ATTENDANCE_RULE_SETTING(9, "考勤规则设置", ApplyEnum.HUMAN_RESOURCE_MANAGEMENT.getType()),


    JXC_MANAGEMENT_CUSTOM_FIELD(1, "自定义字段", ApplyEnum.JXC_MANAGEMENT.getType()),
    JXC_APPROVAL_FLOW(2, "业务审批流", ApplyEnum.JXC_MANAGEMENT.getType()),
    JXC_BUSINESS_PARAM_SETTING(3, "业务参数设置", ApplyEnum.JXC_MANAGEMENT.getType()),

    FS_PACKAGE_MANAGEMENT(1, "账套管理", ApplyEnum.FS_MANAGEMENT.getType()),


    NULL(0, "", 0);

    private final Integer type;
    private final String remarks;

    private final Integer applyType;

    OperateObjectEnum(Integer type, String remarks, Integer applyType) {
        this.type = type;
        this.remarks = remarks;
        this.applyType = applyType;
    }


    public static List<OperateObjectEnum> getOperateObjectList(Integer applyType) {
        List<OperateObjectEnum> operateObjectEnumList = new ArrayList<>();
        for (OperateObjectEnum value : OperateObjectEnum.values()) {
            if (value.applyType.equals(applyType)) {
                operateObjectEnumList.add(value);
            }
        }
        return operateObjectEnumList;
    }

    public static OperateObjectEnum parse(Integer type, Integer applyType) {
        for (OperateObjectEnum value : OperateObjectEnum.values()) {
            if (value.type.equals(type) && value.applyType.equals(applyType)) {
                return value;
            }
        }
        return NULL;
    }


}
