<template>
  <div v-loading="loading" class="b-cont">
    <create-sections
      :dropdown-items="cardDropdownItems"
      title="工资卡信息"
      @command-select="cardCommandSelect">
      <wk-base-detail-section
        :list="cardList"
      />
    </create-sections>
    <create-sections
      :dropdown-items="securityDropdownItems"
      title="社保信息"
      @command-select="securityCommandSelect">
      <wk-base-detail-section
        :list="securityList"
      />
      <!-- <el-button
        plain
        @click="securityCreateClick">+ 添加社保</el-button> -->
    </create-sections>
    <create-sections
      class="b-cells"
      title="薪资信息">
      <div class="money-table">
        <el-table
          :data="moneyList"
          :cell-class-name="cellClassName"
          height="300"
          border
          align="center"
          @row-click="handleRowClick">
          <el-table-column
            v-for="(item, index) in fieldList"
            :key="index"
            :prop="item.prop"
            :width="item.width"
            :label="item.label" />
        </el-table>
        <div class="p-contianer">
          <el-pagination
            :current-page="currentPage"
            :page-sizes="pageSizes"
            :page-size.sync="pageSize"
            :total="total"
            class="p-bar"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </div>
    </create-sections>
    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      @pass="uploadEmployeeRelativeData"
    />

    <money-detail-dialog
      :detail="moneyData"
      :visible.sync="detailDialogShow"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeSocialSecurityQueryByIdAPI,
  hrmEmployeeSocialSecurityAddSalaryCardAPI,
  hrmEmployeeSocialSecurityAddSocialAPI,
  hrmEmployeeSocialSecuritySetSalaryCardAPI,
  hrmEmployeeSocialSecuritySetSocialAPI,
  hrmEmployeeSocialSecuritySalaryListAPI
} from '@/api/hrm/employeeSocialSecurity'
import {
  hrmConfigInsuranceSchemListAPI
} from '@/api/admin/hrm'

import CreateSections from '@/components/CreateSections'
import FormAddDialog from './FormAddDialog'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'

import employeeCard from '../model/employeeCard'
import MoneyDetailDialog from './MoneyDetailDialog'
import employeeSecurity from '../model/employeeSecurity'

