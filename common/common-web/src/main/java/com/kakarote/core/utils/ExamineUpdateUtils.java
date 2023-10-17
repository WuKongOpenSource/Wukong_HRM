package com.kakarote.core.utils;

/**
 * @author: admin
 * @version: v1.0
 * @date:2023/6/7
 */

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批修改字段工具类
 */
public class ExamineUpdateUtils {

    /**
     * 转换审批明细表格数据
     * @param value
     * @return
     */
    private static List<Map<String, Object>> conversionTableValue(Object value) {
        if (value == null) {
            value = new ArrayList<>();
        }
        if (value instanceof String) {
            if ("".equals(value)) {
                value = new ArrayList<>();
            } else {
                value = JSON.parseArray((String) value);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray((List<Object>) value);
        for (int i = 0; i < array.size(); i++) {
            JSONArray objects = array.getJSONArray(i);
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < objects.size(); j++) {
                JSONObject data = objects.getJSONObject(j);
                map.put(data.get("fieldName").toString(), data.get("value"));
                map.put("batchId", data.get("batchId"));
            }
            list.add(map);
        }
        return list;
    }


    /**
     * 处理审批明细表格数据
     * @param value 提交的数据
     * @param initRow 初始化行的数据
     * @param oldValue 旧的数据
     * @return
     */
    public static Object disposeTable(Object value, List<Map<String, Object>> initRow, String oldValue) {
        List<Map<String, Object>> parseNewValue = conversionTableValue(value);
        List<List<Map<String, Object>>> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(parseNewValue)) {
            if (ObjectUtil.isNotEmpty(oldValue)) {
                List<Map<String, Object>> parseOldValue = conversionTableValue(oldValue);
                //获取提交数据的batchId，batchId理解为行Id
                List<String> newBatchIdList = parseNewValue.stream().map(e -> e.get("batchId").toString()).collect(Collectors.toList());

                parseOldValue = parseOldValue.stream().filter(e -> newBatchIdList.contains(e.get("batchId").toString())).collect(Collectors.toList());

                List<String> removeList = new ArrayList<>();
                for (Map<String, Object> oldMap : parseOldValue) {
                    for (Map<String, Object> newMap : parseNewValue) {
                        if (oldMap.get("batchId").equals(newMap.get("batchId"))) {
                            removeList.add(newMap.get("batchId").toString());
                            Set<String> keySet = newMap.keySet();
                            for (String key : keySet) {
                                oldMap.put(key, newMap.get(key));
                            }
                        }
                    }
                }

                parseNewValue.removeIf(e -> removeList.contains(e.get("batchId").toString()));
                parseOldValue.addAll(parseNewValue);
                generateData(initRow, list, parseOldValue);
            } else {
                generateData(initRow, list, parseNewValue);
            }
        }
        return JSON.toJSON(list);

    }


    /**
     * 根据明细表格数据生成标准数据
     * @param initRow
     * @param list
     * @param value
     */
    private static void generateData(List<Map<String, Object>> initRow, List<List<Map<String, Object>>> list, List<Map<String, Object>> value) {
        for (Map<String, Object> row : value) {
            List<Map<String, Object>> newRow = ObjectUtil.cloneByStream(initRow);
            Set<String> keySet = row.keySet();
            for (String key : keySet) {
                for (Map<String, Object> map : newRow) {
                    if (map.get("fieldName").equals(key)) {
                        map.put("value", row.get(key));
                        map.put("batchId", row.get("batchId"));
                    }
                }
            }
            list.add(newRow);
        }
    }
}
