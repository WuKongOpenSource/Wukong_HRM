package com.kakarote.hrm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmFieldSort;
import com.kakarote.hrm.entity.VO.HrmFieldSortVO;

import java.util.List;

/**
 * <p>
 * 字段排序表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-19
 */
public interface IHrmFieldSortService extends BaseService<HrmFieldSort> {

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    public List<HrmFieldSortVO> queryListHead(Integer label);

    /**
     * 查询模块字段列表
     *
     * @param label label
     * @return data
     */
    public List<HrmFieldSortVO> queryListHead(Integer label, List<Long> ids);

    /**
     * 查询模块全部字段排序
     *
     * @param label  label
     * @param userId 用户ID
     * @return data
     */
    public List<HrmFieldSort> queryAllFieldSortList(Integer label, Long userId);
}
