/**
 * Created by jiachenpan on 16/11/18.
 */
import Lockr from 'lockr'

import { isArray, isString } from './types'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

export function parseTime(time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if (('' + time).length === 10) time = parseInt(time) * 1000
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/** 压缩文件 改为 compressorjs  执行图片压缩
 * quality压缩百分比 0.3
 */

/**
 * 获取date类型展示时间
 * @param {*} time
 */
export function getWkDateTime(time) {
  if (time) {
    const temps = time.split(' ')
    return temps.length > 0 ? temps[0] : ''
  }
  return time
}

/** 根据date URL 创建blob 用于上传 */
export function createBlob(result) {
  var arr = result.split(',')
  var mime = arr[0].match(/:(.*?)/)[1]
  var bstr = atob(arr[1])
  var n = bstr.length
  var u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new Blob([u8arr], {
    type: mime
  })
}

/** 获取file大小的名称 */
export function fileSize(value) {
  if (!value || value == 0) {
    return '0 Bytes'
  }
  if (isNaN(Number(value))) return value
  var unitArr = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  var index = 0
  var srcsize = parseFloat(value)
  index = Math.floor(Math.log(srcsize) / Math.log(1024))
  var size = srcsize / Math.pow(1024, index)
  //  保留的小数位数
  size = size.toFixed(2)
  return size + unitArr[index]
}

/** 获取最大 z-index 的值 */
import {
  PopupManager
} from 'element-ui/lib/utils/popup'
export function getMaxIndex() {
  return PopupManager.nextZIndex()
}

/** 深拷贝 */
export function objDeepCopy(source) {
  if (typeof source === 'object') {
    var sourceCopy = source instanceof Array ? [] : {}
    for (var item in source) {
      if (!source[item]) {
        sourceCopy[item] = source[item]
      } else {
        sourceCopy[item] = typeof source[item] === 'object' ? objDeepCopy(source[item]) : source[item]
      }
    }
    return sourceCopy
  }
  return source
}

/**
 * 获取文件类型图标
 * @param {*} file
 */
export function getFileTypeIcon(file) {
  if (file.type.indexOf('image') !== -1) {
    return getFileIconWithSuffix('png')
  } else if (file.type.indexOf('audio') !== -1) {
    return getFileIconWithSuffix('mp3')
  } else if (file.type.indexOf('video') !== -1) {
    return getFileIconWithSuffix('mp4')
  } else {
    const index = file.name.lastIndexOf('.')
    const ext = file.name.substr(index + 1) || ''

    return getFileIconWithSuffix(ext)
  }
}

/**
 * @description: 是压缩文件
 * @param {*} ext 后缀 或者 带后缀文件名
 * @return {*}
 */
export function isRAR(name) {
  const temps = name ? name.split('.') : []
  var ext = ''
  if (temps.length > 0) {
    ext = temps[temps.length - 1]
  } else {
    ext = ''
  }
  return ['7z', 'rar', 'zip'].includes(ext)
}

/**
 * 根据文件名字判断是否能预览
 * @param {*} name
 */
export function canPreviewFile(name) {
  const temps = name ? name.split('.') : []
  var ext = ''
  if (temps.length > 0) {
    ext = temps[temps.length - 1]
  } else {
    ext = ''
  }

  ext = ext.toLowerCase()
  // ['jpg', 'png', 'jpeg', 'bmp', 'ico', 'gif'] 图片交给弹框预览处理
  if (['xlsx', 'xls', 'xlt', 'xltx'].includes(ext)) {
    return true
  } else if (['doc', 'docx'].includes(ext)) {
    return true
  } else if (ext === 'pdf') {
    return true
  } else if (['ppt', 'pptx'].includes(ext)) {
    return true
  } else if (['txt', 'text', 'rtf'].includes(ext)) {
    return true
  } else if (['html'].includes(ext)) {
    return true
  } else if (isRAR(ext)) {
    return true
  }

  return false
}

/**
 * 预览文件
 */
import axios from '@/utils/request'
export function wkPreviewFile(path, name) {
  window.open(wkPreviewFileUrl(path, name))
}

export function wkPreviewFileUrl(path, name) {
  return `${WKConfig.getLocationOrigin()}/file/preview?url=${encodeURIComponent(`${path}${path.includes('?fullfilename=') ? '' : `?fullfilename=${name || ''}`}&c=${axios.defaults.headers.common[LOCAL_ADMIN_TOKEN]}`)}`
}

export function getFileIconWithSuffix(ext) {
  const fileType = getFileTypeWithExt(ext)
  if (fileType) {
    return {
      image: require('@/assets/img/file/file_img.png'),
      tif: require('@/assets/img/file/file_tif.png'),
      video: require('@/assets/img/file/file_video.png'),
      audio: require('@/assets/img/file/file_music.png'),
      excel: require('@/assets/img/file/file_excle.png'),
      word: require('@/assets/img/file/file_word.png'),
      archive: require('@/assets/img/file/file_zip.png'),
      pdf: require('@/assets/img/file/file_pdf.png'),
      ppt: require('@/assets/img/file/file_ppt.png'),
      text: require('@/assets/img/file/file_txt.png')
    }[fileType] || require('@/assets/img/file/file_unknown.png')
  }

  return require('@/assets/img/file/file_unknown.png')
}

/**
 * 获取文件类型根据文件名
 * @param {*} file
 */
export function getFileIconWithFileName(fileName) {
  if (fileName) {
    const index = fileName.lastIndexOf('.')
    const ext = fileName.substr(index + 1) || ''
    return getFileIconWithSuffix(ext)
  }
  return ''
}

import {
  Message
} from 'element-ui'
/**
 * 根据文件名验证文件是否通过
 * @param {*} fileName 文件名
 * @param {*} type 要求的文件类型
 * @param {*} messageShow 展示消息
 */
export function verifyFileTypeWithFileName(fileName, type = 'excel', messageShow = true) {
  let pass = true
  if (fileName) {
    const index = fileName.lastIndexOf('.')
    const ext = fileName.substr(index + 1) || ''
    const fileType = getFileTypeWithExt(ext)
    if (fileType != type) {
      pass = false
    }
  } else {
    pass = false
  }

  if (!pass && messageShow) {
    Message({
      message: '请选择正确的文件类型',
      type: 'error'
    })
  }
  return pass
}

/**
 * 根据后缀获取文件类型
 * @param {*} ext
 */
export function getFileTypeWithExt(ext) {
  if (ext) {
    ext = ext.toLowerCase()
    if (['jpg', 'png', 'jpeg', 'bmp', 'ico', 'gif'].includes(ext)) {
      return 'image'
    } else if (ext === 'psd') {
      return 'psd'
    } else if (ext === 'tif') {
      return 'tif'
    } else if (['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', '3gp'].includes(ext)) {
      return 'video'
    } else if (['mp3', 'wma', 'wav'].includes(ext)) {
      return 'audio'
    } else if (['xlsx', 'xls'].includes(ext)) {
      return 'excel'
    } else if (['doc', 'docx'].includes(ext)) {
      return 'word'
    } else if (['rar', 'zip', '7z', 'tar', 'iso', 'dmg'].includes(ext)) {
      return 'archive'
    } else if (ext === 'pdf') {
      return 'pdf'
    } else if (['ppt', 'pptx'].includes(ext)) {
      return 'ppt'
    } else if (['txt', 'text'].includes(ext)) {
      return 'text'
    }
  }
  return ''
}

/** 判断输入的是number */
export function regexIsNumber(nubmer) {
  var regex = /^[0-9]+.?[0-9]*/
  if (!regex.test(nubmer)) {
    return false
  }
  return true
}

/** 判断输入的是crm数字 数字的整数部分须少于15位，小数部分须少于4位*/
export function regexIsCRMNumber(nubmer) {
  var regex = /^([-+]?\d{1,15})(\.\d{0,4})?$/
  if (!regex.test(nubmer)) {
    return false
  }
  return true
}

/** 判断输入的是货币 货币的整数部分须少于15位，小数部分须少于2位*/
export function regexIsCRMMoneyNumber(nubmer) {
  var regex = /^([-+]?\d{1,15})(\.\d{0,2})?$/
  if (!regex.test(nubmer)) {
    return false
  }
  return true
}

/** 判断输入的是电话*/
export function regexIsCRMMobile(mobile) {
  var regex = /^(\+?0?\d{2,4}\-?)?\d{6,11}$/
  if (!regex.test(mobile)) {
    return false
  }
  return true
}

// 中国手机号
export const chinaMobileRegex = /^1\d{10}$/

// 密码检测正则
export const checkPasswordRegex = /^(?=.*[a-zA-Z])(?=.*\d).{6,20}$/

// 验证输入的邮箱是否合格的正则
export const emailRegex = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,5})$/

