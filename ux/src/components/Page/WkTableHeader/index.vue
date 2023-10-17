<template>
  <div class="wk-table-header">
    <!-- :tabs="sceneList"
      :props="filterHeaderConfig"
      :active-tab="sceneSelectId"
      :selection-list="selectionList"
      :handles="handles"
      @tabs-change="sceneSelect"
      @command="selectionBarClick"
      @on-handle="filterHeaderHandle" -->
    <wk-filter-header
      v-bind="$attrs"
      :props="filterHeaderConfig"
      v-on="$listeners"
      @event-change="filterHeaderHandle"
    >
      <template slot="left-start">
        <slot name="custom" />
      </template>
      <template slot="right">
        <el-button
          v-if="exportFields.length > 0"
          class="collapse-button"
          :type="exportFieldsShow ? 'selected' : 'text'"
          @click="collapseHandle()">
          {{ exportFieldsShow ? '收起筛选' : '展开筛选' }}
          <i :class="['el-icon-arrow-up', { 'is-reverse' : !exportFieldsShow}]" />
        </el-button>
        <el-button
          v-if="config.showFilterView"
          type="subtle"
          class="filter-button"
          :class="{'has-values': filterValidCount > 0}"
          icon="wk wk-screening"
          @click="showFilterClick">高级筛选<span
            v-if="filterValidCount > 0"
            class="values-span"
            closable
            type="info">
            {{ filterValidCount }}<i class="el-icon-close" @click.stop="clearFilterVal(filterObj.form)" />
          </span></el-button>
        <!-- <el-button
          v-if="sortData && sortData.order && sortData.column"
          type="primary"
          plain
          @click="handleCallBack({type: 'clear-sort'})">
          {{ `${sortData.column.label}${{ascending: '升序', descending: '降序'}[sortData.order]}` }}<i style="margin-left: 5px;" class="el-icon-close"/>
        </el-button> -->
        <slot />
      </template>
    </wk-filter-header>

    <filter-export-fields
      v-if="exportFields.length > 0 && exportFieldsShow"
      :condition-type-fun="conditionTypeFun"
      :form="exportFields"
      :field-list="fieldList"
      :props="filterFormProps ? filterFormProps.filterFieldsProps : null"
      @filter="exportFilter"
      @clear="clearFilterVal"
    />

    <!-- 场景 -->
    <scene-set
      :dialog-visible.sync="sceneSetShow"
      :props="sceneSetProps"
      :create-props="sceneCreateProps"
      :filter-fields-props="filterFormProps ? filterFormProps.filterFieldsProps : null"
      :condition-type-fun="conditionTypeFun"
      @save-success="updateSceneList" />
    <!-- :crm-type="crmType" -->

    <!-- 高级筛选 -->
    <filter-form
      v-if="config.showFilterView"
      :field-list="fieldList"
      :dialog-visible.sync="filterShow"
      :obj="filterObj"
      :condition-type-fun="conditionTypeFun"
      :props="filterFormProps"
      @filter="handleFilter" />
      <!-- :save-scene="!isSeas" -->
  </div>
</template>

<script>
/**
 * @description: todo
 * 选择产品 筛选场景隐藏 根据 tabs 数据源来控制
 *
 * 高级筛选
 * show-export 改为了 props
 * save-scene 改为了 props  搜索之后改为 props 逻辑
 *
 * 基础布局 WkFilterHeader
 * showSceneView 改为 tabSetShow
 *
 * props.ignoreFilterFields 兼容
 * $parent 兼容 直接控制父的方法
 *
 */
import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import FilterExportFields from '../FilterForm/FilterExportFields'
import SceneSet from '../SceneForm/SceneSet' // 场景设置
import FilterForm from '../FilterForm'

import merge from '@/utils/merge'
import { isArray } from '@/utils/types'
import AdvancedFilterMixin from '@/mixins/AdvancedFilter'
import { objDeepCopy } from '@/utils'

const DefaultWkTableHeader = {
  showFilterView: true, // 控制是否展示高级筛选
  showExportFields: false // 展示外漏布局 仅指展开闭合
  // tabSetShow: false // 默认不展示场景
}

