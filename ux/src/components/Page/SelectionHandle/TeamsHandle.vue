<template>
  <el-dialog
    ref="wkDialog"
    :title="title"
    :visible.sync="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="500px"
    @close="handleCancel">
    <el-form
      ref="fieldForm"
      :model="form"
      :validate-on-rule-change="false"
      label-position="top">
      <el-form-item
        v-if="!members"
        class="wk-form-item"
        prop="usersList"
        :rules="[
          { required: true, message: '请选择团队成员', trigger: 'change' }
        ]">
        <template slot="label">选择团队成员<el-tooltip
          v-if="!isCreate"
          effect="dark"
          content="此操作不可移除数据负责人"
          placement="top">
          <i class="wk wk-help wk-help-tips" />
        </el-tooltip>
        </template>
        <wk-user-dialog-select
          v-model="form.usersList"
          style="width: 100%;"
          placeholder="点击选择（多选）" />
      </el-form-item>

      <el-form-item
        v-if="isCreate"
        class="wk-form-item"
        label="权限"
        prop="handleType">
        <el-radio-group v-model="form.handleType">
          <el-radio :label="1">只读
            <i v-if="config.readOnlyHelp" :data-type="config.readOnlyHelp.type" :data-id="config.readOnlyHelp.id" class="wk wk-icon-fill-help wk-help-tips" @click.stop="" /></el-radio>
          <el-radio :label="2">读写
            <i v-if="config.readWriteHelp" :data-type="config.readWriteHelp.type" :data-id="config.readWriteHelp.id" class="wk wk-icon-fill-help wk-help-tips" @click.stop="" /></el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        v-if="config.showAddType"
        class="wk-form-item"
        :label="isCreate ? '同时添加至' : '同时移除'"
        prop="addsTypes">
        <el-checkbox-group v-model="form.addsTypes">
          <el-checkbox label="1">联系人</el-checkbox>
          <el-checkbox label="2">商机</el-checkbox>
          <el-checkbox label="3">合同</el-checkbox>
          <el-checkbox label="17">回访</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item
        v-if="isCreate"
        class="wk-form-item"
        label="有效时间">
        <el-select v-model="form.validType">
          <el-option label="不限" value="" />
          <el-option label="截止到" value="end" />
        </el-select>
        <el-date-picker
          v-if="form.validType === 'end'"
          v-model="form.expiresTime"
          :picker-options="pickerOptions"
          value-format="yyyy-MM-dd"
          style="margin-left: 8px;"
          type="date"
          placeholder="选择日期" />
      </el-form-item>
    </el-form>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleCancel">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import WkUserDialogSelect from '@/components/NewCom/WkUserDialogSelect'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import merge from '@/utils/merge'

const DefaultTeamsHandle = {
  type: 'add', // add 添加 delete 移除
  addRequest: null,
  removeRequest: null,
  params: null,
  // 同时添加至联系人
  showAddType: false,
  // 帮助提示
  readOnlyHelp: null,
  readWriteHelp: null
}

