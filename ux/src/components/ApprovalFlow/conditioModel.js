export const selectOptions = [{
  label: '属于',
  value: 7
}]

export const checkboxOptions = [{
  label: '完全等于',
  value: 11
}, {
  label: '包含任意',
  value: 7
}]

export const numberOptions = [{
  label: '小于',
  value: 3
}, {
  label: '大于',
  value: 2
}, {
  label: '小于等于',
  value: 5
}, {
  label: '等于',
  value: 1
}, {
  label: '大于等于',
  value: 4
}, {
  label: '介于(两个数之间)',
  value: 6
}]

export const numberValueOptions = [{
  label: '<',
  value: 1
}, {
  label: '≤',
  value: 2
}]

export function getOptionObj(list) {
  const obj = {}
  list.forEach(item => {
    obj[item.value] = item.label
  })
  return obj
}
// type 连接条件 1 等于 2 大于 3 小于 4 大于等于 5 小于等于 6 两者之间 7 包含 8 员工/部门/角色 11完全等于
export function getSendObj(checked) {
  return {
    name: '发起人',
    conditionType: 8,
    // 用于展示
    userList: [],
    deptList: [],
    roleList: [],
    values: {
      userList: [],
      deptList: [],
      roleList: []
    },
    checked
  }
}
