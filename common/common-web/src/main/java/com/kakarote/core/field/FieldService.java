package com.kakarote.core.field;

import com.kakarote.core.common.enums.FieldEnum;

import java.util.List;
import java.util.function.Function;

/**
 * @author JiaS
 * @date 2021/1/13
 */
public interface FieldService {

    /**
     * 转换字段列表根据表单定位
     *
     * @param fieldList
     * @param groupMapper
     * @param sortMapper
     * @param defaultSortMapper
     * @return data
     */
    <T> List<List<T>> convertFormPositionFieldList(List<T> fieldList, Function<T, Integer> groupMapper,
                                                   Function<T, Integer> sortMapper, Function<T, Integer> defaultSortMapper);


    /**
     * 验证表单字段限制的数值是否正确
     *
     * @param maxNumRestrict
     * @param minNumRestrict
     * @return
     */
    boolean verifyStrForNumRestrict(String maxNumRestrict, String minNumRestrict);


    /**
     * 将符合条件的字段值转换成str
     *
     * @param type
     * @param value
     * @param defaultValue
     * @return data
     */
    String convertObjectValueToString(Integer type, Object value, String defaultValue);


    /**
     * 判断自定义字段类型是否符合
     *
     * @param type
     * @return
     */
    boolean equalsByType(Object type);

    /**
     * 判断自定义字段类型是否符合
     *
     * @param type
     * @param fieldEnums
     * @return
     */
    boolean equalsByType(Object type, FieldEnum... fieldEnums);
}