export default {
  /** 客户管理 的 勾选后的 团队成员 操作 移除操作不可移除客户负责人*/
  name: 'TeamsHandle',
  components: {
    WkUserDialogSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    dialogVisible: {
      type: Boolean,
      required: true,
      default: false
    },
    props: Object,
    // // add 添加 delete 移除
    // type: {
    //   type: String,
    //   default: ''
    // },
    /** 没有值就是全部类型 有值就是当个类型 */
    // crmType: {
    //   type: String,
    //   default: ''
    // },
    // /** 转移数据 */
    // selectionList: {
    //   type: Array,
    //   default: () => {
    //     return []
    //   }
    // },
    // 选择的成员，该字段存在，将不展示员工选择
    members: Array
  },
  data() {
    return {
      loading: false, // 加载动画
      visible: false,

      pickerOptions: {
        disabledDate(time) {
          // 当前0点时间戳
          return time.getTime() < new Date(new Date().toLocaleDateString()).getTime()
        }
      },
      form: {
        usersList: [], // 变更负责人
        handleType: 1, // 操作类型
        addsTypes: [], // 添加至
        validType: '', // 有效类型
        expiresTime: '' // 有效时间
      }

    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultTeamsHandle }, this.props || {})
    },
    // // 客户允许同时添加至联系人
    // addsTypesShow() {
    //   return this.crmType === 'customer'
    // },
    // 是新建
    isCreate() {
      return this.config.type === 'add'
    },
    title() {
      return {
        add: '添加团队成员',
        delete: '移除团队成员'
      }[this.config.type]
    }

    // 帮助信息
    // readOnlyHelpObj() {
    //   return {
    //     customer: {
    //       type: '8',
    //       id: '101'
    //     }, contacts: {
    //       type: '9',
    //       id: '122'
    //     }, business: {
    //       type: '10',
    //       id: '123'
    //     }, contract: {
    //       type: '11',
    //       id: '124'
    //     }, receivables: {
    //       type: '12',
    //       id: '125'
    //     }
    //   }[this.crmType] || null
    // },

    // // 帮助信息
    // readWriteHelpObj() {
    //   return {
    //     customer: {
    //       type: '8',
    //       id: '102'
    //     }, contacts: {
    //       type: '9',
    //       id: '126'
    //     }, business: {
    //       type: '10',
    //       id: '127'
    //     }, contract: {
    //       type: '11',
    //       id: '128'
    //     }, receivables: {
    //       type: '12',
    //       id: '129'
    //     }
    //   }[this.crmType] || null
    // }
  },
  watch: {
    dialogVisible: {
      handler(val) {
        this.visible = val
        if (!val) {
          this.resetData()
        }
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    this.visible = this.dialogVisible
  },
  methods: {
    /**
     * 重置数据
     */
    resetData() {
      this.form = {
        usersList: [], // 变更负责人
        handleType: 1, // 操作类型
        addsTypes: [], // 添加至
        validType: '', // 有效类型
        expiresTime: '' // 有效时间
      }
    },

    /**
     * 取消选择
     */
    handleCancel() {
      this.visible = false
      this.$emit('update:dialogVisible', false)
    },
    handleConfirm() {
      // 移除操作不可移除客户负责人
      this.$refs.fieldForm.validate((valid) => {
        if (valid) {
          if (!this.members && this.form.usersList.length === 0) {
            this.$message.error('请选择团队成员')
          } else if (this.form.validType === 'end' && !this.form.expiresTime) {
            this.$message.error('请选择截止日期')
          } else {
            let params = {
              // ids: this.selectionList.map(item => item[this.crmType + 'Id']),
              memberIds: this.form.usersList
            }

            // 如果有传入成员，替换选择成员
            if (this.members) {
              params.memberIds = this.members.map(item => item.userId)
            }
            if (this.config.showAddType) {
              // 只有客户下面同时添加到
              params.changeType = this.form.addsTypes.map(function(i) {
                return parseInt(i)
              })
            }

            params.expiresTime = this.form.validType === 'end' ? this.form.expiresTime : ''

            let request
            if (this.isCreate) {
              // 1只读，2读写
              params.power = this.form.handleType
              // request = {
              //   customer: crmCustomerSettingTeamSaveAPI,
              //   contract: crmContractSettingTeamSaveAPI,
              //   business: crmBusinessSettingTeamSaveAPI,
              //   contacts: crmContactsSettingTeamSaveAPI,
              //   receivables: crmReceivablesSettingTeamSaveAPI
              // }[this.crmType]
              request = this.config.addRequest
            } else {
              // request = {
              //   customer: crmCustomerSettingTeamDeleteAPI,
              //   contract: crmContractSettingTeamDeleteAPI,
              //   business: crmBusinessSettingTeamDeleteAPI,
              //   contacts: crmContactsSettingTeamDeleteAPI,
              //   receivables: crmReceivablesSettingTeamDeleteAPI
              // }[this.crmType]
              request = this.config.removeRequest
            }

            if (this.config.params) {
              params = { ...params, ...this.config.params }
            }

            this.loading = true

            request(params)
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
                this.loading = false
                this.handleCancel()
                this.$emit('handle', {
                  type: this.isCreate ? 'add_user' : 'delete_user'
                })
              })
              .catch(() => {
                this.loading = false
              })
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/wk-form.scss";

.tips {
  position: absolute;
  bottom: -2px;
  left: 100px;
  font-size: 12px;
  color: $--color-text-secondary;
}
</style>
