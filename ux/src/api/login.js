import request from '@/utils/request'

export function loginAPI(params) {
  return request({
    url: '/login',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function authorizationAPI(params) {
  return request({
    url: '/adminUser/authorization',
    method: 'post',
    data: params
  })
}

export function logoutAPI() {
  return request({
    url: '/adminUser/logout',
    method: 'post'
  })
}
