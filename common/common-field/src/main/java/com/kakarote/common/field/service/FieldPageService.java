package com.kakarote.common.field.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.ModelEntity;
import com.kakarote.common.field.entity.ModelField;
import com.kakarote.common.field.entity.SearchEntity;
import com.kakarote.common.field.utils.EsUtil;
import com.kakarote.common.field.utils.FieldUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.redis.Redis;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.utils.ExcelParseUtil;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kakarote.core.servlet.ApplicationContextHolder.getBean;

/**
 * es查询列表一些通用的接口
 *
 * @author zhangzhiwei
 */
public interface FieldPageService<T extends SearchEntity> {

    /**
     * 查询列表数据
     *
     * @param search 筛选条件
     * @return data
     */
    default BasePage<Map<String, Object>> queryList(T search) {
        return queryList(search, getLabel());
    }

    /**
     * 查询其他模块列表数据
     *
     * @param search     筛选条件
     * @param fieldLabel 索引信息
     * @return data
     */
    default BasePage<Map<String, Object>> queryList(T search, FieldLabel fieldLabel) {
        handlerSearch(search);
        String searchAfterKey = "es:search:" + UserUtil.getUserId().toString();
        List<ModelField> filedList = queryField();
        SearchRequest request = SearchRequest.of(builder -> {
            builder.index(fieldLabel.getIndex());
            builder.sort(sortOptions(search, filedList));
            builder.query(createQueryBuilder(search).build()._toQuery());
            builder.timeout("1m");
            if (search.getFetchFieldNameList().isEmpty()) {
                List<String> sourceFields = filedList.stream().map(f -> StrUtil.toCamelCase(f.getFieldName())).collect(Collectors.toList());
                search.setFetchFieldNameList(sourceFields);
            }
            builder.source(new SourceConfig.Builder().filter(new SourceFilter.Builder().includes(search.getFetchFieldNameList()).build()).build());
            if (search.getPageType().equals(1)) {
                //如果大于100页，尝试使用searchAfter分页
                if (search.getPage() > Const.QUERY_MAX_SIZE) {
                    Redis redis = ApplicationContextHolder.getBean(Redis.class);
                    Long length = redis.getLength(searchAfterKey);
                    if ((search.getPage() - Const.QUERY_MAX_SIZE) > length.intValue()) {
                        //分页数据错误,直接重置
                        search.setPage(1L);
                    } else {
                        FieldValue[] keyIndex = redis.getKeyIndex(searchAfterKey, (int) (search.getPage() - 101));
                        search.searchAfter(Arrays.asList(keyIndex));
                    }
                }
                // 设置起止和结束
                builder.from((int) ((search.getPage() - 1) * search.getLimit()));
            }
            if (ObjectUtil.isNotNull(search.getSearchAfterKey())) {
                search.searchAfter(search.getSearchAfterKey());
            } else {
                //除特殊场景外，最多查询100条数据
                if (search.getLimit() > Const.QUERY_MAX_SIZE && !Objects.equals(0, search.getPageType())) {
                    search.setLimit(100L);
                }
                builder.from((int) ((search.getPage() - 1) * search.getLimit()));
            }
            //设置查询条数
            builder.size(search.getLimit().intValue());
            return builder;
        });
        SearchResponse<JSONObject> response = EsUtil.search(request);
        List<Hit<JSONObject>> hits = response.hits().hits();
        if (search.getPage() >= Const.QUERY_MAX_SIZE) {
            if (!hits.isEmpty()) {
                //处理searchAfter
                Hit<JSONObject> hit = hits.get(hits.size() - 1);
                Redis redis = ApplicationContextHolder.getBean(Redis.class);
                if (Const.QUERY_MAX_SIZE.equals(search.getPage())) {
                    redis.del(searchAfterKey);
                }
                int page = redis.getLength(searchAfterKey).intValue();
                if (search.getPage() - Const.QUERY_MAX_SIZE >= page) {
                    redis.rpush(searchAfterKey, hit.sort());
                }
                //缓存一个小时
                redis.expire(searchAfterKey, 3600);
            }
        }
        if (search.getAppendSearchAfter()) {
            search.searchAfter(hits.get(hits.size() - 1).sort());
        }
        BasePage<Map<String, Object>> basePage = new BasePage<>();
        basePage.setSize(search.getLimit());
        basePage.setList(handlerResult(hits, search));
        basePage.setTotal(response.hits().total() != null ? response.hits().total().value() : 0);
        basePage.setCurrent(search.getPage());
        return basePage;
    }

