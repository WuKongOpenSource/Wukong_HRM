package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.entity.BO.SetChangeTemplateBO;
import com.kakarote.hrm.entity.PO.HrmSalaryChangeTemplate;
import com.kakarote.hrm.entity.VO.ChangeSalaryOptionVO;
import com.kakarote.hrm.entity.VO.QueryChangeTemplateListVO;
import com.kakarote.hrm.mapper.HrmSalaryChangeTemplateMapper;
import com.kakarote.hrm.service.IHrmSalaryChangeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 调薪模板 服务实现类
 * </p>
 *
 * @author hmb
 * @since 2020-11-05
 */
@Service
public class HrmSalaryChangeTemplateServiceImpl extends BaseServiceImpl<HrmSalaryChangeTemplateMapper, HrmSalaryChangeTemplate> implements IHrmSalaryChangeTemplateService {


    @Autowired
    private HrmSalaryChangeTemplateMapper salaryChangeTemplateMapper;

    @Override
    public List<ChangeSalaryOptionVO> queryChangeSalaryOption() {
        List<ChangeSalaryOptionVO> optionVOS = salaryChangeTemplateMapper.queryChangeSalaryOption();
        if (CollectionUtil.isNotEmpty(optionVOS)) {
            for (ChangeSalaryOptionVO optionVO : optionVOS) {
                //添加语言包key
                optionVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.", StrUtil.toString(optionVO.getCode())));
            }
        }
        return optionVOS;
    }

    @Override
    public void setChangeTemplate(SetChangeTemplateBO setChangeTemplateBO) {
        HrmSalaryChangeTemplate salaryChangeTemplate = new HrmSalaryChangeTemplate();
        salaryChangeTemplate.setId(setChangeTemplateBO.getId());
        salaryChangeTemplate.setTemplateName(setChangeTemplateBO.getTemplateName());
        salaryChangeTemplate.setValue(JSON.toJSONString(setChangeTemplateBO.getValue()));
        saveOrUpdate(salaryChangeTemplate);
    }

    @Override
    public List<QueryChangeTemplateListVO> queryChangeTemplateList() {
        List<HrmSalaryChangeTemplate> list = lambdaQuery().list();
        return list.stream().map(template -> {
            QueryChangeTemplateListVO changeTemplateListVO = new QueryChangeTemplateListVO();
            changeTemplateListVO.setId(template.getId());
            changeTemplateListVO.setTemplateName(template.getTemplateName());
            changeTemplateListVO.setIsDefault(template.getIsDefault());
            List<ChangeSalaryOptionVO> salaryOptionVOS = JSON.parseArray(template.getValue(), ChangeSalaryOptionVO.class);
            if (CollectionUtil.isNotEmpty(salaryOptionVOS)) {
                for (ChangeSalaryOptionVO optionVO : salaryOptionVOS) {
                    //添加语言包key
                    optionVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.", StrUtil.toString(optionVO.getCode())));
                }
            }
            changeTemplateListVO.setValue(salaryOptionVOS);
            //添加语言包key
            changeTemplateListVO.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("templateName_resourceKey", "hrm.templateName.", template.getTemplateName()));
            return changeTemplateListVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteChangeTemplate(Long id) {
        HrmSalaryChangeTemplate template = getById(id);
        if (template.getIsDefault() == 1) {
            throw new CrmException(HrmCodeEnum.DEFAULT_TEMPLATE_CANNOT_BE_DELETED);
        }
        removeById(id);
    }
}
