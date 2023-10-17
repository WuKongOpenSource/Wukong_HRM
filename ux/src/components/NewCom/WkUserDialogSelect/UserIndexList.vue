<template>
  <div class="user-index-list">
    <RecycleScroller
      ref="recycleScroller"
      :items="validLetters"
      emit-update
      key-field="userId"
      class="letter-block"
      @update="scrollerUpdate">
      <template slot-scope="{item}">
        <div v-if="item.header" class="header">
          <span class="label">{{ item.letter }}</span>
          <span class="line" />
        </div>
        <check-cell
          v-else
          :key="item[config.value]"
          :item="item"
          :label="item[config.value]"
          :disabled="disabled"
          :props="config"
          @change="userCheck(item)" />
      </template>
    </RecycleScroller>

    <div class="index-nav">
      <div class="index-nav-wrap">
        <ul class="letter-list">
          <li
            v-for="item in leftUl"
            :key="item.letter"
            :class="[{'is-invalid': !item.hasChildren}, {'is-current': selectLetter.letter === item.letter}]"
            @click="clickIndex(item)">{{ item.letter }}</li>
        </ul>
        <ul class="letter-list">
          <li
            v-for="item in rightUl"
            :key="item.letter"
            :class="[{'is-invalid': !item.hasChildren}, {'is-current': selectLetter.letter === item.letter}]"
            @click="clickIndex(item)">{{ item.letter }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
const AlphabetMap = 'A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,#'.split(',')
import CheckCell from './CheckCell'

import merge from '@/utils/merge'

const UserIndexList = {
  value: 'userId',
  label: 'realname'
}

export default {
  // UserIndexList
  name: 'UserIndexList',

  components: {
    CheckCell
  },

  props: {
    data: Object,
    disabled: Boolean,
    props: Object
  },

  data() {
    return {
      letters: [],
      scrollTop: 0,
      selectLetter: {},
      validLetters: [] // 有数据的首字母
    }
  },

  computed: {
    leftUl() {
      return this.letters.length > 0 ? this.letters.slice(0, 14) : []
    },

    rightUl() {
      return this.letters.length > 0 ? this.letters.slice(14) : []
    },

    // 合并 props
    config() {
      return merge({ ...UserIndexList }, this.props || {})
    }
  },

  watch: {},

  created() {
    this.initData()
  },

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    /**
     * initData
     */
    initData() {
      const letters = []
      const validLetters = []

      let scrollTop = 0
      const letterHeight = 21 // 字母高度
      const rowHeight = 40 // 行高
      AlphabetMap.forEach(letter => {
        const children = this.data[letter] || []
        const hasChildren = children.length > 0 // 有children 才是有效的展示块
        const height = (letterHeight + (rowHeight * children.length))

        if (hasChildren) {
          validLetters.push({
            letter,
            userId: letter,
            size: letterHeight,
            header: true
          })
        }

        // 记录偏移位置，点击索引使用
        letters.push({
          letter,
          top: scrollTop,
          height,
          hasChildren
        })

        if (hasChildren) {
          // 计算下一个的开始位置
          scrollTop += height
        }

        children.forEach((item, index) => {
          validLetters.push({
            letter,
            size: rowHeight,
            ...item
          })
        })
      })

      // letters.sort((a, b) => {
      //   return a.letter <= b.letter ? -1 : 1
      // })

      // validLetters.sort((a, b) => {
      //   return a.letter <= b.letter ? -1 : 1
      // })

      this.validLetters = validLetters
      this.letters = letters

      // 默认第一个
      if (letters.length > 0) {
        this.selectLetter = letters[0]
      }
    },

    /**
     * @description: 页面滚动
     * @param {*}
     * @return {*}
     */
    scrollerUpdate() {
      const scrollTop = this.$refs.recycleScroller.$el.scrollTop
      const currentItem = this.letters.find(item => scrollTop >= item.top && scrollTop <= item.height)
      if (currentItem) {
        this.selectLetter = currentItem
      }
    },

    /**
     * 点击index
     */
    clickIndex(item) {
      if (item.hasChildren) {
        this.selectLetter = item
        this.$refs.recycleScroller.$el.scrollTop = item.top
      }
    },

    /**
     * 用户勾选
     */
    userCheck(item) {
      this.$nextTick(() => {
        this.$emit('change', item)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.user-index-list {
  position: relative;
  height: 100%;
  font-size: $--font-size-base;

  .index-nav {
    position: absolute;
    top: -0;
    right: 11px;
    bottom: 0;
    z-index: 100;
    width: 24px;

    &-wrap {
      width: 24px;
      overflow: hidden;
      font-size: 10px;
    }

    .letter-list {
      float: left;
      line-height: 17px;
      color: $--color-text-secondary;
      text-align: center;
      list-style: none;

      li {
        width: 12px;
        cursor: default;

        &.is-invalid {
          color: $--color-n50;
        }

        &.is-current {
          color: $--color-primary;
        }

        &:not(.is-invalid) {
          cursor: pointer;
        }

        &:not(.is-invalid):hover {
          color: $--color-primary;
        }
      }
    }
  }
}
</style>
