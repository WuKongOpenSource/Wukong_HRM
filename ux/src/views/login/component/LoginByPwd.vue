<template>
  <el-form
    ref="ruleForm"
    :model="ruleForm"
    :rules="rules"
    label-width="100px"
    label-position="top"
    @submit.native.prevent>
    <div class="form-title">登录您的帐户</div>
    <el-form-item
      label=""
      prop="username">
      <el-input
        v-if="ruleResult.username.edit"
        ref="userInput"
        v-model.trim="ruleForm.username"
        :disabled="loading"
        placeholder="请输入手机号"
        @keyup.enter.native="continueClick" />
      <show-item
        v-else
        :content="ruleForm.username"
        @click.native="editClick('username')" />
    </el-form-item>
    <el-form-item
      v-if="passwordShow"
      label=""
      prop="password">
      <el-input
        ref="passwordInput"
        v-model.trim="ruleForm.password"
        :type="passwordType"
        placeholder="请输入密码"
        @keyup.enter.native="continueClick">
        <i
          slot="suffix"
          :style="{ color: passwordType === '' ? '#243858':'#C1C7D0' }"
          class="wk wk-icon-eye-solid"
          @click="passwordType = passwordType === '' ? 'password':''" />
      </el-input>
    </el-form-item>
    <div class="handle-bar">
      <el-button
        :loading="loading"
        :disabled="loading"
        type="primary"
        @click="continueClick">{{ loginBtnName }}</el-button>
    </div>
  </el-form>
</template>

<script>
import ShowItem from './ShowItem'

import { debounce } from 'throttle-debounce'
import { isArray } from '@/utils/types'
import LoginMixin from './LoginMixin'
import { removeAuth } from '@/utils/auth'

export default {
  // 密码登录
  name: 'LoginByPwd',

  components: {
    ShowItem
  },

  mixins: [LoginMixin],

  props: {
    username: String
  },

  data() {
    const validateUsername = (rule, value, callback) => {
      if (!this.loading) {
        callback()
      } else if (value === '') {
        callback(new Error('手机号不能为空'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      ruleForm: {
        username: '',
        password: ''
      },
      ruleResult: {
        username: {
          validate: false,
          edit: true
        },
        password: {
          validate: false,
          edit: false
        }
      },
      rules: {
        username: [
          { required: true, message: '手机号不能为空', trigger: 'change' },
          { validator: validateUsername, trigger: '' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'change' }
        ]
      },
      passwordType: 'password'
    }
  },

  computed: {
    passwordShow() {
      return !this.ruleResult.username.edit && this.ruleResult.username.validate
    },
    loginBtnName() {
      return this.passwordShow ? '登录' : '继续'
    }
  },

  watch: {
    ruleForm: {
      handler() {
        this.$nextTick(() => {
          this.changeFormState()
        })
      },
      deep: true
    },

    passwordShow() {
      if (this.passwordShow) {
        this.$nextTick(() => {
          this.$refs.passwordInput.focus()
        })
      }
    },

    username: {
      handler() {
        this.ruleForm.username = this.username
      },
      immediate: true
    },

    'ruleForm.username': {
      handler(val) {
        this.$emit('update:username', val)
      }
    }
  },

  created() {
    // 允许进入登录页面  1005 需要完善信息 不能清除登录信息
    if (!window.accessLogin) {
      removeAuth()
    }
    this.debouncedHandleLogin = debounce(300, this.handleLogin)
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 继续
     */
    continueClick() {
      this.loading = true
      this.$refs.ruleForm.validate(async(valid) => {
        this.loading = false
        await this.changeFormState()
        if (this.ruleResult.username.validate) {
          if (!this.ruleResult.password.validate) {
            this.ruleResult.username.edit = false
          } else {
            this.loading = true
            this.$store
              .dispatch('Login', this.ruleForm)
              .then(res => {
                this.loading = false
                const resData = res.data
                // 如果是数组 要区分不同版本登录
                if (isArray(resData)) {
                  this.$emit('toggle', 'multiple', resData, this.ruleForm)
                } else if (resData.hasOwnProperty('companyList')) {
                  this.$emit('toggle', 'multiple', resData.companyList, this.ruleForm)
                } else {
                  this.$router.push({ path: this.redirect || '/' })
                }
              })
              .catch(() => {
                this.loading = false
              })
          }
        }
        return false
      })
    },

    /**
     * 编辑
     */
    editClick(prop) {
      this.ruleResult[prop].edit = !this.ruleResult[prop].edit
      this.ruleResult.password.validate = false
      if (prop === 'username' && this.ruleResult[prop].edit) {
        this.$nextTick(() => {
          this.$refs.userInput.focus()
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";
</style>
