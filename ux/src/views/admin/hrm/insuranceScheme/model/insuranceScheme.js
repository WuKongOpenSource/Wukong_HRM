/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
 */
const providentFundTypeValue = {
  1: '养老保险',
  2: '医疗保险',
  3: '失业保险',
  4: '工伤保险',
  5: '生育保险',
  6: '补充大病医疗保险',
  7: '补充养老保险',
  8: '残保险'
  // 9: '社保自定义'
}

const socialSecurityTypeValue = {
  10: '公积金'
  // 11: '公积金自定义'
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
  providentFundTypeValue,
  socialSecurityTypeValue,

  getValueList
}
