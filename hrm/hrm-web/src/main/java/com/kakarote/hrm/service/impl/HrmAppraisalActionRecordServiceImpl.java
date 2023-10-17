package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.HrmActionTypeEnum;
import com.kakarote.hrm.entity.BO.QueryRecordListBO;
import com.kakarote.hrm.entity.PO.HrmAppraisalActionRecord;
import com.kakarote.hrm.entity.VO.QueryAppraisalRecordListVO;
import com.kakarote.hrm.service.AdminFileService;
import com.kakarote.hrm.mapper.HrmAppraisalActionRecordMapper;
import com.kakarote.hrm.service.IHrmAppraisalActionRecordService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 绩效考核审核记录 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
@Service
public class HrmAppraisalActionRecordServiceImpl extends BaseServiceImpl<HrmAppraisalActionRecordMapper, HrmAppraisalActionRecord> implements IHrmAppraisalActionRecordService {

    public static final Map<String, String> weekName = new HashMap<String, String>() {{
        put("MONDAY", "星期一");
        put("TUESDAY", "星期二");
        put("WEDNESDAY", "星期三");
        put("THURSDAY", "星期四");
        put("FRIDAY", "星期五");
        put("SATURDAY", "星期六");
        put("SUNDAY", "星期日");
    }};

    @Override
    public boolean saveRecord(HrmActionTypeEnum actionTypeEnum, HrmActionBehaviorEnum behaviorEnum, String content, String transContent, Long typeId, String batchId) {
        if (StrUtil.isEmpty(content)) {
            return true;
        }
        HrmAppraisalActionRecord HrmAppraisalActionRecord = new HrmAppraisalActionRecord();
        HrmAppraisalActionRecord.setIpAddress("127.0.0.1");
        HrmAppraisalActionRecord.setType(actionTypeEnum.getValue());
        HrmAppraisalActionRecord.setBehavior(behaviorEnum.getValue());
        HrmAppraisalActionRecord.setTypeId(typeId);
        HrmAppraisalActionRecord.setContent(JSON.toJSONString(content));
        HrmAppraisalActionRecord.setTransContent(JSON.toJSONString(transContent));
        HrmAppraisalActionRecord.setBatchId(batchId);
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        HrmAppraisalActionRecord.setWeek(weekName.get(dayOfWeek.name()));
        HrmAppraisalActionRecord.setCreateTime(new Date());
        HrmAppraisalActionRecord.setCreateUserId(EmployeeHolder.getEmployeeId());
        return save(HrmAppraisalActionRecord);
    }

    @Override
    public List<QueryAppraisalRecordListVO> queryRecordList(QueryRecordListBO queryRecordListBO) {
        List<QueryAppraisalRecordListVO> recordList = baseMapper.queryAppraisalRecordList(queryRecordListBO.getTypeId(), HrmActionTypeEnum.KPI_APPRAISAL.getValue());
        recordList.forEach(record -> {
            AdminFileService fileService = ApplicationContextHolder.getBean(AdminFileService.class);
            List<FileEntity> fileEntities = new ArrayList<>();
            if (StrUtil.isNotEmpty(record.getBatchId())) {//有些记录没有文件batchId
                fileService.queryFileList(record.getBatchId()).forEach(fileEntity -> {
                    fileEntity.setSource("驳回添加附件");
                    fileEntities.add(fileEntity);//一个审批节点只允许上传一个文件
                });
            }
            if (CollectionUtil.isNotEmpty(fileEntities)) {
                record.setFileEntity(fileEntities.get(0));//一个审批节点只允许上传一个文件因此只用获取到第一个就行
            }
        });
        return recordList;
    }

}
