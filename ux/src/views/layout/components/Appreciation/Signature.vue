<template>
  <div class="main">
    <div class="main-title">
      <div class="main-title-icon">
        <i class="wk wk-approve1 main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">电子签章</div>
        <div class="main-title-information-content">
          基于不可更改的区块链技术，涵盖可信存证、证据管理、司法服务在内的综合证据管理系统。提供司法认可的电子数据，降低合同签署与管理成本。
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">异地签署成本降低</div>
          <div class="main-menu-items-content">
            传统签订方式含括纸张费、打印费、异地签署快递费、跑腿费等无法避免的开销，且合同文本中任何细微的偏差都将引起重复劳动。电子签章无纸化储存，系统随时调取，全程线上完成。
          </div>
          <img
            src="./images/Signature/one.png"
            alt=""
            class="main-flow-items-img"
            style="width: 363px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">签署效率提升</div>
          <div class="main-menu-items-content">
            异地签署周期长，增加合同成交的不稳定因素。电子签章随发随签，实时追踪合同的签署状态与进度。随时随地调用查阅。
          </div>
          <img
            src="./images/Signature/two.png"
            alt=""
            class="main-menu-items-img"
            style="width: 351px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">智能模版</div>
          <div class="main-menu-items-content">
            悟空电子签章为您提供了身份证、图片、单选、多选、多行文本等多种业务组件，您可根据合同内容多样化的需求，匹配相应的表单控件，自动添加字段等，并一键保存为合同模版，方便您下次快捷选用。
          </div>
          <img
            src="./images/Signature/three.png"
            alt=""
            class="main-menu-items-img"
            style="width: 300px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">签署风险降低</div>
          <div class="main-menu-items-content">
            签署过程进行人脸活体识别比对，并通过公安人口库校验；拥有全线证据链，增加合同安全性，同时对合同信息加密，点对点签署。
          </div>
          <img
            src="./images/Signature/four.png"
            alt=""
            class="main-menu-items-img"
            style="width: 351px;">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【电子签章】使用申请。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>申请通过后，电子签章管理提交个人认证。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>进行企业认证。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第四步</div>
            <div class="main-flow-items-content">
              <p>设置需进行实名认证的成员。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>添加公章，并将签章的使用授权给已实名成员。</p>
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
  name: 'Signature',

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
