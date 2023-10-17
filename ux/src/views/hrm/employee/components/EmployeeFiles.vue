<template>
  <div v-loading="loading" class="contianer">
    <div>
      <sections
        v-for="(bigItem, bigIndex) in allData"
        v-show="(bigItem.name.length == 1 && showDetailIndex == -1) || bigItem.name.length > 1 && showDetailIndex == bigIndex"
        :key="bigIndex"
        :index="bigIndex"
        :titles="bigItem.name"
        class="file-sections"
        content-height="auto"
        @on-titles-click="onTitlesClick">
        <div
          v-if="(bigItem.name.length == 1 && showDetailIndex == -1)"
          class="files-box-class">
          <flexbox
            :gutter="0"
            wrap="wrap">
            <flexbox-item
              v-for="(item, index) in bigItem.unfold ? bigItem.list : bigItem.list.slice().splice(0, 2)"
              :key="index"
              :span="1/4"
              class="cell">
              <div class="cell-box">
                <div class="cell-body">
                  <i
                    :class="item.icon"
                    class="cell-body-icon" />
                </div>
                <div class="cell-footer">
                  {{ item.name }}<span v-if="item.count > 0">（{{ item.count }}）</span>
                </div>
                <div
                  class="cell-handle">
                  <div class="handle-items">
                    <span
                      v-if="item.count > 0"
                      class="handle-item"
                      @click="previewDetail(bigItem, bigIndex, item, index)">
                      <i class="wk wk-icon-eye-solid" />
                    </span>
                    <span
                      class="handle-item"
                      @click="checkDetail(bigItem, bigIndex, item, index)">
                      <i class="wk wk-folder" />
                    </span>
                  </div>
                </div>
              </div>
            </flexbox-item>
            <flexbox-item
              :span="1/4"
              class="cell">
              <div class="cell-unfold-box">
                <flexbox
                  v-if="bigItem.unfold"
                  class="unfold-button unfold-button-positon"
                  @click.native="unfloadFiles(bigItem, bigIndex)">
                  <i class="el-icon-d-arrow-left unfold-button-icon" />
                  <div class="unfold-button-name">收起</div>
                </flexbox>
                <flexbox
                  v-else
                  class="unfold-button unfold-button-positon"
                  @click.native="unfloadFiles(bigItem, bigIndex)">
                  <div class="more-button-name">更多</div>
                  <i class="el-icon-d-arrow-right more-button-icon" />
                </flexbox>
              </div>
            </flexbox-item>
          </flexbox>
        </div>

        <div
          v-if="bigItem.name.length > 1 && showDetailIndex == bigIndex"
          class="files-box-detail">
          <flexbox
            :gutter="0"
            wrap="wrap">
            <flexbox-item
              v-if="editAuth"
              :span="1/3"
              class="detail-cell">
              <div class="cell-upload-box">
                <el-upload
                  :show-file-list="false"
                  :http-request="fileUpload"
                  action="http"
                  drag
                  multiple>
                  <i class="wk wk-icon-upload upload-icon" />
                  <div class="upload-name">点击或拖拽上传</div>
                </el-upload>
              </div>
            </flexbox-item>
            <flexbox-item
              v-for="(item, index) in detailList"
              :key="index"
              :span="1/3"
              class="detail-cell">
              <div class="detail-cell-box">
                <div class="detail-cell-body">
                  <span>
                    <img
                      v-if="isShowImage(item.file.name)"
                      :key="item.file.url"
                      v-src="item.file.url">
                    <img
                      v-if="!isShowImage(item.file.name)"
                      :key="item.file.url"
                      v-src="getFileTypeIcon(item.file.url)">
                  </span>

                  <div class="body-handle">
                    <div class="handle-items">
                      <span
                        class="handle-item"
                        @click="previewFile(item, index)">
                        <i class="wk wk-icon-eye-solid" />
                      </span>
                    </div>
                  </div>
                </div>
                <flexbox class="detail-cell-footer">
                  <div
                    v-if="editDetailIndex!=index"
                    class="footer-name">{{ item.file.name }}</div>
                  <el-input
                    v-if="editDetailIndex==index"
                    ref="editInput"
                    v-model="editInputContent"
                    autofocus
                    placeholder="请输入内容"
                    @blur="editInputBlur" />
                  <i
                    v-if="editAuth"
                    class="wk wk-edit footer-item"
                    @click="handleDetailItemClick('edit',item, index)" />
                  <i
                    v-if="editAuth"
                    class="wk wk-s-delete footer-item"
                    @click="handleDetailItemClick('delete',item, index)" />
                  <i
                    class="wk wk-b-download footer-item"
                    @click="handleDetailItemClick('download',item, index)" />
                </flexbox>
              </div>
            </flexbox-item>
          </flexbox>
        </div>
      </sections>
    </div>
  </div>
