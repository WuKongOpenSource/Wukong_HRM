/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
 */
const statusValue = {
  1: '目标待填写',
  2: '目标待确认',
  3: '结果待评定',
  4: '结果待确认',
  5: '考核终止',
  6: '考核完成'
}

function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

export default {
  statusValue,

  getValueList
}
