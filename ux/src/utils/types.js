export function isString(obj) {
  return Object.prototype.toString.call(obj) === '[object String]'
}

export function isObject(obj) {
  return Object.prototype.toString.call(obj) === '[object Object]'
}

export function isArray(obj) {
  return Object.prototype.toString.call(obj) === '[object Array]'
}

/**
 * 数据非空验证
 * @desc 验证数据是否为 null undefined [] {} ''
 * @param data
 * @return {boolean}
 */
export function isEmpty(data) {
  if (data === null) return true
  if (data === undefined) return true
  if (Object.prototype.toString.call(data) === '[object Array]') return data.length === 0
  if (Object.prototype.toString.call(data) === '[object Object]') return Object.keys(data).length === 0
  if (typeof data === 'string') return data.trim() === ''
  return false
}

/**
 * @description: json字符串转json obj
 * @param {*} str
 * @return {object} null 是失败
 */
export function strToJSON(str) {
  if (typeof str == 'string') {
    try {
      var obj = JSON.parse(str)
      if (typeof obj == 'object' && obj) {
        return obj
      } else {
        return null
      }
    } catch (e) {
      return null
    }
  }
}

/**
 * @description: 是附件类型
 * @param {*} val
 * @return {*}
 */
export function isBlob(val) {
  return Object.prototype.toString.call(val) === '[object Blob]'
}
