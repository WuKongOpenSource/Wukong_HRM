<template>
  <div class="setting-serial-number">
    <el-button
      :disabled="!editAuth"
      class="add-btn"
      @click="handleUpdateDialogVisible">
      点击配置
    </el-button>

    <div class="rule">
      <div class="rule-item">
        编号示例：{{ serialNumberExample }}
      </div>
      <div
        v-for="(item,index) in field.setting"
        :key="index"
        class="rule-item">
        <template v-if="item.type === 1">
          自动计数：起始编号{{ item.startNumber }} 递增数{{ item.stepNumber }} {{ getResetTypeStr(item) }}
        </template>
        <template v-else-if="item.type === 2">固定字符：{{ item.value }}</template>
        <template v-else-if="item.type === 3">表单内字段：{{ item.value }}</template>
      </div>
    </div>

    <el-dialog
      :visible.sync="dialogVisible"
      :before-close="handleUpdateDialogVisible"
      :close-on-click-modal="false"
      append-to-body
      width="780px"
      class="edit-dialog">
      <span slot="title" class="el-dialog__title">设置编号规则<i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="27"
        data-id="252" /></span>
      <div class="edit-dialog-body">
        <div class="edit-tips">
          <div>
            提示：编号规则中如果表单内字段选择了日期类型字段，若此日期字段值为空时，默认取创建时间的值
          </div>
          <div v-if="serialNumberEditExample">
            编号示例：{{ serialNumberEditExample }}
          </div>
        </div>
        <draggable
          :list="ruleList"
          :options="dragConfig"
          class="edit-form">
          <flexbox
            v-for="(item,index) in ruleList"
            :key="index"
            class="edit-form-item">
            <flexbox
              v-if="item.type === 1"
              class="left">
              <div class="el-input__icon drag-hook wk wk-grid" />
              <el-input
                value="自动计数"
                disabled />
              <el-input
                v-model="item.startNumber"
                v-wk-number="'positiveNum'"
                :maxlength="12"
                placeholder="起始编号" />
              <el-input-number
                v-model="item.stepNumber"
                :maxlength="4"
                :controls="false"
                placeholder="递增数" />
              <el-select
                v-model="item.resetType"
                >
                <el-option
                  v-for="subItem in resetOptions"
                  :key="subItem.value"
                  :label="subItem.label"
                  :value="subItem.value" />
              </el-select>
            </flexbox>
            <flexbox
              v-else
              class="left">
              <div class="el-input__icon drag-hook wk wk-grid" />
              <el-select
                v-model="item.type"

                @change="typeChange(item)">
                <el-option
                  v-for="subItem in typeOptions"
                  :key="subItem.value"
                  :label="subItem.label"
                  :value="subItem.value" />
              </el-select>
              <el-input
                v-if="item.type === 2"
                v-model.trim="item.value"
                class="el-input" />
              <template v-if="item.type === 3">
                <el-select
                  v-model="item.formAssistId"

                  @change="fieldChange(item)">
                  <el-option
                    v-for="(subItem,subIndex) in fieldLibArr"
                    :key="subIndex"
                    :label="subItem.name"
                    :value="subItem.formAssistId" />
                </el-select>
                <el-select
                  v-if="isTimeField(item)"
                  v-model="item.textFormat"
                  >
                  <el-option
                    v-for="subItem in timeFormatOptions"
                    :key="subItem.value"
                    :label="subItem.label"
                    :value="subItem.value" />
                </el-select>
              </template>
            </flexbox>
            <el-button
              v-if="item.type !== 1"
              type="text"
              class="el-input__icon wk wk-icon-bin"
              @click="handleDelete(index)" />
          </flexbox>
        </draggable>
        <el-button
          class="add"
          type="text"
          @click.native="handleAdd">
          <i class="el-icon-plus" />
          新增编号规则
        </el-button>
      </div>
      <div slot="footer">
        <el-button @click="handleUpdateDialogVisible">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleDialogConfirm">
          确定
        </el-button>
      </div>
    </el-dialog>

    <el-checkbox
      v-model="allowedInput"
      class="magin-top"
      :true-label="1"
      :false-label="0"
      :disabled="!editAuth"
      @change="allowedInputChange">允许手动填写编号</el-checkbox><i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="27"
        data-id="344" />
  </div>
</template>

<script>
import draggable from 'vuedraggable'

import { getFieldAuth } from '../../utils'
import { objDeepCopy } from '@/utils'

