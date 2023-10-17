<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div class="wk-file-form">
    <wk-file-cell
      v-for="(file, fileIndex) in fileList"
      :key="fileIndex"
      :data="file"
      :list="fileList"
      :index="fileIndex"
      delete-show
      @delete="fileDelete" />
    <wk-file-select
      multiple
      @change="fileSelect">
      <el-button type="text">添加附件</el-button>
    </wk-file-select>
  </div>
</template>

<script>
import {
  adminFileUploadAPI,
  adminFileDeleteByIdAPI
} from '@/api/admin/file'
import { guid, objDeepCopy } from '@/utils/index'

import WkFileSelect from '../Select'
import WkFileCell from '../Cell'

export default {
  // 自定义字段附件
  name: 'WkFileForm',
  components: {
    WkFileSelect,
    WkFileCell
  },
  props: {
    max: { // 最大上传图片数量，值为0时不限制
      type: Number,
      default: 9
    },
    value: [String, Number],
    defaultValue: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      fileList: [],
      staticBatchId: ''
    }
  },
  computed: {
    batchId() {
      if (this.fileList && this.fileList.length) {
        return this.fileList[0].batchId
      }
      return this.value || guid()
    }
  },
  watch: {
    batchId: {
      handler(val) {
        this.$emit('input', val)
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.fileList = objDeepCopy(this.defaultValue || [])
  },

  beforeDestroy() {},
  methods: {
    /**
     * 附件操作
     */
    fileSelect(fileList, event) {
      let files = Array.from(fileList)
      if (this.max && files.length + this.fileList.length > this.max) {
        files = files.slice(0, this.max - this.fileList.length)
        this.$message.error(`最多只能上传${this.max}个附件`)
      }

      for (let index = 0; index < files.length; index++) {
        const file = files[index]
        this.uploadFileRequest(file)
      }
      event.target.value = ''
    },

    /**
     * 文件上传
     */
    uploadFileRequest(file) {
      this.$wkUploadFile.upload({
        request: adminFileUploadAPI,
        file: file,
        params: {
          batchId: this.batchId
        }
      }).then(({ res }) => {
        this.fileList.push(res.data || {})
        this.change(this.fileList)
      }).catch(() => {})
    },

    fileDelete(data, index) {
      this.loading = true
      adminFileDeleteByIdAPI(data.fileId).then(res => {
        this.$message.success('删除成功')
        this.fileList.splice(index, 1)
        this.change(this.fileList)
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * change 事件
     */
    change(list) {
      console.log('change: ', list)
      const copyList = objDeepCopy(list)
      this.$emit('update:defaultValue', copyList)
      this.$emit('change', copyList, this.batchId)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-file-form {
  padding-right: 20%;
}
</style>
