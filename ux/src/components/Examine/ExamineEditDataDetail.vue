<template>
  <el-dialog
    ref="wkDialog"
    title="查看节点编辑信息"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="600px"
    @close="close">
    <div
      v-empty="list"
      class="rc-cont"
      style="">
      <flexbox
        v-for="(item, index) in list"
        :key="index"
        class="ha-cont"
        align="stretch"
        justify="flex-start">
        <div class="ha-week">{{ item.createTime|filterTimestampToFormatTime('MM-DD dddd') }}</div>
        <div class="ha-circle" />
        <div class="ha-time">{{ item.createTime|filterTimestampToFormatTime('HH:mm') }}</div>
        <xr-avatar
          :id="item.createUserId"
          :name="item.realname"
          :size="32"
          :src="item.img"
          :disabled="false"
          class="ha-img" />
        <div class="ha-name">{{ item.realname }}</div>
        <div class="ha-content">
          <p
            v-for="(info, infoIndex) in item.content"
            :key="infoIndex">{{ info }}</p>
        </div>
        <div class="ha-line" />
      </flexbox>
    </div>
  </el-dialog>
</template>

<script type="text/javascript">
import { examineRecordQueryUpdateDataLogAPI } from '@/api/examine'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'ExamineEditDataDetail', // 相关操作  可能再很多地方展示 放到客户管理目录下
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    id: [String, Number],
    examineInfo: { // 获取 label examineId
      type: Object,
      default: () => {
        return {}
      }
    },
    itemData: Object // 当前节点
  },
  data() {
    return {
      loading: false,
      list: []
    }
  },
  computed: {},
  mounted() {
    this.getDetail()
  },
  methods: {
    getDetail(loading = true) {
      this.loading = loading
      examineRecordQueryUpdateDataLogAPI({
        typeId: this.id,
        label: this.examineInfo.label,
        examineId: this.examineInfo.examineAdvancedConfigVO.examineId, // /list 接口返回的 examineId 在 examineAdvancedConfigVO 对象里
        flowId: this.itemData.flowId
      })
        .then(res => {
          this.loading = false
          this.list = res.data.map(item => {
            item.createTime = new Date(item.createTime).getTime()
            return item
          })
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 取消选择
     */
    close() {
      this.$emit('update:visible', false)
    }
  }
}
</script>
<style lang="scss" scoped>
  .rc-cont {
    min-height: 250px;
    max-height: 500px;
    padding-right: 10%;
    overflow-y: auto;
  }

  ::v-deep .empty-mask {
    .empty-content {
      top: 20%;
    }
  }

  .ha-cont {
    position: relative;
    min-height: 40px;
    padding-top: 3px;
    line-height: 20px;

    .ha-week {
      flex-shrink: 0;
      width: 90px;
      margin: 0 17px 0 10px;
    }

    .ha-time {
      flex-shrink: 0;
      width: 80px;
      padding: 0 10px 0 24px;
    }

    .ha-circle {
      z-index: 2;
      flex-shrink: 0;
      width: 18px;
      height: 18px;
      background-color: white;
      border: 5px solid $--color-primary;
      border-radius: 9px;
    }

    .ha-img {
      display: block;
      flex-shrink: 0;
      margin: -3px 10px 0;
    }

    .ha-name {
      flex-shrink: 0;
      padding: 0 10px;
    }

    .ha-content {
      flex: 1;
      padding: 0 10px 10px;
    }

    .ha-line {
      position: absolute;
      top: 3px;
      bottom: -3px;
      left: 125px;
      z-index: 1;
      width: 1px;
      background-color: $--border-color-base;
    }
  }
  </style>

