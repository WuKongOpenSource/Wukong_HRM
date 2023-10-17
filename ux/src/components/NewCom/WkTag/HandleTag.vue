<template>
  <el-popover
    v-model="tagShow"
    :placement="placement"
    :width="popoverWidth"
    popper-class="no-padding-popover"
    trigger="click">
    <el-button
      v-if="type=='create'"
      slot="reference"
      class="footer-row"
      type="text"
      @click="createTagClick">+ 创建标签</el-button>
    <el-button
      v-if="type=='manage'"
      slot="reference"
      class="footer-row"
      type="text"
      @click="managementTag">+ 管理标签</el-button>
    <!-- 新建标签页 -新建 - 编辑 -->
    <new-tag
      v-if="tagContent == 1 || tagContent == 3"
      :new-tag-title="newTagTitle"
      :new-tag-input="newTagInput"
      :bg-color-props="bgColorProps"
      @changeColor="changeColor"
      @close="tagClose"
      @tagCreateSubmit="tagCreateSubmit"
      @tagCancel="tagCancel"
      @back="back" />
    <!-- 标签管理 -->
    <editTag
      v-else-if="tagContent == 2"
      :edit-tag-list="tagList"
      @back="back"
      @close="tagClose"
      @editBtn="editTagClick"
      @deleteBtn="deleteBtn" />

  </el-popover>
</template>

<script>
import {
  crmSaveLabelAPI,
  crmDelLabelAPI
} from '@/api/crm/common'

import NewTag from './NewTag'
import EditTag from './EditTag'

export default {
  name: 'HandleTag',
  components: {
    NewTag,
    EditTag
  },
  props: {
    type: String,
    moduleVal: [String, Number],
    show: Boolean,
    placement: String,
    outerTagList: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      tagShow: false,
      tagList: [],
      tagContent: '',
      // 新增、编辑标签标题
      newTagType: 'create', // create update
      // 创建-编辑标签的输入框
      newTagInput: '',
      // 标签颜色
      bgColorProps: '',
      // 标签编辑ID
      editTagId: ''
    }
  },
  computed: {
    // 窗口宽度
    popoverWidth() {
      // 1 新建编辑 2 管理 0 列表
      if (this.tagContent == 1) {
        return 330
      } else if (this.tagContent == 2) {
        return 400
      }

      return 220
    },

    // 新建编辑弹框title
    newTagTitle() {
      return this.newTagType === 'create' ? '创建新标签' : '编辑标签'
    }
  },
  watch: {
    show(val) {
      this.tagShow = val
    },
    outerTagList: {
      handler(val) {
        this.tagList = val || []
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    /**
     * 创建新标签
     */
    createTagClick() {
      this.newTagType = 'create'
      this.newTagInput = ''
      this.tagContent = 1
    },

    /**
     * 标签管理 -- 编辑
     */
    editTagClick(val) {
      this.editTagId = val.labelId
      this.newTagType = 'update'
      this.tagContent = 1
      this.bgColorProps = val.color
      this.newTagInput = val.name
    },

    /**
     * 标签管理弹出框
     */
    managementTag() {
      this.tagContent = 2
    },

    // 标签点击变色
    changeColor(val) {
      this.bgColorProps = val
    },

    /**
     * 标签管理 -- 关闭按钮
     */
    tagClose() {
      this.tagShow = false
    },

    /**
     * 创建新标签 -- 提交
     */
    tagCreateSubmit(val, color) {
      if (this.newTagType == 'create') {
        crmSaveLabelAPI({
          type: this.moduleVal,
          name: val,
          color: color
        }).then(res => {
          // 刷新标签列表
          this.$emit('refresh')
          // 关闭标签页
          this.tagClose()
          this.$message.success('创建成功')
        })
      } else {
        crmSaveLabelAPI({
          type: this.moduleVal,
          name: val,
          labelId: this.editTagId,
          color: color
        }).then(res => {
          // eslint-disable-next-line
          for (const item of this.tagList) {
            if (item.labelId == this.editTagId) {
              item.name = val
              item.color = color
              item.labelName = item.name
              this.$emit('refresh')
            }
          }
          this.tagContent = 2
        })
      }
    },

    /**
     * 创建新标签 -- 取消
     */
    tagCancel() {
      if (this.newTagType == 'create') {
        // 关闭标签页
        this.tagClose()
      } else {
        this.tagContent = 2
      }
    },

    /**
     * 标签管理 ——— 返回上一页
     */
    back() {
      if (this.tagContent == 1) {
        if (this.newTagType == 'create') {
          this.tagClose()
        } else {
          this.tagContent = 2
        }
      } else if (this.tagContent == 2) {
        this.tagClose()
      } else if (this.tagContent == 3) {
        this.tagContent = 2
      }
    },

    /**
     * 标签管理 ——— 删除按钮
     */
    deleteBtn(val) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          crmDelLabelAPI([val.labelId]).then(res => {
            // eslint-disable-next-line no-unused-vars
            for (const i in this.tagList) {
              if (this.tagList[i].labelId == val.labelId) {
                this.tagList.splice(i, 1)
                break
              }
            }
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.tagShow = true
          }).catch(() => {
            // 防止窗口关闭
            this.tagShow = true
          })
        })
        .catch(() => {
          // 防止窗口关闭
          this.tagShow = true
        })
    }
  }
}
</script>

<style lang="scss">
.footer-row:hover {
  color: $--color-primary !important;
}
</style>
