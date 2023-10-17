package com.kakarote.core.feign.jxc.service;


import com.kakarote.core.common.Result;
import com.kakarote.core.feign.crm.entity.ExamineField;
import com.kakarote.core.feign.examine.entity.ExamineFlowFieldUpdateLogBO;
import com.kakarote.core.feign.examine.entity.ExamineUpdateFieldBO;
import com.kakarote.core.feign.jxc.entity.JxcExamine;
import com.kakarote.core.feign.jxc.entity.JxcState;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@FeignClient(name = "jxc", contextId = "jxcExamine")
public interface JxcExamineService {
    /**
     * 审批
     *
     * @param label
     * @param state:状态
     * @param id
     * @return data
     */
    @PostMapping("/jxcExamine/examine")
    Result examine(@RequestParam("label") Integer label, @RequestParam("state") Integer state, @RequestParam("id") Long id);

    /**
     * 审批字段
     *
     * @param label
     * @param id
     * @return
     */
    @PostMapping("/jxcExamine/examineFieldDataMap")
    Result<Map<String, Object>> examineFieldDataMap(@RequestParam("label") Integer label, @RequestParam("id") Long id);

    /**
     * 保存审核信息
     *
     * @param jxcExamine:JxcExamine对象
     * @return
     */
    @PostMapping("/jxcExamine/examineMessage")
    Result examineMessage(@RequestBody JxcExamine jxcExamine);

    /**
     * 根据id查询进销存状态返回
     *
     * @param label:
     * @param id:
     * @return
     */
    @PostMapping("/jxcExamine/queryJxcById")
    Result<JxcState> queryJxcById(@RequestParam("label") Integer label, @RequestParam("id") Long id);

    /**
     * jxc启动
     *
     * @param userId:用户id
     * @param companyId:公司id
     * @return
     */
    @PostMapping("/jxcExamine/startJxc")
    Result startJxc(@RequestParam("userId") Long userId, @RequestParam("companyId") Long companyId);

    /**
     * 初始化jxc数据
     *
     * @param
     * @return
     */
    @PostMapping("/jxcExamine/initJxcData")
    Result<Boolean> initJxcData();

    /**
     * 查询审批字段
     *
     * @param label
     * @return 审批字段对象
     */
    @PostMapping("/jxcExamine/queryExamineField")
    Result<List<ExamineField>> queryExamineField(@RequestParam("label") Integer label);

    @PostMapping("/jxcExamine/updateFieldInfo")
    void updateFieldInfo(@RequestBody ExamineUpdateFieldBO examineUpdateFieldBO);

    @PostMapping("/jxcExamine/queryFieldInfo")
    Result<List<Map<String,Object>>> queryFieldInfo(@RequestParam(value = "label") Integer label,@RequestParam(value = "typeId") Long typeId);

    @PostMapping("/jxcExamine/queryExamineUpdateLog")
    Result<List<Map<String,Object>>> queryExamineUpdateLog(@RequestBody List<ExamineFlowFieldUpdateLogBO> list);


}
