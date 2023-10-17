package com.kakarote.common.log.entity;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.core.common.Result;
import com.kakarote.core.common.ResultCode;
import com.kakarote.core.common.enums.SystemCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * 返回数据
 */

@Getter
@Setter
public class OperationResult<T> extends Result<T> {

    private static final long serialVersionUID = 1L;

    private List<OperationLog> operationLogs;

    public OperationResult(ResultCode resultCode) {
        super(resultCode);
    }

    public static <T> OperationResult<T> ok(T data, List<OperationLog> operationLogs) {
        OperationResult<T> result = new OperationResult<>(SystemCodeEnum.SYSTEM_OK);
        result.setData(data);
        if (operationLogs == null) {
            return result;
        }
        operationLogs = operationLogs.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        result.setOperationLogs(operationLogs);
        return result;
    }

    public static <T> OperationResult<T> ok(List<OperationLog> operationLogs) {
        OperationResult<T> result = new OperationResult<>(SystemCodeEnum.SYSTEM_OK);
        if (operationLogs == null) {
            return result;
        }
        operationLogs = operationLogs.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        result.setOperationLogs(operationLogs);
        return result;
    }

    public static <T> OperationResult<T> ok(OperationLog operationLogs) {
        return ok(ListUtil.toList(operationLogs));
    }


    public static <T> OperationResult error(ResultCode resultCode, List<OperationLog> operationLogs) {
        OperationResult<T> result = new OperationResult<>(resultCode);
        result.setOperationLogs(operationLogs);
        return result;
    }
}
