<template>
  <div id="employee-main-container" class="employee-index">
    <xr-header
      ft-top="0"
      placeholder="请输入员工姓名"
      show-search
      label="员工管理"
      @search="searchClick">
      <template slot="otherLabel">
        <i
          class="wk wk-icon-fill-help wk-help-tips"
          data-type="31"
          data-id="287" />
      </template>
      <template slot="bottom-ft">
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom-end"
          @sure="refreshList"
          @reset="resetFilter"
        />
      </template>
      <template slot="ft">

        <template v-if="createAuth">
          <el-button
            type="primary"
            @click="createClick">新建员工</el-button>
          <!-- <el-button
            plain
            @click="createFromDepClick">从组织架构中选择</el-button> -->
        </template>
        <el-dropdown
          v-if="headerMoreHandle.length > 0"
          trigger="click"
          style="margin-left: 5px;"
          @command="headerMoreHandleClick">
          <el-button icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in headerMoreHandle"
              :key="index"
              :icon="item.icon"
              :command="item.type">{{ item.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </xr-header>
    <div class="crm-container">
      <flexbox class="filter">
        <wk-border-menu
          v-model="tabType"
          :list="tabLeftList"
          :is-select="!!tabLeftList.find(item => item.name == tabType)"
          style="flex: 6;"
          @select="tabClick"
        />
        <wk-border-menu
          v-model="tabType"
          :list="tabCenterList"
          :is-select="!!tabCenterList.find(item => item.name == tabType)"
          style="flex: 2;"
          @select="tabClick"
        />
        <wk-border-menu
          v-model="tabType"
          :list="tabRightList"
          :is-select="!!tabRightList.find(item => item.name == tabType)"
          style="flex: 3;"
          @select="tabClick"
        />
      </flexbox>
      <xr-table-header
        v-if="selectionList.length > 0"
        :handles="tabelHandles"
        :selects="selectionList"
        @command="handleCommand">
        <!-- <el-tabs v-model="tabType" style="width: 100%;margin-bottom: -8px;" @tab-click="createSaveSuccess">
          <el-tab-pane
            v-for="tab in tabList"
            :key="tab.name"
            :name="tab.name">
            <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
          </el-tab-pane>
        </el-tabs> -->
      </xr-table-header>
      <el-table
        id="crm-table"
        v-loading="loading"
        :row-height="44"
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
        @header-dragend="handleHeaderDragend"
        @selection-change="handleSelectionChange"
        @sort-change="sortChange">
        <el-table-column
          v-if="deleteAuth || multiInsuredShow"
          show-overflow-tooltip
          fixed
          type="selection"
          align="center"
          width="55" />
        <el-table-column
          v-for="(item, index) in showFieldList"
          :key="index"
          :fixed="index == 0"
          :prop="item.fieldName"
          :label="item.name"
          :sortable="item.sortable"
          :min-width="item.width"
          :formatter="fieldFormatter"
          show-overflow-tooltip />
        <el-table-column />
        <el-table-column
          v-if="moreHandleShow"
          label="操作"
          fixed="right"
          width="100">
          <template slot-scope="scope">
            <el-dropdown
              v-if="getRowDropdownShow(scope.row)"
              trigger="click"
              @command="handleTypeClick($event, scope.row)">
              <i
                class="el-icon-more table-more" />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(item, index) in getRowDropdownItems(scope.row)"
                  :key="index"
                  :icon="item.icon"
                  :command="item.command">{{ item.label }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column />
        <!-- <el-table-column
          :resizable="false"
          fixed="right"
          width="40">
          <template
            slot="header"
            slot-scope="slot">
            <wk-field-set
              :loading="fieldSetLoading"
              :fields="setFieldList"
              @save="fieldSetSave" />
          </template>
        </el-table-column> -->
        <wk-field-set
          slot="other"
          class="field-set-wrap"
          :loading="fieldSetLoading"
          :fields="setFieldList"
          @save="fieldSetSave" />
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
    <employee-detail
      v-if="showDview"
      :id="rowID"
      @handle="detailHandle"
      @close="showDview=false" />
    <employee-create-view
      v-if="isCreate"
      :create-type="createType"
      :detail="handleRowData"
      @success="createSaveSuccess"
      @close="isCreate=false" />

    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      :help-obj="helpObj"
      @pass="formAddPass"
      @change="formChange"
    />

    <update-scheme-dialog
      :id="handleRowData ? handleRowData.employeeId : ''"
      :list="selectionList"
      :type="updateSchemeType"
      :visible.sync="updateSchemeDialogShow"
      @change="getList"
    />

    <give-up-leave-dialog
      :id="handleRowData ? handleRowData.employeeId : ''"
      :visible.sync="giveUpLeaveShow"
      @change="refreshPageData"
    />

    <dep-add-employ-dialog
      v-if="depAddEmplogyShow"
      :visible.sync="depAddEmplogyShow"
      @change="refreshPageData"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeQueryPageListAPI,
  hrmEmployeeFieldListHeadsAPI,
  hrmEmployeeQueryStatusNumAPI,
  hrmEmployeeDeleteByIdsAPI,
  hrmEmployeeSetBecomeAPI,
  hrmEmployeeSetChangePostAPI,
  hrmEmployeeSetPromotionAPI,
  hrmEmployeeFieldUpdateConfigAPI,
  hrmEmployeeFieldUpdateWidthAPI,
  hrmEmployeeExportAPI,
  hrmEmployeeDownloadExportAPI,
  hrmEmployeeUploadExportAPI
} from '@/api/hrm/employee'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
import {
  hrmEmployeePostAddLeaveAPI
} from '@/api/hrm/employeePost'

import XrHeader from '@/components/XrHeader/Search'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import WkFieldSet from '@/components/NewCom/WkFieldSet'
import EmployeeCreateView from './components/EmployeeCreateView'
import EmployeeDetail from './EmployeeDetail'
import FormAddDialog from './components/FormAddDialog'
import UpdateSchemeDialog from './components/UpdateSchemeDialog'
import GiveUpLeaveDialog from './components/GiveUpLeaveDialog'
import DepAddEmployDialog from './components/DepAddEmployDialog' // 从系统管理导入员工
import WkBorderMenu from '../components/WkBorderMenu'

import { mapGetters } from 'vuex'
import { employeeModel, educationModel, officialModel, changePostModel } from './model/employee'
import employeePost from './model/employeePost'
import employeeContract from './model/employeeContract'
import { objDeepCopy, downloadExcelWithResData } from '@/utils'

export default {
  name: 'EmployeeIndex',
  components: {
    XrHeader,
    XrTableHeader,
    EmployeeCreateView,
    EmployeeDetail,
    WkPopoverFilter,
    WkFieldSet,
    FormAddDialog,
    UpdateSchemeDialog,
    GiveUpLeaveDialog,
    DepAddEmployDialog,
    WkBorderMenu
  },
  beforeRouteUpdate(to, from, next) {
    const query = this.$route.query
    if (query.depAddEmplogyShow) {
      this.depAddEmplogyShow = true
    }
    next()
  },
  data() {
    return {
      loading: false, // 加载动画
      createType: '', // 创建类型
      isCreate: false, // 是创建
      tableHeight: document.documentElement.clientHeight - 345, // 表的高度
      list: [],
      fieldList: [],
      // 性别、出生日期、年龄、入职日期、转正日期、部门、工号、生日
      sortFields: ['sex', 'dateOfBirth', 'age', 'entryTime', 'becomeTime', 'fieldName', 'jobNumber', 'birthday', 'deptId'],
      sortData: {}, // 字段排序
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      queryParams: {}, // query 传入参数
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      tabType: '12',
      // 员工状态 1正式 2试用 3实习 4兼职 5劳务 6顾问 7返聘 8外包 13待离职 14待入职  15已离职
      tabLeftList: [{
        label: '在职',
        name: '11',
        num: 0
      }, {
        label: '全职',
        name: '12',
        num: 0
      }, {
        label: '实习',
        name: '3',
        num: 0
      }, {
        label: '劳务',
        name: '5',
        num: 0
      }, {
        label: '顾问',
        name: '6',
        num: 0
      }, {
        label: '返聘',
        name: '7',
        num: 0
      }, {
        label: '外包',
        name: '8',
        num: 0
      }, {
        label: '兼职',
        name: '4',
        num: 0
      }],
      tabCenterList: [{
        label: '试用',
        name: '2',
        num: 0
      }, {
        label: '正式',
        name: '1',
        num: 0
      }],
      tabRightList: [{
        label: '待入职',
        name: '13',
        num: 0
      }, {
        label: '待离职',
        name: '14',
        num: 0
      }, {
        label: '已离职',
        name: '15',
        num: 0
      }],
      /** 控制详情展示 */
      rowID: '', // 行信息
      showDview: false,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      filterList: [
      //   {
      //   name: '姓名',
      //   field: 'employeeName',
      //   formType: 'text',
      //   setting: []
      // },
        {
          name: '手机号码',
          field: 'mobile',
          formType: 'text',
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
        }, {
          name: '工作地点',
          field: 'workAddress',
          formType: 'text',
          setting: []
        }, {
          name: '聘用形式',
          field: 'employmentForms',
          formType: 'select',
          setting: [{
            label: '正式',
            value: 1
          }, {
            label: '非正式',
            value: 2
          }]
        }, {
          name: '入职日期',
          field: 'entryTime',
          formType: 'dateRange',
          setting: []
        }, {
          name: '岗位',
          field: 'post',
          formType: 'text',
          setting: []
        }, {
          name: '转正日期',
          field: 'becomeTime',
          formType: 'dateRange',
          setting: []
        }, {
          name: '工号',
          field: 'jobNumber',
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
        }],
      selectionList: [], // 勾选数据 用于全局导出
      // 弹窗添加
      formAddType: '', // 事件类型
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 编辑详情
      // 需要编辑的员工详情
      handleRowData: null,
      fieldSetLoading: false,
      // 修改社保
      updateSchemeType: 1, // 1 一个  2  多个
      updateSchemeDialogShow: false,
      // 放弃离职
      giveUpLeaveShow: false,
      // 从系统管理导入员工
      depAddEmplogyShow: false,
      helpObj: {
        dataType: 31,
        dataId: 288
      }
    }
  },
  computed: {
    ...mapGetters([
      'hrm',
      'hrmUserType',
      'userInfo'
    ]),
    // 员工权限
    employeeAuth() {
      return this.hrm.employee
    },

    // 员工新建
    createAuth() {
      return this.employeeAuth && this.employeeAuth.save
    },

    // 删除权限
    deleteAuth() {
      return this.employeeAuth && this.employeeAuth.delete
    },

    // 导出权限
    excelexportAuth() {
      return this.employeeAuth && this.employeeAuth.excelexport
    },

    // 导入权限
    excelimportAuth() {
      return this.employeeAuth && this.employeeAuth.excelimport
    },

    // 员工详情
    detailAuth() {
      return this.employeeAuth && this.employeeAuth.read
    },

    // 更多操作展示
    moreHandleShow() {
      return this.employeeAuth.confirmEntry ||
      this.employeeAuth.againOnboarding ||
      this.employeeAuth.become ||
      this.employeeAuth.changePost ||
      this.employeeAuth.promotion ||
      this.employeeAuth.setInsured ||
      this.employeeAuth.cancelLevel ||
      this.employeeAuth.leave
    },

    /**
     * 头部更多操作
     */
    headerMoreHandle() {
      const temps = []
      if (this.excelimportAuth) {
        temps.push({ type: 'import', name: '导入', icon: 'wk wk-import' })
      }

      if (this.excelexportAuth) {
        temps.push({ type: 'export', name: '导出', icon: 'wk wk-export' })
      }

      return temps
    },

    // 操作
    tabelHandles() {
      const temps = []
      if (this.deleteAuth) {
        temps.push({
          label: '删除',
          command: 'delete',
          icon: 'wk wk-delete'
        })
      }

      if (this.multiInsuredShow) {
        temps.push({
          label: '参保方案',
          command: 'security',
          icon: 'wk wk-approval-9'
        })
      }

      if (this.excelexportAuth) {
        temps.push({
          label: '导出选中',
          command: 'export',
          icon: 'wk wk-export'
        })
      }
      return temps
    },

    // 多员工社保曹组哦权限
    multiInsuredShow() {
      return this.employeeAuth.setInsured && (
        this.tabType == '12' ||
        this.tabType == '2' ||
        this.tabType == '1'
      )
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

    // 表头
    showFieldList() {
      return this.fieldList.filter(item => item.isHide != 1)
    },

    // 设置字段
    setFieldList() {
      const showList = []
      const hideList = []
      for (let index = 0; index < this.fieldList.length; index++) {
        const item = objDeepCopy(this.fieldList[index])
        item.left = ''
        item.center = ''
        item.right = ''
        item.check = item.isHide != 1
        if (item.check) {
          showList.push(item)
        } else {
          hideList.push(item)
        }
      }

      return showList.concat(hideList)
    },

    formAddTitle() {
      return {
        confirm: '',
        again: '',
        official: '办理转正',
        'change-post': '调整部门/岗位',
        'change-level': '晋升/降级',
        security: '',
        'give-up-leave': '',
        leave: '办理离职'
      }[this.formAddType]
    }
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 395 : 345)
    }

    // 监听导入
    this.$bus.on('import-crm-done-bus', () => {
      this.refreshPageData()
    })

    // 加入query筛选 workbench 工作台
    const query = this.$route.query
    if (query) {
      if (query.workbench == 'filter') {
        const queryParams = this.$route.query
        if (queryParams && queryParams.tabType) {
          this.tabType = queryParams.tabType
        } else {
          this.tabType = ''
          this.queryParams = queryParams
          delete this.queryParams['workbench']
        }
      } else if (query.depAddEmplogyShow) {
        this.depAddEmplogyShow = true
      }
    }

    this.getFieldList()
    this.refreshList()
    this.getTabsNum()
  },
  mounted() {
    // 路由配置事件
    this.initTask()
  },
  beforeDestroy() {
    this.$bus.off('import-crm-done-bus')
  },
  methods: {
    /**
     * 刷新当前页面信息
     */
    refreshPageData() {
      // 刷新当前人状态
      this.$store.dispatch('GetHrmUserInfo')
      this.getList()
      this.getTabsNum()
    },

    /**
     * 获取tab 数量
     */
    getTabsNum() {
      hrmEmployeeQueryStatusNumAPI()
        .then(res => {
          const data = res.data || {}
          this.tabLeftList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
          this.tabCenterList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
          this.tabRightList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
        })
        .catch(() => {
        })
    },

    /**
     * tab点击
     */
    tabClick(tab) {
      this.queryParams = {} // 切换置空
      this.refreshList()
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
    createClick() {
      this.handleRowData = null
      this.createType = 'new'
      this.isCreate = true
    },

    /**
     * 新建从组织
     */
    createFromDepClick() {
      this.depAddEmplogyShow = true
    },

    /**
     * 创建成功
     */
    createSaveSuccess() {
      this.getTabsNum()
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
        // 刷新当前人状态
        this.$store.dispatch('GetHrmUserInfo')
        this.showDview = false
      }
      this.getTabsNum()
      this.getList()
    },

    /**
     * 获取表头展示字段
     */
    getFieldList() {
      hrmEmployeeFieldListHeadsAPI()
        .then(res => {
          const fieldList = res.data || []
          fieldList.forEach(item => {
            item.sortable = this.sortFields.includes(item.fieldName)
          })
          this.fieldList = fieldList
        })
        .catch(() => {
        })
    },

    /**
     * 刷新
     */
    refreshList() {
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
     * 字段排序
     */
    sortChange(column, prop, order) {
      this.currentPage = 1
      this.sortData = column
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
        employeeName: this.search,
        status: this.tabType
      }

      for (const key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      for (const key in this.queryParams) {
        params[key] = this.queryParams[key]
      }

      if (this.sortData.order) {
        params.sortField = this.sortData.prop
        params.order = this.sortData.order == 'ascending' ? 2 : 1
      }

      hrmEmployeeQueryPageListAPI(params)
        .then(res => {
          this.list = res.data.list
          this.total = res.data.totalRow
          if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
            this.currentPage = this.currentPage - 1
            this.getList()
          } else {
            this.total = res.data.totalRow
            this.loading = false
          }

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
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const valueObj = employeeModel[`${column.property}Value`]
      if (valueObj) {
        return valueObj[row[column.property]] || '--'
      } else if (column.property == 'highestEducation') {
        return educationModel.educationValue[row[column.property]] || '--'
      } else if (column.property == 'contractType') {
        return employeeContract.contractTypeValue[row.contractType] || '--'
      } else if (column.property == 'term') {
        return employeeContract.termValue[row.term] || '--'
      } else if (column.property == 'deptId') {
        return row.deptName
      } else if (column.property == 'parentId') {
        return row.parentName
      } else if (column.property == 'channelId') {
        return row.channelName
      } else {
        return row[column.property] || '--'
      }
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (this.detailAuth && column.property == 'employeeName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'delete') {
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmEmployeeDeleteByIdsAPI(this.selectionList.map(item => item.employeeId))
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '删除成功'
                })
                this.getTabsNum()
                this.getList()
              })
              .catch(() => {})
          })
          .catch(() => {})
      } else if (command === 'security') {
        this.updateSchemeType = 2
        this.updateSchemeDialogShow = true
      } else if (command === 'export') {
        this.loading = true
        hrmEmployeeExportAPI({
          employeeIds: this.selectionList.map(item => item.employeeId)
        }).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    },

    formChange(item, index, value, valueList, editForm) {
      if ((this.formAddType === 'change-post' || this.formAddType === 'change-level') && item.field == 'changeType') {
        if (value == 7) {
          editForm.changeReason = 4
        } else {
          editForm.changeReason = 1
        }
        this.formAddFields = changePostModel.fieldsFunc(value, this.handleRowData.status)
      } else if (this.formAddType === 'leave' && item.field == 'quitType') {
        if (value == 1) {
          editForm.quitReason = 1
        } else if (value == 2) {
          editForm.quitReason = 11
        } else if (value == 3) {
          editForm.quitReason = ''
        }
        this.formAddRules = employeePost.getRules(editForm)
        this.formAddFields = employeePost.getFields(editForm)
      }
    },

    /**
     * 头部更多操作
     */
    headerMoreHandleClick(command) {
      if (command == 'export') {
        this.loading = true
        const params = {
          employeeName: this.search,
          status: this.tabType
        }

        for (const key in this.filterObj) {
          params[key] = this.filterObj[key]
        }

        for (const key in this.queryParams) {
          params[key] = this.queryParams[key]
        }

        hrmEmployeeExportAPI(params).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      } else if (command == 'import') {
        this.$wkImport.import('hrm', {
          ownerSelectShow: false,
          repeatRuleShow: false,
          importRequest: hrmEmployeeUploadExportAPI, // 导入请求
          templateRequest: hrmEmployeeDownloadExportAPI, // 模板请求
          userInfo: this.userInfo
        })
      }
    },

    /**
     * 获取行操作
     */
    getRowDropdownItems(data) {
      const dropdownItems = []
      // 入职状态 1 在职 2 待入职 3 待离职 4 离职
      if (data.entryStatus == 2) {
        // 确认入职 操作
        if (this.employeeAuth.confirmEntry) {
          dropdownItems.push({
            label: '确认入职',
            command: 'confirm',
            icon: 'wk wk-activation'
          })
        }
      } else if (data.entryStatus == 4) {
        // 已离职 操作
        if (this.employeeAuth.againOnboarding) {
          dropdownItems.push({
            label: '再入职',
            command: 'again',
            icon: 'wk wk-approval-17'
          })
        }
      } else {
        // 试用状态操作
        if (data.status == 2) {
          if (this.employeeAuth.become) {
            dropdownItems.push({
              label: '办理转正',
              command: 'official',
              icon: 'wk wk-transfer'
            })
          }
        }

        // 非已离职 待入职 操作
        if (this.employeeAuth.changePost) {
          dropdownItems.push({
            label: '调整部门/岗位',
            command: 'change-post',
            icon: 'wk wk-employees'
          })
        }

        if (this.employeeAuth.promotion) {
          dropdownItems.push({
            label: '晋升/降级',
            command: 'change-level',
            icon: 'wk wk-approval-12'
          })
        }

        // 全职操作
        if (data.status == 1 || data.status == 2) {
          if (this.employeeAuth.setInsured) {
            dropdownItems.push({
              label: '参保方案',
              command: 'security',
              icon: 'wk wk-approval-9'
            })
          }
        }

        // 3 待离职
        if (data.entryStatus == 3) {
          if (this.employeeAuth.cancelLevel) {
            dropdownItems.push({
              label: '放弃离职',
              command: 'give-up-leave',
              icon: 'wk wk-reset'
            })
          }
        } else {
          if (this.employeeAuth.leave) {
            dropdownItems.push({
              label: '办理离职',
              command: 'leave',
              icon: 'wk wk-approval-16'
            })
          }
        }
      }

      // dropdownItems.push({
      //   label: '删除',
      //   command: 'delete',
      //   icon: 'wk wk-reset'
      // })

      // 1 和 2 是 全职    不是15的是在职
      // 实习 调整部门/岗位、晋升/降级 办理离职 删除
      // 试用 办理转正 办理离职 调整部门/岗位 晋升/降级 删除 2
      // 待离职 调整离职信息 放弃离职 删除 调整部门/岗位、晋升/降级 13
      // 正式 办理离职 调整部门/岗位 晋升/降级 删除 1
      // 已离职 再入职 删除 15
      return dropdownItems
    },

    getRowDropdownShow(data) {
      // 入职状态 1 在职 2 待入职 3 待离职 4 离职
      if (data.entryStatus == 2) {
        // 确认入职 操作
        if (this.employeeAuth.confirmEntry) {
          return true
        }
      } else if (data.entryStatus == 4) {
        // 已离职 操作
        if (this.employeeAuth.againOnboarding) {
          return true
        }
      } else {
        // 试用状态操作
        if (data.status == 2) {
          if (this.employeeAuth.become) {
            return true
          }
        }

        // 非已离职 待入职 操作
        if (this.employeeAuth.changePost) {
          return true
        }

        if (this.employeeAuth.promotion) {
          return true
        }

        // 全职操作
        if (data.status == 1 || data.status == 2) {
          if (this.employeeAuth.setInsured) {
            return true
          }
        }

        // 3 待离职
        if (data.entryStatus == 3) {
          if (this.employeeAuth.cancelLevel) {
            return true
          }
        } else {
          if (this.employeeAuth.leave) {
            return true
          }
        }
      }

      // dropdownItems.push({
      //   label: '删除',
      //   command: 'delete',
      //   icon: 'wk wk-reset'
      // })

      // 1 和 2 是 全职    不是15的是在职
      // 实习 调整部门/岗位、晋升/降级 办理离职 删除
      // 试用 办理转正 办理离职 调整部门/岗位 晋升/降级 删除 2
      // 待离职 调整离职信息 放弃离职 删除 调整部门/岗位、晋升/降级 13
      // 正式 办理离职 调整部门/岗位 晋升/降级 删除 1
      // 已离职 再入职 删除 15
      return false
    },

    handleTypeClick(command, data) {
      // 行编辑信息
      this.handleRowData = data

      if (command === 'leave') {
        this.formAddType = command
        this.formAddForm = {
          quitType: 1,
          quitReason: 1
        }
        this.formAddRules = employeePost.getRules(this.formAddForm)
        this.formAddFields = employeePost.getFields(this.formAddForm)
        this.formAddDialogShow = true
      } else if (command === 'official') {
        this.formAddType = command
        this.formAddForm = {
          newDept: data.deptId || '',
          newParentId: data.parentId || '',
          newPost: data.post || '',
          becomeTime: data.becomeTime,
          changeType: 4 // 变动类型 4 转正 5调岗 6晋升 7降级 8转为全职员工
        }
        this.formAddRules = officialModel.rules
        this.formAddFields = officialModel.fields
        this.formAddDialogShow = true
      } else if (command === 'again') {
        this.handleRowData = data
        this.createType = 'again'
        this.isCreate = true
      } else if (command === 'change-post' || command === 'change-level') {
        this.formAddType = command
        this.formAddForm = {
          changeType: command === 'change-post' ? 5 : 6,
          changeReason: 1,
          oldDept: data.deptId || '',
          newDept: '',
          oldPost: data.post || '',
          newPost: '',
          oldPostLevel: data.postLevel || '',
          newPostLevel: '',
          oldWorkAddress: data.workAddress || '',
          newWorkAddress: '',
          entryTime: data.entryTime || ''
        }
        this.formAddRules = changePostModel.rules
        // 只有员工的“工作性质”为“实习、兼职”时，才可以“转全职员工”
        this.formAddFields = changePostModel.fieldsFunc(this.formAddForm.changeReason, data.status)
        this.formAddDialogShow = true
      } else if (command === 'give-up-leave') {
        this.giveUpLeaveShow = true
        // this.$confirm('确定要放弃离职吗？', '提示', {
        //   confirmButtonText: '确定',
        //   cancelButtonText: '取消',
        //   type: 'warning'
        // })
        //   .then(() => {
        //     hrmEmployeePostDeleteLeaveAPI(data.employeeId)
        //       .then(res => {
        //         this.$message({
        //           type: 'success',
        //           message: '操作成功'
        //         })
        //         this.getTabsNum()
        //         this.getList()
        //       })
        //       .catch(() => {})
        //   })
        //   .catch(() => {})
      } else if (command === 'security') {
        this.updateSchemeType = 1
        this.selectionList = [data]
        this.updateSchemeDialogShow = true
      } else if (command === 'confirm') {
        this.handleRowData = data
        this.createType = 'confirm'
        this.isCreate = true
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (this.detailAuth && column.property == 'employeeName') {
        this.rowID = row.employeeId
        this.showDview = true
      }
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 395 : 345)
    },

    /**
     * 勾选操作
     */
    formAddPass() {
      this.$refs.formAdddialog.loading = true
      this.formAddForm.employeeId = this.handleRowData.employeeId
      let request = null
      if (this.formAddType === 'leave') {
        request = hrmEmployeePostAddLeaveAPI
      } else if (this.formAddType === 'official') {
        request = hrmEmployeeSetBecomeAPI
      } else if (this.formAddType === 'change-post') {
        request = hrmEmployeeSetChangePostAPI
      } else if (this.formAddType === 'change-level') {
        request = hrmEmployeeSetPromotionAPI
      }

      request(this.formAddForm).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getTabsNum()
        this.getList()
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    },

    fieldSetSave(fields) {
      this.fieldSetLoading = true
      fields.forEach((item, index) => {
        item.isHide = item.check ? 0 : 1
        item.sort = index
      })

      hrmEmployeeFieldUpdateConfigAPI(fields)
        .then(res => {
          this.$message.success('操作成功')
          this.getFieldList()
          this.fieldSetLoading = false
        })
        .catch(() => {
          this.fieldSetLoading = false
        })
    },

    handleHeaderDragend(newWidth, oldWidth, column, event) {
      if (column.property) {
        const currentItem = this.showFieldList.find(item => item.fieldName == column.property)
        if (currentItem) {
          hrmEmployeeFieldUpdateWidthAPI({
            width: newWidth,
            fieldId: currentItem.fieldId
          })
            .then(res => {
            })
            .catch(() => { })
        }
      }
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
.employee-index {
  .xr-header {
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
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

  .filter {
    margin-bottom: 8px;

    .wk-border-menu + .wk-border-menu {
      margin-left: 20px;
    }
  }
}

.field-set-wrap {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 99;
}
</style>
