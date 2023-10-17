// 人资
import Layout from '@/views/layout/HrmLayout'
import ChildrenLayout from '@/views/layout/ChildLayOut'

import store from '@/store'

const layout = function(meta, alwaysShow, path = '/hrm') {
  return {
    path: path,
    component: Layout,
    alwaysShow: alwaysShow,
    meta: {
      requiresAuth: true,
      ...meta
    }
  }
}

export default [
  {
    ...layout({
      permissions: ['hrm']
    }, false),
    children: [{
      name: 'hrmWorkbench',
      path: 'workbench',
      component: () => import('@/views/hrm/workbench/index'),
      meta: {
        title: '工作台',
        icon: 'workbench'
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm']
    }, false, '/hrm/self-server'),
    isEmployee: true,
    children: [{
      name: 'hrmWorkbench',
      path: 'workbench',
      component: () => import('@/views/hrm/workbench/index'),
      meta: {
        title: '工作台',
        icon: 'workbench'
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm', 'dept', 'index']
    }, false),
    children: [{
      path: 'dept',
      component: () => import('@/views/hrm/dept/index'),
      meta: {
        title: '组织管理',
        icon: 'subordinate'
      }
    }]
  },
  {
    ...layout({
      icon: 'office',
      title: '招聘管理',
      permissionList: [['hrm', 'recruit', 'read'], ['hrm', 'recruit', 'readPost']]
    }, true, '/hrm/recruit/subs/'),
    children: [{
      name: 'hrmCandidate',
      path: 'candidate',
      component: () => import('@/views/hrm/recruit/candidate/index'),
      meta: {
        title: '候选人',
        requiresAuth: true,
        permissions: ['hrm', 'recruit', 'read']
      }
    }, {
      name: 'hrmPost',
      path: 'post',
      component: () => import('@/views/hrm/recruit/post/index'),
      meta: {
        title: '招聘职位',
        requiresAuth: true,
        permissions: ['hrm', 'recruit', 'readPost']
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm', 'employee', 'index']
    }, false),
    children: [{
      name: 'hrmEmployee',
      path: 'employee',
      component: () => import('@/views/hrm/employee/index'),
      meta: {
        title: '员工管理',
        icon: 'user'
      }
    }]
  },
  {
    ...layout({
      title: '考勤管理',
      icon: 'schedule',
      permissionList: [['hrm', 'attendance', 'readClock'], ['hrm', 'attendance', 'readLeaveRecord'], ['hrm', 'attendance', 'readMonthRecord']]
    }, true, '/hrm/attendance'),
    children: [
      {
        name: 'hrmPunchingRecord',
        path: 'subs/punchingRecord',
        component: () => import('@/views/hrm/clock/punchingRecord/index'),
        meta: {
          title: '打卡记录',
          requiresAuth: true,
          permissions: ['hrm', 'attendance', 'readClock']
        }
      },
      {
        name: 'hrmAskingLeave',
        path: 'subs/askingLeave',
        component: () => import('@/views/hrm/clock/askingLeave'),
        meta: {
          title: '请假记录',
          requiresAuth: true,
          permissions: ['hrm', 'attendance', 'readLeaveRecord']
        }
      },
      {
        name: 'hrmClock',
        path: 'subs/clock',
        component: () => import('@/views/hrm/clock/index'),
        meta: {
          title: '月度汇总',
          requiresAuth: true,
          permissions: ['hrm', 'attendance', 'readMonthRecord']
        }
      }
    ]
  },
  {
    ...layout({
      permissions: ['hrm', 'insurance', 'read']
    }, false),
    children: [{
      name: 'hrmInsurance',
      path: 'insurance-scheme',
      component: () => import('@/views/hrm/insuranceScheme/index'),
      meta: {
        icon: 'social',
        title: '社保管理'
      }
    }, {
      name: 'hrmInsuranceDetail',
      path: 'insurance-scheme/detail/:id',
      hidden: true,
      component: () => import('@/views/hrm/insuranceScheme/Detail'),
      meta: {
        activeMenu: '/hrm/insurance-scheme',
        icon: 'social',
        title: '社保管理'
      }
    }]
  },
  {
    ...layout({
      title: '薪资管理',
      icon: 'payment',
      permissionList: [['hrm', 'salary', 'index'], ['hrm', 'salary', 'history']]
    }, false, '/hrm/salary/subs/'),
    children: [{
      name: 'hrmSalary',
      path: 'index',
      component: () => import('@/views/hrm/salary'),
      meta: {
        title: '薪资管理',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'index']
      }
    }, {
      name: 'hrmSalaryArchives',
      path: 'archives',
      component: () => import('@/views/hrm/salary/archives'),
      meta: {
        title: '薪资档案',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'queryArchives']
      }
    },
    {
      path: 'history',
      component: () => import('@/views/hrm/salary/History'),
      meta: {
        title: '历史工资',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'history']
      }
    }, {
      path: 'history/detail/:id',
      name: 'salaryHistoryDetail',
      hidden: true,
      component: () => import('@/views/hrm/salary/HistoryDetail'),
      meta: {
        activeMenu: '/hrm/salary/subs/history',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'history']
      }
    },
    {
      name: 'hrmSlipHistory',
      path: 'slip/history',
      component: () => import('@/views/hrm/salary/slip/History'),
      meta: {
        title: '发放记录',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'queryRecord']
      }
    }, {
      path: 'slip/history/detail/:id/:month',
      name: 'salarySlipHistoryDetail',
      hidden: true,
      component: () => import('@/views/hrm/salary/slip/HistoryDetail'),
      meta: {
        activeMenu: '/hrm/salary/subs/history/history',
        requiresAuth: true,
        permissions: ['hrm', 'salary', 'queryRecord']
      }
    }]
  },
  {
    ...layout({
      icon: 'perform',
      title: '绩效管理',
      permissionList: [['hrm', 'appraisalPlan'], ['hrm', 'appraisalArchives'], ['hrm', 'appraisalSetting'], ['hrm', 'resultSetting']]
    }, true, '/hrm/performance/subs/'),
    children: [
      {
        path: 'KpiAssessment',
        name: 'KpiAssessment',
        component: () => import('@/views/hrm/performance/KpiAssessment'),
        meta: {
          permissions: ['hrm', 'appraisalPlan'],
          requiresAuth: true,
          title: 'KPI考核'
        }
      },
      {
        path: 'employee',
        component: () => import('@/views/hrm/performance/employee'),
        meta: {
          permissions: ['hrm', 'appraisalArchives'],
          requiresAuth: true,
          title: '绩效档案'
        }
      },
      {
        path: 'kpi-setting',
        component: ChildrenLayout,
        meta: {
          title: 'KPI考核设置',
          requiresAuth: true,
          permissionList: [['hrm', 'appraisalSetting'], ['hrm', 'resultSetting']]
        },
        children: [{
          path: 'assessmentTemplate',
          component: () => import('@/views/hrm/performance/assessmentTemplate'),
          meta: {
            title: '考核指标模板',
            requiresAuth: true,
            permissions: ['hrm', 'appraisalSetting']
          }
        }, {
          path: 'assessmentResults',
          component: () => import('@/views/hrm/performance/assessmentResults'),
          meta: {
            requiresAuth: true,
            title: '考核结果设置',
            permissions: ['hrm', 'resultSetting']
          }
        }]
      }
    ]
  },
  {
    ...layout({
      permissions: ['hrm']
    }, false, '/hrm/self-server'),
    isEmployee: true,
    children: [{
      name: 'myArchives',
      path: 'myArchives',
      component: () => import('@/views/hrm/selfService/myArchives'),
      meta: {
        title: '我的档案',
        icon: 'archive'
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm']
    }, false, '/hrm/self-server'),
    isEmployee: true,
    children: [{
      name: 'myInsuranceScheme',
      path: 'insuranceScheme',
      component: () => import('@/views/hrm/selfService/insuranceScheme'),
      meta: {
        title: '我的社保',
        icon: 'social'
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm']
    }, false, '/hrm/self-server'),
    isEmployee: true,
    children: [{
      name: 'mySalarySlip',
      path: 'salarySlip',
      component: () => import('@/views/hrm/selfService/salarySlip'),
      meta: {
        title: '我的工资条',
        icon: 'icon-slip'
      }
    }]
  },
  {
    ...layout({
      permissions: ['hrm']
    }, false, '/hrm/self-server'),
    isEmployee: true,
    children: [{
      name: 'attendanceReport',
      path: 'attendanceReport',
      component: () => import('@/views/hrm/selfService/attendanceReport'),
      meta: {
        title: '考勤报表'
      }
    }]
  },
  {
    ...layout({
      icon: 'perform',
      title: '绩效管理',
      permissions: ['hrm']
    }, false, '/hrm/self-server/subs'),
    isEmployee: true,
    children: [{
      path: 'performance',
      name: 'myPerformance',
      component: () => import('@/views/hrm/selfService/performanceManagement'),
      meta: {
        title: 'Kpi考核',
        icon: 'invoice-line'
      }
    }, {
      path: 'MyPerformanceArchives',
      name: 'MyPerformanceArchives',
      component: () => import('@/views/hrm/selfService/MyPerformanceArchives'),
      meta: {
        title: '我的绩效档案',
        icon: 'receive-line'
      }
    }]
  },
  // {
  //   ...layout({
  //     permissions: ['hrm']
  //   }, false, '/hrm/self-server'),
  //   isEmployee: true,
  //   children: [{
  //     name: 'myPerformance',
  //     path: 'performance',
  //     component: () => import('@/views/hrm/selfService/performance'),
  //     meta: {
  //       title: '绩效管理',
  //       icon: 'perform'
  //     }
  //   }]
  // },
  {
    ...layout({
      requiresAuth: true,
      authFun: () => {
        return store.state.app.moduleAuth && store.state.app.moduleAuth.hrm
      }
    }, false),
    hidden: true,
    children: [{
      path: 'tips',
      name: 'hrmTips',
      component: () => import('@/views/hrm/tips'),
      meta: {
        title: '员工端'
      }
    }]
  }
]
