<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="debouncedSaveField">
    <wk-form
      ref="wkBaseFrom"
      :model="fieldsForm"
      :rules="fieldsRules"
      :field-from="fieldsForm"
      :field-list="fields"
      label-position="top"
      @change="formChange">
      <template slot-scope="{ data }">
        <el-date-picker
          v-model="fieldsForm[data.field]"
          :picker-options="expireTimeOption"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          clearable
          style="width: 100%;"
          start-placeholder="开始日期"
          end-placeholder="结束日期" />
      </template>
    </wk-form>
  </xr-create>
</template>

<script>
import {
  examineDelegateSaveAPI,
  examineDelegateUpdateAPI
} from '@/api/admin/processDelegation'

import XrCreate from '@/components/XrCreate'
import WkForm from '@/components/NewCom/WkForm'

import { debounce } from 'throttle-debounce'
import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import { objDeepCopy } from '@/utils'
import { isEmpty } from '@/utils/types'

export default {
  // ProcessDelegationCreate 委托创建
  name: 'ProcessDelegationCreate',

  components: {
    XrCreate,
    WkForm
  },

  mixins: [GenerateRulesMixin],

  props: {
    data: {
      type: Object,
      default: () => ({})
    }
  },

  data() {
    return {
      loading: false,
      fieldsForm: {},
      fields: [
        {
          field: 'delegateUserId',
          formType: 'user',
          isNull: 1,
          name: '委托人',
          setting: [],
          inputTips: '',
          radio: true
        }, {
          field: 'trusteeUserId',
          formType: 'user',
          isNull: 1,
          name: '受委托人',
          setting: [],
          inputTips: '',
          radio: true
        }, {
          field: 'labels',
          formType: 'checkbox',
          isNull: 1,
          name: '业务模块',
          setting: [
            { label: '办公审批', value: '0' },
            { label: '合同', value: '1' },
            { label: '回款', value: '2' },
            { label: '发票', value: '3' },
            { label: '薪资', value: '4' },
            { label: '采购审核', value: '5' },
            { label: '采购退货审核', value: '6' },
            { label: '销售审核', value: '7' },
            { label: '销售退货审核', value: '8' },
            { label: '付款单审核', value: '9' },
            { label: '回款单审核', value: '10' },
            { label: '盘点审核', value: '11' },
            { label: '调拨审核', value: '12' },
            { label: '阶段审批', value: '20' }
          ],
          clearable: true
        }, {
          field: 'time',
          formType: 'time',
          isNull: 1,
          dateValueFormat: 'yyyy-MM-dd HH:mm:ss',
          name: '委托时间'
        }, {
          field: 'remarks',
          formType: 'textarea',
          isNull: 0,
          name: '备注'
        }
      ],
      fieldsRules: {},

      expireTimeOption: {
        disabledDate(date) {
          const now = new Date().getTime()
          const today = new Date(now)
          today.setHours(0, 0, 0, 0)
          return date.getTime() < today.getTime()
        }
      }
    }
  },

  computed: {
    title() {
      return this.data ? '编辑委托' : '新建委托'
    }
  },

  watch: {},

  created() {
    // 编辑表单回填
    if (this.data) {
      const { delegateUserId, trusteeUserId, labels, startTime, endTime, remarks } = this.data
      this.fieldsForm = {
        delegateUserId,
        trusteeUserId,
        labels: labels.split(','),
        time: [startTime, endTime],
        remarks
      }
    } else {
      // 新建回填
      this.fieldsForm = {
        delegateUserId: '',
        trusteeUserId: '',
        labels: '',
        time: [],
        remarks: ''
      }
    }
    this.initRules()
    this.debouncedSaveField = debounce(300, this.saveClick)
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 初始化规则
     * @return {*}
     */
    initRules() {
      const fieldsRules = {}
      this.fields.forEach(item => {
        fieldsRules[item.field] = this.getRules(item)
      })
      this.fieldsRules = fieldsRules
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('close')
    },

    /**
     * change
     */
    formChange(item, index, value) {
    },

    /**
     * @description: 保存
     * @return {*}
     */
    saveClick() {
      this.$refs.wkBaseFrom.instance.validate(valid => {
        if (!valid) {
          this.$message.error('请完善基本信息')
        } else {
          const fieldsForm = objDeepCopy(this.fieldsForm)
          this.loading = true
          const params = {
            ...fieldsForm,
            labels: fieldsForm.labels.join(),
            startTime: fieldsForm.time[0],
            endTime: fieldsForm.time[1],
            state: 1
          }

          if (!isEmpty(this.data)) {
            params.delegateId = this.data.delegateId
          }
          const request = isEmpty(this.data) ? examineDelegateSaveAPI : examineDelegateUpdateAPI
          request(params)
            .then(res => {
              this.$message.success(isEmpty(this.data) ? '保存成功' : '编辑成功')
              this.loading = false
              this.$emit('success')
              this.close()
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
