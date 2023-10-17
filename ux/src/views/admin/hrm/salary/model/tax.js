
const cycleTypeValue = {
  1: '上年12月到今年11月（对应的工资发放方式为次月发上月工资）',
  2: '今年1月到12月（对应的工资发放方式为当月发当月工资）'
}

const isTaxValue = {
  0: '否',
  1: '是'
}

const taxTypeValue = {
  1: '工资薪金所得税',
  2: '劳务报酬所得税',
  3: '不计税'
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
  cycleTypeValue,
  isTaxValue,
  taxTypeValue,

  getValueList
}
