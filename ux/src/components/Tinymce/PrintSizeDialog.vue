<template>
  <el-dialog
    ref="wkDialog"
    title="纸张尺寸"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="520px"
    @close="close">
    <el-form
      ref="fieldForm"
      :model="pageData"
      :validate-on-rule-change="false"
      label-position="top">
      <el-form-item
        class="wk-form-item"
        label="纸张大小"
        prop="sizeName">
        <el-select
          v-model="pageData.sizeName"
          @change="sizeChange">
          <el-option
            v-for="option in sizeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value" />
        </el-select>
      </el-form-item>
      <flexbox>
        <el-form-item
          class="wk-form-item is-left"
          label="宽"
          prop="width">
          <el-input-number
            v-model="pageData.width"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="99999"
            :disabled="pageData.sizeName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
        <el-form-item
          class="wk-form-item is-left"
          label="高"
          prop="height">
          <el-input-number
            v-model="pageData.height"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="99999"
            :disabled="pageData.sizeName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
      </flexbox>

      <el-form-item
        class="wk-form-item"
        label="页边距"
        prop="marginName">
        <el-select
          v-model="pageData.marginName"
          @change="marginChange">
          <el-option
            v-for="option in marginOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value" />
        </el-select>
      </el-form-item>
      <flexbox>
        <el-form-item
          class="wk-form-item is-left"
          label="上"
          prop="top">
          <el-input-number
            v-model="pageData.top"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="550"
            :disabled="pageData.marginName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
        <el-form-item
          class="wk-form-item is-left"
          label="下"
          prop="bottom">
          <el-input-number
            v-model="pageData.bottom"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="550"
            :disabled="pageData.marginName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
      </flexbox>
      <flexbox>
        <el-form-item
          class="wk-form-item is-left"
          label="左"
          prop="left">
          <el-input-number
            v-model="pageData.left"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="550"
            :disabled="pageData.marginName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
        <el-form-item
          class="wk-form-item is-left"
          label="右"
          prop="right">
          <el-input-number
            v-model="pageData.right"
            controls-position="right"
            :step="0.1"
            :precision="1"
            :min="1"
            :max="550"
            :disabled="pageData.marginName !== 'custom'" /><span class="is-unit">mm</span>
        </el-form-item>
      </flexbox>

    </el-form>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="sureClick"
        type="primary">保存</el-button>
      <el-button @click.native="close">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 打印尺寸设置
  name: 'PrintSizeDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    defaultData: Object, // 默认值
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      pageData: {
        sizeName: '210,297',
        width: 210,
        height: 297,
        marginName: '25.4,25.4,31.8,31.8', //  normal small medium loose
        top: 25.4,
        bottom: 25.4,
        left: 31.8,
        right: 31.8
      },
      // 纸张大小
      sizeOptions: [{
        value: 'custom',
        label: '自定义'
      }, {
        value: '210,297',
        label: 'A4'
      }, {
        value: '297,420',
        label: 'A3'
      }, {
        value: '148,210',
        label: 'A5'
      }, {
        value: '176,250',
        label: 'B5'
      }],
      // 页边距大小
      marginOptions: [{
        value: 'custom',
        label: '自定义'
      }, {
        value: '25.4,25.4,31.8,31.8',
        label: '常规'
      }, {
        value: '12.7,12.7,12.7,12.7',
        label: '窄'
      }, {
        value: '25.4,25.4,19.1,19.1',
        label: '中等'
      }, {
        value: '25.4,25.4,50.8,50.8',
        label: '宽'
      }]
    }
  },
  computed: {
  },
  watch: {
    visible(value) {
      if (value) {
        this.initInfo()
      }
    }
  },
  created() {
    if (this.defaultData) {
      this.pageData = {
        sizeName: `${this.defaultData.width},${this.defaultData.height}`,
        width: this.defaultData.width,
        height: this.defaultData.height,
        marginName: `${this.defaultData.top},${this.defaultData.bottom},${this.defaultData.left},${this.defaultData.right}`, //  normal small medium loose
        top: this.defaultData.top,
        bottom: this.defaultData.bottom,
        left: this.defaultData.left,
        right: this.defaultData.right
      }
    }
  },

  beforeDestroy() {},
  methods: {
    /**
     * @description: 尺寸修改
     * @return {*}
     */
    sizeChange() {
      if (this.pageData.sizeName !== 'custom') {
        const sizes = this.pageData.sizeName.split(',')
        this.pageData.width = parseFloat(sizes[0])
        this.pageData.height = parseFloat(sizes[1])
      }
    },

    /**
     * @description: 页边距修改
     * @return {*}
     */
    marginChange() {
      if (this.pageData.marginName !== 'custom') {
        const sizes = this.pageData.marginName.split(',')
        this.pageData.top = parseFloat(sizes[0])
        this.pageData.bottom = parseFloat(sizes[1])
        this.pageData.left = parseFloat(sizes[2])
        this.pageData.right = parseFloat(sizes[3])
      }
    },

    /**
     * @description: 取消选择
     * @return {*}
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * @description: 确定
     * @return {*}
     */
    sureClick() {
      // 2.54 cm = 96 px
      this.$emit('confirm', {
        top: this.pageData.top,
        bottom: this.pageData.bottom,
        left: this.pageData.left,
        right: this.pageData.right,
        width: this.pageData.width,
        height: this.pageData.height
      })
      this.close()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/wk-form.scss";

::v-deep.is-left {
  display: flex;
  align-items: center;

  .el-form-item__label {
    margin-right: 8px;
  }

  .is-unit {
    margin-left: 4px;
  }
}

.is-left + .is-left {
  margin-left: 16px;
}
</style>

