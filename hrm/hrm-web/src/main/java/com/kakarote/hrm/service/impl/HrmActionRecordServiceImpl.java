package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TransferUtil;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.HrmActionTypeEnum;
import com.kakarote.hrm.entity.BO.QueryRecordListBO;
import com.kakarote.hrm.entity.PO.HrmActionRecord;
import com.kakarote.hrm.entity.VO.HrmModelFiledVO;
import com.kakarote.hrm.entity.VO.QueryRecordListVO;
import com.kakarote.hrm.mapper.HrmActionRecordMapper;
import com.kakarote.hrm.service.IHrmActionRecordService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * hrm员工操作记录表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmActionRecordServiceImpl extends BaseServiceImpl<HrmActionRecordMapper, HrmActionRecord> implements IHrmActionRecordService {


    @Override
    public boolean saveRecord(HrmActionTypeEnum actionTypeEnum, HrmActionBehaviorEnum behaviorEnum, List<String> content, List<String> transContent, Long typeId) {
        if (CollUtil.isEmpty(content)) {
            return true;
        }
        HrmActionRecord hrmActionRecord = new HrmActionRecord();
        hrmActionRecord.setIpAddress("127.0.0.1");
        hrmActionRecord.setType(actionTypeEnum.getValue());
        hrmActionRecord.setBehavior(behaviorEnum.getValue());
        hrmActionRecord.setTypeId(typeId);
        hrmActionRecord.setContent(JSON.toJSONString(content));
        hrmActionRecord.setTransContent(JSON.toJSONString(transContent));
        return save(hrmActionRecord);
    }

    @Override
    public List<QueryRecordListVO> queryRecordList(QueryRecordListBO queryRecordListBO) {
        List<HrmActionRecord> list = lambdaQuery().eq(HrmActionRecord::getType, queryRecordListBO.getType())
                .eq(HrmActionRecord::getTypeId, queryRecordListBO.getTypeId())
                .orderByDesc(HrmActionRecord::getCreateTime).list();
        List<QueryRecordListVO> recordListVOS = TransferUtil.transferList(list, QueryRecordListVO.class);
        recordListVOS.forEach(record -> {
            SimpleUser simpleUser = UserCacheUtil.getSimpleUser(record.getCreateUserId());
            record.setRealname(simpleUser.getNickname());
        });
        return recordListVOS;
    }

    @Override
    public List<HrmModelFiledVO> queryFieldValue(Dict kv) {
        return baseMapper.queryFieldValue(kv);
    }
}
