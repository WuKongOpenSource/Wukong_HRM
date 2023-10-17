<template>
  <div class="search-view">
    <div class="search-view__hd">搜索关键字为<span>{{ search }}</span>的结果</div>
    <div class="search-view__bd">
      <el-checkbox-group
        v-model="userChecksValues"
        @change="userCheckbox">
        <div
          v-show="validLetters[0].children.length > 0"
          class="letter-block"
          :data-letter="validLetters[0].letter">
          <div class="header">
            <span class="label">{{ validLetters[0].letter }}</span>
            <span class="line" />
          </div>
          <div
            class="children">
            <check-cell
              v-for="user in validLetters[0].children"
              :key="user.userId"
              :item="user"
              :label="user.userId"
              @change="userCheck(user)">
              <xr-avatar
                disabled
                :name="$getUserName(user)"
                :size="30"
                :src="$getUserImg(user)" />
              <div class="content">
                <div
                  class="realname text-one-line"
                  :title="$getUserName(user)">{{ $getUserName(user) }}</div>
                <div
                  class="dept-name text-one-line"
                  :title="$getDeptName(user)">{{ $getDeptName(user) || '无' }}</div>
              </div>
            </check-cell>
          </div>
        </div>
      </el-checkbox-group>

      <el-checkbox-group
        v-model="deptChecksValues"
        @change="deptCheckbox">>
        <div
          v-show="validLetters[1].children.length > 0"
          class="letter-block"
          :data-letter="validLetters[1].letter"
        >
          <div class="header">
            <span class="label">{{ validLetters[1].letter }}</span>
            <span class="line" />
          </div>
          <div
            class="children">
            <dept-check-cell
              v-for="(dept, index) in validLetters[1].children"
              :key="`dept${index}`"
              :item="dept"
              :label="dept.deptId"
              @change="deptCheck(dept, index)" />
          </div>
        </div>
      </el-checkbox-group>

      <el-checkbox-group
        v-model="userChecksValues"
        @change="userCheckbox">>
        <div
          v-show="validLetters[2].children.length > 0"
          class="letter-block"
          :data-letter="validLetters[2].letter"
        >
          <div class="header">
            <span class="label">{{ validLetters[2].letter }}</span>
            <span class="line" />
          </div>
          <div
            class="children">
            <check-cell
              v-for="user in validLetters[2].children"
              :key="user.userId"
              :item="user"
              :label="user.userId"
              @change="userCheck(user)">
              <xr-avatar
                disabled
                :name="$getUserName(user)"
                :size="30"
                :src="$getUserImg(user)" />
              <div class="content">
                <div
                  class="realname text-one-line"
                  :title="$getUserName(user)">{{ $getUserName(user) }}</div>
                <div
                  class="dept-name text-one-line"
                  :title="$getDeptName(user)">{{ $getDeptName(user) || '无' }}</div>
              </div>
            </check-cell>
          </div>
        </div>
      </el-checkbox-group>
    </div>
  </div>
</template>

<script>
import DeptCheckCell from './DeptCheckCell'
import CheckCell from './CheckCell'

import PinyinMatch from 'pinyin-match'

export default {
  // SearchView 暂时未用
  name: 'SearchView',

  components: {
    DeptCheckCell,
    CheckCell
  },

  props: {
    userChecks: Array,
    deptChecks: Array, // 员工部门已选
    search: String,
    users: Array,
    stopUsers: Array,
    depts: Array
  },

  data() {
    return {
      userChecksValues: [],
      deptChecksValues: [],
      validLetters: [{
        letter: '员工',
        type: 'user',
        children: []
      }, {
        letter: '部门',
        type: 'dept',
        children: []
      }, {
        letter: '停用员工',
        type: 'stopUser',
        children: []
      }]
    }
  },

  computed: {},

  watch: {
    search: {
      handler() {
        this.goSearch()
      },
      immediate: true
    }
  },

  created() {
    this.userChecksValues = [...(this.userChecks || [])]
    this.deptChecksValues = [...(this.deptChecks || [])]
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
       * 搜索
       */
    goSearch() {
      this.validLetters[0].children = this.search ? this.users.filter(item => PinyinMatch.match(this.$getUserName(item), this.search)) : []
      this.validLetters[1].children = this.search ? this.stopUsers.filter(item => PinyinMatch.match(this.$getUserName(item), this.search)) : []
      this.validLetters[2].children = this.search ? this.depts.filter(item => PinyinMatch.match(this.$getDeptName(item), this.search)) : []
    },

    /**
     * 整体员工值change
     */
    userCheckbox() {
      this.$nextTick(() => {
        this.$emit('update:userChecks', this.userChecksValues)
      })
    },

    /**
     * 整体部门值change
     */
    deptCheckbox() {
      this.$nextTick(() => {
        this.$emit('update:deptChecks', this.deptChecksValues)
      })
    },

    /**
     * 用户勾选
     */
    userCheck(item) {
      this.$nextTick(() => {
        this.$emit('user-change', item)
      })
    },

    /**
     * 部门勾选
     */
    deptCheck(item) {
      this.$nextTick(() => {
        this.$emit('dept-change', item)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.search-view {
  position: relative;
  height: 100%;
  overflow-y: auto;
  font-size: $--font-size-base;

  &__hd {
    line-height: 30px;
    color: $--color-n70;

    span {
      margin: 0 4px;
      color: $--color-black;
    }
  }

  &__bd {
    height: calc(100% - 30px);
    overflow-y: auto;
  }
}
</style>
