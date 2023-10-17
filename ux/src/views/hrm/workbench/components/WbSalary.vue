<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section :move="false" class="el-card wb-salary">
    <template slot="title-left">
      上月薪资概况
    </template>
    <flexbox class="body">
      <wb-item
        v-for="(item, index) in fieldList"
        :key="index"
        :index="index"
        :label="item.label"
        :unit="item.unit"
        :value="item.num"
        :disabled="disabled"
        @select="itemClick"
      />
      <div class="echart">
        <div id="axismain" />
        <span v-if="!echartHasData" class="empty-text">暂无数据</span>
      </div>
    </flexbox>
  </wb-section>
</template>

<script>
import { hrmDashboardLastSalarySurveyAPI } from '@/api/hrm/workbench'

import WbSection from './WbSection'
import WbItem from './WbItem'

import * as echarts from 'echarts'
import NP from 'number-precision'

export default {
  // 招聘动态
  name: 'Wbsalary',
  components: {
    WbSection,
    WbItem
  },
  props: {},
  data() {
    return {
      fieldList: [{
        label: '计薪人员',
        name: 'total',
        num: 0
      }, {
        label: '实发工资',
        name: 'totalSalary',
        unit: '元',
        num: 0
      }],
      chartObj: null,
      pieOption: null,
      srecordId: null
    }
  },
  computed: {
    disabled() {
      return !this.srecordId
    },

    echartHasData() {
      return this.pieOption && this.pieOption.series[0].data.length > 0
    }
  },
  watch: {},
  created() {
    this.getDetail()
  },
  mounted() {
    this.chartObj = echarts.init(document.getElementById('axismain'))
    this.initPie()
    this.chartObj.setOption(this.pieOption, true)
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardLastSalarySurveyAPI().then(res => {
        const data = res.data || {}
        this.srecordId = data.srecordId
        this.fieldList.forEach(item => {
          item.num = data[item.name] || 0
        })

        const deptProportionList = data.deptProportionList || []
        this.pieOption.series[0].data = deptProportionList.map(item => {
          item.name = item.deptName
          item.value = NP.times(item.proportion, 100)
          return item
        })
        this.chartObj.setOption(this.pieOption, true)
      }).catch(() => {})
    },

    itemClick(index) {
      this.$router.push({
        name: 'salaryHistoryDetail',
        params: {
          id: this.srecordId
        }
      })
    },

    /**
     * 饼状图
     */
    initPie() {
      this.pieOption = {
        title: {
          text: '部门薪资占比',
          left: 'center'
        },
        color: [
          '#6CA2FF',
          '#6AC9D7',
          '#72DCA2',
          '#48E78D',
          '#FECD51',
          '#DBB375',
          '#FF7474',
          '#F59561',
          '#A3AEBC',

          '#4C84FF',
          '#0DBEB4',
          '#00DEDE',
          '#FFAA00',
          '#C7C116',
          '#F7A57C',
          '#F661AC',
          '#8652EE'
        ],
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c}% '
        },
        legend: {
          type: 'scroll',
          bottom: '0',
          x: 'center',
          data: []
        },
        series: [
          {
            name: '',
            type: 'pie',
            radius: '45%',
            stillShowZeroSum: false,
            data: [],
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-salary {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .wb-item {
    flex: 1;
    flex-shrink: 0;
  }

  .echart {
    position: relative;
    flex: 2;

    #axismain {
      width: 100%;
      height: 200px;
      margin: 0 auto;
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
