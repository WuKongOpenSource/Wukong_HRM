<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div class="main">
    <xr-header
      label="薪资设置">
      <el-button
        v-if="activeTab == 'group'"
        slot="ft"
        type="primary"
        @click="createClick">新建薪资组</el-button>
    </xr-header>
    <div class="main-body">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="薪资组" name="group">
          <reminder
            :content="`1、薪资组管理，即把人员分组，为每个组设置不同计薪方案等。
2、同一组内的人员共用同一套计薪方案，一位员工只能适用一个薪资组。`" />
          <el-table
            v-loading="loading"
            :data="groupList"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :height="tableHeight"
            class="main-table"
            highlight-current-row
            style="width: 100%;"
            @row-click="handleRowClick">
            <el-table-column
              v-for="(item, index) in groupTableFields"
              :key="index"
              :prop="item.fieldName"
              :label="item.name"
              :min-width="item.width"
              :formatter="fieldFormatter"
              show-overflow-tooltip />
            <el-table-column
              fixed="right"
              label="操作"
              width="100">
              <template slot-scope="scope">
                <el-button
                  type="primary-text"
                  style="padding: 0;"
                  @click="handleClick('edit', scope)">编辑</el-button>
                <el-button
                  type="primary-text"
                  style="padding: 0;"
                  @click="handleClick('delete', scope)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="计税规则" name="tax">
          <reminder
            :content="`1、计税规则，由系统统一提供。请到【薪资组】关联人员，计薪规则及适用的个税规则。
