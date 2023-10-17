<template>
  <section class="file-cont">
    <flexbox class="f-header">
      <i class="wk wk-file" />
      <div class="f-name">附件</div>
    </flexbox>
    <div class="f-body">
      <flexbox
        v-for="(item, index) in data"
        :key="index"
        class="f-item">
        <img
          :src="item.icon"
          class="f-img">
        <div class="f-name">{{ item.name }}<span class="f-size">{{ `（${item.size}）` }}</span></div>
        <el-button
          icon="wk wk-s-delete"
          type="icon"
          class="close-button"
          @click="deleteItem(item, index)" />
      </flexbox>
    </div>
    <el-button type="text" @click="deleteAll">全部删除</el-button>
  </section>
</template>

<script>

export default {
  // 添加附件展示
  name: 'AddFileList',
  components: {},
  props: {
    data: Array
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    deleteItem(item, index) {
      this.$emit('delete', item, index)
    },

    deleteAll() {
      this.$emit('delete-all')
    }
  }
}
</script>

<style lang="scss" scoped>
/** 附件  */
.file-cont {
  padding: 0 10px;
  padding: #{$--interval-base * 2};
  margin-bottom: #{$--interval-base * 2};
  border: 1px dashed $--border-color-base;
  border-radius: $--border-radius-base;

  .f-header {
    padding-bottom: #{$--interval-base * 2};

    .wk {
      margin-right: $--interval-base;
    }

    .f-name {
      font-weight: 600;
    }
  }

  .f-body {
    .f-item {
      height: 25px;

      .f-img {
        position: block;
        width: 12px;
        margin-right: 8px;
      }

      .f-size {
        color: $--color-n100;
      }
    }

    .f-item + .f-item {
      margin-top: $--interval-base;
    }
  }

  .el-button--text {
    padding: 0;
    margin-top: $--interval-base;
    font-size: 12px;
  }

  // 关闭按钮
  .close-button {
    padding: 4px;
  }
}
</style>
