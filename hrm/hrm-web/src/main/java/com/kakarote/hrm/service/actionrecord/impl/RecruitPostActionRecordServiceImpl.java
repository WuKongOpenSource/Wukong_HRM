package com.kakarote.hrm.service.actionrecord.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.core.common.enums.LanguageFieldEnum;
import com.kakarote.hrm.constant.*;
import com.kakarote.hrm.entity.BO.HrmActionRecordListBO;
import com.kakarote.hrm.entity.BO.UpdateRecruitPostStatusBO;
import com.kakarote.hrm.entity.PO.HrmRecruitPostType;
import com.kakarote.hrm.service.IHrmRecruitPostTypeService;
import com.kakarote.hrm.service.actionrecord.AbstractHrmActionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName: HrmRecruitPostActionRecordService.java
 * @Description: 招聘职位操作记录
 * @author: hmb
 * @date: 2020-04-29 16:14
 */
@Service("recruitPostActionRecordService")
public class RecruitPostActionRecordServiceImpl extends AbstractHrmActionRecordService {

    private static HrmActionTypeEnum actionTypeEnum = HrmActionTypeEnum.RECRUIT_POST;
    private static LabelGroupEnum labelGroupEnum = LabelGroupEnum.RECRUIT_POST;

    @Autowired
    private IHrmRecruitPostTypeService postTypeService;

    private static final String NULL = "空";

    /**
     * 属性kv
     */
    private static Dict properties = Dict.create().set("postName", "职位名称").set("deptId", "部门").set("employmentForms", "聘用形式").set("employmentStatus", "工作性质").set("city", "工作城市")
            .set("recruitNum", "招聘人数").set("reason", "招聘原因").set("workTime", "工作经验").set("educationRequire", "学历要求").set("minSalary", "开始薪资")
            .set("maxSalary", "结束薪资").set("latestEntryTime", "最迟到岗时间").set("ownerEmployeeId", "负责人").set("description", "职位描述")
            .set("emergencyLevel", "紧急程度").set("postTypeId", "职位类型").set("minAge", "最小年龄").set("maxAge", "最大年龄");


    /**
     * 新建/删除操作记录
     */
    public Content addOrDeleteRecord(HrmActionBehaviorEnum behaviorEnum, Long postId, String object) {
        String content = behaviorEnum.getName() + "了" + labelGroupEnum.getDesc() + "：" + object;
        String transContent = behaviorEnum.getFieldFormat() + " " + labelGroupEnum.getFieldFormat();
        actionRecordService.saveRecord(actionTypeEnum, behaviorEnum, Collections.singletonList(content), Collections.singletonList(transContent), postId);
        if (HrmActionBehaviorEnum.ADD.equals(behaviorEnum)) {
            return new Content(object, content, transContent, BehaviorEnum.SAVE);
        } else {
            return new Content(object, content, transContent, BehaviorEnum.DELETE);
        }
    }

