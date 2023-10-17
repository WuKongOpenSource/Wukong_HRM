<template>
  <div class="attendance-date">
    <div class="attendance-date-work">
      <p>{{ dataValue.work }}</p>
      <!-- <p>{{ dataValue.rest }}</p> -->
    </div>
    <div>
      <p v-for="(item,index) in dataValue.shiftList" :key="index">
        {{ item.shiftDetail }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    attendanceValue: {
      type: Object
    }
  },
  computed: {
    dataValue() {
      const rest = []
      const work = []
      const weeks = ['一', '二', '三', '四', '五', '六', '日']
      const shift = []
      for (const key in this.attendanceValue) {
        if (this.attendanceValue[key]['shiftType'] == 0) {
          rest.push(weeks[key - 1])
        } else {
          work.push(weeks[key - 1])
        }
        shift.push(this.attendanceValue[key])
      }
      const shiftList = shift.filter((item, index, self) => {
        return self.findIndex(el => el.shiftId == item.shiftId) === index && item.shiftType
      })
      return {
        rest: rest.join(' 、'),
        work: work.join(' 、'),
        shiftList
      }
    }
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
.attendance-date {
  display: flex;
  flex-direction: column;
}
</style>
