package com.kakarote.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.enums.CrmRelationTypeEnum;
import com.kakarote.core.entity.CrmRelationDTO;
import com.kakarote.core.feign.admin.service.AdminService;
import com.kakarote.core.feign.crm.entity.SimpleCrmEntity;
import com.kakarote.core.feign.crm.service.CrmService;
import com.kakarote.core.servlet.ApplicationContextHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @program: wk_crm
 * @className Utils
 * @description: crmRelation工具类
 * @author: jiao sir
 * @create: 2021-11-22 11:01
 **/
public class CrmRelationUtils {

    private static final CrmRelationDTO TEMP_CRM_RELATION = new CrmRelationDTO();


    /**
     * 获取crm关系的map数据
     *
     * @param relationIdsMap type - <id,crmIds> map
     * @param ids            日志或审批的ids
     * @return java.util.Map<java.lang.Long, com.kakarote.core.entity.CrmRelationDTO>
     * @author jiao sir
     * @date 2021/11/22
     */
    public static Map<Long, CrmRelationDTO> getCrmRelationMap(Map<Integer, Map<Long, Set<Long>>> relationIdsMap, Collection<Long> ids) {
        CrmService crmService = ApplicationContextHolder.getBean(CrmService.class);
        AdminService adminService = ApplicationContextHolder.getBean(AdminService.class);

        //客户list
        Set<Long> customerIds = getAllId(relationIdsMap, CrmRelationTypeEnum.CUSTOMER.getType());
        Map<Long, SimpleCrmEntity> customerListMap = null;
        if (!customerIds.isEmpty()) {
            // 查询客户list
            customerListMap = getCrmEntityMap(crmService
                    .queryCustomerInfo(customerIds)
                    .getData());
        }

        // 查询商机list
        Set<Long> businessIds = getAllId(relationIdsMap, CrmRelationTypeEnum.BUSINESS.getType());
        Map<Long, SimpleCrmEntity> businessListMap = null;
        if (!businessIds.isEmpty()) {
            // 查询客户list
            businessListMap = getCrmEntityMap(crmService
                    .queryBusinessInfo(businessIds)
                    .getData());
        }
        // 查询联系人list
        Set<Long> contactsIds = getAllId(relationIdsMap, CrmRelationTypeEnum.CONTACTS.getType());
        Map<Long, SimpleCrmEntity> contactsListMap = null;
        if (!contactsIds.isEmpty()) {
            contactsListMap = getCrmEntityMap(crmService
                    .queryContactsInfo(contactsIds)
                    .getData());
        }
        // 查询合同list
        Set<Long> contractIds = getAllId(relationIdsMap, CrmRelationTypeEnum.CONTRACT.getType());
        Map<Long, SimpleCrmEntity> contractListMap = null;
        if (!contractIds.isEmpty()) {
            contractListMap = getCrmEntityMap(crmService
                    .queryContractInfo(contractIds)
                    .getData());
        }
        // 查询回款list
        Set<Long> receivablesIds = getAllId(relationIdsMap, CrmRelationTypeEnum.RECEIVABLES.getType());
        Map<Long, SimpleCrmEntity> receivablesListMap = null;
        if (!receivablesIds.isEmpty()) {
            receivablesListMap = getCrmEntityMap(crmService
                    .queryReceivablesInfo(getAllId(relationIdsMap, CrmRelationTypeEnum.RECEIVABLES.getType()))
                    .getData());
        }
        //查询产品list
        Set<Long> productIds = getAllId(relationIdsMap, CrmRelationTypeEnum.PRODUCT.getType());
        Map<Long, SimpleCrmEntity> productListMap = null;
        if (!productIds.isEmpty()) {
            productListMap = getCrmEntityMap(crmService
                    .queryProductInfo(productIds)
                    .getData());
        }

        //查询Modulelist
        Set<Long> moduleIds = getAllId(relationIdsMap, CrmRelationTypeEnum.MODULE.getType());
        Map<Long, SimpleCrmEntity> moduleListMap = null;
        if (!moduleIds.isEmpty()) {
            moduleListMap = getCrmEntityMap(adminService
                    .queryModuleInfo(moduleIds)
                    .getData());
        }

        // 定义结果map
        Map<Long, CrmRelationDTO> crmRelationsMap = new HashMap<>(ids.size());
        for (Long id : ids) {
            // 定义dto对象
            CrmRelationDTO logRelationDTO = new CrmRelationDTO();
            logRelationDTO.setCustomerList(getCrmEntity(id, relationIdsMap.get(CrmRelationTypeEnum.CUSTOMER.getType()), customerListMap));
            logRelationDTO.setBusinessList(getCrmEntity(id, relationIdsMap.get(CrmRelationTypeEnum.BUSINESS.getType()), businessListMap));
            logRelationDTO.setContactsList(getCrmEntity(id, relationIdsMap.get(CrmRelationTypeEnum.CONTACTS.getType()), contactsListMap));
            logRelationDTO.setContractList(getCrmEntity(id, relationIdsMap.get(CrmRelationTypeEnum.CONTRACT.getType()), contractListMap));
            logRelationDTO.setReceivablesList(getCrmEntity(id, relationIdsMap.get(CrmRelationTypeEnum.RECEIVABLES.getType()), receivablesListMap));
            logRelationDTO.setProductList(getCrmEntity(id,relationIdsMap.get(CrmRelationTypeEnum.PRODUCT.getType()),productListMap));
            logRelationDTO.setModuleList(getCrmEntity(id,relationIdsMap.get(CrmRelationTypeEnum.MODULE.getType()),moduleListMap));
            crmRelationsMap.put(id, logRelationDTO);
        }
        return crmRelationsMap;
    }

