<template>
  <transition name="opacity-fade">
    <div class="business-approve-flow-create">
      <!-- 头部 -->
      <wk-backgroud-tabs>
        <template slot="left">
          <span>{{ titleContent }}结果模板</span>
        </template>
        <template slot="right">
          <el-button type="primary" @click="saveClick">保存</el-button>
          <el-button @click="closeClick">取消</el-button>
          <!-- <i class="el-icon-close create-close" @click="closeClick" /> -->
        </template>
      </wk-backgroud-tabs>

      <el-card
        v-loading="loading"
        style="padding: 0 20px;overflow: auto;"
        :body-style="{ height: '100%' }"
        class="base-info-set">
        <create-sections title="基本信息">
          <el-form ref="baseForm" :model="baseForm" :rules="baseRules">
            <div class="base-info-section">
              <el-form-item prop="resultTemplateName">
                <template slot="label">结果设置名称</template>
                <el-input
                  v-model="baseForm.resultTemplateName"
                  :maxlength="100"
                  style="width: 50%;" />
              </el-form-item>
            </div>

            <div class="other-info-section">
              <el-form-item prop="resultTemplateLevelBOList">
                <flexbox
                  v-for="(scoreLevel, sIndex) in baseForm.resultTemplateLevelBOList"
                  :key="sIndex"
                  class="weight-data-row">
                  <div class="column">
                    <div v-if="sIndex == 0" class="label">等级</div>
                    <el-form-item
                      :prop="`resultTemplateLevelBOList[${sIndex}].levelName`"
                      :rules="{
                        required: true,
                        message: '请输入',
                        trigger: 'change',
                      }"
                      label="">
                      <el-input
                        v-model="scoreLevel.levelName"
                        style="width: 200px;"
                        type="text" />
                    </el-form-item>
                  </div>
                  <div class="column">
                    <div v-if="sIndex == 0" class="label">分数范围</div>
                    <flexbox class="flex-box">
                      <el-form-item
                        :prop="`resultTemplateLevelBOList[${sIndex}].scoreLowerLimit`"
                        :rules="getScoreRules(scoreLevel, sIndex, 'min')"
                        label="">
                        <el-input
                          v-model="scoreLevel.scoreLowerLimit"
                          v-wk-number="'positiveInt'"
                          style="width: 200px;height: 34px;"
                          type="text"
                          @blur="inputBlurValidate('score', sIndex, 'min')">
                          <template slot="append">分</template>
                        </el-input>
                      </el-form-item>
                      <div class="range">~</div>
                      <el-form-item
                        :prop="`resultTemplateLevelBOList[${sIndex}].scoreUpperLimit`"
                        :rules="getScoreRules(scoreLevel, sIndex, 'max')"
                        label="">
                        <el-input
                          v-model="scoreLevel.scoreUpperLimit"
                          v-wk-number="'positiveInt'"
                          style="width: 200px;height: 34px;"
                          type="text"
                          @blur="inputBlurValidate('score', sIndex, 'max')">
                          <template slot="append">分</template>
                        </el-input>
                      </el-form-item>

                    </flexbox>
                  </div>
                  <div class="column input-number">
                    <div v-if="sIndex == 0" class="label">系数</div>
                    <flexbox>
                      <el-form-item
                        :prop="`resultTemplateLevelBOList[${sIndex}].coefficient`"
                        :rules="[
                          {
                            required: true,
                            message: '请输入',
                            trigger: 'blur',
                          },
                          {
                            pattern:
                              /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/,
                            message: '请输入正确的格式,可保留两位小数',
                          },
                        ]"
                        label="">
                        <el-input
                          v-model="scoreLevel.coefficient"
                          style="width: 200px;"
                          type="number" />
                      </el-form-item>
                      <i
                        :class="{ 'is-first': sIndex == 0 }"
                        class="el-icon-remove"
                        @click="deleteItemClick(baseForm.resultTemplateLevelBOList, sIndex)" />
                    </flexbox>
                  </div>
                </flexbox>
                <div class="add-bar">
                  <el-button
                    icon="el-icon-plus"
                    type="text"
                    @click="addScoreLevelClick">
                    添加考核结果等级
                  </el-button>
                </div>
              </el-form-item>
            </div>
          </el-form>
        </create-sections>
      </el-card>
    </div>
  </transition>
