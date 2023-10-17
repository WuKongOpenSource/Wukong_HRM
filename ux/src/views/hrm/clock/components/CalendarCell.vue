<template>
  <div v-if="!isEmpty(item)" class="calendar-cell">
    <div
      v-for="(item, index) in getRenderData()"
      :key="index"
      :style="{'color':statusMap[item.status]['color'],
               'background-color':statusMap[item.status]['backgroundColor']}"
      class="cell">
      <span v-if="item.time">{{ item.time }}</span>
      <span v-if="item.content">{{ item.content }}</span>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'
import clockStatusLib from '../clockStatusLib'

export default {
  props: {
    item: Object
  },
  data() {
    return {
      isEmpty,
      statusMap: clockStatusLib
    }
  },
  methods: {
    getRenderData() {
      const item = this.item
      // if (item.shiftType == 0) {
      //   return [{
      //     tiem: '',
      //     content: '休息',
      //     status: 9
      //   }]
      // }
      const arr = []
      for (let index = 1; index <= 3; index++) {
        if (item.shiftType == 0 && item['startStatus' + index] == 9 && item['endStatus' + index] == 9) {
          return [{
            tiem: '',
            content: '休息',
            status: 9
          }]
        }

        if (item['startStatus' + index] == 3 && item['endStatus' + index] == 3) {
          return [{
            tiem: '',
            content: '旷工',
            status: 4
          }]
        }

        if (item['start' + index] || item['startStatus' + index] != null) {
          arr.push({
            time: item['start' + index],
            status: item['startStatus' + index] == -1 ? 3 : item['startStatus' + index],
            content: this.statusMap[item['startStatus' + index]].label
          })
        }
        if (item['end' + index] || item['endStatus' + index] != null) {
          arr.push({
            time: item['end' + index],
            status: item['endStatus' + index] == -1 ? 3 : item['endStatus' + index],
            content: this.statusMap[item['endStatus' + index]].label
          })
        }
      }
      return arr
    }
  }
}
</script>

<style lang="scss" scoped>
.calendar-cell {
  box-sizing: border-box;
}

.cell {
  display: flex;
  justify-content: space-between;
  padding: 2px 5px;
  margin-top: 2px;
}
</style>
