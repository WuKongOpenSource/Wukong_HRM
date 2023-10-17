<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section :move="false" class="el-card wb-recruit">
    <template slot="title-left">
      招聘动态 {{ des }}
    </template>
    <flexbox class="body">
      <wb-item
        v-for="(item, index) in fieldList"
        :key="index"
        :index="index"
        :label="item.label"
        :value="item.num"
        :unit="item.unit"
        :disabled="item.disabled"
        @select="itemClick"
      />
    </flexbox>
  </wb-section>
</template>

<script>
import { hrmDashboardRecruitSurveyAPI } from '@/api/hrm/workbench'

import WbSection from './WbSection'
import WbItem from './WbItem'

import moment from 'moment'
import { mapGetters } from 'vuex'

export default {
  // 招聘动态
  name: 'WbRecruit',
  components: {
    WbSection,
    WbItem
  },
  props: {},
  data() {
    return {
      des: '',
      fieldList: [{
        label: '正在招聘职位',
        name: 'postCount',
        unit: '个',
        disabled: false,
        num: 0
      }, {
        label: '评选中',
        name: 'chooseCount',
        disabled: true,
        num: 0
      }, {
        label: '待入职',
        name: 'pendingEntryCount',
        disabled: false,
        num: 0
      }, {
        label: '已入职',
        name: 'haveJoinedCount',
        disabled: false,
        num: 0
      }]
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),
    andidateCanCheck() {
      return this.hrm.recruit && this.hrm.recruit.read
    },
    postCanCheck() {
      return this.hrm.recruit && this.hrm.recruit.readPost
    }
  },
  watch: {},
  created() {
    const startTime = moment().add(-0.5, 'y').format('YYYY.MM.DD')
    const endTime = moment().format('YYYY.MM.DD')
    this.des = `（${startTime}-${endTime}）`
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardRecruitSurveyAPI().then(res => {
        const data = res.data || {}
        this.fieldList.forEach((item, index) => {
          item.num = data[item.name] || 0
          if (index === 0) {
            item.disabled = !this.postCanCheck
          } else if (index === 2 || index === 3) {
            item.disabled = !this.andidateCanCheck
          }
        })
      }).catch(() => {})
    },

    itemClick(index) {
      const item = this.fieldList[index]
      if (index === 0) {
        this.$router.push({
          name: 'hrmPost'
        })
      } else if (index > 1) {
        this.$router.push({
          name: 'hrmCandidate',
          query: {
            workbench: 'filter',
            recruitSurvey: item.name === 'pendingEntryCount' ? '3' : '4' // 招聘动态 3待入职 4 已入职
          }
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-recruit {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .wb-item {
    flex: 1;
    flex-shrink: 0;
  }
}
</style>
