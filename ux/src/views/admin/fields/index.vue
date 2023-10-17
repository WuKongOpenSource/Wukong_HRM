<template>
  <flexbox
    v-loading="loading"
    align="flex-start"
    justify="flex-start"
    class="fields-index body">
    <div class="body-left">
      <div class="body-left_title">字段库<i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="27"
        data-id="238" /></div>
      <ul>
        <draggable
          :list="fieldLibList"
          :options="dragLeftConfig"
          :clone="dragLeftMove"
          class="lib-wrapper"
          @start="dragLeftStart"
          @end="dragLeftEnd">
          <div
            v-for="item in fieldLibList"
            :key="item.id"
            class="lib-item"
            @click="handleLibFieldClick(item)">
            <i
              :class="item.icon"
              class="lib-item-icon" />
            <span>{{ item.name }}</span>
          </div></draggable>
      </ul>
    </div>

    <div class="body-content">
      <flexbox
        align="flex-start"
        justify="flex-start"
        direction="column"
        class="body-content-warp">
        <el-header>
          <div class="title">编辑{{ title }}字段</div>
          <div>
            <el-button
              v-if="config.showSave"
              v-debounce="handleSave"
              type="primary">保存</el-button>
            <el-button
              v-if="config.showBack"
              @click="handleCancel">返回</el-button>
          </div>
        </el-header>
        <flexbox-item style="margin-left: 0;" class="body-content-main">
          <el-main>
            <draggable
              :list="fieldArr"
              :options="dragListConfig"
              class="field-list"
              @end="dragListEnd">
              <flexbox
                v-for="(childArr, fatherIndex) in fieldArr"
                :key="fatherIndex"
                align="flex-start"
                justify="flex-start"
                class="field-row">
                <component
                  :is="field | typeToComponentName"
                  v-for="(field, childIndex) in childArr"
                  ref="fieldItem"
                  :key="childIndex"
                  :field="field"
                  :field-arr="fieldArr"
                  :point="[fatherIndex, childIndex]"
                  :active-point="selectedPoint"
                  @action="handleAction"
                  @child-drag-add="handleChildDragAdd"
                  @click="handleSelect([fatherIndex, childIndex])" />
              </flexbox>
            </draggable>

            <el-empty
              v-if="!fieldArr || fieldArr.length === 0"
              :image="require('@/assets/img/empty/drag.png')"
              description="拖拽或点击左侧字段创建表单" />
          </el-main>
        </flexbox-item>
      </flexbox>
    </div>

    <div style="margin-left: 0;" class="body-right">
      <setting-field
        v-if="selectedField"
        :field="selectedField"
        :point="selectedPoint"
        :field-arr="fieldArr"
        :can-transform="canTransform"
        :transform-data="transformData"
        @child-edit="handleChildEdit"
        @update-width="handleUpdateFieldWidth" />
    </div>
  </flexbox>
</template>

<script>
import {
  customFieldHandleAPI,
  oaFieldHandleAPI,
  customFieldListAPI,
  oaExamineFieldListAPI,
  crmMarketingNewFieldListAPI,
  crmMarketingFieldHandleAPI
} from '@/api/admin/crm'
import {
  hrmConfigQueryFieldByLabelAPI,
  hrmConfigSaveFieldAPI
} from '@/api/admin/hrm'
import {
  jxcFieldQueryAPI,
  jxcFieldSaveAPI
} from '@/api/admin/jxc'

import { filedGetFieldAPI } from '@/api/crm/common'
import { isEmpty } from '@/utils/types'

import {
  FieldInput,
  FieldTextarea,
  FieldSelect,
  FieldCheckbox,
  FieldFile,
  FieldBoolean,
  FieldPercent,
  FieldPosition,
  FieldLocation,
  FieldDetailTable,
  FieldWritingSign,
  FieldDateInterval,
  FieldDescText,
  FieldSerialNumber,
  FieldTag,
  FieldAttention,
  FieldGroup
} from './components/FieldItem'
import SettingField from './components/SettingField'

import draggable from 'vuedraggable'

