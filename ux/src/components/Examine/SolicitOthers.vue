<template>
  <div class="container">
    <el-dialog
      title="征求他人意见"
      :visible.sync="value"
      :append-to-body="true"
      :close-on-click-modal="false"
      :before-close="close"
      width="40%">
      <el-form
        ref="form"
        :model="solicitOthersForm"
        label-position="top"
        class="examine-node-form">
        <el-form-item
          label="意见审批人"
          :rules="{required:true}">
          <flexbox
            class="user-wrap"
            wrap="wrap">
            <div
              v-for="(item, index) in solicitOthersForm.userList"
              :key="index"
              class="user">
              <xr-avatar
                :name="item.realname || item.email"
                :size="40"
                :src="item.img"
              />
              <div class="user__name">{{ item.realname || item.email }}</div>
              <i class="user__delete el-icon-close" @click="deleteUserClick(index, solicitOthersForm.userList)" />
            </div>
            <div
              v-if="solicitOthersForm.userList.length === 0 || solicitOthersForm.userList.length < 20"
              class="user">
              <span class="el-dropdown-link">
                <el-button
                  class="user__img"
                  icon="el-icon-plus"
                  style="margin-bottom: 8px;"
                  circle
                  @click="addingMode(solicitOthersForm,'selectUser')" />
                <div class="user__name">选择员工</div>
              </span>
              <!-- <el-dropdown
                placement="right"
                @command="addingMode(solicitOthersForm,$event)">
                <span class="el-dropdown-link">
                  <el-button
                    class="user__img"
                    icon="el-icon-plus"
                    style="margin-bottom: 8px;"
                    circle />
                  <div class="user__name">选择员工</div>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                  <el-dropdown-item command="external">选择外部联系人</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown> -->
            </div>
          </flexbox>
        </el-form-item>
        <el-form-item
          label="审批方式"
          prop="examineType"
          :rules="{required:true,message:'请选择审批方式',trigger: 'blur'}">
          <el-radio v-model="solicitOthersForm.examineType" class="radio" label="1">
            在我之前
            <span>流程先经过意见审批人，再由我审批</span>
          </el-radio>
          <el-radio v-model="solicitOthersForm.examineType" class="radio" label="2">
            在我之后
            <span>在我之后审批，即表示我同意该申请并增加审批人员</span>
          </el-radio>
        </el-form-item>

        <el-form-item
          v-if="solicitOthersForm.userList.length > 1"
          label="多人审批方式"
          prop="moreExamineType"
          :rules="{required:true,message:'请选择审批方式',trigger: 'blur'}">
          <el-radio v-model="solicitOthersForm.moreExamineType" label="1">依次审批</el-radio>
          <el-radio v-model="solicitOthersForm.moreExamineType" label="2">会签</el-radio>
          <el-radio v-model="solicitOthersForm.moreExamineType" label="3">或签</el-radio>
        </el-form-item>

        <el-form-item label="审证求意见说明">
          <wk-textarea
            v-model="solicitOthersForm.remark"
            :rows="3"
            :autosize="{ minRows: 3}"
            :maxlength="200"
            placeholder="请输入加签意见"
            resize="none"
            type="textarea" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handlerClick">确定</el-button>
      </span>
    </el-dialog>

    <wk-dep-user-dialog
      v-if="depUserViewDialogShow"
      :props="{
        showUser: true,
        showDept: false,
        showDisableUser: false,
        disableUserLabel: '员工'
      }"
      radio
      :visible.sync="depUserViewDialogShow"
      @change="selectUserChange"
    />

    <el-dialog
      v-if="externalDialogShow"
      :visible.sync="externalDialogShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      title="外部联系人"
      width="30%">
      <span>请输入外部联系人邮箱</span>
      <el-input
        v-model="email"
        style="margin-top: 10px;" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="externalDialogShow = false">取消</el-button>
        <el-button type="primary" @click="verifyEmail">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import WkTextarea from '@/components/NewCom/WkTextarea'

import { regexIsCRMEmail } from '@/utils'
export default {
  name: 'SolicitOthers', // 征求他人意见
  components: {
    WkDepUserDialog,
    WkTextarea
  },
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      solicitOthersForm: {
        userList: [],
        examineType: '1'
      },
      depUserViewDialogShow: false,
      externalDialogShow: false,
      email: '' // 外部联系人邮箱
    }
  },
  watch: {
    value(val) {
      if (val) {
        this.solicitOthersForm.userList = []
      }
    }
  },
  methods: {
    /**
     * 确定
     */
    handlerClick() {
      this.$refs.form.validate((valid) => {
        if (valid && this.solicitOthersForm.userList.length) {
          const userList = []
          this.solicitOthersForm.userList.forEach(item => {
            userList.push({
              userId: item.userId,
              email: item.email
            })
          })
          const params = {
            insertFlag: this.solicitOthersForm.examineType,
            remark: this.solicitOthersForm.remark || '',
            userList
          }
          if (this.solicitOthersForm.moreExamineType) params.type = this.solicitOthersForm.moreExamineType
          this.$emit('submitParams', params)
          this.close()
        } else {
          this.$message.error('请完善基本信息')
        }
      })
    },

    /**
     * 删除user
     */
    deleteUserClick(index, list) {
      list.splice(index, 1)
    },

    /**
     * 选择人员change
     */
    selectUserChange(usersIds, _, users) {
      if (users.length) {
        const userIds = this.solicitOthersForm.userList.map(item => item.userId)
        if (!userIds.includes(users[0].userId)) {
          this.solicitOthersForm.userList.push(users[0])
        }
      }
    },

    /**
     * 选择添加方式
     */
    addingMode(item, command) {
      if (command == 'selectUser') { // 选择员工
        this.depUserViewDialogShow = true
      } else if (command == 'external') { // 选择外部联系人
        this.externalDialogShow = true
        this.email = null
      }
    },

    /**
     * 验证邮箱
     */
    verifyEmail() {
      if (regexIsCRMEmail(this.email)) {
        const email = this.solicitOthersForm.userList.map(item => item.email)
        if (!email.includes(this.email)) {
          this.solicitOthersForm.userList.push({ email: this.email })
          this.externalDialogShow = false
        } else {
          this.$message.error('请勿重复添加')
        }
      } else {
        this.$message.error('邮箱格式有误')
      }
    },

    /**
     * 关闭弹窗
     */
    close() {
      this.$emit('input', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.examine-node-form {
  ::v-deep .el-form-item__content {
    line-height: unset;
  }

  ::v-deep .el-form-item {
    margin-bottom: 10px;

    .radio {
      display: block;

      &:nth-of-type(2) {
        margin-top: 10px;
      }

      span {
        span {
          margin-left: 10px;
          color: $--color-n300;
        }
      }
    }
  }

  .user-wrap {
    padding: 0 $--interval-base;
    text-align: center;

    .user {
      position: relative;
      padding: $--interval-base #{$--interval-base * 3} $--interval-base 0;

      .user__img {
        width: 40px;
        height: 40px;
        border-radius: 20px;
      }

      .user__name {
        margin-top: 4px;
        margin-bottom: 4px;
        overflow: hidden;
        font-size: 12px;
        color: $--color-text-secondary;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      &__delete {
        position: absolute;
        top: 12px;
        right: 25px;
        font-size: 12px;
        color: white;
        cursor: pointer;
        visibility: hidden;
        background-color: #b2b2b2;
        border-radius: 6px;

        &:hover {
          background-color: $--color-primary;
        }
      }

      &:hover {
        .user__delete {
          visibility: visible;
        }
      }
    }
  }
}

</style>
