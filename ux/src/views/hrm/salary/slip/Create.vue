<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    top="10vh"
    width="700px"
    title="发送工资条"
    @close="close">
    <div class="salary-slip-create">
      <template v-if="isNext">
        <div class="filter-header">
          <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
          <wk-popover-filter
            :width="700"
            :field-from.sync="filterObj"
            :field-list="filterList"
            :has-content="hasFilterContent"
            style="margin-right: 5px;"
            placement="bottom"
            @sure="refreshList"
            @reset="resetFilter"
          />
        </div>
        <div class="table-header">选择发放的员工</div>
        <el-table
          :data="employeeList"
          :height="tableHeight"
          :row-height="40"
          use-virtual
          style="width: 100%;"
          @selection-change="handleSelectionChange">
          <el-table-column
            type="selection"
            width="55" />
          <el-table-column
            v-for="(item, index) in fieldList"
            :key="index"
            :label="item.name"
            :prop="item.field"
            show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.column.property === 'sendStatus' ? {
              0: '未发送',
              1: '已发送'
            }[scope.row[scope.column.property]] : scope.row[scope.column.property] }}</template>
          </el-table-column>
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
      </template>
      <template v-else>
        <flexbox class="salary-slip-create__header">
          <span>选择模板</span>
          <i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="33"
            data-id="293" />
          <el-select v-model="templateId" @change="templateChange">
            <el-option
              v-for="item in templates"
              :key="item.id"
              :label="item.templateName"
              :value="item.id">
              <flexbox>
                <flexbox-item>{{ item.templateName }}</flexbox-item>
                <i v-if="item.defaultOption == 0" class="el-icon-close template-delete" @click.stop="deleteTemplateClick(item)" />
              </flexbox>
            </el-option>
            <div class="handle-bar">
              <el-button
                type="text"
                class="save-btn"
                icon="wk wk-icon-save"
                @click="templateSave">保存模板</el-button>
            </div>
          </el-select>
        </flexbox>
        <flexbox />
        <flexbox class="salary-slip-create__header">
          <flexbox-item>选择员工可见项</flexbox-item>
          <el-button type="primary" icon="el-icon-plus" @click="classAdd">新建分类</el-button>
        </flexbox>
        <flexbox />
        <div class="salary-slip-create__body">
          <flexbox class="section__header">
            <el-checkbox
              v-model="allCheckbox"
              :indeterminate="allIsIndeterminate"
              @change="allChange" /><span class="label">全部可见</span>
          </flexbox>
          <draggable
            :list="templateList"
            :options="{
              group: 'mission',
              forceFallback: false,
              dragClass: 'sortable-parent-drag',
              filter: '.ignore-elements'
            }"
            class="section__list"
            handle=".section-handle">
            <div
              v-for="(item, index) in templateList"
              :key="index"
              class="section">
              <flexbox class="item is-section">
                <!-- <el-checkbox
                  v-model="item.isHide"
                  :true-label="0"
                  :false-label="1"
                  class="item__header"/> -->
                <flexbox-item class="item__body">
                  {{ item.name }}

                </flexbox-item>
                <div class="item__footer">
                  <i class="wk wk-write" title="编辑" @click="classNameChange(item)" />
                  <i v-if="templateList.length > 1" class="wk wk-delete" title="删除" @click="deleteItem(item, index)" />
                  <span class="drag-handle section-handle">⋮⋮</span>
                </div>
              </flexbox>
              <draggable
                :list="item.optionList"
                :options="{
                  group: {
                    name: 'missionSon'
                  },
                  forceFallback: false,
                  filter: '.board-item-active',
                  dragClass: 'sortable-drag'
                }"
                class="ignore-elements"
                handle=".child-handle">
                <flexbox
                  v-for="(subItem, subIndex) in item.optionList"
                  :key="subIndex"
                  class="item is-child">
                  <el-checkbox
                    v-model="subItem.isHide"
                    :disabled="subItem.code === disabledCode"
                    :true-label="0"
                    :false-label="1"
                    class="item__header"
                    @change="changeAllCheckboxStatus" />
                  <flexbox-item class="item__body">{{ subItem.name }}
                    <el-tooltip
                      v-if="subItem.remark"
                      :content="subItem.remark"
                      effect="dark"
                      placement="top">
                      <i class="wk wk-help wk-help-tips" />
                    </el-tooltip>
                  </flexbox-item>
                  <div class="item__footer">
                    <i class="wk wk-icon-draft" title="备注" alt="备注" @click="draftAdd(subItem)" />
                    <span class="drag-handle child-handle">⋮⋮</span>
                  </div>
                </flexbox>
                <div v-if="item.optionList.length === 0" class="no-data">将工资项拖拽到此分类</div>
              </draggable>
            </div>
          </draggable>
        </div>
      </template>
    </div>
    <template v-if="isNext">
      <span
        slot="footer"
        :key="isNext ? 'next' :'first'"
        class="dialog-footer">
        <el-button
          @click="isNext = false">上一步</el-button>
        <el-button
          :disabled="selectionList.length === 0"
          type="primary"
          @click="sendClick('select')"
        >发放已选员工</el-button>
        <el-button
          :disabled="employeeList.length === 0"
          type="primary"
          @click="sendClick('all')">全部发放</el-button>
      </span>
    </template>
    <template v-else>
      <span
        slot="footer"
        class="dialog-footer">
        <el-button
          type="primary"
          @click="nextClick">下一步</el-button>
        <el-button @click="close">取消</el-button>
      </span>
      <span
        slot="footer"
        style="float: left;margin-top: 5px;">
        <el-checkbox
          v-model="templateData.hideEmpty"
          :true-label="1"
          :false-label="0"
          style="margin-right: 8px;" />隐藏空数据工资项
      </span>
    </template>
  </el-dialog>
