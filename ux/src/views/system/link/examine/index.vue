<template>
  <div
    v-if="label !=4"
    orient="vertical"
    class="detail-main">
    <flexbox
      direction="column"
      align="stretch"
      class="d-container">
      <wk-detail-header
        class="is-shadow"
        :subtitle="subTitleObj[label] || ''"
        :title="detailInfo[headerTitleObj[label]]"
      />
    </flexbox>
    <div class="d-container-body">
      <flexbox
        class="left-right-wrap is-hidden-right"
        align="stretch">
        <div class="left">
          <!-- 图片附件 -->
          <div class="img-accessory">
            <div class="img-box">
              <img
                v-for="(item, index) in detailInfo.img"
                :key="index"
                v-src="item.url"
                class="main-img"
                @click="previewImage(detailInfo.img, index)">
            </div>
            <div class="accessory">
              <file-cell
                :file-list="detailInfo.file"
                :show-time="false" />
            </div>
          </div>
          <!-- 相关信息 -->
          <div style="padding-right: 12px;">
            <related-business
              :show-foot="false"
              :show-add="false"
              :show-add-btn="false"
              :all-data="relatedBusinessInfo" />
          </div>

        </div>
        <div class="right">
          <examine-info-section
            :examine-record="examineRecord"
            :examine-type="'archive'"
            :external-mailbox="true"
            @success="getAllData()"
            @on-handle="examineHandle" />
        </div>
      </flexbox>
    </div>
  </div>

  <hrm-examine
    v-else
    :email-token="emailToken" />
</template>

<script>

import {
  examineSuperExaminesUseExamineEmailTokenForExamineRecordListAPI,
  oaExamineUseExamineEmailTokenForOaExamineInfoAPI,
  oaExamineUseExamineEmailTokenForOaExaminePositionInfoAPI,
  ExamineEmailTokenForExaminePositionInfoAPI,
  ExamineEmailTokenForExamineInfoAPI,
  useExamineEmailTokenByIdAPI,
  useExamineEmailTokenForInformationAPI
} from '@/api/examine/superExamine'

import WkDetailHeader from '@/components/Page/WkDetailHeader'
import ExamineInfoSection from '@/components/Examine/ExamineInfoSection'
import FileCell from '@/components/FileCell'
import RelatedBusiness from '@/components/RelatedBusiness'

import HrmExamine from './hrmExamine'

