/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-04 11:29:14
 * @LastEditTime: 2020-06-10 16:00:52
 * @LastEditors: yang
 */
function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

const educationValue = {
  1: '小学',
  2: '初中',
  3: '高中',
  4: '大专',
  5: '本科',
  6: '硕士',
  7: '博士'
}

const sexValue = {
  1: '男',
  2: '女'
}

const statusValue = {
  1: '新候选人',
  2: '初选通过',
  3: '安排面试',
  4: '面试通过',
  5: '已发offer',
  6: '待入职',
  7: '已淘汰',
  8: '已入职'
}

// 面试结果
const interviewResultValue = {
  1: '面试未完成',
  2: '面试通过',
  3: '面试未通过',
  4: '面试取消'
}

const statusColorValue = {
  1: '#2362fb',
  2: '#ffc539',
  3: '#ffc539',
  4: '#20b559',
  5: '#20b559',
  6: '#ffc539',
  7: '#f95a5a',
  8: '#20b559'
}

const interviewTypeValue = {
  1: '现场面试',
  2: '电话面试',
  3: '视频面试'
}

const statusKeyValue = {
  new: 1,
  firstPass: 2,
  interview: 3,
  pass: 4,
  offer: 5,
  wait: 6,
  eliminate: 7,
  entry: 8
}

// 职位字段
const fields = [
  {
    name: '姓名',
    field: 'candidateName',
    formType: 'text',
    isNull: 1,
    setting: []
  }, {
    name: '手机号',
    field: 'mobile',
    formType: 'mobile',
    isNull: 1,
    setting: []
  },
  {
    name: '性别',
    field: 'sex',
    formType: 'radio',
    isNull: 1,
    setting: getValueList(sexValue)
  },
  {
    name: '年龄',
    field: 'age',
    formType: 'number',
    setting: []
  },
  {
    name: '邮箱',
    field: 'email',
    formType: 'email',
    setting: []
  },
  {
    name: '应聘职位',
    field: 'postId',
    formType: 'post',
    isNull: 1,
    setting: []
  },
  {
    name: '工作年限',
    field: 'workTime',
    formType: 'number',
    setting: []
  },
  {
    name: '学历',
    field: 'education',
    formType: 'select',
    isNull: 1,
    setting: getValueList(educationValue)
  },
  {
    name: '毕业院校',
    field: 'graduateSchool',
    formType: 'text',
    setting: []
  },
  {
    name: '最近工作单位',
    field: 'latestWorkPlace',
    formType: 'text',
    setting: []
  },
  {
    name: '招聘渠道',
    field: 'channelId',
    formType: 'channel',
    setting: []
  }
]

const tableFields = [
  {
    name: '姓名',
    field: 'candidateName',
    width: 80
  },
  {
    name: '应聘职位',
    field: 'postId',
    width: 120
  },
  {
    name: '用人部门',
    field: 'deptName',
    width: 120
  },
  {
    name: '候选人状态',
    field: 'status',
    width: 180
  },
  {
    name: '手机号',
    field: 'mobile',
    width: 160
  },
  {
    name: '性别',
    field: 'sex',
    width: 80
  },
  {
    name: '年龄',
    field: 'age',
    width: 80
  },
  {
    name: '邮箱',
    field: 'email',
    width: 160
  },
  {
    name: '招聘负责人',
    field: 'ownerEmployeeName',
    width: 115
  },
  {
    name: '工作年限',
    field: 'workTime',
    width: 80
  },
  {
    name: '学历',
    field: 'education',
    width: 80
  },
  {
    name: '毕业院校',
    field: 'graduateSchool',
    width: 80
  },
  {
    name: '最近工作单位',
    field: 'latestWorkPlace',
    width: 120
  },
  {
    name: '招聘渠道',
    field: 'channelId',
    width: 80
  },
  {
    name: '面试时间',
    field: 'interviewTime',
    width: 80
  },
  {
    name: '面试轮次',
    field: 'stageNum',
    width: 80
  },
  {
    name: '面试官',
    field: 'interviewEmployeeName',
    width: 80
  },
  {
    name: '面试方式',
    field: 'interviewType',
    width: 80
  },
  {
    name: '其他面试官',
    field: 'otherInterviewEmployeeName',
    width: 115
  },
  {
    name: '创建时间',
    field: 'createTime',
    width: 170
  }
]

// 职位model
export default {
  fields,
  tableFields,
  educationValue,
  sexValue,
  getValueList,
  statusValue,
  statusColorValue,
  statusKeyValue,
  interviewTypeValue,
  interviewResultValue
}
