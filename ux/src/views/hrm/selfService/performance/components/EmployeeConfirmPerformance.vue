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
            <template v-if="handleType == 'confirm'">
              <el-button type="primary" @click="debouncedSubmitConfirmClick(false)">同意</el-button>
              <el-button v-if="nextList && nextList.length > 1 && nextIndex < nextList.length - 1" type="primary" @click="debouncedSubmitConfirmClick(true)">同意并确认下一个</el-button>
              <el-button @click="rejectClick">驳回目标</el-button>
            </template>
            <template v-else-if="handleType == 'evaluato'">
              <el-button type="primary" @click="debouncedSubmitEvaluatoClick(false)">提交</el-button>
              <el-button v-if="nextList && nextList.length > 1 && nextIndex < nextList.length - 1" type="primary" @click="debouncedSubmitEvaluatoClick(true)">提交并确认下一个</el-button>
              <el-button @click="rejectClick('target')">驳回目标</el-button>
              <el-button v-if="rejectShow" @click="rejectClick('evaluato')">驳回评定</el-button>
            </template>
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

            <div v-if="detailAuth" class="employee">
              <div class="employee__label">
                考核人
              </div>
              <div class="employee__name">
                {{ detailData.employeeName }}
              </div>
              <flexbox class="employee__detail">
                <div class="employee-section">
                  <flexbox align="stretch" style="margin-bottom: 15px;">
                    <flexbox-item class="employee-item">
                      <div class="employee-item__label">目标确认人</div>
                      <div class="employee-item__value">{{ getEmployeeNames(detailData.confirmorsList) }}</div>
                    </flexbox-item>
                    <flexbox-item class="employee-item">
                      <div class="employee-item__label">结果评定人</div>
                      <div class="employee-item__value">{{ getEmployeeNames(detailData.evaluatorsList) }}</div>
                    </flexbox-item>
                  </flexbox>
                  <div class="employee-item">
                    <div class="employee-item__label">结果确认人</div>
                    <div class="employee-item__value">{{ getEmployeeNames(detailData.resultConfirmors) }}</div>
                  </div>
                </div>
                <wk-circle-tag
                  :label="getEmployeeLabelName()"
                  :des="detailData.status == 6 ? `${detailData.score}分` : ''"
                  :bold="detailData.status == 6"
                />

                <img
                  v-if="detailData.appraisalStatus === 4"
                  class="employee-icon"
                  width="120px"
                  src="@/assets/img/status-achieve.png">
                <img
                  v-else-if="detailData.status === 5"
                  class="employee-icon"
                  width="120px"
                  src="@/assets/img/status-stop.png">
              </flexbox>
            </div>

            <div
              v-for="(item, index) in form.fixedSegList"
              :key="`fix-${index}`"
              class="content-section">
              <div class="section__handle">
                <el-button
                  v-if="editScheduleShow"
                  :disabled="!canEditSchedule"
                  type="text"
                  @click="itemHandleClick('schedule', 'fixed', item, index)">
                  {{ (item.schedule === null || item.schedule === '') && canEditSchedule ? '添加进度' : `进度${item.schedule || 0}%` }}</el-button>
                <el-button type="text" disabled>
                  {{ `权重${item.weight || 0}%` }}
                </el-button>
              </div>
              <div
                class="item-section">
                <div class="el-row">
                  {{ item.segName }}
                </div>
                <div>
                  <div
                    v-for="(subItem, itemIndex) in item.items"
                    :key="itemIndex"
                    class="el-row"
                    style="padding-left: 140px;">
                    <div class="sub-input-value">{{ subItem.itemName }}</div>
                  </div>
                </div>
                <div>
                  <el-row v-if="evaluatoWriteShow" type="flex" class="write-score">
                    <el-col :span="8" class="write-score-item">
                      <flexbox>
                        <div class="label">评分</div>
                        <el-form-item
                          :prop="`fixedSegList[${index}].score`"
                          :rules="getScoreRules()"
                          label="">
                          <el-input
                            v-model="item.score"
                            v-wk-number
                            type="text"
                            @blur="scoreInputBlur" />
                        </el-form-item>
                      </flexbox>
                    </el-col>
                    <el-col :span="16" class="write-score-item">
                      <flexbox>
                        <div class="label">评语</div>
                        <el-form-item
                          label="">
                          <el-input
                            v-model="item.evaluate"
                            placeholder="请填写评语"
                            type="text" />
                        </el-form-item>
                      </flexbox>
                    </el-col>
                  </el-row>

                  <div
                    v-for="(evaluato, eIndex) in item.evaluatoSegList"
                    v-show="evaluato.status !== 0"
                    :key="eIndex"
                    class="history-score">
                    <el-row type="flex" class="history-score-item">
                      <el-col :span="8">
                        <span class="label">评分：</span>
                        <span class="laebl-value">{{ evaluato.score }}</span>
                      </el-col>
                      <el-col :span="8">
                        <span class="label">评定人：</span>
                        <span class="laebl-value">{{ evaluato.employeeName }}</span>
                      </el-col>
                      <el-col :span="8">
                        <span class="label">时间：</span>
                        <span class="laebl-value">{{ getYMDTime(evaluato.createTime) }}</span>
                      </el-col>
                    </el-row>
                    <flexbox class="history-score-item">
                      <div class="label">评语：</div>
                      <div class="laebl-value">{{ evaluato.evaluate }}</div>
                    </flexbox>
                  </div>
                </div>
              </div>
            </div>

            <div
              v-for="(item, index) in form.noFixedSegList"
              :key="index"
              class="section">
              <div class="section__handle">
                <el-button
                  v-if="editScheduleShow"
                  :disabled="!canEditSchedule"
                  type="text"
                  @click="itemHandleClick('schedule', 'noFixed', item, index)">
                  {{ (item.schedule === null || item.schedule === '') && canEditSchedule ? '添加进度' : `进度${item.schedule}%` }}</el-button>
                <el-button type="text" disabled>
                  {{ `权重${item.weight || 0}%` }}
                </el-button>
              </div>
              <div
                class="item-section is-no-fix">
                <el-row type="flex" class="item-row">
                  <el-col :span="23">
                    <flexbox align="stretch">
                      <el-tooltip :content="item.segName" placement="top">
                        <div class="div-label">{{ item.segName }}</div>
                      </el-tooltip>
                      <div class="div-body">{{ item.value }}</div>
                    </flexbox>
                  </el-col>
                  <el-col :span="1" class="is-close" />
                </el-row>
                <div>
                  <el-row
                    v-for="(subItem, itemIndex) in item.items"
                    :key="`${index}-${itemIndex}`"
                    type="flex"
                    class="item-row"
                  >
                    <el-col :span="23">
                      <flexbox align="stretch">
                        <div class="sub-input sub-label">{{ subItem.itemName }}</div>
                        <div class="sub-label-content div-body">{{ subItem.value }}</div>
                      </flexbox>
                    </el-col>
                    <el-col :span="1" class="is-close" />
                  </el-row>
                </div>
                <div>
                  <el-row v-if="evaluatoWriteShow" type="flex" class="write-score">
                    <el-col :span="8" class="write-score-item">
                      <flexbox>
                        <div class="label">评分</div>
                        <el-form-item
                          :prop="`noFixedSegList[${index}].score`"
                          :rules="getScoreRules()"
                          label="">
                          <el-input
                            v-model="item.score"
                            v-wk-number
                            type="text"
                            @blur="scoreInputBlur" />
                        </el-form-item>
                      </flexbox>
                    </el-col>
                    <el-col :span="16" class="write-score-item">
                      <flexbox>
                        <div class="label">评语</div>
                        <el-form-item
                          label="">
                          <el-input
                            v-model="item.evaluate"
                            placeholder="请填写评语"
                            type="text" />
                        </el-form-item>
                      </flexbox>
                    </el-col>
                  </el-row>

                  <div
                    v-for="(evaluato, eIndex) in item.evaluatoSegList"
                    v-show="evaluato.status !== 0"
                    :key="eIndex"
                    class="history-score">
                    <el-row type="flex" class="history-score-item">
                      <el-col :span="8">
                        <span class="label">评分：</span>
                        <span class="laebl-value">{{ evaluato.score }}</span>
                      </el-col>
                      <el-col :span="8">
                        <span class="label">评定人：</span>
                        <span class="laebl-value">{{ evaluato.employeeName }}</span>
                      </el-col>
                      <el-col :span="8">
                        <span class="label">时间：</span>
                        <span class="laebl-value">{{ getYMDTime(evaluato.createTime) }}</span>
                      </el-col>
                    </el-row>
                    <flexbox class="history-score-item">
                      <div class="label">评语：</div>
                      <div class="laebl-value">{{ evaluato.evaluate }}</div>
                    </flexbox>
                  </div>
                </div>
              </div>

            </div>

            <div
              v-if="evaluatoResultShow || evaluatoWriteShow"
              class="result">
              <div class="result__label">评价结果</div>
              <div v-if="evaluatoWriteShow" class="result__body">
                <flexbox>
                  <flexbox style="flex: 1;">
                    <div class="label">综合评分</div>
                    <el-form-item
                      :rules="getScoreRules()"
                      prop="score"
                      label="">
                      <el-input
                        v-model="form.score"
                        v-wk-number
                        type="text"
                        @input="debouncedQueryLevelByScore" />
                    </el-form-item>
                  </flexbox>
                  <flexbox style="flex: 1;margin-left: 50px;">
                    <div class="label">等级</div>
                    <el-form-item
                      :rules="{ required: true, message: '请选择', trigger: 'change' }"
                      prop="levelId"
                      label="">
                      <el-select v-model="form.levelId">
                        <el-option
                          v-for="item in levelOptions"
                          :key="item.levelId"
                          :label="item.levelName"
                          :value="item.levelId" />
                      </el-select>
                    </el-form-item>
                  </flexbox>
                </flexbox>
                <flexbox>
                  <div class="label">综合评语</div>
                  <el-form-item
                    prop="evaluate"
                    label="">
                    <el-input
                      v-model="form.evaluate"
                      :autosize="{ minRows: 3}"
                      :maxlength="800"
                      placeholder="您可以从如下角度进行综合自评：1、阶段时间内的主要成绩 2、阶段时间内的不足之处 3、下一阶段得从哪些方面努力"
                      type="textarea"
                      resize="none" />
                  </el-form-item>
                </flexbox>
              </div>
              <div
                v-for="(evaluato, eIndex) in form.evaluatoResultList"
                v-show="evaluato.status !== 0"
                :key="eIndex"
                class="history-score">
                <div class="history-score__title">{{ `${evaluato.employeeName}的评价结果` }}</div>
                <el-row type="flex" class="history-score-item">
                  <el-col :span="8">
                    <span class="label">综合评分：</span>
                    <span class="laebl-value">{{ evaluato.score }}</span>
                  </el-col>
                  <el-col :span="8">
                    <span class="label">等级：</span>
                    <span class="laebl-value">{{ evaluato.levelName }}</span>
                  </el-col>
                  <el-col :span="8">
                    <span class="label">时间：</span>
                    <span class="laebl-value">{{ getYMDTime(evaluato.createTime) }}</span>
                  </el-col>
                </el-row>
                <flexbox class="history-score-item">
                  <div class="label">评语：</div>
                  <div class="laebl-value">{{ evaluato.evaluate }}</div>
                </flexbox>
              </div>
            </div>
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

      <reject-dialog
        :id="id"
        :handle-type="rejectData.type"
        :des="rejectData.des"
        :visible.sync="rejectDialogShow"
        @change="rejectChange"
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
  hrmPerformanceEmployeeTargetConfirmAPI,
  // 结果评定
  hrmPerformanceEmployeeResultEvaluatoAPI,
  hrmPerformanceEmployeeQueryLevelListAPI
} from '@/api/hrm/selfService/performance'

