import { roleListAPI } from '@/api/admin/employeeDep'
import { oaExamineFieldQueryUserFieldAPI, jxcFieldQueryUserFieldAPI } from '@/api/oa/superExamine'
import { crmFieldQueryUserFieldAPI, crmFlowQueryFlowUserFieldAPI } from '@/api/admin/crm'

import merge from '@/utils/merge'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'
import Lockr from 'lockr'

export default {
  data() {
    return {
      wkRoleOption: [],

      memberControlList: [] // 人员字段
    }
  },

  provide() {
    return {
      memberControlFun: this.memberControlFun
    }
  },

  methods: {
    /**
     * @description:
     * @param {*} list 原始流程
     * @param {*} newList 处理后流程
     * @param {*} config 配置信息  disabled 默认false
     * @return {*}
     */
    getListInfo(list, newList, props) {
      const config = merge({
        additionalInfo: null, // 节点附加信息
        additionalInfoFun: null, // 节点附加信息方法1
        cycleFun: null // 条件节点循环方法
      }, props || {})

      list.forEach((item, index) => {
        // 条件
        if (item.examineType === 0) {
          newList.push(this.getConditonWrapInfo(item, index, config, list, newList))
        } else {
          newList.push(this.getNodeInfo(item, index, config, list))
        }
      })
    },

    /**
     * 审批节点信息
     */
    getNodeInfo(data, index, config, parentList) {
      let dataInfo = {
        examineType: data.examineType,
        name: data.name,
        conditionList: [],
        deptList: data.deptList,
        // examineErrorHandling: data.examineErrorHandling,
        roleIdList: data.roleIdList,
        type: data.type,
        userList: data.userList || [],
        chooseType: data.chooseType,
        rangeType: data.rangeType,
        parentLevel: 1,
        tempParentLevel: 1, // 临时变量
        overType: 1, // 临时变量
        isError: false
      }

      if (data.examineType === 1) {
        dataInfo.memberUserList = data.userList // 指定成员数据返回在 userList 但提交是 在 memberUserList
      } else if (data.examineType === 5) {
        if (data.type === 1) {
          // overType 1 开启 0 不开启 parentLevel为0
          if (data.parentLevel === 0) {
            dataInfo.overType = 0
            dataInfo.parentLevel = 1
          } else {
            dataInfo.overType = 1
            dataInfo.parentLevel = data.parentLevel
          }
        } else if (data.type === 2) {
          dataInfo.overType = 0
          dataInfo.parentLevel = 1
          dataInfo.tempParentLevel = data.parentLevel
        }
      } else if (data.examineType === 7) {
        dataInfo = {
          examineType: data.examineType,
          name: data.name,
          userList: data.userList || [],
          isSelf: data.isSelf, // 是否通知本人
          isAdd: data.isAdd, // 是否允许添加
          roleIdList: data.roleIdList || [],
          roleList: data.roleList || [],
          parentLevelList: data.parentLevelList || []
        }
      } else if (data.examineType === 8) { // 关联员工控件
        const { parentLevel } = data
        // 判断当前人员字段是否被删除
        const flag = this.memberControlList.filter(item => item.fieldName == data.fieldName).length
        if (flag) {
          dataInfo.fieldName = data.fieldName
          dataInfo.fieldId = data.fieldId
          dataInfo.parentLevelCheck = Boolean(parentLevel)
          dataInfo.parentLevel = data.parentLevel
        } else {
          dataInfo.fieldName = ''
          dataInfo.fieldId = ''
          dataInfo.type = 1
        }
      } else {
        dataInfo.parentLevel = data.parentLevel
      }

      // 角色对象
      dataInfo.roleList = []
      if (data.roleIdList && data.roleIdList.length > 0) {
        this.getRoleList().then(roles => {
          this.getRoleItems(data.roleIdList, roles, dataInfo.roleList)
        })
      }

      // 字段授权
      if (data.fieldAuthList) {
        dataInfo.fieldAuthList = data.fieldAuthList
      }

      // 附加信息拼入
      if (config.additionalInfo) {
        for (const key in config.additionalInfo) {
          dataInfo[key] = config.additionalInfo[key]
        }
      }

      if (config.additionalInfoFun) {
        config.additionalInfoFun(dataInfo, data, index, parentList, index)
      }

      return dataInfo
    },

    /**
     * 条件节点信息
     */
    getConditionNodeInfo(data, index, config, parentList, parentIndex) {
      const dataInfo = {
        conditionName: data.conditionName,
        sort: index + 1,
        conditionDataList: [],
        examineDataList: [],
        isError: false
      }

      data.conditionDataList.forEach(children => {
        dataInfo.conditionDataList.push(this.getSectionConditionInfo(children))
      })

      // 附加信息拼入
      if (config.additionalInfo) {
        for (const key in config.additionalInfo) {
          dataInfo[key] = config.additionalInfo[key]
        }
      }

      if (config.additionalInfoFun) {
        config.additionalInfoFun(dataInfo, data, index, parentList, parentIndex)
      }
      return dataInfo
    },

    /**
     * @description: 获取单个块的条件展示数据
     * @param {*} children
     * @return {*}
     */
    getSectionConditionInfo(children) {
      return children.map(item => {
        const subItem = {
          name: item.name,
          fieldName: item.fieldName,
          fieldId: item.fieldId,
          type: item.type,
          conditionType: item.conditionType
        }

        // 单选多选
        if (item.type === 3 || item.type === 9) {
          subItem.values = item.values
          this.validateSetting(subItem)
        } else {
          // 数字区间
          if (item.conditionType === 6) {
            if (item.values && item.values.length === 4) {
              subItem.leftValue = parseInt(item.values[0])
              subItem.leftCondition = parseInt(item.values[1])
              subItem.rightCondition = parseInt(item.values[2])
              subItem.rightValue = parseInt(item.values[3])
            } else {
              subItem.leftValue = 0
              subItem.leftCondition = 1
              subItem.rightCondition = 1
              subItem.rightValue = 0
            }
            subItem.values = 0
          } else if (item.conditionType === 8) { // 发起人
            const itemValues = item.values
            subItem.values = {
              deptList: itemValues.deptList.map(item => item.deptId),
              roleList: itemValues.roleList,
              userList: itemValues.userList.map(item => item.userId)
            }
            subItem.deptList = itemValues.deptList
            subItem.userList = itemValues.userList
            subItem.roleList = []
            if (itemValues.roleList.length > 0) {
              this.getRoleList().then(roles => {
                this.getRoleItems(itemValues.roleList, roles, subItem.roleList)
              })
            }
          } else {
            // 数字或金额切换类型的值
            subItem.leftValue = 0
            subItem.leftCondition = 1
            subItem.rightCondition = 1
            subItem.rightValue = 0
            if (item.values && item.values.length > 0) {
              subItem.values = item.values[0]
            } else {
              subItem.values = 0
            }
          }
        }
        return subItem
      })
    },

    /**
     * 验证数据
     */
    validateSetting(item) {

    },

    /**
     * 获取角色列表
     */
    getRoleList() {
      return new Promise((resolve, reject) => {
        if (this.wkRoleOption.length > 0) {
          resolve(this.wkRoleOption)
        } else {
          roleListAPI()
            .then(res => {
              this.wkRoleOption = res.data || []
              resolve(this.wkRoleOption)
            })
            .catch(() => {})
        }
      })
    },

    /**
     * 获取角色对象
     */
    getRoleItems(ids, list, newList) {
      for (let index = 0; index < list.length; index++) {
        const item = list[index]
        if (ids.includes(item.roleId)) {
          newList.push(item)
          if (ids.length === newList.length) {
            break
          }
        }
        if (item.list) {
          this.getRoleItems(ids, item.list, newList)
        }
      }
    },

    /**
     * 获取角色对象
     */
    getRoleObj(id, list, dataInfo) {
      for (let index = 0; index < list.length; index++) {
        const item = list[index]
        if (item.roleId === id) {
          dataInfo.roleObj = item
          break
        }
        if (item.list) {
          this.getRoleObj(id, item.list, dataInfo)
        }
      }
    },

    getConditonWrapInfo(conditionWrap, conditionIndex, config, parentList, newList) {
      const conditionWrapInfo = {
        examineType: conditionWrap.examineType,
        name: conditionWrap.name,
        conditionList: []
      }

      conditionWrap.conditionList.forEach((item, index) => {
        // 条件
        const conditionNodeInfo = this.getConditionNodeInfo(item, index, config, parentList, conditionIndex)

        conditionWrapInfo.conditionList.push(conditionNodeInfo)
        // 条件循环方法
        config.cycleFun && config.cycleFun('conditionList', { conditionWrapInfo, index, conditionWrap, parentList, conditionIndex, newList })

        this.getListInfo(item.examineDataList, conditionNodeInfo.examineDataList, config)
      })
      return conditionWrapInfo
    },

    /**
     * @description: 获取人员必填列表
     * @return {*}
     */
    getUserFieldList(label, examineId, detailData) {
      if (!Lockr.get(LOCAL_ADMIN_TOKEN)) return
      // if (!this.detail) return
      // const { label = null, examineId } = this.detail
      const params = {}
      let request = null
      if (label == 0) { // oa
        params.categoryId = examineId
        request = oaExamineFieldQueryUserFieldAPI
      } else if ([1, 2, 3].includes(label)) { // crm
        // crm审批label值替换为crm自定义字段label值
        params.label = {
          1: 6,
          2: 7,
          3: 18
        }[label]
        request = crmFieldQueryUserFieldAPI
      } else if ([5, 6, 7, 8, 9, 10, 11, 12].includes(label)) { // jxc
        // 签1产品2供应单 3采购订单 4 采购退货单5 销售订单 6销售退货单 7入库单8 出库单 9付款单10 回款单 11盘点 12调拨
        params.label = {
          5: 3,
          6: 4,
          7: 5,
          8: 6,
          9: 9,
          10: 10,
          11: 11,
          12: 12
        }[label]
        request = jxcFieldQueryUserFieldAPI
      } else if (detailData?.settingId) { // 阶段逻辑
        request = crmFlowQueryFlowUserFieldAPI
        params.settingId = detailData?.settingId
      }
      // 阶段通过联动表单获取
      if (request) {
        request(params)
          .then(res => {
            this.memberControlList = res.data || []
          })
      }
    },

    memberControlFun() {
      return this.memberControlList
    }

  }
}
