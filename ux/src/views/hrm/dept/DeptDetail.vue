<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 13:37:31
 * @LastEditTime: 2020-07-03 17:50:17
 * @LastEditors: yang
-->
<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header
        :detail-value="detailData.name"
        :dropdown-items="dropdownItems"
        :show-edit="editAuth"
        :icon-class="iconClass"
        detail-name="组织部门"
        icon-bg-color="#ECEEF2"
        icon-color="#42526E"
        @edit="editClick"
        @command-select="commandSelect"
      />
      <el-tabs
        v-model="tabCurrentName"
        style="flex: 1;"
        class="side-detail__tabs--default">
        <el-tab-pane
          label="详细资料"
          name="baseInfo"
          lazy>
          <wk-base-detail
            :list="baseList"
          />
        </el-tab-pane>
        <el-tab-pane
          label="员工列表"
          name="employee"
          lazy>
          <relative-employee
            :id="id"
          />
        </el-tab-pane>
      </el-tabs>
    </flexbox>
    <dept-add-dialog
      :data="detailData"
      :visible.sync="deptAddDialogShow"
      @success="editSuccess"
    />
  </slide-view>
</template>

<script>
import {
  hrmDeptQueryByIdAPI,
  hrmDeptDeleteByIdAPI
} from '@/api/hrm/dept'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/WkDetailHeader'
import WkBaseDetail from '@/components/WkBaseDetail'
import RelativeEmployee from './components/RelativeEmployee'
import DeptAddDialog from './components/DeptAddDialog'

import { mapGetters } from 'vuex'

export default {
  // 组织详情
  name: 'DeptDetail',
  components: {
    SlideView,
    WkDetailHeader,
    WkBaseDetail,
    RelativeEmployee,
    DeptAddDialog
  },
  props: {
    // 详情信息id
    id: [String, Number],
    // 监听的dom 进行隐藏详情
    listenerIDs: {
      type: Array,
      default: () => {
        return ['crm-main-container']
      }
    },
    // 不监听
    noListenerIDs: {
      type: Array,
      default: () => {
        return []
      }
    },
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body', 'org-tree-node-label']
      }
    }
  },
  data() {
    return {
      loading: false,
      detailData: null,
      tabCurrentName: 'baseInfo',
      // 编辑操作
      deptAddDialogShow: false
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 权限
    deptAuth() {
      return this.hrm.dept
    },

    // 编辑
    editAuth() {
      return this.deptAuth.update
    },

    // 删除 第一层不展示删除
    deleteAuth() {
      return this.deptAuth.delete && this.detailData && this.detailData.parentId !== 0
    },

    dropdownItems() {
      return this.deleteAuth ? [{
        icon: 'wk wk-delete',
        label: '删除',
        command: 'delete'
      }] : []
    },

    baseList() {
      if (this.detailData) {
        return [
          {
            name: '',
            list: [
              {
                label: '组织部门名称',
                value: this.detailData.name
              },
              {
                label: '组织编码',
                value: this.detailData.code
              },
              {
                label: '组织类型',
                value: {
                  1: '公司',
                  2: '部门'
                }[this.detailData.deptType]
              },
              {
                label: '在职员工',
                value: this.detailData.allNum
              },
              {
                label: '全职员工',
                value: this.detailData.fullTimeNum
              },
              {
                label: '非全职人数',
                value: this.detailData.nuFullTimeNum
              },
              {
                label: '上级组织',
                value: this.detailData.pname
              },
              {
                label: '组织负责人',
                value: this.detailData.mainEmployeeName
              },
              {
                label: '分管领导',
                value: this.detailData.leadEmployeeName
              }
            ]
          }]
      }
      return []
    },
    iconClass() {
      if (!this.detailData) {
        return ''
      }
      return {
        1: 'wk wk-subordinate',
        2: 'wk wk-customer-solid'
      }[this.detailData.deptType]
    }
  },
  watch: {
    id() {
      this.detailData = null
      this.getDetial()
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    viewAfterEnter() {
      this.getDetial()
    },

    /**
     * 详情
     */
    getDetial() {
      this.loading = true
      hrmDeptQueryByIdAPI(this.id)
        .then(res => {
          this.detailData = res.data || {}
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    /**
     * 编辑
     */
    editClick() {
      this.deptAddDialogShow = true
    },

    editSuccess() {
      this.getDetial()
      this.$emit('edit-success')
    },

    /**
     * 更多操作
     */
    commandSelect(command) {
      if (command === 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmDeptDeleteByIdAPI(this.id).then(res => {
              this.$message.success('删除成功')
              this.$emit('delete-success')
              this.hideView()
            })
          })
          .catch(() => {})
      }
    },

    /**
     * 关闭
     */
    hideView() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

::v-deep .el-tabs {
  .el-tabs__header {
    margin-bottom: 0;
  }

  .el-tabs__item {
    font-weight: bold;
    color: $--color-text-regular;
  }
}

.side-detail__tabs--default {
  padding: 0 20px;
}
</style>
