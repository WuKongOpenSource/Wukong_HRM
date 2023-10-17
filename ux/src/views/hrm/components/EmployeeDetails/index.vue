<template>
  <slide-view
    v-loading="loading"
    class="d-view"
    :body-style="{ padding: '32px 32px 0', height: '100%' }"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    @close="hideView">
    <div class="d-header">
      <div class="header-title">
        <span class="title">{{ appraisalPlanName }}</span>
        <div
          v-if="subTabType == 2"
          class="operation">
          <el-button
            v-for="(item,index) in btnHandler"
            :key="index"
            :type="item.type"
            @click="handlerSubmit(item.handle)">{{ item.label }}</el-button>
        </div>
      </div>
      <div class="header-user">
        <div class="header-user-ft">
          <xr-avatar
            :name="inspectionPeople"
            :size="40" />
          <div class="user-info">
            <span class="user-info-label">姓名</span>
            <span class="user-info-name">{{ inspectionPeople }}</span>
          </div>
        </div>
        <div v-if="assessmentScore" class="header-user-r">
          <div class="header-user-rt">
            <span style="font-size: 14px; color: #6b778c;">考核分数</span>
            <span style="font-size: 24px; color: #36b37e;">{{ assessmentScore }}分</span>
          </div>
          <div v-if="level" class="header-user-rt">
            <span style="font-size: 14px; color: #6b778c;">评级</span>
            <span style="font-size: 24px; color: #36b37e;">{{ level }}</span>
          </div>
        </div>
      </div>
    </div>
    <div style="height: calc(100% - 130px);padding-right: 20px;overflow-y: auto;">
      <div class="desc">
        <div class="desc-header">
          <div
            v-for="(item, index) in headerList"
            :key="index"
            class="desc-header-item">
            <span>{{ item.label }}</span>
            <span>{{ item.value }}</span>
          </div>
        </div>
        <el-divider />
        <div class="desc-target">
          <el-steps :active="currentActive" :space="150">
            <el-step
              v-for="(step, sIndex) in activities"
              :key="sIndex">
              <template slot="title">
                <div style="display: flex;flex-direction: column;align-items: center;">
                  <span style="color: #42526e;">{{ step.stageName }}</span>
                  <span v-if="step.stageName" style="font-weight: 700;color: #172b4d;">{{ step.stageUserName }}</span>
                  <el-tag v-else type="info" style="width: 60px;margin-top: 10px;">{{ step.label == '结果确认' ? '待审核' : '待确认' }}</el-tag>
                </div>
              </template>
            </el-step>
          </el-steps>
        </div>
      </div>
      <div class="d-container-body">
        <el-tabs
          v-model="tabCurrentName"
          nav-mode="more">
          <el-tab-pane
            v-for="(item, index) in tabNames"
            :key="index"
            :label="item.label"
            :name="item.name"
            lazy>
            <component
              :is="item.name"
              :id="id"
              ref="childForm"
              :sub-tab-type="subTabType"
              :active-tab="activeTab"
              @rejectAuthority="rejectAuthority"
              @realTime="realTime"
              @close="success" />
          </el-tab-pane>
        </el-tabs>
      </div>

      <el-dialog
        v-if="disallowanceShow"
        :title="activeTab == 6 ? '申诉' : '驳回'"
        :visible.sync="disallowanceShow"
        :modal-append-to-body="false"
        :append-to-body="true"
        :close-on-click-modal="false"
        width="50%">
        <el-form
          ref="disallowanceForm"
          :model="disallowanceForm"
          label-position="top"
          style="padding: 0 40% 0 0;">
          <el-form-item
            v-for="(item,index) in disallowanceFieldList"
            :key="index"
            :label="item.name"
            :prop="item.fieldName"
            :rules="item.rules || {}">
            <el-input
              v-if="item.fieldName == 'rejectReason' || item.fieldName == 'reason'"
              v-model="disallowanceForm[item.fieldName]"
              type="textarea"
              :rows="2"
              resize="none" />
            <el-select
              v-if="item.fieldName == 'appraisalStageIdList'"
              v-model="disallowanceForm[item.fieldName]"
              multiple>
              <el-option
                v-for="sItem in rejectUserList"
                :key="sItem.stageId"
                :label="sItem.stageUserName"
                :value="sItem.stageId" />
            </el-select>
            <xh-files
              v-if="item.fieldName == 'batchId'"
              :value="batch"
              :props="{multiple:false}"
              @value-change="oldChange($event, item, index)" />
            <!-- <div>

            </div> -->
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="disallowanceShow = false">取 消</el-button>
          <el-button type="primary" @click="handlerReject">确 定</el-button>
        </span>
      </el-dialog>

    </div>
  </slide-view>
