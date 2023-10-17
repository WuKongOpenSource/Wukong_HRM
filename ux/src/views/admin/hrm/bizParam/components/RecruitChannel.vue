<template>
  <div v-loading="loading">
    <div class="content-header">
      <span>招聘渠道设置</span>
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
          :disabled="item.isSys == 1"
          :maxlength="100" />
        <el-switch
          v-model="item.status"
          :active-value="1"
          :inactive-value="0"
          :width="32" />
        <!-- <el-button class="el-icon-handle" type="text" @click="item.status = item.status == 1 ? 0 : 1">{{ item.status == 1 ?'禁用': '启用' }}</el-button> -->
        <el-button
          v-if="item.isSys != 1"
          class="el-icon-handle is-delete"
          type="text"
          @click="deleteItem(item, index)">删除</el-button>
      </div>
      <el-button
        type="text"
        style="padding-left: 0;"
        @click="addItem">+添加招聘渠道</el-button>
    </div>

    <el-dialog
      :visible.sync="dialogVisible"
      title="删除渠道"
      width="500px">
      <p>渠道删除之后不可恢复，是否确认删除？</p>
      <el-form
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
        label-position="top">
        <el-form-item label="活动区域" prop="changeChannelId">
          <el-select
            v-model="ruleForm.changeChannelId"
            style="width: 100%;">
            <el-option
              v-for="item in options"
              :key="item.channelId"
              :label="item.value"
              :value="item.channelId" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="deleteSureClick">确定</el-button>
        <el-button @click="cancelClick">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  hrmConfigQueryRecruitChannelAPI,
  hrmConfigSaveRecruitChannelAPI,
  hrmConfigDeleteRecruitChannelAPI
} from '@/api/admin/hrm'

export default {
  // 招聘渠道
  name: 'RecruitChannel',

  components: {},

  data() {
    return {
      loading: false, // 展示加载中效果
      list: [], // 展示类型数据
      dialogVisible: false,
      ruleForm: {},
      rules: {
        changeChannelId: [
          { required: true, message: '请选择', trigger: 'blur' }
        ]
      }

    }
  },

  computed: {
    options() {
      return this.list.filter(item => item.status == 1 && item.channelId)
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
      hrmConfigQueryRecruitChannelAPI()
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
      this.list.push({
        status: 1,
        isSys: 0,
        value: ''
      })
    },

    /**
     * 删除事项操作
     */
    deleteItem(item, index) {
      if (!item.channelId) {
        this.list.splice(index, 1)
      } else {
        this.ruleForm = {
          index: index,
          deleteChannelId: item.channelId
        }
        this.dialogVisible = true
      }
    },

    cancelClick() {
      this.dialogVisible = false
      this.$refs.ruleForm.clearValidate()
    },

    deleteSureClick() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          const index = this.ruleForm.index
          delete this.ruleForm['index']
          hrmConfigDeleteRecruitChannelAPI(this.ruleForm)
            .then(res => {
              this.loading = false
              this.list.splice(index, 1)
              this.$message.success('删除成功')
              this.dialogVisible = false
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    },

    /**
     * 保存操作
     */
    save() {
      for (let index = 0; index < this.list.length; index++) {
        const element = this.list[index]
        if (!element.value) {
          this.$message.error('请完善信息')
          return
        }
      }
      this.loading = true
      hrmConfigSaveRecruitChannelAPI(this.list.map(item => {
        const temp = {}
        temp.channelId = item.channelId
        temp.isSys = item.isSys
        temp.status = item.status
        temp.value = item.value
        return temp
      }))
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

  .el-switch,
  .el-button {
    margin-left: 10px;
  }

  .el-icon-handle {
    display: inline;

    &.is-delete {
      color: $--color-danger;
    }
  }
}

.input-item:hover {
  .el-icon-handle {
    display: inline;
  }
}
</style>
