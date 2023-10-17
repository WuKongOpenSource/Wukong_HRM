<template>
  <div class="comment-list">
    <div
      v-for="(item, index) in list"
      :key="index"
      class="comment-list-item">
      <div class="parent-reply reply">
        <div class="user-info">
          <xr-avatar
            :id="item.user.userId"
            :name="$getUserName(item.user)"
            :size="32"
            :src="$getUserImg(item.user)"
            :disabled="false"
            class="user-img" />
          <div class="user-info__bd">
            <span>{{ $getUserName(item.user) }}</span>
            <span class="time">
              {{ item.createTime | formatTime }}
            </span>
          </div>
        </div>
        <div
          class="content"
          @click="handleClick"
          v-html="item.content" />
        <!-- <tinymce
          v-model="item.content"
          disabled
          :toolbar="[]"
          :plugins="[]"
          :init="tinymceInit"
          class="content" /> -->
        <div v-if="showControl" class="control">
          <el-button
            class="reply-btn"
            type="text"
            @click="handleToReply(index)">回复</el-button>
          <el-button
            class="delete-btn"
            type="text"
            @click="handleToDelete(item, index)">删除</el-button>
        </div>
        <reply-comment
          v-if="replyIndex === String(index)"
          :props="replyConfig"
          @reply="handleReply"
          @close="replyIndex = null" />
      </div>
      <template v-if="item.childCommentList && item.childCommentList.length > 0">
        <div
          v-for="(child, childIndex) in item.childCommentList"
          :key="childIndex"
          class="child-reply-list">
          <div class="child-reply reply">
            <div class="user-info">
              <xr-avatar
                :id="child.user.userId"
                :name="$getUserName(child.user)"
                :size="34"
                :src="$getUserImg(child.user)"
                :disabled="false"
                class="user-img" />
              <div class="user-info__bd">
                <span>
                  {{ $getUserName(child.user) }}
                </span>
                <span class="time">
                  {{ child.createTime }}
                </span>
              </div>
            </div>
            <div class="child-content">
              <span>
                回复 @{{ $getUserName(child.replyUser) }}：
              </span>
              <!-- <span class="content">{{ child.content }}</span> -->
              <!-- <tinymce
                v-model="child.content"
                disabled
                :toolbar="[]"
                :plugins="[]"
                :init="tinymceInit"
                class="content" /> -->
              <div
                class="content"
                @click="handleClick"
                v-html="child.content" />
            </div>
            <div v-if="showControl" class="control">
              <el-button
                class="reply-btn"
                type="text"
                @click="handleToReply(index, childIndex)">回复</el-button>
              <el-button
                class="delete-btn"
                type="text"
                @click="handleToDelete(child, index, childIndex)">删除</el-button>
            </div>
            <reply-comment
              v-if="replyIndex === `${index}-${childIndex}`"
              v-loading="commentLoading"
              :props="replyConfig"
              @reply="handleReply"
              @close="replyIndex = null" />
          </div>
        </div>
      </template>
    </div>

    <!-- CRM详情 -->
    <c-r-m-full-screen-detail
      :id="detailId"
      :visible.sync="detailShow"
      :crm-type="detailType" />
  </div>
</template>

<script>
import ReplyComment from '@/components/ReplyComment' // 评论列表
// import Tinymce from '@/components/Tinymce'

import { mapGetters } from 'vuex'
// import xss from 'xss'
import { formatTime } from '@/utils'
import merge from '@/utils/merge'

const DefaultCommentList = {
  addRequest: null, // 添加请求和参数
  addParams: null,
  replyKey: 'pid', // 日志 任务 pid  阶段 replyId
  replyValueKey: 'userId', // 获取值的keys 日志 任务 userId  阶段 user.userId
  deleteRequest: null, // 删除请求和参数
  deleteParams: null,
  contentType: 'text' // text rich 文本或者富文本
}

