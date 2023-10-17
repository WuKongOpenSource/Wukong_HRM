<template>
  <flexbox
    class="main"
    direction="column"
    align="stretch">
    <xr-header
      label="企业首页" />
    <div
      v-loading="loading"
      class="body">
      <flexbox align="stretch">
        <flexbox-item>
          <!-- 平台信息 -->
          <div class="section section-top-border">
            <div class="section-title">企业平台信息<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="18"
              data-id="158" /></div>
            <div class="section-content is-acccount">
              <div>{{ systemInfo ? systemInfo.usingNum : "" }}</div>
              <div>已使用用户数</div>
            </div>
          </div>
        </flexbox-item>
        <flexbox-item>
          <!-- 企业基本信息设置 -->
          <div class="section section-top-border">
            <div class="section-title">企业基本信息设置</div>
            <div class="section-content">
              <el-form ref="ruleForm" :model="companyForm" :rules="companyRules" label-width="100px" label-position="top">
                <el-form-item prop="companyDomain">
                  <template slot="label">公司名称<i
                    class="wk wk-icon-fill-help wk-help-tips"
                    data-type="18"
                    data-id="159" /></template>
                  <el-input
                    v-model.trim="companyForm.companyName"
                    :disabled="!systemSaveAuth"
                    :maxlength="50" />
                </el-form-item>
                <el-form-item prop="">
                  <template slot="label">导航头企业LOGO<i
                    class="wk wk-icon-fill-help wk-help-tips"
                    data-type="18"
                    data-id="160" /></template>
                  <el-upload
                    v-if="!companyLogo"
                    :show-file-list="false"
                    :http-request="fileUpload"
                    drag
                    :disabled="!systemSaveAuth"
                    class="upload"
                    action="http"
                    accept="image/*">
                    <i class="el-icon-plus uploader-icon" />
                  </el-upload>
                  <div
                    v-else
                    class="upload-show">
                    <img v-src="companyLogo">
                    <i
                      v-if="systemSaveAuth"
                      class="el-icon-remove icon-delete"
                      @click="deleteCompanyLogo" />
                  </div>
                </el-form-item>

                <el-form-item prop="">
                  <template slot="label">登录页企业LOGO（长logo）<i
                    class="wk wk-icon-fill-help wk-help-tips"
                    data-type="18"
                    data-id="161" /></template>
                  <el-upload
                    v-if="!companyLoginLogo"
                    :show-file-list="false"
                    :http-request="loginFileUpload"
                    :disabled="!systemSaveAuth"
                    drag
                    class="upload is-login"
                    action="http"
                    accept="image/*">
                    <i class="el-icon-plus uploader-icon" />
                  </el-upload>
                  <div
                    v-else
                    class="upload-show is-login">
                    <img v-src="companyLoginLogo">
                    <i
                      v-if="systemSaveAuth"
                      class="el-icon-remove icon-delete"
                      @click="deleteCompanyLoginLogo" />
                  </div>
                </el-form-item>
              </el-form>
              <div v-if="systemSaveAuth">
                <el-button type="primary" @click="saveClick">保存</el-button>
              </div>
            </div>
          </div>
        </flexbox-item>
      </flexbox>
    </div>
    <edit-image
      :key="uploadType"
      :fixed-number="uploadType === 'login' ? [15, 4] : [4, 4]"
      :show="showEditImage"
      :image="editImage"
      :file="editFile"
      :preview-width="uploadType === 'login' ? '150px' : '40px'"
      title="编辑企业logo"
      preview-height="40px"
      preview-radius="0"
      width="550px"
      save-button-title="确定"
      @save="submiteImage"
      @close="showEditImage=false" />
  </flexbox>
</template>

<script>
import { adminSystemSaveAPI, adminSystemConfigIndexAPI } from '@/api/admin/config'
import { crmFileSaveAPI } from '@/api/common'

import EditImage from '@/components/EditImage'
import XrHeader from '@/components/XrHeader'

import { mapGetters } from 'vuex'

