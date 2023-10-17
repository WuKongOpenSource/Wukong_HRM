/**
 * Create by yxk at 2020/6/11
 * 数学运算
 */

/**
 * 两个浮点数求和
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accAdd(num1, num2) {
  // eslint-disable-next-line one-var
  let r1 = 0, r2 = 0
  try {
    r1 = (num1.toString().split('.')[1] || []).length
  } catch (e) {
    return num1 + num2
  }
  try {
    r2 = (num2.toString().split('.')[1] || []).length
  } catch (e) {
    return num1 + num2
  }
  const m = Math.pow(10, Math.max(r1, r2))
  return Math.round(accMul(num1, m) + accMul(num2, m)) / m
}

/**
 * 两个浮点数相减
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accSub(num1, num2) {
  let r1, r2
  try {
    r1 = (num1.toString().split('.')[1] || []).length
  } catch (e) {
    return num1 - num2
  }
  try {
    r2 = (num2.toString().split('.')[1] || []).length
  } catch (e) {
    return num1 - num2
  }
  const m = Math.pow(10, Math.max(r1, r2))
  return Math.round(accMul(num1, m) - accMul(num2, m)) / m
}

/**
 * 两个浮点数相乘
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accMul(num1, num2) {
  let m = 0
  const s1 = num1.toString()
  const s2 = num2.toString()
  try {
    m += (s1.split('.')[1] || []).length
  } catch (e) {
    return num1 * num2
  }
  try {
    m += (s2.split('.')[1] || []).length
  } catch (e) {
    return num1 * num2
  }
  return Number(s1.replace('.', '')) * Number(s2.replace('.', '')) / Math.pow(10, m)
}

/**
 * 两个浮点数相除
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accDiv(num1, num2) {
  let m = 0
  const s1 = num1.toString()
  const s2 = num2.toString()
  try {
    m += (s1.split('.')[1] || []).length
  } catch (e) {
    return num1 / num2
  }
  try {
    m += (s2.split('.')[1] || []).length
  } catch (e) {
    return num1 / num2
  }
  const n1 = accMul(num1, Math.pow(10, m))
  const n2 = accMul(num2, Math.pow(10, m))
  return Number(n1) / Number(n2)
}
