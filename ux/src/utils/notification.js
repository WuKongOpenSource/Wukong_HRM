import store from '@/store'
const companyName = store.state.app.name || window.WKConfig.companyName

export function sendNotification(content) {
  console.log('Notification.permission---', Notification.permission)
  // 如果用户同意就创建一个通知
  if (window.Notification && Notification.permission === 'granted') {
    const notification = new Notification(companyName, { body: content, icon: './favicon.ico' })
    notification.onclick = function(event) {
      event.preventDefault() // prevent the browser from focusing the Notification's tab
      window.open(getLocationOrigin(), '_blank')
    }
  // eslint-disable-next-line brace-style
  }

  // 如果用户没有选择是否显示通知
  // 注：因为在 Chrome 中我们无法确定 permission 属性是否有值，因此
  // 检查该属性的值是否是 "default" 是不安全的。
  else if (window.Notification && Notification.permission !== 'denied') {
    Notification.requestPermission(function(status) {
      if (Notification.permission !== status) {
        Notification.permission = status
      }

      // 如果用户同意了
      if (status === 'granted') {
        const notification = new Notification(companyName, { body: content, icon: './favicon.ico' })
        notification.onclick = function(event) {
          event.preventDefault() // prevent the browser from focusing the Notification's tab
          window.open(getLocationOrigin(), '_blank')
        }
      // eslint-disable-next-line brace-style
      }

      // 否则，我们可以让步的使用常规模态的 alert
      else {
        // alert('您未打开浏览器通知')
      }
    })
  // eslint-disable-next-line brace-style
  }

  // 如果用户拒绝接受通知
  else {
    // 我们可以让步的使用常规模态的 alert
    // alert('您未打开浏览器通知')
  }
}

function getLocationOrigin() {
  return window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '')
}

export function sendElNotification(content) {
  window.app.$notify({
    title: companyName,
    message: content
    // duration: 0
  })
}
