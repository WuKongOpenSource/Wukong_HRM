<template>
  <div
    v-loading="loading"
    class="rc-cont">
    <flexbox
      v-for="(item, index) in list"
      :key="index"
      class="ha-cont"
      align="stretch"
      justify="flex-start">
      <div class="ha-week">
        <div>{{ getYMDFormaterTime(item.createTime) }}</div>
        <div>{{ getWeekFormaterTime(item.createTime) }}</div>
      </div>
      <div class="ha-circle" />
      <div class="ha-time">{{ getTimeFormaterTime(item.createTime) }}</div>
      <div class="ha-name">{{ item.realname }}</div>
      <div class="ha-content">
        <p
          v-for="(info, infoIndex) in item.content"
          :key="infoIndex">{{ info }}</p>
      </div>
      <div class="ha-line" />
    </flexbox>
  </div>
</template>

<script type="text/javascript">
import { hrmActionRecordQueryAPI } from '@/api/hrm/actionRecord'

import { timeToFormatTime } from '@/utils'

export default {
  name: 'CandidateHandle', // 相关操作
  components: {},
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      list: []
    }
  },
  computed: {},
  watch: {
    id: function(val) {
      this.list = []
      this.getDetail()
    }
  },
  mounted() {
    this.getDetail()
  },
  activated: function() {
    console.log(3)
  },
  deactivated: function() {
    console.log(4)
  },
  methods: {
    getDetail() {
      this.loading = true
      hrmActionRecordQueryAPI({
        type: 3, // 操作类型 1 员工 2 招聘管理 3 候选人 4 绩效管理
        typeId: this.id
      })
        .then(res => {
          this.loading = false
          const list = res.data || []
          list.forEach(item => {
            item.content = item.content ? JSON.parse(item.content) : []
          })
          this.list = list
        })
        .catch(() => {
          this.loading = false
        })
    },

    getYMDFormaterTime(time) {
      return timeToFormatTime(time)
    },

    getWeekFormaterTime(time) {
      return timeToFormatTime(time, 'dddd')
    },

    getTimeFormaterTime(time) {
      return timeToFormatTime(time, 'H:mm')
    }
  }
}
</script>
<style lang="scss" scoped>
.rc-cont {
  position: relative;
  height: 100%;
  padding-top: 16px;
  overflow-y: auto;
}

.ha-cont {
  position: relative;
  min-height: 40px;
  line-height: 20px;

  .ha-week {
    flex-shrink: 0;
    width: 90px;
    margin: 0 17px 0 10px;
  }

  .ha-time {
    flex-shrink: 0;
    width: 80px;
    padding: 0 10px 0 24px;
  }

  .ha-circle {
    z-index: 2;
    flex-shrink: 0;
    width: 18px;
    height: 18px;
    background-color: white;
    border: 5px solid $--color-primary;
    border-radius: 9px;
  }

  .ha-img {
    display: block;
    flex-shrink: 0;
    margin: -3px 10px 0;
  }

  .ha-name {
    flex-shrink: 0;
    padding: 0 10px;
  }

  .ha-content {
    flex: 1;
    padding: 0 10px 10px;
  }

  .ha-line {
    position: absolute;
    top: 3px;
    bottom: -3px;
    left: 125px;
    z-index: 1;
    width: 1px;
    background-color: $--border-color-base;
  }
}
</style>
