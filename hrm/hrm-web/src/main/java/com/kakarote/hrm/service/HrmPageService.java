package com.kakarote.hrm.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.field.utils.EsUtil;
import com.kakarote.common.field.utils.FieldUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.common.enums.FieldSearchEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.Search;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.redis.Redis;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.hrm.constant.HrmEnum;
import com.kakarote.hrm.entity.BO.HrmModelSaveBO;
import com.kakarote.hrm.entity.BO.HrmSearchBO;
import com.kakarote.hrm.entity.VO.HrmFieldSortVO;
import com.kakarote.hrm.entity.VO.HrmModelFiledVO;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kakarote.core.servlet.ApplicationContextHolder.getBean;

public interface HrmPageService {

    Logger logger = LoggerFactory.getLogger(HrmPageService.class);


    int NUMBER = 100;
    /**
     * 查询列表页
     *
     * @param hrmSearchBO 业务查询对象
     * @return data
     */
    /**
     * 保存Elasticsearch数据
     *
     * @param model obj
     * @param id    主键ID
     */
    default void savePage(HrmModelSaveBO model, Object id, boolean isExcel) {
        List<HrmModelFiledVO> crmModelFiledList = queryDefaultField();
        Map<String, Object> map = new HashMap<>(model.getEntity());
        model.getField().forEach(field -> {
            map.put(field.getFieldName(), field.getValue());
        });
        crmModelFiledList.forEach(modelField -> {
            FieldEnum fieldEnum = FieldEnum.parse(modelField.getType());

            if (map.get(modelField.getFieldName()) == null) {
                map.remove(modelField.getFieldName());
                return;
            }
            if (fieldEnum == FieldEnum.DATETIME || fieldEnum == FieldEnum.DATE) {
                Object value = map.remove(modelField.getFieldName());
                if (ObjectUtil.isNotEmpty(value)) {
                    if (value instanceof Date) {
                        map.put(modelField.getFieldName(), fieldEnum == FieldEnum.DATE ? DateUtil.formatDate((Date) value) : DateUtil.formatDateTime((Date) value));
                    } else if (value instanceof String) {
                        map.put(modelField.getFieldName(), value.toString());
                    } else if (value instanceof LocalDateTime) {
                        map.put(modelField.getFieldName(), fieldEnum == FieldEnum.DATE ? LocalDateTimeUtil.format((LocalDateTime) value, "yyyy-MM-dd") : LocalDateTimeUtil.formatNormal((LocalDateTime) value));
                    }
                }
            }
            if (fieldEnum == FieldEnum.TAG) {
                Object value = map.remove(modelField.getFieldName());
                if (value instanceof String) {
                    map.put(modelField.getFieldName(), value);
                } else {
                    map.put(modelField.getFieldName(), JSON.toJSONString(value));
                }
            }
            if (FieldEnum.FILE == fieldEnum) {
                Object value = map.remove(modelField.getFieldName());
                if (!ObjectUtil.isEmpty(value)) {
                    List<FileEntity> data = getBean(AdminFileService.class).queryFileList((String) value);
                    map.put(modelField.getFieldName(), data.stream().map(FileEntity::getName).collect(Collectors.toList()));
                }
            }
            if (FieldUtil.equalsByType(modelField.getType())) {
                Object value = map.remove(modelField.getFieldName());
                if (!ObjectUtil.isEmpty(value)) {
                    if (value instanceof String) {
                        map.put(modelField.getFieldName(), value.toString());
                    } else {
                        map.put(modelField.getFieldName(), JSON.toJSONString(value));
                    }
                }
            }
            if (FieldEnum.DATE_INTERVAL == fieldEnum) {
                Object value = map.remove(modelField.getFieldName());
                if (!ObjectUtil.isEmpty(value)) {
                    if (value instanceof String) {
                        map.put(modelField.getFieldName(), StrUtil.splitTrim(value.toString(), ","));
                    } else if (value instanceof Collection) {
                        map.put(modelField.getFieldName(), value);
                    }
                }
            }

        });
        setOtherField(map);
        EsUtil.updateField(map, id, getLabel(), true, !isExcel);
    }


