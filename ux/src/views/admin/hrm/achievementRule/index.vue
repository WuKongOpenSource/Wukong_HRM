<template>
  <div v-loading="loading" class="main">
    <xr-header
      label="考勤规则设置">
      <el-button
        slot="ft"
        type="primary"
        @click="addHandlerClick">新建扣款规则</el-button>
    </xr-header>
    <div class="main-body">
      <el-tabs value="active">
        <el-tab-pane label="扣款规则" name="active">
          <div class="reminder-content">
            <p>1、用于管理企业员工迟到、早退、旷工、缺卡时的扣款规则。</p>
            <p>2、您可以根据企业的情况进行方案自定义。</p>
          </div>
          <el-table
            :data="list"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :height="tableHeight"
            highlight-current-row
            style="width: 100%;">
            <el-table-column
              v-for="item in fieldList"
              :key="item.label"
              :prop="item.prop"
              :label="item.label"
              :width="item.width">
              <template slot-scope="{row ,column}">
                <div
                  v-if=" ['lateRuleMethod',
                          'earlyRuleMethod',
                          'absenteeismRuleMethod',
                          'misscardRuleMethod'].includes(item.prop) ">
                  <p>{{ getRuleName(item.prop,row).ruleName }}</p>
                  <p>{{ getRuleName(item.prop,row).releTimeUnit }}</p>
                </div>
                <template v-else>
                  {{ row[column.property] }}
                </template>
              </template>
            </el-table-column>
            <el-table-column
              prop="prop"
              label="操作"
              width="width">
              <template slot-scope="{row }">
                <el-button
                  type="primary-text"
                  @click="updateHandler(row)"
                >修改</el-button>
                <el-button
                  v-if="isDelShow(row)"
                  type="primary-text"
                  @click="deleteHandler(row.attendanceRuleId)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 创建 -->
    <create
      v-if="createShow"
      :id="id"
      :action="createAction"
      @close="createShow = false"
      @save-success="saveSuccess"
    />
  </div>
</template>

<script>
import { hrmQueryAttendanceRulePageListAPI, hrmAttendanceRuleDeleteAPI } from '@/api/admin/hrm'
import XrHeader from '@/components/XrHeader'
import Create from './Create'

// import Lockr from 'lockr'

export default {

  components: {
    XrHeader,
    Create
  },

  data() {
    return {
      tableHeight: document.documentElement.clientHeight - 380, // 表的高度

      currentPage: 1,
      pageSizes: [15, 30, 60, 100],
      pageSize: 15,
      total: 0,
      list: [],
      otherParams: {}, // 查询附加条件
      loading: false,
      createAction: {
        type: 'save',
        id: '',
        data: {}
      },

      fieldList: [
        { label: '规则名称', prop: 'attendanceRuleName' },
        { label: '迟到', prop: 'lateRuleMethod' },
        { label: '早退', prop: 'earlyRuleMethod' },
        { label: '旷工', prop: 'absenteeismRuleMethod' },
        { label: '缺卡', prop: 'misscardRuleMethod' }
      ],
      createShow: false,
      id: '',
      nameValueMap: {
        lateRuleMethod: [
          { name: '迟到分钟扣款', value: 1, unit: '元/分钟', computeMode: 'lateDeductMoney' },
          { name: '迟到次数扣款', value: 2, unit: '元/次', computeMode: 'lateDeductMoney' },
          { name: '每月固定扣款', value: 3, unit: '元/月', computeMode: 'lateDeductMoney' }
        ],
        earlyRuleMethod: [
          { name: '早退分钟扣款', value: 1, unit: '元/分钟', computeMode: 'earlyDeductMoney' },
          { name: '早退次数扣款', value: 2, unit: '元/次', computeMode: 'earlyDeductMoney' },
          { name: '每月固定扣款', value: 3, unit: '元/月', computeMode: 'earlyDeductMoney' }
        ],
        absenteeismRuleMethod: [
          { name: '旷工天数扣款', value: 1, unit: '元/天', computeMode: 'absenteeismDeductMoney' }
        ],
        misscardRuleMethod: [
          { name: '缺卡次数扣款', value: 1, unit: '元/次', computeMode: 'misscardDeductMoney' }
        ]
      }
    }
  },

  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 380
    }

    this.getList()
  },
  methods: {
    /**
     * @description: 列表
     * @param {*}
     * @return {*}
     */
    getList() {
      this.loading = true
      hrmQueryAttendanceRulePageListAPI({
        limit: this.pageSize,
        page: this.currentPage
      }).then(res => {
        this.loading = false
        this.list = res.data.list
        this.total = res.data.totalRow
      })
    },

    /**
     * @description: 删除操作
     * @param {*} attendanceRuleId
     * @return {*}
     */
    deleteHandler(attendanceRuleId) {
      this.$confirm('确定删除', '提示', {
        cancelButtonText: '取消',
        confirmButtonText: '确定',
        type: 'warning'
      }).then(res => {
        hrmAttendanceRuleDeleteAPI({
          attendanceRuleId
        }).then(() => {
          this.$message.success('操作成功')
          this.handleRefresh()
        }).catch(() => {})
      }).catch(() => {
        this.$message.info('取消操作')
      })
    },

    /**
     * @description: 新建
     * @param {*}
     * @return {*}
     */
    addHandlerClick() {
      this.id = ''
      this.createAction = {
        id: '',
        type: 'save',
        data: {}
      }
      this.createShow = true
    },

    /**
     * @description: 修改
     * @param {*} row
     * @return {*}
     */
    updateHandler(row) {
      this.createShow = true
      this.createAction = {
        id: row.attendanceRuleId,
        type: 'update',
        data: row
      }
      this.id = row.attendanceRuleId
    },

    /**
     * @description: 创建成功
     * @param {*}
     * @return {*}
     */
    saveSuccess() {
      this.handleRefresh()
    },

    /**
     * @description: 获取规则名
     * @param {*} itemProp
     * @param {*} row
     * @return {*}
     */
    getRuleName(itemProp, row) {
      const findRes = this.nameValueMap[itemProp].find(item => item.value == row[itemProp])
      if (findRes) {
        return {
          ruleName: findRes.name,
          releTimeUnit: row[findRes.computeMode] + findRes.unit
        }
      } else {
        return {
          ruleName: '',
          releTimeUnit: ''
        }
      }
    },

    /**
     * 刷新
     */
    handleRefresh() {
      this.currentPage = 1
      this.getList()
    },
    /**
   * @description: 隐藏默认项修改按钮
   * @param {*}
   * @return {*}
   */
    isDelShow(row) {
      if (row.isDefaultSetting) return false
      return true
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
    // height: calc(100% - 70px);
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

.reminder-content {
  p {
    height: 30px;
    line-height: 30px;
  }
}
</style>
