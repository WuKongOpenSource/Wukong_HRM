<template>
  <create-sections
    v-loading="loading"
    class="salary-archives-info"
    title="薪资信息">
    <el-tooltip
      slot="header"
      content="薪资包含此员工所关联工资表的可定薪项，数据以定薪、调薪的最新数据为准"
      effect="dark"
      placement="top">
      <i class="wk wk-help wk-help-tips" />
    </el-tooltip>
    <div class="content">
      <div class="content__header">
        合计：<span>{{ total }}</span>
      </div>
      <wk-base-detail-section
        :list="list"
      />
    </div>
  </create-sections>
</template>

<script>
import {
  hrmSalaryArchivesQueryByIdAPI
} from '@/api/hrm/salary'

import CreateSections from '@/components/CreateSections'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'

export default {
  // 薪资信息
  name: 'SalaryArchivesInfo',
  components: {
    CreateSections,
    WkBaseDetailSection
  },
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      total: 0,
      list: []
    }
  },

  computed: {},
  watch: {
    id: {
      handler() {
        this.getDetail()
      },
      immediate: true
    }
  },
  mounted() {},
  activated: function() {
  },
  deactivated: function() {
  },
  methods: {
    // 获取基础信息
    getDetail() {
      this.loading = true
      hrmSalaryArchivesQueryByIdAPI(this.id)
        .then(res => {
          const resData = res.data || {}
          const list = resData.salaryOptions || []
          list.forEach(item => {
            item.label = item.name
          })
          this.list = list
          this.total = resData.total || 0
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.content {
  &__header {
    padding: 15px 8px 0;
    font-size: 20px;
    color: #777;

    span {
      color: #f9a74e;
    }
  }
}

.wk-help-tips {
  margin-top: 2px;
  margin-left: 5px;
}
</style>
