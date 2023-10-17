<template>
  <slide-view
    v-loading="loading"
    class="d-view"
    :body-style="{ padding: '32px 32px 0', height: '100%' }"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    @close="hideView">
    <div class="d-header">
      <div class="header-user">
        <el-avatar />
        <div class="user-info">
          <span class="user-info-label">姓名</span>
          <span class="user-info-name">{{ appraisalPlanName }}</span>
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
        <!-- <el-tabs
          v-model="tabCurrentName"
          nav-mode="more">
          <el-tab-pane
            label="考核评分"
            name="ScoreAndDetial"
            lazy>
            <component
              :is="'ScoreAndDetial'"
              :id="id"
              ref="childForm"
              :sub-tab-type="subTabType"
              :active-tab="activeTab"
              @close="success" />
          </el-tab-pane>
        </el-tabs> -->
        <!-- <component
          :is="'ScoreAndDetial'"
          :id="id"
          ref="childForm"
          :sub-tab-type="subTabType"
          :active-tab="activeTab"
          @close="success" /> -->
        <score-and-detial
          :id="id"
          ref="childForm"
          :sub-tab-type="subTabType"
          :active-tab="activeTab"
          :is-manage="true"
          @close="success" />
      </div>
    </div>
  </slide-view>
</template>

<script>
import {
  queryStageListAPI,
  queryEmployeeAppraisalBaseInfo
} from '@/api/hrm/selfService/performance'

import ScoreAndDetial from '@/views/hrm/components/EmployeeDetails/components/ScoreAndDetial' // 填写指标
import SlideView from '@/components/SlideView'
export default {
  name: 'EmployeeDetails',
  components: {
    SlideView,
    ScoreAndDetial
  },
  provide: {
    rootTabs: {
      currentName: 'ScoreAndDetial'
    }
  },
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

      tabCurrentName: 'ScoreAndDetial',

      // 员工填写考核维度
      assessmentDimensionList: [],
      quotaItem: {}
    }
  },
  computed: {
    btnHandler() {
      const arr = []
      if (this.activeTab == 1) {
        arr.push({ label: '提交', type: 'primary' })
      }
      // else if (this.activeTab == )
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
     * 保存
     */
    handlerSubmit() {
      console.log(this.$refs.childForm, 'hhhhh ')
      this.$refs.childForm[0].sendClick()
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

    .header-user {
      display: flex;
      align-items: center;
      margin-top: 32px;

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
