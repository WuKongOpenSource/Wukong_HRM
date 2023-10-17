<template>
  <el-dialog
    :visible.sync="visible"
    :before-close="handleClose"
    :title="title"
    append-to-body
    width="600px">
    <div class="content">
      <div class="list">
        <flexbox
          v-for="(item,index) in taskList"
          :key="index"
          class="list-item"
          justify="space-between">
          <el-input
            v-model="item.taskName"
            :maxlength="50">
            <template slot="prepend">{{ '任务'+(index+1) }}</template>
          </el-input>
          <el-checkbox
            v-model="item.isNull"
            :true-label="1"
            :false-label="0">是否必做</el-checkbox>
          <el-button
            type="text"
            @click="delTask(index)">删除</el-button>
        </flexbox>
      </div>
      <el-button
        type="text"
        @click="addTask">
        <i class="wk wk-l-plus" />
        <span>添加</span>
      </el-button>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="save">确定</el-button>
      <el-button @click="handleClose">取消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { objDeepCopy } from '@/utils'

export default {
  name: 'PhaseDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    rowData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    rowIndex: Number
  },
  data() {
    return {
      taskList: []
    }
  },
  computed: {
    title() {
      return `配置 阶段${this.rowIndex + 1} ${this.rowData.name} 阶段工作`
    }
  },
  watch: {
    rowData: {
      handler(val) {
        this.taskList = objDeepCopy(val.taskList)
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    /**
     * 保存
     */
    save() {
      this.$emit('change', this.taskList)
    },
    /**
       * 关闭弹窗
       */
    handleClose() {
      this.$emit('update:visible', false)
    },
    /**
     * 添加任务
     */
    addTask() {
      this.taskList.push({ taskName: '', isNull: 0 })
    },
    /**
     * 删除任务
     */
    delTask(index) {
      this.taskList.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
.content {
  .switch {
    span {
      margin-right: 5px;
    }
  }

  .list {
    &-item {
      margin: 20px 0;
    }

    ::v-deep .el-input {
      width: 75%;
    }
  }
}
</style>
