<template>
  <el-dialog
    :visible.sync="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="700px"
    @close="handleCancel">
    <span slot="title" class="el-dialog__title">场景管理<i v-if="config.help" class="wk wk-icon-fill-help wk-help-tips" :data-type="config.help.type" :data-id="config.help.id" /></span>
    <div class="scene-name">您可通过拖拽管理场景</div>
    <flexbox class="scene-list">
      <div class="scene-list-box">
        <flexbox class="scene-list-head">
          <el-checkbox
            v-model="checkleftAll"
            :indeterminate="isleftIndeterminate"
            @change="handleleftCheckAllChange" />
          <div class="scene-list-head-name">显示的场景</div>
          <div class="scene-list-head-detail">{{ leftCheckItems.length + '/' + checkedLeftData.length }}</div>
        </flexbox>
        <div class="scene-list-body">
          <draggable
            v-model="checkedLeftData"
            :move="leftMove"
            :options="{group: 'list',forceFallback:false, fallbackClass:'draggingStyle'}"
            style="min-height: 100px;"
            @end="leftMoveEnd">
            <flexbox
              v-for="(item, index) in checkedLeftData"
              :key="index"
              class="list-item">
              <div
                :class="{'default-mark-active': item.sceneId == defaultId}"
                class="default-mark" />
              <el-checkbox
                v-model="item.check"
                class="list-item-check"
                @change="leftCheckItemChange" />
              <div class="list-item-name">{{ item.name }}</div>
              <div class="list-item-handle">
                <i
                  v-if="item.isSystem != 1"
                  class="el-icon-edit"
                  @click="itemHandle('edit', item, index)" />
                <i
                  v-if="item.isSystem != 1"
                  class="el-icon-delete"
                  @click="itemHandle('delete', item, index)" />
                <el-dropdown
                  v-if="item.sceneId != defaultId"
                  v-model="item.visible"
                  @command="defaultHandle(arguments[0], item)">
                  <el-button
                    class="dropdown-btn menu-edit-btn"
                    icon="wk wk-manage"
                    size="small" />
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="default">设置为默认场景</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </flexbox>
          </draggable>

        </div>
      </div>
      <div class="scene-middle-list">
        <el-button
          :class="{'scene-middle-button-select':rightCheckItems.length > 0}"
          :disabled="rightCheckItems.length == 0"
          class="scene-middle-left-button"
          @click="changePositon('left')">
          <i class="el-icon-arrow-left scene-middle-icon" />
        </el-button>
        <el-button
          :class="{'scene-middle-button-select':leftCheckItems.length > 0}"
          :disabled="leftCheckItems.length == 0"
          class="scene-middle-right-button"
          @click="changePositon('right')">
          <i class="el-icon-arrow-right scene-middle-icon" />
        </el-button>
      </div>
      <div class="scene-list-box">
        <flexbox class="scene-list-head">
          <el-checkbox
            v-model="checkrightAll"
            :indeterminate="isrightIndeterminate"
            @change="handlerightCheckAllChange" />
          <div class="scene-list-head-name">隐藏的场景</div>
          <div class="scene-list-head-detail">{{ rightCheckItems.length + '/' + checkedRightData.length }}</div>
        </flexbox>
        <div class="scene-list-body">
          <draggable
            v-model="checkedRightData"
            :move="rightMove"
            :options="{group: 'list',forceFallback:false, fallbackClass:'draggingStyle'}"
            style="min-height: 100px;"
            @end="rightMoveEnd">
            <flexbox
              v-for="(item, index) in checkedRightData"
              :key="index"
              class="list-item">
              <el-checkbox
                v-model="item.check"
                style="margin-left: 9px;"
                class="list-item-check"
                @change="rightCheckItemChange" />
              <div class="list-item-name">{{ item.name }}</div>
            </flexbox>
          </draggable>
        </div>
      </div>
    </flexbox>

    <div class="handle-bar">
      <el-button
        type="text"
        style="padding-right: 0;padding-left: 0;"
        @click="addAndEditScene('add',{})">+ 新建场景</el-button>
    </div>

    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleCancel">取消</el-button>
    </div>
    <scene-create
      :field-list="fieldList"
      :props="createProps"
      :filter-fields-props="filterFieldsProps"
      :condition-type-fun="conditionTypeFun"
      :dialog-visible.sync="showCreateScene"
      :obj="filterObj"
      :name="filterName"
      :edit-id="filterEditId"
      :is-default="filterDefault"
      @save-success="refreshSceneList" />
  </el-dialog>
