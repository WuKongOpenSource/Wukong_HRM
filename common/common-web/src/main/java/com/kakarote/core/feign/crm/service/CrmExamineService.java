package com.kakarote.core.feign.crm.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Result;
import com.kakarote.core.feign.crm.entity.SimpleCrmInfo;
import com.kakarote.core.feign.examine.entity.ExamineConditionDataBO;
import com.kakarote.core.feign.examine.entity.ExamineFlowFieldUpdateLogBO;
import com.kakarote.core.feign.examine.entity.ExamineMessageBO;
import com.kakarote.core.feign.examine.entity.ExamineUpdateFieldBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "crm", contextId = "examine")
public interface CrmExamineService {

    /**
     * 查询审批记录
     *
     * @param recordId:记录id
     * @param ownerUserId:上级id
     * @return data
     */
    @PostMapping("/crmExamineRecord/queryExamineRecordList")
    Result<JSONObject> queryExamineRecordList(
            @RequestParam("recordId") Integer recordId,
            @RequestParam("ownerUserId") Long ownerUserId);


    /**
     * 查询data
     *
     * @param examineConditionDataBO:审批状况BO
     * @return data
     */
    @PostMapping("/crmExamineRecord/queryConditionData")
    Result<Map<String, Object>> getDataMapForNewExamine(@RequestBody ExamineConditionDataBO examineConditionDataBO);


    @PostMapping("/crmExamineRecord/queryFieldInfo")
    Result<List<Map<String,Object>>> queryFieldInfo(@RequestParam("label") Integer label, @RequestParam("typeId")   Long typeId);



    /**
     * 修改检查状态根据examineConditionDataBO
     *
     * @param examineConditionDataBO:审批状况BO
     * @return
     */
    @PostMapping("/crmExamineRecord/updateCheckStatusByNewExamine")
    Result<Boolean> updateCheckStatusByNewExamine(@RequestBody ExamineConditionDataBO examineConditionDataBO);

    /**
     * 增加审核通知
     *
     * @param examineMessageBO:审核通知类型
     * @return data
     */
    @PostMapping("/crmExamineRecord/addMessageForNewExamine")
    Result addMessageForNewExamine(@RequestBody ExamineMessageBO examineMessageBO);


    /**
     * 查询SimpleCrmInfo
     *
     * @param examineConditionDataBO:审核状态BO
     * @return SimpleCrmInfo
     */
    @PostMapping("/crmExamineRecord/getCrmSimpleInfo")
    Result<SimpleCrmInfo> getCrmSimpleInfo(@RequestBody ExamineConditionDataBO examineConditionDataBO);

    /**
     * 修改字段信息
     * @param data
     */
    @PostMapping("/crmExamineRecord/updateFieldInfo")
    void updateFieldInfo(@RequestBody ExamineUpdateFieldBO data);

    /**
     * 查询审批字段修改记录
     * @param list
     * @return
     */
    @PostMapping("/crmExamineRecord/queryExamineUpdateLog")
    Result<List<Map<String, Object>>> queryExamineUpdateLog(@RequestBody List<ExamineFlowFieldUpdateLogBO> list);


}