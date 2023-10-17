<template>
  <div
    v-show="visible"
    :style="{ backgroundColor: background || '' }"
    :class="[customClass]"
    class="empty-mask">
    <div class="empty-content">
      <img
        v-if="iconUrl"
        :src="iconUrl"
        class="empty-icon">
      <p
        v-if="showText"
        class="empty-text">{{ showText }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      text: null,
      background: null,
      visible: false,
      icon: null,
      customClass: ''
    }
  },
  computed: {
    iconUrl: function() {
      /** 内置几个类型  当时none的时候  不展示 */
      if (this.icon) {
        if (this.icon === 'none') {
          return ''
        } else if (this.icon === 'nopermission') {
          return require('@/assets/img/nopermission.png')
        } else {
          return require('@/assets/img/empty.png')
        }
      } else {
        return require('@/assets/img/empty.png')
      }
    },
    showText: function() {
      /** 内置几个类型  当时none的时候  不展示 */
      if (this.text) {
        return this.text
      } else {
        return '没有找到数据'
      }
    }
  },
  methods: {
    setText(text) {
      if (text) {
        this.text = text
      }
    },
    setIcon(icon) {
      this.icon = icon
    }
  }
}
</script>

<style lang="scss" scoped>
.empty-mask {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 5;
  margin: 0;
  background-color: rgba(255, 255, 255, 0.98);
}

.empty-content {
  position: absolute;
  top: 50%;
  width: 100%;
  text-align: center;
}

.empty-text {
  margin: 3px 0;
  font-size: 13px;
  color: $--color-text-secondary;
}

.empty-icon {
  display: block;
  width: 150px;
  margin: 0 auto 20px;
}
</style>