export default {
  name: 'SystemConfig',
  components: {
    EditImage,
    XrHeader
  },
  data() {
    return {
      loading: false,
      showEditImage: false,
      editImage: null,
      editFile: null,
      uploadType: '', // login 登录页  workbench 工作台
      companyLogo: '',
      companyLoginLogo: '', // 登录页logo
      systemInfo: null, // 系统信息
      companyForm: { companyName: '' }, // 公司信息
      companyRules: {
        companyName: [
          { required: true, message: '公司名称不能为空', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['manage']),
    systemSaveAuth() {
      return this.manage && this.manage.system && this.manage.system.update
    }
  },

  created() {
    this.getDetail()
    this.getSystemInfo()
  },
  methods: {
    /**
     * 附件上传
     */
    fileUpload(content) {
      const reader = new FileReader()
      var self = this
      reader.onload = function(e) {
        let result
        if (typeof e.target.result === 'object') {
          // 把Array Buffer转化为blob 如果是base64不需要
          result = window.URL.createObjectURL(new Blob([e.target.result]))
        } else {
          result = e.target.result
        }
        self.editImage = result
        self.editFile = content.file
        self.uploadType = 'workbench'
        self.showEditImage = true
      }
      reader.readAsDataURL(content.file)
    },

    /**
     * 附件上传
     */
    loginFileUpload(content) {
      const reader = new FileReader()
      var self = this
      reader.onload = function(e) {
        let result
        if (typeof e.target.result === 'object') {
          // 把Array Buffer转化为blob 如果是base64不需要
          result = window.URL.createObjectURL(new Blob([e.target.result]))
        } else {
          result = e.target.result
        }
        self.editImage = result
        self.editFile = content.file
        self.uploadType = 'login'
        self.showEditImage = true
      }
      reader.readAsDataURL(content.file)
    },

    /**
     * 删除图片
     */
    deleteCompanyLogo() {
      this.companyLogo = ''
    },

    /**
     * 删除图片
     */
    deleteCompanyLoginLogo() {
      this.companyLoginLogo = ''
    },

    /**
     * 获取详情
     */
    getDetail() {
      this.loading = true
      this.$store
        .dispatch('SystemLogoAndName')
        .then(res => {
          this.loading = false
          const data = res.data || {}
          this.companyForm.companyName = data.companyName ? data.companyName : ''
          this.companyLogo = data.companyLogo
          this.companyLoginLogo = data.companyLoginLogo
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取系统使用信息
     */
    getSystemInfo() {
      this.loading = true
      adminSystemConfigIndexAPI().then(res => {
        const data = res.data || {}

        // 域名
        const { companyDomain } = data
        if (companyDomain) {
          this.domainForm.companyDomain = companyDomain.split('.')[0]
        }

        this.systemInfo = data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    submiteImage(data) {
      this.loading = true

      const params = { file: data.blob }
      if (this.uploadType === 'login') {
        params.isPublic = 1
      }

      crmFileSaveAPI(params).then(res => {
        const resData = res.data || {}
        if (this.uploadType == 'workbench') {
          this.companyLogo = resData.url
        } else {
          this.companyLoginLogo = resData.url
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 保存
     */
    saveClick() {
      if (!this.companyForm.companyName || !this.companyForm.companyName.trim()) {
        this.$message.error('企业名称不能为空')
      } else {
        this.loading = true
        adminSystemSaveAPI({
          companyName: this.companyForm.companyName,
          companyLogo: this.companyLogo,
          companyLoginLogo: this.companyLoginLogo
        })
          .then(res => {
            this.loading = false
            this.$message.success('操作成功')
            this.getDetail()
          })
          .catch(() => {
            this.loading = false
          })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../styles/index.scss";

.main-label {
  color: $--color-b300;
}

.info {
  position: relative;
  padding: 40px 30px;
  margin-bottom: 10px;
  background-color: white;
  border: 1px solid #e6e6e6;
  border-radius: $--border-radius-base;
}

// 表单风格
::v-deep .el-form-item {
  .el-form-item__label {
    padding-bottom: 8px;
    line-height: 1;
  }

  .el-form-item__content {
    line-height: $--input-height;
  }
}

// 快捷键
.quick-item {
  padding: 16px;
  color: $--color-text-regular;
  cursor: pointer;
  border-radius: $--border-radius-base;
  box-shadow: $--box-shadow-bottom-light;

  &-icon {
    width: 40px;
    height: 40px;
    margin-right: 16px;
    line-height: 40px;
    text-align: center;
    background-color: $--color-primary;
    border-radius: $--border-radius-base;

    i {
      font-size: 24px;
      color: $--color-white;
    }
  }

  .quick-item-title {
    font-size: 16px;
    color: $--color-text-primary;

    div:nth-child(2) {
      margin-top: 4px;
      font-size: $--font-size-base;
      font-weight: 600;
      color: $--color-n300;
    }
  }
}

.quick-item + .quick-item {
  margin-top: 16px;
}

.body {
  flex: 1;
}

.save-button {
  margin-left: 250px;
}

.section + .section {
  margin-top: #{$--interval-base * 2};
}

.section-top-border {
  padding: #{$--interval-base * 3};
}

.section-title {
  margin-bottom: 24px;
  font-size: $--font-size-large;
  font-weight: bold;

  > .tips {
    font-size: 12px;
    color: $--color-n60;
  }
}

.section-content {
  .name {
    margin-bottom: $--interval-base;
    font-size: $--font-size-base;
    color: $--color-n300;
  }

  .el-input {
    width: 300px;
  }

  &.is-message {
    line-height: 1.5;
    word-break: break-all;
    word-wrap: break-word;
    white-space: pre-wrap;
  }

  // 账号信息
  &.is-acccount {
    text-align: center;

    div:first-child {
      font-size: $--font-size-xxlarge;
    }

    div:nth-child(2) {
      margin-top: 6px;
      color: $--color-text-secondary;
    }
  }
}

.uploader-icon {
  width: 80px;
  height: 80px;
  font-size: 28px;
  line-height: 80px;
  color: #8c939d;
  text-align: center;
}

.upload ::v-deep .el-upload-dragger {
  width: 80px;
  height: 80px;
}

.upload.is-login ::v-deep .el-upload-dragger {
  width: 300px;
  height: 80px;
}

.upload-show {
  position: relative;
  display: block;
  width: 80px;
  height: 80px;

  img {
    width: 100%;
    height: 100%;
  }

  .icon-delete {
    position: absolute;
    top: -10px;
    right: -8px;
    display: none;
    font-size: 20px;
    color: red;
    cursor: pointer;
  }

  &.is-login {
    width: 300px;
  }
}

.upload-show:hover {
  .icon-delete {
    display: block;
  }
}
</style>

