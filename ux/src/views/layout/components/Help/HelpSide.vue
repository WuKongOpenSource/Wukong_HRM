<template>
  <slide-view
    class="d-view"
    append-to-body
    :body-style="{padding: 0, height: '100%'}"
    :no-listener-class="['wk-help-tips']"
    @afterEnter="viewAfterEnter"
    @close="$emit('close')">
    <div class="help">
      <div class="help-menus">
        <div class="title">帮助</div>
        <div v-if="menuIndex !== 'Handbook'" class="menus-wrap">
          <menu-item
            v-for="(item, index) in menus"
            :key="index"
            :icon="item.iconClass"
            :title="item.name"
            :selectd="menuIndex === item.value"
            @click.native="menuClick(item, index)"
          >
            <i v-if="item.value === 'Handbook'" class="wk wk-icon-circle-right" />
          </menu-item>
        </div>

        <div v-else class="menus-wrap is-sub">
          <div class="menus-wrap-hd">
            <el-button type="text" icon="wk wk-icon-circle-left" @click="backClick">返回</el-button>
          </div>
          <div class="menus-wrap-bd">
            <div
              v-for="(section, sIndex) in subMenus"
              :key="sIndex"
              class="letter-block">
              <div class="header">
                <span class="label">{{ section.name }}</span>
                <span class="line" />
              </div>
              <div
                class="children">
                <menu-item
                  v-for="(item, index) in section.children"
                  :key="index"
                  :icon="item.iconClass"
                  :title="item.name"
                  :selectd="subMenuIndex === item.services_menu_id"
                  @click.native="subMenuClick(item, section.type)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="help-body" :class="{'no-padding': isNoPadding}">
        <component
          :is="menuIndex"
          :id="subMenuIndex"
          :type="subType"
          :q-id="props && props.menuIndex === 'Question' ? props.id : null"
          :q-type="props && props.menuIndex === 'Question' ? props.type : null"
          :class="`is-${menuIndex}`" />
      </div>
    </div>
  </slide-view>
</template>

<script>
import SlideView from '@/components/SlideView'
import MenuItem from '@/views/layout/components/Sidebar/Item'
import Download from './Download'
import Handbook from './Handbook'
import Question from './Question'

import request from './request'

export default {
  // HelpSide
  name: 'HelpSide',

  components: {
    SlideView,
    MenuItem,
    Download,
    Handbook,
    Question
  },

  props: {
    props: Object
  },

  data() {
    return {
      menuIndex: 'Question',
      menus: [{
        name: '系统功能介绍',
        value: 'Question',
        iconClass: 'wk wk-icon-issue-line',
        tips: ''
      }, {
        name: '帮助手册',
        value: 'Handbook',
        iconClass: 'wk wk-icon-intro-line',
        tips: ''
      }, {
        name: '移动版获取',
        value: 'Download',
        iconClass: 'wk wk-icon-mobile-line',
        tips: ''
      }],
      subMenus: [],
      subMenuIndex: '',
      subType: ''
    }
  },

  computed: {
    isNoPadding() {
      return this.menuIndex === 'Download'
    }
  },

  watch: {},

  created() {
    this.getData()
  },

  mounted() {
  },

  beforeDestroy() {
  },

  methods: {
    /**
     * 动画结束页面展示
     */
    viewAfterEnter() {
    },

    /**
     * 菜单点击
     */
    menuClick(item) {
      this.menuIndex = item.value
      if (item.value === 'Handbook') {
        if (this.subMenus.length > 0) {
          const firstMenu = this.subMenus[0]
          this.subMenuIndex = firstMenu.children[0].services_menu_id
          this.subType = firstMenu.type
        }
      }
    },

    /**
     * 子菜单点击
     */
    subMenuClick(item, type) {
      this.subType = type
      this.subMenuIndex = item.services_menu_id
    },

    /**
     * 返回
     */
    backClick() {
      this.menuIndex = 'Question'
    },

    /**
     * 数据获取
     */
    getData() {
      // item 1crm   2oa办公  3人资  4财务
      request.post('/doc').then(response => {
        const res = response.data
        if (response.status === 200 && res.code === 200) {
          const resData = res.data || []
          this.subMenus = resData
        }

        if (this.props && this.props.menuIndex === 'Question') {
          this.menuIndex = this.props.menuIndex
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.d-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
}

.menu-item-content {
  .wk-icon-circle-right {
    visibility: hidden;
  }

  &:hover {
    .wk-icon-circle-right {
      visibility: visible;
    }
  }
}

.wk-icon-circle-right {
  position: absolute;
  top: 6px;
  right: 0;
}

.help {
  position: relative;
  height: 100%;
  overflow-y: auto;

  &-menus {
    position: absolute;
    top: 0;
    left: 0;
    width: 240px;
    height: 100%;
    overflow: auto;
    background-color: $--color-n10;

    > .title {
      padding: 24px 32px 8px;
      font-size: 18px;
    }

    > .menus-wrap {
      height: calc(100% - 70px);
      padding: 0 16px;
      overflow-y: auto;

      &.is-sub {
        padding-right: 0;
      }
    }

    .menus-wrap-hd {
      position: relative;
      line-height: 40px;

      &::before {
        position: absolute;
        right: -16px;
        bottom: 0;
        left: -16px;
        height: 1px;
        content: " ";
        background-color: $--border-color-base;
      }
    }

    .menus-wrap-bd {
      height: calc(100% - 40px);
      padding-top: 8px;
      padding-right: 8px;
      overflow-y: auto;
    }
  }

  &-body {
    position: relative;
    height: 100%;
    padding: 24px 0 24px 32px;
    margin-left: 240px;
    overflow: hidden;
    background-color: white;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);

    &.no-padding {
      padding: 0;
    }

    &.is-Question {
      padding-left: 16px;
    }
  }
}

.letter-block {
  font-size: $--font-size-base;

  > .header {
    position: relative;
    height: 21px;
    line-height: 21px;

    > .label {
      position: absolute;
      left: 0;
      z-index: 2;
      padding-right: 8px;
      color: $--color-n70;
      background-color: $--color-n10;
    }

    > .line {
      position: absolute;
      top: 10px;
      right: 0;
      left: 0;
      z-index: 1;
      height: 0.5px;
      background-color: $--border-color-base;
    }
  }

  .children {}
}
</style>
