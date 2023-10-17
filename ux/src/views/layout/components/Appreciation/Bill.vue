<template>
  <div class="main">
    <div class="main-title">
      <!-- <i class="wk wk-invoice main-title-icon" /> -->
      <div class="main-title-icon">
        <i class="wk wk-invoice main-title-icon" />
      </div>
      <div class="main-title-information">
        <div class="main-title-information-caption">发票扫描</div>
        <div class="main-title-information-content">
          针对财务场景中常见票据，进行智能分类及结构化识别，无需提前进行手动分类处理，上传图片即可完成识别及信息提取。助力企业内部报销等业务场景效率升级，降低企业运营成本。
        </div>
      </div>
      <a :href="WKConfig.contactUrl" class="main-title-connect" target="_blank">联系我们</a>
    </div>

    <el-tabs v-model="activeName" class="main-tabs">
      <el-tab-pane label="功能简介" name="function" class="main-menu">
        <div class="main-menu-items">
          <div class="main-menu-items-title">财税报销</div>
          <div class="main-menu-items-content">
            快速识别录入多样税票的各字段信息，应用于企业税务核算及内部报销等场景，有效减少人工核算工作量，实现财税报销的自动化。
          </div>
          <img src="./images/Bill/one.png" alt="" class="main-flow-items-img" style="width: 290px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">发票验真</div>
          <div class="main-menu-items-content">
            智能识别发票代码、号码、开具金额、开票日期四个关键字段，以便快速接入税务机关发票查验平台进行真伪查验，有效降低人力成本，控制业务风险。
          </div>
          <img src="./images/Bill/two.png" alt="" class="main-flow-items-img" style="width: 317px;">
        </div>
        <div class="main-menu-items">
          <div class="main-menu-items-title">发票审核</div>
          <div class="main-menu-items-content">
            扫描识别纸质发票原件并自动与员工提交的报销数据进行智能对比，审核报销发票是否缺失、金额是否一致，实现智能发票审核。
          </div>
          <img src="./images/Bill/three.png" alt="" class="main-flow-items-img" style="width: 317px;">
        </div>
      </el-tab-pane>
      <el-tab-pane label="使用流程" name="flow">
        <div class="main-flow">
          <div class="main-flow-items">
            <div class="main-flow-items-title">第一步</div>
            <div class="main-flow-items-content">
              <p>点击“联系我们”，提交【发票扫描】使用申请。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第二步</div>
            <div class="main-flow-items-content">
              <p>OA办公中【审批】版块下的【差旅报销】处。</p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第三步</div>
            <div class="main-flow-items-content">
              <p>
                点击【新建审批】，选择【差旅报销】，在报销金额后点击发票扫描窗口。
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
                确保摄像头整齐地对准发票，发票纸张整洁无褶皱，信息清晰显示。
              </p>
            </div>
            <div class="main-flow-items-ball">
              <div class="main-flow-items-ball-ball2" />
            </div>
          </div>
          <div class="main-flow-items">
            <div class="main-flow-items-title">第五步</div>
            <div class="main-flow-items-content">
              <p>扫描成功后则发票金额自动录入所需要添加的字段框中。</p>
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
  name: 'Bill',

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