    default BasePage<Map<String, Object>> queryList(HrmSearchBO hrmSearchBO, boolean isExcel) {
        HitsMetadata<JSONObject> searchResult = getSearchResult(hrmSearchBO);
        List<HrmModelFiledVO> voList = queryDefaultField();
        List<Hit<JSONObject>> hits = searchResult.hits();
        List<Map<String, Object>> mapList = new ArrayList<>(hits.size());
        if (hrmSearchBO.getPage() >= NUMBER) {
            if (!hits.isEmpty()) {
                //处理searchAfter
                Hit<JSONObject> searchHit = hits.get(hits.size() - 1);
                Redis redis = ApplicationContextHolder.getBean(Redis.class);
                String searchAfterKey = "es:search:" + UserUtil.getUserId().toString();
                if (hrmSearchBO.getPage() == NUMBER) {
                    redis.del(searchAfterKey);
                }
                int page = redis.getLength(searchAfterKey).intValue();
                if (hrmSearchBO.getPage() - NUMBER >= page) {
                    redis.rpush(searchAfterKey, searchHit.sort());
                }
                //缓存一个小时
                redis.expire(searchAfterKey, 3600);
            }
        }
        for (Hit<JSONObject> hit : hits) {
            Map<String, Object> sourceAsMap = hit.source();
            sourceAsMap.put(getLabel().getPrimaryKey(), Long.valueOf(hit.id()));
            Map<String, Object> map = parseMap(sourceAsMap, voList, false);
            mapList.add(map);
        }
        BasePage<Map<String, Object>> basePage = new BasePage<>();
//        ApplicationContextHolder.getBean(IHrmRoleFieldService.class).replaceMaskFieldValue(getLabel(), mapList, 1);
        basePage.setSize(hrmSearchBO.getLimit());
        basePage.setList(mapList);
        basePage.setTotal(searchResult.total().value());
        basePage.setCurrent(hrmSearchBO.getPage());
        return basePage;

    }

    default HitsMetadata<JSONObject> getSearchResult(HrmSearchBO hrmSearchBO) {
        SearchRequest.Builder searchRequest = createSourceBuilder(hrmSearchBO);
        SearchResponse<JSONObject> response = EsUtil.search(searchRequest.build());
        return response.hits();
    }

    default String getIndex() {
        return getLabel().getIndex();
    }

    /**
     * 查询的字段，以及排序
     *
     * @param hrmSearchBO data
     * @return data
     */
    default SearchRequest.Builder createSourceBuilder(HrmSearchBO hrmSearchBO) {
        SearchRequest.Builder searchRequest = new SearchRequest.Builder();
        searchRequest.index(getIndex());
        searchRequest.timeout("1m");
        //排序以及查询字段
        sort(hrmSearchBO, searchRequest);
        searchRequest.query(createQueryBuilder(hrmSearchBO).build()._toQuery());
        return searchRequest;
    }


    /**
     * 设置其他冗余字段
     *
     * @param map
     */
    void setOtherField(Map<String, Object> map);

    public String[] appendSearch();

    /**
     * 拼接查询条件
     *
     * @param hrmSearchBO
     * @return
     */
    default BoolQuery.Builder createQueryBuilder(HrmSearchBO hrmSearchBO) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        if (StrUtil.isNotEmpty(hrmSearchBO.getSearch())) {
            BoolQuery.Builder searchBoolQuery = QueryBuilders.bool();
            for (String search : appendSearch()) {
                searchBoolQuery.should(EsUtil.wildcardQuery(search, "*" + hrmSearchBO.getSearch().trim() + "*"));
            }
            queryBuilder.filter(searchBoolQuery.build()._toQuery());
        }

