<template>
  <div class="main-body">
    <div class="content-header">
      <span class="title">{{ title }}</span>
      <span>
        <el-button
          @click="backClick">返回</el-button>
        <el-button
          type="primary"
          @click="saveClick">保存</el-button>
      </span>
    </div>
    <el-tabs v-model="labelTab" class="el-fields-tabs" @tab-click="tabsClick">
      <el-tab-pane
        v-for="(tab, index) in tabList"
        :key="index"
        :label="tab.label"
        :name="tab.name"
        lazy>
        <el-table
          v-loading="loading"
          :data="tabListObj[labelTab] || []"
          :class="WKConfig.tableStyle.class"
          :stripe="WKConfig.tableStyle.stripe"
          :height="tableHeight"
          class="main-table"
          highlight-current-row
          style="width: 100%;">
          <el-table-column
            show-overflow-tooltip
            prop="name"
            width="150"
            label="信息" />
          <el-table-column
            prop="isManageVisible"
            label="员工是否可见">
            <template slot-scope="{ row }">
              <el-checkbox
                v-if="row.isManageVisible === 2 || row.isManageVisible === 3"
                v-model="row.isManageVisible"
                :true-label="3"
                :false-label="2"
                disabled />
              <el-checkbox
                v-else
                v-model="row.isManageVisible"
                :true-label="1"
                :false-label="0" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {
  hrmEmployeeFieldManageQueryAPI,
  hrmEmployeeFieldManageSetAPI
} from '@/api/admin/hrm'

/**
 * isManageVisible 是否管理员可见 0 否 1 是 2 禁用否 3 禁用是
 */
export default {
  // 新建员工字段设置
  name: 'EmployeeFieldsSet',
  components: {
  },
  mixins: [],
  props: {
    entryStatus: [Number, String]
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 280, // 表的高度
      labelTab: '1', // 标签 1 个人信息 2 岗位信息 3 合同 4 工资社保
      tabList: [{
        name: '1',
        label: '个人信息'
      }, {
        name: '2',
        label: '岗位信息'
      }],
      // 存储已获取tab下列表数据
      tabListObj: {}
    }
  },
  computed: {
    title() {
      if (this.entryStatus == 1) {
        return '新建在职员工'
      } else if (this.entryStatus == 2) {
        return '新建待入职员工'
      }

      return ''
    }
  },
  mounted() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 280
    }

    this.getList()
  },
  methods: {
    /**
     * tabs change
     */
    tabsClick() {
      this.getList()
    },

    /**
     * 获取列表数据
     */
    getList() {
      const list = this.tabListObj[this.labelTab]
      if (!list) {
        this.loading = true
        hrmEmployeeFieldManageQueryAPI({
          entryStatus: this.entryStatus,
          label: this.labelTab
        })
          .then(res => {
            this.$set(this.tabListObj, this.labelTab, res.data || [])
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    /**
     * 保存
     */
    saveClick() {
      this.loading = true
      let data = []
      for (const key in this.tabListObj) {
        const list = this.tabListObj[key]
        if (list && list.length > 0) {
          data = [...data, ...list]
        }
      }
      hrmEmployeeFieldManageSetAPI(data)
        .then(res => {
          this.$message.success('操作成功')
          this.tabListObj = {}
          this.getList()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 返回
     */
    backClick() {
      const { redirect } = this.$route.query
      if (redirect) {
        this.$router.push(redirect)
      } else {
        this.$emit('back')
      }
    }
  }
}
</script>

<style lang="scss">
.el-fields-tabs {
  .el-tabs__content {
    height: calc(100% - 100px);
    overflow-y: auto;
  }
}
</style>
<style lang="scss" scoped>
@import "../../style/index.scss";
@import "../../../styles/table.scss";

.mark {
  display: inline-block;
  width: 4px;
  height: 15px;
  margin-right: 5px;
  vertical-align: text-bottom;
  background-color: $--color-primary;
  border-radius: 2px;
}
</style>
