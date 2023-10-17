<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section :move="false" class="el-card wb-backlog">
    <template slot="title-left">
      待办提醒
    </template>
    <flexbox class="body" wrap="wrap">
      <wb-item
        v-for="(item, index) in fieldList"
        :key="index"
        :index="index"
        :label="item.label"
        :value="item.num"
        :disabled="(item.num === 0 && index === 0) || !canCheck"
        :unit="item.unit || '人'"
        @select="itemClick"
      />
    </flexbox>
  </wb-section>
</template>

<script>
import { hrmDashboardToDoRemindAPI } from '@/api/hrm/workbench'

import WbSection from './WbSection'
import WbItem from './WbItem'

import { mapGetters } from 'vuex'

export default {
  // 待办提醒
  name: 'WbBacklog',
  components: {
    WbSection,
    WbItem
  },
  props: {},
  data() {
    return {
      fieldList: [{
        label: '待审核薪资',
        name: 'toSalaryExamine',
        num: 0,
        unit: '条'
      }, {
        label: '待离职',
        name: 'toLeave',
        num: 0
      }, {
        label: '合同到期',
        name: 'toExpireContract',
        num: 0
      }, {
        label: '待转正',
        name: 'toCorrect',
        num: 0
      }, {
        label: '待入职',
        name: 'toIn',
        num: 0
      }, {
        label: '生日',
        name: 'toBirthday',
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
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardToDoRemindAPI().then(res => {
        const data = res.data || {}
        this.fieldList.forEach(item => {
          item.num = data[item.name] || 0
        })
      }).catch(() => {})
    },

    itemClick(index) {
      const item = this.fieldList[index]
      const params = {}
      if (index === 0) {
        this.$router.push({
          name: 'hrmSalary'
        })
      } else {
        // 2 待离职 3 合同到期 4 待转正 5 待入职 6 生日
        params.toDoRemind = {
          toLeave: '2',
          toExpireContract: '3',
          toCorrect: '4',
          toIn: '5',
          toBirthday: '6'
        }[item.name]
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
}
</script>

<style lang="scss" scoped>
.wb-backlog {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .wb-item {
    flex: 0 0 33.33%;
    flex-shrink: 0;
  }

  .wb-item:nth-child(4),
  .wb-item:nth-child(5),
  .wb-item:nth-child(6), {
    margin-top: 20px;
  }
}
</style>
