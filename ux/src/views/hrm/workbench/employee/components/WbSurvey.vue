<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section
    :move="false"
    class="wb-survey-wrap"
  >
    <flexbox v-if="detail" class="wb-survey" align="stretch">
      <div class="wb-survey__left">
        <xr-avatar
          :key="detail.employeeName"
          :name="detail.employeeName"
          :size="60"
          :src="detail.img" />
      </div>
      <div class="wb-survey__right user">
        <div class="user__name">Hi，{{ detail.employeeName }}</div>
        <div class="user__des">这是你在{{ detail.companyName }}的第<span>{{ detail.entryDay }}</span>天</div>
        <div class="user__content">
          <div class="user-item">部门  <span>{{ detail.deptName }}</span>，岗位  <span>{{ detail.post }}</span>，</div>
          <div class="user-item">工号  <span>{{ detail.jobNumber }}</span>，</div>
          <div class="user-item"><span>{{ detail.entryTime | moment('YYYY-MM-DD') }}</span>入职<template v-if="detail.becomeTimeShow">，</template></div>
          <div v-if="detail.becomeTimeShow" class="user-item">将于<span>{{ detail.becomeTime | moment('YYYY-MM-DD') }}</span>转正</div>
          <div v-if="detail.slipRemarks" class="user-item is-visit" @click="enterMySlip">{{ detail.slipRemarks }}</div>
        </div>
      </div>
    </flexbox>
  </wb-section>
</template>

<script>
import {
  hrmDashboardMySurveyAPI
} from '@/api/hrm/workbench'

import WbSection from '../../components/WbSection'

import moment from 'moment'

export default {
  // 员工概况
  name: 'WbSurvey',
  components: {
    WbSection
  },
  props: {},
  data() {
    return {
      detail: null
    }
  },
  computed: {},
  watch: {},
  created() {
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getDetail() {
      hrmDashboardMySurveyAPI()
        .then(res => {
          const detail = res.data || {}
          if (detail.becomeTime && moment().isBefore(moment(detail.becomeTime))) {
            detail.becomeTimeShow = true
          } else {
            detail.becomeTimeShow = false
          }
          this.detail = detail
        })
        .catch(() => {
        })
    },

    /**
     * 进入我的工资条
     */
    enterMySlip() {
      this.$router.push({
        name: 'mySalarySlip'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-survey-wrap {
  min-height: 130px;

  .wb-survey {
    &__left {
      display: inline-block;
      flex-shrink: 0;
      width: 100px;
    }

    &__right {
      display: inline-block;
      flex: 1;
      padding-top: 5px;
    }

    .user {
      span {
        font-weight: bold;
      }

      &__name {
        font-size: 20px;
        font-weight: bold;
      }

      &__des {
        margin-top: 10px;
      }

      &__content {
        margin-top: 20px;

        .user-item {
          margin-top: 8px;

          &.is-visit {
            color: $--color-primary;
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>
