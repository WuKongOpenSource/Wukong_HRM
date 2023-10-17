package com.kakarote.common.field.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.analysis.Normalizer;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.field.constant.EsFieldTypeEnum;
import com.kakarote.common.field.constant.RangeTypeEnum;
import com.kakarote.common.field.entity.FieldLabel;
import com.kakarote.common.field.entity.RangeEntity;
import com.kakarote.common.field.entity.SearchEntity;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.common.enums.FieldSearchEnum;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.entity.ModuleSimpleFieldBO;
import com.kakarote.core.entity.Search;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.feign.crm.entity.BiParams;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.utils.BiTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author elasticSearch的一些通用操作
 */
@Slf4j
public class EsUtil {

    private static ElasticsearchClient client;

    /**
     * 获取ElasticsearchRestTemplate对象
     *
     * @return ElasticsearchRestTemplate
     */
    public static ElasticsearchClient getClient() {
        if (client == null) {
            synchronized (EsUtil.class) {
                client = ApplicationContextHolder.getBean(ElasticsearchClient.class);
            }
        }
        return client;
    }


    /**
     * 批量提交es数据
     */
    public static void bulk(List<Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>>> operationList) {
        bulk(operationList, operationList.size());
    }


    /**
     * 批量提交es数据
     *
     * @param num 分批次提交，每次提交多少条
     */
    public static void bulk(List<Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>>> operationList, int num) {
        if (operationList.isEmpty()) {
            return;
        }
        BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        boolean submitted = false;
        int size = 0;
        for (Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>> fn : operationList) {
            bulkRequest.operations(fn);
            size++;
            if (size % num == 0) {
                bulk(bulkRequest.build());
                if (operationList.size() != size) {
                    bulkRequest = new BulkRequest.Builder();
                } else {
                    submitted = true;
                    break;
                }
            }
        }
        if (!submitted) {
            bulk(bulkRequest.build());
        }
    }


    /**
     * 批量提交es数据
     *
     * @param bulkRequest request
     */
    public static void bulk(BulkRequest bulkRequest) {
        bulk(bulkRequest, 3);
    }

