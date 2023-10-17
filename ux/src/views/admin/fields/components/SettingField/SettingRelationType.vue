<template>
  <div class="setting-logic-form">
    <el-button
      class="add-btn"
      @click="handleToSet">
      点击配置
    </el-button>

    <el-dialog
      v-loading="loading"
      :visible.sync="dialogVisible"
      :before-close="handleCloseDialog"
      :close-on-click-modal="false"
      append-to-body
      title="配置关系类型"
      width="500px"
      class="edit-dialog">
      <div>
        <div class="content-body">
          <div class="product-setting-con">
            <div class="tips">在客户关系网中，包含上下游和上下级关系。</div>
            <div>
              <el-button
                type="primary-text"
                style="padding-left: 0;"
                @click.native="handleTreeSetDrop({type:'create-one'})">+ 添加关系</el-button>
            </div>
            <div class="tree-box">
              <el-tree
                :data="treeData"
                :props="defaultProps"
                default-expand-all>
                <flexbox
                  slot-scope="{ node, data }"
                  class="node-data">
                  <img
                    v-if="node.expanded"
                    class="node-img"
                    src="@/assets/img/fold.png">
                  <img
                    v-if="!node.expanded"
                    class="node-img"
                    src="@/assets/img/unfold.png">
                  <div class="node-label">{{ node.label }}</div>
                  <el-dropdown
                    trigger="click"
                    placement="right-start"
                    @command="handleTreeSetDrop">
                    <div
                      class="node-label-set"
                      @click.stop="getChild(node)">设置</div>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        v-for="(item, index) in treeSetTypes"
                        :key="index"
                        :command="item">{{ item.name }}</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </flexbox>
              </el-tree>
            </div>
          </div>
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

    <el-dialog
      :visible.sync="relationTypeDialog"
      :close-on-click-modal="false"
      :title="relationTitle"
      width="400px">
      <el-form :model="relationForm">
        <el-form-item
          v-if="handleType != 'edit'"
          class="wk-form-item"
          label="关系类型"
          label-width="80">
          <el-select
            v-model="relationForm.type"
            :disabled="handleType == 'create-child'"

            style="width: 100%;">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item
          class="wk-form-item"
          label="关系名称"
          label-width="80">
          <el-input
            v-model="relationForm.name"
            :maxlength="20"
            autocomplete="off" />
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer">
        <el-button
          type="primary"
          @click="handleRelation">确定</el-button>
        <el-button @click="relationTypeDialog = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  crmCustomerRelationsConfigQueryTreeListAPI,
  crmCustomerRelationsConfigSaveAPI,
  crmCustomerRelationsConfigUpdateAPI,
  crmCustomerRelationsConfigDeleteAPI
} from '@/api/admin/crm'

export default {
  name: 'SettingRelationType',
  props: {
  },
  data() {
    return {
      list: [],
      loading: false,
      dialogVisible: false,
      relationTypeDialog: false,

      // 关系类型设置
      treeData: [],
      /** 更多操作 */
      treeSetTypes: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },

      relationTitle: '',
      handleType: '',
      relationForm: {
        type: '',
        name: '',
        id: ''
      },
      typeOptions: []
    }
  },
  computed: {
  },
  watch: {
  },
  mounted() {},
  methods: {
    handleToSet() {
      this.getCustomerRelationsList()
      this.dialogVisible = true
    },

    handleCloseDialog() {
      this.dialogVisible = false
    },

    handleDialogConfirm() {
      this.dialogVisible = false
    },

    /**
     * 关系类别设置
     */
    getChild(node) {
      var temps = []
      if (node.level == 1) {
        temps = [
          { type: 'create-child', name: '新建关系类型' },
          { type: 'edit', name: '编辑关系类型' }
        ]
      } else {
        temps = [
          { type: 'edit', name: '编辑关系类型' },
          { type: 'delete', name: '删除关系类型' }
        ]
      }
      this.treeSetTypes = temps.map(function(item, index, array) {
        item['node'] = node
        return item
      })
    },

    /**
     * 关系操作
     */
    handleTreeSetDrop(command) {
      if (command.type == 'create-one') {
        this.relationTitle = '新建关系类型'
        this.handleType = 'create-one'
        this.relationForm.type = this.typeOptions[0]?.value
        this.relationForm.name = ''
        this.relationTypeDialog = true
      }
      if (command.type == 'create-child') {
        this.relationTitle = '新建关系类型'
        this.handleType = 'create-child'
        this.relationForm.id = command.node.data.id
        this.relationForm.type = command.node.data.id
        this.relationForm.name = ''
        this.relationTypeDialog = true
      } else if (command.type == 'edit') {
        this.relationTitle = '编辑关系类型'
        this.handleType = 'edit'
        this.relationForm.id = command.node.data.id
        this.relationForm.type = command.node.data.parentId
        this.relationForm.name = command.node.data.name
        this.relationTypeDialog = true
      } else if (command.type == 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            crmCustomerRelationsConfigDeleteAPI({
              id: command.node.data.id
            })
              .then(res => {
                this.getCustomerRelationsList()
                this.$message.success('删除成功')
                this.loading = false
              })
              .catch(() => {
                this.loading = false
              })
          })
          .catch(() => {
          })
      }
    },

    /**
     * 关系配置操作
     */
    handleRelation() {
      if (this.relationForm.name.length == 0) {
        this.$message({
          message: '请填写名称',
          type: 'warning'
        })
        return
      }
      this.relationTypeDialog = false
      if (this.handleType == 'create-one') {
        this.loading = true
        crmCustomerRelationsConfigSaveAPI({
          parentId: this.relationForm.type,
          name: this.relationForm.name
        })
          .then(res => {
            this.getCustomerRelationsList()
            this.$message.success('新建成功')
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      } else if (this.handleType == 'create-child') {
        this.loading = true
        crmCustomerRelationsConfigSaveAPI({
          parentId: this.relationForm.type,
          name: this.relationForm.name
        })
          .then(res => {
            this.getCustomerRelationsList()
            this.$message.success('新建成功')
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      } else if (this.handleType == 'edit') {
        this.loading = true
        crmCustomerRelationsConfigUpdateAPI({
          id: this.relationForm.id,
          name: this.relationForm.name
        })
          .then(res => {
            this.getCustomerRelationsList()
            this.$message.success('编辑成功')
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    /**
     * 获取产品分类数据
     */
    getCustomerRelationsList() {
      crmCustomerRelationsConfigQueryTreeListAPI()
        .then(res => {
          this.treeData = res.data
          this.typeOptions = res.data?.map(item => {
            return {
              label: item.name,
              value: item.id
            }
          }) || []
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/styles/wk-form.scss";

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

.content-body {
  height: calc(100% - 57px);
  overflow-y: auto;
}

.edit-dialog {
  .tree-box {
    overflow: auto;
  }

  .tree-box ::v-deep .el-tree-node__expand-icon {
    display: none;
  }

  .tree-box ::v-deep .el-tree-node__content {
    margin-bottom: 10px;
  }

  .node-data {
    .node-img {
      display: block;
      width: 15px;
      height: 15px;
      margin-right: 8px;
    }

    .node-label:hover {
      background-color: #ededed;
    }

    .node-label-set {
      display: none;
      margin-left: 8px;
    }
  }

  .node-data:hover .node-label-set {
    display: block;
  }

  // .product-setting-con {
  //   position: absolute;
  //   top: 40px;
  //   right: 0;
  //   bottom: 0;
  //   left: 0;
  //   overflow: auto;
  // }

  .tips {
    margin-bottom: 5px;
    color: $--color-text-secondary;
  }
}
</style>
