<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section :move="false" class="wb-calendar">
    <template slot="title-left">
      日历
    </template>
    <calendar
      ref="calendar"
      :lunar="true"
      :zero="true"
      :events="eventsList"
      @select="selectDate"
      @render="renderDate" />

    <flexbox v-if="lunarInfo" class="board">
      <div class="board__day">{{ lunarInfo.day }}</div>
      <div class="board__info">
        <div>{{ lunarInfo.ncWeek }}</div>
        <div>{{ `${lunarInfo.IMonthCn}${lunarInfo.IDayCn}` }}</div>
      </div>
      <el-button class="board-add" type="text" icon="wk wk-add" @click="addClick">添加备忘录</el-button>
    </flexbox>

    <div class="list">
      <div class="list__header">当天事项</div>
      <div class="list__body">
        <flexbox
          v-for="(item, index) in showMemoList"
          :key="index"
          class="list__item">
          <el-tag class="xr-tag" disable-transitions>{{ item.typeName }}</el-tag>
          <el-tooltip :content="item.content" placement="top">
            <div
              :class="{'can-visit--underline': item.type != 1}"
              class="text-one-line"
              @click="checkDetail(item)"
            >{{ item.content }}</div>
          </el-tooltip>
          <i v-if="item.type == 1" class="wk wk-icon-bin list__delete" @click="deleteItem(item)" />
        </flexbox>
        <div class="list__more">
          <el-button v-if="!allShow && memoList.length > 4" type="text" @click="allShowClick">查看更多事项<i class="el-icon-d-arrow-right" /></el-button>
          <span v-if="memoList.length == 0">暂无数据</span>
        </div>
      </div>
    </div>

    <memo-add-dialog
      :visible.sync="memoAddShow"
      @change="refreshData" />

    <employee-detail
      v-if="employeeDetailShow"
      :id="detailId"
      @close="employeeDetailShow=false" />

    <candidate-detail
      v-if="candidateDetailShow"
      :id="detailId"
      @close="candidateDetailShow=false" />
  </wb-section>
</template>

<script>
import {
  hrmDashboardQueryNotesStatusAPI,
  hrmDashboardQueryNotesByTimeAPI,
  hrmDashboardDeleteNotesAPI
} from '@/api/hrm/workbench'
import moment from 'moment'

import WbSection from './WbSection'
import Calendar from './WkCalendar'
import calendar from './calendar.js'
import MemoAddDialog from './MemoAddDialog'
import EmployeeDetail from '../../employee/EmployeeDetail'
import CandidateDetail from '../../recruit/candidate/Detail'

