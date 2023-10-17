<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-02 13:56:09
 * @LastEditTime: 2020-06-18 14:35:52
 * @LastEditors: yang
-->
<template>
  <div class="system-customer main">
    <xr-header
      label="自定义字段设置" />
    <div class="main-body">
      <el-table
        v-loading="loading"
        :data="tableList"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        :height="tableHeight"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          width="100"
          label="模块图标">
          <template slot-scope="scope">
            <div
              class="table-icon">
              <i :class="getLableIcon(scope.row.labelGroup) " />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="模块"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="table-item-label">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="更新时间"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="table-item-time">{{ scope.row.updateTime == 0 ? '暂无' : scope.row.updateTime }}</span>
          </template>
        </el-table-column>

        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleCustomField('edit', scope.row, scope.$index)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { hrmConfigQueryFieldsAPI } from '@/api/admin/hrm'

import XrHeader from '@/components/XrHeader'

export default {
  name: 'CustomField',

  components: {
    XrHeader
  },

  data() {
    return {
      loading: false,
      tableHeight: document.documentElement.clientHeight - 140, // 表的高度
      // 自定义字段设置
      tableList: []
    }
  },

  created() {
    // 控制table的高度
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 140
    }

    this.getDetail()
  },

  methods: {
    /**
     * 详情
     */
    getDetail() {
      this.loading = true
      hrmConfigQueryFieldsAPI()
        .then(res => {
          this.tableList = res.data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 列表的编辑和预览
     */
    handleCustomField(type, item, index) {
      if (type == 'edit') {
        this.$router.push({
          name: 'hrmCustomField',
          params: {
            type: 'hrm_employee',
            id: item.labelGroup,
            label: 'none'
          }
        })
      }
    },

    /**
     * 根据自定义字段types 获取展示icon
     */
    getLableIcon(label) {
      return {
        1: 'wk wk-icon-contacts2-solid',
        2: 'wk wk-phone',
        7: 'wk wk-contacts',
        11: 'wk wk-icon-post-solid'
      }[label] || 'wk wk-icon-all-solid'
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../../styles/index.scss";

.main-body {
  margin-top: #{$--interval-base * 2};
}
</style>