</template>

<script type="text/javascript">
import draggable from 'vuedraggable'
import SceneCreate from './SceneCreate' // 新建编辑场景
import merge from '@/utils/merge'

const DefaultSceneSet = {
  // 场景列表请求
  indexRequest: null,
  indexParams: null,
  // 排序请求
  sortRequest: null,
  sortParams: null,
  // 默认请求
  defaultsRequest: null,
  defaultsParams: null,
  // 删除请求
  deleteRequest: null,
  deleteParams: null,
  // 字段列表请求
  fieldsIndexRequest: null,
  fieldsIndexParams: null,
  // 帮助提示
  help: null
}

export default {
  name: 'SceneSet', // 场景 设置
  components: {
    draggable,
    SceneCreate
  },
  props: {
    dialogVisible: {
      type: Boolean,
      required: true,
      default: false
    },
    // // 没有值就是全部类型 有值就是当个类型
    // crmType: {
    //   type: String,
    //   default: ''
    // },
    // 组件配置属性包含 DefaultSceneSet 里的配置项
    props: Object,
    // 组件配置属性包含 DefaultSceneCreate 里的配置项
    createProps: Object,
    filterFieldsProps: Object, // 筛选的配置
    conditionTypeFun: Function // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
  },
  data() {
    return {
      defaultId: '', // 默认场景id
      visible: false, // 控制展示
      isleftIndeterminate: false, // 标注头部是多选框效果
      checkleftAll: false, // 关联全选操作多选框

      checkedLeftData: [], // 数据源
      leftCheckItems: [], // 选择的数据源

      isrightIndeterminate: false,
      checkrightAll: false,

      checkedRightData: [],
      rightCheckItems: [],

      moveItem: {}, // 移动中的item
      handlDefaultItem: {}, // 设置默认的中间item

      /** 添加 编辑 场景 */
      showCreateScene: false, // 展示场景添加
      fieldList: [],
      filterObj: { form: [] }, // 筛选确定数据
      filterName: '',
      filterDefault: false,
      filterEditId: '' // 编辑id
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultSceneSet }, this.props || {})
    }

    // // 帮助信息
    // helpObj() {
    //   return {
    //     leads: {
    //       type: '7',
    //       id: '50'
    //     }, customer: {
    //       type: '8',
    //       id: '51'
    //     }, contacts: {
    //       type: '9',
    //       id: '52'
    //     }, business: {
    //       type: '10',
    //       id: '53'
    //     }, contract: {
    //       type: '11',
    //       id: '54'
    //     }, receivables: {
    //       type: '12',
    //       id: '55'
    //     }, invoice: {
    //       type: '13',
    //       id: '56'
    //     }, visit: {
    //       type: '14',
    //       id: '57'
    //     }, product: {
    //       type: '15',
    //       id: '58'
    //     }
    //   }[this.crmType] || null
    // }
  },
  watch: {
    dialogVisible: {
      handler(val) {
        this.visible = val
        if (val) {
          this.getSceneList()
        }
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * @description: 成功后的刷新
     * @param {*}
     * @return {*}
     */
    refreshSceneList() {
      this.getSceneList()
      this.$emit('save-success')
    },

    /**
     * @description: 获取配置列表
     * @param {*}
     * @return {*}
     */
    getSceneList() {
      this.config.indexRequest({
        ...this.config.indexParams
      })
        .then(res => {
          const resData = res.data || {}
          this.checkedLeftData = resData.value.map(item => {
            item.check = false
            item.data = !item.data ? [] : JSON.parse(item.data)
            return item
          })
          this.checkedRightData = resData.hideValue.map(item => {
            item.check = false
            item.data = !item.data ? [] : JSON.parse(item.data)
            return item
          })
          var defaultScene = this.checkedLeftData.filter(item => {
            return item.isDefault == 1
          })

          // 如果有默认场景
          if (defaultScene && defaultScene.length > 0) {
            this.defaultId = defaultScene[0].sceneId
          } else {
            this.defaultId = ''
          }
        })
        .catch(() => {})
    },

    /**
     * 确定选择
     * hasMessage 默认有提示
     */
    handleConfirm(hasMessage = true) {
      // 没有场景提示
      if (this.checkedLeftData.length === 0) {
        this.$message.error('场景不能全部隐藏！')
        return
      }
      // 如果选择默认的 不包含在左侧列表 重置为没有默认
      var leftTemps = this.checkedLeftData.filter(item => {
        return item.sceneId == this.defaultId
      })
      if (leftTemps.length == 0) {
        this.defaultId = ''
      }
      this.config.sortRequest({
        // type: crmTypeModel[this.crmType],
        noHideIds: this.checkedLeftData
          .map(item => item.sceneId),
        hideIds: this.checkedRightData
          .map(item => item.sceneId),
        ...this.config.sortParams
      })
        .then(res => {
          if (hasMessage !== false) {
            this.$message({
              type: 'success',
              message: '操作成功'
            })
            this.handleCancel()
          }
          this.$emit('save-success')
        })
        .catch(() => {})
    },

    /**
     * 事项操作
     */
    itemHandle(type, item, index) {
      if (item.isTemp) {
        this.$message.error('请保存后操作')
        return
      }
      if (type == 'edit') {
        this.addAndEditScene('edit', item)
      } else if (type == 'delete') {
        this.$confirm('您确定要删除这一条数据吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.config.deleteRequest({
              sceneId: item.sceneId
            })
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '删除成功'
                })
                this.checkedLeftData.splice(index, 1)
                this.leftCheckItemChange()
                this.$emit('save-success')
              })
              .catch(() => {})
          })
          .catch(() => {})
      }
    },

    /**
     * 操作默认
     */
    defaultHandle(_, item) {
      this.config.defaultsRequest({
        sceneId: item.sceneId
      })
        .then(res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.defaultId = item.sceneId

          // 保存一下当前列
          this.handleConfirm(false)
        })
        .catch(() => {})
    },

    /**
     * 添加编辑场景
     */
    addAndEditScene(type, data) {
      this.config.fieldsIndexRequest({
        ...this.config.fieldsIndexParams
      })
        .then(res => {
          const resData = res.data || []
          this.fieldList = resData
          const validFieldNames = resData.map(item => item.fieldName)
          if (type == 'edit') {
            // 如果包含阶段，加入settingName的支持
            if (this.fieldList.find(item => item.formType === 'business_cause' && item.fieldName === 'flowName')) {
              validFieldNames.push('settingName')
            }
            this.filterObj = { form: [], obj: data.data.filter(item => validFieldNames.includes(item.name)) }
            this.filterName = data.name
            this.filterDefault = data.isDefault == 1
            this.filterEditId = data.sceneId.toString()
          } else {
            this.filterObj = { form: [] }
            this.filterEditId = ''
            this.filterName = ''
            this.filterDefault = false
          }
          this.showCreateScene = true
        })
        .catch(() => {})
    },

    /**
     * 取消选择
     */
    handleCancel() {
      this.visible = false
      this.$emit('update:dialogVisible', false)
    },

    /**
     * 拖拽操作
     */
    rightMoveEnd(evt) {
      this.moveItem.check = false
      this.leftCheckItemChange()
      this.rightCheckItemChange()
    },
    rightMove(obj) {
      this.moveItem = obj.draggedContext.element
    },
    leftMoveEnd(evt) {
      this.moveItem.check = false
      this.leftCheckItemChange()
      this.rightCheckItemChange()
    },
    leftMove(obj) {
      this.moveItem = obj.draggedContext.element
    },
    // 选择全部
    handleleftCheckAllChange(value) {
      if (value) {
        this.isleftIndeterminate = false
      }
      this.checkedLeftData.forEach(item => {
        item.check = value
      })

      this.leftCheckItems = value ? this.checkedLeftData.filter(item => {
        return item.check
      }) : []
    },
    leftCheckItemChange() {
      this.leftCheckItems = this.checkedLeftData.filter(item => {
        return item.check == true
      })
      if (this.leftCheckItems.length > 0) {
        if (this.leftCheckItems.length == this.checkedLeftData.length) {
          this.checkleftAll = true
          this.isleftIndeterminate = false
        } else {
          this.checkleftAll = false
          this.isleftIndeterminate = true
        }
      } else {
        this.checkleftAll = false
        this.isleftIndeterminate = false
      }
    },
    // 选择全部
    handlerightCheckAllChange(value) {
      if (value) {
        this.isrightIndeterminate = false
      }
      this.checkedRightData.forEach(item => {
        item.check = value
      })
      this.rightCheckItems = value ? this.checkedRightData : []
    },
    rightCheckItemChange() {
      this.rightCheckItems = this.checkedRightData.filter(item => {
        return item.check == true
      })
      if (this.rightCheckItems.length > 0) {
        if (this.rightCheckItems.length == this.checkedRightData.length) {
          this.checkrightAll = true
          this.isrightIndeterminate = false
        } else {
          this.checkrightAll = false
          this.isrightIndeterminate = true
        }
      } else {
        this.checkrightAll = false
        this.isrightIndeterminate = false
      }
    },
    // 按钮操作
    changePositon(type) {
      var self = this
      // 从右往左
      if (type == 'left') {
        this.checkedRightData = this.checkedRightData.filter(function(
          item,
          index,
          array
        ) {
          var remove = false
          self.rightCheckItems.forEach(function(element, index) {
            if (item.sceneId == element.sceneId) {
              remove = true
            }
          })
          return !remove
        })

        this.rightCheckItems.forEach(function(element, index) {
          element.check = false
          element.isTemp = true // 没有保存的时候  到左侧后不允许编辑
          self.checkedLeftData.push(element)
        })

        this.rightCheckItems = []
        this.isrightIndeterminate = false
        this.checkrightAll = false

        // 刷新左侧效果
        this.leftCheckItemChange()
        this.rightCheckItemChange()
      } else {
        this.checkedLeftData = this.checkedLeftData.filter(function(
          item,
          index,
          array
        ) {
          var remove = false
          self.leftCheckItems.forEach(function(element, index) {
            if (item.sceneId == element.sceneId) {
              remove = true
            }
          })
          return !remove
        })

        this.leftCheckItems.forEach(function(element, index) {
          element.check = false
          self.checkedRightData.push(element)
        })

        this.leftCheckItems = []
        this.isleftIndeterminate = false
        this.checkleftAll = false

        // 刷新右侧效果
        this.leftCheckItemChange()
        this.rightCheckItemChange()
      }
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.scene-name {
  padding-bottom: 5px;
  color: $--color-text-secondary;
}

.default-mark {
  width: 4px;
  height: 4px;
  margin-right: 5px;
  border-radius: 2px;
}

.default-mark-active {
  background-color: $--color-primary;
}

.scene-list {
  user-select: none;

  .scene-list-box {
    position: relative;
    flex: 1;
    height: 300px;
    padding: 15px;
    border: 1px solid $--border-color-base;
    border-radius: 2px;
  }

  .scene-middle-list {
    width: 50px;
    text-align: center;

    button {
      width: 34px;
      height: 34px;
      background-color: $--color-n20;
      border: 1px solid $--border-color-base;
      border-radius: 17px;
    }

    .scene-middle-icon {
      color: $--border-color-base;
    }

    .scene-middle-left-button {
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }

    .scene-middle-right-button {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      margin-top: 15px;
      margin-left: 0;
    }

    .scene-middle-button-select {
      background-color: $--color-primary !important;
      border: 1px solid $--color-primary !important;

      .scene-middle-icon {
        color: white !important;
      }
    }
  }

  .scene-list-head {
    padding-bottom: 15px;
    padding-left: 9px;
    border-bottom: 1px dashed $--border-color-base;

    .scene-list-head-name {
      flex: 1;
      margin: 0 8px;
    }

    .scene-list-head-detail {
      color: $--color-text-secondary;
    }
  }

  .scene-list-body {
    position: relative;
    height: 240px;
    padding: 5px 0;
    overflow-y: auto;
  }

  .list-item {
    height: 26px;
    padding: 5px 0;
    cursor: pointer;
    background-color: white;

    .list-item-check {
      margin-right: 8px;
    }

    .list-item-name {
      flex: 1;
    }

    .list-item-handle {
      display: none;
      flex-shrink: 0;

      i {
        margin-right: 5px;
        color: $--color-text-secondary;
      }

      i:hover {
        color: $--color-primary;
      }
    }
  }

  .list-item:hover {
    .list-item-handle {
      display: block;
    }
  }
}

.handle-bar {
}
</style>
