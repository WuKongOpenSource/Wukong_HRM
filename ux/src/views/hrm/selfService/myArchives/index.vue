<template>
  <div class="my-archives">
    <xr-header
      ft-top="0"
      label="我的档案" />
    <el-tabs
      v-model="tabCurrentName"
      class="side-detail__tabs--default">
      <el-tab-pane
        v-for="(item, index) in tabNames"
        :key="index"
        :label="item.label"
        :name="item.name"
        lazy>
        <component
          :is="item.name"
          class="side-detail-tabs-content" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import XrHeader from '@/components/XrHeader'
import EmployeePostInfo from './components/EmployeePostInfo'
import EmployeeBaseInfo from './components/EmployeeBaseInfo'

export default {
  // 我的档案
  name: 'MyArchives',

  components: {
    XrHeader,
    EmployeePostInfo,
    EmployeeBaseInfo
  },

  props: {},

  data() {
    return {
      loading: false, // 展示加载loading
      detailData: null,
      tabNames: [
        { label: '基本信息', name: 'EmployeeBaseInfo' },
        { label: '岗位信息', name: 'EmployeePostInfo' }
      ],
      tabCurrentName: 'EmployeeBaseInfo'
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {}
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.my-archives {
  height: 100%;

  .xr-header {
    height: 34px;
    margin-bottom: 8px;
  }

  .side-detail__tabs--default {
    height: calc(100% - 50px);

    ::v-deep.el-tabs__header {
      padding: 0;
    }
  }
}
</style>
