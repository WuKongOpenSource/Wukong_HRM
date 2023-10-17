<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <xr-create
    v-loading="loading"
    :title="titleContent"
    @close="close"
    @save="saveClick">
    <create-sections title="基本信息">
      <el-form
        ref="baseForm"
        :model="baseForm"
        :rules="baseRules">
        <div class="base-info-section">
          <el-form-item
            prop="appraisalName">
            <template slot="label">
              考核名称
            </template>
            <el-input
              v-model="baseForm.appraisalName"
              :disabled="optionsType != 10"
              :maxlength="100" />
          </el-form-item>
          <el-form-item
            prop="cycleType">
            <template slot="label">
              考核时间
            </template>
            <el-radio-group
              v-model="baseForm.cycleType"
              :disabled="optionsType != 10"
              style="width: 100%;">
              <el-radio
                v-for="(item, index) in cycleTypeList"
                :key="index"
                :label="item.value">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            prop="startTime">
            <template slot="label">
              考核开始时间
            </template>
            <el-date-picker
              v-model="baseForm.startTime"
              :disabled="optionsType != 10"
              clearable
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期" />
          </el-form-item>
          <el-form-item
            prop="endTime">
            <template slot="label">
              考核结束时间
            </template>
            <el-date-picker
              v-model="baseForm.endTime"
              :disabled="optionsType != 10"
              clearable
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期" />
          </el-form-item>
          <el-form-item
            prop="tableId">
            <template slot="label">
              考核表模板
            </template>
            <el-select
              v-model="baseForm.tableId"
              :disabled="optionsType != 10"
              style="width: 100%;">
              <el-option
                v-for="(item, index) in tableList"
                :key="index"
                :label="item.tableName"
                :value="item.tableId" />
            </el-select>
          </el-form-item>
        </div>

        <div class="other-info-section">
          <el-form-item
            prop="writtenBy">
            <template slot="label">
              考核目标填写人
            </template>
            <div>本人</div>
          </el-form-item>
          <el-form-item
            prop="targetConfirmorsList">
            <template slot="label">
              考核目标确认人<el-tooltip
                content="按照添加的目标确认人顺序对考核目标进行确认"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <flexbox
              v-for="(confirm, cIndex) in baseForm.targetConfirmorsList"
              :key="cIndex"
              class="user-data-row">
              <el-form-item
                :prop="`targetConfirmorsList[${cIndex}].type`"
                :rules="{ required: true, message: '请选择', trigger: 'change' }"
                label="">
                <el-select
                  v-model="confirm.type"
                  :disabled="optionsType != 10">
                  <el-option
                    v-for="(item, index) in userTypeList"
                    :key="index"
                    :label="item.label"
                    :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item
                v-if="confirm.type == 5"
                :prop="`targetConfirmorsList[${cIndex}].employeeId`"
                :rules="getTypeEmployeeIdRules(confirm)"
                label="">
                <wk-user-select
                  v-model="confirm.employeeId"
                  :disabled="optionsType != 10 && confirm.status == 1"
                  :options="userList"
                  :props="{ label: 'employeeName', value: 'employeeId', isList: true }"
                  radio />
              </el-form-item>
              <i
                v-if="optionsType == 10"
                class="el-icon-remove"
                @click="deleteItemClick(baseForm.targetConfirmorsList, cIndex)" />
            </flexbox>
            <div v-if="optionsType == 10" class="add-bar">
              <el-button icon="el-icon-plus" type="text" @click="addConfirmClick">添加目标确认人</el-button>
            </div>
          </el-form-item>
          <el-form-item
            prop="evaluatorsList">
            <template slot="label">
              考核结果评定人<el-tooltip
                content="按照添加的结果评定人顺序对考核结果进行评定"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <flexbox
              v-for="(evaluator, eIndex) in baseForm.evaluatorsList"
              :key="eIndex"
              class="user-data-row">
              <el-form-item
                :prop="`evaluatorsList[${eIndex}].type`"
                :rules="{ required: true, message: '请选择', trigger: 'change' }"
                label="">
                <el-select
                  v-model="evaluator.type"
                  :disabled="optionsType < 9">
                  <el-option
                    v-for="(item, index) in evaluatorTypeList"
                    :key="index"
                    :label="item.label"
                    :value="item.value" />
                </el-select>
              </el-form-item>
              <div class="weight-label">权重</div>
              <el-form-item
                :prop="`evaluatorsList[${eIndex}].weight`"
                :rules="getTypeWeightRules(eIndex)"
                label="">
                <el-input
                  v-model="evaluator.weight"
                  v-wk-number="'positiveInt'"
                  :disabled="optionsType < 9"
                  style="width: 120px;"
                  type="text"
                  @blur="weightInputBlur">
                  <template slot="append">%</template>
                </el-input>
              </el-form-item>
              <el-form-item
                v-if="evaluator.type == 5"
                :prop="`evaluatorsList[${eIndex}].employeeId`"
                :rules="getTypeEmployeeIdRules(evaluator)"
                label="">
                <wk-user-select
                  v-model="evaluator.employeeId"
                  :disabled="optionsType < 9 && evaluator.status == 1"
                  :options="userList"
                  :props="{ label: 'employeeName', value: 'employeeId', isList: true }"
                  radio />
              </el-form-item>
              <i
                v-if="optionsType >= 9"
                class="el-icon-remove"
                @click="deleteItemClick(baseForm.evaluatorsList, eIndex)" />
            </flexbox>
            <div v-if="optionsType >= 9" class="add-bar">
              <el-button icon="el-icon-plus" type="text" @click="addEvaluatorClick">添加评定人</el-button>
            </div>
          </el-form-item>
          <el-form-item
            prop="resultConfirmors">
            <template slot="label">
              考核结果确认人<el-tooltip
                content="考核结果确认人将对整个考核计划进行确认"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <wk-user-select
              v-model="baseForm.resultConfirmors"
              :disabled="optionsType < 8 && resultConfirmorsDisabled"
              :options="resultUserList"
              :props="{ label: 'employeeName', value: 'employeeId', isList: true }"
              :radio="false"
              style="width: 352px;" />
          </el-form-item>

          <el-form-item
            prop="scoreLevelList">
            <template slot="label">
              考核规则
            </template>
            <el-form-item
              :rules="getFullScoreRules()"
              prop="fullScore"
              label="">
              <el-input
                v-model="baseForm.fullScore"
                v-wk-number="'positiveInt'"
                :disabled="optionsType != 10"
                style="width: 120px;"
                type="text">
                <template slot="append">分</template>
              </el-input>
              <el-checkbox
                v-model="baseForm.isForce"
                :false-label="0"
                :true-label="1"
                :disabled="optionsType != 10">开启强制分布</el-checkbox><el-tooltip
                  effect="dark"
                  placement="top">
                  <div slot="content">
                    <div>1.开启强制分布：结果确认人确认时，超过规定比例不能确认通过</div>
                    <div>2.不开启强制分布：结果确认人确认时，超过规定比例也能确认通过</div>
                  </div>
                  <i class="wk wk-help wk-help-tips" />
                </el-tooltip>
            </el-form-item>
            <div class="wk-text-tips">注：评分等级从上到下排序对应评分等级从高到底</div>
            <flexbox
              v-for="(scoreLevel, sIndex) in baseForm.scoreLevelList"
              :key="sIndex"
              class="weight-data-row">
              <div class="column">
                <div v-if="sIndex == 0" class="label">等级</div>
                <el-form-item
                  :prop="`scoreLevelList[${sIndex}].levelName`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="scoreLevel.levelName"
                    :disabled="optionsType != 10"
                    style="width: 120px;"
                    type="text" />
                </el-form-item>
              </div>
              <div class="column">
                <div v-if="sIndex == 0" class="label">分数范围</div>
                <flexbox>
                  <el-form-item
                    :prop="`scoreLevelList[${sIndex}].minScore`"
                    :rules="getScoreRules(scoreLevel, sIndex, 'min')"
                    label="">
                    <el-input
                      v-model="scoreLevel.minScore"
                      v-wk-number="'positiveInt'"
                      :disabled="optionsType != 10"
                      style="width: 120px;"
                      type="text"
                      @blur="inputBlurValidate('score', sIndex, 'min')">
                      <template slot="append">分</template>
                    </el-input>
                  </el-form-item>
                  <div class="range">~</div>
                  <el-form-item
                    :prop="`scoreLevelList[${sIndex}].maxScore`"
                    :rules="getScoreRules(scoreLevel, sIndex, 'max')"
                    label="">
                    <el-input
                      v-model="scoreLevel.maxScore"
                      v-wk-number="'positiveInt'"
                      :disabled="optionsType != 10"
                      style="width: 120px;"
                      type="text"
                      @blur="inputBlurValidate('score', sIndex, 'max')">
                      <template slot="append">分</template>
                    </el-input>
                  </el-form-item>
                </flexbox>
              </div>
              <div class="column">
                <div v-if="sIndex == 0" class="label">人数比例范围</div>
                <flexbox>
                  <el-form-item
                    :prop="`scoreLevelList[${sIndex}].minNum`"
                    :rules="getProportionRules(scoreLevel, sIndex, 'min')"
                    label="">
                    <el-input
                      v-model="scoreLevel.minNum"
                      v-wk-number
                      :disabled="optionsType != 10"
                      style="width: 120px;"
                      type="text"
                      @blur="inputBlurValidate('num', sIndex, 'min')">
                      <template slot="append">%</template>
                    </el-input>
                  </el-form-item>
                  <div class="range">~</div>
                  <el-form-item
                    :prop="`scoreLevelList[${sIndex}].maxNum`"
                    :rules="getProportionRules(scoreLevel, sIndex, 'max')"
                    label="">
                    <el-input
                      v-model="scoreLevel.maxNum"
                      v-wk-number
                      :disabled="optionsType != 10"
                      style="width: 120px;"
                      type="text"
                      @blur="inputBlurValidate('num', sIndex, 'max')">
                      <template slot="append">%</template>
                    </el-input>
                  </el-form-item>
                </flexbox>
              </div>
              <i
                v-if="optionsType == 10"
                :class="{ 'is-first' : sIndex == 0 }"
                class="el-icon-remove"
                @click="deleteItemClick(baseForm.scoreLevelList, sIndex)" />
            </flexbox>
            <div v-if="optionsType == 10" class="add-bar">
              <el-button icon="el-icon-plus" type="text" @click="addScoreLevelClick">添加考核结果等级</el-button>
            </div>
          </el-form-item>

          <el-form-item
            :rules="{ required: true, message: '请选择', trigger: ['blur', 'change'] }"
            prop="range"
          >
            <template slot="label">
              考核范围
            </template>
            <flexbox>
              <el-form-item
                :rules="getRangeRules()"
                prop="employeeIds"
                label="">
                <wk-user-dep-select
                  :disabled="optionsType != 10"
                  :user-value.sync="baseForm.employeeIds"
                  :dep-value.sync="baseForm.deptIds"
                  :props="{dataType: 'hrm'}"
                  style="width: 250px;"
                  placeholder="选择考核范围"
                />
              </el-form-item>
            </flexbox>
          </el-form-item>
        </div>

      </el-form>
    </create-sections>
  </xr-create>
