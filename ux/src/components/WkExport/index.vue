<template>
  <div
    v-loading="loading"
    class="main">
    <el-dialog
      :visible="showCRMExport"
      :title="'导出'+exportTypeName"
      :append-to-body="true"
      :close-on-click-modal="false"
      width="750px"
      @close="closeView">
      <div class="dialog-body">
        <el-steps :active="stepsActive" align-center>
          <el-step title="导出配置" />
          <el-step title="导出完成" />
        </el-steps>
        <div
          v-if="stepsActive==1 && !exportLoading"
          class="step-section">
          <div v-if="config.rangeSelectShow" class="type-section">
            <div class="type-section__header">选择字段导出范围</div>
            <el-select
              v-model="rangeType"
              class="type-section__body"
              style="width: 100%;"
              @change="getFieldList">
              <el-option
                v-for="item in rangeTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value" />
            </el-select>
          </div>
          <flexbox
            direction="column"
            align="stretch"
            class="field">
            <div class="title">选择字段导出范围：</div>
            <div class="field-list">
              <el-checkbox
                v-model="checkAll"
                :indeterminate="isIndeterminate"
                @change="handleCheckAllChange">全选</el-checkbox>
              <el-checkbox-group
                v-model="selectionList"
                @change="handleChange">
                <el-checkbox
                  v-for="(item,index) in fieldList"
                  :key="index"
                  :label="item.sortId">{{ item.label }}</el-checkbox>
              </el-checkbox-group>
            </div>
            <div class="select-num">已选择{{ selectionList.length }}项</div>
          </flexbox>
          <div v-if="exportType != 'examine'" class="type-section">
            <div class="type-section__header">选择导出文件类型</div>
            <el-radio-group v-model="fileType">
              <el-radio :label="1">xls</el-radio>
              <el-radio :label="2">csv</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div
          v-if="exportLoading"
          v-loading="exportLoading"
          element-loading-text="正在导出中"
          element-loading-spinner="el-icon-loading"
          class="step-section" />

        <div
          v-if="stepsActive==2"
          class="step-section is-success">
          <div class="success">
            <img src="@/assets/img/success.png">
            <div>导出完成</div>
          </div>
        </div>

      </div>
      <div
        v-if="!exportLoading"
        slot="footer"
        class="dialog-footer">
        <el-button
          v-if="stepsActive==1"
          type="primary"
          @click="sureClick">立即导出</el-button>
        <el-button
          v-else
          type="primary"
          @click="closeView">确认</el-button>
        <el-button
          @click="closeView">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { downloadExcelWithResData } from '@/utils'
import merge from '@/utils/merge'

