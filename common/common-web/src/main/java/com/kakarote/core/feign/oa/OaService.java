package com.kakarote.core.feign.oa;

import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.AdminCompany;
import com.kakarote.core.feign.crm.entity.ExamineField;
import com.kakarote.core.feign.examine.entity.ExamineConditionDataBO;
import com.kakarote.core.feign.examine.entity.ExamineFlowFieldUpdateLogBO;
import com.kakarote.core.feign.examine.entity.ExamineUpdateFieldBO;
import com.kakarote.core.feign.oa.entity.ExamineVO;
import com.kakarote.core.feign.oa.entity.OaExamine;
import com.kakarote.core.feign.oa.entity.OaExamineVO;
import com.kakarote.core.feign.oa.entity.OaSearchBO;
import com.kakarote.core.feign.oa.impl.OaServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "oa", contextId = "eventJob", fallback = OaServiceImpl.class)
public interface OaService {

    /**
     * 查询时间通知公司
     *
     * @param
     * @return 云平台公司配置表
     */
    @PostMapping("/oaEventJob/queryEventNoticeCompanyList")
    Result<List<AdminCompany>> queryEventNoticeCompanyList();

    /**
     * 事件通知计划
     *
     * @param
     * @return
     */
    @PostMapping("/oaEventJob/eventNoticeCron")
    Result eventNoticeCron(@RequestParam("companyId") Long companyId);

    /**
     * 转换审批
     *
     * @param recordList:记录
     * @return
     */
    @PostMapping("/oaExamine/transfer")
    @ApiOperation("转换审批")
    Result<List<ExamineVO>> transfer(@RequestBody List<ExamineVO> recordList);

    /**
     * 初始化oa数据
     *
     * @param
     * @return
     */
    @PostMapping("/oaLog/initOaData")
    Result<Boolean> initOaData();

    /**
     * 初始化日程数据
     *
     * @param
     * @return
     */
    @PostMapping("/oaLog/initCalendarData")
    Result<Boolean> initCalendarData();

    /**
     * 初始化oa审批数据
     *
     * @param
     * @return
     */
    @PostMapping("/oaLog/initOaExamineData")
    Result<Boolean> initOaExamineData();

    /**
     * 查询审批字段
     *
     * @param categoryId:种类id
     * @return 审批字段对象
     */
    @PostMapping("/oaExamine/queryExamineField")
    Result<List<ExamineField>> queryExamineField(@RequestParam("categoryId") Long categoryId);

    /**
     * 查询审批状态
     *
     * @param examineConditionDataBO:审批状态bo
     * @return data
     */
    @PostMapping("/oaExamine/queryConditionData")
    Result<Map<String, Object>> getDataMapForNewExamine(@RequestBody ExamineConditionDataBO examineConditionDataBO);

    /**
     * 修改审批状态
     *
     * @param examineConditionDataBO:审批状态bo
     * @return
     */
    @PostMapping("/oaExamine/updateCheckStatusByNewExamine")
    Result<Boolean> updateCheckStatusByNewExamine(@RequestBody ExamineConditionDataBO examineConditionDataBO);

    /**
     * 查询审批
     *
     * @param oaExamineId:oa审批id
     * @return
     */
    @PostMapping("/oaExamine/getOaExamineById")
    Result<ExamineVO> getOaExamineById(@RequestParam("oaExamineId") Long oaExamineId);

    /**
     * 保存违约字段
     *
     * @param categoryId:种类id
     * @return data
     */
    @PostMapping("/oaExamineField/saveDefaultField")
    Result saveDefaultField(@RequestParam("categoryId") Long categoryId);

    /**
     * 更改字段种类id
     *
     * @param oldCategoryId:旧种类id
     * @param newCategoryId:新种类id
     * @return
     */
    @PostMapping("/oaExamineField/updateFieldCategoryId")
    Result<Boolean> updateFieldCategoryId(@RequestParam("newCategoryId") Long newCategoryId, @RequestParam("oldCategoryId") Long oldCategoryId);

    /**
     * 更换审批打印模板所属审批id
     *
     * @param oldCategoryId:旧种类id
     * @param newCategoryId:新种类id
     * @return
     */
    @PostMapping("/oaExaminePrintTemplate/updateCategoryId")
    Result<Boolean> updateExaminePrintTemplateCategoryId(@RequestParam("newCategoryId") Long newCategoryId, @RequestParam("oldCategoryId") Long oldCategoryId);

    /**
     * 查询请假审批
     *
     * @param
     * @return
     */
    @PostMapping("/oaExamine/queryLeaveExamineList")
    Result<List<OaExamineVO>> queryLeaveExamineList(@RequestBody Map<String, Object> map);

    /**
     * 查询oa审批信息
     *
     * @param examineId:审批id
     * @return
     */
    @PostMapping("/oaExamine/getOaExamineInfo")
    public Result<ExamineVO> getOaExamineInfo(@RequestParam("examineId") String examineId);


    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/oaExamine/getAllFieldLanguageRel")
    public Result<List<Map<String, Object>>> getAllFieldLanguageRel();


    @ApiOperation("根据事由查询OA审批列表")
    @PostMapping(value = "/oaSuperExamine/queryExamineByName")
    public Result<List<OaExamineVO>> queryExamineByName(@RequestParam String content);

    @ApiOperation("根据事由查询OA审批列表")
    @PostMapping(value = "/oaSuperExamine/queryExamineByNameForPage")
    public Result<BasePage<OaExamine>> queryExamineByNameForPage(@RequestBody OaSearchBO content);

    @ApiOperation("获取指定的审批的关联信息")
    @PostMapping(value = "/oaExamine/getRelationById")
    Result<Map<Integer, Set<Long>>> getRelationById(@RequestParam("oaExamineId") Long oaExamineId);


    @ApiOperation("查询详情或比编辑字段填写信息")
    @PostMapping("/oaExamine/getField")
    Result<List<Map<String,Object>>> getFieldInfo(@RequestBody Map<String, Object> getExamineFieldBO);


    @ApiOperation("查询审批表单字段")
    @PostMapping("/oaExamineField/queryField/{categoryId}")
     Result<List<Map<String,Object>>> getField(@PathVariable("categoryId") Long categoryId,
                                   @RequestParam(value = "type",required = false) String type,
                                   @RequestParam(value = "needHidden",required = false) Boolean needHidden
    );

    @PostMapping("/oaExamine/queryFieldInfo")
    Result<List<Map<String,Object>>> queryFieldInfo(@RequestParam("label") Integer label, @RequestParam("typeId")   Long typeId);

    @PostMapping("/oaExamine/updateFieldInfo")
    void updateFieldInfo(@RequestBody ExamineUpdateFieldBO examineUpdateFieldBO);

    @PostMapping("/oaExamine/queryExamineUpdateLog")
    Result<List<Map<String,Object>>> queryExamineUpdateLog(@RequestBody List<ExamineFlowFieldUpdateLogBO> list);

    @ApiOperation("查询请假类型列表")
    @PostMapping("/oaExamine/queryLeaveTypeList/{fieldId}")
    Result<List<String>> queryLeaveTypeList(@PathVariable("fieldId") Long fieldId);

}
