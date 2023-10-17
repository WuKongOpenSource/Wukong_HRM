<template>
  <div class="main">
    <div class="main-title">
      系统功能介绍
    </div>
    <div class="main-des">
      为了帮助您更好地使用悟空CRM，现对系统功能做进一步介绍，其中包括操作方法、术语解释、数据来源等。
    </div>
    <div class="main-filter">
      <el-select
        v-model="moduleId"
        mode="no-border"
        @change="moduleChange">
        <el-option
          v-for="item in moduleList"
          :key="item.id"
          :label="item.name"
          :value="item.id" />
      </el-select>

      <el-select
        v-model="menuId"
        mode="no-border"
        @change="getData">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.name"
          :value="item.id" />
      </el-select>
    </div>
    <div class="main-body">
      <el-collapse v-model="currentMenuId" accordion>
        <el-collapse-item
          v-for="(item, index) in list"
          :key="index"
          :name="item.id">
          <template slot="title">
            <i class="wk wk-icon-success" /> {{ item.name }}
          </template>
          <div class="item-content" v-html="getContent(item.content)" />
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import request from './request'

export default {
  // Question
  name: 'Question',

  components: {},

  props: {
    qType: Number,
    qId: [String, Number]
  },

  data() {
    return {
      list: [],
      moduleId: '',
      moduleList: [],
      menuId: '',
      currentMenuId: '',
      options: []
    }
  },

  computed: {
  },

  watch: {
    qId: {
      handler() {
        this.menuId = this.qType ? this.qType : 4
        this.getDefaultModuleIdAndOptions()
        this.getData()
      },
      immediate: true
    }
  },

  created() {
    this.getMenus()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取菜单
     */
    getMenus() {
      request.post('/platformList')
        .then((response) => {
          const res = response.data
          if (response.status === 200 && res.code === 200) {
            this.moduleList = res.data || []

            this.getDefaultModuleIdAndOptions()
          }
        })
        .catch(() => {
        })
    },

    /**
     * 获取默认moduleId 和 Options
     */
    getDefaultModuleIdAndOptions() {
      if (this.menuId && this.moduleList.length >= 2) {
        const moduleItem = this.moduleList[this.moduleListCoord()]
        this.moduleId = moduleItem.id
        this.options = moduleItem.children
      }
    },
    /**
     * moduleList坐标
     */
    moduleListCoord() {
      switch (true) {
        case this.menuId == 48 : return 5
        case this.menuId <= 17 : return 0
        case this.menuId <= 28 : return 1
        case this.menuId <= 35 : return 2
        case this.menuId <= 42 : return 3
        case this.menuId == 43 : return 1
      }
    },
    /**
     * 模块 change
     */
    moduleChange() {
      this.options = this.moduleList.find(item => item.id === this.moduleId).children
      this.menuId = this.options.length > 0 ? this.options[0].id : ''
      if (this.menuId) {
        this.getData()
      } else {
        this.list = []
      }
    },

    /**
     * 获取数据
     */
    getData() {
      request.post('/platform', {
        menu_id: this.menuId
      })
        .then((response) => {
          const res = response.data
          if (response.status === 200 && res.code === 200) {
            this.list = res.data || []
            this.$nextTick(() => {
              if (this.qId) {
                this.currentMenuId = this.qId || ''

                setTimeout(() => {
                  const findIndex = this.list.findIndex(item => item.id == this.currentMenuId)
                  if (findIndex >= 5) {
                    this.$el.scrollTop = 52 * (findIndex - 4)
                  }
                }, 300)
              } else {
                this.currentMenuId = ''
              }
            })
          }
        })
        .catch(() => {
        })
    },

    /**
     * 获取内容 过滤掉style
     */
    getContent(value) {
      return value
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style.scss";

.main-title {
  padding-left: 8px;
}

.main-filter {
  display: flex;
  padding-left: 8px;
  margin-top: 16px;

  .el-select + .el-select {
    padding-left: 8px;
  }
}

.main-body {
  margin-top: 8px;

  ::v-deep .item-content {
    img {
      width: 100%;
    }

    color: $--color-n700;

    p,
    span {
      font-family: inherit !important;
      font-size: 14px !important;
      line-height: 2 !important;
      color: $--color-n700 !important;
      background-color: transparent !important;
    }
  }
}

.main-des {
  padding-left: 8px;
}

.el-collapse {
  border-top: none;
  border-bottom: none;
}

::v-deep .el-collapse-item {
  padding: 8px;
  margin-bottom: 6px;
  border-radius: $--border-radius-base;

  &.is-active {
    background-color: $--color-y50;

    .wk-icon-success {
      color: $--color-g400;
    }
  }

  .wk-icon-success {
    margin-right: 8px;
    color: $--color-n60;
  }

  .el-collapse-item__header {
    height: 30px;
    font-size: 16px;
    line-height: 30px;
    background-color: transparent;
    border-bottom: none;
  }

  .el-collapse-item__content {
    padding-bottom: 0;
    margin-top: 8px;
    font-size: 14px;
  }

  .el-collapse-item__wrap {
    padding-left: 23px;
    background-color: transparent;
    border-bottom: none;
  }
}
</style>