// 域名名称
export const domainNameRegex = /^[0-9a-zA-Z]{1}[0-9a-zA-Z-]{2,29}$/

/** 判断输入的是邮箱*/
export function regexIsCRMEmail(email) {
  if (!emailRegex.test(email)) {
    return false
  }
  return true
}

/**
 * 时间操作
 * @param
 */
/** 时间戳转date*/
import moment from 'moment'

export function formatTime(time) {
  const timeMoment = moment(time)
  const nowMoment = moment()
  const diff = nowMoment.diff(timeMoment, 'seconds')

  var minute = 60
  var hour = minute * 60
  var day = hour * 24
  // var halfamonth = day * 15
  var month = day * 30

  var monthC = diff / month
  var weekC = diff / (7 * day)
  var dayC = diff / day
  var hourC = diff / hour
  var minC = diff / minute

  if (monthC >= 1) {
    if (monthC > 11) {
      return timeMoment.format('YY年MM月DD日')
    } else {
      return parseInt(monthC) + '月前'
    }
  } else if (weekC >= 1) {
    return parseInt(weekC) + '周前'
  } else if (dayC >= 1) {
    return parseInt(dayC) + '天前'
  } else if (hourC >= 1) {
    return parseInt(hourC) + '小时前'
  } else if (minC >= 1) {
    return parseInt(minC) + '分钟前'
  } else {
    return '刚刚'
  }

  // if (diff < 30) {
  //   return '刚刚'
  // } else if (diff < 3600) {
  //   // less 1 hour
  //   return Math.ceil(diff / 60) + '分钟前'
  // } else if (diff < 3600 * 24) {
  //   return Math.ceil(diff / 3600) + '小时前'
  // } else if (diff < 3600 * 24 * 2) {
  //   return '1天前'
  // }

  // const timeYear = timeMoment.format('YYYY')
  // const nowYear = nowMoment.format('YYYY')

  // if (timeYear == nowYear) {
  //   return timeMoment.format('MM月DD日')
  // } else {
  //   return timeMoment.format('YY年MM月DD日')
  // }
}

