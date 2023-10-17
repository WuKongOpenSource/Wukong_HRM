<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="salary-index">
    <template v-if="listShow">
      <reminder
        v-if="noNum > 0"
        :content="`有${noNum}名员工未在任何薪资组中，无法参与工资表核算工资`"
        close-show
        @close="noEmployeesCloseClick">
        <el-button style="padding: 0;margin-left: 8px;" type="primary-text" @click="checkNoEmployeesClick">查看无薪资组员工</el-button>
      </reminder>
      <xr-header
        ft-top="0"
        label="薪资管理">
        <template slot="label">
          薪资管理
          <i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="33"
            data-id="291" />
          <span v-if="startEndTimeValue" class="time-label">（{{ startEndTimeValue }}）</span>
        </template>
        <template slot="ft">
          <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
          <wk-popover-filter
            :width="popoverFilterWidth"
            :field-from.sync="filterObj"
            :field-list="filterList"
            :has-content="hasFilterContent"
            style="margin-right: 5px;"
            placement="bottom"
            @sure="refreshList"
            @reset="resetFilter"
          />
        </template>
        <template slot="ft">
          <template v-if="!!lastData">
            <el-button
              v-if="checkSubmitShow && manageAuth"
              @click="checkClick('submit')">提交审核</el-button>
            <el-button
              v-if="checkProgressShow && manageAuth"
              type="text"
              @click="checkClick('progress')">查看审核进度</el-button>
            <el-button
              v-if="computedShow && manageAuth"
              type="primary"
              @click="computeClick">核算工资</el-button>
            <el-button
              v-if="createShow && sendAuth"
              icon="el-icon-plus"
              @click="createSlipClick">发送工资条</el-button>
            <el-button
              v-if="createShow && manageAuth"
              icon="el-icon-plus"
              @click="createClick">创建下月工资表</el-button>
            <el-button
              v-if="manageAuth"
              @click="deleteClick">删除工资表</el-button>
          </template>
        </template>
      </xr-header>
      <div class="crm-container">
        <xr-table-header v-if="tabShow">
          <el-tabs v-model="tabType" style="width: 100%;margin-bottom: -8px;" @tab-click="tabClick">
            <el-tab-pane
              v-for="tab in tabList"
              :key="tab.name"
              :name="tab.name">
              <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
            </el-tab-pane>
          </el-tabs>
          <el-button
            v-if="onlineEditShow && manageAuth"
            type="primary"
            @click="editClick">在线编辑</el-button>
        </xr-table-header>
        <el-table
          id="crm-table"
          v-loading="loading"
          :row-height="40"
          :data="list"
          :height="tableHeight"
          :summary-method="getSummaries"
          :class="WKConfig.tableStyle.class"
          :stripe="WKConfig.tableStyle.stripe"
          show-summary
          use-virtual
          class="n-table--border el-table-header--white"
          highlight-current-row
          style="width: 100%;">
          <el-table-column
            v-for="(item, index) in fieldList"
            :key="index"
            :fixed="index == 0"
            :prop="item.field"
            :label="item.name"
            :min-width="item.width"
            :formatter="fieldFormatter"
            show-overflow-tooltip />
          <el-table-column />
        </el-table>
        <div class="p-contianer">
          <el-pagination
            :current-page="currentPage"
            :page-sizes="pageSizes"
            :page-size.sync="pageSize"
            :total="total"
            class="p-bar"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </div>
    </template>

    <wk-steps-explain-view
      v-if="stepsExplainShow"
      :title="explainTitle"
      :steps="explainSteps"
      @step-click="stepClick" />

    <open-set-dialog
      :detail="initDataConfig"
      :visible.sync="openDialogShow"
      @change="openSetChange"
    />

    <month-set-dialog
      :detail="openData"
      :visible.sync="monthDialogShow"
      @change="monthSetChange"
    />

    <compute-set-dialog
      :last-data="lastData"
      :visible.sync="computeDialogShow"
      @change="refreshPageData"
    />

    <batch-edit-view
      v-if="editViewShow"
      :field-list="fieldList"
      :params="postParams"
      @close="editViewShow=false"
      @change="refreshList" />

    <examine-submit-dialog
      ref="examineSubmitDialog"
      :data="examineData"
      :is-edit="lastData && (lastData.checkStatus == 2 || lastData.checkStatus == 13)"
      :visible.sync="examineSubDialogShow"
      :examine-advanced-setting="examineAdvancedSetting"
      @save="examineSubmitClick"
    />

    <examine-progress-dialog
      :detail="lastData"
      :visible.sync="examineProDialogShow"
      @change="examineProgressChange"
    />

    <no-employee-dialog
      :visible.sync="noEmployeeDialogShow"
    />

    <salary-slip-create
      :visible.sync="slipCreatShow"
    />
  </div>