2、核算工资时，按员工适用的个税方案，自动计算出当月个税金额。`" />
          <el-table
            v-loading="loading"
            :data="taxList"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :height="tableHeight"
            class="main-table"
            highlight-current-row
            style="width: 100%;"
            @row-click="handleRowClick">
            <el-table-column
              v-for="(item, index) in taxTableFields"
              :key="index"
              :prop="item.fieldName"
              :label="item.name"
              :min-width="item.width"
              :formatter="fieldFormatter"
              show-overflow-tooltip />
            <el-table-column
              fixed="right"
              label="操作"
              width="80">
              <template slot-scope="scope">
                <el-button
                  v-if="scope.$index == 0"
                  type="primary-text"
                  style="padding: 0;"
                  @click="handleClick('edit', scope)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <group-edit-dialog
      :detail="editData"
      :visible.sync="groupEditDialogShow"
      @change="getGroupList()" />

    <tax-edit-dialog
      :detail="editData"
      :visible.sync="taxEditDialogShow"
      @change="getTaxList()" />
  </div>
</template>

<script>
import {
  hrmSalaryGroupListAPI,
  hrmSalaryTaxRuleListAPI,
  hrmSalaryGroupDeleteAPI
} from '@/api/admin/hrm'

import Reminder from '@/components/Reminder'
import XrHeader from '@/components/XrHeader'
import GroupEditDialog from './components/GroupEditDialog'
import TaxEditDialog from './components/TaxEditDialog'

import { isEmpty } from '@/utils/types'
import { objDeepCopy } from '@/utils'
import taxModel from './model/tax'

export default {
  // 计薪规则
  name: 'SalaryRules',
  components: {
    Reminder,
    XrHeader,
    GroupEditDialog,
    TaxEditDialog
  },
  mixins: [],
  data() {
    return {
      loading: false, // 加载动画
      activeTab: 'group',
      tableHeight: document.documentElement.clientHeight - 280, // 表的高度
      groupTableFields: [],
      groupList: [],
      taxTableFields: [],
      taxList: [],

      groupEditDialogShow: false,
      taxEditDialogShow: false,
      editData: null
    }
  },
  computed: {

  },
  watch: {
    activeTab(val) {
      if (val == 'group') {
        this.getGroupList()
      } else if (val == 'tax') {
        this.getTaxList()
      }
    }
  },
  created() {
    if (this.$route.query.type) {
      this.activeTab = this.$route.query.type
    }
  },
  mounted() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 280
    }

    this.groupTableFields = this.getTableFileList('group')
    this.taxTableFields = this.getTableFileList()

    this.getGroupList(true)
  },
  methods: {
    getTableFileList(type) {
      if (type == 'group') {
        return [{
          fieldName: 'groupName',
          name: '薪资组名称',
          width: 80
        }, {
          fieldName: 'ruleName',
          name: '计税规则',
          width: 80
        }, {
          fieldName: 'salaryStandard',
          name: '月计薪标准',
          width: 80
        }, {
          fieldName: 'changeRule',
          name: '转正、调薪月规则',
          width: 80
        }, {
          fieldName: 'employeeList',
          name: '适用范围',
          width: 80
        }]
      }

      return [{
        fieldName: 'ruleName',
        name: '方案名称',
        width: 80
      }, {
        fieldName: 'taxType',
        name: '个税类型',
        width: 80
      }, {
        fieldName: 'cycleType',
        name: '计税周期',
        width: 80
      }, {
        fieldName: 'isTax',
        name: '是否计税',
        width: 80
      }, {
        fieldName: 'markingPoint',
        name: '起征点',
        width: 80
      }, {
        fieldName: 'decimalPoint',
        name: '个税结果保留小数位',
        width: 80
      }, {
        fieldName: 'salaryGroupCount',
        name: '适用薪资组',
        width: 80
      }]
    },

    /**
     * 获取薪资组列表数据
     */
    getGroupList(loading = false) {
      this.loading = loading
      hrmSalaryGroupListAPI({
        pageType: 0
      })
        .then(res => {
          this.groupList = res.data.list
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取计税规则列表数据
     */
    getTaxList(loading = false) {
      this.loading = loading
      hrmSalaryTaxRuleListAPI()
        .then(res => {
          this.taxList = res.data
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
      const value = row[column.property]
      if (column.property == 'employeeList') {
        const structures = row.deptList || []
        let strName = structures.map(item => item.deptName).join('、')

        const users = row.employeeList || []
        const userName = users.map(item => item.employeeName).join('、')

        if (strName && userName) {
          strName += '、'
        }
        return strName + userName
      }
      if (this.activeTab == 'tax') {
        const valueObj = taxModel[`${column.property}Value`]
        if (valueObj) {
          return valueObj[row[column.property]] || '--'
        } else if (column.property == 'markingPoint') {
          return isEmpty(value) ? '--' : `${value}元/月`
        } else if (column.property == 'decimalPoint') {
          return isEmpty(value) ? '--' : `保留${value}位小数`
        } else if (column.property == 'salaryGroupCount') {
          return `${value}个薪资组正在使用`
        }
      }

      return isEmpty(value) ? '--' : value
    },

    /**
     * 新建薪资组
     */
    createClick() {
      this.editData = null
      this.groupEditDialogShow = true
    },

    /** 列表操作 */
    /**
     * 当某一行被点击时会触发该事件
     */
    handleRowClick(row, column, event) {},

    /**
     * 编辑删除
     */
    handleClick(type, scope) {
      if (type === 'edit') {
        this.editData = objDeepCopy(scope.row)
        if (this.activeTab == 'group') {
          this.groupEditDialogShow = true
        } else {
          this.taxEditDialogShow = true
        }
      } else if (type === 'delete') {
        // 启用停用
        this.$confirm('您确定要删除吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmSalaryGroupDeleteAPI(scope.row.groupId)
              .then(res => {
                this.getGroupList()
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
              })
              .catch(() => {})
          })
          .catch(() => {
          })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../styles/index.scss";

.main-body {
  margin-top: #{$--interval-base * 2};
}

::v-deep .el-tabs {
  height: 100%;

  .el-table__fixed-body-wrapper {
    td:first-child .cell {
      padding-left: 10px !important;
    }
  }

  .el-tabs__content {
    height: calc(100% - 70px);
    overflow-y: auto;
  }
}

.create-button {
  position: absolute;
  top: 70px;
  right: 20px;
  z-index: 1;
}

.el-table {
  margin-top: 16px;
}

.el-table::before {
  display: none;
}

.wk-help-tips {
  margin-left: 3px;
}
</style>
