<template>
  <el-drawer
    :visible="visible"
    :with-header="false"
    size="500px"
    title="我是标题"
    append-to-body
    @close="close">
    <flexbox v-if="editNode" class="drawer-header">
      <div class="edit-title">
        <el-input
          v-if="isNameEdit"
          ref="conditionNameInput"
          v-model="editNode.conditionName"
          maxlength="20"
          @blur="nameInputBlur" />
        <template v-else>
          <span class="title" @click="titleEditClick">{{ editNode.conditionName }}</span>
          <i class="wk wk-edit" @click="titleEditClick" />
        </template>
      </div>
      <el-popover
        placement="bottom"
        width="200"
        trigger="click">
        <draggable
          v-model="dragList"
          :options="{ group: 'list',forceFallback:false, fallbackClass:'draggingStyle',filter: 'drag-item__label', preventOnFilter: false }">
          <flexbox
            v-for="(item, index) in dragList"
            :key="index"
            class="drag-item">
            <div class="drag-item__label">{{ item.conditionName }}</div>
            <div class="drag-item__handle">⋮⋮</div>
          </flexbox>
        </draggable>
        <el-button slot="reference" type="text">优先级设置</el-button>
      </el-popover>

      <i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="26"
        data-id="236" />
      <i
        class="el-icon-close "
        @click="close" />
    </flexbox>
    <div v-if="editNode" class="drawer-body">
      <div
        v-for="(children, cIndex) in editNode.conditionDataList"
        :key="cIndex">
        <div
          class="section">
          <div class="section-header">
            <span class="sh-title">条件组</span>
            <el-button
              v-if="editNode.conditionDataList.length > 1"
              type="primary-text"
              @click="deleteSection(cIndex)">删除</el-button>
          </div>
          <el-row
            v-for="(item, index) in children"
            :key="index"
            :gutter="20"
            type="flex"
            align="middle"
            class="set-row">
            <el-col :span="5" class="set-row__title">{{ item.name }}</el-col>

            <el-col :span="17" class="set-row__center">
              <flexbox
                v-if="item.conditionType === 8">
                <div style="flex: 1;">
                  <wk-user-dep-dialog-select
                    :user-value.sync="item.values.userList"
                    :dep-value.sync="item.values.deptList"
                    placeholder="请选择员工或部门"
                    style="width: 100%;"
                    @change="userDepSelectChange(arguments, item)"
                  />
                  <role-employee-select
                    ref="roleSelect"
                    v-model="item.values.roleList"
                    :props="{
                      onlyShowRole: true
                    }"
                    multiple
                    placeholder="请选择角色"
                    collapse-tags
                    style="width: 100%;margin-top: 8px;"
                    clearable
                    @change="roleSelectChange(item, cIndex)" />
                </div>
                <div style="flex-shrink: 0;margin-left: 8px;">或</div>
              </flexbox>
              <template
                v-else-if="[9, 3].includes(item.type)">
                <el-select
                  v-if="item.type == 3"
                  v-model="item.conditionType"
                  class="condition-select">
                  <el-option
                    v-for="option in selectOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value" />
                </el-select>
                <el-select
                  v-else-if="item.type == 9"
                  v-model="item.conditionType"
                  class="condition-select">
                  <el-option
                    v-for="option in checkboxOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value" />
                </el-select>

                <el-select
                  v-model="item.values"
                  multiple
                  class="condition-value">
                  <el-option
                    v-for="(option, optIndex) in item.setting"
                    :key="optIndex"
                    :label="option.value !== undefined ? option.label : option"
                    :value="option.value !== undefined ? option.value : option" />
                </el-select>
              </template>

              <template v-else>
                <el-select
                  v-model="item.conditionType"
                  :class="['condition-select', {
                    'is-block': item.conditionType === 6
                  }]">
                  <el-option
                    v-for="option in numberOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value" />
                </el-select>
                <template v-if="item.conditionType === 6">
                  <div style="margin-top: 10px;">
                    <el-input-number
                      v-model="item.leftValue"
                      :controls="false"
                      class="small" />
                    <el-select
                      v-model="item.leftCondition"
                      class="small is-condition">
                      <el-option
                        v-for="option in numberValueOptions"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value" />
                    </el-select>
                    <span class="small-select-label">{{ item.name }}</span>
                    <el-select
                      v-model="item.rightCondition"
                      class="small is-condition">
                      <el-option
                        v-for="option in numberValueOptions"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value" />
                    </el-select>
                    <el-input-number
                      v-model="item.rightValue"
                      :controls="false"
                      class="small" />
                  </div>
                </template>
                <el-input-number
                  v-else
                  v-model="item.values"
                  :controls="false"
                  class="condition-value" />
              </template>

            </el-col>
            <el-col :span="1" class="set-row__footer"><i class="wk wk-delete" @click="deleteItem(cIndex, index)" /></el-col>
          </el-row>

          <div>
            <el-button type="text" icon="el-icon-plus" @click="conditionSelectClick(cIndex)">添加条件</el-button>
            <i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="26"
              data-id="237" />
          </div>
        </div>
        <div
          v-if="editNode.conditionDataList.length - 1 > cIndex"
          class="section-and">或</div>
        <div
          v-else
          class="section-footer">
          <el-button
            v-if="editNode.conditionDataList.length < 4"
            icon="el-icon-plus"
            type="primary"
            @click="addSection()">添加条件组</el-button>
        </div>
      </div>
    </div>
    <div class="drawer-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="close">取消</el-button>
    </div>
    <wk-condition-select
      :props="props"
      :checks="editNode && addSectionIndex !== null ? editNode.conditionDataList[addSectionIndex] : []"
      :visible.sync="conditionSelectVisible"
      @confirm="conditionSelectConfirm"
    />
  </el-drawer>
