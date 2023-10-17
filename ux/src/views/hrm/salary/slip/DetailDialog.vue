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
    <div class="slip-dialog-body">
      <div class="slip-header">
        <div class="slip-header__title">{{ totalValue }}</div>
        <div class="slip-header__des">实发金额（元）</div>
      </div>
      <div
        v-for="(item, index) in list"
        :key="index">
        <flexbox
          class="slip-item">
          <div class="slip-item__title text-one-line is-class">{{ item.name }}</div>
        </flexbox>
        <flexbox
          v-for="(subItem, subIndex) in item.optionList"
          :key="`sub${subIndex}`"
          class="slip-item">
          <div class="slip-item__title text-one-line">{{ subItem.name }}<el-tooltip
            v-if="item.remark"
            :content="item.remark"
            effect="dark"
            placement="top">
            <i class="wk wk-help wk-help-tips" style="margin-left: 3px;" />
          </el-tooltip></div>
          <div class="slip-item__value">{{ subItem.value }}</div>
        </flexbox>
      </div>
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
  hrmSalarySlipRecordQueryDetailAPI
} from '@/api/hrm/salary'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 工资条详情
  name: 'SlipDetailDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    id: [String, Number],
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
      totalValue: 0,
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

      hrmSalarySlipRecordQueryDetailAPI(this.id)
        .then(res => {
          this.list = res.data || []
          let needBreak = false
          for (let index = 0; index < this.list.length; index++) {
            const item = this.list[index]
            if (item.optionList) {
              for (let subIndex = 0; subIndex < item.optionList.length; subIndex++) {
                const subItem = item.optionList[subIndex]
                if (subItem.code === 240101) { // 实发工资
                  this.totalValue = subItem.value || 0
                  needBreak = true
                  break
                }
              }
              if (needBreak) {
                break
              }
            }
          }
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
.slip-dialog-body {
  height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}

.slip-header {
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

.slip-item {
  padding: 8px;

  &__title {
    flex: 1;

    &.is-class {
      font-weight: bold;
    }
  }

  &__value {
    flex-shrink: 0;
    margin-left: 20px;
    color: $--color-text-secondary;
  }
}
</style>