// date 或者格式化时间
export function timeToFormatTime(time, format) {
  if (time) {
    let momentObj = ''
    if (isNaN(time) || time instanceof Date) { // 如果误传非数字时间，直接当做格式化时间处理
      momentObj = moment(time)
    } else {
      momentObj = moment(typeof time === 'number' ? time : parseInt(time))
    }
    return momentObj.isValid() ? momentObj.format(format || 'YYYY-MM-DD') : ''
  }
  return ''
}

/**
 *
 * @param {*} format 格式化字符串
 */
export function formatTimeToTimestamp(format) {
  if (format && format.length > 0) {
    var timeValue = moment(format)
      .valueOf()
      .toString()
    return timeValue.length > 10 ? timeValue.substr(0, 10) : timeValue
  }
  return ''
}

/** image 下载 */
import { downloadFileAPI } from '@/api/common'
import { adminFileDownByUrlAPI } from '@/api/admin/file'

/**
 *
 * @param {*} data url
 * @param {*} filename 名称
 */
export function getImageData(url) {
  return new Promise((resolve, reject) => {
    if (url) {
      adminFileDownByUrlAPI(url).then(res => {
        var reader = new FileReader()
        reader.readAsDataURL(res.data)
        reader.onload = (evt) => {
          resolve({
            blob: res.data,
            src: evt.target.result
          })
        }
      }).catch(() => {
        reject()
      })
    } else {
      reject()
    }
  })
}

/**
 * path  和 name
 */
export function downloadFile(data) {
  downloadFileAPI(data.path).then(res => {
    downloadFileWithBuffer(res.data, data.name || '文件')
  }).catch(() => {})
}

