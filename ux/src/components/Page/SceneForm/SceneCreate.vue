<template>
  <el-dialog
    :title="editId ? '编辑场景' : '新建场景'"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :append-to-body="true"
    width="800px"
    @close="handleCancel">
    <div class="scene-name-container">
      <div class="scene-name">场景名称</div>
      <el-input
        v-model.trim="saveName"
        :maxlength="10"
        class="scene-input"
        placeholder="场景名称，最多10个字符" />
    </div>
    <div class="scene-name">筛选条件</div>
    <filter-fields
      id="scene-filter-container"
      :condition-type-fun="conditionTypeFun"
      :form="form"
      :field-list="fieldList"
      :props="filterFieldsProps"
      :show-export="false"
      @on-field-change="getError"
      @on-field-delete="handleDelete"
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
    <div class="save">
      <div class="save-setting">
        <el-checkbox v-model="saveDefault">设置为默认</el-checkbox><i v-if="config.help" class="wk wk-icon-fill-help wk-help-tips" :data-type="config.help.type" :data-id="config.help.id" />
      </div>
    </div>
    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">确定</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { productCategoryIndexAPI } from '@/api/admin/crm'

import FilterFields from '../FilterForm/FilterFields'

import { objDeepCopy } from '@/utils'
import AdvancedFilterMixin from '@/mixins/AdvancedFilter'
import { isArray, isEmpty } from '@/utils/types'
import merge from '@/utils/merge'

const DefaultSceneCreate = {
  // 场景编辑请求
  updateRequest: null,
  updateParams: null,
  // 场景新建请求
  saveRequest: null,
  saveParams: null,
  // 帮助提示
  help: null
}

/**
 * fieldList: 高级筛选的字段
 *     type:  date || datetime || select || 其他 input
 */
