<template>
  <div>
    <el-dialog
      :visible="showCRMImport"
      :append-to-body="true"
      :close-on-click-modal="false"
      width="750px"
      @close="closeView">
      <span slot="title" class="el-dialog__title">
        导入{{ crmTypeName }}
        <i
          v-if="crmProps && crmProps.helpObj"
          class="wk wk-icon-fill-help wk-help-tips"
          :data-type="crmProps.helpObj.dataType"
          :data-id="crmProps.helpObj.dataId" />
      </span>
      <div class="dialog-body">
        <el-steps
          :active="stepsActive"
          simple>
          <el-step
            v-for="(item, index) in stepList"
            :key="index"
            :title="item.title"
            :icon="item.icon"
            :status="item.status" />
        </el-steps>

        <div v-if="stepsActive == 1" class="step-section">

          <div class="sections">
            <div class="sections__title">一、请按照数据模板的格式准备要导入的数据。<span
              class="download"
              @click="download">点击下载《{{ crmTypeName }}导入模板》</span></div>
            <div class="sections__tips">导入文件请勿超过2MB（约10,000条数据）</div>
          </div>
          <div v-if="config.repeatHandleShow" class="sections">
            <div class="sections__title">二、请选择数据重复时的处理方式<el-tooltip
              effect="dark"
              placement="top">
              <template slot="content">1、覆盖系统原有数据：根据查重规则，重复的数据导入后完全覆盖原有数据<br>2、跳过：根据查重规则，重复的数据不导入<br>3、更新系统原有数据：根据查重规则，重复的数据导入后表格中有值的字段值更新到系统，表格中为空的字段值将不更新到系统<br>4、若未开启查重规则：导入的数据全部按照新增数据处理</template>
              <i class="wk wk-icon-fill-help wk-help-tips" />
            </el-tooltip><template v-if="config.repeatRuleShow">（查重规则<i v-if="helpObj" class="wk wk-icon-fill-help wk-help-tips" :data-type="helpObj.type" :data-id="helpObj.id" />{{ `：【${ fieldUniqueInfo }】）` }}</template></div>
            <div v-if="config.repeatRuleShow" class="sections__tips">查重规则为：添加{{ crmTypeName }}时所需填写的所有唯一字段，当前设置唯一字段为：{{ fieldUniqueInfo }}</div>
            <div class="content">
              <el-select
                v-model="repeatHandling"
              >
                <el-option
                  v-for="(item, index) in [{name: '覆盖系统原有数据',value: 1},{name: '跳过',value: 2},{name: '更新系统原有数据',value: 3}]"
                  :key="index"
                  :label="item.name"
                  :value="item.value" />
              </el-select>
            </div>
          </div>
          <div class="sections">
            <div class="sections__title">{{ config.repeatHandleShow ? '三、请选择需要导入的文件' : '二、请选择需要导入的文件' }}</div>
            <div class="content">
              <flexbox class="file-select">
                <el-input
                  v-model="file.name"
                  :disabled="true" />
                <el-button
                  type="primary"
                  @click="selectFile">选择文件</el-button>
              </flexbox>
            </div>
          </div>
          <div v-if="config.ownerSelectShow" class="sections">
            <div class="sections__title">四、请选择负责人（负责人为必填字段，若不填写，则会导致导入失败）</div>
            <div class="content">
              <div class="user-cell">
                <wk-user-dialog-select
                  v-model="user"
                  radio
                  style="width: 100%;"
                  @change="getFirstStepStatus" />
              </div>
            </div>
          </div>

          <div v-if="config.poolSelectShow" class="sections">
            <div class="sections__title">{{ config.poolSelectName || '四、请选择公海' }}</div>
            <div class="content">
              <div class="user-cell">
                <el-select v-model="poolId">
                  <el-option
                    v-for="item in poolList"
                    :key="item.poolId"
                    :label="item.poolName"
                    :value="item.poolId" />
                </el-select>
              </div>
            </div>
          </div>
        </div>

        <div
          v-else-if="stepsActive == 2"
          v-loading="loading"
          :element-loading-text="importLoadingText"
          element-loading-spinner="el-icon-loading"
          class="step-section">
          <div class="step-section__tips">当前数据正在导入，您可以点击【最小化】隐藏该页面，数据导入不受影响。</div>
        </div>

        <div
          v-else-if="stepsActive == 3"
          v-loading="loading"
          class="step-section">
          <div class="result-info">
            <i v-if="resultData" class="wk wk-success result-info__icon" />
            <p class="result-info__des">{{ resultData ? '数据导入完成' : '数据导入失败' }}</p>
            <p v-if="resultData" class="result-info__detail">导入总数据<span class="result-info__detail--all"> {{ resultData.totalSize }} </span>条，
              导入成功<span class="result-info__detail--suc"> {{ resultData.totalSize - resultData.errSize }} </span>条<template v-if="repeatHandling===1 && config.coverShow">（其中覆盖<span class="result-info__detail--suc"> {{ resultData.updateSize }} </span>条 ）</template>，
              导入失败<span class="result-info__detail--err"> {{ resultData.errSize }} </span>条<span v-if="repeatHandling===2">（其中跳过 {{ resultData.skipSize }} 条，错误 {{ resultData.errSize - resultData.skipSize }} 条 ）</span></p>
            <el-button
              v-if="resultData && resultData.errSize > 0"
              class="result-info__btn--err"
              type="primary-text"
              style="padding: 0;"
              @click="downloadErrData(resultData)">下载错误数据</el-button>
          </div>
        </div>

        <input
          id="import-input-file"
          ref="importInputFile"
          type="file"
          @change="uploadFile">
      </div>
      <span
        slot="footer"
        class="dialog-footer">
        <el-popover
          v-if="config.historyShow"
          v-model="historyPopoverShow"
          placement="top"
          width="800"
          popper-class="no-padding-popover"
          trigger="click">
          <import-history
            :show="historyPopoverShow"
            :crm-type="crmType"
            :props="crmProps"
            @close="historyPopoverShow = false" />
          <el-button
            slot="reference"
            class="history-btn"
            type="text">查看历史导入记录</el-button>
        </el-popover>
        <el-button
          v-if="sureTitle"
          type="primary"
          @click="sureClick">{{ sureTitle }}</el-button>
        <el-button
          :class="{ 'is-hidden': !showCancel }"
          @click="closeView">取消</el-button>
      </span>
    </el-dialog>

    <xr-import
      v-if="showFixImport"
      :process-status="crmImportStatus"
      @click.native="fixImportClick" />

  </div>
