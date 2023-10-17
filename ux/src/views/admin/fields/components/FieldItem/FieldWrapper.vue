<template>
  <div
    :style="{width: fieldWidth}"
    :class="{activate: activate}"
    class="field-item"
    @click.stop="emitClick">
    <div v-if="!hiddenTitle" class="field-item_title">
      <span v-if="showRequired" class="required">{{ field.isNull ? '*' : '' }}</span>
      <span>{{ field.name }}</span>
      <span
        v-if="field.inputTips"
        class="input-tips">
        （{{ field.inputTips }}）
      </span>
    </div>

    <div class="field-item_body">
      <slot />
    </div>

    <template v-if="activate">
      <div
        v-if="controlFlag.top"
        class="control-top control-btn"
        @click.stop="handleControl('top', $event)">
        <i class="wk wk-icon-top" />
      </div>
      <div
        v-if="controlFlag.bottom"
        class="control-bottom control-btn"
        @click.stop="handleControl('bottom', $event)">
        <i class="wk wk-icon-top bottom" />
      </div>
      <div
        v-if="controlFlag.left"
        class="control-left control-btn"
        @click.stop="handleControl('left', $event)">
        <i class="wk wk-transfer" />
      </div>
      <div
        v-if="controlFlag.right"
        class="control-right control-btn"
        @click.stop="handleControl('right', $event)">
        <i class="wk wk-transfer" />
      </div>
      <div class="edit-box">
        <div
          v-if="controlFlag.copy"
          class="control-copy control-btn"
          @click.stop="handleControl('copy', $event)">
          <el-tooltip effect="dark" content="复制" placement="bottom">
            <i class="wk wk-associated" />
          </el-tooltip>
        </div>
        <div
          v-if="controlFlag.delete"
          class="control-delete control-btn"
          @click.stop="handleControl('delete', $event)">
          <i class="wk wk-s-delete" />
        </div>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'FieldWrapper',
  props: {
    field: { // 字段信息
      type: Object,
      required: true
    },
    activate: { // 当前字段是否已激活
      type: Boolean,
      default: false
    },
    controlFlag: { // 字段控制按钮状态
      type: Object,
      default: () => {
        return {
          top: false,
          bottom: false,
          left: false,
          right: false,
          delete: false,
          copy: true
        }
      }
    },
    hiddenTitle: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    fieldWidth() {
      if (!this.field) return '100%'
      return this.field.stylePercent + '%'
    },
    // 自定义编号，不展示必填
    showRequired() {
      return this.field.formType !== 'serial_number'
    }
  },
  watch: {
    field: {
      handler() {
        if (this.field && !this.field.stylePercent) {
          this.field.stylePercent = 100
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    emitClick(evt) {
      this.$emit('click', evt)
    },
    handleControl(action, evt) {
      this.$emit('action', action, evt)
    }
  }
}
</script>

<style scoped lang="scss">
.field-item {
  position: relative;
  padding: 16px;
  cursor: move;
  background-color: white;

  &.activate {
    background-color: $--color-b50;
    border-radius: $--border-radius-base;
  }

  .field-item_title {
    width: 100%;
    min-height: 29px;
    padding-bottom: 4px;
    font-size: 14px;
    line-height: 1.5;
    color: $--color-text-secondary;
    word-break: break-all;
    word-wrap: break-word;

    .required {
      color: $--color-r300;
    }

    .input-tips {
      color: $--color-text-placeholder;
    }
  }

  .field-item_body {}

  .control-btn {
    position: absolute;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 25px;
    height: 25px;
    cursor: pointer;
    background-color: white;
    border-radius: 50%;
    box-shadow: 0 2px 4px 0 rgba(163, 163, 163, 0.5);

    &:hover {
      background-color: $--color-primary;

      .wk {
        color: $--color-white;
      }
    }

    .wk {
      font-size: 14px;
    }

    .wk-icon-top {
      font-size: 12px;
      font-weight: bold;

      &.bottom {
        transform: rotate(180deg);
      }
    }

    &.control-top {
      top: -14px;
      left: 50%;
      transform: translateX(-50%);
    }

    &.control-bottom {
      bottom: -14px;
      left: 50%;
      transform: translateX(-50%);
    }

    &.control-left {
      top: 50%;
      left: -14px;
      transform: translateY(-50%);
    }

    &.control-right {
      top: 50%;
      right: -14px;
      transform: translateY(-50%);
    }
  }

  .edit-box {
    position: absolute;
    right: 5%;
    bottom: -14px;
    z-index: 1;

    .control-btn {
      position: unset;
      display: inline-flex;
      margin: 0 2px;
      vertical-align: middle;
    }
  }
}

</style>
