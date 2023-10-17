<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: chenhaojie 1476192083@qq.com
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    width="550px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <div>计薪人员：{{ number }}人</div>
      <flexbox class="row">
        <div class="row-mark" />
        <div class="row-label">同步社保数据</div>
        <el-switch
          v-model="form.isSyncInsuranceData" />
      </flexbox>
      <div class="row">
        <flexbox>
          <div class="row-mark" />
          <div class="row-label">同步考勤数据</div>
          <el-switch
            v-model="form.isSyncAttendanceData" />
        </flexbox>
        <flexbox style="margin-top: 10px;">
          <div class="row-mark" />
          <div class="row-label">考勤数据<el-button
            :disabled="form.isSyncAttendanceData"
            class="row-btn"
            type="primary-text"
            @click="downloadTemp('attendance')">下载考勤导入模板</el-button></div>
          <wk-file-select
            :disabled="form.isSyncAttendanceData"
            @change="fileSelect(arguments[0],arguments[1], 'attendance')">
            <el-button
              :disabled="form.isSyncAttendanceData"
              class="row-btn"
              type="primary-text">{{ getAttendanceBtnName() }}</el-button>
          </wk-file-select>
        </flexbox>
      </div>
      <div class="row">
        <flexbox>
          <div class="row-mark" />
          <div class="row-label">录入个税扣缴信息（非累计预扣法可跳过此步骤）</div>
        </flexbox>
        <flexbox class="row-children">
          <div class="row-label">专项附加扣除累计<el-button
            class="row-btn"
            type="primary-text"
            @click="downloadTemp('additional')">下载导入模板</el-button></div>
          <wk-file-select
            @change="fileSelect(arguments[0],arguments[1], 'additional')">
            <el-button class="row-btn" type="primary-text">{{ getAdditionalBtnName() }}</el-button>
          </wk-file-select>
        </flexbox>
        <flexbox class="row-children">
          <div class="row-label">截止上月个税累计<el-button
            class="row-btn"
            type="primary-text"
            @click="downloadTemp('lastMonth')">下载导入模板</el-button></div>
          <wk-file-select
            @change="fileSelect(arguments[0],arguments[1], 'lastMonth')">
            <el-button class="row-btn" type="primary-text">{{ getLastMonthBtnName() }}</el-button>
          </wk-file-select>
        </flexbox>
      </div>
    </div>
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
import {
  hrmSalaryMonthRecordComputeAPI,
  hrmSalaryMonthRecordNumAPI,
  hrmSalaryDownloadAttendanceTempAPI,
  hrmSalaryDownCumulativeOfLastMonthTempAPI,
  hrmSalaryDownAdditionalTempAPI
} from '@/api/hrm/salary'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import WkFileSelect from '@/components/NewCom/WkFile/Select'

import { downloadExcelWithResData, verifyFileTypeWithFileName } from '@/utils'

export default {
  // 核算工资
  name: 'ComputeSetDialog',
  components: {
    WkFileSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    // 最新信息 包含 srecordId
    lastData: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '核算工资',
      number: 0,
      form: {},
      file: null,
      lastMonthFile: null,
      additionalFile: null
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.number = 0
          this.file = null
          this.lastMonthFile = null
          this.additionalFile = null
          this.form = { isSyncInsuranceData: true, isSyncAttendanceData: false }
          this.getNum()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

    /**
     * 取消选择
     */
    handleCancel() {
      this.$emit('update:visible', false)
    },

    getNum() {
      // 0 未计薪 1 计薪
      hrmSalaryMonthRecordNumAPI(1)
        .then(res => {
          this.number = res.data || 0
        })
        .catch(() => {
        })
    },

    getAttendanceBtnName() {
      return this.file ? this.file.name : '导入考勤数据'
    },

    getLastMonthBtnName() {
      return this.lastMonthFile ? this.lastMonthFile.name : '导入截止上月个税累计'
    },

    getAdditionalBtnName() {
      return this.additionalFile ? this.additionalFile.name : '导入专项附加扣除累计'
    },

    /**
     * 附件操作
     */
    fileSelect(files, event, type) {
      if (files.length) {
        const file = files[0]
        if (verifyFileTypeWithFileName(file.name)) {
          if (type === 'attendance') {
            this.file = file
          } else if (type === 'additional') {
            this.additionalFile = file
          } else if (type === 'lastMonth') {
            this.lastMonthFile = file
          }
        }
      }
      event.target.value = ''
    },

    /**
     * 下载模板操作
     */
    downloadTemp(type) {
      const request = {
        attendance: hrmSalaryDownloadAttendanceTempAPI,
        additional: hrmSalaryDownAdditionalTempAPI,
        lastMonth: hrmSalaryDownCumulativeOfLastMonthTempAPI
      }[type]
      request()
        .then(res => {
          downloadExcelWithResData(res)
        })
        .catch(() => {})
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.loading = true

      if (this.file) {
        this.form.attendanceFile = this.file
      }

      if (this.lastMonthFile) {
        this.form.cumulativeTaxOfLastMonthFile = this.lastMonthFile
      }

      if (this.additionalFile) {
        this.form.additionalDeductionFile = this.additionalFile
      }

      this.form.srecordId = this.lastData.srecordId
      hrmSalaryMonthRecordComputeAPI(this.form)
        .then(res => {
          this.$message.success('操作成功')
          this.$emit('change')
          this.handleCancel()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;

  .row {
    position: relative;
    padding-bottom: 20px;
    margin-top: 20px;
    border-bottom: 1px solid #e6e6e6;

    &-mark {
      width: 4px;
      height: 14px;
      background-color: $--color-primary;
      border-radius: 2px;
    }

    &-label {
      flex: 1;
      padding: 0 15px;
    }

    &-btn {
      flex: 1;
      padding: 0 15px;
    }

    &-children {
      padding-left: 3px;
      margin-top: 8px;
      font-size: 13px;
    }

    .wk-file-select {
      max-width: 170px;
    }
  }
}
</style>
