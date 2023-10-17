package com.kakarote.hrm.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.utils.BaseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @company: 卡卡罗特软件科技有限公司
 * @author: zyh
 * @date: 2022/6/16 9:57
 * @version: @Version: V1.0
 */
public class LanguageFieldUtil {

    public static String getFieldName(String fieldName) {
        if (StrUtil.isNotBlank(fieldName)) {
            fieldName = StrUtil.toCamelCase(fieldName);
        }
        if (StrUtil.isNotBlank(fieldName) && fieldName.endsWith("Id")) {
            fieldName = fieldName.substring(0, fieldName.lastIndexOf("Id")).concat("Name");
        }
        if (StrUtil.isNotBlank(fieldName) && fieldName.endsWith("Ids")) {
            fieldName = fieldName.substring(0, fieldName.lastIndexOf("Ids")).concat("Names");
        }
        return fieldName;
    }


    public static Map<String, String> getFieldNameKeyMap(String keyName, String prefix, String fieldName) {
        Map<String, String> keyMap = new HashMap<>();
        fieldName = getFieldName(fieldName);
        keyMap.put(keyName, prefix + fieldName);
        return keyMap;
    }

    public static Map<String, String> getFieldNameKeyMap(String keyName, String prefix, String fieldName, List<?> setting) {
        Map<String, String> keyMap = new HashMap<>();
        fieldName = getFieldName(fieldName);
        keyMap.put(keyName, prefix + fieldName);
        Map<String, String> optionsKeyMap = getOptionsKeyMap(prefix, fieldName, setting);
        if (CollectionUtil.isNotEmpty(optionsKeyMap)) {
            keyMap.putAll(optionsKeyMap);
        }
        return keyMap;
    }

    public static Map<String, String> getOptionsKeyMap(String prefix, String fieldName, List<?> setting) {
        Map<String, String> keyMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(setting)) {
            for (int i = 0; i < setting.size(); i++) {
                String jsonStr = StrUtil.toString(setting.get(i));
                Object name = "";
                if (BaseUtil.isJSON(jsonStr)) {
                    Object object = JSON.parse(jsonStr);
                    if (object instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) object;
                        name = jsonObject.get("name");
                    } else if (object instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) object;
                        name = jsonArray.get(0);
                    } else {
                        name = object;
                    }
                } else {
                    name = jsonStr;
                }

                keyMap.put(StrUtil.toString(i), prefix + fieldName + "Options." + name);
            }
        }
        return keyMap;
    }

    /**
     * 功能描述: <br>
     * 〈人资自定义字段获取多语言key,非自定义字段不适用〉
     *
     * @param: fieldName
     * label
     * @return: java.lang.String
     * @throws:
     * @author: zyh
     * @date: 2022/7/27 14:13
     */
    public static String getFieldNameKey(String fieldName, Integer label) {
        fieldName = getFieldName(fieldName);
        return "customField.hrmField." + fieldName;
    }


    public static Map<String, String> getOptionsKeyNameMap(List<?> setting, String fieldName, Integer label) {
        Map<String, String> keyMap = new HashMap<>();
        if ("flowName".equals(fieldName)) {
            return keyMap;
        }
        String nameKey = getFieldNameKey(fieldName, label);
        if (CollectionUtil.isNotEmpty(setting)) {
            for (Object o : setting) {
                String jsonStr = StrUtil.toString(o);
                Object name = "";
                if (BaseUtil.isJSON(jsonStr)) {
                    Object object = JSON.parse(jsonStr);
                    if (object instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) object;
                        if (jsonObject.containsKey("name")) {
                            name = jsonObject.get("name");
                        }
                        if (jsonObject.containsKey("type")) {
                            name = jsonObject.get("value");
                        }
                        if (jsonObject.containsKey("startNumber")) {
                            name = jsonObject.get("startNumber");
                        }
                    } else if (object instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) object;
                        name = jsonArray.get(0);
                    } else {
                        name = object;
                    }
                } else {
                    name = jsonStr;
                }
                keyMap.put(nameKey + "Options." + name, StrUtil.toString(name));

            }

        }
        return keyMap;
    }

    public static String getSettingValue(Object setting) {
        Object value = "";
        if (ObjectUtil.isEmpty(setting)) {
            return StrUtil.toString(value);
        }
        String jsonStr = StrUtil.toString(setting);
        if (BaseUtil.isJSON(jsonStr)) {
            Object object = JSON.parse(jsonStr);
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                value = jsonObject.get("value");
            } else if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                value = jsonArray.get(0);
            } else {
                value = object;
            }
        } else {
            value = jsonStr;
        }
        return StrUtil.toString(value);
    }

    public static Map<String, String> getJSONValue(Object options, String keyName, String valueName) {
        Map<String, String> optionMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(options) && BaseUtil.isJSON(StrUtil.toString(options))) {
            Object name = "";
            Object value = "";
            Object object = JSON.parse(StrUtil.toString(options));
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                name = jsonObject.get(keyName);
                value = jsonObject.get(valueName);
            } else if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                for (Object o : jsonArray) {
                    Map<String, String> jsonValue = getJSONValue(o, keyName, valueName);
                    optionMap.putAll(jsonValue);
                }
            } else {
                name = object;
                value = object;
            }
            if (StrUtil.isNotBlank(StrUtil.toString(name))) {
                optionMap.put(StrUtil.toString(name), StrUtil.toString(value));
            }
        }
        return optionMap;
    }

}
