<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <transition name="opacity-fade">
    <div
      v-loading="loading"
      :style="{ 'z-index': zIndex }"
      class="eep">
      <el-card v-if="form" class="eep-con">
        <div class="eep-header">
          {{ title }}
          <div class="eep-header__ft">
            <el-button type="primary" @click="submitClick(false)">提交</el-button>
            <el-button v-if="saveDraftShow" type="primary" @click="submitClick(true)">保存草稿</el-button>
            <el-button @click="closeClick">关闭</el-button>
          </div>
        </div>
        <div class="eep-body">
          <el-form
            ref="form"
            :model="form"
            :validate-on-rule-change="false"
            class="eep-left"
            hide-required-asterisk>
            <reminder
              :content="form.description"
              class="xr-reminder" />

            <div
              v-for="(fixItem, index) in form.fixedSegList"
              :key="index"
              class="content-section">
              <div class="section__handle">
                <el-button
                  v-if="editScheduleShow"
                  :disabled="!canEditSchedule"
                  type="text"
                  icon="wk wk-edit"
                  @click="itemHandleClick('schedule', 'fixed', fixItem, index, form.fixedSegList)">
                  {{ (fixItem.schedule === null || fixItem.schedule === '') && canEditSchedule ? '添加进度' : `进度${fixItem.schedule || 0}%` }}</el-button>
                <el-button type="text" disabled>
                  {{ `权重${fixItem.weight || 0}%` }}
                </el-button>
              </div>
              <div
                class="item-section">
                <div class="el-row">
                  {{ fixItem.segName }}
                </div>
                <div>
                  <div
                    v-for="(item, itemIndex) in fixItem.items"
                    :key="itemIndex"
                    class="el-row"
                    style="padding-left: 140px;">
                    <div class="sub-input-value">{{ item.itemName }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div
              v-for="(noFixItem, index) in form.noFixedSegList"
              :key="`no-fix-${index}`"
              class="section">
              <div class="section__handle">
                <el-button
                  v-if="editScheduleShow"
                  :disabled="!canEditSchedule"
                  type="text"
                  icon="wk wk-edit"
                  @click="itemHandleClick('schedule', 'noFixed', noFixItem, index, form.noFixedSegList)">
                  {{ (noFixItem.schedule === null || noFixItem.schedule === '') && canEditSchedule ? '添加进度' : `进度${noFixItem.schedule}%` }}</el-button>
                <el-button
                  :disabled="form.isEmpWeight != 1"
                  type="text"
                  icon="wk wk-edit"
                  @click="itemHandleClick('weight', 'noFixed', noFixItem, index, form.noFixedSegList)">
                  {{ noFixItem.weight === null || noFixItem.weight === '' ? '添加权重' : `权重${noFixItem.weight}%` }}
                </el-button>
              </div>
              <div
                class="item-section">
                <el-row type="flex" class="item-row">
                  <el-col :span="23">
                    <flexbox>
                      <el-form-item
                        :prop="`noFixedSegList[${index}].segName`"
                        :rules="{ required: true, message: '请输入', trigger: 'change' }"
                        class="div-label"
                        label="">
                        <el-tooltip v-if="noFixItem.isDefault" :content="noFixItem.segName" placement="top">
                          <div>{{ noFixItem.segName }}</div>
                        </el-tooltip>
                        <el-input
                          v-else
                          v-model="noFixItem.segName"
                          :maxlength="50"
                          resize="none"
                          placeholder="标题"
                          rows="2"
                          type="textarea" />
                      </el-form-item>
                      <el-form-item
                        :prop="`noFixedSegList[${index}].value`"
                        :rules="{ required: true, message: '请输入', trigger: 'change' }"
                        class="div-body"
                        style="margin-left: -1px;"
                        label="">
                        <el-input
                          v-model="noFixItem.value"
                          :maxlength="200"
                          resize="none"
                          placeholder="内容"
                          rows="2"
                          type="textarea" />
                      </el-form-item>
                    </flexbox>
                  </el-col>
                  <el-col :span="1" class="is-close">
                    <el-button
                      v-if="form.noFixedSegList.length > 1 && !noFixItem.isDefault"
                      type="text"
                      @click="itemDeleteClick(form.noFixedSegList, index)">删除</el-button>
                  </el-col>
                </el-row>
                <div>
                  <el-row
                    v-for="(item, itemIndex) in noFixItem.items"
                    :key="`${index}-${itemIndex}`"
                    type="flex"
                    class="item-row"
                  >
                    <el-col :span="23">
                      <flexbox>
                        <el-form-item
                          :prop="`noFixedSegList[${index}].items[${itemIndex}].itemName`"
                          :rules="{ required: true, message: '请输入', trigger: 'change' }"
                          style="padding-left: 140px;"
                          label="">
                          <el-input
                            v-model="item.itemName"
                            :disabled="item.isDefault"
                            :maxlength="50"
                            class="sub-input sub-input-left"
                            placeholder="标题"
                            resize="none"
                            rows="2"
                            type="textarea" />
                        </el-form-item>
                        <el-form-item
                          :prop="`noFixedSegList[${index}].items[${itemIndex}].value`"
                          :rules="{ required: true, message: '请输入', trigger: 'change' }"
                          class="div-body"
                          style="margin-left: -1px;"
                          label=""
                        >
                          <el-input
                            v-model="item.value"
                            :maxlength="200"
                            class="sub-input-right"
                            placeholder="内容"
                            type="textarea"
                            rows="2"
                            resize="none"
                          />
                        </el-form-item>
                      </flexbox>
                    </el-col>
                    <el-col :span="1" class="is-close">
                      <i
                        class="el-icon-close"
                        @click="itemDeleteClick(noFixItem.items, itemIndex)" />
                    </el-col>
                  </el-row>
                  <el-button style="padding: 0 0 8px 140px;" icon="el-icon-plus" type="text" @click="subAddClick(noFixItem.items)">添加</el-button>
                </div>
              </div>
            </div>
            <el-button
              v-if="form.isEmpWeight == 1"
              icon="el-icon-plus"
              type="text"
              @click="bigAddClick(form.noFixedSegList)">添加考核项</el-button>
          </el-form>
          <div class="eep-right">
            <div class="epp-right__header">
              考核记录
            </div>
            <performance-history
              :id="id"
              ref="performanceHistory"
              class="epp-right__body" />
          </div>
        </div>
      </el-card>

      <weight-edit-dialog
        :visible.sync="weightEditShow"
        :surplus-weight="dialogEditData.surplusWeight"
        @change="weightChange"
      />

      <schedule-edit-dialog
        :id="dialogEditData.item ? dialogEditData.item.segId : ''"
        :visible.sync="scheduleEditShow"
        @change="scheduleChange"
      />
    </div>
  </transition>
</template>
<script type="text/javascript">
import {
  hrmPerformanceEmployeeAppraisalDetailAPI,
  hrmPerformanceEmployeeWriteAPI
} from '@/api/hrm/selfService/performance'

import PerformanceHistory from './PerformanceHistory'
import WeightEditDialog from './WeightEditDialog'
import ScheduleEditDialog from './ScheduleEditDialog'
import Reminder from '@/components/Reminder'

import { getMaxIndex, objDeepCopy, floatAdd } from '@/utils'
import NP from 'number-precision'
import { isEmpty } from '@/utils/types'

export default {
  name: 'EmployeeEditPerformance',
  components: {
    PerformanceHistory,
    WeightEditDialog,
    ScheduleEditDialog,
    Reminder
  },
  props: {
    handleType: String, // schedule（结果待评定 可修改状态)
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      zIndex: getMaxIndex(),
      detailData: null,
      form: null,
      weightEditShow: false,
      scheduleEditShow: false,
      dialogEditData: {}
    }
  },
  computed: {
    editScheduleShow() {
      // 3: '结果待评定' 可进行进度操作
      return this.detailData && this.detailData.status != 1 && this.detailData.status != 2
    },

    // 只有员工本人编辑
    canEditSchedule() {
      // 绩效状态 appraisalStatus 0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档
      return this.handleType == 'schedule' && this.detailData && this.detailData.status == 3 && (this.detailData.appraisalStatus == 1 || this.detailData.appraisalStatus == 2)
    },

    // 是否展示草稿
    saveDraftShow() {
      return this.detailData && this.detailData.status == 1
    },

    title() {
      return '绩效目标'
    }
  },
  watch: {
    id: {
      handler(val) {
        if (val) {
          this.detailData = null
          this.form = null
          this.getDetail()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 详情
     */
    getDetail() {
      this.loading = true
      hrmPerformanceEmployeeAppraisalDetailAPI(this.id)
        .then(res => {
          const data = res.data || {}
          this.detailData = objDeepCopy(data)

          const form = {}
          const tableTemp = data.tableTemp
          form.description = tableTemp.description
          form.isEmpWeight = tableTemp.isEmpWeight

          if (!data.fixedSegList) {
            form.fixedSegList = tableTemp.fixedSegListTemp || []
          } else {
            form.fixedSegList = data.fixedSegList
          }

          if (!data.noFixedSegList) {
            tableTemp.noFixedSegListTemp.forEach(item => {
              item.isDefault = true
              item.items.forEach(subItem => {
                subItem.isDefault = true
              })
            })
            form.noFixedSegList = tableTemp.noFixedSegListTemp || []
          } else {
            data.noFixedSegList.forEach(item => {
              item.isDefault = !!item.tempSegId
              item.items.forEach(subItem => {
                subItem.isDefault = !!subItem.tempItemId
              })
            })
            form.noFixedSegList = data.noFixedSegList
          }

          this.form = form
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    /**
     * 大块添加
     */
    bigAddClick(list) {
      const data = objDeepCopy(this.detailData)
      const tableTemp = data.tableTemp
      tableTemp.noFixedSegListTemp.forEach(item => {
        item.isDefault = false
        item.items.forEach(subItem => {
          subItem.isDefault = true
        })
      })
      list.push(tableTemp.noFixedSegListTemp[0])
    },

    /**
     * 子项添加
     */
    subAddClick(list) {
      list.push({
        itemName: ''
      })
    },

    /**
     * 事项删除
     */
    itemDeleteClick(list, index) {
      list.splice(index, 1)
    },

    /**
     * 权重修改
     */
    itemHandleClick(type, dataType, item, index, list) {
      this.dialogEditData = {
        type, dataType, item, index
      }
      if (type == 'weight') {
        let weight = 0
        list.forEach((number, nIndex) => {
          if (nIndex !== index) {
            weight = NP.plus(weight, isEmpty(number.weight) ? 0 : number.weight)
          }
        })
        const surplusWeight = NP.minus(100, weight)
        this.dialogEditData.surplusWeight = surplusWeight < 0 ? 0 : surplusWeight
        this.weightEditShow = true
      } else if (type == 'schedule') {
        this.scheduleEditShow = true
      }
    },

    weightChange(weight) {
      if (this.dialogEditData.type == 'weight') {
        this.dialogEditData.item.weight = weight
      }
    },

    scheduleChange(weight) {
      if (this.dialogEditData.type == 'schedule') {
        this.dialogEditData.item.schedule = weight
        this.$refs.performanceHistory.getDetail()
      }
    },

    submitClick(isDraft = false) {
      if (isDraft) {
        this.submiteData(isDraft)
      } else {
        this.validData().then(() => {
          this.submiteData(isDraft)
        }).catch(() => {})
      }
    },

    validData() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate((valid) => {
          if (valid) {
          // 1 员工自己填写 需要判断权重累加100
            if (this.form.isEmpWeight == 1) {
              let weight = 0
              let pass = true
              this.form.noFixedSegList.forEach(item => {
                if (item.weight == null || item.weight == undefined || item.weight == '') {
                  pass = false
                }
                weight = floatAdd(weight, item.weight)
              })

              if (pass) {
                this.form.fixedSegList.forEach(item => {
                  if (item.weight == null || item.weight == undefined || item.weight == '') {
                    pass = false
                  }
                  weight = floatAdd(weight, item.weight)
                })
              }

              if (!pass) {
                this.$message.error('请输入考核项权重')
                reject()
                return
              } else if (weight != 100) {
                this.$message.error('考核项权重累加，需等于100%')
                reject()
                return
              }
            }

            /**
               * 1: '目标待填写',
                 2: '目标待确认',
                 3: '结果待评定',
                 4: '结果待确认',
                 5: '考核终止',
                 6: '考核完成'
               */
            if (this.detailData.status == 2) {
              this.$confirm('确定重新提交吗？提交后需要目标确认人重新确认', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              })
                .then(() => {
                  resolve()
                })
                .catch(() => {})
            } else {
              resolve()
            }
          } else {
            this.$message.error('请完善考核内容')
            return false
          }
        })
      })
    },

    submiteData(isDraft) {
      const form = objDeepCopy(this.form)
      const uploadForm = {
        employeeAppraisalId: this.id,
        isDraft: isDraft ? 1 : 0// 是否为草稿 0否 1是
      }

      form.noFixedSegList.forEach(item => {
        if (item.isDefault) {
          item.tempSegId = item.segId
        }
        item.items.forEach(subItem => {
          if (subItem.isDefault) {
            subItem.tempItemId = subItem.itemId
          }
        })
      })
      uploadForm.segList = form.noFixedSegList.concat(form.fixedSegList)
      this.loading = true
      hrmPerformanceEmployeeWriteAPI(uploadForm)
        .then(res => {
          this.$message.success(isDraft ? '保存草稿成功' : '保存成功')
          this.$emit('change')
          this.closeClick()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

/** 容器布局 */
.eep {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 40px 0;
  background-color: #f5f6f9;

  &-con {
    width: 80%;
    height: 100%;
    padding: 15px;
    margin: 0 auto;
    background-color: white;

    ::v-deep .el-card__body {
      height: 100%;
    }
  }

  &-header {
    height: 50px;
    font-size: 24px;
    font-weight: bold;
    line-height: 50px;

    &__ft {
      float: right;
    }
  }

  &-body {
    display: flex;
    min-width: 900px;
    height: calc(100% - 50px);
  }

  &-left {
    flex: 1;
    padding: 0 10px;
    overflow-y: auto;
    border-right: 1px solid $--border-color-base;
  }

  &-right {
    flex-shrink: 0;
    width: 400px;
    padding: 10px;

    .epp-right__header {
      height: 40px;
      line-height: 40px;
      border-bottom: 1px solid $--border-color-base;
    }

    .epp-right__body {
      height: calc(100% - 60px);
    }
  }
}

.content-section {
  margin-top: 15px;

  .el-row {
    height: 34px;
    line-height: 34px;
  }

  &__handle {
    text-align: right;
  }

  .item-section {
    padding: 15px;
    color: $--color-text-regular;
    background-color: #f5f5f5;
    border: 1px solid #e6e6e6;
    border-radius: 4px;

    .sub-input-value {
      position: relative;
    }

    .sub-input-value::before {
      position: absolute;
      top: 8px;
      left: -25px;
      width: 10px;
      height: 10px;
      content: "";
      border-bottom: 1px solid #e6e6e6;
      border-left: 1px solid #e6e6e6;
    }
  }
}

.section {
  margin-top: 15px;

  &__title {
    span {
      color: $--color-text-secondary;
    }

    margin-bottom: 10px;
  }

  &__row {
    margin-bottom: 10px;
  }

  &__handle {
    text-align: right;
  }

  .item-section {
    padding: 15px;
    border: 1px solid #e6e6e6;
    border-radius: 4px;

    .item-row {
      .div-label {
        width: 140px;
        padding-right: 15px;
      }

      .div-body {
        flex: 1;
      }
    }

    .sub-input::before {
      position: absolute;
      top: 8px;
      left: -25px;
      width: 10px;
      height: 10px;
      content: "";
      border-bottom: 1px solid #e6e6e6;
      border-left: 1px solid #e6e6e6;
    }

    .sub-input-left {
      ::v-deep .el-input__inner {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
      }

      ::v-deep .el-textarea__inner {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
      }
    }

    .sub-input-right {
      ::v-deep .el-input__inner {
        border-top-left-radius: 0;
        border-bottom-left-radius: 0;
      }

      ::v-deep .el-textarea__inner {
        border-top-left-radius: 0;
        border-bottom-left-radius: 0;
      }
    }
  }
}

.el-form {
  ::v-deep .el-form-item__content {
    line-height: 34px;
  }
}

.el-col.is-close {
  line-height: 34px;
  text-align: center;

  .el-icon-close {
    font-size: 20px;
    color: #ccc;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }
}
</style>