    /**
     * @param bulkRequest request
     * @param retryNum    重试次数
     */
    private static void bulk(BulkRequest bulkRequest, int retryNum) {
        if (bulkRequest == null || bulkRequest.operations().isEmpty()) {
            return;
        }
        try {
            BulkResponse responses = getClient().bulk(bulkRequest);
            if (responses.errors()) {
                Set<String> ids = new HashSet<>();
                for (BulkResponseItem item : responses.items()) {
                    //修改文档忽略404异常
                    if (Objects.equals(SystemCodeEnum.SYSTEM_NO_FOUND.getCode(), item.status())) {
                        continue;
                    }
                    if (item.status() != 200 && item.error() != null) {
                        ids.add(item.id());
                        if (retryNum == 0) {
                            log.warn("bulk item error:{}", item.error());
                        }
                    }
                }
                if (retryNum > 0) {
                    BulkRequest.Builder builder = new BulkRequest.Builder();
                    boolean hasOperation = false;
                    for (BulkOperation operation : bulkRequest.operations()) {
                        String operationId = "";
                        if (operation.isCreate()) {
                            operationId = operation.create().id();
                        } else if (operation.isIndex()) {
                            operationId = operation.index().id();
                        } else if (operation.isUpdate()) {
                            operationId = operation.update().id();
                        } else if (operation.isDelete()) {
                            operationId = operation.delete().id();
                        }
                        if (ids.contains(operationId)) {
                            builder.operations(operation);
                            hasOperation = true;
                        }
                    }
                    if (hasOperation) {
                        log.warn("批量提交es数据重试:{}", retryNum);
                        bulk(builder.build(), --retryNum);
                    }
                }
            }
        } catch (IOException e) {
            log.error("bulk异常", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 获取BulkUpdateOperation
     */
    public static Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>> updateOperations(Object id, String index, Map<String, Object> map) {
        return updateOperations(id, index, map, false);
    }

    /**
     * 获取BulkUpdateOperation
     */
    public static Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>> updateOperations(Object id, String index, Map<String, Object> map, boolean docAsUpsert) {
        return setting -> {
            setting.update(i -> {
                i.id(id.toString());
                i.index(index);
                i.action(acc -> {
                    acc.doc(map);
                    acc.docAsUpsert(docAsUpsert);
                    return acc;
                });
                return i;
            });
            return setting;
        };
    }

    /**
     * 获取BulkIndexOperation
     */
    public static Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>> indexOperations(Object id, String index, Object document) {
        return setting -> {
            setting.index(i -> {
                i.id(id.toString());
                i.index(index);
                i.document(document);
                return i;
            });
            return setting;
        };
    }

    /**
     * 索引增加字段
     *
     * @param fieldName  字段名
     * @param property   es字段类型
     * @param fieldLabel 索引参数
     */
    public static void addField(String fieldName, Property property, FieldLabel fieldLabel) {
        try {
            Map<String, Property> object = Collections.singletonMap(fieldName, property);
            PutMappingRequest request = PutMappingRequest.of(setting -> {
                setting.index(fieldLabel.getIndex());
                setting.properties(object);
                return setting;
            });
            getClient().indices().putMapping(request);
        } catch (IOException e) {
            log.error("新增字段错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 项目默认的分词器
     *
     * @return IndexSettingsAnalysis
     */
    public static IndexSettingsAnalysis defaultAnalysis() {
        return IndexSettingsAnalysis.of(builder -> {
            builder.normalizer("lowercase_normalizer", new Normalizer.Builder().custom(normal -> {
                normal.charFilter(new ArrayList<>());
                normal.filter("lowercase");
                return normal;
            }).build());
            return builder;
        });
    }

    /**
     * 创建索引
     *
     * @param mappingMap 字段参数
     * @param fieldLabel 索引参数
     */
    public static void createIndex(Map<String, Property> mappingMap, IndexSettingsAnalysis settingsAnalysis, FieldLabel fieldLabel) {
        try {
            CreateIndexResponse createIndexResponse = getClient().indices().create(CreateIndexRequest.of(setting -> {
                setting.index(fieldLabel.getIndex());
                setting.settings(settings -> {
                    settings.numberOfShards("5");
                    settings.numberOfReplicas("0");
                    if (settingsAnalysis != null) {
                        settings.analysis(settingsAnalysis);
                    }
                    return settings;
                });
                if (fieldLabel.getAlias() != null) {
                    setting.aliases(fieldLabel.getAlias(), Alias.of(alias -> alias));
                }
                setting.mappings(mapping -> {
                    mapping.properties(mappingMap);
                    mapping.enabled(true);
                    return mapping;
                });
                return setting;
            }));
            if (createIndexResponse.acknowledged()) {
                log.info("创建索引成功,{}", fieldLabel.getIndex());
            } else {
                log.error("创建索引失败,{},{}", fieldLabel.getIndex(), createIndexResponse);
                throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error("创建索引失败,{}", fieldLabel.getIndex(), e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 索引是否存在
     *
     * @param fieldLabel 索引名称
     * @return true代表索引存在
     */
    public static boolean indexExist(FieldLabel fieldLabel) {
        try {
            BooleanResponse booleanResponse = getClient().indices().exists(i -> i.index(fieldLabel.getIndex()));
            return booleanResponse.value();
        } catch (IOException e) {
            log.error("查询索引是否存在错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 设置mapping
     */
    public static void putMapping(Map<String, Property> mappingMap, FieldLabel fieldLabel) {
        try {
            getClient().indices().putMapping(p -> {
                p.index(fieldLabel.getIndex());
                p.properties(mappingMap);
                return p;
            });
        } catch (IOException e) {
            log.error("设置mapping错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 获取mapping
     */
    public static Map<String, Property> getMapping(FieldLabel fieldLabel) {
        try {
            GetMappingResponse mapping = getClient().indices().getMapping(p -> {
                p.index(fieldLabel.getIndex());
                return p;
            });
            return mapping.result().get(fieldLabel.getIndex()).mappings().properties();
        } catch (IOException e) {
            log.error("获取mapping错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 刷新索引
     *
     * @param fieldLabel 索引名称
     */
    public static void refresh(FieldLabel fieldLabel) {
        try {
            getClient().indices().refresh(RefreshRequest.of(setting -> {
                setting.index(fieldLabel.getIndex());
                return setting;
            }));
        } catch (IOException e) {
            log.error("刷新索引错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 格式化字段类型
     *
     * @param fieldTypeEnum 字段类型
     * @return es字段类型
     */
    public static Property parseEsType(EsFieldTypeEnum fieldTypeEnum) {
        switch (fieldTypeEnum) {
            case DATETIME:
            case DATE:
                return DateProperty.of(setting -> {
                    setting.format("yyyy-MM-dd || yyyy-MM-dd HH:mm:ss || yyyy-MM");
                    return setting;
                })._toProperty();
            case NUMBER:
                return ScaledFloatNumberProperty.of(setting -> {
                    setting.scalingFactor(100D);
                    return setting;
                })._toProperty();
            case NESTED:
                return NestedProperty.of(setting -> {
                    setting.dynamic(DynamicMapping.True);
                    setting.enabled(true);
                    return setting;
                })._toProperty();
            case TEXT:
                return TextProperty.of(setting -> {
                    setting.analyzer("standard");
                    return setting;
                })._toProperty();
            default:
                return KeywordProperty.of(setting -> {
                    setting.ignoreAbove(200);
                    setting.normalizer("lowercase_normalizer");
                    setting.fields("sort", s -> {
                        s._custom("sort", new JSONObject().fluentPut("type", "icu_collation_keyword").fluentPut("language", "zh").fluentPut("country", "CN"));
                        return s;
                    });
                    return setting;
                })._toProperty();
        }
    }


    /**
     * 字段类型转换ES字段类型
     *
     * @param type 字段类型
     * @return es字段类型
     */
    public static EsFieldTypeEnum fieldType2EsType(Integer type) {
        FieldEnum fieldEnum = FieldEnum.parse(type);
        return fieldEnum2EsType(fieldEnum);
    }

    /**
     * 字段类型转换ES字段类型
     *
     * @param fieldEnum 字段类型
     * @return es字段类型
     */
    public static EsFieldTypeEnum fieldEnum2EsType(FieldEnum fieldEnum) {
        switch (fieldEnum) {
            case TEXTAREA:
            case RTF:
                return EsFieldTypeEnum.TEXT;
            case DATE:
                return EsFieldTypeEnum.DATE;
            case DATETIME:
                return EsFieldTypeEnum.DATETIME;
            case NUMBER:
            case FLOATNUMBER:
            case BOOLEAN_VALUE:
            case PERCENT:
            case ATTENTION:
                return EsFieldTypeEnum.NUMBER;
            case SELECT:
            case CHECKBOX:
            case AREA_POSITION:
            case DATE_INTERVAL:
            case TAG:
            case DETAIL_TABLE:
                return EsFieldTypeEnum.NESTED;
            default:
                return EsFieldTypeEnum.KEYWORD;
        }
    }

    public static Property field2EsType(FieldEnum fieldEnum) {
        switch (fieldEnum) {
            case TEXTAREA:
            case PIC:
                return TextProperty.of(setting -> {
                    setting.analyzer("standard");
                    return setting;
                })._toProperty();
            case DATETIME:
            case DATE:
                return DateProperty.of(setting -> {
                    setting.format("yyyy-MM-dd || yyyy-MM-dd HH:mm:ss || yyyy-MM");
                    return setting;
                })._toProperty();
            case NUMBER:
                return ScaledFloatNumberProperty.of(setting -> {
                    setting.scalingFactor(1D);
                    return setting;
                })._toProperty();
            case FLOATNUMBER:
                return ScaledFloatNumberProperty.of(setting -> {
                    setting.scalingFactor(100D);
                    return setting;
                })._toProperty();
            case INTENTIONAL_BUSINESS:
            case SUPERIOR_CUSTOMER:
            case CUSTOMER_RELATIONS:
                return NestedProperty.of(setting -> {
                    setting.dynamic(DynamicMapping.True);
                    setting.enabled(true);
                    return setting;
                })._toProperty();
            default:
                return KeywordProperty.of(setting -> {
                    setting.ignoreAbove(200);
                    setting.normalizer("lowercase_normalizer");
                    setting.fields("sort", s -> {
                        s._custom("sort", new JSONObject().fluentPut("type", "icu_collation_keyword").fluentPut("language", "zh").fluentPut("country", "CN"));
                        return s;
                    });
                    return setting;
                })._toProperty();
        }
    }


    /**
     * 修改多条数据中多个字段的值
     *
     * @param dataMap    数据map
     * @param ids        ids
     * @param fieldLabel 索引名称
     */
    public static void updateField(Map<String, Object> dataMap, List<?> ids, FieldLabel fieldLabel) {
        updateField(dataMap, ids, fieldLabel, false);
    }


    /**
     * 修改多条数据中多个字段的值
     *
     * @param dataMap    数据map
     * @param ids        ids
     * @param fieldLabel 索引名称
     */
    public static void updateField(Map<String, Object> dataMap, List<?> ids, FieldLabel fieldLabel, boolean docAsUpsert) {
        if (ids.isEmpty()) {
            return;
        }
        BulkRequest.Builder builder = new BulkRequest.Builder();
        if (fieldLabel.isRefreshIndex()) {
            builder.refresh(Refresh.WaitFor);
        }
        for (Object id : ids) {
            builder.operations(setting -> {
                setting.update(i -> {
                    i.id(id.toString());
                    i.index(fieldLabel.getIndex());
                    i.action(acc -> {
                        acc.doc(dataMap);
                        acc.docAsUpsert(docAsUpsert);
                        return acc;
                    });
                    return i;
                });
                return setting;
            });
        }
        bulk(builder.build());
    }

    /**
     * 修改一条数据中多个字段的值
     *
     * @param dataMap    数据map
     * @param id         id
     * @param fieldLabel 索引名称
     */
    public static void updateField(Map<String, Object> dataMap, Object id, FieldLabel fieldLabel) {
        updateField(dataMap, id, fieldLabel, false);
    }

    /**
     * 修改一条数据中多个字段的值
     *
     * @param dataMap     数据map
     * @param id          id
     * @param fieldLabel  索引名称
     * @param docAsUpsert 索引不存在是否新增
     */
    public static void updateField(Map<String, Object> dataMap, Object id, FieldLabel fieldLabel, boolean docAsUpsert) {
        updateField(dataMap, id, fieldLabel, docAsUpsert, fieldLabel.isRefreshIndex());
    }


    /**
     * 修改一条数据中多个字段的值
     *
     * @param dataMap        数据map
     * @param id             id
     * @param fieldLabel     索引名称
     * @param isRefreshIndex 是否刷新索引
     */
    public static void updateField(Map<String, Object> dataMap, Object id, FieldLabel fieldLabel, boolean docAsUpsert, boolean isRefreshIndex) {
        try {
            getClient().update(UpdateRequest.of(setting -> {
                setting.id(id.toString());
                setting.index(fieldLabel.getIndex());
                setting.doc(dataMap);
                setting.docAsUpsert(docAsUpsert);
                if (isRefreshIndex) {
                    setting.refresh(Refresh.WaitFor);
                }
                return setting;
            }), JSONObject.class);
        } catch (IOException e) {
            log.error("修改字段错误", e);
        }
    }


    /**
     * 保存单条数据
     *
     * @param objectMap  数据对象
     * @param fieldLabel 索引信息
     */
    public static void saveData(Map<String, Object> objectMap, FieldLabel fieldLabel) {
        try {
            getClient().index(IndexRequest.of(data -> {
                Object key = objectMap.get(fieldLabel.getPrimaryKey());
                if (key != null) {
                    data.id(key.toString());
                }
                data.index(fieldLabel.getIndex());
                data.document(objectMap);
                if (fieldLabel.isRefreshIndex()) {
                    data.refresh(Refresh.WaitFor);
                }
                return data;
            }));
        } catch (IOException e) {
            log.error("保存数据错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 保存数据集,此操作不会刷新索引
     *
     * @param valueList  数据集
     * @param fieldLabel 索引名称
     * @param isAdd      是否是新增
     */
    public static void saveData(List<Map<String, Object>> valueList, FieldLabel fieldLabel, boolean isAdd) {
        if (valueList == null || valueList.isEmpty()) {
            return;
        }
        if (valueList.size() == 1) {
            saveData(valueList.get(0), fieldLabel);
        } else {
            BulkRequest.Builder builder = new BulkRequest.Builder();
            if (fieldLabel.isRefreshIndex()) {
                builder.refresh(Refresh.WaitFor);
            }
            builder.index(fieldLabel.getIndex());
            for (Map<String, Object> value : valueList) {
                builder.operations(setting -> {
                    Object key = value.get(fieldLabel.getPrimaryKey());
                    if (isAdd) {
                        setting.create(i -> {
                            if (key != null) {
                                i.id(key.toString());
                            }
                            i.index(fieldLabel.getIndex());
                            i.document(value);
                            return i;
                        });
                    } else {
                        setting.update(i -> {
                            if (key != null) {
                                i.id(key.toString());
                            }
                            i.index(fieldLabel.getIndex());
                            i.action(acc -> {
                                acc.doc(value);
                                acc.docAsUpsert(false);
                                return acc;
                            });
                            return i;
                        });
                    }
                    return setting;
                });
            }
            bulk(builder.build());
        }

    }

    /**
     * 删除数据
     *
     * @param ids        id集合
     * @param fieldLabel 索引信息
     */
    public static void deleteData(List<?> ids, FieldLabel fieldLabel) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        if (ids.size() == 1) {
            try {
                getClient().delete(DeleteRequest.of(setting -> {
                    setting.id(ids.get(0).toString());
                    setting.index(fieldLabel.getIndex());
                    if (fieldLabel.isRefreshIndex()) {
                        setting.refresh(Refresh.WaitFor);
                    }
                    return setting;
                }));
            } catch (IOException e) {
                log.error("删除数据错误", e);
            }
            return;
        }
        BulkRequest.Builder builder = new BulkRequest.Builder();
        if (fieldLabel.isRefreshIndex()) {
            builder.refresh(Refresh.WaitFor);
        }
        for (Object id : ids) {
            builder.operations(setting -> {
                setting.delete(i -> {
                    i.id(id.toString());
                    i.index(fieldLabel.getIndex());
                    return i;
                });
                return setting;
            });
        }
        bulk(builder.build());
    }

    /**
     * 根据条件删除
     */
    public static void deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        try {
            getClient().deleteByQuery(deleteByQueryRequest);
        } catch (IOException e) {
            log.error("根据条件删除错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 根据id查询接口
     *
     * @param id    ID
     * @param index 索引
     * @return 查询结果
     */
    public static GetResponse<JSONObject> getById(Object id, String index) {
        try {
            return getClient().get(GetRequest.of(b -> {
                b.id(id.toString());
                b.index(index);
                return b;
            }), JSONObject.class);
        } catch (IOException e) {
            log.error("查询错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 查询接口
     *
     * @param request 查询参数
     * @return 查询结果
     */
    public static SearchResponse<JSONObject> search(SearchRequest request) {
        try {
            return getClient().search(request, JSONObject.class);
        } catch (IOException e) {
            log.error("查询错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 根据查询条件查询数量接口
     *
     * @param query      查询条件
     * @param fieldLabel 索引名称
     * @return 查询结果
     */
    public static long count(Query query, FieldLabel fieldLabel) {
        try {
            return getClient().count(CountRequest.of(request -> {
                request.query(query);
                request.index(fieldLabel.getIndex());
                return request;
            })).count();
        } catch (IOException e) {
            log.error("查询错误", e);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
    }

    /**
     * 解析字段值
     *
     * @param value 值
     */
    public static FieldValue parseFieldValue(Object value) {
        FieldValue fieldValue = null;
        if (value instanceof String) {
            fieldValue = FieldValue.of((String) value);
        } else if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
            fieldValue = FieldValue.of(((Number) value).longValue());
        } else if (value instanceof Boolean) {
            fieldValue = FieldValue.of((boolean) value);
        } else if (value instanceof Double || value instanceof Float) {
            fieldValue = FieldValue.of(((Number) value).doubleValue());
        }
        return fieldValue;
    }

    /**
     * QueryBuilders.termQuery
     */
    public static Query termQuery(String field, Object value) {
        return QueryBuilders.term(b -> {
            b.field(field);
            b.value(parseFieldValue(value));
            return b;
        });
    }

    /**
     * QueryBuilders.termsQuery
     */
    public static Query termsQuery(String field, Collection<?> values) {
        List<FieldValue> fieldValueList = new ArrayList<>();
        for (Object value : values) {
            fieldValueList.add(parseFieldValue(value));
        }
        return QueryBuilders.terms(b -> {
            b.field(field);
            b.terms(t -> t.value(fieldValueList));
            return b;
        });
    }

    /**
     * QueryBuilders.existsQuery
     */
    public static Query existsQuery(String field) {
        return QueryBuilders.exists(b -> {
            b.field(field);
            return b;
        });
    }

    /**
     * QueryBuilders.prefixQuery
     */
    public static Query prefixQuery(String field, String value) {
        return QueryBuilders.prefix(b -> {
            b.field(field);
            b.value(value);
            return b;
        });
    }

    /**
     * QueryBuilders.wildcardQuery
     */
    public static Query wildcardQuery(String field, String value) {
        return QueryBuilders.wildcard(b -> {
            b.field(field);
            b.value(value);
            return b;
        });
    }

    /**
     * QueryBuilders.scriptQuery
     */
    public static Query scriptQuery(String source, Map<String, Object> map) {
        Script script = new Script.Builder().inline(b -> {
            b.lang("painless");
            for (String key : map.keySet()) {
                Object value = map.get(key);
                b.params(key, JsonData.of(value));
            }
            b.source(source);
            return b;
        }).build();
        return QueryBuilders.script(b -> {
            b.script(script);
            return b;
        });
    }

    /**
     * QueryBuilders.nestedQuery
     */
    public static Query nestedQuery(String path, Query query) {
        return nestedQuery(path, query, ChildScoreMode.None);
    }


    /**
     * 创建es排序
     *
     * @param order  排序方式
     * @param fields 排序字段
     * @return
     */
    public static SortOptions sort(SortOrder order, String... fields) {
        return SortOptionsBuilders.field(b -> {
            b.order(order);
            for (String field : fields) {
                b.field(field);
            }
            return b;
        });
    }

    /**
     * QueryBuilders.nestedQuery
     */
    public static Query nestedQuery(String path, Query query, ChildScoreMode scoreMode) {
        return QueryBuilders.nested(b -> {
            b.path(path);
            b.query(query);
            b.scoreMode(scoreMode);
            return b;
        });
    }


    public static Query rangeQuery(String fieldName, List<RangeEntity> data) {
        return QueryBuilders.range(b -> {
            b.queryName(String.format("%sSize", fieldName));
            for (RangeEntity rangeEntity : data) {
                switch (rangeEntity.getType()) {
                    case GT:
                        b.gt(JsonData.of(rangeEntity.getValue()));
                        break;
                    case GTE:
                        b.gte(JsonData.of(rangeEntity.getValue()));
                        break;
                    case LT:
                        b.lt(JsonData.of(rangeEntity.getValue()));
                        break;
                    case LTE:
                        b.lte(JsonData.of(rangeEntity.getValue()));
                        break;
                }
            }
            return b;
        });
    }

    /**
     * QueryBuilders.rangeQuery
     */
    public static Query rangeQuery(String fieldName, Object value, RangeTypeEnum rangeTypeEnum) {
        return QueryBuilders.range(b -> {
            b.queryName(String.format("%sSize", fieldName));
            switch (rangeTypeEnum) {
                case GT:
                    b.gt(JsonData.of(value));
                    break;
                case GTE:
                    b.gte(JsonData.of(value));
                    break;
                case LT:
                    b.lt(JsonData.of(value));
                    break;
                case LTE:
                    b.lte(JsonData.of(value));
                    break;
            }
            return b;
        });

    }


    /**
     * QueryBuilders.matchQuery
     */
    public static Query matchQuery(String field, Object value) {
        return QueryBuilders.match(b -> {
            b.field(field);
            b.query(parseFieldValue(value));
            return b;
        });
    }

    /**
     * QueryBuilders.idsQuery
     */
    public static Query idsQuery(List<?> value) {
        return QueryBuilders.ids(b -> {
            List<String> ids = value.stream().map(TypeUtils::castToString).collect(Collectors.toList());
            b.values(ids);
            return b;
        });
    }

    /**
     * QueryBuilders.idsQuery
     */
    public static Query idsQuery(Object... value) {
        return QueryBuilders.ids(b -> {
            List<String> list = Arrays.stream(value).map(TypeUtils::castToString).collect(Collectors.toList());
            b.values(list);
            return b;
        });
    }

    /**
     * 追加搜索条件
     *
     * @param search    搜索条件
     * @param boolQuery boolQuery
     */
    public static void appendSearchCondition(SearchEntity search, BoolQuery.Builder boolQuery) {

    }


    /**
     * 检查搜索条件是否存在错误
     *
     * @param search 搜索条件
     */
    private static void checkError(Search search) {
        if (search.getSearchEnum() == null || search.getName() == null) {
            throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        if (search.getSearchEnum() == FieldSearchEnum.IS_NULL || search.getSearchEnum() == FieldSearchEnum.IS_NOT_NULL) {
            return;
        }
        if (search.getValues() == null || search.getValues().isEmpty()) {
            throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
    }

    /**
     * 普通文本类型的es搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void textSearch(Search search, BoolQuery.Builder queryBuilder) {
        if (!checkSearchData(search)) {
            return;
        }
        ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
        String name = "";
        if (ObjectUtil.isNotNull(detailTableField)) {
            name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
        }
        switch (search.getSearchEnum()) {
            case IS:
                if (ObjectUtil.equal(search.getName(), "isWxUser") && search.getValues().contains("0")) {
                    BoolQuery.Builder builder = QueryBuilders.bool();
                    builder.should(termsQuery(search.getName(), search.getValues()));
                    builder.should(QueryBuilders.bool().mustNot(existsQuery(search.getName())).build()._toQuery());
                    queryBuilder.filter(builder.build()._toQuery());
                } else if (ObjectUtil.isNotNull(detailTableField)) {
                    queryBuilder.filter(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.termsQuery(name, search.getValues())));
                } else {
                    queryBuilder.filter(termsQuery(search.getName(), search.getValues()));
                }
                break;
            case IS_NOT:
                if (ObjectUtil.equal(search.getName(), "isWxUser") && search.getValues().contains("0")) {
                    BoolQuery.Builder builder = QueryBuilders.bool();
                    builder.should(termsQuery(search.getName(), search.getValues()));
                    builder.should(QueryBuilders.bool().mustNot(existsQuery(search.getName())).build()._toQuery());
                    queryBuilder.mustNot(builder.build()._toQuery());
                } else if (ObjectUtil.isNotNull(detailTableField)) {
                    queryBuilder.mustNot(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.termsQuery(name, search.getValues())));
                } else {
                    queryBuilder.mustNot(termsQuery(search.getName(), search.getValues()));
                }
                break;
            case PREFIX:
                if (ObjectUtil.isNotNull(detailTableField)) {
                    if (search.getValues().size() == 1) {
                        queryBuilder.filter(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.prefixQuery(name, search.getValues().get(0))));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.prefixQuery(name, value)));
                        }
                        queryBuilder.filter(boolQuery.build()._toQuery());
                    }
                } else {
                    if (search.getValues().size() == 1) {
                        queryBuilder.filter(prefixQuery(search.getName(), search.getValues().get(0)));
                        queryBuilder.filter(prefixQuery(search.getName(), search.getValues().get(0)));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(prefixQuery(search.getName(), value));
                        }
                        queryBuilder.filter(boolQuery.build()._toQuery());
                    }
                }
                break;
            case SUFFIX:
            case CONTAINS:
                String suffix = search.getSearchEnum() == FieldSearchEnum.SUFFIX ? "" : "*";
                if (ObjectUtil.isNotNull(detailTableField)) {
                    if (search.getValues().size() == 1) {
                        queryBuilder.filter(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.wildcardQuery(name, "*" + search.getValues().get(0) + suffix)));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.wildcardQuery(name, "*" + value + suffix)));
                        }
                        queryBuilder.filter(boolQuery.build()._toQuery());
                    }
                } else {
                    if (search.getValues().size() == 1) {
                        queryBuilder.filter(EsUtil.wildcardQuery(search.getFieldName(), "*" + search.getValues().get(0) + suffix));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(EsUtil.wildcardQuery(search.getFieldName(), "*" + value + suffix));
                        }
                        queryBuilder.filter(boolQuery.build()._toQuery());
                    }
                }
                break;
            case NOT_CONTAINS:
                if (ObjectUtil.isNotNull(detailTableField)) {
                    if (search.getValues().size() == 1) {
                        queryBuilder.mustNot(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.wildcardQuery(name, "*" + search.getValues().get(0) + "*")));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(EsUtil.nestedQuery(detailTableField.getFieldName(), EsUtil.wildcardQuery(name, "*" + value + "*")));
                        }
                        queryBuilder.mustNot(boolQuery.build()._toQuery());
                    }
                } else {
                    if (search.getValues().size() == 1) {
                        queryBuilder.mustNot(EsUtil.wildcardQuery(search.getFieldName(), "*" + search.getValues().get(0) + "*"));
                    } else {
                        BoolQuery.Builder boolQuery = QueryBuilders.bool();
                        for (String value : search.getValues()) {
                            boolQuery.should(EsUtil.wildcardQuery(search.getFieldName(), "*" + value + "*"));
                        }
                        queryBuilder.mustNot(boolQuery.build()._toQuery());
                    }
                }
                break;
            case IS_NULL:
                isNullSearch(search, queryBuilder);
                break;
            case IS_NOT_NULL:
                isNotNullSearch(search, queryBuilder);
                break;
            default:
                break;
        }
    }

    /**
     * 下拉选es 搜索
     *
     * @param search
     * @param queryBuilder
     */
    public static void selectSearch(Search search, BoolQuery.Builder queryBuilder) {
        String nestedPath = search.getFieldName();
        String name = String.join(".", nestedPath, "value");
        if (CollUtil.isEmpty(search.getValues())) {
            queryBuilder.mustNot(EsUtil.nestedQuery(search.getFieldName(), EsUtil.existsQuery(name)));
        }
        switch (search.getSearchEnum()) {
            case IS: {
                List<JSONObject> nestedList = search.getValues().stream().map(JSON::parseObject).collect(Collectors.toList());
                BoolQuery.Builder boolQuery = QueryBuilders.bool();
                for (JSONObject nested : nestedList) {
                    boolQuery.should(EsUtil.nestedQuery(search.getFieldName(), EsUtil.termQuery(name, nested.get("value"))));
                }
                queryBuilder.filter(boolQuery.build()._toQuery());
                break;
            }
            case IS_NOT: {
                List<JSONObject> nestedList = search.getValues().stream().map(JSON::parseObject).collect(Collectors.toList());
                BoolQuery.Builder boolQuery = QueryBuilders.bool();
                for (JSONObject nested : nestedList) {
                    boolQuery.should(EsUtil.nestedQuery(search.getFieldName(), EsUtil.termQuery(name, nested.get("value"))));
                }
                queryBuilder.mustNot(boolQuery.build()._toQuery());
                break;
            }
            case IS_NULL: {
                queryBuilder.mustNot(EsUtil.nestedQuery(search.getFieldName(), EsUtil.existsQuery(name)));
                break;
            }
            case IS_NOT_NULL: {
                queryBuilder.must(EsUtil.nestedQuery(search.getFieldName(), EsUtil.existsQuery(name)));
                break;
            }
            default:
                break;
        }
    }

    /**
     * 多选类型的es搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void checkboxSearch(Search search, BoolQuery.Builder queryBuilder) {
        if (!checkSearchData(search)) {
            return;
        }
        //临时逻辑
        String name = search.getName() + ".sort";
        switch (search.getSearchEnum()) {
            case IS_NOT: {
                BoolQuery.Builder builder = QueryBuilders.bool();
                Map<String, Object> map = new HashMap<>();
                map.put("key", name);
                map.put("size", search.getValues().size());
                builder.should(scriptQuery("doc[params.key].size() != params.size", map));
                builder.should(QueryBuilders.bool().mustNot(termsQuery(name, search.getValues())).build()._toQuery());
                queryBuilder.filter(builder.build()._toQuery());
                break;
            }
            case IS: {
                Map<String, Object> map = new HashMap<>();
                map.put("key", name);
                map.put("size", search.getValues().size());
                queryBuilder.filter(scriptQuery("doc[params.key].size() == params.size", map));
                queryBuilder.filter(termsQuery(name, search.getValues()));
                break;
            }
            case CONTAINS:
                queryBuilder.filter(termsQuery(name, search.getValues()));
                break;
            case NOT_CONTAINS: {
                queryBuilder.mustNot(termsQuery(name, search.getValues()));
                break;
            }
            case IS_NULL:
                isNullSearch(search, queryBuilder);
                break;
            case IS_NOT_NULL:
                isNotNullSearch(search, queryBuilder);
                break;
            default:
                break;
        }
    }

    /**
     * 数字类型的es搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void numberSearch(Search search, BoolQuery.Builder queryBuilder) {
        if (!checkSearchData(search)) {
            return;
        }
        //范围筛选只能为数字
        for (String value : search.getValues()) {
            if (!NumberUtil.isNumber(value)) {
                throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
            }
        }
        switch (search.getSearchEnum()) {
            case IS:
                queryBuilder.filter(termQuery(search.getName(), search.getValues().get(0)));
                break;
            case IS_NOT:
                queryBuilder.mustNot(termQuery(search.getName(), search.getValues().get(0)));
                break;
            case GT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gt(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case EGT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gte(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case LT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.lt(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case ELT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.lte(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case RANGE: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gte(JsonData.of(search.getValues().get(0)));
                    b.lte(JsonData.of(search.getValues().get(1)));
                    return b;
                }));
                break;
            }
            case IS_NULL:
                isNullSearch(search, queryBuilder);
                break;
            case IS_NOT_NULL:
                isNotNullSearch(search, queryBuilder);
                break;
            default:
                break;
        }
    }

    /**
     * 时间类型的es搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void dateSearch(Search search, BoolQuery.Builder queryBuilder, FieldEnum fieldEnum) {
        List<String> values = search.getValues();
        if (!checkSearchData(search)) {
            return;
        }
        switch (search.getSearchEnum()) {
            case IS: {
                queryBuilder.filter(termQuery(search.getName(), search.getValues().get(0)));
                break;
            }
            case IS_NOT: {
                queryBuilder.mustNot(termQuery(search.getName(), search.getValues().get(0)));
                break;
            }
            case IS_NULL:
                isNullSearch(search, queryBuilder);
                break;
            case IS_NOT_NULL:
                isNotNullSearch(search, queryBuilder);
                break;
            case GT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gt(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case EGT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gte(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case LT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.lt(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case ELT: {
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.lte(JsonData.of(search.getValues().get(0)));
                    return b;
                }));
                break;
            }
            case RANGE: {
                BiParams biParams = new BiParams();
                if (search.getValues().size() > 1) {
                    biParams.setStartTime(values.get(0));
                    biParams.setEndTime(values.get(1));
                } else {
                    biParams.setType(search.getValues().get(0));
                }
                BiTimeUtil.BiTimeEntity timeEntity = BiTimeUtil.analyzeTime(biParams);
                Date beginDate = timeEntity.getBeginDate();
                Date endDate = timeEntity.getEndDate();
                queryBuilder.filter(QueryBuilders.range(b -> {
                    b.field(search.getName());
                    b.gte(JsonData.of(fieldEnum == FieldEnum.DATETIME ? DateUtil.formatDateTime(beginDate) : DateUtil.formatDate(beginDate)));
                    b.lte(JsonData.of(fieldEnum == FieldEnum.DATETIME ? DateUtil.formatDateTime(endDate) : DateUtil.formatDate(endDate)));
                    return b;
                }));
                break;
            }
        }
    }

    /**
     * 判断搜索条件是否合法
     *
     * @param search 搜索条件
     * @return true为合法
     */
    public static boolean checkSearchData(Search search) {
        if (search.getValues() == null || search.getValues().isEmpty()) {
            return search.getSearchEnum() == FieldSearchEnum.IS_NULL || search.getSearchEnum() == FieldSearchEnum.IS_NOT_NULL;
        }
        return true;
    }

    /**
     * 搜索用户信息
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void userSearch(Search search, BoolQuery.Builder queryBuilder) {
        if (!checkSearchData(search)) {
            return;
        }
        switch (search.getSearchEnum()) {
            case CONTAINS:
                queryBuilder.filter(termsQuery(search.getName(), search.getValues()));
                break;
            case NOT_CONTAINS:
                queryBuilder.mustNot(termsQuery(search.getName(), search.getValues()));
                break;
            case IS_NULL:
                isNullSearch(search, queryBuilder);
                break;
            case IS_NOT_NULL:
                isNotNullSearch(search, queryBuilder);
                break;
        }
    }

    /**
     * 为空搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void isNullSearch(Search search, BoolQuery.Builder queryBuilder) {
        FieldEnum fieldEnum = FieldEnum.parse(search.getFormType());
        if (Arrays.asList(FieldEnum.DATETIME, FieldEnum.DATE, FieldEnum.NUMBER, FieldEnum.FLOATNUMBER).contains(fieldEnum)) {
            queryBuilder.mustNot(existsQuery(search.getName()));
        } else {
            BoolQuery.Builder builder = QueryBuilders.bool();
            builder.should(termQuery(search.getName(), ""));
            builder.should(QueryBuilders.bool().mustNot(existsQuery(search.getName())).build()._toQuery());
            queryBuilder.filter(builder.build()._toQuery());
        }
    }

    /**
     * 不为空搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void isNotNullSearch(Search search, BoolQuery.Builder queryBuilder) {
        queryBuilder.filter(existsQuery(search.getName()));
        FieldEnum fieldEnum = FieldEnum.parse(search.getFormType());
        if (!Arrays.asList(FieldEnum.DATETIME, FieldEnum.DATE, FieldEnum.NUMBER, FieldEnum.FLOATNUMBER).contains(fieldEnum)) {
            queryBuilder.mustNot(termQuery(search.getName(), ""));
        }
        //tag类型的数据空数组也属于空，这块特殊处理下
        if (FieldEnum.TAG == fieldEnum) {
            queryBuilder.mustNot(termQuery(search.getName(), "[]"));
        }
    }

    /**
     * 嵌套类型搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void nestedSearch(Search search, BoolQuery.Builder queryBuilder) {
        List<String> values = search.getValues();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        switch (search.getSearchEnum()) {
            case IS:
            case CONTAINS:
                if (!values.isEmpty()) {
                    for (String value : search.getValues()) {
                        if (FieldEnum.INTENTIONAL_BUSINESS.getFormType().equals(search.getFormType())) {
                            boolQueryBuilder.must(wildcardQuery(search.getName() + ".productName.keyword", "*" + value + "*"));
                        } else {
                            boolQueryBuilder.must(wildcardQuery(search.getName() + ".name.keyword", "*" + value + "*"));
                        }
                    }
                }
                break;
            case IS_NOT:
            case NOT_CONTAINS:
                if (!values.isEmpty()) {
                    for (String value : search.getValues()) {
                        if (FieldEnum.INTENTIONAL_BUSINESS.getFormType().equals(search.getFormType())) {
                            boolQueryBuilder.mustNot(wildcardQuery(search.getName() + ".productName.keyword", "*" + value + "*"));
                        } else {
                            boolQueryBuilder.mustNot(wildcardQuery(search.getName() + ".name.keyword", "*" + value + "*"));
                        }
                    }
                }
                break;
            default:
                break;
        }
        queryBuilder.filter(nestedQuery(search.getName(), boolQueryBuilder.build()._toQuery()));
    }
}
