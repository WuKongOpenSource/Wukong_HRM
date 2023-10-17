<template>
  <el-dialog
    :visible.sync="visible"
    :close-on-click-modal="false"
    modal-append-to-body
    append-to-body
    title="高级筛选"
    width="900px"
    @close="handleCancel">
    <div style="margin-bottom: 8px;">筛选条件<i v-if="config.exportHelp" class="wk wk-icon-fill-help wk-help-tips" :data-type="config.exportHelp.type" :data-id="config.exportHelp.id" /></div>
    <filter-fields
      :condition-type-fun="conditionTypeFun"
      :form="form"
      :field-list="fieldList"
      :props="config.filterFieldsProps"
      :show-export="config.showExport"
      @on-field-change="getError"
      @on-field-delete="handleDelete"
      @on-field-export="handleExport"
    />
    <p
      v-show="showErrors"
      class="el-icon-warning warning-info">
      <span class="desc">{{ errorTypes[errorType] }}</span>
    </p>
    <el-button
      type="text"
      style="padding-right: 0;padding-left: 0;"
      @click="handleAdd">+ 添加筛选条件</el-button>
    <div
      v-if="config.showSaveScene"
      class="save">
      <el-checkbox v-model="saveChecked">保存为场景</el-checkbox>
      <el-input
        v-show="saveChecked"
        v-model.trim="saveName"
        :maxlength="10"
        class="name"
        placeholder="场景名称，最多10个字符" />
      <div
        v-show="saveChecked"
        class="save-setting">
        <el-checkbox v-model="saveDefault">设置为默认</el-checkbox><i v-if="config.help" class="wk wk-icon-fill-help wk-help-tips" :data-type="config.help.type" :data-id="config.help.id" />
      </div>
    </div>
    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        type="primary"
        @click="handleConfirm(false)">确定</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
/**
 * @description: 高级筛选
 *
 * 事件
 * filter 返回 对象和 导出
 */

import FilterFields from './FilterFields'

import { objDeepCopy } from '@/utils'
import { getFilterPostData } from './utils'
import merge from '@/utils/merge'

const DefaultFilterForm = {
  showExport: true, // 控制是否展示外露按钮
  showSaveScene: true, // 控制是否展示保存场景
  help: null,
  exportHelp: null,
  saveRequest: null, // 保存外漏筛选的请求
  saveParams: null,
  filterFieldsProps: null // 标签属性
}

/**
 * fieldList: 高级筛选的字段
 *     type:  date || datetime || select || 其他 input
 */
