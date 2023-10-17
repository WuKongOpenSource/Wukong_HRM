<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <flexbox class="wk-user-select" align="stretch">
    <div v-loading="loading" class="wk-user-select__left">
      <div class="wk-user-select__header">
        <el-autocomplete
          v-model="searchInput"
          :fetch-suggestions="searchListUser"
          placeholder="请输入内容"
          @select="searchSelect"
        >
          <template slot-scope="{ item }">
            <span>{{ item[config.label] }}</span>
            <span style="color: #6b778c;">{{ `(${item.post || '暂无岗位'})` }}</span>
          </template>
        </el-autocomplete>
      </div>
      <div class="wk-user-select__body">
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
        <div class="wk-user-select__list">
          <flexbox
            v-for="(item, index) in showDataList"
            :key="index"
            class="wk-user-select__item">
            <el-checkbox
              v-if="(item.isDep && config.canSelectDep) || !item.isDep"
              v-model="item.isCheck"
              :disabled="item.disabled"
              @change="dataCheckboxChange($event, item)" />
            <template v-if="item.isDep">
              <div class="dep-name text-one-line">{{ `${$getDeptName(item, config.depLabel)}${item.currentNum > 0 ? `(${item.currentNum})` : ''}` }}</div>
              <el-button
                v-if="item.hasChildren == 1 || item.currentNum > 0"
                :disabled="item.isCheck"
                type="text"
                icon="wk wk-icon-structure"
                @click="lowerLevelClick(item)">下级</el-button>
            </template>
            <template v-else>
              <xr-avatar
                disabled
                :name="$getUserName(item, config.label)"
                :size="24"
                :src="$getUserImg(item)"
                class="user-img" />
              <div class="user-info">
                <div class="user-name text-one-line">{{ $getUserName(item, config.label) }}</div>
                <div class="user-post">{{ item.post || '暂无岗位' }}</div>
              </div>
            </template>
          </flexbox>
        </div>
      </div>
    </div>
    <div class="wk-user-select__right">
      <flexbox class="user-selects" wrap="wrap">
        <span
          v-for="(item, index) in selectDeps"
          :key="`dep${index}`"
          class="user-item text-one-line">{{ `${item[config.depLabel]}${item.currentNum > 0 ? `(${item.currentNum})` : ''}` }}
          <i
            class="delete-icon el-icon-close"
            @click.stop="deleteDepUser('dep', index, item, selectDeps)" />
        </span>
        <span
          v-for="(item, index) in selectUsers"
          :key="`user${index}`"
          class="user-item text-one-line">{{ $getUserName(item, config.label) }}
          <i
            class="delete-icon el-icon-close"
            @click.stop="deleteDepUser('user', index, item, selectUsers)" />
        </span>
      </flexbox>
    </div>
  </flexbox>
</template>

