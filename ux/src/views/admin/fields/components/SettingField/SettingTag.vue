<template>
  <div class="setting-tag" @click.stop>
    <el-button
      :disabled="!editAuth"
      class="add-btn"
      @click="handleToSet">
      点击配置
    </el-button>

    <el-dialog
      :visible.sync="dialogVisible"
      :before-close="handleCloseDialog"
      :close-on-click-modal="false"
      title="标签设置"
      width="800px"
      append-to-body
      class="edit-dialog">
      <div class="edit-dialog-body">
        <div
          class="group-box is-tabs">
          <!-- 左边导航 -->
          <div class="nav">
            <flexbox
              justify="space-between"
              class="nav__hd">
              标签分组
              <el-button
                type="text"
                icon="el-icon-circle-plus"
                @click="newGroupBtn">创建分组</el-button>
            </flexbox>
            <draggable
              :list="groupList"
              class="group-nav-box"
              @end="dragEnd">
              <div
                v-for="(item, index) in groupList"
                :key="index"
                :class="{'is-select' : index == groupActiveIndex}"
                class="group-nav-item"
                @click="groupMenuSelect(item,index)">
                <span class="name">{{ item.name }}</span>
                <div
                  class="handle-icon">
                  <el-dropdown
                    trigger="click"
                    @command="groupHandleClick">
                    <i
                      class="el-icon-arrow-down"
                      @click="groupDropdownClick(index)" />
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="edit">编辑</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>
            </draggable>
          </div>

          <!-- 分组编辑 -->
          <el-dialog
            :title="newGroupTitle"
            :visible.sync="newGroupVisible"
            :before-close="newGroupClose"
            :close-on-click-modal="false"
            append-to-body
            width="400px">
            <label class="label-title">分组名称</label>
            <el-input
              v-model="group.name"
              :maxlength="100"
              class="input-group" />
            <span
              slot="footer"
              class="dialog-footer">
              <el-button
                type="primary"
                @click="newGroupSubmit">确定</el-button>
              <el-button @click="newGroupClose">取消</el-button>
            </span>
          </el-dialog>

          <!-- 右边内容 -->
          <div class="content-box">
            <div class="content-table">
              <flexbox
                justify="space-between"
                class="content-table-header">
                <div>{{ groupActive?groupActive.name :'分组' }}</div>
                <el-button
                  :disabled="groupList.length === 0"
                  size="medium"
                  class="xr-btn--orange"
                  type="primary"
                  @click.stop="newTagBtn">新建标签</el-button>
              </flexbox>
              <draggable
                :list="tagList"
                :style="{height: tableHeight + 'px' }"
                class="tag-table">
                <div
                  v-for="(item, index) in tagList"
                  :key="index"
                  class="tag-row">
                  <div class="cell" style="width: 100px;">
                    <span>{{ item.name }}</span>
                  </div>
                  <div class="cell" style="width: 200px;">
                    <div :style="{background:item.color,padding:'10px','border-radius':'4px'}" />
                  </div>
                  <div class="cell">
                    <el-button
                      icon="wk wk-edit"
                      type="text"
                      @click="tagHandleClick('editTag',item, index )" />
                    <el-button
                      icon="wk wk-delete"
                      type="text"
                      @click="tagHandleClick('delete',item, index )" />
                  </div>
                </div>

              </draggable>
              <!-- <el-table
                :data="tagList"
                :class="WKConfig.tableStyle.class"
                :stripe="WKConfig.tableStyle.stripe"
                :height="tableHeight"
                align="center"
                style="width: 100%;">
                <el-table-column
                  prop="name"
                  show-overflow-tooltip
                  label="标签名称" />
                <el-table-column
                  prop="color"
                  width="200"
                  label="颜色">
                  <template slot-scope="scope">
                    <div :style="{background:scope.row.color,padding:'10px','border-radius':'4px'}" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  label="操作">
                  <template slot-scope="{ row, column ,$index }">
                    <el-button
                      icon="wk wk-edit"
                      type="text"
                      @click="tagHandleClick('editTag',row,$index )" />
                    <el-button
                      icon="wk wk-delete"
                      type="text"
                      @click="tagHandleClick('delete',row,$index )" />
                  </template>
                </el-table-column>
              </el-table> -->
              <!-- <div class="p-contianer">
                <el-pagination
                  :current-page="currentPage"
                  :page-sizes="pageSizes"
                  :page-size.sync="pageSize"
                  :total="total"
                  class="p-bar"
                  background
                  layout="prev, pager, next, sizes, total, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"/>
              </div> -->
            </div>
          </div>
          <!-- 标签新建编辑 -->
          <el-dialog
            :title="newTagTitle"
            :visible.sync="newTagVisible"
            :before-close="newTagClose"
            :close-on-click-modal="false"
            append-to-body
            class="tag-add-box"
            width="400px">
            <div class="content">
              <!-- 基础设置 -->
              <div
                class="infrastructure">
                <div class="row">
                  <span class="label name">标签名称</span>
                  <div class="color-dynamic">
                    <el-input
                      v-model="tag.name"
                      :maxlength="50"
                      size="mini" />
                    <span
                      :style="{background: tag.color}"
                      class="dynamic-span" />
                  </div>
                </div>
                <div class="row">
                  <span class="label">标签颜色</span>
                  <div class="color-box">
                    <span
                      v-for="(item, index) in colorList"
                      :key="index"
                      :style="{background: item}"
                      @click="selectColor(item)" />
                  </div>
                </div>
                <div class="row">
                  <span class="label name">所属分组</span>
                  <div class="color-dynamic">
                    <el-select
                      v-model="tag.groupIndex"
                      :disabled="newTagType == 'update'">
                      <el-option
                        v-for="(item,index) in groupList"
                        :key="index"
                        :label="item.name"
                        :value="index" />
                    </el-select>
                  </div>
                </div>
              </div>
            </div>
            <div class="footer">
              <el-button
                type="primary"
                @click.stop="newTagSubmit">确定</el-button>
              <el-button @click.stop="newTagClose">取消</el-button>
            </div>
          </el-dialog>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="handleCloseDialog">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleDialogConfirm">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import draggable from 'vuedraggable'