import Field from './field'
import FieldTypeLib from './fieldTypeLib'
import { objDeepCopy } from '@/utils/index'
import { typeToComponent } from './utils'
import merge from '@/utils/merge'
import { getDefaultOperating } from '@/views/admin/fields/utils'

const DefaultFieldsIndex = {
  title: '', // 模块标题
  showSave: true, // 展示保存
  showBack: true // 展示返回
}

/**
 * 修改该文件逻辑 需要考虑是否修改 src/views/admin/components/FieldsDialog 字段编辑弹框
 */
export default {
  name: 'FieldsIndex',
  components: {
    FieldInput,
    FieldTextarea,
    FieldSelect,
    FieldCheckbox,
    FieldFile,
    FieldBoolean,
    FieldPercent,
    FieldPosition,
    FieldLocation,
    FieldDetailTable,
    FieldWritingSign,
    FieldDateInterval,
    FieldDescText,
    FieldSerialNumber,
    FieldTag,
    FieldAttention,
    FieldGroup,
    SettingField,
    draggable
  },
  filters: {
    /** 根据type 找到组件 */
    typeToComponentName(item) {
      return typeToComponent(item)
    }
  },
  props: {
    props: Object,
    fieldLibFun: Function, // 输入完成Lib 传入过滤后的数据
    getListPromise: Function, // 获取字段列表的方法
    savePromise: Function // 保存方法
  },
  data() {
    return {
      moduleType: '', // 所属模块
      loading: false,

      fieldLibList: [], // 字段库
      dragLeftConfig: {
        group: {
          pull: 'clone',
          put: false,
          name: 'libList'
        },
        forceFallback: true,
        sort: false
      },
      movedField: null, // 被拖拽的字段库字段

      dragListConfig: {
        delay: 100,
        group: {
          name: 'list',
          put: ['libList'],
          pull: true
        },
        forceFallback: true,
        fallbackClass: 'draggingStyle'
      },
      fieldArr: [],
      rejectHandle: true, // 请求未获取前不能操作

      isChildDragAdd: false, // 是否为子组件内部拖拽添加

      selectedPoint: [null, null],
      selectedField: null,
      // 转移匹配字段源
      transformData: null
    }
  },
  computed: {
    // 能转移
    canTransform() {
      const rowNum = this.selectedPoint[0]
      const columnNum = this.selectedPoint[1]
      if (!isEmpty(rowNum) && !isEmpty(columnNum)) {
        const currentField = this.fieldArr[rowNum][columnNum]
        if (currentField.formType === 'detail_table') return false
      }
      return this.moduleType === 'crm_leads'
    },
    title() {
      if (this.config.title) return this.config.title

      return {
        crm_leads: '线索',
        crm_customer: '客户',
        crm_contacts: '联系人',
        crm_business: '商机',
        crm_contract: '合同',
        crm_product: '产品',
        crm_receivables: '回款',
        crm_visit: '客户回访',
        crm_marketing: '市场活动',
        crm_receivables_plan: '回款计划',
        crm_invoice: '发票',
        crm_activity_log: '跟进记录',
        crm_out_work: '外勤签到'
      }[this.moduleType] || ''
    },

    // 合并 props
    config() {
      return merge({ ...DefaultFieldsIndex }, this.props || {})
    }
  },
  created() {
    console.log('ddd', this.$route)
    this.moduleType = this.$route.params.type || ''
    this.initCom()

    // 配置转移字段
    if (this.canTransform) {
      this.getTransformField()
    }
  },
  methods: {
    /**
     * 初始化
     */
    initCom() {
      if (this.fieldLibFun) {
        this.fieldLibList = this.fieldLibFun(FieldTypeLib)
      } else if (this.moduleType === 'crm_marketing') {
        this.fieldLibList = FieldTypeLib.filter(item => {
          // 支持 关注度   自定义标签  自定义编号
          // 不支持 描述文字、地址、定位、手写签名、图片、明细表格、分组标题 自定义编号 自定义标签 分组标题
          return [
            'text',
            'textarea',
            'website',
            'boolean_value',
            'select',
            'checkbox',
            'number',
            'floatnumber',
            'percent',
            'mobile',
            'email',
            'date',
            'datetime',
            'date_interval',
            'field_tag'
          ].includes(item.formType)
        })
      } else if (this.moduleType.startsWith('jxc_')) {
        this.fieldLibList = FieldTypeLib
      } else if (this.moduleType === 'hrm_employee') {
        this.fieldLibList = FieldTypeLib.filter(item => !['rich_text_format'].includes(item.formType))
      } else {
        this.fieldLibList = FieldTypeLib
      }
      this.getFieldList()
    },

    /**
     * 获取字段列表
     */
    getFieldList() {
      console.log('this.getListPromise--', this.getListPromise)
      // 自定义获取数据的请求
      if (this.getListPromise) {
        console.log('1111')
        this.loading = true
        this.getListPromise()
          .then(res => {
            console.log('getListPromise res', res)
            const list = res.data || []
            this.handleFieldList(list)

            this.rejectHandle = false
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      } else {
        let request = null
        const params = {}
        const config = this.$route.params

        if (this.moduleType === 'hrm_employee') {
          this.getFieldListReq(hrmConfigQueryFieldByLabelAPI, config.id)
          return
        }

        if (this.moduleType === 'crm_marketing') {
          request = crmMarketingNewFieldListAPI
          params.id = config.id
        } else if (this.moduleType === 'oa_examine') {
          request = oaExamineFieldListAPI
          params.categoryId = config.id
          params.needHidden = true
        } else if (this.moduleType.startsWith('jxc_')) {
          request = jxcFieldQueryAPI
        } else {
          request = customFieldListAPI
        }

        if (config.label) {
          params.label = config.label
        }

        this.getFieldListReq(request, params)
      }
    },

    /**
     * 请求字段列表
     * @param request
     * @param params
     */
    getFieldListReq(request, params) {
      this.loading = true
      request(params)
        .then(res => {
          const list = res.data || []
          this.handleFieldList(list)

          this.rejectHandle = false
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 处理字段数据
     * @return {*}
     */
    handleFieldList(list) {
      list.forEach(item => {
        item.forEach(subItem => {
          if (!subItem.formAssistId) {
            subItem.formAssistId = subItem.fieldId
          }

          // 市场活动去掉隐藏操作
          if (this.moduleType === 'crm_marketing') {
            subItem.operating = getDefaultOperating(251, 9)
          }
        })
      })

      this.fieldArr = list
      if (list.length > 0) {
        this.handleSelect([0, 0])
      }
    },

    /**
     * 生成逻辑表单辅助id
     */
    generateFormAssistId() {
      return Date.now().toString() + Math.random().toFixed(6).slice(-6)
    },

    /**
     * 点击左侧字段库进行添加
     * @param field {Object}
     */
    handleLibFieldClick(field) {
      console.log('lib click')
      this.movedField = field
      this.dragLeftEnd()
    },

    /**
     * @description: 左侧字段库拖拽开始
     * @return {*}
     */
    dragLeftStart() {
      this.isChildDragAdd = false // 重置默认状态
    },

    /**
     * 左侧字段库拖拽
     * @param field {Object}
     */
    dragLeftMove(field) {
      this.movedField = field
    },

    /**
     * 左侧字段库拖拽结束
     */
    dragLeftEnd(evt) {
      console.log('drag left end', evt)
      if (this.rejectHandle) return
      const ids = []

      // 限制添加一个标签字段
      let isPass = true
      this.fieldArr.forEach(item => {
        item.forEach(subItem => {
          ids.push(subItem.formAssistId)
          if (subItem.formType === 'field_tag' && this.movedField.formType === 'field_tag') {
            isPass = false
          }
        })
      })
      if (!isPass) {
        this.$message.error('只允许添加一个自定义标签字段')
        return
      }

      const newField = new Field({
        name: this.movedField.name,
        formType: this.movedField.formType
      })
      newField.stylePercent = 100
      newField.operating = this.getFieldOperating(255)
      newField.formAssistId = this.generateFormAssistId() // 辅助id
      if (this.$route.params.label !== 'none') {
        newField.label = this.$route.params.label
      }

      switch (this.movedField.formType) {
        case 'desc_text':
          newField.name = ''
          break
        case 'select':
        case 'checkbox':
          newField.options = '选1,选2,选3'
          newField.setting = ['选1', '选2', '选3']
          break
        case 'detail_table':
          // 11101000
          newField.operating = this.getFieldOperating(232)
          newField.fieldExtendList = []
          newField.defaultValue = null
          newField.remark = `添加${newField.name}`
          break
        case 'serial_number':
          newField.operating = this.getFieldOperating(245)
          newField.isUnique = 1
          newField.setting = [
            { type: 1, resetType: 4, startNumber: undefined, stepNumber: undefined }
          ]
          break
        case 'field_tag':
          newField.operating = this.getFieldOperating(249)
          break
        case 'field_attention':
          newField.operating = this.getFieldOperating(250)
          newField.defaultValue = null
          break
        case 'field_group':
          newField.operating = this.getFieldOperating(224)
          break
        case 'rich_text_format':
          newField.operating = this.getFieldOperating(239)
          break
        case 'handwriting_sign':
        case 'file':
        case 'pic':
          // 禁止设置唯一
          newField.operating = this.getFieldOperating(251)
          break
      }
      delete newField.fieldId

      if (this.isChildDragAdd) {
        // 如果是子组件内部拖拽添加
        if ([
          'detail_table',
          'desc_text',
          'handwriting_sign',
          'pic',
          'serial_number',
          'field_tag',
          'field_attention',
          'field_group',
          'rich_text_format'
        ].includes(newField.formType)) {
          this.$message.error('此字段内部不能添加该类型的字段')
          return
        }
        this.childDragAddEnd(newField, evt)
        return
      }

      let rowNum = null
      if (
        evt &&
        evt.pullMode === 'clone' &&
        !isEmpty(evt.newIndex)
      ) {
        rowNum = evt.newIndex
      } else if (this.fieldArr.length === 0) {
        rowNum = 0
      } else {
        rowNum = this.selectedPoint[0] + 1
      }
      this.fieldArr.splice(rowNum, 0, [newField])
      this.handleSelect([rowNum, 0])
    },

    /**
     * 子组件内部(明细表格)拖拽添加
     */
    handleChildDragAdd(point, evt) {
      // console.log('child drag add')
      this.selectedPoint = point
      this.isChildDragAdd = true
    },

    /**
     * 子组件内部(明细表格)追加表格字段
     */
    childDragAddEnd(newField, evt) {
      newField.stylePercent = 50
      // 10101011
      newField.operating = getDefaultOperating(171, 9)
      const findRes = this.fieldLibList.find(o => newField.formType === o.formType)
      if (findRes) newField.type = findRes.type

      // const rowNum = evt.newIndex
      const rowNum = this.selectedPoint[0]
      const columnNum = this.selectedPoint[1]
      const currentField = this.fieldArr[rowNum][columnNum]
      if (isEmpty(currentField.fieldExtendList)) {
        currentField.fieldExtendList = []
      }
      if (currentField.fieldExtendList.length >= 20) {
        this.$message.error('明细表格类型内部字段不能超过20个')
        return
      }
      newField.fieldName = this.generateFieldName(currentField.fieldExtendList)
      currentField.fieldExtendList.push(newField)
      console.log('currentField: ', currentField)
      this.$set(this.fieldArr, rowNum, this.fieldArr[rowNum])
      this.handleSelect(this.selectedPoint, newField)
      this.isChildDragAdd = false
    },

    /**
     * 拖动 list
     */
    dragListEnd(evt) {
      console.log('drag list')
      // this.selectedPoint[0] = evt.newIndex
      this.selectedPoint.splice(0, 1, evt.newIndex)
    },

    /**
     * 字段操作
     * @param action {String} 动作
     * @param point {Array} 字段的坐标
     */
    handleAction(action, point) {
      switch (action) {
        case 'top':
          this.handleActionMoveTop(point)
          break
        case 'bottom':
          this.handleActionMoveBottom(point)
          break
        case 'left':
          this.handleActionExchange(point, -1)
          break
        case 'right':
          this.handleActionExchange(point, 1)
          break
        case 'copy':
          this.handleActionCopy(point)
          break
        case 'delete':
          this.handleDelete(point)
      }
    },

    /**
     * 上移字段
     * @param point {Array} 字段的坐标
     */
    handleActionMoveTop(point) {
      const row = this.fieldArr[point[0] - 1]
      if (!row || row.length === 4) return
      const field = this.fieldArr[point[0]][point[1]]
      const topField = row[0]
      if (
        field.formType === 'detail_table' ||
        topField.formType === 'detail_table' ||
        field.formType === 'field_group' ||
        topField.formType === 'field_group' ||
        field.formType === 'rich_text_format' ||
        topField.formType === 'rich_text_format'
      ) {
        [this.fieldArr[point[0] - 1], this.fieldArr[point[0]]] = [this.fieldArr[point[0]], this.fieldArr[point[0] - 1]]
        this.handleSelect([point[0] - 1, 0])
      } else {
        // 给新行追加字段
        row.push(objDeepCopy(field))
        let config = this.getWidth(row.length)
        row.forEach(o => {
          o.stylePercent = config.stylePercent
        })
        this.$set(this.fieldArr, point[0] - 1, row)

        // 把字段从原来的行中删除
        const oldRow = this.fieldArr[point[0]]
        oldRow.splice(point[1], 1)
        if (oldRow.length === 0) {
          this.fieldArr.splice(point[0], 1)
        } else {
          config = this.getWidth(oldRow.length)
          oldRow.forEach(o => {
            o.stylePercent = config.stylePercent
          })
          this.$set(this.fieldArr, point[0], oldRow)
        }
        this.handleSelect([point[0] - 1, row.length - 1])
      }
    },

    /**
     * 下移字段
     * @param point {Array} 字段的坐标
     */
    handleActionMoveBottom(point) {
      const field = this.fieldArr[point[0]][point[1]]
      const bottomField = this.fieldArr[point[0] + 1][0]
      const row = this.fieldArr[point[0]]

      if (
        field.formType === 'detail_table' ||
        bottomField.formType === 'detail_table' ||
        field.formType === 'field_group' ||
        bottomField.formType === 'field_group' ||
        row.length === 1
      ) {
        [this.fieldArr[point[0] + 1], this.fieldArr[point[0]]] = [this.fieldArr[point[0]], this.fieldArr[point[0] + 1]]
        this.handleSelect([point[0] + 1, 0])
      } else {
        field.stylePercent = 100
        // 把字段放到新行
        this.fieldArr.splice(point[0] + 1, 0, [field])
        // 把字段从原来的行删除
        this.fieldArr[point[0]].splice(point[1], 1)
        // 修改原来行的字段占比
        const config = this.getWidth(row.length)
        row.forEach(o => {
          o.stylePercent = config.stylePercent
        })
        this.$set(this.fieldArr, point[0], row)
        this.handleSelect([point[0] + 1, 0])
      }
    },

    /**
     * 左右移动交换位置
     * @param point {Array} 字段的坐标
     * @param step {Number} 1 向右移动 -1 向左移动
     */
    handleActionExchange(point, step) {
      const row = this.fieldArr[point[0]]
      const field = this.fieldArr[point[0]][point[1]]
      row.splice(point[1], 1)
      row.splice(point[1] + step, 0, field)
      this.handleSelect([point[0], point[1] + step])
    },

    /**
     * 拷贝字段
     * @param point {Array} 字段的坐标
     */
    handleActionCopy(point) {
      const field = this.fieldArr[point[0]][point[1]]
      const copyField = objDeepCopy(field)
      delete copyField.fieldId
      delete copyField.fieldName
      delete copyField.relevant
      copyField.fieldType = 0
      copyField.operating = this.moduleType === 'crm_marketing' ? getDefaultOperating(253, 9) : getDefaultOperating(255, 9)
      if (copyField.formType === 'pic') {
        // 11101011
        copyField.operating = getDefaultOperating(235, 9)
      }
      if (copyField.formType === 'desc_text') {
        copyField.name = ''
      }
      if (copyField.formType === 'serial_number') {
        copyField.operating = getDefaultOperating(245, 9)
      }

      // 限制添加一个标签字段
      if (copyField.formType == 'field_tag') {
        let isPass = true
        this.fieldArr.forEach(item => {
          item.forEach(subItem => {
            if (subItem.formType == copyField.formType) {
              isPass = false
            }
          })
        })
        if (!isPass) {
          this.$message.error('只允许添加一个自定义标签字段')
          return
        }
      }
      if (copyField.formType === 'field_tag') {
        copyField.operating = getDefaultOperating(249, 9)
      }
      if (copyField.formType === 'field_attention') {
        copyField.operating = getDefaultOperating(250, 9)
        copyField.defaultValue = null
      }
      if (copyField.formType === 'field_group') {
        copyField.operating = getDefaultOperating(224, 9)
      }

      copyField.formAssistId = this.generateFormAssistId() // 辅助id
      this.fieldArr.splice(point[0] + 1, 0, [copyField])
      this.handleSelect([point[0] + 1, point[1]])
    },

    /**
     * @description: 根据实际值处理为真实值
     * @param {*} operating
     * @return {*}
     */
    getFieldOperating(operating) {
      if (this.moduleType === 'crm_marketing') {
        const binaryStr = operating.toString(2).padStart(9, '0')
        const binaryStrs = binaryStr.split('')
        binaryStrs.splice(6, 1, '0') //  6 禁止设置隐藏 替换为 0
        return getDefaultOperating(parseInt(binaryStrs.join(''), 2), 9)
      }
      return getDefaultOperating(operating, 9)
    },

    /**
     * 修改字段占比
     */
    handleUpdateFieldWidth() {
      console.log('update width')
      const row = this.fieldArr[this.selectedPoint[0]]

      // 本行占比大于100% 溢出到下一行
      const arr = []
      let child = [] // 行
      let totalWidth = 0
      for (let i = 0; i < row.length; i++) {
        const item = row[i]
        totalWidth += item.stylePercent
        if (totalWidth < 100) {
          // 长度小于 100%
          child.push(item)
        } else if (totalWidth > 100) {
          // 长度大于 100%
          arr.push(objDeepCopy(child))
          child = []
          child.push(item)
          totalWidth = item.stylePercent
        } else {
          // 长度等于 100%
          child.push(item)
          arr.push(objDeepCopy(child))
          child = []
          totalWidth = 0
        }
      }
      if (child.length > 0) {
        arr.push(child)
      }

      // 如果从一行变成多行
      if (arr.length > 1) {
        let rowNum = this.selectedPoint[0] // 行坐标
        let columnNum = this.selectedPoint[1] // 列坐标
        this.fieldArr.splice(rowNum, 1, ...arr)
        let step = 0
        for (let i = 0; i < arr.length; i++) {
          step += arr[i].length
          if (step >= columnNum + 1) {
            rowNum += i
            columnNum = columnNum - step + arr[i].length
            break
          }
        }
        this.handleSelect([rowNum, columnNum])
      }
    },

    getWidth(length) {
      if (length === 1) return { stylePercent: 100 }
      if (length === 2) return { stylePercent: 50 }
      if (length > 2) return { stylePercent: 25 }
    },

    /**
     * 删除字段
     * @param point {Array} 字段的坐标
     */
    handleDelete(point) {
      this.$confirm('确定删除该自定义字段吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.selectedPoint = [null, null]
          this.selectedField = null

          this.fieldArr[point[0]].splice([point[1]], 1)
          // 如果当前行已经没有元素则删除
          if (this.fieldArr[point[0]].length === 0) {
            this.fieldArr.splice(point[0], 1)
          }
        })
        .catch(() => {})
    },

    /**
     * 字段列表点击选择
     * @param point {Array} 字段的坐标
     * @param field {Object} 字段
     */
    handleSelect(point, field = null) {
      console.log('index click: ', point, this.fieldArr)
      this.selectedPoint = point
      this.selectedField = field || this.fieldArr[point[0]][point[1]]
    },

    /**
     * 编辑(明细表格)内部字段
     * @param field {Object} 为空时表明为 clickoutside
     */
    handleChildEdit(field = null) {
      if (field) {
        this.selectedField = field
      } else {
        this.handleSelect(this.selectedPoint)
      }
    },

    /**
     * 生成(明细表格)内部字段fieldName
     * @param list {Array<Object>} 全部的内部字段，防止fieldName重复
     */
    generateFieldName(list) {
      const arr = list.map(o => o.fieldName)
      const generateStr = function(len) {
        const lib = 'abcdefghijklmnopqrstuvwxyz'
        let str = ''
        for (let i = 0; i < len; i++) {
          const index = Math.ceil(Math.random() * 25)
          str += lib[index]
        }
        const res = 'field_' + str
        if (arr.includes(res)) {
          return generateStr(len)
        } else {
          return res
        }
      }
      return generateStr(6)
    },

    /**
     * 保存
     */
    handleSave() {
      return new Promise((resolve, reject) => {
        if (this.rejectHandle) {
          reject()
          return
        }
        const arr = []
        this.loading = true
        // 追加坐标
        objDeepCopy(this.fieldArr).forEach((father, fatherIndex) => {
          father.forEach((child, childIndex) => {
            arr.push({
              ...child,
              formPosition: `${fatherIndex},${childIndex}`
            })
          })
        })

        const limitFields = 'select|update|union|and|or|delete|insert|trancate|char|substr|ascii|declare|exec|count|master|into|drop|execute'.split('|')
        const names = []
        for (let i = 0; i < arr.length; i++) {
          const item = arr[i]
          const position = item.formPosition.split(',')
          const positionStr = `第${Number(position[0]) + 1}行第${Number(position[1]) + 1}列`
          item.name = (item.name || '').trim()
          if (item.formType !== 'desc_text') {
            if (!item.name) {
              this.$message.error(positionStr + '自定义字段，标识名不能为空')
              this.loading = false
              reject()
              return
            }
            if (limitFields.includes(item.name)) {
              this.$message.error(positionStr + `“${item.name}”字段，标识名与系统字段重复，请使用其他字段！`)
              this.loading = false
              reject()
              return
            }
            if (names.includes(item.name)) {
              this.$message.error(positionStr + `“${item.name}”字段，标识名重复`)
              this.loading = false
              reject()
              return
            }
            if (item.isNull === 1 && item.isHidden === 1) {
              this.$message.error(positionStr + `“${item.name}”字段，不能同时设置必填和隐藏`)
              this.loading = false
              reject()
              return
            }
            if (
              item.formType === 'serial_number' &&
            !this.validateSerialNumberField(item)
            ) {
              this.$message.error(positionStr + `“${item.name}”字段，自定义编号规则有误`)
              this.loading = false
              reject()
              return
            } else if (
              item.formType === 'field_tag' &&
            (!item.setting || !item.setting.length)
            ) {
              this.$message.error(positionStr + `“${item.name}”字段，自定义标签未配置标签项`)
              this.loading = false
              reject()
              return
            }

            if (item.formType === 'detail_table') {
            // 明细表格
              if (isEmpty(item.fieldExtendList)) {
                this.$message.error(positionStr + `“${item.name}”字段，不能为空`)
                this.loading = false
                reject()
                return
              }
              for (let j = 0; j < item.fieldExtendList.length; j++) {
                const o = item.fieldExtendList[j]
                delete o.companyId
                delete o.id
                if (isEmpty(o.defaultValue)) {
                  o.defaultValue = null
                }
                o.name = (o.name || '').trim()
                if (!o.name) {
                  this.$message.error(positionStr + `“${o.name}”字段，标识名不能为空`)
                  this.loading = false
                  reject()
                  return
                }
                if (limitFields.includes(o.name)) {
                  this.$message.error(positionStr + `“${o.name}”字段，标识名与系统字段重复，请使用其他字段！`)
                  this.loading = false
                  reject()
                  return
                }
              }
              const _arr = item.fieldExtendList.map(o => o.name)
              if (_arr.length !== Array.from(new Set(_arr)).length) {
                this.$message.error(positionStr + `“${item.name}”字段，标识名重复`)
                this.loading = false
                reject()
                return
              }
            } else if (item.formType === 'pic') {
              if (!item.maxNumRestrict) {
                this.$message.error(positionStr + `“${item.name}”字段，图片最大上传数量不能为空`)
                this.loading = false
                reject()
                return
              }
            }
            names.push(item.name)
          } else {
          // 描述文字
            if (!isEmpty(item.defaultValue) && item.defaultValue.length > 2000) {
              this.$message.error(positionStr + `“${item.name}”字段，描述文字类型字段最多设置2000字`)
              this.loading = false
              reject()
              return
            }
          }
          if (!item.type) {
            const findRes = this.fieldLibList.find(o => o.formType === item.formType)
            if (findRes) item.type = findRes.type
          }

          if (item.hasOwnProperty('optionsData')) {
            delete item.optionsData
          }
        }

        // 自定义保存请求
        if (this.savePromise) {
          this.savePromise(arr).then(res => {
            this.loading = false
            // this.getFieldList()
            resolve()
          }).catch(() => {
            this.loading = false
            reject()
          })
        } else {
        // 请求参数
          const params = {
            data: arr
          }
          const routerParams = this.$route.params
          if (routerParams.label !== 'none') {
            params.label = routerParams.label
          }
          if (this.moduleType === 'oa_examine') {
            params.categoryId = routerParams.id
          } else if (this.moduleType === 'crm_marketing') {
            params.formId = routerParams.id
          } else if (this.moduleType === 'hrm_employee') {
            params.labelGroup = routerParams.id
          }
          console.log('save, ', params)
          // this.loading = false
          // return

          let request = {
            crm_marketing: crmMarketingFieldHandleAPI,
            oa_examine: oaFieldHandleAPI,
            hrm_employee: hrmConfigSaveFieldAPI
          }[this.moduleType] || customFieldHandleAPI

          if (this.moduleType.startsWith('jxc_')) {
            request = jxcFieldSaveAPI
          }

          request(params)
            .then(() => {
              this.$message({
                type: 'success',
                message: '操作成功'
              })
              this.loading = false
              this.getFieldList()
              resolve()
            })
            .catch(() => {
              this.loading = false
              reject()
            })
        }
      })
    },

    /**
     * 验证自动编号规则
     * @param field
     * @return {boolean}
     */
    validateSerialNumberField(field) {
      if (!field.setting || !field.setting.length) return false
      for (let i = 0; i < field.setting.length; i++) {
        const item = field.setting[i]
        if (item.type === 1) {
          if (!item.startNumber && item.startNumber !== 0) {
            return false
          }
          if (!item.stepNumber) {
            return false
          }
        } else if (!item.value) {
          return false
        }
      }

      return true
    },

    /**
     * 获取添加字段
     */
    getTransformField() {
      filedGetFieldAPI({
        type: 1 // 一维数组
      })
        .then(res => {
          const data = {
            text: [],
            textarea: [],
            select: [],
            checkbox: [],
            number: [],
            floatnumber: [],
            mobile: [],
            email: [],
            date: [],
            datetime: [],
            user: [],
            structure: [],
            boolean_value: [],
            percent: [],
            position: [],
            location: [],
            handwriting_sign: [],
            date_interval: [],
            website: [],
            field_tag: [],
            field_attention: []
          }

          for (let index = 0; index < res.data.length; index++) {
            const element = res.data[index]
            const array = data[element.formType]
            if (array) {
              array.push({
                label: element.name,
                value: element.fieldId
              })
            }
          }

          this.transformData = data
        })
        .catch(() => {})
    },

    /**
     * 返回
     */
    handleCancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/styles/mixin.scss";
@import "./style"
</style>
