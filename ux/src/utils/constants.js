// 本地存储key
/**
 * import Lockr from 'lockr'
 * Lockr.set(LOCAL_CALL_PHONE, phoneNumber)
 * Lockr.get(LOCAL_CALL_AUTH_DATA)
 * Lockr.rm(LOCAL_CALL_AUTH_DATA)
 *
 * import { LOCAL_VERIFY_SLIDER } from '@/utils/constants.js'
 */
// 呼叫中心
export const LOCAL_CALL_PHONE = 'callPhone' // 呼出的号码
export const LOCAL_CALL_OUT_DATA = 'callOutData' // 呼出的数据信息
export const LOCAL_CALL_INTERVAL_TIME = 'IntervalTime' // 通话计时器开始时间: 记录通话开始或者振铃开始的时间
export const LOCAL_CALL_LIMIT_DATA = 'wkCallLimitData' // 多卡限制信息
export const LOCAL_CALL_AUTH_DATA = 'wkCallData' // 权限信息
export const LOCAL_CALL_LIMIT_SET = 'wkCallLimitSet' // 多卡配置

// 滑动验证
export const LOCAL_VERIFY_SLIDER = 'slider'
export const LOCAL_VERIFY_POINT = 'point'

// 用户信息
export const LOCAL_LANG = 'lang'
export const LOCAL_LOGIN_TYPE = 'loginType'
// export const LOCAL_USER_INFO = 'loginUserInfo'
export const LOCAL_ADMIN_TOKEN = 'AUTH-TOKEN'
export const COOKIE_ADMIN_TOKEN = 'AUTH-TOKEN' // cookie token

// 邮箱
export const LOCAL_EMAIL_CONTENT = 'crm-emailContent'

// CRM
export const LOCAL_PAGE_SIZE = 'crmPageSizes' // 单页条数

// 财务
export const LOCAL_FM_PAGE_SIZE = 'fmPageSizes' // 单页条数

// JXC

// PM
export const LOCAL_PM_BACK_TYPE = 'backType' // 类型

// 系统操作
export const LOCAL_CRM_IMPORT = 'crmImportInfo' // 导入
export const LOCAL_UPDATE_TIPS = 'wkUpdateTips' // 更新提示
export const LOCAL_TRIAL_TIPS = 'wkTrialTips' // 试用到期提醒
export const LOCAL_EXPIRATION_TIPS = 'wkExpirationTips' // 到期提醒
export const LOCAL_SIDE_BAR_COLLAPSE = 'sideBarCollapse' // 菜单展开闭合
export const LOCAL_SYSTEM_LOGO = 'systemLogo' // 系统图标
export const LOCAL_SYSTEM_NAME = 'systemName' // 系统名称
export const LOCAL_LOGIN_LOGO_NAME = 'LoginLogoNameCache' // 登录页信息
export const LOCAL_AUTO_LOGIN = 'autologin' // 钉钉自动登录
export const LOCAL_CLEAR_PAGE_TIME = 'clearPageTime' // 自动登录清除
export const LOCAL_FREE_TIME = 'freeTime' // 自动登录 时间
export const LOCAL_SCRM_UPDATE_TIPS = 'scrmUpdateTips' // 更新提示

// 三方登录
export const LOCAL_WX_CODE = 'wx_code'