</template>

<script>
import {
  hrmPerformanceAddAPI,
  hrmPerformanceSetAPI,
  hrmPerformanceDetailAPI
} from '@/api/hrm/performance'
import {
  hrmConfigQueryAchievementListAPI
} from '@/api/admin/hrm'
import {
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'

import XrCreate from '@/components/XrCreate'
import CreateSections from '@/components/CreateSections'
import WkUserSelect from '@/components/NewCom/WkUserSelect'
import WkUserDepSelect from '@/components/NewCom/WkUserDepSelect'

import { objDeepCopy, floatAdd, formatTimeToTimestamp } from '@/utils'

export default {
  // 绩效新建
  name: 'PerformanceCreateView',
  components: {
    XrCreate,
    CreateSections,
    WkUserSelect,
    WkUserDepSelect
  },
  filters: {},

  inject: ['editAuth'],
  props: {
    id: [String, Number],
    status: [String, Number]
  },
  data() {
    var validateTime = (rule, value, callback) => {
      if (this.baseForm.startTime && this.baseForm.endTime) {
        if (
          formatTimeToTimestamp(this.baseForm.startTime) >=
          formatTimeToTimestamp(this.baseForm.endTime)
        ) {
          callback(new Error('开始时间必须小于结束时间'))
        }
      }
      callback()
    }
    return {
      loading: false,
      // 编辑用
      detail: null,
      baseRules: {

        appraisalName: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        cycleType: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        startTime: [
          { required: true, message: '请选择', trigger: ['blur', 'change'] },
          { validator: validateTime, trigger: 'blur' }
        ],
        endTime: [
          { required: true, message: '请选择', trigger: ['blur', 'change'] },
          { validator: validateTime, trigger: 'blur' }
        ],
        tableId: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        writtenBy: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        targetConfirmorsList: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        evaluatorsList: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        resultConfirmors: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        scoreLevelList: { required: true, message: '请选择', trigger: ['blur', 'change'] }
      },
      // 是否能编辑结果
      resultConfirmorsDisabled: false,
      baseForm: {
        range: [1],
        writtenBy: 1,
        cycleType: 1,
        targetConfirmorsList: [{
          type: 1,
          employeeId: ''
        }],
        evaluatorsList: [{
          type: 1,
          weight: '100',
          employeeId: ''
        }],
        fullScore: 100,
        scoreLevelList: [{
          levelName: 'S',
          minScore: '85',
          maxScore: '100',
          minNum: '10',
          maxNum: '15'
        }, {
          levelName: 'A',
          minScore: '75',
          maxScore: '84',
          minNum: '50',
          maxNum: '55'
        }, {
          levelName: 'B',
          minScore: '60',
          maxScore: '74',
          minNum: '30',
          maxNum: '35'
        }, {
          levelName: 'C',
          minScore: '0',
          maxScore: '59',
          minNum: '10',
          maxNum: '15'
        }],
        // 是否开启强制分布 1 是 0 否
        isForce: 0
      },
      // 考核模板
      tableList: [],
      // 周期类型
      cycleTypeList: [{
        label: '月',
        value: 1
      }, {
        label: '季',
        value: 2
      }, {
        label: '年',
        value: 3
      }, {
        label: '半年',
        value: 4
      }],
      // 指定人类型
      userTypeList: [{
        label: '员工本人',
        value: 1
      }, {
        label: '直属上级',
        value: 2
      }, {
        label: '所在部门负责人',
        value: 3
      }, {
        label: '上级部门负责人',
        value: 4
      }, {
        label: '指定目标确认人',
        value: 5
      }],
      evaluatorTypeList: [{
        label: '员工本人',
        value: 1
      }, {
        label: '直属上级',
        value: 2
      }, {
        label: '所在部门负责人',
        value: 3
      }, {
        label: '上级部门负责人',
        value: 4
      }, {
        label: '指定结果评定人',
        value: 5
      }],
      // 员工参数
      userList: []
    }
  },

  computed: {
    titleContent() {
      return this.id ? '考核设置' : '新建考核'
    },
    // 10 全部 9 结果评定人和结果确认人 8 结果确认人 小于8 都不能
    //  0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档 5 进行中的考核
    optionsType() {
      if (this.id) {
        // if (this.detail) {
        //   if (this.detail.status == 0) {
        //     return 10
        //   } else if (this.detail.status == 1) {
        //     return 9
        //   } else if (this.detail.status == 2) {
        //     return 8
        //   }
        // }
        // 没有编辑权限 返回 0
        if (!this.editAuth) {
          return 0
        }
        // 是终止
        if (this.detail && this.detail.isStop === 1) {
          return 0
        }
        if (this.status == 0) {
          return 10
        } else if (this.status == 1) {
          return 9
        } else if (this.status == 2) {
          return 8
        }
        return 0
      }
      return 10
    },

    // 结果确认人 userList
    resultUserList() {
      if (this.optionsType < 8) {
        const resultConfirmors = this.detail && this.detail.resultConfirmors ? this.detail.resultConfirmors : []
        const resultConfirmorIds = resultConfirmors.map(item => item.employeeId)

        const userList = []
        this.userList.forEach(item => {
          const newItem = { ...item }
          const has = resultConfirmorIds.includes(item.employeeId)
          newItem.isHide = has
          newItem.disabled = has && newItem.status == 1
          userList.push(newItem)
        })

        return userList
      }

      return this.userList
    }
  },
  watch: {
    id: {
      handler(val) {
        if (val) {
          this.getDetail()
        }
      },
      immediate: true
    }
  },
  created() {
    this.getTableList()
    this.getUserList()
  },
  mounted() {
  },

  beforeDestroy() {},
  methods: {
    /**
     * 获取详情
     */
    getDetail() {
      this.detail = null
      this.loading = true
      hrmPerformanceDetailAPI(this.id)
        .then(res => {
          this.loading = false
          this.detail = res.data
          if (this.detail) {
            this.detail.range = [1] // 绩效范围模板必填
            this.baseForm = objDeepCopy(this.detail)
            const resultConfirmors = this.baseForm.resultConfirmors || []
            this.resultConfirmorsDisabled = !resultConfirmors.find(item => item.status != 1)
          }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('close')
    },

    /**
     * 获取考核模板详情
     */
    getTableList() {
      this.loading = true
      hrmConfigQueryAchievementListAPI()
        .then(res => {
          this.loading = false
          this.tableList = res.data || []
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取员工信息
     */
    getUserList() {
      hrmEmployeeQueryInAPI()
        .then(res => {
          const userList = res.data || []
          userList.forEach(item => {
            item.isHide = item.status != 1
          })
          this.userList = userList

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 增加目标确认人
     */
    addConfirmClick() {
      this.baseForm.targetConfirmorsList.push({
        type: 1,
        employeeId: ''
      })
    },

    deleteItemClick(list, index) {
      list.splice(index, 1)
    },

    /**
     * 增加考核结果评定人
     */
    addEvaluatorClick() {
      this.baseForm.evaluatorsList.push({
        type: 1,
        employeeId: '',
        weight: ''
      })
    },

    /**
     * 增加评测
     */
    addScoreLevelClick() {
      this.baseForm.scoreLevelList.push({
        levelName: ''
      })
    },

    /**
     * 指定人类型 员工值 规则
     */
    getTypeEmployeeIdRules(data) {
      const validateEmployeeId = (rule, value, callback) => {
        if (data.type == 5 && !value) {
          callback(new Error('请选择'))
        } else {
          callback()
        }
      }
      return { validator: validateEmployeeId, trigger: ['change', 'blur'], data }
    },

    getTypeWeightRules(index) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error('范围在0-100之间'))
        } else {
          if (this.baseForm.evaluatorsList.length) {
            let weight = 0
            this.baseForm.evaluatorsList.forEach(item => {
              weight += parseInt(item.weight)
            })
            if (weight != 100 && rule.index == this.baseForm.evaluatorsList.length - 1) {
              callback(new Error('权重之和需等于100'))
            } else {
              callback()
            }
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur', 'change'], index }
    },

    /**
     * 获取比例规则
     */
    getProportionRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error('范围在0-100之间'))
        } else {
          if (parseInt(rule.data.maxNum || 0) < parseInt(rule.data.minNum || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else if (this.baseForm.scoreLevelList.length) {
            let min = 0
            let max = 0
            this.baseForm.scoreLevelList.forEach(item => {
              min = floatAdd(min, item.minNum)
              max = floatAdd(max, item.maxNum)
            })
            if (min > 100 && rule.type == 'min') {
              if (rule.index == this.baseForm.scoreLevelList.length - 1) {
                callback(new Error('下限值之和不能大于100'))
              }
            } else if (max < 100 && rule.type == 'max') {
              if (rule.index == this.baseForm.scoreLevelList.length - 1) {
                callback(new Error('上限值应大于等于100'))
              }
            } else {
              callback()
            }
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 获取总分数规则
     */
    getFullScoreRules() {
      const validateWeight = (rule, value, callback) => {
        if (value < 1 || value > 99999 || value === null || value === undefined || value === '') {
          callback(new Error('范围在1-99999之间'))
        } else {
          callback()
        }
      }
      return { validator: validateWeight, trigger: ['blur'] }
    },

    /**
     * 获取范围规则
     */
    getRangeRules() {
      const validateWeight = (rule, value, callback) => {
        if ((this.baseForm.employeeIds && this.baseForm.employeeIds.length) ||
        (this.baseForm.deptIds && this.baseForm.deptIds.length)) {
          callback()
        } else {
          callback(new Error('选择考核范围'))
        }
      }
      return { validator: validateWeight, trigger: ['change'] }
    },

    /**
     * 获取分数规则
     */
    getScoreRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        // 没有总分 不验证
        if (!this.baseForm.fullScore) {
          callback()
        } else if (value < 0 || value > parseInt(this.baseForm.fullScore) || value === null || value === undefined || value === '') {
          callback(new Error(`范围在0-${this.baseForm.fullScore}之间`))
        } else {
          if (parseInt(rule.data.maxScore || 0) < parseInt(rule.data.minScore || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else {
            if (rule.index > 0) {
              const preItem = this.baseForm.scoreLevelList[rule.index - 1]
              if (parseInt(preItem.minScore || 0) <= parseInt(value || 0)) {
                callback(new Error('须小于上一等级下限值'))
                return
              }
            }
            callback()
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 分数 比例失去焦点 验证准确
     */
    inputBlurValidate(handleType, index, type) {
      if (handleType == 'num') {
        this.$refs.baseForm.validateField(`scoreLevelList[${index}].${type == 'min' ? 'maxNum' : 'minNum'}`)
      } else if (handleType == 'score') {
        this.$refs.baseForm.validateField(`scoreLevelList[${index}].${type == 'min' ? 'maxScore' : 'minScore'}`)
      }
    },

    weightInputChange(data) {
      console.log(data)
      data.weight = data.weight.replace(/[^\d]/g, '')
    },

    weightInputBlur() {
      const length = this.baseForm.evaluatorsList.length
      for (let index = 0; index < length; index++) {
        this.$refs.baseForm.validateField(`evaluatorsList[${index}].weight`)
      }
    },

    /**
     * 保存点击
     */
    saveClick() {
      this.$refs.baseForm.validate(valid => {
        if (valid) {
          this.uploadCreateData()
        }
      })
    },

    /**
     * 提交数据
     */
    uploadCreateData() {
      const params = objDeepCopy(this.baseForm)

      if (this.id) {
        params.appraisalId = this.id
      }
      delete params['range']

      this.loading = true
      const request = this.id ? hrmPerformanceSetAPI : hrmPerformanceAddAPI
      request(params).then(res => {
        this.$emit('close')
        this.$message.success(`${this.titleContent}成功`)
        this.$emit('success')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }

  }
}
</script>

<style lang="scss" scoped>
::v-deep .el-form {
  .el-form-item.is-required .el-form-item__label::before {
    margin-right: 0;
  }

  .el-form-item {
    margin-bottom: 10px;

    .el-form-item__label {
      display: block;
      float: inherit;
      padding: 5px 0;
      line-height: inherit;
      color: $--color-text-primary;
      text-align: left;
    }

    .el-form-item__error {
      padding-top: 2px;
    }

    .el-form-item__content {
      line-height: 34px;
    }
  }
}

.base-info-section {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -23px;

  ::v-deep .el-form-item {
    flex: 0 0 50%;
    flex-shrink: 0;
    padding: 0 40px;
  }
}

.other-info-section {
  padding: 0 17px;
}

.user-data-row {
  padding-bottom: 10px;

  .el-select {
    width: 250px;
  }

  .wk-user-select {
    width: 200px;
  }

  .el-form-item + .el-form-item {
    margin-left: 20px;
  }

  .weight-label {
    margin-right: 8px;
    margin-bottom: 10px;
    margin-left: 20px;
  }

  .el-icon-remove {
    display: none;
    margin-bottom: 10px;
    margin-left: 8px;
    color: #ff6767;
    cursor: pointer;
  }

  &:hover {
    .el-icon-remove {
      display: inline-block;
    }
  }
}

.weight-data-row {
  padding-bottom: 10px;

  .column + .column {
    margin-left: 20px;
  }

  .range {
    margin: 0 8px 10px;
  }

  .el-input {
    width: 120px;
  }

  .el-icon-remove {
    display: none;
    margin-bottom: 10px;
    margin-left: 8px;
    color: #ff6767;
    cursor: pointer;

    &.is-first {
      margin-top: 20px;
      margin-bottom: 0;
    }
  }

  &:hover {
    .el-icon-remove {
      display: inline-block;
    }
  }
}

.wk-help-tips {
  margin-left: 3px;
}

.add-bar {
  line-height: initial;

  .el-button {
    padding-top: 0;
  }
}

.wk-text-tips {
  font-size: 12px;
  color: #ccc;
}
</style>
