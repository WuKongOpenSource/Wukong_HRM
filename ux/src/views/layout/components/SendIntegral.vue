<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-23 13:58:38
 * @LastEditTime: 2023-08-11 16:07:44
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :append-to-body="true"
    :close-on-click-modal="false"
    :visible="show"
    class="active"
    @close="hiddenView">
    <div class="top-wrap">
      <img src="@/assets/login/dialogebg.png" alt="" class="pic">
      <div class="top-txt">
        <div class="block">
          <div class="top-title">邀请好友注册，赚积分赢好礼</div>
          <div class="line" />
          <div class="top-mtitle">
            您可以通过 <i class="tip">企业专属邀请码、二维码、邀请链接</i>  任意一种形式来邀请好友注册悟空CRM。 <br> 注册成功，即可获得悟空积分
          </div>
        </div>
      </div>
    </div>
    <div class="three-wrap">
      <div class="main-wrap">
        <div class="base left">
          <div class="sup">
            <span class="blue">
              <div class="copy-txt" style="margin-bottom: 10px;">{{ codeInfo.sourceCode || '' }}</div>
              <el-button
                v-clipboard:copy="codeInfo.sourceCode || ''"
                v-clipboard:success="clipboardSuccess">复制
              </el-button>
            </span>
            <!-- <span class="btn">复制</span> -->
          </div>
          <div class="sub">企业邀请码</div>
        </div>
        <div class="base middle">
          <div class="sup">
            <img src="@/assets/login/ma.png" alt="" class="picture">
            <div class="er-code">
              <div class="down-code">
                <div id="canvas" />
                <el-button style="margin-top: 10px;" @click="handleClick">下载二维码</el-button>
              </div>
            </div>
          </div>
          <div class="sub">企业二维码</div>
        </div>
        <div class="base right">
          <div class="sup">
            <span class="gray-blue">
              <div class="copy-txt" style="margin-bottom: 10px;">{{ codeInfo.sourceUrl || '' }}</div>
              <el-button
                v-clipboard:copy="codeInfo.sourceUrl || ''"
                v-clipboard:success="clipboardSuccess">复制</el-button>
            </span>
            <!-- <span class="btn">复制</span> -->
          </div>
          <div class="sub">企业专属邀请链接</div>
        </div>
      </div>
    </div>
    <div class="progress">
      <div class="btitle-wrap"><i class="blue-line" />每邀请1人，可获得10积分 <i class="blue-line" /> </div>
      <div class="stitle-wrap">
        <div class="label">可用积分： <i class="num">{{ numInfo.usable || 0 }}</i> 分</div>
        <div class="label">总邀请人数：<i class="num">{{ numInfo.num || 0 }}</i> 人</div>
        <div class="label">本月邀请人数：<i class="num">{{ numInfo.mouthNum || 0 }}</i> 人</div>
      </div>
    </div>

    <div class="integral-tip">
      <div class="title">
        <div class="item-title">初级推荐官</div>
        <div class="item-title" style="left: 23.5%;">中级推荐官</div>
        <div class="item-title" style="left: 47%;">
          <span>高级推荐官</span>
          <el-tooltip class="item" effect="dark" content="达到高级推荐额外赠送100积分" placement="top">
            <img src="@/assets/login/wen.png" alt="" class="wen">
          </el-tooltip>
        </div>
        <div class="item-title" style="left: 70%;">
          <span>资深推荐官</span>
          <el-tooltip class="item" effect="dark" content="达到资深推荐额外赠送200积分" placement="top">
            <img src="@/assets/login/wen.png" alt="" class="wen">
          </el-tooltip>
        </div>
        <div class="item-title" style="left: 91%;white-space: nowrap;">王者推荐官</div>
      </div>
      <div class="integral-progress">
        <div v-for="(item,index) in 5" :key="index" :style="{ left: getLeft(index), backgroundColor: getColor(index) }" class="line-dot" />
        <div :style="{ width: getWidth() }" class="active-blue" />
        <div class="all-line" />
      </div>
      <div class="integral-line">
        <div class="item"> <img src="@/assets/login/icon.png" alt="" class="icon"><span> 10积分</span></div>
        <div class="item" style="left: 23.5%;"> <img src="@/assets/login/icon.png" alt="" class="icon"> <span>300积分</span></div>
        <div class="item" style="left: 47%;"> <img src="@/assets/login/icon.png" alt="" class="icon"> <span>800积分</span></div>
        <div class="item" style="left: 70%;"> <img src="@/assets/login/icon.png" alt="" class="icon"> <span>1500积分</span></div>
        <div class="item" style="left: 91%;"> <img src="@/assets/login/icon.png" alt="" class="icon"> <span style="flex-shrink: 0;">3000积分</span></div>
      </div>
    </div>

    <div class="list">
      <div class="nav">
        <div v-for="(item, index) in itemList" :key="index" :class="{ 'active': index == defaults }" class="item" @click="toggle(index)">{{ item.label }}</div>
      </div>

      <div ref="listwrap" class="card-wrap">
        <div v-if="defaults == 0" ref="cardlist" class="empty-wrap">
          <div v-for="(item,index) in cardList" :key="index" class="item">
            <img :src="item.src" alt="" class="img">
            <div class="dtitle">{{ item.title }}</div>
            <div class="space-end">
              <div class="left">
                <div class="shang">
                  <img src="@/assets/login/icon.png" alt="" class="icon">
                  <span class="nums">{{ item.num }}积分</span>
                </div>
                <div class="xia">
                  已兑换次数：{{ item.times }}次
                </div>
              </div>
              <div v-if="numInfo.usable >= item.num" class="right" @click="getOther(item)">兑换</div>
              <div v-if="numInfo.usable < item.num" class="right no">兑换</div>
            </div>
          </div>
        </div>

        <div v-if="defaults == 1 || defaults == 2" class="crm-container">
          <el-table
            :data="list"
            :height="tableHeight"
            stripe
            style="width: 100%;margin-top: 10px;border: 1px solid #e6e6e6;">
            <el-table-column
              v-for="(item, index) in fieldList"
              :key="index"
              :prop="item.prop"
              :label="item.label"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <span v-if="item.prop == 'cut'" style="font-weight: 600;color: red;">-{{ scope.row.cut }}</span>
                <span v-else-if="item.prop == 'integral'" style="font-weight: 600;color: #2361fb;">+{{ scope.row.integral }}</span>
                <span v-else>{{ scope.row[item.prop] }}</span>
              </template>

            </el-table-column>
          </el-table>

          <div class="p-contianer">
            <el-pagination
              :current-page="currentPage"
              :page-sizes="pageSizes"
              :page-size.sync="pageSize"
              :total="total"
              :pager-count="5"
              class="p-bar"
              background
              layout="prev, pager, next, sizes, total, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange" />
          </div>
        </div>

      </div>
    </div>
  </el-dialog>
