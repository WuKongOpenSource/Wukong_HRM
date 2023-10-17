<template>
  <div class="main-body">
    <div class="content-header">
      <span>员工档案设置</span>
      <span>
        <el-button
          @click="sendClick">邀请员工填写档案</el-button>
        <el-button
          type="primary"
          @click="saveClick">保存</el-button>
      </span>
    </div>
    <el-table
      v-loading="loading"
      :data="list"
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
        label="信息">
        <template slot-scope="scope">
          <span v-if="scope.row.isSection" class="mark" />{{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column
        prop="isEmployeeVisible"
        label="员工是否可见">
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.isEmployeeVisible"
            :true-label="1"
            :false-label="0"
            :indeterminate="scope.row.visibleIndeterminate"
            @change="visibleChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column
        prop="isEmployeeUpdate"
        label="员工是否可编辑">
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.isEmployeeUpdate"
            :true-label="1"
            :false-label="0"
            :disabled="scope.row.isEmployeeUpdate === 2"
            :indeterminate="scope.row.updateIndeterminate"
            @change="updateChange(scope.row)" />
        </template>
      </el-table-column>
    </el-table>
    <wk-dep-user-dialog
      v-if="depUserViewDialogShow"
      :visible.sync="depUserViewDialogShow"
      :props="{
        showDisableUser: false
      }"
      @change="selectUserChange"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeArchivesQueryFieldAPI,
  hrmEmployeeArchivesSendAPI,
  hrmEmployeeArchivesSetFieldAPI
} from '@/api/admin/hrm'

import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'

export default {
  // 员工档案设置
  name: 'EmployeeArchivesSet',
  components: {
    WkDepUserDialog
  },
  mixins: [],
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 196, // 表的高度
      list: [],
      baseList: [],
      baseObj: {
        name: '基本信息',
        isSection: true,
        labelGroup: 1,
        isEmployeeVisible: 1,
        isEmployeeUpdate: 1,
        visibleIndeterminate: false,
        updateIndeterminate: false
      },
      communicationList: [],
      communicationObj: {
        name: '通讯信息',
        labelGroup: 2,
        isSection: true,
        isEmployeeVisible: 1,
        isEmployeeUpdate: 1,
        indeterminate: false,
        updateIndeterminate: false
      },
      depUserViewDialogShow: false
    }
  },
  computed: {},
  mounted() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 196
    }

    this.getList()
  },
  methods: {
    /**
     * 获取列表数据
     */
    getList() {
      this.loading = true
      hrmEmployeeArchivesQueryFieldAPI()
        .then(res => {
          const list = res.data || []
          const baseList = []
          const communicationList = []
          list.forEach(item => {
            if (item.labelGroup === 1) {
              baseList.push(item)
            } else if (item.labelGroup === 2) {
              communicationList.push(item)
            }
          })

          this.communicationList = communicationList
          this.baseList = baseList

          const allList = [this.baseObj]
          baseList.forEach(item => {
            allList.push(item)
          })
          allList.push(this.communicationObj)
          communicationList.forEach(item => {
            allList.push(item)
          })
          this.list = allList
          this.changeAllStatus()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 调整状态
     * type 不存在验证全部
     */
    changeAllStatus() {
      const result = this.changeStatus('isEmployeeVisible', this.baseList)
      this.baseObj.visibleIndeterminate = result.indeterminate
      this.baseObj.isEmployeeVisible = result.check ? 1 : 0
      const result2 = this.changeStatus('isEmployeeUpdate', this.baseList)
      this.baseObj.updateIndeterminate = result2.indeterminate
      this.baseObj.isEmployeeUpdate = result2.check ? 1 : 0
      const result3 = this.changeStatus('isEmployeeVisible', this.communicationList)
      this.communicationObj.visibleIndeterminate = result3.indeterminate
      this.communicationObj.isEmployeeVisible = result3.check ? 1 : 0
      const result4 = this.changeStatus('isEmployeeUpdate', this.communicationList)
      this.communicationObj.updateIndeterminate = result4.indeterminate
      this.communicationObj.isEmployeeUpdate = result4.check ? 1 : 0
    },

    changeStatus(field, list) {
      let checkCount = 0
      let noCheckCount = 0
      list.forEach(item => {
        const value = item[field]
        if (value !== 2) {
          if (value === 1) {
            ++checkCount
          } else {
            ++noCheckCount
          }
        }
      })

      return {
        check: noCheckCount === 0,
        indeterminate: checkCount > 0 && noCheckCount > 0
      }
    },

    /**
     *  添加权限
     */
    sendClick() {
      this.depUserViewDialogShow = true
    },

    /**
     * 选择员工change
     */
    selectUserChange(users, deps) {
      if (users.length > 0 || deps.length > 0) {
        this.loading = true
        hrmEmployeeArchivesSendAPI({
          deptIds: deps,
          userIds: users
        }).then(res => {
          this.$message.success('操作成功')
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      } else {
        this.$message.error('请选择')
      }
    },

    /**
     * change
     */
    visibleChange(data) {
      if (data.isSection) {
        const list = data.labelGroup === 1 ? this.baseList : this.communicationList
        const value = data.isEmployeeVisible === 1
        list.forEach(item => {
          if (item.isEmployeeVisible !== 2) {
            item.isEmployeeVisible = data.isEmployeeVisible
            if (item.isEmployeeUpdate !== 2) {
              if (!value) {
                item.isEmployeeUpdate = 0
              }
            }
          }
        })
      } else {
        if (data.isEmployeeVisible === 0 && data.isEmployeeUpdate !== 2) {
          data.isEmployeeUpdate = 0
        }
      }
      this.changeAllStatus()
    },

    updateChange(data) {
      if (data.isSection) {
        const list = data.labelGroup === 1 ? this.baseList : this.communicationList
        const value = data.isEmployeeUpdate === 1
        list.forEach(item => {
          if (item.isEmployeeUpdate !== 2) {
            item.isEmployeeUpdate = data.isEmployeeUpdate
            if (value) {
              item.isEmployeeVisible = 1
            }
          }
        })
      } else {
        if (data.isEmployeeUpdate === 1) {
          data.isEmployeeVisible = 1
        }
      }
      this.changeAllStatus()
    },

    /**
     * 保存
     */
    saveClick() {
      this.loading = true
      hrmEmployeeArchivesSetFieldAPI(this.baseList.concat(this.communicationList))
        .then(res => {
          this.$message.success('操作成功')
          this.getList()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../style/index.scss"; // 右侧内容样式
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