</template>

<script>
import {
  crmQueryImportNumAPI,
  crmQueryImportInfoAPI,
  crmDownImportErrorAPI,
  filedGetFieldAPI
} from '@/api/crm/common'
import {
  crmCustomerExcelImportAPI,
  crmCustomerDownloadExcelAPI,
  crmCustomerPoolDownloadExcelAPI,
  crmCustomerPoolNameListAPI,
  crmCustomerPoolExcelImportAPI
} from '@/api/crm/customer'
import {
  crmLeadsExcelImportAPI,
  crmLeadsDownloadExcelAPI,
  crmLeadsPoolNameListAPI,
  crmLeadsPoolDownloadExcelAPI,
  crmLeadsPoolExcelImportAPI
} from '@/api/crm/leads'
import {
  crmContactsExcelImportAPI,
  crmContactsDownloadExcelAPI
} from '@/api/crm/contacts'
import {
  crmBusinessExcelImportAPI,
  crmBusinessDownloadExcelAPI
} from '@/api/crm/business'
import {
  crmContractExcelImportAPI,
  crmContractDownloadExcelAPI
} from '@/api/crm/contract'
import {
  crmInvoiceExcelImportAPI,
  crmInvoiceDownloadExcelAPI
} from '@/api/crm/invoice'
import {
  crmReceivablesExcelImportAPI,
  crmReceivablesDownloadExcelAPI
} from '@/api/crm/receivables'
import {
  crmReceivablesPlanExcelImportAPI,
  crmReceivablesPlanDownloadExcelAPI
} from '@/api/crm/receivablesPlan'
import {
  crmReturnVisitExcelImportAPI,
  crmReturnVisitDownloadExcelAPI
} from '@/api/crm/visit'
import {
  crmMarketingExcelImportAPI,
  crmMarketingDownloadExcelAPI
} from '@/api/crm/marketing'
import {
  crmProductExcelImportAPI,
  crmProductDownloadExcelAPI
} from '@/api/crm/product'

