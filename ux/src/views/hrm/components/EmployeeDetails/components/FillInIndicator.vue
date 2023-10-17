<template>
  <div v-loading="loading">
    <p style="padding: 12px 12px 0;margin-top: 10px;">当前维度权重总计<span style="color: #ffb55f;">{{ weightSum }}%</span></p>
    <assessment-dimension
      v-if="assessmentDimensionList && assessmentDimensionList.length"
      ref="dimensionalityForm"
      :quota-item="quotaItem"
      :assessment-dimension-list="assessmentDimensionList"
      :operation="true" />
  </div>
</template>

<script>
import {
  quotaInformationAPI,
  fillInQuotaAPI
} from '@/api/hrm/selfService/performance'

import AssessmentDimension from '@/views/hrm/components/AssessmentDimension'
export default {
  name: 'FillInIndicator',
  components: {
    AssessmentDimension
  },
  inject: ['rootTabs'],
  props: {
    id: String
  },
  data() {
    return {
      loading: false,
      assessmentDimensionList: [],
      quotaItem: {}
    }
  },
  computed: {
    weightSum() {
      var sum = 0
      this.assessmentDimensionList.forEach(item => {
        sum += item.dimensionWeight
      })
      return sum
    }
  },
  watch: {
    'rootTabs.currentName': {
      handler(val) {
        if (val === 'FillInIndicator') {
          this.assessmentDimensionList = []
          this.quotaItem = {}
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    getList() {
      this.loading = true
      quotaInformationAPI({ appraisalEmployeeId: this.id, isScoring: false })
        .then(res => {
          const data = res.data.dimensionVOList || []
          data.forEach((item, index) => {
            this.quotaItem['table' + (+new Date() + index) ] = []
            this.assessmentDimensionList.push({
              dimensionName: item.dimensionName,
              dimensionWeight: item.dimensionWeight,
              quotaType: item.quotaType,
              isAllowEdit: item.isAllowEdit,
              dimensionId: item.dimensionId,
              key: +new Date() + index
            })
            this.quotaItem['table' + (+new Date() + index) ].push(...item.achievementsQuotaVOList)
          })
          for (var sKey in this.quotaItem) {
            this.quotaItem[sKey].push({})
          }
          this.loading = false
        })
        .catch(() => {
          this.$emit('close')
          this.loading = false
        })
    },

    /**
     *
     */
    sendClick() {
      var flag = true
      var weightSum = true

      this.assessmentDimensionList.forEach((item, index) => {
        if (item.isAllowEdit) {
          var sum = 0
          this.$refs.dimensionalityForm.tableForm['table' + item.key].forEach(sItem => {
            sum += sItem.quotaWeight || 0
          })
          if (sum !== 100) {
            weightSum = false
          }
        }
      })

      console.log(this.$refs.dimensionalityForm.$refs.form)
      this.$refs.dimensionalityForm.$refs.form.forEach(item => {
        item.validate(valid => {
          if (flag) {
            if (!valid) {
              this.$message.error('请完善信息')
            } else if (!weightSum) {
              this.$message.error('指标权重总和需等于100%')
            } else if (weightSum && valid) {
              var appraisalEmployeeDimensionBOList = []
              this.assessmentDimensionList.forEach((item, index) => {
                if (item.isAllowEdit) {
                  appraisalEmployeeDimensionBOList.push({
                    ...item,
                    appraisalEmployeeQuotaBOList: this.$refs.dimensionalityForm.tableForm['table' + item.key].filter(item => Object.keys(item).length && !item.preset)
                  })
                }
              })
              this.submiteParams(appraisalEmployeeDimensionBOList)
            }
            flag = false
          }
        })
      })
    },

    submiteParams(appraisalEmployeeDimensionBOList) {
      const params = {
        appraisalEmployeeDimensionBOList,
        appraisalEmployeeId: this.id
      }
      fillInQuotaAPI(params)
        .then(res => {
          this.$message.success('保存成功')
          this.close()
        })
        .catch(() => {
          this.close()
        })
    },

    /**
     * 关闭弹窗
     */
    close() {
      this.$emit('close')
    }
  }
}
</script>

<style>

</style>