</template>

<script>
import {
  hrmSalarySlipTemplateListAPI,
  hrmSalarySlipTemplateDetailAPI,
  hrmSalarySlipTemplateDeleteAPI,
  hrmSalarySlipTemplateAddAPI,
  hrmSalarySlipRecordQueryEmployeeAPI,
  hrmSalarySlipRecordSendAPI
} from '@/api/hrm/salary'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import Draggable from 'vuedraggable'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import { objDeepCopy } from '@/utils'

export default {
  // 工资条创建
  name: 'SalarySlipCreate',
  components: {
    Draggable,
    WkPopoverFilter
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
      allCheckbox: false,
      allIsIndeterminate: false,
      templateId: '',
      templateData: {}, // hideEmpty 0 不隐藏 1 隐藏
      templates: [],
      templateList: [],
      isNext: false,
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      tableHeight: document.documentElement.clientHeight * 0.6 - 90, // 表的高度
      employeeList: [],
      selectionList: [],
      fieldList: [{
        name: '姓名',
        field: 'employeeName'
      }, {
        name: '工号',
        field: 'jobNumber'
      }, {
        name: '部门',
        field: 'deptName'
      }, {
        name: '岗位',
        field: 'post'
      }, {
        name: '手机号',
        field: 'mobile'
      }, {
        name: '发送状态',
        field: 'sendStatus'
      }],
      // 筛选确定数据
      filterList: [
        {
          name: '员工姓名',
          field: 'employeeName',
          formType: 'text',
          setting: []
        }, {
          name: '部门',
          field: 'deptId',
          formType: 'structure',
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        }, {
          name: '发送状态',
          field: 'sendStatus',
          formType: 'select',
          setting: [{
            label: '未发送',
            value: 0
          }, {
            label: '已发送',
            value: 1
          }]
        }
      ],
      disabledCode: 240101, // 实发工资
      filterObj: {}
    }
  },
  computed: {
    // 有筛选内容
    hasFilterContent() {
      if (this.filterObj) {
        let hasContent = false
        const keys = Object.keys(this.filterObj)
        for (let index = 0; index < keys.length; index++) {
          const key = keys[index]
          if (this.filterObj[key] != '' &&
           this.filterObj[key] != [] &&
           this.filterObj[key] != null &&
           this.filterObj[key] != undefined) {
            hasContent = true
          }
        }
        return hasContent
      }
      return false
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.isNext = false
          this.currentPage = 1
          this.selectionList = []
          this.employeeList = []
          this.filterObj = {}
          this.getTemplateList()
        }
      },
      immediate: true
    }
  },
  mounted() {
  },
  methods: {
    /**
     * 全部修改
     */
    allChange() {
      this.templateList.forEach(section => {
        if (section && section.optionList) {
          section.optionList.forEach(item => {
            if (item.code != this.disabledCode) {
              item.isHide = this.allCheckbox ? 0 : 1
            }
          })
        }
      })
    },

    /**
     * 调整状态
     */
    changeAllCheckboxStatus() {
      let firstItem = null
      let indeterminateChange = false
      for (let sectionIndex = 0; sectionIndex < this.templateList.length; sectionIndex++) {
        const section = this.templateList[sectionIndex]
        for (let index = 0; index < section.optionList.length; index++) {
          const item = section.optionList[index]
          if (!firstItem) {
            firstItem = item
          } else if (firstItem.isHide != item.isHide) {
            indeterminateChange = true
            this.allIsIndeterminate = true
            break
          }
        }

        if (indeterminateChange) {
          break
        }
      }

      if (!indeterminateChange) {
        this.allCheckbox = firstItem.isHide !== 1
        this.allIsIndeterminate = false
      }
    },

    /**
     * 更改当前页数
     * init 重置为第一个
     */
    getTemplateList(init = true) {
      this.loading = true
      hrmSalarySlipTemplateListAPI()
        .then(res => {
          this.loading = false
          this.templates = res.data || []
          if (init) {
            if (this.templates.length > 0) {
              this.templateData = this.templates[0]
              this.templateId = this.templateData.id
            } else {
              this.templateData = {}
              this.templateId = ''
            }
            this.getTemplateDetail()
          }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 模板详情
     */
    getTemplateDetail() {
      this.loading = true
      hrmSalarySlipTemplateDetailAPI(this.templateId)
        .then(res => {
          this.loading = false
          this.templateList = res.data || []
          this.changeAllCheckboxStatus()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 模板修改
     */
    templateChange() {
      this.templateData = this.templates.find(item => item.id === this.templateId)
      this.getTemplateDetail()
    },

    /**
     * 删除模板
     */
    deleteTemplateClick(item) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          hrmSalarySlipTemplateDeleteAPI(item.id)
            .then(res => {
              this.loading = false
              this.$message.success('删除成功')
              this.getTemplateList()
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {
        })
    },

    /**
     * 备注
     */
    draftAdd(subItem) {
      this.$prompt('', '备注', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'text',
        inputValue: subItem.remark,
        inputPlaceholder: '请输入内容',
        closeOnClickModal: false
      }).then(({ value }) => {
        subItem.remark = value
      }).catch(() => {})
    },

    /**
     * 分类名称修改
     */
    classNameChange(item) {
      this.$prompt('', '分类名称', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'text',
        inputValue: item.name,
        inputPlaceholder: '请输入内容',
        inputPattern: /\S/,
        closeOnClickModal: false,
        inputErrorMessage: '请输入内容'
      }).then(({ value }) => {
        item.name = value
      }).catch(() => {})
    },

    /**
     * 删除块
     */
    deleteItem(item, index) {
      const sideItem = index === 0 ? this.templateList[index + 1] : this.templateList[index - 1]
      const message = item.optionList.length === 0 ? '确定删除？' : `确定删除？该分类下工资项将并入"${sideItem.name}"分类`

      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.templateList.splice(index, 1)
          if (item.length !== 0) {
            sideItem.optionList = sideItem.optionList.concat(item.optionList)
          }
        })
        .catch(() => {})
    },

    /**
     * 新建分类
     */
    classAdd() {
      this.$prompt('', '分类名称', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'text',
        inputPlaceholder: '请输入内容',
        inputPattern: /\S/,
        closeOnClickModal: false,
        inputErrorMessage: '请输入内容'
      }).then(({ value }) => {
        this.templateList.unshift({
          isHide: 0,
          type: 1,
          name: value,
          optionList: []
        })
      }).catch(() => {})
    },

    /**
     * 模板保存
     */
    templateSave() {
      const emptyObj = this.templateList.find(item => item.optionList.length === 0)
      if (emptyObj) {
        this.$message.error(`请完善分类${emptyObj.name}工资项`)
      } else {
        this.$prompt('', '模板名称', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'text',
          inputPlaceholder: '请输入内容',
          inputPattern: /\S/,
          closeOnClickModal: false,
          inputErrorMessage: '请输入内容'
        }).then(({ value }) => {
          console.log(value)

          this.loading = true
          hrmSalarySlipTemplateAddAPI({
            hideEmpty: this.templateData.hideEmpty,
            slipTemplateOption: this.templateList,
            templateName: value
          })
            .then(res => {
              this.loading = false
              this.getTemplateList(false)
            })
            .catch(() => {
              this.loading = false
            })
        }).catch(() => {})
      }
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 下一步
     */
    nextClick() {
      const emptyItem = this.templateList.find(item => item.optionList.length === 0)
      if (emptyItem) {
        this.$message.error(`请完善分类"${emptyItem.name}"下的工资项`)
      } else {
        this.isNext = true
        this.refreshList()
      }
    },

    /**
     * 刷新
     */
    refreshList() {
      this.currentPage = 1
      this.getEmployeeList()
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getEmployeeList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getEmployeeList()
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
    },

    /**
     * 获取员工信息
     */
    getEmployeeList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmSalarySlipRecordQueryEmployeeAPI(params)
        .then(res => {
          this.employeeList = res.data.list
          this.total = res.data.totalRow
          if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
            this.currentPage = this.currentPage - 1
            this.getList()
          } else {
            this.total = res.data.totalRow
            this.loading = false
          }

          this.$nextTick(() => {
            const warpDom = document.querySelector('.el-table__body-wrapper')
            warpDom.scrollTop = 1
            if (warpDom.scrollLeft != 0) {
              warpDom.scrollLeft = warpDom.scrollLeft - 1
            }
          })
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 选择发放
     * 全部发放
     */
    sendClick(type) {
      if (this.loading === false) {
        this.loading = true
        const templateList = objDeepCopy(this.templateList)
        const newTemplateList = []
        templateList.forEach(item => {
          const optionList = []
          item.optionList.forEach(subItem => {
            if (subItem.isHide == 0) {
              optionList.push(subItem)
            }
          })
          item.optionList = optionList
          if (item.optionList.length > 0) {
            newTemplateList.push(item)
          }
        })
        const params = {
          hideEmpty: this.templateData.hideEmpty,
          slipTemplateOption: newTemplateList
        }

        if (type === 'all') {
          for (var key in this.filterObj) {
            params[key] = this.filterObj[key]
          }
          params.isAll = true
          params.sempRecordIds = []
        } else {
          params.isAll = false
          params.sempRecordIds = this.selectionList.map(item => item.sempRecordId)
        }

        hrmSalarySlipRecordSendAPI(params)
          .then(res => {
            this.loading = false
            this.$message.success('操作成功')
            this.close()
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    /**
     * 选择的员工数据
     */
    handleSelectionChange(val) {
      this.selectionList = val
    }
  }
}
</script>
<style lang="scss" scoped>
.el-dialog__wrapper {
  ::v-deep .el-dialog__body {
    padding-top: 5px;
  }
}

.salary-slip-create {
  height: 60vh;

  &__header {
    margin-bottom: 8px;

    .el-select {
      margin-left: 8px;
    }
  }

  &__body {
    height: calc(100% - 80px);
    overflow-y: auto;
  }

  .section__list {
    padding-right: 8px;
  }

  .section__header {
    padding-bottom: 5px;

    .el-checkbox {
      margin-right: 8px;
    }

    .label {
      font-weight: bold;
    }
  }

  .section {
    .item {
      padding: 5px 0;

      &__header {
        flex-shrink: 0;
      }

      &__body {}

      &__footer {
        flex-shrink: 0;

        .drag-handle,
        i {
          cursor: pointer;

          &:hover {
            color: $--color-primary;
          }
        }

        i + i {
          margin-left: 8px;
        }

        .drag-handle {
          width: 16px;
          margin-left: 8px;
          color: $--color-text-primary;
          cursor: move;
        }
      }

      &.is-section {
        padding-left: 22px;

        .item__body {
          font-weight: bold;
        }
      }

      &.is-child {
        .item__header {
          padding-left: 23px;
        }
      }
    }
  }

  .no-data {
    padding: 10px 0;
    margin-left: 20px;
    color: #ccc;
  }

  .table-header {
    margin-bottom: 8px;
  }

  .filter-header {
    line-height: 32px;
    text-align: right;
  }
}

.el-select-dropdown {
  .el-select-dropdown__item.selected {
    background-color: #f5f7fa;
  }

  .handle-bar {
    padding: 0 15px;
    margin-top: 5px;
    border-top: 1px solid #e6e6e6;

    .save-btn {
      ::v-deep.wk-icon-save {
        margin-right: 3px;
        font-size: 14px;
      }
    }
  }

  .template-delete {
    padding: 5px 0;
    color: $--color-text-regular;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }
}
</style>