import PerformanceHistory from './PerformanceHistory'
import ScheduleEditDialog from './ScheduleEditDialog'
import RejectDialog from './RejectDialog'
import WkCircleTag from '@/views/hrm/performance/employee/components/WkCircleTag'
import Reminder from '@/components/Reminder'
import performanceModel from '../model/performance'
import { floatAdd } from '@/utils'

import { getMaxIndex, objDeepCopy, timeToFormatTime } from '@/utils'
import { mapGetters } from 'vuex'
import { debounce } from 'throttle-debounce'

export default {
  name: 'EmployeeConfirmPerformance',
  components: {
    PerformanceHistory,
    ScheduleEditDialog,
    RejectDialog,
    Reminder,
    WkCircleTag
  },
  props: {
    nextList: Array,
    nextIndex: Number,
    handleType: String, // schedule（结果待评定 可修改状态 移到edit vue页面） evaluato 评定页面 evaluato-view 预览评定后的页面
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      zIndex: getMaxIndex(),
      detailData: null,
      form: null,
      rejectData: {
        type: '',
        des: ''
      },
      rejectDialogShow: false,
      scheduleEditShow: false,
      dialogEditData: {},
      // 等级数据
      levelOptions: []
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 详情权限
    detailAuth() {
      return (this.appraisalAuth && this.appraisalAuth.read) ||
      // 归档 或者 作废 完成状态下展示
      (this.detailData && (this.detailData.appraisalStatus === 4 || this.detailData.status === 5))
    },

    title() {
      // if (this.detailData) {
      //   return this.detailData.employeeName
      // }
      return '绩效目标'
    },

    // 评语布局展示
    evaluatoWriteShow() {
      return this.handleType == 'evaluato' && !this.isDisableHandle
    },

    isDisableHandle() {
      // 归档或者停用 禁止所有操作
      return this.detailData.appraisalStatus === 4 || this.detailData.status === 5
    },

    // 展示结果
    evaluatoResultShow() {
      if (this.detailData) {
        return (this.detailData.status == 3 || this.detailData.status == 4 || this.detailData.status == 6 || this.detailData.status == 5) && this.detailData.evaluatoResultList && this.detailData.evaluatoResultList.length > 0
      }
      return false
      // return this.handleType == 'evaluato' || this.handleType == 'evaluato-view'
    },

    // 开启进度编辑
    editScheduleShow() {
      // 1 目标待填写 2 目标待确认 到评定之后都能看到进度
      return this.detailData && this.detailData.status != 1 && this.detailData.status != 2
    },

    finalResultShow() {
      // 6: '考核完成' 最终结果展示
      return this.detailData && this.detailData.status == 6
    },

    // 只有员工本人编辑
    canEditSchedule() {
      // 绩效状态 appraisalStatus 0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档
      return this.handleType == 'schedule' && this.detailData && this.detailData.status == 3 && (this.detailData.appraisalStatus == 1 || this.detailData.appraisalStatus == 2)
    },

    // 驳回展示
    rejectShow() {
      if (this.detailData && this.detailData.evaluatoResultList && this.detailData.evaluatoResultList.length > 0) {
        const evaluatoResultList = this.detailData.evaluatoResultList.filter(item => item.status === 1) // 1 已评定
        return evaluatoResultList.length > 0
      }

      return false
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
  mounted() {
    this.debouncedQueryLevelByScore = debounce(500, this.queryLevelByScore)
    this.debouncedSubmitConfirmClick = debounce(300, this.submitConfirmClick)
    this.debouncedSubmitEvaluatoClick = debounce(300, this.submitEvaluatoClick)
  },
  methods: {
    /**
     * 详情
     */
    getDetail() {
      // 评定操作获取
      if (this.handleType == 'evaluato') {
        this.getLevelList()
      }
      this.loading = true
      hrmPerformanceEmployeeAppraisalDetailAPI(this.id)
        .then(res => {
          const data = res.data || {}
          this.detailData = objDeepCopy(data)
          const form = {}
          const tableTemp = data.tableTemp
          form.description = tableTemp.description
          form.isEmpWeight = tableTemp.isEmpWeight
          const fixedSegList = data.fixedSegList || []
          fixedSegList.forEach(item => {
            // 如果有驳回信息，填充到当前考核项里
            const evaluatoItem = item.evaluatoSegList.find(item => item.status === 0)
            if (evaluatoItem) {
              item.score = evaluatoItem.score
              item.evaluate = evaluatoItem.evaluate
            }
          })
          form.fixedSegList = fixedSegList
          const noFixedSegList = data.noFixedSegList || []
          noFixedSegList.forEach(item => {
            const evaluatoItem = item.evaluatoSegList.find(item => item.status === 0)
            if (evaluatoItem) {
              item.score = evaluatoItem.score
              item.evaluate = evaluatoItem.evaluate
            }
          })
          form.noFixedSegList = noFixedSegList || []

          const evaluatoResultList = data.evaluatoResultList || []
          const resultItem = evaluatoResultList.find(item => item.status === 0)
          if (resultItem) {
            form.score = resultItem.score
            form.levelId = resultItem.levelId
            form.evaluate = resultItem.evaluate
          }
          form.evaluatoResultList = evaluatoResultList
          this.form = form
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.closeClick()
        })
    },

    /**
     * 获取层级信息
     */
    getLevelList() {
      hrmPerformanceEmployeeQueryLevelListAPI(this.id)
        .then(res => {
          this.levelOptions = res.data || []
        })
        .catch(() => {
        })
    },

    /**
     * 修改进度（待评定状态）
     */
    itemHandleClick(type, dataType, item, index) {
      this.dialogEditData = {
        type, dataType, item, index
      }
      this.scheduleEditShow = true
    },

    scheduleChange(weight) {
      if (this.dialogEditData.type == 'schedule') {
        this.dialogEditData.item.schedule = weight
        this.$refs.performanceHistory.getDetail()
      }
    },

    submitConfirmClick(isNext = false) {
      this.$confirm('您确定同意该员工的考核目标吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // status 状态 1 提交 2 驳回目标
          this.loading = true
          hrmPerformanceEmployeeTargetConfirmAPI({
            employeeAppraisalId: this.id,
            status: 1
          })
            .then(res => {
              if (isNext) {
                if (this.nextIndex + 1 <= this.nextList.length - 1) {
                  this.$emit('nextChange', this.nextIndex + 1)
                  this.$message.success('操作成功')
                } else {
                  this.$message.success('操作成功，没有更多考核了')
                  this.closeClick()
                }
              } else {
                this.$message.success('操作成功')
                this.closeClick()
              }
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {})
    },

    /**
     * 驳回操作
     */
    rejectClick(type) {
      if (this.handleType == 'evaluato') {
        // 驳回目标
        this.rejectData.type = type
        if (type == 'target') {
          this.rejectData.des = ''
        } else if (type == 'evaluato') {
          // 驳回评定
          this.rejectData.des = '您将驳回上一评定人的评定结果，确定驳回吗？'
        }
      }
      this.rejectDialogShow = true
    },

    rejectChange() {
      this.closeClick()
    },

    /**
     * 点评
     */
    submitEvaluatoClick(isNext) {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true
          const params = {
            employeeAppraisalId: this.id,
            levelId: this.form.levelId,
            score: this.form.score,
            evaluate: this.form.evaluate,
            status: 1
          }

          const fixedSegList = this.form.fixedSegList.map(item => {
            return {
              evaluate: item.evaluate,
              score: item.score,
              segId: item.segId
            }
          })

          const noFixedSegList = this.form.noFixedSegList.map(item => {
            return {
              evaluate: item.evaluate,
              score: item.score,
              segId: item.segId
            }
          })

          params.resultEvaluatoSegBOList = fixedSegList.concat(noFixedSegList)

          hrmPerformanceEmployeeResultEvaluatoAPI(params)
            .then(res => {
              if (isNext) {
                if (this.nextIndex + 1 <= this.nextList.length - 1) {
                  this.$emit('nextChange', this.nextIndex + 1)
                  this.$message.success('操作成功')
                } else {
                  this.$message.success('操作成功，没有更多考核了')
                  this.closeClick()
                }
              } else {
                this.$message.success('操作成功')
                this.closeClick()
              }
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          this.$message.error('请完善信息')
          return false
        }
      })
    },

    /**
     * 获取年月日时间
     */
    getYMDTime(time) {
      return timeToFormatTime(time)
    },

    /**
     * 获取分数规则
     */
    getScoreRules() {
      const validateWeight = (rule, value, callback) => {
        if (!this.detailData) {
          callback()
        } else if (value < 0 || value > parseInt(this.detailData.fullScore) || value === null || value === undefined || value === '') {
          callback(new Error(`范围在0-${this.detailData.fullScore}之间`))
        } else {
          callback()
        }
      }
      return { validator: validateWeight, trigger: ['blur', 'change'] }
    },

    /**
     * 圈主展示内容
     */
    getEmployeeLabelName() {
      if (this.detailData.status == 6) {
        return this.detailData.levelName
      }

      return performanceModel.statusValue[this.detailData.status]
    },

    /**
     * 获取人员姓名
     */
    getEmployeeNames(list) {
      return list.map(item => item.employeeName).join('，')
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('change')
      this.$emit('close')
    },

    /**
     * 得分输入
     */
    scoreInputBlur() {
      const fixedSegList = this.form.fixedSegList
      const noFixedSegList = this.form.noFixedSegList
      let score = 0
      fixedSegList.forEach(item => {
        score = floatAdd(score, ((item.score || 0) * (item.weight || 0) / 100).toFixed(2))
      })

      noFixedSegList.forEach(item => {
        score = floatAdd(score, ((item.score || 0) * (item.weight || 0) / 100).toFixed(2))
      })

      this.$set(this.form, 'score', score)
      this.debouncedQueryLevelByScore()
    },

    /**
     * 评分获取等级
     */
    queryLevelByScore() {
      const levelItem = this.levelOptions.find(item => {
        return item.minScore <= this.form.score && this.form.score <= item.maxScore
      })
      if (levelItem) {
        this.$set(this.form, 'levelId', levelItem.levelId)
      }
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
    padding-bottom: 100px;
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
    color: $--color-text-secondary;
    background-color: white;
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

  .item-section.is-no-fix {
    background-color: white;
  }
}

// 写评论
.write-score {
  height: inherit !important;
  padding-top: 10px;
  margin-top: 15px;
  margin-bottom: 8px;
  line-height: inherit !important;
  border-top: 1px solid #e6e6e6;

  &-item {
    padding: 0 5px;

    .label {
      margin-right: 5px;
      font-size: 13px;
      color: $--color-text-secondary;
    }

    .el-form-item {
      flex: 1;
      margin-bottom: 0;
    }

    .laebl-value {
      word-break: break-all;
      word-wrap: break-word;
      white-space: pre-wrap;
    }
  }
}

// 结果
.result {
  padding: 15px;
  margin-top: 15px;
  background-color: #f5f5f5;
  border: 1px solid #e6e6e6;
  border-radius: 4px;

  &__label {
    font-weight: bold;
  }

  &__body {
    padding: 10px 0;

    .label {
      margin-right: 5px;
      margin-bottom: 22px;
      font-size: 13px;
    }

    .el-form-item {
      flex: 1;
    }

    .laebl-value {
      word-break: break-all;
      word-wrap: break-word;
      white-space: pre-wrap;
    }
  }
}

// 历史评论
.history-score {
  margin-top: 15px;
  border-top: 1px solid #e6e6e6;

  .el-row {
    height: 34px;
    line-height: 34px;
  }

  &__title {
    padding: 15px 5px 0;
    font-size: 13px;
  }

  &-item {
    padding: 0 5px;
    font-size: 12px;

    .label {
      flex-shrink: 0;
      margin-right: 5px;
      color: $--color-text-regular;
    }

    .laebl-value {
      flex: 1;
      color: $--color-text-primary;
      word-break: break-all;
      word-wrap: break-word;
      white-space: pre-wrap;
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
      margin-bottom: 15px;
      line-height: 1.5;

      .div-label {
        width: 140px;
        padding-right: 15px;
        color: $--color-text-secondary;
      }

      .div-body {
        flex: 1;
      }

      .sub-label {
        position: relative;
        max-width: 120px;
        margin-left: 140px;
        color: $--color-text-secondary;
      }

      .sub-label-content {
        padding-left: 15px;
      }
    }

    .sub-input::before {
      position: absolute;
      top: 0;
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
    }

    .sub-input-right {
      ::v-deep .el-input__inner {
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

.employee {
  margin-top: 40px;

  &__label {
    font-size: 12px;
    color: $--color-text-regular;
  }

  &__name {
    margin-top: 8px;
    font-size: 24px;
    font-weight: bold;
  }

  &__detail {
    position: relative;
    padding: 15px;
    margin-top: 15px;
    border: 1px solid #e6e6e6;
    border-radius: 4px;
  }

  &-section {
    flex: 1;
    margin-right: 40px;
  }

  &-item {
    &__label {
      font-size: 12px;
    }

    &__value {
      margin-top: 8px;
      line-height: 20px;
      color: $--color-text-secondary;
      word-break: break-all;
      word-wrap: break-word;
      white-space: pre-wrap;
    }
  }

  &-icon {
    position: absolute;
    right: 60px;
    bottom: 0;
    opacity: 0.6;
  }

  .wk-circle-tag {
    flex-shrink: 0;
  }
}
</style>
