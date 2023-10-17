package com.kakarote.hrm.service.actionrecord;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.enums.LanguageFieldEnum;
import com.kakarote.hrm.constant.LabelGroupEnum;
import com.kakarote.hrm.entity.BO.HrmActionRecordBO;
import com.kakarote.hrm.entity.BO.HrmActionRecordListBO;
import com.kakarote.hrm.entity.PO.HrmDept;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.service.IHrmActionRecordService;
import com.kakarote.hrm.service.IHrmAppraisalActionRecordService;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public abstract class AbstractHrmActionRecordService {

    @Autowired
    protected IHrmActionRecordService actionRecordService;

    @Autowired
    protected IHrmAppraisalActionRecordService appraisalActionRecordService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmDeptService hrmDeptService;

    /**
     * 实体类编辑通用处理
     *
     * @return
     */
    protected HrmActionRecordListBO entityCommonUpdateRecord(LabelGroupEnum labelGroupEnum, Dict properties, Map<String, Object> oldColumns, Map<String, Object> newColumns) {
        HrmActionRecordListBO actionRecordListBO = new HrmActionRecordListBO();
        List<String> contentList = new ArrayList<>();
        List<String> transContentList = new ArrayList<>();
        String defaultValue = "空";
        String transDefaultValue = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
        for (String oldFieldKey : oldColumns.keySet()) {
            if (!properties.containsKey(oldFieldKey)) {
                continue;
            }
            Object oldValueObj = oldColumns.get(oldFieldKey);
            if (newColumns.containsKey(oldFieldKey)) {
                Object newValueObj = newColumns.get(oldFieldKey);
                String oldValue;
                String newValue;
                String transOldValue;
                String transNewValue;
                //转换value
                if (newValueObj instanceof Date || oldValueObj instanceof Date) {
                    oldValue = DateUtil.formatDateTime(Convert.toDate(oldValueObj));
                    newValue = DateUtil.formatDateTime(Convert.toDate(newValueObj));
                    transOldValue = oldValue;
                    transNewValue = newValue;
                } else if (newValueObj instanceof BigDecimal || oldValueObj instanceof BigDecimal) {
                    oldValue = Convert.toBigDecimal(oldValueObj, new BigDecimal(0)).setScale(2, BigDecimal.ROUND_UP).toString();
                    newValue = Convert.toBigDecimal(newValueObj, new BigDecimal(0)).setScale(2, BigDecimal.ROUND_UP).toString();
                    transOldValue = oldValue;
                    transNewValue = newValue;
                } else {
                    oldValue = Convert.toStr(oldValueObj);
                    newValue = Convert.toStr(newValueObj);
                    transOldValue = oldValue;
                    transNewValue = newValue;
                }
                if (StrUtil.isEmpty(oldValue)) {
                    oldValue = defaultValue;
                    transOldValue = transDefaultValue;
                }
                if (StrUtil.isEmpty(newValue)) {
                    newValue = defaultValue;
                    transNewValue = transDefaultValue;
                }
                if (!Objects.equals(oldValue, newValue)) {
                    contentList.add(compare(labelGroupEnum, properties, oldFieldKey, oldValue, newValue));
                    transContentList.add(transCompare(labelGroupEnum, properties, oldFieldKey, transOldValue, transNewValue));
                }
            }
        }
        actionRecordListBO.setContentList(contentList);
        actionRecordListBO.setTransContentList(transContentList);
        return actionRecordListBO;
    }

    /**
     * 比较返回content
     *
     * @param newFieldKey 字段名称
     * @param oldValue    老值
     * @param newValue    新值
     * @return content
     */
    protected String compare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        return "将" + properties.getStr(newFieldKey) + "由" + oldValue + "改为" + newValue;
    }

    protected String transCompare(LabelGroupEnum labelGroupEnum, Dict properties, String newFieldKey, String oldValue, String newValue) {
        return "" + properties.getStr(newFieldKey) + " " + oldValue + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + newValue;
    }

    /**
     * 员工比较
     *
     * @param fieldName
     * @param oldValue
     * @param newValue
     * @return
     */
    protected HrmActionRecordBO employeeCompare(String fieldName, String oldValue, String newValue) {
        HrmActionRecordBO hrmActionRecordBO = new HrmActionRecordBO();
        HrmEmployee oldEmployee = employeeService.getById(oldValue);
        HrmEmployee newEmployee = employeeService.getById(newValue);
        String oldDesc = "无";
        String newDesc = "无";
        String transOldDesc = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
        String transNewDesc = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
        if (oldEmployee != null) {
            oldDesc = oldEmployee.getEmployeeName();
            transOldDesc = oldDesc;
        }
        if (newEmployee != null) {
            newDesc = newEmployee.getEmployeeName();
            transNewDesc = newDesc;
        }
        hrmActionRecordBO.setContent("将" + fieldName + "由" + oldDesc + "改为" + newDesc);
        hrmActionRecordBO.setTransContent("" + fieldName + " " + transOldDesc + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + transNewDesc);
        return hrmActionRecordBO;
    }

    /**
     * 人力资源部门比较
     *
     * @param fieldName
     * @param oldValue
     * @param newValue
     * @return
     */
    protected HrmActionRecordBO hrmDeptCompare(String fieldName, String oldValue, String newValue) {
        HrmActionRecordBO hrmActionRecordBO = new HrmActionRecordBO();
        HrmDept oldDept = hrmDeptService.getById(oldValue);
        HrmDept newDept = hrmDeptService.getById(newValue);
        String oldDesc = "无";
        String newDesc = "无";
        String transOldDesc = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
        String transNewDesc = LanguageFieldEnum.ACTIONRECORD_EMPTY.getFieldFormat();
        if (oldDept != null) {
            oldDesc = oldDept.getName();
            transOldDesc = oldDesc;
        }
        if (newDept != null) {
            newDesc = newDept.getName();
            transNewDesc = newDesc;
        }
        hrmActionRecordBO.setContent("将" + fieldName + "由" + oldDesc + "改为" + newDesc);
        hrmActionRecordBO.setTransContent("" + fieldName + " " + transOldDesc + LanguageFieldEnum.ACTIONRECORD_UPDATE.getFieldFormat() + transNewDesc);
        return hrmActionRecordBO;
    }

}
