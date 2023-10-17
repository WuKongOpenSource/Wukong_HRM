<template>
  <slide-view
    v-loading="loading"
    :class="{'slide-fixed-view': config.isFixed}"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    :show-close="config.closeShow"
    @afterEnter="afterEnter"
    @close="close">
    <div
      v-if="detail && !errorShow"
      class="business-info"
      :style="config.style">
      <flexbox class="business-info__header">
        <div class="title">{{ detail.name }}</div>
        <div class="handle">
          <template
            v-if="config.fillBtnShow">
            <el-button
              type="primary"
              @click="fillClick">回填</el-button><i
                class="wk wk-icon-fill-help wk-help-tips"
                data-type="8"
                style="margin-right: 8px;"
                data-id="342" />
          </template>
          <el-button
            @click="updateClick">更新</el-button><i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="8"
            data-id="343" />
        </div>
      </flexbox>

      <!-- 摘要 -->
      <div v-if="contactDetail" class="abstract">
        <div
          v-for="(item, index) in abstractList"
          :key="index"
          class="cell text-one-line">
          <span class="label">{{ item.label }}</span>
          <span class="value">{{ contactDetail[item.field] || detail[item.field] }}</span>
        </div>
      </div>

      <el-tabs
        v-model="activeTab"
        lazy>
        <el-tab-pane name="overview">
          <template slot="label">
            企业概况<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="8"
              data-id="341" />
          </template>
          <basic-info
            :key="updateKey"
            :name="name"
            :data="detail" />
        </el-tab-pane>
      </el-tabs>
    </div>
    <el-empty
      v-if="errorShow && canUse"
      :image="require('@/assets/img/empty/data.png')"
      description="该客户工商信息不存在" />
    <el-empty
      v-if="errorShow && !canUse"
      style="padding-top: 20%;"
      :image="require('@/assets/img/empty/search.png')"
      description="工商查询次数已用完，如果需要继续使用，请及时购买。">
      <el-button type="primary" @click="payShow = true">立即购买</el-button>
    </el-empty>
  </slide-view>
</template>

<script>
import {
  crmEnterpriseBasicInfoAPI,
  crmEnterpriseContactInfoAPI,
  crmEnterpriseCleanCacheAPI
} from '@/api/premium/businessInfo'

import SlideView from '@/components/SlideView'
import BasicInfo from './components/BasicInfo'

import { isArray } from '@/utils/types'
import merge from '@/utils/merge'
import { getBaiduMap } from '@/utils'
import { mapGetters } from 'vuex'

const DefaultProps = {
  fillBtnShow: true, // 填充按钮展示
  isFixed: true, // 是侧滑详情默认是
  closeShow: true, // 关闭按钮是否展示
  style: {} // 样式
}