    /**
     * 获取crm关系的数据
     *
     * @param relationIdsMap 关系idsMap
     * @param id             日志或审批的id
     * @date 2021/11/22
     */
    public static CrmRelationDTO getCrmRelation(Map<Integer, Set<Long>> relationIdsMap, Long id) {
        if (CollUtil.isEmpty(relationIdsMap)) {
            return TEMP_CRM_RELATION;
        }
        Set<Long> ids = new HashSet<>(1);
        ids.add(id);
        // 定义 type - <id,crmIds> 的map
        Map<Integer, Map<Long, Set<Long>>> map = new HashMap<>(1);
        // 遍历原有的
        relationIdsMap.forEach((k, v) -> {
            // 定义 id-crmIds map
            Map<Long, Set<Long>> longSetMap = new HashMap<>(1);
            // 放入
            longSetMap.put(id, v);
            // 放入
            map.put(k, longSetMap);
        });
        Map<Long, CrmRelationDTO> crmRelationMap = getCrmRelationMap(map, ids);
        return crmRelationMap.get(id);
    }


    /**
     * 获取关联crm的数据
     *
     * @param crmRelationDTO 关系对象
     * @return java.lang.String
     * @author jiao sir
     * @date 2021/11/22
     */
    public static String getRelateCrmWork(CrmRelationDTO crmRelationDTO) {
        if (Objects.isNull(crmRelationDTO)) {
            return StrUtil.EMPTY;
        }
        StringBuilder relateCrmWorkSb = new StringBuilder();
        handleExportLogCrmRelation("客户", relateCrmWorkSb, crmRelationDTO.getCustomerList());
        handleExportLogCrmRelation("联系人", relateCrmWorkSb, crmRelationDTO.getContactsList());
        handleExportLogCrmRelation("商机", relateCrmWorkSb, crmRelationDTO.getBusinessList());
        handleExportLogCrmRelation("合同", relateCrmWorkSb, crmRelationDTO.getContractList());
        handleExportLogCrmRelation("回款", relateCrmWorkSb, crmRelationDTO.getReceivablesList());
        return relateCrmWorkSb.toString().trim();
    }


    /**
     * 获取crm实体map
     *
     * @param entities 实例列表
     * @return java.util.Map<java.lang.String, com.kakarote.core.feign.crm.entity.SimpleCrmEntity>
     * @author jiao sir
     * @date 2021/11/20
     */
    private static Map<Long, SimpleCrmEntity> getCrmEntityMap(List<SimpleCrmEntity> entities) {
        return entities.stream().collect(Collectors.toMap(SimpleCrmEntity::getId, entity -> entity));
    }


    /**
     * 获取所有id
     *
     * @param relationIdsMap ids map
     * @param type           类型
     * @date 2021/12/23
     */
    private static Set<Long> getAllId(Map<Integer, Map<Long, Set<Long>>> relationIdsMap, int type) {
        Map<Long, Set<Long>> value = relationIdsMap.get(type);
        if (CollectionUtil.isEmpty(value)) {
            return new HashSet<>();
        }
        Collection<Set<Long>> values = value.values();
        Set<Long> hashSet = new HashSet<>(value.size() * values.size());
        values.forEach(hashSet::addAll);
        return hashSet;
    }


    /**
     * 获取对象列表
     *
     * @param id             id
     * @param relationIdsMap id-crmIds map
     * @param crmEntityMap   crmMap
     * @date 2021/11/23
     */
    private static List<SimpleCrmEntity> getCrmEntity(Long id, Map<Long, Set<Long>> relationIdsMap, Map<Long, SimpleCrmEntity> crmEntityMap) {
        if (CollUtil.isNotEmpty(relationIdsMap) && crmEntityMap != null) {
            // 获取对应的crm-id集合
            Set<Long> crmIds = relationIdsMap.get(id);
            if (CollUtil.isNotEmpty(crmIds)) {
                // 定义列表
                List<SimpleCrmEntity> crmEntities = new ArrayList<>(crmIds.size());
                // 遍历ids
                crmIds.forEach(crmId -> {
                    // 从map中获取此id对应的对象
                    SimpleCrmEntity simpleCrmEntity = crmEntityMap.get(crmId);
                    // 判断
                    if (simpleCrmEntity != null) {
                        crmEntities.add(simpleCrmEntity);
                    }
                });
                return crmEntities;
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    /***
     * 处理导入日志的crm关联
     *
     * @param crmModeName crm模块名称
     * @param sb sb
     * @param crmList crmList
     * @return void
     * @author jiao sir
     * @date 2021/11/20
     */
    private static void handleExportLogCrmRelation(String crmModeName, StringBuilder sb, List<SimpleCrmEntity> crmList) {
        if (CollUtil.isNotEmpty(crmList)) {
            sb.append(crmModeName)
                    .append(" 【");
            for (int i = 0; i < crmList.size(); i++) {
                sb.append(crmList.get(i).getName())
                        .append("】");
                if (crmList.size() - 1 > i) {
                    sb.append("、【");
                } else {
                    sb.append("\n");
                }
            }
        }
    }

}
