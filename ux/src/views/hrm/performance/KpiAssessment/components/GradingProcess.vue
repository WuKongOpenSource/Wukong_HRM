<template>
  <el-form
    ref="form"
    :model="gradingProcessForm"
    label-width="80px">
    <el-table :data="gradingProcessForm['processSettingScoringBOList']">
      <el-table-column
        v-for="(item,index) in tableFieldList"
        :key="index"
        :label="item.label"
        :width="item.width"
        :prop="item.prop">
        <!-- eslint-disable-next-line -->
        <template slot="header" slot-scope=" { column, $index }">
          <span>
            {{ item.label }}
            <span
              v-if="item.prop== 'rater' || item.prop == 'weight'"
              style="color: #de350b;">*</span>
          </span>
        </template>

        <template slot-scope="{$index}">
          <div
            v-if="item.prop == 'rater' && gradingProcessForm['processSettingScoringBOList'].length-1 == $index"
            class="rater">
            <el-button
              v-show="addSelfAssessmentShow"
              :disabled="disabled"
              type="text"
              @click="addScore('self')">+新增自评</el-button>
            <el-button
              type="text"
              :disabled="disabled"
              @click="addScore('others')">+新增他人评分</el-button>
          </div>

          <span
            v-if="item.prop == 'weight' && gradingProcessForm['processSettingScoringBOList'].length-1 == $index && weightTotal"
            :style="weightTotal > 100 ? 'color:red' :'color: #ff991f'">{{ weightTotal }}%</span>

          <span v-if="item.prop == 'rater' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index && !gradingProcessForm['processSettingScoringBOList'][$index].hasOwnProperty('rejectAuthority')">
            被审核人
          </span>

          <div
            v-if="item.prop == 'rater' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index && gradingProcessForm['processSettingScoringBOList'][$index].hasOwnProperty('rejectAuthority')"
            class="rater">
            <el-form-item
              :prop="`processSettingScoringBOList[${$index}].raterType`"
              :rules="{required: true, message: '请选择', trigger: 'change'}">
              <el-select
                v-model="gradingProcessForm['processSettingScoringBOList'][$index]['raterType']"
                :disabled="disabled"
                @change="confirmationTypeChange($event,$index)">
                <el-option
                  v-for="sItem in scorerOptions"
                  :key="sItem.value"
                  :label="sItem.label"
                  :value="sItem.value" />
              </el-select>
            </el-form-item>

            <el-form-item
              v-if="gradingProcessForm['processSettingScoringBOList'][$index]['raterType'] == 1 || gradingProcessForm['processSettingScoringBOList'][$index]['raterType'] == 2"
              :prop="`processSettingScoringBOList[${$index}].raterLevel`"
              :rules="{required: true, message: '请选择', trigger: 'change'}">
              <el-select
                v-if="gradingProcessForm['processSettingScoringBOList'][$index]['raterType'] == 1"
                v-model="gradingProcessForm['processSettingScoringBOList'][$index]['raterLevel']"
                :disabled="disabled"
                @change="confirmationLevelChange($event,'superior')">
                <el-option
                  v-for="sItem in superiorOptions"
                  :key="sItem.value"
                  :label="sItem.label"
                  :disabled="sItem.disabled"
                  :value="sItem.value" />
              </el-select>
              <el-select
                v-if="gradingProcessForm['processSettingScoringBOList'][$index]['raterType'] == 2"
                v-model="gradingProcessForm['processSettingScoringBOList'][$index]['raterLevel']"
                :disabled="disabled"
                @change="confirmationLevelChange($event,'dep')">
                <el-option
                  v-for="sItem in departmentOptions"
                  :key="sItem.value"
                  :label="sItem.label"
                  :disabled="sItem.disabled"
                  :value="sItem.value" />
              </el-select>
            </el-form-item>

            <el-form-item
              v-if="gradingProcessForm['processSettingScoringBOList'][$index]['raterType'] == 3"
              :prop="`processSettingScoringBOList[${$index}].rater`"
              :rules="{required: true, message: '请选择', trigger: 'change'}">
              <wk-user-select
                v-model="gradingProcessForm['processSettingScoringBOList'][$index]['rater']"
                style="width: 120px;height: 32px;"
                :disabled="disabled"
                :props="{dataType: 'hrm',label: 'employeeName',value:'employeeId',isKpi:true}"
                :radio="true"
                @change="userSelectChange(arguments,$index)" />
            </el-form-item>

          </div>
          <el-form-item
            v-if="item.prop== 'weight' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index"
            :prop="`processSettingScoringBOList[${$index}].weight`"
            :rules="{required: true, message: '不能为空', trigger: ['change','blur']}">
            <wk-percent-input
              v-model="gradingProcessForm['processSettingScoringBOList'][$index][item.prop]"
              :disabled="disabled"
              style="width: 100%;"
              :min="0"
              :max="100"
              :controls="false" />
          </el-form-item>

          <el-select
            v-if="item.formType == 'select' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index"
            v-model="gradingProcessForm['processSettingScoringBOList'][$index][item.prop]"
            :disabled="disabled">
            <el-option
              v-for="sItem in item.setting"
              :key="sItem.value"
              :label="sItem.label"
              :value="sItem.value" />
          </el-select>

          <div
            v-if="item.prop == 'requiredSetting' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index"
            class="onOff">
            <span>考核评语</span>
            <el-switch
              v-model="gradingProcessForm['processSettingScoringBOList'][$index][item.prop]"
              :disabled="disabled"
              active-color="#00875A"
              inactive-color="#ff4949" />
          </div>

          <div
            v-if="item.prop == 'rejectAuthority' && gradingProcessForm['processSettingScoringBOList'].length-1 != $index "
            class="onOff">
            <span>{{ gradingProcessForm['processSettingScoringBOList'][$index].hasOwnProperty('rejectAuthority') ? '允许驳回' : '--' }}</span>
            <el-switch
              v-if="gradingProcessForm['processSettingScoringBOList'][$index].hasOwnProperty('rejectAuthority')"
              v-model="gradingProcessForm['processSettingScoringBOList'][$index][item.prop]"
              :disabled="disabled"
              active-color="#00875A"
              inactive-color="#ff4949" />
          </div>

        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{row, $index}">
          <el-button
            v-if="gradingProcessForm['processSettingScoringBOList'].length-1 != $index"
            type="text"
            :disabled="disabled"
            @click="handlerDel(row,$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
