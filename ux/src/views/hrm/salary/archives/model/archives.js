function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

// 调薪原因
const changeReasonValue = {
  0: '入职定薪',
  1: '入职核定',
  2: '转正',
  3: '晋升',
  4: '调动',
  5: '年中调薪',
  6: '年度调薪',
  7: '特别调薪',
  8: '其他'
}

const statusValue = {
  0: '未生效',
  1: '已生效',
  2: '已取消'
}

const typeValue = {
  1: '定薪',
  2: '调薪'
}

// 薪资档案 archivesModel
export default {
  changeReasonValue,
  statusValue,
  typeValue,

  getValueList
}
