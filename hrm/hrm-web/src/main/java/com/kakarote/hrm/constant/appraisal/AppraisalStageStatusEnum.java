package com.kakarote.hrm.constant.appraisal;


import com.kakarote.hrm.entity.VO.HeadFieldVO;

/**
 * 考核计划阶段状态枚举类
 */
public enum AppraisalStageStatusEnum {

    NO_START(0, "未开始", "noStart"),
    FILL(1, "员工填写", "fill"),
    TARGET_CONFIRMATION(2, "目标确认", "targetConfirmation"),
    SELF_SCORE(3, "自评", "selfScore"),
    OTHER_SCORE(4, "他人评分", "otherScore"),
    RESULT_AUDIT(5, "结果审核", "resultAudit"),
    RESULT_CONFIRMATION(6, "结果确认", "resultConfirmation"),
    APPEAL_CONFIRMATION(7, "申诉确认", "appealConfirmation"),
    PLACE_ON_FILE(8, "归档", "placeOnFile"),
    EXECUTING(9, "执行中", "executing"),
    END(10, "结束", "end"),
    ;
    //阶段状态：0：未开始 1：员工填写 2:目标确认3：自评分 4：他人评分 5：结果审核6：结果确认7：申诉确认：归档 9:执行中
    private Integer value;
    private String name;
    private String fieldName;

    AppraisalStageStatusEnum(Integer value, String name, String fieldName) {
        this.value = value;
        this.name = name;
        this.fieldName = fieldName;
    }

    public static AppraisalStageStatusEnum parse(Integer type) {
        for (AppraisalStageStatusEnum value : AppraisalStageStatusEnum.values()) {
            if (value.value.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static String parseName(Integer type) {
        for (AppraisalStageStatusEnum value : AppraisalStageStatusEnum.values()) {
            if (value.value.equals(type)) {
                return value.name;
            }
        }
        return "";
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    public static HeadFieldVO generateKey(Integer stageType) {
        for (AppraisalStageStatusEnum value : AppraisalStageStatusEnum.values()) {
            if (value.value.equals(stageType)) {
                HeadFieldVO headFieldVO = new HeadFieldVO(value.fieldName, value.name, value.value);
                return headFieldVO;
            }
        }
        return null;
    }
}
