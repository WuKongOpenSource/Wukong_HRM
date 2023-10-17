<template>
  <div class="xh-user">
    <div v-if="headerShow" class="xh-user__hd">
      {{ title }}
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
          <span>{{ $getUserName(item, userConfig.label) }}</span>
          <span style="color: #6b778c;">{{ `(${ $getDeptName(item) || ''}${$getDeptName(item) ? '-' : ''}${item.post || '无'})` }}</span>
        </template>
      </el-autocomplete>
      <div
        v-loading="loading"
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
            ref="depCheckbox"
            v-model="depDataValue"
            :class="`is-select-${config.selectType}`"
            :radio="radio"
            :data="currentBreadcrumbItem.deptList"
            :props="depConfig"
            :disabled="disabled || config.selectType === 'user'"
            @next="nextDebounceClick"
            @change="depCheckboxChange"
            @all-select="depAllSelect"
          />

          <wk-user-checkbox
            ref="userCheckbox"
            v-model="userDataValue"
            :class="`is-select-${config.selectType}`"
            :radio="radio"
            :data="currentBreadcrumbItem.employeeList"
            :props="userConfig"
            :disabled="disabled || config.selectType === 'dep'"
            @change="userCheckboxChange"
            @all-select="userAllSelect"
          />

        </div>

      </div>
    </div>
    <flexbox v-if="config.showFooter" class="xh-user__ft">
      <flexbox-item class="select-info">已选择<span class="select-info--num">{{ (depDataValue.length || 0) + (userDataValue.length || 0) }}</span>项</flexbox-item>
      <el-button type="text" @click="clearAll">清空</el-button>
      <el-button v-if="config.showSureBtn" type="text" @click="sureClick">确定</el-button>
    </flexbox>
  </div>
