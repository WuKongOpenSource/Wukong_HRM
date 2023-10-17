// 审批流状态 和 阶段审核状态
export default {
  methods: {
    // /**
    //  * 合同回款的审核状态
    //  * @param {*} status
    //  */
    // getCRMStatusColor(status) {
    //   if (status == 0) {
    //     return '#FF991F'
    //   } else if (status == 1) {
    //     return '#FF991F'
    //   } else if (status == 2) {
    //     return '#00875A'
    //   } else if (status == 3) {
    //     return '#DE350B'
    //   } else if (status == 4) {
    //     return '#dee8ff'
    //   } else if (status == 5) {
    //     return '#FF991F'
    //   }
    // },

    // /**
    //  * 合同回款的审核状态
    //  * @param {*} status
    //  */
    // getCRMStatusName(status) {
    //   if (status == 0) {
    //     return '待审核'
    //   } else if (status == 1) {
    //     return '审核中'
    //   } else if (status == 2) {
    //     return '通过'
    //   } else if (status == 3) {
    //     return '拒绝'
    //   } else if (status == 4) {
    //     return '撤回'
    //   } else if (status == 5) {
    //     return '未提交'
    //   }
    //   return ''
    // },

    /**
     * 审核颜色
     * @param {*} status
     */
    getStatusColor(status) {
      if (status == 0) {
        return '#FF991F'
      } else if (status == 1) {
        return '#00875A'
      } else if (status == 2) {
        return '#DE350B'
      } else if (status == 3) {
        return '#FF991F'
      } else if (status == 4) {
        return '#5E6C84'
      } else if (status == 5) {
        return '#FF991F'
      } else if (status == 6) {
        return '#5E6C84'
      } else if (status == 8) {
        return '#5E6C84'
      } else if (status == 10) {
        return '#0052CC'
      } else if (status == 11) {
        return '#FF991F'
      } else if (status == 12) {
        return '#00875A'
      } else if (status == 13) {
        return '#5E6C84'
      } else if (status == 14) {
        return '#5E6C84'
      } else if (status == 16) {
        return '#5E6C84'
      } else if (status == 18) {
        return '#5E6C84'
      } else if (status == 19) {
        return '#5E6C84'
      }
    },

    /**
     * 审核名称
     * @param {*} status
     */
    getStatusName(status) {
      if (status == 0) {
        return '待审核'
      } else if (status == 1) {
        return '通过'
      } else if (status == 2) {
        return '拒绝'
      } else if (status == 3) {
        return '审核中'
      } else if (status == 4) {
        return '撤回'
      } else if (status == 5) {
        return '未提交'
      } else if (status == 6) {
        return '发起'
      } else if (status == 8) { // 7 已删除
        return '已作废'
      } else if (status == 10) {
        return '正常'
      } else if (status == 11) {
        return '审核中' // 转发
      } else if (status == 13) { // 12 征求他人意见
        return '终止'
      } else if (status == 14) {
        return '归档'
      } else if (status == 15) {
        return '抄送'
      } else if (status == 16) { // 审核记录返回的16
        return '已作废'
      } else if (status == 18) {
        return '跳过'
      } else if (status == 19) {
        return '恢复'
      }
      return ''
    },

    /**
     * 审核图标
     */
    getStatusIcon(status) {
      if (status == 0) {
        return 'wk wk-icon-invalid'
      } else if (status == 1) {
        return 'wk wk-icon-success'
      } else if (status == 2) {
        return 'wk wk-icon-refuse'
      } else if (status == 3) {
        return 'wk wk-icon-in-review'
      } else if (status == 4) {
        return 'wk wk-icon-withdraw'
      } else if (status == 5) {
        return 'wk wk-icon-uncommitted'
      } else if (status == 6) {
        return 'wk wk-icon-create'
      } else if (status == 8) {
        return 'wk wk-icon-invalid'
      } else if (status == 10) {
        return 'wk wk-icon-normal'
      } else if (status == 11) {
        return 'wk wk-icon-in-review'
      } else if (status == 12) {
        return 'wk wk-icon-success'
      } else if (status == 13) {
        return 'wk wk-icon-invalid'
      } else if (status == 14) {
        return 'wk wk-icon-invalid'
      } else if (status == 15) { // 抄送
        return 'wk wk-source-line'
      } else if (status == 16) {
        return 'wk wk-icon-invalid'
      } else if (status == 18) {
        return 'wk wk-d-arrow-right'
      } else if (status == 19) {
        return 'wk wk-icon-reset2'
      }
      return ''
    }

    // /**
    //  * 图片标示
    //  */
    // getStatusImageIcon(status) {
    //   if (status == 0 || status == 3 || status == 5) {
    //     return require('@/assets/img/check_wait.png')
    //   } else if (status == 1) {
    //     return require('@/assets/img/check_suc.png')
    //   } else if (status == 2) {
    //     return require('@/assets/img/check_fail.png')
    //   } else if (status == 4) {
    //     return require('@/assets/img/check_revoke.png')
    //   } else if (status == 6) {
    //     return require('@/assets/img/check_create.png')
    //   } else if (status == 10) {
    //     return require('@/assets/img/check_create.png')
    //   }
    //   return ''
    // }
  }
}
