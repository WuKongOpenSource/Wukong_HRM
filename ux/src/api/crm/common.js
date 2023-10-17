import request from '@/utils/request'

/**
 *crm 自定义字段的添加
 * @param {*} data
 */
export function filedGetFieldAPI(data) {
  const label = data.label
  let url
  if (label === 10) {
    url = '/oaExamineField/queryField/' + data.id
  }

  return request({
    url: url,
    method: 'post',
    data
  })
}

/**
 * 详情页基本信息
 * data 会包含poolId 需保留
 * @param {*} data
 */
export function filedGetInformationAPI(data) {
  return request({
    method: 'post',
    data
  })
}

/**
 * 表头
 * @param {*} data
 */
export function filedGetTableFieldAPI(data) {
  return request({
    url: 'crmField/queryListHead/' + data.label,
    method: 'post',
    data: data
  })
}

/**
 * 基本信息编辑
 * @param {*} data
 */
export function filedUpdateTableFieldAPI(data) {
  return request({
    url: `${crmTypeModel.labelToType[data.label]}/updateInformation`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海表头
 * @param {*} data
 */
export function filedGetPoolTableFieldAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolListHead',
    method: 'post',
    data: data
  })
}

/**
 * 线索池表头
 * @param {*} data
 */
export function filedGetLeadsPoolTableFieldAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolListHead',
    method: 'post',
    data: data
  })
}

/**
 * 自定义字段验重
 * @param {*} data
 */
