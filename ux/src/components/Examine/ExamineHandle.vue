<template>
  <el-dialog
    ref="wkDialog"
    :title="title"
    :append-to-body="true"
    :close-on-click-modal="false"
    :visible.sync="showDialog"
    width="600px"
    @close="hiddenView">
    <div v-if="tips" class="title-tips">{{ tips }}</div>
    <el-form
      v-if="config.setBackNodeShow"
      ref="form"
      label-position="top"
      :model="form">
      <el-form-item
        class="wk-form-item"
        :label="backNodeLabel">
        <el-select
          v-model="form.flowId">
          <el-option
            v-for="item in nodeList"
            :key="item.flowId"
            :label="item.name"
            :value="item.flowId" />
        </el-select>
      </el-form-item>
    </el-form>
    <el-input
      v-model="form.content"
      :rows="8"
      :maxlength="200"
      :placeholder="placeholder"
      type="textarea"
      resize="none"
      show-word-limit />
    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        type="primary"
        @click="handleClick('confirm')">确定</el-button>
      <el-button :disabled="loading" @click="handleClick('cancel')">取消</el-button>
    </div>
  </el-dialog>
</template>
<script type="text/javascript">
import {
  examineRecordQueryRecordLogAPI,
  examineRecordQueryAdminRecordLogAPI, // 跳去的节点
  crmExamineRecordAuditAPI,
  examineEndAPI,
  crmExamineRecordAdminAuditAPI
} from '@/api/examine'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'
import merge from '@/utils/merge'

const ExamineHandleDefault = {
  setBackNodeShow: false // 是否显示驳回至节点
}

