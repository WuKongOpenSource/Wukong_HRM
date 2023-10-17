<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-04 14:52:43
 * @LastEditTime: 2020-06-10 16:35:42
 * @LastEditors: yang
-->
<template>
  <xr-create
    v-loading="loading"
    :title="titleContent"
    @close="close"
    @save="saveClick">
    <create-sections title="基本信息">
      <wk-form
        ref="baseForm"
        :model="baseForm"
        :rules="baseRules"
        :field-from="baseForm"
        :field-list="baseFields"
        label-position="top"
      >
        <template slot-scope="{ data }">
          <el-select
            v-if="data && data.formType == 'post'"
            v-model="baseForm[data.field]"
            style="width: 100%;">
            <el-option
              v-for="(item, index) in postList"
              :key="index"
              :label="item.postName"
              :value="item.postId">
              {{ item.postName }}<span style="color: #6b778c;">{{ item.deptName ? `(${item.deptName})` : '' }}</span>
            </el-option>
            <div slot="empty" class="empty">
              <span>暂无可选职位。</span>
              <el-button type="text" @click="postSetClick">前去设置</el-button>
            </div>
          </el-select>
          <el-select
            v-if="data && data.formType == 'channel'"
            v-model="baseForm[data.field]"
            style="width: 100%;">
            <el-option
              v-for="(item, index) in channelList"
              :key="index"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </template>
      </wk-form>
    </create-sections>
  </xr-create>
</template>

<script>
import {
  hrmRecruitCandidateAddAPI,
  hrmRecruitCandidateSetAPI
} from '@/api/hrm/recruit/candidate'
import {
  hrmRecruitPostQuerAllListAPI
} from '@/api/hrm/recruit/post'
import {
  hrmRecruitChannelQueryAPI
} from '@/api/hrm/employeePost'

import XrCreate from '@/components/XrCreate'
import WkForm from '@/components/NewCom/WkForm'
import CreateSections from '@/components/CreateSections'
import candidateModel from '../model/candidate'

import { objDeepCopy } from '@/utils'
import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'

export default {
  // 岗位创建
  name: 'CandidateCreateView',
  components: {
    XrCreate,
    CreateSections,
    WkForm
  },
  filters: {},
  mixins: [GenerateRulesMixin],
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false,
      baseFields: candidateModel.fields,
      baseRules: {},
      baseForm: {},
      channelList: [],
      postList: []
    }
  },
  computed: {
    titleContent() {
      return this.detail ? '编辑候选人' : '新建候选人'
    }
  },
  watch: {},
  created() {
    this.getChannelList()
    this.getPostList()

    const baseRules = {}
    candidateModel.fields.forEach(item => {
      baseRules[item.field] = this.getRules(item)
    })
    this.baseRules = baseRules

    if (this.detail) {
      const detail = objDeepCopy(this.detail)
      const baseForm = {}
      this.baseFields.forEach(item => {
        if (item.field == 'postId') {
          // 停用状态  将 id 变 空
          if (detail.postStatus == 0) {
            detail.postId = ''
          }
        }
        baseForm[item.field] = detail[item.field]
      })
      this.baseForm = baseForm
    } else {
      this.baseForm = {
        sex: 1
      }
    }
  },
  mounted() {
  },

  beforeDestroy() {},
  methods: {

    /**
     * 获取渠道信息
     */
    getChannelList() {
      hrmRecruitChannelQueryAPI()
        .then(res => {
          const channelList = res.data || []
          channelList.forEach(item => {
            if (item.status == 1) {
              item.label = item.value
              item.value = item.channelId
            }
          })
          this.channelList = channelList
        })
        .catch(() => {
        })
    },

    /**
     * 获取岗位信息
     */
    getPostList() {
      hrmRecruitPostQuerAllListAPI()
        .then(res => {
          this.postList = res.data || []
        })
        .catch(() => {
        })
    },

    close() {
      this.$emit('close')
    },

    saveClick() {
      this.$refs.baseForm.$children[0].validate(valid => {
        if (valid) {
          this.uploadCreateData()
        }
      })
    },

    /**
     * 提交数据
     */
    uploadCreateData() {
      const params = objDeepCopy(this.baseForm)
      for (const key in params) {
        if (params[key] === undefined) {
          params[key] = null
        }
      }
      if (this.detail) {
        params.candidateId = this.detail.candidateId
      }
      this.loading = true
      const request = this.detail ? hrmRecruitCandidateSetAPI : hrmRecruitCandidateAddAPI
      request(params).then(res => {
        this.$emit('close')
        this.$message.success(`${this.titleContent}成功`)
        this.$emit('success')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 去岗位设置点击
     */
    postSetClick() {
      this.$router.push({
        name: 'hrmPost'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.empty {
  padding: 8px;

  span {
    font-size: 13px;
    color: $--color-text-secondary;
  }
}
</style>