    default void exportExcel(T search, List<? extends ModelField> headList, HttpServletResponse response, Integer isXls, ExcelParseUtil.DataFunc dataFunc, FieldLabel fieldLabel) {
        search.setAppendSearchAfter(true);
        BasePage<Map<String, Object>> basePage = queryList(search, fieldLabel);
        handlerExcel(search, basePage);
        ExcelParseUtil.exportExcel(basePage.getList(), new ExcelParseUtil.ExcelParseService() {
            @Override
            public ExcelParseUtil.DataFunc getFunc() {
                if (ObjectUtil.isNotNull(dataFunc)) {
                    return dataFunc;
                }
                ExcelParseUtil.DataFunc func = defaultDataFunc();
                if (ObjectUtil.isNotNull(func)) {
                    return func;
                }
                return (record, headMap) -> {
                    for (String fieldName : headMap.keySet()) {
                        record.put(fieldName, FieldUtil.format2DbString(record.get(fieldName), headMap.get(fieldName), null));
                    }
                };
            }

            @Override
            public boolean isXlsx() {
                return true;
            }

            //继续获取数据
            @Override
            public List<Map<String, Object>> getNextData() {
                return queryList(search, fieldLabel).getList();
            }

            @Override
            public String getExcelName() {
                return fieldLabel.getRemarks();
            }
        }, headList, response, isXls, basePage.isLastPage());
    }

    /**
     * 导出excel
     *
     * @param search   筛选条件
     * @param headList 表头
     * @param response 响应
     * @param isXls    是否是xls格式
     * @param dataFunc 数据处理
     */
    default void exportExcel(T search, List<? extends ModelField> headList, HttpServletResponse response, Integer isXls, ExcelParseUtil.DataFunc dataFunc) {
        exportExcel(search, headList, response, isXls, dataFunc, getLabel());
    }

    /**
     * 大的搜索框的搜索字段
     *
     * @return fields
     */
    public String[] appendSearch();

    /**
     * 导出Excel默认DataFunc
     */
    default ExcelParseUtil.DataFunc defaultDataFunc() {
        return null;
    }

    /**
     * 导出Excel前置处理查询对象
     */
    default void handlerExcel(T search, BasePage<Map<String, Object>> basePage) {
    }

    /**
     * 查询ES数据前置处理查询对象
     */
    default void handlerSearch(T search) {
    }

    /**
     * 查询ES数据后置处理查询结果
     */
    default List<Map<String, Object>> handlerResult(List<Hit<JSONObject>> hits, T search) {
        return hits.stream().map(Hit::source).collect(Collectors.toList());
    }


    /**
     * 场景筛选
     *
     * @param search    筛选条件
     * @param boolQuery boolQuery
     */
    default void sceneQuery(T search, BoolQuery.Builder boolQuery) {

    }


    /**
     * 设置数据权限
     *
     * @param search    搜索条件
     * @param boolQuery boolQuery
     */
    default void setDataAuth(T search, BoolQuery.Builder boolQuery) {

    }


    /**
     * 保存数据
     *
     * @param model          模块信息
     * @param isRefreshIndex 是否刷新索引
     */
    default void savePage(ModelEntity model, boolean isRefreshIndex) {
        savePage(model, getLabel(), isRefreshIndex);
    }

