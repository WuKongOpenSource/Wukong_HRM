<template>
  <wb-section :move="false" class="el-card wb-personnel">
    <template slot="title-left">
      人事概况 {{ des }}
    </template>
    <flexbox class="body">
      <wb-item
        v-for="(item, index) in fieldList"
        :key="index"
        :index="index"
        :label="item.label"
        :value="item.num"
        :disabled="!canCheck"
        @select="itemClick"
      />
    </flexbox>
  </wb-section>
</template>

<script>
import { hrmDashboardEmployeeSurveyAPI } from '@/api/hrm/workbench'

import WbSection from './WbSection'
import WbItem from './WbItem'

import moment from 'moment'
import { mapGetters } from 'vuex'

export default {
  //  人事概况
  name: 'WbPersonnel',
  components: {
    WbSection,
    WbItem
  },
  props: {},
  data() {
    return {
      des: '',
      // 1 入职 2 离职 3 转正 4 调岗 0 在职 5 待入职 6 待离职
      fieldList: [{
        label: '在职',
        name: '0',
        num: 0
      }, {
        label: '入职',
        name: '1',
        num: 0
      }, {
        label: '待入职',
        name: '5',
        num: 0
      }, {
        label: '离职',
        name: '2',
        num: 0
      }, {
        label: '待离职',
        name: '6',
        num: 0
      }, {
        label: '转正',
        name: '3',
        num: 0
      }, {
        label: '调岗',
        name: '4',
        num: 0
      }]
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),
    canCheck() {
      return this.hrm.employee && this.hrm.employee.index
    }
  },
  watch: {},
  created() {
    const startOfMonth = moment().startOf('month').format('YYYY.MM.DD')
    const endOfMonth = moment().endOf('month').format('YYYY.MM.DD')
    this.des = `（${startOfMonth}-${endOfMonth}）`
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardEmployeeSurveyAPI().then(res => {
        const data = res.data || {}
        this.fieldList.forEach(item => {
          item.num = data[parseInt(item.name)] || 0
        })
      }).catch(() => {})
    },

    itemClick(index) {
      const item = this.fieldList[index]
      const params = {}
      if (index === 0) {
        params.tabType = '11' // 在职
      } else {
        // 1 入职 2 离职 3 转正 4 调岗 5 待入职 6 待离职
        params.employeeSurvey = item.name
      }

      this.$router.push({
        name: 'hrmEmployee',
        query: {
          workbench: 'filter',
          ...params
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-personnel {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .wb-item {
    flex: 1;
    flex-shrink: 0;
  }

  .wb-item:first-child {
    flex: 1;
    margin-right: 40px;
    border-right: 1px solid $--border-color-base;
  }
}
</style>
