<template>
  <div class="setting-options">
    <draggable
      :list="optionsList"
      :options="dragConfig"
      @sort="handleChange">
      <div
        v-for="(item, index) in optionsList"
        :key="index"
        class="option-item">
        <el-input
          v-model="item.value"
          :disabled="!optionsEditAuth"
          @change="handleChange">
          <flexbox v-if="optionsEditAuth" slot="suffix">
            <div class="el-input__icon drag-hook wk wk-grid" />
            <el-button
              :disabled="optionsList.length <= 1"
              type="text"
              class="el-input__icon wk wk-icon-bin"
              @click="handleDelete(index)" />
          </flexbox>
        </el-input>
      </div>
    </draggable>

    <div
      v-if="showOther"
      class="option-item other-item">
      <el-input
        value="其他"
        disabled>
        <flexbox v-if="optionsEditAuth" slot="suffix">
          <el-button
            type="text"
            class="el-input__icon wk wk-icon-bin"
            @click="handleDelete(-1)" />
        </flexbox>
      </el-input>
    </div>

    <el-button
      v-if="optionsEditAuth"
      class="add-btn"
      @click="handleAdd">
      <i class="el-icon-plus" /> 添加新选项
    </el-button>

    <flexbox
      v-if="optionsEditAuth"
      align="center"
      justify="center">
      <div class="add-other-btn" @click="handleAddOther">
        添加其他
      </div>
      <flexbox-item />
      <div class="add-other-btn" @click="handleUpdateAll">批量编辑</div>
    </flexbox>

    <el-dialog
      :visible.sync="dialogVisible"
      :before-close="handleCloseDialog"
      :close-on-click-modal="false"
      title="批量编辑"
      width="500px"
      class="edit-dialog">
      <div>
        <div class="edit-tips">
          每行内容对应一个选项，点击完成后，逻辑表单设置将失效
        </div>
        <el-input
          v-model="dialogContentVal"
          :rows="10"
          resize="none"
          type="textarea" />
      </div>
      <div slot="footer">
        <el-button @click="handleCloseDialog">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleDialogConfirm">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import { isEmpty, isArray, isObject } from '@/utils/types'
import { getFieldAuth } from '../../utils'
import { guid } from '@/utils'

const LOGIC_OTHER = '其他'

