<template>
  <div class="main">
    <div class="main-title">
      <!-- <i class="wk wk-phone-radio main-title-icon" /> -->
      <div class="main-title-icon">
        <i class="wk wk-icon-phoning-solid main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">呼叫中心</div>
        <div class="main-title-information-content">
          在销售过程中，通过智能外呼准确把握商机，快速触达目标客户，找准成单商机，除了减轻电话咨询杂乱无章的烦恼外还能大幅提升成单率。
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">把呼叫纳入销售流程</div>
          <div class="main-menu-items-content">
            提前做好呼叫计划安排，在客户最方便的时间打给他/她，促进跟进。从整体了解销售团队的呼叫安排，更好地协调工作，把给客户打电话融入到销售流程中。
          </div>
          <img src="./images/Call/one.png" alt="" class="main-menu-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">一键转客户</div>
          <div class="main-menu-items-content">
            悟空CRM系统中未储存的来电号码（比如新客户的来电咨询），销售人员可以把来电号码一键存储为线索信息，这可以很方便的衔接之后的销售和服务过程，而且当该客户再次来电时，CRM系统就能自动识别该客户。
          </div>
          <img src="./images/Call/two.png" alt="" class="main-menu-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">通话记录自动保存</div>
          <div class="main-menu-items-content">
            在销售人员通话过程中，通话记录将在系统中自动保存：来电时间、通话时间、主叫号码、被叫号码、接线员等数据清晰展示。您可以自动跟踪通话详细信息，无需手动记录。
          </div>
          <img
            src="./images/Call/three.png"
            alt=""
            class="main-menu-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">通话记录智能分析</div>
          <div class="main-menu-items-content">
            对通话记录进行分析，考核销售团队的业绩、激励改进。悟空CRM的商业智能版块提供员工通话记录分析图表，可视化方式呈现您的通话记录数据，从中分析如何给客户提供更好的体验。
          </div>
          <img
            src="./images/Call/four.png"
            alt=""
            class="main-menu-items-img">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【呼叫中心】使用申请。</p>
              <p>
                开通后将在客户列表与联系人列表前以<i class="wk wk-circle-iphone flow-icon" />形式展示，用户点击该呼叫按钮则可省却输入号码的过程直接一键外呼至客户/联系人。
              </p>
            </div>
            <img
              src="./images/Call/first.png"
              alt=""
              class="main-flow-items-img"
              style="width: 403px;">
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>
                点击外呼后，系统首先来电至当前用户手机，用户点击接听开启下一步。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>
                用户接听后，电话则自动连接至客户/联系人手机，提醒客户接听，客户接听后该语音通话则正式开启。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第四步</div>
            <div class="main-flow-items-content">
              <p>
                拨打电话的状态将以空闲（未拨打电话）/振铃中（系统来电至用户与客户时均显示振铃中）/通话中（客户接听后）的三种形式显示在客户或联系人详情页面的右上方，通话时可查看系统已录入的客户信息更加准确把握客户心理。
              </p>
            </div>
            <img
              src="./images/Call/fourth.png"
              alt=""
              class="main-flow-items-img"
              style="width: 446px;">
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>
                通话过程自动录音，通话结束后系统将自动在客户/联系人的详情页面生成跟进记录，准确备案跟进时间、跟进方式、通话录音等。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第六步</div>
            <div class="main-flow-items-content">
              <p>对语音跟进记录与通话记录可随意拖拽进度条至所需部分。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第七步</div>
            <div class="main-flow-items-content">
              <p>通话时长与通话次数无限制。</p>
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
  name: 'Call',

  data() {
    return {
      activeName: 'function'
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style.scss";

.flow-icon {
  color: $--color-primary;
}
</style>
