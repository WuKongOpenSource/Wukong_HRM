<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 13:37:31
 * @LastEditTime: 2023-07-12 16:23:52
 * @LastEditors: chenhaojie 1476192083@qq.com
-->
<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header
        subtitle="绩效详情"
        :title="detailData.appraisalName"
        @edit="editClick"
        @command-select="commandSelect"
      >
        <template slot="left">
          <template v-if="detail">
            <span v-if="detail.status == 2" class="tip-btn-start">未开始</span>
            <span v-if="detail.status == 3" class="tip-btn-ing">进行中</span>
            <span v-if="detail.status == 4" class="tip-btn-end">已归档</span>
          </template>
        </template>
        <template slot="right">
          <el-button
            type="primary"
            @click="headerRightClick('confirm')">确定启动</el-button>
          <el-button
            v-if="addEmployeeAuth"
            type="primary"
            @click="headerRightClick('add')">新增考核人员</el-button>
        </template>
      </wk-detail-header>
      <div class="d-container-body">
        <detail-head-base :list="headDetails" />
        <div class="top-padding">
          <flexbox class="filter-wrap">
            <el-input
              v-model.trim="search"
              placeholder="请输入姓名"
              class="search-input"
              @keyup.enter.native="searchInput">
              <el-button
                slot="suffix"
                type="icon"
                icon="wk wk-sousuo"
                @click="searchInput" />
            </el-input>

            <el-cascader
              v-model="employStatus"
              :options="options"
              :props="{ multiple: true,emitPath:false }"
              collapse-tags
              placeholder="请选择聘用形式"
              style="margin-left: 10px;"
              clearable
              @visible-change="visibleChange"
              @remove-tag="removeTag"
              @change="clearCheckedNodes" />
          </flexbox>
          <el-table
            id="crm-table"
            :data="list"
            :height="tableHeight"
            stripe
            style="width: 100%;margin-top: 15px;"
            @row-click="handleRowClick">
            <el-table-column
              v-for="(item, index) in fieldList"
              :key="index"
              :prop="item.key"
              :label="item.name"
              :min-width="item.width"
              show-overflow-tooltip />
            <!-- <el-table-column /> -->
            <el-table-column
              label="操作"
              fixed="right"
              width="150">
              <template slot-scope="{ row, $index }">
                <el-button
                  v-if="delEmployeeAuth"
                  class="delete-btn"
                  type="text"
                  @click="removeClick(row, $index)">
                  移除
                </el-button>
              </template>
            </el-table-column>
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
      </div>

      <!-- 选择人资员工 -->
      <hrm-employee-dialog
        v-if="employeeDialogShow"
        :visible.sync="employeeDialogShow"
        @change="selectChange"
      />

    </flexbox>
  </slide-view>
</template>

<script>
import {
  hrmAppraisalPlanQueryEmployeeListHeadInfoAPI,
  hrmAppraisalPlanQueryEmployeeListAPI,
  hrmAppraisalPlanAddEmployeeListAPI,
  hrmAppraisalPlanDelEmployeeListAPI,
  hrmAppraisalPlanStartAppraisalPlanAPI
} from '@/api/hrm/performance'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/Page/WkDetailHeader'
import DetailHeadBase from '../../components/DetailHeadBase'
import HrmEmployeeDialog from '../../components/HrmEmployeeDialog'

import { mapGetters } from 'vuex'