</template>

<script>
import {
  hrmEmployeeFileAddAPI,
  hrmEmployeeFileDeleteAPI,
  hrmEmployeeFileQueryNumAPI,
  hrmEmployeeFileQueryBySubTypeAPI
} from '@/api/hrm/employee'
import {
  adminFileUploadAPI,
  adminFileRenameFileAPI,
  adminFileDownAPI
} from '@/api/admin/file'

import Sections from './Sections'

import { getFileIconWithSuffix, downloadFileWithBuffer } from '@/utils'

export default {
  /** 员工管理 的 附件管理*/
  name: 'EmployeeFiles',
  components: {
    Sections
  },

  inject: ['editAuth'],
  props: {
    /** 模块ID */
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      /** 基础资料 */
      baseList: [],
      /** 档案资料 */
      recordList: [],
      /** 离职资料 */
      leaveList: [],
      allData: [],
      /** 详情-1 都可以展示 不等1的时候 只能name 数组 超过两个的展示 */
      showDetailIndex: -1,
      showDetailSubIndex: -1, // 子id
      editDetailItem: {},
      editDetailIndex: -1,
      editInputContent: '',
      /** 详情list */
      detailList: []
    }
  },

  computed: {},
  watch: {
    id: {
      handler() {
        this.initInfo()
        this.getBaseInfo()
      },
      immediate: true
    }
  },
  mounted() {
  },
  activated: function() {
    console.log(3)
  },
  deactivated: function() {
    console.log(4)
  },
  methods: {
    initInfo() {
      /** 详情-1 都可以展示 不等1的时候 只能name 数组 超过两个的展示 */
      this.showDetailIndex = -1
      this.showDetailSubIndex = -1 // 子id
      this.editDetailItem = {}
      this.editDetailIndex = -1
      this.editInputContent = ''
      /** 详情list */
      this.detailList = []
      /** 基础资料 */
      // 11、身份证原件 12、学历证明 13、个人证件照 14、身份证复印件 15、工资银行卡 16、社保卡 17、公积金卡 18、获奖证书 19、其他 21、劳动合同 22、入职简历 23、入职登记表 24、入职体检单 25、离职证明 26、转正申请表 27、其他 31、离职审批 32、离职证明 33 、其他
      this.baseList = [
        {
          name: '身份证原件照片',
          icon: 'wk wk-icon-credentials',
          count: 0,
          subType: 11
        },
        {
          name: '学历证明',
          icon: 'wk wk-icon-medal',
          count: 0,
          subType: 12
        },
        {
          name: '个人证件照',
          icon: 'wk wk-icon-my',
          count: 0,
          subType: 13
        },
        {
          name: '身份证复印件',
          icon: 'wk wk-icon-copy',
          count: 0,
          subType: 14
        },
        {
          name: '工资银行卡',
          icon: 'wk wk-icon-bankcard',
          count: 0,
          subType: 15
        },
        {
          name: '社保卡',
          icon: 'wk wk-icon-license',
          count: 0,
          subType: 16
        },
        {
          name: '公积金卡',
          icon: 'wk wk-icon-home',
          count: 0,
          subType: 17
        },
        {
          name: '获奖证书',
          icon: 'wk wk-icon-barrage',
          count: 0,
          subType: 18
        },
        {
          name: '其他',
          icon: 'wk wk-icon-setup',
          count: 0,
          subType: 19
        }
      ]
      /** 档案资料 */
      this.recordList = [
        {
          name: '劳动合同',
          icon: 'wk wk-icon-star-contract',
          count: 0,
          subType: 21
        },
        {
          name: '入职简历',
          icon: 'wk wk-icon-personal-data',
          count: 0,
          subType: 22
        },
        {
          name: '入职登记表',
          icon: 'wk wk-icon-handle-log',
          count: 0,
          subType: 23
        },
        {
          name: '入职体检单',
          icon: 'wk wk-icon-health-data',
          count: 0,
          subType: 24
        },
        {
          name: '上家公司离职证明',
          icon: 'wk wk-icon-bevel-contract',
          count: 0,
          subType: 25
        },
        {
          name: '转正申请表',
          icon: 'wk wk-icon-contract',
          count: 0,
          subType: 26
        },
        {
          name: '其他',
          icon: 'wk wk-icon-setup',
          count: 0,
          subType: 27
        }
      ]
      /** 离职资料 */
      this.leaveList = [
        {
          name: '离职审批',
          icon: 'wk wk-icon-seal',
          count: 0,
          subType: 31
        },
        {
          name: '离职证明',
          icon: 'wk wk-icon-forbid-user',
          count: 0,
          subType: 32
        },
        {
          name: '其他',
          icon: 'wk wk-icon-setup',
          count: 0,
          subType: 33
        }
      ]

      this.allData = [
        { name: ['员工基本资料'], list: this.baseList, unfold: false },
        { name: ['员工档案资料'], list: this.recordList, unfold: false },
        { name: ['员工离职资料'], list: this.leaveList, unfold: false }
      ]
    },
    // 获取基础信息
    getBaseInfo() {
      hrmEmployeeFileQueryNumAPI(this.id)
        .then(res => {
          const data = res.data || {}
          for (let index = 0; index < this.baseList.length; index++) {
            const element = this.baseList[index]
            element.count = data[`subType${element.subType}`]
          }
          for (let index = 0; index < this.recordList.length; index++) {
            const element = this.recordList[index]
            element.count = data[`subType${element.subType}`]
          }
          for (let index = 0; index < this.leaveList.length; index++) {
            const element = this.leaveList[index]
            element.count = data[`subType${element.subType}`]
          }
        })
        .catch(() => {
        })
    },
    /** 附件上传 */
    fileUpload(val) {
      // val.file
      this.$wkUploadFile.upload({
        request: adminFileUploadAPI,
        file: val.file
      }).then(completeData => {
        this.loading = true
        const item = this.allData[this.showDetailIndex].list[this.showDetailSubIndex]
        hrmEmployeeFileAddAPI({
          employeeId: this.id,
          subType: item.subType,
          fileId: completeData.res.data.fileId
        }).then(res => {
          this.loading = false
          this.getSubDetail()
        }).catch(() => {
          this.loading = false
        })
      }).catch(() => {})
    },

    /** 没有附件时候的查看 */
    checkDetail(bigItem, bigIndex, item, index) {
      bigItem.name.push(item.name)
      this.showDetailIndex = bigIndex
      this.showDetailSubIndex = index

      this.getSubDetail()
    },
    previewDetail(bigItem, bigIndex, item, index) {
      this.loading = true
      hrmEmployeeFileQueryBySubTypeAPI({
        employeeId: this.id,
        subType: item.subType
      })
        .then(res => {
          this.loading = false
          var previewList = res.data.map(element => {
            return element.file
          })
          this.$wkPreviewFile.preview({
            index: 0,
            data: previewList
          })
        })
        .catch(() => {
          this.loading = false
        })
    },
    /**
     * 获取小分类下的数组
     */
    getSubDetail() {
      this.loading = true
      const item = this.allData[this.showDetailIndex].list[this.showDetailSubIndex]
      hrmEmployeeFileQueryBySubTypeAPI({
        employeeId: this.id,
        subType: item.subType
      })
        .then(res => {
          this.detailList = res.data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    // 执行编辑操作
    handleDetailItemClick(type, item, index) {
      if (type == 'edit') {
        this.editDetailItem = item
        this.editDetailIndex = index
        this.editInputContent = item.file.name
        var self = this
        setTimeout(() => {
          self.$refs.editInput[0].focus()
        }, 100)
      } else if (type == 'delete') {
        this.$confirm('您确定要删除此附件吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmEmployeeFileDeleteAPI(item.employeeFileId)
              .then(res => {
                this.detailList.splice(index, 1)
                this.$message.success('删除成功')
              })
              .catch(() => {})
          })
          .catch(() => {})
      } else if (type == 'download') {
        adminFileDownAPI(item.fileId).then(res => {
          downloadFileWithBuffer(res.data, item.file.name)
        }).catch(() => {
        })
      }
    },
    /** 编辑输入完成 */
    editInputBlur() {
      this.loading = true
      adminFileRenameFileAPI({
        fileId: this.editDetailItem.fileId,
        name: this.editInputContent
      })
        .then(res => {
          this.editDetailIndex = -1
          this.editDetailItem.file.name = this.editInputContent
          this.editInputContent = ''
          this.editDetailItem = {}
          this.$message.success('编辑成功')
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    /** 敲击面包屑 */
    onTitlesClick(data) {
      var bigItem = this.allData[data.index]
      if (bigItem.name.length > 1) {
        bigItem.name = bigItem.name.splice(0, 1)
        this.showDetailIndex = -1
        this.showDetailSubIndex = -1
        this.detailList = []
      }
    },
    // 预览图片
    previewFile(item, index) {
      var previewList = this.detailList.map(element => {
        return element.file
      })
      this.$wkPreviewFile.preview({
        index: index,
        data: previewList
      })
    },
    getFileTypeIcon(url) {
      const temps = url ? url.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return getFileIconWithSuffix(ext)
    },
    isShowImage(name) {
      const temps = name ? name.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return ['jpg', 'png', 'gif', 'jpeg'].includes(ext.toLowerCase())
    },
    // 控制展示闭合
    unfloadFiles(item, index) {
      item.unfold = !item.unfold
    }
  }
}
</script>

<style lang="scss" scoped>
.contianer {
  position: relative;
}

.file-sections {
  margin-top: 25px;
}

.files-box-class {
  position: relative;
  padding: 16px 8px;
}

.files-box-detail {
  position: relative;
  padding: 16px 8px;
}

/** 列表cell */
.cell {
  position: relative;
  padding-right: 40px;
  padding-bottom: 20px;

  .cell-box {
    position: relative;
    padding: 15px;
    text-align: center;
    cursor: pointer;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .cell-handle {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      z-index: 2;
      display: none;
      background-color: rgba(0, 0, 0, 0.3);

      .handle-items {
        margin-top: 35px;
        text-align: center;

        .handle-item {
          display: inline-block;
          width: 26px;
          height: 26px;
          line-height: 26px;
          cursor: pointer;
          background-color: rgba(0, 0, 0, 0.5);
          border-radius: 13px;

          i {
            color: white;
          }
        }

        .handle-item + .handle-item {
          margin-left: 10px;
        }
      }
    }

    .cell-body {
      height: 50px;
      padding-top: 10px;

      .cell-body-icon {
        margin-top: 15px;
        font-size: 30px;
        color: $--color-primary;
      }
    }

    .cell-footer {
      padding-top: 5px;
    }
  }

  .cell-box:hover {
    .cell-handle {
      display: block;
    }
  }

  .cell-unfold-box {
    position: relative;
    height: 98px;
  }
}

.unfold-button {
  width: auto;
  padding: 5px 0;
  cursor: pointer;

  .unfold-button-icon {
    margin-right: 5px;
    font-weight: 600;
    color: $--color-primary;
  }

  .unfold-button-name {
    color: $--color-primary;
  }

  .more-button-name {
    color: $--color-primary;
  }

  .more-button-icon {
    margin-left: 5px;
    font-weight: 600;
    color: $--color-primary;
  }
}

.unfold-button-positon {
  position: absolute;
  bottom: 0;
  left: 0;
}

/** 详情cell */
.detail-cell {
  position: relative;
  padding-right: 40px;
  padding-bottom: 20px;

  /** 详情 */
  .cell-upload-box {
    padding: 5px;
    border: 1px solid $--border-color-base;
    border-radius: 2px;

    .upload-icon {
      display: inline-block;
      margin-top: 25px;
      font-size: 40px;
      color: #777;
    }

    .upload-name {
      margin-top: 5px;
      color: #777;
    }
  }

  .detail-cell-box {
    padding: 5px;
    border: 1px solid $--border-color-base;
    border-radius: 2px;

    .detail-cell-body {
      position: relative;
      overflow: hidden;
      border-radius: 2px;

      span {
        display: block;
        width: 100%;
        text-align: center;

        img {
          width: auto;
          height: 100px;
          min-height: 100px;
          overflow: hidden;
          border-style: none;
        }
      }

      .body-handle {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        z-index: 2;
        display: none;
        background-color: rgba(0, 0, 0, 0.5);

        .handle-items {
          margin-top: 35px;
          text-align: center;

          .handle-item {
            display: inline-block;
            width: 30px;
            height: 30px;
            line-height: 30px;
            cursor: pointer;
            background-color: rgba(0, 0, 0, 0.5);
            border-radius: 15px;

            i {
              color: white;
            }
          }
        }
      }
    }

    .detail-cell-body:hover {
      .body-handle {
        display: block;
      }
    }

    .detail-cell-footer {
      padding-top: 5px;

      .footer-name {
        display: flex;
        flex: 1;
        width: 130px;
        height: 14px;
        margin-right: 5px;
        overflow: hidden;
        color: $--color-text-primary;
        text-overflow: ellipsis;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
      }

      .el-input {
        ::v-deep input {
          height: 24px;
          line-height: 24px;
        }
      }

      .footer-item {
        flex-shrink: 0;
        padding: 3px;
        color: #777;
        cursor: pointer;

        &:hover {
          color: $--color-primary;
        }
      }
    }
  }
}

/** 拖拽上传 */
.cell-upload-box ::v-deep .el-upload {
  display: block;
}

.cell-upload-box ::v-deep .el-upload-dragger:hover {
  border-color: $--color-primary;
}

.cell-upload-box ::v-deep .el-upload-dragger {
  width: 100%;
  height: 131px;
  border-radius: 2px;
}
</style>
