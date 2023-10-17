<template>
  <flexbox
    class="detail-head-base"
    align="stretch">
    <div
      v-for="(item,index) in list"
      :key="index"
      class="base-item">
      <div class="base-title">{{ item.title }}<i v-if="item.helpObj" class="wk wk-icon-fill-help wk-help-tips" :data-type="item.helpObj.type" :data-id="item.helpObj.id" /></div>
      <div class="base-value text-one-line">
        <span v-if="item.showPhone" class="phone-item">
          <i class="wk wk-circle-iphone" /> {{ item.value }}
        </span>
        <el-tooltip v-else :content="item.value" class="text-one-line" effect="dark" placement="top">
          <span>{{ item.value }}</span>
        </el-tooltip>
      </div>
    </div>
  </flexbox>
</template>

<script>
export default {
  // 详情头base
  name: 'DetailHeadBase',

  components: {},

  props: {
    list: Array
  },

  data() {
    return {
    }
  },

  methods: {
  }
}
</script>

<style lang="scss" scoped>
.detail-head-base {
  position: relative;
  padding: 16px;
  margin-top: 20px;
  background: $--color-n20;

  .base-item {
    flex: 0 0 20%;
    overflow: hidden;

    & + & {
      padding-left: $--interval-base;
    }
  }

  .base-title {
    color: $--color-text-secondary;
  }

  .base-value {
    min-height: 14px;
    margin-top: #{$--interval-base};
  }

  .phone-item {
    color: $--color-g300;

    i {
      margin-right: #{$--interval-base / 2};
    }
  }
}
</style>
