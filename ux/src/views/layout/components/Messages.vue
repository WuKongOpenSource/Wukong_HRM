<template>
  <slide-view
    class="d-view"
    append-to-body
    :body-style="{padding: 0, height: '100%'}"
    @afterEnter="viewAfterEnter"
    @close="$emit('close')">
    <div class="messages">
      <div class="messages-menus">
        <div class="title">通知/待办</div>
        <div class="menus-wrap">
          <menu-item
            v-for="(item, index) in menus"
            :key="index"
            :icon="item.iconClass"
            :title="item.name"
            :num="item.num"
            :selectd="menuIndex === item.infoType"
            @click.native="menuClick(item, index)"
          />
        </div>
      </div>
      <div v-if="show" class="messages-body">
        <system-message
          :unread-nums="unreadNums"
          lazy
          :show="menuIndex == 'message'"
          @update-count="sendSystemUnreadNum"
        />

        <system-message
          :unread-nums="unreadNums"
          only-announcement
          lazy
          :show="menuIndex == 'announce'"
          @update-count="sendSystemUnreadNum"
        />
      </div>
    </div>
  </slide-view>
</template>

<script>
import SlideView from '@/components/SlideView'
import MenuItem from '@/views/layout/components/Sidebar/Item'
import SystemMessage from './SystemMessage'

// 待办事项
import { mapGetters } from 'vuex'

export default {
  // 系统消息
  name: 'Messages',

  components: {
    SlideView,
    MenuItem,
    SystemMessage
  },

  props: {
    unreadNums: Object
  },

  data() {
    return {
      menuIndex: 'message',
      /**
       * model 1今日需联系客户 2分配给我的线索 3分配给我的客户 4待进入公海的客户 5待审核合同 6待审核回款 7待回款提醒 8即将到期的合同 9待回访合同 10待审核发票
       */
      todoSides: [
        {
          name: '今日需联系线索',
          crmType: 'leads',
          color: '#c623fb',
          iconClass: 'wk wk-leads-line',
          infoType: 'todayLeads',
          model: 11,
          num: 0,
          tips: '下次跟进时间为今日的线索',
          hidden: true
        },
        {
          name: '待进入线索池的线索',
          crmType: 'leads',
          color: '#26D4DA',
          iconClass: 'wk wk-leads-line',
          infoType: 'putInLeadsPoolRemind',
          model: 14,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '今日需联系客户',
          crmType: 'customer',
          color: '#2362FB',
          iconClass: 'wk wk-customer-line',
          infoType: 'todayCustomer',
          model: 1,
          num: 0,
          tips: '下次跟进时间为今日的客户',
          hidden: true
        },
        {
          name: '今日需联系商机',
          crmType: 'business',
          color: '#fb9323',
          iconClass: 'wk wk-business-line',
          infoType: 'todayBusiness',
          model: 12,
          num: 0,
          tips: '下次跟进时间为今日的商机',
          hidden: true
        },
        {
          name: '分配给我的线索',
          crmType: 'leads',
          color: '#704AFD',
          iconClass: 'wk wk-leads-line',
          infoType: 'followLeads',
          model: 2,
          num: 0,
          tips: '转移之后未跟进的线索',
          hidden: true
        },
        {
          name: '分配给我的客户',
          crmType: 'customer',
          color: '#19B5F6',
          iconClass: 'wk wk-icon-s-seas-line',
          infoType: 'followCustomer',
          model: 3,
          num: 0,
          tips: '转移、分配之后未跟进的客户，默认显示自己负责的客户',
          hidden: true
        },
        {
          name: '待进入公海的客户',
          crmType: 'customer',
          color: '#26D4DA',
          iconClass: 'wk wk-customer-line',
          infoType: 'putInPoolRemind',
          model: 4,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '待审核合同',
          crmType: 'contract',
          color: '#FD5B4A',
          iconClass: 'wk wk-contract-line',
          infoType: 'checkContract',
          model: 5,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '待审核回款',
          crmType: 'receivables',
          color: '#FFB940',
          iconClass: 'wk wk-receivables-line',
          infoType: 'checkReceivables',
          model: 6,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '待回款提醒',
          crmType: 'receivablesPlan',
          color: '#27BA4A',
          iconClass: 'wk wk-bell-line',
          infoType: 'remindReceivablesPlan',
          model: 7,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '即将到期的合同',
          crmType: 'contract',
          color: '#FF7A38',
          iconClass: 'wk wk-contract-line',
          infoType: 'endContract',
          model: 8,
          num: 0,
          tips: '根据“合同到期时间”及设置的“提前提醒天数”提醒',
          hidden: true
        },
        {
          name: '待回访合同',
          crmType: 'contract',
          color: '#ff9232',
          iconClass: 'wk wk-visit-line',
          infoType: 'returnVisitRemind',
          model: 9,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '待审核发票',
          crmType: 'invoice',
          color: '#ff9232',
          iconClass: 'wk wk-invoice-line',
          infoType: 'checkInvoice',
          model: 10,
          num: 0,
          tips: '',
          hidden: true
        },
        {
          name: '待审核办公',
          crmType: 'examine',
          color: '#ff9232',
          iconClass: 'wk wk-icon-office-line',
          infoType: 'checkOaTypeIds',
          model: 13,
          num: 0,
          tips: '',
          hidden: true
        }
      ],
      showTodoSides: [],
      show: false
    }
  },

  computed: {
    ...mapGetters(['messageNum']),

    menus() {
      const menus = [{
        name: '消息提醒',
        infoType: 'message',
        iconClass: 'wk wk-bell-line',
        num: this.unreadNums ? this.unreadNums.allCount : 0,
        tips: ''
      }]
      // 载入代办按钮
      this.loadTodoMenus(menus)
      return menus
    }
  },

  watch: {},

  created() {},

  mounted() {
    this.show = true
  },

  beforeDestroy() {},

  methods: {
    /**
     * 动画结束页面展示
     */
    viewAfterEnter() {
      this.requestNumCount()
    },

    /**
     * 待办事项
     */
    requestNumCount() {
      this.$store
        .dispatch('GetMessageNum')
    },

    /**
     * 刷新消息数据
     */
    loadTodoMenus(menus) {
      const todos = []
      for (let index = 0; index < this.todoSides.length; index++) {
        const element = this.todoSides[index]
        if (this.messageNum.hasOwnProperty(element.infoType)) {
          element.num = this.messageNum[element.infoType] || 0
          menus.push(element)
          todos.push(element)
        }
      }
      this.showTodoSides = todos
    },

    /**
     * 菜单点击
     */
    menuClick(item) {
      this.menuIndex = item.infoType
    },

    /**
     * 更新未读数量
     */
    sendSystemUnreadNum() {
      this.$emit('update-unread')
    }
  }
}
</script>

<style lang="scss" scoped>
.d-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
}

.messages {
  position: relative;
  height: 100%;
  overflow-y: auto;

  &-menus {
    position: absolute;
    top: 0;
    left: 0;
    width: 240px;
    height: 100%;
    overflow: auto;
    background-color: $--color-n10;

    > .title {
      padding: 24px 32px 8px;
      font-size: 18px;
    }

    > .menus-wrap {
      padding: 0 16px;
    }
  }

  &-body {
    position: relative;
    height: 100%;
    margin-left: 240px;
    overflow: hidden;
    background-color: white;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);
  }
}
</style>
