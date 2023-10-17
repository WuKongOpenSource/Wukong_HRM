<template>
  <div class="main">
    <div class="main-title">
      <div class="main-title-icon">
        <i class="wk wk-icon-voice-convert main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">语音转换</div>
        <div class="main-title-information-content">
          语音交互提供销售便捷的同时，对重点信息却具有隐蔽性，接收者若想获取关键信息不得不听取全部语音，而这将使销售人员获取关键信息滞后，增加销售工作的繁琐度。而一键语音转文字，则实现语音功能的突破性进展。
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">跟进记录</div>
          <div class="main-menu-items-content">
            语音跟进记录一键转文字，方便各种情景下跟进记录查看。同时帮助销售抓取客户跟进过程中的关键信息，增加对客户的跟进效果。
          </div>
          <img
            src="./images/Conversion/one.png"
            alt=""
            class="main-menu-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">呼叫记录</div>
          <div class="main-menu-items-content">
            在具体客户/联系人下方的呼叫记录处，一键转文字帮助销售重新审视通话内容，定向分析客户性格与特点，增强销售对客户特性的抓取能力。
          </div>
          <img
            src="./images/Conversion/two.png"
            alt=""
            class="main-menu-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">标点智能预测</div>
          <div class="main-menu-items-content">
            根据语音内容，智能预测和添加相应标点符号，还原最真实的文字场景，销售可将文本直接复制生成相应跟进。
          </div>
          <img
            src="./images/Conversion/three.png"
            alt=""
            class="main-menu-items-img">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【语音转换】使用申请。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>
                开通后将在线索、客户、联系人、商机、合同五个板块详情页中的跟进记录部分与呼叫中心部分提供一键转文字功能。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>BI商业智能系统中的员工通话记录部分提供一键转文字功能。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第四步</div>
            <div class="main-flow-items-content">
              <p>
                若无以语音形式添加的跟进记录，则不会显示该语音转文字的功能按钮。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>语音转换不限时长，随心转换。</p>
            </div>
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
  name: 'Conversion',

  data() {
    return {
      activeName: 'function'
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style.scss";
</style>
