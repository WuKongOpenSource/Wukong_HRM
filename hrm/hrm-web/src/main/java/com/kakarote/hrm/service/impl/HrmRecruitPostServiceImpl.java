package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kakarote.common.log.entity.Content;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.hrm.constant.HrmActionBehaviorEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.entity.BO.HrmActionRecordListBO;
import com.kakarote.hrm.entity.BO.QueryRecruitPostPageListBO;
import com.kakarote.hrm.entity.BO.UpdateRecruitPostStatusBO;
import com.kakarote.hrm.entity.PO.HrmDept;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.PO.HrmRecruitPost;
import com.kakarote.hrm.entity.VO.RecruitPostVO;
import com.kakarote.hrm.mapper.HrmRecruitPostMapper;
import com.kakarote.hrm.service.IHrmDeptService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import com.kakarote.hrm.service.IHrmRecruitPostService;
import com.kakarote.hrm.service.actionrecord.impl.RecruitPostActionRecordServiceImpl;
import com.kakarote.hrm.utils.EmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 招聘职位表 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmRecruitPostServiceImpl extends BaseServiceImpl<HrmRecruitPostMapper, HrmRecruitPost> implements IHrmRecruitPostService {

    @Autowired
    private HrmRecruitPostMapper recruitPostMapper;

    @Resource
    private RecruitPostActionRecordServiceImpl recruitPostActionRecordService;

    @Autowired
    private IHrmDeptService deptService;

    @Autowired
    private EmployeeUtil employeeUtil;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog addRecruitPost(HrmRecruitPost recruitPost) {
        save(recruitPost);
        Content content = recruitPostActionRecordService.addOrDeleteRecord(HrmActionBehaviorEnum.ADD, recruitPost.getPostId(), recruitPost.getPostName());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(recruitPost.getPostId(), recruitPost.getPostName());
        operationLog.setOperationInfo(content.getDetail());
        return operationLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog setRecruitPost(HrmRecruitPost recruitPost) {
        HrmRecruitPost hrmRecruitPost = getById(recruitPost.getPostId());
        HrmActionRecordListBO hrmActionRecordListBO = recruitPostActionRecordService.entityUpdateRecord(BeanUtil.beanToMap(hrmRecruitPost), BeanUtil.beanToMap(recruitPost), recruitPost.getPostId());
        baseMapper.setRecruitPost(recruitPost);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(recruitPost.getPostId(), recruitPost.getPostName());
        operationLog.setOperationInfo(JSONUtil.toJsonStr(hrmActionRecordListBO.getContentList()));
        return operationLog;
    }

    @Override
    public RecruitPostVO queryById(Long postId) {
        RecruitPostVO recruitPostVO = recruitPostMapper.queryById(postId);
        if (StrUtil.isNotEmpty(recruitPostVO.getInterviewEmployeeIds())) {
            String interviewEmployeeName = employeeService.lambdaQuery().in(HrmEmployee::getEmployeeId, TagUtil.toLongSet(recruitPostVO.getInterviewEmployeeIds()))
                    .list().stream().map(HrmEmployee::getEmployeeName).collect(Collectors.joining(","));
            recruitPostVO.setInterviewEmployeeName(interviewEmployeeName);
        }
        if (recruitPostVO.getRecruitNum() != null && recruitPostVO.getRecruitNum() != 0) {
            recruitPostVO.setRecruitSchedule((recruitPostVO.getHasEntryNum() * 100 / recruitPostVO.getRecruitNum()) + "");
        }
        return recruitPostVO;
    }

    @Override
    public BasePage<RecruitPostVO> queryRecruitPostPageList(QueryRecruitPostPageListBO queryRecruitPostPageListBO) {
        Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
        BasePage<RecruitPostVO> recruitPostVOBasePage = recruitPostMapper.queryRecruitPostPageList(queryRecruitPostPageListBO.parse(), queryRecruitPostPageListBO, deptIds);
        recruitPostVOBasePage.getList().forEach(recruitPostVO -> {
            if (StrUtil.isNotEmpty(recruitPostVO.getInterviewEmployeeIds())) {
                String interviewEmployeeName = employeeService.lambdaQuery().in(HrmEmployee::getEmployeeId, TagUtil.toLongSet(recruitPostVO.getInterviewEmployeeIds()))
                        .list().stream().map(HrmEmployee::getEmployeeName).collect(Collectors.joining(","));
                recruitPostVO.setInterviewEmployeeName(interviewEmployeeName);
            }
            if (recruitPostVO.getRecruitNum() != null && recruitPostVO.getRecruitNum() != 0) {
                recruitPostVO.setRecruitSchedule((recruitPostVO.getHasEntryNum() * 100 / recruitPostVO.getRecruitNum()) + "");
            }
        });
        return recruitPostVOBasePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperationLog updateRecruitPostStatus(UpdateRecruitPostStatusBO updateRecruitPostStatusBO) {
        HrmRecruitPost hrmRecruitPost = getById(updateRecruitPostStatusBO.getPostId());
        String contentRecord = recruitPostActionRecordService.updateStatusRecord(updateRecruitPostStatusBO, hrmRecruitPost.getPostName());
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationInfo(contentRecord);
        operationLog.setOperationObject(updateRecruitPostStatusBO.getPostId(), hrmRecruitPost.getPostName());
        operationLog.setBehavior(updateRecruitPostStatusBO.getStatus() == 0 ? BehaviorEnum.STOP : BehaviorEnum.START);
        lambdaUpdate().set(HrmRecruitPost::getStatus, updateRecruitPostStatusBO.getStatus())
                .eq(HrmRecruitPost::getPostId, updateRecruitPostStatusBO.getPostId()).update();
        return operationLog;
    }

    @Override
    public Map<Integer, Long> queryPostStatusNum() {
        Collection<Long> deptIds = employeeUtil.queryDataAuthDeptIdByMenuId(MenuIdConstant.RECRUIT_MENU_ID);
        List<Integer> statusList = getBaseMapper().queryPostStatusList(deptIds);
        TreeMap<Integer, Long> collect = statusList.stream().collect(Collectors.groupingBy(e -> e, TreeMap::new, Collectors.counting()));
        for (Integer i : Arrays.asList(0, 1)) {
            if (!collect.containsKey(i)) {
                collect.put(i, 0L);
            }
        }
        return collect;
    }

    @Override
    public List<HrmRecruitPost> queryAllRecruitPostList() {
        List<HrmRecruitPost> list = lambdaQuery().select(HrmRecruitPost::getPostId, HrmRecruitPost::getPostName, HrmRecruitPost::getDeptId)
                .eq(HrmRecruitPost::getStatus, 1).list();
        for (HrmRecruitPost post : list) {
            HrmDept dept = deptService.getById(post.getDeptId());
            if (dept != null) {
                post.setDeptName(dept.getName());
            }
        }
        return list;
    }
}
