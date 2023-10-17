<template>
  <div
    v-loading="loading"
    v-empty="list"
    class="rc-cont"
    style="min-height: 500px;padding-right: 10%;margin-top: 20px;">
    <flexbox
      v-for="(item, index) in list"
      :key="index"
      class="ha-cont"
      align="stretch"
      justify="flex-start">
      <div class="ha-week">{{ item.createTime|filterTimestampToFormatTime('MM-DD dddd') }}</div>
      <div class="ha-circle" />
      <div class="ha-time">{{ item.createTime|filterTimestampToFormatTime('HH:mm') }}</div>
      <HrmAvatar
        :id="item.createUserId"
        :name="item.realname"
        :size="32"
        :src="item.img"
        :disabled="false"
        class="ha-img" />
      <div class="ha-name">{{ item.realname }}</div>
      <div class="ha-content">
        <p>{{ item.content }}</p>

        <div
          v-if="item.fileEntity && item.fileEntity.fileType == 'file'"
          style="display: flex;margin-top: 10px;">
          <div class="cell-hd first-show">
            <i class="wk wk-file" />
          </div>
          <div class="cell-hd cell-label first-show" style="flex-shrink: 0;margin-right: 0;">
            附件：
          </div>
          <div class="cell-bd cell-label text-one-line">
            {{ item.fileEntity.name }}
          </div>
          <div class="cell-ft">
            <span class="des">（{{ item.fileEntity.size | getFileSize }}）</span>
            <a @click="previewImg(item.fileEntity,0)">预览</a>
            <a @click="downloadFile(item.fileEntity)">下载</a>
          </div>
        </div>
      </div>
      <div class="ha-line" />
      <!-- <flexbox
        v-if="item.img && item.img.length > 0"
        class="fl-b-images"
        wrap="wrap">
        <div
          v-for="(file, fileIndex) in item.img"
          :key="file.url"
          v-src:background-image="file.url"
          class="fl-b-img-item"
          @click="previewImg(item.img, fileIndex)" />
      </flexbox> -->

    </flexbox>
  </div>
</template>

<script type="text/javascript">
import {
  queryRecordListAPI
} from '@/api/hrm/selfService/performance'
import HrmAvatar from '../../HrmAvatar.vue'
import { downloadFile, fileSize } from '@/utils'

export default {
  name: 'InspectionRecords',
  filters: {
    getFileSize(size) {
      return fileSize(size)
    }
  },
  components: {
    HrmAvatar
  },
  inject: ['rootTabs'], // 考核记录
  props: {
    /** 模块ID */
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      list: []
    }
  },
  watch: {
    'rootTabs.currentName'(val) {
      if (val === 'InspectionRecords') {
        this.getDetail(false)
      }
    }
  },
  mounted() {
    this.getDetail()
  },
  methods: {
    getDetail(loading = true) {
      this.loading = loading
      queryRecordListAPI({
        typeId: this.id
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

    // 文件预览
    previewImg(list, index) {
      var arr = [list]
      this.$wkPreviewFile.preview({
        index: index,
        data: arr
      })
    },

    // 文件下载
    downloadFile(file) {
      downloadFile({ path: file.url, name: file.name })
    }
  }
}
</script>
<style lang="scss" scoped>
// @import "../styles/relativecrm.scss";

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
