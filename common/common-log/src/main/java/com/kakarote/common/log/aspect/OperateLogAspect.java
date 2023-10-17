package com.kakarote.common.log.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperateLogEntity;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.common.log.service.OperateLogSaveService;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * user注入切面
 */
@Aspect
@Component
@Slf4j
public class OperateLogAspect implements Ordered {

    @Autowired
    private OperateLogSaveService logSaveService;

    @Pointcut("@annotation(com.kakarote.common.log.annotation.OperateLog)")
    public void operateLogAspect() {
    }

    @Around("operateLogAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        OperateLog operateLog = signature.getMethod().getAnnotation(OperateLog.class);

        OperateLogEntity.Entity.Builder logEntity = OperateLogEntity.Entity.newBuilder();


        BehaviorEnum behavior = operateLog.behavior();
        logEntity.setBehavior(behavior.getType());
        logEntity.setApply(operateLog.apply().getType());
        logEntity.setModule(operateLog.object().getType());
        logEntity.setType(operateLog.type().getType());

        Object proceed = point.proceed();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (operateLog.type() == OperateTypeEnum.EXPORT) {
                HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
                if (response != null) {
                    String exportSize = response.getHeader("exportSize");
                    String fileData = response.getHeader("fileData");
                    String name = response.getHeader("name");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fileData", fileData);

                    if (ObjectUtil.isNotEmpty(name)) {
                        jsonObject.put("name", name);
                    } else {
                        jsonObject.put("name", operateLog.object().getRemarks());
                    }
                    logEntity.setOperationObj(jsonObject.toString());
                    logEntity.setOperationInfo(exportSize);
                }
            }

            String ua = request.getHeader("User-Agent");
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setEquipmentType(ua);
            logEntity.setClientIp(ServletUtil.getClientIP(request));
        }
        logEntity.setCreateTime(DateUtil.formatDateTime(new Date()));

        List<OperateLogEntity.Entity.Builder> operateLogEntityList = new ArrayList<>();


        UserInfo info = Optional.ofNullable(UserUtil.getUser()).orElse(new UserInfo().setUserId(0L));
        logEntity.setCreateUserId(Optional.ofNullable(info.getUserId()).orElse(0L));

        if (ObjectUtil.isNotEmpty(proceed)) {
            if (proceed.getClass() == OperationResult.class) {
                OperationResult<?> result = (OperationResult<?>) proceed;
                List<OperationLog> operationLogs = result.getOperationLogs();

                if (!result.getCode().equals(0) || ObjectUtil.isEmpty(operationLogs)) {
                    return proceed;
                }
                for (OperationLog operationLog : operationLogs) {
                    OperateLogEntity.Entity.Builder entity = logEntity.clone();

                    Object operationObject = operationLog.getOperationObject();
                    entity.setOperationObj(operationObject.toString());
                    entity.setOperationInfo(operationLog.getOperationInfo());

                    if (ObjectUtil.isNotEmpty(operationLog.getBehavior())) {
                        entity.setBehavior(operationLog.getBehavior().getType());
                    }
                    if (ObjectUtil.isNotEmpty(operationLog.getApplyObject())) {
                        entity.setModule(operationLog.getApplyObject().getType());
                    }
                    if (ObjectUtil.isNotEmpty(operationLog.getApply())) {
                        entity.setApply(operationLog.getApply().getType());
                    }
                    operateLogEntityList.add(entity);
                }
                ((OperationResult<?>) proceed).setOperationLogs(null);
            }
            if (proceed.getClass() == Result.class) {
                if (!((Result<?>) proceed).getCode().equals(0)) {
                    return proceed;
                }
                logEntity.setOperationObj(parseArgs(point.getArgs()));
                logEntity.setOperationInfo(((Result<?>) proceed).getMsg());
            }
        }
        if (!operateLogEntityList.isEmpty()) {
            for (OperateLogEntity.Entity.Builder entity : operateLogEntityList) {
                logSaveService.saveOperateLog(entity);
            }
        } else {
            logSaveService.saveOperateLog(logEntity);
        }
        if (operateLog.behavior() == BehaviorEnum.DOWNLOAD) {
            return null;
        }
        return proceed;

    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }


    /**
     * 格式化请求传参
     *
     * @param args 原请求参数
     * @return json str
     */
    private String parseArgs(Object[] args) {
        if (args.length == 0) {
            return "";
        }
        JSONArray jsonArray = new JSONArray(args.length);
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                continue;
            } else if (arg instanceof HttpServletResponse) {
                continue;
            } else if (arg instanceof MultipartFile) {
                continue;
            } else if (arg instanceof OutputStream) {
                continue;
            }
            jsonArray.add(arg);
        }
        try {
            return jsonArray.toJSONString();
        } catch (Exception ex) {
            return "";
        }
    }
}