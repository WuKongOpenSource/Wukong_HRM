<template>
  <div class="wk-alert">
    <div
      v-if="systemAlertProps.showIcon"
      class="wk-alert-header"><i class="wk-alert-icon el-icon-warning" /></div>
    <div
      :class="{'is-center': systemAlertProps.center}"
      class="wk-alert-content"><span v-html="systemAlertProps.title" /><el-button
        v-if="systemAlertProps.showButton"
        type="primary-text"
        class="wk-alert-btn"
        @click="handleBtnClick"
      >{{ systemAlertProps.buttonLabel }}</el-button></div>
    <div
      v-if="systemAlertProps.closable"
      class="wk-alert-footer">
      <span
        v-if="systemAlertProps.closeText"
        class="wk-alert-closebtn"
        @click="close">{{ systemAlertProps.closeText }}</span>
      <i
        v-else
        class="wk-alert-closebtn el-icon-close"
        @click="close" />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Lockr from 'lockr'
import { LOCAL_UPDATE_TIPS, LOCAL_TRIAL_TIPS, LOCAL_EXPIRATION_TIPS } from '@/utils/constants.js'

export default {
  // 顶部系统消息
  name: 'WkSystemAlert',

  components: {},

  props: {},

  data() {
    return {
    }
  },

  computed: {
    ...mapGetters([
      'systemAlertProps'
    ])
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 系统消息关闭
     */
    close() {
      // sysType 1 升级提醒 2 试用到期 3 正式到期
      // wkUpdateTips 升级提醒
      // wkTrialTips 试用到期提醒
      // wkExpirationTips 到期提醒
      if (this.systemAlertProps.sysType === 1) {
        if (this.systemAlertProps.data?.startTime) {
          Lockr.set(LOCAL_UPDATE_TIPS, new Date(this.systemAlertProps.data?.startTime).getTime().toString())
        } else {
          Lockr.set(LOCAL_UPDATE_TIPS, Date.now().toString())
        }
      } else if (this.systemAlertProps.sysType === 2) {
        Lockr.set(LOCAL_TRIAL_TIPS, Date.now().toString())
      } else if (this.systemAlertProps.sysType === 3) {
        Lockr.set(LOCAL_EXPIRATION_TIPS, Date.now().toString())
      }

      this.$store.commit('SET_RM_SYSTEM_ALERTS', this.systemAlertProps)
    },

    /**
     * @description: 操作按钮点击
     * @return {*}
     */
    handleBtnClick() {
      if (this.systemAlertProps.handleBtnFun) {
        this.systemAlertProps.handleBtnFun()
      } else {
        this.$emit('click', this.systemAlertProps)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-alert {
  position: relative;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  display: box;
  display: flexbox;
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 34px;
  padding: 8px 16px;
  margin: 0;
  overflow: hidden;
  background-color: $--color-b50;
  border-bottom: 1px solid #e6e6e6;
  border-radius: 3px;
  opacity: 1;
  -webkit-transition: opacity 0.2s;
  transition: opacity 0.2s;
  -webkit-box-align: center;
  -ms-flex-align: center;

  &-footer,
  &-header {
    flex-shrink: 0;
  }

  &-closebtn {
    cursor: pointer;
  }

  &-content {
    flex: 1;
    line-height: 1.2;
  }

  &-icon {
    color: $--color-b300;
  }

  .is-center {
    text-align: center;
  }

  &-btn {
    padding: 0;
    margin-left: 8px;
  }

  .wk-alert-header + .wk-alert-content,
  .wk-alert-content + .wk-alert-footer {
    margin-left: 8px;
  }
}

</style>
