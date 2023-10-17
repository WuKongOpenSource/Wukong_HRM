package com.kakarote.common.log.entity;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/8/16
 */
@Getter
@Setter
public class OperationLog {
    //操作对象
    private Object operationObject;
    //操作详情
    private String operationInfo;

    private BehaviorEnum behavior = null;

    private OperateObjectEnum applyObject = null;

    private ApplyEnum apply = null;

    public void setOperationObject(Object typeId, Object typeName) {
        if (operationObject == null) {
            this.operationObject = new JSONObject();
        }
        if (operationObject instanceof JSONObject) {
            ((JSONObject) operationObject).put("typeId", String.valueOf(typeId));
            ((JSONObject) operationObject).put("typeName", typeName);
        }

    }

    public void setOperationObject(String key, Object value) {
        if (operationObject == null) {
            operationObject = new JSONObject();
        }
        if (operationObject instanceof JSONObject) {
            ((JSONObject) operationObject).put(key, value);
        }
    }

}