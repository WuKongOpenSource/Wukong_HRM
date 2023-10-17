<template>
  <el-dialog
    title="选择模板"
    :visible.sync="selectTemplateShow"
    :close-on-click-modal="false"
    width="70%"
    append-to-body
    :before-close="close"
    :modal-append-to-body="false">
    <div class="dialog-header">
      <el-input
        v-model="search"
        style="width: 200px;"
        placeholder="请输入考核指标名称"
        suffix-icon="el-icon-search"
        @keyup.enter.native="getList" />
      <el-button type="primary" @click="createTemplate">新建模板</el-button>
    </div>
    <div class="dialog-table">
      <el-table
        ref="multipleTable"
        v-loading="loading"
        height="550"
        :data="tableData"
        tooltip-effect="dark"
        style="width: 100%;"
        @select="select"
        @select-all="onSelectAll"
        @selection-change="handleSelectionChange">
        <el-table-column
          type="selection"
          width="55" />
        <el-table-column
          v-for="(item,index) in fieldList"
          :key="index"
          show-overflow-tooltip
          :label="item.label"
          :prop="item.prop" />
        <!-- <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              type="text"
              style="color: #0052cc;">预览
            </el-button>
          </template>
        </el-table-column> -->
      </el-table>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="handlerSubmit">确 定</el-button>
    </span>

    <assessment-template-create
      v-if="createTemplateShow"
      :indicator-template="{}"
      @close="createTemplateSuccess" />
  </el-dialog>
</template>

<script>
import { hrmAchievementsAssessmentTemplateAPI } from '@/api/hrm/achievementsAssessmentTemplate'

import assessmentTemplateCreate from '../../assessmentTemplate/Create'
export default {
  name: 'SelectTemplateDialog',
  components: {
    assessmentTemplateCreate
  },
  props: {
    selectTemplateShow: Boolean,
    selectTemplateName: String,
    status: [String, Number]
  },

  data() {
    return {
      loading: false,
      search: '',

      tableData: [],
      fieldList: [
        { label: '名称', prop: 'templateName' },
        { label: '考核维度', prop: 'dimensionNum' },
        { label: '考核指标(项)', prop: 'quotaNum' },
        { label: '合计(分)', prop: 'upperLimitScore' },
        { label: '考核指标说明', prop: 'templateIllustrate' }
      ],
      selectData: [],
      createTemplateShow: false

    }
  },

  watch: {
    selectTemplateShow(val) {
      if (val && this.selectTemplateName) {
        this.tableData.forEach(item => {
          if (item.templateName == this.selectTemplateName) {
            this.$nextTick(() => {
              this.$refs.multipleTable.toggleRowSelection(item, true)
            })
          }
        })
      }
    }
  },

  created() {
    this.getList()
  },

  methods: {
    getList(type) {
      this.loading = true
      const params = {
        templateName: this.search,
        pageType: 0 // 不分页
      }
      hrmAchievementsAssessmentTemplateAPI(params)
        .then(res => {
          this.tableData = res.data.list || []
          // if (!this.status) {
          //   this.$emit('select', res.data.list[0])
          // }
          if (type) {
            this.tableData.forEach(item => {
              if (item.templateName == this.selectTemplateName) {
                this.$nextTick(() => {
                  this.$refs.multipleTable.toggleRowSelection(item, true)
                })
              }
            })
          }

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    handleSelectionChange(val) {
      this.selectData = val
    },

    select(selection, row) {
      // 清除所有选中
      this.$refs.multipleTable.clearSelection()
      if (selection.length === 0) return
      // 将当前点击项选中
      this.$refs.multipleTable.toggleRowSelection(row, true)
    },

    onSelectAll() {
      this.$refs.multipleTable.clearSelection()
    },

    handlerSubmit() {
      if (this.selectData.length) {
        this.$emit('select', this.selectData.length == 1 ? this.selectData[0] : this.selectData[1])
        this.$message.success('选择模板成功')
        this.close()
      } else {
        this.$message.error('请选择考核模板')
      }
    },

    /**
     * 新建模板
     */
    createTemplate() {
      this.createTemplateShow = true
    },

    createTemplateSuccess() {
      this.createTemplateShow = false
      this.getList('createTemplate')
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.el-dialog__wrapper {
  ::v-deep .el-dialog__header {
    background-color: #f4f5f7;
    border-bottom: 1px solid #dfe1e6;
  }

  .dialog-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }

  .dialog-table {
    height: 550px;
    overflow: auto;
  }
}
</style>
