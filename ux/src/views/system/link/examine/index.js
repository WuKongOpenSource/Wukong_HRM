import { separator } from '@/filters/vueNumeralFilter/filters'
import { getWkDateTime } from '@/utils'

export const label = {
  0: 'oA',
  1: '合同',
  2: '回款',
  3: '发票',
  4: '薪资'
}

export function setHeadDetails(resData, label) {
  switch (label) {
    case 0:
      return [
        { title: '审批名称', value: resData.content },
        { title: '提交部门', value: resData.createUser?.deptName },
        { title: '审批类型', value: resData.categoryTitle },
        { title: '提交人', value: resData.createUser?.realname }
      ]
    case 1: // 合同
      return [
        { title: '客户名称', value: resData.customerName },
        { title: '合同金额（元）', value: separator(resData.money || 0) },
        { title: '下单时间', value: getWkDateTime(resData.orderDate) },
        { title: '回款金额（元）', value: separator(resData.receivablesMoney || 0) },
        { title: '负责人', value: resData.ownerUserName }
      ]
    case 2: // 回款
      return [
        { title: '客户名称', value: resData.customerName },
        { title: '合同金额', value: separator(resData.contractMoney || 0) },
        { title: '回款日期', value: resData.returnTime },
        { title: '回款金额', value: separator(resData.money || 0) },
        { title: '负责人', value: resData.ownerUserName }
      ]
    case 3: // 发票
      return [
        { title: '客户名称', value: resData.customerName },
        { title: '开票金额', value: separator(resData.invoiceMoney || 0) },
        { title: '发票号码', value: resData.invoiceNumber },
        { title: '实际开票日期', value: resData.realInvoiceDate }
      ]
  }
}

export function setFieldList(fieldList, label, detailData) {
  if (!label) {
    return [
      {
        name: '基本信息',
        list: fieldList?.flat(Infinity)
      }
    ]
  } else {
    const baseList = []
    const systemList = []
    fieldList && fieldList.forEach(item => {
      if (item.sysInformation == 1) {
        systemList.push(item)
      } else if (item.formType !== 'product') {
        // 不展示产品
        baseList.push(item)
      }
    })

    const baseInfoList = [
      {
        name: '基本信息',
        list: baseList
      },
      {
        name: '系统信息',
        list: systemList
      }
    ]

    if (label == 7) {
      const list = [
        {
          formType: 'text',
          fieldName: 'contacts',
          name: '联系人',
          value: detailData.contacts
        },
        {
          formType: 'text',
          fieldName: 'telephone',
          name: '电话',
          value: detailData.telephone
        },
        {
          formType: 'text',
          fieldName: 'detailAddress',
          name: '地址',
          value: detailData.detailAddress
        }
      ]

      baseInfoList.splice(1, 0, {
        name: '收货信息',
        list: list
      })
    }

    return baseInfoList
  }
}
