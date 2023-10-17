<template>
  <el-dialog
    ref="wkDialog"
    :title="stageTitle"
    :visible="visible"
    :close-on-click-modal="false"
    width="900px"
    @close="stageClose">
    <create-sections title="基础信息">
      <el-form
        ref="crmForm"
        :model="fieldsForm"
        :rules="fieldsRules"
        :validate-on-rule-change="false"
        class="wk-form"
        label-position="top">
        <wk-form-items
          v-for="(children, index) in fields"
          :key="index"
          :field-from="fieldsForm"
          :field-list="children"
          @change="formChange"
        >
          <template slot-scope="{ data }">
            <wk-user-dep-dialog-select
              v-if="data && data.formType == 'userDep'"
              :disabled="data.disabled"
              :user-value.sync="fieldsForm.userIdList"
              :dep-value.sync="fieldsForm.deptIdList"
              :sub-dept-contain.sync="fieldsForm.isNeedChild"
              :props="{
                showSubDeptContain: true,
                showDisableUser: false
              }"
              style="width: 100%;"
            />
          </template>
        </wk-form-items>
      </el-form>
    </create-sections>

    <create-sections v-if="showStage" title="阶段设置">
      <div class="stage-setting-content">
        <div class="stage-setting-table-header">
          <span class="drag-hook" />
          <span>阶段</span>
          <span>阶段名称</span>
          <span
            v-if="fieldsForm.label==5"
            class="rate">
            赢单率
          </span>
          <span>
            表单<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="24"
              data-id="223" />
          </span>
          <span>
            阶段工作<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="24"
              data-id="224" />
          </span>
          <span>
            审批设置<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="24"
              data-id="225" />
          </span>
          <span class="icon-box" />
        </div>
        <div class="stage-setting-table-body">
          <draggable
            :options="{handle: '.drag-hook'}"
            :list="settingList">
            <div
              v-for="(item, index) in settingList"
              :key="index"
              class="stage-setting-table">
              <span class="drag-hook">⋮⋮</span>
              <span>{{ '阶段' + (index+1) }}</span>
              <span>
                <el-input
                  v-model="item.name"
                  :maxlength="15"
                />
              </span>
              <span
                v-if="fieldsForm.label==5"
                class="rate icon-span">
                <el-input-number
                  v-model="item.rate"
                  :controls="false"
                  :min="0"
                  :max="100"
                  step-strictly /> %
              </span>
              <span>
                <el-checkbox v-model="item.openForm" />
                <el-button
                  type="text"
                  @click="fieldsEdit(item,index)">编辑表单</el-button>
              </span>
              <span>
                <el-checkbox v-model="item.openTask" />
                <el-button
                  type="text"
                  @click="phaseEdit(item,index)">编辑阶段工作</el-button>
              </span>
              <span>
                <el-checkbox v-model="item.openExamine" />
                <el-button
                  type="text"
                  @click="examineEdit(item)">配置审批</el-button>
              </span>
              <span class="icon-box">
                <span
                  class="el-icon-circle-plus"
                  @click="addIcon" />
                <span
                  v-if="index != 0"
                  class="el-icon-remove"
                  @click="removeIcon(index)" />
              </span>
            </div>
          </draggable>
          <template v-if="fieldsForm.label==5">
            <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束</span>
              <span><el-input
                v-model="successName"
                :maxlength="15"
              /></span>
              <span class="rate">{{ winSingle }} %</span>
              <span />
              <span />
              <span />
              <span class="icon-box" />
            </div>
            <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束</span>
              <span><el-input
                v-model="failedName"
                :maxlength="15"
              /></span>
              <span class="rate">{{ loseSingle }} %</span>
              <span />
              <span />
              <span />
              <span class="icon-box" />
            </div>
            <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束</span>
              <span>无效</span>
              <span class="rate">{{ invalidSingle }} %</span>
              <span />
              <span />
              <span />
              <span class="icon-box" />
            </div>
            <!-- <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束</span>
              <span>冻结</span>
              <span class="rate">（保留冻结前赢单率）</span>
              <span/>
              <span/>
              <span/>
              <span class="icon-box"/>
            </div> -->
          </template>
          <template v-else>
            <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束-成功</span>
              <span>
                <el-input
                  v-model="successName"
                  :maxlength="15"
                />
              </span>
              <span />
              <span />
              <span />
              <span class="icon-box" />
            </div>
            <div class="stage-setting-table">
              <span class="drag-hook" />
              <span>结束-失败</span>
              <span>
                <el-input
                  v-model="failedName"
                  :maxlength="15"
                />
              </span>
              <span />
              <span />
              <span />
              <span class="icon-box" />
            </div>
          </template>
        </div>
      </div>
    </create-sections>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="stageSubmit"
        type="primary">确定</el-button>
      <el-button @click="stageClose">取消</el-button>
    </span>

    <!-- 阶段工作 -->
    <phase-dialog
      v-if="phaseEditVisible"
      :visible.sync="phaseEditVisible"
      :row-data="rowData"
      :row-index="rowIndex"
      @change="phaseChange"
    />

    <!-- 表单字段 -->
    <fields-dialog
      v-if="fieldsEditVisible"
      :visible.sync="fieldsEditVisible"
      :row-data="rowData"
      :row-index="rowIndex"
      @change="fieldsChange"
    />

    <!-- 配置审批流 -->
    <examine-dialog
      v-if="examineEditVisible"
      :detail="rowData"
      :examine="true"
      :is-stage-flow="true"
      @change="examineChange"
      @close="examineEditVisible = false"
    />

  </el-dialog>
