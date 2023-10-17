<template>
  <div class="wk-detail-table">
    <template v-if="showType === 'default'">
      <div
        v-for="(_, index) in fieldList"
        :key="index"
        class="detail-item">
        <flexbox class="detail-item__head">
          <div class="detail-item__head-title">{{ title }}（{{ index+1 }}）</div>
          <el-button
            v-if="fieldList.length > 1 && !delDisabled"
            icon="wk wk-icon-bin"
            type="text"
            @click="deleteClick(index)" />
        </flexbox>
        <wk-form-items
          class="detail-item__body"
          :field-from="fieldForm[index]"
          :field-list="addFields"
          :prop-prefix="`${propPrefix || ''}[${index}].`"
          :disabled="disabled"
          @change="formChange"
        />
        <div class="add-btn">
          <el-button v-if="!disabled && !addDisabled" type="text" @click="addClick(index)">
            <i class="wk wk-l-plus" />
            {{ btnName }}
          </el-button>
        </div>
      </div>
    </template>
    <div
      v-else-if="showType === 'table'"
      class="detail-item">
      <wk-table-items
        :field-from="fieldForm"
        :field-list="addFields"
        :prop-prefix="propPrefix"
        :disabled="disabled"
        :add-disabled="addDisabled"
        :del-disabled="delDisabled"
        @delete="deleteClick"
        @change="formChange"
      />
      <div class="add-btn">
        <el-button v-if="!disabled && !addDisabled" type="text" @click="addClick">
          <i class="wk wk-l-plus" />
          {{ btnName }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import WkTableItems from './WkTableItems'

import { objDeepCopy } from '@/utils'
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // 明细表格
  name: 'WkDetailTable',

  components: {
    WkFormItems: () => import('../WkForm/WkFormItems'),
    WkTableItems
  },

  mixins: [Emitter],

  props: {
    title: String,
    showType: {
      type: String,
      default: 'defalut' // defalut table
    },
    propPrefix: String,
    btnName: String,
    addFieldList: Array,
    addFieldForm: Object,
    fieldForm: {
      type: Array,
      default: () => {
        return []
      }
    },
    fieldList: {
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: Boolean, // 总禁用
    addDisabled: { // 添加 和 删除 按钮是否禁用
      type: Boolean,
      default: false
    },
    delDisabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
    }
  },

  computed: {
    addFields() {
      if (this.addFieldList) {
        this.addFieldList.forEach(item => {
          if (item.hasOwnProperty('authLevel')) {
            item.disabled = item.authLevel != 3 // 如果添加单元里有权限，填充权限 disabled
          }
        })
      }
      return this.addFieldList
    }
  },

  watch: {},

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    formChange(item, index, value, valueList) {
      this.$emit('change', item, index, value, valueList)
      this.dispatch('ElFormItem', 'el.form.change', this.fieldForm)
    },

    addClick(index) {
      if (isNaN(index)) {
        this.fieldList.push(objDeepCopy(this.addFields))
        this.fieldForm.push(objDeepCopy(this.addFieldForm))
      } else {
        this.fieldList.splice(index + 1, 0, objDeepCopy(this.addFields))
        this.fieldForm.splice(index + 1, 0, objDeepCopy(this.addFieldForm))
      }
    },

    deleteClick(index) {
      this.fieldList.splice(index, 1)
      this.fieldForm.splice(index, 1)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-detail-table {
  font-size: 14px;
  line-height: inherit;

  .wk-form-items {
    padding: 0;
  }

  .detail-item {
    font-size: $--font-size-base;
    color: $--form-field-default-text-color;
    border: $--border-medium;
    border-radius: $--border-radius-base;

    &__head {
      padding: 10px 20px;
      background-color: #f5f5f5;

      &-title {
        flex: 1;
        height: auto;
        line-height: normal;
        color: $--color-text-primary;
      }

      .el-button {
        padding: 0;
      }
    }

    &__body {
      padding-bottom: 12px;
    }
  }

  .detail-item + .detail-item {
    margin-top: 10px;
  }

  .add-btn {
    padding-right: 10px;
    text-align: right;

    .wk-l-plus {
      font-size: 12px;
    }
  }
}
</style>
