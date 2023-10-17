<template>
  <div class="new-tag-dialog">
    <flexbox class="tag-dialog-header">
      <div class="tag-dialog-header__text">
        <span
          class="el-icon-arrow-left"
          @click="back" />
        {{ newTagTitle }}
      </div>
      <i
        class="el-icon-close icon-close"
        @click="close" />
    </flexbox>
    <div class="search">
      <el-input
        v-model.trim="input"
        :maxlength="10"
        placeholder="输入标签名，最多十个字符" />
      <span
        :style="{'background': showBgColor}"
        class="checked-color" />
    </div>
    <div class="color-box">
      <span
        v-for="(item, index) in colorList"
        :key="index"
        :style="{'background': item}"
        @click="changeColor(item)" />
    </div>
    <div class="footer">
      <el-button
        v-debounce="tagCreateSubmit"
        type="primary">保存</el-button>
      <el-button
        @click="tagCancel">取消</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    newTagTitle: String,
    newTagInput: String,
    bgColorProps: String
  },
  data() {
    return {
      colorList: [
        '#0052CC',
        '#00A3BF',
        '#DE350B',
        '#5243AA',
        '#00875A',
        '#FF991F',
        '#091E42'
      ],
      input: this.newTagInput,
      showBgColor: ''
    }
  },
  watch: {
    bgColorProps: function(color) {
      this.showBgColor = color || '#0052CC'
    }
  },
  mounted() {
    this.showBgColor = this.bgColorProps || '#0052CC'
  },
  methods: {
    back() {
      this.$emit('back')
    },
    close() {
      this.$emit('close')
    },
    // 点击变色
    changeColor(val) {
      this.$emit('changeColor', val)
    },
    // 创建按钮
    tagCreateSubmit() {
      if (!this.input) {
        this.$message.error('请输入标签名')
        return
      }
      this.$emit('tagCreateSubmit', this.input, this.showBgColor)
    },
    // 取消按钮
    tagCancel() {
      this.$emit('tagCancel')
    }
  }
}
</script>

<style scoped lang="scss">
.new-tag-dialog {
  padding-top: 10px;
  overflow: hidden;
  background: #fff;
  border-radius: $--border-radius-base;

  .tag-dialog-header {
    position: relative;
    height: 40px;
    padding: 0 16px;

    .tag-dialog-header__text {
      font-size: 16px;
      color: $--color-text-primary;
    }

    .icon-close {
      position: absolute;
      top: 50%;
      right: 16px;
      font-size: 20px;
      color: $--color-text-secondary;
      cursor: pointer;
      transform: translateY(-50%);
    }
  }

  .search {
    position: relative;
    padding: 0 16px;
    margin-top: 16px;

    .el-input ::v-deep .el-input__inner {
      padding-left: 40px;
    }

    .checked-color {
      position: absolute;
      top: 0;
      left: 5%;
      display: inline-block;
      width: 20px;
      height: 20px;
      margin: 6px 10px;
      border-radius: 50%;
    }
  }

  .footer {
    padding: 0 16px;
    line-height: 50px;
    text-align: right;
  }

  .color-box {
    padding: 16px 56px;

    span {
      display: inline-block;
      width: 20px;
      height: 20px;
      margin-right: 5px;
      cursor: pointer;
      border-radius: 50%;
    }
  }
}
</style>
