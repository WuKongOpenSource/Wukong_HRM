
<script>
import { hrmEmployeeQueryByIdAPI } from '@/api/hrm/employee.js'
import XrAvatar from '@/components/XrAvatar/index.vue'
export default {
  name: 'HrmAvatar',
  extends: XrAvatar,
  methods: {
    getUserData() {
      hrmEmployeeQueryByIdAPI(this.id)
        .then(res => {
          const resData = res.data || null
          if (resData) {
            this.userData = { ...resData, name: resData.employeeName, realname: resData.employeeName }
          } else {
            this.userData = null
          }
        })
        .catch(() => {})
    }
  }
}
</script>
<style></style>
