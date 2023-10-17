<template>
  <el-dialog
    :visible="visible"
    :close-on-click-modal="false"
    :title="getTitle()"
    :width="contentWidth"
    class="crm-ralative"
    append-to-body
    @close="closeView">
    <div ref="crContainer" class="cr-container-wrap">
      <div class="cr-container">
        <div
          v-if="!currentDataType && !config.isCommon"
          class="cr-body-side">
          <div
            v-for="(item, index) in leftSides"
            :key="index"
            :class="leftType===item.type? 'side-item-select' : 'side-item-default'"
            class="side-item"
            @click="sideClick(item)">{{ item.name }}
            <span v-if="item.num > 0" class="side-item__num">{{ `(${item.num})` }}</span>
          </div>
        </div>

        <div v-if="config.isCommon">
          <!-- 常规 -->
          <crm-relative-table
            ref="moduleTable"
            :props="config"
            :selected-data="currentSelectedData[leftType]"
            @selection-change="selectionChange($event, leftSides.find(o => o.type === leftType))"
          />
        </div>
        <div v-else :style="{ 'padding-left': !currentDataType ? '150px' : '0'}">
          <keep-alive :include="leftSides.filter(o => o.loaded).map(o => o.component)">
            <component
              :is="getCurrentComponent(leftType)"
              ref="moduleTable"
              class="crm-table-index"
              :props="componentConfig[leftType]"
              :selected-data="currentSelectedData[leftType]"
              @selection-change="selectionChange($event, leftSides.find(o => o.type === leftType))" />
          </keep-alive>
        </div>
      </div>
      <div
        :class="['dialog-footer', { 'is-common': config.isCommon }]"
      >
        <slot name="footer" />
        <el-button
          type="primary"
          @click="confirmClick(false)">确定</el-button>
        <el-button @click="closeView">取消</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script type="text/javascript">
import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const DefaultCrmRelatieve = {
  // start 普通列表发票抬头有用 // 通用配置 含 CrmRelativeTable 声明的信息
  isCommon: false,
  type: '',
  name: '',
  // end
  dataType: null, // 单模块 props 的优先 config的次之
  showTypes: null, // 多模块 props 的优先 config的次之
  params: null, // 键值参数
  paramsObj: null, // 多模块下 键值参数
  mainTableProps: null // 非普通表格的props
}