export default {
  // WkTableHeader
  name: 'WkTableHeader',

  components: {
    WkFilterHeader,
    FilterExportFields,
    SceneSet,
    FilterForm
  },

  mixins: [AdvancedFilterMixin],

  inheritAttrs: false,

  props: {
    props: Object, // TableHeader的辅助配置
    filterHeaderProps: Object, // 基础布局配置
    filterFormProps: Object, // 高级筛选配置
    sceneSetProps: Object, // 场景设置配置
    sceneCreateProps: Object, // 场景设置配置
    conditionTypeFun: Function, // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
    fields: [Array, Function] // 直接提供数据 或者 提供一个返回promise 数据的方法
  },

  data() {
    return {
      filterObj: { form: [], obj: [] }, // 筛选确定数据 form 是展示数组  obj 是上传数据
      exportFieldsShow: false, // 展示外露筛选
      fieldList: [], // 高级筛选字段
      filterShow: false, // 展示高级筛选
      sceneSetShow: false // 展示场景设置
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultWkTableHeader }, this.props || {})
    },

    filterHeaderConfig() {
      return {
        // maxTabCount: this.sceneMaxCount,
        // searchPlaceholder: this.placeholder,
        tabSetShow: true,
        tabSetLabel: '场景设置',
        tabSetCommand: 'sceneSet',
        ...(this.filterHeaderProps || {})
      }
    },

    // 外露搜索字段
    exportFields() {
      if (this.filterObj.form) {
        return this.filterObj.form.filter(item => item.isOut === 1)
      }
      return []
    },

    // 有条件的值
    filterValidCount() {
      const conditions = isArray(this.filterObj.obj) ? this.filterObj.obj : []
      // 有阶段值，有效条件数减1,以为该筛选占用两个值
      const hasBusinessCause = conditions.filter(item => item.formType === 'business_cause' && (item.values && item.values.length > 0)).length === 2
      // 滤掉无效值 5 6 为空 不为空 不需要提供values
      const hasValueConditions = conditions.filter(item => (item.values && item.values.length > 0) || ([5, 6].includes(item.type)))
      return hasBusinessCause ? hasValueConditions.length - 1 : hasValueConditions.length
    }
  },

  watch: {
    // 处理外露展示及其数据获取
    'config.showExportFields': {
      async handler(val) {
        // 如果展示外漏是true fieldList 是方法，需要理解触发获取数据
        // 默认需要获取一下高级筛选。保证页面根据有外露值正常展示
        if (this.config.showFilterView && (!isArray(this.fieldList) || this.fieldList.length === 0)) {
          this.initFieldList()
          this.exportFieldsShow = val
        }
      },
      immediate: true
    }
  },

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /* ------------------------ 基础展示组件 ------------------------ */
    /**
     * @description: 操作事件
     * @param {*}
     * @return {*}
     */
    filterHeaderHandle(commond, value) {
      if (commond === this.filterHeaderConfig.tabSetCommand) {
        this.sceneSetShow = true
      }
    },

    /* ------------------------ 外露筛选 ------------------------ */
    /**
     * 外露筛选折叠操作
     */
    collapseHandle(show) {
      const exportFieldsShow = show !== undefined ? show : !this.exportFieldsShow
      if (exportFieldsShow !== this.exportFieldsShow) {
        this.exportFieldsShow = exportFieldsShow
        //  true：是折叠不请求数据 更新table高度
        this.$emit('event-change', 'export-fields-collapse', this.exportFieldsShow)
      }
      // this.$emit('filter-change', {}, true)
      // this.updateFilterConfig()
    },

    /**
     * 外露筛选
     */
    exportFilter(data) {
      // 使用外露筛选  更新 obj 里的内容， form 的内容会双向联动
      this.filterObj.obj = data
      this.$emit('filter-change', data)
    },

    /**
     * 清空外露筛选值
     */
    clearFilterVal(list) {
      this.filterObj.obj = []
      // 下面方法 重置 form里的值
      this.resetAdvancedFilterFieldsValue(list)
      this.$emit('filter-change', [])
    },

    // /**
    //  * 更新style样式
    //  */
    // updateFilterConfig() {
    //   this.$parent.updateTableStyle({
    //     filterConfig: {
    //       exportFieldsShow: this.exportFieldsShow
    //     }
    //   })
    // },

    /* ------------------------ 高级筛选 ------------------------ */
    /**
     * 展示高级筛选
     */
    async showFilterClick() {
      if (!isArray(this.fieldList) || this.fieldList.length === 0) {
        await this.initFieldList()
      } else {
        this.fieldList = await this.getFieldList()
      }
      this.filterShow = true
    },

    /**
     * @description: 第一次载入时，也就是 fieldList 无数据，需处理外漏数据
     * @param {*}
     * @return {*}
     */
    async initFieldList() {
      const fields = await this.getFieldList()
      const exportFields = []
      fields.forEach(item => {
        if (item.isOut === 1) {
          const copyItem = objDeepCopy(item)
          exportFields.push(this.getAdvancedFilterDefaultItemByFormType(copyItem, item, this.conditionTypeFun))
        }
      })
      this.fieldList = fields
      // 将外漏的字段，交给高级筛选
      this.filterObj.form = exportFields
    },

    /**
     * 获取高级筛选字段
     */
    getFieldList() {
      return new Promise((resolve) => {
        if (isArray(this.fields)) {
          resolve(this.fields)
        } else if (this.fields instanceof Function) {
          this.fields().then(data => {
            resolve(data)
          })
        }
      })
    },

    /**
     * 高级筛选确定
     * isExport 是外露点击 不发送请求, 主要通知父更新高度
     * saveChecked 保存默认场景
     */
    handleFilter(form, isExport) {
      this.filterObj = form
      if (isExport) {
        if (!this.exportFieldsShow) {
          this.collapseHandle(true)
        }
      } else {
        this.filterShow = false
        // 保存场景
        if (form.saveChecked) {
          this.sceneCreateProps.saveRequest({
            // type: crmTypeModel[this.crmType],
            isDefault: form.saveDefault ? 1 : 0,
            name: form.saveName,
            data: JSON.stringify(form.obj),
            ...this.sceneCreateProps.saveParams
          })
            .then(res => {
              this.updateSceneList()
            })
            .catch(() => {})
        }
        this.$emit('filter-change', form.obj)
      }
    },

    /**
     * @description: 刷新场景
     * @param {*}
     * @return {*}
     */
    updateSceneList() {
      this.$emit('event-change', 'scene-refresh')
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-table-header {
  // 高级筛选
  .filter-button {
    &.has-values {
      background-color: $--color-n30;
    }
  }

  // 有效值
  .values-span {
    margin-left: 2px;
    color: $--color-primary;
    background-color: $--color-n40;
    border-radius: $--border-radius-base;

    .el-icon-close {
      margin-right: 2px;
      font-size: 12px;
      color: $--color-black;
      cursor: pointer;

      &:hover {
        color: $--color-primary;
      }
    }
  }

  // 筛选展开闭合
  .collapse-button {
    padding-right: 12px;
    padding-left: 12px;

    .el-icon-arrow-up {
      transition: transform 0.3s;
    }

    .el-icon-arrow-up.is-reverse {
      transform: rotate(180deg);
    }
  }

  // 外露筛选
  .filter-export-fields {
    margin-top: 8px;
  }
}
</style>
