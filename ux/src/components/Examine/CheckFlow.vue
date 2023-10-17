<template>
  <div
    v-loading="loading"
    v-empty="list"
    xs-empty-icon="none"
    xs-empty-text="暂无记录">
    <flexbox
      class="flow-head">
      <div class="flow-head-name">审批流程</div>
      <i
        class="el-icon-close flow-head-close"
        @click="close" />
    </flexbox>
    <div class="flow-body">
      <flexbox
        v-for="(item, index) in list"
        :key="index"
        class="cf-flow-item"
        align="stretch"
        justify="flex-start">
        <i
          :class="[11, 17].includes(item.examineStatus) ? 'wk wk-icon-success' : getStatusIcon(item.examineStatus)"
          :style="{
            color:[11, 17].includes(item.examineStatus) ? '#00875A' : getStatusColor(item.examineStatus)
          }"
          class="cf-flow-item-img" />
        <div>
          <flexbox class="cf-flow-item-head">
            <!-- <div class="cf-flow-item-des">{{ item.orderId|stepName }}</div> -->
            <div>{{ item.examineTime }}</div>
          </flexbox>
          <flexbox class="cf-flow-item-info">
            <div class="cf-flow-item-name">{{ getLogUserName(item) }}</div>
            <!-- 审核中 3 当做 待审核 0 处理 -->
            <div v-if="item.examineStatus != 11 && item.examineStatus != 17 && item.examineStatus != 12"><span>{{ getLogContent(item) }}</span></div>
            <div v-else-if="item.examineStatus == 11"><span>已转审</span></div>
            <div v-else-if="item.examineStatus == 17"><span>已委托</span></div>
            <div v-else-if="item.examineStatus == 12"><span>已征求他人意见</span></div>
          </flexbox>
          <div
            v-if="item.remarks"
            class="cf-flow-item-content">{{ item.remarks }}
            <div class="cf-flow-item-content-arrow" />
          </div>
        </div>
        <div class="cf-flow-item-line" />
      </flexbox>
    </div>
  </div>
</template>

<script>
import { examineSuperExaminesUseExamineEmailTokenForExamineRecordLogAPI } from '@/api/examine/superExamine.js'
import { crmExamineRecordLogListAPI } from '@/api/examine' // 审批记录
import CheckStatusMixin from '@/mixins/CheckStatusMixin'

export default {
  // 查看审批历史
  name: 'CheckFlow',
  components: {},
  filters: {},
  mixins: [CheckStatusMixin],
  props: {
    examineType: {
      type: String,
      default: ''
    },
    // 详情信息id
    id: [String, Number],
    // 业务id
    typeId: [String, Number],
    show: Boolean
  },
  data() {
    return {
      loading: false,
      list: []
    }
  },
  computed: {},
  watch: {
    show: function(val) {
      if (val) {
        this.list = []
        this.getDetail()
      }
    }
  },
  mounted() {

  },
  methods: {
    getDetail() {
      const { name: routeName, query } = this.$route
      if (this.id) {
        this.loading = true
        crmExamineRecordLogListAPI({
          recordId: this.id,
          typeId: this.typeId
        })
          .then(res => {
            this.loading = false
            this.list = res.data
          })
          .catch(() => {
            this.loading = false
          })
      } else if (routeName === 'link' && query.k) {
        this.loading = true
        examineSuperExaminesUseExamineEmailTokenForExamineRecordLogAPI({
          emailToken: query.k
        })
          .then(res => {
            this.loading = false
            this.list = res.data
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    /**
     * @description:
     * @param {*} item
     * @return {*}
     */
    getLogUserName(item) {
      return item.examineUserName
    },

    /**
     * @description: 获取操作内容
     * @return {*}
     */
    getLogContent(item) {
      return item.examineStatus != 16 ? this.getStatusName(item.examineStatus) + '此申请' : ''
    },

    close() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
/** 头部的css */
.flow-head {
  padding: 8px 0;

  .flow-head-name {
    flex: 1;
    font-size: $--font-size-large;
  }

  .flow-head-close {
    font-size: 24px;
    cursor: pointer;
  }

  .flow-head-close:hover {
    color: $--color-primary;
  }
}

.flow-body {
  max-height: 300px;
  overflow: auto;
}

/** 每行的css */
.cf-flow-item {
  position: relative;
  padding-bottom: #{$--interval-base * 2};

  .cf-flow-item-img {
    height: 18px;
    margin-right: $--interval-base;
    font-size: 18px;
    background-color: white;
  }

  .cf-flow-item-head {
    color: $--color-text-secondary;

    .cf-flow-item-des {
      margin-right: 10px;
    }
  }

  .cf-flow-item-info {
    margin-top: $--interval-base;

    .cf-flow-item-name {
      margin-right: $--interval-base;
    }
  }

  .cf-flow-item-line {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 8px;
    z-index: -1;
    width: 1px;
    background-color: $--border-color-base;
  }

  .cf-flow-item-content {
    position: relative;
    padding: 8px;
    margin-top: 15px;
    font-size: 12px;
    line-height: 18px;
    color: $--color-text-secondary;
    background-color: $--color-n20;
    border-radius: $--border-radius-base;

    .cf-flow-item-content-arrow {
      position: absolute;
      top: -4px;
      left: 25px;
      width: 8px;
      height: 8px;
      background-color: $--color-n20;
      transform: rotate(45deg);
    }
  }
}
</style>
