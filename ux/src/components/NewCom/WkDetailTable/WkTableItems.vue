<template>
  <el-table
    :key="Date.now().toString()"
    :data="fieldFrom"
    class="wk-table-items"
    style="width: 100%;">
    <el-table-column
      label="序号"
      width="50">
      <template slot-scope="{ $index }">
        {{ $index + 1 }}
      </template>
    </el-table-column>
    <el-table-column
      v-for="(item, index) in fieldList"
      v-show="getShowValue(item)"
      :key="index"
      :prop="item.field"
      :min-width="getMinWidth(item.formType)">
      <template slot="header" slot-scope="{}">
        <span v-if="item.isNull == 1" class="red">*</span>{{ item.name }}
      </template>
      <template slot-scope="{$index }">
        <wk-form-item
          :prop-prefix="`${propPrefix || ''}[${$index}].`"
          :item="fieldList[index]"
          :index="$index"
          :field-from="fieldFrom[$index]"
          :disabled="item.disabled || disabled"
          @change="fieldChange"
        >
          <template slot-scope="{ data }">
            <slot :data="data" :index="$index" />
          </template>
        </wk-form-item>
      </template>
    </el-table-column>
    <el-table-column
      v-if="!disabled && !delDisabled"
      :resizable="false"
      fixed="right"
      label="操作"
      width="60">
      <template slot-scope="{ $index }">
        <el-button
          icon="wk wk-icon-bin"
          type="text"
          @click="deleteClick($index)" />
      </template>

    </el-table-column>
  </el-table>
</template>

<script>

export default {
  // table 风格展示事项
  name: 'WkTableItems',

  components: {
    WkFormItem: () => import('../WkForm/WkFormItem')
  },

  props: {
    // 表单验证前缀
    propPrefix: {
      type: String,
      default: ''
    },
    fieldFrom: {
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

  computed: {},

  watch: {},

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    deleteClick(index) {
      this.$emit('delete', index)
    },

    getMinWidth(formType) {
      if (formType === 'date_interval' ||
      formType === 'dateRange' ||
       formType === 'file' ||
       formType === 'location' ||
       formType === 'position') {
        return 250
      }
      return 150
    },

    /**
     * 判断展示
     */
    getShowValue(item) {
      if (item.hasOwnProperty('show')) {
        return item.show
      }
      return true
    },

    /**
     * 字段change
     */
    fieldChange(item, index, value, valueList) {
      this.$emit('change', item, index, value, valueList)
    }
  }
}
</script>

<style lang="scss">
.wk-table-items {
  th {
    line-height: initial;
  }

  .wk-form-item {
    width: auto !important;
    padding: 8px 0 !important;
    margin-bottom: 0 !important;

    .el-form-item__label {
      display: none;
    }
  }
}
</style>

<style lang="scss" scoped>
.red {
  margin-right: 4px;
  color: $--color-danger;
}

.el-table {
  border: none;
}
</style>
