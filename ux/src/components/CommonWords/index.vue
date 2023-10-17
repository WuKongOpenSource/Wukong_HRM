<template>
  <el-dropdown class="common-words" trigger="click" @visible-change="visibleChange" @command="selectItem">
    <!-- <el-input v-model="title" class="wk-select-input" readonly suffix-icon="el-icon-arrow-up" /> -->
    <el-button type="subtle">常用语</el-button>
    <el-dropdown-menu slot="dropdown">
      <div class="dropdown-body">
        <el-dropdown-item
          v-for="(item, index) in list"
          :key="index"
          :command="item">{{ item }}</el-dropdown-item>
      </div>
      <div class="handle-interval">
        <div class="handle-btn">
          <el-button style="margin-left: 16px;" type="primary-text" icon="wk wk-manage" @click="setClick">管理</el-button>
        </div>
      </div>
    </el-dropdown-menu>

    <common-words-set
      :visible.sync="setShow"
      :list="list"
      :help-obj="helpObj"
      :props="setProps"
      @update="udpateCommonWords" />
  </el-dropdown>
</template>

<script>
import merge from '@/utils/merge'

const DefaultCommonWords = {
  request: null // 添加请求和参数
}

import CommonWordsSet from './Set'

export default {
  // 常用语
  name: 'CommonWords',
  components: {
    CommonWordsSet
  },
  props: {
    props: Object, // 配置文件
    setProps: Object, // 设置配置文件
    helpObj: Object // 帮助信息
  },
  data() {
    return {
      loading: false,
      title: '常用语',
      setShow: false,
      list: []
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultCommonWords }, this.props || {})
    }
  },
  watch: {
  },
  mounted() {

  },

  beforeDestroy() {},
  methods: {
    selectItem(command) {
      this.$emit('select', command)
      this.showPopover = false
    },

    setClick() {
      this.setShow = true
    },

    /**
     * 获取常用语
     */
    getCommonWords() {
      if (this.list.length === 0) {
        this.loading = true
        this.config.request().then(res => {
          this.loading = false
          this.list = res.data || []
        }).catch(() => {
          this.loading = false
        })
      }
    },

    /**
     * 更新欢迎语
     */
    udpateCommonWords(data) {
      this.list = data
    },

    /**
     * 展示吟唱
     */
    visibleChange(visible) {
      visible && this.getCommonWords()
    }
  }
}
</script>

<style lang="scss" scoped>
.common-words {
  position: relative;
}

.wk-select-input {
  width: 80px;
  color: $--input-placeholder-color;
}

.dropdown-body {
  max-height: 150px;
  overflow-y: auto;
}
</style>