</template>

<script>
import Lockr from 'lockr'
import Qrcode from '@chenfengyuan/vue-qrcode'
import clipboard from '@/directives/clipboard/index.js' // use clipboard by v-directive
import { crmGetCodeNumAPI, crmGetProductListAPI, crmChangeIntegralAPI, crmPleaseRecordAPI, crmExchangeHistoryAPI } from '@/api/integral'
import QRCode from 'qrcodejs2'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'SendIntegral',
  directives: {
    clipboard
  },
  components: {
    Qrcode
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      defaults: 0,
      tableHeight: '400px',

      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      list: [],
      fieldList: [],
      recordList: [
        { label: '用户名', prop: 'companyName' },
        { label: '手机号', prop: 'phone' },
        { label: '注册时间', prop: 'createTime' },
        { label: '积分', prop: 'integral' }
      ],
      exchangeList: [
        { label: '产品', prop: 'productName' },
        { label: '兑换时间', prop: 'createTime' },
        { label: '消耗积分', prop: 'cut' }
      ],
      // 二维码
      qrcodeOption: {
        width: 500,
        height: 500,
        colorDark: '#000000',
        colorLight: '#ffffff'
      },
      qrcode: null,
      // qrcodeOption1: {
      //   width: 100,
      //   height: 100,
      //   colorDark: '#000000',
      //   colorLight: '#ffffff'
      // },
      codeInfo: {},
      numInfo: {},
      itemList: [
        { label: '积分中心' },
        { label: '邀请记录' },
        { label: '已兑换' }
      ],
      cardList: [
        { src: require('@/assets/login/card1.png'), title: '100元代金券', num: 100, times: 0 },
        { src: require('@/assets/login/card2.png'), title: '200元代金券', num: 200, times: 0 },
        { src: require('@/assets/login/card3.png'), title: '500元代金券', num: 500, times: 0 },
        { src: require('@/assets/login/card4.png'), title: '10000元代金券', num: 500, times: 0 },
        { src: require('@/assets/login/card5.png'), title: '20000元代金券', num: 1000, times: 0 },
        { src: require('@/assets/login/card6.png'), title: '30000元代金券', num: 1500, times: 0 },
        { src: require('@/assets/login/card7.png'), title: '100元京东卡', num: 1000, times: 0 },
        { src: require('@/assets/login/card8.png'), title: '300元京东卡', num: 3000, times: 0 },
        { src: require('@/assets/login/card9.png'), title: '500元京东卡', num: 5000, times: 0 }
      ]
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.getCodeNum()
        this.getProductList()
      }
    }
  },
  mounted() {
    if (this.codeInfo.sourceUrl) {
      this.getDetail()
    }
  },
  methods: {

    // 获取基础信息
    getDetail() {
      if (this.qrcode) {
        this.qrcode.clear()
        this.qrcode.makeCode(this.codeInfo.sourceUrl)
      } else {
        this.qrcode = new QRCode(document.getElementById('canvas'), {
          text: this.codeInfo.sourceUrl,
          width: 200,
          height: 200,
          colorDark: '#000000',
          colorLight: '#ffffff',
          correctLevel: QRCode.CorrectLevel.M
        })
      }
    },
    /** 获取二维码与数量 */
    getCodeNum() {
      this.loading = true
      crmGetCodeNumAPI().then(res => {
        const data = res.data || {}
        this.codeInfo = data.code
        this.numInfo = data
        this.loading = false
        this.getDetail()
      }).catch(() => {
        this.loading = false
      })
    },

    /** 获取产品列表 */
    getProductList() {
      crmGetProductListAPI().then(res => {
        res.data.map((item, index) => {
          this.cardList[index].times = item.num
          this.cardList[index].id = item.id
          return item
        })
      }).catch(() => {
        this.loading = false
      })
    },

    /** 兑换 */
    getOther(item) {
      var tip = `确认要兑换“${item.title}（仅限购买云平台使用）”，兑换需要消耗${item.num}积分,兑换成功后积分不支持退回。`
      this.$confirm(tip, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        crmChangeIntegralAPI({ id: item.id }).then(res => {
          this.$message({
            type: 'success',
            message: '兑换成功!'
          })
          this.getCodeNum()
          this.getProductList()
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }).catch(() => {
      })
    },

    /** 已兑换 */
    getHistoryList() {
      this.loading = true
      var params = {
        page: this.currentPage,
        limit: this.pageSize
      }
      crmExchangeHistoryAPI(params).then(res => {
        this.list = res.data.list.map(item => {
          item.cut = item.integral
          return item
        })
        if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
          this.currentPage = this.currentPage - 1
          this.getHistoryList()
        } else {
          this.total = res.data.totalRow
          this.loading = false
        }

        this.$nextTick(() => {
          document.querySelector('.el-table__body-wrapper').scrollTop = 1
        })
        // this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /** 邀请记录 */
    getRegord() {
      var params = {
        page: this.currentPage,
        limit: this.pageSize
      }
      crmPleaseRecordAPI(params).then(res => {
        this.list = res.data.list
        if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
          this.currentPage = this.currentPage - 1
          this.getRegord()
        } else {
          this.total = res.data.totalRow
          this.loading = false
        }

        this.$nextTick(() => {
          document.querySelector('.el-table__body-wrapper').scrollTop = 1
        })
        // this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /** 积分进度 */
    getWidth() {
      var percent = null
      var base = null
      var sum = null
      if (this.numInfo.integral >= 3000) {
        return '93.5%'
      } else if (this.numInfo.integral >= 1500) {
        base = 71.5
        percent = ((this.numInfo.integral - 1500) / 1500) * 22
        sum = (base + percent) + '%'
        return sum
      } else if (this.numInfo.integral >= 800) {
        base = 47.5
        percent = ((this.numInfo.integral - 800) / 700) * 24
        sum = (base + percent) + '%'
        return sum
      } else if (this.numInfo.integral >= 300) {
        base = 24.5
        percent = ((this.numInfo.integral - 300) / 500) * 23
        sum = (base + percent) + '%'
        return sum
      } else if (this.numInfo.integral >= 10) {
        base = 0.5
        percent = ((this.numInfo.integral - 10) / 290) * 24
        sum = (base + percent) + '%'
        return sum
      } else {
        return '0%'
      }
    },

    handleClick(type) {
      // 获取base64的图片节点
      var img = document.getElementById('canvas').getElementsByTagName('img')[0]
      // 构建画布
      var canvas = document.createElement('canvas')
      canvas.width = 200
      canvas.height = 200
      canvas.getContext('2d').drawImage(img, 0, 0)
      // 构造url
      const url = canvas.toDataURL('image/png')
      // 构造a标签并模拟点击
      var downloadLink = document.createElement('a')
      downloadLink.setAttribute('href', url)
      downloadLink.setAttribute('download', '二维码.png')
      document.body.appendChild(downloadLink)
      downloadLink.click() // 点击下载
      document.body.removeChild(downloadLink)
    },

    // 更改每页展示数量
    handleSizeChange(val) {
      this.pageSize = val
      if (this.defaults == 1) {
        this.getRegord()
      } else if (this.defaults == 2) {
        this.getHistoryList()
      }
    },

    // 更改当前页数
    handleCurrentChange(val) {
      this.currentPage = val
      if (this.defaults == 1) {
        this.getRegord()
      } else if (this.defaults == 2) {
        this.getHistoryList()
      }
    },

    hiddenView() {
      this.$emit('close')
    },

    toggle(index) {
      this.defaults = index
      if (index == 0) {
        this.getProductList()
      } else if (index == 1) {
        this.list = []
        this.currentPage = 1
        this.pageSize = 15
        this.fieldList = this.recordList
        this.getRegord()
      } else if (index == 2) {
        this.list = []
        this.currentPage = 1
        this.pageSize = 15
        this.fieldList = this.exchangeList
        this.getHistoryList()
      }
    },

    /**
     * 复制地址
     */
    clipboardSuccess() {
      this.$message.success('复制成功')
    },

    getLeft(index) {
      if (index == 0) {
        return '2.5%'
      } else if (index == 1) {
        return '26.5%'
      } else if (index == 2) {
        return '49.5%'
      } else if (index == 3) {
        return '73.5%'
      } else if (index == 4) {
        return '95%'
      }
    },

    getColor(index) {
      if (this.numInfo.integral >= 3000) {
        return '#2361FB'
      } else if (this.numInfo.integral >= 1500) {
        if (index == 0 || index == 1 || index == 2 || index == 3) {
          return '#2361FB'
        } else {
          return '#E5EBF3'
        }
      } else if (this.numInfo.integral >= 800) {
        if (index == 0 || index == 1 || index == 2) {
          return '#2361FB'
        } else {
          return '#E5EBF3'
        }
      } else if (this.numInfo.integral >= 300) {
        if (index == 0 || index == 1) {
          return '#2361FB'
        } else {
          return '#E5EBF3'
        }
      } else if (this.numInfo.integral >= 10) {
        if (index == 0) {
          return '#2361FB'
        } else {
          return '#E5EBF3'
        }
      } else {
        return '#E5EBF3'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.el-dialog__wrapper ::v-deep .el-dialog {
  width: 50%;
  min-width: 740px;
  height: 85%;
  margin-top: 8.5vh !important;
  overflow: hidden;

  .el-dialog__body {
    height: 100%;
    padding: 0 !important;
    margin-top: -30px;
    overflow: hidden;
    overflow-y: auto !important;
  }
}

.active ::v-deep .el-dialog .el-dialog__header .el-dialog__headerbtn {
  z-index: 999 !important;
}

.qrcode {
  width: 40px !important;
  height: 40px !important;
}

.picture {
  width: 40px;
  height: 40px;
}

.middle {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.middle:hover .er-code {
  display: block !important;
}

.integral-tip {
  width: 90%;
  margin: 0 auto;
  margin-top: 45px;

  .title {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 30px;

    .item-title {
      position: absolute;
      top: 0;
      left: 0;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      font-size: 12px;
      color: $--color-text-primary;

      .wen {
        width: 15px;
        margin-left: 5px;
      }
    }
  }

  .integral-progress {
    position: relative;
    width: 100%;
    height: 20px;

    .active-blue {
      position: absolute;
      top: 0;
      left: 2.5%;
      z-index: 9;
      height: 4px;
      background: #2361fb;
      border-radius: 2px;
    }

    .all-line {
      position: absolute;
      top: 0;
      left: 2.5%;
      z-index: 8;
      width: 93% !important;
      width: 100%;
      height: 4px;
      background: #e5ebf3;
      border-radius: 2px;
    }

    .line-dot {
      position: absolute;
      top: -3px;
      z-index: 999;
      width: 10px;
      height: 10px;
      background: #e5ebf3;
      border-radius: 50%;
    }
  }

  .integral-line {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 25px;

    .item {
      position: absolute;
      top: 0;
      left: 0;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      font-size: 12px;
      color: #656565;

      .icon {
        width: 15px;
        margin-right: 5px;
      }
    }
  }
}

.top-wrap {
  position: relative;
  width: 100%;

  .pic {
    display: block;
    width: 100%;
  }

  .top-txt {
    position: absolute;
    top: calc(50% - 80px);
    left: 0;
    width: 100%;

    .block {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: flex-start;
      width: 100%;

      .top-title {
        font-size: 24px;
        font-weight: 600;
        color: $--color-text-primary;
      }

      .line {
        width: 36px;
        height: 2px;
        margin-top: 10px;
        margin-bottom: 20px;
        background: rgba(35, 97, 251, 1);
      }

      .top-mtitle {
        font-size: 14px;
        line-height: 26px;
        color: #656565;
        text-align: center;

        .tip {
          font-style: normal;
          font-weight: 600;
          color: $--color-text-primary;
        }
      }
    }
  }
}

.three-wrap {
  width: 100%;
  padding-top: 30px;
  padding-bottom: 30px;
  background: #f7f7f9;

  .main-wrap {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 80%;
    margin: 0 auto;

    .base {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: calc(100% / 3);

      .sup {
        height: 80px;

        .er-code {
          position: absolute;
          top: 60%;
          left: calc(50% - 125px);
          z-index: 999;
          display: none;
          width: 250px;
          padding-top: 25px;
          padding-bottom: 25px;
          background: #fff;

          .down-code {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: flex-start;
          }
        }

        .blue {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          font-weight: 800;
          color: $--color-primary;
        }

        .gray-blue {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          color: $--color-text-primary;
        }

        .btn {
          padding: 3px 5px;
          margin-left: 15px;
          color: $--color-primary;
          cursor: pointer;
          border: 1px solid $--color-primary;
          border-radius: 4px;
        }
      }

      .sub {
        margin-top: 20px;
        font-size: 14px;
        color: $--color-text-primary;
      }
    }
  }
}

.progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  margin: 0 auto;

  .btitle-wrap {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 400px;
    margin-top: 70px;
    font-size: 18px;
    font-weight: 800;
    color: $--color-primary;

    .blue-line {
      display: inline-block;
      width: 68px;
      height: 1px;
      background: rgba(35, 97, 251, 1);
    }
  }

  .stitle-wrap {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 400;

    .label {
      margin-top: 15px;
      margin-right: 10px;
      margin-left: 10px;
      font-size: 12px;
      color: #656565;

      .num {
        font-style: normal;
        font-weight: 600;
        color: $--color-primary;
      }
    }
  }
}

.active {
  color: $--color-primary !important;
  border-bottom: 2px solid $--color-primary !important;
}

.list {
  width: 90%;
  padding-bottom: 50px;
  margin: 0 auto;
  margin-top: 60px;

  .nav {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;

    .item {
      padding-bottom: 10px;
      margin-right: 30px;
      font-size: 15px;
      font-weight: 800;
      color: #656565;
      cursor: pointer;
      border-bottom: 2px solid #fff;
    }
  }

  .card-wrap {
    .empty-wrap {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-start;
      justify-content: space-between;

      .item {
        width: 30%;
        margin-bottom: 15px;

        .img {
          width: 100%;
        }

        .dtitle {
          margin-left: 15px;
          font-size: 16px;
          color: $--color-text-primary;
        }

        .space-end {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-right: 15px;
          padding-left: 15px;
          margin-top: 10px;

          .left {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            justify-content: flex-start;

            .shang {
              display: flex;
              align-items: center;
              justify-content: flex-start;
              margin-bottom: 10px;

              .icon {
                width: 12px;
                margin-right: 5px;
              }

              .nums {
                font-size: 12px;
                color: $--color-text-primary;
              }
            }

            .xia {
              font-size: 12px;
              color: $--color-text-secondary;
            }
          }

          .right {
            padding: 3px 5px;
            margin-left: 15px;
            font-size: 12px;
            color: $--color-primary;
            cursor: pointer;
            border: 1px solid $--color-primary;
            border-radius: 4px;
          }

          .no {
            color: #ccc !important;
            border: 1px solid #ccc !important;
          }
        }
      }
    }
  }
}
</style>

