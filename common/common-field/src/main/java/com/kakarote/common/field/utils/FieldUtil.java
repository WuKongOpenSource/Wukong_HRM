package com.kakarote.common.field.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.field.entity.ModelField;
import com.kakarote.common.field.entity.ModelFieldDetail;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.feign.crm.entity.CrmFieldPatch;
import com.kakarote.core.utils.BaseUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义字段工具类
 *
 * @author zhangzhiwei
 */
public class FieldUtil {

    /**
     * 数字枚举
     */
    private static final String NUM_RESTRICT_REGEX = "^[\\-]?\\d+|[\\-]?\\d+\\.\\d*|[\\-]?\\d*\\.\\d+$";

    /**
     * 默认的字段类型
     */
    private static final FieldEnum[] DEFAULT_FIELD_ENUMS = {FieldEnum.AREA, FieldEnum.AREA_POSITION, FieldEnum.CURRENT_POSITION, FieldEnum.DETAIL_TABLE, FieldEnum.FIELD_GROUP};

    /**
     * 将字符串格式化为es需要的自定义字段值
     *
     * @param value     字符串
     * @param fieldEnum 字段类型
     * @return 字段值
     */
    public static Object format2EsObject(Object value, FieldEnum fieldEnum) {
        if (value == null) {
            return null;
        }
        switch (fieldEnum) {
            case SELECT: {
                JSONObject object;
                if (value instanceof Map) {
                    object = new JSONObject((Map<String, Object>) value);
                } else {
                    object = JSONObject.parseObject(value.toString());
                }
                return object;
            }
            case TAG:
            case AREA_POSITION:
            case DATE_INTERVAL:
            case CHECKBOX: {
                JSONObject object = new JSONObject();
                JSONArray array;
                value = JSON.parse(value.toString());
                if (value instanceof List) {
                    array = new JSONArray((List<Object>) value);
                } else {
                    array = new JSONArray();
                    array.add(value);
                }
                if (array.isEmpty()) {
                    return null;
                }
                Set<String> keySet = array.getJSONObject(0).keySet();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    for (String key : keySet) {
                        if (!object.containsKey(key)) {
                            object.put(key, new JSONArray());
                        }
                        object.getJSONArray(key).add(jsonObject.get(key));
                    }
                }
                object.put("size", array.size());
                return object;
            }
            case DATE: {
                if (value instanceof Date) {
                    return DateUtil.formatDate((Date) value);
                } else if (value instanceof LocalDate) {
                    return LocalDateTimeUtil.format((LocalDate) value, DatePattern.NORM_DATE_PATTERN);
                } else {
                    return value.toString();
                }
            }
            case DATETIME: {
                if (value instanceof Date) {
                    return DateUtil.formatDateTime((Date) value);
                } else if (value instanceof LocalDateTime) {
                    return LocalDateTimeUtil.format((LocalDateTime) value, DatePattern.NORM_DATETIME_PATTERN);
                } else {
                    return value.toString();
                }
            }
            case DETAIL_TABLE:
                //todo 暂时未处理明细表格
                return null;
            default:
                return value.toString();
        }
    }


    /**
     * 格式化字段值为前端需要的格式
     *
     * @param value     字符串
     * @param fieldType 字段类型
     * @return 字段值
     */
    public static Object format2Object(Object value, Integer fieldType) {
        return format2Object(value, FieldEnum.parse(fieldType));
    }

    /**
     * 格式化字段值为前端需要的格式
     *
     * @param value     字符串
     * @param fieldEnum 字段类型
     * @return 字段值
     */
    public static Object format2Object(Object value, FieldEnum fieldEnum) {
        Object newValue;
        switch (fieldEnum) {
            case HANDWRITING_SIGN:
                newValue = value.toString();
                break;
            case AREA:
            case CURRENT_POSITION:
            case AREA_POSITION:
                String valueStr = Optional.ofNullable(value).orElse("").toString();
                newValue = JSON.parse(valueStr);
                break;
            case DETAIL_TABLE:
                newValue = handleDetailTableData(value);
                break;
            case DATE_INTERVAL:
                String dateIntervalStr = Optional.ofNullable(value).orElse("").toString();
                newValue = StrUtil.split(dateIntervalStr, Const.SEPARATOR);
                break;
            default:
                return value;
        }
        return newValue;
    }

    /**
     * 处理明细表格数据
     *
     * @param value 字段值
     * @return CrmFieldPatch
     * @date 2021/3/5 14:38
     **/
    private static List<List<CrmFieldPatch>> handleDetailTableData(Object value) {
        List<List<CrmFieldPatch>> dtDataList = new ArrayList<>();
        String dtStr = Optional.ofNullable(value).orElse("").toString();
        if (StrUtil.isEmpty(dtStr)) {
            return dtDataList;
        }
        List<JSONArray> jsonArrayList = JSON.parseObject(dtStr, List.class);
        jsonArrayList.forEach(jsonArray -> {
            dtDataList.add(jsonArray.toJavaList(CrmFieldPatch.class));
        });
        for (List<CrmFieldPatch> crmFieldPatchList : dtDataList) {
            for (CrmFieldPatch crmFieldPatch : crmFieldPatchList) {
                FieldEnum fieldEnum = FieldEnum.parse(crmFieldPatch.getType());
                Object valueData = crmFieldPatch.getValue();
                if (ObjectUtil.isEmpty(valueData)) {
                    continue;
                }
                switch (fieldEnum) {
                    case CHECKBOX:
                        if (valueData instanceof String) {
                            crmFieldPatch.setValue(StrUtil.split(valueData.toString(), Const.SEPARATOR));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return dtDataList;
    }

    /**
     * 将自定义字段值格式化为字符串用户保存
     *
     * @param value     字段值
     * @param fieldEnum 字段类型
     * @return 格式化后的字符串
     */
    public static String format2DbString(Object value, FieldEnum fieldEnum) {
        return format2DbString(value, fieldEnum, null);
    }

    /**
     * 将自定义字段值格式化为字符串用户保存
     *
     * @param value     字段值
     * @param fieldEnum 字段类型
     * @return 格式化后的字符串
     */
    public static String format2DbString(Object value, FieldEnum fieldEnum, String defaultValue) {
        if (value == null) {
            return null;
        }

        if (equalsByType(fieldEnum.getType())) {
            if (value instanceof JSON) {
                return ((JSON) value).toJSONString();
            }
            if (BaseUtil.isJSON(value.toString())) {
                return JSON.toJSONString(value);
            }
            return null;
        }
        if (FieldEnum.DATE_INTERVAL == fieldEnum) {
            if (value instanceof JSONObject) {
                value = JSON.parseObject(((JSONObject) value).toJSONString(), List.class);
            }
            return !ObjectUtil.isEmpty(value) ? StrUtil.join(Const.SEPARATOR, value) : "";
        }
        if (FieldEnum.TAG == fieldEnum) {
            if (value instanceof String) {
                return (String) value;
            }
            return JSON.toJSONString(value);
        }
        return defaultValue;
    }

    /**
     * 将字符串格式化为自定义字段值
     *
     * @param value     字符串
     * @param fieldType 字段类型
     * @return 字段值
     */
    public static Object format2EsObject(Object value, Integer fieldType) {
        return format2EsObject(value, FieldEnum.parse(fieldType));
    }


    /**
     * 将自定义字段值格式化为字符串用户保存
     *
     * @param value     字段值
     * @param fieldType 字段类型
     * @return 格式化后的字符串
     */
    public static String format2DbString(Object value, Integer fieldType, String defaultValue) {
        return format2DbString(value, FieldEnum.parse(fieldType), defaultValue);
    }


    /**
     * 判断自定义字段类型是否符合
     *
     * @param type 类型
     * @return true为包含
     */

    public static boolean equalsByType(Object type) {
        return equalsByType(type, DEFAULT_FIELD_ENUMS);
    }

    /**
     * 判断自定义字段类型是否符合
     *
     * @param type       类型
     * @param fieldEnums 类型列表
     * @return true为包含
     */
    public static boolean equalsByType(Object type, FieldEnum... fieldEnums) {
        if (type instanceof String) {
            for (FieldEnum anEnum : fieldEnums) {
                if (anEnum.getFormType().equals(type)) {
                    return true;
                }
            }
        } else if (type instanceof Integer) {
            for (FieldEnum anEnum : fieldEnums) {
                if (Objects.equals(anEnum.getType(), type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 格式化字段为多排显示
     *
     * @param fieldList 字段列表
     * @return 格式化后的字段列表
     */
    public static List<List<ModelFieldDetail>> convertFormPositionFieldList(List<? extends ModelFieldDetail> fieldList) {
        Map<Integer, List<ModelFieldDetail>> map = fieldList.stream().collect(Collectors.groupingBy(modelFieldDetails -> {
            if (modelFieldDetails.getFormPosition() == null) {
                return -1;
            }
            String[] split = modelFieldDetails.getFormPosition().split(Const.SEPARATOR);
            if (split.length < 2) {
                return -1;
            }
            return Integer.valueOf(split[0]);
        }));
        List<List<ModelFieldDetail>> list = new ArrayList<>();
        map.keySet().stream().sorted(Integer::compare).forEach(key -> {
            List<ModelFieldDetail> modelFieldDetails = map.get(key);
            if (modelFieldDetails != null && !modelFieldDetails.isEmpty()) {
                if (key < 0) {
                    modelFieldDetails.sort(Comparator.comparing(ModelFieldDetail::getSorting));
                } else {
                    modelFieldDetails.sort(Comparator.comparing(fieldDetail -> {
                        String[] split = fieldDetail.getFormPosition().split(Const.SEPARATOR);
                        if (split.length < 2) {
                            return -1;
                        }
                        return Integer.valueOf(split[1]);
                    }));
                }
                list.add(modelFieldDetails);
            }
        });
        return list;
    }

    /**
     * 格式化字段为多排显示
     *
     * @param fieldList         字段列表
     * @param groupMapper       分组mapper
     * @param sortMapper        排序mapper
     * @param defaultSortMapper 默认排序mapper
     * @param <T>               类型
     * @return 格式化后的字段列表
     */
    public static <T> List<List<T>> convertFormPositionFieldList(List<T> fieldList, Function<T, Integer> groupMapper, Function<T, Integer> sortMapper, Function<T, Integer> defaultSortMapper) {
        List<List<T>> list = new ArrayList<>();
        Map<Integer, List<T>> crmFieldGroupMap = fieldList.stream().collect(Collectors.groupingBy(groupMapper));
        if (!crmFieldGroupMap.isEmpty()) {
            Map<Integer, List<T>> resultMap = new LinkedHashMap<>();
            crmFieldGroupMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(e -> resultMap.put(e.getKey(), e.getValue()));

            resultMap.forEach((key, value) -> {
                if (key != -1) {
                    value.sort(Comparator.comparing(sortMapper));
                    list.add(value);
                }
            });
            List<T> crmFields = resultMap.get(-1);
            if (CollUtil.isNotEmpty(crmFields)) {
                crmFields.sort(Comparator.comparing(defaultSortMapper));
                int size = crmFields.size();
                boolean isWithResidue = size % 2 != 0;
                List<T> temporaryList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    T crmField = crmFields.get(i);
                    temporaryList.add(crmField);
                    if (i % 2 == 1) {
                        list.add(temporaryList);
                        temporaryList = new ArrayList<>();
                        continue;
                    }
                    if (isWithResidue && i == size - 1) {
                        list.add(temporaryList);
                    }
                }
            }
        }
        return list;
    }


    /**
     * 校验数字范围
     *
     * @param maxNumRestrict 最大值
     * @param minNumRestrict 最小值
     * @return 校验结果 true为校验异常
     */
    public static boolean verifyStrForNumRestrict(String maxNumRestrict, String minNumRestrict) {
        if (StrUtil.isNotEmpty(maxNumRestrict) || StrUtil.isNotEmpty(minNumRestrict)) {
            if (StrUtil.isNotEmpty(maxNumRestrict) && StrUtil.isEmpty(minNumRestrict)) {
                return !maxNumRestrict.matches(NUM_RESTRICT_REGEX);
            } else if (StrUtil.isEmpty(maxNumRestrict) && StrUtil.isNotEmpty(minNumRestrict)) {
                return !minNumRestrict.matches(NUM_RESTRICT_REGEX);
            } else {
                if (maxNumRestrict.matches(NUM_RESTRICT_REGEX) && minNumRestrict.matches(NUM_RESTRICT_REGEX)) {
                    return !(Double.parseDouble(maxNumRestrict) > Double.parseDouble(minNumRestrict));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化字段信息
     *
     * @param record 字段信息
     */
    public static void recordToFormType(ModelField record) {
        FieldEnum typeEnum = FieldEnum.parse(record.getType());
        record.setFormType(typeEnum.getFormType());
        switch (typeEnum) {
            case ATTENTION:
                if (record.getDefaultValue() == null) {
                    record.setDefaultValue(0);
                } else {
                    record.setDefaultValue(TypeUtils.castToInt(record.getDefaultValue()));
                }
                if (record.getValue() == null) {
                    record.setValue(0);
                } else {
                    record.setValue(TypeUtils.castToInt(record.getValue()));
                }
            case SERIAL_NUMBER:
                record.setSetting(JSON.parseArray(record.getOptions()));
                break;
            case TAG:
            case CHECKBOX:
                if (record.getDefaultValue() != null && BaseUtil.isJSONArray(record.getDefaultValue().toString())) {
                    record.setDefaultValue(JSON.parseArray(record.getDefaultValue().toString()));
                }
                if (record.getValue() != null && BaseUtil.isJSONArray(record.getValue().toString())) {
                    record.setValue(JSON.parseArray(record.getValue().toString()));
                }
            case SELECT:
                if (Objects.equals(record.getRemark(), FieldEnum.OPTIONS_TYPE.getFormType())) {
                    JSONObject optionsData = JSON.parseObject(record.getOptions());
                    record.setOptionsData(optionsData);
                    record.setSetting(new ArrayList<>(optionsData.keySet()));
                } else {
                    record.setSetting(new ArrayList<>(StrUtil.splitTrim(record.getOptions(), Const.SEPARATOR)));
                }
                break;
            case USER:
            case STRUCTURE:
                if (record.getDefaultValue() != null && record.getDefaultValue() instanceof String) {
                    record.setDefaultValue(new ArrayList<>(0));
                }
                break;
            case DATE_INTERVAL:
                String dataValueStr = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                record.setDefaultValue(StrUtil.split(dataValueStr, Const.SEPARATOR));
                break;
            case AREA:
            case AREA_POSITION:
            case CURRENT_POSITION:
                String defaultValue = Optional.ofNullable(record.getDefaultValue()).orElse("").toString();
                record.setDefaultValue(JSON.parse(defaultValue));
                break;
            default:
                record.setSetting(new ArrayList<>());
                break;
        }

    }

    /**
     * 格式化字段信息
     *
     * @param fieldList 字段列表
     */
    public static void recordToFormType(List<? extends ModelField> fieldList) {
        for (ModelField record : fieldList) {
            recordToFormType(record);
        }
    }
}
