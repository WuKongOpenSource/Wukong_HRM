package com.kakarote.hrm.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.field.utils.EsUtil;
import com.kakarote.common.field.utils.FieldUtil;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.hrm.constant.HrmEnum;
import com.kakarote.hrm.entity.BO.HrmFieldDataBO;
import com.kakarote.hrm.entity.PO.HrmEmployeeField;
import com.kakarote.hrm.entity.VO.HrmModelFiledVO;
import com.kakarote.hrm.mapper.HrmEmployeeFieldMapper;
import com.kakarote.hrm.service.IHrmEmployeeFieldService;
import com.kakarote.hrm.service.impl.HrmAppraisalEmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@Slf4j
public class InitEsIndexRunner implements ApplicationRunner {
    static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());

    @Resource
    private IHrmEmployeeFieldService hrmEmployeeFieldService;

    @Override
    public void run(ApplicationArguments args) {

        for (HrmEnum value : HrmEnum.values()) {
            if (!EsUtil.indexExist(value)) {
                initData(value);
                log.info("es {} index init success!", value.getIndex());
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData(HrmEnum hrmEnum) {
        /*
            初始化es索引,获取固定字段以及值
         */
        Map<String, Integer> typeMap = initField(hrmEnum);
        HrmEmployeeFieldMapper employeeFieldMapper = (HrmEmployeeFieldMapper) hrmEmployeeFieldService.getBaseMapper();
        Long lastId = 0L;
        Map<String, Object> dataMap = new HashMap<>(8);
        String table = hrmEnum.getTableName();
        dataMap.put("table", table);
        dataMap.put("tableName", hrmEnum.getTableName());
        dataMap.put("lastId", lastId);
        dataMap.put("label", hrmEnum.getType());
        List<Future<Boolean>> futureList = new LinkedList<>();
        while (true) {
            List<Map<String, Object>> mapList = employeeFieldMapper.initData(dataMap);
            if (mapList.size() == 0) {
                break;
            }
            Object o = mapList.get(mapList.size() - 1).get(hrmEnum.getTableId());
            lastId = TypeUtils.castToLong(o);
            dataMap.put("lastId", lastId);
            log.warn("最后数据id:{},线程id{}", lastId, Thread.currentThread().getName());
            futureList.add(THREAD_POOL.submit(new SaveES(hrmEnum, typeMap, mapList)));
        }
        /*
          等待所有数据处理完成,再进行下一步
         */
        for (Future<Boolean> future : futureList) {
            try {
                Boolean result = future.get();
                log.info("数据处理完成：{}", result);
            } catch (InterruptedException | ExecutionException e) {
                throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
            }
        }
        EsUtil.refresh(hrmEnum);
    }

    private Map<String, Integer> initField(HrmEnum hrmEnum) {
        String index = hrmEnum.getIndex();
        if (EsUtil.indexExist(hrmEnum)) {
            log.error("索引存在:{}", index);
            throw new CrmException(SystemCodeEnum.SYSTEM_SERVER_ERROR);
        }
        /*
          查询所有自定义字段
         */
        List<HrmModelFiledVO> hrmModelFiledList = queryInitField(hrmEnum.getType());
        Map<String, Property> properties = new HashMap<>(hrmModelFiledList.size());
        Map<String, Integer> typeMap = new HashMap<>(16);
        hrmModelFiledList.forEach(hrmField -> {
            properties.put(hrmField.getFieldName(), EsUtil.field2EsType(FieldEnum.parse(hrmField.getType())));
            if (!Objects.equals(0, hrmField.getFieldType())) {
                typeMap.put(hrmField.getFieldName(), hrmField.getType());
            }
        });
        EsUtil.createIndex(properties, EsUtil.defaultAnalysis(), hrmEnum);
        return typeMap;
    }

    private List<HrmModelFiledVO> queryInitField(Integer type) {

        //查询自定义字段表中定义的字段
        LambdaQueryWrapper<HrmEmployeeField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrmEmployeeField::getLabel, type).orderByAsc(HrmEmployeeField::getSorting);
        wrapper.groupBy(HrmEmployeeField::getFieldName);
        List<HrmEmployeeField> hrmFieldList = hrmEmployeeFieldService.list(wrapper);
        HrmEnum hrmEnum = HrmEnum.parse(type);

        List<HrmModelFiledVO> filedList = hrmFieldList.stream().map(field -> BeanUtil.copyProperties(field, HrmModelFiledVO.class)).collect(Collectors.toList());
        switch (hrmEnum) {
            case APPRAISAL_EMPLOYEE: {
                filedList.add(new HrmModelFiledVO("employeeName", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("stageUsersName", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("stageStatusName", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("appraisalStatusName", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("deptName", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("stageSort", FieldEnum.TEXT, 1));
                filedList.add(new HrmModelFiledVO("createTime", FieldEnum.DATETIME, 1));
                filedList.add(new HrmModelFiledVO("updateTime", FieldEnum.DATETIME, 1));
                filedList.add(new HrmModelFiledVO("fileTime", FieldEnum.DATETIME, 1));
                break;
            }
            default:
                break;

        }
        return filedList;
    }

    public class SaveES implements Callable<Boolean> {
        private HrmEnum hrmEnum;
        private Map<String, Integer> fieldMap;
        private List<Map<String, Object>> mapList;

        private SaveES(HrmEnum hrmEnum, Map<String, Integer> fieldMap, List<Map<String, Object>> mapList) {
            this.hrmEnum = hrmEnum;
            this.fieldMap = fieldMap;
            this.mapList = mapList;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         */
        @Override
        public Boolean call() {
            log.warn("线程id{}", Thread.currentThread().getName());
            List<Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>>> operationList = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                switch (hrmEnum) {
                    case APPRAISAL_EMPLOYEE:
                        ApplicationContextHolder.getBean(HrmAppraisalEmployeeServiceImpl.class).setOtherField(map);
                        break;
                    default:
                        break;
                }
                fieldMap.forEach((k, v) -> {
                    if (FieldEnum.DATE.getType().equals(v)) {
                        Object value = map.remove(k);
                        if (value instanceof Date) {
                            map.put(k, DateUtil.formatDate((Date) value));
                        }
                    } else if (FieldEnum.DATETIME.getType().equals(v)) {
                        Object value = map.remove(k);
                        if (value instanceof Date) {
                            map.put(k, DateUtil.formatDateTime((Date) value));
                        }
                    } else if (FieldUtil.equalsByType(v)) {
                        Object value = map.remove(k);
                        if (!ObjectUtil.isEmpty(value)) {
                            map.put(k, JSON.toJSONString(value));
                        }
                    }
                });
                operationList.add(EsUtil.indexOperations(map.get(hrmEnum.getTableId()), hrmEnum.getIndex(), map));
            }
            EsUtil.bulk(operationList,1000);
            mapList.clear();
            return true;
        }
    }

    public class SaveEsData implements Runnable {

        private final HrmEnum hrmEnum;

        private final List<HrmFieldDataBO> fieldDataList;

        private SaveEsData(HrmEnum crmEnum, List<HrmFieldDataBO> fieldDataList) {
            this.hrmEnum = crmEnum;
            this.fieldDataList = fieldDataList;
        }

        @Override
        public void run() {
            List<Function<BulkOperation.Builder, ObjectBuilder<BulkOperation>>> operationList = new ArrayList<>();
            Map<String, List<HrmFieldDataBO>> listMap = fieldDataList.stream().collect(Collectors.groupingBy(HrmFieldDataBO::getBatchId));
            for (List<HrmFieldDataBO> valueList : listMap.values()) {
                if (StrUtil.isEmpty(valueList.get(0).getTypeId())) {
                    continue;
                }
                Map<String, Object> map = new HashMap<>(valueList.size());
                for (HrmFieldDataBO fieldDataBO : valueList) {
                    //多选，人员，部门,标签
                    if (Arrays.asList(9, 10, 12, 61).contains(fieldDataBO.getType())) {
                        String value = fieldDataBO.getValue();
                        if (StrUtil.isNotEmpty(value)) {
                            if (Objects.equals(61, fieldDataBO.getType())) {
                                map.put(fieldDataBO.getName(), JSON.parse(value));
                            } else {
                                map.put(fieldDataBO.getName(), StrUtil.splitTrim(value, ","));
                            }
                        } else {
                            map.put(fieldDataBO.getName(), Collections.emptyList());
                        }
                    } else {
                        String value = fieldDataBO.getValue();
                        if (StrUtil.isNotEmpty(value)) {
                            map.put(fieldDataBO.getName(), value);
                        }
                    }
                }
                operationList.add(EsUtil.updateOperations(valueList.get(0).getTypeId(), hrmEnum.getIndex(), map, true));
            }
            EsUtil.bulk(operationList,1000);
        }
    }


}
