package com.kakarote.hrm.constant;

public enum HrmLanguageEnum {
    /**
     * 语言翻译类型类型枚举
     */
    //日期部分
    January(1, "一月", "hrm.config.month.1"),
    February(2, "二月", "hrm.config.month.2"),
    March(3, "三月", "hrm.config.month.3"),
    April(4, "四月", "hrm.config.month.4"),
    May(5, "五月", "hrm.config.month.5"),
    June(6, "六月", "hrm.config.month.6"),
    July(7, "七月", "hrm.config.month.7"),
    August(8, "八月", "hrm.config.month.8"),
    September(9, "九月", "hrm.config.month.9"),
    October(10, "十月", "hrm.config.month.10"),
    November(11, "十一月", "hrm.config.month.11"),
    December(12, "十二月", "hrm.config.month.12"),

    //薪资部分
    SALARY_REPORT(13, "薪资报表", "hrm.config.salary.report"),
    //薪资模板
    SALARY_SLIP_OPTION_BASE(14, "基本项", "hrm.config.salary.slip.option.base"),
    SALARY_SLIP_OPTION_DETAIL(15, "明细项", "hrm.config.salary.slip.option.detail"),
    SALARY_SLIP_OPTION_UNI(16, "未分类科目", "hrm.config.salary.slip.option.uni"),

    //日志记录部分
    REASON(28, "原因", "hrm.record.reason"),
    CANCEL(28, "取消", "hrm.record.cancel"),
    MOVE(28, "移到", "hrm.record.move"),

    CANDIDATE_NAME(17, "候选人名称", "hrm.record.candidate.candidateName"),
    CANDIDATE(17, "候选人", "hrm.record.candidate.candidate"),
    MOBILE(18, "手机", "hrm.record.candidate.mobile"),
    SEX(19, "性别", "hrm.record.candidate.sex"),
    EMAIL(20, "邮箱", "hrm.record.candidate.email"),
    AGE(21, "年龄", "hrm.record.candidate.age"),
    POST_ID(22, "职位", "hrm.record.candidate.postId"),
    WORK_TIME(23, "工作年限", "hrm.record.candidate.workTime"),
    EDUCATION(24, "学历", "hrm.record.candidate.education"),
    GRADUATE_SCHOOL(25, "毕业院校", "hrm.record.candidate.graduateSchool"),
    LATESTWORK_PLACE(26, "最近工作单位", "hrm.record.candidate.latestWorkPlace"),
    CHANNEL_ID(27, "招聘渠道", "hrm.record.candidate.channelId"),
    REMARK(28, "备注", "hrm.record.candidate.remark"),
    APPLY_POST(28, "应聘职位", "hrm.record.candidate.apply.post"),
    APPLY_CHANNEL(28, "应聘渠道", "hrm.record.candidate.apply.channel"),

    //candidate 模块
    ELIMINATE(28, "淘汰", "hrm.record.candidate.eliminate"),
    ELIMINATE_REASON(28, "淘汰原因", "hrm.record.candidate.eliminate.reason"),
    CLEAR(28, "清理", "hrm.record.candidate.clear"),
    ARRANGE(28, "安排", "hrm.record.candidate.arrange"),
    INTERVIEW(28, "面试", "hrm.record.candidate.interview"),
    INTERVIEW_METHOD(28, "面试方式", "hrm.record.candidate.interview.method"),
    INTERVIEW_PLAN(28, "面试安排", "hrm.record.candidate.interview.plan"),
    INTERVIEW_TIME(28, "面试时间", "hrm.record.candidate.interview.time"),
    INTERVIEWER(28, "面试官", "hrm.record.candidate.interviewer"),
    INTERVIEWER_OTHER(28, "其他面试官", "hrm.record.candidate.interviewer.other"),
    INTERVIEW_ROUNDS(28, "面试轮次", "hrm.record.candidate.interview.rounds"),
    INTERVIEWER_LAST(28, "原面试官", "hrm.record.candidate.interviewer.last"),
    INTERVIEWER_OTHER_LAST(28, "原其他面试官", "hrm.record.candidate.interviewer.other.last"),
    INTERVIEW_ROUNDS_LAST(28, "原面试轮次", "hrm.record.candidate.interview.rounds.last"),


    CANCEL_REASON(28, "取消原因", "hrm.record.candidate.cancel.reason"),


    //employee 模块
    UPLOAD(16, "上传", "hrm.record.employee.upload"),
    FILE(16, "附件", "hrm.record.employee.file"),
    PERSONNEL_CHANGE(13, "人事异动", "hrm.record.employee.personnel.change"),
    CHANGE_REASON(14, "异动原因", "hrm.record.employee.change.reason"),
    EFFECTIVE_DATE(15, "生效日期", "hrm.record.employee.effective.date"),
    DEPT(16, "部门", "hrm.record.employee.dept"),
    POST(13, "岗位", "hrm.record.employee.post"),
    RANK(14, "职级", "hrm.record.employee.rank"),
    WORKING_ADDRESS(15, "工作地址", "hrm.record.employee.working.address"),
    PROBATION_PERIOD(16, "试用期", "hrm.record.employee.probation.period"),
    MONTH(16, "月", "hrm.record.employee.month"),
    CONFIRMATION_DATE(13, "转正日期", "hrm.record.employee.confirmation_date"),
    REGULAR(14, "转正", "hrm.record.employee.regular"),
    RETIRE(14, "退休", "hrm.record.employee.retire"),

