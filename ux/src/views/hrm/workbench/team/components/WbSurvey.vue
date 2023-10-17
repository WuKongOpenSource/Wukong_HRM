<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section
    :move="false"
    icon="wk wk-s-seas"
    class="wb-survey"
    label="团队概况"
  >
    <flexbox class="body">
      <flexbox-item
        v-for="(item, index) in chartList"
        :key="index">
        <span v-if="item.data.length === 0" class="empty-text">暂无数据</span>
        <wb-pie
          v-else
          :props="item.props"
          :data="item.data"
        />
      </flexbox-item>
    </flexbox>
  </wb-section>
</template>

<script>
import { hrmDashboardTeamSurveyAPI } from '@/api/hrm/workbench'

import WbSection from '../../components/WbSection'
import WbPie from './WbPie'
import NP from 'number-precision'

export default {
  // 团队概况
  name: 'WbSurvey',
  components: {
    WbSection,
    WbPie
  },
  props: {},
  data() {
    return {
      chartList: [{
        props: {
          title: '员工状态占比'
        },
        data: []
      }, {
        props: {
          title: '男女性别占比'
        },
        data: []
      }, {
        props: {
          title: '成员年龄占比'
        },
        data: []
      }, {
        props: {
          title: '成员司龄占比'
        },
        data: []
      }]
    }
  },
  computed: {
  },
  watch: {},
  created() {
    this.getDetail()
  },
  mounted() {
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardTeamSurveyAPI().then(res => {
        const data = res.data || {}
        const statusAnalysis = data.statusAnalysis || []
        const sexAnalysis = data.sexAnalysis || []
        const ageAnalysis = data.ageAnalysis || []
        const companyAgeAnalysis = data.companyAgeAnalysis || []
        this.chartList[0].data = this.getItems(statusAnalysis)
        this.chartList[1].data = this.getItems(sexAnalysis)
        this.chartList[2].data = this.getItems(ageAnalysis)
        this.chartList[3].data = this.getItems(companyAgeAnalysis)
        this.chartObj.setOption(this.pieOption, true)
      }).catch(() => {})
    },

    getItems(list) {
      const newList = []
      list.forEach(item => {
        if (item.count > 0) {
          item.value = NP.times(item.proportion, 100)
          newList.push(item)
        }
      })

      return newList
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-survey {
  .body {
    padding-left: 15px;

    .wb-pie {
      margin: 0 auto;
    }

    .vux-flexbox-item {
      position: relative;
      min-height: 250px;
    }

    .empty-text {
      position: absolute;
      top: 50%;
      left: 50%;
      z-index: 0;
      color: $--color-text-secondary;
      transform: translate(-50%, -50%);
    }
  }
}
</style>