</template>

<script>
import {
  queryStageListAPI,
  queryEmployeeAppraisalBaseInfo,
  targetConfirmationReject,
  rejectScore,
  resultAuditReject,
  queryScoringPoint,
  resultAppeal,
  resultAppealReject
} from '@/api/hrm/selfService/performance'

import FillInIndicator from './components/FillInIndicator' // 填写指标
import ScoreAndDetial from './components/ScoreAndDetial' // 填写评分以及详情展示
import InspectionRecords from './components/InspectionRecords' // 考核记录
import SlideView from '@/components/SlideView'
import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import WkTextarea from '@/components/NewCom/WkTextarea'
import { XhFiles } from '@/components/CreateCom'

import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
export default {
  name: 'EmployeeDetails',
  components: {
    SlideView,
    FillInIndicator,
    ScoreAndDetial,
    InspectionRecords,
    WkFormItems,
    WkTextarea,
    XhFiles
  },
  mixins: [GenerateRulesMixin],
  props: {
    id: String,

    // 需要显示的操作以及tab展示
    activeTab: [String, Number], // 显示场景
    subTabType: [String, Number], // 待填写/已填写

    appraisalPlanName: String // 绩效名称
  },
  data() {
    return {
      loading: false,
      headerList: [
        { label: '考核计划', value: '', prop: 'appraisalPlanName' },
        { label: '考核周期', value: '', prop: 'appraisalCycleType' },
        { label: '部门', value: '', prop: 'deptName' },
        { label: '岗位', value: '', prop: 'post' },
        { label: '聘用形式', value: '', prop: 'employmentForms' }
      ],
      appraisalCycleType: {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年'
      },
      activities: [], // 阶段展示
      currentActive: null, // 当前阶段

      assessmentScore: '', // 考核分数
      inspectionPeople: '', // 考核人
      level: '', // 评级

      // 员工填写考核维度
      assessmentDimensionList: [],
      quotaItem: {},

      // 驳回
      targetRejectAuth: true, // 指标确认驳回权限
      disallowanceShow: false,
      disallowanceForm: {},
      isRejectAuthority: false,
      rejectUserList: [], // 驳回节点人员
      batch: [] // 结果申诉附件
    }
  },
  computed: {
    tabNames() {
      var tempsTabs = [
        { label: '考核评分', name: 'FillInIndicator' },
        { label: '考核评分', name: 'ScoreAndDetial' },
        { label: '考核记录', name: 'InspectionRecords' }
      ]
      /**
       *  this.activeTab 当前场景
       *  1.指标填写 2.指标确认 4.指标评分 5.结果审核 6.结果确认 7.申诉确认
       *  this.subTabType 状态
       *  2.未处理 1.已处理
       *  */
      if (this.activeTab == 1 && this.subTabType == 2) {
        tempsTabs.splice(1, 2)
      } else if (this.activeTab == 1 && this.subTabType == 1) {
        tempsTabs.splice(0, 1)
      } else if (this.activeTab == 2 && this.subTabType == 2) {
        tempsTabs.splice(0, 1)
        tempsTabs.splice(1, 1)
      } else if ((this.activeTab == 2 || this.activeTab == 4) && this.subTabType == 1) {
        tempsTabs.splice(0, 1)
      } else if (this.activeTab == 4 && this.subTabType == 2) {
        tempsTabs.splice(2, 1)
        tempsTabs.splice(0, 1)
      } else if ((this.activeTab == 5 || this.activeTab == 6 || this.activeTab == 7) && (this.subTabType == 2 || this.subTabType == 1 || this.subTabType == 5)) {
        tempsTabs.splice(0, 1)
      }

      return tempsTabs
    },

    tabCurrentName() {
      return this.tabNames[0].name
    },

    btnHandler() {
      var label = this.activeTab == 5 ? '审核' : '提交'
      const arr = [{ label: label, type: 'primary', handle: 'submit' }]
      if (this.activeTab == 2 && this.targetRejectAuth) {
        arr.push({ label: '驳回', type: '', handle: 'reject' })
      } else if (this.activeTab == 4 && this.isRejectAuthority) {
        arr.push({ label: '驳回', type: '', handle: 'reject' })
      } else if (this.activeTab == 6) {
        arr.push({ label: '申诉', type: '', handle: 'reject' })
      } else if (this.activeTab == 5 || this.activeTab == 7) {
        arr.push({ label: '驳回', type: '', handle: 'reject' })
      }

      return arr
    },

    disallowanceFieldList() {
      var arr = []
      if (this.activeTab == 2 || this.activeTab == 4) {
        arr = [
          { name: '驳回原因', fieldName: 'rejectReason', rules: { required: true, message: '驳回原因不能为空', trigger: 'blur' }}
        ]
      } else if (this.activeTab == 5) {
        this.getRejectUserList()
        arr = [
          { name: '驳回原因', fieldName: 'reason', rules: { required: true, message: '驳回原因不能为空', trigger: 'blur' }},
          { name: '评分节点', fieldName: 'appraisalStageIdList', rules: { required: true, message: '请选择申诉节点', trigger: 'change' }}
        ]
      } else if (this.activeTab == 6) {
        this.getRejectUserList()
        arr = [
          { name: '申诉原因', fieldName: 'reason', rules: { required: true, message: '驳回原因不能为空', trigger: 'blur' }},
          { name: '评分节点', fieldName: 'appraisalStageIdList', rules: { required: true, message: '请选择申诉节点', trigger: 'change' }},
          { name: '驳回说明', fieldName: 'batchId' }
        ]
      } else if (this.activeTab == 7) {
        arr = [
          { name: '驳回原因', fieldName: 'reason', rules: { required: true, message: '驳回原因不能为空', trigger: 'blur' }}
        ]
      }

      return arr
    }
  },
  created() {
    this.getDetialHeader()
  },
  methods: {
    /**
     * 获取头部数据
     */
    getDetialHeader() {
      queryStageListAPI({ id: this.id })
        .then(res => {
          this.activities = res.data || []
        })
        .catch(() => {
          this.hideView()
        })
      queryEmployeeAppraisalBaseInfo({ id: this.id })
        .then(res => {
          const data = res.data || {}
          this.currentActive = data.stageSort
          this.inspectionPeople = data.employeeName
          this.assessmentScore = data.score
          this.level = data.level
          this.targetRejectAuth = data.canRejectTarget
          this.headerList.forEach(item => {
            for (var key in data) {
              if (item.prop == key) {
                if (item.prop == 'appraisalCycleType') {
                  item.value = this.appraisalCycleType[data[key]] ? this.appraisalCycleType[data[key]] : data[key]
                } else if (item.prop == 'employmentForms') {
                  item.value = data[key] == 1 ? '正式' : '非正式'
                } else {
                  item.value = data[key]
                }
              }
            }
          })
        })
    },

    /**
     * 保存/驳回
     */
    handlerSubmit(type) {
      if (type == 'submit') {
        this.$refs.childForm[0].sendClick()
      } else if (type == 'reject') {
        this.disallowanceShow = true
        this.disallowanceFieldList.forEach(item => {
          this.$set(this.disallowanceForm, item.fieldName, '')
        })
        this.batch = []
        // 清除验证
        this.$nextTick(() => {
          this.$refs.disallowanceForm.clearValidate()
        })
      }
    },

    /**
     * 驳回
     */
    handlerReject() {
      var request
      if (this.activeTab == 2) {
        request = targetConfirmationReject
      } else if (this.activeTab == 4) {
        request = rejectScore
      } else if (this.activeTab == 5) {
        request = resultAuditReject
      } else if (this.activeTab == 6) {
        request = resultAppeal
        this.disallowanceForm['batchId'] = this.batch.length ? this.batch[0].batchId : ''
      } else if (this.activeTab == 7) {
        request = resultAppealReject
      }
      this.$refs.disallowanceForm.validate(valid => {
        if (valid) {
          this.disallowanceForm['appraisalEmployeeId'] = this.id
          console.log(this.disallowanceForm, 'saisdhf ')
          request(this.disallowanceForm)
            .then(res => {
              this.$message.success('操作成功')
              this.success()
            })
        } else {
          this.$message.error('请完善基本信息')
        }
      })
    },

    /**
     * 判断是否可以驳回
     */
    rejectAuthority(data) {
      this.isRejectAuthority = data
    },

    /**
     * 获取驳回节点人员
     */
    getRejectUserList() {
      const appraisalEmployeeId = this.activities[this.currentActive - 1].appraisalEmployeeId
      queryScoringPoint({ id: appraisalEmployeeId })
        .then(res => {
          this.rejectUserList = res.data
        })
    },

    oldChange($event, item, index) {
      console.log($event, '哈哈哈哈哈')
      if ($event.value.length > 1) {
        this.batch.splice(0, 1)
      }
    },

    /**
     * 实时评分
     */
    realTime(data) {
      this.assessmentScore = data.assessmentScore
      this.level = data.level
    },

    /**
     * 关闭
     */
    hideView() {
      this.$emit('hide-view')
    },

    /**
     * 保存成功
     */
    success() {
      this.hideView()
      this.$emit('success')
    }
  }
}
</script>