</template>

<script type="text/javascript">
import {
  addAchievementsResultTemplate,
  updateResultTemplate
} from '@/api/hrm/achievementsResultTemplate'

import WkBackgroudTabs from '@/views/admin/examine/components/WkBackgroudTabs'
import CreateSections from '@/components/CreateSections'

import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import CustomFieldsMixin from '@/mixins/CustomFields'
import { objDeepCopy } from '@/utils'
import NP from 'number-precision'

export default {
  name: 'AssessmentResultsCreate',
  components: {
    WkBackgroudTabs,
    CreateSections
  },
  mixins: [GenerateRulesMixin, CustomFieldsMixin],

  props: {
    /**
     * 点编辑进入时的传值
     */
    resultsTemplate: Object
  },

  data() {
    return {
      loading: false,
      // 编辑用
      detail: null,
      baseRules: {
        resultTemplateName: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        resultTemplateLevelBOList: { required: true, message: '请选择', trigger: ['blur', 'change'] }
      },
      // 是否能编辑结果
      baseForm: {
        resultTemplateLevelBOList: [{
          levelName: 'S',
          scoreLowerLimit: '85',
          scoreUpperLimit: '100',
          minNum: '10',
          maxNum: '15'
        }, {
          levelName: 'A',
          scoreLowerLimit: '75',
          scoreUpperLimit: '84',
          minNum: '50',
          maxNum: '55'
        }, {
          levelName: 'B',
          scoreLowerLimit: '60',
          scoreUpperLimit: '74',
          minNum: '30',
          maxNum: '35'
        }, {
          levelName: 'C',
          scoreLowerLimit: '0',
          scoreUpperLimit: '59',
          minNum: '10',
          maxNum: '15'
        }]
      }
    }
  },

  computed: {
    titleContent() {
      return Object.keys(this.resultsTemplate).length ? '编辑' : '新建'
    }
  },

  watch: {
    resultsTemplate: {
      handler(val) {
        if (Object.keys(val).length) {
          this.baseForm = objDeepCopy(val)
        }
      },
      immediate: true,
      deep: true
    }
  },

  created() {
  },

  methods: {
    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    },

    // 删除评测
    deleteItemClick(list, index) {
      list.splice(index, 1)
    },

    /**
     * 增加评测
     */
    addScoreLevelClick() {
      this.baseForm.resultTemplateLevelBOList.push({
        levelName: ''
      })
    },

    /**
     * 获取比例规则
     */
    getProportionRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error('范围在0-100之间'))
        } else {
          if (parseInt(rule.data.maxNum || 0) < parseInt(rule.data.minNum || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else if (this.baseForm.resultTemplateLevelBOList.length) {
            let min = 0
            let max = 0
            this.baseForm.resultTemplateLevelBOList.forEach(item => {
              min = NP.plus(min, item.minNum)
              max = NP.plus(max, item.maxNum)
            })
            if (min > 100 && rule.type == 'min') {
              if (rule.index == this.baseForm.resultTemplateLevelBOList.length - 1) {
                callback(new Error('下限值之和不能大于100'))
              }
            } else if (max < 100 && rule.type == 'max') {
              if (rule.index == this.baseForm.resultTemplateLevelBOList.length - 1) {
                callback(new Error('上限值应大于等于100'))
              }
            } else {
              callback()
            }
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 获取分数规则
     */
    getScoreRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error(`范围在0-100之间`))
        } else {
          if (parseInt(rule.data.scoreUpperLimit || 0) < parseInt(rule.data.scoreLowerLimit || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else {
            if (rule.index > 0) {
              const preItem = this.baseForm.resultTemplateLevelBOList[rule.index - 1]
              if (parseInt(preItem.scoreLowerLimit || 0) <= parseInt(value || 0)) {
                callback(new Error('须小于上一等级下限值'))
                return
              }
            }
            callback()
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 分数 比例失去焦点 验证准确
     */
    inputBlurValidate(handleType, index, type) {
      if (handleType == 'num') {
        this.$refs.baseForm.validateField(`resultTemplateLevelBOList[${index}].${type == 'min' ? 'maxNum' : 'scoreLowerLimit'}`)
      } else if (handleType == 'score') {
        this.$refs.baseForm.validateField(`resultTemplateLevelBOList[${index}].${type == 'min' ? 'scoreUpperLimit' : 'scoreLowerLimit'}`)
      }
    },

    /**
     * 保存点击
     */
    saveClick() {
      this.$refs.baseForm.validate(valid => {
        if (valid) {
          this.uploadCreateData()
        }
      })
    },

    /**
     * 提交数据
     */
    uploadCreateData() {
      this.loading = true
      const params = objDeepCopy(this.baseForm)
      const request = params.resultTemplateId ? updateResultTemplate : addAchievementsResultTemplate
      request(params).then(res => {
        this.$emit('close')
        this.$message.success(`${this.titleContent}成功`)
        this.$emit('createEditSuccess')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }

  }
}
</script>

<style lang="scss" scoped>
::v-deep .drawer {
  .el-drawer__header {
    height: 60px;
    padding: 0 15px;
    font-size: 18px;
    font-weight: bold;
    background: #f7f8fa;
    border-bottom: 1px solid $--border-color-base;

    .el-icon-close {
      font-size: 24px;
      color: #909399;
      cursor: pointer;
    }

    .el-icon-close:hover {
      color: $--color-primary;
    }
  }

  .drawer__footer {
    position: absolute;
    bottom: 40px;
    padding: 12px 12px 0;
  }
}

.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

.business-approve-flow-create {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  background-color: #f5f5f7;

  .create-close {
    display: block;
    padding: 10px;
    font-size: 24px;
    color: #909399;
    cursor: pointer;
  }

  .create-close:hover {
    color: $--color-primary;
  }

  .base-info-set {
    width: 100%;
    height: 100%;

    .other {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 12px 0;
    }
  }

  .wk-approve-flow-wrap {
    top: 60px;
  }
}

::v-deep .el-form {
  .el-form-item.is-required .el-form-item__label::before {
    margin-right: 0;
  }

  .el-form-item {
    // margin-bottom: 10px;

    .el-form-item__label {
      display: block;
      float: inherit;
      padding: 5px 0;
      line-height: inherit;
      color: $--color-text-primary;
      text-align: left;
    }

    .el-form-item__error {
      padding-top: 2px;
    }

    .el-form-item__content {
      line-height: 34px;
    }
  }
}

.input-number {
  ::v-deep input {
    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      -webkit-appearance: none;
    }

    &[type="number"] {
      -moz-appearance: textfield;
    }
  }
}

.base-info-section {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -23px;

  ::v-deep .el-form-item {
    flex: 0 0 50%;
    flex-shrink: 0;
    padding: 0 40px;
  }
}

.other-info-section {
  padding: 0 17px;
}

.user-data-row {
  padding-bottom: 10px;

  .el-select {
    width: 250px;
  }

  .wk-user-select {
    width: 200px;
  }

  .el-form-item + .el-form-item {
    margin-left: 20px;
  }

  .weight-label {
    margin-right: 8px;
    margin-bottom: 10px;
    margin-left: 20px;
  }

  .el-icon-remove {
    display: none;
    margin-bottom: 10px;
    margin-left: 8px;
    color: #ff6767;
    cursor: pointer;
  }

  &:hover {
    .el-icon-remove {
      display: inline-block;
    }
  }
}

.weight-data-row {
  padding-bottom: 20px;

  .column + .column {
    margin-left: 20px;
  }

  .flex-box {
    height: 34px;

    ::v-deep input {
      height: 34px;
    }
  }

  .range {
    margin: 0 8px 10px;
  }

  .el-input {
    width: 200px;
  }

  .el-icon-remove {
    display: none;

    // margin-bottom: 10px;
    margin-left: 8px;
    color: #ff6767;
    cursor: pointer;

    &.is-first {
      // margin-top: 20px;
      margin-bottom: 0;
    }
  }

  &:hover {
    .el-icon-remove {
      display: inline-block;
    }
  }
}

.wk-help-tips {
  margin-left: 3px;
}

.add-bar {
  line-height: initial;

  .el-button {
    padding-top: 0;
  }
}

.wk-text-tips {
  font-size: 12px;
  color: #ccc;
}
</style>