import WkUserDialogSelect from '@/components/NewCom/WkUserDialogSelect'
import ImportHistory from './ImportHistory'
import XrImport from './XrImport'

import { downloadExcelWithResData, verifyFileTypeWithFileName } from '@/utils'
import Lockr from 'lockr'
import merge from '@/utils/merge'
import ImportMixins from './ImportMixins'
import { LOCAL_CRM_IMPORT } from '@/utils/constants.js'

const DefaultProps = {
  typeName: '', // 模块名称
  ownerSelectShow: false,
  poolSelectShow: false, // 代表是公海 导入模板也不一样
  poolSelectName: '', // 公海选择名称
  historyShow: true,
  repeatHandleShow: true,
  repeatRuleShow: true, // 步骤二的重复规则是否展示
  importRequest: null, // 导入请求
  importParams: null, // 导入参数
  templateRequest: null, // 模板请求
  templateParams: null, // 导入模板参数
  noImportProcess: false, // 无导入过程，上传完附件直接出结果
  downloadErrFuc: null, // 自定义下载错误的方法
  userInfo: null, // 当前登录人信息
  coverShow: true, // 是否展示覆盖
  doneFun: null // 完成方法
}

export default {
  name: 'WkCRMImport', // 文件导入
  components: {
    WkUserDialogSelect,
    ImportHistory,
    XrImport
  },
  mixins: [ImportMixins],
  props: {
    // show: {
    //   type: Boolean,
    //   default: false
    // }
    // // CRM类型
    // crmType: {
    //   type: String,
    //   default: ''
    // },
    // props: {
    //   type: Object,
    //   default: () => {
    //     return {}
    //   }
    // },
    // 是有缓存的信息展示的页面 是否完成状态
    // cacheShow: {
    //   type: Boolean,
    //   default: false
    // },
    // cacheDone: {
    //   type: Boolean,
    //   default: false
    // }
  },
  data() {
    return {
      loading: false,
      fieldList: [],
      repeatHandling: 1, // 	1 覆盖，2跳过 3 更新系统原有数据
      file: { name: '' },
      user: '',

      // 公海数据
      poolId: '',
      poolList: [],

      stepsActive: 1,
      stepList: [
        {
          icon: 'wk wk-upload',
          title: '上传文件',
          status: 'wait'
        },
        {
          icon: 'wk wk-data-import',
          title: '导入数据',
          status: 'wait'
        },
        {
          icon: 'wk wk-success',
          title: '导入完成',
          status: 'wait'
        }
      ],
      resultData: null,
      processData: {
        count: 0,
        status: ''
      },
      messageId: null,
      intervalTimer: null,

      historyPopoverShow: false
    }
  },
  computed: {
    // ...mapGetters(['userInfo']),
    crmTypeName() {
      if (this.crmProps && this.crmProps.typeName) {
        return this.crmProps.typeName
      }

      return (
        {
          customer: '客户',
          leads: '线索',
          contacts: '联系人',
          product: '产品'
        }[this.crmType] || ''
      )
    },

    importLoadingText() {
      if (this.processData.count) {
        return `数据导入中（当前已导入${this.processData.count}条）`
      }

      return '数据导入中'
    },

    sureTitle() {
      return {
        1: '立即导入',
        2: '最小化',
        3: '确定'
      }[this.stepsActive]
    },

    showCancel() {
      return this.stepsActive != 2
    },

    fieldUniqueInfo() {
      const uniqueList = this.fieldList.filter(item => item.isUnique == 1)

      return uniqueList.map(item => item.name).join('/') || '无'
    },

    config() {
      return merge({ ...DefaultProps }, this.crmProps || {})
    },
    // 帮助信息
    helpObj() {
      return {
        leads: {
          type: '7',
          id: '95'
        }, customer: {
          type: '8',
          id: '96'
        }, contacts: {
          type: '9',
          id: '97'
        }
      }[this.crmType] || null
    }
  },
  watch: {
    cacheShow: {
      handler(val) {
        // 展示缓存
        if (val) {
          // this.$emit('update:cacheShow', false)
          this.cacheShow = false
          const beforeImportInfo = Lockr.get(LOCAL_CRM_IMPORT)
          if (beforeImportInfo && beforeImportInfo.messageId) {
            this.loading = true
            this.messageId = beforeImportInfo.messageId
            this.stepList[0].status = 'finish'

            if (this.cacheDone) {
              this.stepList[1].status = 'finish'
              this.stepsActive = 3
              this.thirdQueryResult()
            } else {
              this.stepsActive = 2
              this.stepList[1].status = 'process'
            }

            this.loopSecondQueryNum()
          }
        }
      },
      immediate: true
    },

    showCRMImport: {
      handler(val) {
        if (val) {
          // 阶段一展示 需要获取的信息
          if (this.stepsActive == 1) {
            if (this.config.userInfo) {
              this.user = this.config.userInfo.userId
            }

            if (this.config.poolSelectShow) {
              this.getPoolList()
            }

            if (this.config.repeatRuleShow && this.config.repeatHandleShow) {
              this.getField()
            }
          }
        } else {
          if (this.stepsActive == 3) {
            this.resetData()
          }

          this.fieldList = []
        }
      },
      immediate: true
    },

    stepsActive() {
      // this.$emit('status', {
      //   1: 'wait',
      //   2: 'process',
      //   3: 'finish'
      // }[this.stepsActive])

      this.crmImportChange({
        1: 'wait',
        2: 'process',
        3: 'finish'
      }[this.stepsActive])
    }

    // file() {
    //   this.getFirstStepStatus()
    // },

    // user() {
    //   this.getFirstStepStatus()
    // }

  },
  created() {},
  methods: {
    import(crmType, props) {
      if (this.crmType != crmType && this.showFixImport) {
        this.$message.error('请先处理当前导入的数据')
      } else {
        this.crmType = crmType
        this.crmProps = props
        this.showCRMImport = true
      }
    },
    sureClick() {
      if (this.stepsActive == 1) {
        if (this.stepList[0].status == 'finish') {
          this.stepList[1].status = 'process'
          this.stepsActive = 2
          this.firstUpdateFile(res => {
            if (res === false) {
              this.stepList[1].status = 'error'
              this.stepList[2].status = 'error'
              this.stepsActive = 3
              this.$emit('status', 'finish')
            } else {
              const resData = res.data || {}
              if (this.config.noImportProcess) {
                this.loading = false
                this.stepList[1].status = 'finish'
                this.stepsActive = 3
                this.$emit('status', 'finish')
                this.resultData = resData
                if (resData.errSize > 0) {
                  this.stepList[2].status = 'error'
                } else {
                  this.stepList[2].status = 'finish'
                }
              } else {
                this.messageId = resData

                // 写入本次缓存
                Lockr.set(LOCAL_CRM_IMPORT, {
                  messageId: this.messageId,
                  crmProps: this.config,
                  crmType: this.crmType
                })

                this.loopSecondQueryNum()
              }
            }
          })
        } else {
          if (!this.file.name) {
            this.$message.error('请选择导入文件')
          } else if (
            this.config.ownerSelectShow && !this.user
          ) {
            this.$message.error('请选择负责人')
          }
        }
      } else {
        this.closeView()
      }
    },

    /**
     * 第一步上传
     */
    firstUpdateFile(result) {
      let params = {}
      params.repeatHandling = this.repeatHandling
      params.file = this.file
      if (this.config.ownerSelectShow) {
        params.ownerUserId = this.user
      }
      if (this.config.poolSelectShow) {
        params.poolId = this.poolId
      }
      const request = this.config.importRequest || {
        customer: this.config.poolSelectShow ? crmCustomerPoolExcelImportAPI : crmCustomerExcelImportAPI,
        leads: this.config.poolSelectShow ? crmLeadsPoolExcelImportAPI : crmLeadsExcelImportAPI,
        contacts: crmContactsExcelImportAPI,
        business: crmBusinessExcelImportAPI,
        contract: crmContractExcelImportAPI,
        invoice: crmInvoiceExcelImportAPI,
        receivables: crmReceivablesExcelImportAPI,
        receivablesPlan: crmReceivablesPlanExcelImportAPI,
        visit: crmReturnVisitExcelImportAPI,
        marketing: crmMarketingExcelImportAPI,
        product: crmProductExcelImportAPI
      }[this.crmType]
      this.loading = true
      if (this.config.importParams) {
        params = {
          ...params,
          ...this.config.importParams
        }
      }
      request(params)
        .then(res => {
          if (result) {
            result(res)
          }
        })
        .catch(() => {
          if (result) {
            result(false)
          }
          this.loading = false
        })
    },

    /**
     * 第二步查询数量
     */
    loopSecondQueryNum() {
      this.secondQueryNum()
      this.intervalTimer = setInterval(() => {
        if (this.processData.status == 'end') {
          clearInterval(this.intervalTimer)
          this.intervalTimer = null
          this.thirdQueryResult()
        } else {
          this.secondQueryNum()
        }
      }, 2000)
    },

    secondQueryNum() {
      crmQueryImportNumAPI({ messageId: this.messageId })
        .then(res => {
          if (res.data === null) {
            this.processData.status = 'end'
          } else {
            this.processData.status = ''
            this.processData.count = res.data
          }
        })
        .catch(() => {
          // this.processData.status = 'err'
        })
    },

    /**
     * 第三部 查询结果
     */
    thirdQueryResult() {
      crmQueryImportInfoAPI({ messageId: this.messageId })
        .then(res => {
          this.loading = false
          this.stepList[1].status = 'finish'
          this.stepsActive = 3
          this.$emit('status', 'finish')
          if (res) {
            this.resultData = res.data
            if (res.data.errSize > 0) {
              this.stepList[2].status = 'error'
            } else {
              this.stepList[2].status = 'finish'
            }
          }
        })
        .catch(() => {})
    },

    /**
     * 下载错误模板
     */
    downloadErrData(result) {
      this.loading = true
      if (this.config.downloadErrFuc) {
        this.config.downloadErrFuc(result).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        })
          .catch(() => {
            this.loading = false
          })
      } else {
        // 模块为财务或员工时，取token值
        crmDownImportErrorAPI({ messageId: this.messageId || result.token })
          .then(res => {
            downloadExcelWithResData(res)
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    // 下载模板操作
    download() {
      const params = {}
      const request = this.config.templateRequest || {
        customer: this.config.poolSelectShow ? crmCustomerPoolDownloadExcelAPI : crmCustomerDownloadExcelAPI,
        leads: this.config.poolSelectShow ? crmLeadsPoolDownloadExcelAPI : crmLeadsDownloadExcelAPI,
        contacts: crmContactsDownloadExcelAPI,
        business: crmBusinessDownloadExcelAPI,
        contract: crmContractDownloadExcelAPI,
        invoice: crmInvoiceDownloadExcelAPI,
        receivables: crmReceivablesDownloadExcelAPI,
        receivablesPlan: crmReceivablesPlanDownloadExcelAPI,
        visit: crmReturnVisitDownloadExcelAPI,
        marketing: crmMarketingDownloadExcelAPI,
        product: crmProductDownloadExcelAPI
      }[this.crmType]
      request(this.config.templateParams || params)
        .then(res => {
          downloadExcelWithResData(res)
        })
        .catch(() => {})
    },
    // 选择文件
    selectFile() {
      this.$refs.importInputFile.click()
    },
    /** 图片选择出发 */
    uploadFile(event) {
      var files = event.target.files
      const file = files[0]
      if (verifyFileTypeWithFileName(file.name)) {
        this.file = file
        // 阶段一状态
        this.getFirstStepStatus()
      }
      event.target.value = ''
    },

    getFirstStepStatus() {
      // 阶段一状态
      const hasFile = this.file && this.file.size
      const hasUser = !!this.user

      if (this.config.ownerSelectShow) {
        this.stepList[0].status = hasFile && hasUser ? 'finish' : 'wait'
      } else {
        this.stepList[0].status = hasFile ? 'finish' : 'wait'
      }
    },

    /**
     * 公海数据
     */
    getPoolList() {
      const request = {
        customer: crmCustomerPoolNameListAPI,
        leads: crmLeadsPoolNameListAPI
      }[this.crmType]
      request()
        .then(res => {
          this.poolList = res.data || []
          this.poolId = this.poolList.length > 0 ? this.poolList[0].poolId : ''
        })
        .catch(() => {
        })
    },

    // 关闭操作
    closeView() {
      // this.$emit('update:show', false)
      this.showCRMImport = false
      if (this.stepsActive == 3) {
        this.$bus.emit('import-crm-done-bus', this.crmType)
        if (this.config.doneFun) this.config.doneFun()
        Lockr.rm(LOCAL_CRM_IMPORT)
      }
      // this.$emit('close', this.stepsActive == 3 ? 'finish' : '')
      this.crmImportClose(this.stepsActive == 3 ? 'finish' : '')
    },

    /**
     * 重置页面数据
     */
    resetData() {
      this.repeatHandling = 1
      this.file = { name: '' }
      this.user = ''

      this.stepsActive = 1
      this.stepList = [
        {
          icon: 'wk wk-upload',
          title: '上传文件',
          status: 'wait'
        },
        {
          icon: 'wk wk-data-import',
          title: '导入数据',
          status: 'wait'
        },
        {
          icon: 'wk wk-success',
          title: '导入完成',
          status: 'wait'
        }
      ]
      this.resultData = null
      this.processData = {
        count: 0,
        status: ''
      }
      this.messageId = null
    },

    /**
     * 获取验证字段
     */
    getField() {
      var params = {
        type: 1
      }

      filedGetFieldAPI(params)
        .then(res => {
          this.fieldList = res.data
        })
        .catch(() => {
        })
    }
  }
}
</script>

<style scoped lang="scss">
.el-steps {
  margin-bottom: 15px;

  ::v-deep .el-step__title {
    font-size: 14px;
  }

  ::v-deep .el-step.is-simple .el-step__arrow::before,
  ::v-deep .el-step.is-simple .el-step__arrow::after {
    width: 2px;
    height: 10px;
  }

  ::v-deep .el-step.is-simple .el-step__arrow::after {
    transform: rotate(45deg) translateY(3px);
  }

  ::v-deep .el-step.is-simple .el-step__arrow::before {
    transform: rotate(-45deg) translateY(-2px);
  }
}

.step-section {
  position: relative;
  min-height: 300px;

  ::v-deep .el-loading-spinner {
    top: 45%;

    .el-icon-loading {
      font-size: 40px;
      color: $--color-text-secondary;
    }

    .el-loading-text {
      margin: 8px 0;
    }
  }

  &__tips {
    position: absolute;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 3000;
    font-size: 12px;
    color: $--color-text-secondary;
    text-align: center;
  }
}

.sections {
  font-size: 14px;
  color: $--color-black;

  &__title {
    font-weight: 600;

    .wk-icon-fill-help {
      margin-left: 4px;
      font-size: 14px;
    }
  }

  &__tips {
    padding-left: 30px;
    margin: 8px 0 15px;
    font-size: 12px;
    line-height: 1.4;
    color: $--color-text-secondary;
  }

  .download {
    color: $--color-primary;
    cursor: pointer;
  }
}

.sections__tips + .content {
  padding-top: 0;
}

.content {
  padding: 10px 10px 10px 30px;

  .el-select {
    width: 400px;
  }

  .user-cell {
    width: 400px;
  }
}

#import-input-file {
  display: none;
}

.file-select {
  .el-input {
    width: 400px;
  }

  button {
    margin-left: $--interval-base;
  }
}

.is-hidden {
  visibility: hidden;
}

.history-btn {
  float: left;
  margin-left: 15px;
}

// 结果信息
.result-info {
  padding-top: 80px;
  text-align: center;

  &__icon {
    font-size: 40px;
    color: $--color-primary;
  }

  &__des {
    margin-top: 15px;
    font-size: 14px;
  }

  &__detail {
    margin-top: 15px;
    color: $--color-text-regular;

    &--all {
      font-weight: 600;
      color: $--color-black;
    }

    &--suc {
      font-weight: 600;
      color: $--color-primary;
    }

    &--err {
      font-weight: 600;
      color: $--color-danger;
    }
  }

  &__btn--err {
    margin-top: 10px;
  }
}

.dialog-footer {
  font-size: 0;
}
</style>
