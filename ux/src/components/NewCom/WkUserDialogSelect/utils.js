/**
 * 获取所有部门父节点
 */
export const findAllParentInfo = (tree, deptId) => {
  const forFn = (arr, deptId) => {
    arr.map(item => {
      if (item.deptId === deptId) {
        // 更新所有部门勾选状态
        const { userList, children } = item
        const depts = children || []
        const users = userList || []

        const deptCount = depts.filter(item => item.isChecked).length
        const userCount = users.filter(item => item.isChecked).length
        const checkCount = deptCount + userCount
        const allCount = depts.length + users.length
        if (checkCount > 0) {
          if (checkCount === allCount) {
            window.app.$set(item, 'isChecked', true)
            window.app.$set(item, 'indeterminate', false)
          } else {
            window.app.$set(item, 'isChecked', false)
            window.app.$set(item, 'indeterminate', true)
          }
        } else {
          window.app.$set(item, 'isChecked', false)
          window.app.$set(item, 'indeterminate', false)
        }
        forFn(tree, item.parentId)
      } else if (item.children && item.children.length > 0) {
        forFn(item.children, deptId)
      }
    })
  }
  forFn(tree, deptId)
}