export default {
  name: 'ExamineHandle', // 合同审核操作
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    show: {
      type: Boolean,
      default: false
    },
    // 审核 1 审核通过 2 审核拒绝 4 已撤回 13 终止 14 归档  18 跳过 19 恢复
    status: {
      type: [String, Number],
      default: 1
    },
    // 详情信息id
    id: [String, Number],
    recordId: [String, Number],
    // crm_contract crm_receivables oa_examine batch_audit 批量审批 只获取文字
    examineType: {
      type: String,
      default: ''
    },
    flowId: {
      type: [String, Number],
      default: ''
    },
    props: Object, // 与 ExamineHandleDefault 内配置项照应
    getPostFields: Function // 获取字段信息
  },
  data() {
    return {
      loading: false,
      showDialog: false,
      nodeList: [], // 返回节点列表
      form: {
        flowId: '',
        content: '' // 输入内容
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),

    // 合并 props
    config() {
      return merge({ ...ExamineHandleDefault }, this.props || {})
    },

    // 审批操作类型
    statusType() {
      return {
        isSuccess: this.status == 1,
        isReject: this.status == 2,
        isWithdraw: this.status == 4,
        isTermination: this.status == 13,
        isArchive: this.status == 14,
        isInvalid: this.status == 8,
        isSkip: this.status == 18,
        isRecovery: this.status == 19
      }
    },

    // 标题
    title() {
      if (this.statusType.isSuccess) {
        return '审批通过'
      } else if (this.statusType.isReject) {
        return '审批拒绝'
      } else if (this.statusType.isWithdraw) {
        return '撤回审批'
      } else if (this.statusType.isTermination) {
        return '审批终止'
      } else if (this.statusType.isArchive) {
        return '归档'
      } else if (this.statusType.isInvalid) {
        return '作废'
      } else if (this.statusType.isSkip) {
        return '跳过'
      } else if (this.statusType.isRecovery) {
        return '恢复'
      }
      return ''
    },

    tips() {
      if (this.statusType.isSkip) {
        return '由流程管理员和超级管理员操作。跳过至哪一节点，则此节点之前的节点自动通过，在此节点继续审批流程。'
      } else if (this.statusType.isRecovery) {
        return '由流程管理员和超级管理员操作。恢复至哪一节点，从哪一节点开始重新审批，如果恢复至发起人节点，则发起人可以编辑数据重新发起审批。'
      }
      return ''
    },

    // 是流程管理员操作
    isSuperAuth() {
      return this.statusType.isSkip || this.statusType.isRecovery
    },

    // 输入框占位文字
    placeholder() {
      // 1 审核通过 2 审核拒绝 4 已撤回
      if (this.statusType.isSuccess) {
        return '请输入审批意见（选填）'
      } else if (this.statusType.isReject) {
        return '请输入审批意见（必填）'
      } else if (this.statusType.isWithdraw) {
        return '请输入撤回理由（必填）'
      } else if (this.statusType.isTermination) {
        return '请输入终止理由（必填）'
      } else if (this.statusType.isArchive) {
        return '请输入归档意见（选填）'
      } else if (this.statusType.isInvalid) {
        return '请输入作废意见（选填）'
      } else if (this.statusType.isSkip) {
        return '请输入跳过理由（必填）'
      } else if (this.statusType.isRecovery) {
        return '请输入恢复理由（必填）'
      }
      return ''
    },

    // 返回节点名称
    backNodeLabel() {
      if (this.statusType.isReject) {
        return '驳回至哪一节点'
      } else if (this.statusType.isSkip) {
        return '跳过至哪一节点'
      } else if (this.statusType.isRecovery) {
        return '恢复至哪一节点'
      }
      return ''
    }
  },
  watch: {
    show: {
      handler(val) {
        this.showDialog = val
        if (val) {
          // 审批拒绝 发送拒绝节点列表
          if (this.config.setBackNodeShow) {
            this.getNodeList()
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * @description: 返回拒绝节点列表
     * @param {*} type
     * @return {*}
     */
    getNodeList() {
      let request = null
      if (this.statusType.isSkip) {
        request = examineRecordQueryAdminRecordLogAPI
      } else if (this.statusType.isRecovery || this.statusType.isReject) {
        request = examineRecordQueryRecordLogAPI
      }

      request({
        recordId: this.recordId
      }).then(res => {
        this.nodeList = res.data
        if (this.nodeList && this.nodeList.length > 0) {
          this.$nextTick(() => {
            this.form.flowId = this.nodeList[0].flowId
          })
        }
      })
        .catch(() => {
        })
    },

    /**
     * 提交数据
     */
    async submitInfo() {
      if ((this.statusType.isReject ||
      this.statusType.isWithdraw ||
      this.statusType.isTermination ||
      this.statusType.isSkip ||
      this.statusType.isRecovery) && !this.form.content) {
        this.$message.error(this.placeholder)
      } else {
        // CRM 待办审批使用
        if (this.$listeners.confirm) {
          this.$emit('confirm', this.form.content)
        } else {
          this.loading = true
          let params = null
          let request = null
          if (this.statusType.isTermination) { // 终止
            request = examineEndAPI
            params = {
              recordId: this.recordId,
              flowId: this.flowId,
              status: this.status,
              remarks: this.form.content,
              examineUserId: this.userInfo.userId
            }
          } else {
            // 通过/拒绝
            request = this.isSuperAuth ? crmExamineRecordAdminAuditAPI : crmExamineRecordAuditAPI
            params = {
              typeId: this.id,
              recordId: this.recordId,
              status: this.status,
              remarks: this.form.content
            }
          }

          // 拒绝 恢复  跳过 传flowId
          if (this.config.setBackNodeShow) {
            params.flowId = this.form.flowId
          }

          // 字段授权值
          if (this.getPostFields) {
            try {
              params.flowFieldUpdateLogs = await this.getPostFields()
            } catch (error) {
              this.loading = false
              return
            }
          }
          request(params)
            .then(res => {
              this.loading = false
              this.$message.success('操作成功')
              // 刷新待办
              if (
                this.examineType == 'crm_contract' ||
                  this.examineType == 'crm_invoice' ||
                  this.examineType == 'crm_receivables'
              ) {
                this.$store.dispatch('GetMessageNum')
              }

              this.resetInfo()

              this.$emit('save', { type: this.status })
              this.hiddenView()
            })
            .catch(() => {
              this.loading = false
            })
        }
      }
    },

    /**
     * 操作点击
     */
    handleClick(type) {
      if (type == 'cancel') {
        this.hiddenView()
        this.resetInfo()
      } else if (type == 'confirm') {
        if (this.examineType === 'batch_audit') {
          if (this.statusType.isReject && !this.form.content) {
            this.$message.error(this.placeholder)
            return
          }
          this.$emit('save', { examineType: 'batch_audit', content: this.form.content })
        } else {
          if (!this.recordId && this.$route.name == 'link') {
            const flag = (this.statusType.isReject ||
        this.statusType.isWithdraw ||
        this.statusType.isTermination) && !this.form.content
            this.$emit('emailExamine', this.form.content, flag)
            return
          }
          this.submitInfo()
        }
      }
    },

    /**
     * 关闭
     */
    hiddenView() {
      this.$emit('close')
    },

    /**
     * 重置信息
     */
    resetInfo() {
      this.form.content = ''
      this.form.flowId = ''
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/styles/wk-form.scss";

.el-dialog__wrapper ::v-deep .el-dialog__body {
  padding: 10px 25px 20px;
}

.title {
  padding-bottom: 8px;
  font-size: 12px;
  color: $--color-text-regular;
}

.title-tips {
  margin-bottom: 8px;
  line-height: 1.5;
  color: $--color-text-placeholder;
}
</style>
