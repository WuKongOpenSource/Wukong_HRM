
function importAll(r) {
  const modules = {}
  r.keys().forEach(key => {
    const fileName = key.slice(2, -3)
    const itemContent = r(key).default
    modules[fileName] = itemContent
  })
  return modules
}

export default importAll(require.context('../modules', false, /\.js$/))
