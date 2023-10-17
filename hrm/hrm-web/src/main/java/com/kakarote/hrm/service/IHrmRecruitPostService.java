package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.QueryRecruitPostPageListBO;
import com.kakarote.hrm.entity.BO.UpdateRecruitPostStatusBO;
import com.kakarote.hrm.entity.PO.HrmRecruitPost;
import com.kakarote.hrm.entity.VO.RecruitPostVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 招聘职位表 服务类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface IHrmRecruitPostService extends BaseService<HrmRecruitPost> {

    /**
     * 添加职位
     *
     * @param recruitPost
     */
    OperationLog addRecruitPost(HrmRecruitPost recruitPost);

    /**
     * 修改职位
     *
     * @param recruitPost
     * @return
     */
    OperationLog setRecruitPost(HrmRecruitPost recruitPost);

    /**
     * 获取职位详情
     *
     * @param postId
     * @return
     */
    RecruitPostVO queryById(Long postId);

    /**
     * 查询职位列表
     *
     * @param queryRecruitPostPageListBO
     * @return
     */
    BasePage<RecruitPostVO> queryRecruitPostPageList(QueryRecruitPostPageListBO queryRecruitPostPageListBO);

    /**
     * 修改职位状态
     *
     * @param updateRecruitPostStatusBO
     */
    OperationLog updateRecruitPostStatus(UpdateRecruitPostStatusBO updateRecruitPostStatusBO);

    /**
     * 查询每个职位状态的数量
     *
     * @return
     */
    Map<Integer, Long> queryPostStatusNum();


    /**
     * 查询所有职位列表(表单使用)
     *
     * @return
     */
    List<HrmRecruitPost> queryAllRecruitPostList();

}
