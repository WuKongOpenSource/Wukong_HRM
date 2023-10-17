package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.feign.admin.entity.AdminMessageBO;
import com.kakarote.hrm.common.admin.AdminMessageEnum;
import com.kakarote.hrm.entity.BO.AdminMessageContentBO;
import com.kakarote.hrm.entity.BO.AdminMessageQueryBO;
import com.kakarote.hrm.entity.PO.AdminMessage;
import com.kakarote.hrm.entity.VO.AdminMessageVO;
import com.kakarote.hrm.mapper.AdminMessageMapper;
import com.kakarote.hrm.service.IAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 系统消息表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@Service
public class AdminMessageServiceImpl extends BaseServiceImpl<AdminMessageMapper, AdminMessage> implements IAdminMessageService {


    @Autowired
    private Redis redis;

    /**
     * 新增或修改消息
     *
     * @param message message
     */
    @Override
    public Long saveOrUpdateMessage(AdminMessage message) {
        AdminMessage adminMessage = BeanUtil.copyProperties(message, AdminMessage.class);
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        saveOrUpdate(adminMessage);
        return adminMessage.getMessageId();
    }

    /**
     * 查询消息列表
     *
     * @param adminMessageBO 搜索对象
     * @return data
     */
    @Override
    public BasePage<AdminMessage> queryList(AdminMessageQueryBO adminMessageBO) {
        adminMessageBO.setUserId(UserUtil.getUserId());
        BasePage<AdminMessage> adminMessageBasePage = getBaseMapper().queryList(adminMessageBO.parse(), adminMessageBO);
        if (Arrays.asList(AdminMessageEnum.CRM_CUSTOMER_IMPORT.getType(), AdminMessageEnum.CRM_CONTACTS_IMPORT.getType(), AdminMessageEnum.CRM_LEADS_IMPORT.getType(), AdminMessageEnum.CRM_PRODUCT_IMPORT.getType()).contains(adminMessageBO.getType())) {
            adminMessageBasePage.getList().forEach(data -> {
//                List<String> splitTrim = StrUtil.splitTrim(data.getContent(), Const.SEPARATOR);
//                data.setContent(splitTrim.size() > 0 ? splitTrim.get(0) : "0");
//                data.setContent(splitTrim.get(0)+","+splitTrim.get(1));
                if (StrUtil.isEmpty(data.getTitle())) {
                    data.setTitle("0");
                }
                Map<String, String> keyMap = new HashMap<>();
                if (!AdminMessageEnum.parse(data.getTitle()).equals(AdminMessageEnum.NULL)) {
                    keyMap.put("tittle_resourceKey", "admin.config.message." + data.getTitle());
                }
                data.setLanguageKeyMap(keyMap);
            });
        }
        return adminMessageBasePage;
    }

    /**
     * 查询未读消息数量
     *
     * @return data
     */
    @Override
    public AdminMessageVO queryUnreadCount() {
        return getBaseMapper().queryUnreadCount(UserUtil.getUserId());
    }

