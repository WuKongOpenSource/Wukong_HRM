<template>
  <div class="field-custom-auth">
    <el-table :data="tableData">
      <el-table-column
        label="字段名称"
        show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span :class="{'sub-name': row.parentFieldId}">{{ row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column :width="110">
        <!--eslint-disable-next-line vue/no-unused-vars-->
        <template slot="header" slot-scope="scope">
          <el-checkbox
            v-model="authViewAll"
            :indeterminate="authViewHalf"
            @change="authViewAllChange" />
          <span class="header-text">可以查看</span>
        </template>
        <template slot-scope="{ row, $index }">
          <el-checkbox
            v-model="row.authView"
            @change="handleRowChange(row, 'authView', $index)" />
        </template>
      </el-table-column>

      <el-table-column
        v-if="!disabledEdit"
        :width="110">
        <!--eslint-disable-next-line vue/no-unused-vars-->
        <template slot="header" slot-scope="scope">
          <el-checkbox
            v-model="authEditAll"
            :indeterminate="authEditHalf"
            @change="authEditAllChange" />
          <span class="header-text">可以修改</span>
        </template>
        <template slot-scope="{ row, $index }">
          <el-checkbox
            v-if="getCanEditStatus(row)"
            v-model="row.authEdit"
            @change="handleRowChange(row, 'authEdit', $index)" />

          <el-popover
            v-if="detailTableFormTypes.includes(row.formType)"
            placement="bottom"
            width="200"
            trigger="click">
            <div class="table-set">
              <div class="set-item">
                <el-checkbox
                  v-model="row.tableLineAddEdit" />
                <span class="header-text">新增行</span>
              </div>
              <div class="set-item">
                <el-checkbox
                  v-model="row.tableLineDeleteEdit" />
                <span class="header-text">删除已有行</span>
              </div>
            </div>
            <el-button
              slot="reference"
              type="primary-text"
              style="padding: 0; margin-left: 8px;">表格权限</el-button>
          </el-popover>

        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'FieldCustomAuth',
  props: {
    fieldList: {
      type: Array,
      required: true
    }, // 元素内 可增加 hideEdit  hideShow
    authData: Array, // 已有字段
    disabledEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // nodeFieldItemParamsFun 处的  formType 也需要调整
      detailTableFormTypes: ['detail_table', 'business_cause', 'examine_cause'], // 类似明细表格逻辑的 formType
      tableData: [],

      authViewAll: false,
      authViewHalf: false,

      authEditAll: false,
      authEditHalf: false

    }
  },
  computed: {
    observerObj() {
      return [this.fieldList, this.authData]
    }
  },
  watch: {
    observerObj: {
      handler() {
        this.initCom()
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    initCom() {
      const list = []
      let authData = []
      if (this.authData) {
        authData = this.authData
      }

      // authLevel 1 不能查看不能编辑 2 可查看  3 可编辑可查看
      this.fieldList.forEach(field => {
        list.push(this.getAuthItem(authData, field))
        if (this.detailTableFormTypes.includes(field.formType) && field.fieldExtendList) {
          const tableItem = authData.find(o => o.fieldName === field.fieldName)
          field.fieldExtendList.forEach(subField => {
            list.push(this.getAuthItem(tableItem?.fieldExtendList, subField, field, tableItem))
          })
        }
      })

      this.tableData = list
      this.setAllCheckStatus()
    },

    /**
     * @description: 获取对象
     * @param {*} authData 已配置的数据
     * @param {*} field 字段
     * @param {*} parentField 明细表格父字段
     * @return {*}
     */
    getAuthItem(authData, field, parentField, parentTableItem) {
      let authLevel = null
      let findRes = null
      if (!authData || authData.length === 0) { // 没数据时 默认都是查看
        authLevel = 2
      } else {
        findRes = authData.find(o => o.fieldName === field.fieldName)
        authLevel = findRes ? findRes.authLevel : 1 // 没在旧数据里的 不能查看不能编辑
      }

      console.log('getAuthItem', field.name, parentField?.name)

      const defaultItem = {
        fieldId: field.fieldId,
        formType: field.formType,
        fieldName: field.fieldName,
        name: field.name,
        hideEdit: field.hideEdit,
        tableLevel: findRes ? findRes.tableLevel || 0 : 0,
        // hideShow: field.hideShow,
        authLevel,
        authView: [2, 3].includes(authLevel), // 查看
        authEdit: authLevel === 3 // 编辑
      }

      // 不存在的话tableLevel默认为0 所有权限都没有
      if (this.detailTableFormTypes.includes(field.formType)) {
        const binaryStr = defaultItem.tableLevel.toString(2).padStart(3, '0')
        defaultItem.tableLineAddEdit = Boolean(Number(binaryStr.charAt(0))) // 行添加
        defaultItem.tableLineEdit = Boolean(Number(binaryStr.charAt(1))) // 行编辑
        defaultItem.tableLineDeleteEdit = Boolean(Number(binaryStr.charAt(2))) // 行删除
      }

      if (parentField) {
        defaultItem.parentFieldId = parentField.fieldId
        // 父不存在是 父级权限默认为1  子也默认为1
        if (!parentTableItem) {
          defaultItem.authLevel = 1
          defaultItem.authView = false
          defaultItem.authEdit = false
        }
        defaultItem.fieldId = field.id
      }
      return defaultItem
    },

    /**
     * 判断字段是否能够赋予编辑权限
     * @param field
     * @return {boolean}
     */
    getCanEditStatus(field) {
      if (this.disabledEdit || field.hideEdit) return false
      return ![
        'serial_number'
      ].includes(field.formType)
    },

    /**
     * 查看权限全部勾选状态
     * @param value
     */
    authViewAllChange(value) {
      this.tableData.forEach(row => {
        this.$set(row, 'authView', value)
      })
      this.authViewHalf = false
      this.authViewAll = value

      // 如果取消查看权限，编辑权限也取消
      if (!value) {
        this.authEditAllChange(false)
      }
    },

    /**
     * 修改权限全部勾选状态
     * @param value
     */
    authEditAllChange(value) {
      this.tableData.forEach(row => {
        this.$set(row, 'authEdit', value)
        if (value) {
          this.$set(row, 'authView', value)
        }
      })
      this.authEditHalf = false
      this.authEditAll = value
    },

    setAllCheckStatus() {
      const len = this.tableData.length
      if (!len) {
        this.authViewAll = false
        this.authViewHalf = false

        this.authEditAll = false
        this.authViewHalf = false
        return
      }

      const keys = ['authView', 'authEdit']
      keys.forEach(key => {
        const count = this.tableData.filter(o => o[key]).length
        this[`${key}All`] = count === len
        this[`${key}Half`] = count > 0 && count !== len
      })
    },

    /**
     * 单个字段勾选状态切换
     */
    handleRowChange(row, key, index) {
      console.log('row change', row, key)
      switch (key) {
        case 'authView':
          if (!row.authView) row.authEdit = false
          break
        case 'authEdit':
          if (row.authEdit) row.authView = true
          break
      }

      // 明细表格 做个简单联动
      if (row.parentFieldId && row[key]) {
        const parentItem = this.tableData.find(o => o.fieldId === row.parentFieldId)
        if (parentItem) {
          parentItem[key] = true
        }
      }

      this.setAllCheckStatus()
    },

    getSaveForm() {
      const result = []
      this.tableData.forEach(row => {
        if (this.detailTableFormTypes.includes(row.formType)) {
          const subList = this.tableData.filter(field => field.parentFieldId === row.fieldId)
          const fieldExtendList = subList.map(field => this.getPostItem(field))
          const postItem = this.getPostItem(row)
          postItem.fieldExtendList = fieldExtendList
          result.push(postItem)
        } else if (!row.parentFieldId) {
          // 不插入明细表格子项
          result.push(this.getPostItem(row))
        }
      })
      return result
    },

    /**
     * @description: 获取提交参数
     * @return {*}
     */
    getPostItem(row) {
      let authLevel
      if (row.authEdit && this.getCanEditStatus(row)) {
        authLevel = 3 // 可编辑可查看
      } else if (row.authView) {
        authLevel = 2 // 可查看
      } else {
        authLevel = 1 // 不能查看不能编辑
      }

      const defaultItem = {
        fieldId: row.fieldId,
        formType: row.formType,
        fieldName: row.fieldName,
        name: row.name,
        authLevel
      }

      if (this.detailTableFormTypes.includes(row.formType)) {
        const binaryArr = []
        binaryArr.push(row.tableLineAddEdit ? '1' : '0')
        binaryArr.push(row.tableLineEdit ? '1' : '0')
        binaryArr.push(row.tableLineDeleteEdit ? '1' : '0')
        const binaryStrs = binaryArr.join('')
        defaultItem.tableLevel = parseInt(binaryStrs, 2)
      }
      return defaultItem
    }
  }
}
</script>

<style scoped lang="scss">
.field-custom-auth {
  .header-text {
    margin: 0 6px;
  }

  .wk-help-tips {
    font-size: 14px;
    color: $--color-n80;
  }

  .table-set {
    .set-item + .set-item {
      margin-top: 8px;
    }
  }

  .sub-name {
    margin-left: 16px;
  }
}
</style>
