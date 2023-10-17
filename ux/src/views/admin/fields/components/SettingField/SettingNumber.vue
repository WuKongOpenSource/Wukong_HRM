<template>
  <div class="setting-number">
    <flexbox
      align="center"
      justify="flex-start"
      class="setting-number-item">
      <el-checkbox
        v-model="checked"
        :disabled="!editAuth"
        @change="checkedChange" />
      <span style="font-size: 13px;">支持小数</span>
      <template v-if="checked">
        <span style="margin: 0 4px;">限制</span>
        <el-select
          v-model="field.precisions"
          size="small"
          placeholder=""
          :disabled="!editAuth"
          @change="handleSelectChange">
          <el-option
            v-for="item in precisionList"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
        <span style="margin-left: 4px;">位</span>
      </template>
    </flexbox>
    <flexbox
      align="flex-start"
      justify="flex-start"
      direction="column"
      class="setting-number-item">
      <el-checkbox
        v-model="limitChecked"
        :disabled="!editAuth"
        @change="limitChange">
        限制数值范围
      </el-checkbox>
      <flexbox
        v-if="limitChecked"
        align="center"
        justify="flex-start"
        class="number-range-body">
        <flexbox-item>
          <el-input-number
            v-model="minNumRestrict"
            :controls="false"
            placeholder="最小值"
            :disabled="!editAuth"
            @change="handleChangeNumber('minNumRestrict')" />
        </flexbox-item>
        <div class="number-range-text">~</div>
        <flexbox-item>
          <el-input-number
            v-model="maxNumRestrict"
            :controls="false"
            placeholder="最大值"
            :disabled="!editAuth"
            @change="handleChangeNumber('maxNumRestrict')" />
        </flexbox-item>
      </flexbox>
    </flexbox>
  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingNumber',
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      checked: false,
      precisionList: [],

      limitChecked: false,
      minNumRestrict: undefined, // 定义为 undefined 防止input number自动填充0
      maxNumRestrict: undefined
    }
  },
  computed: {
    // 编辑权限限制
    editAuth() {
      return getFieldAuth(this.field.operating).defaultEdit
    }
  },
  watch: {
    field: {
      handler() {
        if ([
          'number',
          'floatnumber',
          'percent'
        ].includes(this.field.formType)) {
          if (!this.field.hasOwnProperty('minNumRestrict')) {
            this.field.minNumRestrict = null
          }
          if (!this.field.hasOwnProperty('maxNumRestrict')) {
            this.field.maxNumRestrict = null
          }
          this.minNumRestrict = isEmpty(this.field.minNumRestrict) ? undefined : Number(this.field.minNumRestrict)
          this.maxNumRestrict = isEmpty(this.field.maxNumRestrict) ? undefined : Number(this.field.maxNumRestrict)

          // 小数位
          const max = this.field.formType === 'percent' ? 5 : 14
          this.precisionList = Array.from({ length: max })
            .map((o, i) => {
              return { label: i + 1, value: i + 1 }
            })
          // if (this.field.formType !== 'percent') {
          //   this.precisionList.unshift({ label: '不限', value: 0 })
          // }
          if (!this.field.hasOwnProperty('precisions')) {
            this.field.precisions = this.field.formType === 'number' ? 4 : 2
          }
          if (this.field.precisions > max) {
            // 不能大于最大值
            this.field.precisions = max
          }
          this.checked = !isEmpty(this.field.precisions)
          this.limitChecked = !isEmpty(this.minNumRestrict) || !isEmpty(this.maxNumRestrict)
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    checkedChange() {
      if (!this.checked) {
        this.field.precisions = null
      } else {
        this.field.precisions = 2
      }
    },
    handleSelectChange() {
      this.$set(this.field, 'precisions', this.field.precisions)
      this.$forceUpdate()
    },
    limitChange() {
      if (!this.limitChecked) {
        this.minNumRestrict = undefined
        this.maxNumRestrict = undefined

        this.field.minNumRestrict = ''
        this.field.maxNumRestrict = ''
      }
    },
    handleChangeNumber(type) {
      const current = this[type]
      const len = String(current || '')
        .replace('.', '')
        .replace('-', '')
        .length

      const maxlength = this.field.formType === 'percent' ? 10 : 15
      if (len > maxlength) {
        this.$message.error(`最多支持${maxlength}位数字`)
        this.field[type] = null
        return
      }

      const min = this.minNumRestrict
      const max = this.maxNumRestrict

      if (!isEmpty(min) && !isEmpty(max)) {
        if (Number(min) > Number(max)) {
          this.$message.error('请输入正确的数值范围')
          this.field[type] = null
        }
      }
      const minNum = isEmpty(min) ? '' : min
      const maxNum = isEmpty(max) ? '' : max
      this.field.minNumRestrict = this.minNumRestrict !== null ? String(minNum) : null
      this.field.maxNumRestrict = this.maxNumRestrict !== null ? String(maxNum) : null
    }
  }
}
</script>

<style>
.setting-number-tooltip {
  max-width: 250px;
}
</style>
<style scoped lang="scss">
.setting-number-item {
  height: 32px;

  &:nth-child(2) {
    height: auto;
    margin-top: 10px;
  }

  .el-checkbox {
    margin-right: 8px;
  }

  .el-tooltip {
    margin: 0 10px 0 5px;
  }

  .el-select {
    width: 70px;
  }

  .el-input-number {
    width: 100%;
  }

  .number-range-body {
    margin-top: 10px;
  }

  .number-range-text {
    padding: 0 10px;
  }
}
</style>
