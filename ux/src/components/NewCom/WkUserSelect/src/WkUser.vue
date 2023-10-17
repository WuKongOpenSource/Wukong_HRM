<template>
  <div class="xh-user">
    <div v-if="headerShow" class="xh-user__hd">
      员工
    </div>
    <div class="xh-user__bd">
      <el-autocomplete
        v-model="searchInput"
        :disabled="disabled"
        :popper-append-to-body="false"
        :fetch-suggestions="searchListUser"
        placeholder="请输入内容"
        @select="searchSelect"
      >
        <template slot-scope="{ item }">
          <span>{{ $getUserName(item, config.label) }}</span>
          <span style="color: #6b778c;">{{ `(${ $getDeptName(item) || ''}${$getDeptName(item) ? '-' : ''}${item.post || '无'})` }}</span>
        </template>
      </el-autocomplete>
      <div
        class="search-list">
        <el-breadcrumb
          v-if="breadcrumbList.length > 0 && !config.isList"
          style="padding: 5px 0;"
          separator-class="el-icon-arrow-right">
          <el-breadcrumb-item
            v-for="(item, index) in breadcrumbList"
            :key="index">
            <a
              href="javascript:;"
              @click="breadcrumbBtn(item, index)">{{ item.label }}</a>
          </el-breadcrumb-item>
        </el-breadcrumb>

        <div v-if="currentBreadcrumbItem" class="xh-user__list">
          <el-checkbox
            v-if="!radio"
            v-model="allChecked"
            :disabled="disabled"
            style="line-height: 30px;"
            @change="handleCheckAllChange">全选</el-checkbox>
          <wk-dep-checkbox
            :radio="radio"
            :data="currentBreadcrumbItem.deptList"
            :props="props"
            :disabled="disabled"
            only-show
            @next="nextDebounceClick"
          />

          <wk-user-checkbox
            ref="userCheckbox"
            :radio="radio"
            :data="currentBreadcrumbItem.employeeList"
            :props="props"
            :value="value"
            :disabled="disabled"
            @input="$emit('input', $event)"
            @change="checkboxChange"
            @all-select="userAllSelect"
          />

        </div>

      </div>
    </div>
    <div class="xh-user__ft">
      <span class="select-info">已选择<span class="select-info--num">{{ value ? value.length : 0 }}</span>项</span>
      <el-button type="text" @click="clearAll">清空</el-button>
    </div>
  </div>
</template>
<script type="text/javascript">
import {
  adminUserQueryByDeptAPI
} from '@/api/admin/user'
import {
  hrmEmployeeQueryInAPI,
  hrmEmployeeQueryByDeptAPI
} from '@/api/hrm/employee'
import { userListAPI } from '@/api/common'

import {
  queryInspectionDeptEmployeeList,
  queryInspectionAllEmployeeList } from '@/api/hrm/performance'

import WkDepCheckbox from './WkDepCheckbox'
import WkUserCheckbox from './WkUserCheckbox'

import PinyinMatch from 'pinyin-match'
import merge from '@/utils/merge'
import { debounce } from 'throttle-debounce'
import { getSearchRequestWithRequest } from './utils'

const DefaultWkUser = {
  value: 'userId',
  label: 'realname',
  // 搜索
  // 请求和参数 暂时通过 request name 匹配对应的搜索请求
  searchRequest: null,
  searchParams: null,
  // 树结构请求和参数
  request: null,
  params: null,
  // 默认的搜索人资和管理端人员请求
  dataType: 'manage', // 部门的 value label 一致，用 dataType 区分 manage hrm,
  // userOptions: null, // 固定数据，后可以放入 props options
  isList: false // 默认是树形接口，如果是列需设置为true
}

