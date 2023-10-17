<template>
  <div class="tag">
    <flexbox class="header">
      <span class="title">选择标签</span>
      <i
        class="el-icon-close close"
        @click="close" />
    </flexbox>
    <div class="body">
      <div class="search">
        <el-input
          v-model="searchInput"
          prefix-icon="el-icon-search"
          placeholder="搜索标签" />
      </div>
      <div class="tab-wrap">
        <div>
          <el-button
            v-for="(item, index) in tabs"
            :key="index"
            :title="item.label"
            :type="item.value === activeTab ? 'selected' : null"
            @click="tabsClick(item)">
            {{ item.label }}
          </el-button>
        </div>
        <handle-tag
          v-if="isPerson"
          type="manage"
          :module-val="config.params.type"
          placement="bottom-start"
          :outer-tag-list="tagList"
          :show="isPerson" />
      </div>
      <div v-empty="options.length===0" class="content">
        <div
          v-for="(item,index) in showList"
          :key="index"
          class="group">
          <div v-if="!isPerson" class="group-name">{{ item.name }}</div>
          <flexbox wrap="wrap" class="group-content">
            <div
              v-for="(subItem,subIndex) in item.labelList"
              :key="subIndex"
              :style="getItemStyle(subItem)"
              class="group-item"
              @click="select(subItem)">
              {{ subItem.name }}
            </div>
          </flexbox>
        </div>
        <handle-tag
          v-if="isPerson"
          :show="isPerson"
          :module-val="config.params.type"
          placement="bottom-start"
          type="create"
          @refresh="getPersonLabel" />
      </div>
    </div>
    <div class="footer">
      <el-button
        type="primary"
        @click.stop="submit">确定</el-button>
      <el-button @click.stop="close">取消</el-button>
    </div>
  </div>
</template>

<script>
import HandleTag from '@/components/NewCom/WkTag/HandleTag'

import PinyinMatch from 'pinyin-match'
import { objDeepCopy } from '../../../utils'
import merge from '@/utils/merge'

const DefaultTag = {
  showPersonTag: false,
  request: null,
  params: null
}

export default {
  name: 'Tag', // 标签选择
  components: {
    HandleTag
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    selectedData: { // 选中数据
      type: Array,
      default: () => {
        return []
      }
    },
    options: { // 选项
      type: Array,
      default: () => {
        return []
      }
    },
    props: Object
  },
  data() {
    return {
      list: [], // 列表数据
      searchInput: '', // 搜索值
      activeTab: 'enterprise',
      tabs: [
        { label: '企业标签', value: 'enterprise' }
      ],
      tagList: []

    }
  },
  computed: {
    // 筛选值
    showList() {
      const list = {
        enterprise: this.list,
        person: [{ labelList: this.tagList || [] }]
      }[this.activeTab]
      const showList = []
      list.forEach(item => {
        // PinyinMatch更新没有搜索内容，默认返回true，否则 搜索 空字符串会返回false
        const obj = {
          name: item.name,
          labelList: this.searchInput ? item.labelList.filter(subItem => {
            return PinyinMatch.match(subItem.name || '', this.searchInput)
          }) : item.labelList
        }

        if (obj.labelList?.length > 0) {
          showList.push(obj)
        }
      })
      return showList
    },

    config() {
      return merge({ ...DefaultTag }, this.props || {})
    },

    isPerson() {
      return this.activeTab == 'person' && this.config.showPersonTag
    }

  },
  watch: {
    show: {
      handler(val) {
        if (val) {
          this.checkItems(this.selectedData)
        }
      },
      immediate: true
    },
    selectedData: {
      handler(val) {
        if (this.show) {
          this.checkItems(val)
        }
      },
      deep: true
    }
  },
  created() {
    if (this.config.showPersonTag) {
      this.tabs.push({ label: '个人标签', value: 'person' })
      this.getPersonLabel()
    }
  },
  mounted() {

  },
  methods: {
    /**
     * 切换标签类别
     */
    tabsClick(item) {
      this.activeTab = item.value
    },

    /**
     * 获取个人标签
     */
    getPersonLabel() {
      const { request, params } = this.config
      request(params).then(res => {
        this.tagList = res.data || []
        this.tagList.forEach(subItem => {
          this.selectedData.forEach(thirdItem => {
            if (thirdItem.name == subItem.name && thirdItem.value == subItem.color) {
              subItem.isCheck = true
            }
          })
        })
      }).catch(() => {

      })
    },

    /**
     * 标记勾选
     */
    checkItems(items) {
      this.list = objDeepCopy(this.options)
      this.list.forEach(item => {
        item.labelList.forEach(subItem => {
          items.forEach(thirdItem => {
            if (thirdItem.name == subItem.name && thirdItem.value == subItem.color) {
              subItem.isCheck = true
            }
          })
        })
      })
    },

    /**
     * 获取当前标签的背景颜色、字体颜色
     */
    getItemStyle(item) {
      if (item.isCheck) {
        return {
          'background-color': item.color,
          color: '#fff'
        }
      }
      return
    },

    /**
     * 选择标签
     */
    select(subItem) {
      this.$set(subItem, 'isCheck', !subItem.isCheck)
      this.$forceUpdate()
    },

    /**
    * 关闭
    */
    close() {
      this.$emit('update:show', false)
    },

    /**
     * 确定
     */
    submit() {
      const selectedData = []
      const tagMap = {}
      const newList = [
        ...this.list,
        { labelList: this.tagList }
      ]
      // eslint-disable-next-line
      for (const item of newList) {
        // eslint-disable-next-line
        for (const subItem of item.labelList) {
          const key = `${subItem.name + subItem.color}`
          if (!(key in tagMap) && subItem.isCheck) {
            tagMap[key] = subItem
          }
        }
      }

      Object.values(tagMap).forEach(item => {
        if (item.isCheck) {
          selectedData.push({ name: item.name, value: item.color })
        }
      })

      this.$emit('changeCheckout', selectedData)
    }
  }
}
</script>

<style lang="scss" scoped>
.tag {
  .header {
    height: 45px;
    padding: 0 15px;
    line-height: 40px;
    border-bottom: 1px solid $--border-color-base;

    .title {
      flex: 1;
      font-size: 16px;
    }

    .close {
      display: block;
      padding: 10px;
      margin-right: -10px;
      font-size: 20px;
      color: #909399;
      cursor: pointer;
    }

    .close:hover {
      color: $--color-primary;
    }
  }

  .body {
    padding: 10px 12px;

    .search {
      margin-bottom: 15px;

      ::v-deep .el-input input {
        background-color: #f4f4f4;
        border: none;
      }
    }

    .tab-wrap {
      display: flex;
      align-items: center;
      justify-content: space-between;

      & > .el-button {
        font-size: 14px;
        color: $--color-primary;
      }
    }

    .content {
      height: 280px;
      margin: 10px 0;
      overflow-y: auto;

      .group {
        padding: 5px 0;

        &-name {
          margin-bottom: $--interval-base;
        }

        .group-item {
          position: relative;
          height: 25px;
          padding: 0 10px;
          margin: 0 5px 5px 0;
          font-size: 12px;
          line-height: 25px;
          color: $--color-text-regular;
          cursor: pointer;
          background-color: $--background-color-base;
          border-radius: $--border-radius-base;
        }
      }

      ::v-deep .empty-mask {
        height: 100px;
      }
    }
  }

  .footer {
    padding: 8px;
    text-align: right;
    background-color: #f7f8fa;
    border-top: 1px solid $--border-color-base;
  }
}
</style>
