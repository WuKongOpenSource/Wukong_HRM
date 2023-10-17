<template>
  <div class="main">
    <div class="main-title">
      <div class="main-title-icon">
        <i class="wk wk-icon-scanner-solid main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">扫码枪</div>
        <div class="main-title-information-content">
          扫码功能与进销存系统相结合，更加快速、精确地在系统录入产品信息；利用扫码功能进行出入库操作，能准确识别商品，避免人为选择或者录入错误，同时提升录入单据的速度！
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">与CRM/进销存系统全面融合</div>
          <div class="main-menu-items-content">
            在CRM系统/进销存系统的产品管理版块，您可以根据产品需求设置相应的产品录入面板，列举主要产品信息。扫码枪快速识别产品编码所涵盖的信息后，自动提取关于您在产品录入面板相符合的信息，并快速填充。
          </div>
          <img
            src="./images/Scan/one.png"
            alt=""
            class="main-flow-items-img"
            style="width: 350px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">辨别产品伪劣，维护企业权益</div>
          <div class="main-menu-items-content">
            产品条码的可读性起到识别伪劣产品和防假打假的重要作用，这些往往是人工手动录入难以排查的一点。通过扫码枪，快速辨别产品是否具备有效信息。维护企业权益，减少经济损失。
          </div>
          <img
            src="./images/Scan/two.png"
            alt=""
            class="main-flow-items-img"
            style="width: 350px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">多类型条码录入</div>
          <div class="main-menu-items-content">
            产品种类多、分类多、单位多致使条码类型多样。产品条形码、二维码等均支持扫描，极大地提高了员工操作的便捷度。
          </div>
          <img
            src="./images/Scan/three.png"
            alt=""
            class="main-flow-items-img"
            style="width: 350px;">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【扫码枪】使用申请。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>在系统中开通扫码枪功能后，将扫码枪通过USB接口连接电脑。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>打开产品管理版块，点击【新建产品】，找到【扫码地址】。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第四步</div>
            <div class="main-flow-items-content">
              <p>
                将鼠标移动到【扫码地址】的文本框，对准产品的二维码/条形码进行扫描。
              </p>
              <p>（1）产品自身有条形码/二维码，则直接使用扫码枪扫描即可。</p>
              <p>
                （2）产品自身无条形码/二维码，则需要在进销存系统中，输入相关产品信息后系统自动生成条码。
              </p>
              <p>
                （3）在生成条码后，在电脑连接扫码打印机的情况下，将条码进行打印，打印后用扫码枪进行扫描即可。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>扫码后二维码/条形码的地址信息会自动加载到上述文本框中。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第六步</div>
            <div class="main-flow-items-content">
              <p>
                同时该产品信息将自动填充至【新建产品】页面的其他字段信息处。
              </p>
            </div>
            <img
              src="./images/Scan/sixth.png"
              alt=""
              class="main-flow-items-img"
              style="width: 408px;">
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
  name: 'Scan',

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
