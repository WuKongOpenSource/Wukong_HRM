<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="getDetail"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header>
        <div slot="body" class="employee-header">
          <div class="employee-header__top">
            <span class="name">{{ detailData.employeeName }}</span>
            <span class="post">{{ detailData.post }}</span>
          </div>
          <div class="employee-header__bottom">
            <el-tag
              v-if="detailData.sex === 1 || detailData.sex ===2"
              :class="{
                1: 'is-man',
                2: 'is-woman',
              }[detailData.sex]"
              disable-transitions
              class="xr-sex-tag"
              size="mini">
              <i
                :class="{
                  1: 'wk wk-man',
                  2: 'wk wk-woman',
                }[detailData.sex]" />
            </el-tag>

            <el-tooltip
              :content="`工号：${detailData.jobNumber|| '--'}`"
              effect="dark"
              placement="top">
              <el-tag
                disable-transitions
                class="xr-tag"
                color="#ECEEF2"
                size="mini">
                <i class="wk wk-icon-work-card" />{{ detailData.jobNumber|| '--' }}
              </el-tag>
            </el-tooltip>

            <el-tooltip
              :content="`部门：${detailData.deptName|| '--'}`"
              effect="dark"
              placement="top">
              <el-tag
                disable-transitions
                class="xr-tag"
                color="#ECEEF2"
                size="mini">
                <i class="wk wk-icon-layer" />{{ detailData.deptName || '--' }}
              </el-tag>
            </el-tooltip>

            <el-tooltip
              :content="`员工状态：${getEmployeeStatusName(detailData.status)|| '--'}`"
              effect="dark"
              placement="top">
              <el-tag
                disable-transitions
                class="xr-tag"
                color="#ECEEF2"
                size="mini">
                <i class="wk wk-icon-time" />{{ getEmployeeStatusName(detailData.status)|| '--' }}
              </el-tag>
            </el-tooltip>

            <el-tooltip
              :content="`入职日期：${detailData.entryTime|| '--'}`"
              effect="dark"
              placement="top">
              <el-tag
                disable-transitions
                class="xr-tag"
                color="#ECEEF2"
                size="mini">
                <i class="wk wk-icon-status" />{{ detailData.entryTime|| '--' }}
              </el-tag>
            </el-tooltip>

            <el-tooltip
              :content="`年龄：${detailData.age|| '--'}`"
              effect="dark"
              placement="top">
              <el-tag
                disable-transitions
                class="xr-tag"
                color="#ECEEF2"
                size="mini">
                <i class="wk wk-icon-cake" />{{ detailData.age || '--' }}
              </el-tag>
            </el-tooltip>
          </div>
        </div>
      </wk-detail-header>
      <el-tabs
        v-model="tabCurrentName"
        class="side-detail__tabs--default">
        <el-tab-pane
          v-for="(item, index) in tabNames"
          :key="index"
          :label="item.label"
          :name="item.name"
          lazy>
          <component
            :is="item.name"
            :id="id"
            class="side-detail-tabs-content"
            style="padding: 16px 0;"
            @handle="relativeHandle" />
        </el-tab-pane>
      </el-tabs>
    </flexbox>
  </slide-view>
</template>

<script>
import { hrmEmployeeQueryByIdAPI } from '@/api/hrm/employee'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/WkDetailHeader'
import SalaryArchivesInfo from './components/Info'
import SalaryArchivesRecords from './components/Records'

import { timeToFormatTime } from '@/utils'
import { employeeModel } from '@/views/hrm/employee/model/employee'
import { mapGetters } from 'vuex'

export default {
  // 薪资档案详情
  name: 'SalaryArchivesDetail',
  components: {
    WkDetailHeader,
    SlideView,
    SalaryArchivesInfo,
    SalaryArchivesRecords
  },
  mixins: [],

  provide() {
    return {
      'editAuth': this.editAuth
    }
  },
  props: {
    // 详情信息id
    id: [String, Number],
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
    }
  },
  data() {
    return {
      loading: false, // 展示加载loading
      detailData: null,
      tabNames: [
        { label: '薪资信息', name: 'SalaryArchivesInfo' },
        { label: '调薪记录', name: 'SalaryArchivesRecords' }
      ],
      tabCurrentName: 'SalaryArchivesInfo'
    }
  },

  computed: {
    ...mapGetters([
      'hrm'
    ])
  },
  watch: {
    id: function(val) {
      this.getDetail()
    }
  },
  mounted() {
  },
  methods: {
    getDetail() {
      this.loading = true
      hrmEmployeeQueryByIdAPI(this.id)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          data.entryTime = timeToFormatTime(data.entryTime)

          this.detailData = data
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 员工状态展示值
     */
    getEmployeeStatusName(status) {
      return status ? employeeModel.statusValue[parseInt(status)] : ''
    },

    /**
     * 点击关闭按钮隐藏视图
     */
    hideView() {
      this.$emit('close')
    },

    /**
     * 相关模块的操作
     */
    relativeHandle(data) {
      this.$emit('handle', data)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.employee-header {
  &__top {
    .name {
      font-size: 20px;
      font-weight: bold;
    }

    .post {
      margin-left: 8px;
      font-size: 12px;
      font-weight: bold;
    }
  }

  &__bottom {
    margin-top: 10px;

    .xr-tag {
      color: $--color-text-regular;
      border: none;

      ::v-deep i {
        margin-right: 5px;
        font-size: 12px;
      }
    }

    .xr-sex-tag {
      border: none;

      ::v-deep i {
        font-size: 12px;
        color: white;
      }

      &.is-man {
        background: #3875ff;
      }

      &.is-woman {
        background: #ff3838;
      }
    }
  }
}

.side-detail__tabs--default {
  padding: 0 20px;
}
</style>
