package com.kakarote.hrm.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.core.feign.admin.entity.AdminMessageBO;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.AdminMessageQueryBO;
import com.kakarote.hrm.entity.PO.AdminMessage;
import com.kakarote.hrm.entity.VO.AdminMessageVO;

/**
 * <p>
 * 系统消息表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface IAdminMessageService extends BaseService<AdminMessage> {

    /**
     * 新增或修改消息
     *
     * @param message message
     * @return MessageId
     */
    public Long saveOrUpdateMessage(AdminMessage message);

    /**
     * 查询消息列表
     *
     * @param adminMessageBO 搜索对象
     * @return data
     */
    public BasePage<AdminMessage> queryList(AdminMessageQueryBO adminMessageBO);

    /**
     * 查询未读消息数量
     *
     * @return data
     */
    public AdminMessageVO queryUnreadCount();

    /**
     * 新增消息
     *
     * @param adminMessageBO message
     */
    public void addMessage(AdminMessageBO adminMessageBO);

    /**
     * 删除事件信息
     *
     * @param eventId
     * @return a
     */
    void deleteEventMessage(Integer eventId);

    /**
     * 删除message根据messageid
     *
     * @param messageId
     * @return
     */
    void deleteById(Long messageId);

    /**
     * 根据label删除label
     *
     * @param label
     * @return
     */
    void deleteByLabel(Integer label);

}
