<template>
  <div v-loading="loading">
    <div class="content-header">
      <span>淘汰原因设置</span>
      <el-button
        type="primary"
        @click="save">保存</el-button>
    </div>
    <div class="content-body">
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
        type="text"
        style="padding-left: 0;"
        @click="addItem">+添加淘汰原因</el-button>
    </div>
  </div>
</template>

<script>
import {
  hrmConfigQueryRecruitEliminateAPI,
  hrmConfigSaveRecruitEliminateAPI
} from '@/api/admin/hrm'

export default {
  name: 'EliminateSet',

  components: {},

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
      hrmConfigQueryRecruitEliminateAPI()
        .then(res => {
          this.loading = false
          this.list = res.data.map(item => {
            return { value: item }
          })
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
      this.loading = true
      hrmConfigSaveRecruitEliminateAPI(value)
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
@import "../../style/index.scss";

/* 事项布局 */
.input-item {
  margin-bottom: 10px;

  .el-input {
    width: 300px;
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