import { setHeadDetails, setFieldList } from './index.js'
export default {
  /** 详情 */
  name: 'Detail',
  components: {
    WkDetailHeader,
    ExamineInfoSection,
    FileCell,
    RelatedBusiness,
    HrmExamine
  },
  mixins: [],
  props: {
    examineType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      detail: null,
      titleObj: {
        upcoming: '待办',
        track: '跟踪',
        archive: '归档',
        draft: '草稿'
      },
      headDetails: [],
      examineRecord: {},
      fieldList: [],
      detailInfo: {},

      subTitleObj: {
        5: '采购订单',
        6: '采购退货单',
        7: '销售订单',
        8: '销售退货单',
        9: '付款单',
        10: '回款单',
        11: '库存盘点',
        12: '库存调拨'
      },

      headerTitleObj: {
        0: 'content',
        1: 'content',
        2: 'content',
        3: 'content',
        5: 'orderNumber',
        6: 'orderNumber',
        7: 'orderNumber',
        8: 'orderNumber',
        9: 'paymentNo',
        10: 'collectionNo',
        11: 'inventoryNumber',
        12: 'allocationNumber'
      },

      emailToken: '',

      label: null
    }
  },
  computed: {
    relatedBusinessInfo() {
      return {
        business: this.detailInfo.businessList,
        contacts: this.detailInfo.contactsList,
        contract: this.detailInfo.contractList,
        customer: this.detailInfo.customerList
      }
    }
  },
  mounted() {
    this.label = Number(this.$route.query.label)
    this.emailToken = this.$route.query.k
    if (this.label != 4) {
      this.getAllData()
    }
  },
  methods: {
    getAllData() {
      const params = { emailToken: this.$route.query.k }

      const fieldListRequestObj = {
        0: oaExamineUseExamineEmailTokenForOaExaminePositionInfoAPI,
        1: ExamineEmailTokenForExaminePositionInfoAPI,
        2: ExamineEmailTokenForExaminePositionInfoAPI,
        3: ExamineEmailTokenForExaminePositionInfoAPI,
        5: useExamineEmailTokenForInformationAPI,
        6: useExamineEmailTokenForInformationAPI,
        7: useExamineEmailTokenForInformationAPI,
        8: useExamineEmailTokenForInformationAPI,
        9: useExamineEmailTokenForInformationAPI,
        10: useExamineEmailTokenForInformationAPI,
        11: useExamineEmailTokenForInformationAPI,
        12: useExamineEmailTokenForInformationAPI
      }

      const detailInfoRequestObj = {
        0: oaExamineUseExamineEmailTokenForOaExamineInfoAPI,
        1: ExamineEmailTokenForExamineInfoAPI,
        2: ExamineEmailTokenForExamineInfoAPI,
        3: ExamineEmailTokenForExamineInfoAPI,
        5: useExamineEmailTokenByIdAPI,
        6: useExamineEmailTokenByIdAPI,
        7: useExamineEmailTokenByIdAPI,
        8: useExamineEmailTokenByIdAPI,
        9: useExamineEmailTokenByIdAPI,
        10: useExamineEmailTokenByIdAPI,
        11: useExamineEmailTokenByIdAPI,
        12: useExamineEmailTokenByIdAPI
      }

      Promise.all([
        examineSuperExaminesUseExamineEmailTokenForExamineRecordListAPI(params),
        fieldListRequestObj[this.label](params),
        detailInfoRequestObj[this.label](params)
      ]).then(res => {
        const [examineRecordList, examinePositionInfo, examineInfo] = res

        // 头部字段
        this.detailInfo = examineInfo['data']
        this.headDetails = setHeadDetails(examineInfo['data'], Number(this.label))

        // 详情显示
        const fieldList = examinePositionInfo['data']
        this.fieldList = setFieldList(fieldList, this.label, examineInfo['data'])

        // 审批记录列表
        this.examineRecord = examineRecordList['data']
      })
    },
    /**
     * 审核操作
     */
    examineHandle(data) {

    }
  }
}
</script>

<style lang="scss" scoped>
@import "./content.scss";

.detail-main {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 24px;
  overflow-y: auto;
  overflow-y: overlay;
}

.d-view {
  position: fixed;
  top: $--detail-view-top;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
  background-color: white;

  ::v-deep .el-card__body {
    height: 100%;
  }
}

.detail-header {
  .header-icon {
    width: 36px;
    height: 36px;
    margin-right: 8px;
    line-height: 36px;
    text-align: center;
    border-radius: $--border-radius-base;

    .wk {
      font-size: 26px;
      color: white;
    }
  }

  .header-name {
    flex: 1;
    font-size: 16px;
    font-weight: 600;
  }
}

.d-container-body {
  flex: 1;
  margin-top: 8px;
  overflow: auto;

  .detail-head-base {
    padding: 16px;
    background-color: $--color-n20;
    border-radius: 3px;

    ::v-deep .base-item {
      flex: 0 0 25%;
    }
  }

  .left-right-wrap {
    margin-top: 15px;

    .left {
      flex: 1;
      padding-right: 40px;

      // 图片 附件
      .img-accessory {
        padding: 0 12px 0 20px;
        margin-bottom: 5px;
        margin-bottom: 10px;
        font-size: 12px;

        .img-box {
          position: relative;
          margin-top: 40px;

          img {
            width: 200px;
            cursor: pointer;
          }
        }

        .accessory {
          margin-top: 25px;
          margin-bottom: 20px;
          color: #2362fb;

          .wukong-file {
            font-size: 13px;
          }
        }
      }

      .b-cont {
        height: unset;
      }

      // 基本信息
      .detail-head-base {
        padding: #{$--interval-base * 2};
        margin-bottom: 16px;
        background-color: $--color-n20;
        border-radius: $--border-radius-base;

        ::v-deep .base-item {
          flex: 0 0 25%;
        }
      }
    }

    .right {
      flex-shrink: 0;
      width: 280px;
    }
  }
}
</style>

