package com.kakarote.core.feign.admin.service;

import com.kakarote.core.common.Result;
import com.kakarote.core.entity.MsgBodyBO;
import com.kakarote.core.feign.admin.entity.AdminMessage;
import com.kakarote.core.feign.admin.entity.AdminMessageBO;
import com.kakarote.core.feign.admin.entity.ExternalContactSend;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@FeignClient(name = "admin", contextId = "message")
public interface AdminMessageService {

    /**
     * 保存adminMessage
     *
     * @param adminMessage:系统消息表
     * @return 保存adminMessage
     */
    @PostMapping("/adminMessage/save")
    public Result<AdminMessage> save(@RequestBody AdminMessage adminMessage);

    /**
     * 更新系统消息
     *
     * @param adminMessage:系统消息
     * @return adminMessage
     */
    @PostMapping("/adminMessage/update")
    public Result<AdminMessage> update(@RequestBody AdminMessage adminMessage);

    /**
     * 根据消息id获取系统消息表
     *
     * @param messageId:消息Id
     * @return 系统消息表
     */
    @PostMapping("/adminMessage/getById/{messageId}")
    public Result<AdminMessage> getById(@PathVariable("messageId") Long messageId);

    /**
     * 保存消息业务
     *
     * @param adminMessageBO:消息业务对象
     * @return 消息业务对象
     */
    @PostMapping("/adminMessage/sendMessage")
    public Result<AdminMessage> sendMessage(@RequestBody AdminMessageBO adminMessageBO);


    /**
     * 发送消息(企业微信外部联系人新增时触发)
     * @param externalContactSend
     * @return
     */
    @PostMapping("/adminMessage/sendWxWorkExternalContactSend")
    public Result sendWxWorkExternalContactSend(@RequestBody ExternalContactSend externalContactSend);

    /**
     * 根据事件id删除事件
     *
     * @param eventId:事件id
     * @return data
     */
    @PostMapping("/adminMessage/deleteEventMessage")
    public Result deleteEventMessage(@RequestParam("eventId") Integer eventId);

    /**
     * 根据label删除label
     *
     * @param label:标签
     * @return data
     */
    @PostMapping("/adminMessage/deleteByLabel")
    public Result deleteByLabel(@RequestParam("label") Integer label);

    /**
     * 发送信息进入MQ
     *
     * @param msgBodyBO:消息发送求送
     * @return data
     */
    @PostMapping("/adminMessage/sendMQMsg")
    public Result sendMQMsg(@RequestBody MsgBodyBO msgBodyBO);

    /**
     * 上传企业微信导出文件
     *
     * @param file file
     * @return data
     */
    @PostMapping(value = "/adminMessage/sendWxExportMessage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result sendWxExportMessage(@RequestPart("file") File file, @RequestParam("fileName") String fileName);
}
