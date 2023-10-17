<template>
  <div class="edit-pwd">
    <div class="head">
      <!-- <span class="wk wk-circle-password icon" /> -->
      <span class="text">账号密码</span>
    </div>
    <el-form
      ref="form"
      v-loading="loading"
      :model="form"
      :rules="rules"
      label-position="left"
      label-width="120px">
      <el-form-item label="原密码" prop="oldPwd">
        <el-input
          v-model.trim="form.oldPwd"
          :maxlength="20"
          type="password" />
      </el-form-item>
      <el-form-item
        class="password-item"
        label="新密码"
        prop="newPwd">
        <el-input
          v-model.trim="form.newPwd"
          :maxlength="20"
          type="password" />
        <p>{{ passwordRulesText }}</p>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPwd">
        <el-input
          v-model.trim="form.confirmPwd"
          :maxlength="20"
          type="password" />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { adminUsersResetPasswordAPI } from '@/api/user/personCenter'
import {
  enterpriseSecurityConfigQueryAPI
} from '@/api/admin/enterpriseSecuritySetting'

import { mapGetters } from 'vuex'
import { removeAuth } from '@/utils/auth'
import Lockr from 'lockr'
import { LOCAL_CLEAR_PAGE_TIME, LOCAL_FREE_TIME } from '@/utils/constants.js'

export default {
  name: 'EditPwd',
  data() {
    const pwdReg = /^(?=.*[a-zA-Z])(?=.*\d).{6,20}$/
    return {
      loading: false,
      form: {},
      rules: {
        oldPwd: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPwd: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { pattern: pwdReg, message: '密码必须由6-20位字母、数字组成', trigger: 'blur' }
        ],
        confirmPwd: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: this.validatedConfirmPwd, trigger: 'blur' }
        ]
      },

      passwordRulesText: '',
      logOutOfLogin: false // 是否退出登录
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  created() {
    this.getResetPwdConfig()
  },
  methods: {
    validatedConfirmPwd(rule, value, callback) {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.form.newPwd) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    },
    handleSave() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          const params = {
            id: this.userInfo.userId,
            oldPwd: this.form.oldPwd,
            newPwd: this.form.newPwd
          }
          adminUsersResetPasswordAPI(params).then(() => {
            this.loading = false

            if (!this.logOutOfLogin) {
              this.$message.success('修改成功')
              return
            }

            removeAuth().then(() => {
              this.$confirm('修改成功, 请重新登录', '提示', {
                confirmButtonText: '确定',
                showCancelButton: false,
                type: 'warning'
              }).then(() => {
                Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
                Lockr.rm(LOCAL_FREE_TIME)
                location.reload()
              }).catch(() => {
                Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
                Lockr.rm(LOCAL_FREE_TIME)
                location.reload()
              })
            })
          }).catch(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    },

    /**
     * @description: 获取重置密码配置
     * @return {*}
     */
    getResetPwdConfig() {
      enterpriseSecurityConfigQueryAPI({ type: 1 })
        .then(res => {
          /**
           * pwdCharRequire
           * 0.不限
           * 1.必须包含字母+数字组合
           * 2.必须包含大写字母+小写字母+数字组合
           * 3.必须包含字母+特殊字符+数字组合
           * 4.必须包含大写字母+小写字母+特殊字符+数字组合
           */
          const { minimumPwdLen, pwdCharRequire, enforcePwdHistory, changePwdRule } = res.data
          this.logOutOfLogin = changePwdRule == 1
          const passwordRegexObj = {
            0: new RegExp(`^.{${minimumPwdLen},16}$`), // 不限
            1: new RegExp(`^(?=.*[A-Za-z])(?=.*\\d).{${minimumPwdLen},16}$`), // 必须包含字母+数字组合
            2: new RegExp(`^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{${minimumPwdLen},16}$`), // 必须包含大写字母+小写字母+数字组合
            3: new RegExp(`^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{${minimumPwdLen},16}$`), // 必须包含字母+特殊字符+数字组合
            4: new RegExp(`^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{${minimumPwdLen},16}$`) // 必须包含大写字母+小写字母+特殊字符+数字组合
          }
          const passwordRulesTextObj = {
            0: `密码由${minimumPwdLen}-16位任意字符组成`,
            1: `密码由${minimumPwdLen}-16位字母、数字组成`,
            2: `密码由${minimumPwdLen}-16位大写字母、小写字母和数字组成`,
            3: `密码由${minimumPwdLen}-16位字母、特殊字符和数字组成`,
            4: `密码由${minimumPwdLen}-16位大写字母、小写字母、特殊字符和数字组成`
          }

          const newPasswordRules = [
            { required: true, message: '请输入新密码', trigger: 'blur' },
            {
              pattern: passwordRegexObj[pwdCharRequire],
              message: passwordRulesTextObj[pwdCharRequire]
            }
          ]

          this.$set(this.rules, 'newPwd', newPasswordRules)

          const passwordTextObj = {
            0: '不限规则',
            1: '必须包含字母、数字',
            2: '必须包含大写字母、小写字母和数字',
            3: '必须包含字母、特殊字符和数字',
            4: '必须包含大写字母、小写字母、特殊字符和数字'
          }

          this.passwordRulesText = `密码规则：${minimumPwdLen}-16位，${passwordTextObj[pwdCharRequire]}。${enforcePwdHistory ? `不可与最近${enforcePwdHistory}个密码重复。` : ''}`
        })
    }
  }
}
</script>

<style scoped lang="scss">
  @import "./style";

  .edit-pwd {
    width: 100%;
    padding: 22px 25px;
    background-color: white;
  }

  .el-form {
    ::v-deep .password-item {
      .el-form-item__content {
        display: flex;

        p {
          margin-left: 10px;
          color: $--color-n100;
        }
      }
    }
  }
</style>
