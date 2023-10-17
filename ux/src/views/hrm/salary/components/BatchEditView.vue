<template>
  <transition name="opacity-fade">
    <div class="edit-view">
      <flexbox class="edit-header">
        <div class="title">批量修改薪资</div>
        <div class="handle">
          <el-button @click.native="handleClick('cancel')">放弃编辑</el-button>
          <el-button
            type="primary"
            @click.native="handleClick('save')">保存</el-button>
        </div>
      </flexbox>
      <div class="edit-body">
        <el-table
          v-loading="loading"
          :data="list"
          :height="tableHeight"
          :cell-style="cellStyle"
          :header-cell-style="headerCellStyle"
          border>
          <el-table-column
            v-for="(item, index) in fieldList"
            :key="'field'+index"
            :label="item.name"
            :fixed="index <= 3 "
            :width="item.width"
            :prop="item.field"
            show-overflow-tooltip>
            <template slot-scope="scope">
              <template v-if="index <= 3 || item.isFixed">{{ scope.row[scope.column.property] }}</template>
              <el-input-number
                v-else
                v-model="scope.row[scope.column.property]"
                :controls="false"
                :min="0"
                :max="100000000"
                :precision="2" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </transition>
</template>

<script type="text/javascript">
import {
  hrmSalaryMonthRecordUpdateAPI,
  hrmSalaryMonthRecordListAPI
} from '@/api/hrm/salary'

import { getMaxIndex, objDeepCopy } from '@/utils'

export default {
  name: 'BatchEditView',
  components: {},
  filters: {},
  props: {
    fieldList: Array,
    params: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      loading: false,
      tableHeight: document.documentElement.clientHeight - 100,
      list: [] // 数据源
    }
  },
  computed: {},
  created() {
    this.getList()
  },
  mounted() {
    this.$el.style.zIndex = getMaxIndex()
    document.body.appendChild(this.$el)

    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 100
    }
  },
  destroyed() {
    // remove DOM node after destroy
    if (this.$el && this.$el.parentNode) {
      this.$el.parentNode.removeChild(this.$el)
    }
  },
  methods: {
    /**
     * 固定工资设置展示列表
     */
    getList() {
      this.loading = true
      hrmSalaryMonthRecordListAPI({
        ...this.params,
        pageType: 0
      }).then(res => {
        const list = res.data.list
        const newList = []
        list.forEach(item => {
          const salary = item.salary || []
          const newItem = {
            sempRecordId: item.sempRecordId,
            employeeName: item.employeeName,
            jobNumber: item.jobNumber,
            deptName: item.deptName,
            post: item.post
          }
          salary.forEach(sItem => {
            newItem[sItem.code] = sItem.value
          })
          newList.push(newItem)
        })
        this.list = newList

        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /**
     * 头部操作
     */
    handleClick(type) {
      if (type == 'cancel') {
        this.$confirm('确定放弃编辑并退出, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.$emit('close')
          })
          .catch(() => {})
      } else {
        this.loading = true
        const list = []
        const systemFields = this.fieldList.filter(item => item.isFixed)
        this.list.forEach(item => {
          const temp = objDeepCopy(item)
          const sempRecordId = temp.sempRecordId
          delete temp['sempRecordId']
          delete temp['employeeName']
          delete temp['jobNumber']
          delete temp['deptName']
          delete temp['post']
          systemFields.forEach(fItem => {
            delete temp[fItem.field]
          })

          list.push({
            sempRecordId: sempRecordId,
            salaryValues: temp
          })
        })
        hrmSalaryMonthRecordUpdateAPI(list).then(res => {
          this.$emit('change')
          this.$message.success('操作成功')
          this.loading = false
          this.$emit('close')
        }).catch(() => {
          this.loading = false
        })
      }
    },
    /** 通过回调控制style */
    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex <= 3) {
        return {
          color: '#787879',
          backgroundColor: '#F7F8FA',
          textAlign: 'center'
        }
      } else {
        return { textAlign: 'center' }
      }
    },
    headerCellStyle({ row, column, rowIndex, columnIndex }) {
      return { textAlign: 'center' }
    }
  }
}
</script>
<style lang="scss" scoped>
.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

.edit-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: white;
}

.edit-header {
  padding: 20px 20px 20px 40px;

  .title {
    flex: 1;
    font-size: 16px;
    font-weight: bold;
  }

  .handle {
    flex-shrink: 0;
  }
}

.edit-body {
  padding: 0 40px;
}

.el-table ::v-deep .el-input-number {
  width: 100%;

  // position: absolute;
  // top: 0;
  // right: 0;
  // bottom: 0;
  // left: 0;
  // border: 0 none;
  .el-input__inner {
    height: 39px;
    line-height: 39px;
    text-align: center;
    border: 1px solid transparent;
    border-radius: 0;

    &:focus {
      border: 1px solid #3e84e9;
    }
  }
}
</style>
