package com.kakarote.hrm.service;

import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.BO.*;
import com.kakarote.hrm.entity.PO.HrmRecruitCandidate;
import com.kakarote.hrm.entity.VO.CandidatePageListVO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 招聘候选人表 服务类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
public interface IHrmRecruitCandidateService extends BaseService<HrmRecruitCandidate> {

    /**
     * 查询候选人列表页
     *
     * @param queryCandidatePageListBO
     * @return
     */
    BasePage<CandidatePageListVO> queryCandidateList(QueryCandidatePageListBO queryCandidatePageListBO);

    /**
     * 查询基本信息
     *
     * @param candidateId
     * @return
     */
    CandidatePageListVO queryById(String candidateId);

    /**
     * 新建候选人
     *
     * @param hrmRecruitCandidate
     */
    OperationLog addCandidate(HrmRecruitCandidate hrmRecruitCandidate);

    /**
     * 编辑候选人
     *
     * @param hrmRecruitCandidate
     */
    OperationLog setCandidate(HrmRecruitCandidate hrmRecruitCandidate);

    /**
     * 删除候选人
     *
     * @param candidateId
     */
    OperationLog deleteById(Long candidateId);

    /**
     * 批量修改候选人状态
     *
     * @param updateCandidateStatusBO
     */
    List<OperationLog> updateCandidateStatus(UpdateCandidateStatusBO updateCandidateStatusBO);

    /**
     * 批量修改候选人职位
     *
     * @param updateCandidatePostBO
     */
    List<OperationLog> updateCandidatePost(UpdateCandidatePostBO updateCandidatePostBO);

    /**
     * 批量修改候选人招聘渠道
     *
     * @param updateCandidateRecruitChannelBO
     * @return
     */
    List<OperationLog> updateCandidateRecruitChannel(UpdateCandidateRecruitChannelBO updateCandidateRecruitChannelBO);

    /**
     * 查询一键清理候选人,查询完之后调用修改状态接口
     *
     * @param queryCleanCandidateBO
     * @return
     */
    List<Long> queryCleanCandidateIds(QueryCleanCandidateBO queryCleanCandidateBO);

    /**
     * 查询候选人附件
     *
     * @param candidateId
     * @return
     */
    Result<List<FileEntity>> queryFile(Long candidateId);

    /**
     * 查询每个候选人状态的数量
     *
     * @return
     */
    Map<Integer, Integer> queryCandidateStatusNum();


    /**
     * 淘汰/流失候选人
     *
     * @param eliminateCandidateBO
     * @return
     */
    List<OperationLog> eliminateCandidate(EliminateCandidateBO eliminateCandidateBO);

    /**
     * 删除候选人
     *
     * @param candidateIds
     */
    List<OperationLog> deleteByIds(List<Long> candidateIds);

    /**
     * 查询候选人列表
     *
     * @return
     */
    List<Map<String, Object>> queryRecruitListByTime(LocalDate time, Collection<Long> deptIds);

    /**
     * 根据候选人面试时间查询面试状态
     */
    Set<String> queryRecruitStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> deptIds);
}