</template>

<script>
import {
  hrmSalaryMonthRecordHeadAPI,
  hrmSalaryMonthRecordListAPI,
  hrmSalaryMonthRecordAddNextAPI,
  hrmSalaryMonthLastRecordAPI,
  hrmSalaryMonthRecordChangeNumAPI,
  hrmSalaryMonthRecordSumOptionAPI,
  hrmSalaryMonthRecordSubmitExamineAPI,
  hrmSalaryMonthRecordNumAPI,
  hrmSalaryMonthRecordDeleteAPI
} from '@/api/hrm/salary'
import {
  hrmSalaryConfigQueryInItConfigAPI,
  hrmSalaryConfigUpdateInItConfigAPI
} from '@/api/hrm/salaryConfig'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import WkStepsExplainView from '../components/WkStepsExplainView'
import MonthSetDialog from './components/MonthSetDialog'
import OpenSetDialog from './components/OpenSetDialog'
import ComputeSetDialog from './components/ComputeSetDialog'
import BatchEditView from './components/BatchEditView'
import ExamineSubmitDialog from './components/ExamineSubmitDialog'
import ExamineProgressDialog from './components/ExamineProgressDialog'
import NoEmployeeDialog from './components/NoEmployeeDialog'
import Reminder from '@/components/Reminder'
import SalarySlipCreate from './slip/Create'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import moment from 'moment'
import WkApprovalFlowApplyMixin from '@/components/Examine/mixins/WkApprovalFlowApply'
// import { objDeepCopy } from '@/utils'

