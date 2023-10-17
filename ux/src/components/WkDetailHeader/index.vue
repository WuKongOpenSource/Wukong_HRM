<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 14:44:51
 * @LastEditTime: 2020-07-03 17:03:34
 * @LastEditors: yang
-->
<template>
  <flexbox class="xr-detail-header">
    <div
      v-if="iconClass"
      :style="{ backgroundColor: iconBgColor }"
      class="xr-detail-header__icon">
      <i :class="iconClass" :style="{ color: iconColor }" />
    </div>
    <div class="xr-detail-header__body">
      <slot name="body" />
      <template v-if="defaultBodyShow">
        <div class="detail-name">{{ detailName }}</div>
        <div class="detail-value">{{ detailValue }}</div>
      </template>
    </div>
    <div class="xr-detail-header__ft">
      <slot name="ft" />
      <el-button
        v-if="showEdit"
        :class="editButtonClass"
        style="margin-right: 10px;"
        class="xr-detail-header--button"
        icon="wk wk-circle-edit"
        type="primary"
        @click.native="editClick">编辑</el-button>
      <el-dropdown
        v-if="dropdownItems && dropdownItems.length > 0"
        trigger="click"
        @command="handleTypeClick">
        <el-button
          icon="el-icon-more"
          class="xr-detail-header--more" />
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(item, index) in dropdownItems"
            :key="index"
            :icon="item.icon"
            :command="item.command">{{ item.label }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </flexbox>

</template>

<script>

export default {
  // 详情头部
  name: 'WkDetailHeader',
  components: {},
  props: {
    iconClass: [String, Array],
    iconBgColor: {
      type: String,
      default: '#ECEEF2'
    },
    iconColor: {
      type: String,
      default: '#42526E'
    },
    detailName: String,
    detailValue: String,
    defaultBodyShow: {
      type: Boolean,
      default: true
    },
    dropdownItems: Array,
    showEdit: {
      type: Boolean,
      default: false
    },
    editButtonClass: {
      type: String,
      default: 'xr-btn--primary'
    }
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    handleTypeClick(type) {
      this.$emit('command-select', type)
    },

    editClick() {
      this.$emit('edit')
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-detail-header {
  position: relative;
  min-height: 60px;
  padding: 20px;

  &__icon {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    line-height: 30px;
    text-align: center;
    border-radius: $--border-radius-base;

    .wk {
      font-size: 18px;
    }
  }

  &__body {
    flex: 1;

    .detail-name {
      margin-bottom: 5px;
      font-size: 12px;
      color: $--color-text-secondary;
    }

    .detail-value {
      display: flex;
      overflow: hidden;
      font-size: 16px;
      font-weight: 600;
      color: $--color-text-primary;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
    }
  }

  &--more {
    margin-left: 15px;
  }

  &--more:hover {
    color: $--color-primary;
  }

  &--button {
    ::v-deep i {
      margin-right: 5px;
      font-size: 13px;
    }
  }
}
</style>