<style lang="scss" scoped>
.d-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1400px;
  background-color: #fff;

  .d-header {
    margin-bottom: 10px;

    .header-title {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .title {
        font-size: 24px;
        font-weight: 700;
        text-overflow: ellipsis;
      }
    }

    .header-user {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 32px;

      .header-user-ft {
        display: flex;
        flex-grow: 1;

        // justify-content: space-between;
        width: 110px;

        .user-info {
          display: flex;
          flex-direction: column;
          margin-left: 16px;

          .user-info-label {
            font-size: 14px;
            color: #6b778c;
          }

          .user-info-name {
            font-size: 20px;
            font-weight: 700;
          }
        }
      }

      .header-user-r {
        display: flex;

        .header-user-rt {
          display: flex;
          flex-direction: column;

          &:nth-of-type(2) {
            margin-left: 20px;
          }
        }
      }
    }
  }

  .desc {
    padding: 16px;
    margin-top: 16px;
    background-color: #f4f5f7;

    .desc-header {
      display: flex;
      justify-content: space-between;

      .desc-header-item {
        display: flex;
        flex-direction: column;

        span {
          &:nth-of-type(1) {
            font-size: 14px;
            color: #6b778c;
          }

          &:nth-of-type(2) {
            margin-top: 8px;
            font-size: 14px;
            color: #172b4d;
          }
        }
      }
    }

    .desc-target {
      overflow-x: auto;
      overflow-y: hidden;

      .circle {
        width: 10px;
        height: 10px;
        border: 2px solid #e0edff;
        border-radius: 50%;
      }

      ::v-deep .el-steps {
        padding-left: 30px;
        margin-top: 15px;

        .el-step__head.is-process {
          color: #7a869a;
          border-color: #7a869a;
        }

        .el-step__head.is-success {
          color: $--color-primary;
          border-color: $--color-primary;

          .el-step__line {
            background-color: $--color-primary;
          }
        }

        .el-step.is-horizontal {
          span {
            white-space: nowrap;
          }
        }

        .el-step__title {
          margin-left: 10px;
          font-size: 12px;
          text-align: center;
          transform: translateX(-50%);
        }

        .el-step__title.is-process {
          font-weight: normal;
          color: $--color-text-primary;
        }

        .el-step__title.is-finish,
        .el-step__title.is-success {
          color: $--color-text-primary;
        }

        .el-step__line {
          top: 7px;
          height: 2px;
          overflow: hidden;
        }

        .el-step__icon {
          width: 10px;
          height: 10px;
        }

        div[class="el-step__icon-inner"] {
          display: none;
        }
      }
    }
  }
}
</style>
