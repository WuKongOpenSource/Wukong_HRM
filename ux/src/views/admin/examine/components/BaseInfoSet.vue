<template>
  <el-card
    :loading="loading"
    :body-style="{ height: '100%',overflow: 'auto'}"
    class="base-info-set">
    <div class="base-info-set__header">
      配置基础信息
    </div>
    <create-sections title="基础信息">
      <wk-form
        ref="wkBaseFrom"
        :model="fieldsForm"
        :rules="fieldsRules"
        :field-from="fieldsForm"
        :field-list="fields"
        label-position="top"
        @change="formChange"
      >
        <template slot-scope="{ data, index }">
          <wk-user-dep-dialog-select
            v-if="data && data.formType == 'userDep'"
            :user-value.sync="fieldsForm.userList"
            :dep-value.sync="fieldsForm.deptList"
            :sub-dept-contain.sync="fieldsForm.isNeedChild"
            :props="{
              showSubDeptContain: true,
              showDisableUser: false
            }"
            style="width: 100%;"
            @change="userDepSelectChange(arguments, data, index)"
          />
          <template v-else>
            <slot :data="data" />
          </template>
        </template>
      </wk-form>
    </create-sections>
    <create-sections
      v-if="examine"
      title="高级设置">
      <AdvancedSetting
        ref="advancedSetting"
        v-model="fieldsForm.advancedConfigBO"
        :is-stage-flow="isStageFlow"
        :props="advancedProps"
      />
    </create-sections>
  </el-card>
</template>
<script type="text/javascript">

import CreateSections from '@/components/CreateSections'
import WkForm from '@/components/NewCom/WkForm'
import WkUserDepDialogSelect from '@/components/NewCom/WkUserDepDialogSelect'
import AdvancedSetting from './AdvancedSetting'
export default {
  name: 'BaseInfoSet',
  components: {
    CreateSections,
    WkForm,
    WkUserDepDialogSelect,
    AdvancedSetting
  },
  props: {
    advancedProps: Object,
    fields: Array,
    fieldsForm: Object,
    fieldsRules: Object,
    examine: { // 是否展示高级审批配置
      type: Boolean,
      default: false
    },
    isStageFlow: { // 是否为阶段流程审批
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    form() {
      return this.$refs.wkBaseFrom.instance
    }
  },
  mounted() {},
  methods: {
    /**
     * 验证
     */
    validate() {
      return new Promise((resolve) => {
        this.form.validate(valid => {
          if (!valid) {
            this.$message.error('请完善基本信息')
          }
          resolve(valid)
        })
      })
    },

    /**
     * change
     */
    formChange(item, index, value) {
      this.$emit('change', item, index, value)
    },

    /**
     * 用户部门选择
     */
    userDepSelectChange(list, item, index) {
      const userList = list[2]
      const deptList = list[3]
      this.$emit('change', item, index, {
        userList,
        deptList
      })
    },

    // /**
    //  * 请求参数
    //  */
    // getSubmiteParams() {
    //   var params = {}
    //   for (let index = 0; index < this.wkBaseFrom.crmFields.length; index++) {
    //     const element = this.wkBaseFrom.crmFields[index]
    //     // 关联产品数据需要特殊拼接
    //     if (element.key === 'dept') {
    //       if (element.value['users']) {
    //         params['userList'] = element.value['users'].map(item => item.userId)
    //       }
    //       if (element.value['strucs']) {
    //         params['deptList'] = element.value['strucs'].map(item => item.id)
    //       }
    //     } else {
    //       params[element.key] = element.value
    //     }
    //   }
    //   return params
    // },

    hidenView() {
      this.$emit('hiden-view')
    }

  }
}
</script>
<style lang="scss" scoped>
.base-info-set {
  &__header {
    height: 40px;
    padding: 0 10px;
    margin-bottom: 20px;
    font-size: 17px;
    font-weight: bold;
    color: $--color-text-primary;
  }

  // .wk-form {
  //   ::v-deep .el-form-item.is-textarea {
  //     flex: 0 0 100%;
  //   }
  // }

  .create-sections {
    // height: calc(100% - 60px);
    overflow-y: auto;
  }
}
</style>
