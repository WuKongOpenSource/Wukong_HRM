<template>
  <div
    :style="{'margin-left': marginLeft}"
    class="related-business-wrap">
    <div class="related-business">
      <div
        v-for="(items, key) in relatedListData"
        :key="key"
        :class="{'related-business-list':items.length > 0 }">
        <related-business-cell
          v-for="(item, itemIndex) in items"
          :key="itemIndex"
          class="related-business-items"
          :data="item"
          :cell-index="itemIndex"
          :type="key"
          :show-foot="showFoot"
          @unbind="delRelevance" />
      </div>
    </div>
    <!-- 新建- 相关信息 -->
    <template v-if="showAdd">
      <crm-relative
        v-if="showRelative"
        ref="crmrelative"
        :visible.sync="showRelative"
        :radio="false"
        :selected-data="relatedListData"
        :show-types="showTypes"
        @changeCheckout="checkInfos" />
      <span
        v-if="showCRMPermission"
        slot="reference"
        class="add-btn"
        @click="showRelative = true">
        <i class="wk wk-l-plus" />
        <span class="label">关联业务</span>
      </span>
    </template>
  </div>
</template>

<script>
// 相关信息 - 弹出框
import RelatedBusinessCell from '@/components/RelatedBusinessCell'

import { mapGetters } from 'vuex'

export default {
  name: 'RelatedBusiness',
  components: {
    CrmRelative: () =>
      import('@/components/CreateCom/CrmRelative'),
    RelatedBusinessCell
  },
  props: {
    marginLeft: {
      type: String,
      default: '20px'
    },
    // 编辑时传递所有关联数据   关联联系人-contacts 关联客户-customer 商机-business 合同-contract
    allData: {
      type: Object,
      default: () => {
        return {
          contacts: [],
          customer: [],
          business: [],
          contract: []
        }
      }
    },
    // 展示取消关联
    showFoot: {
      type: Boolean,
      default: false
    },
    showAdd: {
      type: Boolean,
      default: true
    },
    showAddBtn: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      showTypes: ['customer', 'contacts', 'business', 'contract', 'receivables', 'module'],
      // 相关信息信息
      relatedListData: {},
      showRelative: false
    }
  },
  computed: {
    ...mapGetters(['crm']),
    showCRMPermission() {
      // this.crm  都展示，但没有权限的不展示数据
      return this.showAddBtn
    }
  },
  watch: {
    allData: function() {
      this.relatedListData = {}
      for (const i in this.allData) {
        if (this.allData[i] && this.allData[i].length > 0) {
          this.$set(this.relatedListData, i, this.allData[i])
        }
      }
    }
  },
  mounted() {
    // 编辑时勾选
    for (const i in this.allData) {
      if (this.allData[i] && this.allData[i].length > 0) {
        this.$set(this.relatedListData, i, this.allData[i])
      }
    }
  },
  methods: {
    checkInfos(val) {
      this.relatedListData = {}
      for (const i in val.data) {
        if (val.data[i].length > 0) {
          this.$set(this.relatedListData, i, val.data[i])
        }
      }
      const relevanceAll = {}
      for (const key in val.data) {
        const list = val.data[key]
        if (key == 'module') {
          relevanceAll[key + 'Ids'] = list.map(item => item['settingId'])
        } else {
          relevanceAll[key + 'Ids'] = list.map(item => item[key + 'Id'])
        }
      }
      this.$emit('checkInfos', relevanceAll, val.data)
    },
    // 任务页面取消关联
    delRelevance(field, index, item) {
      if (this.showCRMPermission) {
        this.relatedListData[field].splice(index, 1)
        this.$emit('changeInfos', field, item)
      } else {
        this.$emit('unbind', field, item, index)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.related-business-wrap {
  padding: 0 1px;
}

.add-btn {
  display: inline-block;
  min-width: 92px;
  padding: 3px 10px;
  margin-top: 8px;
  font-size: 12px;
  color: $--color-black;
  text-align: center;
  cursor: pointer;
  background-color: $--button-default-background-color;
  border-radius: $--border-radius-base;

  .wk-l-plus {
    font-size: 12px;
  }
}

.add-btn:hover {
  color: $--color-primary;
  background-color: $--button-hover-background-color;
}

.related-business {
  overflow: hidden;
  background: $--color-white;
  border-radius: 4px;
  box-shadow: $--box-shadow-bottom-light;

  &:hover {
    box-shadow: $--box-shadow-hover-bottom-light;
  }

  &-items {
    border-bottom: $--border-base;
  }

  &-items:nth-last-child(1) {
    border-bottom: none;
  }

  &-list {
    border-bottom: $--border-base;
  }

  &-list:nth-last-child(1) {
    border-bottom: none;
  }
}
</style>
