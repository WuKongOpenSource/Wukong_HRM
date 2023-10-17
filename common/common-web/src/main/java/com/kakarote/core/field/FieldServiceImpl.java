package com.kakarote.core.field;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.feign.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author JiaS
 * @date 2021/1/13
 */
@Service
public class FieldServiceImpl implements FieldService {

    private static final String NUM_RESTRICT_REGEX = "^[\\-]?\\d+|[\\-]?\\d+\\.\\d*|[\\-]?\\d*\\.\\d+$";

    private static final FieldEnum[] DEFAULT_FIELD_ENUMS = {FieldEnum.AREA,FieldEnum.AREA_POSITION,FieldEnum.CURRENT_POSITION,FieldEnum.DETAIL_TABLE,FieldEnum.FIELD_GROUP};

    @Autowired
    private AdminService adminService;
    
    @Override
    public <T> List<List<T>> convertFormPositionFieldList(List<T> fieldList, Function<T,Integer> groupMapper,
                                                          Function<T,Integer> sortMapper,Function<T,Integer> defaultSortMapper){
        List<List<T>> list = new ArrayList<>();
        Map<Integer, List<T>> crmFieldGroupMap = fieldList.stream().collect(Collectors.groupingBy(groupMapper));
        if (crmFieldGroupMap.size() > 0) {
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


    @Override
    public boolean verifyStrForNumRestrict(String maxNumRestrict, String minNumRestrict) {
        if (StrUtil.isNotEmpty(maxNumRestrict) || StrUtil.isNotEmpty(minNumRestrict)){
            if (StrUtil.isNotEmpty(maxNumRestrict) && StrUtil.isEmpty(minNumRestrict)){
                return maxNumRestrict.matches(NUM_RESTRICT_REGEX);
            }else if (StrUtil.isEmpty(maxNumRestrict) && StrUtil.isNotEmpty(minNumRestrict)){
                return minNumRestrict.matches(NUM_RESTRICT_REGEX);
            }else {
                if (maxNumRestrict.matches(NUM_RESTRICT_REGEX) && minNumRestrict.matches(NUM_RESTRICT_REGEX)){
                    return Double.parseDouble(maxNumRestrict) > Double.parseDouble(minNumRestrict);
                }
                return false;
            }
        }
        return true;
    }



    @Override
    public String convertObjectValueToString(Integer type, Object value, String defaultValue){
        if (equalsByType(type)) {
            if (value instanceof JSONObject) {
                return !ObjectUtil.isEmpty(value) ? ((JSONObject) value).toJSONString() : "";
            }
            return !ObjectUtil.isEmpty(value) ? JSON.toJSONString(value) : "";
        }
        if (FieldEnum.DATE_INTERVAL.getType().equals(type)){
            if (value instanceof JSONObject){
                value = JSON.parseObject(((JSONObject)value).toJSONString(),List.class);
            }
            return !ObjectUtil.isEmpty(value) ? StrUtil.join(Const.SEPARATOR, value) : "";
        }
        if(FieldEnum.TAG.getType().equals(type)) {
            if(value instanceof String) {
                return (String) value;
            }
            return JSON.toJSONString(value);
        }
        return defaultValue;
    }

    @Override
    public boolean equalsByType(Object type) {
        return equalsByType(type,DEFAULT_FIELD_ENUMS);
    }

    @Override
    public boolean equalsByType(Object type, FieldEnum... fieldEnums){
        if (type instanceof String){
            for (FieldEnum anEnum : fieldEnums) {
                if(anEnum.getFormType().equals(type)){
                    return true;
                }
            }
        } else {
            for (FieldEnum anEnum : fieldEnums) {
                if (Objects.equals(anEnum.getType(),type)) {
                    return true;
                }
            }
        }
        return false;
    }
}

