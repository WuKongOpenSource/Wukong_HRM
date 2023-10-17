<template>
  <div style="padding: 12px 12px 0;" class="container">
    <el-form
      v-for="(item,index) in assessmentDimensionList"
      :key="item.key"
      ref="form"
      :validate-on-rule-change="false"
      hide-required-asterisk
      :model="tableForm"
      label-width="80px">
      <el-table
        :key="+new Date()"
        style="margin-top: 20px;"
        :data="tableForm['table' + item.key]"
        stripe
        border>
        <el-table-column>
          <!-- eslint-disable-next-line -->
          <template slot="header" slot-scope="scope">
            <div class="table-header">
              <div>
                <span class="table-header-title">{{ item.dimensionName }}</span>
                <span class="table-header-title">{{ item.quotaType == 1 ? '业绩指标' : '行为态度指标' }}</span>
                <span class="table-header-title">维度权重<span style="color: #ffb55f;">{{ item.dimensionWeight }}%</span></span>
              </div>
              <div v-if="!operation">
                <el-button
                  type="text"
                  :disabled="disabled"
                  @click="editAssess(index)">编辑</el-button>
                <el-button
                  type="text"
                  :disabled="disabled"
                  @click="deleteAssess(index,item.key)">删除</el-button>
              </div>
            </div>
          </template>
          <el-table-column
            v-for="(child,childIndex) in fieldList"
            :key="childIndex"
            :prop="child.fieldName"
            :label="child.name">
            <!-- eslint-disable-next-line -->
            <template slot="header" slot-scope="scope">
              <span
                v-if="child.fieldName == 'quotaName' || child.fieldName == 'standard' || child.fieldName == 'quotaWeight'"
                style="color: #ff7330;">*</span>{{ child.name }}
            </template>
            <template slot-scope="{ row, $index}">
              <el-button
                v-if="tableForm['table' + item.key] && tableForm['table' + item.key].length == $index+1 && child.fieldName == 'quotaName'"
                type="text"
                :disabled="combinedWeights(item.key) > 99 || disabled || (!item.isAllowEdit && operation)"
                @click="handlerCreate(item.key)">+新建指标项</el-button>

              <span
                v-if="tableForm['table' + item.key] && tableForm['table' + item.key].length == $index+1 && child.fieldName == 'quotaWeight'"
                :style="combinedWeights(item.key) > 100 ? 'color:red' : 'color:#ffb55f'">
                {{ combinedWeights(item.key) || '' }}<span v-if="combinedWeights(item.key)">%</span>
              </span>

              <el-form-item
                v-if="child.formType == 'text' && tableForm['table' + item.key] && tableForm['table' + item.key].length != $index+1"
                :prop="`table${item.key}[${$index}].${child.fieldName}`"
                :rules="child.rules || {}">
                <el-input
                  v-model="row[child.fieldName]"
                  :disabled="disabled || row.preset"
                  :type="child.fieldName != 'quotaName' ? 'textarea' : ''"
                  :autosize="child.fieldName != 'quotaName'"
                  :maxlength="child.max"
                  resize="none" />
              </el-form-item>
              <el-form-item
                v-if="child.formType == 'percent' && tableForm['table' + item.key] && tableForm['table' + item.key].length != $index+1"
                :prop="`table${item.key}[${$index}].${child.fieldName}`"
                :rules="{ required: true, message: '请输入', trigger: 'blur' }">
                <wk-percent-input
                  v-model="row[child.fieldName]"
                  :disabled="disabled || row.preset"
                  style="width: 100%;"
                  :max="child.max"
                  :min="child.min"
                  :controls="false" />
              </el-form-item>
              <el-form-item
                v-else-if="child.formType == 'select' && tableForm['table' + item.key] && tableForm['table' + item.key].length != $index+1"
                :prop="`table${item.key}[${$index}].${child.fieldName}`">
                <el-select
                  v-model="row[child.fieldName]"
                  :disabled="disabled || row.preset"
                  style="width: 100%;">
                  <el-option
                    v-for="(sItem, sIndex) in child.setting"
                    :key="sIndex"
                    :label="sItem.label"
                    :value="sItem.value" />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column
            label="操作">
            <template slot-scope="{row,scope, $index}">
              <el-button
                v-if="tableForm['table' + item.key].length != $index+1"
                type="text"
                :disabled="disabled || row.preset"
                @click="handlerDel(scope, $index,item.key)">删除</el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-form />
    </el-form>
  </div>
