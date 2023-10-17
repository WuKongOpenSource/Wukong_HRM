<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    width="500px"
    @close="handleCancel">
    <div class="moeny-dialog-body">
      <div class="money-header">
        <div class="money-header__title">{{ detail ? detail.realSalary : 0 }}</div>
        <div class="money-header__des">实发金额（元）</div>
      </div>
      <flexbox
        v-for="(item, index) in list"
        :key="index"
        class="money-item">
        <div class="money-item__title text-one-line">{{ item.name }}</div>
        <div class="money-item__value">{{ item.value }}</div>
      </flexbox>
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button @click.native="handleCancel">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  hrmEmployeeSocialSecuritySalaryDetailAPI
} from '@/api/hrm/employeeSocialSecurity'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 员工薪资详情
  name: 'MoneyDetailDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    detail: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '明细详情',
      list: []
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.getDetail()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

    /**
     * 取消选择
     */
    handleCancel() {
      this.close()
    },

    close() {
      this.$emit('update:visible', false)
    },

    getDetail() {
      this.loading = true
      hrmEmployeeSocialSecuritySalaryDetailAPI(this.detail.sempRecordId)
        .then(res => {
          this.list = res.data || []
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
.moeny-dialog-body {
  height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}

.money-header {
  text-align: center;

  &__title {
    font-size: 24px;
    font-weight: bold;
  }

  &__des {
    margin-top: 8px;
    color: $--color-text-secondary;
  }
}

.money-item {
  padding: 8px;

  &__title {
    flex: 1;
  }

  &__value {
    flex-shrink: 0;
    margin-left: 20px;
    color: $--color-text-secondary;
  }
}
</style>