</template>

<script>
import {
  crmFlowSaveBaseInfoAPI,
  crmFlowSaveAPI,
  crmFlowQueryActiveAPI
} from '@/api/admin/crm'

import WkUserDepDialogSelect from '@/components/NewCom/WkUserDepDialogSelect'
import PhaseDialog from './PhaseDialog/index' // 编辑阶段工作
import FieldsDialog from './FieldsDialog/index' // 编辑表单字段
import ExamineDialog from './ExamineDialog/Create'// 配置审批流
import CreateSections from '@/components/CreateSections'
import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import Draggable from 'vuedraggable'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { isEmpty, isObject, isArray } from '@/utils/types'
import { objDeepCopy } from '@/utils'
import FieldTypeLib from '@/views/admin/fields/fieldTypeLib'

export default {
  name: 'StageDialog',
  components: {
    WkUserDepDialogSelect,
    PhaseDialog,
    FieldsDialog,
    ExamineDialog,
    CreateSections,
    WkFormItems,
    Draggable
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: Boolean,
    editType: {
      type: String,
      default: 'create' // create update
    },
    /** 编辑时候传递进来的信息 */
    infoData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    /** 关联对象数据 */
    crmModelList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      loading: false,
      fields: [ // 基础字段
        [{
          field: 'flowName',
          formType: 'text',
          isNull: 1,
          name: '阶段流程名称',
          stylePercent: 50,
          setting: [],
          disabled: false
        },
        {
          field: 'stageUserDep',
          formType: 'userDep',
          isNull: 0,
          name: '适用范围',
          setting: [],
          inputTips: '默认全公司',
          stylePercent: 50,
          disabled: false
        }],
        [{
          field: 'label',
          formType: 'select',
          isNull: 1,
          name: '关联对象',
          stylePercent: 50,
          setting: this.crmModelList,
          helpType: '24',
          helpId: '205',
          disabled: false
        }]
      ],
      // 基础信息
      fieldsForm: {
        flowName: '', // 阶段流程名称
        userIdList: [], // 员工
        deptIdList: [], // 部门
        label: '', // 关联对象
        isNeedChild: 0 // 是否包含下级部门
      },
      fieldsRules: {
        flowName: [{ required: true, message: '请输入阶段流程名称', trigger: ['blur', 'change'] }],
        label: [{ required: true, message: '请选择关联对象', trigger: ['blur', 'change'] }]
      },
      // 阶段设置列表
      settingList: [],

      // 结束成功名称
      successName: '',
      // 结束失败名称
      failedName: '',
      // 成功
      winSingle: 100,
      // 失败
      loseSingle: 0,
      // 无效
      invalidSingle: 0,

      phaseEditVisible: false, // 展示工作阶段编辑
      fieldsEditVisible: false, // 展示编辑表单字段
      examineEditVisible: false, // 展示配置审批流
      rowData: {}, // 选中行数据
      rowIndex: '' // 选中行下标
    }
  },
  computed: {
    stageTitle() {
      return {
        create: '添加阶段流程',
        'update-base': '编辑基础信息',
        'update-stage': '编辑阶段信息'
      }[this.editType]
    },

    showBase() {
      return this.editType === 'update-base'
    },

    showStage() {
      return this.editType !== 'update-base'
    }
  },
  watch: {
    infoData: function(val) {
      if (val.type == 'edit') {
        this.fieldsForm.flowName = val.flowName || ''
        this.fieldsForm.label = val.label || ''
        this.fieldsForm.userIdList = val.userIdList || []
        this.fieldsForm.deptIdList = val.deptIdList || []
      }

      this.fieldsForm.isNeedChild = val.isNeedChild || 0
      this.successName = val.successName || ''
      this.failedName = val.failedName || ''

      if (val.label === 5 && !val.successName) {
        this.successName = '赢单'
        this.failedName = '输单'
      }
      this.settingList = val.settingList || []
      // 编辑 数据填充
      this.settingList.forEach(item => {
        this.$set(item, 'openForm', item.formList && item.formList.length > 0)
        this.$set(item, 'openExamine', item.examineSaveBO && Object.keys(item.examineSaveBO).length > 0)
        this.$set(item, 'openTask', item.taskList && item.taskList.length > 0)
      })
      if (this.settingList.length == 0) {
        this.settingList = [
          this.getSetItem()
        ]
      }
    },
    editType(val) {
      this.fields[1][0].disabled = val === 'update-base'
      this.fields[0][0].disabled = val === 'update-stage'
      this.fields[0][1].disabled = val === 'update-stage'
    }
  },
  mounted() {},
  methods: {
    /**
     * @param {array} data
     * 自定义字段 change
     */
    fieldsChange(data) {
      this.$set(this.rowData, 'formList', data)
      this.$set(this.rowData, 'openForm', data.length > 0)
      this.fieldsEditVisible = false
    },
    /**
     * @param {object} data
     * 审批流 change
     */
    examineChange(data) {
      this.$set(this.rowData, 'examineSaveBO', data)
      this.$set(this.rowData, 'openExamine', true)
      this.examineEditVisible = false
    },
    /**
     * @param {array} taskList  //数据
     * 阶段工作 保存
     */
    phaseChange(data) {
      this.$set(this.rowData, 'taskList', data)
      this.$set(this.rowData, 'openTask', data.length > 0)
      this.phaseEditVisible = false
    },
    /**
     * 展示工作阶段弹窗
     */
    phaseEdit(item, index) {
      this.rowData = item
      this.rowIndex = index
      this.phaseEditVisible = true
    },
    /**
     * 展示编辑表单字段弹窗
     */
    fieldsEdit(item, index) {
      this.rowData = item
      this.rowIndex = index
      this.fieldsEditVisible = true
    },
    /**
     * 展示编辑审批弹窗
     */
    examineEdit(item) {
      this.rowData = item
      this.examineEditVisible = true
    },
    /**
     * 关闭阶段配置弹窗
     */
    stageClose() {
      if (this.showBase) {
        this.resetFields()
      }
      this.$emit('update:visible', false)
    },
    /**
     * 提交阶段配置
     */
    stageSubmit() {
      this.$refs.crmForm.validate(valid => {
        if (valid) {
          for (let index = 0; index < this.settingList.length; index++) {
            const item = this.settingList[index]
            if (isEmpty(item.name)) {
              this.$message.error(`请输入阶段${index + 1}阶段名称`)
              return
            } else if (this.fieldsForm.label == 5 && isEmpty(item.rate)) {
              this.$message.error(`请输入阶段${index + 1}成功率`)
              return
            } else if (item.openForm && item.formList.length == 0) {
              this.$message.error(`请设置阶段${index + 1}表单字段`)
              return
            } else if (item.openTask && item.taskList.length == 0) {
              this.$message.error(`请设置阶段${index + 1}阶段工作`)
              return
            } else if (item.openExamine && (!item.examineSaveBO || Object.keys(item.examineSaveBO).length == 0)) {
              this.$message.error(`请设置阶段${index + 1}审批`)
              return
            }
          }
          // 成功、失败阶段名称
          // if (this.fieldsForm.label != 5) {
          if (!this.successName || !this.failedName) {
            this.$message.error('请设置结束阶段名称')
            return
          }
          // }
          // 员工/部门处理
          const settingList = objDeepCopy(this.settingList)
          settingList.forEach((item, index) => {
            // 保存数据处理
            if (!item.openForm) {
              item.formList = []
            }
            if (!item.openTask) {
              item.taskList = []
            }
            if (!item.openExamine) {
              item.examineSaveBO = null
            } else {
              // 编辑时直接点保存可能会出现 userList和deptList 是一个Object数组导致保存失败
              // 在这里递归统一处理 userList和deptList 问题
              const formatData = function(data) {
                if (isObject(data)) {
                  if (data.userList) {
                    data.userList = data.userList.map(user => {
                      return isObject(user) ? user.userId : user
                    })
                  }
                  if (data.deptList) {
                    data.deptList = data.deptList.map(dept => {
                      return isObject(dept) ? dept.deptId : dept
                    })
                  }
                  Object.keys(data).forEach(key => {
                    if (!['userList', 'deptList'].includes(key)) {
                      formatData(data[key])
                    }
                  })
                } else if (isArray(data)) {
                  data.forEach(item => {
                    formatData(item)
                  })
                }
              }
              formatData(item.examineSaveBO)
            }
            delete item.openForm
            delete item.openExamine
            delete item.openTask
            item.orderNum = index
            // 转化一维数组 追加坐标
            const arr = []
            if (item.formList) {
              item.formList.forEach((father, fatherIndex) => {
                father.forEach((child, childIndex) => {
                  arr.push({
                    ...child,
                    formPosition: `${fatherIndex},${childIndex}`
                  })
                })
              })
            }

            arr.forEach(item => {
              if (item.hasOwnProperty('optionsData')) {
                delete item.optionsData
              }
              if (!item.type) {
                const findRes = FieldTypeLib.find(o => o.formType === item.formType)
                if (findRes) item.type = findRes.type
              }
            })

            item.formList = arr
          })

          // 处理回显异常转交正常保存报错
          const newSettingList = objDeepCopy(settingList)
          newSettingList.forEach(item => {
            const advancedConfigBO = item.examineSaveBO?.advancedConfigBO || []
            if (advancedConfigBO.nodeHandleUser && advancedConfigBO.nodeHandleUser.length) {
              advancedConfigBO.nodeHandleUser = advancedConfigBO.nodeHandleUser.map(item => item.userId)
            }
          })

          let params
          if (this.showBase) {
            params = {
              flowName: this.fieldsForm.flowName,
              userIdList: this.fieldsForm.userIdList,
              deptIdList: this.fieldsForm.deptIdList
            }
          } else {
            params = {
              flowName: this.fieldsForm.flowName,
              label: this.fieldsForm.label,
              userIdList: this.fieldsForm.userIdList,
              deptIdList: this.fieldsForm.deptIdList,
              successName: this.successName,
              failedName: this.failedName,
              settingList: newSettingList
            }
          }

          if (this.fieldsForm.deptIdList?.length) {
            params.isNeedChild = this.fieldsForm.isNeedChild
          }

          const activeParams = {
            label: this.fieldsForm.label
          }
          if (['update-base', 'update-stage'].includes(this.editType)) { // 编辑
            params.flowId = this.infoData.flowId
            activeParams.flowId = this.infoData.flowId

            if (this.showBase) {
              this.loading = true
              crmFlowSaveBaseInfoAPI(params).finally(() => {
                this.resetFields()
                this.$emit('submit')
                this.loading = false
              })
            } else {
              this.$confirm('阶段编辑之后，只对新数据生效，不更新已有数据的阶段信息。且已有数据的阶段信息也不会参与商机阶段统计，是否继续？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                this.saveRequest(params, activeParams)
              }).catch(() => {
              })
            }
          } else {
            this.saveRequest(params, activeParams)
          }
        }
      })
    },

    saveRequest(params, activeParams) {
      // console.log('save: ', params)
      // return
      this.loading = true
      Promise.all([
        crmFlowSaveAPI(params),
        crmFlowQueryActiveAPI(activeParams) // 查询该模块是否被重复关联
      ]).then(resArr => {
        const isRepetition = !!resArr[1].data
        if (
          this.infoData.flowId &&
          this.infoData.status === 2 &&
          !isRepetition
        ) {
          // 是编辑、状态是停用，没有被重复关联，编辑后提示启用
          this.$confirm('保存成功, 是否启用此阶段流程？', '提示', {
            confirmButtonText: '启用',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
            this.resetFields()
            this.$emit('update-status', this.infoData)
          }).catch(() => {
            this.resetFields()
            this.$emit('submit')
          })
          this.loading = false
          return
        }

        if (isRepetition && this.fieldsForm.label !== 5) {
          // 被重复关联并且不是商机类型
          this.$confirm(`保存成功, 因当前有已启用的阶段流程【${resArr[1].data}】此阶段流程默认停用。`, '提示', {
            confirmButtonText: '确定',
            showCancelButton: false,
            type: 'success'
          }).then(() => {
            this.resetFields()
            this.$emit('submit')
          }).catch(() => {
            this.resetFields()
            this.$emit('submit')
          })
          this.loading = false
        } else {
          this.$message.success('操作成功')
          this.resetFields()
          this.$emit('submit')
          this.loading = false
        }
      }).catch(() => {
        this.loading = false
      })
    },

    // 增加阶段
    addIcon() {
      this.settingList.push(this.getSetItem())
    },

    /**
     * 获取setItem
     */
    getSetItem() {
      return { name: '', rate: '', openForm: false, openExamine: false, openTask: false, taskList: [], formList: [], examineSaveBO: null }
    },

    /**
     * 删除阶段
     */
    removeIcon(val) {
      this.settingList.splice(val, 1)
    },
    /**
     *  表单change
     */
    formChange(field, index, value, valueList) {

    },
    /**
     * 重置表单
     */
    resetFields() {
      this.fieldsForm = { flowName: '', userIdList: [], deptIdList: [], label: '' }
      this.$refs['crmForm'].resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
.stage-list {
  margin-bottom: 15px;
}

.stage-label {
  display: inline-block;
  width: 100px;
}

// 阶段设置
.stage-setting-content {
  padding: 0 12px;
  margin-top: $--interval-base;
}

.stage-setting-table-header {
  background: #f5f5f5;
}

.stage-setting-table,
.stage-setting-table-header {
  display: flex;
  height: 50px;
  line-height: 50px;
  text-align: center;
}

.stage-setting-table-body {
  height: 250px;
  overflow-y: auto;
}

.stage-setting-table > span:not(.drag-hook):not(.icon-box):not(.rate),
.stage-setting-table-header > span:not(.drag-hook):not(.icon-box):not(.rate),
.stage-setting-table > .el-input {
  flex: 1;
}

.stage-setting-table > span > .el-input-number,
.stage-setting-table > span > .el-input {
  width: 70%;
}

.stage-setting-table > .icon-span > .el-input-number,
.stage-setting-table > .icon-span > .el-input {
  width: 50%;
}

.stage-setting-table > span .el-icon-circle-plus {
  width: 20px;
  height: 20px;
  color: #2362fb;
  cursor: pointer;
}

.stage-setting-table > span .el-icon-remove {
  width: 20px;
  height: 20px;
  color: #ff6767;
  cursor: pointer;
}

.stage-setting-table:nth-child(2n + 1) {
  background: #f7f8fa;
}

.icon-box {
  display: inline-block;
  width: 50px;
  margin-left: 10px;
  text-align: left;
}

.drag-hook {
  width: 30px;
  color: $--color-text-regular;
  cursor: move;
}

.rate {
  width: 150px;
}

::v-deep .el-dialog__body {
  padding: 10px 20px;
}

::v-deep .section-header {
  padding: 5px 0;
}

::v-deep .section .content {
  padding: 0;
}

</style>
