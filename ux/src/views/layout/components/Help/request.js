import axios from 'axios'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

const instance = axios.create({
  baseURL: 'https://www.5kcrm.com/center/index.php/center/api/',
  method: 'post'
})

instance.interceptors.request.use(
  config => {
    delete config.headers[LOCAL_ADMIN_TOKEN]
    return config
  },
  err => {
    return Promise.reject(err)
  }
)

export default instance
