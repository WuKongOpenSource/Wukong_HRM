package com.kakarote.hrm.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.kakarote.common.ApiExplain;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.common.upload.service.FileService;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.cache.AdminCacheKey;
import com.kakarote.core.feign.admin.entity.AdminMessageBO;
import com.kakarote.hrm.entity.BO.AdminMessageQueryBO;
import com.kakarote.hrm.entity.PO.AdminMessage;
import com.kakarote.hrm.entity.VO.AdminMessageVO;
import com.kakarote.hrm.service.IAdminMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * <p>
 * 系统消息表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/adminMessage")
@Api(tags = "系统消息")
public class AdminMessageController {

    @Autowired
    private IAdminMessageService messageService;

    @Autowired
    private Redis redis;

    @Autowired
    private FileService fileService;

    @PostMapping("/save")
    public Result<AdminMessage> save(@RequestBody AdminMessage adminMessage) {
        AdminMessage adminMessage1 = BeanUtil.copyProperties(adminMessage, AdminMessage.class);
        if (adminMessage.getCreateTime() != null) {
            adminMessage1.setCreateTime(adminMessage.getCreateTime());
        }
        if (adminMessage.getCreateUserId() != null) {
            adminMessage1.setCreateUserId(adminMessage.getCreateUserId());
        } else {
            adminMessage1.setCreateUserId(0L);
        }
        messageService.save(adminMessage1);
        return Result.ok(adminMessage1);
    }

    @PostMapping("/update")
    public Result<AdminMessage> update(@RequestBody AdminMessage adminMessage) {
        AdminMessage adminMessage1 = BeanUtil.copyProperties(adminMessage, AdminMessage.class);
        if (adminMessage.getCreateTime() != null) {
            adminMessage1.setCreateTime(adminMessage.getCreateTime());
        }
        messageService.updateById(adminMessage1);
        return Result.ok(adminMessage1);
    }

    @PostMapping("/saveOrUpdateMessage")
    public Result<Long> saveOrUpdateMessage(@RequestBody AdminMessage message) {
        Long messageId = messageService.saveOrUpdateMessage(message);
        return Result.ok(messageId);
    }

    @PostMapping("/queryList")
    @ApiOperation("查询消息列表")
    public Result<BasePage<AdminMessage>> queryList(@RequestBody AdminMessageQueryBO adminMessageBO) {
        BasePage<AdminMessage> adminMessageBasePage = messageService.queryList(adminMessageBO);
        return Result.ok(adminMessageBasePage);
    }

    @PostMapping("/readMessage")
    @ApiOperation("单个标记为已读")
    public Result readMessage(@RequestParam("messageId") Long messageId) {
        AdminMessage byId = messageService.getById(messageId);
        if (byId != null) {
            byId.setIsRead(1);
            messageService.updateById(byId);
        }
        return Result.ok();
    }

    @PostMapping("/readAllMessage")
    @ApiOperation("全部标记为已读")
    public Result readAllMessage(Integer label) {
        LambdaUpdateChainWrapper<AdminMessage> wrapper = messageService.lambdaUpdate();
        wrapper.set(AdminMessage::getIsRead, 1);
        wrapper.eq(AdminMessage::getRecipientUser, UserUtil.getUserId());
        if (label != null) {
            wrapper.eq(AdminMessage::getLabel, label);
        }
        wrapper.update();
        return Result.ok();
    }

    @PostMapping("/clear")
    @ApiOperation("删除已读消息")
    public Result clear(Integer label) {
        LambdaUpdateChainWrapper<AdminMessage> wrapper = messageService.lambdaUpdate();
        wrapper.eq(AdminMessage::getIsRead, 1);
        wrapper.eq(AdminMessage::getRecipientUser, UserUtil.getUserId());
        if (label != null) {
            wrapper.eq(AdminMessage::getLabel, label);
        }
        wrapper.remove();
        return Result.ok();
    }

    @PostMapping("/getById/{messageId}")
    public Result<AdminMessage> getById(@PathVariable("messageId") Long messageId) {
        AdminMessage adminMessage = messageService.getById(messageId);
        return Result.ok(adminMessage);
    }

    @PostMapping("/queryUnreadCount")
    @ApiOperation("查询未读消息")
    public Result<AdminMessageVO> queryUnreadCount() {
        AdminMessageVO messageVO = messageService.queryUnreadCount();
        return Result.ok(messageVO);
    }

    @PostMapping("/queryImportNum")
    @ApiOperation("查询导入数量")
    public Result<Integer> queryImportNum(Long messageId) {
        if (messageId == null) {
            return Result.ok(null);
        }
        boolean exists = redis.exists(AdminCacheKey.UPLOAD_EXCEL_MESSAGE_PREFIX + messageId.toString());
        Integer num = null;
        if (exists) {
            num = redis.get(AdminCacheKey.UPLOAD_EXCEL_MESSAGE_PREFIX + messageId);
        }
        return Result.ok(num);
    }

    @PostMapping("/queryImportInfo")
    @ApiOperation("查询导入信息")
    public Result<JSONObject> queryImportInfo(@RequestParam("messageId") Long messageId) {
        AdminMessage adminMessage = messageService.getById(messageId);
        if (adminMessage != null && adminMessage.getContent() != null) {
            String[] content = adminMessage.getContent().split(",");
            JSONObject r = new JSONObject().
                    fluentPut("totalSize", adminMessage.getTitle()).fluentPut("errSize", content[0]);
            r.put("updateSize", content.length > 1 ? content[1] : 0);
            r.put("skipSize", content.length > 2 ? content[2] : 0);
            return Result.ok(r);
        } else {
            return Result.ok(new JSONObject().fluentPut("totalSize", 0)
                    .fluentPut("errSize", 0).fluentPut("updateSize", 0).fluentPut("skipSize", 0));
        }
    }

    @PostMapping("/downImportError")
    @ApiOperation("下载错误模板")
    public void downImportError(@RequestParam("messageId") Long messageId, HttpServletResponse response) {
        String path = redis.get(AdminCacheKey.UPLOAD_EXCEL_MESSAGE_PREFIX + "file:" + messageId);
        com.kakarote.common.upload.entity.UploadEntity entity = new com.kakarote.common.upload.entity.UploadEntity();
        entity.setFileId(messageId.toString());
        entity.setPath(path);
        try {
            InputStream inputStream = fileService.downFile(entity);
            if (inputStream != null) {
                final String contentType = ObjectUtil.defaultIfNull(FileUtil.getMimeType(path), "application/octet-stream");
                ServletUtil.write(response, inputStream, contentType, path.substring(path.lastIndexOf("/") + 1));
            }
        } catch (Exception e) {
            throw new BusinessException(1102, "数据不存在");
        }
    }

    @PostMapping("/sendMessage")
    @ApiExplain("发送消息")
    public Result sendMessage(@RequestBody AdminMessageBO adminMessageBO) {
        messageService.addMessage(adminMessageBO);
        return Result.ok();
    }

    @PostMapping("/deleteEventMessage")
    @ApiExplain("删除日程消息")
    public Result deleteEventMessage(@RequestParam("eventId") Integer eventId) {
        messageService.deleteEventMessage(eventId);
        return Result.ok();
    }

    @PostMapping("/deleteById/{messageId}")
    @ApiOperation("删除通知")
    public Result deleteById(@PathVariable("messageId") Long messageId) {
        messageService.deleteById(messageId);
        return Result.ok();
    }

    @PostMapping("/deleteByLabel")
    public Result deleteByLabel(@RequestParam("label") Integer label) {
        messageService.deleteByLabel(label);
        return Result.ok();
    }

}

