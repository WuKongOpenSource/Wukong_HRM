<template>
  <flexbox class="xr-breadcrumb">
    <span
      v-for="(item, index) in showValue"
      :key="index"
      class="xr-breadcrumb__item">
      <template v-if="item.type == 'all'">
        <el-dropdown
          trigger="click"
          @command="dropdownCommand">
          <i class="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(subItem, subIndex) in value"
              :key="subIndex"
              :command="subIndex">{{ subItem.label }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
      <span
        v-else
        :class="{'is-current': currentIndex == index}"
        class="xr-breadcrumb__inner"
        @click="changeClick(item, index)">{{ item.label }}</span>
      <i v-if="currentIndex != index" class="xr-breadcrumb__separator el-icon-arrow-right" />
    </span>
  </flexbox>
</template>

<script>

export default {
  name: 'XrBreadcrumb',
  components: {},
  props: {
    value: Array,
    max: {
      type: Number,
      default: 3
    }
  },
  data() {
    return {}
  },
  computed: {
    currentIndex() {
      return this.value && this.value.length ? this.value.length - 1 : 0
    },

    // 展示value 如果大于 max
    showValue() {
      if (this.value && this.value.length > this.max) {
        const temps = [{
          type: 'all'
        }]
        for (let index = this.max; index > 0; index--) {
          const valueIndex = this.value.length - index
          const element = this.value[valueIndex]
          element.xrBreadcrumbIndex = valueIndex
          temps.push(element)
        }

        return temps
      }

      return this.value || []
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    dropdownCommand(command) {
      this.valueChange(command)
    },

    changeClick(item, index) {
      if (this.value.length != this.showValue.length) {
        this.valueChange(item.xrBreadcrumbIndex)
      } else {
        this.valueChange(index)
      }
    },

    valueChange(index) {
      this.$emit('change', index)
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-breadcrumb {
  font-size: 14px;
  line-height: 1;

  &__item {
    display: flex;
    align-items: center;
    float: left;

    .el-icon-more {
      cursor: pointer;
    }
  }

  &__inner {
    display: inline-block;
    max-width: 110px;
    overflow: hidden;
    color: #606266;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
  }

  &__inner:hover {
    color: #303133;
  }

  &__separator {
    margin: 0 6px;
    font-weight: 400;
    color: #c0c4cc;
  }

  &__inner.is-current {
    font-weight: 700;
    color: #303133;
    text-decoration: none;
    transition: color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  }
}
</style>
