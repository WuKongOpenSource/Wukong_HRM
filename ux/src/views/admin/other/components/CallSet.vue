<template>
  <el-popover
    v-model="visible"
    placement="right"
    width="400"
    trigger="click">
    <div>
      <div class="content-title">
        <span>话机设置</span>
        <el-button
          type="primary"
          size="medium"
          @click="save">保存</el-button>
      </div>
      <div class="content-body">
        <span class="label">自动换卡</span><el-checkbox v-model="data.open">开启</el-checkbox>
        <span class="label">拨打次数</span><el-input-number v-model="data.num" :disabled="!data.open" :controls="false" :min="1" :max="200" />
        <el-button type="primary" @click="changeNext">手动换卡</el-button>
      </div>
    </div>
    <el-button slot="reference" type="primary" style="padding: 5px 15px;">话机设置</el-button>
  </el-popover>
</template>

<script>
import Lockr from 'lockr'
import callCenter from '@/callCenter/callWebSokets'
import { LOCAL_CALL_LIMIT_SET } from '@/utils/constants.js'

export default {
  name: 'CallSet',

  components: {
  },

  data() {
    return {
      loading: false,
      data: {
        open: false,
        num: 0
      },
      visible: false
    }
  },
  watch: {
    visible(val) {
      this.getDetail()
    }
  },
  created() {
    this.getDetail()
    // callCenter.message((data) => {
    //   this.callMessage(data)
    // })
  },

  methods: {
    /**
     * 获取详情
     */
    getDetail() {
      const data = Lockr.get(LOCAL_CALL_LIMIT_SET)
      if (data) {
        this.data = data
      }
    },

    /**
     * 换卡
     */
    changeNext() {
    //   this.loading = true
      callCenter.OnSwitchNext()
    },

    /**
     * 换卡结果
     */
    // callMessage(data) {
    //   if (data && data.type === 'CommandResponse') {
    //     const dataResult = data.data || {}
    //     if (dataResult.invoke_command && dataResult.invoke_command.toLowerCase() == 'cbswitchnext_multi') {
    //       this.loading = false
    //   if (dataResult.invoke_command.toLowerCase() == 'cbswitchnext_multi' && dataResult.state) {
    //     this.$message.success('操作成功')
    //     this.loading = false
    //   } else {
    //     this.$message.error('操作失败')
    //     this.loading = false
    //   }
    //     }
    //   }
    // },

    /**
     * 保存操作
     */
    save() {
      Lockr.set(LOCAL_CALL_LIMIT_SET, this.data)
      this.$message.success('保存成功')
      this.visible = false
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.content-title {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #e6e6e6;
}

.content-body {
  padding: 16px 0 8px;

  .label {
    margin-right: 5px;
  }

  .el-input-number {
    width: 80px;
    margin-right: 16px;
  }
}

</style>
