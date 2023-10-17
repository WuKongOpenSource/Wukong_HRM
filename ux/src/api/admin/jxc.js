/**
 * Create by yxk at 2020/6/22
 */
import request from '@/utils/request'

/**
 * 查询自动编号
 * @param {*} data
 */
export function queryNumberSettingAPI(data) {
  return request({
    url: 'jxcNumberSetting/queryNumberSetting',
    method: 'post',
    data: data
  })
}

/**
 * 设置自动编号
 * @param {*} data
 */
export function setNumberSettingAPI(data) {
  return request({
    url: 'jxcNumberSetting/setNumberSetting',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 字段大类列表
 * @param {*} data
 */
export function jxcFieldQueryFieldsAPI(data) {
  return request({
    url: 'jxcField/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 查询自定义字段
 * @param {*} data
 */
export function jxcFieldQueryAPI(data) {
  return request({
    url: `jxcField/list/${data.label}`,
    method: 'post',
    data: data
  })
}

/**
 * 保存自定义字段
 * @param {*} data
 */
export function jxcFieldSaveAPI(data) {
  return request({
    url: 'jxcField/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
