<template>
  <div class="check-cell">
    <el-checkbox
      ref="checkbox"
      v-bind="$attrs"
      v-on="$listeners" />
    <div class="check-cell__bd" @click="selectClick">
      <xr-avatar
        disabled
        :name="$getUserName(item, config.label)"
        :size="30"
        :src="$getUserImg(item)" />
      <div class="content">
        <div
          class="realname text-one-line"
          :title="$getUserName(item, config.label)">{{ $getUserName(item, config.label) }}</div>
        <div
          class="dept-name text-one-line"
          :title="$getDeptName(item)">{{ $getDeptName(item) || '无' }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import merge from '@/utils/merge'

const CheckCell = {
  value: 'userId',
  label: 'realname'
}

export default {
  // CheckCell 员工行
  name: 'CheckCell',

  components: {},

  inject: ['userDepDialog'],

  inheritAttrs: false,

  props: {
    item: Object,
    props: Object
  },

  data() {
    return {
    }
  },

  computed: {
    isChecked() {
      return this.userDepDialog.currentUserValue.includes(this.item[this.config.value])
    },

    // 合并 props
    config() {
      return merge({ ...CheckCell }, this.props || {})
    }
  },

  watch: {
    isChecked: {
      handler() {
        this.$set(this.item, 'isChecked', this.isChecked)
      },
      deep: true,
      immediate: true
    }
  },

  created() {},

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    selectClick() {
      this.$refs.checkbox.$el.click()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.check-cell {
  display: flex;
  align-items: center;
  height: 40px;
  cursor: pointer;

  ::v-deep .el-checkbox {
    display: inline-block;
    height: auto;
    margin-right: 8px;

    .el-checkbox__label {
      display: none;
    }
  }

  .check-cell__bd {
    display: flex;
    flex: 1;
    flex-shrink: 0;
    align-items: center;
  }

  .xr-avatar {
    flex-shrink: 0;
    margin-right: 8px;
  }

  .content {
    flex: 1;
    width: 0;

    .realname {
      font-size: 14px;
      color: $--color-black;
    }

    .dept-name {
      font-size: 12px;
      color: $--color-text-secondary;
    }
  }
}
</style>