export default {
  name: 'SettingOptions',
  components: {
    draggable
  },
  props: {
    field: {
      type: Object,
      required: true
    },
    isTableChild: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      optionsList: [],
      dialogVisible: false,
      dialogContentVal: ''
    }
  },
  computed: {
    optionsEditAuth() {
      return getFieldAuth(this.field.operating).optionsEdit
    },

    showOther() {
      return this.field.setting.includes(LOGIC_OTHER)
    },

    dragConfig() {
      return {
        group: guid(),
        forceFallback: false,
        disabled: !this.optionsEditAuth,
        fallbackClass: 'draggingStyle',
        handle: '.drag-hook',
        filter: '.el-input__inner',
        preventOnFilter: false
      }
    }
  },
  watch: {
    field: {
      handler(newVal, oldVal) {
        if (!oldVal || newVal.options !== oldVal.options) {
          const setting = this.field.setting
          const isObjectValue = setting.length > 0 && isObject(setting[0])
          if (isObjectValue) {
            this.optionsList = setting.map(o => {
              return {
                value: o.name
              }
            })
          } else {
            this.optionsList = setting
              .filter(o => o !== LOGIC_OTHER)
              .map(o => {
                return { value: o }
              })
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * 修改、排序
     */
    handleChange() {
      // 选项不允许重复或有空
      let arr = this.optionsList
        .map(o => o.value)
        .filter(o => !isEmpty(o) && o !== LOGIC_OTHER)

      // 内容清空 并且没有一个选项时，加入一个选项
      if (arr.length === 0) {
        this.optionsList = [{
          value: '选1'
        }]
        arr = ['选1']
      }
      arr = Array.from(new Set(arr))
      if (arr.length !== this.optionsList.length) {
        this.optionsList = arr.map(o => {
          return { value: o }
        })
      }
      if (this.showOther) {
        arr.push(LOGIC_OTHER)
      }
      this.field.setting = arr
      if (this.field.remark === 'options_type') {
        // 如果是逻辑表单
        const optionsData = {}
        const keys = Object.keys(this.field.optionsData)
        this.optionsList.forEach(o => {
          if (keys.includes(o.value)) {
            optionsData[o.value] = this.field.optionsData[o.value]
          } else {
            optionsData[o.value] = []
          }
        })
        Object.keys(optionsData).forEach(key => {
          const findRes = this.optionsList.find(o => o.value === key && key !== LOGIC_OTHER)
          if (!findRes) {
            delete optionsData[key]
          }
        })
        if (!this.showOther) {
          delete optionsData[LOGIC_OTHER]
        } else if (keys.includes(LOGIC_OTHER)) {
          optionsData[LOGIC_OTHER] = this.field.optionsData[LOGIC_OTHER]
        }
        this.field.options = JSON.stringify(optionsData, this.field.setting)
        this.$set(this.field, 'optionsData', optionsData)
      } else {
        // 如果不是逻辑表单
        this.field.options = arr.join(',')
      }
      this.$set(this.field, 'setting', this.field.setting)
      this.$set(this.field, 'options', this.field.options)
      this.$nextTick(() => {
        this.checkDefaultValue()
      })
    },

    /**
     * 删除
     * @param index
     */
    handleDelete(index) {
      let delItem = null

      if (index !== -1) {
        delItem = this.field.setting[index]
        this.optionsList.splice(index, 1)
        this.field.setting.splice(index, 1)
      } else {
        const findIndex = this.field.setting.lastIndexOf(LOGIC_OTHER)
        if (findIndex !== -1) {
          this.field.setting.splice(findIndex, 1)
          delItem = LOGIC_OTHER
        }
      }

      if (this.field.remark === 'options_type') {
        // 如果是逻辑表单
        delete this.field.optionsData[delItem]
        this.$set(this.field, 'options', JSON.stringify(this.field.optionsData))
      } else {
        // 如果不是逻辑表单
        this.$set(this.field, 'options', this.field.setting.join(','))
      }

      this.$set(this.field, 'setting', this.field.setting)
      this.checkDefaultValue()
    },
    /**
     * 添加
     */
    handleAdd() {
      const val = this.getAddValue(this.optionsList.length + 1)
      this.optionsList.push({
        value: val
      })
      this.handleChange()
    },

    /**
     * 添加其他
     */
    handleAddOther() {
      if (this.field.setting.indexOf(LOGIC_OTHER) === -1) {
        this.field.setting.push(LOGIC_OTHER)
      }
      this.$set(this.field, 'setting', this.field.setting)
      if (this.field.remark === 'options_type') {
        // 如果是逻辑表单
        this.field.optionsData[LOGIC_OTHER] = []
        this.$set(this.field, 'optionsData', this.field.optionsData)
        this.$set(this.field, 'options', JSON.stringify(this.field.optionsData))
      } else {
        // 如果不是逻辑表单
        this.$set(this.field, 'options', this.field.setting.join(','))
      }
    },

    /**
     * 点击批量编辑
     */
    handleUpdateAll() {
      this.dialogContentVal = this.optionsList.map(o => o.value).join('\n')
      this.dialogVisible = true
    },

    /**
     * 关闭弹窗
     */
    handleCloseDialog() {
      this.dialogVisible = false
    },

    /**
     * 批量编辑
     */
    handleDialogConfirm() {
      let arr = this.dialogContentVal.split(/\n|\r/)
      arr = Array.from(new Set(arr))
        .map(o => o.trim())
        .filter(o => !isEmpty(o) && o !== LOGIC_OTHER)
      this.optionsList = arr.map(o => {
        return { value: o }
      })
      this.$set(this.field, 'remark', null)
      this.$set(this.field, 'optionsData', null)
      this.handleChange()
      this.handleCloseDialog()
    },

    getAddValue(index) {
      const findRes = this.optionsList.find(o => o.value === `选${index}`)
      if (findRes) {
        return this.getAddValue(index + 1)
      }
      return `选${index}`
    },

    /**
     * 选项变化后修改默认值
     */
    checkDefaultValue() {
      if (!isEmpty(this.field.defaultValue)) {
        if (isArray(this.field.defaultValue)) {
          const arr = []
          this.field.defaultValue.forEach(o => {
            const findRes = this.optionsList.find(item => item.value === o)
            if (findRes) arr.push(o)
          })
          this.$set(this.field, 'defaultValue', [...arr])
        } else {
          const findRes = this.optionsList.find(item => item.value === this.field.defaultValue)
          if (!findRes) {
            this.$set(this.field, 'defaultValue', null)
          }
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.option-item {
  margin: 5px 0;

  .el-input__icon {
    font-size: 14px;
    color: $--color-text-secondary;
  }

  .drag-hook {
    cursor: move;
  }

  &.other-item {
    .wk-icon-bin {
      cursor: pointer;
    }
  }
}

.add-btn {
  width: 100%;
  height: 34px;
  font-size: 14px;
  color: $--color-text-regular;
  cursor: pointer;
  background-color: #f8f8f8;
  border: 1px dashed $--border-color-base;
  border-radius: $--border-radius-base;
}

.add-other-btn {
  display: inline-block;
  margin-top: 8px;
  font-size: 14px;
  color: $--color-text-regular;
  cursor: pointer;
}

.edit-dialog {
  .edit-tips {
    padding: 5px 20px;
    font-size: 14px;

    // color: $--color-text-secondary;
    color: #ecb971;
    background-color: #fafbfb;
    border-top: 1px solid $--border-color-base;
    border-bottom: 1px solid $--border-color-base;
  }

  .el-textarea {
    margin: 10px 0;
  }

  ::v-deep .el-textarea__inner {
    padding: 10px 20px;
    border: 0 none;
  }

  ::v-deep .el-dialog__body {
    padding: 0;
  }
}

</style>
