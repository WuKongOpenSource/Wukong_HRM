package com.kakarote.core.feign.oa.impl;

import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.AdminCompany;
import com.kakarote.core.feign.crm.entity.ExamineField;
import com.kakarote.core.feign.examine.entity.ExamineConditionDataBO;
import com.kakarote.core.feign.examine.entity.ExamineFlowFieldUpdateLogBO;
import com.kakarote.core.feign.examine.entity.ExamineUpdateFieldBO;
import com.kakarote.core.feign.oa.OaService;
import com.kakarote.core.feign.oa.entity.ExamineVO;
import com.kakarote.core.feign.oa.entity.OaExamine;
import com.kakarote.core.feign.oa.entity.OaExamineVO;
import com.kakarote.core.feign.oa.entity.OaSearchBO;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author wwl
 * @date 2022/8/4 8:52
 */
@Component
public class OaServiceImpl implements OaService {


    @Override
    public Result<List<AdminCompany>> queryEventNoticeCompanyList() {
        return null;
    }

    @Override
    public Result eventNoticeCron(Long companyId) {
        return Result.ok();
    }

    @Override
    public Result<List<ExamineVO>> transfer(List<ExamineVO> recordList) {
        return null;
    }

    @Override
    public Result<Boolean> initOaData() {
        return null;
    }

    @Override
    public Result<Boolean> initCalendarData() {
        return null;
    }

    @Override
    public Result<Boolean> initOaExamineData() {
        return null;
    }

    @Override
    public Result<List<ExamineField>> queryExamineField(Long categoryId) {
        return null;
    }

    @Override
    public Result<Map<String, Object>> getDataMapForNewExamine(ExamineConditionDataBO examineConditionDataBO) {
        return Result.ok(new HashMap<>());
    }

    @Override
    public Result<Boolean> updateCheckStatusByNewExamine(ExamineConditionDataBO examineConditionDataBO) {
        return null;
    }

    @Override
    public Result<ExamineVO> getOaExamineById(Long oaExamineId) {
        return null;
    }

    @Override
    public Result saveDefaultField(Long categoryId) {
        return null;
    }

    @Override
    public Result<Boolean> updateFieldCategoryId(Long newCategoryId, Long oldCategoryId) {
        return null;
    }

    @Override
    public Result<Boolean> updateExaminePrintTemplateCategoryId(Long newCategoryId, Long oldCategoryId) {
        return null;
    }

    @Override
    public Result<List<OaExamineVO>> queryLeaveExamineList(Map<String, Object> map) {
        return  Result.ok(new ArrayList<>());
    }

    @Override
    public Result<ExamineVO> getOaExamineInfo(String examineId) {
        return null;
    }

    @Override
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel() {
        return null;
    }

    @Override
    public Result<List<OaExamineVO>> queryExamineByName(String content) {
        return Result.ok(new ArrayList<>());
    }

    @Override
    public Result<BasePage<OaExamine>> queryExamineByNameForPage(OaSearchBO content) {
        return Result.ok(new BasePage<>());
    }

    @Override
    public Result<Map<Integer, Set<Long>>> getRelationById(Long oaExamineId) {
        return null;
    }

    @Override
    public Result<List<Map<String,Object>>> getFieldInfo(Map<String, Object> getExamineFieldBO) {
        return null;
    }

    @Override
    public Result<List<Map<String, Object>>> getField(Long categoryId, String type, Boolean needHidden) {
        return null;
    }

    @Override
    public Result<List<Map<String, Object>>>  queryFieldInfo(Integer label, Long typeId) {
        return null;
    }

    @Override
    public void updateFieldInfo(ExamineUpdateFieldBO examineUpdateFieldBO) {

    }

    @Override
    public Result<List<Map<String, Object>>> queryExamineUpdateLog(List<ExamineFlowFieldUpdateLogBO> list) {
        return Result.ok(new ArrayList<>());
    }
    @Override
    public Result<List<String>> queryLeaveTypeList(Long fieldId) {
        return Result.ok(new ArrayList<>());
    }
}