export default {
  // 未开始详情
  name: 'NotStartedDetail',
  components: {
    SlideView,
    WkDetailHeader,
    DetailHeadBase,
    HrmEmployeeDialog
  },

  inject: ['editAuth'],
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
        return ['el-table__body', 'org-tree-node-label']
      }
    },
    detail: {
      type: Object,
      default: () => {
        return null
      }
    }
  },
  data() {
    return {
      loading: false,
      detailData: null,
      tabCurrentName: 'baseInfo',
      dropdownItems: [{
        icon: 'wk wk-edit',
        label: '编辑',
        command: 'edit'
      }],
      // 编辑操作
      isCreate: false,

      list: [],
      fieldList: [{
        field: 'employeeName',
        name: '姓名'
      }, {
        field: 'deptName',
        name: '部门'
      }, {
        field: 'raterName',
        name: '自评'
      }],
      tableHeight: '600px',

      search: '',
      employStatus: [],
      options: [{
        // value: 1,
        label: '正式',
        children: [{
          value: 1,
          label: '正式'
        }, {
          value: 2,
          label: '试用'
        }]
      }, {
        // value: 2,
        label: '非正式',
        children: [{
          value: 3,
          label: '实习'
        }, {
          value: 4,
          label: '兼职'
        }, {
          value: 5,
          label: '劳务'
        }, {
          value: 6,
          label: '顾问'
        }, {
          value: 7,
          label: '返聘'
        }, {
          value: 8,
          label: '外包'
        }]
      }],

      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      headDetails: [],

      deptSelectIds: [],
      employeeDialogShow: false
    }
  },

  computed: {
    ...mapGetters(['hrm']),
    // 新增考核人员权限
    addEmployeeAuth() {
      return this.hrm.appraisalPlan && this.hrm.appraisalPlan.addAppraisalEmployeeList
    },

    // 移除考核人员权限
    delEmployeeAuth() {
      return this.hrm.appraisalPlan && this.hrm.appraisalPlan.delAppraisalEmployee
    }
  },
  watch: {
    id() {
      this.detailData = null
      this.getDetial()
      this.getFieldList()
      // this.getEmployeeList()
    }
  },
  mounted() {
    this.getDetial()
    this.getFieldList()
    // this.getEmployeeList()
  },

  beforeDestroy() {},
  methods: {
    viewAfterEnter() {
      this.getDetial()
    },

    /**
     * 详情
     */
    getDetial() {
      this.detailData = {
        appraisalName: this.detail && this.detail.appraisalPlanName
      }

      const headDetails = [
        { title: '考核范围', value: '' },
        { title: '考核周期类型', value: '' },
        { title: '考核说明', value: '' }
      ]
      headDetails[0].value = this.detail && this.detail.inspectionScopeName
      headDetails[1].value = this.detail && {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年',
        6: '其他'
      }[this.detail.appraisalCycleType]
      headDetails[2].value = this.detail && this.detail.appraisalIllustrate

      this.headDetails = headDetails
    },

    getFieldList() {
      this.loading = true
      hrmAppraisalPlanQueryEmployeeListHeadInfoAPI({
        id: this.id
      })
        .then(res => {
          this.loading = false
          this.fieldList = res.data
          this.getEmployeeList()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 员工列表
     */
    getEmployeeList() {
      const params = {
        appraisalPlanId: this.id,
        page: this.currentPage,
        limit: this.pageSize
      }

      if (this.search) {
        params.employeeNamer = this.search
      }
      if (this.employStatus.length) {
        params.employStatus = this.employStatus
      }
      this.loading = true
      hrmAppraisalPlanQueryEmployeeListAPI(params)
        .then(res => {
          console.log(res)
          this.list = res.data.list
          this.total = res.data.totalRow
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getEmployeeList()
    },

    // 筛选聘用形式下拉框消失时触发
    visibleChange(val) {
      if (!val) {
        this.getEmployeeList()
      }
    },

    // 移除聘用形式触发
    removeTag() {
      this.getEmployeeList()
    },

    // 移除所有筛选项
    clearCheckedNodes() {
      if (!this.employStatus.length) {
        this.getEmployeeList()
      }
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getEmployeeList()
    },

    /**
     * 搜索
     */
    searchInput() {
      this.getEmployeeList()
    },

    cellClassName() {},

    handleRowClick() {},

    /**
     * 移除员工
     */
    removeClick(row, index) {
      this.$confirm('确认要移除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          hrmAppraisalPlanDelEmployeeListAPI({
            appraisalPlanId: this.id,
            employeeIdList: [row.employeeId]
          })
            .then(res => {
              this.$message.success('移除成功')
              this.$emit('success')
              this.getEmployeeList()
            })
            .catch(() => {
            })
        })
        .catch(() => {})
    },

    /**
     * 右侧头部按钮点击
     */
    headerRightClick(type) {
      if (type == 'confirm') {
        this.loading = true
        hrmAppraisalPlanStartAppraisalPlanAPI({
          appraisalPlanId: this.id
        })
          .then(res => {
            this.loading = false
            this.$emit('success')
            this.$emit('close')
          })
          .catch(() => {
            this.loading = false
          })
      } else if (type == 'add') {
        this.employeeDialogShow = true
      }
    },

    /**
     * 新增考核人员
     */
    selectChange(users) {
      if (users.length) {
        const userIds = this.list.map(o => o.employeeId)
        const addFlag = users.some(o => userIds.includes(o.employeeId))
        if (addFlag) {
          this.$message.error('添加员工已在考核范围内，请勿重复添加')
          return
        }
        hrmAppraisalPlanAddEmployeeListAPI({
          appraisalPlanId: this.id,
          employeeIdList: users.map(item => item.employeeId)
        })
          .then(res => {
            this.$message.success('新增成功')
            this.employeeDialogShow = false
            this.$emit('success')
            this.getEmployeeList()
          })
          .catch(() => {})
      }
    },

    /**
     * 编辑
     */
    editClick() {
      this.isCreate = true
    },

    editSuccess() {
      this.getDetial()
      this.$emit('edit-success')
      this.$emit('handle', { type: 'edit' })
    },

    /**
     * 更多操作
     */
    commandSelect(command) {},

    /**
     * 关闭
     */
    hideView() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.tip-btn-start,
.tip-btn-ing,
.tip-btn-end {
  width: 42px;
  height: 20px;
  margin-left: 10px;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  border-radius: 3px;
}

.tip-btn-start {
  color: #fff;
  background-color: #c1c7d0;
}

.tip-btn-ing {
  color: #ffab00;
  background-color: #fffae6;
}

.tip-btn-end {
  color: #f8fcfd;
  background-color: #c3c5d2;
}

.side-detail__tabs--default {
  padding: 0 20px;
}

.wk-detail-header {
  padding: 32px 32px 0;
}

// 详情
.d-container-body {
  flex: 1;
  height: 0;
  padding: 0 32px;
  overflow: scroll;

  .top-padding {
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

// 搜索
::v-deep .search-input {
  width: 220px;

  .el-input__inner {
    padding-right: 36px;
  }
}

.top-padding {
  padding-top: 20px;
}

.delete-btn {
  padding: 0;
  color: $--button-default-font-color;

  &:hover {
    color: $--color-primary;
  }
}
</style>
