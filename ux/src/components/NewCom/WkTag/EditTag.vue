<template>
  <div class="edit-tag-dialog">
    <flexbox class="tag-dialog-header">
      <div class="tag-dialog-header__text">
        <span
          class="el-icon-arrow-left"
          @click="back" />
        标签管理
      </div>
      <i
        class="el-icon-close icon-close"
        @click="close" />
    </flexbox>
    <div class="search">
      <el-input
        v-model="searchInput"
        :maxlength="10"
        placeholder="输入标签名，最多十个字符" />
    </div>
    <div class="content">
      <div
        v-for="(item, index) in list"
        :key="index"
        class="tag-list">
        <span
          :style="{'background': item.color ? item.color: '#ccc'}"
          class="tag-name">
          {{ item.name.length > 10 ? item.name.substring(0, 10) + '...' : item.name }}
        </span>
        <div class="rt">
          <span
            class="el-icon-edit"
            @click="editBtn(item)" />
          <span
            class="el-icon-delete"
            @click="deleteBtn(item)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    editTagList: Array
  },
  data() {
    return {
      searchInput: ''
    }
  },
  computed: {
    list() {
      return this.editTagList.filter(item => {
        return item.name.indexOf(this.searchInput) > -1
      })
    }
  },
  watch: {},
  methods: {
    back() {
      this.$emit('back')
    },
    close() {
      this.$emit('close')
    },
    deleteBtn(item) {
      this.$emit('deleteBtn', item)
    },
    editBtn(item) {
      this.$emit('editBtn', item)
    }
  }
}
</script>

<style scoped lang="scss">
.edit-tag-dialog {
  padding-top: 10px;
  overflow: hidden;
  background: #fff;
  border-radius: $--border-radius-base;

  .tag-dialog-header {
    position: relative;
    height: 40px;
    padding: 0 16px;

    .tag-dialog-header__text {
      font-size: 16px;
      color: $--color-text-primary;
    }

    .icon-close {
      position: absolute;
      top: 50%;
      right: 16px;
      font-size: 20px;
      color: $--color-text-secondary;
      cursor: pointer;
      transform: translateY(-50%);
    }
  }

  .search {
    position: relative;
    padding: 0 16px;
    margin-top: 16px;
  }

  .content {
    height: 196px;
    padding: 8px 16px;
    overflow: auto;

    .tag-list {
      padding: 4px 0;
      margin-bottom: 0 !important;

      .rt {
        margin-top: 4px;
        margin-right: 0;
        color: $--color-text-secondary;

        span {
          margin-right: 8px;
          cursor: pointer;
        }
      }

      .tag-name {
        display: inline-block;
        padding: 3px 10px;
        font-size: 12px;
        color: #fff;
        border-radius: $--border-radius-base;
      }
    }

    .tag-list:hover {
      background: #f7f8fa;
    }
  }
}
</style>
