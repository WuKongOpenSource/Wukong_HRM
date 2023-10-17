<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="candidate-index">
    <xr-header
      ft-top="0"
      placeholder="请输入姓名/手机号/邮箱"
      label="候选人"
      show-search
      @search="searchClick">
      <template
        slot="bottom-ft"
      >
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom-end"
          @sure="refreshList"
          @reset="resetFilter"
          @show="filterShowClick"
        />
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
      </template>
      <template slot="ft">
        <el-button
          v-if="createAuth"
          type="primary"
          @click="createClick">新建候选人</el-button>
      </template>
    </xr-header>
    <div class="crm-container">
      <wk-border-menu
        v-model="tabType"
        :list="tabList"
        is-select
        @select="tabClick"
      />
      <xr-table-header
        v-if="selectionList.length > 0"
        :handles="tabelHandles"
        :selects="selectionList"
        @command="handleCommand">
        <!-- <el-tabs v-show="selectionList.length == 0" v-model="tabType" style="width: 100%;margin-bottom: -8px;" @tab-click="tabClick">
          <el-tab-pane
            v-for="tab in tabList"
            :key="tab.name"
            :name="tab.name">
            <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
          </el-tab-pane>
        </el-tabs> -->
        <template v-if="selectionList.length > 0">
          <span v-if="noHandles" slot="prefix">
            <!-- 无操作项 -->
          </span>
          <template v-else>
            <!-- <span
              v-if="statusChangeHandles.length > 0"
              slot="prefix"
              class="el-dropdown-link"
            > -->
            <el-button
              v-if="statusChangeHandles.length > 0"
              slot="prefix"
              class="el-dropdown-link"
              icon="wk wk-transfer"
              type="primary"
              size="medium"
              @click="statusHandleChange(statusChangeHandles[0].command)">
              {{ statusChangeHandles[0].label }}
              <el-dropdown
                v-if="statusChangeHandles.length > 1"
                @click.native.stop=""
                @command="statusHandleChange">
                <i class="el-icon-arrow-down el-icon--right" />
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="(item, index) in statusChangeHandles"
                    v-show="index != 0"
                    :key="item.command"
                    :command="item.command"
                  >{{ item.label }}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-button>

            <el-dropdown
              v-if="moreHandles.length > 0"
              slot="suffix"
              @command="moreHandlesChange">
              <el-button
                style="margin-left: 15px;"
                class="el-dropdown-link is-more dropdown-btn"
                icon="wk wk-transfer"
                size="medium">
                更多操作
                <i class="el-icon-arrow-down el-icon--right" />
              </el-button>

              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(item, index) in moreHandles"
                  :key="index"
                  :command="item.command"
                >{{ item.label }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </template>
      </xr-table-header>
      <el-table
        id="crm-table"
        v-loading="loading"
        :row-height="40"
        :data="list"
        :height="tableHeight"
        :cell-class-name="cellClassName"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        class="n-table--border el-table-header--white"
        highlight-current-row
        style="width: 100%;"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange">
        <el-table-column
          show-overflow-tooltip
          fixed
          type="selection"
          align="center"
          width="55"
          :selectable="selectableFun" />
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index == 0"
          :prop="item.field"
          :label="item.name"
          :min-width="item.width"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <template v-if="scope.column.property == 'status'">
              <span :style="getStatusStyle(scope.row.status)" class="status-mark" />
              <span>{{ fieldFormatter(scope.row, scope.column) }}</span>
            </template>
            <template v-else>
              {{ fieldFormatter(scope.row, scope.column) }}
            </template>
          </template>
        </el-table-column>

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

    <candidate-detail
      v-if="showDview"
      :id="rowID"
      @handle="detailHandle"
      @close="showDview=false" />

    <candidate-create-view
      v-if="isCreate"
      :detail="handleRowData"
      @success="createSaveSuccess"
      @close="isCreate=false" />

    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :width="formAddFields.length == 1 ? '400px' : '700px'"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      @pass="formAddPass"
      @change="formChange"
    >
      <div
        v-if="formAddCommand === 'clear'"
        slot="top"
        style=" margin-bottom: 10px;color: #6b778c;">可根据未更改候选人简历状态的时间清理候选人</div>
      <div
        v-if="formAddCommand === 'clear'"
        style=" margin-top: 10px;color: #6b778c;"
      >{{ clearTips }}</div>

    </form-add-dialog>

    <employee-create-view
      v-if="waitCreateShow"
      :detail="handleRowData"
      :create-type="formAddCommand"
      @success="refreshList"
      @close="waitCreateShow=false" />

  </div>
</template>

<script>
import {
  hrmRecruitCandidateQueryListAPI,
  hrmRecruitCandidateQueryNumAPI,
  hrmRecruitCandidateUpdateStatusAPI,
  hrmRecruitCandidateDeleteByIdsAPI,
  hrmRecruitCandidateUpdatePostAPI,
  hrmRecruitCandidateUpdateChannelAPI,
  hrmRecruitCandidateQueryCleanDataAPI,
  hrmRecruitInterviewAddBatchAPI,
  hrmRecruitInterviewSetResultAPI,
  hrmRecruitCandidateEliminateAPI
} from '@/api/hrm/recruit/candidate'
import {
  hrmConfigQueryRecruitEliminateAPI // 淘汰原因
} from '@/api/admin/hrm'
import {
  hrmEmployeeQueryInAPI,
  hrmEmployeeQueryByIdAPI
} from '@/api/hrm/employee'
import {
  hrmRecruitPostQuerAllListAPI
} from '@/api/hrm/recruit/post'
import {
  hrmRecruitChannelQueryAPI
} from '@/api/hrm/employeePost'

import XrHeader from '@/components/XrHeader/Search'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import CandidateCreateView from './Create'
import CandidateDetail from './Detail'
import FormAddDialog from '../components/FormAddDialog'
import EmployeeCreateView from '@/views/hrm/employee/components/EmployeeCreateView'
import WkBorderMenu from '../../components/WkBorderMenu'

import { mapGetters } from 'vuex'
import { objDeepCopy, timeToFormatTime } from '@/utils'
import candidateModel from '../model/candidate'
import { isEmpty, isArray } from '@/utils/types'

export default {
  name: 'CandidateIndex',
  components: {
    XrHeader,
    XrTableHeader,
    WkPopoverFilter,
    CandidateCreateView,
    CandidateDetail,
    FormAddDialog,
    EmployeeCreateView,
    WkBorderMenu
  },

  provide() {
    return {
      'manageAuth': this.manageAuth
    }
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 350, // 表的高度
      list: [],
      fieldList: [],
      handleObj: {
        firstPass: {
          label: '移动到初选通过',
          command: 'firstPass',
          icon: 'wk wk-delete'
        },
        interview: {
          label: '安排面试',
          command: 'interview',
          icon: 'wk wk-delete'
        },
        pass: {
          label: '移动到面试通过',
          command: 'pass',
          icon: 'wk wk-delete'
        },
        wait: {
          label: '移动到待入职/入职',
          command: 'wait',
          icon: 'wk wk-delete'
        },
        new: {
          label: '移动到新候选人',
          command: 'new',
          icon: 'wk wk-delete'
        },
        offer: {
          label: '发offer',
          command: 'offer',
          icon: 'wk wk-delete'
        },
        again: {
          label: '安排复试',
          command: 'again',
          icon: 'wk wk-delete'
        },
        confirm: {
          label: '移动到入职',
          command: 'confirm',
          icon: 'wk wk-delete'
        }
      },
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      queryParams: {}, // query 传入参数
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      /**
       * 1: '新候选人',
      2: '初选通过',
      3: '安排面试',
      4: '面试通过',
      5: '已发offer',
      6: '待入职',
      7: '已淘汰',
      8: '已入职'
       */
      tabType: 'all',
      tabList: [{
        label: '全部',
        name: 'all',
        num: 0
      }],
      /** 控制详情展示 */
      // 行信息
      rowID: '',
      showDview: false,
      // 高级筛选
      channelList: [],
      postList: [],
      // 筛选确定数据
      filterObj: {
        age: [undefined, undefined],
        workTime: [undefined, undefined]
      },
      // 勾选数据 用于全局导出
      selectionList: [],
      // 编辑详情
      handleRowData: null,
      // 是创建
      isCreate: false,
      // 待入职创建展示
      waitCreateShow: false,
      // 弹窗添加
      formAddTitle: '',
      formAddCommand: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 需要清理的数据
      clearTips: '',
      clearList: [],
      // 淘汰原因列表
      eliminateList: []
    }
  },

  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 招聘权限
    recruitAuth() {
      return this.hrm.recruit
    },

    // 新建权限
    createAuth() {
      return this.recruitAuth && this.recruitAuth.save
    },

    // 详情权限
    detailAuth() {
      return this.recruitAuth && this.recruitAuth.read
    },

    // 维护权限
    manageAuth() {
      return this.recruitAuth && this.recruitAuth.manage
    },

    // 删除状态
    deleteAuth() {
      return this.recruitAuth && this.recruitAuth.delete
    },

    // 操作
    tabelHandles() {
      if (this.manageAuth) {
        if (this.tabType == 1 ||
        this.tabType == 2 ||
        this.tabType == 3 ||
        this.tabType == 4 ||
        this.tabType == 5 ||
        this.tabType == 6) {
          return [{
            label: '淘汰/流失',
            command: 'eliminate',
            icon: 'wk wk-delete'
          }]
        }

        if (this.tabType == 7) {
          return [{
            label: '恢复到新候选人',
            command: 'reset',
            icon: 'wk wk-reset'
          }]
        }
      }
      return []
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
            if (isArray(this.filterObj[key])) {
              // 长度2 都是 undefined 当做区间判断
              if (!(this.filterObj[key].length === 2 && this.filterObj[key][0] === undefined && this.filterObj[key][1] === undefined)) {
                hasContent = true
              }
            } else {
              hasContent = true
            }
          }
        }
        return hasContent
      }
      return false
    },
    filterList() {
      return [
        {
          name: '职位',
          field: 'postId',
          formType: 'select',
          setting: this.postList
        },
        {
          name: '招聘负责人',
          field: 'ownerEmployeeId',
          formType: 'user',
          props: {
            label: 'employeeName',
            value: 'employeeId',
            isList: true
          },
          request: hrmEmployeeQueryInAPI,
          setting: []
        }, {
          name: '性别',
          field: 'sex',
          formType: 'radio',
          setting: [{
            label: '男',
            value: 1
          }, {
            label: '女',
            value: 2
          }]
        },
        {
          name: '年龄',
          field: 'age',
          formType: 'range',
          precision: 0,
          setting: []
        },
        {
          name: '工作年限',
          field: 'workTime',
          formType: 'range',
          precision: 0,
          setting: []
        }, {
          name: '学历',
          field: 'education',
          formType: 'select',
          setting: candidateModel.getValueList(candidateModel.educationValue)
        },
        {
          name: '毕业院校',
          field: 'graduateSchool',
          formType: 'text',
          setting: []
        },
        {
          name: '最近工作单位',
          field: 'latestWorkPlace',
          formType: 'text',
          setting: []
        }, {
          name: '招聘渠道',
          field: 'channelId',
          formType: 'select',
          setting: this.channelList
        }, {
          name: '面试官',
          field: 'interviewEmployeeId',
          formType: 'user',
          props: {
            label: 'employeeName',
            value: 'employeeId',
            isList: true
          },
          request: hrmEmployeeQueryInAPI,
          setting: []
        }, {
          name: '面试时间',
          field: 'interviewTime',
          formType: 'dateRange',
          setting: []
        }, {
          name: '创建人',
          field: 'createUserId',
          formType: 'user',
          props: {
            label: 'employeeName',
            value: 'employeeId',
            isList: true
          },
          request: hrmEmployeeQueryInAPI,
          setting: []
        }, {
          name: '创建时间',
          field: 'createTime',
          formType: 'dateRange',
          setting: []
        }
      ]
    },
    /**
     * 无操作
     */
    noHandles() {
      return this.tabType == 'all' || (!this.manageAuth && !this.deleteAuth)
    },
    /**
     * 修改状态的操作
     *
     */
    statusChangeHandles() {
      if (this.noHandles || !this.manageAuth) {
        return []
      }

      let commands = []
      if (this.tabType == 1) {
        commands = ['firstPass', 'interview', 'pass']
      } else if (this.tabType == 2) {
        commands = ['interview', 'new', 'pass']
      } else if (this.tabType == 3) {
        commands = ['pass', 'new', 'firstPass']
      } else if (this.tabType == 4) {
        commands = ['offer', 'new', 'firstPass', 'again']
      } else if (this.tabType == 5) {
        commands = []
      } else if (this.tabType == 6 && this.selectionList.length == 1) {
        commands = ['confirm']
      }

      // 待入职操作 单人操作
      if ((this.tabType == 1 || this.tabType == 2 || this.tabType == 4 || this.tabType == 5) &&
      this.selectionList.length == 1) {
        commands.push('wait')
      }

      const commandHandles = []
      for (let index = 0; index < commands.length; index++) {
        const command = commands[index]
        commandHandles.push(this.handleObj[command])
      }

      return commandHandles
    },

    moreHandles() {
      if (this.noHandles) {
        return []
      }

      /**
       * 1: '新候选人',
        2: '初选通过',
        3: '安排面试',
        4: '面试通过',
        5: '已发offer',
        6: '待入职',
        7: '已淘汰',
        8: '已入职'
       */
      if (this.tabType == 1 ||
      this.tabType == 2 ||
      this.tabType == 3 ||
      this.tabType == 4) {
        const temps = this.manageAuth ? [{
          label: '更改应聘职位',
          command: 'changePost',
          icon: 'wk wk-delete'
        }, {
          label: '更改应聘渠道',
          command: 'changeChannel',
          icon: 'wk wk-delete'
        }, {
          label: '一键清理候选人',
          command: 'clear',
          icon: 'wk wk-delete'
        }] : []

        // 安排面试 增加操作
        if (this.tabType == 3 && this.selectionList.length == 1) {
          temps.unshift({
            label: '更改面试安排',
            command: 'changeInterview',
            icon: 'wk wk-delete'
          })
          const obj = this.selectionList[0]
          // 1面试未完成 2面试通过 3面试未通过 4 面试取消
          if (obj.interviewResult != 4) {
            temps.unshift({
              label: '取消面试',
              command: 'cancelInterview',
              icon: 'wk wk-delete'
            })
          }
        }

        if (this.deleteAuth) {
          temps.push({
            label: '删除',
            command: 'delete',
            icon: 'wk wk-delete'
          })
        }

        return temps
      }

      if (this.tabType == 7 && this.deleteAuth) {
        return [{
          label: '删除',
          command: 'delete',
          icon: 'wk wk-delete'
        }]
      }

      return []
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 400 : 350)
    }

    const tempTabs = candidateModel.getValueList(candidateModel.statusValue)
    const tabs = []
    tempTabs.forEach(item => {
      tabs.push({
        label: item.label,
        name: item.value.toString(),
        num: 0
      })
    })

    this.tabList = this.tabList.concat(tabs)
    // 加入query筛选 workbench 工作台
    if (this.$route.query.workbench == 'filter') {
      const queryParams = this.$route.query
      if (queryParams && queryParams.tabType) {
        this.tabType = queryParams.tabType
      } else {
        this.tabType = ''
        this.queryParams = queryParams
        delete this.queryParams['workbench']
      }
    }

    // 表头字段
    this.fieldList = candidateModel.tableFields
    this.refreshList()
  },
  mounted() {
    // 路由配置事件
    this.initTask()
  },
  methods: {
    /**
     * 获取tab 数量
     */
    getTabsNum() {
      hrmRecruitCandidateQueryNumAPI()
        .then(res => {
          const data = res.data || {}
          let allNum = 0
          this.tabList.forEach(item => {
            if (item.name != 'all') {
              item.num = data[parseInt(item.name)] || 0
              allNum += item.num
            }
          })
          this.tabList[0].num = allNum
        })
        .catch(() => {
        })
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {
        age: [undefined, undefined],
        workTime: [undefined, undefined]
      }
      this.refreshList()
    },

    /**
     * 新建
     */
    createClick() {
      this.handleRowData = null
      this.isCreate = true
    },

    /**
     * 创建成功
     */
    createSaveSuccess() {
      this.refreshList()
    },

    /**
     * 搜索
     */
    searchClick(search) {
      this.search = search
      this.refreshList()
    },

    /**
     * 详情操作
     */
    detailHandle(data) {
      if (data.type === 'delete') {
        this.showDview = false
      }
      this.refreshList()
    },

    /**
     * tab切换
     */
    tabClick(tab) {
      this.queryParams = {}
      this.refreshList()
    },

    /**
     * 刷新
     */
    refreshList() {
      this.getTabsNum()
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
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        search: this.search
      }

      if (this.tabType != 'all') {
        params.status = this.tabType
      }

      for (const key in this.filterObj) {
        if (key == 'age') {
          params.minAge = this.filterObj.age[0]
          params.maxAge = this.filterObj.age[1]
        } else if (key == 'workTime') {
          params.minWorkTime = this.filterObj.workTime[0]
          params.maxWorkTime = this.filterObj.workTime[1]
        } else {
          params[key] = this.filterObj[key]
        }
      }

      for (const key in this.queryParams) {
        params[key] = this.queryParams[key]
      }

      hrmRecruitCandidateQueryListAPI(params)
        .then(res => {
          this.list = res.data.list
          this.total = res.data.totalRow
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const valueObj = candidateModel[`${column.property}Value`]
      if (valueObj) {
        // 安排面试需要特殊处理 1面试未完成 不展示
        if (column.property === 'status' && row[column.property] === 3 && row.interviewResult != 1) {
          return `${valueObj[row[column.property]]}（${candidateModel.interviewResultValue[row.interviewResult]}）`
        }
        return valueObj[row[column.property]] || '--'
      } else if (column.property == 'postId') {
        return `${row.postName || ''}${row.postStatus === 0 ? '（已停止招聘）' : ''}` || '--'
      } else if (column.property == 'channelId') {
        return row.channelName || '--'
      } else {
        const value = row[column.property]
        return isEmpty(value) ? '--' : value
      }
    },

    /**
     * 获取状态颜色
     */
    getStatusStyle(status) {
      return {
        backgroundColor: candidateModel.statusColorValue[status]
      }
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (this.detailAuth && column.property == 'candidateName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (this.detailAuth && column.property == 'candidateName') {
        this.rowID = row.candidateId
        this.showDview = true
      }
    },

    /**
     * 是否能勾选
     */
    selectableFun() {
      return this.tabType !== 'all'
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 400 : 350)
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      this.formAddCommand = command
      if (command == 'reset') {
        this.$confirm('确定恢复为新候选人?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.updateStatus(candidateModel.statusKeyValue.new)
          })
          .catch(() => {
          })
      } else if (command == 'eliminate') {
        this.formAddTitle = '淘汰/流失候选人'
        this.formAddForm = {
          eliminate: ''
        }
        this.formAddRules = {
          eliminate: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ]
        }

        this.getEliminateList().then(() => {
          this.formAddFields = [{
            name: '淘汰/流失原因',
            field: 'eliminate',
            formType: 'select',
            setting: this.eliminateList
          }, {
            name: '备注',
            field: 'remarks',
            formType: 'textarea',
            setting: []
          }]
          this.formAddDialogShow = true
        })
      }
    },

    /** *** 高级筛选 */
    /**
     * 高级筛选展示
     */
    filterShowClick() {
      if (this.postList.length == 0 || this.channelList.length == 0) {
        this.getChannelList()
        this.getPostList()
      }
    },

    /**
     * 获取渠道信息
     */
    getChannelList() {
      return new Promise((resolve, reject) => {
        if (this.channelList.length == 0) {
          hrmRecruitChannelQueryAPI()
            .then(res => {
              const channelList = res.data || []
              channelList.forEach(item => {
                if (item.status == 1) {
                  item.label = item.value
                  item.value = item.channelId
                }
              })
              this.channelList = channelList
              resolve(this.channelList)
            })
            .catch(() => {
              if (reject) {
                reject()
              }
            })
        } else {
          resolve(this.channelList)
        }
      })
    },

    /**
     * 获取岗位信息
     */
    getPostList() {
      return new Promise((resolve, reject) => {
        if (this.postList.length == 0) {
          hrmRecruitPostQuerAllListAPI()
            .then(res => {
              const postList = res.data || []
              postList.forEach(item => {
                item.label = item.postName
                item.value = item.postId
              })
              this.postList = postList
              resolve(this.postList)
            })
            .catch(() => {
              if (reject) {
                reject()
              }
            })
        } else {
          resolve(this.postList)
        }
      })
    },

    /**
     * 获取淘汰原因
     */
    getEliminateList() {
      return new Promise((resolve, reject) => {
        if (this.eliminateList.length == 0) {
          hrmConfigQueryRecruitEliminateAPI()
            .then(res => {
              const eliminateList = res.data?.recruit || []
              eliminateList.forEach(item => {
                return {
                  label: item,
                  value: item
                }
              })
              this.eliminateList = eliminateList
              resolve(this.eliminateList)
            })
            .catch(() => {
              if (reject) {
                reject()
              }
            })
        } else {
          resolve(this.eliminateList)
        }
      })
    },
    /**
     * 勾选操作
     */
    statusHandleChange(command) {
      this.formAddCommand = command
      if (command == 'firstPass' ||
      command == 'pass' ||
      command == 'offer' ||
      command == 'new') {
        this.updateStatus(candidateModel.statusKeyValue[command])
      } else if (command == 'interview' || command == 'again' || command == 'changeInterview') {
        this.formAddTitle = {
          interview: '安排面试',
          again: '安排复试',
          changeInterview: '更改面试安排'
        }[command]

        const data = command == 'changeInterview' ? this.selectionList[0] : null
        this.formAddForm = {
          interviewEmployeeId: data ? data.interviewEmployeeId : '',
          otherInterviewEmployeeIds: data ? (data.otherInterviewEmployeeIds ? data.otherInterviewEmployeeIds.split(',').filter(item => !isEmpty(item)) : []) : [],
          interviewTime: data ? data.interviewTime : '',
          type: data ? data.interviewType : ''
        }

        console.log(data, this.formAddForm)
        this.formAddRules = {
          interviewTime: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ]
        }

        this.getChannelList().then(() => {
          this.formAddFields = [{
            name: '面试官',
            field: 'interviewEmployeeId',
            formType: 'user',
            props: { label: 'employeeName', value: 'employeeId', isList: true },
            request: hrmEmployeeQueryInAPI,
            setting: []
          }, {
            name: '其他面试官',
            field: 'otherInterviewEmployeeIds',
            formType: 'user',
            radio: false,
            props: { label: 'employeeName', value: 'employeeId', isList: true },
            request: hrmEmployeeQueryInAPI,
            setting: []
          },
          {
            name: '面试时间',
            field: 'interviewTime',
            formType: 'datetime',
            setting: []
          },
          {
            name: '面试方式',
            field: 'type',
            formType: 'select',
            setting: candidateModel.getValueList(candidateModel.interviewTypeValue)
          }]
          this.formAddDialogShow = true
        })
      } else if (command == 'wait') { // 移动到待入职/入职
        if (this.selectionList.length) {
          this.handleRowData = this.selectionList[0]
          this.waitCreateShow = true
        }
      } else if (command == 'confirm') { // 移动到待入职/入职 或 入职
        if (this.selectionList.length) {
          const selectionObj = this.selectionList[0]
          hrmEmployeeQueryByIdAPI(selectionObj.employeeId)
            .then(res => {
              const data = res.data || {}
              data.entryTime = timeToFormatTime(data.entryTime)
              this.handleRowData = data
              this.waitCreateShow = true
            })
            .catch(() => {
            })
        }
      }
    },

    moreHandlesChange(command) {
      this.formAddCommand = command
      if (command == 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmRecruitCandidateDeleteByIdsAPI(this.selectionList.map(item => item.candidateId)).then(res => {
              this.$message.success('操作成功')
              this.refreshList()
              this.selectionList = []
            })
          })
          .catch(() => {
          })
      } else if (command == 'changeChannel') {
        this.formAddTitle = '更改招聘渠道'
        this.formAddForm = {
          channelId: ''
        }
        this.formAddRules = {
          channelId: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ]
        }

        this.getChannelList().then(() => {
          this.formAddFields = [{
            name: '招聘渠道',
            field: 'channelId',
            formType: 'select',
            setting: this.channelList
          }]
          this.formAddDialogShow = true
        })
      } else if (command == 'changePost') {
        this.formAddTitle = '更改应聘职位'
        this.formAddForm = {
          postId: ''
        }
        this.formAddRules = {
          postId: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ]
        }

        this.getPostList().then(() => {
          this.formAddFields = [{
            name: '职位',
            field: 'postId',
            formType: 'select',
            setting: this.postList
          }]
          this.formAddDialogShow = true
        })
      } else if (command == 'clear') {
        this.clearTips = ''
        this.formAddTitle = '一键清理候选人'
        this.formAddForm = {
          status: [],
          day: 3
        }
        this.formAddRules = {
          status: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ],
          day: [
            { required: true, message: '请选择', trigger: ['blur', 'change'] }
          ]
        }

        this.getPostList().then(() => {
          this.formAddFields = [{
            name: '清理的候选人状态',
            field: 'status',
            formType: 'checkbox',
            setting: [{
              label: '新候选人',
              value: candidateModel.statusKeyValue.new
            }, {
              label: '初选通过',
              value: candidateModel.statusKeyValue.firstPass
            }, {
              label: '安排面试',
              value: candidateModel.statusKeyValue.interview
            }, {
              label: '面试通过',
              value: candidateModel.statusKeyValue.pass
            }]
          }, {
            name: '清理的时间段',
            field: 'day',
            formType: 'select',
            setting: [{
              label: '3天',
              value: 3
            }, {
              label: '5天',
              value: 5
            }, {
              label: '7天',
              value: 7
            }, {
              label: '15天',
              value: 15
            }, {
              label: '30天',
              value: 30
            }, {
              label: '45天',
              value: 45
            }]
          }]
          this.formAddDialogShow = true
        })
      } else if (command == 'changeInterview') {
        this.statusHandleChange('changeInterview')
      } else if (command == 'cancelInterview') {
        this.formAddTitle = '取消面试'
        const data = this.selectionList[0]
        this.formAddForm = {
          cancelReason: '',
          interviewTime: data.interviewTime,
          interviewEmployeeName: data.interviewEmployeeName,
          otherInterviewEmployeeName: data.otherInterviewEmployeeName,
          stageNum: data.stageNum,
          interviewType: candidateModel.interviewTypeValue[data.interviewType]
        }
        this.formAddRules = {}
        this.formAddFields = [{
          name: '面试时间',
          field: 'interviewTime',
          formType: 'text',
          disabled: true,
          setting: []
        }, {
          name: '面试方式',
          field: 'interviewType',
          formType: 'text',
          disabled: true,
          setting: []
        }, {
          name: '面试官',
          field: 'interviewEmployeeName',
          formType: 'text',
          disabled: true,
          setting: []
        }, {
          name: '其他面试官',
          field: 'otherInterviewEmployeeName',
          formType: 'text',
          disabled: true,
          setting: []
        }, {
          name: '面试轮次',
          field: 'stageNum',
          formType: 'text',
          disabled: true,
          setting: []
        }, {
          name: '取消原因',
          field: 'cancelReason',
          formType: 'textarea',
          setting: []
        }]
        this.formAddDialogShow = true
      }
    },

    formAddPass() {
      this.$refs.formAdddialog.loading = true
      let params = objDeepCopy(this.formAddForm)
      params.candidateIds = this.selectionList.map(item => item.candidateId)
      let request = null
      if (this.formAddCommand === 'changePost') {
        request = hrmRecruitCandidateUpdatePostAPI
      } else if (this.formAddCommand === 'changeChannel') {
        request = hrmRecruitCandidateUpdateChannelAPI
      } else if (this.formAddCommand === 'clear') {
        // if (!this.clearList || this.clearList.length == 0) {
        //   this.$message.error('')
        //   return ''
        // }
        request = hrmRecruitCandidateUpdateStatusAPI
        params = {
          candidateIds: this.clearList,
          status: 7
        }
      } else if (['interview', 'again', 'changeInterview', 'cancelInterview'].includes(this.formAddCommand)) {
        if (this.formAddCommand === 'cancelInterview') {
          request = hrmRecruitInterviewSetResultAPI
        } else {
          request = hrmRecruitInterviewAddBatchAPI
        }

        if (this.formAddCommand === 'changeInterview') {
          params.candidateId = this.selectionList[0].candidateId
          delete params.candidateIds
        } else if (this.formAddCommand === 'cancelInterview') {
          // result 面试情况 1面试未完成 2面试通过 3面试未通过 4 面试取消
          params = {
            result: 4,
            candidateId: this.selectionList[0].candidateId,
            cancelReason: this.formAddForm.cancelReason
          }
        }
      } else if (this.formAddCommand === 'eliminate') {
        request = hrmRecruitCandidateEliminateAPI
      }

      request(params).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.refreshList()
        this.selectionList = []
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    },

    formChange(item, index, value, valueList, editForm) {
      if (this.formAddCommand == 'clear') {
        this.getEliminateData(editForm)
      }
    },

    /**
     * 查询淘汰信息的操作
     */
    getEliminateData(data) {
      this.clearTips = ''
      if (!data.status || data.status.length == 0) {
        this.clearList = []
      } else {
        hrmRecruitCandidateQueryCleanDataAPI({
          status: data.status,
          day: data.day
        }).then(res => {
          this.clearList = res.data || []
          const statusName = data.status.map(item => candidateModel.statusValue[item]).join('、')
          this.clearTips = `${statusName}状态下，超过${data.day}天未改变状态的候选人共有${this.clearList.length}个，确认后都移到淘汰状态`
        }).catch(() => {
        })
      }
    },

    /**
     * 修改状态的操作
     */
    updateStatus(status) {
      this.loading = true
      hrmRecruitCandidateUpdateStatusAPI({
        candidateIds: this.selectionList.map(item => item.candidateId),
        status
      }).then(res => {
        this.loading = false
        this.$message.success('操作成功')
        this.refreshList()
        this.selectionList = []
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 通过路由等路由携带的需要处理的事情
     * @return {*}
     */
    initTask() {
      const { query } = this.$route
      if (query.event === 'detail') {
        this.rowID = query.id
        this.showDview = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.candidate-index {
  .xr-header {
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  .wk-border-menu {
    margin-bottom: 8px;
  }

  ::v-deep.xr-table-header {
    border-bottom: 1px solid $--border-color-base;

    .xr-table-header__body {
      padding: 0;
    }
  }

  .search-button {
    border: none;

    &.is-select {
      color: white;
      background: $--color-primary;
    }
  }

  ::v-deep .el-tabs {
    .el-tabs__header {
      margin-bottom: 0;
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

  .table-more {
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }

  .el-dropdown-link {
    .el-icon-arrow-down {
      color: white;
      cursor: pointer;
    }

    &.is-more {
      .el-icon-arrow-down {
        color: $--color-text-regular;
      }
    }
  }

  .status-mark {
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: $--border-radius-base;
  }
}
</style>
