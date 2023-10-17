<template>
  <div class="setting-detail-table">
    <div class="item-section">
      <div class="name">表格字段</div>
      <draggable
        :list="field.fieldExtendList"
        :options="dragConfig"
        @sort="handleChange">
        <flexbox
          v-for="(item, index) in field.fieldExtendList"
          :key="index"
          align="center"
          justify="flex-start"
          class="option-item">
          <i
            :class="typeObj(item.formType).icon"
            class="type-icon" />
          <div class="option-item__name">{{ item.name }}</div>
          <el-button
            :disabled="!editAuth"
            type="text"
            class="option-item__icon wk wk-write"
            @click="handleEdit(index)" />
          <el-button
            :disabled="!editAuth"
            type="text"
            class="option-item__icon wk wk-icon-bin"
            @click="handleDelete(index)" />
          <div class="option-item__icon drag-hook wk wk-grid" />
        </flexbox>
      </draggable>
    </div>

    <div class="item-section">
      <div class="name">动作名<i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="27"
        data-id="251" /></div>
      <div>
        <el-input v-model="field.remark" :maxlength="10" :disabled="!editAuth" />
      </div>
    </div>

    <div class="item-section">
      <div class="name">
        填写方式<i
          class="wk wk-icon-fill-help wk-help-tips"
          data-type="27"
          data-id="264" />
      </div>
      <el-radio-group v-model="field.precisions" :disabled="!editAuth">
        <el-radio :label="1">列表</el-radio>
        <el-radio :label="2">表格</el-radio>
      </el-radio-group>
    </div>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import { guid } from '@/utils'
import FieldTypeLib from '../../fieldTypeLib'
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingDetailTable',
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

    }
  },
  computed: {
    // 编辑权限
    editAuth() {
      return getFieldAuth(this.field.operating).defaultEdit
    },

    dragConfig() {
      return {
        group: guid(),
        disabled: !this.editAuth,
        forceFallback: false,
        fallbackClass: 'draggingStyle',
        handle: '.drag-hook',
        filter: '.el-input__inner',
        preventOnFilter: false
      }
    }
  },
  watch: {
    field: {
      handler() {
        if (!this.field.precisions) {
          this.$set(this.field, 'precisions', 1)
        }
        this.$set(this.field, 'precisions', this.field.precisions)
        this.$set(this.field, 'remark', this.field.remark)
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    typeObj(formType) {
      return FieldTypeLib.find(o => o.formType === formType)
    },

    handleChange() {
      this.$set(this.field, 'fieldExtendList', this.field.fieldExtendList)
    },

    handleEdit(index) {
      this.$emit('child-edit', this.field.fieldExtendList[index])
    },

    handleDelete(index) {
      this.field.fieldExtendList.splice(index, 1)
      this.$set(this.field, 'fieldExtendList', this.field.fieldExtendList)
    }
  }
}
</script>

<style scoped lang="scss">
.setting-detail-table {
  .item-section {
    padding: 10px 0;
    border-bottom: 1px solid $--border-color-base;

    .name {
      margin: 10px 0;
      font-size: 13px;
      font-weight: 500;
    }
  }

  .option-item {
    width: 100%;
    height: 34px;
    padding: 0 8px;
    margin: 5px 0;
    border: 1px solid $--border-color-base;
    border-radius: 3px;

    .drag-hook {
      cursor: move;
    }

    .type-icon {
      margin-right: 5px;
      font-size: 14px;
      color: #777;
    }

    .option-item__name {
      width: 174px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .option-item__icon {
      margin: 0 2px;
      font-size: 14px;
      color: $--color-text-secondary;
    }
  }
}
</style>