        //开始搜索相关
        hrmSearchBO.getSearchList().forEach(search -> {
            Dict searchTransferMap = getSearchTransferMap();
            if (searchTransferMap.containsKey(search.getName())) {
                search.setName(searchTransferMap.getStr(search.getName()));
            }
            search(search, queryBuilder);
        });
        //setHrmDataAuth(queryBuilder);
        /*if (queryBuilder.should().size() > 0) {
            queryBuilder.minimumShouldMatch("1");
        }*/
        return queryBuilder;
    }


    default void search(Search search, BoolQuery.Builder queryBuilder) {
        if (search.getSearchEnum() == FieldSearchEnum.ID) {
            queryBuilder.filter(QueryBuilders.ids().values(search.getValues()).build()._toQuery());
            return;
        }
        String formType = search.getFormType();
        FieldEnum fieldEnum = FieldEnum.parse(formType);
        switch (fieldEnum) {
            case TEXTAREA:
                search.setName(search.getName() + ".keyword");
            case TEXT:
            case MOBILE:
            case EMAIL:
            case SELECT:
            case WEBSITE: {
                EsUtil.textSearch(search, queryBuilder);
                break;
            }
            case BOOLEAN_VALUE: {
                boolean value = TypeUtils.castToBoolean(search.getValues().get(0));
                value = (search.getSearchEnum() == FieldSearchEnum.IS) == value;
                if (value) {
                    queryBuilder.filter(EsUtil.termQuery(search.getName(), "1"));
                } else {
                    BoolQuery.Builder builder = QueryBuilders.bool();
                    builder.should(EsUtil.termQuery(search.getName(), "0"));
                    builder.should(EsUtil.termQuery(search.getName(), ""));
                    builder.should(QueryBuilders.bool().mustNot(EsUtil.existsQuery(search.getName())).build()._toQuery());
                    queryBuilder.filter(builder.build()._toQuery());
                }
                break;
            }
            case CHECKBOX: {
                search.setName(search.getName());
                EsUtil.checkboxSearch(search, queryBuilder);
                break;
            }
            case NUMBER:
            case FLOATNUMBER:
            case PERCENT:
                EsUtil.numberSearch(search, queryBuilder);
                break;
            case DATE_INTERVAL:
                break;
            case DATE:
            case DATETIME:
                EsUtil.dateSearch(search, queryBuilder, fieldEnum);
                break;
            case AREA_POSITION:
                Query prefixQuery = EsUtil.prefixQuery(search.getName(), JSONToken.name(JSONToken.LBRACKET) + CollUtil.join(search.getValues(), Const.SEPARATOR));
                queryBuilder.filter(prefixQuery);
                break;
            case CURRENT_POSITION:
                if (search.getSearchEnum() == FieldSearchEnum.IS) {
                    search.setValues(Collections.singletonList("\"" + search.getValues().get(0) + "\""));
                    search.setSearchEnum(FieldSearchEnum.CONTAINS);
                }
                if (search.getSearchEnum() == FieldSearchEnum.IS_NOT) {
                    search.setValues(Collections.singletonList("\"" + search.getValues().get(0) + "\""));
                    search.setSearchEnum(FieldSearchEnum.NOT_CONTAINS);
                }
                EsUtil.textSearch(search, queryBuilder);
                break;
            case USER:
            case SINGLE_USER:
            case STRUCTURE:
                EsUtil.userSearch(search, queryBuilder);
                break;
            default:
                EsUtil.textSearch(search, queryBuilder);
                break;
        }
    }

    /**
     * 获取关联表字段转换
     *
     * @return
     */
    default Dict getSearchTransferMap() {
        return Dict.create();
    }


    default void sort(HrmSearchBO hrmSearchBO, SearchRequest.Builder searchBuilder) {
        //todo 暂时未考虑手机端的高级查询分页
        String searchAfterKey = "es:search:" + UserUtil.getUserId().toString();
        List<HrmFieldSortVO> hrmFieldSortList = getBean(IHrmEmployeeFieldService.class).queryListHead(getLabel().getType());

        //如果大于100页，尝试使用searchAfter分页
        if (hrmSearchBO.getPage() > NUMBER && !Objects.equals(0, hrmSearchBO.getPageType())) {
            Redis redis = ApplicationContextHolder.getBean(Redis.class);;
            Long length = redis.getLength(searchAfterKey);
            if ((hrmSearchBO.getPage() - NUMBER) > length.intValue()) {
                //分页数据错误,直接重置
                hrmSearchBO.setPage(1L);
            } else {
                List<FieldValue> keyIndex = redis.getKeyIndex(searchAfterKey, Convert.toInt(hrmSearchBO.getPage()) - 101);
                hrmSearchBO.searchAfter(keyIndex);
            }
        }
        //如果存在SearchAfterKey，则使用searchAfterKey查询
        if (hrmSearchBO.getSearchAfterKey() != null) {
            searchBuilder.searchAfter(hrmSearchBO.getSearchAfterKey());
        } else {
            //除特殊场景外，最多查询100条数据
            if (hrmSearchBO.getLimit() > NUMBER && !Objects.equals(0, hrmSearchBO.getPageType())) {
                hrmSearchBO.setLimit(100L);
            }
            searchBuilder.from((hrmSearchBO.getPage().intValue() - 1) * hrmSearchBO.getLimit().intValue());
            searchBuilder.size(hrmSearchBO.getLimit().intValue());
        }

        //设置查询条数
        searchBuilder.size(hrmSearchBO.getLimit().intValue());
        AtomicReference<Integer> fieldType = new AtomicReference<>(0);
        List<String> fieldList = new ArrayList<>();
        hrmFieldSortList.forEach(hrmField -> {
            if (hrmField.getFieldName().equals(hrmSearchBO.getSortField())) {
                fieldType.set(hrmField.getType());
            }
            fieldList.add(StrUtil.toCamelCase(hrmField.getFieldName()));
        });
        if (StrUtil.isEmpty(hrmSearchBO.getSortField()) || hrmSearchBO.getOrder() == null || fieldType.get().equals(0)) {
            hrmSearchBO.setOrder(1).setSortField("updateTime");
        } else {
            FieldEnum fieldEnum = FieldEnum.parse(fieldType.get());
            switch (fieldEnum) {
                case TEXT:
                case TEXTAREA:
                case SELECT:
                case MOBILE:
                case FILE:
                case CHECKBOX:
                case USER:
                case STRUCTURE:
                case EMAIL:
                    hrmSearchBO.setSortField(hrmSearchBO.getSortField() + ".sort");
                    break;
                default:
                    break;
            }
        }
        searchBuilder.sort(
                EsUtil.sort(Objects.equals(2, hrmSearchBO.getOrder()) ? SortOrder.Asc : SortOrder.Desc,hrmSearchBO.getSortField()),
                EsUtil.sort(SortOrder.Desc,"_id"));
        List<String> fieldNameList = new ArrayList<>();
        //只查询所需字段
        for (String fieldName : fieldList) {
            fieldNameList.add(fieldName);
            if (fieldName.endsWith("Name")) {
                String name = fieldName.substring(0, fieldName.indexOf("Name"));
                fieldNameList.add(name + "Id");
            }
            if (fieldName.endsWith("Num")) {
                String name = fieldName.substring(0, fieldName.indexOf("Num"));
                fieldNameList.add(name + "Id");
            }
        }
        searchBuilder.source(new SourceConfig.Builder().filter(b -> {
            b.includes(fieldNameList);
            return b;
        }).build());
    }

    List<HrmModelFiledVO> queryDefaultField();

    public HrmEnum getLabel();

    default Map<String, Object> parseMap(Map<String, Object> objectMap, List<HrmModelFiledVO> fieldList, Boolean isStart) {
        fieldList.forEach(field -> {
            if (!objectMap.containsKey(field.getFieldName())) {
                objectMap.put(field.getFieldName(), "");
            }
            if (field.getFieldType() == 0 && field.getType().equals(FieldEnum.USER.getType())) {
                if (ObjectUtil.isNotEmpty(objectMap.get(field.getFieldName()))) {
                    List<Long> ids = Convert.toList(Long.class, objectMap.get(field.getFieldName()));
                    objectMap.put(field.getFieldName(), ids.stream().map(UserCacheUtil::getUserName).collect(Collectors.joining(Const.SEPARATOR)));
                } else {
                    objectMap.put(field.getFieldName(), "");
                }
            }
            if (field.getFieldType() == 0 && field.getType().equals(FieldEnum.STRUCTURE.getType())) {
                if (ObjectUtil.isNotEmpty(objectMap.get(field.getFieldName()))) {
                    List<Long> ids = Convert.toList(Long.class, objectMap.get(field.getFieldName()));
                    objectMap.put(field.getFieldName(), ids.stream().map(UserCacheUtil::getDeptName).collect(Collectors.joining(",")));
                } else {
                    objectMap.put(field.getFieldName(), "");
                }
            }
            int three = 3;
            int eight = 8;
            int nine = 9;
            int fieldType = 11;
            if (field.getFieldType() == 0 && Arrays.asList(three, eight, nine, fieldType).contains(field.getType())) {
                Object value = objectMap.get(field.getFieldName());
                if (ObjectUtil.isNotEmpty(value)) {
                    objectMap.put(field.getFieldName(), CollUtil.join(Convert.toList(String.class, value), ","));
                } else {
                    objectMap.put(field.getFieldName(), "");
                }
            }
            if (FieldUtil.equalsByType(field.getType())) {
                Object value = objectMap.get(field.getFieldName());
                if (ObjectUtil.isNotEmpty(value)) {
                    try {
                        objectMap.put(field.getFieldName(), JSON.parse((String) value));
                    } catch (JSONException e) {
                        objectMap.put(field.getFieldName(), value.toString());
                    }
                } else {
                    objectMap.put(field.getFieldName(), "");
                }
            }
            if (field.getType().equals(FieldEnum.TAG.getType())) {
                Object value = objectMap.remove(field.getFieldName());
                if (value != null && StrUtil.isNotEmpty(value.toString().trim())) {
                    objectMap.put(field.getFieldName(), JSON.parse(value.toString()));
                }
            }
            // 20220223 wwl 新增 对整数类型的金额追加小数点后  1 => 1.00 ,其它类型不管
            if (field.getFieldType() == 1 && field.getType().equals(FieldEnum.FLOATNUMBER.getType())) {
                Object value = objectMap.get(field.getFieldName());
                if (ObjectUtil.isNotEmpty(value)) {
                    if (value instanceof Integer) {
                        objectMap.put(field.getFieldName(), new BigDecimal(value + ".00"));
                    }
                } else {
                    objectMap.put(field.getFieldName(), "");
                }
            }
            if (isStart) {
                if (field.getType().equals(FieldEnum.AREA_POSITION.getType())) {
                    Object value = objectMap.remove(field.getFieldName());
                    if (value != null && StrUtil.isNotEmpty(value.toString().trim())) {
                        StringBuilder address = new StringBuilder();
                        JSONArray array = JSONArray.parseArray((value.toString()));
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            if (address.length() != 0) {
                                address.append(",");
                            }
                            if (json.getInteger("id") <= 3) {
                                address.append(json.get("name"));
                            } else {
                                address.append(json.get("name"));
                            }
                        }
                        objectMap.put(field.getFieldName(), address);
                    }
                }
            }
        });
        return objectMap;
    }


    /**
     * 批量更新es字段
     *
     * @param map
     * @param ids
     */
    default void updateField(Map<String, Object> map, List<Long> ids) {
        EsUtil.updateField(map, ids, getLabel());
    }

    /**
     * 修改某个字段的值
     *
     * @param fieldName 字段
     * @param value     值
     * @param ids       ids
     */
    default void updateField(String fieldName, Object value, List<Long> ids) {
        Map<String, Object> map = new HashMap<>();
        String ownerUserId = "ownerUserId";
        if (ownerUserId.equals(fieldName)) {
            map.put("ownerUserName", UserCacheUtil.getUserName((Long) value));
        }
        map.put(fieldName, value);
        EsUtil.updateField(map, ids, getLabel());
    }

    /**
     * 修改某个字段的值
     *
     * @param id id
     */
    default void updateField(JSONObject jsonObject, Long id) {
        // 3, 8, 9, 10, 11, 12
        int three = 3;
        int eight = 8;
        int nine = 9;
        int oneZero = 10;
        int oneOne = 11;
        int oneTwo = 12;
        Map<String, Object> map = new HashMap<>();
        String fieldName = jsonObject.getString("fieldName");
        String ch = "value";
        String fieldType = "fieldType";
        if (jsonObject.get(ch) != null) {
            Integer type = jsonObject.getInteger("type");
            if (FieldEnum.DATE.getType().equals(type)) {
                Object value = jsonObject.get("value");
                map.put(fieldName, value);
            } else if (FieldEnum.DATETIME.getType().equals(type)) {
                Object value = jsonObject.get("value");
                map.put(fieldName, value);
            } else if (FieldEnum.FILE.getType().equals(type)) {
                Object value = jsonObject.get("value");
                List<FileEntity> data = getBean(AdminFileService.class).queryFileList((String) value);
                map.put(fieldName, data.stream().map(FileEntity::getName).collect(Collectors.joining(",")));
            } else if (FieldEnum.TAG.getType().equals(type)) {
                JSONArray value = jsonObject.getJSONArray("value");
                map.put(fieldName, value.toJSONString());
            } else if (FieldUtil.equalsByType(type)) {
                Object value = jsonObject.get("value");
                if (!ObjectUtil.isEmpty(value)) {
                    map.put(fieldName, JSON.toJSONString(value));
                }
            } else if (jsonObject.getInteger(fieldType) == 0 && Arrays.asList(three, eight, nine, oneZero, oneOne, oneTwo).contains(type)) {
                Object value = jsonObject.get("value");
                if (value != null) {
                    map.put(fieldName, StrUtil.splitTrim(value.toString(), ","));
                } else {
                    map.put(fieldName, new ArrayList<>());
                }
            } else {
                String value = jsonObject.getString("value");
                map.put(fieldName, value);
            }

        } else {
            map.put(fieldName, null);
        }

        map.put("updateTime", DateUtil.formatDateTime(new Date()));
        EsUtil.updateField(map, id, getLabel());
    }


    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     */
    default void deletePage(List<Long> ids) {
        EsUtil.deleteData(ids, getLabel());
    }

}