</template>

<script>
import WkPercentInput from '@/components/NewCom/WkPercentInput'
import WkUserSelect from '@/components/NewCom/WkUserSelect'

import { objDeepCopy } from '@/utils'
export default {
  name: 'GradingProcess',
  components: {
    WkPercentInput,
    WkUserSelect
  },
  inject: {
    disabled: {
      default: false
    }
  },
  props: {
    processSettingScoringBOList: Array,
    type: [String, Number]
  },
  data() {
    return {
      gradingProcessForm: {},
      tableFieldList: [
        { label: '评分人', prop: 'rater', width: '260' },
        { label: '评分权重', prop: 'weight', formType: 'percent' },
        { label: '评分方式', prop: 'scoringType', formType: 'select', setting: [{ label: '按指标评分', value: 1 }] },
        { label: '可见内容', prop: 'visibleContent', formType: 'select', setting: [{ label: '所有人的评分/评语', value: 2 }, { label: '仅自己的评分/评语', value: 1 }] },
        { label: '必填设置', prop: 'requiredSetting', formType: 'onOff' },
        { label: '驳回权限', prop: 'rejectAuthority', formType: 'onOff' }
      ],
      addSelfAssessmentShow: true,

      scorerOptions: [
        { label: '上级', value: 1 },
        { label: '部门负责人', value: 2 },
        { label: '指定评分人', value: 3 }
      ],

      superiorOptions: [
        { label: '直属上级', value: 1 },
        { label: '第二级上级', value: 2 },
        { label: '第三级上级', value: 3 },
        { label: '第四级上级', value: 4 },
        { label: '第五级上级', value: 5 },
        { label: '第六级上级', value: 6 },
        { label: '第七级上级', value: 7 },
        { label: '第八级上级', value: 8 },
        { label: '第九级上级', value: 9 },
        { label: '第十级上级', value: 10 }
      ],

      departmentOptions: [
        { label: '直属部门负责人', value: 1 },
        { label: '第二级部门负责人', value: 2 },
        { label: '第三级部门负责人', value: 3 },
        { label: '第四级部门负责人', value: 4 },
        { label: '第五级部门负责人', value: 5 },
        { label: '第六级部门负责人', value: 6 },
        { label: '第七级部门负责人', value: 7 },
        { label: '第八级部门负责人', value: 8 },
        { label: '第九级部门负责人', value: 9 },
        { label: '第十级部门负责人', value: 10 }
      ]
    }
  },

  computed: {
    weightTotal() {
      var sum = 0
      this.processSettingScoringBOList.forEach(item => {
        if (item.weight)sum += item.weight
      })
      return sum
    }
  },

  watch: {
    processSettingScoringBOList: {
      handler(val) {
        val.forEach(item => {
          this.superiorOptions.forEach(sItem => {
            if (item.raterLevel == sItem.value && item.raterType == 1) {
              sItem.disabled = true
            }
          })
        })

        val.forEach(item => {
          this.departmentOptions.forEach(sItem => {
            if (item.raterLevel == sItem.value && item.raterType == 2) {
              sItem.disabled = true
            }
          })
        })
        if (val.length > 1) {
          val.forEach(item => {
            if (!item.hasOwnProperty('rejectAuthority') && item.hasOwnProperty('requiredSetting')) this.addSelfAssessmentShow = false
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  created() {
    this.gradingProcessForm.processSettingScoringBOList = this.processSettingScoringBOList
  },

  methods: {
    /**
     * 新增评分
     * type: self 自评，other 他人评分
     */
    addScore(type) {
      if (type == 'self') {
        this.processSettingScoringBOList.splice(this.processSettingScoringBOList.length - 1, 0,
          { rater: '', raterType: 4, weight: undefined, scoringType: 1, visibleContent: 2, requiredSetting: false })
        this.addSelfAssessmentShow = false
      } else {
        this.processSettingScoringBOList.splice(this.processSettingScoringBOList.length - 1, 0,
          { rater: '', weight: undefined, scoringType: 1, visibleContent: 2, requiredSetting: false, rejectAuthority: false, raterType: '' })
      }
    },

    /**
     * 删除
     */
    handlerDel(row, index) {
      if (!row.rejectAuthority) {
        this.addSelfAssessmentShow = true
      }

      this.processSettingScoringBOList.forEach(item => {
        this.superiorOptions.forEach(sItem => {
          if (item.raterLevel != sItem.value && item.raterType == 1) {
            sItem.disabled = false
          }
        })
      })

      this.processSettingScoringBOList.forEach(item => {
        this.departmentOptions.forEach(sItem => {
          if (item.raterLevel != sItem.value && item.raterType == 2) {
            sItem.disabled = false
          }
        })
      })

      this.processSettingScoringBOList.splice(index, 1)
    },

    /**
     * 上级或部门变化禁用选项
     */
    confirmationLevelChange(val, type) {
      if (type == 'superior') {
        this.superiorOptions.forEach(item => {
          if (item.value == val) {
            item.disabled = true
          } else {
            item.disabled = false
          }
        })
      } else {
        this.departmentOptions.forEach(item => {
          if (item.value == val) {
            item.disabled = true
          } else {
            item.disabled = false
          }
        })
      }
    },

    /**
     * 选择项变化
     */
    confirmationTypeChange(val, index) {
      if (val == 1) {
        this.$set(this.processSettingScoringBOList[index], 'raterLevel', '')
      } else if (val == 2) {
        this.$set(this.processSettingScoringBOList[index], 'raterLevel', '')
      } else if (val == 3) {
        this.$set(this.processSettingScoringBOList[index], 'rater', '')
      }

      this.processSettingScoringBOList.forEach(item => {
        this.superiorOptions.forEach(sItem => {
          if (item.raterLevel != sItem.value && item.raterType == 1) {
            sItem.disabled = false
          }
        })
      })

      this.processSettingScoringBOList.forEach(item => {
        this.departmentOptions.forEach(sItem => {
          if (item.raterLevel != sItem.value && item.raterType == 2) {
            sItem.disabled = false
          }
        })
      })
    },

    /**
     * 选择人员变化
     */
    userSelectChange(selectUser, index) {
      if (selectUser[0].length && selectUser[1].length) {
        const userId = selectUser[0][0]
        const userName = selectUser[1][0].employeeName
        const processList = objDeepCopy(this.processSettingScoringBOList)
        processList.splice(index, 1)
        processList.forEach(item => {
          if (item.rater == userId) {
            this.$set(this.processSettingScoringBOList[index], 'rater', '')
            this.$message.error(`${userName}已经存在`)
          }
        })
      }
    }
  }
}
</script>

<style scope lang="scss">
.el-form {
  .el-table {
    .el-form-item {
      margin-top: 0 !important;

      .el-form-item__content {
        margin-left: unset !important;
      }
    }

    .rater {
      display: flex;
      justify-content: space-between;

      .wk-tags-view {
        ::v-deep .el-tag__close {
          line-height: unset !important;
        }
      }
    }

    .onOff {
      display: flex;
      align-items: center;
      justify-content: space-around;
    }
  }

  .el-button--text {
    color: #0052cc;

    &.is-disabled {
      color: #a5adba;
    }
  }
}

</style>
