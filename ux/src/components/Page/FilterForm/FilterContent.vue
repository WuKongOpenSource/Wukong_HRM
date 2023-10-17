<template>
  <div class="wrapper">
    <ul class="list">
      <li
        v-for="(item, index) in showObj.form"
        :key="index"
        class="list-item">

        <span v-if="item.formType == 'date' || item.formType === 'datetime'">{{ item.name +' ' + getConditionName(item) + `${getDateContent(item)}` }}</span>
        <span v-else-if="item.formType == 'position'">{{ item.name +' “' + `${getPositionContent(item)}` + '”' }}</span>
        <span v-else-if="item.formType == 'boolean_value'">{{ item.name +' ' + getConditionName(item) }}“<el-switch
          :value="item.value"
          disabled
          active-value="1"
          inactive-value="0" />”</span>
        <span v-else-if="item.formType === 'business_cause'">{{ item.name +' “' + getTypesName(item) + getStatusName(item) + '”' }}</span>
        <span v-else-if="item.formType === 'map_address'">{{ `${item.name} ${item.address.state} ${item.address.city} ${item.address.area}` }}</span>
        <span v-else-if="item.formType === 'checkStatus'">{{ item.name +' ' + getConditionName(item)+ getNameValue(item) }}</span>
        <span v-else-if="item.formType === 'dealStatus'">{{ item.name +' ' + getConditionName(item)+ getDealStatus(item) }}</span>
        <span v-else-if="item.formType === 'user' || item.formType === 'single_user'">{{ item.name +' ' + getConditionName(item) +getValueContent(item) }}</span>
        <span v-else-if="item.formType === 'structure'">{{ item.name +' ' + getConditionName(item) + getValueContent(item) }}</span>
        <span v-else-if="item.formType === 'category' && item.value.length > 0">{{ item.name +' “' + item.valueContent + '”' }}</span>
        <span v-else-if="item.formType === 'select' && getSettingValueType(item.setting) != 'string'">{{ item.name +' ' + getConditionName(item) + getNameValue(item) }}</span>
        <span v-else>{{ item.name + ' ' + getConditionName(item) + getValueContent(item) }}</span>
        <i
          class="el-icon-close icon"
          @click="handleDelete(item, index)" />
      </li>
    </ul>
  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'
import AdvancedFilterMixin from '@/mixins/AdvancedFilter'

export default {
  name: 'FilterContent',
  mixins: [AdvancedFilterMixin],
  props: {
    obj: {
      type: Object,
      required: true,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      // 展示信息
      showObj: {}
    }
  },
  computed: {},
  watch: {
    obj: function(val) {
      this.showObj = val
    }
  },
  mounted() {
    this.showObj = this.obj
  },
  methods: {
    /**
     * 删除高级筛选条件
     * @param index
     */
    handleDelete(item, index) {
      this.showObj.obj.splice(index, 1)
      this.showObj.form.splice(index, 1)
      this.$emit('delete', { item: item, index: index, obj: this.showObj })
    },

    /**
     * 获取条件名称
     */
    getConditionName(formItem) {
      return this.getAdvancedFilterOptionName(formItem.type, formItem.formType)
    },

    /**
     * 商机组展示名称
     */
    getTypesName(data) {
      if (data.flowName) {
        const obj = data.typeOption.find(item => {
          return item.flowName === data.flowName
        })
        return obj.flowName || ''
      }
      return ''
    },

    /**
     * 获取name value 对象展示值
     */
    getNameValue(data) {
      if (this.isNullCondition(data)) {
        return ''
      }
      const obj = data.setting.find(item => item.value === data.value)
      return obj ? `“${obj.name || obj.label}”` : ''
    },

    /**
     * 商机阶段展示名称
     */
    getStatusName(data) {
      if (data.settingName) {
        const obj = data.settingList.find(item => {
          return item.settingName === data.settingName
        })
        if (obj.settingName) {
          return '-' + obj.settingName
        }
        return ''
      }
      return ''
    },

    /**
     * 成交状态名称
     */
    getDealStatus(data) {
      if (this.isNullCondition(data)) {
        return ''
      }
      const { value } = data
      if (value == 0) {
        return '“未成交”'
      } else if (value == 1) {
        return '“已成交”'
      }
      return ''
    },

    /**
     * 时间展示
     */
    getDateContent(data) {
      if (this.isNullCondition(data)) {
        return ''
      }

      if (data.type === 14) {
        if (!isEmpty(data.timeType)) {
          return `“${data.timeTypeName}”`
        }
        return `“${data.range.join('-')}”`
      }

      return `“${data.value}”`
    },

    /**
     * 值展示
     */
    getValueContent(data) {
      if (this.isNullCondition(data)) {
        return ''
      }

      if (data.formType == 'number' ||
        data.formType == 'floatnumber' ||
        data.formType == 'percent') {
        if (data.type === 14) {
          return `“${isEmpty(data.min) ? 0 : data.min}-${isEmpty(data.max) ? 0 : data.max}”`
        } else {
          return `“${data.value}”`
        }
      } else if (data.formType == 'user' || data.formType == 'single_user') {
        const dataValue = data.valueContent || data.value
        return `“${dataValue.map(o => this.$getUserName(o)).join('，')}”`
      } else if (data.formType == 'structure') {
        const dataValue = data.valueContent || data.value
        return `“${dataValue.map(o => this.$getDeptName(o)).join('，')}”`
      }

      return `“${data.value}”`
    },

    /**
     * isNull条件
     */
    isNullCondition(data) {
      // 如果是空条件 隐藏值
      return data.condition === 'isNull' || data.condition === 'isNotNull'
    },

    /**
     * 地址展示
     */
    getPositionContent(data) {
      return data.value.filter(item => !isEmpty(item.name)).map(item => item.name).join('/')
    },

    /**
     * 获取setting数据类型
     */
    getSettingValueType(setting) {
      if (setting && setting.length > 0) {
        const value = setting[0]
        return typeof value
      }
      return []
    }
  }
}
</script>

<style scoped lang="scss">
@mixin left() {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

@mixin center() {
  display: flex;
  align-items: center;
  justify-content: center;
}

.wrapper {
  width: 100%;
  min-height: 40px;
  overflow-x: auto;
  color: $--color-text-secondary;
  background: white;

  @include left;

  .list {
    flex-shrink: 0;
    width: 100%;

    @include left;

    .list-item {
      flex-shrink: 0;
      height: 32px;
      padding: 0 $--interval-base;
      margin: 0 #{$--interval-base * 2} 0 0;
      border: $--border-width-medium solid $--border-color-base;
      border-radius: $--border-radius-base;

      @include center;

      .icon {
        margin-left: #{$--interval-base * 2};
        cursor: pointer;
      }
    }
  }
}
</style>
