<template>
  <div v-loading="loading" class="d-container">
    <create-sections v-if="!isManage" title="考核评分明细" style="margin-top: 20px;" />
    <el-form
      ref="scoreFrom"
      :model="scoreFrom">
      <el-table
        :data="tableData"
        class="d-table"
        :stripe="false"
        :span-method="mergeMethod"
        style="width: 100%;"
        :style="{ 'margin-top': isManage ? '20px' : '0' }">
        <el-table-column
          v-for="(item,index) in tableFieldList"
          :key="index"
          :label="item.label"
          :prop="item.prop">
          <template slot-scope="{row,$index}">
            <el-form-item
              v-if="item.prop == 'score' && row.canFill"
              :prop="`quotaScoreInfoBOList[${$index}].score`"
              :rules="{required:true,message:'评分不能为空', trigger: 'blur'}">
              <el-input-number
                v-model="scoreFrom.quotaScoreInfoBOList[$index].score"
                style="width: 100%;"
                :controls="false"
                :min="0"
                :max="upperLimitScore"
                @change="scoreChange"
              />
            </el-form-item>
            <el-form-item v-if="item.prop == 'comments' && row.canFill">
              <el-input v-model="scoreFrom. quotaScoreInfoBOList[$index].comments" />
            </el-form-item>
            <span v-else>{{ item.prop=='score'&&row.canFill ? '' : row[item.prop] }}{{ item.prop == 'quotaWeight' ? '%' : '' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <create-sections
        v-if="activeTab != 2 && activeTab != 1 && !isManage"
        title="考核评语"
        style="margin-top: 10px;" />
      <div
        v-if="activeTab != 2 && !isManage"
        class="record">
        <el-form-item
          v-if="subTabType == 2 && activeTab != 5 && activeTab != 6 && activeTab != 7"
          prop="comments"
          :rules="isRequiredSetting ? {required:true, message: '考核评语不能为空', trigger: 'blur'} : {}">
          <el-input
            v-model="scoreFrom.comments"
            style="margin-top: 10px;"
            placeholder="添加评语..."
            type="textarea"
            resize="none" />
        </el-form-item>
        <template v-for="(item,index) in assessmentComments">
          <div
            v-if="!item.canFill"
            :key="index"
            class="grading-records">
            <div class="grading-records-user">
              <xr-avatar
                :name="item.employeeName"
                :size="40" />
              <div class="user-name">
                <span>{{ item.employeeName }}</span>
                <span>{{ item.comments }}</span>
              </div>
            </div>
            <div class="grading-records-result">
              <div class="records-result-item">
                <span>评分结果</span>
                <span>{{ item.result }}</span>
              </div>
              <div class="records-result-item">
                <span>权重</span>
                <span>{{ item.weight }}</span>
              </div>
            </div>
          </div>
        </template>
      </div>
    </el-form>
  </div>
</template>

<script>
import {
  quotaInformationAPI,
  quotaScoreAPI,
  targetConfirmationPass,
  resultAuditPass,
  resultConfirmation,
  resultAppealPass,
  preCalculationQuotaScore
} from '@/api/hrm/selfService/performance'

import CreateSections from '@/components/CreateSections'

import { objDeepCopy } from '@/utils'
export default {
  name: 'ScoreAndDetial',
  components: {
    CreateSections
  },
  inject: ['rootTabs'],
  props: {
    id: String,
    subTabType: [String, Number],
    activeTab: [String, Number],
    isManage: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      tableData: [],
      scoreFrom: {
        comments: '',
        quotaScoreInfoBOList: []
      },
      mergeCellsList: [], // 单元格列表

      assessmentComments: [], // 考核评语

      isRejectAuthority: false, // 驳回权限
      isRequiredSetting: true, // 考核评分权限

      upperLimitScore: null, // 评分上限
      isEnd: true // 是否最后一人评分
    }
  },
  computed: {
    tableFieldList() {
      const list = [
        { label: '维度', prop: 'dimensionName' },
        { label: '指标', prop: 'quotaName' },
        { label: '指标说明', prop: 'quotaIllustrate' },
        { label: '考核标准', prop: 'standard' },
        { label: '权重', prop: 'quotaWeight' }
      ]

      if (this.activeTab != 1 && this.activeTab != 2) {
        list.push(
          { label: '评分人', prop: 'employeeName' },
          { label: '评分', prop: 'score' },
          { label: '评语', prop: 'comments' }
        )
      }
      return list
    }
  },
  watch: {
    'rootTabs.currentName': {
      handler(val) {
        if (val === 'ScoreAndDetial') {
          this.getList()
        }
      },
      immediate: true
    },

    mergeCellsList: {
      handler(val) {
        if (val && val.length) {
          val.forEach(item => {
            item.achievementsQuotaVOList.forEach(sItem => {
              if (sItem.quotaEmployeeScoreVOList && sItem.quotaEmployeeScoreVOList.length) {
                sItem.quotaEmployeeScoreVOList.forEach(subItem => {
                  var obj = {}
                  obj['score'] = subItem.score
                  obj['comments'] = subItem.comments
                  obj['quotaId'] = subItem.quotaId
                  obj['canFill'] = subItem.canFill
                  this.scoreFrom.quotaScoreInfoBOList.push(objDeepCopy(obj))
                })
              }
            })
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    getList() {
      this.loading = true
      const tableData = []
      var checkData = [] // 考核维度数据
      const params = {
        appraisalEmployeeId: this.id,
        isScoring: this.subTabType == 2 && this.activeTab != 2 && this.activeTab != 5 && this.activeTab != 6 && this.activeTab != 7
      }
      quotaInformationAPI(params)
        .then(res => {
          checkData = res.data.dimensionVOList || []
          this.isEnd = res.data.isEnd
          this.mergeCellsList = checkData
          this.assessmentComments = res.data.scoreRecordVOList
          this.isRejectAuthority = res.data.rejectAuthority
          this.isRequiredSetting = res.data.requiredSetting
          this.upperLimitScore = res.data.upperLimitScore

          // 驳回自动填写考核评语
          this.assessmentComments.forEach(item => {
            if (item.canFill) {
              this.$set(this.scoreFrom, 'comments', item.comments)
            }
          })

          this.$emit('rejectAuthority', this.isRejectAuthority)
          checkData.forEach(item => {
            const obj = {}
            obj['dimensionName'] = item.dimensionName
            if (item.achievementsQuotaVOList && item.achievementsQuotaVOList.length) {
              item.achievementsQuotaVOList.forEach(sItem => {
                obj['quotaName'] = sItem.quotaName
                obj['quotaIllustrate'] = sItem.quotaIllustrate
                obj['standard'] = sItem.standard
                obj['quotaWeight'] = sItem.quotaWeight
                if (sItem.quotaEmployeeScoreVOList && sItem.quotaEmployeeScoreVOList.length) {
                  sItem.quotaEmployeeScoreVOList.forEach(scoreItem => {
                    obj['employeeName'] = scoreItem.employeeName
                    obj['score'] = scoreItem.score
                    obj['comments'] = scoreItem.comments
                    obj['canFill'] = scoreItem.canFill
                    tableData.push(objDeepCopy(obj))
                  })
                } else {
                  tableData.push(objDeepCopy(obj))
                }
              })
            }
          })
          this.tableData = tableData
          this.$nextTick(() => {
            this.loading = false
          })
        })
        .catch(() => {
          this.$emit('close')
          this.loading = false
        })
    },

    /**
     * 合并单元格
     */
    mergeMethod({ row, column, rowIndex, columnIndex }) {
      const rowSpanObj = {}
      const rowOtherSpanObj = {}
      const childList = []
      this.mergeCellsList.forEach(item => {
        item.firstList = []
        childList.push(...item.achievementsQuotaVOList)
        item.achievementsQuotaVOList.forEach(subItem => {
          item.firstList.push(...subItem.quotaEmployeeScoreVOList)
        })
      })

      this.mergeCellsList.forEach((item, index) => {
        if (item.achievementsQuotaVOList[0].quotaEmployeeScoreVOList && item.achievementsQuotaVOList[0].quotaEmployeeScoreVOList.length != 1 && item.achievementsQuotaVOList[0].quotaEmployeeScoreVOList.length !== 0) {
          childList.forEach((subItem, subIndex) => {
            subItem.quotaEmployeeScoreVOList.forEach((childItem, childIndex) => {
              if (subIndex === 0 && childIndex === 0) {
                rowOtherSpanObj[childIndex] = subItem.quotaEmployeeScoreVOList.length
              } else if (subIndex === 0 && childIndex !== 0) {
                rowOtherSpanObj[childIndex] = 0
              } else if (subIndex !== 0 && childIndex === 0) {
                rowOtherSpanObj[this.getNewLength(subIndex, childList, 'quotaEmployeeScoreVOList')] = subItem.quotaEmployeeScoreVOList.length
              } else if (subIndex !== 0 && childIndex !== 0) {
                rowOtherSpanObj[childIndex + this.getNewLength(subIndex, childList, 'quotaEmployeeScoreVOList')] = 0
              }
            })
          })
          item.firstList.forEach((subItem, subIndex) => {
            if (index === 0 && subIndex === 0) {
              rowSpanObj[subIndex] = item.firstList.length
            } else if (index === 0 && subIndex !== 0) {
              rowSpanObj[subIndex] = 0
            } else if (index !== 0 && subIndex === 0) {
              rowSpanObj[this.getNewLength(index, this.mergeCellsList, 'firstList')] = item.firstList.length
            } else if (index !== 0 && subIndex !== 0) {
              rowSpanObj[subIndex + this.getNewLength(index, this.mergeCellsList, 'firstList')] = 0
            }
          })
        } else {
          item.achievementsQuotaVOList.forEach((subItem, subIndex) => {
            if (index === 0 && subIndex === 0) {
              rowSpanObj[subIndex] = item.achievementsQuotaVOList.length
              rowOtherSpanObj[subIndex] = 1
            } else if (index === 0 && subIndex !== 0) {
              rowSpanObj[subIndex] = 0
              rowOtherSpanObj[subIndex] = 1
            } else if (index !== 0 && subIndex === 0) {
              rowSpanObj[this.getNewLength(index, this.mergeCellsList, 'achievementsQuotaVOList')] = item.achievementsQuotaVOList.length
              rowOtherSpanObj[this.getNewLength(index, this.mergeCellsList, 'achievementsQuotaVOList')] = 1
            } else if (index !== 0 && subIndex !== 0) {
              rowSpanObj[subIndex + this.getNewLength(index, this.mergeCellsList, 'achievementsQuotaVOList')] = 0
              rowOtherSpanObj[subIndex + this.getNewLength(index, this.mergeCellsList, 'achievementsQuotaVOList')] = 1
            }
          })
        }
      })

      if (columnIndex === 0) {
        return {
          rowspan: rowSpanObj[rowIndex],
          colspan: rowSpanObj[rowIndex] === 0 ? 0 : 1
        }
      } else if (columnIndex === 1 || columnIndex === 2 || columnIndex === 3 || columnIndex === 4) {
        return {
          rowspan: rowOtherSpanObj[rowIndex],
          colspan: rowOtherSpanObj[rowIndex] === 0 ? 0 : 1
        }
      } else {
        return {
          rowspan: 1,
          colspan: 1
        }
      }
    },

    getNewLength(index, arr, addArr) {
      let length = 0
      arr.forEach((item, i) => {
        if (i < index) {
          length += item[addArr].length
        }
      })
      return length
    },

    /**
     * 点击保存
     */
    sendClick() {
      var request
      var params
      if (this.activeTab == 2) {
        request = targetConfirmationPass
        params = { appraisalEmployeeId: this.id }
      } else if (this.activeTab == 4) {
        request = quotaScoreAPI
        const data = objDeepCopy(this.scoreFrom)
        data.quotaScoreInfoBOList = data.quotaScoreInfoBOList.filter(item => item.canFill)
        data['appraisalEmployeeId'] = this.id
        params = data
      } else if (this.activeTab == 5) {
        request = resultAuditPass
        params = { appraisalEmployeeId: this.id }
      } else if (this.activeTab == 6) {
        request = resultConfirmation
        params = { appraisalEmployeeId: this.id }
      } else if (this.activeTab == 7) {
        request = resultAppealPass
        params = { appraisalEmployeeId: this.id }
      }
      console.log(params, 'params')
      this.$refs.scoreFrom.validate(valid => {
        if (valid) {
          this.loading = true
          request(params)
            .then(res => {
              this.$message.success('保存成功')
              this.loading = false
              this.close()
            })
            .catch(() => {
              this.loading = false
              this.close()
            })
        } else {
          this.$message.error('请完善信息')
        }
      })
    },

    /**
     * 实时评分
     */
    scoreChange(val) {
      let flag = false
      const data = objDeepCopy(this.scoreFrom)
      data.quotaScoreInfoBOList = data.quotaScoreInfoBOList.filter(item => item.canFill)
      data['appraisalEmployeeId'] = this.id
      data.quotaScoreInfoBOList.forEach(item => {
        if (!item.hasOwnProperty('score') || (!item.score && item.score != 0)) {
          flag = true
        }
      })
      console.log(flag)
      if (!this.isEnd || (!val && val != 0) || flag) {
        console.log('12312')
        this.$emit('realTime', { assessmentScore: null, level: null })
        return
      }

      preCalculationQuotaScore(data)
        .then(res => {
          const data = {
            assessmentScore: res.data.scoreOfTotal,
            level: res.data.levelNameOfTotal
          }
          this.$emit('realTime', data)
        })
    },

    close() {
      this.$emit('close')
    }
  }
}
</script>

<style scope lang="scss">
.d-container {
  .d-table .el-table__body-wrapper tr {
    &:hover td {
      background-color: unset;
    }

    & .el-form-item {
      margin-bottom: 0 !important;
    }
  }

  .record {
    .grading-records {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 80px;
      padding: 0 24px;
      margin-top: 20px;
      border: 1px solid #dfe1e6;

      // box-shadow: 0px 11px 15px 5px rgba(0,0,0,0.75);
      // -webkit-box-shadow: 0px 11px 15px 5px rgba(0,0,0,0.75);
      // -moz-box-shadow: 0px 11px 15px 5px rgba(0,0,0,0.75);
      .grading-records-user {
        display: flex;
        justify-content: space-between;

        .user-name {
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          margin-left: 16px;

          span {
            &:nth-of-type(1) {
              font-size: 18px;
              font-weight: bold;
              color: #172b4d;
            }

            &:nth-of-type(2) {
              font-size: 14px;
              color: #6b778c;
            }
          }
        }
      }

      .grading-records-result {
        display: flex;
        justify-content: space-between;

        .records-result-item {
          margin-left: 20px;

          span {
            &:nth-of-type(1) {
              color: #6b778c;
            }

            &:nth-of-type(2) {
              color: #172b4d;
            }
          }
        }
      }
    }
  }
}

</style>
