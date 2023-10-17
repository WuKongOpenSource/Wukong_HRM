<template>
  <div class="import-history">
    <div class="import-history__hd">
      <span class="import-history__title">查看历史导入记录</span>
      <span class="import-history__des">(错误数据只保存七天,七天后将自动失效)</span>

      <el-button
        class="xr-icon-close-btn"
        icon="el-icon-close"
        @click="closeClick" />
    </div>

    <el-table
      :data="tableList"
      class="import-history__bd"
      height="250"
      style="width: 100%;">
      <el-table-column
        prop="createTime"
        label="导入时间"
        width="120">
        <template slot-scope="scope">
          {{ scope.row.createTime | createTime }}
        </template>
      </el-table-column>
      <el-table-column
        prop="realname"
        label="操作人"
        width="80" />
      <el-table-column
        prop="address"
        label="导入结果">
        <template slot-scope="scope">
          {{ `导入总数据${scope.row.title}条，${getImportContent(scope.row.title, scope.row.content)}` }}
        </template>
      </el-table-column>
      <el-table-column
        prop="option"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <template v-if="getErrorNum(scope.row.content) > 0">
            <el-button
              v-if="scope.row.valid == 1"
              type="primary-text"
              style="padding: 0;"
              @click="downloadError(scope.row.messageId)">下载错误数据</el-button>
            <span v-else-if="scope.row.valid == 0" class="invalid-tips">已失效</span>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <div class="import-history__ft">
      <el-button
        type="primary"
        @click="closeClick">关闭</el-button>
    </div>
  </div>
</template>

<script>
import { systemMessageListAPI } from '@/api/common'
import {
  crmDownImportErrorAPI
} from '@/api/crm/common'

import { downloadExcelWithResData } from '@/utils/index'

export default {
  // 导入历史
  name: 'CRMImportHistory',
  components: {},
  filters: {
    createTime(time) {
      const times = time.split(' ')
      return times.length > 0 ? times[0] : ''
    }
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    // CRM类型
    crmType: {
      type: String,
      default: ''
    },
    // moduleType
    props: Object
  },
  data() {
    return {
      loading: false,
      tableList: []
    }
  },
  computed: {},
  watch: {
    show(value) {
      if (value) {
        this.getList()
      }
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    getList() {
      this.loading = true
      systemMessageListAPI({
        page: 1,
        limit: 9999,
        type: {
          customer: 14,
          contacts: 16,
          leads: 18,
          business: 160,
          contract: 161,
          invoice: 162,
          receivables: 163,
          receivablesPlan: 164,
          visit: 165,
          marketing: 166,
          product: 20,
          visit: 43,
          hrm: 50
        }[this.crmType] || this.props.moduleType
      })
        .then(res => {
          this.loading = false
          this.tableList = res.data.list
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 下载错误数据
     */
    downloadError(messageId) {
      crmDownImportErrorAPI({ messageId })
        .then(res => {
          downloadExcelWithResData(res)
        })
        .catch(() => {
        })
    },

    getImportContent(totalSize, content) {
      const list = content.split(',') || []
      const updateSize = Number(list[1] || '0')
      const errSize = Number(list[0] || '0')
      return `覆盖${updateSize}条，导入成功${totalSize - errSize}条，导入失败${errSize}条。`
    },

    /**
     * 获取错误数据数
     */
    getErrorNum(content) {
      const list = content.split(',') || []
      const errSize = Number(list[0] || '0')
      return parseInt(errSize)
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.import-history {
  &__hd {
    padding: 20px;
    padding-bottom: 10px;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    color: $--color-text-primary;
  }

  &__des {
    font-size: 12px;
    color: #ccc;
  }

  &__bd {
    border-top: 1px solid #e6e6e6;
  }

  &__ft {
    padding: 10px;
    text-align: right;
    background-color: #f7f8fa;
  }

  .xr-icon-close-btn {
    float: right;
  }
}

.invalid-tips {
  color: $--color-text-secondary;
}
</style>
