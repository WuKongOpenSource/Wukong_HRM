<template>
  <slide-view
    v-empty="!canShowDetail"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%'}"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <div
      ref="crmDetailMain"
      v-loading="loading"
      class="detail-main">
      <flexbox
        v-if="canShowDetail"
        direction="column"
        align="stretch"
        class="d-container">

        <detail-header
          type-name="打卡记录"
          :detail-data="detailData"
          :fields="headerFields"
          :label="detailData.employeeName"
          :icon="`el-icon-user-solid`"
        />

        <flexbox
          class="d-container-body"
          align="stretch">
          <el-tabs
            value="attendance_details"
            nav-mode="more"
            class="top-padding"
          >
            <el-tab-pane
              label="考勤详情"
              name="attendance_details"
              lazy>
              <attendance-detail
                :detail-data="detailData"
                :date-list="dateList"
                @tabChange="getDetail" />
            </el-tab-pane>
          </el-tabs>
        </flexbox>
      </flexbox>
    </div>
  </slide-view>
</template>

<script>
import { hrmQueryAttendanceEmpMonthDailyDetailAPI } from '@/api/hrm/clock'
import Detail from './mixins/Detail'
import SlideView from '@/components/SlideView'
import DetailHeader from './components/DetailHeader'
import AttendanceDetail from './components/AttendanceDetail'

export default {
  components: {
    SlideView,
    DetailHeader,
    AttendanceDetail
  },
  mixins: [Detail],
  props: {
    // 详情信息id
    id: [String, Number],
    times: Array,
    // 监听的dom 进行隐藏详情
    listenerIDs: {
      type: Array,
      default: () => {
        return ['crm-main-container']
      }
    },
    // 不监听
    noListenerIDs: {
      type: Array,
      default: () => {
        return []
      }
    },
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body']
      }
    },
    visible: Boolean,
    detailData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      // 展示加载loading
      loading: false,
      headerFields: [
        { fieldName: '工号', field: 'jobNumber' },
        { fieldName: '部门', field: 'deptName' },
        // { fieldName: '岗位', field: 'post' },
        { fieldName: '入职日期', field: 'entryTime' },
        { fieldName: '员工状态', field: 'status',
          valueFormat({ field, value }) {
            return { 1: '正式', 2: '试用', 3: '实习', 4: '兼职',
              5: '劳务', 6: '顾问', 7: '返聘', 8: '外包',
              9: '待离职', 10: '已离职', 11: '在职', 12: '全职' }[value]
          } },
        { fieldName: '工作城市', field: 'workCity' }
        // { fieldName: '工龄', field: 'conpanyAge' }
      ],
      dateList: []
    }
  },
  watch: {},
  mounted() {},
  methods: {
    /**
     * 详情
     */
    getDetail(status = -1) {
      this.loading = true
      const params = {
        employeeId: this.id,
        times: this.times,
        status,
        includeToday: 0
      }
      hrmQueryAttendanceEmpMonthDailyDetailAPI(params).then(res => {
        this.loading = false
        const resData = res.data || {}
        this.dateList = resData.dateList || []
      })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    /**
     * 关闭
     */
    hideView() {
      this.$emit('hide-view')
    }
  }
}
</script>

<style lang="scss" scoped>
.slide-fade-leave-active {
  will-change: transform;
  transition: all 0.1s;
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.wk-circle-password {
  padding: 2px;
  margin-left: 5px;
  font-size: 12px;
  color: white;
  background-color: #f56c6c;
  border-radius: 3px;
  transform: scale(0.6);
}

.el-tab-pane {
  overflow: auto !important;
}

.d-container {
  position: relative;
  height: 100%;
  overflow-y: auto;
}

.detail-main {
  position: relative;
  height: 100%;
  overflow: hidden;
  background-color: $--color-white;

  &.no-padding {
    padding: 0;
  }
}

// 详情
.d-container-body {
  flex: 1;
  height: 0;
  padding: 0 32px;
  overflow: scroll;

  .top-padding {
    width: 100%;
    padding-top: 16px;
  }

  .left-right-wrap {
    margin-top: #{$--interval-base * 2};

    &.is-hidden-right {
      >.right {
        display: none;
      }

      >.left {
        padding-right: 0;
      }
    }

    > .left {
      flex: 1;
      padding-right: 40px;
      overflow: hidden;
    }

    > .right {
      flex-shrink: 0;
      width: 280px;
    }
  }

  ::v-deep .el-tabs__content {
    position: relative;
    padding: 0;
    overflow: hidden;

    .el-tab-pane {
      overflow: hidden;
    }
  }

  ::v-deep .el-badge {
    .el-badge__content {
      top: 50%;
      right: -4px;
    }
  }
}
</style>