export default {
  name: 'WkExport',
  data() {
    return {
      loading: false,
      showCRMExport: false,
      config: {},
      exportType: '',
      rangeType: 'table', // table 列表字段 all 全部字段
      rangeTypeOptions: [{
        value: 'all',
        label: '全部字段'
      }, {
        value: 'table',
        label: '列表字段'
      }],
      fieldList: [], // 字段数据
      selectionList: [], // 勾选字段数据
      fileType: 1, // 导出文件类型
      stepsActive: 1, // 步骤

      checkAll: false, // 选择所有状态
      isIndeterminate: false,

      exportLoading: false // 正在导出中
    }
  },
  computed: {
    exportTypeName() {
      if (this.config && this.config.typeName) {
        return this.config.typeName
      }
      return (
        {
          customer: '客户',
          leads: '线索',
          contacts: '联系人',
          applet: '名片',
          business: '商机',
          contract: '合同',
          receivables: '回款',
          product: '产品',
          invoice: '发票',
          receivablesPlan: '回款计划',
          examine: '审批'
        }[this.exportType] || ''
      )
    }
  },
  methods: {
    /**
     * 展示导出弹窗
     */
    export(exportType, props) {
      this.exportType = exportType
      /**
       * typeName 标题
       * params 确定传参
       * request 确定请求
       * fieldRequest 字段请求
       * fieldParams 字段参数
       * allFieldRequest 全部字段请求
       * allFieldParams 全部字段参数
       * idKey 默认id
       * selectsKey sortIds  用于提交最终选择 idKey 值的key
       * rangeSelectShow: false  范围选择展示 默认false
       */
      this.config = merge({ idKey: 'id', selectsKey: 'sortIds', rangeSelectShow: false }, props || {})
      this.showCRMExport = true
      this.getFieldList()
    },
    /**
     * 确认导出
     */
    sureClick() {
      if (this.selectionList.length == 0) {
        this.$message.error('请选择字段导出范围')
        return
      }
      this.exportLoading = true
      const params = {
        isXls: this.fileType,
        ... this.config.params
      }

      if (this.exportType === 'examine') {
        const data = []
        this.fieldList.forEach(item => {
          this.selectionList.forEach((sItem, sIndex) => {
            if (item.sortId == sItem) {
              data.push({
                name: item.label,
                fieldKey: item.sortId,
                sort: sIndex
              })
            }
          })
        })

        params['fieldList'] = data
      } else {
        params[this.config.selectsKey] = this.selectionList
      }

      this.config.request(params)
        .then(res => {
          downloadExcelWithResData(res)
          this.exportLoading = false
          this.stepsActive = 2
        })
        .catch(() => {
          this.exportLoading = false
        })
    },
    /**
     * 获取导出字段
     */
    getFieldList() {
      this.loading = true
      let params = {}
      let request = null

      if (this.rangeType === 'table') {
        if (this.config.fieldParams) {
          params = this.config.fieldParams
        }
        request = this.config.fieldRequest
      } else {
        if (this.config.allFieldParams) {
          params = this.config.allFieldParams
        }
        request = this.config.allFieldRequest
      }

      request(params)
        .then(res => {
          res.data = res.data.filter(o => {
            if (!o.hasOwnProperty('formType')) return true
            return o.formType &&
              ![
                'handwriting_sign',
                'desc_text'
              ].includes(o.formType)
          })
          const fieldList = []
          const selectionList = []

          for (let index = 0; index < res.data.length; index++) {
            const element = res.data[index]
            var width = 0
            if (!element.width) {
              if (element.name && element.name.length <= 6) {
                width = element.name.length * 15 + 45
              } else {
                width = 140
              }
            } else {
              width = element.width
            }
            fieldList.push({
              prop: element.fieldName,
              formType: element.formType,
              label: element.name,
              width: width,
              sortId: element[this.config.idKey]
            })

            selectionList.push(element[this.config.idKey])
          }
          // 审批导出特殊处理
          if (this.config.hasOwnProperty('fieldList')) {
            fieldList.push(...this.config.fieldList)
            selectionList.push(...this.config.selectionList)
          }

          this.fieldList = fieldList
          this.selectionList = selectionList
          this.checkAll = true
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    /**
     * 选中数据
     */
    handleSelectionChange(val) {
      this.selectionList = val
    },
    // 关闭操作
    closeView() {
      if (this.exportLoading) { // 正在导出 禁止关闭
        return
      }
      this.showCRMExport = false
      this.stepsActive = 1
      this.selectionList = []
    },
    /**
     * 选中所有
     */
    handleCheckAllChange(val) {
      this.selectionList = val ? this.fieldList.map(item => item.sortId) : []
      this.isIndeterminate = false
    },
    /**
     * 选择
     */
    handleChange(value) {
      const checkedCount = value.length
      this.checkAll = checkedCount === this.fieldList.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.fieldList.length
    }
  }
}
</script>

<style lang="scss" scoped>
.el-steps {
  margin-bottom: 16px;
}

.type-section + .field {
  margin-top: 16px;
}

.field + .type-section {
  margin-top: 16px;
}

.type-section {
  font-size: 14px;

  &__header {
    margin-bottom: 8px;
    font-weight: bold;
  }
}

.step-section {
  min-height: 300px;

  .field {
    height: 100%;
    padding: 12px;
    background-color: $--color-n10;
    border-radius: $--border-radius-base;

    .field-list {
      flex: 1;
      margin-top: 8px;
      overflow-y: auto;
    }

    .select-num {
      margin-top: 8px;
      color: $--color-text-secondary;
    }
  }

  &.is-success {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .success {
    font-weight: 600;
    color: $--color-text-primary;

    img {
      width: 50px;
      height: 50px;
      margin-bottom: 8px;
    }
  }
}

.dialog-footer {
  text-align: right;
}

::v-deep .el-checkbox {
  margin-right: 20px;
  margin-bottom: 8px;
}

::v-deep .el-loading-spinner {
  top: 45%;

  .el-icon-loading {
    font-size: 40px;
    color: $--color-primary;
  }

  .el-loading-text {
    margin: 8px 0;
    color: $--color-primary;
  }
}
</style>
