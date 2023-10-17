<template>
  <el-dialog
    :visible.sync="visible"
    :before-close="handleClose"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :title="title"
    custom-class="no-padding-dialog fields-dialog"
    append-to-body
    fullscreen
    lock-scroll>
    <flexbox
      v-loading="loading"
      align="flex-start"
      justify="flex-start"
      class="fields-index body">
      <flexbox-item class="body-left">
        <div class="body-left_title">字段库</div>
        <ul>
          <draggable
            :list="fieldLibList"
            :options="dragLeftConfig"
            :clone="dragLeftMove"
            class="lib-wrapper"
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
      </flexbox-item>

      <div class="body-content">
        <flexbox
          align="flex-start"
          justify="flex-start"
          direction="column"
          class="body-content-warp">
          <el-header>
            <div class="title">编辑字段</div>
            <div>
              <el-button
                v-debounce="handleSave"
                type="primary">保存</el-button>
              <el-button @click="handleCancel">返回</el-button>
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

      <flexbox-item style="margin-left: 0;" class="body-right">
        <setting-field
          v-if="selectedField"
          :field="selectedField"
          :point="selectedPoint"
          :field-arr="fieldArr"
          :can-transform="canTransform"
          :transform-data="transformData"
          :show-logic="false"
          @child-edit="handleChildEdit"
          @update-width="handleUpdateFieldWidth" />
      </flexbox-item>
    </flexbox>
  </el-dialog>
</template>

<script>
import { filedGetFieldAPI } from '@/api/crm/common'
import { isEmpty } from '@/utils/types'
import { getDefaultOperating } from '@/views/admin/fields/utils'

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
} from '@/views/admin/fields/components/FieldItem'
import SettingField from '@/views/admin/fields/components/SettingField'

import draggable from 'vuedraggable'

import Field from '@/views/admin/fields/field'
import FieldTypeLib from '@/views/admin/fields/fieldTypeLib'
import { objDeepCopy } from '@/utils/index'
import { typeToComponent } from '@/views/admin/fields/utils'