export function filedValidatesAPI(data) {
  return request({
    url: 'crmField/verify',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟进自定义字段验重
 * @param {*} data
 */
export function crmActivityValidatesAPI(data) {
  return request({
    url: 'crmActivity/verify',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 自定义字段(高级筛选)
 * @param {*} data
 */
export function filterIndexfieldsAPI(data) {
  return request({
    url: 'crmScene/queryField',
    method: 'post',
    data: data
  })
}

// /**
//  * 公海自定义字段(高级筛选) 同  crmCustomerPoolQueryPoolFieldAPI
//  * @param {*} data
//  */
// export function filterIndexPoolfieldsAPI(data) {
//   return request({
//     url: 'crmCustomerPool/queryPoolField',
//     method: 'post',
//     data: data
//   })
// }

/**
 * 场景列表
 * @param {*} data
 */
export function crmSceneIndexAPI(data) {
  return request({
    url: 'crmScene/queryScene',
    method: 'post',
    data: data
  })
}

/**
 * 场景设置列表
 * @param {*} data
 */
export function crmSceneSetIndexAPI(data) {
  return request({
    url: 'crmScene/querySceneConfig',
    method: 'post',
    data: data
  })
}

/**
 * 场景创建
 * @param {*} data
 */
export function crmSceneSaveAPI(data) {
  return request({
    url: 'crmScene/addScene',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 场景编辑
 * @param {*} data
 */
export function crmSceneUpdateAPI(data) {
  return request({
    url: 'crmScene/updateScene',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 场景默认
 * @param {*} data
 */
export function crmSceneDefaultsAPI(data) {
  return request({
    url: 'crmScene/setDefaultScene',
    method: 'post',
    data: data
  })
}

/**
 * 场景删除
 * @param {*} data
 */
export function crmSceneDeleteAPI(data) {
  return request({
    url: 'crmScene/deleteScene',
    method: 'post',
    data: data
  })
}

/**
 * 场景排序
 * @param {*} data
 */
export function crmSceneSortAPI(data) {
  return request({
    url: 'crmScene/sceneConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 列表字段排序数据
 * @param {*} data
 */
export function crmFieldConfigAPIIndexAPI(data) {
  return request({
    url: 'crmField/queryFieldConfig',
    method: 'post',
    data: data
  })
}

/**
 * 公海列表字段排序数据
 * @param {*} data
 */
export function crmPoolFieldConfigIndexAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolFieldConfig',
    method: 'post',
    data: data
  })
}

/**
 * 列表排序编辑
 * @param {*} data
 */
export function crmFieldConfigAPI(data) {
  return request({
    url: 'crmField/setFieldConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 列表宽度设置
 * @param {*} data
 */
export function crmFieldColumnWidthAPI(data) {
  return request({
    url: 'crmField/setFieldStyle',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设置锁定字段
 * @param {*} data
 */
export function crmFieldSetIsLockAPI(data) {
  return request({
    url: 'crmField/setIsLock',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 操作记录
 * @param {*} data
 */
export function crmIndexFieldRecordAPI(data) {
  return request({
    url: 'crmActionRecord/queryRecordList',
    method: 'post',
    data: data
  })
}

// /**
//  * 客户管理下 合同回款的待审批
//  * @param {*} data
//  */
// export function crmExamineMyExamineAPI(data) {
//   return request({
//     url: 'crmExamine/myExamine',
//     method: 'post',
//     data: data,
//     headers: {
//       'Content-Type': 'application/json;charset=UTF-8'
//     }
//   })
// }

/**
 * 查询活动分页列表
 * @param {*} data
 */
export function crmActivityListAPI(data) {
  return request({
    url: 'crmActivity/getCrmActivityPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取跟进记录详情
 * @param {*} data
 */
export function crmActivityDataAPI(id) {
  return request({
    url: `crmActivity/getCrmActivity/${id}`,
    method: 'post'
  })
}

/**
 * 删除跟进记录
 * @param {*} data
 */
export function crmActivityDeleteAPI(data) {
  return request({
    url: `crmActivity/deleteCrmActivityRecord/${data.id}`,
    method: 'post'
  })
}

/**
 * 删除外勤签到
 * @param {*} data
 */
export function crmActivityOutworkSignDeleteAPI(data) {
  return request({
    url: 'crmActivity/deleteOutworkSign',
    method: 'post',
    data: data
  })
}

/**
 * 添加跟进记录
 * @param {*} data
 */
export function crmActivityAddAPI(data) {
  return request({
    url: 'crmActivity/addCrmActivityRecord',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑跟进记录
 * @param {*} data
 */
export function crmActivityUpdateAPI(data) {
  return request({
    url: 'crmActivity/updateActivityRecord',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询新增所需字段
 * @param {*} data
 */
export function crmActivityFieldAPI(data) {
  return request({
    url: 'crmActivity/field',
    method: 'post',
    data
  })
}

/**
 * 查询修改数据所需字段信息
 * @param {*} data
 */
export function crmActivityFieldEditAPI(id) {
  return request({
    url: `crmActivity/field/${id}`,
    method: 'post'
  })
}

/**
 * 查看当前导入数量
 * @param {*} data
 */
export function crmQueryImportNumAPI(data) {
  return request({
    url: 'adminMessage/queryImportNum',
    method: 'post',
    data: data
  })
}

/**
 * 查询导入结果
 * @param {*} data
 */
export function crmQueryImportInfoAPI(data) {
  return request({
    url: 'adminMessage/queryImportInfo',
    method: 'post',
    data: data
  })
}

/**
 * 查看错误数据接口
 * @param {*} data
 */
export function crmDownImportErrorAPI(data) {
  return request({
    url: 'adminMessage/downImportError',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 查询跟进记录常用语
 * @param {*} data
 */
export function sysConfigQueryPhraseAPI(data) {
  return request({
    url: 'adminConfig/queryActivityPhrase',
    method: 'post',
    data: data
  })
}

/**
 * 设置跟进记录常用语
 * @param {*} data
 */
export function sysConfigSetPhraseAPI(data) {
  return request({
    url: 'adminConfig/setActivityPhrase',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/** 阶段记录 */
/**
 * 查询阶段列表
 * @param {*} data
 */
export function crmFlowQueryFlowSettingListAPI(data) {
  return request({
    url: 'crmFlow/queryFlowSettingList/' + data.label,
    method: 'post',
    data: data
  })
}

/**
 * 查询阶段详情信息
 * @param {*} data
 */
export function crmFlowQueryFlowDataInfoAPI(data) {
  return request({
    url: 'crmFlow/queryFlowDataInfo',
    method: 'post',
    data: data
  })
}
/**
 * 提交阶段信息 审核
 * @param {*} data
 */
export function crmFlowSaveFlowDataAPI(data) {
  return request({
    url: 'crmFlow/saveFlowData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
/**
 * 查看评论列表
 * @param {*} data
 */
export function crmFlowQueryCommentListAPI(data) {
  return request({
    url: `crmFlow/queryCommentList/${data.id}`,
    method: 'post',
    data: data
  })
}
/**
 * 删除评论
 * @param {*} data
 */
export function crmFlowDeleteCommentAPI(data) {
  return request({
    url: 'crmFlow/deleteComment',
    method: 'post',
    data: data
  })
}
/**
 * 添加、修改评论
 * @param {*} data
 */
export function crmFlowSetCommentAPI(data) {
  return request({
    url: 'crmFlow/setComment',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 推进、结束状态
 * @param {*} data
 */
export function crmFlowUpdateFlowDataStatusAPI(data) {
  return request({
    url: `crmFlow/updateFlowDataStatus/${data.dataId}`,
    method: 'post',
    data: data
  })
}

/**
 * 保存外漏高级筛选条件
 * @param {*} data
 */
export function crmSearchExpertSaveAPI(data) {
  return request({
    url: 'crmSearchExpert/save',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加、编辑标签
 */
export function crmSaveLabelAPI(data) {
  return request({
    url: 'crmLabel/addOrUpdateLabelById',
    method: 'post',
    data: data
  })
}

/**
 * 删除标签
 */
export function crmDelLabelAPI(data) {
  return request({
    url: 'crmLabel/deleteLabelById',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询客户管理对应模块的标签
 */
export function crmQueryLabelAPI(data) {
  return request({
    url: `crmLabel/queryLabelList/${data.type}`,
    method: 'post',
    data
  })
}

/**
 * @description: 标星
 * @param {*} data  模块类型 type  id
 * @return {*}
 */
export function crmCommonStarAPI(data) {
  return request({
    url: `crmCommon/star/${data.type}/${data.id}`,
    method: 'post',
    data
  })
}

/**
 * @description: 查询关联产品表头字段
 * @param {*} data
 * @return {*}
 */
export function crmRelativeProductListHeadAPI(data) {
  return request({
    url: 'crmProductFieldRelatedSetting/queryRelatedListHead',
    method: 'post'
  })
}

/**
 * @description: 阶段流程信息
 * @param {*} data
 * @return {*}
 */
export function crmCommonStageFlowAPI(data) {
  return request({
    url: `crmFlow/queryCrmFlowSearchFieldInfo`,
    method: 'post',
    data
  })
}

/**
 * @description: 获取模块金额比例信息
 * @param {*} data
 * @return {*}
 */
export function crmGetRatioSettingAPI(data) {
  return request({
    url: `crmAmountRatioSetting/getRatioSetting`,
    method: 'post',
    data
  })
}

/**
 * @description: 删除模块金额比例信息
 * @param {*} data
 * @return {*}
 */
export function deleteRatioSettingAPI(data) {
  return request({
    url: `/crmAmountRatioSetting/deleteRatioSetting/${data}`,
    method: 'post'
  })
}

/**
 * @description: 获取模块金额比例信息
 * @param {*} data
 * @return {*}
 */
export function saveOrUpdateRatioSettingAPI(data) {
  return request({
    url: `/crmAmountRatioSetting/saveOrUpdateRatioSetting/${data.label}`,
    method: 'post',
    data: data.data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