export default {
  name: 'SettingSerialNumber',
  components: {
    draggable
  },
  props: {
    field: {
      type: Object,
      required: true
    },
    fieldArr: { // 所有字段
      type: Array,
      required: true
    },
    point: { // 字段坐标
      type: Array,
      required: true
    }
  },
  data() {
    return {
      typeOptions: [
        { label: '固定字符', value: 2 },
        { label: '表单内字段', value: 3 }
      ],
      timeFormatOptions: [
        { label: 'yyyyMMdd（年月日）', value: 'yyyyMMdd' },
        { label: 'yyyyMM（年月）', value: 'yyyyMM' },
        { label: 'yyyy（年）', value: 'yyyy' }
      ],

      dialogVisible: false,
      ruleList: [],
      allowedInput: 0 // 1 允许 0 不允许
    }
  },
  computed: {
    // 选项不能配置，逻辑表单也禁止配置
    editAuth() {
      return getFieldAuth(this.field.operating).optionsEdit
    },
    dragConfig() {
      return {
        group: 'serial-number-drag',
        forceFallback: false,
        disabled: !this.editAuth,
        fallbackClass: 'draggingStyle',
        handle: '.drag-hook',
        filter: '.el-input__inner',
        preventOnFilter: false
      }
    },

    // 字段库
    fieldLibArr() {
      const arr = []
      const typesArr = ['date', 'datetime', 'text', 'select', 'number']
      this.fieldArr.forEach((father, fatherIndex) => {
        father.forEach((child, childIndex) => {
          if (
            typesArr.includes(child.formType) &&
            (fatherIndex !== this.point[0] || childIndex !== this.point[1])
          ) {
            arr.push(child)
          }
        })
      })

      const findRes = arr.find(o => o.fieldName === 'createTime')
      if (!findRes) {
        arr.push({
          name: '创建时间',
          fieldName: 'createTime',
          formType: 'datetime',
          formAssistId: -100
        })
      }
      return arr
    },

    resetOptions() {
      const arr = [
        { label: '每天开始重新编号', value: 1 },
        { label: '每月开始重新编号', value: 2 },
        { label: '每年开始重新编号', value: 3 },
        { label: '从不开始重新编号', value: 4 }
      ]
      const map = {
        'yyyyMMdd': 0,
        'yyyyMM': 1,
        'yyyy': 2
      }
      let startIndex = null
      this.ruleList.forEach(ruleItem => {
        if (
          ruleItem.type === 3 &&
          ruleItem.textFormat &&
          this.isTimeField(ruleItem)
        ) {
          const num = map[ruleItem.textFormat]
          if (num >= (startIndex || 0)) {
            startIndex = num
          }
        }
      })

      if (startIndex === null) {
        startIndex = 3
      }
      return arr.slice(startIndex)
    },

    // 编号示例
    serialNumberExample() {
      return this.getNumberExample(this.field.setting)
    },

    // 实时编号示例
    serialNumberEditExample() {
      return this.getNumberExample(this.ruleList)
    }
  },
  watch: {
    field: {
      handler() {
        const list = objDeepCopy(this.field.setting)
        list.forEach(item => {
          if (!item.hasOwnProperty('textFormat')) {
            item.textFormat = null
          }

          // 由于未点击保存之前字段的name没有验重，可能出现重复
          // 故表单内字段通过 formAssistId 来确定唯一
          if (item.type === 3) {
            const findRes = this.fieldLibArr.find(o => o.name === item.value)
            if (findRes) {
              item.formAssistId = findRes.formAssistId
            }
          } else if (item.type === 1) {
            // 允许手动填写编号 的信息，存入 自动计数对象里
            this.allowedInput = !item.allowedInput ? 0 : 1
          }
        })

        this.ruleList = list
      },
      deep: true,
      immediate: true
    },

    resetOptions: {
      handler() {
        const findRes = this.ruleList.find(o => o.type === 1)
        if (findRes) {
          const findOption = this.resetOptions.find(o => o.value === findRes.resetType)
          if (!findOption) {
            this.$set(findRes, 'resetType', this.resetOptions[0].value)
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * 获取重置规则展示名
     */
    getResetTypeStr(item) {
      return {
        1: '每天开始重新编号',
        2: '每月开始重新编号',
        3: '每年开始重新编号',
        4: '从不开始重新编号'
      }[item.resetType]
    },

    /**
     * 获取编号示例展示值
     */
    getNumberExample(ruleList) {
      const arr = []
      ruleList.forEach(item => {
        switch (item.type) {
          case 1:
            arr.push(item.startNumber)
            break
          case 2:
            arr.push(item.value)
            break
          case 3:
            arr.push(item.value)
            break
        }
      })
      return arr.join('-')
    },

    handleUpdateDialogVisible() {
      this.dialogVisible = !this.dialogVisible
    },

    /**
     * 判断规则是否为时间类型
     * @param ruleItem
     * @return {boolean}
     */
    isTimeField(ruleItem) {
      const findRes = this.fieldLibArr.find(o => {
        return o.formAssistId === ruleItem.value ||
          o.name === ruleItem.value
      })
      return findRes && ['date', 'datetime'].includes(findRes.formType)
    },

    /**
     * 修改规则类型
     * @param ruleItem
     */
    typeChange(ruleItem) {
      this.$set(ruleItem, 'value', null)
      this.$set(ruleItem, 'formAssistId', null)
      if (ruleItem.type === 3) {
        this.$set(ruleItem, 'textFormat', '')
      }
    },

    /**
     * 修改规则字段
     * @param ruleItem
     */
    fieldChange(ruleItem) {
      this.$nextTick(() => {
        const findRes = this.fieldLibArr.find(o => o.formAssistId === ruleItem.formAssistId) || {}
        // 内联字段值，新增字段没有id，传值name，让后端判断
        this.$set(ruleItem, 'value', findRes.name)
        if ([
          'date',
          'datetime'
        ].includes(findRes.formType)) {
          this.$set(ruleItem, 'textFormat', 'yyyyMMdd')
        } else {
          this.$set(ruleItem, 'textFormat', '')
        }
      })
    },

    /**
     * 添加规则
     */
    handleAdd() {
      this.ruleList.push({
        type: 2,
        value: null,
        textFormat: null,
        formAssistId: null
      })
    },

    /**
     * 删除
     */
    handleDelete(index) {
      this.ruleList.splice(index, 1)
    },

    /**
     * @description: 允许手动填写change
     * @param {*}
     * @return {*}
     */
    allowedInputChange() {
      const item = this.field.setting.find(item => item.type === 1)
      if (item) {
        this.$set(item, 'allowedInput', this.allowedInput)
        this.$set(this.field, 'options', JSON.stringify(this.field.setting))
      }
    },

    /**
     * 保存
     */
    handleDialogConfirm() {
      const arr = []
      for (let i = 0; i < this.ruleList.length; i++) {
        const item = this.ruleList[i]
        if (item.type === 1) {
          if (!item.startNumber && item.startNumber !== 0) {
            this.$message.error('起始编号不能为空')
            return
          }
          if (!item.stepNumber) {
            this.$message.error('递增数不能为空或等于0')
            return
          }
        } else if (item.type === 2 && !item.value) {
          this.$message.error('固定字符不能为空')
          return
        } else if (item.type === 3 && !item.value) {
          this.$message.error('请选择规则字段')
          return
        }
        arr.push({ ...item })
      }

      // 保存规则时删除多余字段
      arr.forEach(item => {
        if (item.type !== 3) {
          delete item.textFormat
        }
        delete item.formAssistId
      })
      // console.log('kkkk', arr)

      this.$set(this.field, 'setting', arr)
      this.$set(this.field, 'options', JSON.stringify(arr))
      this.handleUpdateDialogVisible()
    }
  }
}
</script>

<style scoped lang="scss">
.el-input,
.el-input-number,
.el-select {
  width: 150px;
  margin: 0 5px;
  text-align: left;
}

::v-deep .el-input-number {
  .el-input__inner {
    padding: 0 6px !important;
    text-align: left;
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

.rule {
  color: $--color-text-regular;

  .rule-item {
    margin-top: 10px;
  }
}

.edit-dialog {
  .edit-dialog-body {
    padding: 0 15px;
  }

  .edit-tips {
    padding: 5px 20px;
    font-size: 14px;
    color: $--color-text-secondary;
    background-color: #fafbfb;
    border-top: 1px solid $--border-color-base;
    border-bottom: 1px solid $--border-color-base;

    & > div:nth-child(1) {
      margin-bottom: 8px;
    }
  }

  ::v-deep .el-dialog__body {
    padding: 0;
  }

  .edit-form {
    height: 300px;
    padding: 15px;
    overflow-y: auto;

    .edit-form-item {
      margin-bottom: 10px;

      .left {
        flex: 1;

        div:nth-child(4) {
          width: 180px;
        }
      }

      .drag-hook {
        color: $--color-text-secondary;
        cursor: move;
      }
    }
  }
}

.type-box {
  width: 130px;
}

.magin-top {
  margin-top: 8px;
}
</style>
