package com.kakarote.hrm.mapper;


import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.AdminMessageQueryBO;
import com.kakarote.hrm.entity.PO.AdminMessage;
import com.kakarote.hrm.entity.VO.AdminMessageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统消息表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface AdminMessageMapper extends BaseMapper<AdminMessage> {

    /**
     * 根据AdminMessage,adminMessageBO查询wk_admin_message表中数据
     * @param parse:系统消息
     * @param adminMessageBO:消息列表查询对象
     * @return AdminMessage
     */
    public BasePage<AdminMessage> queryList(BasePage<AdminMessage> parse, @Param("data") AdminMessageQueryBO adminMessageBO);

    /**
     * 根据用户id查询出未读消息
     * @param userId:用户id
     * @return 消息数量VO
     */
    public AdminMessageVO queryUnreadCount(@Param("userId") Long userId);

    /**
     * 根据合同id查询合同金额、客户id、客户姓名
     * @param typeId:类型id
     * @return data
     */
    Map<String, Object> queryContract(@Param("typeId") Long typeId);

    /**
     * 查询商机
     * @param typeId
     * @return
     */
    Map<String, Object> queryData(@Param("typeId") Long typeId);

    /**
     * 查询商机
     * @param typeId
     * @return
     */
    Map<String, Object> queryScrmData(@Param("typeId") Long typeId);

    /**
     * 根据合同id查询产品id和产品名
     * @param typeId
     * @return data
     */
    List<JSONObject> queryContractProduct(@Param("typeId") Long typeId);

    /**
     * 根据receivables_id查询出客户id、客户姓名、金额、数量、合同id等
     * @param typeId:receivables_id
     * @return data
     */
   Map<String, Object> queryReceivables(@Param("typeId") Long typeId);

    /**
     * 根据invoice_id查询出客户id、客户姓名、合同id、合同名等
     * @param typeId:invoice_id
     * @return data
     */
   Map<String, Object> queryInvoice(@Param("typeId")Long typeId);

    /**
     * 根据customer_id查询出客户id、客户姓名
     * @param typeId:customer_id
     * @return data
     */
    Map<String, Object> queryCustomer(@Param("typeId")Long typeId);
}
