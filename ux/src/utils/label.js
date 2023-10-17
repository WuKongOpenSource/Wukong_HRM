const applyModuleMap = {
  crm: {
    leads: [1, 'leadsName'],
    customer: [2, 'customerName'],
    contacts: [3, 'name'],
    product: [4, 'name'],
    business: [5, 'businessName'],
    contract: [6, 'name'],
    receivables: [7, 'number'],
    receivablesPlan: [8, 'num'],
    marketing: [10, 'marketingName'],
    visit: [17, 'visitNumber'],
    invoice: [18, 'invoiceApplyNumber']
  },
  jxc: {
    1: 'product',
    2: 'supplier',
    3: 'purchase',
    4: 'retreat',
    5: 'sale',
    6: 'saleReturn',
    7: 'receipt',
    8: 'outbound',
    9: 'payment',
    10: 'collection',
    11: 'inventory',
    12: 'allocation',
    15: 'warehouse'
  }
}

/**
 * @description: 获取对应模块label
 * @param {*} apply
 * @param {*} moduleType
 * @return {*}
 */
export function getModuleLabelName(apply, moduleType) {
  if (apply && moduleType) {
    return applyModuleMap[apply][moduleType]
  } else {
    console.error('缺少参数apply或moduleType')
    return
  }
}

/**
 * @description: 获取进销存模块类型
 * @param {*} apply
 * @param {*} label
 * @return {*}
 */
export function getJxcModuleType(apply, label) {
  if (apply && label) {
    return applyModuleMap[apply][label]
  } else {
    console.error('缺少参数apply或label')
    return
  }
}
