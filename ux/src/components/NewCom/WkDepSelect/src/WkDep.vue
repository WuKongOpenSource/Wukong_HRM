<template>
  <div class="xh-user">
    <div v-if="headerShow" class="xh-user__hd">
      部门
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
          <span>{{ item[config.label] }}</span>
          <!-- <span style="color: #6B778C;">{{ `(${ item.deptName || ''}${item.deptName ? '-' : ''}${item.post || '无'})` }}</span> -->
        </template>
      </el-autocomplete>
      <div
        class="search-list">
        <el-breadcrumb
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
            :radio="radio"
            :data="currentBreadcrumbItem.deptList"
            :props="props"
            :disabled="disabled"
            :value="value"
            @input="$emit('input', $event)"
            @change="checkboxChange"
            @all-select="userAllSelect"
            @next="nextDebounceClick"
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
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
import { depListAPI } from '@/api/common'

import WkDepCheckbox from '../../WkUserSelect/src/WkDepCheckbox'

import PinyinMatch from 'pinyin-match'
import merge from '@/utils/merge'
import { debounce } from 'throttle-debounce'

const DefaultWkDep = {
  value: 'deptId',
  label: 'name',
  // 搜索
  // 请求和参数
  request: null,
  params: null,
  // 默认的搜索人资和管理端人员请求
  dataType: 'manage' // 部门的 value label 一致，用 dataType 区分 manage hrm
}

export default {
  name: 'WkDep', // 新建 dep
  components: {
    WkDepCheckbox
  },
  inheritAttrs: false,
  props: {
    radio: Boolean,
    headerShow: {
      type: Boolean,
      default: true
    },
    // isHide 可不显示 但数据源里包含
    options: Array,
    value: Array,
    // type: {
    //   type: String,
    //   default: 'user' // user  dep  用户和部门
    // },
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
      allChecked: false
    }
  },
  computed: {
    config() {
      return merge({ ...DefaultWkDep }, this.props || {})
    }
  },
  watch: {
    breadcrumbList() {
      this.currentBreadcrumbItem = this.breadcrumbList[this.breadcrumbList.length - 1]
    },

    options: {
      handler() {
        if (this.options) {
          this.getDepUserList(0, this.options)
        } else {
          this.requestDepList()
        }
      },
      immediate: true
    }
  },
  created() {
    this.nextDebounceClick = debounce(300, this.nextClick)
  },
  methods: {
    /**
     * 列效果进行搜索
     */
    searchListUser(queryString, cb) {
      const deptList = this.currentBreadcrumbItem.deptList
      if (deptList && deptList.length > 0) {
        const currentList = deptList
        if (queryString) {
          cb(currentList.filter(item => PinyinMatch.match(item[this.config.label] || '', queryString)))
        } else {
          cb(currentList)
        }
      }
    },

    /**
     * 搜索选择
     */
    searchSelect(item) {
      this.$refs.depCheckbox.searchSelect(item)
    },

    /**
     * 勾选
     */
    checkboxChange(val) {
      if (this.radio) {
        this.$emit('input', val.length > 0 ? [val[val.length - 1]] : [])

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
      if (this.$refs.depCheckbox) {
        this.$refs.depCheckbox.handleCheckAllChange(val)
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
    getDepUserList(deptId, data) {
      if (deptId == 0) {
        this.breadcrumbList = [{ label: '全部', deptList: data, employeeList: [] }]
      } else {
        this.breadcrumbList.push({ label: data.name, deptList: data.children, employeeList: [] })
      }
    },

    /**
     * 请求部门数据
     */
    requestDepList() {
      this.loading = true
      const request = this.config.dataType === 'hrm' ? hrmDeptQueryTreeListAPI : (this.config.request || depListAPI)
      request(this.config.params || {
        type: 'tree'
      }).then(res => {
        this.getDepUserList(0, res.data || [])
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
</style>
