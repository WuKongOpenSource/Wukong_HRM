package com.kakarote.core.feign.crm.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.AdminCompany;
import com.kakarote.core.feign.bi.entity.BiFieldVO;
import com.kakarote.core.feign.crm.entity.*;
import com.kakarote.core.feign.oa.entity.ExamineVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "crm", contextId = "")
public interface CrmService {

    /**
     * 查询客户信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmCustomer/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryCustomerInfo(@RequestBody Collection ids);

    /**
     * 查询客户名
     * @param customerId:客户id
     * @return data
    */
	@PostMapping("/crmCustomer/queryCustomerName")
	Result<String> queryCustomerName(@RequestParam("customerId") Long customerId);

	/**
	 * 查询账单信息
	 * @param ids
	 * @return SimpleCrmEntity
	*/
    @PostMapping("/crmInvoice/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryInvoiceInfo(@RequestBody Collection ids);

    /**
     * 查询应收账单信息
     * @param ids
     * @return
    */
    @PostMapping("/crmReceivables/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryReceivablesInfo(@RequestBody Collection ids);

    /**
     * 查询返回访问信息
     * @param ids
     * @return
    */
    @PostMapping("/crmReturnVisit/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryReturnVisitInfo(@RequestBody Collection ids);


    /**
     * 查询联系人信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmContacts/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryContactsInfo(@RequestBody Collection ids);

    /**
     * 查询产品信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmProduct/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryProductInfo(@RequestBody Collection ids);

    /**
     * 查询线索信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmLeads/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryLeadsInfo(@RequestBody Collection ids);

    /**
     * @Description  查询商机可用阶段组
     * @Author UNIQUE
     * @Date 2022/11/15
     * @Param
     * @return
     **/
    @PostMapping("/crmBusiness/queryCrmFlowSearchFieldInfo")
    public Result<List<Object>> queryCrmFlowSearchFieldInfo(@RequestParam("label") Integer label);


    /**
     * 查询商机信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmBusiness/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryBusinessInfo(@RequestBody Collection ids);

    /**
     * 查询合同信息
     *
     * @param ids ids
     * @return entity
     */
    @PostMapping("/crmContract/querySimpleEntity")
    public Result<List<SimpleCrmEntity>> queryContractInfo(@RequestBody Collection ids);

    /**
     * 添加活动记录
     *
     * @param type
     * @param activityType   活动类型
     * @param activityTypeId 类型ID
     * @return
     */
    @PostMapping("/crmActivity/addActivity")
    Result addActivity(@RequestParam("type") Integer type, @RequestParam("activityType") Integer activityType, @RequestParam("activityTypeId") Long activityTypeId);

    /**
     * 添加活动记录 并反推关联数据
     * @param crmActivityBO:活动记录保存
     * @return data
     */
    @PostMapping("/crmActivity/addRelationActivity")
    Result addRelationActivity(@RequestBody CrmActivityBO crmActivityBO);


    /**
     * 批量更新es
     * @param id:数据id
     * @param name:数据名
     * @param type:数据类型
     * @return
    */
    @PostMapping(value = "/crmField/batchUpdateEsData")
    Result batchUpdateEsData(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("type")String type);

    /**
     * 查询开启的公海
     * @param
     * @return
    */
    @PostMapping("/crmCustomerJob/queryOpenPoolCompany")
    Result<List<AdminCompany>> queryOpenPoolCompany();

    /**
     * 更新应收账单计划
     * @param
     * @return 
    */
    @PostMapping("/crmCustomerJob/updateReceivablesPlan")
    Result updateReceivablesPlan();


    /**
     * 客户流入公海
     * @param
     * @return
    */
    @PostMapping("/crmCustomerJob/putInInternational")
    Result<Integer> putInInternational(@RequestParam("companyId") Long companyId);


    /**
     * 根据权限查询公海
     * @param
     * @return
    */
    @PostMapping("/crmCustomerPool/queryPoolNameListByAuth")
    Result<List> queryPoolNameListByAuth() ;


    /**
     * 查询审批字段
     * @param label:标签
     * @return 
    */
    @PostMapping("/crmField/queryExamineField")
    public Result<List<ExamineField>> queryExamineField(@RequestParam("label") Integer label);

    /**
     * 查询列表页数据
     * @param search:高级筛选表
     * @return
    */
    @PostMapping("/crmCustomer/queryPageList")
    @ApiOperation("查询列表页数据")
    Result<BasePage<Map<String, Object>>> queryCustomerPageList(@RequestBody CrmSearchBO search);

    /**
     * 待审核发票
     * @param crmBackLogBO:待办事项模块查询BO
     * @return data
    */
    @PostMapping("/crmBackLog/checkInvoice")
    @ApiOperation("待审核发票")
    public Result<BasePage<Map<String, Object>>> checkInvoice(@RequestBody CrmBackLogBO crmBackLogBO);

    /**
     * 待审核回款
     * @param crmBackLogBO:待办事项模块查询BO
     * @return data
    */
    @PostMapping("/crmBackLog/checkReceivables")
    @ApiOperation("待审核回款")
    public Result<BasePage<Map<String, Object>>> checkReceivables(@RequestBody CrmBackLogBO crmBackLogBO);

    /**
     * 待审核合同
     * @param crmBackLogBO:待办事项模块查询BO
     * @return data
    */
    @PostMapping("/crmBackLog/checkContract")
    @ApiOperation("待审核合同")
    public Result<BasePage<Map<String, Object>>> checkContract(@RequestBody CrmBackLogBO crmBackLogBO);

    /**
     * 待审核办公审批
     * @param crmBackLogBO:待办事项模块查询BO
     * @return data
    */
    @PostMapping("/crmBackLog/checkOa")
    @ApiOperation("待审核办公审批")
    public Result<BasePage<ExamineVO>> checkOa(@RequestBody CrmBackLogBO crmBackLogBO);


    /**
     * 下载软呼文件
     * @return
     */
    @PostMapping("/crmCall/callFileTask")
    Result callFileTask();

    @ApiOperation("查询所有字段语言包key信息")
    @PostMapping(value = "/crmField/getAllFieldLanguageRel")
    public Result<List<Map<String,Object>>> getAllFieldLanguageRel();

    @PostMapping("/crmCustomer/cpAdd")
    @ApiOperation("企业微信同步保存数据")
    public Result<Map<String, Object>> cpAdd(@RequestBody JSONObject jsonObject);

    @PostMapping("/crmCustomer/cpAddOne")
    @ApiOperation("企业微信单个同步保存数据")
    public Result<Map<String, Object>> cpAddOne(@RequestBody JSONObject jsonObject);

    @ApiOperation("供bi使用查询--查询产品类型")
    @PostMapping(value = "/crmProductCategory/queryByIds")
    Result<Map<Long, Object>> crmProductCategoryByIds(@RequestParam List<Long> categoryIds);

    @ApiOperation("供bi使用查询 查询某个label下的字段")
    @PostMapping(value = "/crmBiSearch/queryListHeadForDashboard")
    Result<List<BiFieldVO>> queryListHeadForDashboard(@RequestParam Integer label);


    @PostMapping("/crmCustomerJob/todayContact")
    Result todayContact();

    @PostMapping("/crmCustomerJob/followContact")
    Result followContact();

    /**
     *线索放入线索池
     * @return
     */
    @PostMapping("/crmLeadsJob/putInInternational")
    Result putInLeadsInternational(@RequestParam("companyId") Long companyId);

    /**
     * 查询开启的线索池
     * @param
     * @return
     */
    @PostMapping("/crmLeadsJob/queryOpenLeadsPoolCompany")
    Result<List<AdminCompany>> queryOpenLeadsPoolCompany();

    /**
     * 根据权限查询线索池
     * @param
     * @return
     */
    @PostMapping("/crmLeadsPool/queryPoolNameListByAuth")
    Result<List> queryLeadsPoolNameListByAuth() ;

    @PostMapping("/crmContract/queryById/{contractId}")
    Result<JSONObject> queryContractById(@PathVariable("contractId")  Long contractId);

    @PostMapping("/crmCustomer/addOrUpdateScrm")
    public Result<Map<String, Object>> addOrUpdateScrm(@RequestBody JSONObject object);

    @PostMapping("/crmActivity/addEmailActivity")
    Result addEmailActivity(@RequestBody JSONObject object);

    @PostMapping("/crmActivity/updateEmailActivity")
    Result updateEmailActivity(@RequestBody JSONObject object);

    @PostMapping(value = "/crmField/replaceEmailContent")
    Result<Map<String, String>> replaceEmailContent(@RequestBody JSONObject object);
}