export function dataURLtoBlob(dataurl) {
  // eslint-disable-next-line one-var
  var arr = dataurl.split(','),
    mime = arr[0].match(/:(.*?);/)[1],
    bstr = atob(arr[1]),
    n = bstr.length,
    u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new Blob([u8arr], {
    type: mime
  })
}

export function getBase64Image(img) {
  var canvas = document.createElement('canvas')
  canvas.width = img.width
  canvas.height = img.height
  var ctx = canvas.getContext('2d')
  ctx.drawImage(img, 0, 0, img.width, img.height)
  var ext = img.src.substring(img.src.lastIndexOf('.') + 1).toLowerCase()
  var dataURL = canvas.toDataURL('image/' + ext)
  return dataURL
}

// 获取绑定参数
export function guid() {
  function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
  }
  return (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4())
}

import JsEncrypt from 'jsencrypt'

export function RSAencrypt(pas) {
  const publicKey = '-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXSsDQ5jiqzMBbDS8SdwXVyCByv5GwE13PnKipBJb2awmsc0THksjhKgcjW/HCT7GRhUOv4bv4pyTaE2f3xBVmZwut126M6xZvfuds1T10DF7jlUD14oJcpX5W7LKExoIPGYJMq83iyIV7rRT5+Bc80eHQ5hjWZ3lyv5upYWMkjwIDAQAB-----END PUBLIC KEY-----'
  // 实例化jsEncrypt对象
  const jse = new JsEncrypt()
  // 设置公钥
  jse.setPublicKey(publicKey)
  return jse.encrypt(pas)
}

import * as XLSX from 'xlsx'
export function getExcelLines(file) {
  var reader = new FileReader() // 读取操作就是由它完成.
  reader.readAsBinaryString(file)// 读取文件的内容,也可以读取文件的URL
  reader.onload = function(evt) {
    // 当读取完成后回调这个函数,然后此时文件的内容存储到了result中,直接操作即可
    try {
      var data = evt.target.result
      var workbook = XLSX.read(data, {
        type: 'binary'
      }) // 以二进制流方式读取得到整份excel表格对象
      var buildings = [] // 存储获取到的数据
      // var fromTo = ''
      // 遍历每张表读取
      for (var sheet in workbook.Sheets) {
        if (workbook.Sheets.hasOwnProperty(sheet)) {
          // fromTo = workbook.Sheets[sheet]['!ref']
          buildings = buildings.concat(XLSX.utils.sheet_to_json(workbook.Sheets[sheet]))
          break // 如果只取第一张sheet表，就取消注释这行
        }
      }
      const fileRows = buildings.length - 1// 表格内容行数，减去表头的一行

      return fileRows
    } catch (e) {
      console.log('文件类型不正确', e)
      return
    }
  }
}

/**
 * 两个浮点数求和
 * @param num1
 * @param num2
 * @return {number}
 */
import NP from 'number-precision'
export function floatAdd() {
  return NP.plus(...arguments)
}

/**
 * 下载excel
 */
