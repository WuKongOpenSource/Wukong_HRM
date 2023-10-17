import { crmQueryImportNumAPI } from '@/api/crm/common'

import Lockr from 'lockr'
import { LOCAL_CRM_IMPORT } from '@/utils/constants.js'

export default {
  data() {
    return {
      showCRMImport: false,
      crmType: '',
      crmProps: null,
      crmImportStatus: '',
      cacheShow: false, // 缓存展示
      cacheDone: false, // 缓存导入是否完成

      userInfo: null
    }
  },

  created() {
    // 处理上次缓存
    const beforeImportInfo = Lockr.get(LOCAL_CRM_IMPORT)
    if (beforeImportInfo && beforeImportInfo.messageId) {
      this.crmType = beforeImportInfo.crmType
      this.crmProps = beforeImportInfo.crmProps
      this.lockrSecondQueryNum(beforeImportInfo.messageId)
      this.cacheShow = true
    } else {
      this.cacheShow = false
    }
  },

  computed: {
    // 1.导入框展示 2.导入状态状态为空或者是等待状态  缩小框不展示
    showFixImport() {
      return !this.showCRMImport && this.crmImportStatus && this.crmImportStatus != 'wait'
    }
  },

  watch: {},

  methods: {
    crmImportChange(status) {
      this.crmImportStatus = this.showCRMImport && status == 'finish' ? '' : status
    },

    fixImportClick() {
      this.showCRMImport = true
    },

    crmImportClose(status) {
      if (status == 'finish') {
        this.crmImportStatus = ''
      }
    },

    /**
     * 第二步查询数量
     */
    lockrSecondQueryNum(messageId) {
      crmQueryImportNumAPI({ messageId: messageId })
        .then(res => {
          if (res.data === null) { // 结束 否则 进行中
            this.crmImportStatus = 'finish'
            this.cacheDone = true
          } else {
            this.cacheDone = false
            this.crmImportStatus = 'process'
          }
          this.showCRMImport = false
        })
        .catch(() => {
          Lockr.rm(LOCAL_CRM_IMPORT)
        })
    }
  }

}
