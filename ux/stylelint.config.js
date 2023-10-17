module.exports = {
  plugins: [
    'stylelint-order'
  ],
  customSyntax: 'postcss-html',
  extends: [
    'stylelint-config-recess-order',
    'stylelint-config-standard-scss',
    'stylelint-config-standard-vue/scss'
  ],
  rules: {
    'function-url-quotes': null, // 设置url(路径可不加引号)
    'selector-class-pattern': null, // 设置类名选择器不遵循 kebab-case
    'no-descending-specificity': null, // 设置允许低优先级的选择器出现在高优先级的选择器之后
    'no-empty-source': null, // 允许文件内容为空
    'font-family-no-missing-generic-family-keyword': null, // 设置允许定义非"generic-family"风格字体
    'color-function-notation': null, // rgba legacy
    'alpha-value-notation': null, // 透明度值不限制
    'selector-pseudo-class-no-unknown': [ // sass :export  报错
      true,
      {
        'ignorePseudoClasses': [':export']
      }
    ],
    'property-no-vendor-prefix': null, // 允许输入 -webkit-
    'at-rule-no-vendor-prefix': null, // 允许  @-webkit-keyframes
    'block-no-empty': null, // 允许空块
    'no-invalid-double-slash-comments': null, // 允许 // 注释
    'scss/at-import-partial-extension': null // scss 允许 @import 待 后缀名
  }
}

