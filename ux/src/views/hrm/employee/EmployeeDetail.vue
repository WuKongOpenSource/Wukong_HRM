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
      <wk-detail-header
        :dropdown-items="dropdownItems"
        @command-select="commandSelect">
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
            :detail="detailData"
            class="side-detail-tabs-content"
            style="padding-top: 16px;"
            @handle="relativeHandle" />
        </el-tab-pane>
      </el-tabs>
    </flexbox>
    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      @pass="formAddPass"
    />
  </slide-view>
</template>

<script>
import { hrmEmployeeQueryByIdAPI, hrmEmployeeDeleteByIdsAPI } from '@/api/hrm/employee'

import SlideView from '@/components/SlideView'
import EmployeePostInfo from './components/EmployeePostInfo'
import EmployeeBaseInfo from './components/EmployeeBaseInfo'
import EmployeeContract from './components/EmployeeContract'
import EmployeeMoney from './components/EmployeeMoney'
import EmployeeFiles from './components/EmployeeFiles'
import EmployeeHandle from './components/EmployeeHandle'
import FormAddDialog from './components/FormAddDialog'
import WkDetailHeader from '@/components/WkDetailHeader'

import { timeToFormatTime } from '@/utils'
import { employeeModel } from './model/employee'
import { mapGetters } from 'vuex'

export default {
  // 员工详情
  name: 'EmpolyeeDetail',
  components: {
    WkDetailHeader,
    SlideView,
    EmployeePostInfo,
    EmployeeBaseInfo,
    EmployeeContract,
    EmployeeMoney,
    EmployeeFiles,
    EmployeeHandle,
    // EmployeeCreateView,
    FormAddDialog
  },
  mixins: [],

  provide() {
    return {
      'editAuth': this.editAuth,
      'employeeAuth': this.employeeAuth
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
        { label: '岗位信息', name: 'EmployeePostInfo' },
        { label: '基本信息', name: 'EmployeeBaseInfo' },
        { label: '员工合同', name: 'EmployeeContract' },
        { label: '工资社保', name: 'EmployeeMoney' },
        { label: '材料附件', name: 'EmployeeFiles' },
        { label: '操作记录', name: 'EmployeeHandle' }
      ],
      tabCurrentName: 'EmployeePostInfo',
      // 弹窗添加
      formAddCommond: '',
      formAddTitle: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false
    }
  },

  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 员工权限
    employeeAuth() {
      return this.hrm.employee
    },

    // 删除权限
    deleteAuth() {
      return this.employeeAuth && this.employeeAuth.delete
    },

    // 删除权限
    editAuth() {
      return this.employeeAuth && this.employeeAuth.update
    },

    tabName() {
      if (this.tabCurrentName == 'EmployeePostInfo') {
        return 'EmployeePostInfo'
      } else if (this.tabCurrentName == 'EmployeeBaseInfo') {
        return 'EmployeeBaseInfo'
      } else if (this.tabCurrentName == 'EmployeeContract') {
        return 'EmployeeContract'
      } else if (this.tabCurrentName == 'EmployeeMoney') {
        return 'EmployeeMoney'
      } else if (this.tabCurrentName == 'EmployeeFiles') {
        return 'EmployeeFiles'
      } else if (this.tabCurrentName == 'EmployeeHandle') {
        return 'EmployeeHandle'
      }
      return ''
    },

    dropdownItems() {
      const temps = []
      // if (this.detailData && this.detailData.status == 9) {
      //   temps.push({
      //     label: '调整离职信息',
      //     command: 'change-leave',
      //     icon: 'wk wk-reset'
      //   })
      // }
      if (this.deleteAuth) {
        temps.push({
          icon: 'wk wk-delete',
          label: '删除',
          command: 'delete'
        })
      }
      return temps
    }
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
     * 更多操作
     */
    commandSelect(command) {
      this.formAddCommond = command
      if (command == 'delete') {
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmEmployeeDeleteByIdsAPI([this.id])
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '删除成功'
                })
                this.$emit('handle', { type: command })
              })
              .catch(() => {})
          })
          .catch(() => {})
      }
    },

    /**
     * 相关模块的操作
     */
    relativeHandle(data) {
      this.$emit('handle', data)
    },

    /**
     * 勾选操作
     */
    formAddPass() {
      this.$refs.formAdddialog.loading = true
      this.formAddForm.employeeId = this.handleRowData.employeeId
      const request = null

      request(this.formAddForm).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getTabsNum()
        this.getList()
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
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
