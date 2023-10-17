<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="600px"
    @close="close">
    <span slot="title" class="el-dialog__title">常用语管理<i v-if="helpObj" class="wk wk-icon-fill-help wk-help-tips" :data-type="helpObj.type" :data-id="helpObj.id" /></span>
    <div class="set">
      <draggable
        v-model="setList"
        :options="{ group: 'list',forceFallback:false, fallbackClass:'draggingStyle',filter: '.el-input__inner', preventOnFilter: false }">
        <flexbox
          v-for="(item, index) in setList"
          :key="index"
          :class="{ 'is-drag': isDrag }"
          class="set-item">
          <el-input
            v-model.trim="item.value"
            :maxlength="100">
            <div
              slot="suffix"
              class="drag-handle"
              @mouseenter="dragEnter"
              @mouseleave="dragLeave">⋮⋮</div>
          </el-input>
          <i
            class="el-icon-remove"
            @click="deleteClick(index)" />
        </flexbox>
      </draggable>
      <el-button
        type="text"
        icon="el-icon-circle-plus"
        @click="addClick">添加常用语</el-button>
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="sureClick"
        type="primary">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import draggable from 'vuedraggable'
import merge from '@/utils/merge'

const DefaultCommonWordsSet = {
  request: null // 添加请求和参数
}

export default {
  // 常用语设置
  name: 'CommonWordsSet',
  components: {
    draggable
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    props: Object, // 配置文件
    helpObj: Object, // 帮助信息
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    list: Array
  },
  data() {
    return {
      loading: false,
      setList: [],
      isDrag: false
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultCommonWordsSet }, this.props || {})
    }
  },
  watch: {
    list: {
      handler() {
        this.initInfo()
      },
      immediate: true
    },
    visible(value) {
      if (value) {
        this.initInfo()
      }
    }
  },
  created() {},

  beforeDestroy() {},
  methods: {
    initInfo() {
      this.setList = this.list.map(item => {
        return { value: item }
      })

      if (this.setList.length == 0) {
        this.setList = [{ value: '' }]
      }
    },

    /**
     * 取消选择
     */
    close() {
      this.$emit('update:visible', false)
    },

    addClick() {
      this.setList.push({ value: '' })
    },

    deleteClick(index) {
      this.setList.splice(index, 1)
    },

    sureClick() {
      const value = []
      for (let index = 0; index < this.setList.length; index++) {
        const element = this.setList[index]
        if (element.value) {
          value.push(element.value)
        }
      }

      if (value.length == 0) {
        this.$message.error('请输入常用语')
        return
      }

      this.loading = true
      this.config.request(value)
        .then(res => {
          this.loading = false
          this.$emit('update', value)
          this.close()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 拖动样式
     */
    dragEnter() {
      this.isDrag = true
    },

    dragLeave() {
      this.isDrag = false
    }

  }
}
</script>

<style lang="scss" scoped>
.set {
  height: 30vh;
  padding: 8px;
  overflow-y: auto;
}

.set-item.is-drag {
  ::v-deep input {
    background-color: #f8f8f8;
  }

  .el-icon-remove {
    visibility: hidden !important;
  }
}

.set-item {
  margin-bottom: 10px;

  .el-input {
    flex: 1;
  }

  .el-icon-remove {
    flex-shrink: 0;
    margin-left: 8px;
    color: #ff6767;
    cursor: pointer;
    visibility: hidden;
  }

  .drag-handle {
    flex-shrink: 0;
    padding: 0 8px;
    margin-top: 9px;
    color: $--color-text-primary;
    cursor: move;

    &:hover {

    }

    // visibility: hidden;
  }

  &:hover {
    .el-icon-remove {
      visibility: visible;
    }

    .drag-handle {
      visibility: visible;
    }
  }
}
</style>