<script>
import {
  hrmEmployeeQueryByDeptAPI,
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'
import {
  adminUserQueryByDeptAPI
} from '@/api/admin/user'
import { userListAPI } from '@/api/common'

import PinyinMatch from 'pinyin-match'

export default {
  // 用户选择 以部门的形式
  name: 'WkDepUserView',
  components: {},
  props: {
    // 取值字段
    props: {
      type: Object,
      default: () => {
        return {}
      }
    },
    depValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    userValue: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      loading: false,
      searchInput: '',
      searchUserList: [],
      // 面包头
      breadcrumbList: [],
      showDataList: [],
      // 选择的部门 选择的员工
      selectUsers: [],
      selectDeps: []
    }
  },
  computed: {
    config() {
      const defaultConfig = {
        value: 'userId',
        label: 'realname',
        depValue: 'id',
        depLabel: 'name',
        canSelectDep: true,
        userOptions: null,
        radio: false, // 仅员工
        isHrm: false,
        request: null,
        searchParams: null
      }
      const config = this.props || {}

      return {
        ...defaultConfig,
        ...config
      }
    }
  },
  watch: {
    selectUsers() {
      this.selectChange()
    },
    selectDeps() {
      this.selectChange()
    }
  },
  created() {
    this.selectUsers = this.userValue
    this.selectDeps = this.depValue
    if (this.config.userOptions) {
      const employeeList = this.config.userOptions
      this.handleArrayCheckValue('user', employeeList, this.selectUsers)
      this.showDataList = employeeList
    } else {
      this.getDepUserList(0)
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 列效果进行搜索
     */
    searchListUser(queryString, cb) {
      if (this.searchUserList && this.searchUserList.length || this.config.userOptions) {
        const searchUserList = this.config.userOptions || this.searchUserList
        if (queryString) {
          cb(searchUserList.filter(item => PinyinMatch.match(this.$getUserName(item, this.config.label) || '', queryString)))
        } else {
          cb(searchUserList)
        }
      } else {
        const request = this.config.isHrm ? hrmEmployeeQueryInAPI : userListAPI
        let params = { pageType: 0 }
        if (this.config.searchParams) {
          params = { ...params, ...this.config.searchParams }
        }
        request(params).then(res => {
          this.searchUserList = res.data.hasOwnProperty('list') ? (res.data.list || []) : (res.data || [])
          if (queryString) {
            cb(this.searchUserList.filter(item => PinyinMatch.match(this.$getUserName(item, this.config.label) || '', queryString)))
          } else {
            cb(this.searchUserList)
          }
        }).catch(() => {})
      }
    },

    /**
     * 搜索选择
     */
    searchSelect(item) {
      const name = this.config.value
      const selectIds = this.selectUsers.map(item => item[name])
      const value = item[name]
      if (!selectIds.includes(value)) {
        if (this.config.radio) {
          this.selectUsers = [item]
        } else {
          this.selectUsers.push(item)
        }

        for (let index = 0; index < this.showDataList.length; index++) {
          const element = this.showDataList[index]
          if (!element.isDep && value == element[name]) {
            element.isCheck = true
            break
          }
        }
      }
    },

    /**
     * 获取部门员工数据
     */
    getDepUserList(deptId, depInfo) {
      this.loading = true
      let request = this.config.isHrm ? hrmEmployeeQueryByDeptAPI : adminUserQueryByDeptAPI
      if (this.config.request) {
        request = this.config.request
      }
      request(deptId).then(res => {
        const data = res.data || {}
        const deptList = data.deptList || []
        deptList.forEach(item => {
          item.isDep = true
        })
        this.handleArrayCheckValue('dep', deptList, this.selectDeps)

        // employeeList 人资  userList 系统管理
        const employeeList = data.employeeList || data.userList || []
        this.handleArrayCheckValue('user', employeeList, this.selectUsers)

        this.showDataList = deptList.concat(employeeList)
        if (deptId == 0) {
          this.breadcrumbList = [{ label: '组织', deptList: deptList, employeeList: employeeList }]
        } else {
          this.breadcrumbList.push({ label: depInfo.name, deptList: deptList, employeeList: employeeList })
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 操作数组check的值
     * type user dep
     */
    handleArrayCheckValue(type, array, selectArray) {
      const name = type == 'user' ? this.config.value : this.config.depValue
      const selectIds = selectArray.map(item => item[name])
      array.forEach(item => {
        item.isCheck = selectIds.includes(item[name])
      })
    },

    /**
     * 面包屑点击
     */
    breadcrumbBtn(item, index) {
      if (index + 1 <= this.breadcrumbList.length - 1) {
        this.breadcrumbList.splice(index + 1, this.breadcrumbList.length - 1)
      }
      const deptList = item.deptList
      this.handleArrayCheckValue('dep', deptList, this.selectDeps)
      const employeeList = item.employeeList
      this.handleArrayCheckValue('user', employeeList, this.selectUsers)
      this.showDataList = deptList.concat(employeeList)
    },

    /**
     * 数据选择
     */
    dataCheckboxChange(isCheck, data) {
      if (data.isDep) {
        if (isCheck) {
          this.selectDeps.push(data)
        } else {
          const index = this.getArrayIndexWithItem(this.selectDeps, data, this.config.depValue)
          if (index >= 0) {
            this.selectDeps.splice(index, 1)
          }
        }
      } else {
        if (isCheck) {
          if (this.config.radio) {
            this.selectUsers = [data]
            this.handleArrayCheckValue('user', this.showDataList, this.selectUsers)
          } else {
            this.selectUsers.push(data)
          }
        } else {
          const index = this.getArrayIndexWithItem(this.selectUsers, data, this.config.value)
          if (index >= 0) {
            this.selectUsers.splice(index, 1)
          }
        }
      }
    },

    getArrayIndexWithItem(array, item, name) {
      for (let i = 0; i < array.length; i++) {
        const element = array[i]
        if (element[name] == item[name]) {
          return i
        }
      }
      return -1
    },

    /**
     * 下一级
     */
    lowerLevelClick(item) {
      this.showDataList = []
      this.getDepUserList(item.deptId, item)
    },

    /**
     * 部门员工删除
     */
    deleteDepUser(type, index, data, array) {
      array.splice(index, 1)
      const name = type == 'user' ? this.config.value : this.config.depValue
      const value = data[name]
      for (let index = 0; index < this.showDataList.length; index++) {
        const element = this.showDataList[index]
        if (element.isDep) {
          if (type == 'dep' && value == element[name]) {
            element.isCheck = false
            break
          }
        } else {
          if (type == 'user' && value == element[name]) {
            element.isCheck = false
            break
          }
        }
      }
    },

    /**
     * 选择change
     */
    selectChange() {
      this.$emit('change', this.selectUsers, this.selectDeps)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-user-select {
  height: 300px;

  &__left {
    flex: 4;
    height: 100%;
    padding: 10px 15px;
  }

  &__right {
    position: relative;
    flex: 5;
    height: 100%;
    padding: 10px;
    border-left: 1px solid $--border-color-base;

    .user-selects {
      overflow-y: auto;
    }

    .user-item {
      position: relative;
      max-width: 200px;
      padding: 5px 15px 5px 5px;
      margin: 3px;
      background-color: #f3f7ff;
      border-radius: $--border-radius-base;
    }

    .delete-icon {
      position: absolute;
      top: 8px;
      right: 2px;
      color: $--color-text-secondary;
      cursor: pointer;

      &:hover {
        color: $--color-primary;
      }
    }
  }

  &__header {
    margin-bottom: 10px;

    .el-autocomplete {
      width: 100%;
    }
  }

  &__body {
    height: calc(100% - 50px);
  }

  &__list {
    height: calc(100% - 24px);
    overflow-y: auto;
  }

  &__item {
    height: 40px;

    .el-checkbox {
      margin-right: 8px;
    }

    .el-button--text {
      position: relative;
      flex-shrink: 0;
      padding-right: 12px;
      padding-left: 12px;
      color: #ccc;

      ::v-deep i {
        margin-right: 3px;
        font-size: 13px;
      }

      &::before {
        position: absolute;
        top: 8px;
        bottom: 8px;
        left: 0;
        width: 1px;
        content: " ";
        background-color: $--border-color-base;
      }

      &:hover {
        color: $--color-primary;
      }

      &.is-disabled {
        color: #c0c4cc;
      }
    }

    .user {
      &-img {
        flex-shrink: 0;
      }

      &-info {
        flex: 1;
        padding: 0 8px;
      }

      &-name {
        max-width: 200px;
      }

      &-post {
        margin-top: 2px;
        font-size: 12px;
        color: $--color-text-secondary;
      }
    }

    .dep {
      &-name {
        flex: 1;
        padding-right: 8px;
      }
    }
  }
}
</style>
