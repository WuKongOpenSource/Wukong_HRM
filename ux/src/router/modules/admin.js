/** 系统管理路由 */
import Layout from '@/views/layout/AdminLayout'

const layout = function(meta = {}, path = '/manage', requiresAuth = true) {
  return {
    path: path,
    component: Layout,
    meta: {
      requiresAuth: requiresAuth,
      ...meta
    }
  }
}

export default [
  {
    ...layout({
      permissions: ['manage', 'hrm'],
      title: '人力资源管理',
      icon: 's-contacts-line'
    }, '/manage/hrm'),
    alwaysShow: true,
    children: [{
      path: 'custom-field',
      component: () => import('@/views/admin/hrm/customField'),
      meta: {
        title: '自定义字段设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'field']
      }
    }, {
      name: 'hrmExamine',
      path: 'examine', // 业务审批流
      component: () => import('@/views/admin/examine'),
      meta: {
        title: '业务审批流',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'examine']
      }
    }, {
      name: 'hrmCustomField',
      path: 'customField/:type/:label/:id',
      component: () => import('@/views/admin/fields'),
      hidden: true,
      meta: {
        activeMenu: '/manage/hrm/custom-field',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'field']
      }
    }, {
      path: 'salary/rules',
      name: 'salaryRules',
      component: () => import('@/views/admin/hrm/salary/Rules'),
      meta: {
        title: '薪资设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'salary']
      }
    }, {
      path: 'salary/compute',
      name: 'salaryCompute',
      component: () => import('@/views/admin/hrm/salary/Compute'),
      meta: {
        title: '计薪设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'computeSalary']
      }
    }, {
      path: 'salary/options',
      name: 'salaryOptions',
      component: () => import('@/views/admin/hrm/salary/Options'),
      meta: {
        title: '工资表设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'optionSalary']
      }
    }, {
      name: 'insuranceSchemeSet',
      path: 'insurance-scheme',
      component: () => import('@/views/admin/hrm/insuranceScheme'),
      meta: {
        title: '社保方案管理',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'insurance']
      }
    },
    {
      path: 'schedule',
      component: () => import('@/views/admin/hrm/schedule'),
      meta: {
        title: '考勤规则设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'attendanceRule']
      }
    },
    // {
    //   path: 'achievementRule',
    //   component: () => import('@/views/admin/hrm/achievementRule/index.vue'),
    //   meta: {
    //     title: '考勤规则设置',
    //     requiresAuth: true,
    //     permissions: ['manage', 'hrm', 'appraisal']
    //   }
    // },
    // {
    //   path: 'achievement',
    //   component: () => import('@/views/admin/hrm/achievement'),
    //   meta: {
    //     title: '考核模板设置',
    //     requiresAuth: true,
    //     permissions: ['manage', 'hrm', 'appraisal']
    //   }
    // },
    {
      path: 'biz-param',
      component: () => import('@/views/admin/hrm/bizParam'),
      meta: {
        title: '业务参数设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'params']
      }
    }, {
      name: 'employeeManageSet',
      path: 'employee-manage',
      component: () => import('@/views/admin/hrm/employeeManage'),
      meta: {
        title: '员工管理设置',
        requiresAuth: true,
        permissions: ['manage', 'hrm', 'archives']
      }
    }]
  },
  {
    ...layout({
      permissions: ['manage']
    }, '/manage', true),
    children: [{
      path: 'process-delegation',
      component: () => import('@/views/admin/processDelegation'),
      meta: {
        title: '流程委托',
        icon: 'icon-stage'
      }
    }]
  }
]