</template>

<script>
import WkConditionSelect from './WkConditionSelect'
import Draggable from 'vuedraggable'
import WkUserDepDialogSelect from '@/components/NewCom/WkUserDepDialogSelect'
import RoleEmployeeSelect from '@/views/admin/employeeDep/components/RoleEmployeeSelect'

import { objDeepCopy } from '@/utils'
import {
  selectOptions,
  checkboxOptions,
  numberOptions,
  numberValueOptions,
  getSendObj
} from './conditioModel'
import { isEmpty } from '@/utils/types'

export default {
  // 条件配置
  name: 'WkConditionSet',

  components: {
    WkConditionSelect,
    Draggable,
    WkUserDepDialogSelect,
    RoleEmployeeSelect
  },

  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    node: {
      type: Object,
      default() {
        return {}
      }
    },
    conditionParent: Array,
    conditionParentIndex: Number,
    props: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },

  data() {
    return {
      editNode: null,
      isNameEdit: false,
      // select 3 checkbox 9 number 5 floatnumber 6
      selectOptions: selectOptions,
      checkboxOptions: checkboxOptions,
      numberOptions: numberOptions,
      numberValueOptions: numberValueOptions,
      addSectionIndex: null, // 当前需要添加的条件的索引
      conditionSelectVisible: false,
      dragList: []
    }
  },

  computed: {},

  watch: {
    visible(val) {
      if (val) {
        const dragList = objDeepCopy(this.conditionParent)
        this.editNode = dragList[this.conditionParentIndex]
        if (this.editNode.conditionDataList.length === 0) {
          this.editNode.conditionDataList.push([getSendObj(true)])
        } else {
          this.validateSetting(this.editNode.conditionDataList)
        }
        dragList.forEach((item, index) => {
          item.index = index
        })
        this.dragList = dragList
      } else {
        const dragList = objDeepCopy(this.conditionParent)
        this.conditionParent.splice(0, this.conditionParent.length)
        this.dragList.forEach(item => {
          this.conditionParent.push(dragList[item.index])
        })
        this.editNode = null
      }
    }
  },

  created() {

  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 验证数据
     */
    validateSetting(list) {
      const noSetting = []
      list.forEach(item => {
        if ((item.type === 3 || item.type === 9) && !item.setting) {
          noSetting.push(item)
        }
      })
      if (noSetting && noSetting.length > 0) {
        const { conditionSelectRequest, conditionSelectParams, conditionSelectList } = this.props
        if (conditionSelectList) {
          const list = objDeepCopy(conditionSelectList)
          noSetting.forEach(item => {
            const field = list.find(fieldItem => fieldItem.fieldName === item.fieldName)
            if (field) {
              this.$set(item, 'setting', field.setting)
            }
          })
        } else {
          conditionSelectRequest(conditionSelectParams).then(res => {
            const list = res.data || []
            noSetting.forEach(item => {
              const field = list.find(fieldItem => fieldItem.fieldName === item.fieldName)
              if (field) {
                this.$set(item, 'setting', field.setting)
              }
            })
          }).catch(() => {
          })
        }
      }
    },

    /**
     * 输入失去焦点
     */
    nameInputBlur() {
      this.isNameEdit = false
      if (this.editNode.conditionName === '') {
        this.editNode.conditionName = '条件'
      }
    },

    /**
     * 编辑名称
     */
    titleEditClick() {
      this.isNameEdit = true
      this.$nextTick(() => {
        this.$refs.conditionNameInput.focus()
      })
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 保存
     */
    handleConfirm() {
      // 处理掉不符合的数据
      const conditionDataList = this.editNode.conditionDataList || []
      const validList = []
      conditionDataList.forEach(children => {
        const list = this.getValidCondition(children)
        if (list.length > 0) {
          validList.push(list)
        }
      })
      this.editNode.conditionDataList = validList.length > 4 ? validList.slice(0, 4) : validList

      // 条件为0 报红
      // this.editNode.isError = this.editNode.conditionDataList.length === 0
      this.editNode.isError = false

      for (const key in this.editNode) {
        if (key !== 'examineDataList') {
          this.node[key] = this.editNode[key]
        }
      }

      this.close()
    },

    /**
     * @description: 获取有效条件
     * @return {*}
     */
    getValidCondition(conditionDataList) {
      return conditionDataList.filter(item => {
        if (item.type === 3 || item.type === 9) {
          return item.values && item.values.length > 0
        } else {
          if (item.conditionType === 6) {
            return !isEmpty(item.leftValue) && !isEmpty(item.rightValue) && item.leftValue < item.rightValue
          } else if (item.conditionType === 8) { // 人员部门角色
            return !isEmpty(item.userList) || !isEmpty(item.deptList) || !isEmpty(item.roleList)
          } else {
            return !isEmpty(item.values)
          }
        }
      })
    },

    /**
     * 条件选择
     */
    conditionSelectClick(cIndex) {
      this.addSectionIndex = cIndex
      this.conditionSelectVisible = true
    },

    /**
     * 删除条件
     */
    deleteItem(cIndex, index) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.editNode.conditionDataList[cIndex].splice(index, 1)
        })
        .catch(() => {})
    },

    /**
      * @description: 删除块
      * @param {*} cIndex 索引
      * @return {*}
      */
    deleteSection(cIndex) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.editNode.conditionDataList.splice(cIndex, 1)
        })
        .catch(() => {})
    },

    /**
     * @description: 增加一个新的块
     * @return {*}
     */
    addSection() {
      if (this.editNode.conditionDataList.length < 5) {
        this.editNode.conditionDataList.push([getSendObj(true)])
      }
    },

    /**
     * 条件选择确定
     */
    conditionSelectConfirm(list) {
      list.forEach(item => {
        if (!item.conditionType) {
          // select 3 checkbox 9 number 5 floatnumber 6
          if (item.type === 3 || item.type === 9) {
            item.conditionType = item.type === 3 ? 7 : 11
            item.values = []
          } else if (item.type === 5 || item.type === 6) { // 8 不用校准
            item.conditionType = 3
            item.values = 0
            item.leftValue = 0
            item.leftCondition = 1
            item.rightCondition = 1
            item.rightValue = 0
          }
        }
      })

      this.$set(this.editNode.conditionDataList, this.addSectionIndex, objDeepCopy(list))
      // this.editNode.conditionDataList[this.addSectionIndex] = objDeepCopy(list)
    },

    /**
     * 用户部门选择
     */
    userDepSelectChange(list, item) {
      const userList = list[2]
      const deptList = list[3]
      item.userList = userList
      item.deptList = deptList
    },

    /**
     * 角色选择
     */
    roleSelectChange(item, cIndex) {
      this.$nextTick(() => {
        const selecteds = this.$refs.roleSelect[cIndex].select.selected
        item.roleList = selecteds.map(item => {
          return {
            roleName: item.$props.label
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.drawer-header {
  height: 50px;
  padding: 0 15px;
  background: #f7f8fa;
  border-bottom: 1px solid $--border-color-base;

  .edit-title {
    width: 360px;
    margin-right: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    .title {
      cursor: pointer;
    }

    .wk-edit {
      cursor: pointer;
    }
  }

  .wk-help-tips {
    margin: 0 5px;
  }

  .el-icon-close {
    font-size: 24px;
    color: #909399;
    cursor: pointer;
  }

  .el-icon-close:hover {
    color: $--color-primary;
  }
}

.drag-item {
  padding: 5px 0;

  &__label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__handle {
    flex-shrink: 0;
    padding: 0 8px;
    color: $--color-text-primary;
    cursor: move;
  }
}

.drawer-body {
  height: calc(100% - 115px);
  padding: 20px 10px;
  overflow-y: auto;
}

.drawer-footer {
  padding: 15px;
  text-align: right;
}

.section {
  padding: 16px;
  background-color: $--color-n10;

  &-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .sh-title {
      font-size: 18px;
    }

    .sh-ft {}
  }

  &-and {
    margin: 8px 0;
    color: $--color-text-regular;
  }

  &-footer {
    margin-top: 16px;
  }

  & + & {
    margin-top: 16px;
  }
}

.set-row {
  margin-bottom: 10px;

  &__title {
    display: flex;
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  &__center {
    .condition-select {
      width: 100px;

      &.is-block {
        width: 100%;
      }
    }

    .small {
      width: 20%;

      &.is-condition {
        width: 15%;
      }

      & + & {
        margin-left: 10px;
      }
    }

    .condition-value {
      width: calc(100% - 120px);
      margin-left: 15px;
    }

    .small-select-label {
      width: 20%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &__footer {
    line-height: 34px;

    .wk-delete {
      cursor: pointer;

      &:hover {
        color: $--color-primary;
      }
    }
  }
}
</style>