export function downloadExcelWithResData(res) {
  const disposition = res.headers['content-disposition'] || res.headers['Content-Disposition']
  let fileName = disposition?.split('filename=')[1]
  if (!fileName) {
    fileName = disposition?.split("UTF-8''")[1]
  }
  fileName = fileName ? fileName.replace(/\"/g, '') : 'file.xlsx'
  fileName = decodeURI(fileName) || ''
  downloadFileWithBuffer(res.data, fileName, 'application/vnd.ms-excel;charset=utf-8')
}

export function downloadFileWithBuffer(data, name, type) {
  var downloadElement = document.createElement('a')
  var href = window.URL.createObjectURL(data) // 创建下载的链接
  downloadElement.href = href
  downloadElement.download = name // 下载后文件名
  document.body.appendChild(downloadElement)
  downloadElement.click() // 点击下载
  document.body.removeChild(downloadElement) // 下载完成移除元素
  window.URL.revokeObjectURL(href) // 释放掉blob对象
}

import FileSaver from 'file-saver'
/**
 * 导出ElTable表格
 * @param {*} name
 */
export function exportElTable(name, domId) {
  const fix = document.querySelector('.el-table__fixed')
  let wb
  if (fix) {
    document.getElementById(domId).removeChild(fix)
    wb = XLSX.utils.table_to_book(
      document.getElementById(domId)
    )
    document.getElementById(domId).appendChild(fix)
  } else {
    wb = XLSX.utils.table_to_book(
      document.getElementById(domId)
    )
  }
  const wopts = {
    bookType: 'xlsx',
    bookSST: false,
    type: 'binary'
  }
  const wbout = XLSX.write(wb, wopts)

  FileSaver.saveAs(
    new Blob([s2ab(wbout)], {
      type: 'application/octet-stream;charset=utf-8'
    }),
    name
  )
}

function s2ab(s) {
  var cuf
  var i
  if (typeof ArrayBuffer !== 'undefined') {
    cuf = new ArrayBuffer(s.length)
    var view = new Uint8Array(cuf)
    for (i = 0; i !== s.length; i++) {
      view[i] = s.charCodeAt(i) & 0xff
    }
    return cuf
  } else {
    cuf = new Array(s.length)
    for (i = 0; i !== s.length; ++i) {
      cuf[i] = s.charCodeAt(i) & 0xff
    }
    return cuf
  }
}

/**
 * 获取百度地图
 */
export function getBaiduMap() {
  if (!global.BMap) {
    global.BMap = {}
    global.BMap._preloader = new Promise((resolve, reject) => {
      global._initBaiduMap = function() {
        resolve(global.BMap)
        global.document.body.removeChild($script)
        global.BMap._preloader = null
        global._initBaiduMap = null
      }
      const $script = document.createElement('script')
      global.document.body.appendChild($script)
      $script.src = `https://api.map.baidu.com/api?v=3.0&ak=${WKConfig.baiduKey}&callback=_initBaiduMap`
    })
    return global.BMap._preloader
  } else if (!global.BMap._preloader) {
    return Promise.resolve(global.BMap)
  } else {
    return global.BMap._preloader
  }
}
/** 将url转化为img对象 */
export function urltoImage(url, fn) {
  var img = new Image()
  img.src = url
  return img
}
/** img对象转化为canvas对象 */
export function imagetoCanvas(image) {
  var cvs = document.createElement('canvas')
  var ctx = cvs.getContext('2d')
  cvs.width = image.width
  cvs.height = image.height
  ctx.drawImage(image, 0, 0, cvs.width, cvs.height)
  return cvs
}

export function canvasToDataURL(canvas, format, quality) {
  return canvas.toDataURL(format || 'image/jpeg', quality || 1.0)
}

/**
 * file Path to blob
 */
export function filePathToBlob(filePath) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    xhr.open('get', filePath, true)
    xhr.responseType = 'blob'
    xhr.onload = function() {
      if (this.status == 200) {
        resolve(this.response)
      } else {
        reject()
      }
    }
    xhr.send()
  })
}

/**
 * 获取树形接口的值
 */
export function getTreeValue(id, treeList, key = 'id', children = 'children') {
  const resultList = []
  loopTree(id, treeList, resultList, key, children)
  return resultList.reverse()
}

function loopTree(id, treeList, resultList, key, children) {
  for (var i = 0; i < treeList.length; i++) {
    if (treeList[i][key] == id) {
      resultList.push(treeList[i][key])
      return true
    } else {
      if (treeList[i][children] && treeList[i][children].length > 0) {
        if (loopTree(id, treeList[i][children], resultList, key, children)) {
          resultList.push(treeList[i][key])
          return true
        }
      }
    }
  }
}

/**
 * 判断是手机
 */
export function isMobileDevice() {
  if (/(iPhone|iPad|iPod|iOS|Android)/i.test(navigator.userAgent)) {
    return true
  }
  return false
}

/**
 * 判断是微信
 */
export function isWeiXin() {
  var ua = window.navigator.userAgent.toLowerCase()
  if (ua.match(/MicroMessenger/i) == 'micromessenger') {
    return true
  } else {
    return false
  }
}

/**
 * 下划线转换驼峰
 */
export function toCamelCase(name) {
  if (!name) return ''
  return name.replace(/\_(\w)/g, function(all, letter) {
    return letter.toUpperCase()
  })
}

/**
 * 驼峰转换下划线
 */
export function toUnderScoreCase(name) {
  return name.replace(/([A-Z])/g, '_$1').toLowerCase()
}

/**
 * 通过.间隔字符串或者方法获取对象值
 */