import { getFieldAuth } from '../../utils'
import { objDeepCopy } from '@/utils'

export default {
  name: 'SettingTag',
  components: {
    draggable
  },
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      groupList: [], // 分组列表
      tagList: [], // 标签列表
      tagFieldList: [
        { name: '标签名称', field: 'name' },
        { name: '颜色', field: 'color' }
      ],
      dialogVisible: false,
      // currentPage: 1,
      // pageSize: 15,
      // pageSizes: [15, 30, 45, 60],
      // total: 0,
      tableHeight: document.documentElement.clientHeight - 600, // 表的高度

      // 新建编辑分组title
      newGroupType: 'create', // create update
      // 新建分组
      newGroupVisible: false,
      group: {}, // 操作分组的框 关联的信息
      // 选中的分组
      groupActive: { },
      groupActiveIndex: 0,
      dropdownHandleGroup: null, // 下拉操作编辑分组  index

      newTagType: 'create', // create update
      newTagVisible: false, // 展示添加标签

      tag: {}, // 操作标签的框
      colorList: [ // 标签颜色列表
        '#0052CC',
        '#00A3BF',
        '#DE350B',
        '#5243AA',
        '#00875A',
        '#FF991F',
        '#091E42'
      ]
    }
  },
  computed: {
    // 选项不能配置，逻辑表单也禁止配置
    editAuth() {
      return getFieldAuth(this.field.operating).defaultEdit
    },
    newTagTitle() {
      return this.newTagType === 'create' ? '新建标签' : '编辑标签'
    },
    newGroupTitle() {
      return this.newGroupType === 'create' ? '新建分组' : '编辑分组'
    }
  },
  watch: {
    field: {
      handler() {
        this.groupList = objDeepCopy(this.field.setting) || []
        if (this.groupList.length > 0) {
          this.groupActive = this.groupList[0]
          this.groupActiveIndex = 0
          this.tagList = this.groupList[0].labelList
        } else {
          this.groupActive = []
          this.groupActiveIndex = 0
          this.tagList = []
        }
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    /** 控制table的高度 */
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 600
    }
  },
  methods: {
    /**
     * 分组列表点击
     */
    groupMenuSelect(val, index) {
      this.groupActive = val
      this.groupActiveIndex = index
      this.refreshTagList()
    },
    /**
     * 左侧拖拽结束，校正右侧数据
     */
    dragEnd() {
      this.groupActive = this.groupList[this.groupActiveIndex]
      this.refreshTagList()
    },
    /**
     * 新建分组
     */
    newGroupClose() {
      this.newGroupVisible = false
    },
    newGroupBtn() {
      this.newGroupType = 'create'
      this.group = {}
      this.newGroupVisible = true
    },
    /**
     * 分组编辑
     */
    groupEditBtn(index) {
      this.newGroupType = 'update'
      this.group = {
        name: this.groupList[index].name
      }
      this.newGroupVisible = true
    },

    /**
     * 分组删除
     */
    groupDelect(index) {
      this.groupList.splice(index, 1)
    },
    /**
     * 分组保存
     */
    newGroupSubmit() {
      if (!this.group.name) {
        this.$message.error('分组名称不能为空')
        return
      }
      if (this.newGroupType == 'update') {
        this.groupList[this.dropdownHandleGroup].name = this.group.name
      } else {
        const obj = { name: this.group.name, labelList: [] }
        if (this.groupList.length == 0) {
          this.groupMenuSelect(obj, 0)
        }
        this.groupList.push(obj)
      }

      this.newGroupVisible = false
    },
    /**
     * 分组操作
     */
    groupDropdownClick(index) {
      this.dropdownHandleGroup = index
    },
    groupHandleClick(command) {
      if (command == 'edit') {
        this.groupEditBtn(this.dropdownHandleGroup)
      } else if (command == 'delete') {
        this.groupDelect(this.dropdownHandleGroup)
      }
    },
    /**
     * 刷新标签列表
     */
    refreshTagList() {
      // this.currentPage = 1
      this.getTagList()
    },
    /**
     * 获取标签列表
     */
    getTagList() {
      this.tagList = this.groupActive.labelList || []
    },
    /**
     * 标签列表操作
     */
    tagHandleClick(type, val, index) {
      if (type === 'delete') {
        this.groupActive.labelList.splice(index, 1)
      } else if (type === 'editTag') {
        this.tag = {
          name: val.name,
          color: val.color,
          index: index,
          groupIndex: this.groupActiveIndex
        }
        this.newTagType = 'update'
        this.newTagVisible = true
      }
    },
    /**
     * 新建标签
     */
    newTagClose() {
      this.newTagVisible = false
    },
    newTagBtn() {
      this.tag = {
        name: '',
        color: '#0052CC',
        groupIndex: this.groupActiveIndex
      }
      this.newTagType = 'create'
      this.newTagVisible = true
    },
    /**
     * 选择颜色
     */
    selectColor(item) {
      this.$set(this.tag, 'color', item)
    },
    /**
     * 标签保存
     */
    newTagSubmit() {
      if (!this.tag.name) {
        this.$message.error('标签名称不能为空')
        return
      }
      if (this.tag.groupIndex === '') {
        this.$message.error('所属分组不能为空')
        return
      }
      if (this.newTagType == 'update') {
        this.$set(this.groupActive.labelList, this.tag.index, { name: this.tag.name, color: this.tag.color })
      } else {
        const groupActive = this.groupList[this.tag.groupIndex]
        groupActive.labelList.push({ name: this.tag.name, color: this.tag.color })
      }
      this.newTagVisible = false
    },

    /**
     * 更改每页展示数量
     */
    // handleSizeChange(val) {
    //   this.pageSize = val
    //   this.refreshTagList()
    // },

    // /**
    //  * 更改当前页数
    //  */
    // handleCurrentChange(val) {
    //   this.currentPage = val
    //   this.getTagList()
    // },

    handleToSet() {
      this.dialogVisible = true
    },

    handleCloseDialog() {
      // 关闭弹窗，触发watch更新
      this.$set(this.field, '_remark', '')
      this.$nextTick(() => {
        delete this.field._remark
      })
      this.dialogVisible = false
    },
    /**
    * 确定保存
    */
    handleDialogConfirm() {
      this.$set(this.field, 'setting', this.groupList)
      const optionsStr = JSON.stringify(this.groupList)
      this.$set(this.field, 'options', optionsStr)
      this.handleCloseDialog()
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/styles/table.scss";

.add-btn {
  width: 100%;
  height: 34px;
  font-size: 14px;
  color: $--color-text-regular;
  cursor: pointer;
  background-color: #f8f8f8;
  border: 1px dashed $--border-color-base;
  border-radius: $--border-radius-base;
}

.edit-dialog {
  .edit-dialog-body {
    padding: 0 15px;
  }

  ::v-deep .el-dialog__body {
    padding: 0;
  }
}

/* 新建分组 */
.input-group {
  width: 100%;
  padding: 10px 0 20px;
}

.group-box {
  position: relative;
  height: calc(100% - 60px);
  overflow: hidden;

  &.is-tabs {
    .group-nav-box {
      height: calc(100% - 100px);
      padding-top: 0;
    }
  }

  .group-nav-box {
    height: calc(100% - 50px);
    padding: 20px 0;
    overflow-y: auto;
    line-height: 30px;
  }

  // 菜单
  .group-nav-item {
    position: relative;
    height: 40px;
    padding: 0 15px;
    font-size: 13px;
    line-height: 40px;
    cursor: move;

    .name {
      cursor: pointer;
    }

    .handle-icon {
      position: absolute;
      top: 0;
      right: 8px;
      z-index: 1;
      display: none;
      cursor: pointer;
    }
  }

  .group-nav-item:hover,
  .group-nav-item.is-select {
    background-color: $--background-color-base;
  }

  .group-nav-item:hover::before,
  .group-nav-item.is-select::before {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    width: 2px;
    content: " ";
    background-color: #5383ed;
  }

  .group-nav-box .group-nav-item:hover .handle-icon {
    display: block;
    float: right;
  }
}

.nav {
  position: absolute;
  top: 0;
  left: 0;
  width: 280px;
  height: 100%;
  background: #fff;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;
}

.nav__hd {
  position: relative;
  padding: 15px;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid $--border-color-base;
}

.content-box {
  position: relative;
  height: 100%;
  margin-left: 295px;
  overflow: hidden;
  background: #fff;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;
}

.content-table {
  overflow: hidden;
}

.content-table > .xr-btn--orange {
  float: right;
  margin-right: 30px;
  margin-bottom: 15px;
}

.content-table-header {
  padding: 10px 15px;

  .content-table-header-reminder {
    flex: 1;
    margin-right: 5px;
  }
}

.tag-table {
  .tag-row {
    display: flex;
    justify-content: space-around;
    cursor: move;
    border-bottom: 1px solid #dfe1e6;

    &:first-child {
      border-top: 1px solid #dfe1e6;
    }

    .cell {
      padding: 12px 8px;

      & > .el-button {
        padding: 0;
      }
    }
  }
}

/** 分页布局 */
.p-contianer {
  position: relative;
  height: 44px;
  background-color: white;

  .p-bar {
    float: right;
    margin: 5px 10px 0 0 !important;
    font-size: 14px !important;
  }
}

.el-tabs ::v-deep .el-tabs__header {
  padding: 0 17px;
  margin: 0 0 15px !important;
}

.el-tabs ::v-deep .el-tabs__item {
  height: 40px !important;
  font-size: 13px !important;
  line-height: 40px !important;
}

.node-label {
  position: relative;
  font-size: 15px;
  font-weight: bold;

  .el-button {
    position: absolute;
    top: -8px;
    right: -80px;

    ::v-deep span {
      margin-left: 3px;
    }
  }
}

.common-node-label {
  position: relative;

  .el-button {
    position: absolute;
    top: -8px;
    right: -105px;

    ::v-deep span {
      margin-left: 3px;
    }
  }
}

.status-name {
  .main-mark {
    padding: 0 4px;
    margin: 0 3px;
    font-size: 12px;
    color: white;
    background-color: #ff6a00;
    border-radius: 2px;
  }
}

.tag-add-box {
  .content {
    padding-top: 0;

    .infrastructure {
      padding: 10px 20px;

      .row {
        display: flex;
        flex-direction: row;
        margin-bottom: 20px;

        .el-textarea {
          flex: 1;
        }

        .label {
          margin-right: 10px;
          font-size: 13px;
        }

        .color-box {
          span {
            display: inline-block;
            width: 15px;
            height: 15px;
            margin-right: 7px;
            cursor: pointer;
            border-radius: 50%;
          }
        }

        .name {
          line-height: 28px;
        }

        .color-dynamic {
          position: relative;
          flex: 1;

          .dynamic-span {
            position: absolute;
            top: 6px;
            left: 10px;
            display: inline-block;
            width: 15px;
            height: 15px;
            border-radius: 50%;
          }

          .el-input ::v-deep .el-input__inner {
            padding-left: 30px;
          }

          .el-select {
            width: 100%;
          }
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 0;
  }

  .footer {
    padding: 10px;
    text-align: right;
    background-color: #f7f8fa;
    border-top: 1px solid $--border-color-base;
  }
}
</style>
