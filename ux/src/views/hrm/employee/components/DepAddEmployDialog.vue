<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    fullscreen
    @close="close">
    <div class="dep-add">
      <div class="dep-add__header">
        <el-button type="primary" @click="selectClick">从系统管理选择员工</el-button>
      </div>
      <el-form
        ref="tableFrom"
        :model="tableForm"
        :validate-on-rule-change="false"
        :show-message="false"
        hide-required-asterisk
      >
        <el-table
          :data="tableList"
          :height="tableHeight"
          stripe
          border>
          <el-table-column
            v-for="(item, index) in fieldList"
            :key="index"
            :prop="item.field"
            :label="item.name"
            :min-width="item.disabled ? 120 : 170"
            show-overflow-tooltip>
            <template slot-scope="{ row , $index}">
              <template v-if="item.disabled">
                {{ fieldFormatter(row, item) }}
              </template>
              <el-form-item
                v-else-if="item.formType == 'text'"
                :prop="`table[${$index}].${item.field}`"
                :rules="{ required: true, message: '请输入', trigger: 'change' }"
                label="">
                <el-input
                  v-model="row[item.field]"
                  :maxlength="100" />
              </el-form-item>
              <wk-dep-select
                v-else-if="item.formType == 'structure'"
                v-model="row[item.field]"
                :options="depOptions"
                :props="item.props"
                radio
                style="width: 100%;"
              />
              <wk-user-select
                v-else-if="item.formType == 'user'"
                v-model="row[item.field]"
                :props="item.props"
                radio
                style="width: 100%;"
              />
              <el-form-item
                v-else-if="item.formType == 'date'"
                :prop="`table[${$index}].${item.field}`"
                :rules="{ required: true, message: '请输入', trigger: 'change' }"
                label="">
                <el-date-picker
                  v-model="row[item.field]"
                  clearable
                  style="width: 100%;"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="选择日期" />
              </el-form-item>
              <el-form-item
                v-else-if="item.field == 'typeValue'"
                :prop="`table[${$index}].${item.field}`"
                :rules="{ required: true, message: '请输入', trigger: 'change' }"
                label="">
                <el-select
                  v-model="row[item.field]"
                  style="width: 100%;">
                  <el-option
                    v-for="(typeItem, typeIndex) in row.typeValueOptions"
                    :key="typeIndex"
                    :label="typeItem.label"
                    :value="typeItem.value" />
                </el-select>
              </el-form-item>
              <el-form-item
                v-else-if="item.formType == 'select'"
                :prop="`table[${$index}].${item.field}`"
                :rules="{ required: true, message: '请输入', trigger: 'change' }"
                label="">
                <el-select
                  v-model="row[item.field]"
                  style="width: 100%;"
                  @change="selectChange($event, row, item)">
                  <el-option
                    v-for="(setItem, setIndex) in item.setting"
                    :key="setIndex"
                    :label="setItem.label"
                    :value="setItem.value" />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="80">
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleteClick(scope)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-form>
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="close">取消</el-button>
    </span>

    <wk-dep-user-dialog
      :visible.sync="depUserViewDialogShow"
      :props="{
        showDisableUser: false
      }"
      @change="selectUserChange"
    />
  </el-dialog>
</template>

<script>
import {
  hrmEmployeeAdminAddAPI
} from '@/api/hrm/employee'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
import {
  adminUserQueryDeptUserByHrmAPI
} from '@/api/admin/user'

import WkUserSelect from '@/components/NewCom/WkUserSelect'
import WkDepSelect from '@/components/NewCom/WkDepSelect'
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { employeeModel } from '../model/employee'
import { objDeepCopy } from '@/utils'
// import { isEmpty } from '@/utils/types'

