// 各模块logo 和logo颜色
export default {
  methods: {

    /**
     * logo
     * @param {*} status
     */
    getXrIcon(type) {
      return {
        leads: 'wk wk-leads',
        customer: 'wk wk-customer',
        contacts: 'wk wk-contacts',
        product: 'wk wk-product',
        business: 'wk wk-business',
        contract: 'wk wk-contract',
        receivables: 'wk wk-receivables',
        invoice: 'wk wk-invoice',
        log: 'wk wk-log',
        examine: 'wk wk-approve',
        task: 'wk wk-o-task',
        record: 'wk wk-record'
      }[type]
    },

    /**
     * logo 颜色
     * @param {*} status
     */
    getXrIconColor(type) {
      return {
        leads: '#704afd',
        customer: '#487DFF',
        contacts: '#27BA4A',
        product: '#26d4da',
        business: '#FB9323',
        contract: '#FD5B4A',
        receivables: '#FFB940',
        invoice: '#1cbaf5',
        log: '#5864FF',
        approve: '#9376FF',
        task: '#D376FF',
        record: '#19B5F6'
      }[type]
    }
  }
}
