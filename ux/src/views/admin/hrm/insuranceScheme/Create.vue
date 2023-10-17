<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: mt mt@5kcrm.com
-->
<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="saveClick">
    <create-sections title="基本信息">
      <wk-form
        ref="baseForm"
        :model="baseForm"
        :rules="baseRules"
        :field-from="baseForm"
        :field-list="baseFields"
        label-position="top"
        @change="baseFormChange"
      />
    </create-sections>
    <el-radio-group
      v-model="baseForm.schemeType"
      @change="tabChange">
      <el-radio-button
        v-for="tab in tabList"
        :key="tab.name"
        :label="tab.name">
        {{ tab.label }}
      </el-radio-button>
    </el-radio-group>

    <el-form
      v-if="otherFrom"
      ref="otherFrom"
      :model="otherFrom"
      :validate-on-rule-change="false"
      hide-required-asterisk
    >
      <create-sections title="社保">
        <div class="table-wrap">
          <el-table
            :data="otherFrom.topList"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :summary-method="getSummaries"
            show-summary
            style="width: 100%;">
            <el-table-column
              :min-width="150"
              fixed
              prop="projectName"
              label="项目名称"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.row.type == 9 || scope.row.type == 11"
                  :prop="`topList[${scope.$index}].projectName`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="scope.row.projectName"
                    placeholder="请输入"
                    type="text" />
                </el-form-item>
                {{ getProjectName(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column
              v-for="(item, index) in tableFields"
              :key="index"
              :prop="item.prop"
              :label="item.label"
              :min-width="150"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <el-form-item
                  :prop="`topList[${scope.$index}][${scope.column.property}]`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="scope.row[scope.column.property]"
                    v-wk-number="getNumberType(scope.column.property)"
                    :disabled="getLeftInputDisabledValue(scope.column.property) "
                    placeholder="请输入"
                    type="text"
                    @input="numberInputChange(scope.row)">
                    <template
                      v-if="getLeftInputAppendShowValue(scope.column.property)"
                      slot="append">%</template>
                  </el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              fixed="right"
              width="100">
              <template slot-scope="scope">
                <el-button
                  type="primary-text"
                  style="padding: 0;"
                  @click="deleteItems(scope.$index, otherFrom.topList)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-dropdown
            ref="topDropdown"
            :hide-on-click="false"
            trigger="click"
            @command="dropdownCommand($event, otherFrom.topList, 'top')">
            <el-button type="text">添加项目</el-button>
            <el-dropdown-menu
              slot="dropdown"
            >
              <el-dropdown-item
                v-for="(item, index) in getHandleOptions('top', otherFrom.topList)"
                :key="index"
                :command="item.value"
                @click.stop="">
                <el-checkbox
                  v-model="item.checked"
                  @change="checkboxChange($event,item.value,otherFrom.topList)">{{ item.label }}</el-checkbox>
              </el-dropdown-item>
              <el-dropdown-item
                command="other"
                icon="el-icon-plus"
                divided>其他</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </create-sections>

      <create-sections title="公积金">
        <div class="table-wrap">
          <el-table
            :data="otherFrom.bottomList"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :summary-method="getSummaries"
            show-summary
            style="width: 100%;">
            <el-table-column
              :min-width="150"
              fixed
              prop="projectName"
              label="项目名称"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.row.type == 9 || scope.row.type == 11"
                  :prop="`bottomList[${scope.$index}].projectName`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="scope.row.projectName"
                    placeholder="请输入"
                    type="text" />
                </el-form-item>
                {{ getProjectName(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column
              v-for="(item, index) in tableFields"
              :key="index"
              :prop="item.prop"
              :label="item.label"
              :min-width="150"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <el-form-item
                  :prop="`bottomList[${scope.$index}][${scope.column.property}]`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="scope.row[scope.column.property]"
                    v-wk-number="getNumberType(scope.column.property)"
                    :disabled="getLeftInputDisabledValue(scope.column.property) "
                    placeholder="请输入"
                    type="text"
                    @input="numberInputChange(scope.row)">
                    <template
                      v-if="getLeftInputAppendShowValue(scope.column.property)"
                      slot="append">%</template>
                  </el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              fixed="right"
              width="100">
              <template slot-scope="scope">
                <el-button
                  type="primary-text"
                  style="padding: 0;"
                  @click="deleteItems(scope.$index, otherFrom.bottomList)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-dropdown
            ref="bottomDropdown"
            :hide-on-click="false"
            trigger="click"
            @command="dropdownCommand($event, otherFrom.bottomList, 'bottom')">
            <el-button type="text">添加项目</el-button>
            <el-dropdown-menu
              slot="dropdown"
            >
              <el-dropdown-item
                v-for="(item, index) in getHandleOptions('bottom', otherFrom.bottomList)"
                :key="index"
                :command="item.value"
                @click.stop="">
                <el-checkbox
                  v-model="item.checked"
                  @change="checkboxChange($event,item.value,otherFrom.bottomList)">{{ item.label }}</el-checkbox>
              </el-dropdown-item>
              <el-dropdown-item
                command="other"
                icon="el-icon-plus"
                divided>其他</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </create-sections>

    </el-form>
  </xr-create>
</template>

<script>
import {
  hrmConfigAddInsuranceSchemAPI,
  hrmConfigInsuranceSchemeDetailAPI
} from '@/api/admin/hrm'
import {
  hrmInsuranceSchemeScaleAPI,
  hrmInsuranceSchemeTypeAPI
} from '@/api/hrm/insuranceScheme'

import XrCreate from '@/components/XrCreate'
import WkForm from '@/components/NewCom/WkForm'
import CreateSections from '@/components/CreateSections'

import insuranceSchemeModel from './model/insuranceScheme'
import { objDeepCopy, floatAdd } from '@/utils'

const SYS_SCHEME_TYPE = {
  '养老保险': 1,
  '医疗保险': 2,
  '工伤保险': 3,
  '失业保险': 4,
  '生育保险': 5,
  '大病/补充/大额医疗': 6,
  '残保金': 8,
  '住房公积金': 10
}

export default {
  // 社保方案创建
  name: 'InsuranceSchemeCreate',
  components: {
    XrCreate,
    CreateSections,
    WkForm
  },
  filters: {},
  props: {
    id: [String, Number]
  },
  data() {
    var validateCity = (rule, value, callback) => {
      if (!value || !value.city) {
        callback(new Error('请选择参保城市'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      // 1已入职 2待入职
      tabList: [{
        label: '设置参保基数和比例',
        name: 1
      }, {
        label: '仅设置参保金额',
        name: 2
      }],
      // 方案类型
      typeCity: '',
      typeDetail: null,
      typeList: [],
      baseRules: {
        city: [
          { required: true, message: '请选择', trigger: ['blur', 'change'] },
          { validator: validateCity, trigger: ['blur', 'change'] }
        ],
        schemeName: [
          { required: true, message: '请输入', trigger: ['blur', 'change'] }
        ]
      },
      baseForm: {
        schemeType: 1,
        houseType: '',
        city: {
          province: '',
          city: '',
          area: ''
        }
      },

      // 其他
      otherFrom: null,

      handleOptions: []
    }
  },
  computed: {
    title() {
      return this.id ? '编辑参保方案' : '新建参保方案'
    },

    baseFields() {
      return [
        {
          name: '方案名称',
          field: 'schemeName',
          formType: 'text',
          setting: []
        },
        {
          name: '参保城市',
          field: 'city',
          formType: 'address',
          setting: []
        },
        {
          name: '可选参保方案',
          field: 'houseType',
          formType: 'select',
          setting: this.typeList
        }]
    },

    tableFields() {
      if (this.baseForm.schemeType == 1) {
        return [{
          label: '默认基数',
          prop: 'defaultAmount'
        }, {
          label: '公司缴纳比例',
          prop: 'corporateProportion'
        }, {
          label: '个人缴纳比例',
          prop: 'personalProportion'
        }, {
          label: '公司金额',
          prop: 'corporateAmount'
        }, {
          label: '个人金额',
          prop: 'personalAmount'
        }]
      }

      return [{
        label: '默认基数',
        prop: 'defaultAmount'
      }, {
        label: '公司金额',
        prop: 'corporateAmount'
      }, {
        label: '个人金额',
        prop: 'personalAmount'
      }]
    }
  },
  watch: {},
  created() {
    this.requestData()
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 动画结束的方法里处理初始化逻辑
     */
    requestData() {
      if (this.id) {
        this.getDetial()
      } else {
        this.loading = true
        // 渲染慢 加loading
        setTimeout(() => {
          this.otherFrom = {
            topList: this.getDefaultItems([1, 2, 3, 4, 5]),
            bottomList: this.getDefaultItems([10]),
            leftList: [],
            leftFundList: [],
            rightList: [],
            rightFundList: []
          }

          this.$nextTick(() => {
            this.loading = false
          })
        }, 300)
      }
    },

    /**
     * 编辑获取详情
     */
    getDetial() {
      this.loading = true
      hrmConfigInsuranceSchemeDetailAPI(this.id)
        .then(res => {
          const data = res.data || {}
          const city = {
            province: '',
            city: '',
            area: ''
          }
          if (data.city) {
            const citys = data.city.split(',')
            city.province = citys[0] || ''
            city.city = citys[1] || ''
            city.area = citys[2] || ''

            if (city.city) {
              const cityName = this.getCityName(city.city)
              this.typeCity = cityName
              this.getSchemeType(cityName)
            }
          }

          this.baseForm = {
            city,
            schemeName: data.schemeName,
            schemeType: data.schemeType,
            houseType: data.houseType
          }

          this.$nextTick(() => {
            this.baseForm.houseType = data.houseType
          })

          // const defaultTopList = this.getDefaultItems([1, 2, 3, 4, 5])
          // const defaultBottomList = this.getDefaultItems([10])

          this.otherFrom = {
            topList: data.socialSecurityProjectList,
            bottomList: data.providentFundProjectList,
            leftList: [],
            leftFundList: [],
            rightList: [],
            rightFundList: []
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    close() {
      this.$emit('close')
    },

    tabChange() {
      if (this.baseForm.schemeType == 1) {
        this.otherFrom.rightList = this.otherFrom.topList
        this.otherFrom.rightFundList = this.otherFrom.bottomList

        this.otherFrom.topList = this.otherFrom.leftList.length == 0 ? this.getDefaultItems([1, 2, 3, 4, 5]) : objDeepCopy(this.otherFrom.leftList)
        this.otherFrom.bottomList = this.otherFrom.leftFundList.length == 0 ? this.getDefaultItems([10]) : objDeepCopy(this.otherFrom.leftFundList)
      } else {
        this.otherFrom.leftList = this.otherFrom.topList
        this.otherFrom.leftFundList = this.otherFrom.bottomList

        this.otherFrom.topList = this.otherFrom.rightList.length == 0 ? this.getDefaultItems([1, 2, 3, 4, 5]) : objDeepCopy(this.otherFrom.rightList)
        this.otherFrom.bottomList = this.otherFrom.rightFundList.length == 0 ? this.getDefaultItems([10]) : objDeepCopy(this.otherFrom.rightFundList)
      }
    },

    getDefaultItems(list) {
      return list.map(type => this.getDefaultItem(type))
    },

    getDefaultItem(type) {
      const typeValue = this.typeDetail ? this.typeDetail[type] : null
      return {
        type: type || 1,
        projectName: '',
        corporateAmount: typeValue ? typeValue.corporateAmount : 0,
        corporateProportion: typeValue ? typeValue.corporateProportion : 0,
        defaultAmount: typeValue ? typeValue.defaultAmount : 0,
        personalAmount: typeValue ? typeValue.personalAmount : 0,
        personalProportion: typeValue ? typeValue.personalProportion : 0
      }
    },

    getProjectName(type) {
      if (type < 9) {
        return insuranceSchemeModel.providentFundTypeValue[type]
      }

      return insuranceSchemeModel.socialSecurityTypeValue[type]
    },

    /**
     * 左边表格是否不能输入
     */
    getLeftInputDisabledValue(prop) {
      return this.baseForm.schemeType == 2 ? false : ['corporateAmount', 'personalAmount'].includes(prop)
    },

    /**
     * 左边表格是否展示百分号
     */
    getLeftInputAppendShowValue(prop) {
      return ['corporateProportion', 'personalProportion'].includes(prop)
    },

    getNumberType(prop) {
      if (['corporateAmount', 'personalAmount', 'defaultAmount'].includes(prop)) {
        return 'positive3Float'
      } else if (['corporateProportion', 'personalProportion'].includes(prop)) {
        return 'positive4Float'
      }

      return ''
    },

    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '总计'
          return
        } else if (['personalProportion', 'corporateProportion', 'defaultAmount'].includes(column.property) || !column.property) {
          sums[index] = ''
          return
        }
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return floatAdd(prev, curr)
            } else {
              return prev
            }
          }, 0)
        } else {
          sums[index] = 'N/A'
        }
      })

      return sums
    },

    numberInputChange(data) {
      if (this.baseForm.schemeType == 1) {
        const personalProportion = parseFloat(data.personalProportion || 0)
        const personalAmount = parseFloat(data.defaultAmount || 0) * (personalProportion > 0 ? personalProportion / 100 : 0)
        data.personalAmount = Math.round(personalAmount * 100) / 100
        const corporateProportion = parseFloat(data.corporateProportion || 0)
        const corporateAmount = parseFloat(data.defaultAmount || 0) * (corporateProportion > 0 ? corporateProportion / 100 : 0)
        data.corporateAmount = Math.round(corporateAmount * 100) / 100
      }
    },

    /**
     * 获取操作项
     */
    getHandleOptions(type, list) {
      const options = type == 'top' ? insuranceSchemeModel.getValueList(insuranceSchemeModel.providentFundTypeValue) : insuranceSchemeModel.getValueList(insuranceSchemeModel.socialSecurityTypeValue)
      const hasTypes = list.map(item => item.type)
      return options.map(item => {
        item.checked = hasTypes.includes(item.value)
        return item
      })
    },

    /**
     * 勾线添加删除操作
     */
    checkboxChange(check, type, list) {
      if (check) {
        list.push(this.getDefaultItem(type))
      } else {
        let rIndex = -1
        for (let index = 0; index < list.length; index++) {
          if (list[index].type == type) {
            rIndex = index
            break
          }
        }

        if (rIndex >= 0) {
          list.splice(rIndex, 1)
        }
      }
    },

    /**
     * 添加其他
     */
    dropdownCommand(command, list, type) {
      if (command == 'other') {
        this.$refs[`${type}Dropdown`].hide()
        list.push(this.getDefaultItem(type == 'top' ? 9 : 11))
      }
    },

    /**
     * 删除
     */
    deleteItems(index, list) {
      list.splice(index, 1)
    },

    saveClick() {
      this.$refs.baseForm.$children[0].validate((valid) => {
        if (valid) {
          this.$refs.otherFrom.validate((valid) => {
            if (valid) {
              if (this.otherFrom.topList.length == 0) {
                this.$message.error('请添加社保项目')
                return
              }

              const params = {
                schemeType: this.baseForm.schemeType,
                schemeName: this.baseForm.schemeName,
                houseType: this.baseForm.houseType
              }

              if (this.id) {
                params.schemeId = this.id
              }

              if (this.baseForm.city) {
                const keys = Object.keys(this.baseForm.city)
                let address = ''
                keys.forEach(key => {
                  if (this.baseForm.city[key]) {
                    if (!address) {
                      address = this.baseForm.city[key]
                    } else {
                      address += `,${this.baseForm.city[key]}`
                    }
                  }
                })
                params.city = address
              } else {
                params.city = ''
              }

              // if (!params.city || ) {
              //   this.$message.error('选择参保城市')
              //   return
              // }

              params.socialSecurityProjectList = this.otherFrom.topList
              params.providentFundProjectList = this.otherFrom.bottomList

              this.getConfirmResult().then(() => {
                this.loading = true
                hrmConfigAddInsuranceSchemAPI(params).then(res => {
                  this.$emit('close')
                  this.$message.success(`${this.title}成功`)
                  this.$emit('success')
                  this.loading = false
                }).catch(() => {
                  this.loading = false
                })
              }).catch(() => {})
            } else {
              return false
            }
          })
        } else {
          return false
        }
      })
    },

    /**
     * 弹窗确定
     */
    getConfirmResult() {
      return new Promise((resolve, reject) => { // eslint-disable-line
        if (this.id) {
          this.$confirm('编辑参保方案后，不会变更现有的参保信息，确定提交？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              resolve()
            })
            .catch(() => {})
        } else {
          resolve()
        }
      })
    },

    /**
     * 社保基数
     */
    baseFormChange(item, index, value) {
      if (item.formType === 'address' && this.baseForm.city && this.baseForm.city.city) {
        const city = this.getCityName(this.baseForm.city.city)
        if (this.typeCity != city) {
          this.typeCity = city
          this.baseForm.houseType = ''
          this.typeList = []

          this.getSchemeType(city)
        }
      } else if (item.field === 'houseType') {
        if (value) {
          this.getSchemeTypeDetail(value)
        }
      }
    },

    /**
     * 获取社保详情
     */
    getSchemeTypeDetail(value) {
      hrmInsuranceSchemeScaleAPI({
        cityId: this.typeCity,
        hujiId: value
      }).then(res => {
        const typeDetail = {}

        var el = document.createElement('html')
        el.innerHTML = res.data
        const trDoms = el.getElementsByTagName('tr')
        if (trDoms) {
          const trList = Array.prototype.slice.call(trDoms, 0)

          const keys = {
            1: 'defaultAmount',
            3: 'corporateProportion',
            4: 'personalProportion',
            5: 'corporateAmount',
            6: 'personalAmount'
          }
          trList.forEach(trItem => {
            if (trItem.children && trItem.children.length) {
              const tdDom = trItem.children[0]
              if (tdDom.nodeName == 'TD') {
                const trChildren = Array.prototype.slice.call(trItem.children, 0)
                // 如果是系统更包含类型 读取值
                const firstTd = trChildren[0]
                const sysType = SYS_SCHEME_TYPE[firstTd.innerText.trim()]
                if (sysType) {
                  const typeItems = {}
                  for (let index = 0; index < trChildren.length; index++) {
                    const td = trChildren[index]
                    if (keys[index]) {
                      typeItems[keys[index]] = td.innerText.replace('%', '').trim()
                    }
                  }
                  typeDetail[sysType] = typeItems
                }
              }
            }
          })

          this.typeDetail = typeDetail
        } else {
          this.typeDetail = null
        }

        this.handleOtherFrom(this.typeDetail)
      }).catch(() => {})
    },

    handleOtherFrom(detail) {
      if (detail) {
        this.otherFrom.topList.forEach(item => {
          const typeValue = detail[item.type]
          if (typeValue) {
            item.corporateAmount = typeValue.corporateAmount
            item.corporateProportion = typeValue.corporateProportion
            item.defaultAmount = typeValue.defaultAmount
            item.personalAmount = typeValue.personalAmount
            item.personalProportion = typeValue.personalProportion
          } else {
            this.handleItemDefault(item)
          }
        })

        this.otherFrom.bottomList.forEach(item => {
          const typeValue = detail[item.type]
          if (typeValue) {
            item.corporateAmount = typeValue.corporateAmount
            item.corporateProportion = typeValue.corporateProportion
            item.defaultAmount = typeValue.defaultAmount
            item.personalAmount = typeValue.personalAmount
            item.personalProportion = typeValue.personalProportion
          } else {
            this.handleItemDefault(item)
          }
        })
      } else {
        this.otherFrom.topList.forEach(item => {
          this.handleItemDefault(item)
        })

        this.otherFrom.bottomList.forEach(item => {
          this.handleItemDefault(item)
        })
      }

      this.otherFrom.leftList = []
      this.otherFrom.leftFundList = []
      this.otherFrom.rightList = []
      this.otherFrom.rightFundList = []
    },

    handleItemDefault(item) {
      item.corporateAmount = 0
      item.corporateProportion = 0
      item.defaultAmount = 0
      item.personalAmount = 0
      item.personalProportion = 0
    },

    getCityName(city) {
      const cityAreaIndex = city.indexOf('城区')
      if (cityAreaIndex != -1 && city.length - 2 == cityAreaIndex) {
        city = city.substr(0, city.length - 2)
      }

      const cityIndex = city.indexOf('市')
      if (cityIndex != -1 && city.length - 1 == cityIndex) {
        city = city.substr(0, city.length - 1)
      }

      return city
    },

    /**
     * 获取参保方案
     */
    getSchemeType(city) {
      hrmInsuranceSchemeTypeAPI({
        cityId: city
      }).then(res => {
        const data = res.data || []
        this.typeList = data.map(item => {
          return {
            label: item.InsuredType,
            value: item.AccountNo
          }
        })
      }).catch(() => {})
    }

  }
}
</script>

<style lang="scss" scoped>
.el-radio-group {
  margin: 8px;

  ::v-deep .el-radio-button {
    .el-radio-button__inner {
      font-weight: inherit;
    }
  }
}

::v-deep .el-table {
  .el-form-item__content {
    line-height: $--input-height;
  }
}

.table-wrap {
  padding: 10px 20px;
}
</style>
