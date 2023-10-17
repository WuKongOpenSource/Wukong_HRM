package com.kakarote.core.exception;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.Result;
import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;


/**
 * @author zhangzhiwei
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {


    @ExceptionHandler(value = Exception.class)
    public Result<String> defaultException(Exception ex) {
        //TODO 默认异常需要处理
        log.error("默认异常需要处理", ex);
        if (ex instanceof ResultCode) {
            return Result.error(((ResultCode) ex).getCode(), ((ResultCode) ex).getMsg());
        }
        return Result.error(SystemCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = MyBatisSystemException.class)
    public Result<String> mybatisException(MyBatisSystemException ex) {
        log.error("默认异常需要处理", ex);
        CrmException crmException = checkCrmException(ex, Const.AUTH_DATA_RECURSION_NUM);
        if (crmException != null) {
            return Result.error(crmException.getCode(), crmException.getMsg());
        }
        return Result.error(SystemCodeEnum.SYSTEM_ERROR);
    }

    private CrmException checkCrmException(Exception ex, Integer dept) {
        --dept;
        if (dept < 0) {
            return null;
        }
        if (ex.getCause() == null || !(ex.getCause() instanceof Exception)) {
            return null;
        }
        if (ex.getCause() instanceof CrmException) {
            return (CrmException) ex.getCause();
        }
        return checkCrmException((Exception) ex.getCause(), dept);
    }

    @ExceptionHandler(value = AuthException.class)
    public Result<String> authException() {
        return Result.noAuth();
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Result<String> methodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("方法请求错误", e);
        return Result.noAuth();
    }

    @ExceptionHandler(value = ServletException.class)
    public Result<String> servletException(ServletException e) {
        /*
          TODO 直接捕获NoHandlerFoundException异常gateway会报错，所以捕获ServletException
         */
        if (e instanceof NoHandlerFoundException) {
            log.error("请求路径未找到:{}", ((NoHandlerFoundException) e).getRequestURL());
            return Result.error(SystemCodeEnum.SYSTEM_NO_FOUND);
        }
        log.error("ServletException:", e);
        return Result.error(SystemCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = NoLoginException.class)
    public Result noLoginException(NoLoginException e) {
        Result<Object> result = Result.error(SystemCodeEnum.SYSTEM_NOT_LOGIN);
        return result;
    }

    @ExceptionHandler(value = BindException.class)
    public Result<String> validException(BindException e) {
        log.error("BindException:", e);
        if (e.getGlobalError() != null) {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID, e.getGlobalError().getDefaultMessage());
        } else if (e.getFieldError() != null) {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID, e.getFieldError().getDefaultMessage());
        } else {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID);
        }
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<String> argumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:", e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.getGlobalError() != null) {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID, bindingResult.getGlobalError().getDefaultMessage());
        } else if (bindingResult.getFieldError() != null) {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID, bindingResult.getFieldError().getDefaultMessage());
        } else {
            return Result.error(SystemCodeEnum.SYSTEM_NO_VALID);
        }
    }
    @ExceptionHandler(value = FeignException.class)
    public Result<String> feignException(FeignException ex) {
        log.error("feign异常：", ex);
        String message = ex.getMessage();
        if (StrUtil.isNotEmpty(message) && ReUtil.contains("[\\u4e00-\\u9fa5]", message)) {
            return Result.error(ex.status(), message);
        } else {
            return Result.error(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }
}
