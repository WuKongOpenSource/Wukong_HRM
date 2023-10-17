<template>
  <ul
    ref="breadcrumbUl"
    class="breadcrumb">
    <li
      v-for="(item, index) in data"
      :key="index"
      class="breadcrumb-item"
      @click="breadcrumbsClick(item, index)"
    ><div
      class="label"
      :title="item.name"
      :class="{ 'is-last': index === data.length - 1}">{{ $getDeptName(item) }}</div><i
        v-if="index < data.length - 1"
        :class="{ 'is-last': index === data.length - 2}"
        class="el-icon-arrow-right" /></li>
  </ul>
</template>

<script>
export default {
  // Breadcrumb
  name: 'Breadcrumb',

  components: {},

  props: {
    data: Array
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
    breadcrumbsClick(item, index) {
      this.$emit('select', item, index)
      this.updatePostion()
    },

    /**
     * 更新偏移位置
     */
    updatePostion() {
      this.$nextTick(() => {
        const breadcrumb = this.$refs.breadcrumbUl
        breadcrumb.style.marginLeft = `${breadcrumb.offsetWidth - breadcrumb.scrollWidth}px`
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.breadcrumb {
  display: inline-block;
  width: 100%;
  height: 30px;
  clear: both;
  white-space: nowrap;
  list-style: none;

  &-item {
    display: inline-block;
    height: 30px;
    line-height: 30px;
    color: $--color-primary;

    >.label {
      float: left;
      max-width: 300px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;
    }

    .el-icon-arrow-right {
      display: inline-block;
      float: left;
      padding: 8px 0;
      line-height: 16px;
    }

    >.is-last {
      color: $--color-text-secondary;
    }
  }
}
</style>