    RESIGNATION(15, "离职", "hrm.record.employee.resignation"),
    RESIGNATION_APPLY(15, "离职申请", "hrm.record.employee.resignation.application"),
    PLANNED_TERMINATION_DATE(16, "计划离职日期", "hrm.record.employee.planned.termination.date"),
    INSURANCE_SCHEME(16, "参保方案", "hrm.record.employee.insurance_scheme"),

    MALE(16, "男", "hrm.record.employee.male"),

    FEMALE(16, "女", "hrm.record.employee.female"),
    NO_PROBATION_PERIOD(16, "无试用期", "hrm.record.employee.no.probation.period"),
    SOLAR_CALENDAR(16, "阳历", "hrm.record.employee.solar.calendar"),
    LUNAR_CALENDAR(16, "农历", "hrm.record.employee.lunar.calendar"),
    FORMAL(16, "正式", "customField.hrmField.employmentFormsOptions.正式"),
    INFORMAL(16, "非正式", "customField.hrmField.employmentFormsOptions.非正式"),

    //appraisal
    ASSESSMENT_STARTED(16, "开启了考核", "hrm.record.employee.appraisal.assessment.started"),
    ASSESSMENT_COMMIT(16, "填写并提交了", "hrm.record.employee.appraisal.assessment.commit"),
    ASSESSMENT_END(16, "终止了考核", "hrm.record.employee.appraisal.assessment.end"),
    CONFIRM_ASSESSMENT_RESULT(16, "确认考核结果", "hrm.record.employee.appraisal.confirm.assessment.result"),
    ASSESSMENT_COMPLETED(16, "考核完成", "hrm.record.employee.appraisal.assessment.completed"),
    ASSESSMENT_APPEAL(16, "考核结果申诉", "hrm.record.employee.appraisal.assessment.appeal"),
    ASSESSMENT_APPEAL_NODE(16, "申诉节点列表", "hrm.record.employee.appraisal.assessment.node"),
    ASSESSMENT_APPEAL_PASS(16, "考核结果申诉通过", "hrm.record.employee.appraisal.assessment.appeal.pass"),
    ASSESSMENT_APPEAL_REJECTED(16, "考核结果申诉拒绝", "hrm.record.employee.appraisal.assessment.appeal.rejected"),
    TARGET_COMMIT(16, "提交了目标", "hrm.record.employee.appraisal.target.commit"),
    TARGET_CONFIRM(16, "确认了目标", "hrm.record.employee.appraisal.target.confirm"),
    TARGET_UPDATE(16, "修改了目标", "hrm.record.employee.appraisal.target.update"),
    TARGET_REJECTED(16, "驳回了目标", "hrm.record.employee.appraisal.target.rejected"),
    PROGRESS(16, "进度", "hrm.record.employee.appraisal.progress"),
    COMPLETION_DESCRIPTION(16, "完成情况说明", "hrm.record.employee.appraisal.completion.description"),
    EVALUATE_SELF_COMMIT(16, "提交了自评", "hrm.record.employee.appraisal.evaluate.commit"),
    EVALUATE_SELF_REJECTED(16, "驳回了自评", "hrm.record.employee.appraisal.evaluate.rejected"),
    EVALUATE_COMMIT(16, "提交了评定", "hrm.record.employee.appraisal.evaluate.commit"),
    EVALUATE_REJECTED(16, "驳回了评定", "hrm.record.employee.appraisal.evaluate.rejected"),
    REJECTED_REASON(16, "驳回理由", "hrm.record.employee.appraisal.rejected.reason"),
    //insurance
    GENERATE(16, "生成", "hrm.record.employee.insurance.generate"),
    //recruitPost
    OPEN(16, "启用", "hrm.record.employee.recruit.post.open"),
    CLOSE(16, "停止", "hrm.record.employee.recruit.post.close"),
    RECRUIT(27, "招聘", "hrm.record.recruit.post.recruit"),
    UNLIMITED(27, "不限", "hrm.record.recruit.post.unlimited"),
    FACE_TO_FACE(27, "面议", "hrm.record.recruit.post.face.to.face"),

    //salary
    ADD(27, "添加", "hrm.record.salary.add"),
    ACCOUNTING(27, "核算", "hrm.record.salary.accounting"),
    DELETE(27, "删除", "hrm.record.salary.delete"),
    ;

    HrmLanguageEnum(int value, String name, String key) {
        this.value = value;
        this.name = name;
        this.key = key;
    }


    private int value;
    private String name;
    private String key;

    public static String parseName(int value) {
        for (HrmLanguageEnum hrmLanguage : HrmLanguageEnum.values()) {
            if (hrmLanguage.value == value) {
                return hrmLanguage.name;
            }
        }
        return "";
    }

    public static String parseKey(int value) {
        for (HrmLanguageEnum hrmLanguage : HrmLanguageEnum.values()) {
            if (hrmLanguage.value == value) {
                return hrmLanguage.getKey();
            }
        }
        return "";
    }

    public static int valueOfType(String name) {
        for (HrmLanguageEnum value : HrmLanguageEnum.values()) {
            if (value.name.equals(name)) {
                return value.value;
            }
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public String getFieldFormat() {
        return "{" + this.getKey() + "}";
    }
}
