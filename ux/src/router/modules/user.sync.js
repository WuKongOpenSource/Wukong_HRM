/** 个人中西管理路由 */
import Layout from '@/views/layout/UserLayout'

const personRouter = [
  {
    path: '/person',
    component: Layout,
    redirect: '/person/index',
    name: 'person',
    hidden: true,
    meta: {
      title: '个人中心'
    },
    children: [{
      path: 'index',
      component: () => import('@/views/user/index')
    }]
  }
]

export default personRouter
