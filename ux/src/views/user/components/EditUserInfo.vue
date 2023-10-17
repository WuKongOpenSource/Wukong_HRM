<template>
  <div
    v-loading="loading"
    class="edit-user-info">
    <div class="head">
      <!-- <span class="wk wk-user icon" /> -->
      <span class="text">个人信息</span>
    </div>
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-position="left"
      label-width="120px">
      <el-form-item label="头像">
        <flexbox class="user-box">
          <xr-avatar
            :name="userInfo.realname"
            :size="70"
            :src="userInfo.img"
            class="user-img" />
          <div class="change-avatar" @click="handleChangeAvatar">
            更换头像
          </div>
        </flexbox>
      </el-form-item>
      <el-form-item
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.field"
        :label="item.label">
        <el-input
          v-if="item.type !== 'select'"
          v-model="form[item.field]"
          :maxlength="30"
          :disabled="item.disabled" />
        <el-select
          v-else
          v-model="form[item.field]">
          <el-option
            v-for="option in item.setting"
            :key="option.value"
            :label="option.label"
            :value="option.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>

    <input
      id="inputFile"
      type="file"
      accept="image/png, image/jpeg, image/gif, image/jpg"
      style="display: none;"
      @change="uploadFile">
    <edit-image
      :show="showEditImage"
      :file="editFile"
      :image="editImage"
      @save="submitImage"
      @close="showEditImage=false" />
  </div>
</template>

<script>
import {
  adminUsersUpdateImgAPI,
  adminUsersUpdateAPI
} from '@/api/user/personCenter'

import EditImage from '@/components/EditImage'

import { mapGetters } from 'vuex'
import { regexIsCRMMobile, regexIsCRMEmail } from '@/utils'

export default {
  name: 'EditUserInfo',
  components: {
    EditImage
  },
  data() {
    const validateCRMMobile = (rule, value, callback) => {
      if (!value || value == '' || regexIsCRMMobile(value)) {
        callback()
      } else {
        callback(new Error('手机格式有误'))
      }
    }
    const validateCRMEmail = (rule, value, callback) => {
      if (!value || value == '' || regexIsCRMEmail(value)) {
        callback()
      } else {
        callback(new Error('邮箱格式有误'))
      }
    }
    return {
      rules: {
        realname: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
        email: [{ validator: validateCRMEmail, trigger: 'change' }],
        username: [
          { required: true, message: '请填写姓名', trigger: 'blur' },
          { validator: validateCRMMobile, trigger: 'change' }
        ]
      },
      form: {},
      loading: false,

      showEditImage: false,
      editFile: null,
      editImage: null
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    fieldList() {
      return [
        { label: '姓名', field: 'realname' },
        { label: '手机号(登录名)', field: 'mobile', disabled: true },
        { label: '直属上级', field: 'parentName', disabled: true },
        { label: '性别', field: 'sex', type: 'select', setting: [
          { label: '请选择', value: 0 },
          { label: '男', value: 1 },
          { label: '女', value: 2 }
        ] },
        { label: '邮箱', field: 'email' },
        { label: '部门', field: 'deptName', disabled: true },
        { label: '岗位', field: 'post' }
      ]
    }
  },
  watch: {
    userInfo: {
      handler() {
        this.initData()
      },
      deep: true,
      immediate: true
    }
  },
  created() {
  },
  methods: {
    initData() {
      this.form = Object.assign({}, this.userInfo)
    },
    handleChangeAvatar() {
      document.getElementById('inputFile').click()
    },
    /**
     * 图片操作
     * @param event
     */
    uploadFile(event) {
      const files = event.target.files
      const file = files[0]
      const reader = new FileReader()
      const self = this
      reader.onload = function(e) {
        let result
        if (typeof e.target.result === 'object') {
          // 把Array Buffer转化为blob 如果是base64不需要
          result = window.URL.createObjectURL(new Blob([e.target.result]))
        } else {
          result = e.target.result
        }
        self.editImage = result
        self.editFile = file
        self.showEditImage = true
        e.target.value = ''
      }
      reader.readAsDataURL(file)
    },
    /**
     * 上传提交头像修改
     * @param data
     */
    submitImage(data) {
      this.loading = true
      const param = new FormData()
      param.append('userId', this.form.userId)
      param.append('file', data.blob, data.file.name)
      adminUsersUpdateImgAPI(param).then(() => {
        this.loading = false
        this.$emit('change')
      }).catch(() => {
        this.loading = false
      })
    },
    /**
     * 个人信息编辑
     */
    handleSave() {
      const params = {
        realname: this.form.realname,
        sex: this.form.sex,
        email: this.form.email,
        post: this.form.post,
        username: this.form.username
      }
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          adminUsersUpdateAPI(params).then(() => {
            this.loading = false
            this.$message.success('保存成功')
            this.$emit('change')
          }).catch(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
  @import "./style";

  .edit-user-info {
    width: 100%;
    padding: 22px 25px;
    background-color: white;
  }

  .valid-mark {
    .wk-icon-safe-line {
      margin-right: 4px;
    }

    color: $--color-primary;
  }
</style>
