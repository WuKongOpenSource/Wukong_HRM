<template>
  <div class="main">
    <div class="main-title">
      <div class="main-title-icon">
        <i class="wk wk-icon-business-info-solid main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">工商信息</div>
        <div class="main-title-information-content">
          通过查询客户的工商档案，基本上可以了解客户的整体情况，公司的开立主体，公司的股东，股东出资额及出资比例，公司注册资本，公司财务状况，公司年检情况等。有效避免和控制交易中的各种风险，创造相互信任的交易环境。
        </div>
      </div>
      <el-button type="primary" class="main-title-connect" @click="payClick()">立即购买</el-button>
    </div>
    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">2.3亿+企业主体查询</div>
          <div class="main-menu-items-content">
            全国亿级社会主体的全面数据信息，覆盖企业全维度，帮助客户深入了解所查企业全景信息，绘制客户全景画像。
          </div>
          <img src="./images/BusinessInformation/one.png" alt="" class="main-flow-items-img" style="width: 290px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">模糊搜索，快速响应</div>
          <div class="main-menu-items-content">
            您可以只输入企业名称的部分字段，系统则快速列举您与其相关的所有符合条件的企业名称，帮助销售快速输入客户信息，无论销售何时添加企业或查询，都将毫秒级响应，第一时间助其把握企业信息要点。
          </div>
          <img src="./images/BusinessInformation/two.png" alt="" class="main-flow-items-img" style="width: 290px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">电话获取，即刻联系</div>
          <div class="main-menu-items-content">
            悟空提供企业工商信息包括有效电话信息，使销售能够快速获取公司负责人联系方式，便于业务即刻开展，获取更多线索资源。
          </div>
          <img src="./images/BusinessInformation/three.png" alt="" class="main-flow-items-img" style="width: 290px;">
        </div>
        <div class="main-menu-all">
          <div class="main-menu-list">
            <p class="main-menu-list-title">初级版</p>
            <p class="main-menu-list-info">工商基础信息</p>
            <img src="./images/BusinessInformation/primary.png" alt="" class="main-menu-list-img">
            <ul class="main-menu-list-ul">
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>工商注册号
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>法定代表人
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>登记状态
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>成立日期
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>企业对外投资
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>统一社会信用代码
              </li>
            </ul>
          </div>
          <div class="main-menu-list">
            <p class="main-menu-list-title">中级版</p>
            <p class="main-menu-list-info">工商信息全面评估</p>
            <img src="./images/BusinessInformation/middle.png" alt="" class="main-menu-list-img">
            <ul class="main-menu-list-ul">
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>初级版信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>高管信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>股东信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>企业分支机构
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>变更信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>对外投资
              </li>
            </ul>
          </div>
          <div class="main-menu-list">
            <p class="main-menu-list-title">高级版</p>
            <p class="main-menu-list-info">工商信息进阶核验</p>
            <img src="./images/BusinessInformation/heigh.png" alt="" class="main-menu-list-img">
            <ul class="main-menu-list-ul">
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>初级版信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>中级版信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>企业风险
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>企业年报
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>招投标信息
              </li>
              <li class="main-menu-list-ul-li">
                <span class="wk green">&#xe721;</span>招聘信息
              </li>
            </ul>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【工商信息】使用申请。</p>
              <p>
                开通后，客户管理版块的【新建客户】处，在您输入有效的企业名称后，点击【<i class="wk wk-icon-lightning-solid flow-icon" />查看工商信息】即可。
              </p>
            </div>
            <img
              src="./images/BusinessInformation/first.png"
              alt=""
              class="main-flow-items-img">
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>
                客户管理版块的【客户详情】处：客户管理版块显示所有企业名称列表，点击任一企业名称即打开该客户的详情页面，页面右上方即客户名称后将出现相应的【<i class="wk wk-icon-lightning-solid flow-icon" />查看工商信息】，点击即可显示。
              </p>
            </div>
            <img
              src="./images/BusinessInformation/scend.png"
              alt=""
              class="main-flow-items-img">
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>
                若个别信息条目未显示则说明该企业的此部分信息未对外公开或缺少，不影响您对其他信息的使用。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第四步</div>
            <div class="main-flow-items-content">
              <p>企业客户的工商信息可不限次数重复查看。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>
                若开通基础版工商信息显示，则企业风险、企业年报、招投标信息与招聘信息等将显示灰色且不可点击。
              </p>
            </div>
            <img
              src="./images/BusinessInformation/five.png"
              alt=""
              class="main-flow-items-img">
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>

export default {
  name: 'BusinessInformation',
  data() {
    return {
      activeName: 'function',
      showPay: false
    }
  },
  methods: {
    /**
     * @description: 点击购买事件，工商查询的商品id是2
     * @param {*}
     * @return {*}
     */
    payClick() {
      this.$emit('payClick', 2)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style.scss";

.main-menu-all {
  display: flex;
  padding-left: 32px;

  .main-menu-list {
    position: relative;
    width: 240px;
    height: 288px;
    padding-top: 24px;
    margin: 24px 32px 147px 2px;
    margin-top: 24px;
    border: 2px solid $--color-n50;
    border-top: 3px solid $--color-primary;

    &-title {
      margin-bottom: 8px;
      font-size: 18px;
      color: $--color-primary;
      text-align: center;
    }

    &-info {
      font-size: 14px;
      color: $--color-n700;
      text-align: center;
    }

    &-img {
      position: absolute;
      top: -2px;
      right: -2px;
      width: 45px;
    }

    &-ul {
      padding-top: 18px;
      padding-left: 60px;

      &-li {
        font-family: "PingFang SC";
        font-size: $--font-size-base;
        font-weight: 400;
        line-height: 28px;
        color: $--color-n500;
      }
    }
  }
}

.green {
  margin-right: 9px;
  font-size: 16px;
  color: $--color-g300;
  background: $--color-white;
}

.flow-icon {
  font-size: $--font-size-base;
  color: $--color-y400;
}

</style>
