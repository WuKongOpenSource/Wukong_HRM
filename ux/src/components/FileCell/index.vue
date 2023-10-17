<template>
  <div class="cell">
    <flexbox
      v-for="(data, fileIndex) in fileList"
      :key="fileIndex"
      class="cell-list">
      <img
        :src="data.name | fileIcon"
        class="cell-head"
        alt>
      <div
        :class="{'cursor-pointer' :cursorPointer}"
        class="cell-body text-one-line">
        <span>{{ data.name.length > 20 ? data.name.substring(0, 20) + '...' : data.name }}</span>
      </div>
      <div class="info text-one-line">
        ({{ data.size | getFileSize }})
        <span v-if="showTime" style="margin-left: 15px;">{{ data.createTime | changeTime }}</span>
      </div>
      <!-- 按钮外露 -->
      <div v-if="isExposed">
        <el-button type="primary-text" @click="handleCommand({type:'preview',data,fileIndex})">预览</el-button>
        <el-button type="primary-text" @click="handleCommand({type:'download',data,fileIndex})">下载</el-button>
        <el-button type="primary-text" @click="handleCommand({type:'delete',data,fileIndex})">删除</el-button>
      </div>
      <el-dropdown
        v-else
        :hide-on-click="false"
        @command="handleCommand">
        <i class="el-icon-more more" style="cursor: pointer;" />
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item :command="{type:'preview',data,fileIndex}">
            <span>
              预览
            </span>
          </el-dropdown-item>
          <el-dropdown-item :command="{type:'download',data,fileIndex}">
            <span
              type="text">
              下载
            </span>
          </el-dropdown-item>
          <el-dropdown-item :command="{type:'delete',data,fileIndex}">
            <span
              v-if="showDelete"
              type="text">
              删除
            </span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </flexbox>
  </div>
</template>

<script type="text/javascript">
import { crmFileDeleteAPI } from '@/api/common'

import {
  downloadFile,
  getFileIconWithSuffix,
  fileSize,
  canPreviewFile,
  wkPreviewFile,
  timeToFormatTime } from '@/utils'

export default {
  name: 'FileCell',
  filters: {
    getFileSize(size) {
      return fileSize(Number(size))
    },
    changeTime(value) {
      var oldTime = (new Date(value)).getTime()
      return timeToFormatTime(oldTime, 'yyyy年MM月DD日 ah:mm:ss')
    },
    fileIcon(value) {
      const temps = value ? value.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return getFileIconWithSuffix(ext)
    }
  },
  props: {
    // 完整数据
    fileList: Array,
    showFoot: {
      type: Boolean,
      default: true
    },
    showTime: {
      type: Boolean,
      default: true
    },
    showDelete: {
      type: Boolean,
      default: false
    },
    cursorPointer: {
      type: Boolean,
      default: false
    },
    moduleId: [String, Number],
    // 按钮是否外露
    isExposed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  }, // 附件展示效果
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    handleCommand(value) {
      if (value.type === 'download') {
        this.downloadClick(value.data)
      } else if (value.type === 'preview') {
        this.previewClick(value.data, value.fileIndex)
      } else if (value.type === 'delete') {
        this.deleteClick(value.data, value.fileIndex)
      }
    },
    /**
     * 下载
     */
    downloadClick(data) {
      downloadFile({ path: data.filePath || data.url, name: data.name })
    },

    /**
     * 附件预览
     */
    previewClick(data, fileIndex) {
      if (canPreviewFile(data.name)) {
        wkPreviewFile(data.filePath || data.url, data.name)
      } else {
        this.$wkPreviewFile.preview({
          index: fileIndex || 0,
          data: this.fileList.map(function(item) {
            return {
              url: item.filePath || item.url,
              name: item.name
            }
          })
        })
      }
    },

    /**
     * 删除
     */
    deleteClick(data, fileIndex) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          crmFileDeleteAPI({
            id: data.fileId
          })
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('delete', fileIndex, data)
            })
            .catch(() => {})
        })
        .catch(() => {
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.cell {
  width: 100%;
  margin: 0 1px;
  background: $--color-white;
  border-radius: 4px;
  box-shadow: $--box-shadow-bottom-light;

  &:hover {
    box-shadow: $--box-shadow-hover-bottom-light;
  }

  &-list {
    display: flex;
    flex-direction: row;
    width: 100%;
    height: 48px;
    padding: 14px;
    margin-top: 0;
    line-height: 48px;
    border-bottom: $--border-base;

    &:nth-last-child(1) {
      border-bottom: none;
    }

    &:hover {
      background-color: $--background-color-base;
    }

    .cell-head {
      display: block;
      width: 16px;
    }

    .cell-body {
      margin-left: 12px;
      font-size: 14px;
      color: $--color-primary;
    }

    .info {
      flex: 1;
      margin-left: 15px;
      color: $--color-text-secondary;
    }

    .more {
      cursor: pointer;
    }
  }
}

.cursor-pointer {
  cursor: pointer;
}
</style>
