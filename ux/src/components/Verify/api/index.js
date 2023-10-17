import request from '@/utils/request' // 组件内部封装的axios

// 获取验证图片  以及token
export function reqGet(data) {
  return request({
    url: 'cloud/getCaptcha',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// 滑动或者点选验证
export function reqCheck(data) {
  return request({
    url: '/cloud/checkCaptcha',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