</template>

<script>
import WkPercentInput from '@/components/NewCom/WkPercentInput'
export default {
  name: 'AssessmentDimension',
  components: {
    WkPercentInput
  },
  inject: {
    disabled: {
      default: false
    }
  },
  props: {
    assessmentDimensionList: Array,
    quotaItem: Object,
    operation: Boolean
  },
  data() {
    return {
      fieldList: [
        {
          fieldName: 'quotaName',
          name: '指标名称',
          formType: 'text',
          max: 50,
          rules: { required: true, message: '请输入', trigger: 'blur' }
        }, {
          fieldName: 'quotaIllustrate',
          name: '指标说明',
          formType: 'text',
          max: 200,
          rules: { max: 200, message: '最多输入200个字符', trigger: 'blur' }
        }, {
          fieldName: 'standard',
          name: '考核标准',
          formType: 'text',
          max: 200,
          rules: [
            { required: true, message: '请输入', trigger: 'blur' },
            { max: 200, message: '最多输入200个字符', trigger: 'blur' }
          ]
        }, {
          fieldName: 'quotaWeight',
          name: '指标权重',
          formType: 'percent',
          max: 100,
          min: 0
        }, {
          fieldName: 'scoreType',
          name: '评分方式',
          formType: 'select',
          setting: [{ label: '直接输入', value: 1 }],
          value: 1
        }
      ],
      tableForm: {}
    }
  },
  computed: {
    // 当前考核维度权重总数
    combinedWeights() {
      return function(key) {
        var sum = 0
        if (this.tableForm['table' + key]) {
          this.tableForm['table' + key].forEach(item => {
            if (item.quotaWeight) {
              sum += item.quotaWeight
            }
          })
        }
        return sum
      }
    }
  },

  watch: {
    assessmentDimensionList: {
      handler() {
        this.assessmentDimensionList.forEach(item => {
          if (item.type == 'update' || item.type == 'new') {
            return
          }
          this.tableForm['table' + item.key] = [{}]
          item.type = 'new'
        })
      },
      immediate: true,
      deep: true
    },

    quotaItem: {
      handler() {
        if (Object.keys(this.quotaItem).length) {
          this.tableForm = this.quotaItem
        }
      },
      deep: true,
      immediate: true
    }
  },

  mounted() {},

  methods: {
    // 添加新项
    handlerCreate(key) {
      const tableForm = this.tableForm['table' + key]
      const obj = {
        quotaName: '',
        quotaIllustrate: '',
        standard: '',
        quotaWeight: undefined,
        scoreType: 1
      }
      tableForm.splice(tableForm.length ? tableForm.length - 1 : 0, 0, obj)
    },

    // 删除项
    handlerDel(scope, index, key) {
      this.tableForm['table' + key].splice(index, 1)
    },

    // 删除考核维度
    deleteAssess(index, key) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.assessmentDimensionList.splice(index, 1)
          for (var k in this.quotaItem) {
            if (k == ('table' + key)) {
              delete this.quotaItem[k]
            }
          }
        })
        .catch(() => {
        })
    },

    // 编辑考核维度
    editAssess(index) {
      this.$emit('editAssess', index)
    }
  }
}
</script>

<style scope lang="scss">
.container {
  .table-header {
    display: flex;
    justify-content: space-between;
    width: 100%;

    .table-header-title {
      margin-left: 10px;

      &:nth-of-type(2) {
        margin-left: 40px;
      }

      &:nth-of-type(1),
      &:nth-of-type(3) {
        color: black;
      }
    }
  }

  .el-form-item {
    margin-bottom: 0;

    .el-form-item__content {
      margin-left: unset !important;
    }
  }

  .el-button--text {
    color: #0052cc;

    &.is-disabled {
      color: #a5adba;
    }
  }
}
</style>
