package com.kakarote.core.common.enums;

import lombok.Getter;

/**
 * @author JiaS
 * @date 2021/1/11
 */
public enum LanguageFieldEnum {


    /**
     *
     */
    MARKET_ACTIVITY_ASSOCIATED_OPTION("CRM-市场活动", "marketActivity.associated.option"),

    OPPORTUNITY_BUSINESSS_TATUSGROUP("CRM-商机", "opportunity.business_cause"),

    ADMINCRM_FOLLOWMETHODS_OPTION("CRM-跟进", "adminCrm.followMethods.option"),

    APPROVAL_CATEGORY("系统设置-办公审批", "approval.category"),

    ADMINCRM_POOLNAME_LIST("CRM-公海", "adminCrm.poolName.list"),

    ADMINCRM_PRODUCTCATEGORIES("CRM-产品分类", "adminCrm.productCategorieType"),

    SCENENAME("场景", " views"),

    EXAMINE_REMARKS("系统审批流备注", "examine.remarks"),

    EXAMINE_FLOW("系统审批流流程配置", "examine.flow"),

    TASKTYPE_OPTION("任务类型", "task.type.option"),
    ACTIONRECORD_UPDATE("修改为", "actionRecord.update"),
    ACTIONRECORD_EMPTY("空", "actionRecord.empty"),
    ACTIONRECORD_ADD("新建了", "actionRecord.add"),
    ACTIONRECORD_TRANSFER("转移给", "actionRecord.transfer"),
    ACTIONRECORD_LOCKING("锁定", "actionRecord.locking"),
    ACTIONRECORD_UNLOCKING("解锁", "actionRecord.Unlocking"),
    ACTIONRECORD_ASSIGNEDTO("分配给：", "actionRecord.Assignedto"),

    ACTIONRECORDD_DELETED("删除了", "actionRecord.Deleted"),
    ACTIONRECORD_QUIT("退出了", "actionRecord.Quit"),
    ACTIONRECORD_REMOVED("移除了", "actionRecord.Removed"),

    ACTIONRECORD_EXPORTED("导出了", "actionRecord.Exported"),

    ACTIONRECORD_STRIP("条", "actionRecord.strip"),
    ACTIONRECORD_TOVOID("作废 ", "actionRecord.tovoid"),

    ACTIONRECORD_ACTIVE("活动 ", "actionRecord.active"),

    ACTIONRECORD_ENABLE("启用", "actionRecord.Enable"),

    ACTIONRECORD_OUTOFSERVICE("停用", "actionRecord.Outofservice"),

    ACTIONRECORD_EDITED("编辑了 ", "actionRecord.Edited"),
    ACTIONRECORD_WITHDRAWN("撤回了", "actionRecord.Withdrawn"),
    ACTIONRECORD_PASSED("通过了", "actionRecord.Passed"),
    ACTIONRECORD_REJECTED("驳回了", "actionRecord.Rejected"),
    ACTIONRECORD_NEWFOLLOWUPRECORD("新建了跟进记录", "actionRecord.Newfollowuprecord"),
    ACTIONRECORD_SUBMITTED("提交了", "actionRecord.Submitted"),

    ACTIONRECORD_CONVERTTOCUSTOMER("转化为客户", "actionRecord.Converttocustomer"),

    ACTIONRECORD_PUTITONTHEHIGHSEAS("放入公海", "actionRecord.Putitonthehighseas"),
    ACTIONRECORD_RECEIVED("领取", "actionRecord.Received"),
    ACTIONRECORD_TEAMMEMBERS("团队成员", "actionRecord.Teammembers"),

    ACTIONRECORD_YES("是", "actionRecord.yes"),

    ACTIONRECORD_NO("否", "actionRecord.no"),

    ACTIONRECORD_UP("上架", "actionRecord.up"),

    ACTIONRECORD_OFF("下架", "actionRecord.down"),

    ACTIONRECORD_STATUS("状态", "actionRecord.status"),
    ;


    @Getter
    private String module;

    @Getter
    private String field;

    LanguageFieldEnum() {
    }

    LanguageFieldEnum(String module, String field) {
        this.module = module;
        this.field = field;

    }

    public String getFieldFormat() {
        return "{" + this.getField() + "}";
    }


}
