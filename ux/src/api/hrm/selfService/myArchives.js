import request from '@/utils/request'

/**
 * 个人档案信息
 * @param {*} data
 */
export function hrmEmployeeArchivesPersonalAPI() {
  return request({
    url: 'hrmEmployeeArchives/personalArchives',
    method: 'post'
  })
}

/**
 * 岗位档案信息
 * @param {*} data
 */
export function hrmEmployeeArchivesPostAPI() {
  return request({
    url: 'hrmEmployeeArchives/postArchives',
    method: 'post'
  })
}

