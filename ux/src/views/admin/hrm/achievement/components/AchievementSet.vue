<template>
  <div v-loading="loading">
    <div class="content-header">
      <span>{{ tableName }}</span>
      <el-button
        type="primary"
        @click="save">保存</el-button>
    </div>
    <div class="content-body">
      <el-form
        ref="form"
        :model="form"
        :validate-on-rule-change="false"
        hide-required-asterisk
      >
        <div
          class="section">
          <div class="section__title">
            非固定模板<span>（由管理员统一设置模板标题，员工自行填写内容，也可增加考核项）</span>
          </div>
          <div class="section__row">
            <span>员工填写权重占比</span>
            <el-switch
              v-model="form.isEmpWeight"
              :active-value="1"
              :inactive-value="0"
              @change="weightSwithChange" />
          </div>
          <div
            v-for="(fixItem, index) in form.noFixedSegList"
            :key="index"
            class="item-section">
            <el-row type="flex">
              <el-col :span="12">
                <el-form-item
                  :prop="`noFixedSegList[${index}].segName`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="fixItem.segName"
                    placeholder="请输入标题"
                    type="text">
                    <template slot="append">具体内容由员工填写</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="1" />
              <el-col :span="5">
                <el-form-item
                  :prop="`noFixedSegList[${index}].weight`"
                  :rules="getWeightRules(form)"
                  label="">
                  <el-input
                    v-model="fixItem.weight"
                    v-wk-number
                    :disabled="form.isEmpWeight == 1"
                    :placeholder="form.isEmpWeight == 1 ? '员工填写权限占比' : '请输入权重信息'"
                    type="text">
                    <template slot="prepend">权重</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="1" class="is-close">
                <i
                  v-if="form.isEmpWeight == 1 && form.noFixedSegList.length > 1"
                  class="el-icon-close"
                  @click="itemDeleteClick(form.noFixedSegList, index)" />
              </el-col>
            </el-row>
            <div>
              <el-row
                v-for="(item, itemIndex) in fixItem.items"
                :key="itemIndex"
              >
                <el-col :span="12">
                  <el-form-item
                    :prop="`noFixedSegList[${index}].items[${itemIndex}].itemName`"
                    :rules="{ required: true, message: '请输入', trigger: 'change' }"
                    style="padding-left: 140px;"
                    label="">
                    <el-input
                      v-model="item.itemName"
                      class="sub-input"
                      placeholder="请输入标题名称"
                      type="text">
                      <template slot="append">具体内容由员工填写</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="1" class="is-close">
                  <i
                    class="el-icon-close"
                    @click="itemDeleteClick(fixItem.items, itemIndex)" />
                </el-col>
              </el-row>
              <el-button style="padding: 0 0 8px 140px;" icon="el-icon-plus" type="text" @click="subAddClick(fixItem.items)">添加</el-button>
            </div>
          </div>
          <el-button
            v-if="form.isEmpWeight == 0"
            icon="el-icon-plus"
            type="text"
            @click="bigAddClick(form.noFixedSegList)">添加考核项</el-button>
        </div>
        <div
          class="section">
          <div class="section__title">
            固定模板<span>（由管理员统一设置考核内容，员工无法修改内容，也无法增加考核项）</span><el-switch
              v-model="form.isOpenFix"
              :active-value="1"
              :inactive-value="0"
              @change="fixSwithChange" />
          </div>
          <div
            v-for="(fixItem, index) in form.fixedSegList"
            :key="index"
            class="item-section">
            <el-row>
              <el-col :span="12">
                <el-form-item
                  :prop="`fixedSegList[${index}].segName`"
                  :rules="{ required: true, message: '请输入', trigger: 'change' }"
                  label="">
                  <el-input
                    v-model="fixItem.segName"
                    placeholder="价值观考核"
                    type="text" />
                </el-form-item>
              </el-col>
              <el-col :span="1" />
              <el-col :span="5">
                <el-form-item
                  :prop="`fixedSegList[${index}].weight`"
                  :rules="getWeightRules()"
                  label="">
                  <el-input
                    v-model="fixItem.weight"
                    v-wk-number
                    placeholder="请输入权重信息"
                    type="text">
                    <template slot="prepend">权重</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="1" class="is-close">
                <i
                  v-if="form.fixedSegList.length > 1"
                  class="el-icon-close"
                  @click="itemDeleteClick(form.fixedSegList, index)" />
              </el-col>
            </el-row>
            <div>
              <el-row
                v-for="(item, itemIndex) in fixItem.items"
                :key="itemIndex"
              >
                <el-col :span="12">
                  <el-form-item
                    :prop="`fixedSegList[${index}].items[${itemIndex}].itemName`"
                    :rules="{ required: true, message: '请输入', trigger: 'change' }"
                    style="padding-left: 140px;"
                    label="">
                    <el-input
                      v-model="item.itemName"
                      class="sub-input"
                      placeholder="请设置具体考核内容"
                      type="text" />
                  </el-form-item>
                </el-col>
                <el-col :span="1" class="is-close">
                  <i
                    class="el-icon-close"
                    @click="itemDeleteClick(fixItem.items, itemIndex)" />
                </el-col>
              </el-row>
              <el-button style="padding: 0 0 8px 140px;" icon="el-icon-plus" type="text" @click="subAddClick(fixItem.items)">添加</el-button>
            </div>
          </div>
          <el-button
            style="padding-left: 0;"
            icon="el-icon-plus"
            type="text"
            @click="bigAddClick(form.fixedSegList)">添加考核项</el-button>
        </div>

        <div
          class="section">
          <div class="section__title">考核说明</div>
          <el-form-item
            :rules="{ required: true, message: '请输入', trigger: 'change' }"
            prop="description"
            label="">
            <el-input
              v-model="form.description"
              :autosize="{ minRows: 5}"
              :maxlength="800"
              resize="none"
              type="textarea" />
          </el-form-item>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import {
  hrmConfigQueryAchievementAPI,
  hrmConfigSetAchievementAPI
} from '@/api/admin/hrm'

