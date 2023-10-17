package com.kakarote.core.utils;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.enums.FieldSearchEnum;
import com.kakarote.core.entity.Search;
import com.kakarote.core.feign.crm.entity.CrmSearchBO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语言包处理工具类
 */
public class ESConditionToSqlUtil {

    /**
     * 功能描述: <br>
     * 〈学员班级信息表头〉
     *
     * @Param: * @param null
     * @Return:
     * @Author: zyh
     * @Date: 2021/8/23 19:14
     */
    public Map<String, String> getFieldNamePrefixMap(List<String> fields, String prefix) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isEmpty(fields) || StrUtil.isBlank(prefix)) {
            return map;
        }
        fields.forEach(item -> {
            map.put(item, prefix);
        });
        return map;
    }

    /**
     * 功能描述: <br>
     * 〈ES 转换 sql 查询〉
     * @Param:  * @param null
     * @Return:
     * @Author: zyh
     * @Date: 2021/8/24 10:09
     */
    public String searchESToSql(CrmSearchBO search, Map<String,String> filedMap) {
        if(ObjectUtil.isEmpty(search)){
            search = new CrmSearchBO();
        }
        List<Search> searchList = search.getSearchList();
        return this.searchESToSql(searchList,filedMap);
    }


    /**
     * 功能描述: <br>
     * 〈ES 转换 sql 查询〉
     * @Param:  * @param null
     * @Return:
     * @Author: zyh
     * @Date: 2021/8/24 10:09
     */
    public String searchESToSql(List<Search> searchList,Map<String,String> filedMap) {
        StringBuilder conditions = new StringBuilder("");
        if (CollectionUtil.isEmpty(searchList)) {
            return conditions.toString();
        }
        searchList.forEach(r -> {
            List<String> values = r.getValues();
            String columnName =StrUtil.toUnderlineCase(r.getName());
            String prefix = "";
            if(filedMap.containsKey(columnName)){
                prefix = filedMap.get(columnName);
                columnName = StrUtil.toUnderlineCase(columnName);
                if (r.getSearchEnum() == FieldSearchEnum.IS_NULL) {
                    //不存在
                    conditions.append(" and ").append(getPrefix(prefix)).append(columnName).append(" IS NULL");

                } else if (r.getSearchEnum() == FieldSearchEnum.IS) {
                    //等于
                    if (r.getValues().size() > 1) {
                        List<String> list = new ArrayList<String>();
                        r.getValues().stream().filter(ObjectUtil::isNotEmpty).distinct().forEach(string -> {
                            list.add("'" + string + "'");
                        });
                        String a = String.join(",", list);
                        conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" IN(").append(a).append(") ");
                    }else{
                        conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" = '").append(r.getValues().get(0)).append("' ");
                    }

                } else if (r.getSearchEnum() == FieldSearchEnum.ID) {
                    //等于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(String.join(",", r.getValues())).append(") ");


                } else if (r.getSearchEnum() == FieldSearchEnum.IS_NOT) {
                    //不等于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" != '").append(r.getValues().get(0)).append("' ");

                } else if (r.getSearchEnum() == FieldSearchEnum.CONTAINS) {
                    if(r.getValues().size() > 0){
                        for (int i = 0; i < r.getValues().size(); i++) {
                            if(i ==0){
                                //不包含
                                conditions.append(" and  (");
                            }
                            if(i ==0){
                                //不包含
                                conditions.append(getPrefix(prefix)).append(columnName).append(" LIKE CONCAT('%','").append(r.getValues().get(i)).append("','%')");
                            }else{
                                //不包含
                                conditions.append(" or  ").append(getPrefix(prefix)).append(columnName).append(" LIKE CONCAT('%','").append(r.getValues().get(i)).append("','%')");
                            }

                            if (i == (r.getValues().size()-1)) {
                                //不包含
                                conditions. append(" )");
                            }
                        }
                    }

                } else if (r.getSearchEnum() == FieldSearchEnum.NOT_CONTAINS) {
                    if(r.getValues().size() > 0){
                        for (int i = 0; i < r.getValues().size(); i++) {
                            if(i ==0){
                                //不包含
                                conditions.append(" and  (");
                            }
                            if(i ==0){
                                //不包含
                                conditions.append(getPrefix(prefix)).append(columnName).append(" NOT LIKE CONCAT('%','").append(r.getValues().get(i)).append("','%')");
                            }else{
                                //不包含
                                conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" NOT LIKE CONCAT('%','").append(r.getValues().get(i)).append("','%')");
                            }

                            if (i == (r.getValues().size()-1)) {
                                //不包含
                                conditions. append(" )");
                            }
                        }
                    }

                } else if (r.getSearchEnum() == FieldSearchEnum.IS_NOT_NULL) {
                    //不为空
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" IS NOT NULL ");

                } else if (r.getSearchEnum() == FieldSearchEnum.GT) {
                    //大于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" > '").append(r.getValues().get(0)).append("' ");

                } else if (r.getSearchEnum() == FieldSearchEnum.EGT) {
                    //大于等于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" >= '").append(r.getValues().get(0)).append("' ");

                } else if (r.getSearchEnum() == FieldSearchEnum.LT) {
                    //小于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" < '").append(r.getValues().get(0)).append("' ");

                } else if (r.getSearchEnum() == FieldSearchEnum.ELT) {
                    //小于等于
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" <= '").append(r.getValues().get(0)).append("' ");
                } else if (r.getSearchEnum() == FieldSearchEnum.PREFIX) {
                    //前缀匹配
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" like '").append(r.getValues().get(0)).append("%' ");
                } else if (r.getSearchEnum() == FieldSearchEnum.SUFFIX) {
                    //后缀匹配
                    conditions.append(" and  ").append(getPrefix(prefix)).append(columnName).append(" like '%").append(r.getValues().get(0)).append("' ");
                } else if (r.getSearchEnum() == FieldSearchEnum.RANGE) {
                    //在..之中
                    if (r.getValues().size() > 0) {
                        conditions.append(" and ").append(getPrefix(prefix)).append(columnName).append("  BETWEEN  '").append(r.getValues().get(0)).append("' and '").append(r.getValues().get(1)).append("' ");
                    }
                }
            }
        });
        return conditions.toString();

    }

    public String getPrefix(String prefix){
        StringBuilder conditions = new StringBuilder("");
        if(StrUtil.isBlank(prefix)){
            return conditions.toString();
        }
        conditions.append(prefix).append(".");
        return conditions.toString();

    }
}
