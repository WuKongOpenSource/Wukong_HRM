package com.kakarote.hrm.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.constant.HrmLanguageEnum;
import com.kakarote.hrm.entity.PO.HrmSalaryOption;
import com.kakarote.hrm.entity.PO.HrmSalarySlipTemplate;
import com.kakarote.hrm.entity.PO.HrmSalarySlipTemplateOption;
import com.kakarote.hrm.mapper.HrmSalarySlipTemplateOptionMapper;
import com.kakarote.hrm.service.IHrmSalaryOptionService;
import com.kakarote.hrm.service.IHrmSalarySlipTemplateOptionService;
import com.kakarote.hrm.service.IHrmSalarySlipTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 工资条模板项 服务实现类
 * </p>
 *
 * @author hmb
 * @since 2020-11-03
 */
@Service
public class HrmSalarySlipTemplateOptionServiceImpl extends BaseServiceImpl<HrmSalarySlipTemplateOptionMapper, HrmSalarySlipTemplateOption> implements IHrmSalarySlipTemplateOptionService {

    @Autowired
    private IHrmSalarySlipTemplateService salarySlipTemplateService;

    @Autowired
    private IHrmSalaryOptionService salaryOptionService;


    @Override
    public List<HrmSalarySlipTemplateOption> queryTemplateOptionByTemplateId(Long templateId) {
        HrmSalarySlipTemplate template = salarySlipTemplateService.getById(templateId);
        List<HrmSalaryOption> optionList2 = salaryOptionService.lambdaQuery().between(HrmSalaryOption::getParentCode, 10, 90).list();
        if (CollectionUtil.isNotEmpty(optionList2)) {
            for (HrmSalaryOption option : optionList2) {
                //添加语言包key
                Map<String, String> keyMap = new HashMap<>();
                keyMap.put("name_resourceKey", "hrm." + option.getCode());
                keyMap.put("remarks_resourceKey", "hrm." + option.getCode() + option.getRemarks());
                option.setLanguageKeyMap(keyMap);
            }
        }
        if (template.getDefaultOption() == 1) {
            List<HrmSalarySlipTemplateOption> list = new ArrayList<>();
            HrmSalarySlipTemplateOption salarySlipTemplateOption1 = new HrmSalarySlipTemplateOption();
            salarySlipTemplateOption1.setTemplateId(templateId);
            salarySlipTemplateOption1.setName("基本项");
            salarySlipTemplateOption1.setType(1);
            salarySlipTemplateOption1.setCode(0);
            salarySlipTemplateOption1.setPid(0L);
            salarySlipTemplateOption1.setSort(1);
            salarySlipTemplateOption1.setIsHide(0);
            Map<String, String> keyMap1 = new HashMap<>();
            keyMap1.put("name_resourcekey", HrmLanguageEnum.SALARY_SLIP_OPTION_BASE.getKey());
            salarySlipTemplateOption1.setLanguageKeyMap(keyMap1);
            HrmSalarySlipTemplateOption salarySlipTemplateOption2 = new HrmSalarySlipTemplateOption();
            salarySlipTemplateOption2.setTemplateId(templateId);
            salarySlipTemplateOption2.setName("明细项");
            salarySlipTemplateOption2.setType(1);
            salarySlipTemplateOption2.setCode(0);
            salarySlipTemplateOption2.setPid(0L);
            salarySlipTemplateOption2.setSort(2);
            salarySlipTemplateOption2.setIsHide(0);
            Map<String, String> keyMap2 = new HashMap<>();
            keyMap2.put("name_resourcekey", HrmLanguageEnum.SALARY_SLIP_OPTION_DETAIL.getKey());
            salarySlipTemplateOption2.setLanguageKeyMap(keyMap2);
            //基础项
            List<HrmSalarySlipTemplateOption> templateOptions1 = new ArrayList<>();
            //明细项
            List<HrmSalarySlipTemplateOption> templateOptions2 = new ArrayList<>();
            List<HrmSalaryOption> optionList1 = salaryOptionService.lambdaQuery().in(HrmSalaryOption::getCode, 210101, 230101, 240101).list();
            if (CollectionUtil.isNotEmpty(optionList1)) {
                for (HrmSalaryOption option : optionList1) {
                    //添加语言包key
                    Map<String, String> keyMap = new HashMap<>();
                    keyMap.put("name_resourceKey", "hrm." + option.getCode());
                    keyMap.put("remarks_resourceKey", "hrm." + option.getCode() + option.getRemarks());
                    option.setLanguageKeyMap(keyMap);
                }
            }
            for (int i = 0; i < optionList1.size(); i++) {
                HrmSalaryOption option = optionList1.get(i);
                HrmSalarySlipTemplateOption templateOption = new HrmSalarySlipTemplateOption();
                templateOption.setTemplateId(templateId);
                templateOption.setName(option.getName());
                templateOption.setType(2);
                templateOption.setCode(option.getCode());
                templateOption.setIsHide(0);
                templateOption.setSort(i + 1);
                templateOption.setLanguageKeyMap(option.getLanguageKeyMap());
                templateOptions1.add(templateOption);
            }
            for (int i = 0; i < optionList2.size(); i++) {
                HrmSalaryOption option = optionList2.get(i);
                HrmSalarySlipTemplateOption templateOption = new HrmSalarySlipTemplateOption();
                templateOption.setTemplateId(templateId);
                templateOption.setName(option.getName());
                templateOption.setType(2);
                templateOption.setCode(option.getCode());
                templateOption.setIsHide(0);
                templateOption.setSort(i + 1);
                templateOption.setLanguageKeyMap(option.getLanguageKeyMap());
                templateOptions2.add(templateOption);
            }
            salarySlipTemplateOption1.setOptionList(templateOptions1);
            salarySlipTemplateOption2.setOptionList(templateOptions2);
            list.add(salarySlipTemplateOption1);
            list.add(salarySlipTemplateOption2);
            return list;
        } else {
            List<HrmSalarySlipTemplateOption> list = lambdaQuery().eq(HrmSalarySlipTemplateOption::getTemplateId, templateId)
                    .orderByAsc(HrmSalarySlipTemplateOption::getSort).list();
            List<HrmSalarySlipTemplateOption> options = list.stream().filter(option -> option.getType() == 2).collect(Collectors.toList());
            List<Integer> codes = options.stream().map(HrmSalarySlipTemplateOption::getCode).collect(Collectors.toList());
            int i = 0;
            List<HrmSalarySlipTemplateOption> newTemplateOptions = optionList2.stream().filter(option -> !codes.contains(option.getCode())).map(option -> {
                HrmSalarySlipTemplateOption templateOption = new HrmSalarySlipTemplateOption();
                templateOption.setTemplateId(templateId);
                templateOption.setName(option.getName());
                templateOption.setType(2);
                templateOption.setCode(option.getCode());
                templateOption.setIsHide(0);
                templateOption.setSort(i + 1);
                templateOption.setLanguageKeyMap(option.getLanguageKeyMap());
                return templateOption;
            }).collect(Collectors.toList());
            Map<Long, List<HrmSalarySlipTemplateOption>> optionListMap = list.stream().filter(option -> option.getPid() != 0).collect(Collectors.groupingBy(HrmSalarySlipTemplateOption::getPid));
            List<HrmSalarySlipTemplateOption> collect = list.stream().filter(option -> option.getPid() == 0).peek(option -> option.setOptionList(optionListMap.get(option.getId()))).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(newTemplateOptions)) {
                HrmSalarySlipTemplateOption newTemplateOption = new HrmSalarySlipTemplateOption();
                newTemplateOption.setTemplateId(templateId);
                newTemplateOption.setName("未分类科目");
                newTemplateOption.setType(1);
                newTemplateOption.setCode(0);
                newTemplateOption.setPid(0L);
                newTemplateOption.setIsHide(0);
                Map<String, String> keyMap = new HashMap<>();
                keyMap.put("name_resourcekey", HrmLanguageEnum.SALARY_SLIP_OPTION_UNI.getKey());
                newTemplateOption.setLanguageKeyMap(keyMap);
                newTemplateOption.setOptionList(newTemplateOptions);
                collect.add(newTemplateOption);
            }
            return collect;
        }
    }
}
