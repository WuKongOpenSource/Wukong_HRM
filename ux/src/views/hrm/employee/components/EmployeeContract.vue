<template>
  <div v-loading="loading">
    <create-sections
      title="合同信息">
      <wk-base-detail-section
        v-for="(item, index) in contractList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, index)"
      >
        <div style="flex: 0 0 80%;">
          <wk-file-cell
            v-for="(file, fileIndex) in contractBaseList[index].fileList"
            :key="fileIndex"
            :data="file"
            :list="contractBaseList[index].fileList"
            :index="fileIndex" />
        </div>
      </wk-base-detail-section>
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="contractCreateClick">+ 添加合同</el-button>
    </create-sections>

    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      @pass="uploadEmployeeRelativeData"
      @change="formAddDialogChange"
    >
      <div class="contract-files">
        <wk-file-select
          style="display: inline-block;"
          multiple
          @change="fileSelect"
        >
          <el-button type="text">添加合同附件</el-button>
        </wk-file-select>
        <wk-file-cell
          v-for="(file, fileIndex) in fileList"
          :key="fileIndex"
          :data="file"
          :list="fileList"
          :index="fileIndex"
          delete-show
          @delete="fileDelete" />
      </div>
    </form-add-dialog>
  </div>
</template>

<script>
import {
  hrmEmployeeContractInformationAPI,
  hrmEmployeeContractaddContractAPI,
  hrmEmployeeContractSetContractAPI,
  hrmEmployeeContractDeleteAPI
} from '@/api/hrm/employeeContract'
import {
  adminFileUploadAPI,
  adminFileDeleteByIdAPI
} from '@/api/admin/file'

import CreateSections from '@/components/CreateSections'
import WkFileSelect from '@/components/NewCom/WkFile/Select'
import WkFileCell from '@/components/NewCom/WkFile/Cell'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'
import FormAddDialog from './FormAddDialog'

import employeeContract from '../model/employeeContract'
import { guid } from '@/utils'

export default {
  name: 'EmployeeContract',
  components: {
    CreateSections,
    WkBaseDetailSection,
    FormAddDialog,
    WkFileSelect,
    WkFileCell
  },

  inject: ['editAuth'],
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      contractList: [],
      contractBaseList: [],
      // 弹窗添加
      formAddType: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 附件
      batchId: '',
      fileList: []
    }
  },

  computed: {
    // 事项操作
    dropdownItems() {
      return this.editAuth ? [{
        label: '编辑',
        command: 'edit'
      }, {
        label: '删除',
        command: 'delete'
      }] : []
    },

    // 新建编辑标题
    formAddTitle() {
      return {
        'create-contract': '添加合同',
        'update-contract': '编辑合同'
      }[this.formAddType]
    }
  },
  watch: {
    id: {
      handler() {
        this.getDetail()
      },
      immediate: true
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 获取基础信息
     */
    getDetail() {
      this.loading = true
      hrmEmployeeContractInformationAPI(this.id)
        .then(res => {
          this.loading = false
          const contractList = res.data || []
          this.contractBaseList = contractList
          this.contractList = this.getCommonFieldListValue(contractList)
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     *常规字段值
     */
    getCommonFieldListValue(dataList) {
      const resultList = []
      dataList.forEach(item => {
        const list = []

        employeeContract.getFields(item).forEach(field => {
          const temp = {}
          temp.label = field.name
          if (field.field === 'contractType') {
            temp.value = employeeContract.contractTypeValue[item[field.field]]
          } else if (field.field === 'term') {
            temp.value = employeeContract.termValue[item[field.field]]
          } else if (field.field === 'status') {
            temp.value = employeeContract.statusValue[item[field.field]]
          } else {
            temp.value = item[field.field]
          }
          list.push(temp)
        })
        resultList.push(list)
      })
      return resultList
    },

    /**
     * 培训经历
     */
    contractCreateClick() {
      this.batchId = guid()
      this.fileList = []
      this.formAddType = 'create-contract'
      this.formAddForm = {
        contractType: 1,
        term: 1,
        status: 0
      }
      this.formAddRules = employeeContract.getRules(this.formAddForm)
      this.formAddFields = employeeContract.getFields(this.formAddForm)
      this.formAddDialogShow = true
    },

    /**
     * form change
     */
    formAddDialogChange(item, index, value, valueList) {
      if (item.field === 'contractType') {
        this.formAddRules = employeeContract.getRules({ contractType: value })
        this.formAddFields = employeeContract.getFields({ contractType: value })
      }
    },

    /**
     * 提交数据
     */
    uploadEmployeeRelativeData() {
      this.formAddDialogShow = true
      this.formAddForm.batchId = this.batchId
      this.formAddForm.employeeId = this.id
      let request = null
      if (this.formAddType === 'create-contract') {
        request = hrmEmployeeContractaddContractAPI
      } else if (this.formAddType === 'update-contract') {
        request = hrmEmployeeContractSetContractAPI
      }
      request(this.formAddForm).then(res => {
        this.formAddDialogShow = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getDetail()
      }).catch(() => {
        this.formAddDialogShow = false
      })
    },

    /**
     * 编辑删除操作
     */
    commandSelect(command, index) {
      const data = this.contractBaseList[index]
      if (command == 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmEmployeeContractDeleteAPI(data.contractId).then(res => {
              this.$message.success('删除成功')
              this.contractBaseList.splice(index, 1)
              this.contractList.splice(index, 1)
            })
          })
          .catch(() => {})
      } else if (command == 'edit') {
        this.formAddType = 'update-contract'
        this.formAddForm = {
          contractNum: data.contractNum,
          contractType: data.contractType,
          startTime: data.startTime,
          endTime: data.endTime,
          term: data.term,
          status: data.status,
          signCompany: data.signCompany,
          signTime: data.signTime,
          remarks: data.remarks,
          contractId: data.contractId
        }
        this.batchId = data.batchId
        this.fileList = data.fileList || []
        this.formAddRules = employeeContract.getRules(this.formAddForm)
        this.formAddFields = employeeContract.getFields(this.formAddForm)
        this.formAddDialogShow = true
      }
    },

    /**
     * 附件操作
     */
    fileSelect(files, event) {
      for (let index = 0; index < files.length; index++) {
        const file = files[index]
        this.uploadFileRequest(file)
      }
      event.target.value = ''
    },

    /**
     * 文件上传
     */
    uploadFileRequest(file) {
      this.$wkUploadFile.upload({
        request: adminFileUploadAPI,
        file: file,
        params: {
          batchId: this.batchId
        }
      }).then(({ res }) => {
        this.fileList.push(res.data)
      }).catch(() => {})
    },

    fileDelete(data, index) {
      adminFileDeleteByIdAPI(data.fileId).then(res => {
        this.fileList.splice(index, 1)
      }).catch(() => {})
    }

  }
}
</script>

<style lang="scss" scoped>
.base-add-button {
  width: 100%;
  padding: 11px 12px;
  margin: 10px 0;
}

.wk-base-detail-section + .wk-base-detail-section {
  border-top: 1px solid $--border-color-base;
}

.contract-files {
  width: 80%;
  padding: 0 5px;
}
</style>