export default {
  // 日历
  name: 'WbCalendar',
  components: {
    WbSection,
    Calendar,
    MemoAddDialog,
    EmployeeDetail,
    CandidateDetail
  },
  props: {
    props: {
      type: Object,
      default: () => {
        return {
          monthRequest: null,
          dayRequest: null
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      dateLine: {},
      eventsList: [],
      lunarInfo: null,
      memoList: [],
      memoAddShow: false,
      // 记录月 和天的参数
      postParams: null,
      postTime: '',
      allShow: false,
      detailId: '',
      employeeDetailShow: false,
      candidateDetailShow: false
    }
  },
  computed: {
    showMemoList() {
      if (this.allShow) {
        return this.memoList
      }
      if (this.memoList.length > 4) {
        return this.memoList.slice(0, 4)
      }

      return this.memoList
    }
  },
  watch: {},
  created() {
    this.getLunarInfo()
    this.getDayDetail(moment().format('YYYY-MM-DD'))
  },

  beforeDestroy() {},
  methods: {
    renderDate(start, end) {
      const startTime = `${start.year}-${start.month < 10 ? `0${start.month}` : start.month}-${start.day < 10 ? `0${start.day}` : start.day}`
      const endTime = `${end.year}-${end.month < 10 ? `0${end.month}` : end.month}-${end.day < 10 ? `0${end.day}` : end.day}`
      if (!this.postParams || (
        this.postParams.startTime != startTime ||
        this.postParams.endTime != endTime
      )) {
        this.getDetail({
          startTime,
          endTime
        })
      }
    },

    getLunarInfo() {
      const now = new Date()
      const year = now.getFullYear()
      const month = now.getMonth()
      const day = now.getDate()

      const lunarInfo = calendar.solar2lunar(year, month + 1, day)
      lunarInfo.day = this.zeroPad(lunarInfo.cDay)
      this.lunarInfo = lunarInfo
    },

    /**
     * 日历选择的日期
     */
    selectDate(year, month, day) {
      this.getDayDetail(`${year}-${month}-${day}`)
    },

    /**
     * 获取详情
     */
    getDetail(params) {
      this.postParams = params
      this.loading = true
      const request = this.props.monthRequest || hrmDashboardQueryNotesStatusAPI
      request(params).then(res => {
        this.eventsList = res.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 获取某天详情
     */
    getDayDetail(time) {
      this.loading = true
      this.postTime = time
      const request = this.props.dayRequest || hrmDashboardQueryNotesByTimeAPI
      request({
        time
      }).then(res => {
        this.loading = false
        const list = res.data || []
        // 1 备忘 2 生日 3 入职 4 转正 5 离职 6 招聘 7 考勤
        list.forEach(item => {
          item.typeName = {
            1: '备忘',
            2: '生日',
            3: '入职',
            4: '转正',
            5: '离职',
            6: '招聘',
            7: '考勤'
          }[item.type]
        })
        this.memoList = list
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 查看详情
     */
    checkDetail(item) {
      if (item.typeId) {
        this.detailId = item.typeId
        if (item.type === 6) {
          this.candidateDetailShow = true
        } else if (item.type !== 1) {
          this.employeeDetailShow = true
        }
      }
    },

    /**
     * 展示全部
     */
    allShowClick() {
      this.allShow = true
    },

    /**
     * 刷新页面
     */
    refreshData() {
      this.getDetail(this.postParams)
      this.getDayDetail(this.postTime)
    },

    /**
     * 日期补零
     */
    zeroPad(n) {
      return String(n < 10 ? '0' + n : n)
    },

    /**
     * 添加备忘
     */
    addClick() {
      this.memoAddShow = true
    },

    /**
     * 删除备忘
     */
    deleteItem(item) {
      this.loading = true
      hrmDashboardDeleteNotesAPI(item.notesId).then(res => {
        this.refreshData()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-calendar {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .board {
    padding: 10px 20px;
    margin-top: 20px;
    background-color: #edf2ff;
    border-radius: 4px;

    &__day {
      font-size: 45px;
      font-weight: 900;
    }

    &__info {
      flex: 1;
      padding: 0 8px;
      font-size: 12px;
      color: $--color-text-secondary;

      div:nth-child(2) {
        margin-top: 4px;
      }
    }

    &__add {
      flex-shrink: 0;

      ::v-deep i {
        margin-right: 3px;
      }
    }
  }

  .xr-tag {
    color: $--color-text-regular;
    border: none;
  }

  .list {
    margin-top: 20px;
    margin-bottom: 40px;

    &__header {
      font-size: 16px;
      font-weight: bold;
    }

    &__body {
      position: relative;
      max-height: 200px;
      margin-top: 10px;
      overflow: auto;
    }

    &__more {
      color: $--color-text-secondary;
      text-align: center;

      .el-button {
        font-size: 12px;
        color: $--color-text-secondary;
      }
    }

    &__item {
      padding: 5px 0;

      .text-one-line {
        padding: 0 8px;
      }
    }

    &__delete {
      display: none;
      flex-shrink: 0;
      color: #ccc;
      cursor: pointer;
    }

    &__delete:hover {
      color: #f94e4e;
    }

    &:hover {
      .list__delete {
        display: inline-block;
      }
    }
  }
}
</style>
