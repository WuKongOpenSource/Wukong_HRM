<template>
  <div class="wb-pie" />
</template>

<script>
import * as echarts from 'echarts'

export default {
  // 饼图
  name: 'WbPie',

  components: {},

  props: {
    props: {
      type: Object,
      default: () => {
        return {
          title: ''
        }
      }
    },
    data: Array
  },

  data() {
    return {
      chartObj: null,
      pieOption: null
    }
  },

  computed: {},

  watch: {
    data() {
      this.initPie()
      this.chartObj.setOption(this.pieOption, true)
    }
  },

  created() {

  },

  mounted() {
    this.chartObj = echarts.init(this.$el)
    this.initPie()
    this.chartObj.setOption(this.pieOption, true)
  },

  beforeDestroy() {},

  methods: {
    /**
     * 饼状图
     */
    initPie() {
      this.pieOption = {
        title: {
          text: this.props.title,
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
          formatter: function(data) {
            const dataValue = data.data || {}
            return `${dataValue.name}: ${dataValue.value}% ${dataValue.count}人`
          }
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
            top: 60,
            type: 'pie',
            radius: '45%',
            stillShowZeroSum: false,
            data: this.data,
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
.wb-pie {
  width: 100%;
  min-width: 200px;
  height: 250px;
}
</style>
