<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-detail-table"
    @click="emitClick"
    @action="handleAction">

    <div
      :class="{'is-empty': isEmpty}"
      class="box">
      <draggable
        :list="list"
        :options="dragListConfig"
        :class="{'is-table': field.precisions === 2}"
        class="field-list"
        @end="dragListEnd"
        @add="dragAdded">
        <div v-if="isEmpty" class="empty-box">
          <div class="empty-box-title">可拖拽添加多个字段</div>
          <div class="empty-box-desc">（不支持明细中添加明细字段）</div>
        </div>

        <template v-if="!isEmpty && field.precisions === 1">
          <component
            :is="field | typeToComponentName"
            v-for="(field, index) in list"
            :key="index"
            :field="field"
            :point="[index, 0]"
            :active-point="[null, null]"
            class="draggable-hook"
            @click="emitClick" />
        </template>
        <template v-if="!isEmpty && field.precisions === 2">
          <el-table
            :data="tableData"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            style="width: 100%;">
            <el-table-column
              v-for="(field, index) in list"
              :key="index"
              :prop="field.fieldName"
              :label="field.name">
              <template slot-scope="{ row, column }">
                <div class="input-box">
                  {{ row[column.property] }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right">
              <template slot-scope="{}">
                <el-button>删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </draggable>

      <div v-if="!isEmpty" class="add-btn">
        <el-button type="text">
          <i class="wk wk-l-plus" />
          {{ field.remark || '' }}
        </el-button>
      </div>
    </div>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import draggable from 'vuedraggable'

import mixins from './mixins'
import { isEmpty } from '@/utils/types'
import { typeToComponent } from '../../utils'

export default {
  name: 'FieldDetailTable',
  components: {
    draggable,
    FieldWrapper,

    'FieldInput': () => import('./FieldInput'),
    'FieldTextarea': () => import('./FieldTextarea'),
    'FieldSelect': () => import('./FieldSelect'),
    'FieldCheckbox': () => import('./FieldCheckbox'),
    'FieldFile': () => import('./FieldFile'),
    'FieldBoolean': () => import('./FieldBoolean'),
    'FieldPercent': () => import('./FieldPercent'),
    'FieldPosition': () => import('./FieldPosition'),
    'FieldLocation': () => import('./FieldLocation'),
    'FieldWritingSign': () => import('./FieldWritingSign'),
    'FieldDateInterval': () => import('./FieldDateInterval'),
    'FieldDescText': () => import('./FieldDescText')
  },
  filters: {
    /** 根据type 找到组件 */
    typeToComponentName(item) {
      return typeToComponent(item)
    }
  },
  mixins: [mixins],
  data() {
    return {
      dragListConfig: {
        delay: 50,
        group: {
          name: 'childList',
          put: ['libList'],
          pull: false
        },
        sort: false,
        forceFallback: true,
        fallbackClass: 'draggingStyle',
        filter: '.empty-box'
      },
      selectedPoint: [null, null]
    }
  },
  computed: {
    isEmpty() {
      return isEmpty(this.field.fieldExtendList)
    },
    isList() {
      return true
    },
    list() {
      return this.isEmpty ? [] : this.field.fieldExtendList
    },
    tableData() {
      const obj = {}
      this.list.forEach(item => {
        obj[item.fieldName] = this.formatterDefaultValue(item)
      })
      return [obj]
    }
  },
  methods: {
    dragListEnd(evt) {
      // console.log('table drag list end', evt)
    },
    /**
     * 拖拽派发新增事件
     * @param evt
     */
    dragAdded(evt) {
      this.$emit('child-drag-add', this.point, evt)
      this.$nextTick(() => {
        this.selectedPoint = [evt.newIndex, 0]
      })
    },

    formatterDefaultValue(data) {
      if (!data.defaultValue) return ''
      if (data.formType === 'boolean_value') {
        return { 0: '不选中', 1: '选中' }[data.defaultValue]
      } else if (data.formType === 'date_interval') {
        return data.defaultValue.join('-')
      } else if (data.formType === 'checkbox') {
        return data.defaultValue.join(',')
      } else if (typeof data.defaultValue === 'string') {
        return data.defaultValue
      }
    }
  }
}
</script>

<style scoped lang="scss">
.box {
  width: 100%;
  padding: 5px;
  font-size: 14px;
  background-color: white;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  &.is-empty {
    background-color: #f7f8fa;
    border: unset;
  }

  .add-btn {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 10px;

    .wk-l-plus {
      font-size: 12px;
    }
  }

  .empty-box {
    width: 100%;
    padding: 25px 0;
    text-align: center;
    background-color: #f7f8fa;

    .empty-box-title {
      color: $--color-text-primary;
    }

    .empty-box-desc {
      margin-top: 5px;
      font-size: 12px;
      color: $--color-text-placeholder;
    }
  }

  .field-list {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    justify-content: flex-start;

    &.is-table {
      flex-wrap: nowrap;
      overflow-x: auto;
    }
  }

  .input-box {
    width: 100%;
    height: 30px;
    padding: 3px 0;
    border: $--border-medium;
  }

  .table-field {
    width: 200px;

    ::v-deep .field-item {
      padding-bottom: 0;

      .field-item_title {
        padding-left: 10px;
        border: $--border-medium;
      }

      .field-item_body {
        padding: 10px 5px;
      }
    }
  }
}
</style>
