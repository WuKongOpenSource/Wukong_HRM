<template>
  <div class="user-list">
    <RecycleScroller
      :items="data"
      :item-size="40"
      key-field="userId">
      <template slot-scope="{item}">
        <check-cell
          :item="item"
          :label="item[config.value]"
          :disabled="disabled"
          :props="config"
          @change="userCheck(item)" />
      </template>
    </RecycleScroller>
  </div>
</template>

<script>
import CheckCell from './CheckCell'

import merge from '@/utils/merge'

const UserList = {
  value: 'userId',
  label: 'realname'
}

export default {
  // UserList
  name: 'UserList',

  components: {
    CheckCell
  },

  props: {
    data: Array,
    disabled: Boolean,
    props: Object
  },

  data() {
    return {
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...UserList }, this.props || {})
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 用户勾选
     */
    userCheck(item) {
      this.$nextTick(() => {
        this.$emit('change', item)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.user-list {
  position: relative;
  height: 100%;
  padding-top: 8px;
  overflow-y: auto;
  font-size: $--font-size-base;

  .vue-recycle-scroller {
    height: 100%;
  }
}
</style>