export default {
  name: 'SalaryIndex',
  components: {
    XrHeader,
    XrTableHeader,
    WkPopoverFilter,
    WkStepsExplainView,
    MonthSetDialog,
    OpenSetDialog,
    ComputeSetDialog,
    BatchEditView,
    ExamineSubmitDialog,
    ExamineProgressDialog,
    NoEmployeeDialog,
    Reminder,
    SalarySlipCreate
  },
  mixins: [WkApprovalFlowApplyMixin],
  data() {
    return {
      loading: false, // 加载动画
      noNum: 0,
      noEmployeeDialogShow: false,
      tableHeight: 400, // 表的高度
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      tabType: '0',
      tabList: [{
        label: '计薪人数',
        name: '0',
        num: 0
      }, {
        label: '新入职',
        name: '1',
        num: 0
      }, {
        label: '离职',
        name: '2',
        num: 0
      }, {
        label: '转正',
        name: '3',
        num: 0
      }, {
        label: '调岗',
        name: '4',
        num: 0
      }],
      /** 控制详情展示 */
      // 行信息
      rowID: '',
      showDview: false,
      // 筛选确定数据
      filterList: [
        {
          name: '员工姓名',
          field: 'employeeName',
          formType: 'text',
          setting: []
        }, {
          name: '部门',
          field: 'deptId',
          formType: 'structure',
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        }
      ],
      filterObj: {},
      // 首次
      explainTitle: '首次生成薪资前，请先配置薪资信息',
      explainSteps: [{
        icon: require('@/assets/img/resource/setting.png'),
        label: '第一步：',
        desLeft: '去企业后台',
        desCenter: '【配置工资表】',
        desRight: ''
      }, {
        icon: require('@/assets/img/resource/people.png'),
        label: '第二步：',
        desLeft: '去企业后台',
        desCenter: '【配置计税规则】',
        desRight: '',
        button: '已配置完成，去生成薪资'
      }, {
        icon: require('@/assets/img/resource/cycle.png'),
        label: '第三步：',
        desLeft: '',
        desCenter: '【创建薪资组】',
        desRight: '关联员工及计税规则'
      }],
      initStatusConfig: null,
      initDataConfig: null,
      stepsExplainShow: false,
      // 月份设置
      monthDialogShow: false,
      // 开启薪资设置
      openData: null,
      openDialogShow: false,
      // 核算设置
      computeDialogShow: false,
      // 最新薪资的信息
      lastData: null,
      // 编辑数据
      editViewShow: false,
      // 总合计
      sumData: null,
      // 审批提交
      examineSubDialogShow: false,
      examineProDialogShow: false,
      examineData: null,
      slipCreatShow: false // 发送工资条
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 权限
    salaryAuth() {
      return this.hrm.salary
    },

    // 维护
    manageAuth() {
      return this.salaryAuth && this.salaryAuth.manage
    },

    // 发工资条
    sendAuth() {
      return this.salaryAuth && this.salaryAuth.sendSlip
    },

    // 有筛选内容
    hasFilterContent() {
      if (this.filterObj) {
        let hasContent = false
        const keys = Object.keys(this.filterObj)
        for (let index = 0; index < keys.length; index++) {
          const key = keys[index]
          if (this.filterObj[key] != '' &&
           this.filterObj[key] != [] &&
           this.filterObj[key] != null &&
           this.filterObj[key] != undefined) {
            hasContent = true
          }
        }
        return hasContent
      }
      return false
    },
    listShow() {
      if (!this.initStatusConfig) {
        return false
      }
      return this.initStatusConfig.firstConfig && this.initStatusConfig.secondConfig
    },
    startEndTimeValue() {
      if (this.lastData) {
        return `计薪周期：${moment(this.lastData.startTime).format('MM月DD日')}-${moment(this.lastData.endTime).format('MM月DD日')}`
      }
      return ''
    },
    postParams() {
      const params = {}
      if (this.lastData) {
        params.srecordId = this.lastData.srecordId
      }

      params.type = this.tabType

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }
      return params
    },
    // 审核功能展示
    checkShow() {
      return !!this.examineData
    },
    checkSubmitShow() {
      return this.checkShow && this.lastData && (this.lastData.checkStatus == 2 ||
        this.lastData.checkStatus == 4 ||
        this.lastData.checkStatus == 11 ||
        this.lastData.checkStatus == 13)
    },
    // 展示审批进度 examineRecordId 存在展示
    checkProgressShow() {
      return this.checkShow && this.lastData && this.lastData.checkStatus != 5 && this.lastData.checkStatus != 11 && this.lastData.examineRecordId
    },
    // 有数据tab就可以展示
    tabShow() {
      return this.lastData && this.lastData.checkStatus != 5
    },
    // 有审批流 2拒绝 4:撤回 5 未提交 展示
    computedShow() {
      if (this.checkShow && this.lastData) {
        return this.lastData.checkStatus == 2 ||
        this.lastData.checkStatus == 4 ||
        this.lastData.checkStatus == 5 ||
        this.lastData.checkStatus == 11 ||
        this.lastData.checkStatus == 13
      }
      return true
    },
    // 有审批流 2拒绝 4:撤回 5 未提交 展示
    onlineEditShow() {
      if (this.checkShow && this.lastData) {
        return this.lastData.checkStatus == 2 ||
        this.lastData.checkStatus == 4 ||
        this.lastData.checkStatus == 11
      }
      return true
    },
    // 有审批流 审批必须通过的前提下 展示新建按钮
    createShow() {
      // 0待审核、1通过、2拒绝、3审核中 4:撤回 5 未提交 14 归档
      if (this.examineData && this.lastData) {
        return this.lastData.checkStatus == 1 || this.lastData.checkStatus == 14
      }
      return true
    }
  },
  watch: {},
  created() {
    this.getTableHeight()
    window.onresize = () => {
      this.getTableHeight()
    }

    this.refreshPageData()
  },
  methods: {
    /**
     * 获取没有计薪的员工数量
     */
    getNoNum() {
      // 0 未计薪 1 计薪
      hrmSalaryMonthRecordNumAPI(0)
        .then(res => {
          this.noNum = res.data || 0
        })
        .catch(() => {
        })
    },

    /**
     * 刷新整个页面
     */
    refreshPageData() {
      this.lastData = null
      this.getConfigList()
    },

    getExamineData(lastData) {
      // 审核信息
      const params = {
        label: 4 // 4 薪资
      }

      if (this.lastData.checkStatus == 2 || this.lastData.checkStatus == 13) {
        params.recordId = this.lastData.examineRecordId
      }

      this.initWkFlowData({
        params,
        fieldForm: lastData
      }, (res) => {
        const resData = res.resData
        // resData.examineFlowList?.forEach(item => {
        //   if (item.rangeType === 2 || item.rangeType == 3) {
        //     item.finalUserList = objDeepCopy(item.userList)
        //     item.userList = []
        //   }
        // })
        this.examineData = resData.examineId ? resData : null
        // this.wkFlowList = res.list
        // this.flowRemarks = res.resData ? res.resData.remarks : ''
      })
    },
    /**
     * 获取table高
     */
    getTableHeight() {
      this.tableHeight = document.documentElement.clientHeight - (this.tabShow ? 255 : 205) - (this.noNum > 0 ? 70 : 0)
    },

    /**
     * 获取配置信息
     */
    getConfigList() {
      this.loading = true
      // 2 薪资初始化配置1 3 薪资初始化配置2 4 社保初始化配置1 5 社保初始化配置2
      hrmSalaryConfigQueryInItConfigAPI()
        .then(res => {
          const statusInitConfig = res.data.statusInitConfig
          // 仅需要验证最后一步是否配置
          const firstConfig = statusInitConfig[2] == 1
          const secondConfig = statusInitConfig[3] == 1
          this.initStatusConfig = { firstConfig, secondConfig }

          this.initDataConfig = res.data.otherInitConfig
          if (!firstConfig) {
            this.stepsExplainShow = true
          } else if (!secondConfig) {
            this.openDialogShow = true
          } else {
            this.stepsExplainShow = false
            this.openDialogShow = false
            // const setYear = this.initDataConfig.socialSecurityStartMonth ? this.initDataConfig.socialSecurityStartMonth.split('-')[0] : new Date().getFullYear().toString()
            // this.year = this.initDataConfig.lastSocialSecurityYear ? this.initDataConfig.lastSocialSecurityYear.toString() : setYear
            // this.initDataConfig.year = setYear
            this.getTableHeader()
          }

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 步骤点击
     */
    stepClick(type, index, step) {
      if (type == 'des') {
        if (index == 0) {
          this.$router.push({
            name: 'salaryOptions'
          })
        } else if (index == 1 || index == 2) {
          this.$router.push({
            name: 'salaryRules',
            query: {
              type: {
                1: 'tax',
                2: 'group'
              }[index]
            }
          })
        }
      } else if (type == 'button') {
        this.updateInItConfig(2)
      }
    },

    /**
     * 更新状态
     */
    updateInItConfig(type) {
      this.loading = true
      // 2 薪资初始化配置1 3 薪资初始化配置2 4 社保初始化配置1 5 社保初始化配置2
      hrmSalaryConfigUpdateInItConfigAPI(type)
        .then(res => {
          if (type == 2) {
            this.openDialogShow = true
          } else {
            this.getTableHeader()
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 开始设置完成
     */
    openSetChange(data) {
      this.openData = data
      this.monthDialogShow = true
    },

    /**
     * 月份设置chang
     */
    monthSetChange(year, month) {
      this.getConfigList()
    },

    /**
     * 获取表头
     */
    getTableHeader() {
      this.loading = true
      hrmSalaryMonthRecordHeadAPI()
        .then(res => {
          this.loading = false
          const defaultTables = [
            { field: 'employeeName', name: '姓名', width: 100 },
            { field: 'jobNumber', name: '工号', width: 100 },
            { field: 'deptName', name: '部门', width: 100 },
            { field: 'post', name: '岗位', width: 100 }]

          const otherTables = res.data.map(item => {
            return {
              field: item.code.toString(),
              name: item.name,
              isFixed: item.isFixed === 1,
              width: 100
            }
          })
          this.fieldList = defaultTables.concat(otherTables)
          this.refreshList()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取最新的薪资配置
     */
    getLastData() {
      return new Promise((resolve, reject) => { // eslint-disable-line
        if (this.lastData) {
          return resolve(this.lastData)
        } else {
          this.loading = true
          hrmSalaryMonthLastRecordAPI()
            .then(res => {
              this.lastData = res.data
              this.loading = false
              resolve(this.lastData)
            })
            .catch(() => {
              this.loading = false
              reject && reject()
            })
        }
      })
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
    },

    /**
     * 新建
     */
    checkClick(type) {
      if (type == 'submit') {
        this.examineSubDialogShow = true
      } else {
        this.examineProDialogShow = true
      }
    },

    /**
     * 提交了审批
     */
    examineSubmitClick(wkFlowResult) {
      this.$refs.examineSubmitDialog.loading = true
      if (wkFlowResult.pass) {
        const params = {
          srecordId: this.lastData.srecordId,
          checkStatus: this.lastData.checkStatus,
          examineRecordId: this.lastData.examineRecordId
        }
        params.examineFlowData = {
          label: this.examineAdvancedSetting.label,
          dataMap: {},
          flowDataList: wkFlowResult.flowParams.list
        }
        hrmSalaryMonthRecordSubmitExamineAPI(params).then(res => {
          this.lastData = null
          this.$refs.examineSubmitDialog.loading = false
          this.examineSubDialogShow = false
          this.getLastData()
        }).catch(() => {
          this.$refs.examineSubmitDialog.loading = false
        })
      } else {
        this.$refs.examineSubmitDialog.loading = false
        this.$message.error('请完善审批信息')
      }
    },

    /**
     * 进行了审批
     */
    examineProgressChange() {
      this.lastData = null
      this.getLastData()
    },

    computeClick() {
      this.computeDialogShow = true
    },

    /**
     * 创建下月工资表
     */
    createClick() {
      this.$confirm('新建下月工资表，当前的工资表将归入历史工资表中，且历史工资表数据不可修改。请确认现在就开始做下月工资吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        hrmSalaryMonthRecordAddNextAPI().then(res => {
          this.refreshList()
        }).catch(() => {})
      }).catch(() => {
      })
    },

    /**
     * 创建成功
     */
    tabClick() {
      this.handleCurrentChange(1)
    },

    /**
     * 创建工资条
     */
    createSlipClick() {
      this.slipCreatShow = true
    },

    /**
     * 删除工资表
     */
    deleteClick() {
      this.$confirm('当月工资表删除后，上月工资表将纳入当月工资表中，并且支持修改，确定要删除当月工资表吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        hrmSalaryMonthRecordDeleteAPI(this.lastData.srecordId).then(res => {
          this.refreshList()
        }).catch(() => {})
      }).catch(() => {
      })
    },

    /**
     * 刷新
     */
    refreshList() {
      this.lastData = null
      this.getNoNum()
      this.handleCurrentChange(1)
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 列表数据
     */
    getList() {
      this.getLastData().then(lastData => {
        this.getExamineData(lastData)

        this.loading = true
        const params = {
          ...this.postParams,
          page: this.currentPage,
          limit: this.pageSize
        }

        hrmSalaryMonthRecordListAPI(params)
          .then(res => {
            const resData = res.data || {}
            const list = resData.list
            list.forEach(item => {
              const salary = item.salary || []
              salary.forEach(sItem => {
                item[sItem.code] = sItem.value
              })
            })
            this.list = list
            this.total = resData.totalRow

            const extraData = resData.extraData || {}
            const salaryOption = extraData.salaryOption

            const sumData = {}
            salaryOption.forEach(sItem => {
              sumData[sItem.code] = sItem.value
            })
            this.sumData = sumData

            if (this.total > 0) {
              this.getTabNum()
            }
            this.getTableHeight()

            this.$nextTick(() => {
              const warpDom = document.querySelector('.el-table__body-wrapper')
              warpDom.scrollTop = 1
              if (warpDom.scrollLeft != 0) {
                warpDom.scrollLeft = warpDom.scrollLeft - 1
              }
            })
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }).catch(() => {})
    },

    /**
     * tab 数量
     */
    getTabNum() {
      hrmSalaryMonthRecordChangeNumAPI()
        .then(res => {
          const data = res.data || {}
          this.tabList.forEach(item => {
            if (item.name != 'all') {
              item.num = data[parseInt(item.name)] || 0
            }
          })
        })
        .catch(() => {
        })
    },

    /**
     * 合计，通过列表反馈了，暂时无用
     */
    getSumData() {
      this.getLastData().then(lastData => {
        hrmSalaryMonthRecordSumOptionAPI(lastData.srecordId)
          .then(res => {
            const dataList = res.data || []
            const sumData = {}
            dataList.forEach(sItem => {
              sumData[sItem.code] = sItem.value
            })
            this.sumData = sumData
          })
          .catch(() => {
          })
      }).catch(() => {})
    },

    getSummaries(param) {
      const { columns } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '总合计'
        } else if (index <= 5) {
          sums[index] = ''
        } else {
          sums[index] = this.sumData ? this.sumData[column.property] : ''
        }
      })

      return sums
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const value = row[column.property]
      return isEmpty(value) ? '--' : value
    },

    editClick() {
      this.editViewShow = true
    },

    /**
     * 查询未计算薪资员工
     */
    checkNoEmployeesClick() {
      this.noEmployeeDialogShow = true
    },

    noEmployeesCloseClick() {
      this.noNum = 0
      this.getTableHeight()
    }
  }
}
</script>

<style lang="scss" scoped>
.salary-index {
  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  ::v-deep.reminder-wrapper {
    margin-bottom: 8px;

    .reminder-body {
      align-items: center !important;
    }
  }

  .time-label {
    font-size: 14px;
    font-weight: normal;
    color: $--color-text-secondary;
  }

  ::v-deep.xr-table-header {
    border-bottom: 1px solid $--border-color-base;

    .xr-table-header__body {
      padding: 0;
    }
  }

  ::v-deep .el-tabs {
    .el-tabs__header {
      margin-bottom: 0;
    }

    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      font-weight: bold;
      color: $--color-text-regular;
    }
  }

  .tab-pane-num {
    display: inline-block;
    height: 18px;
    padding: 0 6px;
    font-size: 12px;
    font-weight: bold;
    line-height: 18px;
    color: $--color-text-regular;
    text-align: center;
    white-space: nowrap;
    background-color: #dfe1e6;
    border: 1px solid #fff;
    border-radius: 10px;
  }
}
</style>
