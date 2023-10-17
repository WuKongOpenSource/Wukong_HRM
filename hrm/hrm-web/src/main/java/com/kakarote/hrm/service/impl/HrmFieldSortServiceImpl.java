package com.kakarote.hrm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.constant.HrmEnum;
import com.kakarote.hrm.entity.PO.HrmEmployeeField;
import com.kakarote.hrm.entity.PO.HrmFieldSort;
import com.kakarote.hrm.entity.VO.HrmFieldSortVO;
import com.kakarote.hrm.mapper.HrmFieldSortMapper;
import com.kakarote.hrm.service.IHrmEmployeeFieldService;
import com.kakarote.hrm.service.IHrmFieldSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段排序表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-19
 */
@Service
public class HrmFieldSortServiceImpl extends BaseServiceImpl<HrmFieldSortMapper, HrmFieldSort> implements IHrmFieldSortService {

    @Autowired
    private IHrmEmployeeFieldService hrmEmployeeFieldService;

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    @Override
    public List<HrmFieldSortVO> queryListHead(Integer label) {
        Long userId = UserUtil.getUserId();
        QueryWrapper<HrmFieldSort> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("label", label);
        int number = (int) count(wrapper);
        if (number == 0) {
            saveUserFieldSort(label, userId);
        }
        List<HrmFieldSortVO> hrmFieldSortVOS = getBaseMapper().queryListHead(label, userId);
        //加工是否锁定排序
        List<HrmFieldSortVO> collect = hrmFieldSortVOS.stream().sorted(Comparator.comparing(HrmFieldSortVO::getIsLock, Comparator.nullsFirst(Integer::compareTo)).reversed()).collect(Collectors.toList());
        return collect;
    }

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    @Override
    public List<HrmFieldSortVO> queryListHead(Integer label, List<Long> ids) {
        Long userId = UserUtil.getUserId();
        QueryWrapper<HrmFieldSort> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("label", label).in("id", ids);
        int number = (int) count(wrapper);
        if (number == 0) {
            saveUserFieldSort(label, userId);
        }
        return getBaseMapper().queryListHeadByIds(label, userId, ids);
    }

    @Override
    public List<HrmFieldSort> queryAllFieldSortList(Integer label, Long userId) {
        List<HrmEmployeeField> crmFieldList = hrmEmployeeFieldService.list(label, false);
        HrmEnum crmEnum = HrmEnum.parse(label);
        //需要初始化时锁定的字段
        List<String> isLockStrNames = new ArrayList<>();
        switch (crmEnum) {
            case APPRAISAL_EMPLOYEE:
                crmFieldList = new ArrayList<>();
                crmFieldList.add(new HrmEmployeeField("mobile", "手机号", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("job_number", "工号", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("dept_id", "部门", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("employment_forms", "聘用形式", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("stage_status", "考核状态", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("stage_users", "处理人", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("score", "评分", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("level", "结果", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("appraisal_employee_id", "员工考核id", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("employee_id", "考核员工id", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("appraisal_plan_id", "考核计划id", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("stageUsersName", "处理人名称", FieldEnum.TEXT));
                crmFieldList.add(new HrmEmployeeField("stageStatusName", "考核状态名称", FieldEnum.TEXT));
                isLockStrNames.add("customerName");
                break;
            default:
                break;
        }

        crmFieldList.add(new HrmEmployeeField("updateTime", "更新时间", FieldEnum.DATETIME));
        crmFieldList.add(new HrmEmployeeField("createTime", "创建时间", FieldEnum.DATETIME));
        crmFieldList.add(new HrmEmployeeField("createUserName", "创建人", FieldEnum.TEXT));


        List<HrmFieldSort> fieldSortList = new ArrayList<>();
        for (int i = 0; i < crmFieldList.size(); i++) {
            HrmFieldSort fieldSort = new HrmFieldSort();
            fieldSort.setFieldId(crmFieldList.get(i).getFieldId());
            fieldSort.setFieldName(parseFieldName(crmFieldList.get(i).getFieldName()));
            fieldSort.setName(crmFieldList.get(i).getName());
            fieldSort.setSort(i);
            fieldSort.setUserId(userId);
            if (crmFieldList.get(i).getIsHidden() == null) {
                fieldSort.setIsHide(0);
            } else {
                fieldSort.setIsHide(crmFieldList.get(i).getIsHidden());
            }
            if (isLockStrNames.contains(fieldSort.getFieldName())) {
                fieldSort.setIsLock(1);
            }
            fieldSort.setLabel(label);
            fieldSort.setType(crmFieldList.get(i).getType());
            fieldSortList.add(fieldSort);
        }
        return fieldSortList;
    }

    /**
     * 保存用户排序
     *
     * @param label  label
     * @param userId 用户ID
     */
    private void saveUserFieldSort(Integer label, Long userId) {
        List<HrmFieldSort> fieldSortList = queryAllFieldSortList(label, userId);
        saveBatch(fieldSortList, Const.BATCH_SAVE_SIZE);
    }

    private String parseFieldName(String fieldName) {
        String id = "_id";
        if (fieldName.endsWith(id)) {
            String name = "_name";
            fieldName = fieldName.substring(0, fieldName.lastIndexOf(id)).concat(name);
        }
        return StrUtil.toCamelCase(fieldName);
    }
}