export function getRowValueByKey(row, rowKey) {
  if (!row) return false
  if (typeof rowKey === 'string') {
    if (rowKey.indexOf('.') < 0) {
      return row[rowKey]
    }
    const key = rowKey.split('.')
    let current = row
    for (let i = 0; i < key.length; i++) {
      current = current[key[i]]
      if (current === undefined) {
        return false
      }
    }
    return current
  } else if (typeof rowKey === 'function') {
    // eslint-disable-next-line no-useless-call
    return rowKey.call(null, row)
  }
}

import store from '@/store'
export function getPermissionByKey(rowKey, type) {
  if (type == 'PM') {
    return !!getRowValueByKey(store.state.pm.projectAuth, rowKey)
  } else {
    return !!getRowValueByKey(store.state.user.allAuth, rowKey)
  }
}

/**
 * 浏览器打印指定区域的内容
 * @desc 打印区域需要用一个标签包裹起来
 * @param {String} id 要打印的区域元素ID
 */
export function printHTML(id) {
  const contentEl = document.getElementById(id)
  if (!contentEl) return
  let iframe = document.getElementById('print-hook')
  if (iframe) {
    iframe.parentNode.removeChild(iframe)
  }
  iframe = document.createElement('iframe')
  iframe.src = ''
  iframe.id = 'print-hook'
  iframe.width = '0'
  iframe.height = '0'
  document.body.appendChild(iframe)
  iframe.contentWindow.document.body.innerHTML = contentEl.innerHTML
  iframe.contentWindow.print()
}

/**
 * 获取cookies domain
 */
export function getCookiesDomain() {
  const host = window.location.hostname || ''
  const hosts = host.split('.')
  if (hosts.length > 1) {
    return `.${hosts[hosts.length - 2]}.${hosts[hosts.length - 1]}`
  }
  return ''
}

/**
 * 元素可见
 */
export function isEleVisible(ele) {
  const { top, right, bottom, left } = ele.getBoundingClientRect()
  const w = window.innerWidth
  const h = window.innerHeight
  if (bottom < 0 || top > h) {
    // y 轴方向
    return false
  }
  if (right < 0 || left > w) {
    // x 轴方向
    return false
  }
  return true
}

/**
 * 获取对应名称的父组件
 * @param {*} componentName
 */
export function getParentComponent(componentName) {
  var parent = this.$parent || this.$root
  var name = parent.$options.componentName

  while (parent && (!name || name !== componentName)) {
    parent = parent.$parent

    if (parent) {
      name = parent.$options.componentName
    }
  }
  return parent
}

/**
 * 颜色增加透明度
 */
export function convertHexByOpacity(hexCode, opacity) {
  if (!hexCode) return ''
  let hex = hexCode.replace('#', '')

  if (hex.length === 3) {
    hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2]
  }

  const r = parseInt(hex.substring(0, 2), 16)
  const g = parseInt(hex.substring(2, 4), 16)
  const b = parseInt(hex.substring(4, 6), 16)

  return 'rgba(' + r + ',' + g + ',' + b + ',' + opacity + ')'
}

/**
 * @description: 跨域请求
 * @param {*} url
 * @param {*} params
 * @param {*} cb
 * @return {*}
 */
export function jsonp({ url, params, cb }) {
  params = { ...params, cb }

  const paramsStringify = Object.keys(params).map(item => `${item}=${params[item]}`).join('&')

  return new Promise((resolve, reject) => {
    var element = document.createElement('script')
    element.setAttribute('src', `${url}?${paramsStringify}`)
    document.getElementsByTagName('head')[0].appendChild(element)
    if (cb) {
      window[cb] = res => {
        resolve(res)
      }
    }
  })
}

/**
 * @description: 设置link 内容
 * @param {*} link
 * @return {*}
 */
function setLink(link) {
  let favicon = document.querySelector('link[rel="icon"]')
  if (favicon !== null) {
    favicon.href = link
  } else {
    favicon = document.createElement('link')
    favicon.rel = 'icon'
    favicon.href = link
    document.head.appendChild(favicon)
  }
}

import { img2ico } from '@/utils/imageToIcon'
/**
 * @description: 更新导航菜单头信息
 * @param {*} data 企业配置信息, data不存在 读取换成加载
 * @return {*}
 */
