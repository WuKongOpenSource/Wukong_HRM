// set function formatTime to filter
import {
  timeToFormatTime,
  formatTimeToTimestamp
} from '@/utils'

export { formatTime } from '@/utils'

/**
 * 时间戳到格式化时间
 * {{item.createTime|filterTimestampToFormatTime('MM-DD dddd')}}
 * @param {*} time
 * @param {*} cFormat
 */
export function filterTimestampToFormatTime(time, cFormat) {
  if (!cFormat) {
    cFormat = 'YYYY-MM-DD HH:mm'
  }
  if (!time || time == 0) {
    return ''
  }
  return timeToFormatTime(time, cFormat)
}

/** 格式化时间到时间戳 */
export function filterFormatTimeToTimestamp(format) {
  return formatTimeToTimestamp(format)
}

/** 用户头像的占位图标 */
export function filterUserLazyImg(value) {
  return {
    src: value,
    error: require('@/assets/img/head.png'),
    loading: require('@/assets/img/loading.gif')
  }
}

/**
 * wk 10.0 iconfont 前缀
 */
export function wkIconPre(name) {
  return name.startsWith('wk') ? name : `wk wk-${name}`
}