</template>
<script type="text/javascript">
import {
  adminUserQueryByDeptAPI
} from '@/api/admin/user'
import {
  hrmEmployeeQueryByDeptAPI,
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'
import { userListAPI } from '@/api/common'
import {
  queryInspectionDeptEmployeeList,
  queryInspectionAllEmployeeList } from '@/api/hrm/performance'

import WkDepCheckbox from '../../WkUserSelect/src/WkDepCheckbox'
import WkUserCheckbox from '../../WkUserSelect/src/WkUserCheckbox'

import PinyinMatch from 'pinyin-match'
import merge from '@/utils/merge'
import { debounce } from 'throttle-debounce'
import { objDeepCopy } from '@/utils'
import { valueEquals } from 'element-ui/lib/utils/util'
import { getSearchRequestWithRequest } from '../../WkUserSelect/src/utils'

const DefaultWkUser = {
  value: 'userId',
  label: 'realname'
}

const DefaultWkDep = {
  value: 'deptId',
  label: 'name'
}

const DefaultWkUserDep = {
  // 树结构请求和参数
  request: null,
  params: null,
  searchParams: null,
  // 默认的搜索人资和管理端人员请求
  // isHrm: false, // 替换为 dataType 默认 manage 其余 hrm
  dataType: 'manage', // 默认管理端
  // userOptions: null, // 固定数据，后可以放入 props options
  isList: false, // 默认是树形接口，如果是列需设置为true
  showFooter: true, // 默认不隐藏，MembersDep 有数量条，需要隐藏
  showSureBtn: false, // 默认不展示按钮
  selectType: 'all' //  all user dep 全选  仅员工  仅部门
}

export default {
  name: 'WkUserDep', // 新建 WkUserDep
  components: {
    WkDepCheckbox,
    WkUserCheckbox
  },
  inheritAttrs: false,
  props: {
    headerShow: {
      type: Boolean,
      default: true
    },
    options: Array,
    radio: Boolean, // 未支持
    props: {
      type: Object,
      default: () => {
        return {}
      }
    },
    userProps: {
      type: Object,
      default: () => {
        return {}
      }
    },
    userValue: Array,
    depProps: {
      type: Object,
      default: () => {
        return {}
      }
    },
    depValue: Array,
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      searchInput: '',
      searchUserList: [],

      // 面包头
      breadcrumbList: [],

      // 当前面包屑
      currentBreadcrumbItem: null,
      // 全选
      allChecked: false,
      userAllChecked: false,
      depAllChecked: false,
      userDataValue: [],
      depDataValue: []
    }
  },
  computed: {
    userConfig() {
      return merge({ ...DefaultWkUser }, this.userProps || {})
    },

    depConfig() {
      return merge({ ...DefaultWkDep }, this.depProps || {})
    },

    config() {
      return merge({ ...DefaultWkUserDep }, this.props || {})
    },

    title() {
      return {
        'all': '员工/部门',
        'user': '员工',
        'dep': '部门'
      }[this.config.selectType]
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
    },
    userValue: {
      handler() {
        if (!valueEquals(this.userValue, this.userDataValue)) {
          this.userDataValue = objDeepCopy(this.userValue)
        }
      },
      immediate: true
    },
    depValue: {
      handler() {
        if (!valueEquals(this.depValue, this.depDataValue)) {
          this.depDataValue = objDeepCopy(this.depValue)
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
      this.nextDebounceClick = () => {}
    }
  },
  methods: {
    /**
     * 列效果进行搜索
     */
    searchListUser(queryString, cb) {
      if (this.searchUserList && this.searchUserList.length || this.options) {
        const searchUserList = this.options || this.searchUserList
        if (queryString) {
          cb(searchUserList.filter(item => PinyinMatch.match(item[this.userConfig.label] || '', queryString) || PinyinMatch.match(this.$getDeptName(item) || '', queryString)))
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
            cb(this.searchUserList.filter(item => PinyinMatch.match(item[this.userConfig.label] || '', queryString)))
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
      // 人资的默认搜索接口
      if (this.props.isKpi) {
        return queryInspectionAllEmployeeList
      }

      if (this.config.dataType === 'hrm') {
        return hrmEmployeeQueryInAPI
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
     * 部门 change
     */
    depCheckboxChange(val) {
      if (this.config.selectType === 'user') {
        return
      }
      if (this.radio) {
        if (this.userDataValue && this.userDataValue.length > 0) {
          this.userDataValue = []
          if (!this.config.showSureBtn) {
            this.$emit('update:userValue', [])
          }
        }
        // 单选直接关闭窗口
        this.$emit('close')
      }
      if (!this.config.showSureBtn) {
        this.$emit('update:depValue', val)
        this.$nextTick(() => {
          this.$emit('depChange', val)
          this.$emit('change', this.userDataValue, val, 'dep')
        })
      }
    },

    /**
     * user change
     */
    userCheckboxChange(val) {
      if (this.config.selectType === 'dep') {
        return
      }
      if (this.radio) {
        if (this.depDataValue && this.depDataValue.length > 0) {
          this.depDataValue = []
          if (!this.config.showSureBtn) {
            this.$emit('update:depValue', [])
          }
        }
        // 单选直接关闭窗口
        this.$emit('close')
      }
      if (!this.config.showSureBtn) {
        this.$emit('update:userValue', val)
        this.$nextTick(() => {
          this.$emit('userChange', val)
          this.$emit('change', val, this.depDataValue, 'user')
        })
      }
    },

    /**
     * 全部勾选
     */
    handleCheckAllChange(val) {
      if (this.$refs.userCheckbox) {
        this.$refs.userCheckbox.handleCheckAllChange(val)
      }
      if (this.$refs.depCheckbox) {
        this.$refs.depCheckbox.handleCheckAllChange(val)
      }
    },

    /**
     * 用户全选
     */
    userAllSelect(allChecked) {
      this.userAllChecked = allChecked
      if ((this.depAllChecked || !this.currentBreadcrumbItem.deptList || this.currentBreadcrumbItem.deptList.length === 0 || this.config.selectType === 'user') && this.userAllChecked) {
        this.allChecked = true
      } else {
        this.allChecked = false
      }
    },

    /**
     * 部门全选
     */
    depAllSelect(allChecked) {
      this.depAllChecked = allChecked
      if (this.depAllChecked && (this.userAllChecked || !this.currentBreadcrumbItem.employeeList || this.currentBreadcrumbItem.employeeList.length === 0 || this.config.selectType === 'dep')) {
        this.allChecked = true
      } else {
        this.allChecked = false
      }
    },

    /**
     * 清空全部
     */
    clearAll() {
      if (this.config.showSureBtn) {
        this.depDataValue = []
        this.userDataValue = []
      } else {
        this.$emit('update:depValue', [])
        this.$emit('update:userValue', [])
        this.$emit('depChange', [])
        this.$emit('userChange', [])
      }
    },

    /**
     * 确定点击
     */
    sureClick() {
      this.$emit('update:depValue', this.depDataValue)
      this.$emit('update:userValue', this.userDataValue)
      this.$emit('depChange', this.depDataValue)
      this.$emit('userChange', this.userDataValue)
      this.$emit('change', this.userDataValue, this.userDataValue)
      this.$emit('close')
    },

    /**
     * 获取部门员工数据
     */
    getDepUserList(deptId, depInfo) {
      this.loading = true
      const request = this.props.isKpi ? queryInspectionDeptEmployeeList : this.config.dataType === 'hrm' ? hrmEmployeeQueryByDeptAPI : (this.config.request || adminUserQueryByDeptAPI)
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
     * 下一级
     */
    nextClick(item) {
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
  font-size: 13px;
  color: $--color-text-primary;

  &__hd {
    height: 40px;
    padding: 0 15px;
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

::v-deep .wk-dep-checkbox {
  &.is-select-user {
    .el-checkbox__input {
      display: none;
    }

    .el-checkbox__label {
      padding-left: 0;
    }
  }
}
</style>