    /**
     * 新增消息
     *
     * @param adminMessageBO message
     */
    @Override
    public void addMessage(AdminMessageBO adminMessageBO) {
        if (adminMessageBO.getIds().size() == 0) {
            return;
        }
        AdminMessageEnum messageEnum = AdminMessageEnum.parse(adminMessageBO.getMessageType());
        switch (messageEnum) {
            case NULL:
                break;
            case OA_TASK_ALLOCATION:
            case OA_TASK_JOIN:
            case OA_TASK_OVER:
                addOaTaskMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), adminMessageBO.getContent());
                break;
            case OA_LOG_SEND:
                addOaLogSendMessage(messageEnum, adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), adminMessageBO.getContent());
                break;
            case OA_LOG_FAVOUR:
            case OA_LOG_REPLY:
            case OA_COMMENT_REPLY:
                addOaLogReplyMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getContent(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case OA_TASH_REPLY:
            case OA_TASK_COMMENT_REPLY:
            case PROJECT_COMMENT_REPLY:
            case PROJECT_REPLY:
            case CRM_LEADS_COMMENT_REPLY:
            case CRM_LEADS_REPLY:
            case CRM_CUSTOMER_COMMENT_REPLY:
            case CRM_CONTRACT_REPLY:
            case CRM_CONTRACT_COMMENT_REPLY:
            case CRM_BUSINESS_REPLY:
            case CRM_BUSINESS_COMMENT_REPLY:
            case CRM_CONTACTS_REPLY:
            case CRM_CONTACTS_COMMENT_REPLY:
            case CRM_CREATEUSER_CONTRACT_REPLY:
            case CRM_CUSTOMER_REPLY:
                addWorkTaskMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), adminMessageBO.getContent());
                break;
            case OA_UNDERWAY_EXAMINE_REJECT:
            case OA_UNDERWAY_EXAMINE_RESTORE:
            case OA_EXAMINE_RESTORE:
            case OA_EXAMINE_REJECT:
            case OA_EXAMINE_PASS:
                addOaLogExamineMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getContent(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getUserEmail(), adminMessageBO.getIds());
                break;
            case OA_EXAMINE_COPY:
                addOaLogExamineMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getContent(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case OA_NOTICE_MESSAGE:
                addOaNoticeMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case OA_EVENT_MESSAGE:
                addOaEventMessage(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case CRM_FLOW_PASS:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case CRM_FLOW_REJECT:
            case CRM_FLOW_RESTORE:
            case CRM_UNDERWAY_FLOW_REJECT:
            case CRM_UNDERWAY_FLOW_RESTORE:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case CRM_CONTRACT_PASS:
            case CRM_CONTRACT_REJECT:
            case CRM_CONTRACT_RESTORE:
            case CRM_UNDERWAY_CONTRACT_REJECT:
            case CRM_UNDERWAY_CONTRACT_RESTORE:
            case CRM_RECEIVABLES_PASS:
            case CRM_RECEIVABLES_REJECT:
            case CRM_RECEIVABLES_RESTORE:
            case CRM_UNDERWAY_RECEIVABLES_REJECT:
            case CRM_UNDERWAY_RECEIVABLES_RESTORE:
            case CRM_INVOICE_PASS:
            case CRM_INVOICE_REJECT:
            case CRM_INVOICE_RESTORE:
            case CRM_UNDERWAY_INVOICE_REJECT:
            case CRM_UNDERWAY_INVOICE_RESTORE:
            case CRM_CUSTOMER_TEAM_WAIT:
            case CRM_CUSTOMER_TEAM_REJECT:
            case CRM_CUSTOMER_TEAM_PASS:
            case CRM_CUSTOMER_TEAM:
                addCrmExamineMessage(messageEnum, adminMessageBO.getContent(), adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), adminMessageBO.getExamineReason());
                break;
            case CRM_CUSTOMER_IMPORT:
                break;
            case CRM_CUSTOMER_IMPORT_CANCEL:
                break;
            case CRM_CONTACTS_IMPORT:
                break;
            case CRM_CONTACTS_IMPORT_CANCEL:
                break;
            case CRM_LEADS_IMPORT:
                break;
            case CRM_LEADS_IMPORT_CANCEL:
                break;
            case CRM_PRODUCT_IMPORT:
                break;
            case CRM_PRODUCT_IMPORT_CANCEL:
                break;
            case CRM_BUSINESS_USER:
            case CRM_CONTRACT_USER:
            case CRM_CUSTOMER_USER:
            case CRM_BUSINESS_TEAM_EXIT:
            case CRM_CUSTOMER_TEAM_EXIT:
            case CRM_CONTRACT_TEAM_EXIT:
            case CRM_BUSINESS_REMOVE_TEAM:
            case CRM_CUSTOMER_REMOVE_TEAM:
            case CRM_CONTRACT_REMOVE_TEAM:
                addCrmTeamMessage(messageEnum, adminMessageBO.getTypeId(), adminMessageBO.getTitle(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case OA_EXAMINE_NOTICE:
                addOaExamineNotice(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case CRM_CONTRACT_EXAMINE:
            case CRM_RECEIVABLES_EXAMINE:
            case CRM_INVOICE_EXAMINE:
            case CRM_FLOW_EXPORT:
                addCrmExamineNotice(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case PROJECT_ADD_USER_NOTICE:
                break;
            case PROJECT_EDIT_MATTERS_USER_NOTICE:
                break;
            case PROJECT_CREATE_NOTICE:
                break;
            case PROJECT_EVENT_DETAIL_NOTICE:
                break;
            case PROJECT_EVENT_STATUS_NOTICE:
                break;
            case PROJECT_REMOVE_USER_NOTICE:
                break;
            case KM_DOCUMENT_SHARE_OPEN:
                addKmDocumentShareNotice(messageEnum, adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds());
                break;
            case JXC_PURCHASE_EXAMINE:
            case JXC_RETREAT_EXAMINE:
            case JXC_SALE_EXAMINE:
            case JXC_SALE_RETURN_EXAMINE:
            case JXC_PAYMENT_EXAMINE:
            case JXC_COLLECTION_EXAMINE:
            case JXC_INVENTORY_EXAMINE:
            case JXC_ALLOCATION_EXAMINE:
            case HRM_EMPLOYEE_SALARY_EXAMINE:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case HRM_EMPLOYEE_IMPORT:
                break;
            case HRM_SEND_SLIP:
                break;
            case HRM_FIX_SALARY_IMPORT:
                break;
            case HRM_CHANGE_SALARY_IMPORT:
                break;
            case HRM_WRITE_ARCHIVES:
                break;
            case JXC_PURCHASE_PASS:
            case JXC_RETREAT_REJECT:
            case JXC_RETREAT_PASS:
            case JXC_SALE_REJECT:
            case JXC_SALE_PASS:
            case JXC_SALE_RETURN_REJECT:
            case JXC_SALE_RETURN_PASS:
            case JXC_PAYMENT_REJECT:
            case JXC_PAYMENT_PASS:
            case JXC_COLLECTION_REJECT:
            case JXC_COLLECTION_PASS:
            case JXC_INVENTORY_REJECT:
            case JXC_INVENTORY_PASS:
            case JXC_ALLOCATION_REJECT:
            case JXC_ALLOCATION_PASS:
            case HRM_EMPLOYEE_SALARY_PASS:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case HRM_EMPLOYEE_SALARY_REJECT:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case CRM_CONTRACT_COPY:
                addCrmExamineMessage(messageEnum, adminMessageBO.getContent(), adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), null);
                break;
            case CRM_RECEIVABLES_COPY:
                addCrmExamineMessage(messageEnum, adminMessageBO.getContent(), adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), null);
                break;
            case CRM_INVOICE_COPY:
                addCrmExamineMessage(messageEnum, adminMessageBO.getContent(), adminMessageBO.getTitle(), adminMessageBO.getTypeId(), adminMessageBO.getUserId(), adminMessageBO.getIds(), null);
                break;
            case HRM_SALARY_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_PURCHASE_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_RETREAT_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_SALE_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_SALE_RETURN_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_PAYMENT_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_COLLECTION_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_INVENTORY_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
            case JXC_ALLOCATION_COPY:
                addExamineMessage(messageEnum, adminMessageBO);
                break;


            default:
                addExamineMessage(messageEnum, adminMessageBO);
                break;
        }

    }

    /**
     * 添加任务消息
     *
     * @author zhangzhiwei
     */
    private void addOaTaskMessage(AdminMessageEnum messageEnum, String name, Long typeId, Long userId, List<Long> ids, String content) {
        List<AdminMessage> messageList = new ArrayList<>();
        ids.forEach(id -> {
//            if (userId.equals(id)) {
//                return;
//            }
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(name);
            adminMessage.setContent(content);
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(typeId);
            messageList.add(adminMessage);
        });
        saveBatch(messageList);
    }

    /**
     * 添加任务评论
     *
     * @author zyy
     */
    private void addWorkTaskMessage(AdminMessageEnum messageEnum, String name, Long typeId, Long userId, List<Long> ids, String content) {
        List<AdminMessage> messageList = new ArrayList<>();
        ids.forEach(id -> {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(name);
            adminMessage.setContent(content);
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(typeId);
            messageList.add(adminMessage);
        });
        saveBatch(messageList);
    }

    /**
     * 添加日志发送消息
     *
     * @author zhangzhiwei
     */
    private void addOaLogSendMessage(AdminMessageEnum messageEnum, Long typeId, Long userId, List<Long> ids, String content) {
        List<AdminMessage> messageList = new ArrayList<>();
        ids.forEach(id -> {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(DateUtil.today());
            adminMessage.setContent(content);
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(typeId);
            messageList.add(adminMessage);
        });
        saveBatch(messageList);
    }

    /**
     * 添加日志回复消息
     *
     * @author zhangzhiwei
     */
    private void addOaLogReplyMessage(AdminMessageEnum messageEnum, String title, String content, Long typeId, Long userId, List<Long> ids) {
        List<AdminMessage> adminMessageList = new ArrayList<>();
        for (Long shareUserId : ids) {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setContent(content);
            adminMessage.setTitle(title);
            adminMessage.setRecipientUser(shareUserId);
            adminMessage.setTypeId(typeId);
            adminMessageList.add(adminMessage);
        }
        saveBatch(adminMessageList);

    }

    /**
     * 添加日志审批
     *
     * @author zhangzhiwei
     */
    private void addOaLogExamineMessage(AdminMessageEnum messageEnum, String title, String content, Long typeId, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setContent(setContent(messageEnum, typeId, content, adminMessage, null));
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        save(adminMessage);
    }

    /**
     * 添加日志审批
     *
     * @author zhangzhiwei
     */
    private void addOaLogExamineMessage(AdminMessageEnum messageEnum, String title, String content, Long typeId, Long userId, String userEmail, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setCreateUserEmail(userEmail);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setContent(setContent(messageEnum, typeId, content, adminMessage, null));
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        save(adminMessage);
    }


    private void addKmDocumentShareNotice(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        List<AdminMessage> adminMessageList = new ArrayList<>();
        for (Long shareUserId : ids) {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(title);
            adminMessage.setRecipientUser(shareUserId);
            adminMessage.setTypeId(typeId);
            adminMessageList.add(adminMessage);
        }
        saveBatch(adminMessageList);
    }

    /**
     * 添加oa待审批提醒
     *
     * @author wyq
     */
    private void addOaExamineNotice(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setTitle(title);
        adminMessage.setContent(setContent(messageEnum, typeId, null, adminMessage, null));
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        save(adminMessage);
    }

    /**
     * 添加OA公告消息
     *
     * @author zhangzhiwei
     */
    private void addOaNoticeMessage(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        List<AdminMessage> messageList = new ArrayList<>();
        ids.forEach(id -> {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(title);
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(typeId);
            messageList.add(adminMessage);
        });
        saveBatch(messageList);
    }

    /**
     * 添加OA日程消息
     *
     * @author zhangzhiwei
     */
    private void addOaEventMessage(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        List<AdminMessage> messageList = new ArrayList<>();
        ids.forEach(id -> {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(userId);
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(title);
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(typeId);
            messageList.add(adminMessage);
        });
        saveBatch(messageList);
    }

    /**
     * 添加CRM审批消息
     *
     * @author zhangzhiwei
     */
    private void addCrmExamineMessage(AdminMessageEnum messageEnum, String content, String title, Long typeId, Long userId, List<Long> ids, String examineReason) {
        if (userId == null) {
            userId = UserUtil.getUserId();
        }
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        adminMessage.setContent(setContent(messageEnum, typeId, content, adminMessage, examineReason));
        save(adminMessage);
    }

    private void addCrmExamineNotice(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        adminMessage.setContent(setContent(messageEnum, typeId, null, adminMessage, null));
        save(adminMessage);
    }

    /**
     * 团队成员提醒
     */
    private void addCrmTeamMessage(AdminMessageEnum messageEnum, Long typeId, String title, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        adminMessage.setTitle(title);
        save(adminMessage);
    }

    /**
     * 添加JXC审批消息
     */
    private void addJXCExamineMessage(AdminMessageEnum messageEnum, String content, String title, Long typeId, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setContent(content);
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        save(adminMessage);
    }

    private void addJXCExamineNotice(AdminMessageEnum messageEnum, String title, Long typeId, Long userId, List<Long> ids) {
        AdminMessage adminMessage = new AdminMessage();
        adminMessage.setCreateTime(LocalDateTimeUtil.now());
        adminMessage.setCreateUser(userId);
        adminMessage.setType(messageEnum.getType());
        adminMessage.setLabel(messageEnum.getLabel());
        adminMessage.setTitle(title);
        adminMessage.setRecipientUser(ids.get(0));
        adminMessage.setTypeId(typeId);
        save(adminMessage);
    }

    /**
     * 添加JXC审批消息
     */
    private void addExamineMessage(AdminMessageEnum messageEnum, AdminMessageBO adminMessageBO) {

        List<AdminMessage> messageList = new ArrayList<>();
        List<Long> ids = adminMessageBO.getIds();
        for (Long id : ids) {
            AdminMessage adminMessage = new AdminMessage();
            adminMessage.setCreateTime(LocalDateTimeUtil.now());
            adminMessage.setCreateUser(adminMessageBO.getUserId() == null ? UserUtil.getUserId() : adminMessageBO.getUserId());
            adminMessage.setCreateUserEmail(adminMessageBO.getUserEmail());
            adminMessage.setType(messageEnum.getType());
            adminMessage.setLabel(messageEnum.getLabel());
            adminMessage.setTitle(adminMessageBO.getTitle());
            adminMessage.setRecipientUser(id);
            adminMessage.setTypeId(adminMessageBO.getTypeId());
            adminMessage.setContent(adminMessageBO.getContent());
            String setContent = setContent(messageEnum, adminMessage.getTypeId(), null, adminMessage, null);
            adminMessage.setContent(setContent);
            messageList.add(adminMessage);
        }


        saveBatch(messageList);
    }

    /**
     * 加工消息列表预览详情数据
     *
     * @param messageEnum 消息类型
     * @param typeId      消息来源数据ID
     * @param refuse      审批拒绝使用：拒绝原因
     * @return
     */
    private String setContent(AdminMessageEnum messageEnum, Long typeId, String refuse, AdminMessage adminMessage, String examineReason) {
        String content;
        AdminMessageContentBO adminMessageContentBO = new AdminMessageContentBO();
        adminMessageContentBO.setRemarks(refuse);
        switch (messageEnum) {

            case CRM_CONTRACT_EXAMINE:
            case CRM_CONTRACT_REJECT:
            case CRM_CONTRACT_RESTORE:
            case CRM_UNDERWAY_CONTRACT_REJECT:
            case CRM_UNDERWAY_CONTRACT_RESTORE:
            case CRM_CONTRACT_PASS:
                break;
            case CRM_INVOICE_EXAMINE:
            case CRM_INVOICE_PASS:
            case CRM_INVOICE_REJECT:
            case CRM_UNDERWAY_INVOICE_REJECT:
            case CRM_UNDERWAY_INVOICE_RESTORE:
                break;
            case CRM_RECEIVABLES_EXAMINE:
            case CRM_RECEIVABLES_PASS:
            case CRM_RECEIVABLES_REJECT:
            case CRM_UNDERWAY_RECEIVABLES_REJECT:
            case CRM_UNDERWAY_RECEIVABLES_RESTORE:
            case CRM_RECEIVABLES_RESTORE:
                break;
            case CRM_FLOW_EXPORT:
            case CRM_FLOW_PASS:
            case CRM_FLOW_REJECT:
            case CRM_UNDERWAY_FLOW_REJECT:
            case CRM_UNDERWAY_FLOW_RESTORE:
            case CRM_FLOW_RESTORE:
                Map<String, Object> flowData = getBaseMapper().queryData(typeId);
                if (flowData == null) {
                    flowData = getBaseMapper().queryScrmData(typeId);
                }
                adminMessageContentBO.setType(flowData.get("label"));
                adminMessageContentBO.setName(flowData.get("flowName").toString());
                adminMessage.setTitle(flowData.get("name").toString());
//                adminMessage.setTypeId(Long.parseLong(flowData.get("typeId").toString()));
                adminMessage.setTypeId(typeId);
                break;
            case CRM_FLOW_COPY:
                Map<String, Object> flowInfo = getBaseMapper().queryData(typeId);
                if (flowInfo == null) {
                    flowInfo = getBaseMapper().queryScrmData(typeId);
                }
                adminMessageContentBO.setType(flowInfo.get("label"));
                adminMessageContentBO.setName(flowInfo.get("flowName").toString());
                adminMessage.setTitle(flowInfo.get("name").toString());
//                adminMessage.setTypeId(Long.parseLong(flowInfo.get("typeId").toString()));
                adminMessage.setTypeId(typeId);
                break;
            // 客户团队成员审批申请
            case CRM_CUSTOMER_TEAM_WAIT:
            case CRM_CUSTOMER_TEAM_REJECT:
            case CRM_CUSTOMER_TEAM_PASS:
            case CRM_CUSTOMER_TEAM:
                break;
            default:
                return adminMessage.getContent();
        }
        content = JSONObject.toJSONString(adminMessageContentBO);
        return content;
    }

    @Override
    public void deleteEventMessage(Integer eventId) {
        lambdaUpdate().eq(AdminMessage::getLabel, AdminMessageEnum.OA_EVENT_MESSAGE.getLabel())
                .eq(AdminMessage::getType, AdminMessageEnum.OA_EVENT_MESSAGE.getType())
                .apply("create_time > now()").eq(AdminMessage::getTypeId, eventId).remove();
    }

    @Override
    public void deleteById(Long messageId) {
        removeById(messageId);
    }

    @Override
    public void deleteByLabel(Integer label) {
        lambdaUpdate().eq(AdminMessage::getLabel, label).remove();
    }

}
