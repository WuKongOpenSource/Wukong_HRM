<template>
  <div class="main">
    <div class="main-title">
      <div class="main-title-icon">
        <i class="wk wk-icon-business-card-solid main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">名片扫描</div>
        <div class="main-title-information-content">
          使用名片识别技术，实现对用户名片关键信息的结构化识别和录入，可应用于线下会议、论坛、商务交流等场景，满足用户快速录入名片关键信息的需求，有效降低用户输入成本，提升用户使用体验辑文本
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">快速填充联系人信息</div>
          <div class="main-menu-items-content">
            支持对姓名、公司、职位、邮编、邮箱、电话、网址、地址、手机号9个名片关键字段进行结构化识别，快速填充悟空CRM中联系人信息项，助力销售一键添加联系人
          </div>
          <img
            src="./images/Card/one.png"
            alt=""
            class="main-flow-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">助力销售抓取海量人脉</div>
          <div class="main-menu-items-content">
            销售无需再保存管理名片，名片扫描一键抓取关键信息快速生成联系人，打破传统客户信息收集模式，更减轻了销售对于名片管理这种琐碎的工作，更好的投入到客户的开拓与维护中，挖掘更多商机。
          </div>
          <img
            src="./images/Card/two.png"
            alt=""
            class="main-flow-items-img">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">避免手动输错文字</div>
          <div class="main-menu-items-content">
            在销售手动录入名片信息的过程中，打字不仅拖累了销售的工作步伐，更有可能产生错别字的尴尬。而一旦发现错误信息，又需要从无数的名片中查找进行核实，不仅降低了客户的满意度，又影响了销售的工作进度。使用名片扫描，即可从根本上避免这一出错机会。
          </div>
          <img
            src="./images/Card/three.png"
            alt=""
            class="main-flow-items-img">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【名片扫描】使用申请。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>仅在CRM移动端新建联系人处可用。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>
                打开移动端联系人管理版块，点击【新建联系人】功能按钮，出现“手动输入”与“名片扫描”选择框，点击“名片扫描”。
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
                确保摄像头整齐地对准名片，名片纸张整洁无褶皱，信息清晰显示。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>扫描成功后系统自动打开新建联系人面板，并已录入名片信息。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第六步</div>
            <div class="main-flow-items-content">
              <p>
                此时与该联系人关联的客户名称默认为无，可直接点击从已有客户列表中选择，也可点击新建客户进行关联。
              </p>
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
  name: 'Card',

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
