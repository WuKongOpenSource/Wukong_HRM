<template>
  <div v-loading="loading">
    <div class="content-header">
      <span>日志欢迎语</span>
      <el-button
        type="primary"
        @click="save">保存</el-button>
    </div>
    <div class="content-body">
      <reminder class="reminder" content="以下内容为系统默认欢迎语，在日志随机展示，可自定义更改欢迎语。" />
      <div
        v-for="(item, index) in list"
        :key="index"
        class="input-item">
        <el-input
          v-model="item.value"
          :maxlength="100" />
        <i
          class="el-icon-remove"
          @click="deleteItem(item, index)" />
      </div>
      <el-button
        type="primary-text"
        style="padding-left: 0;"
        @click="addItem">+添加欢迎语</el-button>
    </div>
  </div>
</template>

<script>
import {
  sysSetLogWelcomeAPI,
  sysGetLogWelcomeListAPI
} from '@/api/admin/other'

import Reminder from '@/components/Reminder'

export default {
  name: 'LogWelcome',

  components: {
    Reminder
  },

  data() {
    return {
      loading: false, // 展示加载中效果

      list: [] // 展示类型数据
    }
  },

  created() {
    this.getDetail()
  },

  methods: {
    /**
     * 获取详情
     */
    getDetail() {
      this.loading = true
      sysGetLogWelcomeListAPI()
        .then(res => {
          this.loading = false
          this.list = res.data || []
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 增加类型
     */
    addItem() {
      this.list.push({ value: '' })
    },

    /**
     * 删除事项操作
     */
    deleteItem(item, index) {
      this.list.splice(index, 1)
    },

    /**
     * 保存操作
     */
    save() {
      const value = []
      for (let index = 0; index < this.list.length; index++) {
        const element = this.list[index]
        if (element.value) {
          value.push(element.value)
        }
      }

      if (value.length == 0) {
        this.$message.error('请输入欢迎语')
        return
      }
      this.loading = true
      sysSetLogWelcomeAPI(value)
        .then(res => {
          this.loading = false
          this.getDetail()
          this.$message.success('操作成功')
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "./index.scss";

.reminder {
  margin-bottom: #{$--interval-base * 2};
}

/* 事项布局 */
.input-item {
  margin-bottom: $--interval-base;

  .el-input {
    width: 500px;
  }

  .el-icon-remove {
    display: none;
    margin-left: 2px;
    color: $--color-danger;
    cursor: pointer;
  }
}

.input-item:hover {
  .el-icon-remove {
    display: inline;
  }
}
</style>
