<template>
  <el-dialog
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    :show-close="false"
    class="wk-user-select-dialog"
    width="700px">
    <!-- 内容 -->
    <div :key="visible ? 'visible' : 'hidden'" class="main">
      <div class="main-left">
        <div class="main-top">
          <el-input
            v-model="searchInput"
            maxlength="8"
            placeholder="请输入关键字">
            <el-button
              slot="suffix"
              type="icon"
              icon="wk wk-sousuo"
              @click="goSearch" />
          </el-input>
        </div>
        <div class="main-body-wrap">
          <!-- <search-view
          v-if="searchInput"
          :users="searchUserDept.userList"
          :stop-users="searchStopUserOrigin"
          :depts="searchDeptOrigin"
          :search="searchInput"
          :user-checks.sync="currentUserValue"
          :dept-checks.sync="currentDepValue"
          @user-change="userChange"
          @dept-change="deptTreeChange"
        /> -->

          <div
            v-if="searchInput"
            class="search-view">
            <div class="search-view__hd">搜索关键字为<span>{{ searchInput }}</span>的结果</div>
            <div class="search-view__bd">
              <el-checkbox-group
                v-show="resultList[0].children.length > 0"
                v-model="currentUserValue"
                :disabled="disabled">
                <div
                  class="letter-block"
                  :data-letter="resultList[0].letter">
                  <div class="header">
                    <span class="label">{{ resultList[0].letter }}</span>
                    <span class="line" />
                  </div>
                  <div
                    class="children">
                    <check-cell
                      v-for="user in resultList[0].children"
                      :key="user[userConfig.value]"
                      :item="user"
                      :label="user[userConfig.value]"
                      @change="searchUserChange(user)">
                      <xr-avatar
                        disabled
                        :name="$getUserName(user, userConfig.label)"
                        :size="30"
                        :src="$getUserImg(user)" />
                      <div class="content">
                        <div
                          class="realname text-one-line"
                          :title="$getUserName(user, userConfig.label)">{{ $getUserName(user, userConfig.label) }}</div>
                        <div
                          class="dept-name text-one-line"
                          :title="$getDeptName(user)">{{ $getDeptName(user) || '无' }}</div>
                      </div>
                    </check-cell>
                  </div>
                </div>
              </el-checkbox-group>

              <el-checkbox-group
                v-show="resultList[1].children.length > 0"
                v-model="currentDepValue"
                :disabled="disabled">
                <div
                  class="letter-block"
                  :data-letter="resultList[1].letter"
                >
                  <div class="header">
                    <span class="label">{{ resultList[1].letter }}</span>
                    <span class="line" />
                  </div>
                  <div
                    class="children">
                    <dept-check-cell
                      v-for="(dept, index) in resultList[1].children"
                      :key="`dept${index}`"
                      :item="dept"
                      :label="dept[deptConfig.value]"
                      :show-next="false"
                      @change="searchDeptChange(dept)" />
                  </div>
                </div>
              </el-checkbox-group>

              <el-checkbox-group
                v-show="resultList[2].children.length > 0"
                v-model="currentUserValue"
                :disabled="disabled">
                <div
                  class="letter-block"
                  :data-letter="resultList[2].letter"
                >
                  <div class="header">
                    <span class="label">{{ resultList[2].letter }}</span>
                    <span class="line" />
                  </div>
                  <div
                    class="children">
                    <check-cell
                      v-for="user in resultList[2].children"
                      :key="user[userConfig.value]"
                      :item="user"
                      :label="$getUserName(user, userConfig.value)"
                      @change="searchUserChange(user)">
                      <xr-avatar
                        disabled
                        :name="$getUserName(user, userConfig.label)"
                        :size="30"
                        :src="$getUserImg(user)" />
                      <div class="content">
                        <div
                          class="realname text-one-line"
                          :title="$getUserName(user, userConfig.label)">{{ $getUserName(user, userConfig.label) }}</div>
                        <div
                          class="dept-name text-one-line"
                          :title="user.deptName">{{ user.deptName || '无' }}</div>
                      </div>
                    </check-cell>
                  </div>
                </div>
              </el-checkbox-group>
            </div>
          </div>

          <div v-show="!searchInput" class="main-body">
            <div v-if="tabs.length > 1" class="main-tabs">
              <el-button
                v-for="(item, index) in tabs"
                :key="index"
                :style="{ width: `calc(${parseInt(100/tabs.length)}% - 8px)` }"
                :type="item.value === tabIndex ? 'selected' : ''"
                @click="tabsClick(item)">{{ item.label }}</el-button>
            </div>

            <!-- 仅员工展示  停用员工不展示 -->
            <flexbox
              v-if="tabIndex === 'user'"
              class="item-cell user-switch"
              @click.native="userTypeChange">
              <div class="item-cell__hd">{{ userShowTypeName }}</div><i class="el-icon-arrow-right" />
            </flexbox>
            <el-checkbox-group
              v-show="tabIndex !== 'dept'"
              v-model="currentUserValue"
              :disabled="disabled">
              <!-- 员工 tabIndex user 下展示-->
              <user-index-list
                v-show="userShowType === 'user' && tabIndex === 'user'"
                :data="userMap"
                :disabled="disabled"
                :props="userConfig"
                @change="userChange" />
              <user-tree
                v-show="userShowType === 'tree' && tabIndex === 'user'"
                ref="userTree"
                :tree="userTreeData"
                :visisble="userShowType === 'tree'"
                :radio="radio"
                :disabled="disabled"
                :user-props="userConfig"
                :dept-props="deptConfig"
                @change="userTreeChange"
                @action="userTreeAction" />
              <!-- 停用员工 -->
              <user-list
                v-show="tabIndex === 'stopUser'"
                :data="disableUserList"
                :disabled="disabled"
                :props="userConfig"
                @change="userChange" />
            </el-checkbox-group>

            <!-- 部门 -->
            <el-checkbox-group
              v-show="tabIndex === 'dept'"
              v-model="currentDepValue"
              :disabled="disabled">
              <dept-tree
                ref="deptTree"
                :tree="deptTreeData"
                :radio="radio"
                :disabled="disabled"
                :props="deptConfig"
                @all-change="deptTreeAllChange"
                @action="deptTreeAction"
              />
            </el-checkbox-group>

          </div>
        </div>
      </div>
      <div
        class="main-right">
        <div class="main-top">
          已选：<template v-if="config.showUser || config.showDisableUser">{{ currentUserValue.length }}个员工 </template><template v-if="config.showDept">{{ currentDepValue.length }}个部门</template>
          <el-button
            class="clear-btn"
            type="link"
            :disabled="(currentUserValue.length + currentDepValue.length) === 0"
            @click="clearClick">清空</el-button>
        </div>
        <div class="main-body is-right">
          <div class="main-select">
            <el-tag
              v-for="(idKey, index) in selectData"
              :key="index"
              size="small"
              type="info"
              closable
              disable-transitions
              @close="selectDelete(idKey, index)"><span v-if="!idKey.includes('user')" class="is-dept" />{{ getSelectShowName(idKey, userDeptMap) }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div slot="footer" class="user-dialog-footer">
      <span class="udf-left">
        <el-checkbox
          v-if="tabIndex === 'dept' && config.showSubDeptContain"
          v-model="currentSubDeptContain"
          :true-label="1"
          :false-label="0"
        >包含下级部门</el-checkbox>
      </span>
      <span class="udf-right">
        <el-button
          v-debounce="handleConfirm"
          type="primary">确定</el-button>
        <el-button @click.native="close">取消</el-button>
      </span>
    </div>
  </el-dialog>
</template>

<script>
import UserIndexList from './UserIndexList'
import UserList from './UserList'
import UserTree from './UserTree'
import DeptTree from './DeptTree'
import DeptCheckCell from './DeptCheckCell'
import CheckCell from './CheckCell'

import PinyinMatch from 'pinyin-match'
import { mapGetters } from 'vuex'
import { valueEquals } from 'element-ui/lib/utils/util'
import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const WkDepUserDialog = {
  showUser: true,
  showDept: true,
  showDisableUser: true,
  disableUserList: null, // 用于单列固定数据的展示
  disableUserLabel: '停用员工',
  showSubDeptContain: false, // 是否下级部门
  customDeptList: null // 自定义部门数据
}

const WkDepUserDialogUser = {
  value: 'userId',
  label: 'realname'
}

const WkDepUserDialogDept = {
  value: 'deptId',
  label: 'name'
}

/**
 * todo: 加入各数据源 搜索源 传参
 */
export default {
  name: 'WkDepUserDialog',

  components: {
    UserIndexList,
    UserList,
    UserTree,
    DeptTree,
    DeptCheckCell,
    CheckCell
  },

  provide() {
    return {
      'userDepDialog': this
    }
  },

  props: {
    radio: Boolean,
    disabled: Boolean,
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    title: {
      type: String,
      default: '选择'
    },
    userValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    depValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    subDeptContain: [Number, Boolean],
    // 控制项
    props: {
      type: Object,
      default: () => {
        return {

        }
      }
    },
    userProps: Object, // 员工部门控制项
    deptProps: Object
  },

  data() {
    return {
      searchInput: '',
      userShowType: 'user', // user tree stopUser停用员工
      userMap: {},
      deptList: [],
      disableUserList: [],
      selectData: [], // 选择的数据
      tabIndex: 'user',
      // 当前选择值
      currentUserValue: [],
      currentDepValue: [],
      currentSubDeptContain: false,

      // 搜索
      resultList: [{
        letter: '员工',
        type: 'user',
        children: []
      }, {
        letter: '部门',
        type: 'dept',
        children: []
      }, {
        letter: '',
        type: 'stopUser',
        children: []
      }]
    }
  },

  computed: {
    ...mapGetters(['userDeptObj', 'userDeptMap', 'searchUserDept']),

    // 合并 props
    config() {
      return merge({ ...WkDepUserDialog }, this.props || {})
    },

    // 合并 props
    userConfig() {
      return merge({ ...WkDepUserDialogUser }, this.userProps || {})
    },

    deptConfig() {
      return merge({ ...WkDepUserDialogDept }, this.deptProps || {})
    },

    tabs() {
      const tabs = []
      if (this.config.showUser) {
        tabs.push({
          label: '员工',
          value: 'user'
        })
      }

      if (this.config.showDept) {
        tabs.push({
          label: '部门',
          value: 'dept'
        })
      }

      if (this.config.showDisableUser) {
        tabs.push({
          label: this.config.disableUserLabel,
          value: 'stopUser'
        })
      }

      return tabs
    },

    userShowTypeName() {
      return {
        user: '按组织框架查看',
        tree: '按字母排序查看'
      }[this.userShowType]
    },

    deptTreeData() {
      return objDeepCopy(this.deptList)
    },

    userTreeData() {
      return objDeepCopy(this.deptList)
    }
  },

  watch: {
    searchInput: {
      handler() {
        this.goSearch()
      },
      immediate: true
    },

    userDeptObj: {
      handler() {
        this.initAllData()
      },
      immediate: true
    },

    depValue() {
      if (!valueEquals(this.depValue, this.currentDepValue)) {
        this.currentDepValue = objDeepCopy(this.depValue || [])

        if (this.userDeptMap && Object.keys(this.userDeptMap).length > 0) {
          // 更新选择的数据
          this.updateSelectData()
        }
      }
    },

    userValue() {
      if (!valueEquals(this.userValue, this.currentUserValue)) {
        this.currentUserValue = objDeepCopy(this.userValue || [])

        if (this.userDeptMap && Object.keys(this.userDeptMap).length > 0) {
          // 更新选择的数据
          this.updateSelectData()
        }
      }
    },

    subDeptContain() {
      if (this.subDeptContain !== this.currentSubDeptContain) {
        this.currentSubDeptContain = this.subDeptContain
      }
    },

    tabs: {
      handler() {
        this.tabIndex = this.tabs.length > 0 ? this.tabs[0].value : ''
      },
      immediate: true
    },

    // 监听单列员工数据改变
    'config.showDisableUser': {
      handler() {
        this.disableUserList = this.config.showDisableUser ? (this.config.disableUserList || this.userDeptObj.disableUserList) : []
      }
    }
  },

  created() {
    // 确定禁用员工title
    this.resultList[2].letter = this.config.disableUserLabel
  },

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    /**
       * 获取全部数据
       */
    initAllData() {
      // this.userDeptObj 为默认值，如果props 不存在时使用
      const { userMap = {}, deptList = [], disableUserList = [] } = this.userDeptObj
      // 只有展示的布局进行赋值
      this.userMap = this.config.showUser ? userMap : {}
      this.deptList = this.config.customDeptList || (this.config.showUser || this.config.showDept ? deptList : [])
      // disableUserList 有值 以 disableUserList  为准，为展示单列
      this.disableUserList = this.config.showDisableUser ? (this.config.disableUserList || disableUserList) : []

      // 更新选择的数据  后期依靠watch事件
      this.currentDepValue = objDeepCopy(this.depValue || [])
      this.currentUserValue = objDeepCopy(this.userValue || [])
      if (this.config.showSubDeptContain) {
        this.currentSubDeptContain = this.subDeptContain
      }
      this.updateSelectData()
    },

    /* ------------------------ 切换筛选条件 ------------------------ */
    /**
     * tabs change
     */
    tabsClick(item) {
      if (item.value !== this.tabIndex) {
        this.tabIndex = item.value
      }
    },

    /**
     * user type change
     */
    userTypeChange() {
      this.userShowType = this.userShowType === 'user' ? 'tree' : 'user'
    },

    /* ------------------------ 已选数据操作 ------------------------ */
    /**
     * 更新选择的数据
     */
    updateSelectData() {
      // 初始化右侧选择数据
      const userSelectData = this.currentUserValue.map(userId => `user-${userId}`)
      const deptSelectData = this.currentDepValue.map(deptId => `dept-${deptId}`)
      this.selectData = userSelectData.concat(deptSelectData)
    },

    /**
     * 选择数据删除
     */
    selectDelete(idKey, index) {
      if (idKey.includes('user-')) {
        this.selectData.splice(index, 1)
        const id = idKey.split('-')[1]
        const removeIndex = this.currentUserValue.findIndex(userId => userId == id)
        if (index > -1) {
          this.currentUserValue.splice(removeIndex, 1)
        }

        // 刷新树结构员工部门勾选状态 和 全选状态
        if (this.$refs.userTree) {
          this.$refs.userTree.refreshData()
          this.$refs.userTree.setAllStatus()
        }
      } else if (idKey.includes('dept-')) {
        this.selectData.splice(index, 1)
        const id = idKey.split('-')[1]
        const removeIndex = this.currentDepValue.findIndex(deptId => deptId == id)
        if (index > -1) {
          this.currentDepValue.splice(removeIndex, 1)
        }

        // 刷新全选状态
        if (this.$refs.deptTree) {
          this.$refs.deptTree.setAllStatus()
        }
      }
    },

    /* ------------------------ 员工编辑 ------------------------ */
    /**
     * 用户的添加或者删除 根据 isChecked true 是添加 false 是删除
     */
    userChange(item) {
      const idKey = `user-${item[this.userConfig.value]}`
      if (item.isChecked) {
        if (!this.selectData.includes(idKey)) {
          if (this.radio) {
            this.currentDepValue = []
            this.currentUserValue = [item[this.userConfig.value]]
            this.selectData = [idKey]
          } else {
            this.selectData.push(idKey)
          }
        }
      } else {
        const index = this.selectData.findIndex(key => key === idKey)
        if (index > -1) {
          this.selectData.splice(index, 1)
        }
      }
    },

    /**
     * 用户
     * action add remove addUser(单个)
     * 单选场景下，该批量方法不会触发
     */
    userTreeAction(action, data) {
      if (action === 'addUser') {
        this.userChange(data)
      } else if (action === 'add') {
        this.selectData = this.selectData.concat(data.map(userId => `user-${userId}`))
      } else if (action === 'remove') {
        const removeKeys = data.map(userId => `user-${userId}`)
        const newKeys = []
        this.selectData.forEach(idKey => {
          if (!removeKeys.includes(idKey)) {
            newKeys.push(idKey)
          }
        })
        this.selectData = newKeys
      }
    },

    /* ------------------------ 部门编辑 ------------------------ */
    /**
     * 部门的添加或者删除 根据 isChecked true 是添加 false 是删除
     */
    deptTreeChange(item) {
      const idKey = `dept-${item[this.deptConfig.value]}`
      if (item.isChecked) {
        if (!this.selectData.includes(idKey)) {
          if (this.radio) {
            this.currentUserValue = []
            this.currentDepValue = [item[this.deptConfig.value]]
            this.selectData = [idKey]
          } else {
            this.selectData.push(idKey)
          }
        }
      } else {
        const index = this.selectData.findIndex(key => key === idKey)
        if (index > -1) {
          this.selectData.splice(index, 1)
        }
      }
    },

    /**
     * 全选change
     * deptTreeAllChange
     */
    deptTreeAllChange(data) {
      this.currentDepValue = data
    },

    /**
     * 部门
     * action add remove addUser(单个)
     * 单选场景下，该批量方法不会触发
     */
    deptTreeAction(action, data) {
      if (action === 'addDept') {
        this.deptTreeChange(data)
      } else if (action === 'add') {
        this.selectData = this.selectData.concat(data.map(deptId => `dept-${deptId}`))
      } else if (action === 'remove') {
        const removeKeys = data.map(deptId => `dept-${deptId}`)
        const newKeys = []
        this.selectData.forEach(idKey => {
          if (!removeKeys.includes(idKey)) {
            newKeys.push(idKey)
          }
        })
        this.selectData = newKeys
      }
    },

    /**
     * 员工按部门展示树change
     */
    userTreeChange(data) {
      this.currentUserValue = data
    },

    /* ------------------------ 搜索 ------------------------ */
    /**
       * 搜索
       */
    goSearch() {
      // 不展示的信息 不进行搜索值返回
      this.resultList[0].children = this.config.showUser && this.searchInput ? this.searchUserDept.userList.filter(item => PinyinMatch.match(item[this.userConfig.label], this.searchInput)) : []
      this.resultList[1].children = this.config.showDept && this.searchInput ? this.searchUserDept.deptList.filter(item => PinyinMatch.match(item[this.deptConfig.label], this.searchInput)) : []
      this.resultList[2].children = this.config.showDisableUser && this.searchInput ? (this.config.disableUserList || this.searchUserDept.disableUserList).filter(item => PinyinMatch.match(item[this.userConfig.label], this.searchInput)) : []
    },

    /**
     * 搜索部门修改
     */
    searchDeptChange(dept) {
      this.$nextTick(() => {
        this.deptTreeChange(dept)
      })
    },

    /**
     * 搜索员工修改
     */
    searchUserChange(user) {
      this.$nextTick(() => {
        this.userChange(user)
      })
    },

    /**
     * 获取展示名称
     */
    getSelectShowName(idKey, userDeptMap) {
      const data = userDeptMap[idKey]
      if (idKey.includes('user')) {
        return data ? this.$getUserName(userDeptMap[idKey], this.userConfig.label) : ''
      }
      return data ? this.$getDeptName(userDeptMap[idKey], this.deptConfig.label) : ''
    },

    /* ------------------------ 清空 ------------------------ */
    /**
     * 清空
     */
    clearClick() {
      this.currentUserValue = []
      this.currentDepValue = []
      this.selectData = []
    },

    /**
     * 关闭 确定
     */
    close() {
      this.$emit('close')
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      // 确定 更新同步数据
      // 使用深拷贝值更新，防止造成外出数据污染 当前值
      const currentUserValue = objDeepCopy(this.currentUserValue)
      const currentDepValue = objDeepCopy(this.currentDepValue)

      this.$emit('update:userValue', currentUserValue)
      this.$emit('update:depValue', currentDepValue)
      this.$emit('update:subDeptContain', this.currentSubDeptContain)

      const userSelectData = []
      currentUserValue.forEach(userId => {
        const data = this.userDeptMap[`user-${userId}`]
        if (data) {
          userSelectData.push(data)
        }
      })

      const deptSelectData = []
      currentDepValue.forEach(deptId => {
        const data = this.userDeptMap[`dept-${deptId}`]
        if (data) {
          deptSelectData.push(data)
        }
      })
      this.$emit('change', currentUserValue, currentDepValue, userSelectData, deptSelectData, this.currentSubDeptContain)

      // 关闭窗口
      this.close()
    }
  }
}
</script>

