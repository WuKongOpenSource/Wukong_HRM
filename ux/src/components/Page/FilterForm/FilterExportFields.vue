<template>
  <div class="filter-export-fields">
    <div class="title">筛选条件</div>
    <filter-fields
      :condition-type-fun="conditionTypeFun"
      :form="form"
      :field-list="fieldList"
      :props="props"
      is-export
    />
    <div class="filter-export-fields__handle">
      <el-button type="primary" @click="filterClick">筛选</el-button>
      <el-button @click="clearClick">清空外露筛选值</el-button>
    </div>
  </div>
</template>

<script>
import FilterFields from './FilterFields'

import { getFilterPostData } from './utils'

export default {
  // 筛选外露字段
  name: 'FilterExportFields',

  components: {
    FilterFields
  },

  props: {
    conditionTypeFun: Function, // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
    form: Array, // 展示的字段数组
    fieldList: Array, // 字段数据
    props: Object // 目前和 FilterFields 一致
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 筛选
     */
    filterClick() {
      this.$emit('filter', getFilterPostData(this.form))
    },

    /**
     * 清楚筛选
     */
    clearClick() {
      this.$emit('clear', this.form)
    }
  }
}
</script>

<style lang="scss" scoped>
.filter-export-fields {
  padding: 16px;
  box-shadow: $--box-shadow-bottom;

  &__handle {
    margin-top: 10px;
    font-size: 0;
  }

  > .title {
    color: $--color-n300;
  }
}

::v-deep .filter-fields {
  display: flex;
  flex-wrap: wrap;

  .el-row {
    flex: 0 0 50%;
    flex-shrink: 0;
  }

  .el-col-1 {
    width: $--interval-base;
  }
}
</style>
