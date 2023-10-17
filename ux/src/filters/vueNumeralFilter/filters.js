import numeral from 'numeral'

const abbreviate = value => numeral(value).format('0.0a')

const bytes = value => numeral(value).format('0 b')

const exposedNumeral = (value, format) => numeral(value).format(format)

const exponential = value => numeral(value).format('0.[00]e+0')

const ordinal = value => numeral(value).format('Oo')

const percentage = value => numeral(value).format('0.[00]%')

const separator = value => {
  if (value === '' || value === null || value === undefined) {
    return ''
  }
  const valueStr = value.toString()
  const index = valueStr.indexOf('.')
  if (index == -1) {
    return numeral(value).format('0,0.00')
  } else {
    const values = valueStr.split('.')
    const decimals = values[1].padEnd(2, '0')
    return numeral(values[0]).format('0,0') + '.' + decimals
  }
}

const separatorInt = value => numeral(value).format('0,0')

const currency = value => numeral(value).format('$0,0.00')

export {
  abbreviate,
  bytes,
  exponential,
  exposedNumeral,
  ordinal,
  percentage,
  separator,
  separatorInt,
  currency
}