export default {
  name: 'FilterForm',
  components: {
    FilterFields
  },
  mixins: [],
  props: {
    dialogVisible: {
      type: Boolean,
      required: true,
      default: false
    },
    fieldList: {
      type: Array,
      required: true,
      default: () => {
        return []
      }
    },
    obj: {
      type: Object,
      required: true,
      default: () => {
        return {}
      }
    },
    conditionTypeFun: Function, // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
    props: Object
    // // 辅助 使用 公海没有场景
    // saveScene: {
    //   type: Boolean,
    //   default: true
    // },
    // // 控制是否展示外露按钮
    // showExport: {
    //   type: Boolean,
    //   default: true
    // }
  },
  data() {
    return {
      form: [], // 当前展示数组包含值， obj 请求提交数组
      visible: false,
      errorTypes: ['筛选条件中有重复项！', '请选择筛选条件！'],
      errorType: 0,
      showErrors: false,
      saveChecked: false, // 展示场景
      saveDefault: false, // 设置为默认场景
      saveName: null // 场景名称
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultFilterForm }, this.props || {})
    }
    // // 帮助信息
    // helpObj() {
    //   return {
    //     leads: {
    //       type: '7',
    //       id: '59'
    //     }, customer: {
    //       type: '8',
    //       id: '60'
    //     }, contacts: {
    //       type: '9',
    //       id: '61'
    //     }, business: {
    //       type: '10',
    //       id: '62'
    //     }, contract: {
    //       type: '11',
    //       id: '63'
    //     }, receivables: {
    //       type: '12',
    //       id: '64'
    //     }, invoice: {
    //       type: '13',
    //       id: '65'
    //     }, visit: {
    //       type: '14',
    //       id: '66'
    //     }, product: {
    //       type: '15',
    //       id: '67'
    //     }
    //   }[this.crmType] || null
    // },

    // // 外露帮助信息
    // exportHelpObj() {
    //   return {
    //     leads: {
    //       type: '7',
    //       id: '68'
    //     }, customer: {
    //       type: '8',
    //       id: '69'
    //     }, contacts: {
    //       type: '9',
    //       id: '70'
    //     }, business: {
    //       type: '10',
    //       id: '71'
    //     }, contract: {
    //       type: '11',
    //       id: '72'
    //     }, receivables: {
    //       type: '12',
    //       id: '73'
    //     }, invoice: {
    //       type: '13',
    //       id: '74'
    //     }, visit: {
    //       type: '14',
    //       id: '75'
    //     }, product: {
    //       type: '15',
    //       id: '76'
    //     }
    //   }[this.crmType] || null
    // }
  },
  watch: {
    dialogVisible: {
      handler(val) {
        if (val) {
          this.form = objDeepCopy(this.obj.form)
          if (this.form.length == 0) {
            this.form.push(this.getFilterItem())
          }
          this.saveChecked = false
          this.saveDefault = false
          this.saveName = null
        }
        this.visible = this.dialogVisible
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * 取消选择
     */
    handleCancel() {
      this.$emit('update:dialogVisible', false)
    },

    /**
     *获取错误
     */
    getError() {
      this.showErrors = false
      const fieldNames = []
      for (let index = 0; index < this.form.length; index++) {
        const item = this.form[index]
        if (item.fieldName === '' || item.fieldName === undefined || item.fieldName === null) {
          this.errorType = 1
          this.showErrors = true
          break
        } else if (fieldNames.includes(item.fieldName)) {
          this.errorType = 0
          this.showErrors = true
          break
        } else {
          fieldNames.push(item.fieldName)
        }
      }

      return this.showErrors
    },

    /**
     * 确定选择
     * isExport 是外露点击 不发送请求
     */
    handleConfirm(isExport = false) {
      if (this.getError()) {
        return
      }
      if (this.saveChecked) {
        if (!this.saveName || this.saveName === '') {
          this.$message.error('场景名称不能为空！')
          return
        }
      }

      const data = {
        obj: getFilterPostData(this.form),
        form: this.form,
        saveChecked: this.saveChecked,
        saveDefault: this.saveDefault,
        saveName: this.saveName
      }
      this.$emit('filter', data, isExport)

      this.saveExportFields()
    },

    /**
     * 保存外露筛选
     */
    saveExportFields() {
      if (this.config.saveRequest) {
        const exportFieldNames = []
        this.form.forEach(item => {
          if (item.isOut === 1) {
            exportFieldNames.push(item.fieldName)
          }
        })

        const params = this.config.saveParams ? {
          defaultValue: JSON.stringify(exportFieldNames),
          ...this.config.saveParams
        } : {
          defaultValue: JSON.stringify(exportFieldNames)
        }
        this.config.saveRequest(params).then(res => {

        }).catch(() => {})
      }
    },

    /**
     * 添加筛选条件
     */
    handleAdd() {
      this.form.push(this.getFilterItem())
    },

    /**
     * 获取filterItem
     */
    getFilterItem() {
      return {
        fieldName: '',
        name: '',
        formType: '',
        isOut: 0, // 外露
        condition: 'contains',
        type: 3,
        value: '',
        setting: [],
        typeOption: [],
        settingList: [],
        flowName: '', // 阶段名称
        settingName: '', // 流程名称
        address: {
          state: '',
          city: '',
          area: ''
        }
      }
    },

    /**
     * 删除筛选条件
     * @param index
     */
    handleDelete(index) {
      this.$confirm('您确定要删除这一条数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.form.splice(index, 1)
          this.getError()
        })
        .catch(() => {})
    },

    /**
     * 点击了外漏筛选
     */
    handleExport() {
      this.handleConfirm(true)
    }
  }
}
</script>

<style lang="scss" scoped>
::v-deep .el-dialog__body {
  padding: 10px 20px;
}

::v-deep .el-form-item__label {
  width: 100%;
  text-align: left;
}

.filter-fields {
  max-height: 300px;
  overflow-y: auto;
}

.save {
  margin-top: 10px;

  .name {
    width: 300px;
    margin-left: 10px;

    ::v-deep .el-input__inner {
      height: 32px;
    }
  }

  .save-setting {
    margin-top: #{$--interval-base};
  }
}

.warning-info {
  width: 100%;
  margin-top: 10px;
  font-size: 14px;
  color: #f56c6c;

  .desc {
    padding-left: 8px;
  }
}
</style>
