/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : wk_hrm_open

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 17/10/2023 10:40:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid` ASC, `branch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for wk_admin_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_file`;
CREATE TABLE `wk_admin_file`  (
  `file_id` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '附件名称',
  `size` bigint NOT NULL COMMENT '附件大小（字节）',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件真实路径',
  `file_type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'file' COMMENT '文件类型,file,img',
  `type` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件上传类型',
  `source` int NULL DEFAULT NULL COMMENT '来源 0 默认 1 admin 2 crm 3 work 4 oa 5 进销存 6 hrm',
  `is_public` int NULL DEFAULT 0 COMMENT '1 公有访问 0 私有访问',
  `batch_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '批次id',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `batch_id`(`batch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_admin_file
-- ----------------------------

-- ----------------------------
-- Table structure for wk_admin_menu
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_menu`;
CREATE TABLE `wk_admin_menu`  (
  `menu_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` int UNSIGNED NULL DEFAULT 0 COMMENT '上级菜单ID',
  `menu_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单名称',
  `realm` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '权限标识',
  `realm_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限URL',
  `realm_module` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属模块',
  `menu_type` int NULL DEFAULT NULL COMMENT '菜单类型  1目录 2 菜单 3 按钮 4特殊',
  `sort` int UNSIGNED NULL DEFAULT 0 COMMENT '排序（同级有效）',
  `status` int NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单说明',
  `project_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1普通项目 2敏捷项目',
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `menu_id`(`menu_id` ASC) USING BTREE,
  INDEX `parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `realm`(`realm` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1341 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_admin_menu
-- ----------------------------
INSERT INTO `wk_admin_menu` VALUES (3, 0, '全部', 'manage', NULL, NULL, 1, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (5, 0, '全部', 'hrm', NULL, NULL, 1, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (160, 3, '企业首页', 'system', NULL, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (161, 160, '查看', 'read', '', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (800, 5, '员工管理', 'employee', '', NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (801, 800, '新建', 'save', '/hrmEmployee/addEmployee', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (802, 800, '编辑', 'update', '/hrmEmployeePost/updatePostInformation,/hrmEmployee/setEduExperience,/hrmEmployee/addExperience,/hrmEmployee/deleteEduExperience,/hrmEmployee/addWorkExperience,/hrmEmployee/setWorkExperience,/hrmEmployee/deleteWorkExperience,/hrmEmployee/addCertificate,/hrmEmployee/setCertificate,/hrmEmployee/deleteCertificate,/hrmEmployee/addTrainingExperience,/hrmEmployee/setTrainingExperience,/hrmEmployee/deleteTrainingExperience,/hrmEmployee/addContacts,/hrmEmployee/setContacts,/hrmEmployee/deleteContacts,/hrmEmployeeContract/addContract,/hrmEmployeeContract/setContract,/hrmEmployeeContract/deleteContract,/SocialSecurity/setSalaryCard,/SocialSecurity/setSocialSecurity,/hrmEmployeeFile/addFile,/hrmEmployeeFile/deleteFile', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (803, 800, '查看列表', 'index', '/hrmEmployee/queryPageList', NULL, 3, 3, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (804, 800, '查看详情', 'read', '/hrmEmployee/queryById,/hrmEmployeePost/postInformation,/hrmEmployee/personalInformation,/hrmEmployeeContract/contractInformation,/hrmEmployee/SocialSecurity/salarySocialSecurityInformation,/hrmEmployeeFile/queryFileNum', NULL, 3, 4, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (805, 800, '导入', 'excelimport', '/hrmEmployee/uploadExcel', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (806, 800, '导出', 'excelexport', '/hrmEmployee/export', NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (807, 800, '删除', 'delete', '/hrmEmployee/deleteByIds', NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (808, 800, '办理转正', 'become', '/hrmEmployee/become', NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (809, 800, '调整部门/岗位', 'changePost', '/hrmEmployee/changePost', NULL, 3, 9, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (810, 800, '晋升/降级', 'promotion', '/hrmEmployee/promotion', NULL, 3, 10, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (811, 800, '办理离职', 'leave', '/hrmEmployeePost/addLeaveInformation', NULL, 3, 11, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (812, 800, '设置参保方案', 'setInsured', '/hrmEmployee/updateInsuranceScheme', NULL, 3, 12, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (813, 800, '再入职', 'againOnboarding', '/hrmEmployee/againOnboarding', NULL, 3, 13, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (814, 800, '确认入职', 'confirmEntry', '/hrmEmployee/confirmEntry', NULL, 3, 13, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (815, 800, '放弃离职', 'cancelLevel', '/hrmEmployeePost/deleteLeaveInformation', NULL, 3, 14, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (830, 5, '组织管理', 'dept', '', NULL, 1, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (831, 830, '新建', 'save', '/hrmDept/addDept', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (832, 830, '编辑', 'update', '/hrmDept/setDept', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (833, 830, '查看列表', 'index', '', NULL, 3, 3, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (834, 830, '查看详情', 'read', '/hrmDept/queryById', NULL, 3, 4, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (835, 830, '删除', 'delete', '/hrmDept/deleteDeptById', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (840, 5, '薪资管理', 'salary', '', NULL, 1, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (841, 840, '工资表维护', 'manage', '/hrmSalaryMonthRecord/computeSalaryData,/hrmSalaryMonthRecord/updateSalary,/hrmSalaryMonthRecord/submitExamine,/hrmSalaryMonthRecord/addNextMonthSalary', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (842, 840, '查看薪酬列表', 'index', '/hrmSalaryOption/querySalaryOptionDetail', NULL, 3, 2, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (843, 840, '查看历史工资', 'history', '/hrmSalaryHistoryRecord/queryHistorySalaryList', NULL, 3, 3, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (844, 840, '发放工资条', 'sendSlip', '/hrmSalarySlipRecord/sendSalarySlip', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (845, 840, '查看发放记录', 'queryRecord', '', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (846, 840, '查看薪资档案', 'queryArchives', '/hrmSalaryArchives/querySalaryArchivesList', NULL, 3, 6, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (847, 840, '维护薪资档案', 'updateArchives', '/hrmSalaryArchives/setFixSalaryRecord,/hrmSalaryArchives/setChangeSalaryRecord,/deleteChangeSalary/setChangeSalaryRecord,/deleteChangeSalary/batchChangeSalaryRecord,/deleteChangeSalary/exportFixSalaryRecord', NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (850, 5, '社保管理', 'insurance', '', NULL, 1, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (851, 850, '维护社保', 'manage', '/hrmInsuranceMonthRecord/computeInsuranceData,/hrmInsuranceMonthEmpRecord/stop,/hrmInsuranceMonthEmpRecord/updateInsuranceProject', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (852, 850, '查看社保', 'read', '/hrmInsuranceMonthRecord/queryInsuranceRecordList', NULL, 3, 2, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (860, 5, '招聘管理', 'recruit', '', NULL, 1, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (861, 860, '新建候选人', 'save', '/hrmRecruitCandidate/addCandidate', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (862, 860, '查看候选人', 'read', '/hrmRecruitCandidate/queryPageList,/hrmRecruitCandidate/queryById', NULL, 3, 2, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (863, 860, '维护候选人', 'manage', '/hrmRecruitCandidate/setCandidate,/hrmRecruitCandidate/updateCandidateStatus,/hrmRecruitCandidate/updateCandidatePost,/hrmRecruitCandidate/updateCandidateRecruitChannel,/hrmRecruitCandidate/eliminateCandidate', NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (864, 860, '删除候选人', 'delete', '/hrmRecruitCandidate/deleteByIds,/hrmRecruitCandidate/deleteById', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (865, 860, '新建招聘职位', 'savePost', '/hrmRecruitPost/addRecruitPost', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (866, 860, '编辑招聘职位', 'updatePost', '/hrmRecruitPost/setRecruitPost', NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (867, 860, '查看招聘职位', 'readPost', '/hrmRecruitPost/queryRecruitPostPageList,/hrmRecruitPost/queryById', NULL, 3, 7, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (868, 860, '停用/启用招聘职位', 'updatePostStatus', '/hrmRecruitPost/updateRecruitPostStatus', NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (880, 5, '绩效管理', 'appraisal', '', NULL, 1, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (881, 880, '新建绩效', 'save', '/hrmRecruitPost/addAppraisal', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (882, 880, '编辑绩效', 'update', '/hrmRecruitPost/setAppraisal', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (883, 880, '查看绩效', 'read', '', NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (884, 880, '删除绩效', 'delete', '/hrmRecruitPost/delete', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (885, 880, '终止绩效', 'stop', '/hrmRecruitPoststopAppraisal', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (886, 880, '查看员工绩效', 'readEmp', '', NULL, 3, 3, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (890, 5, '考勤管理', 'attendance', '', NULL, 1, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (891, 890, '查看打卡记录', 'readClock', '', NULL, 3, 1, 1, 'label-92', NULL);
INSERT INTO `wk_admin_menu` VALUES (892, 890, '导出打卡记录', 'excelexport', '/hrmAttendanceClock/excelExport', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (900, 3, '人力资源管理', 'hrm', NULL, NULL, 1, 10, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (901, 900, '自定义字段设置', 'field', '/hrmConfig/queryFields,/hrmConfig/saveField', NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (902, 900, '薪资设置', 'salary', '/hrmSalaryGroup/querySalaryGroupPageList,/hrmSalaryGroup/addSalaryGroup,/hrmSalaryGroup/setSalaryGroup', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (903, 900, '社保设置', 'insurance', '/hrmConfig/addInsuranceScheme', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (904, 900, '绩效设置', 'appraisal', '', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (905, 900, '业务参数设置', 'params', '/hrmConfig/queryRecruitChannelList', NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (906, 900, '员工管理设置', 'archives', '', NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (923, 3, '初始化', 'init', NULL, NULL, 1, 13, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (924, 923, '初始化管理', 'initData', '/adminConfig/moduleInitData', NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (927, 301, '管理参与人权限', 'manageTaskOwnerUser', '', NULL, 3, 29, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (935, 166, '角色权限查看', 'read', NULL, NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (937, 936, '新建', 'save', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (938, 936, '编辑', 'update', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (939, 936, '查看列表', 'index', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (940, 936, '查看详情', 'read', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (941, 936, '删除', 'delete', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (959, 944, '编辑', 'update', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (960, 944, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (961, 944, '新增', 'save', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (962, 944, '导入', 'import', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (963, 944, '删除', 'delete', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (964, 944, '导出', 'export', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (965, 945, '编辑', 'update', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (966, 945, '新增', 'save', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (967, 945, '删除', 'delete', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (968, 946, '编辑', 'update', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (969, 946, '新增', 'save', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (970, 946, '删除', 'delete', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (971, 946, '排序', 'sort', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (972, 947, '编辑', 'update', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (973, 947, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (974, 948, '编辑', 'update', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (975, 948, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (976, 948, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (977, 949, '新增', 'save', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (978, 949, '编辑', 'update', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (979, 949, '查看', 'read', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (980, 949, '删除', 'delete', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (981, 949, '导出', 'export', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (982, 949, '打印', 'print', NULL, NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (983, 949, '导入', 'import', NULL, NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (984, 949, '插入', 'insert', NULL, NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (985, 949, '整理', 'arrangement', NULL, NULL, 3, 9, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (986, 950, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (987, 950, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (989, 951, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (990, 951, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (992, 952, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (993, 952, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (995, 953, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (996, 953, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (998, 954, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (999, 954, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1000, 955, '查看', 'read', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1001, 955, '打印', 'print', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1002, 955, '导出', 'export', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1003, 955, '编辑', 'update', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1004, 956, '编辑', 'update', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1005, 956, '查看', 'read', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1006, 956, '导出', 'export', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1007, 956, '打印', 'print', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1008, 957, '打印', 'print', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1009, 957, '导出', 'export', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1010, 957, '查看', 'read', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1011, 957, '编辑', 'update', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1012, 958, '生成结转凭证', 'generate', NULL, NULL, 4, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1013, 958, '结转损益', 'profitAndLoss', NULL, NULL, 4, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1014, 958, '结账', 'checkOut', NULL, NULL, 4, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1015, 958, '反结账', 'cancelClosing', NULL, NULL, 4, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1017, 1016, '账套管理', 'accountSet', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1018, 949, '审核', 'examine', NULL, NULL, 3, 10, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1019, 949, '反审核', 'noExamine', NULL, NULL, 3, 11, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1024, 1201, '查看考核计划列表', 'queryPageList', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1122, 1118, '自定义字段设置', 'setting', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1131, 176, '日志模板设置', 'oaLogTempalte', '/oaLogTemplate/addTemplate,/oaLogTemplate/updateLogTemplateStatus', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1132, 176, '日志打印模板设置', 'oaLogPrintTempalte', '/oaLogPrintTemplate/copyTemplate', NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1133, 176, '办公审批打印模板设置', 'oaExaminePrintTempalte', '/oaExaminePrintTemplate/add,/oaExaminePrintTemplate/update,/oaExaminePrintTemplate/delete,/oaExaminePrintTemplate/copyTemplate', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1136, 163, '应用导入', 'importApp', NULL, NULL, 3, 11, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1137, 163, '应用导出', 'exportApp', NULL, NULL, 3, 12, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1139, 1138, '新建', 'save', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1140, 1138, '编辑', 'update', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1141, 1138, '删除', 'delete', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1143, 1142, '新建', 'save', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1144, 1142, '编辑', 'update', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1145, 1142, '删除', 'delete', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1146, 152, '访问项目协同', 'read', NULL, NULL, 2, 0, 1, NULL, '2');
INSERT INTO `wk_admin_menu` VALUES (1147, 152, '编辑迭代', 'editIteration', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1148, 152, '删除迭代', 'deleteIteration', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1149, 152, '编辑事项', 'editMatters', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1150, 152, '删除事项', 'deletMatters', '', NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1154, 1151, '项目协同配置', 'editCoordination', NULL, NULL, 2, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1158, 1157, '访问项目概况', 'viewDescription', NULL, NULL, 2, 0, 1, NULL, '2');
INSERT INTO `wk_admin_menu` VALUES (1159, 1157, '编辑项目概况', 'editDescription', NULL, NULL, 4, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1161, 1160, '访问项目公告', 'viewAnnouncement', NULL, NULL, 2, 0, 1, NULL, '2');
INSERT INTO `wk_admin_menu` VALUES (1162, 1160, '编辑项目公告', 'editAnnouncement', NULL, NULL, 4, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1163, 1160, '删除项目公告', 'deleteAnnouncement', NULL, NULL, 4, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1164, 1151, '编辑项目基本信息', 'editProjectInfo', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1165, 1151, '成员管理', 'memberManage', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1166, 1151, '成员权限配置', 'memberPermissionsConfig', NULL, NULL, 3, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1167, 1151, '项目协作配置', 'synergyConfig', NULL, NULL, 2, 0, 1, NULL, '1');
INSERT INTO `wk_admin_menu` VALUES (1180, 1119, '自定义角色设置', 'setRole', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1181, 1119, '状态设置', 'setStatus', NULL, NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1201, 5, 'KPI考核', 'appraisalPlan', NULL, NULL, 1, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1202, 5, '绩效档案', 'appraisalArchives', NULL, NULL, 1, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1203, 5, 'KPI考核设置', 'appraisalSetting', NULL, NULL, 1, 9, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1204, 5, '考核结果设置', 'resultSetting', NULL, NULL, 1, 10, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1205, 1201, '新建KPI考核计划', 'addAppraisalPlan', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1206, 1201, '删除考核', 'delAppraisalPlan', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1207, 1201, '检查并开启考核', 'checkAppraisalPlan', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1208, 1201, '查看考核设置', 'querySetting', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1209, 1201, '新增考核人员', 'addAppraisalEmployeeList', NULL, NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1210, 1201, '移除', 'delAppraisalEmployee', NULL, NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1211, 1201, '终止考核', 'terminationPlan', NULL, NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1212, 1201, '考核结果', 'queryAppraisalResultPageList', NULL, NULL, 3, 9, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1213, 1201, '开启评分', 'openScoring', NULL, NULL, 3, 10, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1214, 1201, '发起绩效面谈', 'toInterview', NULL, NULL, 3, 11, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1215, 1201, '归档', 'placeOnFile', NULL, NULL, 3, 12, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1216, 1201, '考核详情', 'quotaInformation', NULL, NULL, 3, 13, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1217, 1202, '查看绩效档案列表', 'queryAppraisalFileList', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1218, 1202, '查看详情', 'queryEmployeeAppraisalList', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1219, 1202, '查看考核计划详情', 'queryAppraisalInformation', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1220, 1203, '查看KPI考核模板列表', 'queryPageList', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1221, 1203, '新建模板', 'saveTemplate', NULL, NULL, NULL, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1222, 1203, '删除', 'delTemplate', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1223, 1203, '编辑', 'updateTemplate', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1224, 1204, '查看考核结果模板列表', 'queryPageList', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1225, 1204, '新建结果模板', 'addResultTemplate', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1226, 1204, '删除', 'delTemplate', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1227, 1204, '编辑', 'updateResultTemplate', NULL, NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1228, 1201, '删除已归档考核计划', 'delAppraisalPlanOfFile', NULL, NULL, 3, 14, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1229, 1202, '删除员工考核记录', 'delAppraisalFileRecordList', NULL, NULL, 3, 15, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1312, 3, '系统日志', 'systemLog', NULL, NULL, 1, 16, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1313, 1312, '登录日志', 'loginLog', NULL, NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1315, 1314, '新建', 'save', '/save', NULL, 3, 1, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1316, 1314, '查看列表', 'list', '/queryList', NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1317, 1314, '查看详情', 'detail', '/queryInteractiveFlow', NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1318, 1314, '导出', 'export', '/exportExcel', NULL, 3, 4, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1319, 1314, '操作', 'operation', '/operation', NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1330, 1325, '管理', 'index', '', NULL, 3, 0, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1331, 900, '考勤规则设置', 'attendanceRule', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1332, 890, '同步打卡记录', 'punching', NULL, NULL, 3, 2, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1333, 890, '新建打卡记录', 'addClock', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1334, 890, '查看请假记录', 'readLeaveRecord', NULL, NULL, 3, 5, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1335, 890, '导出请假记录', 'exportLeaveRecord', NULL, NULL, 3, 6, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1336, 890, '查看月度汇总', 'readMonthRecord', NULL, NULL, 3, 7, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1337, 890, '导出月度汇总', 'exportMonthRecord', NULL, NULL, 3, 8, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1338, 900, '计薪设置', 'computeSalary', NULL, NULL, 3, 3, 1, NULL, NULL);
INSERT INTO `wk_admin_menu` VALUES (1339, 900, '工资表设置', 'optionSalary', NULL, NULL, 3, 4, 1, NULL, NULL);

-- ----------------------------
-- Table structure for wk_admin_message
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_message`;
CREATE TABLE `wk_admin_message`  (
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `label` int NULL DEFAULT NULL COMMENT '消息大类 1 任务 2 日志 3 oa审批 4公告 5 日程 6 crm消息 7 知识库 8 人资',
  `type` int NULL DEFAULT NULL COMMENT '消息类型 详见AdminMessageEnum',
  `type_id` bigint NULL DEFAULT NULL COMMENT '关联ID',
  `create_user` bigint NOT NULL COMMENT '消息创建者 0为系统',
  `recipient_user` bigint NOT NULL COMMENT '接收人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `is_read` int NULL DEFAULT 0 COMMENT '是否已读 0 未读 1 已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  `create_user_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批类型',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `recipient_user`(`recipient_user` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wk_admin_message
-- ----------------------------

-- ----------------------------
-- Table structure for wk_admin_role
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_role`;
CREATE TABLE `wk_admin_role`  (
  `role_id` bigint NOT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `role_type` int NULL DEFAULT NULL COMMENT '0、自定义角色1、管理角色 2、客户管理角色 3、人事角色 4、财务角色 5、项目角色 8、项目自定义角色',
  `sorting` int NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
  `data_type` int NOT NULL DEFAULT 5 COMMENT '数据权限 1、本人，2、本人及下属，3、本部门，4、本部门及下属部门，5、全部 ',
  `is_hidden` int NOT NULL DEFAULT 1 COMMENT '0 隐藏 1 不隐藏',
  `label` int NULL DEFAULT NULL COMMENT '1 系统项目管理员角色 2 项目管理角色 3 项目编辑角色 4 项目只读角色',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `role_type`(`role_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_admin_role
-- ----------------------------
INSERT INTO `wk_admin_role` VALUES (1559019593879896074, '超级管理员', 1, 0, 'admin', 1, 5, 1, 5, '2022-08-15 11:30:10', 1559019593470803969, '2022-08-15 11:30:10', 1559019593470803969);
INSERT INTO `wk_admin_role` VALUES (1559019593896673282, '人力资源管理员', 1, 0, NULL, 1, 5, 1, 12, '2022-08-15 11:30:10', 1559019593470803969, '2022-08-15 11:30:10', 1559019593470803969);
INSERT INTO `wk_admin_role` VALUES (1559019593896673297, '公告管理员', 7, 0, NULL, 1, 5, 1, 11, '2022-08-15 11:30:10', 1559019593470803969, '2022-08-15 11:30:10', 1559019593470803969);
INSERT INTO `wk_admin_role` VALUES (1559019593921839107, '上级角色', 9, 0, '上级角色', 1, 5, 1, 92, '2022-08-15 11:30:10', 1559019593470803969, '2022-08-23 10:19:00', 1559371618901528578);
INSERT INTO `wk_admin_role` VALUES (1559019597340196979, '人力资源管理员', 9, 0, NULL, 1, 5, 1, 91, '2022-08-15 11:30:12', 1559019593470803969, '2022-08-15 11:30:12', 1559019593470803969);
INSERT INTO `wk_admin_role` VALUES (1559019597340197331, '默认角色', 1, 0, 'cp', 1, 2, 1, 5, '2022-08-15 11:30:12', 1559019593470803969, '2022-08-15 11:30:12', 1559019593470803969);

-- ----------------------------
-- Table structure for wk_admin_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_role_menu`;
CREATE TABLE `wk_admin_role_menu`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  INDEX `menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色菜单对应关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_admin_role_menu
-- ----------------------------
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340288, 1559019597340197331, 310, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340289, 1559019597340197331, 311, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340290, 1559019597340197331, 312, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340291, 1559019597340197331, 313, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340292, 1559019597340197331, 314, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340293, 1559019597340197331, 315, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340294, 1559019597340197331, 316, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340295, 1559019597340197331, 317, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340296, 1559019597340197331, 318, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340297, 1559019597340197331, 319, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340298, 1559019597340197331, 320, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340299, 1559019597340197331, 321, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340300, 1559019597340197331, 322, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340301, 1559019597340197331, 323, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340302, 1559019597340197331, 324, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340303, 1559019597340197331, 325, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340304, 1559019597340197331, 326, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340305, 1559019597340197331, 327, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340306, 1559019597340197331, 328, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340307, 1559019597340197331, 329, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340308, 1559019597340197331, 330, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340309, 1559019597340197331, 331, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340310, 1559019597340197331, 332, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340311, 1559019597340197331, 333, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340312, 1559019597340197331, 334, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340313, 1559019597340197331, 335, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340314, 1559019597340197331, 336, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340315, 1559019597340197331, 337, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);
INSERT INTO `wk_admin_role_menu` VALUES (1523592245283340316, 1559019597340197331, 927, '2022-05-09 17:14:32', 672, '2022-05-09 17:14:32', NULL);

-- ----------------------------
-- Table structure for wk_admin_user_role
-- ----------------------------
DROP TABLE IF EXISTS `wk_admin_user_role`;
CREATE TABLE `wk_admin_user_role`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户角色对应关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_admin_user_role
-- ----------------------------
INSERT INTO `wk_admin_user_role` VALUES (1559019593879896075, 1559019593470803969, 1559019593879896074, '2022-08-15 11:30:10', 1559019593470803969, '2022-08-15 11:30:10', 1559019593470803969);
INSERT INTO `wk_admin_user_role` VALUES (1559023932864131073, 1559023932788633602, 1559019593879896074, '2022-08-15 11:47:25', 1559019593470803969, '2022-08-15 11:47:25', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559055746676748294, 1559055746609639426, 1559019593879896074, '2022-08-15 13:53:50', 1559019593470803969, '2022-08-15 13:53:50', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559416522646196231, 1559416522646196225, 1559019593879896074, '2022-08-16 13:47:26', 1559019593470803969, '2022-08-16 13:47:26', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559834770063724546, 1559834487271165953, 1559019593921839107, '2022-08-17 17:29:24', 1559371618901528578, '2022-08-17 17:29:24', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559834850887962625, 1559371618901528578, 1559019593879896074, '2022-08-17 17:29:43', 1559371618901528578, '2022-08-17 17:29:43', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559835227989446658, 1559835227926532098, 1559019593921839107, '2022-08-17 17:31:13', 1559371618901528578, '2022-08-17 17:31:13', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559835343219560455, 1559835343219560449, 1559019593921839107, '2022-08-17 17:31:40', 1559371618901528578, '2022-08-17 17:31:40', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1559835907445723143, 1559835907445723137, 1559019597340196931, '2022-08-17 17:33:55', 1559055746609639426, '2022-08-17 17:33:55', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1560819902524518400, 1560819902449020928, 1559019593879896074, '2022-08-20 10:43:58', 1559019593470803969, '2022-08-20 10:43:58', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1561895285340577792, 1561895285277663232, 1559019593879896074, '2022-08-23 09:57:09', 1559371618901528578, '2022-08-23 09:57:09', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1561911523919863808, 1561911523861143552, 1559019593879896074, '2022-08-23 11:01:40', 1559416522646196225, '2022-08-23 11:01:40', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1561964312964366336, 1559835045302341633, 1559019593921839107, '2022-08-23 14:31:26', 1559371618901528578, '2022-08-23 14:31:26', NULL);
INSERT INTO `wk_admin_user_role` VALUES (1561964478316412928, 1559835045302341633, 1559019593896673282, '2022-08-23 14:32:06', 1559371618901528578, '2022-08-23 14:32:06', NULL);

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal`;
CREATE TABLE `wk_hrm_achievement_appraisal`  (
  `appraisal_id` bigint NOT NULL,
  `appraisal_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核名称',
  `cycle_type` int NULL DEFAULT NULL COMMENT '1 月 2 季 3 年 4 半年',
  `start_time` date NULL DEFAULT NULL COMMENT '考核开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '考核结束时间',
  `table_id` bigint NULL DEFAULT NULL COMMENT '考核表模板id',
  `written_by` int NULL DEFAULT 1 COMMENT '考核目标填写人 1 本人',
  `full_score` decimal(7, 2) NULL DEFAULT NULL COMMENT '考评总分数',
  `is_force` int NULL DEFAULT NULL COMMENT '是否开启强制分布 1 是 0 否',
  `appraisal_steps` int NULL DEFAULT -1 COMMENT '考核步骤进度',
  `activate_steps` int NULL DEFAULT -1 COMMENT '进行中步骤进度',
  `status` int NULL DEFAULT 0 COMMENT '绩效状态 0 未开启考核 1 绩效填写中 2 绩效评定中 3 结果确认中 4 归档',
  `is_stop` int NULL DEFAULT 0 COMMENT '是否终止 0 否 1 是',
  `stop_time` datetime NULL DEFAULT NULL COMMENT '终止时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`appraisal_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_evaluators
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_evaluators`;
CREATE TABLE `wk_hrm_achievement_appraisal_evaluators`  (
  `evaluators_id` bigint NOT NULL,
  `appraisal_id` bigint NOT NULL COMMENT '考核id',
  `type` int NOT NULL COMMENT '1 员工本人 2 直属上级 3 所在部门负责人 4 上级部门负责人 5 指定目标确认人',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '指定确认人id',
  `weight` decimal(5, 2) NOT NULL COMMENT '权重',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`evaluators_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核结果评定人' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_evaluators
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_relation_dept
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_relation_dept`;
CREATE TABLE `wk_hrm_achievement_appraisal_relation_dept`  (
  `achievement_appraisal_relation_dept_id` bigint NOT NULL,
  `achievement_appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效考核id',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`achievement_appraisal_relation_dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '绩效考核关联部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_relation_dept
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_relation_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_relation_employee`;
CREATE TABLE `wk_hrm_achievement_appraisal_relation_employee`  (
  `achievement_appraisal_relation_employee_id` bigint NOT NULL,
  `achievement_appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效考核id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`achievement_appraisal_relation_employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '绩效考核关联员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_relation_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_result_confirmors
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_result_confirmors`;
CREATE TABLE `wk_hrm_achievement_appraisal_result_confirmors`  (
  `confirmors_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`confirmors_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '考核结果确认人' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_result_confirmors
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_score_level
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_score_level`;
CREATE TABLE `wk_hrm_achievement_appraisal_score_level`  (
  `level_id` bigint NOT NULL,
  `appraisal_id` bigint NOT NULL COMMENT '考核id',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '等级名称',
  `min_score` decimal(7, 2) NULL DEFAULT NULL COMMENT '最小分数',
  `max_score` decimal(7, 2) NOT NULL COMMENT '最大分数',
  `min_num` int NOT NULL COMMENT '最小人数比例',
  `max_num` int NOT NULL COMMENT '最大人数比例',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考评规则等级' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_score_level
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_appraisal_target_confirmors
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_appraisal_target_confirmors`;
CREATE TABLE `wk_hrm_achievement_appraisal_target_confirmors`  (
  `target_confirmors_id` bigint NOT NULL,
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '考核id',
  `type` int NULL DEFAULT NULL COMMENT '1 员工本人 2 直属上级 3 所在部门负责人 4 上级部门负责人 5 指定目标确认人',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '指定确认人id',
  `sort` int NULL DEFAULT NULL COMMENT '步骤号 从小到大',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`target_confirmors_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核目标确认人' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_appraisal_target_confirmors
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_appraisal
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_appraisal`;
CREATE TABLE `wk_hrm_achievement_employee_appraisal`  (
  `employee_appraisal_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效id',
  `status` int NULL DEFAULT NULL COMMENT '考核状态 1 待填写 2 待目标确认 3 待评定 4 待结果确认 5 终止绩效 6 考核完成',
  `score` double(10, 2) NULL DEFAULT NULL COMMENT '评分',
  `level_id` bigint NULL DEFAULT NULL COMMENT '考核结果',
  `read_status` int NULL DEFAULT 0 COMMENT '结果阅读状态 0 未读 1 已读',
  `follow_up_employee_id` bigint NULL DEFAULT NULL COMMENT '跟进员工id',
  `follow_sort` int NULL DEFAULT NULL COMMENT '跟进员工排序',
  `is_draft` int NULL DEFAULT 0 COMMENT '是否为草稿 0否 1是',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`employee_appraisal_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_appraisal
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_evaluato
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_evaluato`;
CREATE TABLE `wk_hrm_achievement_employee_evaluato`  (
  `employee_evaluato_id` bigint NOT NULL,
  `employee_appraisal_id` bigint NULL DEFAULT NULL COMMENT '员工端考核id',
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效id',
  `employee_id` bigint NOT NULL COMMENT '确认人',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '权重',
  `score` decimal(7, 2) NULL DEFAULT NULL COMMENT '评分',
  `level_id` bigint NULL DEFAULT NULL COMMENT '考核等级',
  `evaluate` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评语',
  `reject_reason` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `sort` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0 COMMENT '0 待评定 1 已评定',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`employee_evaluato_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效结果评定表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_evaluato
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_evaluato_seg
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_evaluato_seg`;
CREATE TABLE `wk_hrm_achievement_employee_evaluato_seg`  (
  `employee_evaluato_seg_id` bigint NOT NULL,
  `employee_appraisal_id` bigint NULL DEFAULT NULL COMMENT '员工端考核id',
  `employee_evaluato_id` bigint NULL DEFAULT NULL COMMENT '结果评定id',
  `seg_id` bigint NULL DEFAULT NULL COMMENT '考核项id',
  `employee_id` bigint NOT NULL COMMENT '评定人',
  `score` decimal(7, 2) NULL DEFAULT NULL COMMENT '评分',
  `evaluate` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评语',
  `status` int NULL DEFAULT 1 COMMENT '0 待评定 1 已评定',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`employee_evaluato_seg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核项评定表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_evaluato_seg
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_result_confirmors
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_result_confirmors`;
CREATE TABLE `wk_hrm_achievement_employee_result_confirmors`  (
  `confirmors_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL,
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效id',
  `status` int NULL DEFAULT 0 COMMENT '0 未确认 1 已确认',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`confirmors_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效结果确认表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_result_confirmors
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_seg
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_seg`;
CREATE TABLE `wk_hrm_achievement_employee_seg`  (
  `seg_id` bigint NOT NULL,
  `employee_appraisal_id` bigint NULL DEFAULT NULL,
  `temp_seg_id` bigint NULL DEFAULT 0 COMMENT '模板考核项id',
  `employee_id` bigint NULL DEFAULT NULL,
  `seg_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核项名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值',
  `is_fixed` int NULL DEFAULT NULL COMMENT '是否固定 1 是 0 否',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '权重 -1 员工写权重比 0~100',
  `schedule` int NULL DEFAULT 0 COMMENT '目标进度',
  `explain_desc` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '完成情况说明',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`seg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_seg
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_seg_item
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_seg_item`;
CREATE TABLE `wk_hrm_achievement_employee_seg_item`  (
  `item_id` bigint NOT NULL,
  `seg_id` bigint NULL DEFAULT NULL,
  `temp_item_id` bigint NULL DEFAULT 0 COMMENT '模板考核项id',
  `item_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工考核项选项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_seg_item
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_employee_target_confirm
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_employee_target_confirm`;
CREATE TABLE `wk_hrm_achievement_employee_target_confirm`  (
  `employee_confirm_id` bigint NOT NULL,
  `employee_appraisal_id` bigint NULL DEFAULT NULL COMMENT '员工端考核id',
  `appraisal_id` bigint NULL DEFAULT NULL COMMENT '绩效id',
  `employee_id` bigint NOT NULL COMMENT '确认人',
  `reject_reason` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `sort` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0 COMMENT '0 待确认 1 已确认 2 驳回 ',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`employee_confirm_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工考核目标确认表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_employee_target_confirm
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievement_seg
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_seg`;
CREATE TABLE `wk_hrm_achievement_seg`  (
  `seg_id` bigint NOT NULL,
  `table_id` bigint NULL DEFAULT NULL,
  `seg_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核项名称',
  `is_fixed` int NULL DEFAULT NULL COMMENT '是否固定 1 是 0 否',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '权重 -1 员工写权重比 0~100',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`seg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核项模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_seg
-- ----------------------------
INSERT INTO `wk_hrm_achievement_seg` VALUES (1481534121625661538, 1481534121625661536, '关键绩效（KP）', 0, NULL, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg` VALUES (1481534121625661539, 1481534121625661537, '目标（O）', 0, NULL, 1, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_achievement_seg_item
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_seg_item`;
CREATE TABLE `wk_hrm_achievement_seg_item`  (
  `item_id` bigint NOT NULL,
  `seg_id` bigint NULL DEFAULT NULL,
  `item_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项名称',
  `sort` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核项选项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_seg_item
-- ----------------------------
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661540, 1481534121625661538, 'KPI指标1', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661541, 1481534121625661538, 'KPI指标2', 2, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661542, 1481534121625661538, 'KPI指标3', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661543, 1481534121625661539, '关键结果（kr）', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661544, 1481534121625661539, '关键结果（kr）', 2, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_seg_item` VALUES (1481534121625661545, 1481534121625661539, '关键结果（kr）', 3, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_achievement_table
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievement_table`;
CREATE TABLE `wk_hrm_achievement_table`  (
  `table_id` bigint NOT NULL,
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核名称',
  `type` int NULL DEFAULT NULL COMMENT '1 OKR模板 2 KPI模板',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '考核表描述',
  `is_emp_weight` int NULL DEFAULT 0 COMMENT '是否员工填写权重 0 否 1 是',
  `status` int NULL DEFAULT 1 COMMENT ' 1 使用 0 删除',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核表模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievement_table
-- ----------------------------
INSERT INTO `wk_hrm_achievement_table` VALUES (1481534121625661536, 'KPI模板', 2, '1.结果导向原则：以业绩目标考核为主，员工要时刻关注岗位目标、充分理解团队目标，个人目标要支撑团队目标。\n2.关键绩效（KP）由本岗位岗位职责、重点工作、团队目标等分解而来。\n3.KPI为衡量该关键绩效（KP）的关键业绩指标，要目标明确、可量化、易计算。\n4.固定项考核指公司可根据实际情况设定固定考核指标，适用于考核范围内所有人员，员工不可编辑 。例如：行为态度类考核、能力素质类考核等。', 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_achievement_table` VALUES (1481534121625661537, 'OKR模板', 1, '1.OKR的主要目标是明确公司和团队的“目标”以及明确每个目标达成的可衡量的“关键结果”。\n2.个人首先要充分理解团队整体目标，个人目标的设定要支撑团队目标。\n3.本着“挑战导向”原则，目标一定要具有挑战性，超越当前现状，可衡量，可分解；关键成果是支持目标落地的具体事项，也要可衡量。\n4.目标一般不超过5项，每个目标一般拆解为1-3个关键成果，权重总和为100%。', 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_achievements_assessment_dimension
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievements_assessment_dimension`;
CREATE TABLE `wk_hrm_achievements_assessment_dimension`  (
  `dimension_id` bigint NOT NULL COMMENT '考核维度id',
  `dimension_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '维度名称',
  `quota_type` int NULL DEFAULT NULL COMMENT '指标类型',
  `dimension_weight` double NULL DEFAULT NULL COMMENT '维度权重',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_allow_edit` bit(1) NULL DEFAULT NULL COMMENT '允许员工填写1：是 0：否',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板id',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '修改人',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  PRIMARY KEY (`dimension_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效管理-考核模板-维度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievements_assessment_dimension
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievements_assessment_dimension_quota
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievements_assessment_dimension_quota`;
CREATE TABLE `wk_hrm_achievements_assessment_dimension_quota`  (
  `quota_id` bigint NOT NULL COMMENT '维度指标id',
  `quota_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标名称',
  `quota_illustrate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标说明',
  `standard` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核标准',
  `quota_weight` double NULL DEFAULT NULL COMMENT '指标权重',
  `score_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分方式',
  `dimension_id` bigint NULL DEFAULT NULL COMMENT '维度id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  PRIMARY KEY (`quota_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效管理-考核模板-维度指标表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievements_assessment_dimension_quota
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievements_assessment_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievements_assessment_template`;
CREATE TABLE `wk_hrm_achievements_assessment_template`  (
  `template_id` bigint NOT NULL COMMENT '考核模板id',
  `template_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `template_illustrate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板说明',
  `score_calculation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '总分计算',
  `upper_limit_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评分上限类型',
  `upper_limit_score` int NOT NULL COMMENT '评分上限',
  `dimension_num` int NULL DEFAULT NULL COMMENT '维度数量',
  `quota_num` int NULL DEFAULT NULL COMMENT '指标数量',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '修改人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int NULL DEFAULT NULL COMMENT '1:正常 0:删除',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效管理-考核模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievements_assessment_template
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievements_result_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievements_result_template`;
CREATE TABLE `wk_hrm_achievements_result_template`  (
  `result_template_id` bigint NOT NULL COMMENT '结果模板id',
  `result_template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果设置名称',
  `level_setting` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级设置',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  `status` int NOT NULL COMMENT '状态：1：正常 0：删除',
  PRIMARY KEY (`result_template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效管理-结果模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievements_result_template
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_achievements_result_template_level
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_achievements_result_template_level`;
CREATE TABLE `wk_hrm_achievements_result_template_level`  (
  `level_id` bigint NOT NULL COMMENT '结果模板等级id',
  `level_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '等级名称',
  `score_lower_limit` double NOT NULL COMMENT '分数下限',
  `score_upper_limit` double NOT NULL COMMENT '分数上限',
  `coefficient` double NULL DEFAULT NULL COMMENT '系数',
  `result_template_id` bigint NOT NULL COMMENT '结果模板id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新日期',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效管理-结构模板等级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_achievements_result_template_level
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_action_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_action_record`;
CREATE TABLE `wk_hrm_action_record`  (
  `id` bigint NOT NULL,
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `type` int NULL DEFAULT NULL COMMENT '操作类型 1 员工 2 招聘管理 3 候选人 4 绩效管理',
  `type_id` bigint NULL DEFAULT NULL COMMENT '操作对象id',
  `behavior` int NULL DEFAULT NULL COMMENT '操作行为 1 新建 2 编辑 3 删除 4 转正 5 调岗 6 晋升 7 降级 8 转全职员工 9 离职 10 参保方案',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `trans_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '翻译内容',
  `create_user_id` bigint NOT NULL COMMENT '操作人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `types`(`type` ASC, `type_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'hrm员工操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_action_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_action_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_action_record`;
CREATE TABLE `wk_hrm_appraisal_action_record`  (
  `id` bigint NOT NULL,
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `type` int NULL DEFAULT NULL COMMENT '操作类型 1 员工 2 招聘管理 3 候选人 4 绩效管理 5：KPI考核计划',
  `type_id` bigint NULL DEFAULT NULL COMMENT '操作对象id',
  `behavior` int NULL DEFAULT NULL COMMENT '操作行为 1 新建 2 编辑 3 删除 4 转正 5 调岗 6 晋升 7 降级 8 转全职员工 9 离职 10 参保方案',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `trans_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '翻译内容',
  `batch_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `create_user_id` bigint NOT NULL COMMENT '操作人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `week` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周几',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `types`(`type` ASC, `type_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核审核记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_action_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_appeal_pending_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_appeal_pending_record`;
CREATE TABLE `wk_hrm_appraisal_appeal_pending_record`  (
  `pending_record_id` bigint NOT NULL COMMENT '待处理记录id',
  `appraisal_employee_id` bigint NOT NULL COMMENT '员工考核绩效id',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `stage_id` bigint NOT NULL COMMENT '待处理节点id',
  `overdue_type` int NOT NULL COMMENT '超期未处理类型1：超期未审批拒绝2：超期未审批通过',
  `overdue_time` datetime NOT NULL COMMENT '逾期日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`pending_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核申诉确认待处理节点记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_appeal_pending_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee`;
CREATE TABLE `wk_hrm_appraisal_employee`  (
  `appraisal_employee_id` bigint NOT NULL COMMENT '绩效考核员工考核信息id',
  `appraisal_plan_id` bigint NOT NULL COMMENT '考核计划id',
  `employee_id` bigint NOT NULL COMMENT '考核员工id',
  `appraisal_status` int NULL DEFAULT NULL COMMENT '状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档 5:考核终止',
  `activate_status` int NULL DEFAULT NULL COMMENT '当前阶段处理状态1进行中 2：已完成',
  `stage_status` int NULL DEFAULT NULL COMMENT '阶段状态：0：未开始 1：员工填写2：自评分 3：他人评分 4:目标确认5：结果审核6：结果确认 7：归档',
  `stage_sort` int NULL DEFAULT NULL COMMENT '当前阶段进度',
  `score` int NULL DEFAULT NULL COMMENT '评分',
  `level` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核等级 S,A,B,C,未定级',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `file_time` datetime NULL DEFAULT NULL COMMENT '归档时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`appraisal_employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核-考核绩效基本信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_appeal_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_appeal_record`;
CREATE TABLE `wk_hrm_appraisal_employee_appeal_record`  (
  `appeal_record_id` bigint NOT NULL COMMENT '申诉记录id',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工绩效考核id',
  `appraisal_stage_id` bigint NULL DEFAULT NULL COMMENT '申诉评分节点',
  `status` int NULL DEFAULT NULL COMMENT '状态0：未处理 1：已处理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`appeal_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'KPI考核-员工绩效申诉记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_appeal_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_dimension
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_dimension`;
CREATE TABLE `wk_hrm_appraisal_employee_dimension`  (
  `dimension_id` bigint NOT NULL COMMENT '考核维度id',
  `dimension_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '维度名称',
  `quota_type` int NULL DEFAULT NULL COMMENT '指标类型',
  `dimension_weight` double NULL DEFAULT NULL COMMENT '维度权重',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_allow_edit` bit(1) NULL DEFAULT b'0' COMMENT '允许员工填写1：是 0：否',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工考核计划id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  PRIMARY KEY (`dimension_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工端-考核维度' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_dimension
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_quota
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_quota`;
CREATE TABLE `wk_hrm_appraisal_employee_quota`  (
  `quota_id` bigint NOT NULL COMMENT '员工填写指标id',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工考核计划id',
  `dimension_id` bigint NULL DEFAULT NULL COMMENT '维度id',
  `quota_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标名称',
  `quota_illustrate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标说明',
  `standard` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核标准',
  `quota_weight` double NULL DEFAULT NULL COMMENT '指标权重',
  `score_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分方式',
  `preset` bit(1) NULL DEFAULT b'0' COMMENT '是否系统预设1：是 0：否',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`quota_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核-填写指标项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_quota
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_quota_score
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_quota_score`;
CREATE TABLE `wk_hrm_appraisal_employee_quota_score`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `score_id` bigint NOT NULL COMMENT '评分id',
  `quota_id` bigint NULL DEFAULT NULL COMMENT '指标id',
  `score` bigint NULL DEFAULT NULL COMMENT '评分',
  `comments` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评语',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工考核计划id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `rater` bigint NULL DEFAULT NULL COMMENT '评分人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 555 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工考核计划评分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_quota_score
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_real_score_user
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_real_score_user`;
CREATE TABLE `wk_hrm_appraisal_employee_real_score_user`  (
  `real_id` bigint NOT NULL COMMENT '主键id',
  `score_user_id` bigint NULL DEFAULT NULL COMMENT '评分人id',
  `weight` double NULL DEFAULT NULL COMMENT '评分权重',
  `required_setting` bit(1) NULL DEFAULT NULL COMMENT '必填设置',
  `reject_authority` bit(1) NULL DEFAULT NULL COMMENT '驳回权限',
  `visible_content` int NULL DEFAULT NULL COMMENT '可见内容',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '考核员工id',
  `stage_sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`real_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'KPI绩效考核-员工考核流程配置实际评分人列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_real_score_user
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_reject_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_reject_record`;
CREATE TABLE `wk_hrm_appraisal_employee_reject_record`  (
  `reject_id` bigint NOT NULL COMMENT '驳回主键di',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工绩效id',
  `reject_stage` int NULL DEFAULT NULL COMMENT '驳回阶段 1：评分阶段',
  `reject_record_id` bigint NULL DEFAULT NULL COMMENT '驳回记录id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '驳回人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int NULL DEFAULT NULL COMMENT '处理状态:0未处理 1已处理',
  PRIMARY KEY (`reject_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效考核-驳回记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_reject_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_score
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_score`;
CREATE TABLE `wk_hrm_appraisal_employee_score`  (
  `score_id` bigint NOT NULL COMMENT 'id',
  `appraisal_employee_id` bigint NOT NULL COMMENT '员工考核计划id',
  `comments` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评语',
  `rater` bigint NULL DEFAULT NULL COMMENT '评分人id',
  `score` double NULL DEFAULT NULL COMMENT '得分',
  `level` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级',
  `weight` double NULL DEFAULT NULL COMMENT '权重',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`score_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工考核计划-评分' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_score
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_employee_stage
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_employee_stage`;
CREATE TABLE `wk_hrm_appraisal_employee_stage`  (
  `appraisal_stage_id` bigint NOT NULL COMMENT '绩效考核阶段id',
  `appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '员工绩效id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '考核员工id',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `stage_type` int NULL DEFAULT NULL COMMENT '阶段状态：0：未开始 1：员工填写2:目标确认3：自评分 4：他人评分 5：结果审核6：结果确认 7：归档',
  `stage_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阶段名称',
  `stage_user_id` bigint NULL DEFAULT NULL COMMENT '当前处理人名称',
  `weight` double NULL DEFAULT NULL COMMENT '评分权重，只有评分阶段会有',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `status` int NULL DEFAULT NULL COMMENT '状态：0：未处理 1已处理 2待处理 3：驳回 4：重新处理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`appraisal_stage_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'KPI考核-员工考核流程阶段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_employee_stage
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan`;
CREATE TABLE `wk_hrm_appraisal_plan`  (
  `appraisal_plan_id` bigint NOT NULL COMMENT '考核计划id',
  `appraisal_plan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核计划名称',
  `employ_type` int NULL DEFAULT NULL COMMENT '聘用形式1 正式 2 非正式',
  `employee_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '员工类型',
  `appraisal_cycle_type` int NULL DEFAULT NULL COMMENT '考核周期类型1 月 2 季 3 年 4 半年 5:全年 6:其他',
  `appraisal_cycle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核周期',
  `quarter` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '季度',
  `start_time` date NULL DEFAULT NULL COMMENT '考核开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '考核结束时间',
  `appraisal_illustrate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核说明',
  `appraisal_template_id` bigint NULL DEFAULT NULL COMMENT '考核模板id',
  `sync_to_salary` bit(1) NULL DEFAULT NULL COMMENT '是否同步到薪资',
  `paid_for_month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步薪资月份',
  `result_template_id` bigint NULL DEFAULT NULL COMMENT '引用的结果模板id',
  `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `stage_status` int NULL DEFAULT NULL COMMENT '阶段状态：0：未开始 1：员工填写 2:目标确认3：自评分 4：他人评分 5：结果审核6：结果确认 7：归档',
  `status` int NOT NULL COMMENT '状态 0：删除 1：草稿 2:未开始 3：进行中 4：已归档 5:考核终止',
  `operation_stage` int NULL DEFAULT NULL COMMENT '进行中可操作阶段状态： 1：开启评分 2：发起绩效面谈3：归档',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`appraisal_plan_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划基础信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_employee_type
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_employee_type`;
CREATE TABLE `wk_hrm_appraisal_plan_employee_type`  (
  `employee_type_id` bigint NOT NULL COMMENT '主键id',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `employee_type` int NULL DEFAULT NULL COMMENT '聘用形式',
  `employee_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '员工状态',
  PRIMARY KEY (`employee_type_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划设置-考核范围-聘用形式范围表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_employee_type
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_inspection_scope
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_inspection_scope`;
CREATE TABLE `wk_hrm_appraisal_plan_inspection_scope`  (
  `inspection_scope_id` bigint NOT NULL COMMENT '考核范围id',
  `type` int NULL DEFAULT NULL COMMENT '新建考核计划-考核范围类型1:员工 2:部门',
  `record_id` bigint NULL DEFAULT NULL COMMENT '员工或部门id',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`inspection_scope_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-考核范围配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_inspection_scope
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_process_setting
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_process_setting`;
CREATE TABLE `wk_hrm_appraisal_plan_process_setting`  (
  `process_id` bigint NOT NULL COMMENT '流程id',
  `quota_setting_type` int NULL DEFAULT NULL COMMENT '指标制定类型1:统一制定2：员工填写',
  `target_confirmation` bit(1) NOT NULL DEFAULT b'0' COMMENT '目标确认：1：开启 0:未开启',
  `result_audit` bit(1) NULL DEFAULT NULL COMMENT '结果审核',
  `result_confirmation` bit(1) NULL DEFAULT NULL COMMENT '结果确认',
  `be_overdue_type` int NULL DEFAULT NULL COMMENT '超期未处理类型:1未审批拒绝 2：未审批通过',
  `overdue_days` int NULL DEFAULT NULL COMMENT '超期未处理天数',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  PRIMARY KEY (`process_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-流程配置-基础表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_process_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_quota_setting
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_quota_setting`;
CREATE TABLE `wk_hrm_appraisal_plan_quota_setting`  (
  `quota_setting_id` bigint NOT NULL COMMENT '指标id',
  `score_calculation` int NULL DEFAULT NULL COMMENT '总分计算方式:1:加权计算',
  `upper_limit_type` int NOT NULL COMMENT '评分上限类型1:统一上限',
  `upper_limit_score` double NULL DEFAULT NULL COMMENT '评分上限',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '考核计划id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`quota_setting_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-指标配置-基础表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_quota_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_relation_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_relation_employee`;
CREATE TABLE `wk_hrm_appraisal_plan_relation_employee`  (
  `appraisal_plan_relation_employee_id` bigint NOT NULL COMMENT '关联id',
  `appraisal_plan_id` bigint NULL DEFAULT NULL COMMENT '绩效考核id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `status` int NULL DEFAULT NULL COMMENT '实际开启的时候员工状态是否正常1：正常0：异常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`appraisal_plan_relation_employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核关联员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_relation_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_appraisal_plan_result_setting
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_appraisal_plan_result_setting`;
CREATE TABLE `wk_hrm_appraisal_plan_result_setting`  (
  `level_id` bigint NOT NULL COMMENT '等级id',
  `level_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '等级名称',
  `score_lower_limit` double NOT NULL COMMENT '分数下限',
  `score_upper_limit` double NOT NULL COMMENT '分数上限',
  `coefficient` double NULL DEFAULT NULL COMMENT '系数',
  `appraisal_plan_id` bigint NOT NULL COMMENT '考核计划id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新日期',
  `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核结果等级列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_appraisal_plan_result_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_clock
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_clock`;
CREATE TABLE `wk_hrm_attendance_clock`  (
  `clock_id` bigint NOT NULL COMMENT '打卡记录id',
  `clock_employee_id` bigint NULL DEFAULT NULL,
  `clock_time` datetime NOT NULL COMMENT '打卡时间',
  `clock_type` int NOT NULL COMMENT '打卡类型 1 上班打卡 2 下班打卡',
  `attendance_time` datetime NOT NULL COMMENT '上班时间（正常打卡时间）',
  `type` int NOT NULL DEFAULT 1 COMMENT '打卡来源类型 1手机端打卡 2手工录入3.自动打卡',
  `clock_status` int NULL DEFAULT 0 COMMENT '打卡状态 0 正常 1 迟到 2 早退  4 加班',
  `clock_stage` int NULL DEFAULT 1 COMMENT '打卡阶段',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考勤地址',
  `lng` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '经度',
  `lat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '维度',
  `ssid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'wifi名称',
  `mac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mac地址',
  `is_out_work` int NULL DEFAULT 0 COMMENT '是否外勤（0否 1是）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`clock_id`) USING BTREE,
  INDEX `index_attendance_clock_clock_employee_id`(`clock_employee_id` ASC) USING BTREE,
  INDEX `index_attendance_clock_clock_time`(`clock_time` ASC) USING BTREE,
  INDEX `index_attendance_clock_attendance_time`(`attendance_time` ASC) USING BTREE,
  INDEX `index_attendance_clock_type`(`type` ASC) USING BTREE,
  INDEX `index_attendance_clock_stage`(`clock_stage` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_clock
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_date_shift
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_date_shift`;
CREATE TABLE `wk_hrm_attendance_date_shift`  (
  `user_shift_id` bigint NOT NULL COMMENT '用户班次id',
  `user_shift_time` datetime NOT NULL COMMENT '出勤时间',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `shift_id` bigint NOT NULL COMMENT '班次id',
  `shift_type` int NULL DEFAULT 0 COMMENT '班次类型（0 休息 1早晚打卡 2 分段打卡）',
  `shift_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班次名称',
  `shift_hours` int NULL DEFAULT 0 COMMENT '班次时长（分钟）',
  `start1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间1',
  `end1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间1',
  `start2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间2',
  `end2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间2',
  `start3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间3',
  `end3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间3',
  `advance_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间1',
  `late_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间1',
  `advance_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间2',
  `late_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间2',
  `advance_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间3',
  `late_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间3',
  `early_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间1',
  `early_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间2',
  `postpone_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间1',
  `postpone_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间2',
  `early_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间3',
  `postpone_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间3',
  `rest_time_status` int NULL DEFAULT 0 COMMENT '是否设置休息时间（0否 1是）',
  `rest_start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息开始时间',
  `rest_end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息结束时间',
  `is_default_setting` int NULL DEFAULT 0 COMMENT '是否是默认配置（0否 1是）',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_shift_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日出勤班次' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_date_shift
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_examine
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_examine`;
CREATE TABLE `wk_hrm_attendance_examine`  (
  `attendance_examine_id` bigint NOT NULL COMMENT '审批id',
  `examine_field_id` bigint NULL DEFAULT NULL COMMENT '关联审批字段id',
  `type_field_id` bigint NULL DEFAULT NULL COMMENT '类型字段id',
  `start_time_field_id` bigint NULL DEFAULT NULL COMMENT '开始时间字段id',
  `end_time_field_id` bigint NULL DEFAULT NULL COMMENT '结束时间字段id',
  `duration_field_id` bigint NULL DEFAULT NULL COMMENT '天数字段id',
  `remark_field_id` bigint NULL DEFAULT NULL COMMENT '备注字段id',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_examine_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤审批设置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_examine
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_group
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_group`;
CREATE TABLE `wk_hrm_attendance_group`  (
  `attendance_group_id` bigint NOT NULL COMMENT '考勤组id',
  `old_group_id` bigint NULL DEFAULT NULL COMMENT '考勤组初始化id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `daily_time` decimal(10, 2) NULL DEFAULT NULL COMMENT '工作时长',
  `attendance_rule_id` bigint NULL DEFAULT NULL COMMENT '扣款规则id',
  `is_open_wifi_card` int NULL DEFAULT 0 COMMENT '是否开启wifi打卡',
  `is_open_point_card` int NULL DEFAULT 0 COMMENT '是否开启定位打卡',
  `is_auto_card` int NULL DEFAULT 0 COMMENT '是否自动打卡（0否 1是）',
  `shift_setting` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '考勤班组设置',
  `is_rest` int NULL DEFAULT 1 COMMENT '是否法定节假日休息（0否 1是）',
  `special_date_setting` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '特殊日期设置',
  `is_default_setting` int NULL DEFAULT 0 COMMENT '是否是默认配置（0否 1是）',
  `old_setting` int NULL DEFAULT 0 COMMENT '是否历史配置 (0否 1是)',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考勤组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_group
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_group_relation_dept
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_group_relation_dept`;
CREATE TABLE `wk_hrm_attendance_group_relation_dept`  (
  `attendance_group_relation_dept_id` bigint NOT NULL,
  `attendance_group_id` bigint NULL DEFAULT NULL COMMENT '考勤组id',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_group_relation_dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '考勤组关联部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_group_relation_dept
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_group_relation_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_group_relation_employee`;
CREATE TABLE `wk_hrm_attendance_group_relation_employee`  (
  `attendance_group_relation_employee_id` bigint NOT NULL,
  `attendance_group_id` bigint NULL DEFAULT NULL COMMENT '考勤组id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_group_relation_employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '考勤组关联员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_group_relation_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_history_shift
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_history_shift`;
CREATE TABLE `wk_hrm_attendance_history_shift`  (
  `shift_history_id` bigint NOT NULL COMMENT '历史班次id',
  `shift_id` bigint NOT NULL COMMENT '班次id',
  `shift_type` int NULL DEFAULT 0 COMMENT '班次类型（0 休息 1早晚打卡 2 分段打卡）',
  `shift_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班次名称',
  `shift_hours` int NULL DEFAULT 0 COMMENT '班次时长（分钟）',
  `start1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间1',
  `end1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间1',
  `start2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间2',
  `end2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间2',
  `start3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间3',
  `end3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间3',
  `advance_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间1',
  `late_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间1',
  `advance_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间2',
  `late_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间2',
  `advance_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间3',
  `late_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间3',
  `early_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间1',
  `postpone_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间1',
  `early_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间2',
  `postpone_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间2',
  `early_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间3',
  `postpone_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间3',
  `rest_time_status` int NULL DEFAULT 0 COMMENT '是否设置休息时间（0否 1是）',
  `rest_start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息开始时间',
  `rest_end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息结束时间',
  `is_default_setting` int NULL DEFAULT 0 COMMENT '是否是默认配置（0否 1是）',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`shift_history_id`) USING BTREE,
  INDEX `index_shift_type`(`shift_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史班次表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_history_shift
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_legal_holidays
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_legal_holidays`;
CREATE TABLE `wk_hrm_attendance_legal_holidays`  (
  `holiday_id` bigint NOT NULL,
  `holiday_time` datetime NULL DEFAULT NULL COMMENT '假期时间',
  `type` int NULL DEFAULT 2 COMMENT '类型 1.上班 2.休息',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`holiday_id`) USING BTREE,
  INDEX `index_holiday_time`(`holiday_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考勤法定节假日' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_legal_holidays
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_point
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_point`;
CREATE TABLE `wk_hrm_attendance_point`  (
  `attendance_point_id` bigint NOT NULL COMMENT '打卡地点id',
  `attendance_group_id` bigint NULL DEFAULT NULL COMMENT '考勤组id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地点名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定位名称',
  `lat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '纬度',
  `lng` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '经度',
  `point_range` int NULL DEFAULT NULL COMMENT '范围（米）',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_point_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打卡地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_point
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_rule
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_rule`;
CREATE TABLE `wk_hrm_attendance_rule`  (
  `attendance_rule_id` bigint NOT NULL COMMENT '打卡规则id',
  `attendance_rule_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打卡规则名称',
  `late_rule_method` int NOT NULL COMMENT '迟到规则计算方式',
  `late_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '迟到扣款金额',
  `early_rule_method` int NOT NULL COMMENT '早退规则计算方式',
  `early_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '早退扣款金额',
  `misscard_rule_method` int NOT NULL COMMENT '缺卡规则计算方式',
  `misscard_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '缺卡扣款金额',
  `absenteeism_rule_method` int NOT NULL COMMENT '旷工规则计算方式',
  `absenteeism_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '旷工扣款金额',
  `is_personalization` int NULL DEFAULT 0 COMMENT '是否个性化设置(0 否 1是)',
  `late_minutes_or_counts` int NULL DEFAULT 0 COMMENT '迟到的总分钟或总次数',
  `early_minutes_or_counts` int NULL DEFAULT 0 COMMENT '早退的总分钟或总次数',
  `is_default_setting` int NULL DEFAULT 0 COMMENT '是否是默认配置（0否 1是）',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_rule_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打卡规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_rule
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_shift
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_shift`;
CREATE TABLE `wk_hrm_attendance_shift`  (
  `shift_id` bigint NOT NULL COMMENT '班次id',
  `shift_type` int NULL DEFAULT 0 COMMENT '班次类型（0 休息 1早晚打卡 2 分段打卡）',
  `shift_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班次名称',
  `shift_hours` int NULL DEFAULT 0 COMMENT '班次时长（分钟）',
  `start1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间1',
  `end1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间1',
  `start2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间2',
  `end2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间2',
  `start3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班时间3',
  `end3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班时间3',
  `advance_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间1',
  `late_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间1',
  `advance_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间2',
  `late_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间2',
  `advance_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最早打卡时间3',
  `late_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上班最晚打卡时间3',
  `early_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间1',
  `postpone_card1` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间1',
  `early_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间2',
  `postpone_card2` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间2',
  `early_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最早打卡时间3',
  `postpone_card3` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '下班最晚打卡时间3',
  `rest_time_status` int NULL DEFAULT 0 COMMENT '是否设置休息时间（0否 1是）',
  `rest_start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息开始时间',
  `rest_end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '休息结束时间',
  `is_default_setting` int NULL DEFAULT 0 COMMENT '是否是默认配置（0否 1是）',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`shift_id`) USING BTREE,
  INDEX `index_shift_type`(`shift_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班次表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_shift
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_attendance_wifi
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_attendance_wifi`;
CREATE TABLE `wk_hrm_attendance_wifi`  (
  `attendance_wifi_id` bigint NOT NULL COMMENT '打卡wifiid',
  `attendance_group_id` bigint NULL DEFAULT NULL COMMENT '考勤组id',
  `ssid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'wifi名称',
  `mac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mac地址',
  `create_user_id` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attendance_wifi_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打卡wifi表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_attendance_wifi
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_config`;
CREATE TABLE `wk_hrm_config`  (
  `config_id` bigint NOT NULL,
  `type` int NULL DEFAULT NULL COMMENT '配置类型 1 淘汰原因 2 薪资初始化配置1 3 薪资初始化配置2 4 社保初始化配置1 5 社保初始化配置2',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '人力资源配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_config
-- ----------------------------
INSERT INTO `wk_hrm_config` VALUES (1481534121625661549, 2, '0', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121625661550, 3, '0', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121625661551, 4, '0', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121625661552, 5, '0', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121625661553, 1, '沟通表达能力差', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121625661554, 1, '候选人放弃', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121629855744, 1, '薪资要求过高', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121629855745, 1, '稳定性较差', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_config` VALUES (1481534121629855746, 1, '相关经验少', 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_dept
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_dept`;
CREATE TABLE `wk_hrm_dept`  (
  `dept_id` bigint NOT NULL,
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID 顶级部门为0',
  `dept_type` int NULL DEFAULT NULL COMMENT '1 公司 2 部门',
  `name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '部门名称',
  `code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '部门编码',
  `main_employee_id` bigint NULL DEFAULT NULL COMMENT '部门负责人ID',
  `leader_employee_id` bigint NULL DEFAULT NULL COMMENT '分管领导',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_dept
-- ----------------------------
INSERT INTO `wk_hrm_dept` VALUES (1481534121629855747, 0, 1, '测试企业', '1', NULL, NULL, 0, '2022-01-13 15:50:34', NULL, '2022-01-13 15:50:34');

-- ----------------------------
-- Table structure for wk_hrm_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee`;
CREATE TABLE `wk_hrm_employee`  (
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `employee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '员工姓名',
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
  `country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家地区',
  `nation` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '民族',
  `id_type` int NULL DEFAULT NULL COMMENT '证件类型 1 身份证 2 港澳通行证 3 台湾通行证 4 护照 5 其他',
  `id_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '证件号码',
  `sex` int NULL DEFAULT NULL COMMENT '性别 1 男 2 女',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `native_place` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '籍贯',
  `date_of_birth` datetime NULL DEFAULT NULL COMMENT '出生日期',
  `birthday_type` int NULL DEFAULT 1 COMMENT '生日类型 1 阳历 2 农历',
  `birthday` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生日 示例：0323',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '户籍地址',
  `highest_education` int NULL DEFAULT NULL COMMENT '最高学历',
  `entry_time` datetime NULL DEFAULT NULL COMMENT '入职时间',
  `probation` int NULL DEFAULT NULL COMMENT '试用期 0 无试用期',
  `become_time` datetime NULL DEFAULT NULL COMMENT '转正日期',
  `job_number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '直属上级ID',
  `post` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职位',
  `post_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位职级',
  `work_address` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作地点',
  `work_detail_address` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作详细地址',
  `work_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作城市',
  `channel_id` bigint NULL DEFAULT NULL COMMENT '招聘渠道',
  `employment_forms` int NULL DEFAULT NULL COMMENT '聘用形式 1 正式 2 非正式',
  `status` int NULL DEFAULT NULL COMMENT '员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包',
  `company_age_start_time` datetime NULL DEFAULT NULL COMMENT '司龄开始日期',
  `company_age` int NULL DEFAULT 0 COMMENT '司龄',
  `entry_status` int NULL DEFAULT NULL COMMENT '入职状态 1 在职 2 待入职 3 待离职 4 离职',
  `candidate_id` bigint NULL DEFAULT NULL COMMENT '候选人id',
  `is_del` int NULL DEFAULT 0 COMMENT '0 未删除 1 删除',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`employee_id`) USING BTREE,
  INDEX `wk_hrm_employee_job_number_index`(`job_number` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_abnormal_change_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_abnormal_change_record`;
CREATE TABLE `wk_hrm_employee_abnormal_change_record`  (
  `change_record_id` bigint NOT NULL,
  `type` int NULL DEFAULT NULL COMMENT '异动类型 1 新入职 2 离职 3 转正 4 调岗',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '异动员工id',
  `change_time` datetime NULL DEFAULT NULL COMMENT '异动时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`change_record_id`) USING BTREE,
  INDEX `wk_hrm_employee_abnormal_change_record_type_index`(`type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工异常表动记录表（薪资列表统计需要）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_abnormal_change_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_achievement_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_achievement_file`;
CREATE TABLE `wk_hrm_employee_achievement_file`  (
  `achievement_file_id` bigint NOT NULL COMMENT '绩效档案id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `recently_appraisal_employee_id` bigint NULL DEFAULT NULL COMMENT '最近员工考核绩效id',
  `appraisal_count` int NULL DEFAULT NULL COMMENT '考核次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`achievement_file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工绩效档案' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_achievement_file
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_candidate
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_candidate`;
CREATE TABLE `wk_hrm_employee_candidate`  (
  `id` bigint NOT NULL COMMENT 'id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `candidate_id` bigint NULL DEFAULT NULL COMMENT '候选人id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工候选人关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_candidate
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_certificate
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_certificate`;
CREATE TABLE `wk_hrm_employee_certificate`  (
  `certificate_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `certificate_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证书名称',
  `certificate_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证书级别',
  `certificate_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证书编号',
  `start_time` datetime NULL DEFAULT NULL COMMENT '有效起始日期',
  `end_time` datetime NULL DEFAULT NULL COMMENT '有效结束日期',
  `issuing_authority` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发证机构',
  `issuing_time` datetime NULL DEFAULT NULL COMMENT '发证日期',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `sort` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`certificate_id`) USING BTREE,
  INDEX `wk_hrm_employee_certificate_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工证书' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_certificate
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_change_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_change_record`;
CREATE TABLE `wk_hrm_employee_change_record`  (
  `record_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `change_type` int NULL DEFAULT NULL COMMENT '变动类型 4 转正 5调岗 6晋升 7降级 8转为全职员工',
  `change_reason` int NULL DEFAULT NULL COMMENT '异动原因 1 组织架构调整 2个人申请 3 工作安排 4 违规违纪 5 绩效不达标 6 个人身体原因 7 不适应当前岗位',
  `old_dept` bigint NULL DEFAULT NULL COMMENT '原部门',
  `new_dept` bigint NULL DEFAULT NULL COMMENT '新部门',
  `old_post` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '原岗位',
  `new_post` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新岗位',
  `old_post_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新职级',
  `new_post_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新职级',
  `old_work_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '原工作地点',
  `new_work_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新工作地点',
  `old_parent_id` bigint NULL DEFAULT NULL COMMENT '原直属上级',
  `new_parent_id` bigint NULL DEFAULT NULL COMMENT '新直属上级',
  `probation` int NULL DEFAULT NULL COMMENT '试用期',
  `effect_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `wk_hrm_employee_change_record_change_type_index`(`change_type` ASC) USING BTREE,
  INDEX `wk_hrm_employee_change_record_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工岗位/职位变更记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_change_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_contacts
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_contacts`;
CREATE TABLE `wk_hrm_employee_contacts`  (
  `contacts_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL,
  `contacts_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人名称',
  `relation` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关系',
  `contacts_phone` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人电话',
  `contacts_work_unit` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人工作单位',
  `contacts_post` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系儿职务',
  `contacts_address` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人地址',
  `sort` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`contacts_id`) USING BTREE,
  INDEX `wk_hrm_employee_contacts_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工联系人' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_contacts
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_contacts_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_contacts_data`;
CREATE TABLE `wk_hrm_employee_contacts_data`  (
  `id` bigint NOT NULL,
  `contacts_id` bigint NOT NULL COMMENT 'contacts_id',
  `field_id` bigint NOT NULL,
  `label_group` int NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `field_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段值',
  `field_value_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段值描述',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_employee_contacts_data_contacts_id_index`(`contacts_id` ASC) USING BTREE,
  INDEX `wk_hrm_employee_contacts_data_field_id_index`(`field_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户扩展字段数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_contacts_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_contract
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_contract`;
CREATE TABLE `wk_hrm_employee_contract`  (
  `contract_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `contract_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '合同编号',
  `contract_type` int NULL DEFAULT NULL COMMENT '1、固定期限劳动合同 2、无固定期限劳动合同 3、已完成一定工作任务为期限的劳动合同 4、实习协议 5、劳务合同 6、返聘协议 7、劳务派遣合同 8、借调合同 9、其他',
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `term` int NULL DEFAULT NULL COMMENT '期限',
  `status` int NULL DEFAULT NULL COMMENT '合同状态  0未执行 1 执行中、 2已到期、 ',
  `sign_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '签约公司',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '合同签订日期',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `is_expire_remind` int NULL DEFAULT NULL COMMENT '是否到期提醒 0 否 1 是',
  `sort` int NULL DEFAULT NULL,
  `batch_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`contract_id`) USING BTREE,
  INDEX `wk_hrm_employee_contract_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工合同' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_contract
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_data
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_data`;
CREATE TABLE `wk_hrm_employee_data`  (
  `id` bigint NOT NULL,
  `field_id` bigint NOT NULL,
  `label_group` int NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名称',
  `field_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段值',
  `field_value_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段值描述',
  `employee_id` bigint NOT NULL COMMENT 'employee_id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_employee_data_employee_id_index`(`employee_id` ASC) USING BTREE,
  INDEX `wk_hrm_employee_data_field_id_index`(`field_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户扩展字段数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_data
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_education_experience
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_education_experience`;
CREATE TABLE `wk_hrm_employee_education_experience`  (
  `education_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL,
  `education` int NULL DEFAULT NULL COMMENT '学历 1小学、2初中、3中专、4中职、5中技、6高中、7大专、8本科、9硕士、10博士、11博士后、12其他',
  `graduate_school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '毕业院校',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '专业',
  `admission_time` datetime NULL DEFAULT NULL COMMENT '入学时间',
  `graduation_time` datetime NULL DEFAULT NULL COMMENT '毕业时间',
  `teaching_methods` int NULL DEFAULT NULL COMMENT '教学方式 1 全日制、2成人教育、3远程教育、4自学考试、5其他',
  `is_first_degree` int NULL DEFAULT NULL COMMENT '是否第一学历 0 否 1 是',
  `sort` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`education_id`) USING BTREE,
  INDEX `wk_hrm_employee_education_experience_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工教育经历' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_education_experience
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_field
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_field`;
CREATE TABLE `wk_hrm_employee_field`  (
  `field_id` bigint NOT NULL COMMENT '主键ID',
  `field_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int NOT NULL DEFAULT 1 COMMENT '字段类型 1 单行文本 2 多行文本 3 单选 4日期 5 数字 6 小数 7 手机  8 文件 9 多选   10 日期时间 11 邮箱 12 籍贯地区',
  `component_type` int NOT NULL DEFAULT 0 COMMENT '关联表类型 0 不需要关联 1 hrm员工 2 hrm部门 3 hrm职位 4 系统用户 5 系统部门 6 招聘渠道',
  `label` int NULL DEFAULT NULL COMMENT '标签 1 个人信息 2 岗位信息 3 合同 4 工资社保',
  `label_group` int NOT NULL COMMENT '标签分组 * 1 员工个人信息 2 通讯信息 3 教育经历 4 工作经历 5 证书/证件 6 培训经历 7 联系人\n        * 11 岗位信息 12 离职信息 \n        * 21 合同信息 \n        * 31 工资卡信息 32 社保信息',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int NULL DEFAULT NULL COMMENT '最大长度',
  `default_value` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '默认值',
  `is_unique` int NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int NULL DEFAULT 1 COMMENT '排序 从小到大',
  `options` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '如果类型是选项，此处不能为空，使用kv格式',
  `is_fixed` int NOT NULL DEFAULT 0 COMMENT '是否固定字段 0 否 1 是',
  `operating` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '111111' COMMENT '000000 (1:标题,2:选项,3:必填,4:唯一,5:隐藏,6:删除)',
  `is_hidden` int NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `is_update_value` int NULL DEFAULT 1 COMMENT '是否可以修改值 0 否 1 是',
  `is_head_field` int NULL DEFAULT 0 COMMENT '是否在列表头展示 0 否 1 是',
  `is_import_field` int NULL DEFAULT 0 COMMENT '是否需要导入字段 0 否 1 是',
  `is_employee_visible` int NOT NULL DEFAULT 1 COMMENT '是否员工可见 0 否 1 是',
  `is_employee_update` int NOT NULL DEFAULT 0 COMMENT '是否员工可修改 0 否 1 是 2 禁用否',
  `style_percent` int NULL DEFAULT 50 COMMENT '样式百分比%',
  `precisions` int NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式： 1,1',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数值',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数值',
  `form_assist_id` bigint NULL DEFAULT NULL COMMENT '表单辅助id，前端生成',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`field_id`) USING BTREE,
  INDEX `wk_hrm_employee_field_label_index`(`label` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_field
-- ----------------------------
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467136, 'employee_name', '姓名', 1, 0, 1, 1, '', '', 50, '', 0, 1, 1, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1001, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467139, 'flied_hukzra', '英文名', 1, 1, 1, 1, '', '', 50, '', 0, 0, 2, '', 0, '255', 0, 1, 0, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1002, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467142, 'mobile', '手机', 7, 0, 1, 1, '', '11位手机号', 11, '', 1, 1, 3, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1003, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467145, 'id_type', '证件类型', 3, 0, 1, 1, '', '', 1, '', 0, 0, 4, '[{\"name\":\"身份证\",\"value\":1},{\"name\":\"港澳通行证\",\"value\":2},{\"name\":\"台湾通行证\",\"value\":3},{\"name\":\"护照\",\"value\":4},{\"name\":\"其他\",\"value\":5}]', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1004, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467148, 'id_number', '证件号码', 1, 0, 1, 1, '', '', 255, '', 0, 0, 5, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1005, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467151, 'sex', '性别', 3, 0, 1, 1, '', '', 1, '', 0, 0, 6, '[{\"name\":\"男\",\"value\":1},{\"name\":\"女\",\"value\":2}]', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1006, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467154, 'date_of_birth', '出生日期', 4, 0, 1, 1, '', '', 1, '', 0, 0, 7, '', 1, '48', 0, 0, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1007, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467157, 'birthday_type', '生日类型', 3, 0, 1, 1, '', '', 50, '', 0, 0, 8, '[{\"name\":\"阳历\",\"value\":1},{\"name\":\"农历\",\"value\":2}]', 1, '48', 0, 1, 0, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1008, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467160, 'birthday', '生日', 1, 0, 1, 1, '', '示例 : 0323', 50, '', 0, 0, 9, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1009, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467163, 'age', '年龄', 5, 0, 1, 1, '', '', 50, '', 0, 0, 10, '', 1, '48', 0, 0, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467166, 'flied_bbnpqh', '是否已婚', 3, 0, 1, 1, '', '', 50, '', 0, 0, 11, '[{\"name\":\"是\",\"value\":\"是\"},{\"name\":\"否\",\"value\":\"否\"}]', 0, '191', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1010, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467169, 'flied_dxnkqj', '是否已育', 3, 0, 1, 1, '', '', 50, '', 0, 0, 12, '[{\"name\":\"是\",\"value\":\"是\"},{\"name\":\"否\",\"value\":\"否\"}]', 0, '191', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1011, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467172, 'country', '国家地区', 3, 0, 1, 1, '', '', 50, '', 0, 0, 13, '[{\"name\":\"中国\",\"value\":\"中国\"},{\"name\":\"中国香港\",\"value\":\"中国香港\"},{\"name\":\"中国澳门\",\"value\":\"中国澳门\"},{\"name\":\"中国台湾\",\"value\":\"中国台湾\"}]', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1012, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121621467175, 'nation', '民族', 3, 0, 1, 1, '', '', 50, '', 0, 0, 14, '[{\"name\":\"汉族\",\"value\":\"汉族\"},{\"name\":\"满族\",\"value\":\"满族\"},{\"name\":\"蒙古族\",\"value\":\"蒙古族\"},{\"name\":\"回族\",\"value\":\"回族\"},{\"name\":\"藏族\",\"value\":\"藏族\"},{\"name\":\"维吾尔族\",\"value\":\"维吾尔族\"},{\"name\":\"苗族\",\"value\":\"苗族\"},{\"name\":\"彝族\",\"value\":\"彝族\"},{\"name\":\"壮族\",\"value\":\"壮族\"},{\"name\":\"布依族\",\"value\":\"布依族\"},{\"name\":\"侗族\",\"value\":\"侗族\"},{\"name\":\"瑶族\",\"value\":\"瑶族\"},{\"name\":\"白族\",\"value\":\"白族\"},{\"name\":\"土家族\",\"value\":\"土家族\"},{\"name\":\"哈尼族\",\"value\":\"哈尼族\"},{\"name\":\"哈萨克族\",\"value\":\"哈萨克族\"},{\"name\":\"傣族\",\"value\":\"傣族\"},{\"name\":\"黎族\",\"value\":\"黎族\"},{\"name\":\"傈僳族\",\"value\":\"傈僳族\"},{\"name\":\"佤族\",\"value\":\"佤族\"},{\"name\":\"畲族\",\"value\":\"畲族\"},{\"name\":\"高山族\",\"value\":\"高山族\"},{\"name\":\"拉祜族\",\"value\":\"拉祜族\"},{\"name\":\"水族\",\"value\":\"水族\"},{\"name\":\"东乡族\",\"value\":\"东乡族\"},{\"name\":\"纳西族\",\"value\":\"纳西族\"},{\"name\":\"景颇族\",\"value\":\"景颇族\"},{\"name\":\"柯尔克孜族\",\"value\":\"柯尔克孜族\"},{\"name\":\"土族\",\"value\":\"土族\"},{\"name\":\"达斡尔族\",\"value\":\"达斡尔族\"},{\"name\":\"仫佬族\",\"value\":\"仫佬族\"},{\"name\":\"羌族\",\"value\":\"羌族\"},{\"name\":\"布朗族\",\"value\":\"布朗族\"},{\"name\":\"撒拉族\",\"value\":\"撒拉族\"},{\"name\":\"毛南族\",\"value\":\"毛南族\"},{\"name\":\"仡佬族\",\"value\":\"仡佬族\"},{\"name\":\"锡伯族\",\"value\":\"锡伯族\"},{\"name\":\"阿昌族\",\"value\":\"阿昌族\"},{\"name\":\"普米族\",\"value\":\"普米族\"},{\"name\":\"朝鲜族\",\"value\":\"朝鲜族\"},{\"name\":\"塔吉克族\",\"value\":\"塔吉克族\"},{\"name\":\"怒族\",\"value\":\"怒族\"},{\"name\":\"乌孜别克族\",\"value\":\"乌孜别克族\"},{\"name\":\"俄罗斯族\",\"value\":\"俄罗斯族\"},{\"name\":\"鄂温克族\",\"value\":\"鄂温克族\"},{\"name\":\"德昂族\",\"value\":\"德昂族\"},{\"name\":\"保安族\",\"value\":\"保安族\"},{\"name\":\"裕固族\",\"value\":\"裕固族\"},{\"name\":\"京族\",\"value\":\"京族\"},{\"name\":\"塔塔尔族\",\"value\":\"塔塔尔族\"},{\"name\":\"独龙族\",\"value\":\"独龙族\"},{\"name\":\"鄂伦春族\",\"value\":\"鄂伦春族\"},{\"name\":\"赫哲族\",\"value\":\"赫哲族\"},{\"name\":\"门巴族\",\"value\":\"门巴族\"},{\"name\":\"珞巴族\",\"value\":\"珞巴族\"},{\"name\":\"基诺族\",\"value\":\"基诺族\"}]', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1013, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661441, 'flied_luxpii', '政治面貌', 1, 0, 1, 1, '', '', 50, '', 0, 0, 15, '', 0, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1014, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661444, 'native_place', '籍贯', 54, 7, 1, 1, '', '', 50, '', 0, 0, 16, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1015, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661447, 'address', '户籍所在地', 1, 0, 1, 1, '', '', 255, '', 0, 0, 17, '', 1, '190', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1016, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661450, 'flied_mosheh', '健康状态', 1, 0, 1, 1, '', '', 255, '', 0, 0, 18, '', 0, '254', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1017, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661453, 'highest_education', '最高学历', 3, 0, 1, 1, '', '', 50, '', 0, 0, 19, '[{\"name\":\"小学\",\"value\":1},{\"name\":\"初中\",\"value\":2},{\"name\":\"中专\",\"value\":3},{\"name\":\"中职\",\"value\":4},{\"name\":\"中技\",\"value\":5},{\"name\":\"高中\",\"value\":6},{\"name\":\"大专\",\"value\":7},{\"name\":\"本科\",\"value\":8},{\"name\":\"硕士\",\"value\":9},{\"name\":\"博士\",\"value\":10},{\"name\":\"博士后\",\"value\":11},{\"name\":\"其他\",\"value\":12}]', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, 1018, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661456, 'contacts_name', '联系人姓名', 1, 0, 1, 7, '', '', 255, '', 0, 1, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661457, 'relation', '关系', 1, 0, 1, 7, '', '', 255, '', 0, 0, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661458, 'contacts_phone', '联系人电话', 7, 0, 1, 7, '', '', 255, '', 0, 0, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661459, 'contacts_work_unit', '联系人工作单位', 1, 0, 1, 7, '', '', 255, '', 0, 0, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661460, 'contacts_post', '联系人职务', 1, 0, 1, 7, '', '', 255, '', 0, 0, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661461, 'contacts_address', '联系人地址', 1, 0, 1, 7, '', '', 255, '', 0, 0, 1, '', 1, '62', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661462, 'flied_kwbova', '手机号码', 7, 0, 1, 2, '', '', 255, '', 0, 0, 1, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661463, 'email', '个人邮箱', 14, 0, 1, 2, '', '', 255, '', 0, 0, 2, '', 1, '48', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661464, 'flied_dbwahc', 'QQ', 1, 0, 1, 2, '', '', 255, '', 0, 0, 3, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661465, 'flied_utxiir', '微信', 1, 0, 1, 2, '', '', 255, '', 0, 0, 4, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661466, 'flied_mhktwv', '现居住地', 1, 0, 1, 2, '', '', 255, '', 0, 0, 5, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661467, 'flied_qppedz', '紧急联系人', 1, 0, 1, 2, '', '', 255, '', 0, 0, 6, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661468, 'flied_dumavf', '紧急联系人电话', 7, 0, 1, 2, '', '', 255, '', 0, 0, 7, '', 0, '255', 0, 1, 0, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661469, 'job_number', '工号', 1, 0, 2, 11, '', '', 50, '', 1, 1, 4, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661472, 'entry_time', '入职日期', 4, 0, 2, 11, '', '', 50, '', 0, 1, 1, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661475, 'dept_id', '部门', 12, 2, 2, 11, '', '', 50, '', 0, 0, 5, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661478, 'post', '岗位', 1, 0, 2, 11, '', '', 50, '', 0, 0, 7, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661481, 'parent_id', '直属上级', 10, 1, 2, 11, '', '', 50, '', 0, 0, 6, '', 1, '48', 0, 1, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661484, 'post_level', '职级', 1, 0, 2, 11, '', '', 50, '', 0, 0, 8, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661487, 'work_city', '工作城市', 40, 0, 2, 11, '', '', 255, '', 0, 0, 11, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661490, 'work_address', '工作地点', 1, 0, 2, 11, '', '', 255, '', 0, 0, 9, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661493, 'work_detail_address', '详细工作地点', 1, 0, 2, 11, '', '', 255, '', 0, 0, 10, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661496, 'employment_forms', '聘用形式', 3, 0, 2, 11, '', '', 255, '', 0, 1, 13, '[{\"name\":\"正式\",\"value\":1},{\"name\":\"非正式\",\"value\":2}]', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661499, 'probation', '试用期', 3, 0, 2, 11, '', '', 50, '', 0, 0, 2, '[{\"name\":\"无试用期\",\"value\":0},{\"name\":\"1个月\",\"value\":1},{\"name\":\"2个月\",\"value\":2},{\"name\":\"3个月\",\"value\":3},{\"name\":\"4个月\",\"value\":4},{\"name\":\"5个月\",\"value\":5},{\"name\":\"6个月\",\"value\":6}]', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661502, 'become_time', '转正日期', 4, 0, 2, 11, '', '', 50, '', 0, 0, 3, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661503, 'company_age_start_time', '司龄开始日期', 4, 0, 2, 11, '', '', 255, '', 0, 0, 14, '', 1, '50', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661506, 'company_age', '司龄', 5, 0, 2, 11, '', '', 255, '', 0, 0, 15, '', 1, '48', 0, 0, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661507, 'channel_id', '招聘渠道', 55, 6, 2, 11, '', '', 255, '', 0, 0, 12, '', 1, '62', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661510, 'status', '员工状态', 3, 0, 2, 11, '', '', 255, '', 0, 0, 13, '[{\"name\":\"正式\",\"value\":1},{\"name\":\"试用\",\"value\":2},{\"name\":\"实习\",\"value\":3},{\"name\":\"兼职\",\"value\":4},{\"name\":\"劳务\",\"value\":5},{\"name\":\"顾问\",\"value\":6},{\"name\":\"返聘\",\"value\":7},{\"name\":\"外包\",\"value\":8},{\"name\":\"待离职\",\"value\":9},{\"name\":\"已离职\",\"value\":10}]', 1, '48', 0, 1, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661513, 'contract_type', '合同类型', 3, 0, 3, 21, '', '', 255, '', 0, 0, 1, '[{\"name\":\"固定期限劳动合同\",\"value\":1},{\"name\":\"无固定期限劳动合同\",\"value\":2},{\"name\":\"已完成一定工作任务为期限的劳动合同\",\"value\":3},{\"name\":\"实习协议\",\"value\":4},{\"name\":\"劳务合同\",\"value\":5},{\"name\":\"返聘协议\",\"value\":6},{\"name\":\"劳务派遣合同\",\"value\":7},{\"name\":\"借调合同\",\"value\":8},{\"name\":\"其他\",\"value\":9}]', 1, '48', 0, 1, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661514, 'end_time', '现合同结束日期', 4, 0, 3, 21, '', '', 255, '', 0, 0, 3, '', 1, '48', 0, 1, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661515, 'term', '现合同期限', 5, 0, 3, 21, '', '', 255, '', 0, 0, 4, '', 1, '48', 0, 1, 1, 0, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661516, 'salary_card_num', '工资卡卡号', 1, 0, 4, 31, '', '', 255, '', 0, 0, 1, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661517, 'account_opening_city', '工资卡开户城市', 1, 0, 4, 31, '', '', 255, '', 0, 0, 2, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661518, 'bank_name', '银行名称', 1, 0, 4, 31, '', '', 255, '', 0, 0, 4, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661519, 'opening_bank', '工资卡开户行', 1, 0, 4, 31, '', '', 255, '', 0, 0, 5, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661520, 'social_security_num', '个人社保账号', 1, 0, 4, 32, '', '', 255, '', 0, 0, 1, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field` VALUES (1481534121625661521, 'accumulation_fund_num', '个人公积金账号', 1, 0, 4, 32, '', '', 255, '', 0, 0, 2, '', 1, '48', 0, 1, 1, 1, 1, 0, 50, NULL, NULL, NULL, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_employee_field_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_field_config`;
CREATE TABLE `wk_hrm_employee_field_config`  (
  `id` bigint NOT NULL COMMENT '字段id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '字段排序',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户id',
  `is_hide` int NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `width` int NULL DEFAULT NULL COMMENT '字段宽度',
  `field_id` bigint NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `label`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_field_config
-- ----------------------------
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762365620224, 1, 1711560060463276032, 0, 100, 1481534121621467136, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762369814528, 2, 1711560060463276032, 0, 100, 1481534121621467142, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762369814529, 3, 1711560060463276032, 0, 100, 1481534121621467145, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762369814530, 4, 1711560060463276032, 0, 100, 1481534121621467148, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762369814531, 5, 1711560060463276032, 0, 100, 1481534121621467151, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762374008832, 6, 1711560060463276032, 0, 100, 1481534121621467154, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762374008833, 7, 1711560060463276032, 0, 100, 1481534121621467160, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762374008834, 8, 1711560060463276032, 0, 100, 1481534121621467163, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762374008835, 9, 1711560060463276032, 0, 100, 1481534121621467166, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762378203136, 10, 1711560060463276032, 0, 100, 1481534121621467169, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762378203137, 11, 1711560060463276032, 0, 100, 1481534121621467172, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762378203138, 12, 1711560060463276032, 0, 100, 1481534121621467175, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762378203139, 13, 1711560060463276032, 0, 100, 1481534121625661441, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762382397440, 14, 1711560060463276032, 0, 100, 1481534121625661444, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762382397441, 15, 1711560060463276032, 0, 100, 1481534121625661447, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762386591744, 16, 1711560060463276032, 0, 100, 1481534121625661450, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762386591745, 17, 1711560060463276032, 0, 100, 1481534121625661453, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762390786048, 18, 1711560060463276032, 0, 100, 1481534121625661472, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762390786049, 19, 1711560060463276032, 0, 100, 1481534121625661499, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762390786050, 20, 1711560060463276032, 0, 100, 1481534121625661502, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762390786051, 21, 1711560060463276032, 0, 100, 1481534121625661469, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762390786052, 22, 1711560060463276032, 0, 100, 1481534121625661475, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980352, 23, 1711560060463276032, 0, 100, 1481534121625661481, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980353, 24, 1711560060463276032, 0, 100, 1481534121625661478, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980354, 25, 1711560060463276032, 0, 100, 1481534121625661484, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980355, 26, 1711560060463276032, 0, 100, 1481534121625661490, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980356, 27, 1711560060463276032, 0, 100, 1481534121625661493, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762394980357, 28, 1711560060463276032, 0, 100, 1481534121625661487, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174656, 29, 1711560060463276032, 0, 100, 1481534121625661507, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174657, 30, 1711560060463276032, 0, 100, 1481534121625661496, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174658, 31, 1711560060463276032, 0, 100, 1481534121625661510, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174659, 32, 1711560060463276032, 0, 100, 1481534121625661503, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174660, 33, 1711560060463276032, 0, 100, 1481534121625661506, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762399174661, 34, 1711560060463276032, 0, 100, 1481534121625661513, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368960, 35, 1711560060463276032, 0, 100, 1481534121625661514, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368961, 36, 1711560060463276032, 0, 100, 1481534121625661515, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368962, 37, 1711560060463276032, 0, 100, 1481534121625661516, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368963, 38, 1711560060463276032, 0, 100, 1481534121625661517, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368964, 39, 1711560060463276032, 0, 100, 1481534121625661518, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368965, 40, 1711560060463276032, 0, 100, 1481534121625661519, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762403368966, 41, 1711560060463276032, 0, 100, 1481534121625661520, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);
INSERT INTO `wk_hrm_employee_field_config` VALUES (1712716762407563264, 42, 1711560060463276032, 0, 100, 1481534121625661521, NULL, '2023-10-13 14:27:50', '2023-10-13 14:27:50', NULL);

-- ----------------------------
-- Table structure for wk_hrm_employee_field_manage
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_field_manage`;
CREATE TABLE `wk_hrm_employee_field_manage`  (
  `id` bigint NOT NULL,
  `entry_status` int NOT NULL DEFAULT 1 COMMENT '入职状态 1 在职 2 待入职 ',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段id',
  `field_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名称',
  `is_manage_visible` int NOT NULL DEFAULT 1 COMMENT '是否管理员可见 0 否 1 是  2 禁用否 3 禁用是',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义字段管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_field_manage
-- ----------------------------
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467137, 1, 1481534121621467136, 'employee_name', '姓名', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467138, 2, 1481534121621467136, 'employee_name', '姓名', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467140, 1, 1481534121621467139, 'flied_hukzra', '英文名', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467141, 2, 1481534121621467139, 'flied_hukzra', '英文名', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467143, 1, 1481534121621467142, 'mobile', '手机', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467144, 2, 1481534121621467142, 'mobile', '手机', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467146, 1, 1481534121621467145, 'id_type', '证件类型', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467147, 2, 1481534121621467145, 'id_type', '证件类型', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467149, 1, 1481534121621467148, 'id_number', '证件号码', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467150, 2, 1481534121621467148, 'id_number', '证件号码', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467152, 1, 1481534121621467151, 'sex', '性别', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467153, 2, 1481534121621467151, 'sex', '性别', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467155, 1, 1481534121621467154, 'date_of_birth', '出生日期', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467156, 2, 1481534121621467154, 'date_of_birth', '出生日期', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467158, 1, 1481534121621467157, 'birthday_type', '生日类型', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467159, 2, 1481534121621467157, 'birthday_type', '生日类型', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467161, 1, 1481534121621467160, 'birthday', '生日', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467162, 2, 1481534121621467160, 'birthday', '生日', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467164, 1, 1481534121621467163, 'age', '年龄', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467165, 2, 1481534121621467163, 'age', '年龄', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467167, 1, 1481534121621467166, 'flied_bbnpqh', '是否已婚', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467168, 2, 1481534121621467166, 'flied_bbnpqh', '是否已婚', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467170, 1, 1481534121621467169, 'flied_dxnkqj', '是否已育', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467171, 2, 1481534121621467169, 'flied_dxnkqj', '是否已育', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467173, 1, 1481534121621467172, 'country', '国家地区', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467174, 2, 1481534121621467172, 'country', '国家地区', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121621467176, 1, 1481534121621467175, 'nation', '民族', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661440, 2, 1481534121621467175, 'nation', '民族', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661442, 1, 1481534121625661441, 'flied_luxpii', '政治面貌', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661443, 2, 1481534121625661441, 'flied_luxpii', '政治面貌', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661445, 1, 1481534121625661444, 'native_place', '籍贯', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661446, 2, 1481534121625661444, 'native_place', '籍贯', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661448, 1, 1481534121625661447, 'address', '户籍所在地', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661449, 2, 1481534121625661447, 'address', '户籍所在地', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661451, 1, 1481534121625661450, 'flied_mosheh', '健康状态', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661452, 2, 1481534121625661450, 'flied_mosheh', '健康状态', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661454, 1, 1481534121625661453, 'highest_education', '最高学历', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661455, 2, 1481534121625661453, 'highest_education', '最高学历', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661470, 1, 1481534121625661469, 'job_number', '工号', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661471, 2, 1481534121625661469, 'job_number', '工号', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661473, 1, 1481534121625661472, 'entry_time', '入职日期', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661474, 2, 1481534121625661472, 'entry_time', '入职日期', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661476, 1, 1481534121625661475, 'dept_id', '部门', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661477, 2, 1481534121625661475, 'dept_id', '部门', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661479, 1, 1481534121625661478, 'post', '岗位', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661480, 2, 1481534121625661478, 'post', '岗位', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661482, 1, 1481534121625661481, 'parent_id', '直属上级', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661483, 2, 1481534121625661481, 'parent_id', '直属上级', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661485, 1, 1481534121625661484, 'post_level', '职级', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661486, 2, 1481534121625661484, 'post_level', '职级', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661488, 1, 1481534121625661487, 'work_city', '工作城市', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661489, 2, 1481534121625661487, 'work_city', '工作城市', 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661491, 1, 1481534121625661490, 'work_address', '工作地点', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661492, 2, 1481534121625661490, 'work_address', '工作地点', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661494, 1, 1481534121625661493, 'work_detail_address', '详细工作地点', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661495, 2, 1481534121625661493, 'work_detail_address', '详细工作地点', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661497, 1, 1481534121625661496, 'employment_forms', '聘用形式', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661498, 2, 1481534121625661496, 'employment_forms', '聘用形式', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661500, 1, 1481534121625661499, 'probation', '试用期', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661501, 2, 1481534121625661499, 'probation', '试用期', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661504, 1, 1481534121625661503, 'company_age_start_time', '司龄开始日期', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661505, 2, 1481534121625661503, 'company_age_start_time', '司龄开始日期', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661508, 1, 1481534121625661507, 'channel_id', '招聘渠道', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661509, 2, 1481534121625661507, 'channel_id', '招聘渠道', 0, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661511, 1, 1481534121625661510, 'status', '员工状态', 3, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_employee_field_manage` VALUES (1481534121625661512, 2, 1481534121625661510, 'status', '员工状态', 3, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_employee_file
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_file`;
CREATE TABLE `wk_hrm_employee_file`  (
  `employee_file_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `file_id` bigint NOT NULL COMMENT 'admin模块附件id',
  `type` int NULL DEFAULT NULL COMMENT '1 员工基本资料 2 员工档案资料 3 员工离职资料',
  `sub_type` int NOT NULL COMMENT '11、身份证原件 12、学历证明 13、个人证件照 14、身份证复印件 15、工资银行卡 16、社保卡 17、公积金卡 18、获奖证书 19、其他 21、劳动合同 22、入职简历 23、入职登记表 24、入职体检单 25、离职证明 26、转正申请表 27、其他\n31、离职审批 32、离职证明 33 、其他\n',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`employee_file_id`) USING BTREE,
  INDEX `wk_hrm_employee_file_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_file
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_leave_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_leave_record`;
CREATE TABLE `wk_hrm_employee_leave_record`  (
  `leave_record_id` bigint NOT NULL COMMENT '请假记录id',
  `examine_id` bigint NULL DEFAULT NULL COMMENT '审批id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `leave_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请假类型',
  `leave_start_time` datetime NULL DEFAULT NULL COMMENT '请假开始时间',
  `leave_end_time` datetime NULL DEFAULT NULL COMMENT '请假结束时间',
  `leave_day` decimal(5, 2) NULL DEFAULT NULL COMMENT '请假时长',
  `leave_reason` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请假理由',
  `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`leave_record_id`) USING BTREE,
  INDEX `index_employee_id`(`employee_id` ASC) USING BTREE,
  INDEX `index_leave_start_time`(`leave_start_time` ASC) USING BTREE,
  INDEX `index_leave_end_time`(`leave_end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工请假记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_leave_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_over_time_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_over_time_record`;
CREATE TABLE `wk_hrm_employee_over_time_record`  (
  `over_time_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `over_time_type` int NULL DEFAULT 1 COMMENT '加班类型(1.工作日加班, 2.休息日加班)',
  `over_time_start_time` datetime NULL DEFAULT NULL COMMENT '加班开始时间',
  `over_time_end_time` datetime NULL DEFAULT NULL COMMENT '加班结束时间',
  `attendance_time` datetime NULL DEFAULT NULL COMMENT '上班时间',
  `over_times` decimal(5, 2) NULL DEFAULT NULL COMMENT '加班时长',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`over_time_id`) USING BTREE,
  INDEX `index_employee_id`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工加班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_over_time_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_quit_info
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_quit_info`;
CREATE TABLE `wk_hrm_employee_quit_info`  (
  `quit_info_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `plan_quit_time` datetime NULL DEFAULT NULL COMMENT '计划离职日期',
  `apply_quit_time` datetime NULL DEFAULT NULL COMMENT '申请离职日期',
  `salary_settlement_time` datetime NULL DEFAULT NULL COMMENT '薪资结算日期',
  `quit_type` int NULL DEFAULT NULL COMMENT '离职类型 1 主动离职 2 被动离职 3 退休',
  `quit_reason` int NULL DEFAULT NULL COMMENT '离职原因 1家庭原因 2身体原因 3薪资原因 4交通不便 5工作压力 6管理问题 7无晋升机会 8职业规划 9合同到期放弃续签 10其他个人原因  11试用期内辞退 12违反公司条例 13组织调整/裁员 14绩效不达标辞退 15合同到期不续签 16 其他原因被动离职',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `old_status` int NULL DEFAULT NULL COMMENT '离职前状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`quit_info_id`) USING BTREE,
  INDEX `wk_hrm_employee_quit_info_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '离职信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_quit_info
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_salary_card
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_salary_card`;
CREATE TABLE `wk_hrm_employee_salary_card`  (
  `salary_card_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `salary_card_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工资卡卡号',
  `account_opening_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '开户城市',
  `bank_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行名称',
  `opening_bank` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工资卡开户行',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`salary_card_id`) USING BTREE,
  INDEX `wk_hrm_employee_salary_card_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工薪资卡信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_salary_card
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_social_security_info
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_social_security_info`;
CREATE TABLE `wk_hrm_employee_social_security_info`  (
  `social_security_info_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL,
  `is_first_social_security` int NULL DEFAULT NULL COMMENT '是否首次缴纳社保 0 否 1 是',
  `is_first_accumulation_fund` int NULL DEFAULT NULL COMMENT '是否首次缴纳公积金 0 否 1 是',
  `social_security_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '社保号',
  `accumulation_fund_num` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公积金账号',
  `social_security_start_month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '参保起始月份（2020.05）',
  `scheme_id` bigint NULL DEFAULT NULL COMMENT '参保方案',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`social_security_info_id`) USING BTREE,
  INDEX `wk_hrm_employee_social_security_info_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工公积金信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_social_security_info
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_training_experience
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_training_experience`;
CREATE TABLE `wk_hrm_employee_training_experience`  (
  `training_id` bigint NOT NULL,
  `employee_id` bigint NULL DEFAULT NULL,
  `training_course` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '培训课程',
  `training_organ_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '培训机构名称',
  `start_time` datetime NULL DEFAULT NULL COMMENT '培训开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '培训结束时间',
  `training_duration` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '培训时长',
  `training_results` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '培训成绩',
  `training_certificate_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '培训课程名称',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`training_id`) USING BTREE,
  INDEX `wk_hrm_employee_training_experience_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '培训经历' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_training_experience
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_employee_work_experience
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_employee_work_experience`;
CREATE TABLE `wk_hrm_employee_work_experience`  (
  `work_exp_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `work_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工作单位',
  `post` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '职务',
  `work_start_time` datetime NULL DEFAULT NULL COMMENT '工作开始时间',
  `work_end_time` datetime NULL DEFAULT NULL COMMENT '工作结束时间',
  `leaving_reason` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '离职原因',
  `witness` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证明人',
  `witness_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证明人手机号',
  `work_remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工作备注',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`work_exp_id`) USING BTREE,
  INDEX `wk_hrm_employee_work_experience_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工工作经历' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_employee_work_experience
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_field_extend
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_field_extend`;
CREATE TABLE `wk_hrm_field_extend`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_field_id` bigint NOT NULL COMMENT '对应主字段id',
  `field_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自定义字段英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `type` int NOT NULL DEFAULT 1 COMMENT '字段类型',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段说明',
  `input_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输入提示',
  `max_length` int NULL DEFAULT NULL COMMENT '最大长度',
  `default_value` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '默认值',
  `is_unique` int NULL DEFAULT 0 COMMENT '是否唯一 1 是 0 否',
  `is_null` int NULL DEFAULT 0 COMMENT '是否必填 1 是 0 否',
  `sorting` int NULL DEFAULT 1 COMMENT '排序 从小到大',
  `options` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '如果类型是选项，此处不能为空，多个选项以，隔开',
  `operating` int NULL DEFAULT 255 COMMENT '是否允许编辑',
  `is_hidden` int NOT NULL DEFAULT 0 COMMENT '是否隐藏  0不隐藏 1隐藏',
  `field_type` int NOT NULL DEFAULT 0 COMMENT '字段来源  0.自定义 1.原始固定 2原始字段但值存在扩展表中',
  `style_percent` int NULL DEFAULT 50 COMMENT '样式百分比%',
  `precisions` int NULL DEFAULT NULL COMMENT '精度，允许的最大小数位',
  `form_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单定位 坐标格式： 1,1',
  `max_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最大数值',
  `min_num_restrict` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制的最小数值',
  `form_assist_id` bigint NULL DEFAULT NULL COMMENT '表单辅助id，前端生成',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '扩展自定义字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_field_extend
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_field_sort
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_field_sort`;
CREATE TABLE `wk_hrm_field_sort`  (
  `id` bigint NOT NULL COMMENT 'id',
  `field_id` bigint NULL DEFAULT NULL COMMENT '字段ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名称',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `label` int NOT NULL COMMENT '标签 5:员工绩效',
  `type` int NULL DEFAULT NULL COMMENT '字段类型',
  `style` int NULL DEFAULT NULL COMMENT '字段宽度',
  `sort` int NOT NULL DEFAULT 0 COMMENT '字段排序',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户id',
  `is_hide` int NOT NULL DEFAULT 1 COMMENT '是否隐藏 0、不隐藏 1、隐藏',
  `is_lock` int NULL DEFAULT 0 COMMENT '是否锁定 1是',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `label`(`user_id` ASC, `field_name` ASC, `label` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段排序表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_field_sort
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_insurance_month_emp_project_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_insurance_month_emp_project_record`;
CREATE TABLE `wk_hrm_insurance_month_emp_project_record`  (
  `emp_project_record_id` bigint NOT NULL COMMENT '参保项目记录id',
  `i_emp_record_id` bigint NOT NULL COMMENT '参保员工记录id',
  `project_id` bigint NULL DEFAULT NULL COMMENT '后台配置项目id',
  `type` int NOT NULL COMMENT '1 养老保险基数 2 医疗保险基数 3 失业保险基数 4 工伤保险基数 5 生育保险基数 6 补充大病医疗保险 7 补充养老保险 8 公积金',
  `project_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `default_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '默认基数',
  `corporate_proportion` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '公司比例',
  `personal_proportion` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '个人比例',
  `corporate_amount` decimal(10, 2) NOT NULL COMMENT '公司缴纳金额',
  `personal_amount` decimal(10, 2) NOT NULL COMMENT '个人缴纳金额',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`emp_project_record_id`) USING BTREE,
  INDEX `wk_hrm_insurance_month_emp_project_record_i_emp_record_id_index`(`i_emp_record_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工每月参保项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_insurance_month_emp_project_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_insurance_month_emp_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_insurance_month_emp_record`;
CREATE TABLE `wk_hrm_insurance_month_emp_record`  (
  `i_emp_record_id` bigint NOT NULL,
  `i_record_id` bigint NULL DEFAULT NULL COMMENT '每月生成社保id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `scheme_id` bigint NULL DEFAULT NULL COMMENT '社保方案id',
  `year` int NULL DEFAULT NULL COMMENT '年',
  `month` int NULL DEFAULT NULL COMMENT '月',
  `personal_insurance_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '个人社保金额',
  `personal_provident_fund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '个人公积金金额',
  `corporate_insurance_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '公司社保金额',
  `corporate_provident_fund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '公司社保金额',
  `status` int NULL DEFAULT 1 COMMENT '每月社保状态 0 停保 1 正常',
  `create_time` datetime NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`i_emp_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工每月社保记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_insurance_month_emp_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_insurance_month_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_insurance_month_record`;
CREATE TABLE `wk_hrm_insurance_month_record`  (
  `i_record_id` bigint NOT NULL,
  `title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报表名称',
  `year` int NULL DEFAULT NULL COMMENT '年份',
  `month` int NULL DEFAULT NULL COMMENT '月份',
  `num` int NULL DEFAULT NULL COMMENT '参保人数',
  `status` int NULL DEFAULT 0 COMMENT '每月社保状态 0 未归档 1 已归档',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`i_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每月社保记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_insurance_month_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_insurance_project
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_insurance_project`;
CREATE TABLE `wk_hrm_insurance_project`  (
  `project_id` bigint NOT NULL COMMENT '项目id',
  `scheme_id` bigint NOT NULL COMMENT '参保方案id',
  `type` int NOT NULL COMMENT '1 养老保险基数 2 医疗保险基数 3 失业保险基数 4 工伤保险基数 5 生育保险基数 6 补充大病医疗保险 7 补充养老保险 8 残保险 9 社保自定义 10 公积金 11 公积金自定义',
  `project_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `default_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '默认基数',
  `corporate_proportion` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '公司比例',
  `personal_proportion` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '个人比例',
  `corporate_amount` decimal(10, 2) NOT NULL COMMENT '公司缴纳金额',
  `personal_amount` decimal(10, 2) NOT NULL COMMENT '个人缴纳金额',
  `is_del` int NOT NULL DEFAULT 1 COMMENT '1 删除 0 使用',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`project_id`) USING BTREE,
  INDEX `wk_hrm_insurance_project_scheme_id_index`(`scheme_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参保项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_insurance_project
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_insurance_scheme
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_insurance_scheme`;
CREATE TABLE `wk_hrm_insurance_scheme`  (
  `scheme_id` bigint NOT NULL COMMENT '社保方案id',
  `scheme_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '方案名称',
  `city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参保城市',
  `house_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '户籍类型',
  `scheme_type` int NOT NULL COMMENT '参保类型 1 比例 2 金额',
  `is_del` int NOT NULL DEFAULT 0 COMMENT '1 删除 0 使用',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`scheme_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '社保方案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_insurance_scheme
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_notes
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_notes`;
CREATE TABLE `wk_hrm_notes`  (
  `notes_id` bigint NOT NULL,
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `reminder_time` datetime NOT NULL,
  `employee_id` bigint NOT NULL,
  `create_user_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`notes_id`) USING BTREE,
  INDEX `wk_hrm_notes_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '备忘' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_notes
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_process_setting_result_audit
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_process_setting_result_audit`;
CREATE TABLE `wk_hrm_process_setting_result_audit`  (
  `result_audit_id` bigint NOT NULL COMMENT '结果审核id',
  `level` int NULL DEFAULT NULL COMMENT '审核级别：1直属上级2第2级上级',
  `audit_type` int NULL DEFAULT NULL COMMENT '审核类型：1上级2：部门负责人3：指定评分人4：被考核人',
  `designated_person` bigint NULL DEFAULT NULL COMMENT '指定评分人',
  `process_id` bigint NOT NULL COMMENT '流程id',
  `sort` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`result_audit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'KIP考核计划-流程设置-结果审核表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_process_setting_result_audit
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_process_setting_result_confirmation
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_process_setting_result_confirmation`;
CREATE TABLE `wk_hrm_process_setting_result_confirmation`  (
  `result_confirmation_id` bigint NOT NULL COMMENT '结果确认id',
  `level` int NULL DEFAULT NULL COMMENT '确认层级',
  `confirmation_type` int NOT NULL COMMENT '确认类型：1上级2：部门负责人3：指定评分人4：被考核人',
  `designated_user_id` bigint NULL DEFAULT NULL COMMENT '指定人id',
  `process_id` bigint NULL DEFAULT NULL COMMENT '流程id',
  `sort` int NULL DEFAULT NULL COMMENT '顺序',
  PRIMARY KEY (`result_confirmation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核计划-流程设置-结果确认表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_process_setting_result_confirmation
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_process_setting_scoring
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_process_setting_scoring`;
CREATE TABLE `wk_hrm_process_setting_scoring`  (
  `scoring_process_id` bigint NOT NULL COMMENT '评分流程id',
  `rater_type` int NULL DEFAULT NULL COMMENT '评分人类型：1上级2：部门负责人3：指定评分人4：被考核人',
  `rater_level` int NULL DEFAULT NULL COMMENT '评分人级别',
  `rater` bigint NULL DEFAULT NULL COMMENT '评分人',
  `weight` double NULL DEFAULT NULL COMMENT '权重',
  `scoring_type` int NULL DEFAULT NULL COMMENT '评分方式1:按指标评分',
  `visible_content` int NULL DEFAULT NULL COMMENT '可见内容',
  `required_setting` bit(1) NULL DEFAULT b'0' COMMENT '必填设置0:关闭 1：开启',
  `reject_authority` bit(1) NULL DEFAULT b'0' COMMENT '驳回权限:默认为关闭状态0:关闭 1：开启',
  `sort` int NULL DEFAULT NULL COMMENT '层级',
  `process_id` bigint NULL DEFAULT NULL COMMENT '流程id',
  PRIMARY KEY (`scoring_process_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-流程设置-评分流程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_process_setting_scoring
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_process_setting_target_confirmation
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_process_setting_target_confirmation`;
CREATE TABLE `wk_hrm_process_setting_target_confirmation`  (
  `confirmation_id` bigint NOT NULL COMMENT 'id',
  `confirmation_type` int NULL DEFAULT NULL COMMENT '确认类型：1上级2：部门负责人3：指定评分人4：被考核人',
  `confirmation_level` int NULL DEFAULT NULL COMMENT '确认级别',
  `identifying_people` bigint NULL DEFAULT NULL COMMENT '确认人',
  `process_id` bigint NULL DEFAULT NULL COMMENT '流程id',
  `sort` int NULL DEFAULT NULL COMMENT '确认顺序',
  PRIMARY KEY (`confirmation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '绩效考核计划-流程设置-目标确认' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_process_setting_target_confirmation
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_quota_setting_dimension
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_quota_setting_dimension`;
CREATE TABLE `wk_hrm_quota_setting_dimension`  (
  `dimension_id` bigint NOT NULL COMMENT '考核维度id',
  `dimension_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '维度名称',
  `quota_type` int NULL DEFAULT NULL COMMENT '指标类型',
  `dimension_weight` double NULL DEFAULT NULL COMMENT '维度权重',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_allow_edit` bit(1) NULL DEFAULT b'0' COMMENT '允许员工填写1：是 0：否',
  `quota_setting_id` bigint NULL DEFAULT NULL COMMENT '指标设置id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  `appraisal_plan_id` bigint NOT NULL COMMENT '考核计划id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`dimension_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-指标设置-考核维度' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_quota_setting_dimension
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_quota_setting_dimension_quota
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_quota_setting_dimension_quota`;
CREATE TABLE `wk_hrm_quota_setting_dimension_quota`  (
  `quota_id` bigint NOT NULL COMMENT '维度指标id',
  `quota_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标名称',
  `quota_illustrate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标说明',
  `standard` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考核标准',
  `quota_weight` double NULL DEFAULT NULL COMMENT '指标权重',
  `score_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分方式',
  `dimension_id` bigint NULL DEFAULT NULL COMMENT '维度id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
  `appraisal_plan_id` bigint NOT NULL COMMENT '考核计划id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`quota_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考核计划-指标设置-考核维度-考核指标' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_quota_setting_dimension_quota
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_recruit_candidate
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_recruit_candidate`;
CREATE TABLE `wk_hrm_recruit_candidate`  (
  `candidate_id` bigint NOT NULL COMMENT '候选人id',
  `candidate_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '候选人名称',
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机',
  `sex` int NOT NULL COMMENT '性别 1男 2女',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `post_id` bigint NOT NULL COMMENT '职位id',
  `stage_num` int NOT NULL DEFAULT 0 COMMENT '面试轮次 ',
  `work_time` int NULL DEFAULT NULL COMMENT '工作年限',
  `education` int NOT NULL COMMENT '学历 1小学 2初中 3高中 4大专 5本科 6硕士 7博士',
  `graduate_school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '毕业院校',
  `latest_work_place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近工作单位',
  `channel_id` bigint NULL DEFAULT NULL COMMENT '招聘渠道',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT 1 COMMENT '候选人状态 1 新候选人 2初选通过 3安排面试 4面试通过 5已发offer 6待入职 7已淘汰 8已入职',
  `eliminate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '淘汰原因',
  `status_update_time` datetime NULL DEFAULT NULL COMMENT '状态更新时间',
  `entry_time` datetime NULL DEFAULT NULL COMMENT '入职时间',
  `batch_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次id',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`candidate_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '招聘候选人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_recruit_candidate
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_recruit_channel
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_recruit_channel`;
CREATE TABLE `wk_hrm_recruit_channel`  (
  `channel_id` bigint NOT NULL,
  `is_sys` int NULL DEFAULT 0 COMMENT '是否系统默认0 否 1 是',
  `status` int NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`channel_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '招聘渠道表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_recruit_channel
-- ----------------------------
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661522, 1, 1, '前程无忧', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661523, 1, 1, '智联招聘', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661524, 1, 1, '拉勾网', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661525, 1, 1, '猎聘网', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661526, 1, 1, '中国人才热线', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661527, 1, 1, '58同城', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661528, 1, 1, '赶集网', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661529, 1, 1, 'BOSS直聘', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661530, 1, 1, '大街网', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661531, 1, 1, '中华英才网', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661532, 1, 1, '内部举荐', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661533, 1, 1, '员工推荐', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661534, 1, 1, '微信招聘', 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_recruit_channel` VALUES (1481534121625661535, 1, 1, '其他', 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_recruit_interview
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_recruit_interview`;
CREATE TABLE `wk_hrm_recruit_interview`  (
  `interview_id` bigint NOT NULL COMMENT '面试id',
  `candidate_id` bigint NOT NULL COMMENT '候选人id',
  `type` int NULL DEFAULT NULL COMMENT '面试方式 1现场面试 2电话面试 3视频面试',
  `stage_num` int NOT NULL DEFAULT 1 COMMENT '面试轮次',
  `interview_employee_id` bigint NULL DEFAULT NULL COMMENT '面试官id',
  `other_interview_employee_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他面试官',
  `interview_time` datetime NULL DEFAULT NULL COMMENT '面试时间',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '面试地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `result` int NULL DEFAULT 1 COMMENT '面试情况 1面试未完成 2面试通过 3面试未通过 4 面试取消',
  `evaluate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评价',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`interview_id`) USING BTREE,
  INDEX `wk_hrm_recruit_interview_candidate_id_index`(`candidate_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '面试表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_recruit_interview
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_recruit_post
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_recruit_post`;
CREATE TABLE `wk_hrm_recruit_post`  (
  `post_id` bigint NOT NULL COMMENT '职位id',
  `post_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职位名称',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `job_nature` int NULL DEFAULT NULL COMMENT '工作性质 1 全职 2实习 3兼职',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作城市',
  `recruit_num` int NULL DEFAULT 0 COMMENT '招聘人数',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '招聘原因',
  `work_time` int NULL DEFAULT NULL COMMENT '工作经验 1不限 2一年以内 3一至三年 4三至五年 5五至十年 6十年以上',
  `education_require` int NULL DEFAULT NULL COMMENT '学历要求 1不限 2高中及以上 3大专及以上 4本科及以上 5硕士及以上 6博士',
  `min_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '开始薪资 -1 面议',
  `max_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '结束薪资 -1 面议',
  `salary_unit` int NULL DEFAULT NULL COMMENT '薪资单位 1 元/年 2 元/月',
  `min_age` int NULL DEFAULT NULL COMMENT '最小年龄 -1 不限',
  `max_age` int NULL DEFAULT NULL COMMENT '最大年龄 -1 不限',
  `latest_entry_time` datetime NULL DEFAULT NULL COMMENT '最迟到岗时间',
  `owner_employee_id` bigint NULL DEFAULT NULL COMMENT '负责人id',
  `interview_employee_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '面试官',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '职位描述',
  `emergency_level` int NULL DEFAULT NULL COMMENT '紧急程度 1紧急 2 一般',
  `post_type_id` bigint NULL DEFAULT NULL COMMENT '职位类型',
  `batch_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次id',
  `status` int NULL DEFAULT 1 COMMENT '0 停止  1 启用',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '招聘职位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_recruit_post
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_recruit_post_type
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_recruit_post_type`;
CREATE TABLE `wk_hrm_recruit_post_type`  (
  `id` bigint NOT NULL,
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '职位类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_recruit_post_type
-- ----------------------------
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1, '高级管理', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (2, '高级管理职位', 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (3, '高级管理职位', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (4, '总裁/总经理/CEO', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (5, '副总裁/副总经理/VP', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (6, '分公司/代表处负责人', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (7, '区域负责人(辖多个分公司)', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (8, '总助/CEO助理/董事长助理', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (9, '合伙人', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (10, '联合创始人', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (11, '董事会秘书', 2, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (12, '技术', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (13, '后端开发', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (14, '后端开发', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (15, 'Java', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (16, 'C++', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (17, 'PHP', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (18, '数据挖掘', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (19, 'C', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (20, 'C#', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (21, '.NET', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (22, 'Hadoop', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (23, 'Python', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (24, 'Delphi', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (25, 'VB', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (26, 'Perl', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (27, 'Ruby', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (28, 'Node.js', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (29, '搜索算法', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (30, 'Golang', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (31, '推荐算法', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (32, 'Erlang', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (33, '算法工程师', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (34, '语音/视频/图形开发', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (35, '数据采集', 13, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (36, '移动开发', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (37, 'UE4', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (38, '移动开发', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (39, 'HTML5', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (40, 'Android', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (41, 'iOS', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (42, 'WP', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (43, '移动web前端', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (44, 'Flash开发', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (45, 'JavaScript', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (46, 'U3D', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (47, 'COCOS2DX', 36, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (48, '测试', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (49, '测试工程师', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (50, '自动化测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (51, '功能测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (52, '性能测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (53, '测试开发', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (54, '移动端测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (55, '游戏测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (56, '硬件测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (57, '软件测试', 48, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (58, '运维/技术支持', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (59, '运维工程师', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (60, '运维开发工程师', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (61, '网络工程师', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (62, '系统工程师', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (63, 'IT技术支持', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (64, '系统管理员', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (65, '网络安全', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (66, '系统安全', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (67, 'DBA', 58, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (68, '数据', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (69, '数据', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (70, 'ETL工程师', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (71, '数据仓库', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (72, '数据开发', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (73, '数据挖掘', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (74, '数据分析师', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (75, '数据架构师', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (76, '算法研究员', 68, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (77, '项目管理', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (78, '项目经理', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (79, '项目主管', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (80, '项目助理', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (81, '项目专员', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (82, '实施顾问', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (83, '实施工程师', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (84, '需求分析工程师', 77, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (85, '硬件开发', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (86, '硬件', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (87, '嵌入式', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (88, '自动化', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (89, '单片机', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (90, '电路设计', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (91, '驱动开发', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (92, '系统集成', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (93, 'FPGA开发', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (94, 'DSP开发', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (95, 'ARM开发', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (96, 'PCB工艺', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (97, '射频工程师', 85, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (98, '前端开发', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (99, '前端开发', 98, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (100, 'web前端', 98, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (101, 'JavaScript', 98, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (102, 'Flash开发', 98, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (103, 'HTML5', 98, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (104, '通信', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (105, '通信技术工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (106, '通信研发工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (107, '数据通信工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (108, '移动通信工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (109, '电信网络工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (110, '电信交换工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (111, '有线传输工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (112, '无线射频工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (113, '通信电源工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (114, '通信标准化工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (115, '通信项目专员', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (116, '通信项目经理', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (117, '核心网工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (118, '通信测试工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (119, '通信设备工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (120, '光通信工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (121, '光传输工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (122, '光网络工程师', 104, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (123, '电子/半导体', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (124, '电子工程师', 123, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (125, '电气工程师', 123, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (126, 'FAE', 123, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (127, '电气设计工程师', 123, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (128, '高端技术职位', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (129, '高端技术职位', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (130, '技术经理', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (131, '技术总监', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (132, '测试经理', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (133, '架构师', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (134, 'CTO', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (135, '运维总监', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (136, '技术合伙人', 128, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (137, '人工智能', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (138, '智能驾驶系统工程师', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (139, '反欺诈/风控算法', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (140, '人工智能', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (141, '自然语言处理', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (142, '机器学习', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (143, '深度学习', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (144, '语音识别', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (145, '图像识别', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (146, '算法研究员', 137, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (147, '销售技术支持', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (148, '销售技术支持', 147, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (149, '售前工程师', 147, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (150, '售后工程师', 147, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (151, '其他技术职位', 12, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (152, '其他技术职位', 151, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (153, '产品', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (154, '产品经理', 153, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (155, '硬件产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (156, '产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (157, '网页产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (158, '移动产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (159, '产品助理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (160, '数据产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (161, '电商产品经理', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (162, '游戏策划', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (163, '产品专员', 154, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (164, '高端产品职位', 153, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (165, '高端产品职位', 164, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (166, '产品总监', 164, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (167, '游戏制作人', 164, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (168, '产品VP', 164, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (169, '其他产品职位', 153, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (170, '其他产品职位', 169, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (171, '设计', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (172, '视觉设计', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (173, '漫画师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (174, '人像修图师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (175, '视觉设计', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (176, '视觉设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (177, '网页设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (178, 'Flash设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (179, 'APP设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (180, 'UI设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (181, '平面设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (182, '美术设计师（2D/3D）', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (183, '广告设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (184, '多媒体设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (185, '原画师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (186, '游戏特效', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (187, '游戏界面设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (188, '游戏场景', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (189, '游戏角色', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (190, '游戏动作', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (191, 'CAD设计/制图', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (192, '美工', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (193, '包装设计', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (194, '设计师助理', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (195, '动画设计师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (196, '插画师', 172, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (197, '交互设计', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (198, '交互设计师', 197, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (199, '无线交互设计师', 197, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (200, '网页交互设计师', 197, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (201, '硬件交互设计师', 197, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (202, '用户研究', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (203, '数据分析师', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (204, '用户研究员', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (205, '游戏数值策划', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (206, 'UX设计师', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (207, '用户研究经理', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (208, '用户研究总监', 202, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (209, '高端设计职位', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (210, '高端设计职位', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (211, '设计经理/主管', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (212, '设计总监', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (213, '视觉设计经理', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (214, '视觉设计总监', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (215, '交互设计经理/主管', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (216, '交互设计总监', 209, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (217, '非视觉设计', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (218, '展览/展示设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (219, '非视觉设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (220, '服装/纺织设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (221, '工业设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (222, '橱柜设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (223, '家具设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (224, '家居设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (225, '珠宝设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (226, '室内设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (227, '陈列设计', 217, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (228, '其他设计职位', 171, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (229, '其他设计职位', 228, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (230, '运营', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (231, '运营', 230, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (232, '数据标注', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (233, '直播运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (234, '车辆运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (235, '跨境电商运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (236, '网店店长', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (237, '运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (238, '用户运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (239, '产品运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (240, '数据运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (241, '内容运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (242, '活动运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (243, '商家运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (244, '品类运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (245, '游戏运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (246, '网络推广', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (247, '网站运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (248, '新媒体运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (249, '社区运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (250, '微信运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (251, '微博运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (252, '策略运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (253, '线下拓展运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (254, '电商运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (255, '运营助理/专员', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (256, '内容审核', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (257, '销售运营', 231, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (258, '编辑', 230, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (259, '编辑', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (260, '副主编', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (261, '内容编辑', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (262, '文案策划', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (263, '网站编辑', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (264, '记者', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (265, '采编', 258, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (266, '客服', 230, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (267, '售前咨询', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (268, '售后咨询', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (269, '网络客服', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (270, '客服经理', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (271, '客服专员/助理', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (272, '客服主管', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (273, '客服总监', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (274, '电话客服', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (275, '咨询热线/呼叫中心客服', 266, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (276, '高端运营职位', 230, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (277, '高端运营职位', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (278, '主编', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (279, '运营总监', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (280, 'COO', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (281, '客服总监', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (282, '运营经理/主管', 276, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (283, '其他运营职位', 230, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (284, '其他运营职位', 283, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (285, '市场', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (286, '政府事务', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (287, '政策研究', 286, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (288, '企业党建', 286, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (289, '政府关系', 286, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (290, '市场/营销', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (291, '选址开发', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (292, '游戏推广', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (293, '市场营销', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (294, '市场策划', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (295, '市场顾问', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (296, '市场推广', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (297, 'SEO', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (298, 'SEM', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (299, '商务渠道', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (300, '商业数据分析', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (301, '活动策划', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (302, '网络营销', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (303, '海外市场', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (304, 'APP推广', 290, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (305, '公关媒介', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (306, '公关媒介', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (307, '媒介经理', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (308, '广告协调', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (309, '品牌公关', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (310, '媒介专员', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (311, '活动策划执行', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (312, '媒介策划', 305, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (313, '会务会展', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (314, '会务会展', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (315, '会议活动销售', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (316, '会议活动策划', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (317, '会议活动执行', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (318, '会展活动销售', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (319, '会展活动策划', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (320, '会展活动执行', 313, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (321, '广告', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (322, '广告', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (323, '广告创意', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (324, '美术指导', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (325, '广告设计师', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (326, '策划经理', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (327, '文案', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (328, '广告制作', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (329, '媒介投放', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (330, '媒介合作', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (331, '媒介顾问', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (332, '广告审核', 321, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (333, '高端市场职位', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (334, '高端市场职位', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (335, '市场总监', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (336, 'CMO', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (337, '公关总监', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (338, '媒介总监', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (339, '创意总监', 333, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (340, '其他市场职位', 285, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (341, '其他市场职位', 340, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (342, '人事/财务/行政', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (343, '人力资源', 342, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (344, '人力资源主管', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (345, '招聘', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (346, 'HRBP', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (347, '人力资源专员/助理', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (348, '培训', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (349, '薪资福利', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (350, '绩效考核', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (351, '人力资源经理', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (352, '人力资源VP/CHO', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (353, '人力资源总监', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (354, '员工关系', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (355, '组织发展', 343, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (356, '行政', 342, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (357, '行政专员/助理', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (358, '前台', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (359, '行政主管', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (360, '经理助理', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (361, '后勤', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (362, '商务司机', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (363, '行政经理', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (364, '行政总监', 356, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (365, '财务', 342, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (366, '成本', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (367, '财务', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (368, '会计', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (369, '出纳', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (370, '财务顾问', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (371, '结算', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (372, '税务', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (373, '审计', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (374, '风控', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (375, '财务经理', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (376, 'CFO', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (377, '财务总监', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (378, '财务主管', 365, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (379, '法务', 342, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (380, '法务专员/助理', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (381, '律师', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (382, '法律顾问', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (383, '法务主管', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (384, '法务经理', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (385, '法务总监', 379, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (386, '其他职能职位', 342, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (387, '其他职能职位', 386, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (388, '销售', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (389, '销售', 388, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (390, '销售', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (391, '销售专员', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (392, '销售经理', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (393, '客户代表', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (394, '大客户代表', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (395, 'BD经理', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (396, '商务渠道', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (397, '渠道销售', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (398, '代理商销售', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (399, '销售助理', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (400, '电话销售', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (401, '销售顾问', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (402, '商品经理', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (403, '广告销售', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (404, '网络营销', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (405, '营销主管', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (406, '销售工程师', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (407, '客户经理', 389, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (408, '销售管理', 388, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (409, '销售管理', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (410, '销售总监', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (411, '商务总监', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (412, '区域总监', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (413, '城市经理', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (414, '销售VP', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (415, '团队经理', 408, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (416, '其他销售职位', 388, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (417, '其他销售职位', 416, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (418, '传媒', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (419, '采编/写作/出版', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (420, '采编/写作/出版', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (421, '记者', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (422, '编辑', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (423, '采编', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (424, '撰稿人', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (425, '出版发行', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (426, '校对录入', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (427, '总编', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (428, '自媒体', 419, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (429, '公关媒介', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (430, '公关媒介', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (431, '媒介经理', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (432, '媒介专员', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (433, '广告协调', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (434, '品牌公关', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (435, '活动策划执行', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (436, '媒介策划', 429, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (437, '会务会展', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (438, '会务会展', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (439, '会议活动销售', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (440, '会议活动策划', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (441, '会议活动执行', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (442, '会展活动销售', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (443, '会展活动策划', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (444, '会展活动执行', 437, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (445, '广告', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (446, '广告', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (447, '广告创意', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (448, '美术指导', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (449, '广告设计师', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (450, '策划经理', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (451, '文案', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (452, '广告制作', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (453, '媒介投放', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (454, '媒介合作', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (455, '媒介顾问', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (456, '广告审核', 445, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (457, '影视媒体', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (458, '主持人/DJ', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (459, '主播助理', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (460, '灯光师', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (461, '剪辑师', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (462, '影视特效', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (463, '影视媒体', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (464, '艺人助理', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (465, '统筹制片人', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (466, '执行制片人', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (467, '导演/编导', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (468, '摄影/摄像', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (469, '视频编辑', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (470, '音频编辑', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (471, '经纪人', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (472, '后期制作', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (473, '影视制作', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (474, '影视发行', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (475, '影视策划', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (476, '主播', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (477, '演员/配音/模特', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (478, '化妆/造型/服装', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (479, '放映管理', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (480, '录音/音效', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (481, '制片人', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (482, '编剧', 457, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (483, '其他传媒职位', 418, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (484, '其他传媒职位', 483, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (485, '金融', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (486, '投融资', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (487, '投融资', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (488, '投资经理', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (489, '行业研究', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (490, '资产管理', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (491, '投资总监', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (492, '投资VP', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (493, '投资合伙人', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (494, '融资', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (495, '并购', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (496, '投后管理', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (497, '投资助理', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (498, '其他投融资职位', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (499, '投资顾问', 486, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (500, '风控', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (501, '风控', 500, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (502, '律师', 500, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (503, '资信评估', 500, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (504, '合规稽查', 500, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (505, '税务审计', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (506, '审计', 505, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (507, '法务', 505, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (508, '会计', 505, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (509, '清算', 505, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (510, '银行', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (511, '银行', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (512, '信用卡销售', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (513, '分析师', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (514, '柜员', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (515, '商务渠道', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (516, '大堂经理', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (517, '理财顾问', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (518, '客户经理', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (519, '信贷管理', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (520, '风控', 510, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (521, '互联网金融', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (522, '互联网金融', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (523, '金融产品经理', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (524, '风控', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (525, '催收员', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (526, '分析师', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (527, '投资经理', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (528, '交易员', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (529, '理财顾问', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (530, '合规稽查', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (531, '审计', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (532, '清算', 521, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (533, '保险', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (534, '保险业务', 533, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (535, '精算师', 533, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (536, '保险理赔', 533, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (537, '证券', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (538, '证券', 537, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (539, '证券经纪人', 537, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (540, '证券分析师', 537, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (541, '其他金融职位', 485, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (542, '其他金融职位', 541, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (543, '教育培训', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (544, '教育产品研发', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (545, '教育产品研发', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (546, '课程设计', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (547, '课程编辑', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (548, '教师', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (549, '培训研究', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (550, '培训师', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (551, '培训策划', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (552, '其他教育产品研发职位', 544, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (553, '教育行政', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (554, '园长/副园长', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (555, '教育行政', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (556, '校长/副校长', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (557, '教务管理', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (558, '教学管理', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (559, '班主任/辅导员', 553, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (560, '教师', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (561, '日语教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (562, '其他外语教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (563, '语文教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (564, '数学教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (565, '物理教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (566, '化学教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (567, '生物教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (568, '教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (569, '助教', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (570, '高中教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (571, '初中教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (572, '小学教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (573, '幼教', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (574, '理科教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (575, '文科教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (576, '英语教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (577, '音乐教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (578, '美术教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (579, '体育教师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (580, '就业老师', 560, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (581, 'IT培训', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (582, 'IT培训', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (583, 'JAVA培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (584, 'Android培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (585, 'iOS培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (586, 'PHP培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (587, '.NET培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (588, 'C++培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (589, 'Unity 3D培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (590, 'Web前端培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (591, '软件测试培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (592, '动漫培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (593, 'UI设计培训讲师', 581, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (594, '职业培训', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (595, '财会培训讲师', 594, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (596, 'HR培训讲师', 594, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (597, '培训师', 594, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (598, '拓展培训', 594, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (599, '招生', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (600, '课程顾问', 599, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (601, '招生顾问', 599, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (602, '留学顾问', 599, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (603, '特长培训', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (604, '武术教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (605, '轮滑教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (606, '表演教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (607, '机器人教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (608, '书法教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (609, '钢琴教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (610, '吉他教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (611, '古筝教师', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (612, '教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (613, '舞蹈教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (614, '瑜伽教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (615, '瘦身顾问', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (616, '游泳教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (617, '健身教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (618, '篮球/羽毛球教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (619, '跆拳道教练', 603, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (620, '其他教育培训职位', 543, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (621, '其他教育培训职位', 620, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (622, '医疗健康', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (623, '临床试验', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (624, '临床研究', 623, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (625, '临床协调', 623, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (626, '临床数据分析', 623, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (627, '医学总监', 623, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (628, '医生/医技', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (629, '医生助理', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (630, '医学影像', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (631, 'B超医生', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (632, '中医', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (633, '医师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (634, '心理医生', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (635, '药剂师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (636, '牙科医生', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (637, '康复治疗师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (638, '验光师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (639, '放射科医师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (640, '检验科医师', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (641, '其他医生职位', 628, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (642, '护士/护理', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (643, '护士长', 642, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (644, '护士/护理', 642, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (645, '导医', 642, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (646, '健康整形', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (647, '健康整形', 646, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (648, '营养师', 646, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (649, '整形师', 646, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (650, '理疗师', 646, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (651, '针灸推拿', 646, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (652, '生物制药', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (653, '生物制药', 652, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (654, '药品注册', 652, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (655, '药品生产', 652, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (656, '医学总监', 652, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (657, '医药研发', 652, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (658, '医疗器械', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (659, '医疗器械注册', 658, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (660, '医疗器械生产/质量管理', 658, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (661, '医疗器械研发', 658, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (662, '药店', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (663, '药店店长', 662, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (664, '执业药师/驻店药师', 662, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (665, '药店店员', 662, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (666, '医学营销/媒体', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (667, '医学营销/媒体', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (668, '医疗器械销售', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (669, '医学编辑', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (670, '药学编辑', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (671, '医药代表', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (672, '健康顾问', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (673, '医美咨询', 666, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (674, '其他医疗健康职位', 622, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (675, '其他医疗健康职位', 674, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (676, '采购/贸易', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (677, '采购', 676, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (678, '采购', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (679, '采购总监', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (680, '采购经理', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (681, '采购专员', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (682, '买手', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (683, '采购工程师', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (684, '采购主管', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (685, '采购助理', 677, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (686, '进出口贸易', 676, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (687, '进出口贸易', 686, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (688, '外贸经理', 686, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (689, '外贸专员', 686, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (690, '外贸业务员', 686, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (691, '贸易跟单', 686, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (692, '其他采购/贸易职位', 676, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (693, '其他采购/贸易类职位', 692, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (694, '供应链/物流', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (695, '物流', 694, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (696, '物流', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (697, '供应链专员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (698, '供应链经理', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (699, '物流专员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (700, '物流经理', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (701, '物流运营', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (702, '物流跟单', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (703, '贸易跟单', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (704, '物仓调度', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (705, '物仓项目', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (706, '运输经理/主管', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (707, '货运代理专员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (708, '货运代理经理', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (709, '水/空/陆运操作', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (710, '报关员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (711, '报检员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (712, '核销员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (713, '单证员', 695, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (714, '仓储', 694, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (715, '仓储', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (716, '仓储物料经理', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (717, '仓储物料专员', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (718, '仓储物料项目', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (719, '仓储管理', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (720, '仓库文员', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (721, '配/理/拣/发货', 714, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (722, '运输', 694, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (723, '运输', 722, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (724, '货运司机', 722, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (725, '集装箱管理', 722, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (726, '配送', 722, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (727, '快递', 722, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (728, '高端供应链职位', 694, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (729, '高端供应链职位', 728, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (730, '供应链总监', 728, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (731, '物流总监', 728, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (732, '其他供应链职位', 694, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (733, '其他供应链职位', 732, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (734, '房地产/建筑', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (735, '房地产规划开发', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (736, '房地产规划开发', 735, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (737, '房产策划', 735, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (738, '地产项目管理', 735, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (739, '地产招投标', 735, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (740, '设计装修与市政建设', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (741, '弱电工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (742, '给排水工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (743, '暖通工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (744, '幕墙工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (745, '软装设计师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (746, '施工员', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (747, '测绘/测量', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (748, '材料员', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (749, 'BIM工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (750, '装修项目经理', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (751, '设计装修与市政建设', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (752, '高级建筑工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (753, '建筑工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (754, '建筑设计师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (755, '土木/土建/结构工程师', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (756, '室内设计', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (757, '园林设计', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (758, '城市规划设计', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (759, '工程监理', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (760, '工程造价', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (761, '预结算', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (762, '工程资料管理', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (763, '建筑施工现场管理', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (764, '景观设计', 740, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (765, '房地产经纪', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (766, '房地产经纪', 765, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (767, '地产置业顾问', 765, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (768, '地产评估', 765, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (769, '地产中介', 765, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (770, '物业管理', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (771, '物业维修', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (772, '绿化工', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (773, '物业管家', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (774, '物业经理', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (775, '物业租赁销售 ', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (776, '物业招商管理', 770, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (777, '高端房地产职位', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (778, '高端房地产职位', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (779, '地产项目总监', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (780, '地产策划总监', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (781, '地产招投标总监', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (782, '物业总监', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (783, '房地产销售总监', 777, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (784, '其他房地产职位', 734, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (785, '其他房地产职位', 784, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (786, '咨询/翻译/法律', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (787, '咨询/调研', 786, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (788, '知识产权/专利/商标代理人', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (789, '心理咨询', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (790, '婚恋咨询', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (791, '咨询/调研', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (792, '企业管理咨询', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (793, '咨询总监', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (794, '数据分析师', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (795, '咨询经理', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (796, '财务咨询顾问', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (797, 'IT咨询顾问', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (798, '人力资源顾问', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (799, '咨询项目管理', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (800, '战略咨询', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (801, '猎头顾问', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (802, '市场调研', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (803, '其他咨询顾问', 787, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (804, '律师', 786, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (805, '知识产权律师', 804, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (806, '律师助理', 804, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (807, '专利律师', 804, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (808, '事务所律师', 804, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (809, '公司法务', 804, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (810, '翻译', 786, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (811, '英语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (812, '日语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (813, '韩语/朝鲜语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (814, '法语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (815, '同声传译  ', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (816, '德语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (817, '俄语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (818, '西班牙语翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (819, '其他语种翻译', 810, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (820, '其他咨询类职位', 786, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (821, '其他咨询/翻译类职位', 820, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (822, '管培生/储备干部', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (823, '管培生', 822, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (824, '管理培训生', 823, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (825, '储备干部', 823, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (826, '其他管培生职位', 822, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (827, '其他实习/培训/储备职位', 826, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (828, '旅游', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (829, '旅游服务', 828, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (830, '旅游服务', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (831, '计调', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (832, '签证', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (833, '旅游顾问', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (834, '导游', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (835, '预定票务', 829, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (836, '旅游产品开发/策划', 828, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (837, '旅游产品开发/策划', 836, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (838, '旅游产品经理', 836, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (839, '旅游策划师', 836, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (840, '其他旅游职位', 828, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (841, '其他旅游职位', 840, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (842, '服务业', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (843, '安保/家政/维修', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (844, '保安', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (845, '保洁', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (846, '保姆', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (847, '月嫂', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (848, '育婴师', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (849, '护工', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (850, '安检员', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (851, '手机维修', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (852, '家电维修', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (853, '保安经理', 843, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (854, '宠物服务', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (855, '宠物美容', 854, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (856, '宠物医生', 854, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (857, '婚礼/花艺', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (858, '花艺师', 857, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (859, '婚礼策划师', 857, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (860, '美容保健', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (861, '彩妆顾问', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (862, '纹绣师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (863, '美体师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (864, '美发学徒', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (865, '美容店长', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (866, '足疗师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (867, '按摩师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (868, '发型师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (869, '美甲师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (870, '化妆师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (871, '养发师', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (872, '美容师/顾问', 860, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (873, '酒店', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (874, '礼仪迎宾', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (875, '前厅经理', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (876, '客房经理', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (877, '收银', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (878, '酒店前台', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (879, '客房服务员', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (880, '酒店经理', 873, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (881, '餐饮', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (882, '后厨', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (883, '配菜打荷', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (884, '茶艺师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (885, '西点师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (886, '餐饮学徒', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (887, '面点师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (888, '行政总厨', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (889, '厨师长', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (890, '传菜员', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (891, '洗碗工', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (892, '凉菜厨师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (893, '中餐厨师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (894, '西餐厨师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (895, '日式厨师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (896, '烧烤师傅', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (897, '餐饮', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (898, '收银', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (899, '服务员', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (900, '厨师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (901, '咖啡师', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (902, '送餐员', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (903, '餐饮店长', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (904, '领班', 881, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (905, '零售', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (906, '督导/巡店', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (907, '陈列员', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (908, '理货员', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (909, '防损员', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (910, '卖场经理', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (911, '收银', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (912, '导购', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (913, '店员/营业员', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (914, '门店店长', 905, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (915, '运动健身', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (916, '会籍顾问', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (917, '救生员', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (918, '健身', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (919, '瑜伽教练', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (920, '瘦身顾问', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (921, '游泳教练', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (922, '美体教练', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (923, '舞蹈教练', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (924, '健身教练', 915, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (925, '其他服务业职位', 842, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (926, '其他服务业职位', 925, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (927, '生产制造', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (928, '生产营运', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (929, '生产营运', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (930, '厂长/工厂经理', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (931, '生产总监', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (932, '生产经理/车间主任', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (933, '生产组长/拉长', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (934, '生产员', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (935, '生产设备管理', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (936, '生产计划/物料控制', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (937, '生产跟单', 928, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (938, '质量安全', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (939, '质检员', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (940, '质量管理/测试', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (941, '可靠度工程师', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (942, '故障分析师', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (943, '认证工程师', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (944, '体系工程师', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (945, '审核员', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (946, '安全员', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (947, '汽车质量工程师', 938, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (948, '新能源', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (949, '电池工程师', 948, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (950, '电机工程师', 948, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (951, '线束设计', 948, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (952, '充电桩设计', 948, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (953, '汽车制造', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (954, '汽车设计', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (955, '车身/造型设计', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (956, '底盘工程师', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (957, '动力系统工程师', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (958, '汽车电子工程师', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (959, '汽车零部件设计', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (960, '汽车项目管理', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (961, '内外饰设计工程师', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (962, '总装工程师', 953, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (963, '汽车销售与服务', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (964, '汽车销售', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (965, '汽车配件销售', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (966, '汽车服务顾问', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (967, '汽车维修', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (968, '汽车美容', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (969, '汽车定损理赔', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (970, '二手车评估师', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (971, '4S店店长/维修站长', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (972, '汽车改装工程师', 963, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (973, '机械设计/制造', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (974, '机械设计/制造', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (975, '热传导', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (976, '精益工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (977, '机械工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (978, '机械设计师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (979, '机械设备工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (980, '机械维修/保养', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (981, '机械制图', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (982, '机械结构工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (983, '工业工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (984, '工艺/制程工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (985, '材料工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (986, '机电工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (987, 'CNC/数控', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (988, '冲压工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (989, '夹具工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (990, '模具工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (991, '焊接工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (992, '注塑工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (993, '铸造/锻造工程师', 973, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (994, '化工', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (995, '化工工程师', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (996, '实验室技术员', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (997, '化学分析', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (998, '涂料研发', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (999, '化妆品研发', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1000, '食品/饮料研发', 994, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1001, '服装/纺织/皮革', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1002, '服装/纺织设计', 1001, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1003, '面料辅料开发', 1001, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1004, '打样/制版', 1001, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1005, '服装/纺织/皮革跟单', 1001, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1006, '技工/普工', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1007, '缝纫工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1008, '搬运工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1009, '普工/操作工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1010, '叉车', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1011, '铲车', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1012, '焊工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1013, '氩弧焊工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1014, '电工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1015, '木工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1016, '漆工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1017, '车工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1018, '磨工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1019, '铣工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1020, '钳工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1021, '钻工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1022, '铆工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1023, '钣金', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1024, '抛光', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1025, '机修工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1026, '折弯工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1027, '电镀工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1028, '喷塑工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1029, '注塑工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1030, '组装工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1031, '包装工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1032, '空调工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1033, '电梯工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1034, '锅炉工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1035, '学徒工', 1006, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1036, '其他生产制造职位', 927, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1037, '其他生产制造职位', 1036, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1038, '其他', 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1039, '其他职位类别', 1038, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_recruit_post_type` VALUES (1040, '其他职位', 1039, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_salary_archives
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_archives`;
CREATE TABLE `wk_hrm_salary_archives`  (
  `id` bigint NOT NULL,
  `change_reason` int NOT NULL COMMENT '调薪原因 1 入职核定 2 转正 3 晋升 4 调动 5 年中调薪 6 年度调薪 7 特别调薪 8 其他',
  `change_data` date NOT NULL COMMENT '最近调整日期',
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `change_type` int NULL DEFAULT 0 COMMENT '0 未定薪 1 已定薪 2 已调薪',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_archives_change_reason_index`(`change_reason` ASC) USING BTREE,
  INDEX `wk_hrm_salary_archives_employee_id_index`(`employee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '薪资档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_archives
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_archives_option
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_archives_option`;
CREATE TABLE `wk_hrm_salary_archives_option`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `is_pro` int NOT NULL COMMENT '是否是试用期 0 正式 1 试用期',
  `code` int NOT NULL COMMENT '薪资项code',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '薪资项名称',
  `value` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '薪资',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_archives_option_employee_id_index`(`employee_id` ASC) USING BTREE,
  INDEX `wk_hrm_salary_archives_option_is_pro_index`(`is_pro` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '薪资档案选项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_archives_option
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_change_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_change_record`;
CREATE TABLE `wk_hrm_salary_change_record`  (
  `id` bigint NOT NULL,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `record_type` int NOT NULL DEFAULT 1 COMMENT '记录类型 1 定薪 2 调薪',
  `change_reason` int NOT NULL COMMENT '调薪原因 1 入职核定 2 转正 3 晋升 4 调动 5 年中调薪 6 年度调薪 7 特别调薪 8 其他',
  `enable_date` date NOT NULL COMMENT '生效时间',
  `pro_before_sum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '试用期调整前工资',
  `pro_after_sum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '试用期调整后工资',
  `pro_salary` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '试用期工资明细',
  `before_sum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '正式调整前工资 json',
  `after_sum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '正式调整后工资',
  `salary` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '正式工资明细 json',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态 0 未生效 1 已生效 2 已取消',
  `employee_status` int NULL DEFAULT NULL,
  `before_total` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '调整前总薪资',
  `after_total` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '调整后总薪资',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint NOT NULL,
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_change_record_employee_id_index`(`employee_id` ASC) USING BTREE,
  INDEX `wk_hrm_salary_change_record_status_index`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定薪调薪记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_change_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_change_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_change_template`;
CREATE TABLE `wk_hrm_salary_change_template`  (
  `id` bigint NOT NULL,
  `template_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `is_default` int NOT NULL DEFAULT 0 COMMENT '是否默认 0 否 1 是',
  `value` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '调薪模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_change_template
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_config
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_config`;
CREATE TABLE `wk_hrm_salary_config`  (
  `config_id` bigint NOT NULL,
  `salary_cycle_start_day` int NULL DEFAULT NULL COMMENT '计薪周期开始日',
  `salary_cycle_end_day` int NULL DEFAULT NULL COMMENT '计薪周期结束日',
  `pay_type` int NULL DEFAULT NULL COMMENT '发薪日期类型 1 当月 2 次月',
  `pay_day` int NULL DEFAULT NULL COMMENT '发薪日期',
  `social_security_month_type` int NULL DEFAULT NULL COMMENT '对应社保自然月 0上月 1当月 2次月',
  `salary_start_month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '薪酬起始月份（例2020.05）',
  `social_security_start_month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '社保开始月（例2020.05）',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '薪资初始配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_config
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_group
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_group`;
CREATE TABLE `wk_hrm_salary_group`  (
  `group_id` bigint NOT NULL COMMENT '薪资组id',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '薪资组名称',
  `dept_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门范围',
  `employee_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '员工范围',
  `salary_standard` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '21.75' COMMENT '月计薪标准',
  `change_rule` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '按转正/调薪前后的工资混合计算' COMMENT '转正、调薪月规则',
  `rule_id` bigint NULL DEFAULT NULL COMMENT '计税规则id',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '薪资组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_group
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_group_relation_dept
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_group_relation_dept`;
CREATE TABLE `wk_hrm_salary_group_relation_dept`  (
  `salary_group_relation_dept_id` bigint NOT NULL,
  `salary_group_id` bigint NULL DEFAULT NULL COMMENT '薪资组id',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`salary_group_relation_dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '薪资组关联部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_group_relation_dept
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_group_relation_employee
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_group_relation_employee`;
CREATE TABLE `wk_hrm_salary_group_relation_employee`  (
  `salary_group_relation_employee_id` bigint NOT NULL,
  `salary_group_id` bigint NULL DEFAULT NULL COMMENT '薪资组id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`salary_group_relation_employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '薪资组关联员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_group_relation_employee
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_month_emp_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_month_emp_record`;
CREATE TABLE `wk_hrm_salary_month_emp_record`  (
  `s_emp_record_id` bigint NOT NULL,
  `s_record_id` bigint NULL DEFAULT NULL COMMENT '每月生成薪资id',
  `employee_id` bigint NULL DEFAULT NULL COMMENT '员工id',
  `actual_work_day` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际计薪时长',
  `need_work_day` decimal(10, 2) NULL DEFAULT NULL COMMENT '月计薪时长',
  `year` int NULL DEFAULT NULL COMMENT '年',
  `month` int NULL DEFAULT NULL COMMENT '月',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`s_emp_record_id`) USING BTREE,
  INDEX `wk_hrm_salary_month_emp_record_employee_id_index`(`employee_id` ASC) USING BTREE,
  INDEX `wk_hrm_salary_month_emp_record_s_record_id_index`(`s_record_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工每月薪资记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_month_emp_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_month_option_value
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_month_option_value`;
CREATE TABLE `wk_hrm_salary_month_option_value`  (
  `id` bigint NOT NULL,
  `s_emp_record_id` bigint NULL DEFAULT NULL COMMENT '每月员工薪资记录id',
  `code` int NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_month_option_value_s_emp_record_id_code_index`(`s_emp_record_id` ASC, `code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每月员工薪资项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_month_option_value
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_month_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_month_record`;
CREATE TABLE `wk_hrm_salary_month_record`  (
  `s_record_id` bigint NOT NULL,
  `title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报表标题',
  `year` int NULL DEFAULT NULL COMMENT '年',
  `month` int NULL DEFAULT NULL COMMENT '月',
  `num` int NULL DEFAULT NULL COMMENT '计薪人数',
  `start_time` date NULL DEFAULT NULL COMMENT '计薪开始日期',
  `end_time` date NULL DEFAULT NULL COMMENT '计薪结束日期',
  `personal_insurance_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '个人社保金额',
  `personal_provident_fund_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '个人公积金金额',
  `corporate_insurance_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '公司社保金额',
  `corporate_provident_fund_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '公司社保金额',
  `expected_pay_salary` decimal(18, 2) NULL DEFAULT NULL COMMENT '预应发工资',
  `personal_tax` decimal(18, 2) NULL DEFAULT NULL COMMENT '个人所得税',
  `real_pay_salary` decimal(18, 2) NULL DEFAULT NULL COMMENT '预计实发工资',
  `option_head` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '薪资项表头',
  `examine_record_id` bigint NULL DEFAULT NULL COMMENT '审核记录id',
  `check_status` int NULL DEFAULT 5 COMMENT '状态  0待审核、1通过、2拒绝、3审核中 4:撤回 5 未提交  10 历史薪资 11核算完成',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`s_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每月薪资记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_month_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_option
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_option`;
CREATE TABLE `wk_hrm_salary_option`  (
  `option_id` bigint NOT NULL,
  `code` int NULL DEFAULT NULL,
  `parent_code` int NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_fixed` int NULL DEFAULT NULL COMMENT '是否固定 0 否 1 是',
  `is_plus` int NULL DEFAULT NULL COMMENT '是否加项 0 减 1 加',
  `is_tax` int NULL DEFAULT NULL COMMENT '是否计税 0 否 1 是',
  `is_show` int NULL DEFAULT NULL COMMENT '是否展示 0 否 1 是',
  `is_compute` int NULL DEFAULT NULL COMMENT '是否参与薪资计算 0 否 1 是',
  `is_open` int NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`option_id`) USING BTREE,
  INDEX `wk_hrm_salary_option_code_parent_code_index`(`code` ASC, `parent_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统薪资项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_option
-- ----------------------------
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855754, 10, 0, '基本工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855755, 10101, 10, '基本工资', '劳动合同中约定的标准工资。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855756, 10102, 10, '岗位工资', '根据岗位、责任、技能要求等，不同岗位，工资不同。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855757, 10103, 10, '职务工资', '根据职务高低、业务技术水平等因素的不同，工资不同，随职务变动而变动。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855758, 20, 0, '津补贴', '企业给员工提供的福利', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855759, 20101, 20, '住房补贴', '为职工解决住房问题而给予的补贴资助。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855760, 20102, 20, '高温津贴', '用人单位安排劳动者在高温天气下工作，当温度高于33℃，应支付高温补贴。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855761, 20103, 20, '交通补贴', '企业按月按标准支付的交通补贴', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855762, 20104, 20, '餐补', '企业不统一供餐，而是按月按标准支付的午饭或晚餐补贴', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855763, 30, 0, '浮动工资', '浮动工资需要在每月算薪前手工录入', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855764, 30101, 30, '绩效工资', '以员工的实际劳动成果或表现为考核标准，支付一定的奖励工资。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855765, 40, 0, '奖金', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855766, 40101, 40, '季度奖', '对于表现优秀的员工，按季度给予一定的奖励工资。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855767, 40102, 40, '全勤奖', '对于按公司规定到岗工作，未出现任何迟到、早退的员工给予的奖励，一般以月度为考核期限。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855768, 40103, 40, '推荐奖', '企业在招揽人才的过程中，对推荐人才并顺利入职的内部员工，给予的奖励工资。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855769, 50, 0, '提成工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855770, 50101, 50, '销售提成', '对于销售或业务人员，通常按照一定的比例，企业和员工之间对盈利进行分成。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855771, 60, 0, '计件工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855772, 60101, 60, '计件工资', '根据职工完成的劳动数量和按事先规定的计件单价计算和支付的工资。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855773, 70, 0, '计时工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855774, 70101, 70, '计时工资', '根据职工的工作时间,按照工资标准、等级计算和支付的工资', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855775, 80, 0, '工龄/司龄工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855776, 80101, 80, '工龄工资', '企业按照员工的工作年数，即员工的工作经验和劳动贡献的积累给予的经济补偿。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855777, 80102, 80, '司龄工资', '司龄工资是按照劳动者在在公司做的时间的长短进行相应工资的计算。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855778, 90, 0, '职称工资', NULL, 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855779, 90101, 90, '职称工资', '工资高低与职称挂钩，职称越高，工资越高。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855780, 90102, 90, '技能工资', '以员工个人所掌握的知识、技术和所具备的能力为基础来进行工资报酬的支付。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855781, 90103, 90, '学历工资', '根据员工在教育机构取得的学历而给予适当的津贴。', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855782, 100, 0, '代扣代缴', '公司代替个人缴纳的费用，如个人社保、个人公积金。该类别下的薪酬项，将从应纳税所得额中扣除，影响个税计算', 0, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855783, 100101, 100, '个人社保', '社保中个人缴纳的部分', 0, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855784, 100102, 100, '个人公积金', '公积金中个人缴纳的部分', 0, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855785, 110, 0, '企业社保', '该类别下的薪酬项，不参与工资计算，只参与企业成本统计或社保成本分析', 0, 1, 0, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855786, 110101, 110, '企业社保', '企业承担的社保费用', 0, 1, 0, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855787, 120, 0, '企业公积金', '该类别下的薪酬项，不参与工资计算，只参与企业成本统计或社保成本分析', 0, 1, 0, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855788, 120101, 120, '企业公积金', '企业承担的公积金费用', 0, 1, 0, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855789, 130, 0, '税前补发', '税前补发，与该月的工资一起发放，需参与计税', 0, 1, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855790, 130101, 130, '税前补发', '如上月漏发，这月补发的工资', 0, 1, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855791, 140, 0, '税前补扣', '税前从该月的工资中补扣的金额，会影响本月的应税工资总额', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855792, 140101, 140, '税前补扣', '如上月未扣，这月补扣的扣款；', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855793, 150, 0, '税后补发', '税后补发，不参与该月工资计算，会影响当月的实际所得', 0, 1, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855794, 150101, 150, '税后补发', '其它税后补发', 0, 1, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855795, 160, 0, '税后补扣', '从税后的工资中补扣的款项，不参与该月的工资计税', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855796, 160101, 160, '党费', '向党组织交纳的用于党的事业和党的活动的经费，由公司代收。', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855797, 160102, 160, '工会费', '员工缴纳的，工会组织开展各项活动所需要的费用', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855798, 160103, 160, '补充医疗', '企业在基本医疗保险的基础上，为员工缴纳的额外补充医疗保险。', 0, 0, 0, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855799, 170, 0, '特殊计税项', '其它里的科目不参与工资计算，但要计税', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855800, 170101, 170, '商业保险金', '商业保险金不参与计算，但要计税', 0, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855801, 180, 0, '加班工资', '公司按员工加班时长计算的加班报酬', 1, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855802, 180101, 180, '加班工资', '按一定的比例，按加班时长计算加班工资', 1, 1, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855803, 190, 0, '考勤扣款明细', NULL, 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855804, 190101, 190, '迟到扣款', '员工无故迟到，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855805, 190102, 190, '早退扣款', '员工无故早退，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855806, 190103, 190, '旷工扣款', '员工无故旷工，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855807, 190104, 190, '假期扣款', '员工因请事假、病假等，工作日未能到岗，需扣除一定的工资', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855808, 190105, 190, '缺卡扣款', '员工无故缺卡，需扣除一定的金额作为惩戒', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855809, 190106, 190, '综合扣款', '按月累计迟到和早退时长或次数，计算的考勤扣款金额', 1, 0, 1, 1, 1, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855810, 200, 0, '考勤扣款合计', NULL, 1, 0, 1, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855811, 200101, 200, '考勤扣款合计', '迟到、早退、旷工、缺卡、请假等6中异常考勤的扣款合计', 1, 0, 1, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855812, 210, 0, '应发工资', NULL, 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855813, 210101, 210, '应发工资', '应发工资=员工工资总额-请假扣款-考勤扣款等', 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855814, 220, 0, '应税工资', NULL, 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855815, 220101, 220, '应税工资', '应税工资=应发工资-个人社保-个人公积金-每月减除费用', 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855816, 230, 0, '个人所得税', NULL, 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855817, 230101, 230, '个人所得税', '根据个税计算规则，计算的每月应缴纳个税', 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855818, 240, 0, '实发工资', NULL, 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855819, 240101, 240, '实发工资', '员工每月获得的实际收入。', 1, 2, 2, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855820, 250, 0, '上月个税累计信息', '上月个税累计信息，将影响当月个税的计算。与税务系统不一致时可手动调整', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855821, 250101, 250, '累计收入额（截至上月）', '同一个纳税年度内，员工在该企业累计至上个月份的收入额（收入额=应发工资-税后补发+其它）', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855822, 250102, 250, '累计减除费用（截至上月）', '同一个纳税年度内，员工在该企业累计至上一个月份的基本减除，按5000每月扣除标准计算', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855823, 250103, 250, '累计专项扣除（截至上月）', '同一个纳税年度内，员工在该企业累计至上个月份的个人社保费用、个人公积金等费用', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855824, 250105, 250, '累计已预缴税额', '同一个纳税年度内，员工在该企业累计至上个月份（含）的累计已缴纳个税', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855825, 260, 0, '个税专项附加扣除累计', '个税抵扣项目，将影响个税的计算', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855826, 260101, 260, '累计子女教育', '同一个纳税年度内，员工在该企业累计至当前月份的子女教育扣除', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855827, 260102, 260, '累计住房租金', '同一个纳税年度内，员工在该企业累计至当前月份的住房租金扣除', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855828, 260103, 260, '累计住房贷款利息', '同一个纳税年度内，员工在该企业累计至当前月份的住房贷款利息扣除', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855829, 260104, 260, '累计赡养老人', '同一个纳税年度内，员工在该企业累计至当前月份的赡养老人扣除', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855830, 260105, 260, '累计继续教育', '同一个纳税年度内，员工在该企业累计至当前月份的继续教育扣除', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855831, 270, 0, '个税累计信息', '使用累计预扣法计算个税时，需要使用的个税累计信息', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855832, 270101, 270, '累计收入额', '同一个纳税年度内，员工在该企业累计至当前月份的收入额（收入额=应发工资-税后补发+其它）', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855833, 270102, 270, '累计减除费用', '同一个纳税年度内，员工在该企业累计至当前月份的基本减除，按5000每月扣除标准计算', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855834, 270103, 270, '累计专项扣除', '同一个纳税年度内，员工在该企业累计至当前月份的个人社保、个人公积金等费用', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855835, 270104, 270, '累计专项附加扣除', '同一个纳税年度内，员工在该企业累计至当前月份的五项专项附加扣除合计', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855836, 270105, 270, '累计应纳税所得额', '同一个纳税年度内，员工在该企业累计至当前月份的应税工资', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_option` VALUES (1481534121629855837, 270106, 270, '累计应纳税额', '同一个纳税年度内，员工在该企业累计至当前月份的累计应缴个税', 1, 2, NULL, 1, 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_salary_option_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_option_template`;
CREATE TABLE `wk_hrm_salary_option_template`  (
  `id` bigint NOT NULL,
  `code` int NULL DEFAULT NULL,
  `parent_code` int NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_fixed` int NULL DEFAULT NULL COMMENT '是否固定 0 否 1 是',
  `is_plus` int NULL DEFAULT NULL COMMENT '是否加项 0 减 1 加 2 计算所得',
  `is_tax` int NULL DEFAULT NULL COMMENT '是否计税 0 否 1 是',
  `is_show` int NULL DEFAULT NULL COMMENT '是否展示 0 否 1 是',
  `is_compute` int NULL DEFAULT NULL COMMENT '是否参与薪资计算 0 否 1 是',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统薪资项模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_option_template
-- ----------------------------
INSERT INTO `wk_hrm_salary_option_template` VALUES (1, 10, 0, '基本工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (2, 10101, 10, '基本工资', '劳动合同中约定的标准工资。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (3, 10102, 10, '岗位工资', '根据岗位、责任、技能要求等，不同岗位，工资不同。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (4, 10103, 10, '职务工资', '根据职务高低、业务技术水平等因素的不同，工资不同，随职务变动而变动。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (5, 20, 0, '津补贴', '企业给员工提供的福利', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (6, 20101, 20, '住房补贴', '为职工解决住房问题而给予的补贴资助。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (7, 20102, 20, '高温津贴', '用人单位安排劳动者在高温天气下工作，当温度高于33℃，应支付高温补贴。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (8, 20103, 20, '交通补贴', '企业按月按标准支付的交通补贴', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (9, 20104, 20, '餐补', '企业不统一供餐，而是按月按标准支付的午饭或晚餐补贴', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (10, 30, 0, '浮动工资', '浮动工资需要在每月算薪前手工录入', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (11, 30101, 30, '绩效工资', '以员工的实际劳动成果或表现为考核标准，支付一定的奖励工资。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (12, 40, 0, '奖金', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (13, 40101, 40, '季度奖', '对于表现优秀的员工，按季度给予一定的奖励工资。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (14, 40102, 40, '全勤奖', '对于按公司规定到岗工作，未出现任何迟到、早退的员工给予的奖励，一般以月度为考核期限。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (15, 40103, 40, '推荐奖', '企业在招揽人才的过程中，对推荐人才并顺利入职的内部员工，给予的奖励工资。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (16, 50, 0, '提成工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (17, 50101, 50, '销售提成', '对于销售或业务人员，通常按照一定的比例，企业和员工之间对盈利进行分成。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (18, 60, 0, '计件工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (19, 60101, 60, '计件工资', '根据职工完成的劳动数量和按事先规定的计件单价计算和支付的工资。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (20, 70, 0, '计时工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (21, 70101, 70, '计时工资', '根据职工的工作时间,按照工资标准、等级计算和支付的工资', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (22, 80, 0, '工龄/司龄工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (23, 80101, 80, '工龄工资', '企业按照员工的工作年数，即员工的工作经验和劳动贡献的积累给予的经济补偿。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (24, 80102, 80, '司龄工资', '司龄工资是按照劳动者在在公司做的时间的长短进行相应工资的计算。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (25, 90, 0, '职称工资', NULL, 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (26, 90101, 90, '职称工资', '工资高低与职称挂钩，职称越高，工资越高。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (27, 90102, 90, '技能工资', '以员工个人所掌握的知识、技术和所具备的能力为基础来进行工资报酬的支付。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (28, 90103, 90, '学历工资', '根据员工在教育机构取得的学历而给予适当的津贴。', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (29, 100, 0, '代扣代缴', '公司代替个人缴纳的费用，如个人社保、个人公积金。该类别下的薪酬项，将从应纳税所得额中扣除，影响个税计算', 0, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (30, 100101, 100, '个人社保', '社保中个人缴纳的部分', 0, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (31, 100102, 100, '个人公积金', '公积金中个人缴纳的部分', 0, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (32, 110, 0, '企业社保', '该类别下的薪酬项，不参与工资计算，只参与企业成本统计或社保成本分析', 0, 1, 0, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (33, 110101, 110, '企业社保', '企业承担的社保费用', 0, 1, 0, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (34, 120, 0, '企业公积金', '该类别下的薪酬项，不参与工资计算，只参与企业成本统计或社保成本分析', 0, 1, 0, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (35, 120101, 120, '企业公积金', '企业承担的公积金费用', 0, 1, 0, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (36, 130, 0, '税前补发', '税前补发，与该月的工资一起发放，需参与计税', 0, 1, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (37, 130101, 130, '税前补发', '如上月漏发，这月补发的工资', 0, 1, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (38, 140, 0, '税前补扣', '税前从该月的工资中补扣的金额，会影响本月的应税工资总额', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (39, 140101, 140, '税前补扣', '如上月未扣，这月补扣的扣款；', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (40, 150, 0, '税后补发', '税后补发，不参与该月工资计算，会影响当月的实际所得', 0, 1, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (41, 150101, 150, '税后补发', '其它税后补发', 0, 1, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (42, 160, 0, '税后补扣', '从税后的工资中补扣的款项，不参与该月的工资计税', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (43, 160101, 160, '党费', '向党组织交纳的用于党的事业和党的活动的经费，由公司代收。', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (44, 160102, 160, '工会费', '员工缴纳的，工会组织开展各项活动所需要的费用', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (45, 160103, 160, '补充医疗', '企业在基本医疗保险的基础上，为员工缴纳的额外补充医疗保险。', 0, 0, 0, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (46, 170, 0, '特殊计税项', '其它里的科目不参与工资计算，但要计税', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (47, 170101, 170, '商业保险金', '商业保险金不参与计算，但要计税', 0, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (48, 180, 0, '加班工资', '公司按员工加班时长计算的加班报酬', 1, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (49, 180101, 180, '加班工资', '按一定的比例，按加班时长计算加班工资', 1, 1, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (50, 190, 0, '考勤扣款明细', NULL, 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (51, 190101, 190, '迟到扣款', '员工无故迟到，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (52, 190102, 190, '早退扣款', '员工无故早退，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (53, 190103, 190, '旷工扣款', '员工无故旷工，需扣除一定的工资作为惩戒', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (54, 190104, 190, '假期扣款', '员工因请事假、病假等，工作日未能到岗，需扣除一定的工资', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (55, 190105, 190, '缺卡扣款', '员工无故缺卡，需扣除一定的金额作为惩戒', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (56, 190106, 190, '综合扣款', '按月累计迟到和早退时长或次数，计算的考勤扣款金额', 1, 0, 1, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (57, 200, 0, '考勤扣款合计', NULL, 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (58, 200101, 200, '考勤扣款合计', '迟到、早退、旷工、缺卡、请假等6中异常考勤的扣款合计', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (59, 210, 0, '应发工资', NULL, 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (60, 210101, 210, '应发工资', '应发工资=员工工资总额-请假扣款-考勤扣款等', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (61, 220, 0, '应税工资', NULL, 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (62, 220101, 220, '应税工资', '应税工资=应发工资-个人社保-个人公积金-每月减除费用', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (63, 230, 0, '个人所得税', NULL, 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (64, 230101, 230, '个人所得税', '根据个税计算规则，计算的每月应缴纳个税', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (65, 240, 0, '实发工资', NULL, 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (66, 240101, 240, '实发工资', '员工每月获得的实际收入。', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (67, 250, 0, '上月个税累计信息', '上月个税累计信息，将影响当月个税的计算。与税务系统不一致时可手动调整', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (68, 250101, 250, '累计收入额（截至上月）', '同一个纳税年度内，员工在该企业累计至上个月份的收入额（收入额=应发工资-税后补发+其它）', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (69, 250102, 250, '累计减除费用（截至上月）', '同一个纳税年度内，员工在该企业累计至上一个月份的基本减除，按5000每月扣除标准计算', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (70, 250103, 250, '累计专项扣除（截至上月）', '同一个纳税年度内，员工在该企业累计至上个月份的个人社保费用、个人公积金等费用', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (72, 250105, 250, '累计已预缴税额', '同一个纳税年度内，员工在该企业累计至上个月份（含）的累计已缴纳个税', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (73, 260, 0, '个税专项附加扣除累计', '个税抵扣项目，将影响个税的计算', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (74, 260101, 260, '累计子女教育', '同一个纳税年度内，员工在该企业累计至当前月份的子女教育扣除', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (75, 260102, 260, '累计住房租金', '同一个纳税年度内，员工在该企业累计至当前月份的住房租金扣除', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (76, 260103, 260, '累计住房贷款利息', '同一个纳税年度内，员工在该企业累计至当前月份的住房贷款利息扣除', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (77, 260104, 260, '累计赡养老人', '同一个纳税年度内，员工在该企业累计至当前月份的赡养老人扣除', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (78, 260105, 260, '累计继续教育', '同一个纳税年度内，员工在该企业累计至当前月份的继续教育扣除', 1, 0, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (79, 270, 0, '个税累计信息', '使用累计预扣法计算个税时，需要使用的个税累计信息', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (80, 270101, 270, '累计收入额', '同一个纳税年度内，员工在该企业累计至当前月份的收入额（收入额=应发工资-税后补发+其它）', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (81, 270102, 270, '累计减除费用', '同一个纳税年度内，员工在该企业累计至当前月份的基本减除，按5000每月扣除标准计算', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (82, 270103, 270, '累计专项扣除', '同一个纳税年度内，员工在该企业累计至当前月份的个人社保、个人公积金等费用', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (83, 270104, 270, '累计专项附加扣除', '同一个纳税年度内，员工在该企业累计至当前月份的五项专项附加扣除合计', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (84, 270105, 270, '累计应纳税所得额', '同一个纳税年度内，员工在该企业累计至当前月份的应税工资', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `wk_hrm_salary_option_template` VALUES (85, 270106, 270, '累计应纳税额', '同一个纳税年度内，员工在该企业累计至当前月份的累计应缴个税', 1, 2, NULL, 1, 0, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_salary_slip
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_slip`;
CREATE TABLE `wk_hrm_salary_slip`  (
  `id` bigint NOT NULL,
  `record_id` bigint NOT NULL COMMENT '工资条发放记录id',
  `s_emp_record_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `year` int NULL DEFAULT NULL,
  `month` int NULL DEFAULT NULL,
  `read_status` int NOT NULL DEFAULT 0 COMMENT '查看状态 0 未读 1 已读',
  `real_salary` decimal(10, 2) NOT NULL COMMENT '实发工资',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发放时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_slip_read_status_index`(`read_status` ASC) USING BTREE,
  INDEX `wk_hrm_salary_slip_record_id_index`(`record_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工资条' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_slip
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_slip_option
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_slip_option`;
CREATE TABLE `wk_hrm_salary_slip_option`  (
  `id` bigint NOT NULL,
  `slip_id` bigint NOT NULL COMMENT '工资条id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '薪资项名称',
  `type` int NOT NULL COMMENT '选项类型 1 分类 2 薪资项',
  `code` int NOT NULL DEFAULT 0 COMMENT '薪资项code',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '薪资项value',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `pid` bigint NOT NULL DEFAULT 0 COMMENT '父级分类id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_slip_option_slip_id_index`(`slip_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工资条工资项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_slip_option
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_slip_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_slip_record`;
CREATE TABLE `wk_hrm_salary_slip_record`  (
  `id` bigint NOT NULL,
  `s_record_id` bigint NOT NULL COMMENT '每月薪资记录id',
  `salary_num` int NOT NULL COMMENT '薪资表总人数',
  `pay_num` int NOT NULL COMMENT '发放人数',
  `year` int NOT NULL,
  `month` int NOT NULL,
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `wk_hrm_salary_slip_record_year_month_index`(`year` ASC, `month` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '发工资条记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_slip_record
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_slip_template
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_slip_template`;
CREATE TABLE `wk_hrm_salary_slip_template`  (
  `id` bigint NOT NULL,
  `template_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `hide_empty` int NOT NULL DEFAULT 0 COMMENT '是否隐藏空的工资项 0 不隐藏 1 隐藏',
  `default_option` int NOT NULL DEFAULT 0 COMMENT '是否是默认模板项 0 否 1 是',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工资表模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_slip_template
-- ----------------------------
INSERT INTO `wk_hrm_salary_slip_template` VALUES (1481534121629855838, '默认模板', 0, 1, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_salary_slip_template_option
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_slip_template_option`;
CREATE TABLE `wk_hrm_salary_slip_template_option`  (
  `id` bigint NOT NULL,
  `template_id` bigint NOT NULL COMMENT '模板id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '薪资项名称',
  `type` int NOT NULL COMMENT '选项类型 1 分类 2 薪资项',
  `code` int NOT NULL DEFAULT 0 COMMENT '薪资项code',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `pid` bigint NOT NULL DEFAULT 0 COMMENT '父级分类id',
  `is_hide` int NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 否 1 是',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_user_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工资条模板项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_slip_template_option
-- ----------------------------

-- ----------------------------
-- Table structure for wk_hrm_salary_tax_rule
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_salary_tax_rule`;
CREATE TABLE `wk_hrm_salary_tax_rule`  (
  `rule_id` bigint NOT NULL,
  `rule_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `tax_type` int NULL DEFAULT NULL COMMENT '个税类型 1 工资薪金所得税 2 劳务报酬所得税 3 不计税',
  `is_tax` int NULL DEFAULT NULL COMMENT '是否计税 0 否 1 是',
  `marking_point` int NULL DEFAULT NULL COMMENT '起征点',
  `decimal_point` int NULL DEFAULT NULL COMMENT '个税结果(保留小数点)',
  `cycle_type` int NULL DEFAULT NULL COMMENT '计税周期类型 1 上年12月到今年11月（对应的工资发放方式为次月发上月工资） 2 今年1月到12月（对应的工资发放方式为当月发当月工资）',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '计税规则' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_salary_tax_rule
-- ----------------------------
INSERT INTO `wk_hrm_salary_tax_rule` VALUES (1481534121625661546, '工资薪金所得税', 1, 1, 5000, 2, 1, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_tax_rule` VALUES (1481534121625661547, '劳务报酬所得税', 2, 1, 800, 2, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);
INSERT INTO `wk_hrm_salary_tax_rule` VALUES (1481534121625661548, '不计税', 3, 0, 0, NULL, NULL, 0, '2022-01-13 15:50:34', NULL, NULL);

-- ----------------------------
-- Table structure for wk_hrm_user_attendance
-- ----------------------------
DROP TABLE IF EXISTS `wk_hrm_user_attendance`;
CREATE TABLE `wk_hrm_user_attendance`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `attendance_id` bigint NOT NULL COMMENT '考勤组id',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和考勤组关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wk_hrm_user_attendance
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