import { objDeepCopy, floatAdd } from '@/utils'

export default {
  name: 'AchievementSet',

  components: {},

  props: {
    id: [String, Number]
  },

  data() {
    return {
      loading: false,
      tableName: '',
      form: {}
    }
  },

  watch: {
    id: {
      handler(val) {
        if (val) {
          this.getDetail()
        }
      },
      immediate: true
    }
  },

  created() {

  },

  methods: {
    /**
     * 获取详情
     */
    getDetail() {
      this.loading = true
      hrmConfigQueryAchievementAPI(this.id)
        .then(res => {
          const data = res.data || {}
          this.tableName = data.tableName
          if (!data.fixedSegList) {
            data.fixedSegList = []
          }

          if (!data.noFixedSegList) {
            data.noFixedSegList = []
          }
          data.isOpenFix = data.fixedSegList && data.fixedSegList.length > 0 ? 1 : 0
          this.form = data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * weight 验证规则
     */
    getWeightRules(form) {
      const validateWeight = (rule, value, callback) => {
        if (form && form.isEmpWeight == 1) {
          callback()
        } else if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error('范围在0-100之间'))
        } else {
          callback()
        }
      }
      return { validator: validateWeight, trigger: 'change', form }
    },

    /**
     * 是否员工填写
     */
    weightSwithChange(val) {
      if (val == 1) {
        const firstItem = this.form.noFixedSegList[0]
        firstItem.weight = ''
        this.form.noFixedSegList = [firstItem]
      }
      this.$nextTick(() => {
        this.$refs.form.validateField('noFixedSegList[0].weight')
      })
    },

    /**
     * 是否开启固定
     */
    fixSwithChange(val) {
      if (val) {
        this.bigAddClick(this.form.fixedSegList)
      } else {
        this.form.fixedSegList = []
      }
    },

    /**
     * 大块添加
     */
    bigAddClick(list) {
      list.push({
        segName: '',
        weight: '',
        items: [{
          itemName: ''
        }, {
          itemName: ''
        }, {
          itemName: ''
        }]
      })
    },

    /**
     * 子项添加
     */
    subAddClick(list) {
      list.push({
        itemName: ''
      })
    },

    /**
     * 事项删除
     */
    itemDeleteClick(list, index) {
      list.splice(index, 1)
    },

    /**
     * 保存操作
     */
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 验证权重累加是否100%
          let weight = 0
          if (this.form.isEmpWeight == 0) {
            this.form.noFixedSegList.forEach(item => {
              weight = floatAdd(weight, item.weight)
            })
          }

          if (this.form.isOpenFix == 1) {
            this.form.fixedSegList.forEach(item => {
              weight = floatAdd(weight, item.weight)
            })
          }

          // 非固定管理员设置，必须weight 累加是 100
          // 非固定员工设置， 固定weight累加必须要小于100
          if (this.form.isEmpWeight == 0 && weight != 100) {
            this.$message.error('考核项权重累加，需等于100%')
          } else if (this.form.isEmpWeight == 1 && this.form.isEmpWeight == 1 && weight >= 100) {
            this.$message.error('固定模板考核项权重累加，需小于100%')
          } else {
            this.submiteData()
          }
        } else {
          return false
        }
      })
    },

    submiteData() {
      const form = objDeepCopy(this.form)
      delete form['isOpenFix']
      this.loading = true
      hrmConfigSetAchievementAPI(form)
        .then(res => {
          this.$message.success('保存成功')
          this.$emit('change', res.data || {})
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../../style/index.scss";

.nav-sections-wrap {
  padding-bottom: $--interval-base;
}

.section {
  &__title {
    span {
      color: $--color-text-secondary;
    }

    margin-bottom: 10px;
  }

  &__row {
    margin-bottom: 10px;
  }

  .item-section {
    .sub-input::before {
      position: absolute;
      top: 8px;
      left: -25px;
      width: 10px;
      height: 10px;
      content: "";
      border-bottom: 2px solid $--border-color-base;
      border-left: 2px solid $--border-color-base;
    }
  }

  & + & {
    padding-top: #{$--interval-base * 2};
    margin-top: #{$--interval-base * 2};
  }
}

.el-form {
  ::v-deep .el-form-item__content {
    line-height: 34px;
  }
}

.el-col.is-close {
  line-height: 34px;
  text-align: center;

  .el-icon-close {
    font-size: 20px;
    color: $--border-color-base;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }
}
</style>