export default {
  // 工商查询
  name: 'WkBusinessInfoView',

  components: {
    SlideView,
    BasicInfo
  },

  props: {
    name: String,
    module: {
      type: String,
      default: 'customer'
    }, // 回填模块 线索 客户
    form: Object, // 回填对象
    fields: Array, // 字段对象，填充数据要判断类型
    props: Object,
    fillFun: Function // 回填方法
  },

  data() {
    return {
      loading: false,
      errorShow: false,
      detail: null,
      contactDetail: null,
      // 摘要
      abstractList: this.getAbstracts(),
      activeTab: 'overview',
      updateKey: Date.now().toString(),
      payShow: false
    }
  },

  computed: {
    ...mapGetters([
      'moduleData'
    ]),
    // 如果有次数就可以使用
    canUse() {
      const enterprise = this.moduleData.find(item => item.module === 'enterprise' && item.status === 1)
      return enterprise ? enterprise.number > 0 : false
    },
    config() {
      return merge({ ...DefaultProps }, this.props || {})
    }
  },

  watch: {},

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 页面滑出
     * @param {*}
     * @return {*}
     */
    afterEnter() {
      this.getDetail()
      this.getContactInfo()
    },

    /**
     * @description: 获取详情
     * @param {*}
     * @return {*}
     */
    getDetail() {
      this.loading = true
      crmEnterpriseBasicInfoAPI(this.name).then(res => {
        this.detail = res.data
        this.loading = false
        this.errorShow = !this.detail
      }).catch(() => {
        this.errorShow = true
        this.loading = false
      })
    },

    /**
     * @description: 获取联系信息
     * @param {*}
     * @return {*}
     */
    getContactInfo() {
      this.loading = true
      crmEnterpriseContactInfoAPI(this.name).then(res => {
        this.contactDetail = res.data
        this.loading = false
        this.errorShow = !this.contactDetail
      }).catch(() => {
        this.errorShow = true
        this.loading = false
      })
    },

    /**
     * @description: 回填回填信息包括基本信息（客户名称"customerName"、手机号"mobile"、电话"telephone"、邮箱"email"、网址、客户行业、地址）
                     客户在后台设置显示工商注册号、法定代表人、统一社会信用代码、组织机构代码、
                     企业类型、营业期限、注册资本字段时，且所查询企业的工商信息包含这些信息，回填显示。
     * @param {*}
     * @return {*}
     */
    async fillClick() {
      if (this.fillFun) {
        this.fillFun(this.detail, this.contactDetail)
        return
      }

      const address = this.getFillValue(this.contactDetail.address)

      // 常规字段
      if (this.module === 'leads') {
        this.$set(this.form, 'leadsName', this.getFillValue(this.detail.name))
        this.$set(this.form, 'address', [
          { id: 4, name: address, code: '' }
        ])
      } else {
        this.$set(this.form, 'customerName', this.getFillValue(this.detail.name))
      }
      this.$set(this.form, 'mobile', this.getFillValue(this.contactDetail.telephone))
      this.$set(this.form, 'telephone', this.getFillValue(this.contactDetail.telephone))
      this.$set(this.form, 'email', this.getFillValue(this.contactDetail.email))

      // 客户地址
      if (this.module === 'customer' && address) {
        await getBaiduMap()
        // 创建地址解析器实例
        var myGeo = new BMap.Geocoder()
        myGeo.getPoint(address, (point) => {
          if (point) {
            myGeo.getLocation(point, (result) => {
              if (result) {
                this.$set(this.form, 'mapAddress', {
                  address: [
                    result.addressComponents.province,
                    result.addressComponents.city,
                    result.addressComponents.district
                  ],
                  location: address,
                  detailAddress: address,
                  lat: point.lat,
                  lng: point.lng
                })
              } else {
                this.$set(this.form, 'mapAddress', {
                  address: [],
                  location: address,
                  detailAddress: address,
                  lat: point.lat,
                  lng: point.lng
                })
              }
            })
          } else {
            this.$set(this.form, 'mapAddress', {
              address: [],
              location: address,
              detailAddress: address,
              lat: point.lat,
              lng: point.lng
            })
          }
        })
      }

      // 自定义字段填充
      const supportFields = [{
        name: '工商注册号',
        formType: 'text',
        field: 'regNo'
      }, {
        name: '法定代表人',
        formType: 'text',
        field: 'operName'
      }, {
        name: '统一社会信用代码',
        formType: 'text',
        field: 'creditNo'
      }, {
        name: '组织机构代码',
        formType: 'text',
        field: 'orgNo'
      }, {
        name: '企业类型',
        formType: 'text',
        field: 'econKind'
      }, {
        name: '营业期限',
        formType: ['text', 'date_interval'],
        field: ['termStart', 'termEnd']
      }, {
        name: '注册资本',
        formType: 'text',
        field: 'registCapi'
      }]
      const supportFieldNames = supportFields.map(item => item.name)
      this.fields.forEach(item => {
        if (supportFieldNames.includes(item.name)) {
          const supportField = supportFields.find(sItem => sItem.name === item.name)
          if (supportField) {
            if (isArray(supportField.formType) && supportField.formType.includes(item.formType)) {
              const list = []
              const termStart = this.detail.termStart ? this.detail.termStart.split(' ')[0] : ''
              list.push(termStart || '')
              const termEnd = this.detail.termEnd ? this.detail.termEnd.split(' ')[0] : ''
              list.push(termEnd || '')
              if (item.formType === 'text') {
                this.$set(this.form, item.fieldName, list.join('-'))
              } else { // 以数组处理
                this.$set(this.form, item.fieldName, list)
              }
            } else if (supportField.formType === item.formType) {
              this.$set(this.form, item.fieldName, this.getFillValue(this.detail[supportField.field]))
            }
          }
        }
      })

      this.close()
    },

    /**
     * @description: 主要滤掉-
     * @param {*}
     * @return {*}
     */
    getFillValue(value) {
      return value === '-' ? '' : value
    },

    /**
     * @description: 更新
     * @param {*}
     * @return {*}
     */
    updateClick() {
      this.loading = true
      crmEnterpriseCleanCacheAPI(this.name).then(res => {
        this.afterEnter()
        this.updateKey = Date.now().toString() // 重载相关详情
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 获取摘要展示字段
     * @param {*}
     * @return {*}
     */
    getAbstracts() {
      return [{
        label: '法定代表人:',
        field: 'operName'
      }, {
        label: '统一社会信用代码:',
        field: 'creditNo'
      }, {
        label: '注册资本:',
        field: 'registCapi'
      }, {
        label: '地址:',
        field: 'address'
      }, {
        label: '电话:',
        field: 'telephone'
      }, {
        label: '邮箱:',
        field: 'email'
      }]
    },

    /**
     * @description: 关闭
     * @param {*}
     * @return {*}
     */
    close() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.business-info {
  height: 100%;
  padding: 32px;
  overflow-y: auto;
  background-color: white;

  &__header {
    .title {
      display: flex;
      flex: 1;
      overflow: hidden;
      font-size: $--font-size-xxlarge;
      font-weight: $--font-weight-bold;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
    }

    .handle {
      display: inline-flex;
      flex-shrink: 0;
      line-height: 32px;
    }
  }

  // 摘要
  .abstract {
    padding: 16px 24px 8px;
    margin-top: 16px;
    background-color: $--color-n20;
    border-radius: $--border-radius-base;

    .cell {
      display: inline-block;
      width: 50%;
      margin-bottom: 8px;

      .label {
        color: $--color-text-secondary;
      }
    }
  }
}
</style>
