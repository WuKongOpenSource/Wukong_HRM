<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-08 20:12:32
 * @LastEditTime: 2022-05-25 10:46:47
 * @LastEditors: yang
-->
<template>
  <div v-loading="loading" class="file-index">
    <div class="file-index__header">
      <wk-file-select
        v-if="detail"
        style="display: inline-block;"
        multiple
        @change="uploadFile"
      >
        <el-button
          icon="el-icon-plus"
          type="primary">上传材料附件</el-button>
      </wk-file-select>

    </div>
    <el-table
      :data="list"
      :height="tableHeight"
      stripe
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        show-overflow-tooltip />
      <el-table-column
        label="操作"
        width="200">
        <template slot-scope="scope">
          <flexbox>
            <el-button
              type="text"
              @click.native="handleFile('preview', scope)">预览</el-button>
            <el-button
              type="text"
              @click.native="handleFile('download', scope)">下载</el-button>
            <el-button
              type="text"
              @click.native="handleFile('edit', scope)">重命名</el-button>
            <el-button
              type="text"
              style="color: #f94e4e;"
              @click.native="handleFile('delete', scope)">删除</el-button>
          </flexbox>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="editDialog"
      title="编辑"
      width="400px">
      <el-form :model="editForm">
        <el-form-item
          label="新名称"
          label-width="100">
          <el-input
            v-model="editForm.name"
            autocomplete="off" />
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer">
        <el-button
          type="primary"
          @click="confirmEdit">确定</el-button>
        <el-button @click="editDialog = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script type="text/javascript">
import { downloadFileAPI } from '@/api/common'
import {
  adminFileUploadAPI,
  adminFileDeleteByIdAPI,
  adminFileRenameFileAPI
} from '@/api/admin/file'

import {
  hrmRecruitCandidateQueryFileAPI
} from '@/api/hrm/recruit/candidate'
import WkFileSelect from '@/components/NewCom/WkFile/Select'

import { fileSize, canPreviewFile, wkPreviewFile, downloadFileWithBuffer } from '@/utils'
import { debounce } from 'throttle-debounce'

export default {
  name: 'CandidateFile',
  components: {
    WkFileSelect
  },
  props: {
    id: [String, Number],
    detail: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      loading: false,
      list: [],
      fieldList: [],
      tableHeight: '400px',
      // 重命名 弹窗
      editDialog: false,
      // 编辑信息
      editForm: { name: '', data: {}}
    }
  },
  computed: {},
  watch: {
    id: function(val) {
      this.list = []
      this.getDetail()
    }
  },
  created() {
    this.fieldList.push({ prop: 'name', width: '200', label: '附件名称' })
    this.getDetail()

    this.debouncedGetDetail = debounce(300, () => {
      this.getDetail()
    })
  },
  activated: function() {},
  deactivated: function() {},
  methods: {
    getDetail() {
      this.loading = true
      hrmRecruitCandidateQueryFileAPI(this.id)
        .then(res => {
          this.loading = false
          this.list = res.data.map(item => {
            item.size = fileSize(item.size)
            return item
          })
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 图片选择出发
     */
    uploadFile(files, event) {
      for (let index = 0; index < files.length; index++) {
        const file = files[index]
        this.$wkUploadFile.upload({
          request: adminFileUploadAPI,
          file: file,
          params: {
            batchId: this.detail.batchId
          }
        }).then(completeData => {
          this.debouncedGetDetail()
        }).catch(() => {})
      }

      event.target.value = ''
    },

    /**
     * 当某一行被点击时会触发该事件
     */
    handleRowClick(row, column, event) {},

    showPreviewBtn(item) {
      return canPreviewFile(item.row.name)
    },

    /**
     * 编辑删除cell
     */
    handleFile(type, item) {
      if (type === 'preview') {
        if (canPreviewFile(item.row.name)) {
          wkPreviewFile(item.row.url, item.row.name)
        } else {
          this.$wkPreviewFile.preview({
            index: item.$index,
            data: this.list
          })
        }
      } else if (type === 'delete') {
        this.$confirm('您确定要删除该文件吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            adminFileDeleteByIdAPI(item.row.fileId)
              .then(res => {
                this.list.splice(item.$index, 1)
                this.$bus.emit('crm-tab-num-update')
                this.$message.success('操作成功')
              })
              .catch(() => {})
          })
          .catch(() => {})
      } else if (type === 'download') {
        downloadFileAPI(item.row.url).then(res => {
          downloadFileWithBuffer(res.data, item.row.name)
        }).catch(() => {})
      } else {
        this.editForm.data = item
        this.editForm.name = item.row.name
        this.editDialog = true
      }
    },

    /**
     * 编辑确定
     */
    confirmEdit() {
      if (this.editForm.name) {
        adminFileRenameFileAPI({
          fileId: this.editForm.data.row.fileId,
          name: this.editForm.name
        })
          .then(res => {
            this.$message.success('编辑成功')
            this.editDialog = false
            var item = this.list[this.editForm.data.$index]
            item.name = this.editForm.name
          })
          .catch(() => {})
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.file-index {
  &__header {
    margin: 10px;
    text-align: right;
  }

  .el-table {
    ::v-deep .el-table__header-wrapper {
      display: none;
    }
  }
}
</style>