    /**
     * 修改职位状态操作记录
     */
    public String updateStatusRecord(UpdateRecruitPostStatusBO updateRecruitPostStatusBO, String postName) {
        String content = "";
        String transContent = "";
        if (updateRecruitPostStatusBO.getStatus() == IsEnum.YES.getValue()) {
            content += "启用";
            transContent += HrmLanguageEnum.OPEN.getFieldFormat();
        } else {
            content += "停止";
            transContent += HrmLanguageEnum.CLOSE.getFieldFormat();
        }
        content += "了招聘：" + postName;
        transContent += " " + HrmLanguageEnum.RECRUIT.getFieldFormat();
        if (StrUtil.isNotEmpty(updateRecruitPostStatusBO.getReason())) {
            content += ",原因:" + updateRecruitPostStatusBO.getReason();
            transContent += "," + HrmLanguageEnum.REASON.getFieldFormat() + ":" + updateRecruitPostStatusBO.getReason();
        }
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, Collections.singletonList(content), Collections.singletonList(transContent), updateRecruitPostStatusBO.getPostId());
        return content;
    }

    /**
     * 职位实体类修改
     */
    public HrmActionRecordListBO entityUpdateRecord(Map<String, Object> oldRecord, Map<String, Object> newRecord, Long postId) {
        HrmActionRecordListBO recordListBO = entityCommonUpdateRecord(labelGroupEnum, properties, oldRecord, newRecord);
        actionRecordService.saveRecord(actionTypeEnum, HrmActionBehaviorEnum.UPDATE, recordListBO.getContentList(), recordListBO.getTransContentList(), postId);
        return recordListBO;
    }

    @Override
    protected String compare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        String content = super.compare(labelGroupEnum, properties, newFieldKey, oldValue, newValue);
        String deptId = "deptId";
        String workTime = "workTime";
        String educationRequire = "educationRequire";
        String ownerEmployeeId = "ownerEmployeeId";
        String emergencyLevel = "emergencyLevel";
        String employmentForms = "employmentForms";
        String employmentStatus = "employmentStatus";
        String postTypeId = "postTypeId";
        String minAge = "minAge";
        String maxAge = "maxAge";
        String maxSalary = "maxSalary";
        String minSalary = "minSalary";
        if (deptId.equals(newFieldKey)) {
            content = hrmDeptCompare(properties.getStr(newFieldKey), oldValue, newValue).getContent();
        } else if (workTime.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = RecruitEnum.RecruitPostWorkTime.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = RecruitEnum.RecruitPostWorkTime.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (educationRequire.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = PostEducationEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = PostEducationEnum.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (ownerEmployeeId.equals(newFieldKey)) {
            content = employeeCompare(properties.getStr(newFieldKey), oldValue, newValue).getContent();
        } else if (emergencyLevel.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = RecruitEnum.RecruitPostEmergencyLevel.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = RecruitEnum.RecruitPostEmergencyLevel.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (employmentForms.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = EmploymentFormsEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = EmploymentFormsEnum.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (employmentStatus.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = EmployeeStatusEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = EmployeeStatusEnum.parseName(Integer.parseInt(newValue));
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (postTypeId.equals(newFieldKey)) {
            HrmRecruitPostType oldPoseType = postTypeService.getById(oldValue);
            HrmRecruitPostType newPoseType = postTypeService.getById(newValue);
            if (oldPoseType != null) {
                oldValue = oldPoseType.getName();
            }
            if (!NULL.equals(newValue)) {
                newValue = newPoseType.getName();
            }
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (minAge.equals(newFieldKey) || maxAge.equals(newFieldKey)) {
            oldValue = "-1".equals(oldValue) ? "不限" : oldValue;
            newValue = "-1".equals(newValue) ? "不限" : newValue;
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        } else if (minSalary.equals(newFieldKey) || maxSalary.equals(newFieldKey)) {
            oldValue = "-1.00".equals(oldValue) ? "面议" : oldValue;
            newValue = "-1.00".equals(newValue) ? "面议" : newValue;
            content = "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
        }
        return content;
    }

    @Override
    protected String transCompare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        String content = super.transCompare(labelGroupEnum, properties, newFieldKey, oldValue, newValue);
        String deptId = "deptId";
        String workTime = "workTime";
        String educationRequire = "educationRequire";
        String ownerEmployeeId = "ownerEmployeeId";
        String emergencyLevel = "emergencyLevel";
        String employmentForms = "employmentForms";
        String employmentStatus = "employmentStatus";
        String postTypeId = "postTypeId";
        String minAge = "minAge";
        String maxAge = "maxAge";
        String maxSalary = "maxSalary";
        String minSalary = "minSalary";
        if (deptId.equals(newFieldKey)) {
            content = hrmDeptCompare(properties.getStr(newFieldKey), oldValue, newValue).getTransContent();
        } else if (workTime.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = RecruitEnum.RecruitPostWorkTime.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = RecruitEnum.RecruitPostWorkTime.parseName(Integer.parseInt(newValue));
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (educationRequire.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = PostEducationEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = PostEducationEnum.parseName(Integer.parseInt(newValue));
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (ownerEmployeeId.equals(newFieldKey)) {
            content = employeeCompare(properties.getStr(newFieldKey), oldValue, newValue).getTransContent();
        } else if (emergencyLevel.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = RecruitEnum.RecruitPostEmergencyLevel.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = RecruitEnum.RecruitPostEmergencyLevel.parseName(Integer.parseInt(newValue));
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (employmentForms.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = EmploymentFormsEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = EmploymentFormsEnum.parseName(Integer.parseInt(newValue));
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (employmentStatus.equals(newFieldKey)) {
            if (!NULL.equals(oldValue) && StrUtil.isNumeric(oldValue)) {
                oldValue = EmployeeStatusEnum.parseName(Integer.parseInt(oldValue));
            }
            if (!NULL.equals(newValue) && StrUtil.isNumeric(newValue)) {
                newValue = EmployeeStatusEnum.parseName(Integer.parseInt(newValue));
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (postTypeId.equals(newFieldKey)) {
            HrmRecruitPostType oldPoseType = postTypeService.getById(oldValue);
            HrmRecruitPostType newPoseType = postTypeService.getById(newValue);
            if (oldPoseType != null) {
                oldValue = oldPoseType.getName();
            }
            if (!NULL.equals(newValue)) {
                newValue = newPoseType.getName();
            }
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (minAge.equals(newFieldKey) || maxAge.equals(newFieldKey)) {
            oldValue = "-1".equals(oldValue) ? HrmLanguageEnum.UNLIMITED.getFieldFormat() : oldValue;
            newValue = "-1".equals(newValue) ? HrmLanguageEnum.UNLIMITED.getFieldFormat() : newValue;
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        } else if (minSalary.equals(newFieldKey) || maxSalary.equals(newFieldKey)) {
            oldValue = "-1.00".equals(oldValue) ? HrmLanguageEnum.FACE_TO_FACE.getFieldFormat() : oldValue;
            newValue = "-1.00".equals(newValue) ? HrmLanguageEnum.FACE_TO_FACE.getFieldFormat() : newValue;
            content = " " + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
        }
        return content;
    }

}