export default {
  name: 'WkUser', // 新建 user
  components: {
    WkDepCheckbox,
    WkUserCheckbox
  },
  inheritAttrs: false,
  props: {
    radio: Boolean,
    headerShow: {
      type: Boolean,
      default: true
    },
    options: Array,
    value: Array,
    type: {
      type: String,
      default: 'user' // user  dep  用户和部门
    },
    // 取值字段
    props: {
      type: Object,
      default: () => {
        return {}
      }
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      searchInput: '',
      searchUserList: [],

      // 面包头
      breadcrumbList: [],

      // 当前面包屑
      currentBreadcrumbItem: null,
      // 全选
      allChecked: false,
      requstOptions: null // props options 传入数据， requstOptions 请求获取的数据
    }
  },
  computed: {
    config() {
      return merge({ ...DefaultWkUser }, this.props || {})
    },

    // 暂时一维数组数据源
    userOptions() {
      return this.requstOptions || this.options
    }
  },
  watch: {
    breadcrumbList() {
      if (!this.config.isList) {
        this.currentBreadcrumbItem = this.breadcrumbList[this.breadcrumbList.length - 1]
      }
    },

    options: {
      handler() {
        if (this.config.isList) {
          this.currentBreadcrumbItem = {
            employeeList: this.options
          }
        }
      },
      immediate: true
    }
  },
  created() {
    if (!this.config.isList) {
      this.getDepUserList(0)
      this.nextDebounceClick = debounce(300, this.nextClick)
    } else {
      if (this.config.request) {
        this.getUserOptions()
      }
      this.nextDebounceClick = () => {}
    }
  },
  methods: {
    /**
     * 列效果进行搜索
     */
    searchListUser(queryString, cb) {
      if (this.searchUserList && this.searchUserList.length || this.userOptions) {
        const searchUserList = this.userOptions || this.searchUserList
        if (queryString) {
          cb(searchUserList.filter(item => {
            return PinyinMatch.match(this.$getUserName(item, this.config.label) || '', queryString) ||
              PinyinMatch.match(this.$getDeptName(item) || '', queryString)
          }))
        } else {
          cb(searchUserList)
        }
      } else {
        const request = this.getSearchRequest()
        let params = { pageType: 0 }
        if (this.config.searchParams) {
          params = { ...params, ...this.config.searchParams }
        }
        request(params).then(res => {
          const resData = res.data
          this.searchUserList = resData.hasOwnProperty('list') ? (resData.list || []) : (resData || [])
          if (queryString) {
            cb(this.searchUserList.filter(item => PinyinMatch.match(this.$getUserName(item, this.config.label) || '', queryString)))
          } else {
            cb(this.searchUserList)
          }
        }).catch(() => {})
      }
    },

    /**
     * 获取搜索请求
     * 根据列表请求对应获取，如果 props searchParams 有值，以 searchParams 为准
     */
    getSearchRequest() {
      if (this.props.isKpi) {
        return queryInspectionAllEmployeeList
      }

      // 人资的默认搜索接口
      if (this.config.dataType === 'hrm') {
        return hrmEmployeeQueryInAPI
      }

      if (this.config.searchRequest) {
        return this.config.searchRequest
      }

      return getSearchRequestWithRequest(this.config.request) || userListAPI
    },

    /**
     * 搜索选择
     */
    searchSelect(item) {
      this.$refs.userCheckbox.searchSelect(item)
    },

    /**
     * 勾选
     */
    checkboxChange(val) {
      if (this.radio) {
        this.$emit('input', val.length ? [val[val.length - 1]] : [])

        // 单选直接关闭窗口
        this.$emit('close')
      } else {
        this.$emit('input', val)
      }

      this.$nextTick(() => {
        this.$emit('change', val)
      })
    },

    /**
     * 全部勾选
     */
    handleCheckAllChange(val) {
      if (this.$refs.userCheckbox) {
        this.$refs.userCheckbox.handleCheckAllChange(val)
      }
    },

    /**
     * 用户全选
     */
    userAllSelect(allChecked) {
      if (this.allChecked != allChecked) {
        this.allChecked = allChecked
      }
    },

    /**
     * 清空全部
     */
    clearAll() {
      this.$emit('input', [])
    },

    /**
     * 获取部门员工数据
     */
    getDepUserList(deptId, depInfo) {
      this.loading = true
      const request = this.config.isKpi ? queryInspectionDeptEmployeeList : this.config.dataType === 'hrm' ? hrmEmployeeQueryByDeptAPI : (this.config.request || adminUserQueryByDeptAPI)
      request(deptId).then(res => {
        const data = res.data || {}
        const deptList = data.deptList || []

        // employeeList 人资  userList 系统管理
        const employeeList = data.employeeList || data.userList || []

        if (deptId == 0) {
          this.breadcrumbList = [{ label: '全部', deptList: deptList, employeeList: employeeList }]
        } else {
          this.breadcrumbList.push({ label: depInfo.name, deptList: deptList, employeeList: employeeList })
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 获取 isList 并且有 request 的数据
     */
    getUserOptions() {
      this.loading = true
      this.config.request(this.config.params).then(res => {
        const data = res.data || []
        this.requstOptions = data
        this.currentBreadcrumbItem = {
          employeeList: data
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 下一级
     */
    nextClick(item) {
      if (!this.radio) {
        this.allChecked = false
      }
      this.getDepUserList(item.deptId, item)
    },

    /**
     * 面包屑点击
     */
    breadcrumbBtn(item, index) {
      if (index + 1 <= this.breadcrumbList.length - 1) {
        this.breadcrumbList.splice(index + 1, this.breadcrumbList.length - 1)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
/* 选择员工 */

.search-list {
  height: 248px;
  padding: 5px 0;
  overflow: auto;
}

.xh-user {
  font-size: $--font-size-base;

  &__hd {
    height: 40px;
    padding: 0 15px;
    font-size: 16px;
    line-height: 40px;
    border-bottom: 1px solid $--border-color-base;
  }

  &__bd {
    padding: 10px 12px;
  }

  &__ft {
    padding: 5px 12px;
    background: #f7f8fa;
    border-top: 1px solid $--border-color-base;

    .el-button {
      font-size: 12px;
    }
  }

  &__list {
    height: calc(100% - 24px);
    padding-left: 5px;
    overflow-y: auto;
  }
}

// 选择信息
.select-info {
  display: inline-block;
  width: calc(100% - 30px);

  &--num {
    color: $--color-primary;
  }
}

// check样式
.el-checkbox-group {
  overflow-x: hidden;
}

.el-checkbox {
  ::v-deep .el-checkbox__label {
    color: $--color-text-primary;
  }
}

.all-check {
  display: inline-block;
  padding: 5px 0;
}

.el-autocomplete {
  width: 100%;

  ::v-deep .el-input__inner {
    background-color: #f4f4f4;
    border: none;
  }
}
</style>