    /**
     * 保存数据
     *
     * @param model      模块信息
     * @param fieldLabel 索引信息
     */
    default void savePage(ModelEntity model, FieldLabel fieldLabel, boolean isRefreshIndex) {
        List<? extends ModelField> modelFields = queryField();
        Map<String, Object> map = new HashMap<>(model.getEntity());
        model.getField().forEach(field -> {
            map.put(field.getFieldName(), field.getValue());
        });
        Long id = MapUtil.getLong(model.getEntity(), fieldLabel.getPrimaryKey());
        modelFields.forEach(modelField -> {
            FieldEnum fieldEnum = FieldEnum.parse(modelField.getType());
            //生成编号规则信息
            if (fieldEnum == FieldEnum.SERIAL_NUMBER && Objects.equals(0, modelField.getFieldType())) {
                String generateNumber = getBean(FieldNumberDataService.class).generateNumber(getLabel(), modelField, map);
                if (generateNumber != null) {
                    map.put(modelField.getFieldName(), generateNumber);
                }
            }
            //生成关注度数据
            if (fieldEnum == FieldEnum.ATTENTION && Objects.equals(0, modelField.getFieldType())) {
                getBean(FieldAttentionDataService.class).saveOrUpdate(getLabel(), modelField, map, id);
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
            if (FieldUtil.equalsByType(modelField.getType())) {
                Object value = map.remove(modelField.getFieldName());
                if (!ObjectUtil.isEmpty(value)) {
                    if (value instanceof String) {
                        map.put(modelField.getFieldName(), value.toString());
                    } else {
                        map.put(modelField.getFieldName(), JSON.toJSONString(value));
                    }
                } else {
                    map.put(modelField.getFieldName(), null);

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

        EsUtil.updateField(map, id, getLabel(), true, isRefreshIndex);
    }

    /**
     * 设置其他冗余字段
     *
     * @param map 当前map数据
     */
    default void setOtherField(Map<String, Object> map) {

    }

    /**
     * 创建查询条件
     *
     * @param search 筛选条件
     * @return data
     */
    default BoolQuery.Builder createQueryBuilder(T search) {
        BoolQuery.Builder boolQuery = QueryBuilders.bool();
        sceneQuery(search, boolQuery);
        setDataAuth(search, boolQuery);
        return boolQuery;
    }

    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     */
    default void deletePage(List<Long> ids) {
        deletePage(ids, getLabel());
    }

    /**
     * 根据ID列表删除数据
     *
     * @param ids        ids
     * @param fieldLabel 索引信息
     */
    default void deletePage(List<Long> ids, FieldLabel fieldLabel) {
        EsUtil.deleteData(ids, fieldLabel);
    }

    /**
     * 查询排序
     *
     * @param search    搜索条件
     * @param filedList 字段列表
     * @return 排序
     */
    default List<SortOptions> sortOptions(T search, List<? extends ModelField> filedList) {
        Optional<? extends ModelField> optionalModelFiled = filedList.stream().filter(modelFiled -> Objects.equals(StrUtil.toCamelCase(modelFiled.getFieldName()), search.getSortField())).findFirst();
        if (!optionalModelFiled.isPresent()) {
            search.setSortField("updateTime");
        }
        List<SortOptions> sortOptions = new ArrayList<>();
        sortOptions.add(SortOptions.of(builder -> {
            builder.field(FieldSort.of(sort -> {
                sort.field(search.getSortField());
                sort.order(Objects.equals(2, search.getOrder()) ? SortOrder.Asc : SortOrder.Desc);
                return sort;
            }));
            return builder;
        }));
        sortOptions.add(SortOptions.of(builder -> {
            builder.field(FieldSort.of(sort -> {
                sort.field("_id");
                sort.order(SortOrder.Asc);
                return sort;
            }));
            return builder;
        }));
        return sortOptions;
    }

    /**
     * 获取索引类型
     *
     * @return data
     */
    public FieldLabel getLabel();

    /**
     * 查询默认字段
     *
     * @return data
     */
    List<ModelField> queryField();
}
