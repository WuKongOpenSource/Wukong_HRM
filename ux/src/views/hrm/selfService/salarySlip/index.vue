<template>
  <div v-loading="loading" class="my-slip">
    <xr-header
      ft-top="0"
      label="我的工资条">
      <template slot="ft">
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom-end"
          @sure="getList"
          @reset="resetFilter"
        />
      </template>
    </xr-header>
    <div class="my-slip__body">
      <div v-if="sectionList.length === 0" class="empty-text">暂无数据</div>
      <el-table
        v-for="(item, index) in sectionList"
        :key="index"
        :data="[item.data]"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        style="width: 100%;">
        <el-table-column
          v-for="(field, fieldIndex) in item.fieldList"
          :key="fieldIndex"
          :min-width="fieldIndex === 0 ? '100px' : '80px'"
          :label="field.label"
          :prop="field.prop"
          show-overflow-tooltip>
          <el-table-column
            v-for="(childField, childFieldIndex) in field.children"
            :key="childFieldIndex"
            :prop="childField.prop">
            <template
              slot="header"
              slot-scope="{}">
              {{ childField.label }}
              <el-tooltip
                v-if="childField.remark"
                :content="childField.remark"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import {
  hrmSalarySlipQueryMyListAPI
} from '@/api/hrm/selfService/salarySlip'

import XrHeader from '@/components/XrHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'

import { mapGetters } from 'vuex'

export default {
  // 我的工资条
  name: 'MySlip',

  components: {
    XrHeader,
    WkPopoverFilter
  },

  props: {},

  data() {
    return {
      loading: false,
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      filterList: [
        {
          name: '时间范围',
          field: 'time',
          formType: 'dateRange',
          dateType: 'monthrange',
          dateValueFormat: 'yyyy-MM',
          setting: []
        }, {
          name: '排序',
          field: 'sort',
          formType: 'select',
          setting: [{
            label: '按最近发放',
            value: 1
          }, {
            label: '按最早发放',
            value: 2
          }, {
            label: '按实发工资升序',
            value: 3
          }, {
            label: '按实发工资降序',
            value: 4
          }]
        }
      ],
      filterObj: {}, // 筛选确定数据
      sectionList: []
    }
  },

  computed: {
    ...mapGetters([
    ]),
    // 有筛选内容
    hasFilterContent() {
      if (this.filterObj) {
        let hasContent = false
        const keys = Object.keys(this.filterObj)
        for (let index = 0; index < keys.length; index++) {
          const key = keys[index]
          if (this.filterObj[key] != '' &&
           this.filterObj[key] != [] &&
           this.filterObj[key] != null &&
           this.filterObj[key] != undefined) {
            hasContent = true
          }
        }
        return hasContent
      }
      return false
    }
  },

  watch: {},

  created() {
    this.getList()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.getList()
    },

    /**
     * 获取列表数据
     */
    getList() {
      this.loading = true
      const params = {
        pageType: 0
      }

      /**
       * order	1 倒序 2 正序
       * orderType	排序类型 1 发放时间 2 实发工资
       */
      if (this.filterObj.sort == 1 || this.filterObj.sort == 2 || !this.filterObj.sort) {
        params.orderType = 1
        params.order = this.filterObj.sort == 2 ? 2 : 1
      } else if (this.filterObj.sort == 3 || this.filterObj.sort == 4) {
        params.orderType = 2
        params.order = this.filterObj.sort == 4 ? 1 : 2
      }

      if (this.filterObj.time && this.filterObj.time.length > 0) {
        params.startTime = this.filterObj.time[0]
        params.endTime = this.filterObj.time[1]
      }

      hrmSalarySlipQueryMyListAPI(params)
        .then(res => {
          this.loading = false
          const list = res.data.list || []
          const sectionList = []
          list.forEach(sectionItem => {
            const data = {
              title: `${sectionItem.year}-${sectionItem.month}`
            }
            const fieldList = [{
              label: '所属月份',
              prop: 'title'
            }]
            const salarySlipOptionList = sectionItem.salarySlipOptionList || []
            salarySlipOptionList.forEach(item => {
              const fieldObj = {
                label: item.name,
                children: []
              }
              fieldList.push(fieldObj)

              const optionList = item.optionList || []
              optionList.forEach(subItem => {
                const valueKey = `value${subItem.id}`
                fieldObj.children.push({
                  remark: subItem.remark,
                  label: subItem.name,
                  prop: valueKey
                })
                data[valueKey] = subItem.value
              })
            })

            sectionList.push({
              data,
              fieldList
            })
          })

          this.sectionList = sectionList
        })
        .catch(() => {
          this.loading = false
        })
    }

  }
}
</script>

<style lang="scss" scoped>
.my-slip {
  height: 100%;

  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  &__body {
    position: relative;

    .empty-text {
      position: absolute;
      top: 50%;
      width: 100%;
      color: $--color-text-secondary;
      text-align: center;
    }

    height: calc(100% - 50px);
    overflow-y: auto;

    .el-table + .el-table {
      margin-top: 30px;
    }
  }
}
</style>
