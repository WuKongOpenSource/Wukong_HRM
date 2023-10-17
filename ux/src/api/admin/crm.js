import request from '@/utils/request'

// 阶段流程配置
/**
 * 保存阶段基础信息
 * @param {*} data
 */
export function crmFlowSaveBaseInfoAPI(data) {
  return request({
    url: 'crmFlow/updateBasicInfo',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
/**
 * 保存阶段流程
 * @param {*} data
 */
export function crmFlowSaveAPI(data) {
  return request({
    url: 'crmFlow/save',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
/**
 * 查询阶段流程
 * @param {*} data
 */
export function crmFlowQueryListAPI(data) {
  return request({
    url: 'crmFlow/queryList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
/**
 * 修改阶段流程状态
 * @param {*} data
 */
export function crmFlowUpdateStatusAPI(data) {
  return request({
    url: `crmFlow/updateStatus/${data.flowId}`,
    method: 'post',
    data: data
  })
}

/**
 * 查询当前类型下是否存在启用的流程
 */
export function crmFlowQueryActiveAPI(data) {
  return request({
    url: `/crmFlow/queryActiveFlowName/${data.label}`,
    method: 'post',
    data: data
  })
}

/**
 * 阶段流程详情
 * @param {*} data
 */
export function crmFlowFlowInfoAPI(data) {
  return request({
    url: `crmFlow/flowInfo/${data.flowId}`,
    method: 'post',
    data: data
  })
}

export function businessGroupListAPI(data) {
  return request({
    url: 'crmBusinessType/queryBusinessTypeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function businessGroupAddAPI(data) {
  return request({
    url: 'crmBusinessType/setBusinessType',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 商机状态组状态修改
 * @param {*} data
 */
export function businessGroupUpdateStatusAPI(data) {
  return request({
    url: 'crmBusinessType/updateStatus',
    method: 'post',
    data: data
  })
}

/**
 * 商机状态组详情
 * @param {*} data
 */
export function businessGroupReadAPI(data) {
  return request({
    url: 'crmBusinessType/getBusinessType',
    method: 'post',
    data: data
  })
}

/**
 * 商机状态组编辑
 * @param {*} data
 */
export function businessGroupUpdateAPI(data) {
  return request({
    url: 'crmBusinessType/setBusinessType',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 商机状态组删除
 * @param {*} data
 */
export function businessGroupDeleteAPI(data) {
  return request({
    url: 'crmBusinessType/deleteById',
    method: 'post',
    data: data
  })
}

/**
 * 自定义字段（字段数据）的添加编辑操作
 * @param {*} data
 */
export function customFieldHandleAPI(data) {
  return request({
    url: 'crmField/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 自定义字段（字段数据）的详情
 * @param {*} data
 */
export function customFieldListAPI(data) {
  return request({
    url: `crmField/list/${data.label}`,
    method: 'post'
  })
}

/**
 * 办公审批的自定义字段
 * @param {*} data
 */
export function oaExamineFieldListAPI(data) {
  return request({
    url: `oaExamineField/queryField/${data.categoryId}`,
    data: data,
    method: 'post'
  })
}

/**
 * 办公审批自定义字段保存
 * @param {*} data
 */
export function oaFieldHandleAPI(data) {
  return request({
    url: 'oaExamineField/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 自定义字段（字段数据）的列表更新时间
 * @param {*} data
 */
export function customFieldIndexAPI(data) {
  return request({
    url: 'crmField/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 产品类别 数据获取
 * @param {*} data
 */
export function productCategoryIndexAPI(data) {
  return request({
    url: 'crmProductCategory/queryList',
    method: 'post',
    data: data
  })
}

/**
 * 产品分类添加
 * @param {*} data
 */
export function productCategorySaveAPI(data) {
  const url = data.categoryId ? 'update' : 'save'
  return request({
    url: 'crmProductCategory/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 产品分类删除
 * @param {*} data
 */
export function productCategoryDeleteAPI(data) {
  return request({
    url: `crmProductCategory/deleteById/${data.id}`,
    method: 'post'
  })
}

/**
 * 客户保护规则
 * @param {*} data
 */
export function crmSettingConfigAPI(data) {
  return request({
    url: 'crmCustomer/updateRulesSetting',
    method: 'post',
    data: data
  })
}

/**
 * 客户保护规则
 * @param {*} data
 */
export function crmSettingConfigDataAPI(data) {
  return request({
    url: 'crmCustomer/getRulesSetting',
    method: 'post',
    data: data
  })
}

/**
 * 部门业绩目标列表
 * @param {*} data
 */
export function crmAchievementIndex(data) {
  return request({
    url: 'crmAchievement/queryAchievementList',
    method: 'post',
    data: data
  })
}

/**
 * 业绩目标编辑接口
 * @param {*} data
 */
export function crmAchievementUpdate(data) {
  return request({
    url: 'crmAchievement/setAchievement',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 新增业绩目标
 * @param {*} data
 */
export function crmAchievementAdd(data) {
  return request({
    url: 'crmAchievement/addAchievement',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 删除业绩目标
 * @param {*} data
 */
export function crmAchievementDelete(data) {
  return request({
    url: 'crmAchievement/deleteAchievement',
    method: 'post',
    data: data
  })
}

/**
 * 记录类型 数据
 * @param {*} data
 */
export function crmSettingRecordListAPI(data) {
  return request({
    url: 'crmActionRecord/queryRecordOptions',
    method: 'post',
    data: data
  })
}

/**
 * 记录类型 编辑
 * @param {*} data
 */
export function crmSettingRecordEditAPI(data) {
  return request({
    url: 'adminConfig/setRecordOptions',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 合同到期提醒 设置
 * @param {*} data
 */
export function crmSettingContractDayAPI(data) {
  return request({
    url: 'crmContract/setContractConfig',
    method: 'post',
    data: data
  })
}

/**
 * 拥有、锁定客户数限制
 * @param {*} data
 */
export function crmSettingCustomerConfigListAPI(data) {
  return request({
    url: 'crmCustomer/queryCustomerSetting',
    method: 'post',
    data: data
  })
}

/**
 * 拥有、锁定客户数限制 - 创建 编辑
 * @param {*} data
 */
export function crmSettingCustomerConfigSetAPI(data) {
  return request({
    url: 'crmCustomer/customerSetting',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 拥有、锁定客户数限制 - 删除
 * @param {*} data
 */
export function crmSettingCustomerConfigDelAPI(data) {
  return request({
    url: 'crmCustomer/deleteCustomerSetting',
    method: 'post',
    data: data
  })
}

/**
 * 客户回访查询
 * @param {*} data
 */
export function returnVisitConfigQueryAPI(data) {
  return request({
    url: 'crmReturnVisit/queryReturnVisitRemindConfig',
    method: 'post',
    data: data
  })
}

/**
 * 客户回访设置
 * @param {*} data
 */
export function returnVisitConfigSetAPI(data) {
  return request({
    url: 'crmReturnVisit/updateReturnVisitRemindConfig',
    method: 'post',
    data: data
  })
}

/**
 * 编号查询
 * @param {*} data
 */
export function sysConfigNumberQueryAPI(data) {
  return request({
    url: 'crmNumberSetting/queryNumberSetting',
    method: 'post',
    data: data
  })
}

/**
 * 编号设置
 * @param {*} data
 */
export function sysConfigNumberSetAPI(data) {
  return request({
    url: 'crmNumberSetting/setNumberSetting',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/** ****************自定义模板***********************/

/**
 * 查询 打印模板列表
 * @param {*} data
 */
export function printTemplateListAPI(data) {
  return request({
    url: 'crmPrint/queryPrintTemplateList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询指定打印模板
 * templateId
 * @param {*} data
 */
export function printTemplateByIdAPI(data) {
  return request({
    url: 'crmPrint/queryPrintTemplateById',
    method: 'post',
    data: data
  })
}

/**
 * 添加模板 打印模板
 * templateName 模板名称
 * type 关联对象
 * content模板
 * @param {*} data
 */
export function printAddTemplateAPI(data) {
  return request({
    url: 'crmPrint/addPrintTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑模板 打印模板
 * templateName 模板名称
 * type 关联对象
 * content模板
 * @param {*} data
 */
export function printUpdateTemplateAPI(data) {
  return request({
    url: 'crmPrint/updatePrintTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除指定打印模板
 * templateId
 * @param {*} data
 */
export function printDeleteTemplateAPI(data) {
  return request({
    url: 'crmPrint/deletePrintTemplate',
    method: 'post',
    data: data
  })
}

/**
 * 模块对应字段查询
 * type
 * @param {*} data
 */
export function printQueryFieldsAPI(data) {
  return request({
    url: 'crmPrint/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 模板校准为内容
 * type
 * @param {*} data
 */
export function printPrintAPI(data) {
  return request({
    url: 'crmPrint/print',
    method: 'post',
    data: data
  })
}

/**
 * 模板复制
 * type
 * @param {*} data
 */
export function printCopyTemplateAPI(data) {
  return request({
    url: 'crmPrint/copyTemplate',
    method: 'post',
    data: data
  })
}

/**
 * 保存打印记录
 * type
 * @param {*} data
 */
export function printSaveRecordAPI(data) {
  return request({
    url: 'crmPrint/savePrintRecord',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询打印记录
 * type
 * @param {*} data
 */
export function printQueryPrintRecordAPI(data) {
  return request({
    url: 'crmPrint/queryPrintRecord',
    method: 'post',
    data: data
  })
}

/**
 * 打印记录详情
 * type
 * @param {*} data
 */
export function printRecordDetailAPI(data) {
  return request({
    url: 'crmPrint/queryPrintRecordById',
    method: 'post',
    data: data
  })
}

/**
 * 打印预览
 * type
 * @param {*} data
 */
export function printPreviewAPI(data) {
  return request({
    url: 'crmPrint/preview',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 市场活动删除表单
 * type
 * @param {*} data
 */
export function crmMarketingFormDeleteAPI(data) {
  return request({
    url: 'crmMarketingForm/delete',
    method: 'post',
    data: data
  })
}

/**
 * 市场活动分页查询表单列表
 * type
 * @param {*} data
 */
export function crmMarketingFormPageAPI(data) {
  return request({
    url: 'crmMarketingForm/page',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 市场活动查询表单列表
 * type
 * @param {*} data
 */
export function crmMarketingFormListAPI(data) {
  return request({
    url: 'crmMarketingForm/list',
    method: 'post',
    data: data
  })
}

/**
 * 市场活动添加或修改表单
 * type
 * @param {*} data
 */
export function crmMarketingFormSaveOrUpdateAPI(data) {
  return request({
    url: 'crmMarketingForm/saveOrUpdate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 市场活动表单修改状态
 * type
 * @param {*} data
 */
export function crmMarketingFormUpdateStatusAPI(data) {
  return request({
    url: 'crmMarketingForm/updateStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 市场活动表单查询新增或编辑字段
 * @param {*} data
 */
export function crmMarketingFieldListAPI(data) {
  return request({
    url: `crmMarketingField/queryField/${data.id}`,
    method: 'post'
  })
}
/**
 * 市场活动表单查询新增或编辑字段
 * @param {*} data
 */
export function crmMarketingNewFieldListAPI(data) {
  return request({
    url: `crmMarketingField/queryNewField/${data.id}`,
    method: 'post'
  })
}

/**
 * 保存自定义字段
 * @param {*} data
 */
export function crmMarketingFieldHandleAPI(data) {
  return request({
    url: 'crmMarketingField/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 业绩目标导入
 * @param {*} data
 *
 */
export function biAchievementExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmAchievement/excelImport',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
/**
 * 业绩目标导入模板下载
 * @param {*} data
 *
 */
export const biAchievementExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmAchievement/downloadExcel'
export function biAchievementDownloadExcelAPI(data) {
  return request({
    url: 'crmAchievement/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 业绩目标查看错误数据接口
 * @param {*} data
 */
export function biAchievementDownExcelAPI(data) {
  return request({
    url: 'crmAchievement/downExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 产品类别导出
 * @param {*} data
 */
export function crmProductCategoryAllExportExcelAPI(data) {
  return request({
    url: 'crmProductCategory/allExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 产品类别导入
 * @param {*} data
 */
export function crmProductCategoryUploadExcelAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmProductCategory/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 产品类别导入模板
 * @param {*} data
 */
export function crmProductCategoryDownloadExcelAPI() {
  return request({
    url: 'crmProductCategory/downloadExcel',
    method: 'post',
    responseType: 'blob'
  })
}

/**
 * 查询客户关系配置
 * @param {*} data
 */
export function crmCustomerRelationsConfigQueryTreeListAPI(data) {
  return request({
    url: '/crmCustomerRelationsConfig/queryTreeList',
    method: 'post',
    data: data
  })
}

/**
 * 保存客户关系配置
 * @param {*} data
 */
export function crmCustomerRelationsConfigSaveAPI(data) {
  return request({
    url: '/crmCustomerRelationsConfig/save',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 修改客户关系配置
 * @param {*} data
 */
export function crmCustomerRelationsConfigUpdateAPI(data) {
  return request({
    url: '/crmCustomerRelationsConfig/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 删除客户关系配置
 * @param {*} data
 */
export function crmCustomerRelationsConfigDeleteAPI(data) {
  return request({
    url: `/crmCustomerRelationsConfig/delete/${data.id}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取开票类型列表
 * @param {*} data
 */
export function crmInvoiceTypeListAPI(data) {
  return request({
    url: 'crmInvoice/queryInvoiceTypeList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 查询商机、合同模块关联产品
/**
 * @description: 查询关联产品所有字段
 * @return {*}
 */
export function crmQueryRelationsProductAllFieldAPI() {
  return request({
    url: 'crmProductFieldRelatedSetting/queryFieldConfig',
    method: 'post'
  })
}

/**
 * @description: 编辑关联产品字段排序
 * @param {*} data
 * @return {*}
 */
export function crmUpdateRelationsProductSortAPI(data) {
  return request({
    url: 'crmProductFieldRelatedSetting/setFieldConfig',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * @description: 查询关联产品配置
 * @return {*}
 */
export function crmQueryRelationsProductConfigAPI() {
  return request({
    url: 'crmProduct/queryRelatedConfig',
    method: 'post'
  })
}

/**
 * 设置关联产品配置
 * @param {*} data
 */
export function crmSetRelationsProductConfigAPI(data) {
  return request({
    url: 'crmProduct/updateRelatedConfig',
    method: 'post',
    data
  })
}

/**
 *  crm查询人员控件
 * @param {*} data
 */
export function crmFieldQueryUserFieldAPI(data) {
  return request({
    url: `/crmField/queryUserField/${data.label}`,
    method: 'post',
    data: data
  })
}

/**
 *  阶段流程查询人员控件
 * @param {*} data
 */
export function crmFlowQueryFlowUserFieldAPI(data) {
  return request({
    url: `/crmFlow/queryFlowUserField/${data.settingId}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
