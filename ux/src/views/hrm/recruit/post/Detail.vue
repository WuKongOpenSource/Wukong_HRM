<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 13:37:31
 * @LastEditTime: 2020-07-03 18:54:54
 * @LastEditors: yang
-->
<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header
        :detail-value="detailData.postName"
        :show-edit="editAuth"
        icon-class="wk wk-office"
        detail-name="职位"
        icon-bg-color="#ECEEF2"
        icon-color="#42526E"
        @edit="editClick"
        @command-select="commandSelect"
      />
      <el-tabs
        v-model="tabCurrentName"
        style="flex: 1;"
        class="side-detail__tabs--default">
        <el-tab-pane
          label="详细资料"
          name="baseInfo"
          lazy>
          <wk-base-detail
            :list="baseList"
          />
        </el-tab-pane>
        <el-tab-pane
          label="操作记录"
          name="handle"
          lazy>
          <post-handle
            :id="id"
          />
        </el-tab-pane>
      </el-tabs>
    </flexbox>

    <post-create-view
      v-if="isCreate"
      :detail="detailData"
      @success="getDetial"
      @close="isCreate=false" />
  </slide-view>
</template>

<script>
import {
  hrmRecruitPostQueryByIdAPI
} from '@/api/hrm/recruit/post'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/WkDetailHeader'
import WkBaseDetail from '@/components/WkBaseDetail'
import PostHandle from './components/PostHandle'
import PostCreateView from './Create'
import postModel from '../model/post'

export default {
  // 职位详情
  name: 'PostDetail',
  components: {
    SlideView,
    WkDetailHeader,
    WkBaseDetail,
    PostHandle,
    PostCreateView
  },

  inject: ['editAuth'],
  props: {
    // 详情信息id
    id: [String, Number],
    // 监听的dom 进行隐藏详情
    listenerIDs: {
      type: Array,
      default: () => {
        return ['crm-main-container']
      }
    },
    // 不监听
    noListenerIDs: {
      type: Array,
      default: () => {
        return []
      }
    },
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body', 'org-tree-node-label']
      }
    }
  },
  data() {
    return {
      loading: false,
      detailData: null,
      tabCurrentName: 'baseInfo',
      dropdownItems: [{
        icon: 'wk wk-edit',
        label: '编辑',
        command: 'edit'
      }],
      // 编辑操作
      isCreate: false
    }
  },

  computed: {
    baseList() {
      if (this.detailData) {
        const fieldList = []
        for (let index = 0; index < postModel.fields.length; index++) {
          const field = postModel.fields[index]
          const temp = {}
          temp.label = field.name
          temp.value = this.fieldFormatter(this.detailData, field.field)
          fieldList.push(temp)
        }
        fieldList.push({
          label: '状态',
          value: this.fieldFormatter(this.detailData, 'status')
        })
        return [
          {
            name: '',
            list: fieldList
          }]
      }
      return []
    }
  },
  watch: {
    id() {
      this.detailData = null
      this.getDetial()
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    viewAfterEnter() {
      this.getDetial()
    },

    /**
     * 详情
     */
    getDetial() {
      this.loading = true
      hrmRecruitPostQueryByIdAPI(this.id)
        .then(res => {
          this.detailData = res.data || {}
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, field) {
      const valueObj = postModel[`${field}Value`]
      if (valueObj) {
        return valueObj[row[field]] || '--'
      } else if (field == 'ownerEmployeeId') {
        return row.ownerEmployeeName
      } else if (field == 'interviewEmployeeIds') {
        return row.interviewEmployeeName
      } else if (field == 'deptId') {
        return row.deptName
      } else if (field == 'postTypeId') {
        return row.postTypeName
      } else if (field == 'status') {
        return {
          1: '招聘中',
          0: '停止招聘'
        }[row[field] ] || '--'
      } else if (field == 'salary') {
        if (row.salaryUnit == -1) {
          return '面议'
        }
        return `${row.minSalary || ''}-${row.maxSalary || ''} ${postModel.salaryUnitValue[row.salaryUnit] || ''}`
      } else if (field == 'age') {
        if (row.minAge == -1 && row.maxAge == -1) {
          return '不限'
        }
        return `${row.minAge || ''}-${row.maxAge || ''}`
      } else {
        return row[field] || '--'
      }
    },

    /**
     * 编辑
     */
    editClick() {
      this.isCreate = true
    },

    editSuccess() {
      this.getDetial()
      this.$emit('edit-success')
      this.$emit('handle', { type: 'edit' })
    },

    /**
     * 更多操作
     */
    commandSelect(command) {},

    /**
     * 关闭
     */
    hideView() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.side-detail__tabs--default {
  padding: 0 20px;
}
</style>
