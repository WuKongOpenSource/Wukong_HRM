<template>
  <section
    class="c-cont">
    <flexbox class="c-header">
      <i :class="['wk', `wk-${type}`]" />
      <div class="c-name">{{ typeName }}</div>
    </flexbox>
    <div class="c-body">
      <flexbox wrap="wrap">
        <flexbox
          v-for="(item, index) in data"
          :key="index"
          class="c-item">
          <div class="c-item-name">{{ getItemName(item) }}</div>
          <el-button
            icon="el-icon-close"
            type="icon"
            class="close-button"
            @click="deleteItem(item, index)" />
        </flexbox>
      </flexbox>
    </div>
  </section>
</template>

<script>
/**
 * 名称字段
 * customerName
 * name
 * businessName
 * name
 */
export default {
  // 添加相关业务信息展示
  name: 'AddRelateList',
  components: {},
  props: {
    data: Array,
    // 业务关键字 customer contacts business contract
    type: {
      type: [String, Number],
      require: true
    },
    label: String
  },
  data() {
    return {
    }
  },
  computed: {
    typeName() {
      if (this.label) {
        return this.label
      }

      return {
        customer: '客户',
        contacts: '联系人',
        business: '商机',
        contract: '合同'
      }[this.type]
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    getItemName(data) {
      const key = {
        customer: 'customerName',
        contacts: 'name',
        business: 'businessName',
        contract: 'name'
      }[this.type]
      return data[key] || data.name
    },

    deleteItem(item, index) {
      this.$emit('delete', item, index, this.type)
    }
  }
}
</script>

<style lang="scss" scoped>
/** CRM  */
.c-cont {
  padding: 0 10px;
  padding: #{$--interval-base * 2};
  margin-bottom: #{$--interval-base * 2};
  border: 1px dashed $--border-color-base;
  border-radius: $--border-radius-base;

  .c-header {
    padding-bottom: #{$--interval-base * 2};

    .wk {
      margin-right: $--interval-base;
    }

    .c-name {
      font-weight: 600;
    }
  }

  .c-body {
    .c-item {
      width: auto;
      height: 24px;
      padding: 0 8px;
      margin: 0 4px 4px 0;
      color: white;
      cursor: pointer;
      background-color: $--color-primary;
      border-radius: 12px;

      .c-item-name {
        font-size: 12px;
      }

      // 关闭按钮
      .close-button {
        padding: 4px;
        color: white;
      }
    }
  }
}
</style>
