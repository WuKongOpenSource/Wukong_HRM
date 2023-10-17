<template>
  <div
    v-if="!isEmpty"
    :class="[`is-${formType}`, {
      'is-common-type': isCommonType || isInline || isModule
    }]"
    class="wk-field-view"
  >
    <template v-if="ignoreFields.includes(props.field) || ignoreFields.includes(props.fieldName)">
      <slot :data="$props" />
    </template>
    <span v-else-if="props.fieldName === 'lastContent'" v-html="getCommonShowValue()" />
    <span v-else-if="isCommonType">{{ getCommonShowValue() }}</span>
    <el-switch
      v-else-if="formType == 'boolean_value'"
      :value="value"
      disabled
      active-value="1"
      inactive-value="0"
    />
    <wk-signature-image
      v-else-if="formType == 'handwriting_sign'"
      :src="value"
      :height="config.signatureHeight"
    />
    <wk-desc-text
      v-else-if="formType == 'desc_text'"
      :key="Date.now().toString()"
      :value="value"
    />
    <span
      v-else-if="formType == 'location'"
      :class="{'can-check':objectHasValue(value, 'address')}"
      @click.stop="mapViewShow=true"
    >{{ objectHasValue(value, 'address') ? value.address : '' }}</span>
    <span
      v-else-if="formType == 'website'"
      :class="{'can-check': !isEmpty}"
      @click.stop="openUrl(value)"
    >{{ value }}</span>
    <file-list-view
      v-else-if="formType == 'file'"
      :list="value || []"
    />
    <wk-detail-table-view
      v-else-if="formType == 'detail_table'"
      :show-type="props.precisions === 2 ? 'table' : 'default'"
      :title="props.name"
      :add-field-list="props.fieldExtendList"
      :field-form="value"
      :field-list="props.fieldList"
    >
      <template slot-scope="{ data }">
        <slot :data="data" />
      </template>
    </wk-detail-table-view>
    <!-- 分组标题 -->
    <wk-group
      v-else-if="formType == 'field_group'"
      :show-tips="false"
      :data="props" />
    <!-- 关注度 -->
    <el-rate
      v-else-if="formType == 'field_attention'"
      :value="Number(value || 0)"
      disabled />
    <!-- 标签 -->
    <tag-view
      v-else-if="formType == 'field_tag'"
      :item-bottom="0"
      :value="value" />
    <!-- 富文本 -->
    <div
      v-else-if="formType == 'rich_text_format'"
      v-html="value" />
    <!-- 图片 -->
    <wk-picture-preview
      v-else-if="formType == 'pic'"
      :wrap="props.wrap"
      :picture-url="value" />
    <template v-else>
      <slot :data="$props" />
    </template>

    <map-view
      v-if="mapViewShow"
      :title="value.address"
      :lat="value.lat"
      :lng="value.lng"
      @hidden="mapViewShow=false"
    />
  </div>
</template>

<script>
import WkSignatureImage from '@/components/NewCom/WkSignaturePad/Image'
import WkDescText from '@/components/NewCom/WkDescText'
import MapView from '@/components/MapView' // 地图详情
import FileListView from '@/components/FileListView' // 附件
import WkDetailTableView from '@/components/NewCom/WkDetailTable/View'
import WkGroup from '@/components/NewCom/WkGroup' // 分组标题
import TagView from '@/components/NewCom/WkTag/TagView' // 标签
import WkPicturePreview from '@/components/NewCom/WkPicturePreview' // 图片

import merge from '@/utils/merge'
import { isObject, isEmpty } from '@/utils/types'
import { getFormFieldShowName } from './utils'

const DefaultWkFieldView = {
  signatureHeight: '26px'
}

export default {
  // 特殊字段展示
  name: 'WkFieldView',

  components: {
    WkSignatureImage,
    WkDescText,
    MapView,
    FileListView,
    WkDetailTableView,
    WkGroup,
    TagView,
    WkPicturePreview
  },

  props: {
    props: Object, // 自定义字段参数信息
    formType: String,
    value: [String, Object, Array, Number],
    // 忽略的字段直接输出
    ignoreFields: {
      type: Array,
      default: () => {
        return []
      }
    }
  },

  data() {
    return {
      // 控制展示地图详情
      mapViewShow: false
    }
  },

  computed: {
    config() {
      return merge({ ...DefaultWkFieldView }, this.props || {})
    },
    isEmpty() {
      // 分组标题永远展示
      // 明细表格无数据也要展示内部字段
      return !['field_group', 'detail_table'].includes(this.formType) && isEmpty(this.value) && !(this.ignoreFields.includes(this.props.field) || this.ignoreFields.includes(this.props.fieldName))
    },
    isCommonType() {
      return [
        'text',
        'textarea',
        'select',
        'checkbox',
        'number',
        // 'floatnumber',
        'percent',
        'mobile',
        'email',
        'date',
        'datetime',
        'date_interval',
        'user',
        'structure',
        'position'
      ].includes(this.formType)
    },
    // 是模块
    isModule() {
      return [
        'customer',
        'business',
        'contract',
        'contacts'
      ].includes(this.formType)
    },

    // 是内联样式
    isInline() {
      return [
        'website',
        'category',
        'field_attention'
      ].includes(this.formType)
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
		 * 判断对象是否值
		 */
    objectHasValue(obj, key) {
      if (isObject(obj)) {
        return !isEmpty(obj[key])
      }
      return false
    },

    openUrl(url) {
      if (!url.match(/^https?:\/\//i)) {
        url = 'http://' + url
      }
      window.open(url)
    },

    /**
     * 获取类型的展示值
     */
    getCommonShowValue() {
      return getFormFieldShowName(this.formType, this.value, '', this.props)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-field-view {
  position: relative;
  overflow: hidden;
  text-overflow: ellipsis;

  .can-check {
    color: $--color-primary;
    cursor: pointer;
  }

  &.is-category,
  &.is-website {
    display: inline;
  }

  &.is-file {
    line-height: 1;
  }

  &.is-floatnumber {
    // text-align: right;
    color: $--color-y400;
  }

  &.is-handwriting_sign {
    height: 23px;

    .wk-signature-image {
      position: absolute;
    }
  }

  &.is-field_tag {
    white-space: normal;
  }
}
</style>