export default {
  name: 'SceneCreate', // 新建场景
  components: {
    FilterFields
  },
  mixins: [AdvancedFilterMixin],
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
    // /** 没有值就是全部类型 有值就是当个类型 */
    // crmType: {
    //   type: String,
    //   default: ''
    // },
    // 组件配置属性包含 DefaultSceneSet 里的配置项
    props: Object,
    filterFieldsProps: Object, // 筛选的配置
    conditionTypeFun: Function, // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
    /** 名字和 默认 id 编辑的时候需要 */
    name: {
      type: String,
      default: ''
    },
    isDefault: {
      type: Boolean,
      default: false
    },
    editId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      form: [],
      visible: false, // 控制展示
      errorTypes: ['筛选条件中有重复项！', '请选择筛选条件！'],
      errorType: 0,
      showErrors: false,
      saveDefault: false, // 设置为默认场景
      saveName: null, // 场景名称
      timeTypes: [
        { text: '本年度', value: 'year' },
        { text: '上一年度', value: 'lastYear' },
        { text: '下一年度', value: 'nextYear' },
        { text: '上半年', value: 'firstHalfYear' },
        { text: '下半年', value: 'nextHalfYear' },
        { text: '本季度', value: 'quarter' },
        { text: '上一季度', value: 'lastQuarter' },
        { text: '下一季度', value: 'nextQuarter' },
        { text: '本月', value: 'month' },
        { text: '上月', value: 'lastMonth' },
        { text: '下月', value: 'nextMonth' },
        { text: '本周', value: 'week' },
        { text: '上周', value: 'lastWeek' },
        { text: '下周', value: 'nextWeek' },
        { text: '今天', value: 'today' },
        { text: '昨天', value: 'yesterday' },
        { text: '明天', value: 'tomorrow' },
        { text: '过去7天', value: 'previous7day' },
        { text: '过去30天', value: 'previous30day' },
        { text: '未来7天', value: 'future7day' },
        { text: '未来30天', value: 'future30day' }
      ]
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultSceneCreate }, this.props || {})
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
    // }
  },
  watch: {
    dialogVisible: {
      handler(val) {
        if (val) {
          // 处理编辑数据
          let form = []
          if (this.editId) {
            // eslint-disable-next-line no-unused-vars
            for (const field in this.obj.obj) {
              const element = this.obj.obj[field]
              let item = this.getFilterItem()
              item.fieldName = element.name
              item.formType = element.formType
              item.type = element.type
              this.getAdvancedFilterOptions(element.formType, element.fieldName).forEach(child => {
                if (element.type === child.type) {
                  item.condition = child.value
                }
              })

              // 校准setting
              if (item.formType === 'checkbox' || item.formType === 'select' || item.formType === 'dealStatus' || item.formType === 'checkStatus') {
                item.setting = this.getEditSetting(item.formType, item.fieldName)
              }

              if (element.formType == 'date' || element.formType == 'datetime') {
                item.value = '' // 时间点
                this.$set(item, 'timeType', '')
                this.$set(item, 'timeTypeName', '')
                this.$set(item, 'range', [])

                if (item.type === 14) {
                  if (isArray(element.values) && element.values.length > 0) {
                    if (element.values.length === 1) {
                      item.timeType = element.values[0]
                      const timeObj = this.timeTypes.find(tItem => tItem.value === item.timeType)
                      if (timeObj) {
                        item.timeTypeName = timeObj.text
                      }
                    } else {
                      this.$set(item, 'range', element.values.slice(0, 2))
                    }
                  } else {
                    item.value = ''
                  }
                } else {
                  item.value = element.values[0]
                }
              } else if (
                element.formType == 'number' ||
                element.formType == 'floatnumber' ||
                element.formType == 'percent'
              ) {
                item.min = ''
                item.max = ''
                item.value = ''
                if (item.type === 14) {
                  item.min = isArray(element.values) && !isEmpty(element.values[0]) ? element.values[0] : ''
                  item.max = isArray(element.values) && !isEmpty(element.values[1]) ? element.values[1] : ''
                } else {
                  item.value = element.values[0]
                }
              } else if (element.formType == 'business_cause') {
                // name  flowName settingName
                // 仅 flowName 推入数组， settingName 仅处理值  flowName 在 settingName 之前
                if (element.name === 'settingName') {
                  item = form.find(item => item.formType === 'business_cause') || item
                  item.ignorePush = true // 忽略推入
                }
                item[element.name] = element.values[0]
                item.typeOption = this.getEditSetting(item.formType, item.fieldName)
                if (item.flowName) {
                  const obj = item.typeOption.find(typeItem => {
                    return typeItem.flowName === item.flowName
                  })
                  if (obj) {
                    item.settingList = obj.settingList
                  } else {
                    item.settingList = []
                  }
                }
              } else if (element.formType == 'user' ||
               element.formType == 'single_user' ||
               element.formType == 'structure' ||
               element.formType == 'checkbox' ||
               element.formType == 'position') {
                item.value = element.values
              } else if (element.formType == 'select') {
                if (this.getSettingValueType(item.setting) != 'string') {
                  item.value = element.values[0]
                } else {
                  item.value = element.values
                }
              } else if (element.formType == 'category') {
                item.value = []
                this.getProductCategoryValue(item, element.values[0])
              } else if (element.formType == 'map_address') {
                const addressArr = element.values[0].split(',')
                item.address = {
                  state: addressArr.length > 0 ? addressArr[0] : '',
                  city: addressArr.length > 1 ? addressArr[1] : '',
                  area: addressArr.length > 2 ? addressArr[2] : ''
                }
              } else {
                item.setting = element.setting
                item.value = element.values.join(';')
              }

              // 阶段组 会有重复 formType数据，拼成一个item，第二个item忽略
              if (!item.ignorePush) {
                form.push(item)
              }
            }
          } else {
            form = objDeepCopy(this.obj.form)
            if (form.length == 0) {
              form.push(this.getFilterItem())
            }
          }

          this.form = form
          /** 只有编辑会牵扯到这两个字段赋值 */
          if (this.name) {
            this.saveName = this.name
          } else {
            this.saveName = ''
          }
          if (this.isDefault) {
            this.saveDefault = this.isDefault
          } else {
            this.saveDefault = false
          }
        }
        this.visible = this.dialogVisible
      },
      deep: true,
      immediate: true
    },

    form() {
      this.$nextTick(() => {
        var container = document.getElementById('scene-filter-container')
        container.scrollTop = container.scrollHeight
      })
    }
  },
  methods: {
    /**
     * 获取编辑setting
     */
    getEditSetting(formType, fieldName) {
      const obj = this.fieldList.find(item => {
        return item.formType == formType && item.fieldName == fieldName
      })

      if (obj) {
        return obj.setting
      }

      return []
    },

    /**
     * 获取产品类别的值
     */
    getProductCategoryValue(item, categoryId) {
      productCategoryIndexAPI()
        .then(res => {
          const categoryList = res.data || []
          const categoryIds = []
          this.filterProductTree(categoryList, categoryId, categoryIds)
          item.value = categoryIds.reverse()
        })
        .catch(() => {})
    },

    filterProductTree(tree, categoryId, categoryIds) {
      for (let index = 0; index < tree.length; index++) {
        const element = tree[index]
        if (element.categoryId == categoryId) {
          categoryIds.push(categoryId)
          this.filterProductTree(tree, element.parentId, categoryIds)
          break
        }
      }
    },

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
     * 取消选择
     */
    handleCancel() {
      this.visible = false
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
     */
    handleConfirm() {
      if (this.getError()) {
        return
      }

      if (!this.saveName || this.saveName === '') {
        this.$message.error('场景名称不能为空！')
        return
      }
      for (let i = 0; i < this.form.length; i++) {
        const o = this.form[i]
        if (!o.fieldName || o.fieldName === '') {
          this.$message.error('筛选的字段名称不能为空！')
          return
        }

        // 商机组 formType 新建 business_type 筛选处同阶段 business_cause
        if (o.formType == 'business_cause') {
          if (!o.flowName && !o.settingName) {
            this.$message.error('筛选内容不能为空！')
            return
          }
        } else if (o.formType == 'map_address') {
          if (!o.address.state && !o.address.city && !o.address.area) {
            this.$message.error('筛选内容不能为空！')
            return
          }
        } else if (
          o.formType == 'date' ||
          o.formType == 'datetime'
        ) {
          if (o.condition != 'isNull' && o.condition != 'isNotNull') {
            if (o.type === 14) {
              if (isEmpty(o.timeType) && isEmpty(o.range)) {
                this.$message.error('筛选内容不能为空！')
                return
              }
            } else {
              if (isEmpty(o.value)) {
                this.$message.error('筛选内容不能为空！')
                return
              }
            }
          }
        } else if (
          o.formType == 'number' ||
          o.formType == 'floatnumber' ||
          o.formType == 'percent'
        ) {
          if (o.condition != 'isNull' && o.condition != 'isNotNull') {
            if (o.type === 14) {
              if (isEmpty(o.min) || isEmpty(o.max)) {
                this.$message.error('筛选内容不能为空！')
                return
              }
            } else {
              if (isEmpty(o.value)) {
                this.$message.error('筛选内容不能为空！')
                return
              }
            }
          }
        } else if (
          o.formType == 'user' ||
          o.formType == 'single_user' ||
          o.formType == 'structure' ||
          o.formType == 'category' ||
          o.formType == 'checkbox' ||
          o.formType == 'position'
        ) {
          if (isEmpty(o.value)) {
            if (o.condition != 'isNull' && o.condition != 'isNotNull') {
              this.$message.error('筛选内容不能为空！')
              return
            }
          }
        } else if (isEmpty(o.value)) {
          if (o.condition != 'isNull' && o.condition != 'isNotNull') {
            this.$message.error('筛选内容不能为空！')
            return
          }
        }
      }
      const obj = []
      this.form.forEach(o => {
        if (o.formType == 'datetime' || o.formType == 'date') {
          let dataValues = []
          if (o.type === 14) {
            if (!isEmpty(o.timeType)) {
              dataValues = [o.timeType]
            } else {
              dataValues = o.range
            }
          } else {
            dataValues = [o.value]
          }
          obj.push({
            formType: o.formType,
            name: o.fieldName,
            type: o.type,
            values: dataValues
          })
        } else if (
          o.formType == 'number' ||
          o.formType == 'floatnumber' ||
          o.formType == 'percent'
        ) {
          let dataValues = []
          if (o.type === 14) {
            dataValues = [isEmpty(o.min) ? '' : o.min, isEmpty(o.max) ? '' : o.max]
          } else {
            dataValues = [o.value]
          }
          obj.push({
            formType: o.formType,
            name: o.fieldName,
            type: o.type,
            values: dataValues
          })
        } else if (o.formType == 'business_cause') {
          // 阶段组 和 阶段 依据name传值
          if (o.flowName) {
            obj.push({
              formType: o.formType,
              name: 'flowName',
              type: 1,
              values: [o.flowName]
            })
          }
          if (o.settingName) {
            obj.push({
              formType: o.formType,
              name: 'settingName',
              type: 1,
              values: [o.settingName]
            })
          }
        } else if (o.formType == 'user' || o.formType == 'single_user') {
          obj.push({
            type: o.type,
            values: o.value,
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'position') {
          obj.push({
            type: o.type,
            values: o.value.filter(item => !isEmpty(item.name)).map(item => JSON.stringify(item)),
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'structure') {
          obj.push({
            type: o.type,
            values: o.value,
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'checkbox') {
          obj.push({
            type: o.type,
            values: o.value,
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'category') {
          obj.push({
            type: 1,
            values: [o.value[o.value.length - 1]],
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'select') {
          let values = o.value
          if (this.getSettingValueType(o.setting) != 'string') {
            values = [o.value]
          }
          obj.push({
            type: o.type,
            values: values,
            formType: o.formType,
            name: o.fieldName
          })
        } else if (o.formType == 'map_address') {
          const value = []
          // eslint-disable-next-line no-unused-vars
          for (const key in o.address) {
            const addValue = o.address[key]
            if (addValue) {
              value.push(addValue)
            }
          }
          obj.push({
            values: [value.join(',')],
            type: 1,
            formType: o.formType,
            name: o.fieldName
          })
        } else {
          let values = []
          if (typeof o.value === 'string') {
            const temp = o.value.replace(/；/g, ';')
            values = temp.split(';').filter(item => item !== '' && item !== null)
          } else {
            values = [o.value]
          }

          obj.push({
            type: o.type,
            values: values,
            formType: o.formType,
            name: o.fieldName
          })
        }
      })
      const data = {
        obj: obj,
        form: this.form,
        saveDefault: this.saveDefault,
        saveName: this.saveName
      }
      this.requestCreateScene(data)
    },

    /**
     * 创建场景
     */
    requestCreateScene(data) {
      /** 编辑操作 */
      if (this.editId) {
        this.config.updateRequest({
          isDefault: data.saveDefault ? 1 : 0,
          name: data.saveName,
          sceneId: this.editId,
          // type: crmTypeModel[this.crmType],
          data: JSON.stringify(data.obj),
          ...this.config.updateParams
        })
          .then(res => {
            this.$message({
              type: 'success',
              message: '编辑成功'
            })
            // 新建成功
            this.$emit('save-success')
            this.handleCancel()
          })
          .catch(() => {})
      } else {
        this.config.saveRequest({
          // type: crmTypeModel[this.crmType],
          isDefault: data.saveDefault ? 1 : 0,
          name: data.saveName,
          data: JSON.stringify(data.obj),
          ...this.config.saveParams
        })
          .then(res => {
            // 新建成功
            this.$emit('save-success')
            this.$message({
              type: 'success',
              message: '创建成功'
            })
            this.handleCancel()
          })
          .catch(() => {})
      }
    },

    /**
     * 添加筛选条件
     */
    handleAdd() {
      this.form.push(this.getFilterItem())
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
     * 获取setting数据类型
     */
    getSettingValueType(setting) {
      if (setting && setting.length > 0) {
        const value = setting[0]
        return typeof value
      }
      return []
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

.filter-container {
  max-height: 200px;
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

.el-form-item {
  margin-bottom: 0;
}

.el-row {
  margin-bottom: 20px;

  .delete-btn {
    margin-left: 15px;
    font-weight: normal;
    color: #bbb;
    cursor: pointer;
  }

  .el-select,
  .el-date-editor {
    width: 100%;

    ::v-deep .el-range__icon {
      line-height: 26px;
    }
  }

  .el-input-number {
    ::v-deep input {
      padding: 0 8px;
      text-align: left;
    }
  }

  .date-range-content {
    position: absolute;
    top: 2px;
    right: 30px;
    left: 30px;
    height: $--input-height - 4;
    line-height: $--input-height - 4;
    cursor: pointer;
    background-color: $--input-background-color;
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

.scene-name-container {
  padding-bottom: 15px;

  .scene-input {
    width: 300px;
  }
}

.scene-name {
  margin-bottom: $--interval-base;
}
</style>
