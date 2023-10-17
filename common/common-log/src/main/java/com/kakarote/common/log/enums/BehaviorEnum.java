package com.kakarote.common.log.enums;

import lombok.Getter;

/**
 * 操作记录行为
 */
@Getter
public enum BehaviorEnum {

    SAVE(1, "新建", 0),
    UPDATE(2, "编辑", 0),
    DELETE(3, "删除", 0),
    CHANGE_OWNER(4, "转移", 0),
    TRANSFER(5, "转化", 0),
    EXCEL_IMPORT(6, "导入", 1),
    EXCEL_EXPORT(7, "导出", 1),
    PUT_IN_POOL(8, "放入公海", 0),
    RECEIVE(9, "领取", 0),
    DISTRIBUTE(10, "分配", 0),
    LOCK(11, "锁定", 0),
    UNLOCK(12, "解锁", 0),
    CHANGE_DEAL_STATUS(13, "更改成交状态", 0),
    ADD_MEMBER(14, "添加团队成员", 0),
    UPDATE_MEMBER(15, "编辑团队成员", 0),
    REMOVE_MEMBER(16, "移除团队成员", 0),
    EXIT_MEMBER(17, "退出团队", 0),
    UPLOAD(18, "上传", 0),
    UPDATE_BUSINESS_STATUS(19, "编辑商机阶段", 1),
    SUBMIT_EXAMINE(20, "提交审核", 1),
    CANCEL_EXAMINE(21, "作废合同", 1),
    PUT_ON_SALE(22, "上架", 0),
    PUT_OFF_SALE(23, "下架", 0),
    START(24, "启用", 0),
    STOP(25, "停用", 0),
    FOLLOW_UP(26, "新建跟进记录", 1),
    PASS_EXAMINE(27, "通过审批", 1),
    REJECT_EXAMINE(28, "驳回审批", 1),
    RECHECK_EXAMINE(29, "撤回审批", 1),
    ARCHIVE(30, "归档", 0),
    RESTORE(31, "恢复", 1),
    EXIT(32, "退出", 1),
    ACTIVE(33, "激活", 0),
    CLEAN(34, "彻底删除", 1),
    FORBID(35, "禁用", 0),
    RESET(36, "重置", 0),
    COPY(37, "复制", 0),
    RELATE(38, "关联", 0),
    UNBINDING(39, "解绑", 0),
    PUT_IN_CLUE_POOLING(40, "放入线索池", 0),
    CLUE_POOLING_RULES_SETTING(41, "线索池规则设置", 0),
    MARK_INVOICING(42, "标记开票", 0),
    RESET_INVOICING_INFO(43, "重置开票信息", 0),
    LOGIN(44, "登录", 1),
    INVALID(45, "作废", 0),
    UPDATE_STATUS(46, "修改状态", 0),
    BECOME(47, "转正", 0),
    CHANGE_POST(48, "调整部门/岗位", 0),
    ADD_LEAVE(49, "办理离职", 0),
    UPDATE_LEAVE(50, "修改离职信息", 0),
    DELETE_LEAVE(51, "取消离职", 0),
    INSURANCE_SCHEME(52, "参保方案", 0),
    FIX_SALARY(53, "定薪", 0),
    CHANGE_SALARY(54, "调薪", 0),
    REINSTATEMENT(55, "再入职", 0),
    SEND_SALARY_SLIP(56, "发送工资条", 0),
    OPEN_APPRAISAL(57, "开启考核", 0),
    OPEN_SCORING(58, "开启评分", 0),
    START_INTERVIEW(59, "发起绩效面谈", 0),
    TERMINATION_APPRAISAL(60, "终止考核", 0),
    STOP_INSURANCE(61, "停止参保", 0),
    ADD_INSURANCE_SCHEME(62, "添加参保方案", 0),
    ARRANGE_INTERVIEW(63, "安排面试", 0),
    CANCEL_INTERVIEW(64, "取消面试", 0),
    PASS_INTERVIEW(65, "通过面试", 0),
    NOT_PASS_INTERVIEW(66, "未通过面试", 0),
    DETERMINE_ENTRY(67, "确定入职", 0),
    PROMOTION(68, "晋升/降级", 0),
    EDIT_ROLE(69, "编辑角色", 0),
    EXAMINE(70, "审核", 1),
    REVERSE_EXAMINE(71, "反审核", 1),
    INSERT(72, "插入", 0),
    GENERATE(73, "生成", 0),
    INVITE(74, "邀请", 0),
    AUTHORIZATION(75, "授权", 0),
    CANCEL_AUTHORIZATION(76, "取消授权", 0),
    DOWNLOAD(77, "下载", 0),
    INIT(78, "初始化", 0),
    UPDATE_RECRUIT_POST(79, "更改招聘职位", 0),
    UPDATE_RECRUIT_CHANNEL(80, "更改招聘渠道", 0),
    PRIMARY_ELECTION(81, "初选通过", 0),
    ARRANGE_AN_INTERVIEW(82, "安排面试", 0),
    PASS_THE_INTERVIEW(83, "面试通过", 0),
    OFFER_HAS_BEEN_SENT(84, "已发offer", 0),
    PENDING_ENTRY(85, "待入职", 0),
    OBSOLETE(86, "已淘汰", 0),
    HAVE_JOINED(87, "已入职", 0),
    UPDATE_INSURANCE_SCHEME(88, "修改参保方案", 0),
    //添加参保人员
    ADD_INSURANCE_EMP(89, "添加参保人员", 0),
    MERGE(90, "合并", 0),

    NULL(0, "null", 1);

    private final int type;
    private final String name;
    //0 展示 1不展示
    private final int state;

    BehaviorEnum(int type, String name, int state) {
        this.type = type;
        this.name = name;
        this.state = state;
    }


    public static BehaviorEnum parse(int type) {
        for (BehaviorEnum Enum : BehaviorEnum.values()) {
            if (Enum.getType() == type) {
                return Enum;
            }
        }
        return NULL;
    }
}
