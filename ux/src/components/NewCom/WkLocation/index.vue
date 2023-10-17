<template>
  <div
    class="wk-location"
    @mouseenter="hovering = true"
    @mouseleave="hovering = false">
    <el-input
      v-model="dataValue.address"
      :disabled="disabled"
      readonly
      clearable
      placeholder="点击定位"
      @click.native="inputClick"
      @clear="inputClear">
      <template slot="suffix">
        <i
          v-if="dataValue.address && hovering"
          class="el-icon-circle-close"
          style="cursor: pointer;"
          @click.stop="inputClear" />
        <i v-else class="wk wk-icon-location" />
      </template>
    </el-input>

    <wk-location-point-dialog
      :value="dataValue"
      :visible.sync="pointDialogVisible"
      @select="pointSelect"
    />
  </div>
</template>

<script>
import WkLocationPointDialog from '../WkLocationPointDialog'

import { isObject } from '@/utils/types'
import { valueEquals } from 'element-ui/lib/utils/util'
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // 定位
  name: 'WkLocation',

  components: {
    WkLocationPointDialog
  },

  mixins: [Emitter],

  props: {
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    disabled: Boolean
  },

  data() {
    return {
      hovering: false,
      dataValue: this.getDefaultValue(),
      pointDialogVisible: false
    }
  },

  computed: {},

  watch: {
    value: {
      handler(val) {
        if (!valueEquals(val, this.dataValue)) {
          if (isObject(this.value)) {
            this.dataValue = val
          } else {
            this.dataValue = this.getDefaultValue()
            this.$emit('input', this.dataValue)
          }
        }
      },
      immediate: true
    }
  },

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    getDefaultValue() {
      return {
        lat: '',
        lng: '',
        address: ''
      }
    },

    inputClick() {
      if (!this.disabled) {
        this.pointDialogVisible = true
      }
    },

    inputClear() {
      this.dataValue = this.getDefaultValue()
      this.valueChange()
    },

    pointSelect(data) {
      if (data) {
        this.dataValue = {
          lat: data.point.lat,
          lng: data.point.lng,
          address: data.address + data.title
        }
      }

      this.valueChange()
    },

    valueChange() {
      this.$emit('input', this.dataValue)
      this.$emit('change', this.dataValue)
      this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