<style lang="scss">
.wk-user-select-dialog {
  .el-dialog__body {
    padding: 8px 20px;
  }
}
</style>
<style lang="scss" scoped>
@import "./style";

.main {
  display: flex;
  border: $--border-base;

  &-left {
    flex: 1;
    overflow: hidden;
    border-right: 1px solid $--border-color-base;
  }

  &-right {
    flex: 1;
  }

  &-top {
    position: relative;
    padding: 0 16px;
    line-height: 50px;
    border-bottom: 1px solid $--border-color-base;

    .clear-btn {
      float: right;
      margin-top: 8px;
    }
  }

  &-body-wrap {
    position: relative;
    height: 320px;
    padding: 8px 16px;
  }

  &-body:not(.is-right) {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  &-body.is-right {
    position: relative;
    height: calc(100% - 50px);
    max-height: 320px;
    padding: 8px 16px;
    overflow-y: auto;
  }

  &-tabs {
    flex-shrink: 0;
    text-align: center;
  }

  .user-switch {
    flex-shrink: 0;
  }

  .el-checkbox-group {
    flex: 1;
    overflow: hidden;
  }

  &-select {
    display: flex;
    flex-wrap: wrap;

    .el-tag {
      margin-right: 8px;
      margin-bottom: 8px;

      &:last-child {
        margin-right: 0;
      }
    }
  }

  .item-cell {
    height: 40px;
    cursor: pointer;
    border-bottom: $--border-base;

    &__hd {
      flex: 1;
    }
  }

  // 是部门
  .is-dept {
    display: inline-block;
    width: 10px;
    height: 10px;
    margin-right: 4px;
    vertical-align: middle;
    border: 2px solid $--color-n500;
    border-radius: 5px;
  }
}

.user-dialog-footer {
  overflow: hidden;
  text-align: start;

  .udf-left {
    float: left;
  }

  .udf-right {
    float: right;
  }
}
</style>
