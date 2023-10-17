<template>
  <flexbox class="cell">
    <div class="cell-head">
      <span>
        {{ `相关${typeName}-` }}
      </span>
    </div>
    <div
      :class="{ 'cursor-pointer': cursorPointer }"
      class="cell-body text-one-line"
      @click="bodyClick">
      {{ name }}
    </div>
    <div v-if="showFoot" class="cell-foot">
      <el-dropdown @command="handleClick">
        <span class="el-dropdown-link">
          <i class="el-icon-more" style="cursor: pointer;" />
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>
            <a class="cancel-btn"> 取消关联 </a>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!-- CRM详情 -->
    <c-r-m-full-screen-detail
      :id="detailId"
      :visible.sync="showFullDetail"
      :crm-type="type" />
  </flexbox>
</template>

<script type="text/javascript">
export default {
  name: 'RelatedBusinessCell',
  components: {
    CRMFullScreenDetail: () => import('@/components/CRMFullScreenDetail')
  },
  props: {
    type: {
      type: String,
      default: ''
    },
    cellIndex: Number,
    data: Object,
    showFoot: {
      type: Boolean,
      default: true
    },
    cursorPointer: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      // 详情参数
      showFullDetail: false,
      detailId: 0
    }
  }, // 相关CRM等类型展示布局
  computed: {
    typeName() {
      if (this.type == 'customer') {
        return '客户'
      } else if (this.type == 'contacts') {
        return '联系人'
      } else if (this.type == 'business') {
        return '商机'
      } else if (this.type == 'contract') {
        return '合同'
      } else if (this.type == 'receivables') {
        return '回款'
      } else if (this.type == 'product') {
        return '产品'
      } else if (this.type == 'module') {
        return '产品'
      }
      return ''
    },
    name() {
      if (this.type == 'receivables') {
        // 添加是展示 number 编辑时展示name
        return this.data.number || this.data.name
      }
      return (
        this.data.description ||
        this.data.name ||
        this.data.businessName ||
        this.data.customerName ||
        this.data.number
      )
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    handleClick() {
      this.$emit('unbind', this.type, this.cellIndex, this.data)
    },
    footClick() {
      this.$emit('unbind', this.type, this.cellIndex, this.data)
    },
    bodyClick() {
      if (this.type == 'module') {
        return
      }
      this.detailId = this.data[`${this.type}Id`] || this.data.id
      this.showFullDetail = true
    }
  }
}
</script>
<style lang="scss" scoped>
.cell {
  position: relative;
  height: 48px;
  padding: 15px;

  &:hover {
    background-color: $--color-n20;
  }

  &-head {
    flex-shrink: 0;
    color: $--color-n200;

    i {
      display: inline-block;
      width: 15px;
      height: 15px;
      margin-right: 5px;
      font-size: 14px;
      color: $--color-text-regular;
    }
  }

  &-body {
    flex: 1;
    color: $--color-primary;
  }

  &-body:hover {
    text-decoration: underline;
  }

  &-foot {
    flex-shrink: 0;
  }
}

.cancel-btn {
  font-size: 12px;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
