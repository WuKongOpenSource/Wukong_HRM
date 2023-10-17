import request from '@/utils/request'

/**
 * 我下属的任务列表
 * @param {*} data
 */
export function taskListAPI(data) {
  return request({
    url: 'oaTask/queryTaskList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新增任务
 * @param {*} data
 */
export function setTaskAPI(data) {
  return request({
    url: 'workTask/saveWorkTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// /**
//  * 删除任务
//  * @param {*} data
//  */
// export function deleteTaskAPI(data) {
//   return request({
//     url: 'task/deleteTask',
//     method: 'post',
//     data: data
//   })
// }

/**
 * 任务详情
 * @param {*} data
 */
export function detailsTaskAPI(data) {
  return request({
    url: `workTask/queryTaskInfo/${data.taskId}`,
    method: 'post'
  })
}

/**
 * 回收站任务详情
 * @param {*} data
 */
export function detailsTrashTaskAPI(data) {
  return request({
    url: 'workTask/queryTrashTaskInfo',
    method: 'post',
    data: data
  })
}

/**
 * 任务活动
 * @param {*} data
 */
export function queryLogTaskAPI(data) {
  return request({
    url: `workTaskLog/queryTaskLog/${data.taskId}`,
    method: 'post'
  })
}

/**
 * 编辑相关信息
 * @param {*} data
 */
export function editTaskRelationAPI(data) {
  return request({
    url: 'workTaskRelation/saveWorkTaskRelation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 取消选择的标签 taskId  label
 * @param {*} data
 */
export function taskDeleteLabelAPI(data) {
  return request({
    url: 'oaTask/deleteWorkTaskLabel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 办公任务导出
 * @param {*} data
 */
export function taskOaExportAPI(data) {
  return request({
    url: 'oaTask/oaTaskExport',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