const SystemLogoCacheKey = 'SystemLogoCache'
export function updateNavLinkName(data) {
  if (data) {
    WKConfig.companyName = data.companyName
    if (WKConfig.companyName) {
      document.title = WKConfig.companyName
    }
    if (data.companyLogo) {
      getImageData(data.companyLogo)
        .then(async(data) => {
          const link = await img2ico.convert({
            source: data.src,
            size: [32],
            shape: 'circle',
            bleed: false
          })

          if (link) {
            var reader = new FileReader()
            reader.readAsDataURL(link)
            reader.onload = (evt) => {
              setLink(evt.target.result)

              Lockr.set(SystemLogoCacheKey, evt.target.result)
            }
          }
        }).catch(() => {})
    } else {
      setLink('./favicon.ico')
    }
  } else {
    const companyName = Lockr.get('systemName')
    WKConfig.companyName = companyName
    if (WKConfig.companyName) {
      document.title = WKConfig.companyName
    }

    const companyLogoIco = Lockr.get(SystemLogoCacheKey)
    console.log('companyLogoIco---', companyLogoIco)
    if (companyLogoIco) {
      setLink(companyLogoIco)
    }
  }
}
/**
 * @description: 解析多个图片url字符串为数组对象
 * @param {*} value
 * @return {*}
 */
export function toArrayObjectByPicUrl(value) {
  if (isArray(value)) {
    return value.map(item => ({ url: item.url, small: item.url }))
  } else if (isString(value)) {
    if (value) {
      return value.split(',').map(item => {
        if (item.includes('&')) {
          const [bigUrl, smallUrl] = item.split('&') || []
          return { url: bigUrl, children: { url: smallUrl }}
        } else {
          return { url: item }
        }
      })
    } else {
      return []
    }
  } else {
    return []
  }
}

/**
 * @description: 根据url返回缩略图
 * @param {*} url
 * @return {*}
 */
export function getSmallPicUrl(url) {
  if (url) {
    const token = axios.defaults.headers.common[LOCAL_ADMIN_TOKEN]
    if (url.includes('&')) {
      const [, smallUrl] = url.split('&') || []
      return smallUrl + `?c=${token}`
    } else {
      return url + `?c=${token}`
    }
  }
  return ''
}

/**
 * @description: 获取金额单位状态
 * @return {*}
 */
export function getUnitStatus(module) {
  return store.state.unit.unit[module]
}

/**
 * @description: 获取模块单位
 * @param {*} module
 * @return {*}
 */
export function getMoneyUnit(module) {
  if (module) {
    return ['元', '万元'][getUnitStatus(module)]
  } else {
    console.warn('[缺少参数]:module')
    return '元'
  }
}

/**
 * @description: 获取金额通过模块
 * @param {*} module
 * @return {*}
 */
export function getMoneyUnitValue(module, value) {
  if (module) {
    const status = getUnitStatus(module)
    if (status === 0) {
      return value
    } else if (status === 1) {
      return NP.divide(value, 10000)
    }
  } else {
    console.warn('[缺少参数]: value')
    return value
  }
}

/**
 * @description: 查看文件大小通过返回true 不通过返回false
 * @param {*} file
 * @param {*} maxSize
 * @return {*}
 */
export function checkFileSize(size, maxSize) {
  const fileSize = size / 1024 / 1024 // Convert to MB
  return fileSize <= maxSize
}

/**
 * @description: HTML文本 转为用于展示的文本内容
 * @param {*} content
 * @return {*}
 */
export function getEmailShowContent(emailContent) {
  let content = emailContent.replace(/<img.*?src=[\"|\']?(.*?)[\"|\']?\s.*?>/g, '')
  content = content.replace(/<style[\s\S]*?<\/style>/ig, '')
  let div = document.createElement('div')
  div.innerHTML = content
  content = div.innerText || div.textContent
  div = null
  content = content.replace(/(\n)/g, '')
  content = content.replace(/(\t)/g, '')
  content = content.replace(/(\r)/g, '')
  content = content.replace(/<\/?[^>]*>/g, '')
  content = content.replace(/(^\s*)|(\s*$)/g, '')
  if (content.length > 50) {
    content = content.substring(0, 50)
  }
  return content
}
