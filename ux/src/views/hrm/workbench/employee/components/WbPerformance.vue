<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section
    class="wb-performance"
    icon="wk wk-perform"
    label="绩效管理"
  >
    <flexbox class="body" wrap="wrap">
      <wb-item
        v-for="(item, index) in fieldList"
        :key="index"
        :index="index"
        :label="item.label"
        :value="item.num"
        :unit="''"
        @select="itemClick"
      />
    </flexbox>
  </wb-section>
</template>

<script>
import {
  hrmPerformanceEmployeeQueryNumAPI
} from '@/api/hrm/selfService/performance'

import WbSection from '../../components/WbSection'
import WbItem from '../../components/WbItem'

export default {
  // 绩效档案
  name: 'WbPerformance',
  components: {
    WbSection,
    WbItem
  },
  props: {},
  data() {
    return {
      fieldList: [{
        label: '目标待填写',
        key: 7,
        name: 'my',
        num: 0
      }, {
        label: '目标待确认',
        name: 'target',
        key: 2,
        num: 0
      }, {
        label: '结果待评定',
        name: 'evaluato',
        key: 5,
        num: 0
      }, {
        label: '结果待确认',
        name: 'result',
        key: 4,
        num: 0
      }]
    }
  },
  computed: {},
  watch: {},
  created() {
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmPerformanceEmployeeQueryNumAPI()
        .then(res => {
          const data = res.data || {}
          this.fieldList.forEach((item, index) => {
            item.num = data[item.key] || 0
          })
        })
        .catch(() => {
        })
    },

    itemClick(index) {
      const item = this.fieldList[index]
      this.$router.push({
        name: 'myPerformance',
        query: {
          tabType: item.name,
          subTabType: '0' // 子菜单状态
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-performance {
  .body {
    padding-left: 15px;
  }

  .wb-item {
    flex: 0 0 25%;
    flex-shrink: 0;
  }
}
</style>