export default {
  // 工资社保
  name: 'EmployeeMoney',
  components: {
    CreateSections,
    FormAddDialog,
    WkBaseDetailSection,
    MoneyDetailDialog
  },

  inject: ['editAuth', 'employeeAuth'],
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      list: [],
      // 弹窗添加
      formAddType: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 工资卡信息
      cardData: null,
      cardList: [],
      // 社保信息
      securityData: null,
      securityList: [],
      schemeList: [],
      // 薪资信息
      moneyList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      fieldList: [
        {
          prop: 'yearMonth',
          label: '计薪月份',
          width: '150'
        },
        {
          prop: 'time',
          label: '计薪周期',
          width: '220'
        },
        {
          prop: 'shouldSalary',
          label: '应发工资'
        },
        {
          prop: 'personalTax',
          label: '个人所得税'
        },
        {
          prop: 'realSalary',
          label: '实发工资'
        }
      ],
      moneyData: null,
      detailDialogShow: false
    }
  },

  computed: {
    cardDropdownItems() {
      const temps = []
      if (this.editAuth) {
        temps.push({
          label: '编辑',
          command: 'edit',
          icon: ''
        })
      }
      return temps
    },

    securityDropdownItems() {
      const temps = []
      if (this.editAuth) {
        temps.push({
          label: '编辑',
          command: 'edit',
          icon: ''
        })
      }
      return temps
    },

    // 新建编辑标题
    formAddTitle() {
      return {
        'create-wage-card': '添加工资卡',
        'update-wage-card': '编辑工资卡',
        'create-social-security': '添加社保',
        'update-social-security': '编辑社保'
      }[this.formAddType]
    }
  },
  watch: {
    id: {
      handler() {
        this.getDetail()
        this.refreshList()
        // this.getMoneyDetail()
      },
      immediate: true
    }
  },
  mounted() {},
  activated: function() {
    console.log(3)
  },
  deactivated: function() {
    console.log(4)
  },
  methods: {
    // 获取基础信息
    getDetail() {
      this.loading = true
      hrmEmployeeSocialSecurityQueryByIdAPI(this.id)
        .then(res => {
          const data = res.data || {}
          this.cardData = data.salaryCard
          const cardList = []
          employeeCard.fields.forEach(item => {
            cardList.push({
              label: item.name,
              value: this.cardData ? this.cardData[item.field] : ''
            })
          })
          this.cardList = cardList

          this.securityData = data.socialSecurityInfo
          const securityList = []
          const securityField = employeeSecurity.getFields(null, this.employeeAuth)
          securityField.forEach(item => {
            let value = this.securityData ? this.securityData[item.field] : ''
            if (item.field === 'isFirstSocialSecurity' || item.field === 'isFirstAccumulationFund') {
              value = {
                0: '否',
                1: '是'
              }[value]
            } else if (item.field === 'schemeId') {
              value = this.securityData ? this.securityData.schemeName : ''
            }
            securityList.push({
              label: item.name,
              value: value
            })
          })
          this.securityList = securityList
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 工资卡操作
     */
    cardCommandSelect(command) {
      if (command == 'edit') {
        this.formAddType = 'update-wage-card'
        this.formAddForm = this.cardData ? {
          salaryCardNum: this.cardData.salaryCardNum || '',
          accountOpeningCity: this.cardData.accountOpeningCity || '',
          bankName: this.cardData.bankName || '',
          openingBank: this.cardData.openingBank || '',
          salaryCardId: this.cardData.salaryCardId || ''
        } : {}
        this.formAddRules = employeeCard.rules
        this.formAddFields = employeeCard.fields
        this.formAddDialogShow = true
      }
    },

    // cardCreateClick() {
    //   this.formAddType = 'create-wage-card'
    //   this.formAddForm = {}
    //   this.formAddRules = employeeCard.rules
    //   this.formAddFields = employeeCard.fields
    //   this.formAddDialogShow = true
    // },

    /**
     * 社保编辑
     */
    securityCommandSelect(command) {
      if (command == 'edit') {
        this.formAddType = 'update-social-security'
        this.formAddForm = this.securityData ? {
          isFirstSocialSecurity: this.securityData.isFirstSocialSecurity,
          isFirstAccumulationFund: this.securityData.isFirstAccumulationFund,
          socialSecurityNum: this.securityData.socialSecurityNum,
          accumulationFundNum: this.securityData.accumulationFundNum,
          socialSecurityStartMonth: this.securityData.socialSecurityStartMonth,
          socialSecurityInfoId: this.securityData.socialSecurityInfoId,
          schemeId: this.securityData.schemeId
        } : {
          isFirstSocialSecurity: 0,
          isFirstAccumulationFund: 0
        }
        this.formAddRules = employeeSecurity.rules
        this.getSchemList().then(() => {
          this.formAddFields = employeeSecurity.getFields(this.schemeList, this.employeeAuth)
          this.formAddDialogShow = true
        })
      }
    },

    /**
     * 获取参保方案
     */
    getSchemList() {
      return new Promise((resolve, reject) => { // eslint-disable-line
        if (this.schemeList.length) {
          resolve()
        } else {
          hrmConfigInsuranceSchemListAPI({
            pageType: 0
          })
            .then(res => {
              this.schemeList = res.data.list.map(item => {
                return {
                  label: item.schemeName,
                  value: item.schemeId
                }
              })
              resolve()
            })
            .catch(() => {
              reject && reject()
            })
        }
      })
    },

    // securityCreateClick() {
    //   this.formAddType = 'create-social-security'
    //   this.formAddForm = {
    //     isFirstSocialSecurity: 0,
    //     isFirstAccumulationFund: 0
    //   }
    //   this.formAddRules = employeeSecurity.rules
    //   this.formAddFields = employeeSecurity.fields
    //   this.formAddDialogShow = true
    // },

    /**
     * 提交数据
     */
    uploadEmployeeRelativeData() {
      this.$refs.formAdddialog.loading = true
      this.formAddForm.employeeId = this.id
      let request = null
      if (this.formAddType === 'create-wage-card') {
        request = hrmEmployeeSocialSecurityAddSalaryCardAPI
      } else if (this.formAddType === 'update-wage-card') {
        request = hrmEmployeeSocialSecuritySetSalaryCardAPI
      } else if (this.formAddType === 'create-social-security') {
        request = hrmEmployeeSocialSecurityAddSocialAPI
      } else if (this.formAddType === 'update-social-security') {
        request = hrmEmployeeSocialSecuritySetSocialAPI
      }
      request(this.formAddForm).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getDetail()
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0) {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'yearMonth') {
        this.moneyData = row
        this.detailDialogShow = true
      }
    },

    /**
     * 刷新
     */
    refreshList() {
      this.handleCurrentChange(1)
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 列表数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        employeeId: this.id
      }

      hrmEmployeeSocialSecuritySalaryListAPI(params)
        .then(res => {
          const list = res.data.list
          list.forEach(item => {
            item.time = `${item.startTime} ~ ${item.endTime}`
            item.yearMonth = `${item.year}-${item.month}`
          })
          this.moneyList = list
          this.total = res.data.totalRow
          if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
            this.currentPage = this.currentPage - 1
            this.getList()
          } else {
            this.total = res.data.totalRow
            this.loading = false
          }
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
.b-cont {
  position: relative;
}

.b-cells {
  margin-top: 25px;
}

.b-cell {
  padding: 0 10px;

  .b-cell-b {
    width: auto;
    padding: 8px;

    .b-cell-name {
      flex-shrink: 0;
      width: 100px;
      margin-right: 10px;
      font-size: 12px;
      color: #777;
    }

    .b-cell-value {
      font-size: 12px;
      color: $--color-text-primary;
    }

    .b-cell-foot {
      display: block;
      flex-shrink: 0;
      width: 15px;
      height: 15px;
      margin-left: 8px;
    }
  }
}

.money-table {
  position: relative;
  padding: 20px;
}

.edit-button {
  position: absolute;
  top: 0;
  right: 30px;
}
</style>
