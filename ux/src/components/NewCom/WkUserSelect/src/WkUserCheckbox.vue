<template>
  <el-checkbox-group
    ref="checkboxGroup"
    v-model="currentValue"
    :disabled="disabled"
    class="wk-user-checkbox"
    @change="userChange">
    <el-checkbox
      v-for="(item, i) in data"
      :key="i"
      :label="item[config.value]"
      class="xh-user__item">
      <xr-avatar
        disabled
        :name="$getUserName(item, config.label)"
        :size="24"
        :src="$getUserImg(item)"
        class="user-img" />
      <div class="user-info">
        <div class="user-name text-one-line">{{ $getUserName(item, config.label) }}</div>
        <div class="user-post">{{ item.post || '暂无岗位' }}</div>
      </div>
    </el-checkbox>
  </el-checkbox-group>
</template>

<script>
import UserDepMixin from './UserDepMixin'

export default {
  // 员工选择
  name: 'WkUserCheckbox',

  components: {},

  mixins: [UserDepMixin],

  props: {},

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
    userChange(val) {
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-user-checkbox {
  .xh-user {
    &__item {
      display: flex;
      align-items: center;
      height: 40px;

      ::v-deep .el-checkbox__input {
        flex-shrink: 0;
      }

      ::v-deep .el-checkbox__label {
        display: flex;
        flex: 1;
      }

      .dep-name {
        flex: 1;
        padding-right: 8px;
      }

      .el-checkbox {
        margin-right: 8px;
      }

      .el-button--text {
        position: relative;
        flex-shrink: 0;
        padding: 0 12px;
        color: #ccc;

        ::v-deep i {
          margin-right: 3px;
          font-size: 13px;
        }

        &::before {
          position: absolute;
          top: 2px;
          bottom: 2px;
          left: 0;
          width: 1px;
          content: " ";
          background-color: $--border-color-base;
        }

        &:hover {
          color: $--color-primary;
        }

        &.is-disabled {
          color: #c0c4cc;
        }
      }

      .user {
        &-img {
          flex-shrink: 0;
        }

        &-info {
          flex: 1;
          padding: 0 8px;
        }

        &-name {
          max-width: 200px;
        }

        &-post {
          margin-top: 2px;
          font-size: 12px;
          color: $--color-text-secondary;
        }
      }
    }
  }
}
</style>