export default {
  name: 'FieldsDialog',
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
    visible: {
      type: Boolean,
      default: false
    },
    rowData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    rowIndex: Number
  },
  data() {
    return {
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
    title() {
      return `配置 阶段${this.rowIndex + 1} ${this.rowData.name} 表单`
    },
    // 能转移
    canTransform() {
      const rowNum = this.selectedPoint[0]
      const columnNum = this.selectedPoint[1]
      if (!isEmpty(rowNum) && !isEmpty(columnNum)) {
        const currentField = this.fieldArr[rowNum][columnNum]
        if (currentField.formType === 'detail_table') return false
      }
      return false
    }
  },
  created() {
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
      this.fieldLibList = FieldTypeLib.filter(item => {
        return ![
          'pic',
          'detail_table',
          'field_group',
          'field_attention',
          'serial_number',
          'field_tag',
          'handwriting_sign',
          'rich_text_format'
        ].includes(item.formType)
      })
      this.getFieldList()
    },

    /**
     * 获取字段列表
     */
    getFieldList() {
      this.loading = true
      this.fieldArr = this.rowData.formList || []
      // 一维字段数组数据
      const fieldData = []
      this.fieldArr.forEach(item => {
        item.forEach(subItem => {
          if (subItem) {
            const binaryStr = subItem.operating.toString(2).padStart(9, '0')
            // 如果数据开启必填，进行关闭
            if (binaryStr.charAt(5) == '1') {
              const strAry = binaryStr.split('')
              strAry[5] = '0'
              subItem.operating = strAry.join('')
            }
          }

          fieldData.push(subItem)
        })
      })

      // fieldData.forEach(item => {
      //   // 自定义编号
      //   if (item.formType == 'serial_number') {
      //     item.setting.forEach(subItem => {
      //       if (subItem.type == 3) {
      //         fieldData.forEach(thirdItem => {
      //           if (subItem.value == thirdItem.name) {
      //             subItem.value = thirdItem.formAssistId
      //           }
      //         })
      //       }
      //     })
      //   }
      // })

      if (this.fieldArr.length > 0) {
        this.handleSelect([0, 0])
      }
      this.rejectHandle = false
      this.loading = false
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

      // 限制添加一个标签字段
      if (this.movedField.formType == 'field_tag') {
        let isPass = true
        this.fieldArr.forEach(item => {
          item.forEach(subItem => {
            if (subItem.formType == this.movedField.formType) {
              isPass = false
            }
          })
        })
        if (!isPass) {
          this.$message.error('只允许添加一个自定义标签字段')
          return
        }
      }

      const newField = new Field({
        name: this.movedField.name,
        formType: this.movedField.formType
      })
      newField.stylePercent = 50 // 都50
      newField.operating = getDefaultOperating(235, 9) // 关闭字段占比修改 唯一 11101011
      // if (this.$route.params.label !== 'none') {
      //   newField.label = this.$route.params.label
      // }

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
          newField.operating = getDefaultOperating(232, 9)
          newField.fieldExtendList = []
          newField.defaultValue = null
          newField.remark = `添加${newField.name}`
          break
        case 'serial_number':
          newField.operating = getDefaultOperating(245, 9)
          newField.isUnique = 1
          newField.setting = [
            { type: 1, resetType: 4, startNumber: undefined, stepNumber: undefined }
          ]
          break
        case 'field_tag':
          newField.operating = getDefaultOperating(249, 9)
          break
        case 'field_attention':
          newField.operating = getDefaultOperating(250, 9)
          newField.defaultValue = null
          break
        case 'field_group':
          newField.operating = getDefaultOperating(224, 9)
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
          'field_group'
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
      console.log(rowNum)
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
      const findRes = FieldTypeLib.find(o => newField.formType === o.formType)
      if (findRes) newField.type = findRes.type

      // const rowNum = evt.newIndex
      const rowNum = this.selectedPoint[0]
      const columnNum = this.selectedPoint[1]
      const currentField = this.fieldArr[rowNum][columnNum]
      if (isEmpty(currentField.fieldExtendList)) {
        currentField.fieldExtendList = []
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
      if (!row || row.length >= 2) return
      const field = this.fieldArr[point[0]][point[1]]
      const topField = row[0]
      if (field.formType === 'detail_table' || topField.formType === 'detail_table' || field.formType === 'field_group' || topField.formType === 'field_group') {
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

      if (field.formType === 'detail_table' || bottomField.formType === 'detail_table' || field.formType === 'field_group' || bottomField.formType === 'field_group' || row.length === 1) {
        [this.fieldArr[point[0] + 1], this.fieldArr[point[0]]] = [this.fieldArr[point[0]], this.fieldArr[point[0] + 1]]
        this.handleSelect([point[0] + 1, 0])
      } else {
        field.stylePercent = 50
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
      copyField.operating = getDefaultOperating(239, 9)// 关闭字段占比修改
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
      this.fieldArr.splice(point[0] + 1, 0, [copyField])
      this.handleSelect([point[0] + 1, point[1]])
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
      if (length === 1) return { stylePercent: 50 }
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
      console.log('index click: ', point)
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
      if (this.rejectHandle) return

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

      const allFormAssistId = arr.map(o => o.formAssistId)

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
            return
          }
          if (limitFields.includes(item.name)) {
            this.$message.error(positionStr + `“${item.name}”字段，标识名与系统字段重复，请使用其他字段！`)
            this.loading = false
            return
          }
          if (names.includes(item.name)) {
            this.$message.error(positionStr + `“${item.name}”字段，标识名重复`)
            this.loading = false
            return
          }
          if (item.isNull === 1 && item.isHidden === 1) {
            this.$message.error(positionStr + `“${item.name}”字段，不能同时设置必填和隐藏`)
            this.loading = false
            return
          }

          if (item.formType === 'detail_table') {
            // 明细表格
            if (isEmpty(item.fieldExtendList)) {
              this.$message.error(positionStr + `“${item.name}”字段，不能为空`)
              this.loading = false
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
                return
              }
              if (limitFields.includes(o.name)) {
                this.$message.error(positionStr + `“${o.name}”字段，标识名与系统字段重复，请使用其他字段！`)
                this.loading = false
                return
              }
            }
            const _arr = item.fieldExtendList.map(o => o.name)
            if (_arr.length !== Array.from(new Set(_arr)).length) {
              this.$message.error(positionStr + `“${item.name}”字段，标识名重复`)
              this.loading = false
              return
            }
          }
          names.push(item.name)
        } else {
          // 描述文字
          if (!isEmpty(item.defaultValue) && item.defaultValue.length > 2000) {
            this.$message.error(positionStr + `“${item.name}”字段，描述文字类型字段最多设置2000字`)
            this.loading = false
            return
          }
        }
        if (!item.type) {
          const findRes = FieldTypeLib.find(o => o.formType === item.formType)
          if (findRes) item.type = findRes.type
        }

        // 自定义编号
        if (item.formType == 'serial_number') {
          item.setting.forEach(subItem => {
            if (subItem.type == 3) {
              if (subItem.value === -100) {
                subItem.value = 'createTime'
              } else if (!allFormAssistId.includes(subItem.value)) {
                subItem.value = ''
              } else {
                arr.forEach(thirdItem => {
                  if (thirdItem.formAssistId == subItem.value) {
                    subItem.value = thirdItem.name
                  }
                })
              }
            }
          })
        }

        if (item.hasOwnProperty('optionsData')) {
          delete item.optionsData
        }
      }

      // 请求参数
      const params = this.fieldArr
      console.log('save, ', params)
      this.$emit('change', params)
      this.loading = false
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
            date_interval: []
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
      this.$emit('update:visible', false)
    },
    /**
       * 关闭弹窗
       */
    handleClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/styles/mixin.scss";
@import "../../fields/style";

::v-deep .el-dialog {
  .el-dialog__body {
    height: calc(100% - 50px);
  }
}

.fields-index {
  border-radius: $--border-radius-base;
}
</style>