export default {
  name: 'CrmRelatieve', // 相关
  components: {
    CrmRelativeTable: () => import('./CrmRelativeTable')
  },
  // 处理el-input 右侧展示删除按钮
  provide() {
    return {
      'elForm': '',
      'elFormItem': ''
    }
  },
  props: {
    // 自定义配置
    props: Object,
    // 没有值就是全部类型 有值就是当个类型
    dataType: String,
    // 需要展示哪些类型 默认关键字数组
    showTypes: Array,
    // 已选信息单类型或者多类型都按照键值保存
    selectedData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    visible: Boolean,
    // 多选框 只能选一个
    radio: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      leftType: '',
      leftSides: [],
      // 各类型选择的值
      currentSelectedData: {}
    }
  },
  computed: {
    config() {
      return merge({ ...DefaultCrmRelatieve }, this.props || {})
    },

    currentDataType() {
      return this.dataType || this.config.dataType
    },

    currentShowTypes() {
      return this.showTypes || this.config.showTypes || [
        'customer',
        'contacts',
        'leads',
        'business',
        'contract',
        'product',
        'receivables'
      ]
    },

    // 模块配置 非普通类型的
    componentConfig() {
      const getConfig = (dataTpe) => {
        let params = {}
        // dataType存在读取params值，说明是单模块选择
        if (this.currentDataType) {
          params = this.config.params
        } else if (this.config.paramsObj && this.config.paramsObj[dataTpe]) {
          params = this.config.paramsObj[dataTpe]
        }

        const config = {
          isSelect: true,
          showModuleName: false,
          selectionHandle: false,
          radio: this.radio,
          params,
          otherHeight: 340, // 去掉dialog 等处的定高区域
          ...(this.config.mainTableProps || {})
        }
        return config
      }

      const propsObj = {}
      this.leftSides.forEach(item => {
        propsObj[item.type] = getConfig(item.type)
      })

      return propsObj
    },

    // 内容宽度
    contentWidth() {
      return this.currentDataType && !this.config.isCommon ? '1050px' : '910px'
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.$nextTick(() => {
          const mainTable = this.getCurrentMainTable()
          if (mainTable.updateTableHeight) {
            mainTable.updateTableHeight()
          }
        })
      }
    },
    selectedData: function(data) {
      this.handleCurrentSelectData(data)
    }
  },
  mounted() {
    // 获取传值
    this.handleCurrentSelectData(this.selectedData)
  },
  methods: {
    /**
     * @description: 目前选择值
     * @param {*} data 选择的数据
     * @return {*}
     */
    handleCurrentSelectData(data) {
      if (this.config.isCommon) {
        const tempData = {}
        tempData[this.config.type] = []
        this.currentSelectedData = tempData
      } else {
        if (data && Object.keys(data).length) {
          this.currentSelectedData = objDeepCopy(data)
        } else {
          const tempData = {}
          if (this.currentDataType) {
            tempData[this.currentDataType] = []
            this.currentSelectedData = tempData
          } else {
            for (let index = 0; index < this.currentShowTypes.length; index++) {
              const key = this.currentShowTypes[index]
              tempData[key] = []
            }
            this.currentSelectedData = tempData
          }
        }
      }

      // 初始化
      if (this.config.isCommon) {
        this.leftSides.push({
          name: this.config.name,
          isCommon: true, // 是统一效果
          loaded: true,
          num: 0,
          type: this.config.type
        })
        this.leftType = this.config.type
      } else {
        if (this.leftSides.length === 0) {
          const leftItems = this.getLeftItems()
          if (this.currentDataType) {
            const leftItem = leftItems[this.currentDataType]
            leftItem.loaded = true
            this.leftSides.push(leftItem)
            this.leftType = this.currentDataType
          } else {
            for (let index = 0; index < this.currentShowTypes.length; index++) {
              const leftItem = leftItems[this.currentShowTypes[index]]
              const leftData = this.currentSelectedData[leftItem.type]
              leftItem.num = leftData ? leftData.length : 0
              this.leftSides.push(leftItem)
            }

            if (this.leftSides.length > 0) {
              const leftItem = this.leftSides[0]
              leftItem.loaded = true
              this.leftType = leftItem.type
            }
          }
        } else {
          // 更新多tabs 下数据
          if (!this.currentDataType) {
            for (let index = 0; index < this.leftSides.length; index++) {
              const leftItem = this.leftSides[index]
              const leftData = this.currentSelectedData[leftItem.type]
              leftItem.num = leftData ? leftData.length : 0
            }
          }
        }
      }
    },

    /**
     * @description: 刷新列表
     * @return {*}
     */
    refreshList() {
      this.$refs['crm' + this.currentDataType][0].refreshList()
    },

    /**
     * @description: 侧边点击
     * @param {*} item
     * @return {*}
     */
    sideClick(item) {
      this.leftType = item.type
      item.loaded = true
    },

    /**
     * @description: 关闭操作
     * @return {*}
     */
    closeView() {
      this.$emit('cancel')
      this.closeEmit()
    },

    /**
     * @description: 关闭逻辑与取消按钮分开
     * @return {*}
     */
    closeEmit() {
      this.$emit('close')
      this.$emit('update:visible', false)
    },

    /**
     * @description: 勾选change
     * @param {*} data 选择的数据
     * @param {*} item 数据对应的菜单
     * @return {*}
     */
    selectionChange(data, item) {
      item.num = data.length
      this.currentSelectedData[this.leftType] = data
    },

    /**
     * @description: 确定选择
     * @return {*}
     */
    confirmClick() {
      // 如果是单类型 在这里数据赋值
      if (this.currentDataType) {
        this.currentSelectedData[this.leftType] = this.getCurrentMainTable().selectionList
      }

      if (this.currentDataType) {
        // 以单类型传值
        this.$emit('changeCheckout', {
          data: this.currentSelectedData[this.currentDataType]
            ? this.currentSelectedData[this.currentDataType]
            : [],
          props: this.config
        })
      } else {
        this.$emit('changeCheckout', {
          data: this.currentSelectedData,
          props: this.config
        })
      }
      this.$nextTick(() => {
        this.closeEmit()
      })
    },

    /**
     * @description: 获取当前主列表对象
     * @return {*}
     */
    getCurrentMainTable() {
      return this.$refs.moduleTable
    },

    /**
     * @description: 获取当前组件
     * @return {*}
     */
    getCurrentComponent(leftType) {
      return this.getLeftItems()[leftType]?.component || 'CrmRelativeTable'
    },

    /**
     * @description: 根据类型获取标题展示名称
     * @return {*}
     */
    getTitle() {
      if (this.config.isCommon) {
        return this.config.name
      }
      if (this.currentDataType == 'leads') {
        return '关联线索模块'
      } else if (this.currentDataType == 'customer') {
        return '关联客户模块'
      } else if (this.currentDataType == 'contacts') {
        return '关联联系人模块'
      } else if (this.currentDataType == 'business') {
        return '关联商机模块'
      } else if (this.currentDataType == 'product') {
        return '关联产品模块'
      } else if (this.currentDataType == 'contract') {
        return '关联合同模块'
      } else if (this.currentDataType == 'receivables') {
        return '关联回款模块'
      } else if (this.currentDataType == 'item') {
        return '关联事项'
      } else if (this.currentDataType == 'module') {
        return '关联产品模块'
      } else {
        return '关联相关信息'
      }
    },

    /**
     * @description: 设置勾选值
     * @param {*} data
     * @return {*}
     */
    setSelections(data) {
      this.getCurrentMainTable().setSelections(data)
    },

    /**
     * @description: 切换某一行的选中状态
     * @param {*} rowKey
     * @param {*} rowId
     * @param {*} selected
     * @return {*}
     */
    toggleRowSelection(rowKey, rowId, selected) {
      this.getCurrentMainTable().toggleRowSelection(rowKey, rowId, selected)
    },

    /**
     * @description: 获取左侧菜单信息
     * @return {*}
     */
    getLeftItems() {
      return {
        customer: {
          name: '客户',
          loaded: false,
          num: 0,
          type: 'customer',
          component: 'CustomerIndex'
        },
        contacts: {
          name: '联系人',
          loaded: false,
          num: 0,
          type: 'contacts',
          component: 'ContactsIndex'
        },
        leads: {
          name: '线索',
          loaded: false,
          num: 0,
          type: 'leads',
          component: 'LeadsIndex'
        },
        business: {
          name: '商机',
          loaded: false,
          num: 0,
          type: 'business',
          component: 'BusinessIndex'
        },
        contract: {
          name: '合同',
          loaded: false,
          num: 0,
          type: 'contract',
          component: 'ContractIndex'
        },
        product: {
          name: '产品',
          loaded: false,
          num: 0,
          type: 'product',
          component: 'ProductIndex'
        },
        receivables: {
          name: '回款',
          loaded: false,
          num: 0,
          type: 'receivables',
          component: 'ReceivablesIndex'
        },
        // 全部事项
        item: {
          name: '全部事项',
          loaded: false,
          num: 0,
          type: 'item',
          component: 'AllItem'
        },
        module: {
          name: '产品',
          loaded: false,
          num: 0,
          type: 'module',
          component: 'ModuleIndex'
        }
      }
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.cr-container {
  position: relative;
  height: 100%;
}

.cr-container-wrap {
  position: relative;
  height: 100%;
}

.dialog-footer {
  margin-top: 0;
  margin-right: 40px;
  text-align: right;
}

.cr-body-side {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 3;
  flex-shrink: 0;
  width: 150px;
  height: 100%;
  font-size: 12px;
  background-color: white;
  border-right: 1px solid $--border-color-base;

  .side-item {
    position: relative;
    height: 40px;
    padding: 0 20px;
    font-size: 14px;
    line-height: 40px;
    cursor: pointer;

    &__num {
      position: absolute;
      top: 0;
      right: 5px;
      font-size: 12px;
      color: $--color-text-regular;
    }
  }

  .side-item::before {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    width: 2px;
    content: " ";
  }
}

.side-item-default {
  color: $--color-text-primary;
}

.side-item-select {
  background-color: $--background-color-base;
}

.side-item-select::before {
  background-color: $--color-primary;
}

.el-dialog__wrapper {
  ::v-deep .el-dialog {
    height: calc(100% - 80px);
    margin-top: 40px !important;
    margin-bottom: 40px;
  }

  ::v-deep .el-dialog__body {
    height: calc(100% - 100px);
    padding: 0;
  }
}

// 客户 线索等列表头上的padding 去除
::v-deep .crm-table-index {
  .wk-page-header {
    padding-top: 0;
  }
}
</style>