export default {
  name: 'CommentList',
  components: {
    ReplyComment,
    CRMFullScreenDetail: () =>
      import('@/components/CRMFullScreenDetail')
    // Tinymce
  },
  filters: {
    formatTime(time) {
      return formatTime(time)
    }
  },
  props: {
    // 取值字段 如果继续新增 改为 props 方案
    props: {
      type: Object,
      default: () => {
        return {

        }
      }
    },
    list: {
      type: Array,
      required: true
    },
    showControl: { // 是否展示操作按钮
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      replyIndex: null,
      commentLoading: false,

      // 插件拼入
      detailId: '',
      detailShow: false,
      detailType: ''
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),

    // 合并 props
    config() {
      return merge({ ...DefaultCommentList }, this.props || {})
    },

    replyConfig() {
      return {
        contentType: this.config.contentType
      }
    },

    // 富文本配置
    tinymceInit() {
      return {
        menubar: false,
        statusbar: false,
        extended_valid_elements: 'span[class|title|wktag|style|contenteditable]',
        content_style: `p { margin: 5px 0;} body { margin: 0; }
              .wk-at-tag {
                color: #005af3;
              }
              `,
        plugins: 'autoresize'
      }
    }
  },
  methods: {
    /**
     * 显示回复框
     * @param index
     * @param childIndex
     */
    handleToReply(index, childIndex = null) {
      const str = `${index}${childIndex !== null ? ('-' + childIndex) : ''}`
      this.$emit('close-other-reply')
      if (str === this.replyIndex) return
      this.replyIndex = str
    },
    closeReply() {
      this.replyIndex = null
    },
    handleClick(e) {
      const { tagName, src, dataset } = e.target
      if (tagName === 'IMG') {
        const list = src.split('/')
        this.$wkPreviewFile.preview({
          index: 0,
          data: [{
            url: src,
            name: list[list.length - 1]
          }]
        })
      } else if (tagName === 'SPAN') {
        // @进详情
        const { wkType, wkTypeId } = dataset
        if (wkType !== 'user') {
          this.detailId = wkTypeId
          this.detailType = wkType
          this.detailShow = true
        }
      }
    },
    /**
     * 子评论排序
     */
    // sortChildComment(childList) {
    //   let arr = [].concat(childList || [])
    //   arr = arr.sort((a, b) => {
    //     return new Date(b.createTime) - new Date(a.createTime)
    //   }) || []
    //   return arr
    // },
    /**
     * 回复
     * @param data
     */
    handleReply(data, atList) {
      const params = {
        content: data,
        atUserId: (atList || []).map(item => item.userId)
      }

      let c_comment = null
      const arr = this.replyIndex.split('-')
      const f_comment = this.list[arr[0]]
      if (arr[1]) {
        c_comment = f_comment.childCommentList[arr[1]]
      }
      if (f_comment) {
        params.mainId = f_comment.commentId
        params[this.config.replyKey] = this.getReplyValue(f_comment, this.config.replyValueKey)
      }
      if (c_comment) {
        params[this.config.replyKey] = this.getReplyValue(c_comment, this.config.replyValueKey)
      }
      this.commentLoading = true
      this.config.addRequest({
        ...params,
        ...(this.config.addParams || {})
      }).then(res => {
        res.data.user = {
          userId: this.userInfo.userId,
          realname: this.$getUserName(this.userInfo),
          img: this.$getUserImg(this.userInfo)
        }
        if (c_comment) {
          res.data.replyUser = c_comment.user
        } else {
          res.data.replyUser = f_comment.user
        }
        this.list[arr[0]].childCommentList.push(res.data)
        this.replyIndex = null
        this.commentLoading = false
      }).catch(() => {
        this.commentLoading = false
      })
    },

    /**
     * @description: 获取回复对象
     * @param {*}
     * @return {*}
     */
    getReplyValue(data, keyStr) {
      if (keyStr.indexOf('.') < 0) {
        return data[keyStr]
      }
      const keys = keyStr.split('.')
      let current = data
      for (let i = 0; i < keys.length; i++) {
        current = current[keys[i]]
        if (current === undefined) {
          return '' // 如果找不到 返回空
        }
      }
      return current
    },

    /**
     * 删除回复
     */
    handleToDelete(data, index, childIndex = -1) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.config.deleteRequest({
            commentId: data.commentId,
            ...(this.config.deleteParams || {})
          }).then(res => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })

            if (childIndex >= 0) {
              this.list[index].childCommentList.splice(childIndex, 1)
            } else {
              this.$emit('delete', index)
            }
          })
        })
        .catch(() => {
        })
    }
  }
}
</script>

<style scoped lang="scss">
  .comment-list {
    .comment-list-item {
      color: $--color-text-regular;

      .reply {
        margin-top: $--interval-base;

        .user-info {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          line-height: 1;

          &__bd {
            flex: 1;
            margin-left: $--interval-base;

            .time {
              margin-left: $--interval-base;
            }
          }
        }

        .content {
          margin-top: 5px;
          margin-left: 40px;
          line-height: 1.5;
          word-wrap: break-word;

          // white-space: pre-wrap;
        }

        ::v-deep .tox-tinymce {
          border: none;
        }

        .control {
          margin-left: 40px;
          line-height: 32px;

          i {
            font-size: 14px;
          }

          .delete-btn,
          .reply-btn {
            padding: 0;
            color: $--button-default-font-color;

            &:hover {
              color: $--color-primary;
            }
          }
        }

        .child-content {
          margin-left: 40px;

          .content {
            margin-left: 0;
          }
        }
      }

      .reply-comment {
        margin-top: 10px;
      }
    }

    .child-reply-list {
      padding-left: 40px;
    }
  }

  ::v-deep .content {
    text-align: justify;

    & img {
      max-width: 100%;
      margin: 5px 0;
      cursor: pointer;
    }

    .wk-at-tag {
      color: #0052cc;
      cursor: pointer;
    }
  }
</style>
