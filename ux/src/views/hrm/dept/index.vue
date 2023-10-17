<template>
  <div class="wrapper">
    <xr-header
      ft-top="0"
      placeholder="请输入组织部门名称"
      show-search
      @search="searchClick">
      <template slot="label">
        组织管理<el-tooltip
          v-if="hrmUserType === 2"
          effect="dark"
          placement="top"
          style="margin-left: 8px;"
          content="仅展示您管理范围内的员工">
          <i class="wk wk-help wk-help-tips" />
        </el-tooltip>
      </template>
      <template slot="bottom-ft">
        <wk-toggle-button
          v-model="showType">
          <wk-toggle-item
            v-for="item in [{name:'列表视图', value: 'table', icon: 'wk wk-icon-three-line'}, {name:'架构视图', value: 'tree', icon: 'wk wk-icon-org-solid'}]"
            :key="item.value"
            :label="item.name"
            :value="item.value">
            {{ item.name }}<i :class="item.icon" />
          </wk-toggle-item>
        </wk-toggle-button>
      </template>
      <template slot="ft">
        <el-button
          v-if="createAuth"
          type="primary"
          @click="createClick">新建组织
        </el-button>
      </template>
    </xr-header>
    <div
      v-loading="loading"
      :class="{'is-org' : showType === 'tree'}"
      class="wrapper-main">
      <tree-table
        v-if="showType == 'table'"
        :data="list"
        :height="tableHeight"
        :columns="columns"
        :eval-args="[null, null, fieldFormatter]"
        :cell-class-name="cellClassName"
        stripe
        class="el-table-header--white"
        expand-all
        @row-click="handleRowClick" />
      <div
        v-else
        :style="{height: `${tableHeight}px`}"
        class="wk-org-tree">
        <vue2-org-tree
          :data="orgTreeData"
          :label-class-name="orgTreeClass"
          :props="{label: 'name', children: 'children', expand: 'expand'}"
          :render-content="renderContent"
          @on-node-click="orgTreeClick"
        />
      </div>
    </div>

    <dept-add-dialog
      v-if="deptAddDialogShow"
      :visible.sync="deptAddDialogShow"
      @success="getDeptList"
    />

    <dept-detail
      v-if="detailShow"
      :id="detailId"
      @close="detailShow = false"
      @edit-success="getDeptList"
      @delete-success="getDeptList"
    />
  </div>
</template>

<script>
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import { WkToggleButton, WkToggleItem } from '@/components/NewCom/WkToggleButton/index'
import TreeTable from './TreeTable/index'
import XrHeader from '@/components/XrHeader/Search'
import DeptAddDialog from './components/DeptAddDialog'
import DeptDetail from './DeptDetail'

import { mapGetters } from 'vuex'

export default {
  name: 'Index',
  components: {
    TreeTable,
    XrHeader,
    DeptAddDialog,
    DeptDetail,
    WkToggleButton,
    WkToggleItem
  },
  data() {
    return {
      loading: false,
      tableHeight: document.documentElement.clientHeight - 220, // 表的高度
      search: '',
      showType: 'table', // tree
      list: [], // 列表
      deptAddDialogShow: false, // 添加
      columns: [
        {
          text: '组织部门名称',
          value: 'name',
          width: 400
        },
        {
          text: '组织类型',
          value: 'deptTypeName'
        },
        {
          text: '在职员工',
          value: 'allNumName',
          helpType: '30',
          helpId: '277'
        },
        {
          text: '全职员工',
          value: 'fullTimeNumName',
          helpType: '30',
          helpId: '285'
        },
        {
          text: '非全职人数',
          value: 'nuFullTimeNumName',
          helpType: '30',
          helpId: '286'
        }
      ],
      // 详情
      detailShow: false,
      detailId: null
    }
  },
  computed: {
    ...mapGetters([
      'hrm',
      'hrmUserType'
    ]),

    // 权限
    deptAuth() {
      return this.hrm && this.hrm.dept
    },

    // 新建
    createAuth() {
      return this.deptAuth.save
    },

    // 详情
    detailAuth() {
      return this.deptAuth.read
    },

    orgTreeData() {
      if (this.list.length > 0) {
        return this.list[0]
      }
      return {}
    }
  },
  watch: {
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 220
    }
    this.getDeptList()
  },
  mounted() {
  },
  methods: {
    /**
     * 获取部门列表
     * @param  获取树形结构列表
     */
    getDeptList() {
      this.loading = true
      hrmDeptQueryTreeListAPI({
        type: 'tree',
        name: this.search
      })
        .then(response => {
          this.list = response.data || []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 展示格式化
     */
    fieldFormatter(row) {
      row.deptTypeName = {
        1: '公司',
        2: '部门'
      }[row.deptType]

      row.allNumName = `${row.myAllNum}（${row.allNum || 0}）`
      row.fullTimeNumName = `${row.myFullTimeNum}（${row.fullTimeNum || 0}）`
      row.nuFullTimeNumName = `${row.myNuFullTimeNum}（${row.nuFullTimeNum || 0}）`
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0 && this.detailAuth) {
        return 'can-visit-default'
      } else {
        return ''
      }
    },

    /**
     * 搜索
     */
    searchClick(search) {
      this.search = search
      this.getDeptList()
    },
    /**
     * 新建
     */
    createClick() {
      this.deptAddDialogShow = true
    },

    /**
     * 列表点击 详情
     */
    handleRowClick(row, column, event) {
      if (this.detailAuth) {
        this.detailId = row.deptId
        this.detailShow = true
      }
    },

    /** ****组织图 */
    renderContent(h, data) {
      return `${data.name}（${data.allNum}）`
    },

    orgTreeClass(data) {
      return {
        1: 'org-tree-company',
        2: 'org-tree-department'
      }[data.deptType]
    },

    orgTreeClick(e, data) {
      this.handleRowClick(data)
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  width: 100%;

  .xr-header {
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  .search-button {
    border: none;

    &.is-select {
      color: white;
      background: $--color-primary;
    }
  }

  &-main {
    &.is-org {
      text-align: center;
    }
  }

  ::v-deep .el-radio-button__inner {
    padding: 7px 15px;
  }

  .wk-org-tree {
    overflow: auto;
  }
}
</style>