export default {
  // 从组织架构中选择
  name: 'DepAddEmployDialog',
  components: {
    WkUserSelect,
    WkDepSelect,
    WkDepUserDialog
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '从组织部门添加员工',
      depOptions: [],
      users: [],
      deps: [],
      tableList: [],
      tableForm: {},
      fieldList: [{
        name: '姓名',
        field: 'employeeName',
        formType: 'text',
        disabled: true,
        setting: []
      }, {
        name: '手机号码',
        field: 'mobile',
        formType: 'text',
        disabled: true,
        setting: []
      }, {
        name: '性别',
        field: 'sex',
        formType: 'radio',
        disabled: true,
        setting: employeeModel.sexSetting
      }, {
        name: '岗位',
        field: 'post',
        formType: 'text',
        disabled: true,
        setting: []
      }, {
        name: '部门',
        field: 'deptId',
        formType: 'structure',
        props: {
          dataType: 'hrm'
        },
        setting: []
      }, {
        name: '直属上级',
        field: 'parentId',
        formType: 'user',
        props: { label: 'employeeName', value: 'employeeId' },
        setting: []
      }, {
        name: '工号',
        field: 'jobNumber',
        formType: 'text',
        setting: []
      }, {
        name: '入职日期',
        field: 'entryTime',
        formType: 'date',
        setting: []
      }, {
        name: '聘用形式',
        field: 'employmentForms',
        formType: 'select',
        setting: employeeModel.employmentFormsSetting
      }, {
        name: '试用期/非正式类型',
        field: 'typeValue',
        formType: 'select'
      }],
      tableHeight: document.documentElement.clientHeight - 195,
      depUserViewDialogShow: false
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.depOptions && this.depOptions.length == 0) {
            this.getDepOptions()
          }
        }
      },
      immediate: true
    }
  },
  mounted() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 195
    }
  },
  methods: {
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 选择员工
     */
    selectClick() {
      this.depUserViewDialogShow = true
    },

    /**
     * 选择员工change
     */
    selectUserChange(usersIds, depsIds, users, deps) {
      this.users = users
      this.deps = deps
      if (this.users.length > 0 || this.deps.length > 0) {
        this.loading = true
        adminUserQueryDeptUserByHrmAPI({
          deptIdList: depsIds,
          userIdList: usersIds
        }).then(res => {
          const tableList = res.data || []
          const tempList = []
          const currentUserIds = this.tableList ? this.tableList.map(item => item.userId) : []
          tableList.forEach(item => {
            if (currentUserIds.includes(item.userId)) {
              const user = this.tableList.find(user => user.userId === item.userId)
              if (user) {
                tempList.push(user)
              }
            } else {
              const temp = {
                employeeName: item.realname,
                sex: item.sex,
                mobile: item.mobile,
                userId: item.userId,
                post: item.post,
                typeValueOptions: []
              }
              tempList.push(temp)
            }
          })
          this.tableList = tempList
          this.tableForm = {
            table: this.tableList
          }
          // this.$nextTick(() => {
          //   if (this.$refs.hrmAddForm) {
          //     this.$refs.hrmAddForm.clearValidate()
          //   }
          // })
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      } else {
        this.$message.error('请选择')
      }
    },

    /**
     * 格式化展示
     */
    fieldFormatter(data, column) {
      if (column.field == 'sex') {
        return employeeModel.sexValue[data.sex]
      }
      return data[column.field]
    },

    /**
     * 删除
     */
    deleteClick(scope) {
      this.tableList.splice(scope.$index, 1)
    },

    /**
     * 获取试用期/非正式类型筛选数据
     */
    getTypeValueOptions(data) {
      if (data.employmentForms) {
        // 1 正式 2 非正式
        if (data.employmentForms == 1) {
          return employeeModel.probationSetting
        } else if (data.employmentForms == 2) {
          return employeeModel.statusSetting
        }
        return []
      } else {
        return []
      }
    },

    /**
     * 单选change
     */
    selectChange(value, data, column) {
      if (column.field == 'employmentForms') {
        // 切换 形式 清空 类型值
        this.$set(data, 'typeValueOptions', this.getTypeValueOptions(data))
        this.$set(data, 'typeValue', '')
      }
    },

    /**
     * 获取部门请求
     */
    getDepOptions() {
      hrmDeptQueryTreeListAPI()
        .then(res => {
          this.depOptions = res.data || []
        })
        .catch(() => {
        })
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.tableFrom.validate((valid) => {
        if (valid) {
          const tableList = objDeepCopy(this.tableList)
          // let pass = true
          for (let index = 0; index < tableList.length; index++) {
            const data = tableList[index]
            delete data['typeValueOptions']
            // for (let fIndex = 0; fIndex < this.fieldList.length; fIndex++) {
            //   const column = this.fieldList[fIndex]
            //   if (!column.disabled && isEmpty(data[column.field])) {
            //     pass = false
            //     this.$message.error('请完善信息')
            //     return
            //   }
            // }
            // 1 正式 2 非正式
            if (data.employmentForms == 1) {
              data.probation = data.typeValue
            } else if (data.employmentForms == 2) {
              data.status = data.typeValue
            }
            delete data['typeValue']
          }
          this.loading = true

          hrmEmployeeAdminAddAPI(tableList)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.close()
              this.loading = false
            })
            .catch(() => {
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
<style lang="scss" scoped>
.dep-add {
  height: calc(100% - 200px);

  &__header {
    padding: 0 20px;
    margin-top: -20px;
    line-height: 50px;
    text-align: right;
  }

  .el-form-item {
    margin-bottom: 0;
  }
}
</style>
