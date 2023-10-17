<template>
  <employee-fields-set
    v-if="isSet"
    :entry-status="entryStatus"
    @back="backMain" />
  <div v-else>
    <div class="content-header">
      <span>新建员工字段设置</span>
    </div>
    <div
      class="content-body">
      <div class="card-wrap">
        <el-card>
          <i class="wk wk-s-contacts-line" style="padding: 2px;font-size: 26px;" />
          <div class="body-content">
            <div>新建在职员工</div>
            <div class="des">设置【新建在职员工】时可填写的员工信息字段</div>

            <el-button type="primary" size="medium" @click="enterSet(1)">编辑</el-button>
          </div>
        </el-card>

        <el-card>
          <i class="wk wk-allow-me" />
          <div class="body-content">
            <div>新建待入职员工</div>
            <div class="des">设置【新建待入职员工】时可填写的员工信息字段</div>
            <el-button type="primary" size="medium" @click="enterSet(2)">编辑</el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import EmployeeFieldsSet from './EmployeeFieldsSet' // 员工档案设置

export default {
  // 新建员工字段设置
  name: 'EmployeeFields',

  components: {
    EmployeeFieldsSet
  },

  data() {
    return {
      loading: false, // 展示加载中效果
      list: [],
      isSet: false,
      entryStatus: 1 // 入职状态 1 在职 2 待入职
    }
  },

  computed: {
  },

  created() {
    const { entryStatus } = this.$route.query
    if (entryStatus) {
      this.enterSet(entryStatus)
    }
  },

  methods: {
    /**
     * 进入配置
     */
    enterSet(entryStatus) {
      this.entryStatus = entryStatus
      this.isSet = true
    },

    /**
     * 返回主页面
     */
    backMain() {
      this.isSet = false
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../../style/index.scss"; // 右侧内容样式

.card-wrap {
  display: block;
  padding: #{$--interval-base * 2};
  background-color: $--color-n10;
  border-radius: $--border-radius-base;
}

::v-deep .el-card {
  display: inline-block;
  width: 250px;
  border: none;
  box-shadow: $--box-shadow-bottom-light;

  .el-card__header {
    display: none;
  }

  .el-card__body {
    display: flex;

    i {
      flex-shrink: 0;
      font-size: 30px;
      color: $--color-primary;
    }

    .body-content {
      flex: 1;
      margin-left: #{$--interval-base * 2};

      > .des {
        margin-top: #{$--interval-base / 2};
        font-size: $--font-size-small;
        color: $--color-text-secondary;
      }

      .el-button {
        margin-top: #{$--interval-base * 2};
      }
    }
  }
}

.el-card + .el-card {
  margin-left: #{$--interval-base * 2};
}
</style>
