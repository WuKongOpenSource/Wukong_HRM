<template>
  <div class="setting-logic-form">
    <el-button
      :disabled="!editAuth"
      class="add-btn"
      @click="handleToSet">
      {{ hasConfig ? '已配置' : '点击配置' }}
    </el-button>

    <el-dialog
      :visible.sync="dialogVisible"
      :before-close="handleCloseDialog"
      :close-on-click-modal="false"
      append-to-body
      title="添加逻辑表单规则"
      width="500px"
      class="edit-dialog">
      <div>
        <div class="edit-tips">
          选择选项后，才会显示所设置的其他字段
        </div>

        <div class="edit-table">
          <flexbox
            align="center"
            justify="flex-start"
            class="edit-table__header row">
            <div class="label">选项内容</div>
            <flexbox-item class="content">显示字段</flexbox-item>
          </flexbox>
          <div
            v-if="list.length > 0 && fieldLibArr.length > 0"
            class="edit-table__body">
            <flexbox
              v-for="(item, index) in list"
              :key="index"
              align="center"
              justify="flex-start"
              class="row">
              <div class="label">{{ item.name }}</div>
              <flexbox-item class="content">
                <el-select
                  v-model="item.value"

                  multiple>
                  <el-option
                    v-for="(childItem, childIndex) in fieldLibArr"
                    :key="childIndex"
                    :label="childItem.formType === 'desc_text' ? '描述文字' : (childItem.name || '未命名')"
                    :value="childItem.formAssistId" />
                </el-select>
              </flexbox-item>
            </flexbox>
          </div>
        </div>
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
// import { objDeepCopy } from '@/utils/index'
import { isEmpty } from '@/utils/types'
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingLogicForm',
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
      list: [],
      dialogVisible: false
    }
  },
  computed: {
    // 编辑权限
    editAuth() {
      return getFieldAuth(this.field.operating).optionsEdit
    },
    fieldLibArr() {
      const arr = []
      const ids = [] // 记录所有formAssistId防止重复
      this.fieldArr.forEach(father => {
        father.forEach(child => {
          if (child.hasOwnProperty('formAssistId') && !isEmpty(child.formAssistId)) {
            ids.push(child.formAssistId)
          }
        })
      })
      this.fieldArr.forEach((father, fatherIndex) => {
        father.forEach((child, childIndex) => {
          if (
            fatherIndex !== this.point[0] ||
            childIndex !== this.point[1]) {
            arr.push(child)
          }
        })
      })
      return arr.filter(item => ![
        'customer',
        'business',
        'contract'
      ].includes(item.formType))
    },
    allFormAssistId() {
      return this.fieldLibArr.map(o => o.formAssistId)
    },
    hasConfig() {
      return this.list.some(item => item.value && item.value.length > 0)
    }
  },
  watch: {
    field: {
      handler() {
        // remark null 普通  options_type 逻辑表单
        if (this.field.remark !== 'options_type') {
          // 普通单选多选
          this.list = this.field.setting.map(o => {
            return {
              name: o,
              value: null
            }
          })
        } else {
          // 逻辑表单单选多选
          let data = {}
          if (this.field.optionsData) {
            data = this.field.optionsData || {}
          } else {
            try {
              data = JSON.parse(this.field.options) || {}
            } catch (e) {
              this.list = this.field.setting.map(o => {
                return {
                  name: o,
                  value: null
                }
              })
              // 如果异常
              this.$set(this.field, 'remark', null)
              this.$set(this.field, 'optionsData', null)
              this.$set(this.field, 'options', this.field.setting.join(','))
              return
            }
          }
          this.list = Object.keys(data).map(key => {
            return {
              name: key,
              value: isEmpty(data[key]) ? [] : data[key]
            }
          })
        }
      },
      deep: true,
      immediate: true
    },
    allFormAssistId: {
      handler() {
        if (this.field.remark === 'options_type') {
          // 某个字段被删除后，则同时删除其对应的逻辑关联
          this.list.forEach(item => {
            const ids = []
            if (item.value) {
              item.value.forEach(id => {
                if (this.allFormAssistId.includes(id)) {
                  ids.push(id)
                }
              })
            }
            item.value = ids
          })

          const optionsData = {}
          this.list.forEach(o => {
            optionsData[o.name] = o.value
          })
          this.$set(this.field, 'optionsData', optionsData)
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    handleToSet() {
      this.dialogVisible = true
    },

    handleCloseDialog() {
      // 关闭弹窗，触发watch更新
      this.$set(this.field, '_remark', '')
      this.$nextTick(() => {
        delete this.field._remark
      })
      this.dialogVisible = false
    },

    handleDialogConfirm() {
      const optionsData = {}
      this.list.forEach(o => {
        optionsData[o.name] = o.value
      })
      const len = this.list.filter(o => !isEmpty(o.value)).length
      if (len !== 0) {
        // 保存逻辑表单关系
        this.$set(this.field, 'remark', 'options_type')
        this.$set(this.field, 'optionsData', optionsData)
        const optionsStr = JSON.stringify(optionsData)
        this.$set(this.field, 'options', optionsStr)
      } else {
        // 如果没有配置逻辑表单则置普通类型
        this.$set(this.field, 'remark', null)
        this.$set(this.field, 'optionsData', null)
        this.$set(this.field, 'options', this.field.setting.join(','))
      }

      this.handleCloseDialog()
    }
  }
}
</script>

<style scoped lang="scss">
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

.edit-dialog {
  .edit-tips {
    padding: 5px 20px;
    font-size: 14px;
    color: $--color-text-secondary;
    background-color: #fafbfb;
    border-top: 1px solid $--border-color-base;
    border-bottom: 1px solid $--border-color-base;
  }

  ::v-deep .el-dialog__body {
    padding: 0;
  }

  .edit-table {
    height: 300px;
    padding: 0 15px;
    overflow-y: auto;

    .row {
      border-bottom: 1px solid $--border-color-base;

      &:last-child {
        border-bottom: 0 none;
      }

      .label {
        width: 180px;
        padding: 0 15px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .content {
        padding-right: 15px;

        .el-select {
          width: 100%;
        }
      }
    }

    .edit-table__header {
      width: 100%;
      height: 36px;
      color: $--color-text-secondary;
    }

    .edit-table__body {
      .row {
        padding: 6px 0;
      }
    }
  }
}
</style>
